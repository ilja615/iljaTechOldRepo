package net.minecraft.item;

import net.minecraft.entity.IEquipable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;

public class SaddleItem extends Item {
   public SaddleItem(Item.Properties p_i48474_1_) {
      super(p_i48474_1_);
   }

   public ActionResultType interactLivingEntity(ItemStack p_111207_1_, PlayerEntity p_111207_2_, LivingEntity p_111207_3_, Hand p_111207_4_) {
      if (p_111207_3_ instanceof IEquipable && p_111207_3_.isAlive()) {
         IEquipable iequipable = (IEquipable)p_111207_3_;
         if (!iequipable.isSaddled() && iequipable.isSaddleable()) {
            if (!p_111207_2_.level.isClientSide) {
               iequipable.equipSaddle(SoundCategory.NEUTRAL);
               p_111207_1_.shrink(1);
            }

            return ActionResultType.sidedSuccess(p_111207_2_.level.isClientSide);
         }
      }

      return ActionResultType.PASS;
   }
}
