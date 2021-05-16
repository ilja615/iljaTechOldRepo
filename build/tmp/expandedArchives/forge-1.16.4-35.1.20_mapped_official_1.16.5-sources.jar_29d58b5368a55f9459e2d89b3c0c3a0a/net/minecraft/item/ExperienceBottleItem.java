package net.minecraft.item;

import net.minecraft.entity.item.ExperienceBottleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class ExperienceBottleItem extends Item {
   public ExperienceBottleItem(Item.Properties p_i48500_1_) {
      super(p_i48500_1_);
   }

   public boolean isFoil(ItemStack p_77636_1_) {
      return true;
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
      p_77659_1_.playSound((PlayerEntity)null, p_77659_2_.getX(), p_77659_2_.getY(), p_77659_2_.getZ(), SoundEvents.EXPERIENCE_BOTTLE_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      if (!p_77659_1_.isClientSide) {
         ExperienceBottleEntity experiencebottleentity = new ExperienceBottleEntity(p_77659_1_, p_77659_2_);
         experiencebottleentity.setItem(itemstack);
         experiencebottleentity.shootFromRotation(p_77659_2_, p_77659_2_.xRot, p_77659_2_.yRot, -20.0F, 0.7F, 1.0F);
         p_77659_1_.addFreshEntity(experiencebottleentity);
      }

      p_77659_2_.awardStat(Stats.ITEM_USED.get(this));
      if (!p_77659_2_.abilities.instabuild) {
         itemstack.shrink(1);
      }

      return ActionResult.sidedSuccess(itemstack, p_77659_1_.isClientSide());
   }
}
