package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public interface IRuleTestType<P extends RuleTest> {
   IRuleTestType<AlwaysTrueRuleTest> ALWAYS_TRUE_TEST = register("always_true", AlwaysTrueRuleTest.CODEC);
   IRuleTestType<BlockMatchRuleTest> BLOCK_TEST = register("block_match", BlockMatchRuleTest.CODEC);
   IRuleTestType<BlockStateMatchRuleTest> BLOCKSTATE_TEST = register("blockstate_match", BlockStateMatchRuleTest.CODEC);
   IRuleTestType<TagMatchRuleTest> TAG_TEST = register("tag_match", TagMatchRuleTest.CODEC);
   IRuleTestType<RandomBlockMatchRuleTest> RANDOM_BLOCK_TEST = register("random_block_match", RandomBlockMatchRuleTest.CODEC);
   IRuleTestType<RandomBlockStateMatchRuleTest> RANDOM_BLOCKSTATE_TEST = register("random_blockstate_match", RandomBlockStateMatchRuleTest.CODEC);

   Codec<P> codec();

   static <P extends RuleTest> IRuleTestType<P> register(String p_237129_0_, Codec<P> p_237129_1_) {
      return Registry.register(Registry.RULE_TEST, p_237129_0_, () -> {
         return p_237129_1_;
      });
   }
}
