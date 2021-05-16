package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.VineBlock;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.Feature;

public class LeaveVineTreeDecorator extends TreeDecorator {
   public static final Codec<LeaveVineTreeDecorator> CODEC;
   public static final LeaveVineTreeDecorator INSTANCE = new LeaveVineTreeDecorator();

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.LEAVE_VINE;
   }

   public void place(ISeedReader p_225576_1_, Random p_225576_2_, List<BlockPos> p_225576_3_, List<BlockPos> p_225576_4_, Set<BlockPos> p_225576_5_, MutableBoundingBox p_225576_6_) {
      p_225576_4_.forEach((p_242866_5_) -> {
         if (p_225576_2_.nextInt(4) == 0) {
            BlockPos blockpos = p_242866_5_.west();
            if (Feature.isAir(p_225576_1_, blockpos)) {
               this.addHangingVine(p_225576_1_, blockpos, VineBlock.EAST, p_225576_5_, p_225576_6_);
            }
         }

         if (p_225576_2_.nextInt(4) == 0) {
            BlockPos blockpos1 = p_242866_5_.east();
            if (Feature.isAir(p_225576_1_, blockpos1)) {
               this.addHangingVine(p_225576_1_, blockpos1, VineBlock.WEST, p_225576_5_, p_225576_6_);
            }
         }

         if (p_225576_2_.nextInt(4) == 0) {
            BlockPos blockpos2 = p_242866_5_.north();
            if (Feature.isAir(p_225576_1_, blockpos2)) {
               this.addHangingVine(p_225576_1_, blockpos2, VineBlock.SOUTH, p_225576_5_, p_225576_6_);
            }
         }

         if (p_225576_2_.nextInt(4) == 0) {
            BlockPos blockpos3 = p_242866_5_.south();
            if (Feature.isAir(p_225576_1_, blockpos3)) {
               this.addHangingVine(p_225576_1_, blockpos3, VineBlock.NORTH, p_225576_5_, p_225576_6_);
            }
         }

      });
   }

   private void addHangingVine(IWorldGenerationReader p_227420_1_, BlockPos p_227420_2_, BooleanProperty p_227420_3_, Set<BlockPos> p_227420_4_, MutableBoundingBox p_227420_5_) {
      this.placeVine(p_227420_1_, p_227420_2_, p_227420_3_, p_227420_4_, p_227420_5_);
      int i = 4;

      for(BlockPos blockpos = p_227420_2_.below(); Feature.isAir(p_227420_1_, blockpos) && i > 0; --i) {
         this.placeVine(p_227420_1_, blockpos, p_227420_3_, p_227420_4_, p_227420_5_);
         blockpos = blockpos.below();
      }

   }

   static {
      CODEC = Codec.unit(() -> {
         return INSTANCE;
      });
   }
}
