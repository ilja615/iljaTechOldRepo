package net.minecraft.world.gen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKeyCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class ConfiguredSurfaceBuilder<SC extends ISurfaceBuilderConfig> {
   public static final Codec<ConfiguredSurfaceBuilder<?>> DIRECT_CODEC = Registry.SURFACE_BUILDER.dispatch((p_237169_0_) -> {
      return p_237169_0_.surfaceBuilder;
   }, SurfaceBuilder::configuredCodec);
   public static final Codec<Supplier<ConfiguredSurfaceBuilder<?>>> CODEC = RegistryKeyCodec.create(Registry.CONFIGURED_SURFACE_BUILDER_REGISTRY, DIRECT_CODEC);
   public final SurfaceBuilder<SC> surfaceBuilder;
   public final SC config;

   public ConfiguredSurfaceBuilder(SurfaceBuilder<SC> p_i51316_1_, SC p_i51316_2_) {
      this.surfaceBuilder = p_i51316_1_;
      this.config = p_i51316_2_;
   }

   public void apply(Random p_215450_1_, IChunk p_215450_2_, Biome p_215450_3_, int p_215450_4_, int p_215450_5_, int p_215450_6_, double p_215450_7_, BlockState p_215450_9_, BlockState p_215450_10_, int p_215450_11_, long p_215450_12_) {
      this.surfaceBuilder.apply(p_215450_1_, p_215450_2_, p_215450_3_, p_215450_4_, p_215450_5_, p_215450_6_, p_215450_7_, p_215450_9_, p_215450_10_, p_215450_11_, p_215450_12_, this.config);
   }

   public void initNoise(long p_215451_1_) {
      this.surfaceBuilder.initNoise(p_215451_1_);
   }

   public SC config() {
      return this.config;
   }
}
