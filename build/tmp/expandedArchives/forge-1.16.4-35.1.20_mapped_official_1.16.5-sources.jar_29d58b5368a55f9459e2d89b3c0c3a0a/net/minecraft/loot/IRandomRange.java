package net.minecraft.loot;

import java.util.Random;
import net.minecraft.util.ResourceLocation;

public interface IRandomRange {
   ResourceLocation CONSTANT = new ResourceLocation("constant");
   ResourceLocation UNIFORM = new ResourceLocation("uniform");
   ResourceLocation BINOMIAL = new ResourceLocation("binomial");

   int getInt(Random p_186511_1_);

   ResourceLocation getType();
}
