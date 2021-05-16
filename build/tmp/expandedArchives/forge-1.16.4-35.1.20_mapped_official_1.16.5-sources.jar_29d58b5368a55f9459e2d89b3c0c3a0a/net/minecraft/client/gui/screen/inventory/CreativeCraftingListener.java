package net.minecraft.client.gui.screen.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreativeCraftingListener implements IContainerListener {
   private final Minecraft minecraft;

   public CreativeCraftingListener(Minecraft p_i46314_1_) {
      this.minecraft = p_i46314_1_;
   }

   public void refreshContainer(Container p_71110_1_, NonNullList<ItemStack> p_71110_2_) {
   }

   public void slotChanged(Container p_71111_1_, int p_71111_2_, ItemStack p_71111_3_) {
      this.minecraft.gameMode.handleCreativeModeItemAdd(p_71111_3_, p_71111_2_);
   }

   public void setContainerData(Container p_71112_1_, int p_71112_2_, int p_71112_3_) {
   }
}
