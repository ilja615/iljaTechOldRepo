package com.mojang.realmsclient.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkinProcessor {
   private int[] pixels;
   private int width;
   private int height;

   @Nullable
   public BufferedImage process(BufferedImage p_225228_1_) {
      if (p_225228_1_ == null) {
         return null;
      } else {
         this.width = 64;
         this.height = 64;
         BufferedImage bufferedimage = new BufferedImage(this.width, this.height, 2);
         Graphics graphics = bufferedimage.getGraphics();
         graphics.drawImage(p_225228_1_, 0, 0, (ImageObserver)null);
         boolean flag = p_225228_1_.getHeight() == 32;
         if (flag) {
            graphics.setColor(new Color(0, 0, 0, 0));
            graphics.fillRect(0, 32, 64, 32);
            graphics.drawImage(bufferedimage, 24, 48, 20, 52, 4, 16, 8, 20, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 28, 48, 24, 52, 8, 16, 12, 20, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 20, 52, 16, 64, 8, 20, 12, 32, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 24, 52, 20, 64, 4, 20, 8, 32, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 28, 52, 24, 64, 0, 20, 4, 32, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 32, 52, 28, 64, 12, 20, 16, 32, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 40, 48, 36, 52, 44, 16, 48, 20, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 44, 48, 40, 52, 48, 16, 52, 20, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 36, 52, 32, 64, 48, 20, 52, 32, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 40, 52, 36, 64, 44, 20, 48, 32, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 44, 52, 40, 64, 40, 20, 44, 32, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 48, 52, 44, 64, 52, 20, 56, 32, (ImageObserver)null);
         }

         graphics.dispose();
         this.pixels = ((DataBufferInt)bufferedimage.getRaster().getDataBuffer()).getData();
         this.setNoAlpha(0, 0, 32, 16);
         if (flag) {
            this.doLegacyTransparencyHack(32, 0, 64, 32);
         }

         this.setNoAlpha(0, 16, 64, 32);
         this.setNoAlpha(16, 48, 48, 64);
         return bufferedimage;
      }
   }

   private void doLegacyTransparencyHack(int p_225227_1_, int p_225227_2_, int p_225227_3_, int p_225227_4_) {
      for(int i = p_225227_1_; i < p_225227_3_; ++i) {
         for(int j = p_225227_2_; j < p_225227_4_; ++j) {
            int k = this.pixels[i + j * this.width];
            if ((k >> 24 & 255) < 128) {
               return;
            }
         }
      }

      for(int l = p_225227_1_; l < p_225227_3_; ++l) {
         for(int i1 = p_225227_2_; i1 < p_225227_4_; ++i1) {
            this.pixels[l + i1 * this.width] &= 16777215;
         }
      }

   }

   private void setNoAlpha(int p_225229_1_, int p_225229_2_, int p_225229_3_, int p_225229_4_) {
      for(int i = p_225229_1_; i < p_225229_3_; ++i) {
         for(int j = p_225229_2_; j < p_225229_4_; ++j) {
            this.pixels[i + j * this.width] |= -16777216;
         }
      }

   }
}
