package net.minecraft.entity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IJumpingMount {
   @OnlyIn(Dist.CLIENT)
   void onPlayerJump(int p_110206_1_);

   boolean canJump();

   void handleStartJump(int p_184775_1_);

   void handleStopJump();
}
