package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.feature.Feature;

public class AlterGroundTreeDecorator extends TreeDecorator {
   public static final Codec<AlterGroundTreeDecorator> CODEC = BlockStateProvider.CODEC.fieldOf("provider").xmap(AlterGroundTreeDecorator::new, (p_236862_0_) -> {
      return p_236862_0_.provider;
   }).codec();
   private final BlockStateProvider provider;

   public AlterGroundTreeDecorator(BlockStateProvider p_i225864_1_) {
      this.provider = p_i225864_1_;
   }

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.ALTER_GROUND;
   }

   public void place(ISeedReader p_225576_1_, Random p_225576_2_, List<BlockPos> p_225576_3_, List<BlockPos> p_225576_4_, Set<BlockPos> p_225576_5_, MutableBoundingBox p_225576_6_) {
      int i = p_225576_3_.get(0).getY();
      p_225576_3_.stream().filter((p_236860_1_) -> {
         return p_236860_1_.getY() == i;
      }).forEach((p_236861_3_) -> {
         this.placeCircle(p_225576_1_, p_225576_2_, p_236861_3_.west().north());
         this.placeCircle(p_225576_1_, p_225576_2_, p_236861_3_.east(2).north());
         this.placeCircle(p_225576_1_, p_225576_2_, p_236861_3_.west().south(2));
         this.placeCircle(p_225576_1_, p_225576_2_, p_236861_3_.east(2).south(2));

         for(int j = 0; j < 5; ++j) {
            int k = p_225576_2_.nextInt(64);
            int l = k % 8;
            int i1 = k / 8;
            if (l == 0 || l == 7 || i1 == 0 || i1 == 7) {
               this.placeCircle(p_225576_1_, p_225576_2_, p_236861_3_.offset(-3 + l, 0, -3 + i1));
            }
         }

      });
   }

   private void placeCircle(IWorldGenerationReader p_227413_1_, Random p_227413_2_, BlockPos p_227413_3_) {
      for(int i = -2; i <= 2; ++i) {
         for(int j = -2; j <= 2; ++j) {
            if (Math.abs(i) != 2 || Math.abs(j) != 2) {
               this.placeBlockAt(p_227413_1_, p_227413_2_, p_227413_3_.offset(i, 0, j));
            }
         }
      }

   }

   private void placeBlockAt(IWorldGenerationReader p_227414_1_, Random p_227414_2_, BlockPos p_227414_3_) {
      for(int i = 2; i >= -3; --i) {
         BlockPos blockpos = p_227414_3_.above(i);
         if (Feature.isGrassOrDirt(p_227414_1_, blockpos)) {
            p_227414_1_.setBlock(blockpos, this.provider.getState(p_227414_2_, p_227414_3_), 19);
            break;
         }

         if (!Feature.isAir(p_227414_1_, blockpos) && i < 0) {
            break;
         }
      }

   }
}
