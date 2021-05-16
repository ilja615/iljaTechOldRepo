package net.minecraft.item;

public class TieredItem extends Item {
   private final IItemTier tier;

   public TieredItem(IItemTier p_i48459_1_, Item.Properties p_i48459_2_) {
      super(p_i48459_2_.defaultDurability(p_i48459_1_.getUses()));
      this.tier = p_i48459_1_;
   }

   public IItemTier getTier() {
      return this.tier;
   }

   public int getEnchantmentValue() {
      return this.tier.getEnchantmentValue();
   }

   public boolean isValidRepairItem(ItemStack p_82789_1_, ItemStack p_82789_2_) {
      return this.tier.getRepairIngredient().test(p_82789_2_) || super.isValidRepairItem(p_82789_1_, p_82789_2_);
   }
}
