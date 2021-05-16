package net.minecraft.client.gui.fonts;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FontTexture extends Texture {
   private final ResourceLocation name;
   private final RenderType normalType;
   private final RenderType seeThroughType;
   private final boolean colored;
   private final FontTexture.Entry root;

   public FontTexture(ResourceLocation p_i49770_1_, boolean p_i49770_2_) {
      this.name = p_i49770_1_;
      this.colored = p_i49770_2_;
      this.root = new FontTexture.Entry(0, 0, 256, 256);
      TextureUtil.prepareImage(p_i49770_2_ ? NativeImage.PixelFormatGLCode.RGBA : NativeImage.PixelFormatGLCode.INTENSITY, this.getId(), 256, 256);
      this.normalType = RenderType.text(p_i49770_1_);
      this.seeThroughType = RenderType.textSeeThrough(p_i49770_1_);
   }

   public void load(IResourceManager p_195413_1_) {
   }

   public void close() {
      this.releaseId();
   }

   @Nullable
   public TexturedGlyph add(IGlyphInfo p_211131_1_) {
      if (p_211131_1_.isColored() != this.colored) {
         return null;
      } else {
         FontTexture.Entry fonttexture$entry = this.root.insert(p_211131_1_);
         if (fonttexture$entry != null) {
            this.bind();
            p_211131_1_.upload(fonttexture$entry.x, fonttexture$entry.y);
            float f = 256.0F;
            float f1 = 256.0F;
            float f2 = 0.01F;
            return new TexturedGlyph(this.normalType, this.seeThroughType, ((float)fonttexture$entry.x + 0.01F) / 256.0F, ((float)fonttexture$entry.x - 0.01F + (float)p_211131_1_.getPixelWidth()) / 256.0F, ((float)fonttexture$entry.y + 0.01F) / 256.0F, ((float)fonttexture$entry.y - 0.01F + (float)p_211131_1_.getPixelHeight()) / 256.0F, p_211131_1_.getLeft(), p_211131_1_.getRight(), p_211131_1_.getUp(), p_211131_1_.getDown());
         } else {
            return null;
         }
      }
   }

   public ResourceLocation getName() {
      return this.name;
   }

   @OnlyIn(Dist.CLIENT)
   static class Entry {
      private final int x;
      private final int y;
      private final int width;
      private final int height;
      private FontTexture.Entry left;
      private FontTexture.Entry right;
      private boolean occupied;

      private Entry(int p_i49711_1_, int p_i49711_2_, int p_i49711_3_, int p_i49711_4_) {
         this.x = p_i49711_1_;
         this.y = p_i49711_2_;
         this.width = p_i49711_3_;
         this.height = p_i49711_4_;
      }

      @Nullable
      FontTexture.Entry insert(IGlyphInfo p_211224_1_) {
         if (this.left != null && this.right != null) {
            FontTexture.Entry fonttexture$entry = this.left.insert(p_211224_1_);
            if (fonttexture$entry == null) {
               fonttexture$entry = this.right.insert(p_211224_1_);
            }

            return fonttexture$entry;
         } else if (this.occupied) {
            return null;
         } else {
            int i = p_211224_1_.getPixelWidth();
            int j = p_211224_1_.getPixelHeight();
            if (i <= this.width && j <= this.height) {
               if (i == this.width && j == this.height) {
                  this.occupied = true;
                  return this;
               } else {
                  int k = this.width - i;
                  int l = this.height - j;
                  if (k > l) {
                     this.left = new FontTexture.Entry(this.x, this.y, i, this.height);
                     this.right = new FontTexture.Entry(this.x + i + 1, this.y, this.width - i - 1, this.height);
                  } else {
                     this.left = new FontTexture.Entry(this.x, this.y, this.width, j);
                     this.right = new FontTexture.Entry(this.x, this.y + j + 1, this.width, this.height - j - 1);
                  }

                  return this.left.insert(p_211224_1_);
               }
            } else {
               return null;
            }
         }
      }
   }
}
