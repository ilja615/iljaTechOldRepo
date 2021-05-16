package net.minecraft.world;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FoliageColors {
   private static int[] pixels = new int[65536];

   public static void init(int[] p_77467_0_) {
      pixels = p_77467_0_;
   }

   public static int get(double p_77470_0_, double p_77470_2_) {
      p_77470_2_ = p_77470_2_ * p_77470_0_;
      int i = (int)((1.0D - p_77470_0_) * 255.0D);
      int j = (int)((1.0D - p_77470_2_) * 255.0D);
      return pixels[j << 8 | i];
   }

   public static int getEvergreenColor() {
      return 6396257;
   }

   public static int getBirchColor() {
      return 8431445;
   }

   public static int getDefaultColor() {
      return 4764952;
   }
}
