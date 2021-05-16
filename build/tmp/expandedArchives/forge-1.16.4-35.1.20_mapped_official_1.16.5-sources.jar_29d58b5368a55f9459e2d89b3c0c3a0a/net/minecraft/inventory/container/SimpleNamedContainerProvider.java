package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public final class SimpleNamedContainerProvider implements INamedContainerProvider {
   private final ITextComponent title;
   private final IContainerProvider menuConstructor;

   public SimpleNamedContainerProvider(IContainerProvider p_i50396_1_, ITextComponent p_i50396_2_) {
      this.menuConstructor = p_i50396_1_;
      this.title = p_i50396_2_;
   }

   public ITextComponent getDisplayName() {
      return this.title;
   }

   public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
      return this.menuConstructor.createMenu(p_createMenu_1_, p_createMenu_2_, p_createMenu_3_);
   }
}
