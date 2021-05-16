package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.Property;
import net.minecraft.state.StateHolder;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.BlockVoxelShape;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.EmptyBlockReader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//TODO, Delegates are weird here now, because Block extends this.
public abstract class AbstractBlock extends net.minecraftforge.registries.ForgeRegistryEntry<Block> {
   protected static final Direction[] UPDATE_SHAPE_ORDER = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};
   protected final Material material;
   protected final boolean hasCollision;
   protected final float explosionResistance;
   protected final boolean isRandomlyTicking;
   protected final SoundType soundType;
   protected final float friction;
   protected final float speedFactor;
   protected final float jumpFactor;
   protected final boolean dynamicShape;
   protected final AbstractBlock.Properties properties;
   @Nullable
   protected ResourceLocation drops;

   public AbstractBlock(AbstractBlock.Properties p_i241196_1_) {
      this.material = p_i241196_1_.material;
      this.hasCollision = p_i241196_1_.hasCollision;
      this.drops = p_i241196_1_.drops;
      this.explosionResistance = p_i241196_1_.explosionResistance;
      this.isRandomlyTicking = p_i241196_1_.isRandomlyTicking;
      this.soundType = p_i241196_1_.soundType;
      this.friction = p_i241196_1_.friction;
      this.speedFactor = p_i241196_1_.speedFactor;
      this.jumpFactor = p_i241196_1_.jumpFactor;
      this.dynamicShape = p_i241196_1_.dynamicShape;
      this.properties = p_i241196_1_;
      final ResourceLocation lootTableCache = p_i241196_1_.drops;
      this.lootTableSupplier = lootTableCache != null ? () -> lootTableCache : p_i241196_1_.lootTableSupplier != null ? p_i241196_1_.lootTableSupplier : () -> new ResourceLocation(this.getRegistryName().getNamespace(), "blocks/" + this.getRegistryName().getPath());
   }

   @Deprecated
   public void updateIndirectNeighbourShapes(BlockState p_196248_1_, IWorld p_196248_2_, BlockPos p_196248_3_, int p_196248_4_, int p_196248_5_) {
   }

   @Deprecated
   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      switch(p_196266_4_) {
      case LAND:
         return !p_196266_1_.isCollisionShapeFullBlock(p_196266_2_, p_196266_3_);
      case WATER:
         return p_196266_2_.getFluidState(p_196266_3_).is(FluidTags.WATER);
      case AIR:
         return !p_196266_1_.isCollisionShapeFullBlock(p_196266_2_, p_196266_3_);
      default:
         return false;
      }
   }

   @Deprecated
   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_1_;
   }

   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public boolean skipRendering(BlockState p_200122_1_, BlockState p_200122_2_, Direction p_200122_3_) {
      return false;
   }

   @Deprecated
   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      DebugPacketSender.sendNeighborsUpdatePacket(p_220069_2_, p_220069_3_);
   }

   @Deprecated
   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
   }

   @Deprecated
   public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.hasTileEntity() && (!p_196243_1_.is(p_196243_4_.getBlock()) || !p_196243_4_.hasTileEntity())) {
         p_196243_2_.removeBlockEntity(p_196243_3_);
      }

   }

   @Deprecated
   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      return ActionResultType.PASS;
   }

   @Deprecated
   public boolean triggerEvent(BlockState p_189539_1_, World p_189539_2_, BlockPos p_189539_3_, int p_189539_4_, int p_189539_5_) {
      return false;
   }

   @Deprecated
   public BlockRenderType getRenderShape(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   @Deprecated
   public boolean useShapeForLightOcclusion(BlockState p_220074_1_) {
      return false;
   }

   @Deprecated
   public boolean isSignalSource(BlockState p_149744_1_) {
      return false;
   }

   @Deprecated
   public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
      return this.material.getPushReaction();
   }

   @Deprecated
   public FluidState getFluidState(BlockState p_204507_1_) {
      return Fluids.EMPTY.defaultFluidState();
   }

   @Deprecated
   public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
      return false;
   }

   public AbstractBlock.OffsetType getOffsetType() {
      return AbstractBlock.OffsetType.NONE;
   }

   @Deprecated
   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_;
   }

   @Deprecated
   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_;
   }

   @Deprecated
   public boolean canBeReplaced(BlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
      return p_196253_1_.getMaterial().isReplaceable() && (p_196253_2_.getItemInHand().isEmpty() || p_196253_2_.getItemInHand().getItem() != this.asItem());
   }

   @Deprecated
   public boolean canBeReplaced(BlockState p_225541_1_, Fluid p_225541_2_) {
      return this.material.isReplaceable() || !this.material.isSolid();
   }

   @Deprecated
   public List<ItemStack> getDrops(BlockState p_220076_1_, LootContext.Builder p_220076_2_) {
      ResourceLocation resourcelocation = this.getLootTable();
      if (resourcelocation == LootTables.EMPTY) {
         return Collections.emptyList();
      } else {
         LootContext lootcontext = p_220076_2_.withParameter(LootParameters.BLOCK_STATE, p_220076_1_).create(LootParameterSets.BLOCK);
         ServerWorld serverworld = lootcontext.getLevel();
         LootTable loottable = serverworld.getServer().getLootTables().get(resourcelocation);
         return loottable.getRandomItems(lootcontext);
      }
   }

   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public long getSeed(BlockState p_209900_1_, BlockPos p_209900_2_) {
      return MathHelper.getSeed(p_209900_2_);
   }

   @Deprecated
   public VoxelShape getOcclusionShape(BlockState p_196247_1_, IBlockReader p_196247_2_, BlockPos p_196247_3_) {
      return p_196247_1_.getShape(p_196247_2_, p_196247_3_);
   }

   @Deprecated
   public VoxelShape getBlockSupportShape(BlockState p_230335_1_, IBlockReader p_230335_2_, BlockPos p_230335_3_) {
      return this.getCollisionShape(p_230335_1_, p_230335_2_, p_230335_3_, ISelectionContext.empty());
   }

   @Deprecated
   public VoxelShape getInteractionShape(BlockState p_199600_1_, IBlockReader p_199600_2_, BlockPos p_199600_3_) {
      return VoxelShapes.empty();
   }

   @Deprecated
   public int getLightBlock(BlockState p_200011_1_, IBlockReader p_200011_2_, BlockPos p_200011_3_) {
      if (p_200011_1_.isSolidRender(p_200011_2_, p_200011_3_)) {
         return p_200011_2_.getMaxLightLevel();
      } else {
         return p_200011_1_.propagatesSkylightDown(p_200011_2_, p_200011_3_) ? 0 : 1;
      }
   }

   @Nullable
   @Deprecated
   public INamedContainerProvider getMenuProvider(BlockState p_220052_1_, World p_220052_2_, BlockPos p_220052_3_) {
      return null;
   }

   @Deprecated
   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return true;
   }

   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public float getShadeBrightness(BlockState p_220080_1_, IBlockReader p_220080_2_, BlockPos p_220080_3_) {
      return p_220080_1_.isCollisionShapeFullBlock(p_220080_2_, p_220080_3_) ? 0.2F : 1.0F;
   }

   @Deprecated
   public int getAnalogOutputSignal(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return 0;
   }

   @Deprecated
   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return VoxelShapes.block();
   }

   @Deprecated
   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      return this.hasCollision ? p_220071_1_.getShape(p_220071_2_, p_220071_3_) : VoxelShapes.empty();
   }

   @Deprecated
   public VoxelShape getVisualShape(BlockState p_230322_1_, IBlockReader p_230322_2_, BlockPos p_230322_3_, ISelectionContext p_230322_4_) {
      return this.getCollisionShape(p_230322_1_, p_230322_2_, p_230322_3_, p_230322_4_);
   }

   @Deprecated
   public void randomTick(BlockState p_225542_1_, ServerWorld p_225542_2_, BlockPos p_225542_3_, Random p_225542_4_) {
      this.tick(p_225542_1_, p_225542_2_, p_225542_3_, p_225542_4_);
   }

   @Deprecated
   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
   }

   @Deprecated
   public float getDestroyProgress(BlockState p_180647_1_, PlayerEntity p_180647_2_, IBlockReader p_180647_3_, BlockPos p_180647_4_) {
      float f = p_180647_1_.getDestroySpeed(p_180647_3_, p_180647_4_);
      if (f == -1.0F) {
         return 0.0F;
      } else {
         int i = net.minecraftforge.common.ForgeHooks.canHarvestBlock(p_180647_1_, p_180647_2_, p_180647_3_, p_180647_4_) ? 30 : 100;
         return p_180647_2_.getDigSpeed(p_180647_1_, p_180647_4_) / f / (float)i;
      }
   }

   @Deprecated
   public void spawnAfterBreak(BlockState p_220062_1_, ServerWorld p_220062_2_, BlockPos p_220062_3_, ItemStack p_220062_4_) {
   }

   @Deprecated
   public void attack(BlockState p_196270_1_, World p_196270_2_, BlockPos p_196270_3_, PlayerEntity p_196270_4_) {
   }

   @Deprecated
   public int getSignal(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      return 0;
   }

   @Deprecated
   public void entityInside(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
   }

   @Deprecated
   public int getDirectSignal(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
      return 0;
   }

   @Deprecated //Forge: Use state.hasTileEntity()
   public final boolean isEntityBlock() {
      return this instanceof ITileEntityProvider;
   }

   public final ResourceLocation getLootTable() {
      if (this.drops == null) {
         this.drops = this.lootTableSupplier.get();
      }

      return this.drops;
   }

   @Deprecated
   public void onProjectileHit(World p_220066_1_, BlockState p_220066_2_, BlockRayTraceResult p_220066_3_, ProjectileEntity p_220066_4_) {
   }

   public abstract Item asItem();

   protected abstract Block asBlock();

   public MaterialColor defaultMaterialColor() {
      return this.properties.materialColor.apply(this.asBlock().defaultBlockState());
   }

   protected boolean isAir(BlockState state) {
      return ((AbstractBlockState)state).isAir;
   }

   /* ======================================== FORGE START ===================================== */
   private final java.util.function.Supplier<ResourceLocation> lootTableSupplier;
   /* ========================================= FORGE END ====================================== */

   public abstract static class AbstractBlockState extends StateHolder<Block, BlockState> {
      private final int lightEmission;
      private final boolean useShapeForLightOcclusion;
      private final boolean isAir;
      private final Material material;
      private final MaterialColor materialColor;
      private final float destroySpeed;
      private final boolean requiresCorrectToolForDrops;
      private final boolean canOcclude;
      private final AbstractBlock.IPositionPredicate isRedstoneConductor;
      private final AbstractBlock.IPositionPredicate isSuffocating;
      private final AbstractBlock.IPositionPredicate isViewBlocking;
      private final AbstractBlock.IPositionPredicate hasPostProcess;
      private final AbstractBlock.IPositionPredicate emissiveRendering;
      @Nullable
      protected AbstractBlock.AbstractBlockState.Cache cache;

      protected AbstractBlockState(Block p_i231870_1_, ImmutableMap<Property<?>, Comparable<?>> p_i231870_2_, MapCodec<BlockState> p_i231870_3_) {
         super(p_i231870_1_, p_i231870_2_, p_i231870_3_);
         AbstractBlock.Properties abstractblock$properties = p_i231870_1_.properties;
         this.lightEmission = abstractblock$properties.lightEmission.applyAsInt(this.asState());
         this.useShapeForLightOcclusion = p_i231870_1_.useShapeForLightOcclusion(this.asState());
         this.isAir = abstractblock$properties.isAir;
         this.material = abstractblock$properties.material;
         this.materialColor = abstractblock$properties.materialColor.apply(this.asState());
         this.destroySpeed = abstractblock$properties.destroyTime;
         this.requiresCorrectToolForDrops = abstractblock$properties.requiresCorrectToolForDrops;
         this.canOcclude = abstractblock$properties.canOcclude;
         this.isRedstoneConductor = abstractblock$properties.isRedstoneConductor;
         this.isSuffocating = abstractblock$properties.isSuffocating;
         this.isViewBlocking = abstractblock$properties.isViewBlocking;
         this.hasPostProcess = abstractblock$properties.hasPostProcess;
         this.emissiveRendering = abstractblock$properties.emissiveRendering;
      }

      public void initCache() {
         if (!this.getBlock().hasDynamicShape()) {
            this.cache = new AbstractBlock.AbstractBlockState.Cache(this.asState());
         }

      }

      public Block getBlock() {
         return this.owner;
      }

      public Material getMaterial() {
         return this.material;
      }

      public boolean isValidSpawn(IBlockReader p_215688_1_, BlockPos p_215688_2_, EntityType<?> p_215688_3_) {
         return this.getBlock().properties.isValidSpawn.test(this.asState(), p_215688_1_, p_215688_2_, p_215688_3_);
      }

      public boolean propagatesSkylightDown(IBlockReader p_200131_1_, BlockPos p_200131_2_) {
         return this.cache != null ? this.cache.propagatesSkylightDown : this.getBlock().propagatesSkylightDown(this.asState(), p_200131_1_, p_200131_2_);
      }

      public int getLightBlock(IBlockReader p_200016_1_, BlockPos p_200016_2_) {
         return this.cache != null ? this.cache.lightBlock : this.getBlock().getLightBlock(this.asState(), p_200016_1_, p_200016_2_);
      }

      public VoxelShape getFaceOcclusionShape(IBlockReader p_215702_1_, BlockPos p_215702_2_, Direction p_215702_3_) {
         return this.cache != null && this.cache.occlusionShapes != null ? this.cache.occlusionShapes[p_215702_3_.ordinal()] : VoxelShapes.getFaceShape(this.getOcclusionShape(p_215702_1_, p_215702_2_), p_215702_3_);
      }

      public VoxelShape getOcclusionShape(IBlockReader p_235754_1_, BlockPos p_235754_2_) {
         return this.getBlock().getOcclusionShape(this.asState(), p_235754_1_, p_235754_2_);
      }

      public boolean hasLargeCollisionShape() {
         return this.cache == null || this.cache.largeCollisionShape;
      }

      public boolean useShapeForLightOcclusion() {
         return this.useShapeForLightOcclusion;
      }

      public int getLightEmission() {
         return this.lightEmission;
      }

      /** @deprecated use {@link BlockState#isAir(IBlockReader, BlockPos) */
      @Deprecated
      public boolean isAir() {
         return this.getBlock().isAir((BlockState)this);
      }

      public MaterialColor getMapColor(IBlockReader p_185909_1_, BlockPos p_185909_2_) {
         return this.materialColor;
      }

      /** @deprecated use {@link BlockState#rotate(IWorld, BlockPos, Rotation) */
      @Deprecated
      public BlockState rotate(Rotation p_185907_1_) {
         return this.getBlock().rotate(this.asState(), p_185907_1_);
      }

      public BlockState mirror(Mirror p_185902_1_) {
         return this.getBlock().mirror(this.asState(), p_185902_1_);
      }

      public BlockRenderType getRenderShape() {
         return this.getBlock().getRenderShape(this.asState());
      }

      @OnlyIn(Dist.CLIENT)
      public boolean emissiveRendering(IBlockReader p_227035_1_, BlockPos p_227035_2_) {
         return this.emissiveRendering.test(this.asState(), p_227035_1_, p_227035_2_);
      }

      @OnlyIn(Dist.CLIENT)
      public float getShadeBrightness(IBlockReader p_215703_1_, BlockPos p_215703_2_) {
         return this.getBlock().getShadeBrightness(this.asState(), p_215703_1_, p_215703_2_);
      }

      public boolean isRedstoneConductor(IBlockReader p_215686_1_, BlockPos p_215686_2_) {
         return this.isRedstoneConductor.test(this.asState(), p_215686_1_, p_215686_2_);
      }

      public boolean isSignalSource() {
         return this.getBlock().isSignalSource(this.asState());
      }

      public int getSignal(IBlockReader p_185911_1_, BlockPos p_185911_2_, Direction p_185911_3_) {
         return this.getBlock().getSignal(this.asState(), p_185911_1_, p_185911_2_, p_185911_3_);
      }

      public boolean hasAnalogOutputSignal() {
         return this.getBlock().hasAnalogOutputSignal(this.asState());
      }

      public int getAnalogOutputSignal(World p_185888_1_, BlockPos p_185888_2_) {
         return this.getBlock().getAnalogOutputSignal(this.asState(), p_185888_1_, p_185888_2_);
      }

      public float getDestroySpeed(IBlockReader p_185887_1_, BlockPos p_185887_2_) {
         return this.destroySpeed;
      }

      public float getDestroyProgress(PlayerEntity p_185903_1_, IBlockReader p_185903_2_, BlockPos p_185903_3_) {
         return this.getBlock().getDestroyProgress(this.asState(), p_185903_1_, p_185903_2_, p_185903_3_);
      }

      public int getDirectSignal(IBlockReader p_185893_1_, BlockPos p_185893_2_, Direction p_185893_3_) {
         return this.getBlock().getDirectSignal(this.asState(), p_185893_1_, p_185893_2_, p_185893_3_);
      }

      public PushReaction getPistonPushReaction() {
         return this.getBlock().getPistonPushReaction(this.asState());
      }

      public boolean isSolidRender(IBlockReader p_200015_1_, BlockPos p_200015_2_) {
         if (this.cache != null) {
            return this.cache.solidRender;
         } else {
            BlockState blockstate = this.asState();
            return blockstate.canOcclude() ? Block.isShapeFullBlock(blockstate.getOcclusionShape(p_200015_1_, p_200015_2_)) : false;
         }
      }

      public boolean canOcclude() {
         return this.canOcclude;
      }

      @OnlyIn(Dist.CLIENT)
      public boolean skipRendering(BlockState p_200017_1_, Direction p_200017_2_) {
         return this.getBlock().skipRendering(this.asState(), p_200017_1_, p_200017_2_);
      }

      public VoxelShape getShape(IBlockReader p_196954_1_, BlockPos p_196954_2_) {
         return this.getShape(p_196954_1_, p_196954_2_, ISelectionContext.empty());
      }

      public VoxelShape getShape(IBlockReader p_215700_1_, BlockPos p_215700_2_, ISelectionContext p_215700_3_) {
         return this.getBlock().getShape(this.asState(), p_215700_1_, p_215700_2_, p_215700_3_);
      }

      public VoxelShape getCollisionShape(IBlockReader p_196952_1_, BlockPos p_196952_2_) {
         return this.cache != null ? this.cache.collisionShape : this.getCollisionShape(p_196952_1_, p_196952_2_, ISelectionContext.empty());
      }

      public VoxelShape getCollisionShape(IBlockReader p_215685_1_, BlockPos p_215685_2_, ISelectionContext p_215685_3_) {
         return this.getBlock().getCollisionShape(this.asState(), p_215685_1_, p_215685_2_, p_215685_3_);
      }

      public VoxelShape getBlockSupportShape(IBlockReader p_196951_1_, BlockPos p_196951_2_) {
         return this.getBlock().getBlockSupportShape(this.asState(), p_196951_1_, p_196951_2_);
      }

      public VoxelShape getVisualShape(IBlockReader p_199611_1_, BlockPos p_199611_2_, ISelectionContext p_199611_3_) {
         return this.getBlock().getVisualShape(this.asState(), p_199611_1_, p_199611_2_, p_199611_3_);
      }

      public VoxelShape getInteractionShape(IBlockReader p_235777_1_, BlockPos p_235777_2_) {
         return this.getBlock().getInteractionShape(this.asState(), p_235777_1_, p_235777_2_);
      }

      public final boolean entityCanStandOn(IBlockReader p_235719_1_, BlockPos p_235719_2_, Entity p_235719_3_) {
         return this.entityCanStandOnFace(p_235719_1_, p_235719_2_, p_235719_3_, Direction.UP);
      }

      public final boolean entityCanStandOnFace(IBlockReader p_215682_1_, BlockPos p_215682_2_, Entity p_215682_3_, Direction p_215682_4_) {
         return Block.isFaceFull(this.getCollisionShape(p_215682_1_, p_215682_2_, ISelectionContext.of(p_215682_3_)), p_215682_4_);
      }

      public Vector3d getOffset(IBlockReader p_191059_1_, BlockPos p_191059_2_) {
         AbstractBlock.OffsetType abstractblock$offsettype = this.getBlock().getOffsetType();
         if (abstractblock$offsettype == AbstractBlock.OffsetType.NONE) {
            return Vector3d.ZERO;
         } else {
            long i = MathHelper.getSeed(p_191059_2_.getX(), 0, p_191059_2_.getZ());
            return new Vector3d(((double)((float)(i & 15L) / 15.0F) - 0.5D) * 0.5D, abstractblock$offsettype == AbstractBlock.OffsetType.XYZ ? ((double)((float)(i >> 4 & 15L) / 15.0F) - 1.0D) * 0.2D : 0.0D, ((double)((float)(i >> 8 & 15L) / 15.0F) - 0.5D) * 0.5D);
         }
      }

      public boolean triggerEvent(World p_235728_1_, BlockPos p_235728_2_, int p_235728_3_, int p_235728_4_) {
         return this.getBlock().triggerEvent(this.asState(), p_235728_1_, p_235728_2_, p_235728_3_, p_235728_4_);
      }

      public void neighborChanged(World p_215697_1_, BlockPos p_215697_2_, Block p_215697_3_, BlockPos p_215697_4_, boolean p_215697_5_) {
         this.getBlock().neighborChanged(this.asState(), p_215697_1_, p_215697_2_, p_215697_3_, p_215697_4_, p_215697_5_);
      }

      public final void updateNeighbourShapes(IWorld p_235734_1_, BlockPos p_235734_2_, int p_235734_3_) {
         this.updateNeighbourShapes(p_235734_1_, p_235734_2_, p_235734_3_, 512);
      }

      public final void updateNeighbourShapes(IWorld p_241482_1_, BlockPos p_241482_2_, int p_241482_3_, int p_241482_4_) {
         this.getBlock();
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

         for(Direction direction : AbstractBlock.UPDATE_SHAPE_ORDER) {
            blockpos$mutable.setWithOffset(p_241482_2_, direction);
            BlockState blockstate = p_241482_1_.getBlockState(blockpos$mutable);
            BlockState blockstate1 = blockstate.updateShape(direction.getOpposite(), this.asState(), p_241482_1_, blockpos$mutable, p_241482_2_);
            Block.updateOrDestroy(blockstate, blockstate1, p_241482_1_, blockpos$mutable, p_241482_3_, p_241482_4_);
         }

      }

      public final void updateIndirectNeighbourShapes(IWorld p_196948_1_, BlockPos p_196948_2_, int p_196948_3_) {
         this.updateIndirectNeighbourShapes(p_196948_1_, p_196948_2_, p_196948_3_, 512);
      }

      public void updateIndirectNeighbourShapes(IWorld p_241483_1_, BlockPos p_241483_2_, int p_241483_3_, int p_241483_4_) {
         this.getBlock().updateIndirectNeighbourShapes(this.asState(), p_241483_1_, p_241483_2_, p_241483_3_, p_241483_4_);
      }

      public void onPlace(World p_215705_1_, BlockPos p_215705_2_, BlockState p_215705_3_, boolean p_215705_4_) {
         this.getBlock().onPlace(this.asState(), p_215705_1_, p_215705_2_, p_215705_3_, p_215705_4_);
      }

      public void onRemove(World p_196947_1_, BlockPos p_196947_2_, BlockState p_196947_3_, boolean p_196947_4_) {
         this.getBlock().onRemove(this.asState(), p_196947_1_, p_196947_2_, p_196947_3_, p_196947_4_);
      }

      public void tick(ServerWorld p_227033_1_, BlockPos p_227033_2_, Random p_227033_3_) {
         this.getBlock().tick(this.asState(), p_227033_1_, p_227033_2_, p_227033_3_);
      }

      public void randomTick(ServerWorld p_227034_1_, BlockPos p_227034_2_, Random p_227034_3_) {
         this.getBlock().randomTick(this.asState(), p_227034_1_, p_227034_2_, p_227034_3_);
      }

      public void entityInside(World p_196950_1_, BlockPos p_196950_2_, Entity p_196950_3_) {
         this.getBlock().entityInside(this.asState(), p_196950_1_, p_196950_2_, p_196950_3_);
      }

      public void spawnAfterBreak(ServerWorld p_215706_1_, BlockPos p_215706_2_, ItemStack p_215706_3_) {
         this.getBlock().spawnAfterBreak(this.asState(), p_215706_1_, p_215706_2_, p_215706_3_);
      }

      public List<ItemStack> getDrops(LootContext.Builder p_215693_1_) {
         return this.getBlock().getDrops(this.asState(), p_215693_1_);
      }

      public ActionResultType use(World p_227031_1_, PlayerEntity p_227031_2_, Hand p_227031_3_, BlockRayTraceResult p_227031_4_) {
         return this.getBlock().use(this.asState(), p_227031_1_, p_227031_4_.getBlockPos(), p_227031_2_, p_227031_3_, p_227031_4_);
      }

      public void attack(World p_196942_1_, BlockPos p_196942_2_, PlayerEntity p_196942_3_) {
         this.getBlock().attack(this.asState(), p_196942_1_, p_196942_2_, p_196942_3_);
      }

      public boolean isSuffocating(IBlockReader p_229980_1_, BlockPos p_229980_2_) {
         return this.isSuffocating.test(this.asState(), p_229980_1_, p_229980_2_);
      }

      @OnlyIn(Dist.CLIENT)
      public boolean isViewBlocking(IBlockReader p_215696_1_, BlockPos p_215696_2_) {
         return this.isViewBlocking.test(this.asState(), p_215696_1_, p_215696_2_);
      }

      public BlockState updateShape(Direction p_196956_1_, BlockState p_196956_2_, IWorld p_196956_3_, BlockPos p_196956_4_, BlockPos p_196956_5_) {
         return this.getBlock().updateShape(this.asState(), p_196956_1_, p_196956_2_, p_196956_3_, p_196956_4_, p_196956_5_);
      }

      public boolean isPathfindable(IBlockReader p_196957_1_, BlockPos p_196957_2_, PathType p_196957_3_) {
         return this.getBlock().isPathfindable(this.asState(), p_196957_1_, p_196957_2_, p_196957_3_);
      }

      public boolean canBeReplaced(BlockItemUseContext p_196953_1_) {
         return this.getBlock().canBeReplaced(this.asState(), p_196953_1_);
      }

      public boolean canBeReplaced(Fluid p_227032_1_) {
         return this.getBlock().canBeReplaced(this.asState(), p_227032_1_);
      }

      public boolean canSurvive(IWorldReader p_196955_1_, BlockPos p_196955_2_) {
         return this.getBlock().canSurvive(this.asState(), p_196955_1_, p_196955_2_);
      }

      public boolean hasPostProcess(IBlockReader p_202065_1_, BlockPos p_202065_2_) {
         return this.hasPostProcess.test(this.asState(), p_202065_1_, p_202065_2_);
      }

      @Nullable
      public INamedContainerProvider getMenuProvider(World p_215699_1_, BlockPos p_215699_2_) {
         return this.getBlock().getMenuProvider(this.asState(), p_215699_1_, p_215699_2_);
      }

      public boolean is(ITag<Block> p_235714_1_) {
         return this.getBlock().is(p_235714_1_);
      }

      public boolean is(ITag<Block> p_235715_1_, Predicate<AbstractBlock.AbstractBlockState> p_235715_2_) {
         return this.getBlock().is(p_235715_1_) && p_235715_2_.test(this);
      }

      public boolean is(Block p_203425_1_) {
         return this.getBlock().is(p_203425_1_);
      }

      public FluidState getFluidState() {
         return this.getBlock().getFluidState(this.asState());
      }

      public boolean isRandomlyTicking() {
         return this.getBlock().isRandomlyTicking(this.asState());
      }

      @OnlyIn(Dist.CLIENT)
      public long getSeed(BlockPos p_209533_1_) {
         return this.getBlock().getSeed(this.asState(), p_209533_1_);
      }

      public SoundType getSoundType() {
         return this.getBlock().getSoundType(this.asState());
      }

      public void onProjectileHit(World p_215690_1_, BlockState p_215690_2_, BlockRayTraceResult p_215690_3_, ProjectileEntity p_215690_4_) {
         this.getBlock().onProjectileHit(p_215690_1_, p_215690_2_, p_215690_3_, p_215690_4_);
      }

      public boolean isFaceSturdy(IBlockReader p_224755_1_, BlockPos p_224755_2_, Direction p_224755_3_) {
         return this.isFaceSturdy(p_224755_1_, p_224755_2_, p_224755_3_, BlockVoxelShape.FULL);
      }

      public boolean isFaceSturdy(IBlockReader p_242698_1_, BlockPos p_242698_2_, Direction p_242698_3_, BlockVoxelShape p_242698_4_) {
         return this.cache != null ? this.cache.isFaceSturdy(p_242698_3_, p_242698_4_) : p_242698_4_.isSupporting(this.asState(), p_242698_1_, p_242698_2_, p_242698_3_);
      }

      public boolean isCollisionShapeFullBlock(IBlockReader p_235785_1_, BlockPos p_235785_2_) {
         return this.cache != null ? this.cache.isCollisionShapeFullBlock : Block.isShapeFullBlock(this.getCollisionShape(p_235785_1_, p_235785_2_));
      }

      protected abstract BlockState asState();

      public boolean requiresCorrectToolForDrops() {
         return this.requiresCorrectToolForDrops;
      }

      static final class Cache {
         private static final Direction[] DIRECTIONS = Direction.values();
         private static final int SUPPORT_TYPE_COUNT = BlockVoxelShape.values().length;
         protected final boolean solidRender;
         private final boolean propagatesSkylightDown;
         private final int lightBlock;
         @Nullable
         private final VoxelShape[] occlusionShapes;
         protected final VoxelShape collisionShape;
         protected final boolean largeCollisionShape;
         private final boolean[] faceSturdy;
         protected final boolean isCollisionShapeFullBlock;

         private Cache(BlockState p_i50627_1_) {
            Block block = p_i50627_1_.getBlock();
            this.solidRender = p_i50627_1_.isSolidRender(EmptyBlockReader.INSTANCE, BlockPos.ZERO);
            this.propagatesSkylightDown = block.propagatesSkylightDown(p_i50627_1_, EmptyBlockReader.INSTANCE, BlockPos.ZERO);
            this.lightBlock = block.getLightBlock(p_i50627_1_, EmptyBlockReader.INSTANCE, BlockPos.ZERO);
            if (!p_i50627_1_.canOcclude()) {
               this.occlusionShapes = null;
            } else {
               this.occlusionShapes = new VoxelShape[DIRECTIONS.length];
               VoxelShape voxelshape = block.getOcclusionShape(p_i50627_1_, EmptyBlockReader.INSTANCE, BlockPos.ZERO);

               for(Direction direction : DIRECTIONS) {
                  this.occlusionShapes[direction.ordinal()] = VoxelShapes.getFaceShape(voxelshape, direction);
               }
            }

            this.collisionShape = block.getCollisionShape(p_i50627_1_, EmptyBlockReader.INSTANCE, BlockPos.ZERO, ISelectionContext.empty());
            this.largeCollisionShape = Arrays.stream(Direction.Axis.values()).anyMatch((p_235796_1_) -> {
               return this.collisionShape.min(p_235796_1_) < 0.0D || this.collisionShape.max(p_235796_1_) > 1.0D;
            });
            this.faceSturdy = new boolean[DIRECTIONS.length * SUPPORT_TYPE_COUNT];

            for(Direction direction1 : DIRECTIONS) {
               for(BlockVoxelShape blockvoxelshape : BlockVoxelShape.values()) {
                  this.faceSturdy[getFaceSupportIndex(direction1, blockvoxelshape)] = blockvoxelshape.isSupporting(p_i50627_1_, EmptyBlockReader.INSTANCE, BlockPos.ZERO, direction1);
               }
            }

            this.isCollisionShapeFullBlock = Block.isShapeFullBlock(p_i50627_1_.getCollisionShape(EmptyBlockReader.INSTANCE, BlockPos.ZERO));
         }

         public boolean isFaceSturdy(Direction p_242700_1_, BlockVoxelShape p_242700_2_) {
            return this.faceSturdy[getFaceSupportIndex(p_242700_1_, p_242700_2_)];
         }

         private static int getFaceSupportIndex(Direction p_242701_0_, BlockVoxelShape p_242701_1_) {
            return p_242701_0_.ordinal() * SUPPORT_TYPE_COUNT + p_242701_1_.ordinal();
         }
      }
   }

   public interface IExtendedPositionPredicate<A> {
      boolean test(BlockState p_test_1_, IBlockReader p_test_2_, BlockPos p_test_3_, A p_test_4_);
   }

   public interface IPositionPredicate {
      boolean test(BlockState p_test_1_, IBlockReader p_test_2_, BlockPos p_test_3_);
   }

   public static enum OffsetType {
      NONE,
      XZ,
      XYZ;
   }

   public static class Properties {
      private Material material;
      private Function<BlockState, MaterialColor> materialColor;
      private boolean hasCollision = true;
      private SoundType soundType = SoundType.STONE;
      private ToIntFunction<BlockState> lightEmission = (p_235830_0_) -> {
         return 0;
      };
      private float explosionResistance;
      private float destroyTime;
      private boolean requiresCorrectToolForDrops;
      private boolean isRandomlyTicking;
      private float friction = 0.6F;
      private float speedFactor = 1.0F;
      private float jumpFactor = 1.0F;
      private ResourceLocation drops;
      private boolean canOcclude = true;
      private boolean isAir;
      private int harvestLevel = -1;
      private net.minecraftforge.common.ToolType harvestTool;
      private java.util.function.Supplier<ResourceLocation> lootTableSupplier;
      private AbstractBlock.IExtendedPositionPredicate<EntityType<?>> isValidSpawn = (p_235832_0_, p_235832_1_, p_235832_2_, p_235832_3_) -> {
         return p_235832_0_.isFaceSturdy(p_235832_1_, p_235832_2_, Direction.UP) && p_235832_0_.getLightEmission() < 14;
      };
      private AbstractBlock.IPositionPredicate isRedstoneConductor = (p_235853_0_, p_235853_1_, p_235853_2_) -> {
         return p_235853_0_.getMaterial().isSolidBlocking() && p_235853_0_.isCollisionShapeFullBlock(p_235853_1_, p_235853_2_);
      };
      private AbstractBlock.IPositionPredicate isSuffocating = (p_235848_1_, p_235848_2_, p_235848_3_) -> {
         return this.material.blocksMotion() && p_235848_1_.isCollisionShapeFullBlock(p_235848_2_, p_235848_3_);
      };
      private AbstractBlock.IPositionPredicate isViewBlocking = this.isSuffocating;
      private AbstractBlock.IPositionPredicate hasPostProcess = (p_235843_0_, p_235843_1_, p_235843_2_) -> {
         return false;
      };
      private AbstractBlock.IPositionPredicate emissiveRendering = (p_235831_0_, p_235831_1_, p_235831_2_) -> {
         return false;
      };
      private boolean dynamicShape;

      private Properties(Material p_i48616_1_, MaterialColor p_i48616_2_) {
         this(p_i48616_1_, (p_235837_1_) -> {
            return p_i48616_2_;
         });
      }

      private Properties(Material p_i241199_1_, Function<BlockState, MaterialColor> p_i241199_2_) {
         this.material = p_i241199_1_;
         this.materialColor = p_i241199_2_;
      }

      public static AbstractBlock.Properties of(Material p_200945_0_) {
         return of(p_200945_0_, p_200945_0_.getColor());
      }

      public static AbstractBlock.Properties of(Material p_200952_0_, DyeColor p_200952_1_) {
         return of(p_200952_0_, p_200952_1_.getMaterialColor());
      }

      public static AbstractBlock.Properties of(Material p_200949_0_, MaterialColor p_200949_1_) {
         return new AbstractBlock.Properties(p_200949_0_, p_200949_1_);
      }

      public static AbstractBlock.Properties of(Material p_235836_0_, Function<BlockState, MaterialColor> p_235836_1_) {
         return new AbstractBlock.Properties(p_235836_0_, p_235836_1_);
      }

      public static AbstractBlock.Properties copy(AbstractBlock p_200950_0_) {
         AbstractBlock.Properties abstractblock$properties = new AbstractBlock.Properties(p_200950_0_.material, p_200950_0_.properties.materialColor);
         abstractblock$properties.material = p_200950_0_.properties.material;
         abstractblock$properties.destroyTime = p_200950_0_.properties.destroyTime;
         abstractblock$properties.explosionResistance = p_200950_0_.properties.explosionResistance;
         abstractblock$properties.hasCollision = p_200950_0_.properties.hasCollision;
         abstractblock$properties.isRandomlyTicking = p_200950_0_.properties.isRandomlyTicking;
         abstractblock$properties.lightEmission = p_200950_0_.properties.lightEmission;
         abstractblock$properties.materialColor = p_200950_0_.properties.materialColor;
         abstractblock$properties.soundType = p_200950_0_.properties.soundType;
         abstractblock$properties.friction = p_200950_0_.properties.friction;
         abstractblock$properties.speedFactor = p_200950_0_.properties.speedFactor;
         abstractblock$properties.dynamicShape = p_200950_0_.properties.dynamicShape;
         abstractblock$properties.canOcclude = p_200950_0_.properties.canOcclude;
         abstractblock$properties.isAir = p_200950_0_.properties.isAir;
         abstractblock$properties.requiresCorrectToolForDrops = p_200950_0_.properties.requiresCorrectToolForDrops;
         abstractblock$properties.harvestLevel = p_200950_0_.properties.harvestLevel;
         abstractblock$properties.harvestTool = p_200950_0_.properties.harvestTool;
         return abstractblock$properties;
      }

      public AbstractBlock.Properties noCollission() {
         this.hasCollision = false;
         this.canOcclude = false;
         return this;
      }

      public AbstractBlock.Properties noOcclusion() {
         this.canOcclude = false;
         return this;
      }

      public AbstractBlock.Properties harvestLevel(int harvestLevel) {
         this.harvestLevel = harvestLevel;
         return this;
      }

      public AbstractBlock.Properties harvestTool(net.minecraftforge.common.ToolType harvestTool) {
         this.harvestTool = harvestTool;
         return this;
      }

      public int getHarvestLevel() {
         return this.harvestLevel;
      }

      public net.minecraftforge.common.ToolType getHarvestTool() {
         return this.harvestTool;
      }

      public AbstractBlock.Properties friction(float p_200941_1_) {
         this.friction = p_200941_1_;
         return this;
      }

      public AbstractBlock.Properties speedFactor(float p_226897_1_) {
         this.speedFactor = p_226897_1_;
         return this;
      }

      public AbstractBlock.Properties jumpFactor(float p_226898_1_) {
         this.jumpFactor = p_226898_1_;
         return this;
      }

      public AbstractBlock.Properties sound(SoundType p_200947_1_) {
         this.soundType = p_200947_1_;
         return this;
      }

      public AbstractBlock.Properties lightLevel(ToIntFunction<BlockState> p_235838_1_) {
         this.lightEmission = p_235838_1_;
         return this;
      }

      public AbstractBlock.Properties strength(float p_200948_1_, float p_200948_2_) {
         this.destroyTime = p_200948_1_;
         this.explosionResistance = Math.max(0.0F, p_200948_2_);
         return this;
      }

      public AbstractBlock.Properties instabreak() {
         return this.strength(0.0F);
      }

      public AbstractBlock.Properties strength(float p_200943_1_) {
         this.strength(p_200943_1_, p_200943_1_);
         return this;
      }

      public AbstractBlock.Properties randomTicks() {
         this.isRandomlyTicking = true;
         return this;
      }

      public AbstractBlock.Properties dynamicShape() {
         this.dynamicShape = true;
         return this;
      }

      public AbstractBlock.Properties noDrops() {
         this.drops = LootTables.EMPTY;
         return this;
      }

      public AbstractBlock.Properties dropsLike(Block p_222379_1_) {
         this.lootTableSupplier = () -> p_222379_1_.delegate.get().getLootTable();
         return this;
      }

      public AbstractBlock.Properties air() {
         this.isAir = true;
         return this;
      }

      public AbstractBlock.Properties isValidSpawn(AbstractBlock.IExtendedPositionPredicate<EntityType<?>> p_235827_1_) {
         this.isValidSpawn = p_235827_1_;
         return this;
      }

      public AbstractBlock.Properties isRedstoneConductor(AbstractBlock.IPositionPredicate p_235828_1_) {
         this.isRedstoneConductor = p_235828_1_;
         return this;
      }

      public AbstractBlock.Properties isSuffocating(AbstractBlock.IPositionPredicate p_235842_1_) {
         this.isSuffocating = p_235842_1_;
         return this;
      }

      public AbstractBlock.Properties isViewBlocking(AbstractBlock.IPositionPredicate p_235847_1_) {
         this.isViewBlocking = p_235847_1_;
         return this;
      }

      public AbstractBlock.Properties hasPostProcess(AbstractBlock.IPositionPredicate p_235852_1_) {
         this.hasPostProcess = p_235852_1_;
         return this;
      }

      public AbstractBlock.Properties emissiveRendering(AbstractBlock.IPositionPredicate p_235856_1_) {
         this.emissiveRendering = p_235856_1_;
         return this;
      }

      public AbstractBlock.Properties requiresCorrectToolForDrops() {
         this.requiresCorrectToolForDrops = true;
         return this;
      }
   }
}
