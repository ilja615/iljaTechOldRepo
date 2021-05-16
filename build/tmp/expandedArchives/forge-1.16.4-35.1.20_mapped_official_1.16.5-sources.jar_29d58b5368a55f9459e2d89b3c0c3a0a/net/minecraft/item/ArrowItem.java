package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.world.World;

public class ArrowItem extends Item {
   public ArrowItem(Item.Properties p_i48531_1_) {
      super(p_i48531_1_);
   }

   public AbstractArrowEntity createArrow(World p_200887_1_, ItemStack p_200887_2_, LivingEntity p_200887_3_) {
      ArrowEntity arrowentity = new ArrowEntity(p_200887_1_, p_200887_3_);
      arrowentity.setEffectsFromItem(p_200887_2_);
      return arrowentity;
   }

   public boolean isInfinite(ItemStack stack, ItemStack bow, net.minecraft.entity.player.PlayerEntity player) {
      int enchant = net.minecraft.enchantment.EnchantmentHelper.getItemEnchantmentLevel(net.minecraft.enchantment.Enchantments.INFINITY_ARROWS, bow);
      return enchant <= 0 ? false : this.getClass() == ArrowItem.class;
   }
}
