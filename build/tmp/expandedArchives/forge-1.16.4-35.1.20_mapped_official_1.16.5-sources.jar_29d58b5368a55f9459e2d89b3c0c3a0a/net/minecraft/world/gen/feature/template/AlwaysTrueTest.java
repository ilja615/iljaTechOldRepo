package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;

public class AlwaysTrueTest extends PosRuleTest {
   public static final Codec<AlwaysTrueTest> CODEC;
   public static final AlwaysTrueTest INSTANCE = new AlwaysTrueTest();

   private AlwaysTrueTest() {
   }

   public boolean test(BlockPos p_230385_1_, BlockPos p_230385_2_, BlockPos p_230385_3_, Random p_230385_4_) {
      return true;
   }

   protected IPosRuleTests<?> getType() {
      return IPosRuleTests.ALWAYS_TRUE_TEST;
   }

   static {
      CODEC = Codec.unit(() -> {
         return INSTANCE;
      });
   }
}
