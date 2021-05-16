package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ComposterBlock extends Block implements ISidedInventoryProvider {
   public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_COMPOSTER;
   public static final Object2FloatMap<IItemProvider> COMPOSTABLES = new Object2FloatOpenHashMap<>();
   private static final VoxelShape OUTER_SHAPE = VoxelShapes.block();
   private static final VoxelShape[] SHAPES = Util.make(new VoxelShape[9], (p_220291_0_) -> {
      for(int i = 0; i < 8; ++i) {
         p_220291_0_[i] = VoxelShapes.join(OUTER_SHAPE, Block.box(2.0D, (double)Math.max(2, 1 + i * 2), 2.0D, 14.0D, 16.0D, 14.0D), IBooleanFunction.ONLY_FIRST);
      }

      p_220291_0_[8] = p_220291_0_[7];
   });

   public static void bootStrap() {
      COMPOSTABLES.defaultReturnValue(-1.0F);
      float f = 0.3F;
      float f1 = 0.5F;
      float f2 = 0.65F;
      float f3 = 0.85F;
      float f4 = 1.0F;
      add(0.3F, Items.JUNGLE_LEAVES);
      add(0.3F, Items.OAK_LEAVES);
      add(0.3F, Items.SPRUCE_LEAVES);
      add(0.3F, Items.DARK_OAK_LEAVES);
      add(0.3F, Items.ACACIA_LEAVES);
      add(0.3F, Items.BIRCH_LEAVES);
      add(0.3F, Items.OAK_SAPLING);
      add(0.3F, Items.SPRUCE_SAPLING);
      add(0.3F, Items.BIRCH_SAPLING);
      add(0.3F, Items.JUNGLE_SAPLING);
      add(0.3F, Items.ACACIA_SAPLING);
      add(0.3F, Items.DARK_OAK_SAPLING);
      add(0.3F, Items.BEETROOT_SEEDS);
      add(0.3F, Items.DRIED_KELP);
      add(0.3F, Items.GRASS);
      add(0.3F, Items.KELP);
      add(0.3F, Items.MELON_SEEDS);
      add(0.3F, Items.PUMPKIN_SEEDS);
      add(0.3F, Items.SEAGRASS);
      add(0.3F, Items.SWEET_BERRIES);
      add(0.3F, Items.WHEAT_SEEDS);
      add(0.5F, Items.DRIED_KELP_BLOCK);
      add(0.5F, Items.TALL_GRASS);
      add(0.5F, Items.CACTUS);
      add(0.5F, Items.SUGAR_CANE);
      add(0.5F, Items.VINE);
      add(0.5F, Items.NETHER_SPROUTS);
      add(0.5F, Items.WEEPING_VINES);
      add(0.5F, Items.TWISTING_VINES);
      add(0.5F, Items.MELON_SLICE);
      add(0.65F, Items.SEA_PICKLE);
      add(0.65F, Items.LILY_PAD);
      add(0.65F, Items.PUMPKIN);
      add(0.65F, Items.CARVED_PUMPKIN);
      add(0.65F, Items.MELON);
      add(0.65F, Items.APPLE);
      add(0.65F, Items.BEETROOT);
      add(0.65F, Items.CARROT);
      add(0.65F, Items.COCOA_BEANS);
      add(0.65F, Items.POTATO);
      add(0.65F, Items.WHEAT);
      add(0.65F, Items.BROWN_MUSHROOM);
      add(0.65F, Items.RED_MUSHROOM);
      add(0.65F, Items.MUSHROOM_STEM);
      add(0.65F, Items.CRIMSON_FUNGUS);
      add(0.65F, Items.WARPED_FUNGUS);
      add(0.65F, Items.NETHER_WART);
      add(0.65F, Items.CRIMSON_ROOTS);
      add(0.65F, Items.WARPED_ROOTS);
      add(0.65F, Items.SHROOMLIGHT);
      add(0.65F, Items.DANDELION);
      add(0.65F, Items.POPPY);
      add(0.65F, Items.BLUE_ORCHID);
      add(0.65F, Items.ALLIUM);
      add(0.65F, Items.AZURE_BLUET);
      add(0.65F, Items.RED_TULIP);
      add(0.65F, Items.ORANGE_TULIP);
      add(0.65F, Items.WHITE_TULIP);
      add(0.65F, Items.PINK_TULIP);
      add(0.65F, Items.OXEYE_DAISY);
      add(0.65F, Items.CORNFLOWER);
      add(0.65F, Items.LILY_OF_THE_VALLEY);
      add(0.65F, Items.WITHER_ROSE);
      add(0.65F, Items.FERN);
      add(0.65F, Items.SUNFLOWER);
      add(0.65F, Items.LILAC);
      add(0.65F, Items.ROSE_BUSH);
      add(0.65F, Items.PEONY);
      add(0.65F, Items.LARGE_FERN);
      add(0.85F, Items.HAY_BLOCK);
      add(0.85F, Items.BROWN_MUSHROOM_BLOCK);
      add(0.85F, Items.RED_MUSHROOM_BLOCK);
      add(0.85F, Items.NETHER_WART_BLOCK);
      add(0.85F, Items.WARPED_WART_BLOCK);
      add(0.85F, Items.BREAD);
      add(0.85F, Items.BAKED_POTATO);
      add(0.85F, Items.COOKIE);
      add(1.0F, Items.CAKE);
      add(1.0F, Items.PUMPKIN_PIE);
   }

   private static void add(float p_220290_0_, IItemProvider p_220290_1_) {
      COMPOSTABLES.put(p_220290_1_.asItem(), p_220290_0_);
   }

   public ComposterBlock(AbstractBlock.Properties p_i49986_1_) {
      super(p_i49986_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, Integer.valueOf(0)));
   }

   @OnlyIn(Dist.CLIENT)
   public static void handleFill(World p_220292_0_, BlockPos p_220292_1_, boolean p_220292_2_) {
      BlockState blockstate = p_220292_0_.getBlockState(p_220292_1_);
      p_220292_0_.playLocalSound((double)p_220292_1_.getX(), (double)p_220292_1_.getY(), (double)p_220292_1_.getZ(), p_220292_2_ ? SoundEvents.COMPOSTER_FILL_SUCCESS : SoundEvents.COMPOSTER_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
      double d0 = blockstate.getShape(p_220292_0_, p_220292_1_).max(Direction.Axis.Y, 0.5D, 0.5D) + 0.03125D;
      double d1 = (double)0.13125F;
      double d2 = (double)0.7375F;
      Random random = p_220292_0_.getRandom();

      for(int i = 0; i < 10; ++i) {
         double d3 = random.nextGaussian() * 0.02D;
         double d4 = random.nextGaussian() * 0.02D;
         double d5 = random.nextGaussian() * 0.02D;
         p_220292_0_.addParticle(ParticleTypes.COMPOSTER, (double)p_220292_1_.getX() + (double)0.13125F + (double)0.7375F * (double)random.nextFloat(), (double)p_220292_1_.getY() + d0 + (double)random.nextFloat() * (1.0D - d0), (double)p_220292_1_.getZ() + (double)0.13125F + (double)0.7375F * (double)random.nextFloat(), d3, d4, d5);
      }

   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPES[p_220053_1_.getValue(LEVEL)];
   }

   public VoxelShape getInteractionShape(BlockState p_199600_1_, IBlockReader p_199600_2_, BlockPos p_199600_3_) {
      return OUTER_SHAPE;
   }

   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      return SHAPES[0];
   }

   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (p_220082_1_.getValue(LEVEL) == 7) {
         p_220082_2_.getBlockTicks().scheduleTick(p_220082_3_, p_220082_1_.getBlock(), 20);
      }

   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      int i = p_225533_1_.getValue(LEVEL);
      ItemStack itemstack = p_225533_4_.getItemInHand(p_225533_5_);
      if (i < 8 && COMPOSTABLES.containsKey(itemstack.getItem())) {
         if (i < 7 && !p_225533_2_.isClientSide) {
            BlockState blockstate = addItem(p_225533_1_, p_225533_2_, p_225533_3_, itemstack);
            p_225533_2_.levelEvent(1500, p_225533_3_, p_225533_1_ != blockstate ? 1 : 0);
            if (!p_225533_4_.abilities.instabuild) {
               itemstack.shrink(1);
            }
         }

         return ActionResultType.sidedSuccess(p_225533_2_.isClientSide);
      } else if (i == 8) {
         extractProduce(p_225533_1_, p_225533_2_, p_225533_3_);
         return ActionResultType.sidedSuccess(p_225533_2_.isClientSide);
      } else {
         return ActionResultType.PASS;
      }
   }

   public static BlockState insertItem(BlockState p_235486_0_, ServerWorld p_235486_1_, ItemStack p_235486_2_, BlockPos p_235486_3_) {
      int i = p_235486_0_.getValue(LEVEL);
      if (i < 7 && COMPOSTABLES.containsKey(p_235486_2_.getItem())) {
         BlockState blockstate = addItem(p_235486_0_, p_235486_1_, p_235486_3_, p_235486_2_);
         p_235486_2_.shrink(1);
         return blockstate;
      } else {
         return p_235486_0_;
      }
   }

   public static BlockState extractProduce(BlockState p_235489_0_, World p_235489_1_, BlockPos p_235489_2_) {
      if (!p_235489_1_.isClientSide) {
         float f = 0.7F;
         double d0 = (double)(p_235489_1_.random.nextFloat() * 0.7F) + (double)0.15F;
         double d1 = (double)(p_235489_1_.random.nextFloat() * 0.7F) + (double)0.060000002F + 0.6D;
         double d2 = (double)(p_235489_1_.random.nextFloat() * 0.7F) + (double)0.15F;
         ItemEntity itementity = new ItemEntity(p_235489_1_, (double)p_235489_2_.getX() + d0, (double)p_235489_2_.getY() + d1, (double)p_235489_2_.getZ() + d2, new ItemStack(Items.BONE_MEAL));
         itementity.setDefaultPickUpDelay();
         p_235489_1_.addFreshEntity(itementity);
      }

      BlockState blockstate = empty(p_235489_0_, p_235489_1_, p_235489_2_);
      p_235489_1_.playSound((PlayerEntity)null, p_235489_2_, SoundEvents.COMPOSTER_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
      return blockstate;
   }

   private static BlockState empty(BlockState p_235490_0_, IWorld p_235490_1_, BlockPos p_235490_2_) {
      BlockState blockstate = p_235490_0_.setValue(LEVEL, Integer.valueOf(0));
      p_235490_1_.setBlock(p_235490_2_, blockstate, 3);
      return blockstate;
   }

   private static BlockState addItem(BlockState p_235487_0_, IWorld p_235487_1_, BlockPos p_235487_2_, ItemStack p_235487_3_) {
      int i = p_235487_0_.getValue(LEVEL);
      float f = COMPOSTABLES.getFloat(p_235487_3_.getItem());
      if ((i != 0 || !(f > 0.0F)) && !(p_235487_1_.getRandom().nextDouble() < (double)f)) {
         return p_235487_0_;
      } else {
         int j = i + 1;
         BlockState blockstate = p_235487_0_.setValue(LEVEL, Integer.valueOf(j));
         p_235487_1_.setBlock(p_235487_2_, blockstate, 3);
         if (j == 7) {
            p_235487_1_.getBlockTicks().scheduleTick(p_235487_2_, p_235487_0_.getBlock(), 20);
         }

         return blockstate;
      }
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (p_225534_1_.getValue(LEVEL) == 7) {
         p_225534_2_.setBlock(p_225534_3_, p_225534_1_.cycle(LEVEL), 3);
         p_225534_2_.playSound((PlayerEntity)null, p_225534_3_, SoundEvents.COMPOSTER_READY, SoundCategory.BLOCKS, 1.0F, 1.0F);
      }

   }

   public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return p_180641_1_.getValue(LEVEL);
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(LEVEL);
   }

   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   public ISidedInventory getContainer(BlockState p_219966_1_, IWorld p_219966_2_, BlockPos p_219966_3_) {
      int i = p_219966_1_.getValue(LEVEL);
      if (i == 8) {
         return new ComposterBlock.FullInventory(p_219966_1_, p_219966_2_, p_219966_3_, new ItemStack(Items.BONE_MEAL));
      } else {
         return (ISidedInventory)(i < 7 ? new ComposterBlock.PartialInventory(p_219966_1_, p_219966_2_, p_219966_3_) : new ComposterBlock.EmptyInventory());
      }
   }

   static class EmptyInventory extends Inventory implements ISidedInventory {
      public EmptyInventory() {
         super(0);
      }

      public int[] getSlotsForFace(Direction p_180463_1_) {
         return new int[0];
      }

      public boolean canPlaceItemThroughFace(int p_180462_1_, ItemStack p_180462_2_, @Nullable Direction p_180462_3_) {
         return false;
      }

      public boolean canTakeItemThroughFace(int p_180461_1_, ItemStack p_180461_2_, Direction p_180461_3_) {
         return false;
      }
   }

   static class FullInventory extends Inventory implements ISidedInventory {
      private final BlockState state;
      private final IWorld level;
      private final BlockPos pos;
      private boolean changed;

      public FullInventory(BlockState p_i50463_1_, IWorld p_i50463_2_, BlockPos p_i50463_3_, ItemStack p_i50463_4_) {
         super(p_i50463_4_);
         this.state = p_i50463_1_;
         this.level = p_i50463_2_;
         this.pos = p_i50463_3_;
      }

      public int getMaxStackSize() {
         return 1;
      }

      public int[] getSlotsForFace(Direction p_180463_1_) {
         return p_180463_1_ == Direction.DOWN ? new int[]{0} : new int[0];
      }

      public boolean canPlaceItemThroughFace(int p_180462_1_, ItemStack p_180462_2_, @Nullable Direction p_180462_3_) {
         return false;
      }

      public boolean canTakeItemThroughFace(int p_180461_1_, ItemStack p_180461_2_, Direction p_180461_3_) {
         return !this.changed && p_180461_3_ == Direction.DOWN && p_180461_2_.getItem() == Items.BONE_MEAL;
      }

      public void setChanged() {
         ComposterBlock.empty(this.state, this.level, this.pos);
         this.changed = true;
      }
   }

   static class PartialInventory extends Inventory implements ISidedInventory {
      private final BlockState state;
      private final IWorld level;
      private final BlockPos pos;
      private boolean changed;

      public PartialInventory(BlockState p_i50464_1_, IWorld p_i50464_2_, BlockPos p_i50464_3_) {
         super(1);
         this.state = p_i50464_1_;
         this.level = p_i50464_2_;
         this.pos = p_i50464_3_;
      }

      public int getMaxStackSize() {
         return 1;
      }

      public int[] getSlotsForFace(Direction p_180463_1_) {
         return p_180463_1_ == Direction.UP ? new int[]{0} : new int[0];
      }

      public boolean canPlaceItemThroughFace(int p_180462_1_, ItemStack p_180462_2_, @Nullable Direction p_180462_3_) {
         return !this.changed && p_180462_3_ == Direction.UP && ComposterBlock.COMPOSTABLES.containsKey(p_180462_2_.getItem());
      }

      public boolean canTakeItemThroughFace(int p_180461_1_, ItemStack p_180461_2_, Direction p_180461_3_) {
         return false;
      }

      public void setChanged() {
         ItemStack itemstack = this.getItem(0);
         if (!itemstack.isEmpty()) {
            this.changed = true;
            BlockState blockstate = ComposterBlock.addItem(this.state, this.level, this.pos, itemstack);
            this.level.levelEvent(1500, this.pos, blockstate != this.state ? 1 : 0);
            this.removeItemNoUpdate(0);
         }

      }
   }
}
