package net.minecraft.item;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IArmorMaterial {
   int getDurabilityForSlot(EquipmentSlotType p_200896_1_);

   int getDefenseForSlot(EquipmentSlotType p_200902_1_);

   int getEnchantmentValue();

   SoundEvent getEquipSound();

   Ingredient getRepairIngredient();

   @OnlyIn(Dist.CLIENT)
   String getName();

   float getToughness();

   float getKnockbackResistance();
}
