package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;

public class NoFeatureConfig implements IFeatureConfig {
   public static final Codec<NoFeatureConfig> CODEC;
   public static final NoFeatureConfig INSTANCE = new NoFeatureConfig();

   static {
      CODEC = Codec.unit(() -> {
         return INSTANCE;
      });
   }
}
