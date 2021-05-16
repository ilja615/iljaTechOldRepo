package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LecternContainer extends Container {
   private final IInventory lectern;
   private final IIntArray lecternData;

   public LecternContainer(int p_i50075_1_) {
      this(p_i50075_1_, new Inventory(1), new IntArray(1));
   }

   public LecternContainer(int p_i50076_1_, IInventory p_i50076_2_, IIntArray p_i50076_3_) {
      super(ContainerType.LECTERN, p_i50076_1_);
      checkContainerSize(p_i50076_2_, 1);
      checkContainerDataCount(p_i50076_3_, 1);
      this.lectern = p_i50076_2_;
      this.lecternData = p_i50076_3_;
      this.addSlot(new Slot(p_i50076_2_, 0, 0, 0) {
         public void setChanged() {
            super.setChanged();
            LecternContainer.this.slotsChanged(this.container);
         }
      });
      this.addDataSlots(p_i50076_3_);
   }

   public boolean clickMenuButton(PlayerEntity p_75140_1_, int p_75140_2_) {
      if (p_75140_2_ >= 100) {
         int k = p_75140_2_ - 100;
         this.setData(0, k);
         return true;
      } else {
         switch(p_75140_2_) {
         case 1:
            int j = this.lecternData.get(0);
            this.setData(0, j - 1);
            return true;
         case 2:
            int i = this.lecternData.get(0);
            this.setData(0, i + 1);
            return true;
         case 3:
            if (!p_75140_1_.mayBuild()) {
               return false;
            }

            ItemStack itemstack = this.lectern.removeItemNoUpdate(0);
            this.lectern.setChanged();
            if (!p_75140_1_.inventory.add(itemstack)) {
               p_75140_1_.drop(itemstack, false);
            }

            return true;
         default:
            return false;
         }
      }
   }

   public void setData(int p_75137_1_, int p_75137_2_) {
      super.setData(p_75137_1_, p_75137_2_);
      this.broadcastChanges();
   }

   public boolean stillValid(PlayerEntity p_75145_1_) {
      return this.lectern.stillValid(p_75145_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getBook() {
      return this.lectern.getItem(0);
   }

   @OnlyIn(Dist.CLIENT)
   public int getPage() {
      return this.lecternData.get(0);
   }
}
