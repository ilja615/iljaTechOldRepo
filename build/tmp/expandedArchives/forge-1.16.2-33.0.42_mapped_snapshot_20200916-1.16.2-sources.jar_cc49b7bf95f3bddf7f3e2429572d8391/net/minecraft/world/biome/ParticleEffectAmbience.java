package net.minecraft.world.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleEffectAmbience {
   public static final Codec<ParticleEffectAmbience> CODEC = RecordCodecBuilder.create((particleAmbienceCodecInstance) -> {
      return particleAmbienceCodecInstance.group(ParticleTypes.CODEC.fieldOf("options").forGetter((particleAmbience) -> {
         return particleAmbience.particleOptions;
      }), Codec.FLOAT.fieldOf("probability").forGetter((particleAmbience) -> {
         return particleAmbience.probability;
      })).apply(particleAmbienceCodecInstance, ParticleEffectAmbience::new);
   });
   private final IParticleData particleOptions;
   private final float probability;

   public ParticleEffectAmbience(IParticleData particleOptions, float probability) {
      this.particleOptions = particleOptions;
      this.probability = probability;
   }

   @OnlyIn(Dist.CLIENT)
   public IParticleData getParticleOptions() {
      return this.particleOptions;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldParticleSpawn(Random rand) {
      return rand.nextFloat() <= this.probability;
   }
}