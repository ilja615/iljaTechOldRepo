package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;

public class NameTagItem extends Item {
   public NameTagItem(Item.Properties p_i48479_1_) {
      super(p_i48479_1_);
   }

   public ActionResultType interactLivingEntity(ItemStack p_111207_1_, PlayerEntity p_111207_2_, LivingEntity p_111207_3_, Hand p_111207_4_) {
      if (p_111207_1_.hasCustomHoverName() && !(p_111207_3_ instanceof PlayerEntity)) {
         if (!p_111207_2_.level.isClientSide && p_111207_3_.isAlive()) {
            p_111207_3_.setCustomName(p_111207_1_.getHoverName());
            if (p_111207_3_ instanceof MobEntity) {
               ((MobEntity)p_111207_3_).setPersistenceRequired();
            }

            p_111207_1_.shrink(1);
         }

         return ActionResultType.sidedSuccess(p_111207_2_.level.isClientSide);
      } else {
         return ActionResultType.PASS;
      }
   }
}
