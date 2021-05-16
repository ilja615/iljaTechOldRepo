package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class DragonEggBlock extends FallingBlock {
   protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

   public DragonEggBlock(AbstractBlock.Properties p_i48411_1_) {
      super(p_i48411_1_);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      this.teleport(p_225533_1_, p_225533_2_, p_225533_3_);
      return ActionResultType.sidedSuccess(p_225533_2_.isClientSide);
   }

   public void attack(BlockState p_196270_1_, World p_196270_2_, BlockPos p_196270_3_, PlayerEntity p_196270_4_) {
      this.teleport(p_196270_1_, p_196270_2_, p_196270_3_);
   }

   private void teleport(BlockState p_196443_1_, World p_196443_2_, BlockPos p_196443_3_) {
      for(int i = 0; i < 1000; ++i) {
         BlockPos blockpos = p_196443_3_.offset(p_196443_2_.random.nextInt(16) - p_196443_2_.random.nextInt(16), p_196443_2_.random.nextInt(8) - p_196443_2_.random.nextInt(8), p_196443_2_.random.nextInt(16) - p_196443_2_.random.nextInt(16));
         if (p_196443_2_.getBlockState(blockpos).isAir()) {
            if (p_196443_2_.isClientSide) {
               for(int j = 0; j < 128; ++j) {
                  double d0 = p_196443_2_.random.nextDouble();
                  float f = (p_196443_2_.random.nextFloat() - 0.5F) * 0.2F;
                  float f1 = (p_196443_2_.random.nextFloat() - 0.5F) * 0.2F;
                  float f2 = (p_196443_2_.random.nextFloat() - 0.5F) * 0.2F;
                  double d1 = MathHelper.lerp(d0, (double)blockpos.getX(), (double)p_196443_3_.getX()) + (p_196443_2_.random.nextDouble() - 0.5D) + 0.5D;
                  double d2 = MathHelper.lerp(d0, (double)blockpos.getY(), (double)p_196443_3_.getY()) + p_196443_2_.random.nextDouble() - 0.5D;
                  double d3 = MathHelper.lerp(d0, (double)blockpos.getZ(), (double)p_196443_3_.getZ()) + (p_196443_2_.random.nextDouble() - 0.5D) + 0.5D;
                  p_196443_2_.addParticle(ParticleTypes.PORTAL, d1, d2, d3, (double)f, (double)f1, (double)f2);
               }
            } else {
               p_196443_2_.setBlock(blockpos, p_196443_1_, 2);
               p_196443_2_.removeBlock(p_196443_3_, false);
            }

            return;
         }
      }

   }

   protected int getDelayAfterPlace() {
      return 5;
   }

   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}
