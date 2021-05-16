package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public interface IPosRuleTests<P extends PosRuleTest> {
   IPosRuleTests<AlwaysTrueTest> ALWAYS_TRUE_TEST = register("always_true", AlwaysTrueTest.CODEC);
   IPosRuleTests<LinearPosTest> LINEAR_POS_TEST = register("linear_pos", LinearPosTest.CODEC);
   IPosRuleTests<AxisAlignedLinearPosTest> AXIS_ALIGNED_LINEAR_POS_TEST = register("axis_aligned_linear_pos", AxisAlignedLinearPosTest.CODEC);

   Codec<P> codec();

   static <P extends PosRuleTest> IPosRuleTests<P> register(String p_237107_0_, Codec<P> p_237107_1_) {
      return Registry.register(Registry.POS_RULE_TEST, p_237107_0_, () -> {
         return p_237107_1_;
      });
   }
}
