package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;

public class RandomBlockMatchRuleTest extends RuleTest {
   public static final Codec<RandomBlockMatchRuleTest> CODEC = RecordCodecBuilder.create((p_237118_0_) -> {
      return p_237118_0_.group(Registry.BLOCK.fieldOf("block").forGetter((p_237120_0_) -> {
         return p_237120_0_.block;
      }), Codec.FLOAT.fieldOf("probability").forGetter((p_237119_0_) -> {
         return p_237119_0_.probability;
      })).apply(p_237118_0_, RandomBlockMatchRuleTest::new);
   });
   private final Block block;
   private final float probability;

   public RandomBlockMatchRuleTest(Block p_i51324_1_, float p_i51324_2_) {
      this.block = p_i51324_1_;
      this.probability = p_i51324_2_;
   }

   public boolean test(BlockState p_215181_1_, Random p_215181_2_) {
      return p_215181_1_.is(this.block) && p_215181_2_.nextFloat() < this.probability;
   }

   protected IRuleTestType<?> getType() {
      return IRuleTestType.RANDOM_BLOCK_TEST;
   }
}
