package net.minecraft.world.biome.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CheckerboardBiomeProvider extends BiomeProvider {
   public static final Codec<CheckerboardBiomeProvider> CODEC = RecordCodecBuilder.create((p_235258_0_) -> {
      return p_235258_0_.group(Biome.LIST_CODEC.fieldOf("biomes").forGetter((p_235259_0_) -> {
         return p_235259_0_.allowedBiomes;
      }), Codec.intRange(0, 62).fieldOf("scale").orElse(2).forGetter((p_235257_0_) -> {
         return p_235257_0_.size;
      })).apply(p_235258_0_, CheckerboardBiomeProvider::new);
   });
   private final List<Supplier<Biome>> allowedBiomes;
   private final int bitShift;
   private final int size;

   public CheckerboardBiomeProvider(List<Supplier<Biome>> p_i231637_1_, int p_i231637_2_) {
      super(p_i231637_1_.stream());
      this.allowedBiomes = p_i231637_1_;
      this.bitShift = p_i231637_2_ + 2;
      this.size = p_i231637_2_;
   }

   protected Codec<? extends BiomeProvider> codec() {
      return CODEC;
   }

   @OnlyIn(Dist.CLIENT)
   public BiomeProvider withSeed(long p_230320_1_) {
      return this;
   }

   public Biome getNoiseBiome(int p_225526_1_, int p_225526_2_, int p_225526_3_) {
      return this.allowedBiomes.get(Math.floorMod((p_225526_1_ >> this.bitShift) + (p_225526_3_ >> this.bitShift), this.allowedBiomes.size())).get();
   }
}
