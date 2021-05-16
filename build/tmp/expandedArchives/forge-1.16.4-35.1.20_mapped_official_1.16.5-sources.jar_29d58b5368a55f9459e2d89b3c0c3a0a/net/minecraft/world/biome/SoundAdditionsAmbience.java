package net.minecraft.world.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SoundAdditionsAmbience {
   public static final Codec<SoundAdditionsAmbience> CODEC = RecordCodecBuilder.create((p_235023_0_) -> {
      return p_235023_0_.group(SoundEvent.CODEC.fieldOf("sound").forGetter((p_235025_0_) -> {
         return p_235025_0_.soundEvent;
      }), Codec.DOUBLE.fieldOf("tick_chance").forGetter((p_235022_0_) -> {
         return p_235022_0_.tickChance;
      })).apply(p_235023_0_, SoundAdditionsAmbience::new);
   });
   private SoundEvent soundEvent;
   private double tickChance;

   public SoundAdditionsAmbience(SoundEvent p_i231627_1_, double p_i231627_2_) {
      this.soundEvent = p_i231627_1_;
      this.tickChance = p_i231627_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public SoundEvent getSoundEvent() {
      return this.soundEvent;
   }

   @OnlyIn(Dist.CLIENT)
   public double getTickChance() {
      return this.tickChance;
   }
}
