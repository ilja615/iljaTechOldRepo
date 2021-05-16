package net.minecraft.client.audio;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ISoundEventListener {
   void onPlaySound(ISound p_184067_1_, SoundEventAccessor p_184067_2_);
}
