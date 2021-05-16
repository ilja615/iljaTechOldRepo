package net.minecraft.profiler;

import java.io.File;
import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IProfileResult {
   @OnlyIn(Dist.CLIENT)
   List<DataPoint> getTimes(String p_219917_1_);

   boolean saveResults(File p_219919_1_);

   long getStartTimeNano();

   int getStartTimeTicks();

   long getEndTimeNano();

   int getEndTimeTicks();

   default long getNanoDuration() {
      return this.getEndTimeNano() - this.getStartTimeNano();
   }

   default int getTickDuration() {
      return this.getEndTimeTicks() - this.getStartTimeTicks();
   }

   static String demanglePath(String p_225434_0_) {
      return p_225434_0_.replace('\u001e', '.');
   }
}
