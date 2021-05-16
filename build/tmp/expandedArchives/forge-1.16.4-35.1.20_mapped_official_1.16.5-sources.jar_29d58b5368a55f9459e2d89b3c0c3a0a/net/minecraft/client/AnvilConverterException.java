package net.minecraft.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnvilConverterException extends Exception {
   public AnvilConverterException(String p_i2160_1_) {
      super(p_i2160_1_);
   }
}
