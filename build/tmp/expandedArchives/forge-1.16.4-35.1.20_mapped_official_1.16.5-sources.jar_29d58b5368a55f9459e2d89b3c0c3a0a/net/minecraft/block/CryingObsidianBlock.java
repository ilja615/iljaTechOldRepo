package net.minecraft.block;

import java.util.Random;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CryingObsidianBlock extends Block {
   public CryingObsidianBlock(AbstractBlock.Properties p_i241176_1_) {
      super(p_i241176_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (p_180655_4_.nextInt(5) == 0) {
         Direction direction = Direction.getRandom(p_180655_4_);
         if (direction != Direction.UP) {
            BlockPos blockpos = p_180655_3_.relative(direction);
            BlockState blockstate = p_180655_2_.getBlockState(blockpos);
            if (!p_180655_1_.canOcclude() || !blockstate.isFaceSturdy(p_180655_2_, blockpos, direction.getOpposite())) {
               double d0 = direction.getStepX() == 0 ? p_180655_4_.nextDouble() : 0.5D + (double)direction.getStepX() * 0.6D;
               double d1 = direction.getStepY() == 0 ? p_180655_4_.nextDouble() : 0.5D + (double)direction.getStepY() * 0.6D;
               double d2 = direction.getStepZ() == 0 ? p_180655_4_.nextDouble() : 0.5D + (double)direction.getStepZ() * 0.6D;
               p_180655_2_.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, (double)p_180655_3_.getX() + d0, (double)p_180655_3_.getY() + d1, (double)p_180655_3_.getZ() + d2, 0.0D, 0.0D, 0.0D);
            }
         }
      }
   }
}
