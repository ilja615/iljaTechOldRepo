package net.minecraft.client.renderer.color;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.state.Property;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockColors {
   // FORGE: Use RegistryDelegates as non-Vanilla block ids are not constant
   private final java.util.Map<net.minecraftforge.registries.IRegistryDelegate<Block>, IBlockColor> blockColors = new java.util.HashMap<>();
   private final Map<Block, Set<Property<?>>> coloringStates = Maps.newHashMap();

   public static BlockColors createDefault() {
      BlockColors blockcolors = new BlockColors();
      blockcolors.register((p_228065_0_, p_228065_1_, p_228065_2_, p_228065_3_) -> {
         return p_228065_1_ != null && p_228065_2_ != null ? BiomeColors.getAverageGrassColor(p_228065_1_, p_228065_0_.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? p_228065_2_.below() : p_228065_2_) : -1;
      }, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
      blockcolors.addColoringState(DoublePlantBlock.HALF, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
      blockcolors.register((p_228064_0_, p_228064_1_, p_228064_2_, p_228064_3_) -> {
         return p_228064_1_ != null && p_228064_2_ != null ? BiomeColors.getAverageGrassColor(p_228064_1_, p_228064_2_) : GrassColors.get(0.5D, 1.0D);
      }, Blocks.GRASS_BLOCK, Blocks.FERN, Blocks.GRASS, Blocks.POTTED_FERN);
      blockcolors.register((p_228063_0_, p_228063_1_, p_228063_2_, p_228063_3_) -> {
         return FoliageColors.getEvergreenColor();
      }, Blocks.SPRUCE_LEAVES);
      blockcolors.register((p_228062_0_, p_228062_1_, p_228062_2_, p_228062_3_) -> {
         return FoliageColors.getBirchColor();
      }, Blocks.BIRCH_LEAVES);
      blockcolors.register((p_228061_0_, p_228061_1_, p_228061_2_, p_228061_3_) -> {
         return p_228061_1_ != null && p_228061_2_ != null ? BiomeColors.getAverageFoliageColor(p_228061_1_, p_228061_2_) : FoliageColors.getDefaultColor();
      }, Blocks.OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.VINE);
      blockcolors.register((p_228060_0_, p_228060_1_, p_228060_2_, p_228060_3_) -> {
         return p_228060_1_ != null && p_228060_2_ != null ? BiomeColors.getAverageWaterColor(p_228060_1_, p_228060_2_) : -1;
      }, Blocks.WATER, Blocks.BUBBLE_COLUMN, Blocks.CAULDRON);
      blockcolors.register((p_228059_0_, p_228059_1_, p_228059_2_, p_228059_3_) -> {
         return RedstoneWireBlock.getColorForPower(p_228059_0_.getValue(RedstoneWireBlock.POWER));
      }, Blocks.REDSTONE_WIRE);
      blockcolors.addColoringState(RedstoneWireBlock.POWER, Blocks.REDSTONE_WIRE);
      blockcolors.register((p_228058_0_, p_228058_1_, p_228058_2_, p_228058_3_) -> {
         return p_228058_1_ != null && p_228058_2_ != null ? BiomeColors.getAverageGrassColor(p_228058_1_, p_228058_2_) : -1;
      }, Blocks.SUGAR_CANE);
      blockcolors.register((p_228057_0_, p_228057_1_, p_228057_2_, p_228057_3_) -> {
         return 14731036;
      }, Blocks.ATTACHED_MELON_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
      blockcolors.register((p_228056_0_, p_228056_1_, p_228056_2_, p_228056_3_) -> {
         int i = p_228056_0_.getValue(StemBlock.AGE);
         int j = i * 32;
         int k = 255 - i * 8;
         int l = i * 4;
         return j << 16 | k << 8 | l;
      }, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
      blockcolors.addColoringState(StemBlock.AGE, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
      blockcolors.register((p_228055_0_, p_228055_1_, p_228055_2_, p_228055_3_) -> {
         return p_228055_1_ != null && p_228055_2_ != null ? 2129968 : 7455580;
      }, Blocks.LILY_PAD);
      net.minecraftforge.client.ForgeHooksClient.onBlockColorsInit(blockcolors);
      return blockcolors;
   }

   public int getColor(BlockState p_189991_1_, World p_189991_2_, BlockPos p_189991_3_) {
      IBlockColor iblockcolor = this.blockColors.get(p_189991_1_.getBlock().delegate);
      if (iblockcolor != null) {
         return iblockcolor.getColor(p_189991_1_, (IBlockDisplayReader)null, (BlockPos)null, 0);
      } else {
         MaterialColor materialcolor = p_189991_1_.getMapColor(p_189991_2_, p_189991_3_);
         return materialcolor != null ? materialcolor.col : -1;
      }
   }

   public int getColor(BlockState p_228054_1_, @Nullable IBlockDisplayReader p_228054_2_, @Nullable BlockPos p_228054_3_, int p_228054_4_) {
      IBlockColor iblockcolor = this.blockColors.get(p_228054_1_.getBlock().delegate);
      return iblockcolor == null ? -1 : iblockcolor.getColor(p_228054_1_, p_228054_2_, p_228054_3_, p_228054_4_);
   }

   public void register(IBlockColor p_186722_1_, Block... p_186722_2_) {
      for(Block block : p_186722_2_) {
         this.blockColors.put(block.delegate, p_186722_1_);
      }

   }

   private void addColoringStates(Set<Property<?>> p_225309_1_, Block... p_225309_2_) {
      for(Block block : p_225309_2_) {
         this.coloringStates.put(block, p_225309_1_);
      }

   }

   private void addColoringState(Property<?> p_225308_1_, Block... p_225308_2_) {
      this.addColoringStates(ImmutableSet.of(p_225308_1_), p_225308_2_);
   }

   public Set<Property<?>> getColoringProperties(Block p_225310_1_) {
      return this.coloringStates.getOrDefault(p_225310_1_, ImmutableSet.of());
   }
}
