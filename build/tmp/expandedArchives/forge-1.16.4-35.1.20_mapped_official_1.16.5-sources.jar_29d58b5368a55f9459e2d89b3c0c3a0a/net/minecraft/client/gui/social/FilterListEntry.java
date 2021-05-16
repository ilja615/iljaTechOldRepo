package net.minecraft.client.gui.social;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FilterListEntry extends AbstractOptionList.Entry<FilterListEntry> {
   private final Minecraft minecraft;
   private final List<IGuiEventListener> children;
   private final UUID id;
   private final String playerName;
   private final Supplier<ResourceLocation> skinGetter;
   private boolean isRemoved;
   @Nullable
   private Button hideButton;
   @Nullable
   private Button showButton;
   private final List<IReorderingProcessor> hideTooltip;
   private final List<IReorderingProcessor> showTooltip;
   private float tooltipHoverTime;
   private static final ITextComponent HIDDEN = (new TranslationTextComponent("gui.socialInteractions.status_hidden")).withStyle(TextFormatting.ITALIC);
   private static final ITextComponent BLOCKED = (new TranslationTextComponent("gui.socialInteractions.status_blocked")).withStyle(TextFormatting.ITALIC);
   private static final ITextComponent OFFLINE = (new TranslationTextComponent("gui.socialInteractions.status_offline")).withStyle(TextFormatting.ITALIC);
   private static final ITextComponent HIDDEN_OFFLINE = (new TranslationTextComponent("gui.socialInteractions.status_hidden_offline")).withStyle(TextFormatting.ITALIC);
   private static final ITextComponent BLOCKED_OFFLINE = (new TranslationTextComponent("gui.socialInteractions.status_blocked_offline")).withStyle(TextFormatting.ITALIC);
   public static final int SKIN_SHADE = ColorHelper.PackedColor.color(190, 0, 0, 0);
   public static final int BG_FILL = ColorHelper.PackedColor.color(255, 74, 74, 74);
   public static final int BG_FILL_REMOVED = ColorHelper.PackedColor.color(255, 48, 48, 48);
   public static final int PLAYERNAME_COLOR = ColorHelper.PackedColor.color(255, 255, 255, 255);
   public static final int PLAYER_STATUS_COLOR = ColorHelper.PackedColor.color(140, 255, 255, 255);

   public FilterListEntry(Minecraft p_i244722_1_, SocialInteractionsScreen p_i244722_2_, UUID p_i244722_3_, String p_i244722_4_, Supplier<ResourceLocation> p_i244722_5_) {
      this.minecraft = p_i244722_1_;
      this.id = p_i244722_3_;
      this.playerName = p_i244722_4_;
      this.skinGetter = p_i244722_5_;
      this.hideTooltip = p_i244722_1_.font.split(new TranslationTextComponent("gui.socialInteractions.tooltip.hide", p_i244722_4_), 150);
      this.showTooltip = p_i244722_1_.font.split(new TranslationTextComponent("gui.socialInteractions.tooltip.show", p_i244722_4_), 150);
      FilterManager filtermanager = p_i244722_1_.getPlayerSocialManager();
      if (!p_i244722_1_.player.getGameProfile().getId().equals(p_i244722_3_) && !filtermanager.isBlocked(p_i244722_3_)) {
         this.hideButton = new ImageButton(0, 0, 20, 20, 0, 38, 20, SocialInteractionsScreen.SOCIAL_INTERACTIONS_LOCATION, 256, 256, (p_244751_4_) -> {
            filtermanager.hidePlayer(p_i244722_3_);
            this.onHiddenOrShown(true, new TranslationTextComponent("gui.socialInteractions.hidden_in_chat", p_i244722_4_));
         }, (p_244637_3_, p_244637_4_, p_244637_5_, p_244637_6_) -> {
            this.tooltipHoverTime += p_i244722_1_.getDeltaFrameTime();
            if (this.tooltipHoverTime >= 10.0F) {
               p_i244722_2_.setPostRenderRunnable(() -> {
                  postRenderTooltip(p_i244722_2_, p_244637_4_, this.hideTooltip, p_244637_5_, p_244637_6_);
               });
            }

         }, new TranslationTextComponent("gui.socialInteractions.hide")) {
            protected IFormattableTextComponent createNarrationMessage() {
               return FilterListEntry.this.getEntryNarationMessage(super.createNarrationMessage());
            }
         };
         this.showButton = new ImageButton(0, 0, 20, 20, 20, 38, 20, SocialInteractionsScreen.SOCIAL_INTERACTIONS_LOCATION, 256, 256, (p_244749_4_) -> {
            filtermanager.showPlayer(p_i244722_3_);
            this.onHiddenOrShown(false, new TranslationTextComponent("gui.socialInteractions.shown_in_chat", p_i244722_4_));
         }, (p_244631_3_, p_244631_4_, p_244631_5_, p_244631_6_) -> {
            this.tooltipHoverTime += p_i244722_1_.getDeltaFrameTime();
            if (this.tooltipHoverTime >= 10.0F) {
               p_i244722_2_.setPostRenderRunnable(() -> {
                  postRenderTooltip(p_i244722_2_, p_244631_4_, this.showTooltip, p_244631_5_, p_244631_6_);
               });
            }

         }, new TranslationTextComponent("gui.socialInteractions.show")) {
            protected IFormattableTextComponent createNarrationMessage() {
               return FilterListEntry.this.getEntryNarationMessage(super.createNarrationMessage());
            }
         };
         this.showButton.visible = filtermanager.isHidden(p_i244722_3_);
         this.hideButton.visible = !this.showButton.visible;
         this.children = ImmutableList.of(this.hideButton, this.showButton);
      } else {
         this.children = ImmutableList.of();
      }

   }

   public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
      int i = p_230432_4_ + 4;
      int j = p_230432_3_ + (p_230432_6_ - 24) / 2;
      int k = i + 24 + 4;
      ITextComponent itextcomponent = this.getStatusComponent();
      int l;
      if (itextcomponent == StringTextComponent.EMPTY) {
         AbstractGui.fill(p_230432_1_, p_230432_4_, p_230432_3_, p_230432_4_ + p_230432_5_, p_230432_3_ + p_230432_6_, BG_FILL);
         l = p_230432_3_ + (p_230432_6_ - 9) / 2;
      } else {
         AbstractGui.fill(p_230432_1_, p_230432_4_, p_230432_3_, p_230432_4_ + p_230432_5_, p_230432_3_ + p_230432_6_, BG_FILL_REMOVED);
         l = p_230432_3_ + (p_230432_6_ - (9 + 9)) / 2;
         this.minecraft.font.draw(p_230432_1_, itextcomponent, (float)k, (float)(l + 12), PLAYER_STATUS_COLOR);
      }

      this.minecraft.getTextureManager().bind(this.skinGetter.get());
      AbstractGui.blit(p_230432_1_, i, j, 24, 24, 8.0F, 8.0F, 8, 8, 64, 64);
      RenderSystem.enableBlend();
      AbstractGui.blit(p_230432_1_, i, j, 24, 24, 40.0F, 8.0F, 8, 8, 64, 64);
      RenderSystem.disableBlend();
      this.minecraft.font.draw(p_230432_1_, this.playerName, (float)k, (float)l, PLAYERNAME_COLOR);
      if (this.isRemoved) {
         AbstractGui.fill(p_230432_1_, i, j, i + 24, j + 24, SKIN_SHADE);
      }

      if (this.hideButton != null && this.showButton != null) {
         float f = this.tooltipHoverTime;
         this.hideButton.x = p_230432_4_ + (p_230432_5_ - this.hideButton.getWidth() - 4);
         this.hideButton.y = p_230432_3_ + (p_230432_6_ - this.hideButton.getHeight()) / 2;
         this.hideButton.render(p_230432_1_, p_230432_7_, p_230432_8_, p_230432_10_);
         this.showButton.x = p_230432_4_ + (p_230432_5_ - this.showButton.getWidth() - 4);
         this.showButton.y = p_230432_3_ + (p_230432_6_ - this.showButton.getHeight()) / 2;
         this.showButton.render(p_230432_1_, p_230432_7_, p_230432_8_, p_230432_10_);
         if (f == this.tooltipHoverTime) {
            this.tooltipHoverTime = 0.0F;
         }
      }

   }

   public List<? extends IGuiEventListener> children() {
      return this.children;
   }

   public String getPlayerName() {
      return this.playerName;
   }

   public UUID getPlayerId() {
      return this.id;
   }

   public void setRemoved(boolean p_244641_1_) {
      this.isRemoved = p_244641_1_;
   }

   private void onHiddenOrShown(boolean p_244635_1_, ITextComponent p_244635_2_) {
      this.showButton.visible = p_244635_1_;
      this.hideButton.visible = !p_244635_1_;
      this.minecraft.gui.getChat().addMessage(p_244635_2_);
      NarratorChatListener.INSTANCE.sayNow(p_244635_2_.getString());
   }

   private IFormattableTextComponent getEntryNarationMessage(IFormattableTextComponent p_244750_1_) {
      ITextComponent itextcomponent = this.getStatusComponent();
      return itextcomponent == StringTextComponent.EMPTY ? (new StringTextComponent(this.playerName)).append(", ").append(p_244750_1_) : (new StringTextComponent(this.playerName)).append(", ").append(itextcomponent).append(", ").append(p_244750_1_);
   }

   private ITextComponent getStatusComponent() {
      boolean flag = this.minecraft.getPlayerSocialManager().isHidden(this.id);
      boolean flag1 = this.minecraft.getPlayerSocialManager().isBlocked(this.id);
      if (flag1 && this.isRemoved) {
         return BLOCKED_OFFLINE;
      } else if (flag && this.isRemoved) {
         return HIDDEN_OFFLINE;
      } else if (flag1) {
         return BLOCKED;
      } else if (flag) {
         return HIDDEN;
      } else {
         return this.isRemoved ? OFFLINE : StringTextComponent.EMPTY;
      }
   }

   private static void postRenderTooltip(SocialInteractionsScreen p_244634_0_, MatrixStack p_244634_1_, List<IReorderingProcessor> p_244634_2_, int p_244634_3_, int p_244634_4_) {
      p_244634_0_.renderTooltip(p_244634_1_, p_244634_2_, p_244634_3_, p_244634_4_);
      p_244634_0_.setPostRenderRunnable((Runnable)null);
   }
}
