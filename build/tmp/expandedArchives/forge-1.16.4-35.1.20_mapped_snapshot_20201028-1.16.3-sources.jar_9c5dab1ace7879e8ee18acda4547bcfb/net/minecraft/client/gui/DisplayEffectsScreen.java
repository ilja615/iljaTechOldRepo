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
   /** True if there is some potion effect to display */
   protected boolean hasActivePotionEffects;

   public DisplayEffectsScreen(T screenContainer, PlayerInventory inv, ITextComponent titleIn) {
      super(screenContainer, inv, titleIn);
   }

   protected void init() {
      super.init();
      this.updateActivePotionEffects();
   }

   protected void updateActivePotionEffects() {
      if (this.minecraft.player.getActivePotionEffects().isEmpty()) {
         this.guiLeft = (this.width - this.xSize) / 2;
         this.hasActivePotionEffects = false;
      } else {
         if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.PotionShiftEvent(this)))
            this.guiLeft = (this.width - this.xSize) / 2;
         else
         this.guiLeft = 160 + (this.width - this.xSize - 200) / 2;
         this.hasActivePotionEffects = true;
      }

   }

   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      super.render(matrixStack, mouseX, mouseY, partialTicks);
      if (this.hasActivePotionEffects) {
         this.renderEffects(matrixStack);
      }

   }

   private void renderEffects(MatrixStack matrixStack) {
      int i = this.guiLeft - 124;
      Collection<EffectInstance> collection = this.minecraft.player.getActivePotionEffects();
      if (!collection.isEmpty()) {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         int j = 33;
         if (collection.size() > 5) {
            j = 132 / (collection.size() - 1);
         }

         Iterable<EffectInstance> iterable = collection.stream().filter(effectInstance -> effectInstance.shouldRender()).sorted().collect(java.util.stream.Collectors.toList());
         this.renderEffectBackground(matrixStack, i, j, iterable);
         this.renderEffectSprites(matrixStack, i, j, iterable);
         this.renderEffectText(matrixStack, i, j, iterable);
      }
   }

   private void renderEffectBackground(MatrixStack matrixStack, int p_238810_2_, int p_238810_3_, Iterable<EffectInstance> effects) {
      this.minecraft.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
      int i = this.guiTop;

      for(EffectInstance effectinstance : effects) {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.blit(matrixStack, p_238810_2_, i, 0, 166, 140, 32);
         i += p_238810_3_;
      }

   }

   private void renderEffectSprites(MatrixStack matrixStack, int p_238812_2_, int p_238812_3_, Iterable<EffectInstance> effects) {
      PotionSpriteUploader potionspriteuploader = this.minecraft.getPotionSpriteUploader();
      int i = this.guiTop;

      for(EffectInstance effectinstance : effects) {
         Effect effect = effectinstance.getPotion();
         TextureAtlasSprite textureatlassprite = potionspriteuploader.getSprite(effect);
         this.minecraft.getTextureManager().bindTexture(textureatlassprite.getAtlasTexture().getTextureLocation());
         blit(matrixStack, p_238812_2_ + 6, i + 7, this.getBlitOffset(), 18, 18, textureatlassprite);
         i += p_238812_3_;
      }

   }

   private void renderEffectText(MatrixStack matrixStack, int p_238813_2_, int p_238813_3_, Iterable<EffectInstance> effects) {
      int i = this.guiTop;

      for(EffectInstance effectinstance : effects) {
         effectinstance.renderInventoryEffect(this, matrixStack, p_238813_2_, i, this.getBlitOffset());
         if (!effectinstance.shouldRenderInvText()) {
            i += p_238813_3_;
            continue;
         }
         String s = I18n.format(effectinstance.getPotion().getName());
         if (effectinstance.getAmplifier() >= 1 && effectinstance.getAmplifier() <= 9) {
            s = s + ' ' + I18n.format("enchantment.level." + (effectinstance.getAmplifier() + 1));
         }

         this.font.drawStringWithShadow(matrixStack, s, (float)(p_238813_2_ + 10 + 18), (float)(i + 6), 16777215);
         String s1 = EffectUtils.getPotionDurationString(effectinstance, 1.0F);
         this.font.drawStringWithShadow(matrixStack, s1, (float)(p_238813_2_ + 10 + 18), (float)(i + 6 + 10), 8355711);
         i += p_238813_3_;
      }

   }
}
