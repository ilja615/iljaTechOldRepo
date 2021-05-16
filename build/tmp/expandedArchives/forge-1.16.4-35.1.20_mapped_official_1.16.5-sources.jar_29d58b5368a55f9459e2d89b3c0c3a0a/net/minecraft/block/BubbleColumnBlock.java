package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BubbleColumnBlock extends Block implements IBucketPickupHandler {
   public static final BooleanProperty DRAG_DOWN = BlockStateProperties.DRAG;

   public BubbleColumnBlock(AbstractBlock.Properties p_i48783_1_) {
      super(p_i48783_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(DRAG_DOWN, Boolean.valueOf(true)));
   }

   public void entityInside(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      BlockState blockstate = p_196262_2_.getBlockState(p_196262_3_.above());
      if (blockstate.isAir()) {
         p_196262_4_.onAboveBubbleCol(p_196262_1_.getValue(DRAG_DOWN));
         if (!p_196262_2_.isClientSide) {
            ServerWorld serverworld = (ServerWorld)p_196262_2_;

            for(int i = 0; i < 2; ++i) {
               serverworld.sendParticles(ParticleTypes.SPLASH, (double)p_196262_3_.getX() + p_196262_2_.random.nextDouble(), (double)(p_196262_3_.getY() + 1), (double)p_196262_3_.getZ() + p_196262_2_.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
               serverworld.sendParticles(ParticleTypes.BUBBLE, (double)p_196262_3_.getX() + p_196262_2_.random.nextDouble(), (double)(p_196262_3_.getY() + 1), (double)p_196262_3_.getZ() + p_196262_2_.random.nextDouble(), 1, 0.0D, 0.01D, 0.0D, 0.2D);
            }
         }
      } else {
         p_196262_4_.onInsideBubbleColumn(p_196262_1_.getValue(DRAG_DOWN));
      }

   }

   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      growColumn(p_220082_2_, p_220082_3_.above(), getDrag(p_220082_2_, p_220082_3_.below()));
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      growColumn(p_225534_2_, p_225534_3_.above(), getDrag(p_225534_2_, p_225534_3_));
   }

   public FluidState getFluidState(BlockState p_204507_1_) {
      return Fluids.WATER.getSource(false);
   }

   public static void growColumn(IWorld p_203159_0_, BlockPos p_203159_1_, boolean p_203159_2_) {
      if (canExistIn(p_203159_0_, p_203159_1_)) {
         p_203159_0_.setBlock(p_203159_1_, Blocks.BUBBLE_COLUMN.defaultBlockState().setValue(DRAG_DOWN, Boolean.valueOf(p_203159_2_)), 2);
      }

   }

   public static boolean canExistIn(IWorld p_208072_0_, BlockPos p_208072_1_) {
      FluidState fluidstate = p_208072_0_.getFluidState(p_208072_1_);
      return p_208072_0_.getBlockState(p_208072_1_).is(Blocks.WATER) && fluidstate.getAmount() >= 8 && fluidstate.isSource();
   }

   private static boolean getDrag(IBlockReader p_203157_0_, BlockPos p_203157_1_) {
      BlockState blockstate = p_203157_0_.getBlockState(p_203157_1_);
      if (blockstate.is(Blocks.BUBBLE_COLUMN)) {
         return blockstate.getValue(DRAG_DOWN);
      } else {
         return !blockstate.is(Blocks.SOUL_SAND);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      double d0 = (double)p_180655_3_.getX();
      double d1 = (double)p_180655_3_.getY();
      double d2 = (double)p_180655_3_.getZ();
      if (p_180655_1_.getValue(DRAG_DOWN)) {
         p_180655_2_.addAlwaysVisibleParticle(ParticleTypes.CURRENT_DOWN, d0 + 0.5D, d1 + 0.8D, d2, 0.0D, 0.0D, 0.0D);
         if (p_180655_4_.nextInt(200) == 0) {
            p_180655_2_.playLocalSound(d0, d1, d2, SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundCategory.BLOCKS, 0.2F + p_180655_4_.nextFloat() * 0.2F, 0.9F + p_180655_4_.nextFloat() * 0.15F, false);
         }
      } else {
         p_180655_2_.addAlwaysVisibleParticle(ParticleTypes.BUBBLE_COLUMN_UP, d0 + 0.5D, d1, d2 + 0.5D, 0.0D, 0.04D, 0.0D);
         p_180655_2_.addAlwaysVisibleParticle(ParticleTypes.BUBBLE_COLUMN_UP, d0 + (double)p_180655_4_.nextFloat(), d1 + (double)p_180655_4_.nextFloat(), d2 + (double)p_180655_4_.nextFloat(), 0.0D, 0.04D, 0.0D);
         if (p_180655_4_.nextInt(200) == 0) {
            p_180655_2_.playLocalSound(d0, d1, d2, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.2F + p_180655_4_.nextFloat() * 0.2F, 0.9F + p_180655_4_.nextFloat() * 0.15F, false);
         }
      }

   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (!p_196271_1_.canSurvive(p_196271_4_, p_196271_5_)) {
         return Blocks.WATER.defaultBlockState();
      } else {
         if (p_196271_2_ == Direction.DOWN) {
            p_196271_4_.setBlock(p_196271_5_, Blocks.BUBBLE_COLUMN.defaultBlockState().setValue(DRAG_DOWN, Boolean.valueOf(getDrag(p_196271_4_, p_196271_6_))), 2);
         } else if (p_196271_2_ == Direction.UP && !p_196271_3_.is(Blocks.BUBBLE_COLUMN) && canExistIn(p_196271_4_, p_196271_6_)) {
            p_196271_4_.getBlockTicks().scheduleTick(p_196271_5_, this, 5);
         }

         p_196271_4_.getLiquidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickDelay(p_196271_4_));
         return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      }
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockState blockstate = p_196260_2_.getBlockState(p_196260_3_.below());
      return blockstate.is(Blocks.BUBBLE_COLUMN) || blockstate.is(Blocks.MAGMA_BLOCK) || blockstate.is(Blocks.SOUL_SAND);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return VoxelShapes.empty();
   }

   public BlockRenderType getRenderShape(BlockState p_149645_1_) {
      return BlockRenderType.INVISIBLE;
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(DRAG_DOWN);
   }

   public Fluid takeLiquid(IWorld p_204508_1_, BlockPos p_204508_2_, BlockState p_204508_3_) {
      p_204508_1_.setBlock(p_204508_2_, Blocks.AIR.defaultBlockState(), 11);
      return Fluids.WATER;
   }
}
