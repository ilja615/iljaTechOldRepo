package net.minecraft.client.gui.advancements;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.client.CSeenAdvancementsPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancementsScreen extends Screen implements ClientAdvancementManager.IListener {
   private static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("textures/gui/advancements/window.png");
   private static final ResourceLocation TABS_LOCATION = new ResourceLocation("textures/gui/advancements/tabs.png");
   private static final ITextComponent VERY_SAD_LABEL = new TranslationTextComponent("advancements.sad_label");
   private static final ITextComponent NO_ADVANCEMENTS_LABEL = new TranslationTextComponent("advancements.empty");
   private static final ITextComponent TITLE = new TranslationTextComponent("gui.advancements");
   private final ClientAdvancementManager advancements;
   private final Map<Advancement, AdvancementTabGui> tabs = Maps.newLinkedHashMap();
   private AdvancementTabGui selectedTab;
   private boolean isScrolling;
   private static int tabPage, maxPages;

   public AdvancementsScreen(ClientAdvancementManager p_i47383_1_) {
      super(NarratorChatListener.NO_TITLE);
      this.advancements = p_i47383_1_;
   }

   protected void init() {
      this.tabs.clear();
      this.selectedTab = null;
      this.advancements.setListener(this);
      if (this.selectedTab == null && !this.tabs.isEmpty()) {
         this.advancements.setSelectedTab(this.tabs.values().iterator().next().getAdvancement(), true);
      } else {
         this.advancements.setSelectedTab(this.selectedTab == null ? null : this.selectedTab.getAdvancement(), true);
      }
      if (this.tabs.size() > AdvancementTabType.MAX_TABS) {
          int guiLeft = (this.width - 252) / 2;
          int guiTop = (this.height - 140) / 2;
          addButton(new net.minecraft.client.gui.widget.button.Button(guiLeft,            guiTop - 50, 20, 20, new net.minecraft.util.text.StringTextComponent("<"), b -> tabPage = Math.max(tabPage - 1, 0       )));
          addButton(new net.minecraft.client.gui.widget.button.Button(guiLeft + 252 - 20, guiTop - 50, 20, 20, new net.minecraft.util.text.StringTextComponent(">"), b -> tabPage = Math.min(tabPage + 1, maxPages)));
          maxPages = this.tabs.size() / AdvancementTabType.MAX_TABS;
      }
   }

   public void removed() {
      this.advancements.setListener((ClientAdvancementManager.IListener)null);
      ClientPlayNetHandler clientplaynethandler = this.minecraft.getConnection();
      if (clientplaynethandler != null) {
         clientplaynethandler.send(CSeenAdvancementsPacket.closedScreen());
      }

   }

   public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
      if (p_231044_5_ == 0) {
         int i = (this.width - 252) / 2;
         int j = (this.height - 140) / 2;

         for(AdvancementTabGui advancementtabgui : this.tabs.values()) {
            if (advancementtabgui.getPage() == tabPage && advancementtabgui.isMouseOver(i, j, p_231044_1_, p_231044_3_)) {
               this.advancements.setSelectedTab(advancementtabgui.getAdvancement(), true);
               break;
            }
         }
      }

      return super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      if (this.minecraft.options.keyAdvancements.matches(p_231046_1_, p_231046_2_)) {
         this.minecraft.setScreen((Screen)null);
         this.minecraft.mouseHandler.grabMouse();
         return true;
      } else {
         return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
      }
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      int i = (this.width - 252) / 2;
      int j = (this.height - 140) / 2;
      this.renderBackground(p_230430_1_);
      if (maxPages != 0) {
          net.minecraft.util.text.ITextComponent page = new net.minecraft.util.text.StringTextComponent(String.format("%d / %d", tabPage + 1, maxPages + 1));
         int width = this.font.width(page);
         RenderSystem.disableLighting();
         this.font.drawShadow(p_230430_1_, page.getVisualOrderText(), i + (252 / 2) - (width / 2), j - 44, -1);
      }
      this.renderInside(p_230430_1_, p_230430_2_, p_230430_3_, i, j);
      this.renderWindow(p_230430_1_, i, j);
      this.renderTooltips(p_230430_1_, p_230430_2_, p_230430_3_, i, j);
   }

   public boolean mouseDragged(double p_231045_1_, double p_231045_3_, int p_231045_5_, double p_231045_6_, double p_231045_8_) {
      if (p_231045_5_ != 0) {
         this.isScrolling = false;
         return false;
      } else {
         if (!this.isScrolling) {
            this.isScrolling = true;
         } else if (this.selectedTab != null) {
            this.selectedTab.scroll(p_231045_6_, p_231045_8_);
         }

         return true;
      }
   }

   private void renderInside(MatrixStack p_238696_1_, int p_238696_2_, int p_238696_3_, int p_238696_4_, int p_238696_5_) {
      AdvancementTabGui advancementtabgui = this.selectedTab;
      if (advancementtabgui == null) {
         fill(p_238696_1_, p_238696_4_ + 9, p_238696_5_ + 18, p_238696_4_ + 9 + 234, p_238696_5_ + 18 + 113, -16777216);
         int i = p_238696_4_ + 9 + 117;
         drawCenteredString(p_238696_1_, this.font, NO_ADVANCEMENTS_LABEL, i, p_238696_5_ + 18 + 56 - 9 / 2, -1);
         drawCenteredString(p_238696_1_, this.font, VERY_SAD_LABEL, i, p_238696_5_ + 18 + 113 - 9, -1);
      } else {
         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)(p_238696_4_ + 9), (float)(p_238696_5_ + 18), 0.0F);
         advancementtabgui.drawContents(p_238696_1_);
         RenderSystem.popMatrix();
         RenderSystem.depthFunc(515);
         RenderSystem.disableDepthTest();
      }
   }

   public void renderWindow(MatrixStack p_238695_1_, int p_238695_2_, int p_238695_3_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableBlend();
      this.minecraft.getTextureManager().bind(WINDOW_LOCATION);
      this.blit(p_238695_1_, p_238695_2_, p_238695_3_, 0, 0, 252, 140);
      if (this.tabs.size() > 1) {
         this.minecraft.getTextureManager().bind(TABS_LOCATION);

         for(AdvancementTabGui advancementtabgui : this.tabs.values()) {
            if (advancementtabgui.getPage() == tabPage)
            advancementtabgui.drawTab(p_238695_1_, p_238695_2_, p_238695_3_, advancementtabgui == this.selectedTab);
         }

         RenderSystem.enableRescaleNormal();
         RenderSystem.defaultBlendFunc();

         for(AdvancementTabGui advancementtabgui1 : this.tabs.values()) {
            if (advancementtabgui1.getPage() == tabPage)
            advancementtabgui1.drawIcon(p_238695_2_, p_238695_3_, this.itemRenderer);
         }

         RenderSystem.disableBlend();
      }

      this.font.draw(p_238695_1_, TITLE, (float)(p_238695_2_ + 8), (float)(p_238695_3_ + 6), 4210752);
   }

   private void renderTooltips(MatrixStack p_238697_1_, int p_238697_2_, int p_238697_3_, int p_238697_4_, int p_238697_5_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.selectedTab != null) {
         RenderSystem.pushMatrix();
         RenderSystem.enableDepthTest();
         RenderSystem.translatef((float)(p_238697_4_ + 9), (float)(p_238697_5_ + 18), 400.0F);
         this.selectedTab.drawTooltips(p_238697_1_, p_238697_2_ - p_238697_4_ - 9, p_238697_3_ - p_238697_5_ - 18, p_238697_4_, p_238697_5_);
         RenderSystem.disableDepthTest();
         RenderSystem.popMatrix();
      }

      if (this.tabs.size() > 1) {
         for(AdvancementTabGui advancementtabgui : this.tabs.values()) {
            if (advancementtabgui.getPage() == tabPage && advancementtabgui.isMouseOver(p_238697_4_, p_238697_5_, (double)p_238697_2_, (double)p_238697_3_)) {
               this.renderTooltip(p_238697_1_, advancementtabgui.getTitle(), p_238697_2_, p_238697_3_);
            }
         }
      }

   }

   public void onAddAdvancementRoot(Advancement p_191931_1_) {
      AdvancementTabGui advancementtabgui = AdvancementTabGui.create(this.minecraft, this, this.tabs.size(), p_191931_1_);
      if (advancementtabgui != null) {
         this.tabs.put(p_191931_1_, advancementtabgui);
      }
   }

   public void onRemoveAdvancementRoot(Advancement p_191928_1_) {
   }

   public void onAddAdvancementTask(Advancement p_191932_1_) {
      AdvancementTabGui advancementtabgui = this.getTab(p_191932_1_);
      if (advancementtabgui != null) {
         advancementtabgui.addAdvancement(p_191932_1_);
      }

   }

   public void onRemoveAdvancementTask(Advancement p_191929_1_) {
   }

   public void onUpdateAdvancementProgress(Advancement p_191933_1_, AdvancementProgress p_191933_2_) {
      AdvancementEntryGui advancemententrygui = this.getAdvancementWidget(p_191933_1_);
      if (advancemententrygui != null) {
         advancemententrygui.setProgress(p_191933_2_);
      }

   }

   public void onSelectedTabChanged(@Nullable Advancement p_193982_1_) {
      this.selectedTab = this.tabs.get(p_193982_1_);
   }

   public void onAdvancementsCleared() {
      this.tabs.clear();
      this.selectedTab = null;
   }

   @Nullable
   public AdvancementEntryGui getAdvancementWidget(Advancement p_191938_1_) {
      AdvancementTabGui advancementtabgui = this.getTab(p_191938_1_);
      return advancementtabgui == null ? null : advancementtabgui.getWidget(p_191938_1_);
   }

   @Nullable
   private AdvancementTabGui getTab(Advancement p_191935_1_) {
      while(p_191935_1_.getParent() != null) {
         p_191935_1_ = p_191935_1_.getParent();
      }

      return this.tabs.get(p_191935_1_);
   }
}
