package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class DownloadingTexture extends SimpleTexture {
   private static final Logger LOGGER = LogManager.getLogger();
   @Nullable
   private final File file;
   private final String urlString;
   private final boolean processLegacySkin;
   @Nullable
   private final Runnable onDownloaded;
   @Nullable
   private CompletableFuture<?> future;
   private boolean uploaded;

   public DownloadingTexture(@Nullable File p_i226043_1_, String p_i226043_2_, ResourceLocation p_i226043_3_, boolean p_i226043_4_, @Nullable Runnable p_i226043_5_) {
      super(p_i226043_3_);
      this.file = p_i226043_1_;
      this.urlString = p_i226043_2_;
      this.processLegacySkin = p_i226043_4_;
      this.onDownloaded = p_i226043_5_;
   }

   private void loadCallback(NativeImage p_195417_1_) {
      if (this.onDownloaded != null) {
         this.onDownloaded.run();
      }

      Minecraft.getInstance().execute(() -> {
         this.uploaded = true;
         if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
               this.upload(p_195417_1_);
            });
         } else {
            this.upload(p_195417_1_);
         }

      });
   }

   private void upload(NativeImage p_229160_1_) {
      TextureUtil.prepareImage(this.getId(), p_229160_1_.getWidth(), p_229160_1_.getHeight());
      p_229160_1_.upload(0, 0, 0, true);
   }

   public void load(IResourceManager p_195413_1_) throws IOException {
      Minecraft.getInstance().execute(() -> {
         if (!this.uploaded) {
            try {
               super.load(p_195413_1_);
            } catch (IOException ioexception) {
               LOGGER.warn("Failed to load texture: {}", this.location, ioexception);
            }

            this.uploaded = true;
         }

      });
      if (this.future == null) {
         NativeImage nativeimage;
         if (this.file != null && this.file.isFile()) {
            LOGGER.debug("Loading http texture from local cache ({})", (Object)this.file);
            FileInputStream fileinputstream = new FileInputStream(this.file);
            nativeimage = this.load(fileinputstream);
         } else {
            nativeimage = null;
         }

         if (nativeimage != null) {
            this.loadCallback(nativeimage);
         } else {
            this.future = CompletableFuture.runAsync(() -> {
               HttpURLConnection httpurlconnection = null;
               LOGGER.debug("Downloading http texture from {} to {}", this.urlString, this.file);

               try {
                  httpurlconnection = (HttpURLConnection)(new URL(this.urlString)).openConnection(Minecraft.getInstance().getProxy());
                  httpurlconnection.setDoInput(true);
                  httpurlconnection.setDoOutput(false);
                  httpurlconnection.connect();
                  if (httpurlconnection.getResponseCode() / 100 == 2) {
                     InputStream inputstream;
                     if (this.file != null) {
                        FileUtils.copyInputStreamToFile(httpurlconnection.getInputStream(), this.file);
                        inputstream = new FileInputStream(this.file);
                     } else {
                        inputstream = httpurlconnection.getInputStream();
                     }

                     Minecraft.getInstance().execute(() -> {
                        NativeImage nativeimage1 = this.load(inputstream);
                        if (nativeimage1 != null) {
                           this.loadCallback(nativeimage1);
                        }

                     });
                     return;
                  }
               } catch (Exception exception) {
                  LOGGER.error("Couldn't download http texture", (Throwable)exception);
                  return;
               } finally {
                  if (httpurlconnection != null) {
                     httpurlconnection.disconnect();
                  }

               }

            }, Util.backgroundExecutor());
         }
      }
   }

   @Nullable
   private NativeImage load(InputStream p_229159_1_) {
      NativeImage nativeimage = null;

      try {
         nativeimage = NativeImage.read(p_229159_1_);
         if (this.processLegacySkin) {
            nativeimage = processLegacySkin(nativeimage);
         }
      } catch (IOException ioexception) {
         LOGGER.warn("Error while loading the skin texture", (Throwable)ioexception);
      }

      return nativeimage;
   }

   private static NativeImage processLegacySkin(NativeImage p_229163_0_) {
      boolean flag = p_229163_0_.getHeight() == 32;
      if (flag) {
         NativeImage nativeimage = new NativeImage(64, 64, true);
         nativeimage.copyFrom(p_229163_0_);
         p_229163_0_.close();
         p_229163_0_ = nativeimage;
         nativeimage.fillRect(0, 32, 64, 32, 0);
         nativeimage.copyRect(4, 16, 16, 32, 4, 4, true, false);
         nativeimage.copyRect(8, 16, 16, 32, 4, 4, true, false);
         nativeimage.copyRect(0, 20, 24, 32, 4, 12, true, false);
         nativeimage.copyRect(4, 20, 16, 32, 4, 12, true, false);
         nativeimage.copyRect(8, 20, 8, 32, 4, 12, true, false);
         nativeimage.copyRect(12, 20, 16, 32, 4, 12, true, false);
         nativeimage.copyRect(44, 16, -8, 32, 4, 4, true, false);
         nativeimage.copyRect(48, 16, -8, 32, 4, 4, true, false);
         nativeimage.copyRect(40, 20, 0, 32, 4, 12, true, false);
         nativeimage.copyRect(44, 20, -8, 32, 4, 12, true, false);
         nativeimage.copyRect(48, 20, -16, 32, 4, 12, true, false);
         nativeimage.copyRect(52, 20, -8, 32, 4, 12, true, false);
      }

      setNoAlpha(p_229163_0_, 0, 0, 32, 16);
      if (flag) {
         doNotchTransparencyHack(p_229163_0_, 32, 0, 64, 32);
      }

      setNoAlpha(p_229163_0_, 0, 16, 64, 32);
      setNoAlpha(p_229163_0_, 16, 48, 48, 64);
      return p_229163_0_;
   }

   private static void doNotchTransparencyHack(NativeImage p_229158_0_, int p_229158_1_, int p_229158_2_, int p_229158_3_, int p_229158_4_) {
      for(int i = p_229158_1_; i < p_229158_3_; ++i) {
         for(int j = p_229158_2_; j < p_229158_4_; ++j) {
            int k = p_229158_0_.getPixelRGBA(i, j);
            if ((k >> 24 & 255) < 128) {
               return;
            }
         }
      }

      for(int l = p_229158_1_; l < p_229158_3_; ++l) {
         for(int i1 = p_229158_2_; i1 < p_229158_4_; ++i1) {
            p_229158_0_.setPixelRGBA(l, i1, p_229158_0_.getPixelRGBA(l, i1) & 16777215);
         }
      }

   }

   private static void setNoAlpha(NativeImage p_229161_0_, int p_229161_1_, int p_229161_2_, int p_229161_3_, int p_229161_4_) {
      for(int i = p_229161_1_; i < p_229161_3_; ++i) {
         for(int j = p_229161_2_; j < p_229161_4_; ++j) {
            p_229161_0_.setPixelRGBA(i, j, p_229161_0_.getPixelRGBA(i, j) | -16777216);
         }
      }

   }
}
