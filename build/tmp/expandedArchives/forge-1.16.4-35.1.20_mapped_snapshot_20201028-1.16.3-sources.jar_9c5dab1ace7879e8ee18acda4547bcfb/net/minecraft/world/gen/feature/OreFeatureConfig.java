package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;

public class OreFeatureConfig implements IFeatureConfig {
   public static final Codec<OreFeatureConfig> CODEC = RecordCodecBuilder.create((p_236568_0_) -> {
      return p_236568_0_.group(RuleTest.field_237127_c_.fieldOf("target").forGetter((config) -> {
         return config.target;
      }), BlockState.CODEC.fieldOf("state").forGetter((config) -> {
         return config.state;
      }), Codec.intRange(0, 64).fieldOf("size").forGetter((config) -> {
         return config.size;
      })).apply(p_236568_0_, OreFeatureConfig::new);
   });
   public final RuleTest target;
   public final int size;
   public final BlockState state;

   public OreFeatureConfig(RuleTest p_i241989_1_, BlockState state, int size) {
      this.size = size;
      this.state = state;
      this.target = p_i241989_1_;
   }

   public static final class FillerBlockType {
      public static final RuleTest BASE_STONE_OVERWORLD = new TagMatchRuleTest(BlockTags.BASE_STONE_OVERWORLD);
      public static final RuleTest NETHERRACK = new BlockMatchRuleTest(Blocks.NETHERRACK);
      public static final RuleTest BASE_STONE_NETHER = new TagMatchRuleTest(BlockTags.BASE_STONE_NETHER);
   }
}
