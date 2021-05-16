package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.blockplacer.BlockPlacer;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;

public class BlockClusterFeatureConfig implements IFeatureConfig {
   public static final Codec<BlockClusterFeatureConfig> CODEC = RecordCodecBuilder.create((p_236589_0_) -> {
      return p_236589_0_.group(BlockStateProvider.CODEC.fieldOf("state_provider").forGetter((p_236599_0_) -> {
         return p_236599_0_.stateProvider;
      }), BlockPlacer.CODEC.fieldOf("block_placer").forGetter((p_236598_0_) -> {
         return p_236598_0_.blockPlacer;
      }), BlockState.CODEC.listOf().fieldOf("whitelist").forGetter((p_236597_0_) -> {
         return p_236597_0_.whitelist.stream().map(Block::defaultBlockState).collect(Collectors.toList());
      }), BlockState.CODEC.listOf().fieldOf("blacklist").forGetter((p_236596_0_) -> {
         return ImmutableList.copyOf(p_236596_0_.blacklist);
      }), Codec.INT.fieldOf("tries").orElse(128).forGetter((p_236595_0_) -> {
         return p_236595_0_.tries;
      }), Codec.INT.fieldOf("xspread").orElse(7).forGetter((p_236594_0_) -> {
         return p_236594_0_.xspread;
      }), Codec.INT.fieldOf("yspread").orElse(3).forGetter((p_236593_0_) -> {
         return p_236593_0_.yspread;
      }), Codec.INT.fieldOf("zspread").orElse(7).forGetter((p_236592_0_) -> {
         return p_236592_0_.zspread;
      }), Codec.BOOL.fieldOf("can_replace").orElse(false).forGetter((p_236591_0_) -> {
         return p_236591_0_.canReplace;
      }), Codec.BOOL.fieldOf("project").orElse(true).forGetter((p_236590_0_) -> {
         return p_236590_0_.project;
      }), Codec.BOOL.fieldOf("need_water").orElse(false).forGetter((p_236588_0_) -> {
         return p_236588_0_.needWater;
      })).apply(p_236589_0_, BlockClusterFeatureConfig::new);
   });
   public final BlockStateProvider stateProvider;
   public final BlockPlacer blockPlacer;
   public final Set<Block> whitelist;
   public final Set<BlockState> blacklist;
   public final int tries;
   public final int xspread;
   public final int yspread;
   public final int zspread;
   public final boolean canReplace;
   public final boolean project;
   public final boolean needWater;

   private BlockClusterFeatureConfig(BlockStateProvider p_i232014_1_, BlockPlacer p_i232014_2_, List<BlockState> p_i232014_3_, List<BlockState> p_i232014_4_, int p_i232014_5_, int p_i232014_6_, int p_i232014_7_, int p_i232014_8_, boolean p_i232014_9_, boolean p_i232014_10_, boolean p_i232014_11_) {
      this(p_i232014_1_, p_i232014_2_, p_i232014_3_.stream().map(AbstractBlock.AbstractBlockState::getBlock).collect(Collectors.toSet()), ImmutableSet.copyOf(p_i232014_4_), p_i232014_5_, p_i232014_6_, p_i232014_7_, p_i232014_8_, p_i232014_9_, p_i232014_10_, p_i232014_11_);
   }

   private BlockClusterFeatureConfig(BlockStateProvider p_i225836_1_, BlockPlacer p_i225836_2_, Set<Block> p_i225836_3_, Set<BlockState> p_i225836_4_, int p_i225836_5_, int p_i225836_6_, int p_i225836_7_, int p_i225836_8_, boolean p_i225836_9_, boolean p_i225836_10_, boolean p_i225836_11_) {
      this.stateProvider = p_i225836_1_;
      this.blockPlacer = p_i225836_2_;
      this.whitelist = p_i225836_3_;
      this.blacklist = p_i225836_4_;
      this.tries = p_i225836_5_;
      this.xspread = p_i225836_6_;
      this.yspread = p_i225836_7_;
      this.zspread = p_i225836_8_;
      this.canReplace = p_i225836_9_;
      this.project = p_i225836_10_;
      this.needWater = p_i225836_11_;
   }

   public static class Builder {
      private final BlockStateProvider stateProvider;
      private final BlockPlacer blockPlacer;
      private Set<Block> whitelist = ImmutableSet.of();
      private Set<BlockState> blacklist = ImmutableSet.of();
      private int tries = 64;
      private int xspread = 7;
      private int yspread = 3;
      private int zspread = 7;
      private boolean canReplace;
      private boolean project = true;
      private boolean needWater = false;

      public Builder(BlockStateProvider p_i225838_1_, BlockPlacer p_i225838_2_) {
         this.stateProvider = p_i225838_1_;
         this.blockPlacer = p_i225838_2_;
      }

      public BlockClusterFeatureConfig.Builder whitelist(Set<Block> p_227316_1_) {
         this.whitelist = p_227316_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder blacklist(Set<BlockState> p_227319_1_) {
         this.blacklist = p_227319_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder tries(int p_227315_1_) {
         this.tries = p_227315_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder xspread(int p_227318_1_) {
         this.xspread = p_227318_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder yspread(int p_227321_1_) {
         this.yspread = p_227321_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder zspread(int p_227323_1_) {
         this.zspread = p_227323_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder canReplace() {
         this.canReplace = true;
         return this;
      }

      public BlockClusterFeatureConfig.Builder noProjection() {
         this.project = false;
         return this;
      }

      public BlockClusterFeatureConfig.Builder needWater() {
         this.needWater = true;
         return this;
      }

      public BlockClusterFeatureConfig build() {
         return new BlockClusterFeatureConfig(this.stateProvider, this.blockPlacer, this.whitelist, this.blacklist, this.tries, this.xspread, this.yspread, this.zspread, this.canReplace, this.project, this.needWater);
      }
   }
}
