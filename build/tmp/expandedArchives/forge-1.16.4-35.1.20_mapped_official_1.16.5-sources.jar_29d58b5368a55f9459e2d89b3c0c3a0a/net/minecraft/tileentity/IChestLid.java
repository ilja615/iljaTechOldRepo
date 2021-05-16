package net.minecraft.tileentity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IChestLid {
   float getOpenNess(float p_195480_1_);
}
