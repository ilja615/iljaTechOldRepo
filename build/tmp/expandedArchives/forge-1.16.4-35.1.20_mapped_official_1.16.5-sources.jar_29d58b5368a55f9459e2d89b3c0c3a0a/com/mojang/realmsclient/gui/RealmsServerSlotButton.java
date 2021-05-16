package com.mojang.realmsclient.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.IScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsServerSlotButton extends Button implements IScreen {
   public static final ResourceLocation SLOT_FRAME_LOCATION = new ResourceLocation("realms", "textures/gui/realms/slot_frame.png");
   public static final ResourceLocation EMPTY_SLOT_LOCATION = new ResourceLocation("realms", "textures/gui/realms/empty_frame.png");
   public static final ResourceLocation DEFAULT_WORLD_SLOT_1 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_0.png");
   public static final ResourceLocation DEFAULT_WORLD_SLOT_2 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_2.png");
   public static final ResourceLocation DEFAULT_WORLD_SLOT_3 = new ResourceLocation("minecraft", "textures/gui/title/background/panorama_3.png");
   private static final ITextComponent SLOT_ACTIVE_TOOLTIP = new TranslationTextComponent("mco.configure.world.slot.tooltip.active");
   private static final ITextComponent SWITCH_TO_MINIGAME_SLOT_TOOLTIP = new TranslationTextComponent("mco.configure.world.slot.tooltip.minigame");
   private static final ITextComponent SWITCH_TO_WORLD_SLOT_TOOLTIP = new TranslationTextComponent("mco.configure.world.slot.tooltip");
   private final Supplier<RealmsServer> serverDataProvider;
   private final Consumer<ITextComponent> toolTipSetter;
   private final int slotIndex;
   private int animTick;
   @Nullable
   private RealmsServerSlotButton.ServerData state;

   public RealmsServerSlotButton(int p_i232195_1_, int p_i232195_2_, int p_i232195_3_, int p_i232195_4_, Supplier<RealmsServer> p_i232195_5_, Consumer<ITextComponent> p_i232195_6_, int p_i232195_7_, Button.IPressable p_i232195_8_) {
      super(p_i232195_1_, p_i232195_2_, p_i232195_3_, p_i232195_4_, StringTextComponent.EMPTY, p_i232195_8_);
      this.serverDataProvider = p_i232195_5_;
      this.slotIndex = p_i232195_7_;
      this.toolTipSetter = p_i232195_6_;
   }

   @Nullable
   public RealmsServerSlotButton.ServerData getState() {
      return this.state;
   }

   public void tick() {
      ++this.animTick;
      RealmsServer realmsserver = this.serverDataProvider.get();
      if (realmsserver != null) {
         RealmsWorldOptions realmsworldoptions = realmsserver.slots.get(this.slotIndex);
         boolean flag2 = this.slotIndex == 4;
         boolean flag;
         String s;
         long i;
         String s1;
         boolean flag1;
         if (flag2) {
            flag = realmsserver.worldType == RealmsServer.ServerType.MINIGAME;
            s = "Minigame";
            i = (long)realmsserver.minigameId;
            s1 = realmsserver.minigameImage;
            flag1 = realmsserver.minigameId == -1;
         } else {
            flag = realmsserver.activeSlot == this.slotIndex && realmsserver.worldType != RealmsServer.ServerType.MINIGAME;
            s = realmsworldoptions.getSlotName(this.slotIndex);
            i = realmsworldoptions.templateId;
            s1 = realmsworldoptions.templateImage;
            flag1 = realmsworldoptions.empty;
         }

         RealmsServerSlotButton.Action realmsserverslotbutton$action = getAction(realmsserver, flag, flag2);
         Pair<ITextComponent, ITextComponent> pair = this.getTooltipAndNarration(realmsserver, s, flag1, flag2, realmsserverslotbutton$action);
         this.state = new RealmsServerSlotButton.ServerData(flag, s, i, s1, flag1, flag2, realmsserverslotbutton$action, pair.getFirst());
         this.setMessage(pair.getSecond());
      }
   }

   private static RealmsServerSlotButton.Action getAction(RealmsServer p_237720_0_, boolean p_237720_1_, boolean p_237720_2_) {
      if (p_237720_1_) {
         if (!p_237720_0_.expired && p_237720_0_.state != RealmsServer.Status.UNINITIALIZED) {
            return RealmsServerSlotButton.Action.JOIN;
         }
      } else {
         if (!p_237720_2_) {
            return RealmsServerSlotButton.Action.SWITCH_SLOT;
         }

         if (!p_237720_0_.expired) {
            return RealmsServerSlotButton.Action.SWITCH_SLOT;
         }
      }

      return RealmsServerSlotButton.Action.NOTHING;
   }

   private Pair<ITextComponent, ITextComponent> getTooltipAndNarration(RealmsServer p_237719_1_, String p_237719_2_, boolean p_237719_3_, boolean p_237719_4_, RealmsServerSlotButton.Action p_237719_5_) {
      if (p_237719_5_ == RealmsServerSlotButton.Action.NOTHING) {
         return Pair.of((ITextComponent)null, new StringTextComponent(p_237719_2_));
      } else {
         ITextComponent itextcomponent;
         if (p_237719_4_) {
            if (p_237719_3_) {
               itextcomponent = StringTextComponent.EMPTY;
            } else {
               itextcomponent = (new StringTextComponent(" ")).append(p_237719_2_).append(" ").append(p_237719_1_.minigameName);
            }
         } else {
            itextcomponent = (new StringTextComponent(" ")).append(p_237719_2_);
         }

         ITextComponent itextcomponent1;
         if (p_237719_5_ == RealmsServerSlotButton.Action.JOIN) {
            itextcomponent1 = SLOT_ACTIVE_TOOLTIP;
         } else {
            itextcomponent1 = p_237719_4_ ? SWITCH_TO_MINIGAME_SLOT_TOOLTIP : SWITCH_TO_WORLD_SLOT_TOOLTIP;
         }

         ITextComponent itextcomponent2 = itextcomponent1.copy().append(itextcomponent);
         return Pair.of(itextcomponent1, itextcomponent2);
      }
   }

   public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
      if (this.state != null) {
         this.drawSlotFrame(p_230431_1_, this.x, this.y, p_230431_2_, p_230431_3_, this.state.isCurrentlyActiveSlot, this.state.slotName, this.slotIndex, this.state.imageId, this.state.image, this.state.empty, this.state.minigame, this.state.action, this.state.actionPrompt);
      }
   }

   private void drawSlotFrame(MatrixStack p_237718_1_, int p_237718_2_, int p_237718_3_, int p_237718_4_, int p_237718_5_, boolean p_237718_6_, String p_237718_7_, int p_237718_8_, long p_237718_9_, @Nullable String p_237718_11_, boolean p_237718_12_, boolean p_237718_13_, RealmsServerSlotButton.Action p_237718_14_, @Nullable ITextComponent p_237718_15_) {
      boolean flag = this.isHovered();
      if (this.isMouseOver((double)p_237718_4_, (double)p_237718_5_) && p_237718_15_ != null) {
         this.toolTipSetter.accept(p_237718_15_);
      }

      Minecraft minecraft = Minecraft.getInstance();
      TextureManager texturemanager = minecraft.getTextureManager();
      if (p_237718_13_) {
         RealmsTextureManager.bindWorldTemplate(String.valueOf(p_237718_9_), p_237718_11_);
      } else if (p_237718_12_) {
         texturemanager.bind(EMPTY_SLOT_LOCATION);
      } else if (p_237718_11_ != null && p_237718_9_ != -1L) {
         RealmsTextureManager.bindWorldTemplate(String.valueOf(p_237718_9_), p_237718_11_);
      } else if (p_237718_8_ == 1) {
         texturemanager.bind(DEFAULT_WORLD_SLOT_1);
      } else if (p_237718_8_ == 2) {
         texturemanager.bind(DEFAULT_WORLD_SLOT_2);
      } else if (p_237718_8_ == 3) {
         texturemanager.bind(DEFAULT_WORLD_SLOT_3);
      }

      if (p_237718_6_) {
         float f = 0.85F + 0.15F * MathHelper.cos((float)this.animTick * 0.2F);
         RenderSystem.color4f(f, f, f, 1.0F);
      } else {
         RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      }

      blit(p_237718_1_, p_237718_2_ + 3, p_237718_3_ + 3, 0.0F, 0.0F, 74, 74, 74, 74);
      texturemanager.bind(SLOT_FRAME_LOCATION);
      boolean flag1 = flag && p_237718_14_ != RealmsServerSlotButton.Action.NOTHING;
      if (flag1) {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      } else if (p_237718_6_) {
         RenderSystem.color4f(0.8F, 0.8F, 0.8F, 1.0F);
      } else {
         RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      }

      blit(p_237718_1_, p_237718_2_, p_237718_3_, 0.0F, 0.0F, 80, 80, 80, 80);
      drawCenteredString(p_237718_1_, minecraft.font, p_237718_7_, p_237718_2_ + 40, p_237718_3_ + 66, 16777215);
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Action {
      NOTHING,
      SWITCH_SLOT,
      JOIN;
   }

   @OnlyIn(Dist.CLIENT)
   public static class ServerData {
      private final boolean isCurrentlyActiveSlot;
      private final String slotName;
      private final long imageId;
      private final String image;
      public final boolean empty;
      public final boolean minigame;
      public final RealmsServerSlotButton.Action action;
      @Nullable
      private final ITextComponent actionPrompt;

      ServerData(boolean p_i232196_1_, String p_i232196_2_, long p_i232196_3_, @Nullable String p_i232196_5_, boolean p_i232196_6_, boolean p_i232196_7_, RealmsServerSlotButton.Action p_i232196_8_, @Nullable ITextComponent p_i232196_9_) {
         this.isCurrentlyActiveSlot = p_i232196_1_;
         this.slotName = p_i232196_2_;
         this.imageId = p_i232196_3_;
         this.image = p_i232196_5_;
         this.empty = p_i232196_6_;
         this.minigame = p_i232196_7_;
         this.action = p_i232196_8_;
         this.actionPrompt = p_i232196_9_;
      }
   }
}
