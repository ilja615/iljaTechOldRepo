package net.minecraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRideable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class OnAStickItem<T extends Entity & IRideable> extends Item {
   private final EntityType<T> canInteractWith;
   private final int consumeItemDamage;

   public OnAStickItem(Item.Properties p_i231594_1_, EntityType<T> p_i231594_2_, int p_i231594_3_) {
      super(p_i231594_1_);
      this.canInteractWith = p_i231594_2_;
      this.consumeItemDamage = p_i231594_3_;
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
      if (p_77659_1_.isClientSide) {
         return ActionResult.pass(itemstack);
      } else {
         Entity entity = p_77659_2_.getVehicle();
         if (p_77659_2_.isPassenger() && entity instanceof IRideable && entity.getType() == this.canInteractWith) {
            IRideable irideable = (IRideable)entity;
            if (irideable.boost()) {
               itemstack.hurtAndBreak(this.consumeItemDamage, p_77659_2_, (p_234682_1_) -> {
                  p_234682_1_.broadcastBreakEvent(p_77659_3_);
               });
               if (itemstack.isEmpty()) {
                  ItemStack itemstack1 = new ItemStack(Items.FISHING_ROD);
                  itemstack1.setTag(itemstack.getTag());
                  return ActionResult.success(itemstack1);
               }

               return ActionResult.success(itemstack);
            }
         }

         p_77659_2_.awardStat(Stats.ITEM_USED.get(this));
         return ActionResult.pass(itemstack);
      }
   }
}
