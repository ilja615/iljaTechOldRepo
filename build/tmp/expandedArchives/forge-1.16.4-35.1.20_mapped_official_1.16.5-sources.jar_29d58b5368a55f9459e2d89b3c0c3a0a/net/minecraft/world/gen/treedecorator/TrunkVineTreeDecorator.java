package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.feature.Feature;

public class TrunkVineTreeDecorator extends TreeDecorator {
   public static final Codec<TrunkVineTreeDecorator> CODEC;
   public static final TrunkVineTreeDecorator INSTANCE = new TrunkVineTreeDecorator();

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.TRUNK_VINE;
   }

   public void place(ISeedReader p_225576_1_, Random p_225576_2_, List<BlockPos> p_225576_3_, List<BlockPos> p_225576_4_, Set<BlockPos> p_225576_5_, MutableBoundingBox p_225576_6_) {
      p_225576_3_.forEach((p_236880_5_) -> {
         if (p_225576_2_.nextInt(3) > 0) {
            BlockPos blockpos = p_236880_5_.west();
            if (Feature.isAir(p_225576_1_, blockpos)) {
               this.placeVine(p_225576_1_, blockpos, VineBlock.EAST, p_225576_5_, p_225576_6_);
            }
         }

         if (p_225576_2_.nextInt(3) > 0) {
            BlockPos blockpos1 = p_236880_5_.east();
            if (Feature.isAir(p_225576_1_, blockpos1)) {
               this.placeVine(p_225576_1_, blockpos1, VineBlock.WEST, p_225576_5_, p_225576_6_);
            }
         }

         if (p_225576_2_.nextInt(3) > 0) {
            BlockPos blockpos2 = p_236880_5_.north();
            if (Feature.isAir(p_225576_1_, blockpos2)) {
               this.placeVine(p_225576_1_, blockpos2, VineBlock.SOUTH, p_225576_5_, p_225576_6_);
            }
         }

         if (p_225576_2_.nextInt(3) > 0) {
            BlockPos blockpos3 = p_236880_5_.south();
            if (Feature.isAir(p_225576_1_, blockpos3)) {
               this.placeVine(p_225576_1_, blockpos3, VineBlock.NORTH, p_225576_5_, p_225576_6_);
            }
         }

      });
   }

   static {
      CODEC = Codec.unit(() -> {
         return INSTANCE;
      });
   }
}
