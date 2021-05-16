package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Widget extends AbstractGui implements IRenderable, IGuiEventListener {
   public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   protected int width;
   protected int height;
   public int x;
   public int y;
   private ITextComponent message;
   private boolean wasHovered;
   protected boolean isHovered;
   public boolean active = true;
   public boolean visible = true;
   protected float alpha = 1.0F;
   protected long nextNarration = Long.MAX_VALUE;
   private boolean focused;

   public Widget(int p_i232254_1_, int p_i232254_2_, int p_i232254_3_, int p_i232254_4_, ITextComponent p_i232254_5_) {
      this.x = p_i232254_1_;
      this.y = p_i232254_2_;
      this.width = p_i232254_3_;
      this.height = p_i232254_4_;
      this.message = p_i232254_5_;
   }

   public int getHeight() {
      return this.height;
   }

   protected int getYImage(boolean p_230989_1_) {
      int i = 1;
      if (!this.active) {
         i = 0;
      } else if (p_230989_1_) {
         i = 2;
      }

      return i;
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      if (this.visible) {
         this.isHovered = p_230430_2_ >= this.x && p_230430_3_ >= this.y && p_230430_2_ < this.x + this.width && p_230430_3_ < this.y + this.height;
         if (this.wasHovered != this.isHovered()) {
            if (this.isHovered()) {
               if (this.focused) {
                  this.queueNarration(200);
               } else {
                  this.queueNarration(750);
               }
            } else {
               this.nextNarration = Long.MAX_VALUE;
            }
         }

         if (this.visible) {
            this.renderButton(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
         }

         this.narrate();
         this.wasHovered = this.isHovered();
      }
   }

   protected void narrate() {
      if (this.active && this.isHovered() && Util.getMillis() > this.nextNarration) {
         String s = this.createNarrationMessage().getString();
         if (!s.isEmpty()) {
            NarratorChatListener.INSTANCE.sayNow(s);
            this.nextNarration = Long.MAX_VALUE;
         }
      }

   }

   protected IFormattableTextComponent createNarrationMessage() {
      return new TranslationTextComponent("gui.narrate.button", this.getMessage());
   }

   public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
      Minecraft minecraft = Minecraft.getInstance();
      FontRenderer fontrenderer = minecraft.font;
      minecraft.getTextureManager().bind(WIDGETS_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
      int i = this.getYImage(this.isHovered());
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.enableDepthTest();
      this.blit(p_230431_1_, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
      this.blit(p_230431_1_, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
      this.renderBg(p_230431_1_, minecraft, p_230431_2_, p_230431_3_);
      int j = getFGColor();
      drawCenteredString(p_230431_1_, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
   }

   protected void renderBg(MatrixStack p_230441_1_, Minecraft p_230441_2_, int p_230441_3_, int p_230441_4_) {
   }

   public void onClick(double p_230982_1_, double p_230982_3_) {
   }

   public void onRelease(double p_231000_1_, double p_231000_3_) {
   }

   protected void onDrag(double p_230983_1_, double p_230983_3_, double p_230983_5_, double p_230983_7_) {
   }

   public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
      if (this.active && this.visible) {
         if (this.isValidClickButton(p_231044_5_)) {
            boolean flag = this.clicked(p_231044_1_, p_231044_3_);
            if (flag) {
               this.playDownSound(Minecraft.getInstance().getSoundManager());
               this.onClick(p_231044_1_, p_231044_3_);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean mouseReleased(double p_231048_1_, double p_231048_3_, int p_231048_5_) {
      if (this.isValidClickButton(p_231048_5_)) {
         this.onRelease(p_231048_1_, p_231048_3_);
         return true;
      } else {
         return false;
      }
   }

   protected boolean isValidClickButton(int p_230987_1_) {
      return p_230987_1_ == 0;
   }

   public boolean mouseDragged(double p_231045_1_, double p_231045_3_, int p_231045_5_, double p_231045_6_, double p_231045_8_) {
      if (this.isValidClickButton(p_231045_5_)) {
         this.onDrag(p_231045_1_, p_231045_3_, p_231045_6_, p_231045_8_);
         return true;
      } else {
         return false;
      }
   }

   protected boolean clicked(double p_230992_1_, double p_230992_3_) {
      return this.active && this.visible && p_230992_1_ >= (double)this.x && p_230992_3_ >= (double)this.y && p_230992_1_ < (double)(this.x + this.width) && p_230992_3_ < (double)(this.y + this.height);
   }

   public boolean isHovered() {
      return this.isHovered || this.focused;
   }

   public boolean changeFocus(boolean p_231049_1_) {
      if (this.active && this.visible) {
         this.focused = !this.focused;
         this.onFocusedChanged(this.focused);
         return this.focused;
      } else {
         return false;
      }
   }

   protected void onFocusedChanged(boolean p_230995_1_) {
   }

   public boolean isMouseOver(double p_231047_1_, double p_231047_3_) {
      return this.active && this.visible && p_231047_1_ >= (double)this.x && p_231047_3_ >= (double)this.y && p_231047_1_ < (double)(this.x + this.width) && p_231047_3_ < (double)(this.y + this.height);
   }

   public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_) {
   }

   public void playDownSound(SoundHandler p_230988_1_) {
      p_230988_1_.play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int p_230991_1_) {
      this.width = p_230991_1_;
   }

   public void setHeight(int value) {
      this.height = value;
   }

   public void setAlpha(float p_230986_1_) {
      this.alpha = p_230986_1_;
   }

   public void setMessage(ITextComponent p_238482_1_) {
      if (!Objects.equals(p_238482_1_.getString(), this.message.getString())) {
         this.queueNarration(250);
      }

      this.message = p_238482_1_;
   }

   public void queueNarration(int p_230994_1_) {
      this.nextNarration = Util.getMillis() + (long)p_230994_1_;
   }

   public ITextComponent getMessage() {
      return this.message;
   }

   public boolean isFocused() {
      return this.focused;
   }

   protected void setFocused(boolean p_230996_1_) {
      this.focused = p_230996_1_;
   }

   public static final int UNSET_FG_COLOR = -1;
   protected int packedFGColor = UNSET_FG_COLOR;
   public int getFGColor() {
      if (packedFGColor != UNSET_FG_COLOR) return packedFGColor;
      return this.active ? 16777215 : 10526880; // White : Light Grey
   }
   public void setFGColor(int color) {
      this.packedFGColor = color;
   }
   public void clearFGColor() {
      this.packedFGColor = UNSET_FG_COLOR;
   }
}
