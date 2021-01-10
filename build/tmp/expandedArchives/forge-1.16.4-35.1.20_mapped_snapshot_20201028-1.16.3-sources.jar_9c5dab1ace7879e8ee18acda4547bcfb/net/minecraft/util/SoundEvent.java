package net.minecraft.util;

import com.mojang.serialization.Codec;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SoundEvent extends net.minecraftforge.registries.ForgeRegistryEntry<SoundEvent> {
   public static final Codec<SoundEvent> CODEC = ResourceLocation.CODEC.xmap(SoundEvent::new, (sound) -> {
      return sound.name;
   });
   private final ResourceLocation name;

   public SoundEvent(ResourceLocation name) {
      this.name = name;
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getName() {
      return this.name;
   }
}
