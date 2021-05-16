package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WetSpongeBlock extends Block {
   public WetSpongeBlock(AbstractBlock.Properties p_i48294_1_) {
      super(p_i48294_1_);
   }

   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (p_220082_2_.dimensionType().ultraWarm()) {
         p_220082_2_.setBlock(p_220082_3_, Blocks.SPONGE.defaultBlockState(), 3);
         p_220082_2_.levelEvent(2009, p_220082_3_, 0);
         p_220082_2_.playSound((PlayerEntity)null, p_220082_3_, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, (1.0F + p_220082_2_.getRandom().nextFloat() * 0.2F) * 0.7F);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      Direction direction = Direction.getRandom(p_180655_4_);
      if (direction != Direction.UP) {
         BlockPos blockpos = p_180655_3_.relative(direction);
         BlockState blockstate = p_180655_2_.getBlockState(blockpos);
         if (!p_180655_1_.canOcclude() || !blockstate.isFaceSturdy(p_180655_2_, blockpos, direction.getOpposite())) {
            double d0 = (double)p_180655_3_.getX();
            double d1 = (double)p_180655_3_.getY();
            double d2 = (double)p_180655_3_.getZ();
            if (direction == Direction.DOWN) {
               d1 = d1 - 0.05D;
               d0 += p_180655_4_.nextDouble();
               d2 += p_180655_4_.nextDouble();
            } else {
               d1 = d1 + p_180655_4_.nextDouble() * 0.8D;
               if (direction.getAxis() == Direction.Axis.X) {
                  d2 += p_180655_4_.nextDouble();
                  if (direction == Direction.EAST) {
                     ++d0;
                  } else {
                     d0 += 0.05D;
                  }
               } else {
                  d0 += p_180655_4_.nextDouble();
                  if (direction == Direction.SOUTH) {
                     ++d2;
                  } else {
                     d2 += 0.05D;
                  }
               }
            }

            p_180655_2_.addParticle(ParticleTypes.DRIPPING_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
         }
      }
   }
}
