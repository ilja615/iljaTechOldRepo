package net.minecraft.block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockVoxelShape;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Block extends AbstractBlock implements IItemProvider, net.minecraftforge.common.extensions.IForgeBlock {
   protected static final Logger LOGGER = LogManager.getLogger();
   @Deprecated //Forge: Do not use, use GameRegistry
   public static final ObjectIntIdentityMap<BlockState> BLOCK_STATE_REGISTRY = net.minecraftforge.registries.GameData.getBlockStateIDMap();
   private static final LoadingCache<VoxelShape, Boolean> SHAPE_FULL_BLOCK_CACHE = CacheBuilder.newBuilder().maximumSize(512L).weakKeys().build(new CacheLoader<VoxelShape, Boolean>() {
      public Boolean load(VoxelShape p_load_1_) {
         return !VoxelShapes.joinIsNotEmpty(VoxelShapes.block(), p_load_1_, IBooleanFunction.NOT_SAME);
      }
   });
   protected final StateContainer<Block, BlockState> stateDefinition;
   private BlockState defaultBlockState;
   @Nullable
   private String descriptionId;
   @Nullable
   private Item item;
   private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>> OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
      Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>(2048, 0.25F) {
         protected void rehash(int p_rehash_1_) {
         }
      };
      object2bytelinkedopenhashmap.defaultReturnValue((byte)127);
      return object2bytelinkedopenhashmap;
   });

   public static int getId(@Nullable BlockState p_196246_0_) {
      if (p_196246_0_ == null) {
         return 0;
      } else {
         int i = BLOCK_STATE_REGISTRY.getId(p_196246_0_);
         return i == -1 ? 0 : i;
      }
   }

   public static BlockState stateById(int p_196257_0_) {
      BlockState blockstate = BLOCK_STATE_REGISTRY.byId(p_196257_0_);
      return blockstate == null ? Blocks.AIR.defaultBlockState() : blockstate;
   }

   public static Block byItem(@Nullable Item p_149634_0_) {
      return p_149634_0_ instanceof BlockItem ? ((BlockItem)p_149634_0_).getBlock() : Blocks.AIR;
   }

   public static BlockState pushEntitiesUp(BlockState p_199601_0_, BlockState p_199601_1_, World p_199601_2_, BlockPos p_199601_3_) {
      VoxelShape voxelshape = VoxelShapes.joinUnoptimized(p_199601_0_.getCollisionShape(p_199601_2_, p_199601_3_), p_199601_1_.getCollisionShape(p_199601_2_, p_199601_3_), IBooleanFunction.ONLY_SECOND).move((double)p_199601_3_.getX(), (double)p_199601_3_.getY(), (double)p_199601_3_.getZ());

      for(Entity entity : p_199601_2_.getEntities((Entity)null, voxelshape.bounds())) {
         double d0 = VoxelShapes.collide(Direction.Axis.Y, entity.getBoundingBox().move(0.0D, 1.0D, 0.0D), Stream.of(voxelshape), -1.0D);
         entity.teleportTo(entity.getX(), entity.getY() + 1.0D + d0, entity.getZ());
      }

      return p_199601_1_;
   }

   public static VoxelShape box(double p_208617_0_, double p_208617_2_, double p_208617_4_, double p_208617_6_, double p_208617_8_, double p_208617_10_) {
      return VoxelShapes.box(p_208617_0_ / 16.0D, p_208617_2_ / 16.0D, p_208617_4_ / 16.0D, p_208617_6_ / 16.0D, p_208617_8_ / 16.0D, p_208617_10_ / 16.0D);
   }

   public boolean is(ITag<Block> p_203417_1_) {
      return p_203417_1_.contains(this);
   }

   public boolean is(Block p_235332_1_) {
      return this == p_235332_1_;
   }

   public static BlockState updateFromNeighbourShapes(BlockState p_199770_0_, IWorld p_199770_1_, BlockPos p_199770_2_) {
      BlockState blockstate = p_199770_0_;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(Direction direction : UPDATE_SHAPE_ORDER) {
         blockpos$mutable.setWithOffset(p_199770_2_, direction);
         blockstate = blockstate.updateShape(direction, p_199770_1_.getBlockState(blockpos$mutable), p_199770_1_, p_199770_2_, blockpos$mutable);
      }

      return blockstate;
   }

   public static void updateOrDestroy(BlockState p_196263_0_, BlockState p_196263_1_, IWorld p_196263_2_, BlockPos p_196263_3_, int p_196263_4_) {
      updateOrDestroy(p_196263_0_, p_196263_1_, p_196263_2_, p_196263_3_, p_196263_4_, 512);
   }

   public static void updateOrDestroy(BlockState p_241468_0_, BlockState p_241468_1_, IWorld p_241468_2_, BlockPos p_241468_3_, int p_241468_4_, int p_241468_5_) {
      if (p_241468_1_ != p_241468_0_) {
         if (p_241468_1_.isAir()) {
            if (!p_241468_2_.isClientSide()) {
               p_241468_2_.destroyBlock(p_241468_3_, (p_241468_4_ & 32) == 0, (Entity)null, p_241468_5_);
            }
         } else {
            p_241468_2_.setBlock(p_241468_3_, p_241468_1_, p_241468_4_ & -33, p_241468_5_);
         }
      }

   }

   public Block(AbstractBlock.Properties p_i48440_1_) {
      super(p_i48440_1_);
      StateContainer.Builder<Block, BlockState> builder = new StateContainer.Builder<>(this);
      this.createBlockStateDefinition(builder);
      this.harvestLevel = p_i48440_1_.getHarvestLevel();
      this.harvestTool = p_i48440_1_.getHarvestTool();
      this.stateDefinition = builder.create(Block::defaultBlockState, BlockState::new);
      this.registerDefaultState(this.stateDefinition.any());
   }

   public static boolean isExceptionForConnection(Block p_220073_0_) {
      return p_220073_0_ instanceof LeavesBlock || p_220073_0_ == Blocks.BARRIER || p_220073_0_ == Blocks.CARVED_PUMPKIN || p_220073_0_ == Blocks.JACK_O_LANTERN || p_220073_0_ == Blocks.MELON || p_220073_0_ == Blocks.PUMPKIN || p_220073_0_.is(BlockTags.SHULKER_BOXES);
   }

   public boolean isRandomlyTicking(BlockState p_149653_1_) {
      return this.isRandomlyTicking;
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean shouldRenderFace(BlockState p_176225_0_, IBlockReader p_176225_1_, BlockPos p_176225_2_, Direction p_176225_3_) {
      BlockPos blockpos = p_176225_2_.relative(p_176225_3_);
      BlockState blockstate = p_176225_1_.getBlockState(blockpos);
      if (p_176225_0_.skipRendering(blockstate, p_176225_3_)) {
         return false;
      } else if (blockstate.canOcclude()) {
         Block.RenderSideCacheKey block$rendersidecachekey = new Block.RenderSideCacheKey(p_176225_0_, blockstate, p_176225_3_);
         Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> object2bytelinkedopenhashmap = OCCLUSION_CACHE.get();
         byte b0 = object2bytelinkedopenhashmap.getAndMoveToFirst(block$rendersidecachekey);
         if (b0 != 127) {
            return b0 != 0;
         } else {
            VoxelShape voxelshape = p_176225_0_.getFaceOcclusionShape(p_176225_1_, p_176225_2_, p_176225_3_);
            VoxelShape voxelshape1 = blockstate.getFaceOcclusionShape(p_176225_1_, blockpos, p_176225_3_.getOpposite());
            boolean flag = VoxelShapes.joinIsNotEmpty(voxelshape, voxelshape1, IBooleanFunction.ONLY_FIRST);
            if (object2bytelinkedopenhashmap.size() == 2048) {
               object2bytelinkedopenhashmap.removeLastByte();
            }

            object2bytelinkedopenhashmap.putAndMoveToFirst(block$rendersidecachekey, (byte)(flag ? 1 : 0));
            return flag;
         }
      } else {
         return true;
      }
   }

   public static boolean canSupportRigidBlock(IBlockReader p_220064_0_, BlockPos p_220064_1_) {
      return p_220064_0_.getBlockState(p_220064_1_).isFaceSturdy(p_220064_0_, p_220064_1_, Direction.UP, BlockVoxelShape.RIGID);
   }

   public static boolean canSupportCenter(IWorldReader p_220055_0_, BlockPos p_220055_1_, Direction p_220055_2_) {
      BlockState blockstate = p_220055_0_.getBlockState(p_220055_1_);
      return p_220055_2_ == Direction.DOWN && blockstate.is(BlockTags.UNSTABLE_BOTTOM_CENTER) ? false : blockstate.isFaceSturdy(p_220055_0_, p_220055_1_, p_220055_2_, BlockVoxelShape.CENTER);
   }

   public static boolean isFaceFull(VoxelShape p_208061_0_, Direction p_208061_1_) {
      VoxelShape voxelshape = p_208061_0_.getFaceShape(p_208061_1_);
      return isShapeFullBlock(voxelshape);
   }

   public static boolean isShapeFullBlock(VoxelShape p_208062_0_) {
      return SHAPE_FULL_BLOCK_CACHE.getUnchecked(p_208062_0_);
   }

   public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
      return !isShapeFullBlock(p_200123_1_.getShape(p_200123_2_, p_200123_3_)) && p_200123_1_.getFluidState().isEmpty();
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
   }

   public void destroy(IWorld p_176206_1_, BlockPos p_176206_2_, BlockState p_176206_3_) {
   }

   public static List<ItemStack> getDrops(BlockState p_220070_0_, ServerWorld p_220070_1_, BlockPos p_220070_2_, @Nullable TileEntity p_220070_3_) {
      LootContext.Builder lootcontext$builder = (new LootContext.Builder(p_220070_1_)).withRandom(p_220070_1_.random).withParameter(LootParameters.ORIGIN, Vector3d.atCenterOf(p_220070_2_)).withParameter(LootParameters.TOOL, ItemStack.EMPTY).withOptionalParameter(LootParameters.BLOCK_ENTITY, p_220070_3_);
      return p_220070_0_.getDrops(lootcontext$builder);
   }

   public static List<ItemStack> getDrops(BlockState p_220077_0_, ServerWorld p_220077_1_, BlockPos p_220077_2_, @Nullable TileEntity p_220077_3_, @Nullable Entity p_220077_4_, ItemStack p_220077_5_) {
      LootContext.Builder lootcontext$builder = (new LootContext.Builder(p_220077_1_)).withRandom(p_220077_1_.random).withParameter(LootParameters.ORIGIN, Vector3d.atCenterOf(p_220077_2_)).withParameter(LootParameters.TOOL, p_220077_5_).withOptionalParameter(LootParameters.THIS_ENTITY, p_220077_4_).withOptionalParameter(LootParameters.BLOCK_ENTITY, p_220077_3_);
      return p_220077_0_.getDrops(lootcontext$builder);
   }

   public static void dropResources(BlockState p_220075_0_, World p_220075_1_, BlockPos p_220075_2_) {
      if (p_220075_1_ instanceof ServerWorld) {
         getDrops(p_220075_0_, (ServerWorld)p_220075_1_, p_220075_2_, (TileEntity)null).forEach((p_220079_2_) -> {
            popResource(p_220075_1_, p_220075_2_, p_220079_2_);
         });
         p_220075_0_.spawnAfterBreak((ServerWorld)p_220075_1_, p_220075_2_, ItemStack.EMPTY);
      }

   }

   public static void dropResources(BlockState p_220059_0_, IWorld p_220059_1_, BlockPos p_220059_2_, @Nullable TileEntity p_220059_3_) {
      if (p_220059_1_ instanceof ServerWorld) {
         getDrops(p_220059_0_, (ServerWorld)p_220059_1_, p_220059_2_, p_220059_3_).forEach((p_220061_2_) -> {
            popResource((ServerWorld)p_220059_1_, p_220059_2_, p_220061_2_);
         });
         p_220059_0_.spawnAfterBreak((ServerWorld)p_220059_1_, p_220059_2_, ItemStack.EMPTY);
      }

   }

   public static void dropResources(BlockState p_220054_0_, World p_220054_1_, BlockPos p_220054_2_, @Nullable TileEntity p_220054_3_, Entity p_220054_4_, ItemStack p_220054_5_) {
      if (p_220054_1_ instanceof ServerWorld) {
         getDrops(p_220054_0_, (ServerWorld)p_220054_1_, p_220054_2_, p_220054_3_, p_220054_4_, p_220054_5_).forEach((p_220057_2_) -> {
            popResource(p_220054_1_, p_220054_2_, p_220057_2_);
         });
         p_220054_0_.spawnAfterBreak((ServerWorld)p_220054_1_, p_220054_2_, p_220054_5_);
      }

   }

   public static void popResource(World p_180635_0_, BlockPos p_180635_1_, ItemStack p_180635_2_) {
      if (!p_180635_0_.isClientSide && !p_180635_2_.isEmpty() && p_180635_0_.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !p_180635_0_.restoringBlockSnapshots) {
         float f = 0.5F;
         double d0 = (double)(p_180635_0_.random.nextFloat() * 0.5F) + 0.25D;
         double d1 = (double)(p_180635_0_.random.nextFloat() * 0.5F) + 0.25D;
         double d2 = (double)(p_180635_0_.random.nextFloat() * 0.5F) + 0.25D;
         ItemEntity itementity = new ItemEntity(p_180635_0_, (double)p_180635_1_.getX() + d0, (double)p_180635_1_.getY() + d1, (double)p_180635_1_.getZ() + d2, p_180635_2_);
         itementity.setDefaultPickUpDelay();
         p_180635_0_.addFreshEntity(itementity);
      }
   }

   public void popExperience(ServerWorld p_180637_1_, BlockPos p_180637_2_, int p_180637_3_) {
      if (p_180637_1_.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !p_180637_1_.restoringBlockSnapshots) {
         while(p_180637_3_ > 0) {
            int i = ExperienceOrbEntity.getExperienceValue(p_180637_3_);
            p_180637_3_ -= i;
            p_180637_1_.addFreshEntity(new ExperienceOrbEntity(p_180637_1_, (double)p_180637_2_.getX() + 0.5D, (double)p_180637_2_.getY() + 0.5D, (double)p_180637_2_.getZ() + 0.5D, i));
         }
      }

   }

   @Deprecated //Forge: Use more sensitive version
   public float getExplosionResistance() {
      return this.explosionResistance;
   }

   public void wasExploded(World p_180652_1_, BlockPos p_180652_2_, Explosion p_180652_3_) {
   }

   public void stepOn(World p_176199_1_, BlockPos p_176199_2_, Entity p_176199_3_) {
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.defaultBlockState();
   }

   public void playerDestroy(World p_180657_1_, PlayerEntity p_180657_2_, BlockPos p_180657_3_, BlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
      p_180657_2_.awardStat(Stats.BLOCK_MINED.get(this));
      p_180657_2_.causeFoodExhaustion(0.005F);
      dropResources(p_180657_4_, p_180657_1_, p_180657_3_, p_180657_5_, p_180657_2_, p_180657_6_);
   }

   public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, @Nullable LivingEntity p_180633_4_, ItemStack p_180633_5_) {
   }

   public boolean isPossibleToRespawnInThis() {
      return !this.material.isSolid() && !this.material.isLiquid();
   }

   @OnlyIn(Dist.CLIENT)
   public IFormattableTextComponent getName() {
      return new TranslationTextComponent(this.getDescriptionId());
   }

   public String getDescriptionId() {
      if (this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("block", Registry.BLOCK.getKey(this));
      }

      return this.descriptionId;
   }

   public void fallOn(World p_180658_1_, BlockPos p_180658_2_, Entity p_180658_3_, float p_180658_4_) {
      p_180658_3_.causeFallDamage(p_180658_4_, 1.0F);
   }

   public void updateEntityAfterFallOn(IBlockReader p_176216_1_, Entity p_176216_2_) {
      p_176216_2_.setDeltaMovement(p_176216_2_.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
   }

   @Deprecated //Forge: Use more sensitive version
   public ItemStack getCloneItemStack(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      return new ItemStack(this);
   }

   public void fillItemCategory(ItemGroup p_149666_1_, NonNullList<ItemStack> p_149666_2_) {
      p_149666_2_.add(new ItemStack(this));
   }

   public float getFriction() {
      return this.friction;
   }

   public float getSpeedFactor() {
      return this.speedFactor;
   }

   public float getJumpFactor() {
      return this.jumpFactor;
   }

   public void playerWillDestroy(World p_176208_1_, BlockPos p_176208_2_, BlockState p_176208_3_, PlayerEntity p_176208_4_) {
      p_176208_1_.levelEvent(p_176208_4_, 2001, p_176208_2_, getId(p_176208_3_));
      if (this.is(BlockTags.GUARDED_BY_PIGLINS)) {
         PiglinTasks.angerNearbyPiglins(p_176208_4_, false);
      }

   }

   public void handleRain(World p_176224_1_, BlockPos p_176224_2_) {
   }

   @Deprecated //Forge: Use more sensitive version
   public boolean dropFromExplosion(Explosion p_149659_1_) {
      return true;
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
   }

   public StateContainer<Block, BlockState> getStateDefinition() {
      return this.stateDefinition;
   }

   protected final void registerDefaultState(BlockState p_180632_1_) {
      this.defaultBlockState = p_180632_1_;
   }

   public final BlockState defaultBlockState() {
      return this.defaultBlockState;
   }

   @Deprecated //Forge: Use more sensitive version {@link IForgeBlockState#getSoundType(IWorldReader, BlockPos, Entity) }
   public SoundType getSoundType(BlockState p_220072_1_) {
      return this.soundType;
   }

   public Item asItem() {
      if (this.item == null) {
         this.item = Item.byBlock(this);
      }

      return this.item.delegate.get(); //Forge: Vanilla caches the items, update with registry replacements.
   }

   public boolean hasDynamicShape() {
      return this.dynamicShape;
   }

   public String toString() {
      return "Block{" + getRegistryName() + "}";
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack p_190948_1_, @Nullable IBlockReader p_190948_2_, List<ITextComponent> p_190948_3_, ITooltipFlag p_190948_4_) {
   }

   protected Block asBlock() {
      return this;
   }

   /* ======================================== FORGE START =====================================*/
   protected Random RANDOM = new Random();
   private net.minecraftforge.common.ToolType harvestTool;
   private int harvestLevel;
   private final net.minecraftforge.common.util.ReverseTagWrapper<Block> reverseTags = new net.minecraftforge.common.util.ReverseTagWrapper<>(this, BlockTags::getAllTags);

   @Override
   public float getSlipperiness(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
      return this.friction;
   }

   @Nullable
   @Override
   public net.minecraftforge.common.ToolType getHarvestTool(BlockState state) {
      return harvestTool; //TODO: RE-Evaluate
   }

   @Override
   public int getHarvestLevel(BlockState state) {
     return harvestLevel; //TODO: RE-Evaluate
   }

   @Override
   public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable) {
      BlockState plant = plantable.getPlant(world, pos.relative(facing));
      net.minecraftforge.common.PlantType type = plantable.getPlantType(world, pos.relative(facing));

      if (plant.getBlock() == Blocks.CACTUS)
         return state.is(Blocks.CACTUS) || state.is(Blocks.SAND) || state.is(Blocks.RED_SAND);

      if (plant.getBlock() == Blocks.SUGAR_CANE && this == Blocks.SUGAR_CANE)
         return true;

      if (plantable instanceof BushBlock && ((BushBlock)plantable).mayPlaceOn(state, world, pos))
         return true;

      if (net.minecraftforge.common.PlantType.DESERT.equals(type)) {
         return this.getBlock() == Blocks.SAND || this.getBlock() == Blocks.TERRACOTTA || this.getBlock() instanceof GlazedTerracottaBlock;
      } else if (net.minecraftforge.common.PlantType.NETHER.equals(type)) {
         return this.getBlock() == Blocks.SOUL_SAND;
      } else if (net.minecraftforge.common.PlantType.CROP.equals(type)) {
         return state.is(Blocks.FARMLAND);
      } else if (net.minecraftforge.common.PlantType.CAVE.equals(type)) {
         return state.isFaceSturdy(world, pos, Direction.UP);
      } else if (net.minecraftforge.common.PlantType.PLAINS.equals(type)) {
         return this.getBlock() == Blocks.GRASS_BLOCK || net.minecraftforge.common.Tags.Blocks.DIRT.contains(this) || this.getBlock() == Blocks.FARMLAND;
      } else if (net.minecraftforge.common.PlantType.WATER.equals(type)) {
         return state.getMaterial() == net.minecraft.block.material.Material.WATER; //&& state.getValue(BlockLiquidWrapper)
      } else if (net.minecraftforge.common.PlantType.BEACH.equals(type)) {
         boolean isBeach = state.is(Blocks.GRASS_BLOCK) || net.minecraftforge.common.Tags.Blocks.DIRT.contains(this) || state.is(Blocks.SAND) || state.is(Blocks.RED_SAND);
         boolean hasWater = false;
         for (Direction face : Direction.Plane.HORIZONTAL) {
             BlockState blockState = world.getBlockState(pos.relative(face));
             net.minecraft.fluid.FluidState fluidState = world.getFluidState(pos.relative(face));
             hasWater |= blockState.is(Blocks.FROSTED_ICE);
             hasWater |= fluidState.is(net.minecraft.tags.FluidTags.WATER);
             if (hasWater)
                break; //No point continuing.
         }
         return isBeach && hasWater;
      }
      return false;
  }

  @Override
  public final java.util.Set<net.minecraft.util.ResourceLocation> getTags() {
     return reverseTags.getTagNames();
  }

  static {
      net.minecraftforge.common.ForgeHooks.setBlockToolSetter((block, tool, level) -> {
            block.harvestTool = tool;
            block.harvestLevel = level;
      });
  }
   /* ========================================= FORGE END ======================================*/

   public static final class RenderSideCacheKey {
      private final BlockState first;
      private final BlockState second;
      private final Direction direction;

      public RenderSideCacheKey(BlockState p_i49791_1_, BlockState p_i49791_2_, Direction p_i49791_3_) {
         this.first = p_i49791_1_;
         this.second = p_i49791_2_;
         this.direction = p_i49791_3_;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (!(p_equals_1_ instanceof Block.RenderSideCacheKey)) {
            return false;
         } else {
            Block.RenderSideCacheKey block$rendersidecachekey = (Block.RenderSideCacheKey)p_equals_1_;
            return this.first == block$rendersidecachekey.first && this.second == block$rendersidecachekey.second && this.direction == block$rendersidecachekey.direction;
         }
      }

      public int hashCode() {
         int i = this.first.hashCode();
         i = 31 * i + this.second.hashCode();
         return 31 * i + this.direction.hashCode();
      }
   }
}
