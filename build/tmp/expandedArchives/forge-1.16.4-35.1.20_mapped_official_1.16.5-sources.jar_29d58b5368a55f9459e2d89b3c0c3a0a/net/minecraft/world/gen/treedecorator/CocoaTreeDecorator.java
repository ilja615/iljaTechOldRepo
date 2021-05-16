package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.feature.Feature;

public class CocoaTreeDecorator extends TreeDecorator {
   public static final Codec<CocoaTreeDecorator> CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(CocoaTreeDecorator::new, (p_236868_0_) -> {
      return p_236868_0_.probability;
   }).codec();
   private final float probability;

   public CocoaTreeDecorator(float p_i225868_1_) {
      this.probability = p_i225868_1_;
   }

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.COCOA;
   }

   public void place(ISeedReader p_225576_1_, Random p_225576_2_, List<BlockPos> p_225576_3_, List<BlockPos> p_225576_4_, Set<BlockPos> p_225576_5_, MutableBoundingBox p_225576_6_) {
      if (!(p_225576_2_.nextFloat() >= this.probability)) {
         int i = p_225576_3_.get(0).getY();
         p_225576_3_.stream().filter((p_236867_1_) -> {
            return p_236867_1_.getY() - i <= 2;
         }).forEach((p_242865_5_) -> {
            for(Direction direction : Direction.Plane.HORIZONTAL) {
               if (p_225576_2_.nextFloat() <= 0.25F) {
                  Direction direction1 = direction.getOpposite();
                  BlockPos blockpos = p_242865_5_.offset(direction1.getStepX(), 0, direction1.getStepZ());
                  if (Feature.isAir(p_225576_1_, blockpos)) {
                     BlockState blockstate = Blocks.COCOA.defaultBlockState().setValue(CocoaBlock.AGE, Integer.valueOf(p_225576_2_.nextInt(3))).setValue(CocoaBlock.FACING, direction);
                     this.setBlock(p_225576_1_, blockpos, blockstate, p_225576_5_, p_225576_6_);
                  }
               }
            }

         });
      }
   }
}
