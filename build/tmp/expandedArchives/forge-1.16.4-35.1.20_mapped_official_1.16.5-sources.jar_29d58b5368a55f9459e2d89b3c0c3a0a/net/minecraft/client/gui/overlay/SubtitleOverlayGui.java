package net.minecraft.client.gui.overlay;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventListener;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SubtitleOverlayGui extends AbstractGui implements ISoundEventListener {
   private final Minecraft minecraft;
   private final List<SubtitleOverlayGui.Subtitle> subtitles = Lists.newArrayList();
   private boolean isListening;

   public SubtitleOverlayGui(Minecraft p_i46603_1_) {
      this.minecraft = p_i46603_1_;
   }

   public void render(MatrixStack p_195620_1_) {
      if (!this.isListening && this.minecraft.options.showSubtitles) {
         this.minecraft.getSoundManager().addListener(this);
         this.isListening = true;
      } else if (this.isListening && !this.minecraft.options.showSubtitles) {
         this.minecraft.getSoundManager().removeListener(this);
         this.isListening = false;
      }

      if (this.isListening && !this.subtitles.isEmpty()) {
         RenderSystem.pushMatrix();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         Vector3d vector3d = new Vector3d(this.minecraft.player.getX(), this.minecraft.player.getEyeY(), this.minecraft.player.getZ());
         Vector3d vector3d1 = (new Vector3d(0.0D, 0.0D, -1.0D)).xRot(-this.minecraft.player.xRot * ((float)Math.PI / 180F)).yRot(-this.minecraft.player.yRot * ((float)Math.PI / 180F));
         Vector3d vector3d2 = (new Vector3d(0.0D, 1.0D, 0.0D)).xRot(-this.minecraft.player.xRot * ((float)Math.PI / 180F)).yRot(-this.minecraft.player.yRot * ((float)Math.PI / 180F));
         Vector3d vector3d3 = vector3d1.cross(vector3d2);
         int i = 0;
         int j = 0;
         Iterator<SubtitleOverlayGui.Subtitle> iterator = this.subtitles.iterator();

         while(iterator.hasNext()) {
            SubtitleOverlayGui.Subtitle subtitleoverlaygui$subtitle = iterator.next();
            if (subtitleoverlaygui$subtitle.getTime() + 3000L <= Util.getMillis()) {
               iterator.remove();
            } else {
               j = Math.max(j, this.minecraft.font.width(subtitleoverlaygui$subtitle.getText()));
            }
         }

         j = j + this.minecraft.font.width("<") + this.minecraft.font.width(" ") + this.minecraft.font.width(">") + this.minecraft.font.width(" ");

         for(SubtitleOverlayGui.Subtitle subtitleoverlaygui$subtitle1 : this.subtitles) {
            int k = 255;
            ITextComponent itextcomponent = subtitleoverlaygui$subtitle1.getText();
            Vector3d vector3d4 = subtitleoverlaygui$subtitle1.getLocation().subtract(vector3d).normalize();
            double d0 = -vector3d3.dot(vector3d4);
            double d1 = -vector3d1.dot(vector3d4);
            boolean flag = d1 > 0.5D;
            int l = j / 2;
            int i1 = 9;
            int j1 = i1 / 2;
            float f = 1.0F;
            int k1 = this.minecraft.font.width(itextcomponent);
            int l1 = MathHelper.floor(MathHelper.clampedLerp(255.0D, 75.0D, (double)((float)(Util.getMillis() - subtitleoverlaygui$subtitle1.getTime()) / 3000.0F)));
            int i2 = l1 << 16 | l1 << 8 | l1;
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)this.minecraft.getWindow().getGuiScaledWidth() - (float)l * 1.0F - 2.0F, (float)(this.minecraft.getWindow().getGuiScaledHeight() - 30) - (float)(i * (i1 + 1)) * 1.0F, 0.0F);
            RenderSystem.scalef(1.0F, 1.0F, 1.0F);
            fill(p_195620_1_, -l - 1, -j1 - 1, l + 1, j1 + 1, this.minecraft.options.getBackgroundColor(0.8F));
            RenderSystem.enableBlend();
            if (!flag) {
               if (d0 > 0.0D) {
                  this.minecraft.font.draw(p_195620_1_, ">", (float)(l - this.minecraft.font.width(">")), (float)(-j1), i2 + -16777216);
               } else if (d0 < 0.0D) {
                  this.minecraft.font.draw(p_195620_1_, "<", (float)(-l), (float)(-j1), i2 + -16777216);
               }
            }

            this.minecraft.font.draw(p_195620_1_, itextcomponent, (float)(-k1 / 2), (float)(-j1), i2 + -16777216);
            RenderSystem.popMatrix();
            ++i;
         }

         RenderSystem.disableBlend();
         RenderSystem.popMatrix();
      }
   }

   public void onPlaySound(ISound p_184067_1_, SoundEventAccessor p_184067_2_) {
      if (p_184067_2_.getSubtitle() != null) {
         ITextComponent itextcomponent = p_184067_2_.getSubtitle();
         if (!this.subtitles.isEmpty()) {
            for(SubtitleOverlayGui.Subtitle subtitleoverlaygui$subtitle : this.subtitles) {
               if (subtitleoverlaygui$subtitle.getText().equals(itextcomponent)) {
                  subtitleoverlaygui$subtitle.refresh(new Vector3d(p_184067_1_.getX(), p_184067_1_.getY(), p_184067_1_.getZ()));
                  return;
               }
            }
         }

         this.subtitles.add(new SubtitleOverlayGui.Subtitle(itextcomponent, new Vector3d(p_184067_1_.getX(), p_184067_1_.getY(), p_184067_1_.getZ())));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public class Subtitle {
      private final ITextComponent text;
      private long time;
      private Vector3d location;

      public Subtitle(ITextComponent p_i232263_2_, Vector3d p_i232263_3_) {
         this.text = p_i232263_2_;
         this.location = p_i232263_3_;
         this.time = Util.getMillis();
      }

      public ITextComponent getText() {
         return this.text;
      }

      public long getTime() {
         return this.time;
      }

      public Vector3d getLocation() {
         return this.location;
      }

      public void refresh(Vector3d p_186823_1_) {
         this.location = p_186823_1_;
         this.time = Util.getMillis();
      }
   }
}
