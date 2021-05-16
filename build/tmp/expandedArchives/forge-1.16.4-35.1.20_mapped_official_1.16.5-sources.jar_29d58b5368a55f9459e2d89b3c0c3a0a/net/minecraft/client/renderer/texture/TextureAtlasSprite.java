package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SpriteAwareVertexBuilder;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextureAtlasSprite implements AutoCloseable, net.minecraftforge.client.extensions.IForgeTextureAtlasSprite {
   private final AtlasTexture atlas;
   private final TextureAtlasSprite.Info info;
   private final AnimationMetadataSection metadata;
   protected final NativeImage[] mainImage;
   private final int[] framesX;
   private final int[] framesY;
   @Nullable
   private final TextureAtlasSprite.InterpolationData interpolationData;
   private final int x;
   private final int y;
   private final float u0;
   private final float u1;
   private final float v0;
   private final float v1;
   private int frame;
   private int subFrame;

   protected TextureAtlasSprite(AtlasTexture p_i226049_1_, TextureAtlasSprite.Info p_i226049_2_, int p_i226049_3_, int p_i226049_4_, int p_i226049_5_, int p_i226049_6_, int p_i226049_7_, NativeImage p_i226049_8_) {
      this.atlas = p_i226049_1_;
      AnimationMetadataSection animationmetadatasection = p_i226049_2_.metadata;
      int i = p_i226049_2_.width;
      int j = p_i226049_2_.height;
      this.x = p_i226049_6_;
      this.y = p_i226049_7_;
      this.u0 = (float)p_i226049_6_ / (float)p_i226049_4_;
      this.u1 = (float)(p_i226049_6_ + i) / (float)p_i226049_4_;
      this.v0 = (float)p_i226049_7_ / (float)p_i226049_5_;
      this.v1 = (float)(p_i226049_7_ + j) / (float)p_i226049_5_;
      int k = p_i226049_8_.getWidth() / animationmetadatasection.getFrameWidth(i);
      int l = p_i226049_8_.getHeight() / animationmetadatasection.getFrameHeight(j);
      if (animationmetadatasection.getFrameCount() > 0) {
         int i1 = animationmetadatasection.getUniqueFrameIndices().stream().max(Integer::compareTo).get() + 1;
         this.framesX = new int[i1];
         this.framesY = new int[i1];
         Arrays.fill(this.framesX, -1);
         Arrays.fill(this.framesY, -1);

         for(int j1 : animationmetadatasection.getUniqueFrameIndices()) {
            if (j1 >= k * l) {
               throw new RuntimeException("invalid frameindex " + j1);
            }

            int k1 = j1 / k;
            int l1 = j1 % k;
            this.framesX[j1] = l1;
            this.framesY[j1] = k1;
         }
      } else {
         List<AnimationFrame> list = Lists.newArrayList();
         int i2 = k * l;
         this.framesX = new int[i2];
         this.framesY = new int[i2];

         for(int j2 = 0; j2 < l; ++j2) {
            for(int k2 = 0; k2 < k; ++k2) {
               int l2 = j2 * k + k2;
               this.framesX[l2] = k2;
               this.framesY[l2] = j2;
               list.add(new AnimationFrame(l2, -1));
            }
         }

         animationmetadatasection = new AnimationMetadataSection(list, i, j, animationmetadatasection.getDefaultFrameTime(), animationmetadatasection.isInterpolatedFrames());
      }

      this.info = new TextureAtlasSprite.Info(p_i226049_2_.name, i, j, animationmetadatasection);
      this.metadata = animationmetadatasection;

      try {
         try {
            this.mainImage = MipmapGenerator.generateMipLevels(p_i226049_8_, p_i226049_3_);
         } catch (Throwable throwable) {
            CrashReport crashreport1 = CrashReport.forThrowable(throwable, "Generating mipmaps for frame");
            CrashReportCategory crashreportcategory1 = crashreport1.addCategory("Frame being iterated");
            crashreportcategory1.setDetail("First frame", () -> {
               StringBuilder stringbuilder = new StringBuilder();
               if (stringbuilder.length() > 0) {
                  stringbuilder.append(", ");
               }

               stringbuilder.append(p_i226049_8_.getWidth()).append("x").append(p_i226049_8_.getHeight());
               return stringbuilder.toString();
            });
            throw new ReportedException(crashreport1);
         }
      } catch (Throwable throwable1) {
         CrashReport crashreport = CrashReport.forThrowable(throwable1, "Applying mipmap");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Sprite being mipmapped");
         crashreportcategory.setDetail("Sprite name", () -> {
            return this.getName().toString();
         });
         crashreportcategory.setDetail("Sprite size", () -> {
            return this.getWidth() + " x " + this.getHeight();
         });
         crashreportcategory.setDetail("Sprite frames", () -> {
            return this.getFrameCount() + " frames";
         });
         crashreportcategory.setDetail("Mipmap levels", p_i226049_3_);
         throw new ReportedException(crashreport);
      }

      if (animationmetadatasection.isInterpolatedFrames()) {
         this.interpolationData = new TextureAtlasSprite.InterpolationData(p_i226049_2_, p_i226049_3_);
      } else {
         this.interpolationData = null;
      }

   }

   private void upload(int p_195659_1_) {
      int i = this.framesX[p_195659_1_] * this.info.width;
      int j = this.framesY[p_195659_1_] * this.info.height;
      this.upload(i, j, this.mainImage);
   }

   private void upload(int p_195667_1_, int p_195667_2_, NativeImage[] p_195667_3_) {
      for(int i = 0; i < this.mainImage.length; ++i) {
         if ((this.info.width >> i <= 0) || (this.info.height >> i <= 0)) break;
         p_195667_3_[i].upload(i, this.x >> i, this.y >> i, p_195667_1_ >> i, p_195667_2_ >> i, this.info.width >> i, this.info.height >> i, this.mainImage.length > 1, false);
      }

   }

   public int getWidth() {
      return this.info.width;
   }

   public int getHeight() {
      return this.info.height;
   }

   public float getU0() {
      return this.u0;
   }

   public float getU1() {
      return this.u1;
   }

   public float getU(double p_94214_1_) {
      float f = this.u1 - this.u0;
      return this.u0 + f * (float)p_94214_1_ / 16.0F;
   }

   public float getV0() {
      return this.v0;
   }

   public float getV1() {
      return this.v1;
   }

   public float getV(double p_94207_1_) {
      float f = this.v1 - this.v0;
      return this.v0 + f * (float)p_94207_1_ / 16.0F;
   }

   public ResourceLocation getName() {
      return this.info.name;
   }

   public AtlasTexture atlas() {
      return this.atlas;
   }

   public int getFrameCount() {
      return this.framesX.length;
   }

   public void close() {
      for(NativeImage nativeimage : this.mainImage) {
         if (nativeimage != null) {
            nativeimage.close();
         }
      }

      if (this.interpolationData != null) {
         this.interpolationData.close();
      }

   }

   public String toString() {
      int i = this.framesX.length;
      return "TextureAtlasSprite{name='" + this.info.name + '\'' + ", frameCount=" + i + ", x=" + this.x + ", y=" + this.y + ", height=" + this.info.height + ", width=" + this.info.width + ", u0=" + this.u0 + ", u1=" + this.u1 + ", v0=" + this.v0 + ", v1=" + this.v1 + '}';
   }

   public boolean isTransparent(int p_195662_1_, int p_195662_2_, int p_195662_3_) {
      return (this.mainImage[0].getPixelRGBA(p_195662_2_ + this.framesX[p_195662_1_] * this.info.width, p_195662_3_ + this.framesY[p_195662_1_] * this.info.height) >> 24 & 255) == 0;
   }

   public void uploadFirstFrame() {
      this.upload(0);
   }

   private float atlasSize() {
      float f = (float)this.info.width / (this.u1 - this.u0);
      float f1 = (float)this.info.height / (this.v1 - this.v0);
      return Math.max(f1, f);
   }

   public float uvShrinkRatio() {
      return 4.0F / this.atlasSize();
   }

   public void cycleFrames() {
      ++this.subFrame;
      if (this.subFrame >= this.metadata.getFrameTime(this.frame)) {
         int i = this.metadata.getFrameIndex(this.frame);
         int j = this.metadata.getFrameCount() == 0 ? this.getFrameCount() : this.metadata.getFrameCount();
         this.frame = (this.frame + 1) % j;
         this.subFrame = 0;
         int k = this.metadata.getFrameIndex(this.frame);
         if (i != k && k >= 0 && k < this.getFrameCount()) {
            this.upload(k);
         }
      } else if (this.interpolationData != null) {
         if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
               TextureAtlasSprite.this.interpolationData.uploadInterpolatedFrame();
            });
         } else {
            this.interpolationData.uploadInterpolatedFrame();
         }
      }

   }

   public boolean isAnimation() {
      return this.metadata.getFrameCount() > 1;
   }

   public IVertexBuilder wrap(IVertexBuilder p_229230_1_) {
      return new SpriteAwareVertexBuilder(p_229230_1_, this);
   }

   @OnlyIn(Dist.CLIENT)
   public static final class Info {
      private final ResourceLocation name;
      private final int width;
      private final int height;
      private final AnimationMetadataSection metadata;

      public Info(ResourceLocation p_i226050_1_, int p_i226050_2_, int p_i226050_3_, AnimationMetadataSection p_i226050_4_) {
         this.name = p_i226050_1_;
         this.width = p_i226050_2_;
         this.height = p_i226050_3_;
         this.metadata = p_i226050_4_;
      }

      public ResourceLocation name() {
         return this.name;
      }

      public int width() {
         return this.width;
      }

      public int height() {
         return this.height;
      }
   }

   @OnlyIn(Dist.CLIENT)
   final class InterpolationData implements AutoCloseable {
      private final NativeImage[] activeFrame;

      private InterpolationData(TextureAtlasSprite.Info p_i226051_2_, int p_i226051_3_) {
         this.activeFrame = new NativeImage[p_i226051_3_ + 1];

         for(int i = 0; i < this.activeFrame.length; ++i) {
            int j = p_i226051_2_.width >> i;
            int k = p_i226051_2_.height >> i;
            if (this.activeFrame[i] == null) {
               this.activeFrame[i] = new NativeImage(j, k, false);
            }
         }

      }

      private void uploadInterpolatedFrame() {
         double d0 = 1.0D - (double)TextureAtlasSprite.this.subFrame / (double)TextureAtlasSprite.this.metadata.getFrameTime(TextureAtlasSprite.this.frame);
         int i = TextureAtlasSprite.this.metadata.getFrameIndex(TextureAtlasSprite.this.frame);
         int j = TextureAtlasSprite.this.metadata.getFrameCount() == 0 ? TextureAtlasSprite.this.getFrameCount() : TextureAtlasSprite.this.metadata.getFrameCount();
         int k = TextureAtlasSprite.this.metadata.getFrameIndex((TextureAtlasSprite.this.frame + 1) % j);
         if (i != k && k >= 0 && k < TextureAtlasSprite.this.getFrameCount()) {
            for(int l = 0; l < this.activeFrame.length; ++l) {
               int i1 = TextureAtlasSprite.this.info.width >> l;
               int j1 = TextureAtlasSprite.this.info.height >> l;

               for(int k1 = 0; k1 < j1; ++k1) {
                  for(int l1 = 0; l1 < i1; ++l1) {
                     int i2 = this.getPixel(i, l, l1, k1);
                     int j2 = this.getPixel(k, l, l1, k1);
                     int k2 = this.mix(d0, i2 >> 16 & 255, j2 >> 16 & 255);
                     int l2 = this.mix(d0, i2 >> 8 & 255, j2 >> 8 & 255);
                     int i3 = this.mix(d0, i2 & 255, j2 & 255);
                     this.activeFrame[l].setPixelRGBA(l1, k1, i2 & -16777216 | k2 << 16 | l2 << 8 | i3);
                  }
               }
            }

            TextureAtlasSprite.this.upload(0, 0, this.activeFrame);
         }

      }

      private int getPixel(int p_229259_1_, int p_229259_2_, int p_229259_3_, int p_229259_4_) {
         return TextureAtlasSprite.this.mainImage[p_229259_2_].getPixelRGBA(p_229259_3_ + (TextureAtlasSprite.this.framesX[p_229259_1_] * TextureAtlasSprite.this.info.width >> p_229259_2_), p_229259_4_ + (TextureAtlasSprite.this.framesY[p_229259_1_] * TextureAtlasSprite.this.info.height >> p_229259_2_));
      }

      private int mix(double p_229258_1_, int p_229258_3_, int p_229258_4_) {
         return (int)(p_229258_1_ * (double)p_229258_3_ + (1.0D - p_229258_1_) * (double)p_229258_4_);
      }

      public void close() {
         for(NativeImage nativeimage : this.activeFrame) {
            if (nativeimage != null) {
               nativeimage.close();
            }
         }

      }
   }

   // Forge Start
   public int getPixelRGBA(int frameIndex, int x, int y) {
      return this.mainImage[0].getPixelRGBA(x + this.framesX[frameIndex] * getWidth(), y + this.framesY[frameIndex] * getHeight());
   }
}
