package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CRenameItemPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnvilScreen extends AbstractRepairScreen<RepairContainer> {
   private static final ResourceLocation ANVIL_LOCATION = new ResourceLocation("textures/gui/container/anvil.png");
   private static final ITextComponent TOO_EXPENSIVE_TEXT = new TranslationTextComponent("container.repair.expensive");
   private TextFieldWidget name;

   public AnvilScreen(RepairContainer p_i51103_1_, PlayerInventory p_i51103_2_, ITextComponent p_i51103_3_) {
      super(p_i51103_1_, p_i51103_2_, p_i51103_3_, ANVIL_LOCATION);
      this.titleLabelX = 60;
   }

   public void tick() {
      super.tick();
      this.name.tick();
   }

   protected void subInit() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      this.name = new TextFieldWidget(this.font, i + 62, j + 24, 103, 12, new TranslationTextComponent("container.repair"));
      this.name.setCanLoseFocus(false);
      this.name.setTextColor(-1);
      this.name.setTextColorUneditable(-1);
      this.name.setBordered(false);
      this.name.setMaxLength(35);
      this.name.setResponder(this::onNameChanged);
      this.children.add(this.name);
      this.setInitialFocus(this.name);
   }

   public void resize(Minecraft p_231152_1_, int p_231152_2_, int p_231152_3_) {
      String s = this.name.getValue();
      this.init(p_231152_1_, p_231152_2_, p_231152_3_);
      this.name.setValue(s);
   }

   public void removed() {
      super.removed();
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      if (p_231046_1_ == 256) {
         this.minecraft.player.closeContainer();
      }

      return !this.name.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_) && !this.name.canConsumeInput() ? super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_) : true;
   }

   private void onNameChanged(String p_214075_1_) {
      if (!p_214075_1_.isEmpty()) {
         String s = p_214075_1_;
         Slot slot = this.menu.getSlot(0);
         if (slot != null && slot.hasItem() && !slot.getItem().hasCustomHoverName() && p_214075_1_.equals(slot.getItem().getHoverName().getString())) {
            s = "";
         }

         this.menu.setItemName(s);
         this.minecraft.player.connection.send(new CRenameItemPacket(s));
      }
   }

   protected void renderLabels(MatrixStack p_230451_1_, int p_230451_2_, int p_230451_3_) {
      RenderSystem.disableBlend();
      super.renderLabels(p_230451_1_, p_230451_2_, p_230451_3_);
      int i = this.menu.getCost();
      if (i > 0) {
         int j = 8453920;
         ITextComponent itextcomponent;
         if (i >= 40 && !this.minecraft.player.abilities.instabuild) {
            itextcomponent = TOO_EXPENSIVE_TEXT;
            j = 16736352;
         } else if (!this.menu.getSlot(2).hasItem()) {
            itextcomponent = null;
         } else {
            itextcomponent = new TranslationTextComponent("container.repair.cost", i);
            if (!this.menu.getSlot(2).mayPickup(this.inventory.player)) {
               j = 16736352;
            }
         }

         if (itextcomponent != null) {
            int k = this.imageWidth - 8 - this.font.width(itextcomponent) - 2;
            int l = 69;
            fill(p_230451_1_, k - 2, 67, this.imageWidth - 8, 79, 1325400064);
            this.font.drawShadow(p_230451_1_, itextcomponent, (float)k, 69.0F, j);
         }
      }

   }

   public void renderFg(MatrixStack p_230452_1_, int p_230452_2_, int p_230452_3_, float p_230452_4_) {
      this.name.render(p_230452_1_, p_230452_2_, p_230452_3_, p_230452_4_);
   }

   public void slotChanged(Container p_71111_1_, int p_71111_2_, ItemStack p_71111_3_) {
      if (p_71111_2_ == 0) {
         this.name.setValue(p_71111_3_.isEmpty() ? "" : p_71111_3_.getHoverName().getString());
         this.name.setEditable(!p_71111_3_.isEmpty());
         this.setFocused(this.name);
      }

   }
}
