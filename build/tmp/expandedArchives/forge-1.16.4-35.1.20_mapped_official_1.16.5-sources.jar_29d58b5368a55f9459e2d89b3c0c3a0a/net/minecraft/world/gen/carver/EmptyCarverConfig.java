package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;

public class EmptyCarverConfig implements ICarverConfig {
   public static final Codec<EmptyCarverConfig> CODEC;
   public static final EmptyCarverConfig INSTANCE = new EmptyCarverConfig();

   static {
      CODEC = Codec.unit(() -> {
         return INSTANCE;
      });
   }
}
