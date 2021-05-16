package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.block.BlockState;

public class RandomBlockStateMatchRuleTest extends RuleTest {
   public static final Codec<RandomBlockStateMatchRuleTest> CODEC = RecordCodecBuilder.create((p_237122_0_) -> {
      return p_237122_0_.group(BlockState.CODEC.fieldOf("block_state").forGetter((p_237124_0_) -> {
         return p_237124_0_.blockState;
      }), Codec.FLOAT.fieldOf("probability").forGetter((p_237123_0_) -> {
         return p_237123_0_.probability;
      })).apply(p_237122_0_, RandomBlockStateMatchRuleTest::new);
   });
   private final BlockState blockState;
   private final float probability;

   public RandomBlockStateMatchRuleTest(BlockState p_i51322_1_, float p_i51322_2_) {
      this.blockState = p_i51322_1_;
      this.probability = p_i51322_2_;
   }

   public boolean test(BlockState p_215181_1_, Random p_215181_2_) {
      return p_215181_1_ == this.blockState && p_215181_2_.nextFloat() < this.probability;
   }

   protected IRuleTestType<?> getType() {
      return IRuleTestType.RANDOM_BLOCKSTATE_TEST;
   }
}
