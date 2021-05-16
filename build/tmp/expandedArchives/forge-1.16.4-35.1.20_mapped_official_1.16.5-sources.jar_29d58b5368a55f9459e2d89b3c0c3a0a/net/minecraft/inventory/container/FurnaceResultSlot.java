package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;

public class FurnaceResultSlot extends Slot {
   private final PlayerEntity player;
   private int removeCount;

   public FurnaceResultSlot(PlayerEntity p_i45793_1_, IInventory p_i45793_2_, int p_i45793_3_, int p_i45793_4_, int p_i45793_5_) {
      super(p_i45793_2_, p_i45793_3_, p_i45793_4_, p_i45793_5_);
      this.player = p_i45793_1_;
   }

   public boolean mayPlace(ItemStack p_75214_1_) {
      return false;
   }

   public ItemStack remove(int p_75209_1_) {
      if (this.hasItem()) {
         this.removeCount += Math.min(p_75209_1_, this.getItem().getCount());
      }

      return super.remove(p_75209_1_);
   }

   public ItemStack onTake(PlayerEntity p_190901_1_, ItemStack p_190901_2_) {
      this.checkTakeAchievements(p_190901_2_);
      super.onTake(p_190901_1_, p_190901_2_);
      return p_190901_2_;
   }

   protected void onQuickCraft(ItemStack p_75210_1_, int p_75210_2_) {
      this.removeCount += p_75210_2_;
      this.checkTakeAchievements(p_75210_1_);
   }

   protected void checkTakeAchievements(ItemStack p_75208_1_) {
      p_75208_1_.onCraftedBy(this.player.level, this.player, this.removeCount);
      if (!this.player.level.isClientSide && this.container instanceof AbstractFurnaceTileEntity) {
         ((AbstractFurnaceTileEntity)this.container).awardUsedRecipesAndPopExperience(this.player);
      }

      this.removeCount = 0;
      net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerSmeltedEvent(this.player, p_75208_1_);
   }
}
