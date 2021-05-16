package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FallingBlock extends Block {
   public FallingBlock(AbstractBlock.Properties p_i48401_1_) {
      super(p_i48401_1_);
   }

   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      p_220082_2_.getBlockTicks().scheduleTick(p_220082_3_, this, this.getDelayAfterPlace());
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      p_196271_4_.getBlockTicks().scheduleTick(p_196271_5_, this, this.getDelayAfterPlace());
      return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (p_225534_2_.isEmptyBlock(p_225534_3_.below()) || isFree(p_225534_2_.getBlockState(p_225534_3_.below())) && p_225534_3_.getY() >= 0) {
         FallingBlockEntity fallingblockentity = new FallingBlockEntity(p_225534_2_, (double)p_225534_3_.getX() + 0.5D, (double)p_225534_3_.getY(), (double)p_225534_3_.getZ() + 0.5D, p_225534_2_.getBlockState(p_225534_3_));
         this.falling(fallingblockentity);
         p_225534_2_.addFreshEntity(fallingblockentity);
      }
   }

   protected void falling(FallingBlockEntity p_149829_1_) {
   }

   protected int getDelayAfterPlace() {
      return 2;
   }

   public static boolean isFree(BlockState p_185759_0_) {
      Material material = p_185759_0_.getMaterial();
      return p_185759_0_.isAir() || p_185759_0_.is(BlockTags.FIRE) || material.isLiquid() || material.isReplaceable();
   }

   public void onLand(World p_176502_1_, BlockPos p_176502_2_, BlockState p_176502_3_, BlockState p_176502_4_, FallingBlockEntity p_176502_5_) {
   }

   public void onBroken(World p_190974_1_, BlockPos p_190974_2_, FallingBlockEntity p_190974_3_) {
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (p_180655_4_.nextInt(16) == 0) {
         BlockPos blockpos = p_180655_3_.below();
         if (p_180655_2_.isEmptyBlock(blockpos) || isFree(p_180655_2_.getBlockState(blockpos))) {
            double d0 = (double)p_180655_3_.getX() + p_180655_4_.nextDouble();
            double d1 = (double)p_180655_3_.getY() - 0.05D;
            double d2 = (double)p_180655_3_.getZ() + p_180655_4_.nextDouble();
            p_180655_2_.addParticle(new BlockParticleData(ParticleTypes.FALLING_DUST, p_180655_1_), d0, d1, d2, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public int getDustColor(BlockState p_189876_1_, IBlockReader p_189876_2_, BlockPos p_189876_3_) {
      return -16777216;
   }
}
