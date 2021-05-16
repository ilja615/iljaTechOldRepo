package net.minecraft.world.spawner;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.util.math.BlockPos;

public class MobDensityTracker {
   private final List<MobDensityTracker.DensityEntry> charges = Lists.newArrayList();

   public void addCharge(BlockPos p_234998_1_, double p_234998_2_) {
      if (p_234998_2_ != 0.0D) {
         this.charges.add(new MobDensityTracker.DensityEntry(p_234998_1_, p_234998_2_));
      }

   }

   public double getPotentialEnergyChange(BlockPos p_234999_1_, double p_234999_2_) {
      if (p_234999_2_ == 0.0D) {
         return 0.0D;
      } else {
         double d0 = 0.0D;

         for(MobDensityTracker.DensityEntry mobdensitytracker$densityentry : this.charges) {
            d0 += mobdensitytracker$densityentry.getPotentialChange(p_234999_1_);
         }

         return d0 * p_234999_2_;
      }
   }

   static class DensityEntry {
      private final BlockPos pos;
      private final double charge;

      public DensityEntry(BlockPos p_i231624_1_, double p_i231624_2_) {
         this.pos = p_i231624_1_;
         this.charge = p_i231624_2_;
      }

      public double getPotentialChange(BlockPos p_235002_1_) {
         double d0 = this.pos.distSqr(p_235002_1_);
         return d0 == 0.0D ? Double.POSITIVE_INFINITY : this.charge / Math.sqrt(d0);
      }
   }
}
