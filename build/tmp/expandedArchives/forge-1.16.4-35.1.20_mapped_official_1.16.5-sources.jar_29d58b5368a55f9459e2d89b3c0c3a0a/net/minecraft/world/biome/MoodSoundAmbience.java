package net.minecraft.world.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MoodSoundAmbience {
   public static final Codec<MoodSoundAmbience> CODEC = RecordCodecBuilder.create((p_235034_0_) -> {
      return p_235034_0_.group(SoundEvent.CODEC.fieldOf("sound").forGetter((p_235040_0_) -> {
         return p_235040_0_.soundEvent;
      }), Codec.INT.fieldOf("tick_delay").forGetter((p_235038_0_) -> {
         return p_235038_0_.tickDelay;
      }), Codec.INT.fieldOf("block_search_extent").forGetter((p_235036_0_) -> {
         return p_235036_0_.blockSearchExtent;
      }), Codec.DOUBLE.fieldOf("offset").forGetter((p_235033_0_) -> {
         return p_235033_0_.soundPositionOffset;
      })).apply(p_235034_0_, MoodSoundAmbience::new);
   });
   public static final MoodSoundAmbience LEGACY_CAVE_SETTINGS = new MoodSoundAmbience(SoundEvents.AMBIENT_CAVE, 6000, 8, 2.0D);
   private SoundEvent soundEvent;
   private int tickDelay;
   private int blockSearchExtent;
   private double soundPositionOffset;

   public MoodSoundAmbience(SoundEvent p_i231628_1_, int p_i231628_2_, int p_i231628_3_, double p_i231628_4_) {
      this.soundEvent = p_i231628_1_;
      this.tickDelay = p_i231628_2_;
      this.blockSearchExtent = p_i231628_3_;
      this.soundPositionOffset = p_i231628_4_;
   }

   @OnlyIn(Dist.CLIENT)
   public SoundEvent getSoundEvent() {
      return this.soundEvent;
   }

   @OnlyIn(Dist.CLIENT)
   public int getTickDelay() {
      return this.tickDelay;
   }

   @OnlyIn(Dist.CLIENT)
   public int getBlockSearchExtent() {
      return this.blockSearchExtent;
   }

   @OnlyIn(Dist.CLIENT)
   public double getSoundPositionOffset() {
      return this.soundPositionOffset;
   }
}
