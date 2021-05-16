package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;

public class BlockStateMatchRuleTest extends RuleTest {
   public static final Codec<BlockStateMatchRuleTest> CODEC = BlockState.CODEC.fieldOf("block_state").xmap(BlockStateMatchRuleTest::new, (p_237080_0_) -> {
      return p_237080_0_.blockState;
   }).codec();
   private final BlockState blockState;

   public BlockStateMatchRuleTest(BlockState p_i51330_1_) {
      this.blockState = p_i51330_1_;
   }

   public boolean test(BlockState p_215181_1_, Random p_215181_2_) {
      return p_215181_1_ == this.blockState;
   }

   protected IRuleTestType<?> getType() {
      return IRuleTestType.BLOCKSTATE_TEST;
   }
}
