package net.minecraft.world;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IDayTimeReader extends IWorldReader {
   long dayTime();

   default float getMoonBrightness() {
      return DimensionType.MOON_BRIGHTNESS_PER_PHASE[this.dimensionType().moonPhase(this.dayTime())];
   }

   default float getTimeOfDay(float p_242415_1_) {
      return this.dimensionType().timeOfDay(this.dayTime());
   }

   @OnlyIn(Dist.CLIENT)
   default int getMoonPhase() {
      return this.dimensionType().moonPhase(this.dayTime());
   }
}
