package net.minecraft.item;

public class BookItem extends Item {
   public BookItem(Item.Properties p_i48524_1_) {
      super(p_i48524_1_);
   }

   public boolean isEnchantable(ItemStack p_77616_1_) {
      return p_77616_1_.getCount() == 1;
   }

   public int getEnchantmentValue() {
      return 1;
   }
}
