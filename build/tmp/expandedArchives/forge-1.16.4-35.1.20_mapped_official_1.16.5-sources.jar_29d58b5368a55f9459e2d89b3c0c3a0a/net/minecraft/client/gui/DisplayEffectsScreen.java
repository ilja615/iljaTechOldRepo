package net.minecraft.client.gui;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class DisplayEffectsScreen<T extends Container> extends ContainerScreen<T> {
   protected boolean doRenderEffects;

   public DisplayEffectsScreen(T p_i51091_1_, PlayerInventory p_i51091_2_, ITextComponent p_i51091_3_) {
      super(p_i51091_1_, p_i51091_2_, p_i51091_3_);
   }

   protected void init() {
      super.init();
      this.checkEffectRendering();
   }

   protected void checkEffectRendering() {
      if (this.minecraft.player.getActiveEffects().isEmpty()) {
         this.leftPos = (this.width - this.imageWidth) / 2;
         this.doRenderEffects = false;
      } else {
         if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.PotionShiftEvent(this)))
            this.leftPos = (this.width - this.imageWidth) / 2;
         else
         this.leftPos = 160 + (this.width - this.imageWidth - 200) / 2;
         this.doRenderEffects = true;
      }

   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      if (this.doRenderEffects) {
         this.renderEffects(p_230430_1_);
      }

   }

   private void renderEffects(MatrixStack p_238811_1_) {
      int i = this.leftPos - 124;
      Collection<EffectInstance> collection = this.minecraft.player.getActiveEffects();
      if (!collection.isEmpty()) {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         int j = 33;
         if (collection.size() > 5) {
            j = 132 / (collection.size() - 1);
         }

         Iterable<EffectInstance> iterable = collection.stream().filter(effectInstance -> effectInstance.shouldRender()).sorted().collect(java.util.stream.Collectors.toList());
         this.renderBackgrounds(p_238811_1_, i, j, iterable);
         this.renderIcons(p_238811_1_, i, j, iterable);
         this.renderLabels(p_238811_1_, i, j, iterable);
      }
   }

   private void renderBackgrounds(MatrixStack p_238810_1_, int p_238810_2_, int p_238810_3_, Iterable<EffectInstance> p_238810_4_) {
      this.minecraft.getTextureManager().bind(INVENTORY_LOCATION);
      int i = this.topPos;

      for(EffectInstance effectinstance : p_238810_4_) {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.blit(p_238810_1_, p_238810_2_, i, 0, 166, 140, 32);
         i += p_238810_3_;
      }

   }

   private void renderIcons(MatrixStack p_238812_1_, int p_238812_2_, int p_238812_3_, Iterable<EffectInstance> p_238812_4_) {
      PotionSpriteUploader potionspriteuploader = this.minecraft.getMobEffectTextures();
      int i = this.topPos;

      for(EffectInstance effectinstance : p_238812_4_) {
         Effect effect = effectinstance.getEffect();
         TextureAtlasSprite textureatlassprite = potionspriteuploader.get(effect);
         this.minecraft.getTextureManager().bind(textureatlassprite.atlas().location());
         blit(p_238812_1_, p_238812_2_ + 6, i + 7, this.getBlitOffset(), 18, 18, textureatlassprite);
         i += p_238812_3_;
      }

   }

   private void renderLabels(MatrixStack p_238813_1_, int p_238813_2_, int p_238813_3_, Iterable<EffectInstance> p_238813_4_) {
      int i = this.topPos;

      for(EffectInstance effectinstance : p_238813_4_) {
         effectinstance.renderInventoryEffect(this, p_238813_1_, p_238813_2_, i, this.getBlitOffset());
         if (!effectinstance.shouldRenderInvText()) {
            i += p_238813_3_;
            continue;
         }
         String s = I18n.get(effectinstance.getEffect().getDescriptionId());
         if (effectinstance.getAmplifier() >= 1 && effectinstance.getAmplifier() <= 9) {
            s = s + ' ' + I18n.get("enchantment.level." + (effectinstance.getAmplifier() + 1));
         }

         this.font.drawShadow(p_238813_1_, s, (float)(p_238813_2_ + 10 + 18), (float)(i + 6), 16777215);
         String s1 = EffectUtils.formatDuration(effectinstance, 1.0F);
         this.font.drawShadow(p_238813_1_, s1, (float)(p_238813_2_ + 10 + 18), (float)(i + 6 + 10), 8355711);
         i += p_238813_3_;
      }

   }
}
