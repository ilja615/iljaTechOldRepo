package net.minecraft.world.biome;

import com.google.common.hash.Hashing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BiomeManager {
   private final BiomeManager.IBiomeReader noiseBiomeSource;
   private final long biomeZoomSeed;
   private final IBiomeMagnifier zoomer;

   public BiomeManager(BiomeManager.IBiomeReader p_i225744_1_, long p_i225744_2_, IBiomeMagnifier p_i225744_4_) {
      this.noiseBiomeSource = p_i225744_1_;
      this.biomeZoomSeed = p_i225744_2_;
      this.zoomer = p_i225744_4_;
   }

   public static long obfuscateSeed(long p_235200_0_) {
      return Hashing.sha256().hashLong(p_235200_0_).asLong();
   }

   public BiomeManager withDifferentSource(BiomeProvider p_226835_1_) {
      return new BiomeManager(p_226835_1_, this.biomeZoomSeed, this.zoomer);
   }

   public Biome getBiome(BlockPos p_226836_1_) {
      return this.zoomer.getBiome(this.biomeZoomSeed, p_226836_1_.getX(), p_226836_1_.getY(), p_226836_1_.getZ(), this.noiseBiomeSource);
   }

   @OnlyIn(Dist.CLIENT)
   public Biome getNoiseBiomeAtPosition(double p_235198_1_, double p_235198_3_, double p_235198_5_) {
      int i = MathHelper.floor(p_235198_1_) >> 2;
      int j = MathHelper.floor(p_235198_3_) >> 2;
      int k = MathHelper.floor(p_235198_5_) >> 2;
      return this.getNoiseBiomeAtQuart(i, j, k);
   }

   @OnlyIn(Dist.CLIENT)
   public Biome getNoiseBiomeAtPosition(BlockPos p_235201_1_) {
      int i = p_235201_1_.getX() >> 2;
      int j = p_235201_1_.getY() >> 2;
      int k = p_235201_1_.getZ() >> 2;
      return this.getNoiseBiomeAtQuart(i, j, k);
   }

   @OnlyIn(Dist.CLIENT)
   public Biome getNoiseBiomeAtQuart(int p_235199_1_, int p_235199_2_, int p_235199_3_) {
      return this.noiseBiomeSource.getNoiseBiome(p_235199_1_, p_235199_2_, p_235199_3_);
   }

   public interface IBiomeReader {
      Biome getNoiseBiome(int p_225526_1_, int p_225526_2_, int p_225526_3_);
   }
}
