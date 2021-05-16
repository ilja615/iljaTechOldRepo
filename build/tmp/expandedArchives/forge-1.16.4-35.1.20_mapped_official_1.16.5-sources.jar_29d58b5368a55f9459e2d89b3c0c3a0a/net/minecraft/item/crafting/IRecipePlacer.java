package net.minecraft.item.crafting;

import java.util.Iterator;
import net.minecraft.util.math.MathHelper;

public interface IRecipePlacer<T> {
   default void placeRecipe(int p_201501_1_, int p_201501_2_, int p_201501_3_, IRecipe<?> p_201501_4_, Iterator<T> p_201501_5_, int p_201501_6_) {
      int i = p_201501_1_;
      int j = p_201501_2_;
      if (p_201501_4_ instanceof net.minecraftforge.common.crafting.IShapedRecipe) {
         net.minecraftforge.common.crafting.IShapedRecipe shapedrecipe = (net.minecraftforge.common.crafting.IShapedRecipe)p_201501_4_;
         i = shapedrecipe.getRecipeWidth();
         j = shapedrecipe.getRecipeHeight();
      }

      int k1 = 0;

      for(int k = 0; k < p_201501_2_; ++k) {
         if (k1 == p_201501_3_) {
            ++k1;
         }

         boolean flag = (float)j < (float)p_201501_2_ / 2.0F;
         int l = MathHelper.floor((float)p_201501_2_ / 2.0F - (float)j / 2.0F);
         if (flag && l > k) {
            k1 += p_201501_1_;
            ++k;
         }

         for(int i1 = 0; i1 < p_201501_1_; ++i1) {
            if (!p_201501_5_.hasNext()) {
               return;
            }

            flag = (float)i < (float)p_201501_1_ / 2.0F;
            l = MathHelper.floor((float)p_201501_1_ / 2.0F - (float)i / 2.0F);
            int j1 = i;
            boolean flag1 = i1 < i;
            if (flag) {
               j1 = l + i;
               flag1 = l <= i1 && i1 < l + i;
            }

            if (flag1) {
               this.addItemToSlot(p_201501_5_, k1, p_201501_6_, k, i1);
            } else if (j1 == i1) {
               k1 += p_201501_1_ - i1;
               break;
            }

            ++k1;
         }
      }

   }

   void addItemToSlot(Iterator<T> p_201500_1_, int p_201500_2_, int p_201500_3_, int p_201500_4_, int p_201500_5_);
}
