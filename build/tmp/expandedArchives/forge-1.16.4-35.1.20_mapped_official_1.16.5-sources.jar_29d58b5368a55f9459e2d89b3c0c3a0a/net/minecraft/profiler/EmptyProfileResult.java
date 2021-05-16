package net.minecraft.profiler;

import java.io.File;
import java.util.Collections;
import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EmptyProfileResult implements IProfileResult {
   public static final EmptyProfileResult EMPTY = new EmptyProfileResult();

   private EmptyProfileResult() {
   }

   @OnlyIn(Dist.CLIENT)
   public List<DataPoint> getTimes(String p_219917_1_) {
      return Collections.emptyList();
   }

   public boolean saveResults(File p_219919_1_) {
      return false;
   }

   public long getStartTimeNano() {
      return 0L;
   }

   public int getStartTimeTicks() {
      return 0;
   }

   public long getEndTimeNano() {
      return 0L;
   }

   public int getEndTimeTicks() {
      return 0;
   }
}
