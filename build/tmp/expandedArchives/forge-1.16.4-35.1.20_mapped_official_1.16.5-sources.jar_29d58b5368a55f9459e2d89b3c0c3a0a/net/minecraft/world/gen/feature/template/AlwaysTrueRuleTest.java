package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;

public class AlwaysTrueRuleTest extends RuleTest {
   public static final Codec<AlwaysTrueRuleTest> CODEC;
   public static final AlwaysTrueRuleTest INSTANCE = new AlwaysTrueRuleTest();

   private AlwaysTrueRuleTest() {
   }

   public boolean test(BlockState p_215181_1_, Random p_215181_2_) {
      return true;
   }

   protected IRuleTestType<?> getType() {
      return IRuleTestType.ALWAYS_TRUE_TEST;
   }

   static {
      CODEC = Codec.unit(() -> {
         return INSTANCE;
      });
   }
}
