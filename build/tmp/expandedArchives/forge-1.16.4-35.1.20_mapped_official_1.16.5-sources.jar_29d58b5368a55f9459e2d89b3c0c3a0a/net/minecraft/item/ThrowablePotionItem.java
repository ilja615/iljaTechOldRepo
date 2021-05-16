package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ThrowablePotionItem extends PotionItem {
   public ThrowablePotionItem(Item.Properties p_i225739_1_) {
      super(p_i225739_1_);
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
      if (!p_77659_1_.isClientSide) {
         PotionEntity potionentity = new PotionEntity(p_77659_1_, p_77659_2_);
         potionentity.setItem(itemstack);
         potionentity.shootFromRotation(p_77659_2_, p_77659_2_.xRot, p_77659_2_.yRot, -20.0F, 0.5F, 1.0F);
         p_77659_1_.addFreshEntity(potionentity);
      }

      p_77659_2_.awardStat(Stats.ITEM_USED.get(this));
      if (!p_77659_2_.abilities.instabuild) {
         itemstack.shrink(1);
      }

      return ActionResult.sidedSuccess(itemstack, p_77659_1_.isClientSide());
   }
}
