package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public class EdgeLayer {
   public static enum CoolWarm implements ICastleTransformer {
      INSTANCE;

      public int apply(INoiseRandom p_202748_1_, int p_202748_2_, int p_202748_3_, int p_202748_4_, int p_202748_5_, int p_202748_6_) {
         return p_202748_6_ != 1 || p_202748_2_ != 3 && p_202748_3_ != 3 && p_202748_5_ != 3 && p_202748_4_ != 3 && p_202748_2_ != 4 && p_202748_3_ != 4 && p_202748_5_ != 4 && p_202748_4_ != 4 ? p_202748_6_ : 2;
      }
   }

   public static enum HeatIce implements ICastleTransformer {
      INSTANCE;

      public int apply(INoiseRandom p_202748_1_, int p_202748_2_, int p_202748_3_, int p_202748_4_, int p_202748_5_, int p_202748_6_) {
         return p_202748_6_ != 4 || p_202748_2_ != 1 && p_202748_3_ != 1 && p_202748_5_ != 1 && p_202748_4_ != 1 && p_202748_2_ != 2 && p_202748_3_ != 2 && p_202748_5_ != 2 && p_202748_4_ != 2 ? p_202748_6_ : 3;
      }
   }

   public static enum Special implements IC0Transformer {
      INSTANCE;

      public int apply(INoiseRandom p_202726_1_, int p_202726_2_) {
         if (!LayerUtil.isShallowOcean(p_202726_2_) && p_202726_1_.nextRandom(13) == 0) {
            p_202726_2_ |= 1 + p_202726_1_.nextRandom(15) << 8 & 3840;
         }

         return p_202726_2_;
      }
   }
}
