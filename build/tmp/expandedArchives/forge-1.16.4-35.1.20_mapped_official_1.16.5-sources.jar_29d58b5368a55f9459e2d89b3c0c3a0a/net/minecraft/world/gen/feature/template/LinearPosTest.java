package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class LinearPosTest extends PosRuleTest {
   public static final Codec<LinearPosTest> CODEC = RecordCodecBuilder.create((p_237092_0_) -> {
      return p_237092_0_.group(Codec.FLOAT.fieldOf("min_chance").orElse(0.0F).forGetter((p_237096_0_) -> {
         return p_237096_0_.minChance;
      }), Codec.FLOAT.fieldOf("max_chance").orElse(0.0F).forGetter((p_237095_0_) -> {
         return p_237095_0_.maxChance;
      }), Codec.INT.fieldOf("min_dist").orElse(0).forGetter((p_237094_0_) -> {
         return p_237094_0_.minDist;
      }), Codec.INT.fieldOf("max_dist").orElse(0).forGetter((p_237093_0_) -> {
         return p_237093_0_.maxDist;
      })).apply(p_237092_0_, LinearPosTest::new);
   });
   private final float minChance;
   private final float maxChance;
   private final int minDist;
   private final int maxDist;

   public LinearPosTest(float p_i232116_1_, float p_i232116_2_, int p_i232116_3_, int p_i232116_4_) {
      if (p_i232116_3_ >= p_i232116_4_) {
         throw new IllegalArgumentException("Invalid range: [" + p_i232116_3_ + "," + p_i232116_4_ + "]");
      } else {
         this.minChance = p_i232116_1_;
         this.maxChance = p_i232116_2_;
         this.minDist = p_i232116_3_;
         this.maxDist = p_i232116_4_;
      }
   }

   public boolean test(BlockPos p_230385_1_, BlockPos p_230385_2_, BlockPos p_230385_3_, Random p_230385_4_) {
      int i = p_230385_2_.distManhattan(p_230385_3_);
      float f = p_230385_4_.nextFloat();
      return (double)f <= MathHelper.clampedLerp((double)this.minChance, (double)this.maxChance, MathHelper.inverseLerp((double)i, (double)this.minDist, (double)this.maxDist));
   }

   protected IPosRuleTests<?> getType() {
      return IPosRuleTests.LINEAR_POS_TEST;
   }
}
