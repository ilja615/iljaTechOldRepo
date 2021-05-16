package net.minecraft.world;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GrassColors {
   private static int[] pixels = new int[65536];

   public static void init(int[] p_77479_0_) {
      pixels = p_77479_0_;
   }

   public static int get(double p_77480_0_, double p_77480_2_) {
      p_77480_2_ = p_77480_2_ * p_77480_0_;
      int i = (int)((1.0D - p_77480_0_) * 255.0D);
      int j = (int)((1.0D - p_77480_2_) * 255.0D);
      int k = j << 8 | i;
      return k > pixels.length ? -65281 : pixels[k];
   }
}
