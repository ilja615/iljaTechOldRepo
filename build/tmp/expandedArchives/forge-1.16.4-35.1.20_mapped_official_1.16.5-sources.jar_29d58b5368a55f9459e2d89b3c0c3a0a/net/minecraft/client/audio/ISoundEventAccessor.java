package net.minecraft.client.audio;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ISoundEventAccessor<T> {
   int getWeight();

   T getSound();

   void preloadIfRequired(SoundEngine p_217867_1_);
}
