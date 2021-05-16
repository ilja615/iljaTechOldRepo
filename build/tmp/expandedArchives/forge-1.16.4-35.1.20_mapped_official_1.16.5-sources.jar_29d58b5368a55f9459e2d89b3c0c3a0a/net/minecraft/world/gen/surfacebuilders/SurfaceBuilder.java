package net.minecraft.world.gen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public abstract class SurfaceBuilder<C extends ISurfaceBuilderConfig> extends net.minecraftforge.registries.ForgeRegistryEntry<SurfaceBuilder<?>> {
   private static final BlockState DIRT = Blocks.DIRT.defaultBlockState();
   private static final BlockState GRASS_BLOCK = Blocks.GRASS_BLOCK.defaultBlockState();
   private static final BlockState PODZOL = Blocks.PODZOL.defaultBlockState();
   private static final BlockState GRAVEL = Blocks.GRAVEL.defaultBlockState();
   private static final BlockState STONE = Blocks.STONE.defaultBlockState();
   private static final BlockState COARSE_DIRT = Blocks.COARSE_DIRT.defaultBlockState();
   private static final BlockState SAND = Blocks.SAND.defaultBlockState();
   private static final BlockState RED_SAND = Blocks.RED_SAND.defaultBlockState();
   private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.defaultBlockState();
   private static final BlockState MYCELIUM = Blocks.MYCELIUM.defaultBlockState();
   private static final BlockState SOUL_SAND = Blocks.SOUL_SAND.defaultBlockState();
   private static final BlockState NETHERRACK = Blocks.NETHERRACK.defaultBlockState();
   private static final BlockState ENDSTONE = Blocks.END_STONE.defaultBlockState();
   private static final BlockState CRIMSON_NYLIUM = Blocks.CRIMSON_NYLIUM.defaultBlockState();
   private static final BlockState WARPED_NYLIUM = Blocks.WARPED_NYLIUM.defaultBlockState();
   private static final BlockState NETHER_WART_BLOCK = Blocks.NETHER_WART_BLOCK.defaultBlockState();
   private static final BlockState WARPED_WART_BLOCK = Blocks.WARPED_WART_BLOCK.defaultBlockState();
   private static final BlockState BLACKSTONE = Blocks.BLACKSTONE.defaultBlockState();
   private static final BlockState BASALT = Blocks.BASALT.defaultBlockState();
   private static final BlockState MAGMA = Blocks.MAGMA_BLOCK.defaultBlockState();
   public static final SurfaceBuilderConfig CONFIG_PODZOL = new SurfaceBuilderConfig(PODZOL, DIRT, GRAVEL);
   public static final SurfaceBuilderConfig CONFIG_GRAVEL = new SurfaceBuilderConfig(GRAVEL, GRAVEL, GRAVEL);
   public static final SurfaceBuilderConfig CONFIG_GRASS = new SurfaceBuilderConfig(GRASS_BLOCK, DIRT, GRAVEL);
   public static final SurfaceBuilderConfig CONFIG_STONE = new SurfaceBuilderConfig(STONE, STONE, GRAVEL);
   public static final SurfaceBuilderConfig CONFIG_COARSE_DIRT = new SurfaceBuilderConfig(COARSE_DIRT, DIRT, GRAVEL);
   public static final SurfaceBuilderConfig CONFIG_DESERT = new SurfaceBuilderConfig(SAND, SAND, GRAVEL);
   public static final SurfaceBuilderConfig CONFIG_OCEAN_SAND = new SurfaceBuilderConfig(GRASS_BLOCK, DIRT, SAND);
   public static final SurfaceBuilderConfig CONFIG_FULL_SAND = new SurfaceBuilderConfig(SAND, SAND, SAND);
   public static final SurfaceBuilderConfig CONFIG_BADLANDS = new SurfaceBuilderConfig(RED_SAND, WHITE_TERRACOTTA, GRAVEL);
   public static final SurfaceBuilderConfig CONFIG_MYCELIUM = new SurfaceBuilderConfig(MYCELIUM, DIRT, GRAVEL);
   public static final SurfaceBuilderConfig CONFIG_HELL = new SurfaceBuilderConfig(NETHERRACK, NETHERRACK, NETHERRACK);
   public static final SurfaceBuilderConfig CONFIG_SOUL_SAND_VALLEY = new SurfaceBuilderConfig(SOUL_SAND, SOUL_SAND, SOUL_SAND);
   public static final SurfaceBuilderConfig CONFIG_THEEND = new SurfaceBuilderConfig(ENDSTONE, ENDSTONE, ENDSTONE);
   public static final SurfaceBuilderConfig CONFIG_CRIMSON_FOREST = new SurfaceBuilderConfig(CRIMSON_NYLIUM, NETHERRACK, NETHER_WART_BLOCK);
   public static final SurfaceBuilderConfig CONFIG_WARPED_FOREST = new SurfaceBuilderConfig(WARPED_NYLIUM, NETHERRACK, WARPED_WART_BLOCK);
   public static final SurfaceBuilderConfig CONFIG_BASALT_DELTAS = new SurfaceBuilderConfig(BLACKSTONE, BASALT, MAGMA);
   public static final SurfaceBuilder<SurfaceBuilderConfig> DEFAULT = register("default", new DefaultSurfaceBuilder(SurfaceBuilderConfig.CODEC));
   public static final SurfaceBuilder<SurfaceBuilderConfig> MOUNTAIN = register("mountain", new MountainSurfaceBuilder(SurfaceBuilderConfig.CODEC));
   public static final SurfaceBuilder<SurfaceBuilderConfig> SHATTERED_SAVANNA = register("shattered_savanna", new ShatteredSavannaSurfaceBuilder(SurfaceBuilderConfig.CODEC));
   public static final SurfaceBuilder<SurfaceBuilderConfig> GRAVELLY_MOUNTAIN = register("gravelly_mountain", new GravellyMountainSurfaceBuilder(SurfaceBuilderConfig.CODEC));
   public static final SurfaceBuilder<SurfaceBuilderConfig> GIANT_TREE_TAIGA = register("giant_tree_taiga", new GiantTreeTaigaSurfaceBuilder(SurfaceBuilderConfig.CODEC));
   public static final SurfaceBuilder<SurfaceBuilderConfig> SWAMP = register("swamp", new SwampSurfaceBuilder(SurfaceBuilderConfig.CODEC));
   public static final SurfaceBuilder<SurfaceBuilderConfig> BADLANDS = register("badlands", new BadlandsSurfaceBuilder(SurfaceBuilderConfig.CODEC));
   public static final SurfaceBuilder<SurfaceBuilderConfig> WOODED_BADLANDS = register("wooded_badlands", new WoodedBadlandsSurfaceBuilder(SurfaceBuilderConfig.CODEC));
   public static final SurfaceBuilder<SurfaceBuilderConfig> ERODED_BADLANDS = register("eroded_badlands", new ErodedBadlandsSurfaceBuilder(SurfaceBuilderConfig.CODEC));
   public static final SurfaceBuilder<SurfaceBuilderConfig> FROZEN_OCEAN = register("frozen_ocean", new FrozenOceanSurfaceBuilder(SurfaceBuilderConfig.CODEC));
   public static final SurfaceBuilder<SurfaceBuilderConfig> NETHER = register("nether", new NetherSurfaceBuilder(SurfaceBuilderConfig.CODEC));
   public static final SurfaceBuilder<SurfaceBuilderConfig> NETHER_FOREST = register("nether_forest", new NetherForestsSurfaceBuilder(SurfaceBuilderConfig.CODEC));
   public static final SurfaceBuilder<SurfaceBuilderConfig> SOUL_SAND_VALLEY = register("soul_sand_valley", new SoulSandValleySurfaceBuilder(SurfaceBuilderConfig.CODEC));
   public static final SurfaceBuilder<SurfaceBuilderConfig> BASALT_DELTAS = register("basalt_deltas", new BasaltDeltasSurfaceBuilder(SurfaceBuilderConfig.CODEC));
   public static final SurfaceBuilder<SurfaceBuilderConfig> NOPE = register("nope", new NoopSurfaceBuilder(SurfaceBuilderConfig.CODEC));
   private final Codec<ConfiguredSurfaceBuilder<C>> configuredCodec;

   private static <C extends ISurfaceBuilderConfig, F extends SurfaceBuilder<C>> F register(String p_215389_0_, F p_215389_1_) {
      return Registry.register(Registry.SURFACE_BUILDER, p_215389_0_, p_215389_1_);
   }

   public SurfaceBuilder(Codec<C> p_i232136_1_) {
      this.configuredCodec = p_i232136_1_.fieldOf("config").xmap(this::configured, ConfiguredSurfaceBuilder::config).codec();
   }

   public Codec<ConfiguredSurfaceBuilder<C>> configuredCodec() {
      return this.configuredCodec;
   }

   public ConfiguredSurfaceBuilder<C> configured(C p_242929_1_) {
      return new ConfiguredSurfaceBuilder<>(this, p_242929_1_);
   }

   public abstract void apply(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, BlockState p_205610_9_, BlockState p_205610_10_, int p_205610_11_, long p_205610_12_, C p_205610_14_);

   public void initNoise(long p_205548_1_) {
   }
}
