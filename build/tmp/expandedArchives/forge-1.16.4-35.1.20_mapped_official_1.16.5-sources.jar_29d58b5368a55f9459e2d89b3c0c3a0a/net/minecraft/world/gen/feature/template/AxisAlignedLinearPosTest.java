package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class AxisAlignedLinearPosTest extends PosRuleTest {
   public static final Codec<AxisAlignedLinearPosTest> CODEC = RecordCodecBuilder.create((p_237051_0_) -> {
      return p_237051_0_.group(Codec.FLOAT.fieldOf("min_chance").orElse(0.0F).forGetter((p_237056_0_) -> {
         return p_237056_0_.minChance;
      }), Codec.FLOAT.fieldOf("max_chance").orElse(0.0F).forGetter((p_237055_0_) -> {
         return p_237055_0_.maxChance;
      }), Codec.INT.fieldOf("min_dist").orElse(0).forGetter((p_237054_0_) -> {
         return p_237054_0_.minDist;
      }), Codec.INT.fieldOf("max_dist").orElse(0).forGetter((p_237053_0_) -> {
         return p_237053_0_.maxDist;
      }), Direction.Axis.CODEC.fieldOf("axis").orElse(Direction.Axis.Y).forGetter((p_237052_0_) -> {
         return p_237052_0_.axis;
      })).apply(p_237051_0_, AxisAlignedLinearPosTest::new);
   });
   private final float minChance;
   private final float maxChance;
   private final int minDist;
   private final int maxDist;
   private final Direction.Axis axis;

   public AxisAlignedLinearPosTest(float p_i232114_1_, float p_i232114_2_, int p_i232114_3_, int p_i232114_4_, Direction.Axis p_i232114_5_) {
      if (p_i232114_3_ >= p_i232114_4_) {
         throw new IllegalArgumentException("Invalid range: [" + p_i232114_3_ + "," + p_i232114_4_ + "]");
      } else {
         this.minChance = p_i232114_1_;
         this.maxChance = p_i232114_2_;
         this.minDist = p_i232114_3_;
         this.maxDist = p_i232114_4_;
         this.axis = p_i232114_5_;
      }
   }

   public boolean test(BlockPos p_230385_1_, BlockPos p_230385_2_, BlockPos p_230385_3_, Random p_230385_4_) {
      Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, this.axis);
      float f = (float)Math.abs((p_230385_2_.getX() - p_230385_3_.getX()) * direction.getStepX());
      float f1 = (float)Math.abs((p_230385_2_.getY() - p_230385_3_.getY()) * direction.getStepY());
      float f2 = (float)Math.abs((p_230385_2_.getZ() - p_230385_3_.getZ()) * direction.getStepZ());
      int i = (int)(f + f1 + f2);
      float f3 = p_230385_4_.nextFloat();
      return (double)f3 <= MathHelper.clampedLerp((double)this.minChance, (double)this.maxChance, MathHelper.inverseLerp((double)i, (double)this.minDist, (double)this.maxDist));
   }

   protected IPosRuleTests<?> getType() {
      return IPosRuleTests.AXIS_ALIGNED_LINEAR_POS_TEST;
   }
}
