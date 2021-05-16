package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import org.apache.commons.lang3.mutable.MutableBoolean;

public abstract class WorldCarver<C extends ICarverConfig> extends net.minecraftforge.registries.ForgeRegistryEntry<WorldCarver<?>> {
   public static final WorldCarver<ProbabilityConfig> CAVE = register("cave", new CaveWorldCarver(ProbabilityConfig.CODEC, 256));
   public static final WorldCarver<ProbabilityConfig> NETHER_CAVE = register("nether_cave", new NetherCaveCarver(ProbabilityConfig.CODEC));
   public static final WorldCarver<ProbabilityConfig> CANYON = register("canyon", new CanyonWorldCarver(ProbabilityConfig.CODEC));
   public static final WorldCarver<ProbabilityConfig> UNDERWATER_CANYON = register("underwater_canyon", new UnderwaterCanyonWorldCarver(ProbabilityConfig.CODEC));
   public static final WorldCarver<ProbabilityConfig> UNDERWATER_CAVE = register("underwater_cave", new UnderwaterCaveWorldCarver(ProbabilityConfig.CODEC));
   protected static final BlockState AIR = Blocks.AIR.defaultBlockState();
   protected static final BlockState CAVE_AIR = Blocks.CAVE_AIR.defaultBlockState();
   protected static final FluidState WATER = Fluids.WATER.defaultFluidState();
   protected static final FluidState LAVA = Fluids.LAVA.defaultFluidState();
   protected Set<Block> replaceableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.PACKED_ICE);
   protected Set<Fluid> liquids = ImmutableSet.of(Fluids.WATER);
   private final Codec<ConfiguredCarver<C>> configuredCodec;
   protected final int genHeight;

   private static <C extends ICarverConfig, F extends WorldCarver<C>> F register(String p_222699_0_, F p_222699_1_) {
      return Registry.register(Registry.CARVER, p_222699_0_, p_222699_1_);
   }

   public WorldCarver(Codec<C> p_i231921_1_, int p_i231921_2_) {
      this.genHeight = p_i231921_2_;
      this.configuredCodec = p_i231921_1_.fieldOf("config").xmap(this::configured, ConfiguredCarver::config).codec();
   }

   public ConfiguredCarver<C> configured(C p_242761_1_) {
      return new ConfiguredCarver<>(this, p_242761_1_);
   }

   public Codec<ConfiguredCarver<C>> configuredCodec() {
      return this.configuredCodec;
   }

   public int getRange() {
      return 4;
   }

   protected boolean carveSphere(IChunk p_227208_1_, Function<BlockPos, Biome> p_227208_2_, long p_227208_3_, int p_227208_5_, int p_227208_6_, int p_227208_7_, double p_227208_8_, double p_227208_10_, double p_227208_12_, double p_227208_14_, double p_227208_16_, BitSet p_227208_18_) {
      Random random = new Random(p_227208_3_ + (long)p_227208_6_ + (long)p_227208_7_);
      double d0 = (double)(p_227208_6_ * 16 + 8);
      double d1 = (double)(p_227208_7_ * 16 + 8);
      if (!(p_227208_8_ < d0 - 16.0D - p_227208_14_ * 2.0D) && !(p_227208_12_ < d1 - 16.0D - p_227208_14_ * 2.0D) && !(p_227208_8_ > d0 + 16.0D + p_227208_14_ * 2.0D) && !(p_227208_12_ > d1 + 16.0D + p_227208_14_ * 2.0D)) {
         int i = Math.max(MathHelper.floor(p_227208_8_ - p_227208_14_) - p_227208_6_ * 16 - 1, 0);
         int j = Math.min(MathHelper.floor(p_227208_8_ + p_227208_14_) - p_227208_6_ * 16 + 1, 16);
         int k = Math.max(MathHelper.floor(p_227208_10_ - p_227208_16_) - 1, 1);
         int l = Math.min(MathHelper.floor(p_227208_10_ + p_227208_16_) + 1, this.genHeight - 8);
         int i1 = Math.max(MathHelper.floor(p_227208_12_ - p_227208_14_) - p_227208_7_ * 16 - 1, 0);
         int j1 = Math.min(MathHelper.floor(p_227208_12_ + p_227208_14_) - p_227208_7_ * 16 + 1, 16);
         if (this.hasWater(p_227208_1_, p_227208_6_, p_227208_7_, i, j, k, l, i1, j1)) {
            return false;
         } else {
            boolean flag = false;
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
            BlockPos.Mutable blockpos$mutable1 = new BlockPos.Mutable();
            BlockPos.Mutable blockpos$mutable2 = new BlockPos.Mutable();

            for(int k1 = i; k1 < j; ++k1) {
               int l1 = k1 + p_227208_6_ * 16;
               double d2 = ((double)l1 + 0.5D - p_227208_8_) / p_227208_14_;

               for(int i2 = i1; i2 < j1; ++i2) {
                  int j2 = i2 + p_227208_7_ * 16;
                  double d3 = ((double)j2 + 0.5D - p_227208_12_) / p_227208_14_;
                  if (!(d2 * d2 + d3 * d3 >= 1.0D)) {
                     MutableBoolean mutableboolean = new MutableBoolean(false);

                     for(int k2 = l; k2 > k; --k2) {
                        double d4 = ((double)k2 - 0.5D - p_227208_10_) / p_227208_16_;
                        if (!this.skip(d2, d4, d3, k2)) {
                           flag |= this.carveBlock(p_227208_1_, p_227208_2_, p_227208_18_, random, blockpos$mutable, blockpos$mutable1, blockpos$mutable2, p_227208_5_, p_227208_6_, p_227208_7_, l1, j2, k1, k2, i2, mutableboolean);
                        }
                     }
                  }
               }
            }

            return flag;
         }
      } else {
         return false;
      }
   }

   protected boolean carveBlock(IChunk p_230358_1_, Function<BlockPos, Biome> p_230358_2_, BitSet p_230358_3_, Random p_230358_4_, BlockPos.Mutable p_230358_5_, BlockPos.Mutable p_230358_6_, BlockPos.Mutable p_230358_7_, int p_230358_8_, int p_230358_9_, int p_230358_10_, int p_230358_11_, int p_230358_12_, int p_230358_13_, int p_230358_14_, int p_230358_15_, MutableBoolean p_230358_16_) {
      int i = p_230358_13_ | p_230358_15_ << 4 | p_230358_14_ << 8;
      if (p_230358_3_.get(i)) {
         return false;
      } else {
         p_230358_3_.set(i);
         p_230358_5_.set(p_230358_11_, p_230358_14_, p_230358_12_);
         BlockState blockstate = p_230358_1_.getBlockState(p_230358_5_);
         BlockState blockstate1 = p_230358_1_.getBlockState(p_230358_6_.setWithOffset(p_230358_5_, Direction.UP));
         if (blockstate.is(Blocks.GRASS_BLOCK) || blockstate.is(Blocks.MYCELIUM)) {
            p_230358_16_.setTrue();
         }

         if (!this.canReplaceBlock(blockstate, blockstate1)) {
            return false;
         } else {
            if (p_230358_14_ < 11) {
               p_230358_1_.setBlockState(p_230358_5_, LAVA.createLegacyBlock(), false);
            } else {
               p_230358_1_.setBlockState(p_230358_5_, CAVE_AIR, false);
               if (p_230358_16_.isTrue()) {
                  p_230358_7_.setWithOffset(p_230358_5_, Direction.DOWN);
                  if (p_230358_1_.getBlockState(p_230358_7_).is(Blocks.DIRT)) {
                     p_230358_1_.setBlockState(p_230358_7_, p_230358_2_.apply(p_230358_5_).getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial(), false);
                  }
               }
            }

            return true;
         }
      }
   }

   public abstract boolean carve(IChunk p_225555_1_, Function<BlockPos, Biome> p_225555_2_, Random p_225555_3_, int p_225555_4_, int p_225555_5_, int p_225555_6_, int p_225555_7_, int p_225555_8_, BitSet p_225555_9_, C p_225555_10_);

   public abstract boolean isStartChunk(Random p_212868_1_, int p_212868_2_, int p_212868_3_, C p_212868_4_);

   protected boolean canReplaceBlock(BlockState p_222706_1_) {
      return this.replaceableBlocks.contains(p_222706_1_.getBlock());
   }

   protected boolean canReplaceBlock(BlockState p_222707_1_, BlockState p_222707_2_) {
      return this.canReplaceBlock(p_222707_1_) || (p_222707_1_.is(Blocks.SAND) || p_222707_1_.is(Blocks.GRAVEL)) && !p_222707_2_.getFluidState().is(FluidTags.WATER);
   }

   protected boolean hasWater(IChunk p_222700_1_, int p_222700_2_, int p_222700_3_, int p_222700_4_, int p_222700_5_, int p_222700_6_, int p_222700_7_, int p_222700_8_, int p_222700_9_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int i = p_222700_4_; i < p_222700_5_; ++i) {
         for(int j = p_222700_8_; j < p_222700_9_; ++j) {
            for(int k = p_222700_6_ - 1; k <= p_222700_7_ + 1; ++k) {
               if (this.liquids.contains(p_222700_1_.getFluidState(blockpos$mutable.set(i + p_222700_2_ * 16, k, j + p_222700_3_ * 16)).getType())) {
                  return true;
               }

               if (k != p_222700_7_ + 1 && !this.isEdge(p_222700_4_, p_222700_5_, p_222700_8_, p_222700_9_, i, j)) {
                  k = p_222700_7_;
               }
            }
         }
      }

      return false;
   }

   private boolean isEdge(int p_222701_1_, int p_222701_2_, int p_222701_3_, int p_222701_4_, int p_222701_5_, int p_222701_6_) {
      return p_222701_5_ == p_222701_1_ || p_222701_5_ == p_222701_2_ - 1 || p_222701_6_ == p_222701_3_ || p_222701_6_ == p_222701_4_ - 1;
   }

   protected boolean canReach(int p_222702_1_, int p_222702_2_, double p_222702_3_, double p_222702_5_, int p_222702_7_, int p_222702_8_, float p_222702_9_) {
      double d0 = (double)(p_222702_1_ * 16 + 8);
      double d1 = (double)(p_222702_2_ * 16 + 8);
      double d2 = p_222702_3_ - d0;
      double d3 = p_222702_5_ - d1;
      double d4 = (double)(p_222702_8_ - p_222702_7_);
      double d5 = (double)(p_222702_9_ + 2.0F + 16.0F);
      return d2 * d2 + d3 * d3 - d4 * d4 <= d5 * d5;
   }

   protected abstract boolean skip(double p_222708_1_, double p_222708_3_, double p_222708_5_, int p_222708_7_);
}
