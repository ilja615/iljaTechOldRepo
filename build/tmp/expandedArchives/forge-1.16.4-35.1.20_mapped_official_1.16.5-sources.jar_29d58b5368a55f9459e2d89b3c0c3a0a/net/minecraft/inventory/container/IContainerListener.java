package net.minecraft.inventory.container;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface IContainerListener {
   void refreshContainer(Container p_71110_1_, NonNullList<ItemStack> p_71110_2_);

   void slotChanged(Container p_71111_1_, int p_71111_2_, ItemStack p_71111_3_);

   void setContainerData(Container p_71112_1_, int p_71112_2_, int p_71112_3_);
}
