package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.AbstractRepairContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AbstractRepairScreen<T extends AbstractRepairContainer> extends ContainerScreen<T> implements IContainerListener {
   private ResourceLocation menuResource;

   public AbstractRepairScreen(T p_i232291_1_, PlayerInventory p_i232291_2_, ITextComponent p_i232291_3_, ResourceLocation p_i232291_4_) {
      super(p_i232291_1_, p_i232291_2_, p_i232291_3_);
      this.menuResource = p_i232291_4_;
   }

   protected void subInit() {
   }

   protected void init() {
      super.init();
      this.subInit();
      this.menu.addSlotListener(this);
   }

   public void removed() {
      super.removed();
      this.menu.removeSlotListener(this);
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      RenderSystem.disableBlend();
      this.renderFg(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
   }

   protected void renderFg(MatrixStack p_230452_1_, int p_230452_2_, int p_230452_3_, float p_230452_4_) {
   }

   protected void renderBg(MatrixStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(this.menuResource);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      this.blit(p_230450_1_, i, j, 0, 0, this.imageWidth, this.imageHeight);
      this.blit(p_230450_1_, i + 59, j + 20, 0, this.imageHeight + (this.menu.getSlot(0).hasItem() ? 0 : 16), 110, 16);
      if ((this.menu.getSlot(0).hasItem() || this.menu.getSlot(1).hasItem()) && !this.menu.getSlot(2).hasItem()) {
         this.blit(p_230450_1_, i + 99, j + 45, this.imageWidth, 0, 28, 21);
      }

   }

   public void refreshContainer(Container p_71110_1_, NonNullList<ItemStack> p_71110_2_) {
      this.slotChanged(p_71110_1_, 0, p_71110_1_.getSlot(0).getItem());
   }

   public void setContainerData(Container p_71112_1_, int p_71112_2_, int p_71112_3_) {
   }

   public void slotChanged(Container p_71111_1_, int p_71111_2_, ItemStack p_71111_3_) {
   }
}
