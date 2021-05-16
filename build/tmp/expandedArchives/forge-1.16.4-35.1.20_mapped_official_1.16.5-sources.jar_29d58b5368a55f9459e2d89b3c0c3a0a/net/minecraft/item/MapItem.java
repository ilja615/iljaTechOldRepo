package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class MapItem extends AbstractMapItem {
   public MapItem(Item.Properties p_i48506_1_) {
      super(p_i48506_1_);
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack itemstack = FilledMapItem.create(p_77659_1_, MathHelper.floor(p_77659_2_.getX()), MathHelper.floor(p_77659_2_.getZ()), (byte)0, true, false);
      ItemStack itemstack1 = p_77659_2_.getItemInHand(p_77659_3_);
      if (!p_77659_2_.abilities.instabuild) {
         itemstack1.shrink(1);
      }

      p_77659_2_.awardStat(Stats.ITEM_USED.get(this));
      p_77659_2_.playSound(SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1.0F, 1.0F);
      if (itemstack1.isEmpty()) {
         return ActionResult.sidedSuccess(itemstack, p_77659_1_.isClientSide());
      } else {
         if (!p_77659_2_.inventory.add(itemstack.copy())) {
            p_77659_2_.drop(itemstack, false);
         }

         return ActionResult.sidedSuccess(itemstack1, p_77659_1_.isClientSide());
      }
   }
}
