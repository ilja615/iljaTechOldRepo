package net.minecraft.item;

public class DyeableHorseArmorItem extends HorseArmorItem implements IDyeableArmorItem {
   public DyeableHorseArmorItem(int armorValue, String p_i50047_2_, Item.Properties builder) {
      super(armorValue, p_i50047_2_, builder);
   }
   public DyeableHorseArmorItem(int armorValue, net.minecraft.util.ResourceLocation texture, Item.Properties builder) {
      super(armorValue, texture, builder);
   }
}
