package net.minecraft.world.biome.provider;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SingleBiomeProvider extends BiomeProvider {
   public static final Codec<SingleBiomeProvider> CODEC = Biome.CODEC.fieldOf("biome").xmap(SingleBiomeProvider::new, (p_235261_0_) -> {
      return p_235261_0_.biome;
   }).stable().codec();
   private final Supplier<Biome> biome;

   public SingleBiomeProvider(Biome p_i46709_1_) {
      this(() -> {
         return p_i46709_1_;
      });
   }

   public SingleBiomeProvider(Supplier<Biome> p_i241945_1_) {
      super(ImmutableList.of(p_i241945_1_.get()));
      this.biome = p_i241945_1_;
   }

   protected Codec<? extends BiomeProvider> codec() {
      return CODEC;
   }

   @OnlyIn(Dist.CLIENT)
   public BiomeProvider withSeed(long p_230320_1_) {
      return this;
   }

   public Biome getNoiseBiome(int p_225526_1_, int p_225526_2_, int p_225526_3_) {
      return this.biome.get();
   }

   @Nullable
   public BlockPos findBiomeHorizontal(int p_230321_1_, int p_230321_2_, int p_230321_3_, int p_230321_4_, int p_230321_5_, Predicate<Biome> p_230321_6_, Random p_230321_7_, boolean p_230321_8_) {
      if (p_230321_6_.test(this.biome.get())) {
         return p_230321_8_ ? new BlockPos(p_230321_1_, p_230321_2_, p_230321_3_) : new BlockPos(p_230321_1_ - p_230321_4_ + p_230321_7_.nextInt(p_230321_4_ * 2 + 1), p_230321_2_, p_230321_3_ - p_230321_4_ + p_230321_7_.nextInt(p_230321_4_ * 2 + 1));
      } else {
         return null;
      }
   }

   public Set<Biome> getBiomesWithin(int p_225530_1_, int p_225530_2_, int p_225530_3_, int p_225530_4_) {
      return Sets.newHashSet(this.biome.get());
   }
}
