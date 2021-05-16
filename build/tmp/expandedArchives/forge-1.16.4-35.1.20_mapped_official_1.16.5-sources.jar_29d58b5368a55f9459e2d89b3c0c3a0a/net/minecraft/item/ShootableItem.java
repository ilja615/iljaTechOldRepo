package net.minecraft.item;

import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Hand;

public abstract class ShootableItem extends Item {
   public static final Predicate<ItemStack> ARROW_ONLY = (p_220002_0_) -> {
      return p_220002_0_.getItem().is(ItemTags.ARROWS);
   };
   public static final Predicate<ItemStack> ARROW_OR_FIREWORK = ARROW_ONLY.or((p_220003_0_) -> {
      return p_220003_0_.getItem() == Items.FIREWORK_ROCKET;
   });

   public ShootableItem(Item.Properties p_i50040_1_) {
      super(p_i50040_1_);
   }

   public Predicate<ItemStack> getSupportedHeldProjectiles() {
      return this.getAllSupportedProjectiles();
   }

   public abstract Predicate<ItemStack> getAllSupportedProjectiles();

   public static ItemStack getHeldProjectile(LivingEntity p_220005_0_, Predicate<ItemStack> p_220005_1_) {
      if (p_220005_1_.test(p_220005_0_.getItemInHand(Hand.OFF_HAND))) {
         return p_220005_0_.getItemInHand(Hand.OFF_HAND);
      } else {
         return p_220005_1_.test(p_220005_0_.getItemInHand(Hand.MAIN_HAND)) ? p_220005_0_.getItemInHand(Hand.MAIN_HAND) : ItemStack.EMPTY;
      }
   }

   public int getEnchantmentValue() {
      return 1;
   }

   public abstract int getDefaultProjectileRange();
}
