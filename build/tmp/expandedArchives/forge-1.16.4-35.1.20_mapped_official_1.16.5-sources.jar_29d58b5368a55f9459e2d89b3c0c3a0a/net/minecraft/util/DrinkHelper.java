package net.minecraft.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DrinkHelper {
   public static ActionResult<ItemStack> useDrink(World p_234707_0_, PlayerEntity p_234707_1_, Hand p_234707_2_) {
      p_234707_1_.startUsingItem(p_234707_2_);
      return ActionResult.consume(p_234707_1_.getItemInHand(p_234707_2_));
   }

   public static ItemStack createFilledResult(ItemStack p_241445_0_, PlayerEntity p_241445_1_, ItemStack p_241445_2_, boolean p_241445_3_) {
      boolean flag = p_241445_1_.abilities.instabuild;
      if (p_241445_3_ && flag) {
         if (!p_241445_1_.inventory.contains(p_241445_2_)) {
            p_241445_1_.inventory.add(p_241445_2_);
         }

         return p_241445_0_;
      } else {
         if (!flag) {
            p_241445_0_.shrink(1);
         }

         if (p_241445_0_.isEmpty()) {
            return p_241445_2_;
         } else {
            if (!p_241445_1_.inventory.add(p_241445_2_)) {
               p_241445_1_.drop(p_241445_2_, false);
            }

            return p_241445_0_;
         }
      }
   }

   public static ItemStack createFilledResult(ItemStack p_242398_0_, PlayerEntity p_242398_1_, ItemStack p_242398_2_) {
      return createFilledResult(p_242398_0_, p_242398_1_, p_242398_2_, true);
   }
}
