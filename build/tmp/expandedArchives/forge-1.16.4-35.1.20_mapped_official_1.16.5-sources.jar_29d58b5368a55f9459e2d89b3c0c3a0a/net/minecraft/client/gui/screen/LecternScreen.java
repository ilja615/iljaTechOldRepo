package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.LecternContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LecternScreen extends ReadBookScreen implements IHasContainer<LecternContainer> {
   private final LecternContainer menu;
   private final IContainerListener listener = new IContainerListener() {
      public void refreshContainer(Container p_71110_1_, NonNullList<ItemStack> p_71110_2_) {
         LecternScreen.this.bookChanged();
      }

      public void slotChanged(Container p_71111_1_, int p_71111_2_, ItemStack p_71111_3_) {
         LecternScreen.this.bookChanged();
      }

      public void setContainerData(Container p_71112_1_, int p_71112_2_, int p_71112_3_) {
         if (p_71112_2_ == 0) {
            LecternScreen.this.pageChanged();
         }

      }
   };

   public LecternScreen(LecternContainer p_i51082_1_, PlayerInventory p_i51082_2_, ITextComponent p_i51082_3_) {
      this.menu = p_i51082_1_;
   }

   public LecternContainer getMenu() {
      return this.menu;
   }

   protected void init() {
      super.init();
      this.menu.addSlotListener(this.listener);
   }

   public void onClose() {
      this.minecraft.player.closeContainer();
      super.onClose();
   }

   public void removed() {
      super.removed();
      this.menu.removeSlotListener(this.listener);
   }

   protected void createMenuControls() {
      if (this.minecraft.player.mayBuild()) {
         this.addButton(new Button(this.width / 2 - 100, 196, 98, 20, DialogTexts.GUI_DONE, (p_214181_1_) -> {
            this.minecraft.setScreen((Screen)null);
         }));
         this.addButton(new Button(this.width / 2 + 2, 196, 98, 20, new TranslationTextComponent("lectern.take_book"), (p_214178_1_) -> {
            this.sendButtonClick(3);
         }));
      } else {
         super.createMenuControls();
      }

   }

   protected void pageBack() {
      this.sendButtonClick(1);
   }

   protected void pageForward() {
      this.sendButtonClick(2);
   }

   protected boolean forcePage(int p_214153_1_) {
      if (p_214153_1_ != this.menu.getPage()) {
         this.sendButtonClick(100 + p_214153_1_);
         return true;
      } else {
         return false;
      }
   }

   private void sendButtonClick(int p_214179_1_) {
      this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, p_214179_1_);
   }

   public boolean isPauseScreen() {
      return false;
   }

   private void bookChanged() {
      ItemStack itemstack = this.menu.getBook();
      this.setBookAccess(ReadBookScreen.IBookInfo.fromItem(itemstack));
   }

   private void pageChanged() {
      this.setPage(this.menu.getPage());
   }
}
