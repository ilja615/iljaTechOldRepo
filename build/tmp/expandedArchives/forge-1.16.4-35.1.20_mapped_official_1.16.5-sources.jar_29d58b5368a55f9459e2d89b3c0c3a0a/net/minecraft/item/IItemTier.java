package net.minecraft.item;

import net.minecraft.item.crafting.Ingredient;

public interface IItemTier {
   int getUses();

   float getSpeed();

   float getAttackDamageBonus();

   int getLevel();

   int getEnchantmentValue();

   Ingredient getRepairIngredient();
}
