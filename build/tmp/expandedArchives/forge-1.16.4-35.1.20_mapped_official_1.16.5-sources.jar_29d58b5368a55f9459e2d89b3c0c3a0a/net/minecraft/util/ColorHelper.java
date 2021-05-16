package net.minecraft.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ColorHelper {
   public static class PackedColor {
      @OnlyIn(Dist.CLIENT)
      public static int alpha(int p_233004_0_) {
         return p_233004_0_ >>> 24;
      }

      public static int red(int p_233007_0_) {
         return p_233007_0_ >> 16 & 255;
      }

      public static int green(int p_233008_0_) {
         return p_233008_0_ >> 8 & 255;
      }

      public static int blue(int p_233009_0_) {
         return p_233009_0_ & 255;
      }

      @OnlyIn(Dist.CLIENT)
      public static int color(int p_233006_0_, int p_233006_1_, int p_233006_2_, int p_233006_3_) {
         return p_233006_0_ << 24 | p_233006_1_ << 16 | p_233006_2_ << 8 | p_233006_3_;
      }

      @OnlyIn(Dist.CLIENT)
      public static int multiply(int p_233005_0_, int p_233005_1_) {
         return color(alpha(p_233005_0_) * alpha(p_233005_1_) / 255, red(p_233005_0_) * red(p_233005_1_) / 255, green(p_233005_0_) * green(p_233005_1_) / 255, blue(p_233005_0_) * blue(p_233005_1_) / 255);
      }
   }
}
