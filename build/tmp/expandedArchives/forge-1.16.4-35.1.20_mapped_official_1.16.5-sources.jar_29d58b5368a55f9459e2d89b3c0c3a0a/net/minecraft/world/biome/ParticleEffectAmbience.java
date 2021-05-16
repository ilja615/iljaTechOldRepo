package net.minecraft.world.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleEffectAmbience {
   public static final Codec<ParticleEffectAmbience> CODEC = RecordCodecBuilder.create((p_235046_0_) -> {
      return p_235046_0_.group(ParticleTypes.CODEC.fieldOf("options").forGetter((p_235048_0_) -> {
         return p_235048_0_.options;
      }), Codec.FLOAT.fieldOf("probability").forGetter((p_235045_0_) -> {
         return p_235045_0_.probability;
      })).apply(p_235046_0_, ParticleEffectAmbience::new);
   });
   private final IParticleData options;
   private final float probability;

   public ParticleEffectAmbience(IParticleData p_i231629_1_, float p_i231629_2_) {
      this.options = p_i231629_1_;
      this.probability = p_i231629_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public IParticleData getOptions() {
      return this.options;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canSpawn(Random p_235047_1_) {
      return p_235047_1_.nextFloat() <= this.probability;
   }
}
