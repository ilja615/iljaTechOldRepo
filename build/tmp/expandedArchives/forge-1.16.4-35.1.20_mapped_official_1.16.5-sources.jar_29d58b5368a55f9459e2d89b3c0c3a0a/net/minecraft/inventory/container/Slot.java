package net.minecraft.inventory.container;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Slot {
   private final int slot;
   public final IInventory container;
   public int index;
   public final int x;
   public final int y;

   public Slot(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
      this.container = p_i1824_1_;
      this.slot = p_i1824_2_;
      this.x = p_i1824_3_;
      this.y = p_i1824_4_;
   }

   public void onQuickCraft(ItemStack p_75220_1_, ItemStack p_75220_2_) {
      int i = p_75220_2_.getCount() - p_75220_1_.getCount();
      if (i > 0) {
         this.onQuickCraft(p_75220_2_, i);
      }

   }

   protected void onQuickCraft(ItemStack p_75210_1_, int p_75210_2_) {
   }

   protected void onSwapCraft(int p_190900_1_) {
   }

   protected void checkTakeAchievements(ItemStack p_75208_1_) {
   }

   public ItemStack onTake(PlayerEntity p_190901_1_, ItemStack p_190901_2_) {
      this.setChanged();
      return p_190901_2_;
   }

   public boolean mayPlace(ItemStack p_75214_1_) {
      return true;
   }

   public ItemStack getItem() {
      return this.container.getItem(this.slot);
   }

   public boolean hasItem() {
      return !this.getItem().isEmpty();
   }

   public void set(ItemStack p_75215_1_) {
      this.container.setItem(this.slot, p_75215_1_);
      this.setChanged();
   }

   public void setChanged() {
      this.container.setChanged();
   }

   public int getMaxStackSize() {
      return this.container.getMaxStackSize();
   }

   public int getMaxStackSize(ItemStack p_178170_1_) {
      return this.getMaxStackSize();
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
      return backgroundPair;
   }

   public ItemStack remove(int p_75209_1_) {
      return this.container.removeItem(this.slot, p_75209_1_);
   }

   public boolean mayPickup(PlayerEntity p_82869_1_) {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isActive() {
      return true;
   }

   /**
    * Retrieves the index in the inventory for this slot, this value should typically not
    * be used, but can be useful for some occasions.
    *
    * @return Index in associated inventory for this slot.
    */
   public int getSlotIndex() {
      return slot;
   }

   /**
    * Checks if the other slot is in the same inventory, by comparing the inventory reference.
    * @param other
    * @return true if the other slot is in the same inventory
    */
   public boolean isSameInventory(Slot other) {
      return this.container == other.container;
   }

   private Pair<ResourceLocation, ResourceLocation> backgroundPair;
   /**
    * Sets the background atlas and sprite location.
    *
    * @param atlas The atlas name
    * @param sprite The sprite located on that atlas.
    * @return this, to allow chaining.
    */
   public Slot setBackground(ResourceLocation atlas, ResourceLocation sprite) {
       this.backgroundPair = Pair.of(atlas, sprite);
       return this;
   }
}
