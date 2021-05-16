package net.minecraft.world.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import net.minecraft.client.audio.BackgroundMusicSelector;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BiomeAmbience {
   public static final Codec<BiomeAmbience> CODEC = RecordCodecBuilder.create((p_235215_0_) -> {
      return p_235215_0_.group(Codec.INT.fieldOf("fog_color").forGetter((p_235229_0_) -> {
         return p_235229_0_.fogColor;
      }), Codec.INT.fieldOf("water_color").forGetter((p_235227_0_) -> {
         return p_235227_0_.waterColor;
      }), Codec.INT.fieldOf("water_fog_color").forGetter((p_235225_0_) -> {
         return p_235225_0_.waterFogColor;
      }), Codec.INT.fieldOf("sky_color").forGetter((p_242532_0_) -> {
         return p_242532_0_.skyColor;
      }), Codec.INT.optionalFieldOf("foliage_color").forGetter((p_244421_0_) -> {
         return p_244421_0_.foliageColorOverride;
      }), Codec.INT.optionalFieldOf("grass_color").forGetter((p_244426_0_) -> {
         return p_244426_0_.grassColorOverride;
      }), BiomeAmbience.GrassColorModifier.CODEC.optionalFieldOf("grass_color_modifier", BiomeAmbience.GrassColorModifier.NONE).forGetter((p_242530_0_) -> {
         return p_242530_0_.grassColorModifier;
      }), ParticleEffectAmbience.CODEC.optionalFieldOf("particle").forGetter((p_235223_0_) -> {
         return p_235223_0_.ambientParticleSettings;
      }), SoundEvent.CODEC.optionalFieldOf("ambient_sound").forGetter((p_235221_0_) -> {
         return p_235221_0_.ambientLoopSoundEvent;
      }), MoodSoundAmbience.CODEC.optionalFieldOf("mood_sound").forGetter((p_235219_0_) -> {
         return p_235219_0_.ambientMoodSettings;
      }), SoundAdditionsAmbience.CODEC.optionalFieldOf("additions_sound").forGetter((p_235217_0_) -> {
         return p_235217_0_.ambientAdditionsSettings;
      }), BackgroundMusicSelector.CODEC.optionalFieldOf("music").forGetter((p_244420_0_) -> {
         return p_244420_0_.backgroundMusic;
      })).apply(p_235215_0_, BiomeAmbience::new);
   });
   private final int fogColor;
   private final int waterColor;
   private final int waterFogColor;
   private final int skyColor;
   private final Optional<Integer> foliageColorOverride;
   private final Optional<Integer> grassColorOverride;
   private final BiomeAmbience.GrassColorModifier grassColorModifier;
   private final Optional<ParticleEffectAmbience> ambientParticleSettings;
   private final Optional<SoundEvent> ambientLoopSoundEvent;
   private final Optional<MoodSoundAmbience> ambientMoodSettings;
   private final Optional<SoundAdditionsAmbience> ambientAdditionsSettings;
   private final Optional<BackgroundMusicSelector> backgroundMusic;

   private BiomeAmbience(int p_i241938_1_, int p_i241938_2_, int p_i241938_3_, int p_i241938_4_, Optional<Integer> p_i241938_5_, Optional<Integer> p_i241938_6_, BiomeAmbience.GrassColorModifier p_i241938_7_, Optional<ParticleEffectAmbience> p_i241938_8_, Optional<SoundEvent> p_i241938_9_, Optional<MoodSoundAmbience> p_i241938_10_, Optional<SoundAdditionsAmbience> p_i241938_11_, Optional<BackgroundMusicSelector> p_i241938_12_) {
      this.fogColor = p_i241938_1_;
      this.waterColor = p_i241938_2_;
      this.waterFogColor = p_i241938_3_;
      this.skyColor = p_i241938_4_;
      this.foliageColorOverride = p_i241938_5_;
      this.grassColorOverride = p_i241938_6_;
      this.grassColorModifier = p_i241938_7_;
      this.ambientParticleSettings = p_i241938_8_;
      this.ambientLoopSoundEvent = p_i241938_9_;
      this.ambientMoodSettings = p_i241938_10_;
      this.ambientAdditionsSettings = p_i241938_11_;
      this.backgroundMusic = p_i241938_12_;
   }

   public int getFogColor() {
      return this.fogColor;
   }

   public int getWaterColor() {
      return this.waterColor;
   }

   public int getWaterFogColor() {
      return this.waterFogColor;
   }

   public int getSkyColor() {
      return this.skyColor;
   }

   public Optional<Integer> getFoliageColorOverride() {
      return this.foliageColorOverride;
   }

   public Optional<Integer> getGrassColorOverride() {
      return this.grassColorOverride;
   }

   public BiomeAmbience.GrassColorModifier getGrassColorModifier() {
      return this.grassColorModifier;
   }

   public Optional<ParticleEffectAmbience> getAmbientParticleSettings() {
      return this.ambientParticleSettings;
   }

   public Optional<SoundEvent> getAmbientLoopSoundEvent() {
      return this.ambientLoopSoundEvent;
   }

   public Optional<MoodSoundAmbience> getAmbientMoodSettings() {
      return this.ambientMoodSettings;
   }

   public Optional<SoundAdditionsAmbience> getAmbientAdditionsSettings() {
      return this.ambientAdditionsSettings;
   }

   public Optional<BackgroundMusicSelector> getBackgroundMusic() {
      return this.backgroundMusic;
   }

   public static class Builder {
      private OptionalInt fogColor = OptionalInt.empty();
      private OptionalInt waterColor = OptionalInt.empty();
      private OptionalInt waterFogColor = OptionalInt.empty();
      private OptionalInt skyColor = OptionalInt.empty();
      private Optional<Integer> foliageColorOverride = Optional.empty();
      private Optional<Integer> grassColorOverride = Optional.empty();
      private BiomeAmbience.GrassColorModifier grassColorModifier = BiomeAmbience.GrassColorModifier.NONE;
      private Optional<ParticleEffectAmbience> ambientParticle = Optional.empty();
      private Optional<SoundEvent> ambientLoopSoundEvent = Optional.empty();
      private Optional<MoodSoundAmbience> ambientMoodSettings = Optional.empty();
      private Optional<SoundAdditionsAmbience> ambientAdditionsSettings = Optional.empty();
      private Optional<BackgroundMusicSelector> backgroundMusic = Optional.empty();

      public BiomeAmbience.Builder fogColor(int p_235239_1_) {
         this.fogColor = OptionalInt.of(p_235239_1_);
         return this;
      }

      public BiomeAmbience.Builder waterColor(int p_235246_1_) {
         this.waterColor = OptionalInt.of(p_235246_1_);
         return this;
      }

      public BiomeAmbience.Builder waterFogColor(int p_235248_1_) {
         this.waterFogColor = OptionalInt.of(p_235248_1_);
         return this;
      }

      public BiomeAmbience.Builder skyColor(int p_242539_1_) {
         this.skyColor = OptionalInt.of(p_242539_1_);
         return this;
      }

      public BiomeAmbience.Builder foliageColorOverride(int p_242540_1_) {
         this.foliageColorOverride = Optional.of(p_242540_1_);
         return this;
      }

      public BiomeAmbience.Builder grassColorOverride(int p_242541_1_) {
         this.grassColorOverride = Optional.of(p_242541_1_);
         return this;
      }

      public BiomeAmbience.Builder grassColorModifier(BiomeAmbience.GrassColorModifier p_242537_1_) {
         this.grassColorModifier = p_242537_1_;
         return this;
      }

      public BiomeAmbience.Builder ambientParticle(ParticleEffectAmbience p_235244_1_) {
         this.ambientParticle = Optional.of(p_235244_1_);
         return this;
      }

      public BiomeAmbience.Builder ambientLoopSound(SoundEvent p_235241_1_) {
         this.ambientLoopSoundEvent = Optional.of(p_235241_1_);
         return this;
      }

      public BiomeAmbience.Builder ambientMoodSound(MoodSoundAmbience p_235243_1_) {
         this.ambientMoodSettings = Optional.of(p_235243_1_);
         return this;
      }

      public BiomeAmbience.Builder ambientAdditionsSound(SoundAdditionsAmbience p_235242_1_) {
         this.ambientAdditionsSettings = Optional.of(p_235242_1_);
         return this;
      }

      public BiomeAmbience.Builder backgroundMusic(BackgroundMusicSelector p_235240_1_) {
         this.backgroundMusic = Optional.of(p_235240_1_);
         return this;
      }

      public BiomeAmbience build() {
         return new BiomeAmbience(this.fogColor.orElseThrow(() -> {
            return new IllegalStateException("Missing 'fog' color.");
         }), this.waterColor.orElseThrow(() -> {
            return new IllegalStateException("Missing 'water' color.");
         }), this.waterFogColor.orElseThrow(() -> {
            return new IllegalStateException("Missing 'water fog' color.");
         }), this.skyColor.orElseThrow(() -> {
            return new IllegalStateException("Missing 'sky' color.");
         }), this.foliageColorOverride, this.grassColorOverride, this.grassColorModifier, this.ambientParticle, this.ambientLoopSoundEvent, this.ambientMoodSettings, this.ambientAdditionsSettings, this.backgroundMusic);
      }
   }

   public static enum GrassColorModifier implements IStringSerializable {
      NONE("none") {
         @OnlyIn(Dist.CLIENT)
         public int modifyColor(double p_241853_1_, double p_241853_3_, int p_241853_5_) {
            return p_241853_5_;
         }
      },
      DARK_FOREST("dark_forest") {
         @OnlyIn(Dist.CLIENT)
         public int modifyColor(double p_241853_1_, double p_241853_3_, int p_241853_5_) {
            return (p_241853_5_ & 16711422) + 2634762 >> 1;
         }
      },
      SWAMP("swamp") {
         @OnlyIn(Dist.CLIENT)
         public int modifyColor(double p_241853_1_, double p_241853_3_, int p_241853_5_) {
            double d0 = Biome.BIOME_INFO_NOISE.getValue(p_241853_1_ * 0.0225D, p_241853_3_ * 0.0225D, false);
            return d0 < -0.1D ? 5011004 : 6975545;
         }
      };

      private final String name;
      public static final Codec<BiomeAmbience.GrassColorModifier> CODEC = IStringSerializable.fromEnum(BiomeAmbience.GrassColorModifier::values, BiomeAmbience.GrassColorModifier::byName);
      private static final Map<String, BiomeAmbience.GrassColorModifier> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(BiomeAmbience.GrassColorModifier::getName, (p_242545_0_) -> {
         return p_242545_0_;
      }));

      @OnlyIn(Dist.CLIENT)
      public abstract int modifyColor(double p_241853_1_, double p_241853_3_, int p_241853_5_);

      private GrassColorModifier(String p_i241940_3_) {
         this.name = p_i241940_3_;
      }

      public String getName() {
         return this.name;
      }

      public String getSerializedName() {
         return this.name;
      }

      public static BiomeAmbience.GrassColorModifier byName(String p_242546_0_) {
         return BY_NAME.get(p_242546_0_);
      }
   }
}
