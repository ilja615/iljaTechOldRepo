package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKeyCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class ConfiguredCarver<WC extends ICarverConfig> {
   public static final Codec<ConfiguredCarver<?>> DIRECT_CODEC = Registry.CARVER.dispatch((p_236236_0_) -> {
      return p_236236_0_.worldCarver;
   }, WorldCarver::configuredCodec);
   public static final Codec<Supplier<ConfiguredCarver<?>>> CODEC = RegistryKeyCodec.create(Registry.CONFIGURED_CARVER_REGISTRY, DIRECT_CODEC);
   public static final Codec<List<Supplier<ConfiguredCarver<?>>>> LIST_CODEC = RegistryKeyCodec.homogeneousList(Registry.CONFIGURED_CARVER_REGISTRY, DIRECT_CODEC);
   private final WorldCarver<WC> worldCarver;
   private final WC config;

   public ConfiguredCarver(WorldCarver<WC> p_i49928_1_, WC p_i49928_2_) {
      this.worldCarver = p_i49928_1_;
      this.config = p_i49928_2_;
   }

   public WC config() {
      return this.config;
   }

   public boolean isStartChunk(Random p_222730_1_, int p_222730_2_, int p_222730_3_) {
      return this.worldCarver.isStartChunk(p_222730_1_, p_222730_2_, p_222730_3_, this.config);
   }

   public boolean carve(IChunk p_227207_1_, Function<BlockPos, Biome> p_227207_2_, Random p_227207_3_, int p_227207_4_, int p_227207_5_, int p_227207_6_, int p_227207_7_, int p_227207_8_, BitSet p_227207_9_) {
      return this.worldCarver.carve(p_227207_1_, p_227207_2_, p_227207_3_, p_227207_4_, p_227207_5_, p_227207_6_, p_227207_7_, p_227207_8_, p_227207_9_, this.config);
   }
}
