package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.trunkplacer.AbstractTrunkPlacer;

public class BaseTreeFeatureConfig implements IFeatureConfig {
   public static final Codec<BaseTreeFeatureConfig> CODEC = RecordCodecBuilder.create((p_236683_0_) -> {
      return p_236683_0_.group(BlockStateProvider.CODEC.fieldOf("trunk_provider").forGetter((p_236693_0_) -> {
         return p_236693_0_.trunkProvider;
      }), BlockStateProvider.CODEC.fieldOf("leaves_provider").forGetter((p_236692_0_) -> {
         return p_236692_0_.leavesProvider;
      }), FoliagePlacer.CODEC.fieldOf("foliage_placer").forGetter((p_236691_0_) -> {
         return p_236691_0_.foliagePlacer;
      }), AbstractTrunkPlacer.CODEC.fieldOf("trunk_placer").forGetter((p_236690_0_) -> {
         return p_236690_0_.trunkPlacer;
      }), AbstractFeatureSizeType.CODEC.fieldOf("minimum_size").forGetter((p_236689_0_) -> {
         return p_236689_0_.minimumSize;
      }), TreeDecorator.CODEC.listOf().fieldOf("decorators").forGetter((p_236688_0_) -> {
         return p_236688_0_.decorators;
      }), Codec.INT.fieldOf("max_water_depth").orElse(0).forGetter((p_236687_0_) -> {
         return p_236687_0_.maxWaterDepth;
      }), Codec.BOOL.fieldOf("ignore_vines").orElse(false).forGetter((p_236686_0_) -> {
         return p_236686_0_.ignoreVines;
      }), Heightmap.Type.CODEC.fieldOf("heightmap").forGetter((p_236684_0_) -> {
         return p_236684_0_.heightmap;
      })).apply(p_236683_0_, BaseTreeFeatureConfig::new);
   });
   //TODO: Review this, see if we can hook in the sapling into the Codec
   public final BlockStateProvider trunkProvider;
   public final BlockStateProvider leavesProvider;
   public final List<TreeDecorator> decorators;
   public transient boolean fromSapling;
   public final FoliagePlacer foliagePlacer;
   public final AbstractTrunkPlacer trunkPlacer;
   public final AbstractFeatureSizeType minimumSize;
   public final int maxWaterDepth;
   public final boolean ignoreVines;
   public final Heightmap.Type heightmap;

   protected BaseTreeFeatureConfig(BlockStateProvider p_i232020_1_, BlockStateProvider p_i232020_2_, FoliagePlacer p_i232020_3_, AbstractTrunkPlacer p_i232020_4_, AbstractFeatureSizeType p_i232020_5_, List<TreeDecorator> p_i232020_6_, int p_i232020_7_, boolean p_i232020_8_, Heightmap.Type p_i232020_9_) {
      this.trunkProvider = p_i232020_1_;
      this.leavesProvider = p_i232020_2_;
      this.decorators = p_i232020_6_;
      this.foliagePlacer = p_i232020_3_;
      this.minimumSize = p_i232020_5_;
      this.trunkPlacer = p_i232020_4_;
      this.maxWaterDepth = p_i232020_7_;
      this.ignoreVines = p_i232020_8_;
      this.heightmap = p_i232020_9_;
   }

   public void setFromSapling() {
      this.fromSapling = true;
   }

   public BaseTreeFeatureConfig withDecorators(List<TreeDecorator> p_236685_1_) {
      return new BaseTreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.foliagePlacer, this.trunkPlacer, this.minimumSize, p_236685_1_, this.maxWaterDepth, this.ignoreVines, this.heightmap);
   }

   public static class Builder {
      public final BlockStateProvider trunkProvider;
      public final BlockStateProvider leavesProvider;
      private final FoliagePlacer foliagePlacer;
      private final AbstractTrunkPlacer trunkPlacer;
      private final AbstractFeatureSizeType minimumSize;
      private List<TreeDecorator> decorators = ImmutableList.of();
      private int maxWaterDepth;
      private boolean ignoreVines;
      private Heightmap.Type heightmap = Heightmap.Type.OCEAN_FLOOR;

      public Builder(BlockStateProvider p_i232021_1_, BlockStateProvider p_i232021_2_, FoliagePlacer p_i232021_3_, AbstractTrunkPlacer p_i232021_4_, AbstractFeatureSizeType p_i232021_5_) {
         this.trunkProvider = p_i232021_1_;
         this.leavesProvider = p_i232021_2_;
         this.foliagePlacer = p_i232021_3_;
         this.trunkPlacer = p_i232021_4_;
         this.minimumSize = p_i232021_5_;
      }

      public BaseTreeFeatureConfig.Builder decorators(List<TreeDecorator> p_236703_1_) {
         this.decorators = p_236703_1_;
         return this;
      }

      public BaseTreeFeatureConfig.Builder maxWaterDepth(int p_236701_1_) {
         this.maxWaterDepth = p_236701_1_;
         return this;
      }

      public BaseTreeFeatureConfig.Builder ignoreVines() {
         this.ignoreVines = true;
         return this;
      }

      public BaseTreeFeatureConfig.Builder heightmap(Heightmap.Type p_236702_1_) {
         this.heightmap = p_236702_1_;
         return this;
      }

      public BaseTreeFeatureConfig build() {
         return new BaseTreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.foliagePlacer, this.trunkPlacer, this.minimumSize, this.decorators, this.maxWaterDepth, this.ignoreVines, this.heightmap);
      }
   }
}
