package net.minecraft.client.gui.fonts.providers;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.fonts.IGlyphInfo;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class UnicodeTextureGlyphProvider implements IGlyphProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final IResourceManager resourceManager;
   private final byte[] sizes;
   private final String texturePattern;
   private final Map<ResourceLocation, NativeImage> textures = Maps.newHashMap();

   public UnicodeTextureGlyphProvider(IResourceManager p_i49737_1_, byte[] p_i49737_2_, String p_i49737_3_) {
      this.resourceManager = p_i49737_1_;
      this.sizes = p_i49737_2_;
      this.texturePattern = p_i49737_3_;

      for(int i = 0; i < 256; ++i) {
         int j = i * 256;
         ResourceLocation resourcelocation = this.getSheetLocation(j);

         try (
            IResource iresource = this.resourceManager.getResource(resourcelocation);
            NativeImage nativeimage = NativeImage.read(NativeImage.PixelFormat.RGBA, iresource.getInputStream());
         ) {
            if (nativeimage.getWidth() == 256 && nativeimage.getHeight() == 256) {
               for(int k = 0; k < 256; ++k) {
                  byte b0 = p_i49737_2_[j + k];
                  if (b0 != 0 && getLeft(b0) > getRight(b0)) {
                     p_i49737_2_[j + k] = 0;
                  }
               }
               continue;
            }
         } catch (IOException ioexception) {
         }

         Arrays.fill(p_i49737_2_, j, j + 256, (byte)0);
      }

   }

   public void close() {
      this.textures.values().forEach(NativeImage::close);
   }

   private ResourceLocation getSheetLocation(int p_238591_1_) {
      ResourceLocation resourcelocation = new ResourceLocation(String.format(this.texturePattern, String.format("%02x", p_238591_1_ / 256)));
      return new ResourceLocation(resourcelocation.getNamespace(), "textures/" + resourcelocation.getPath());
   }

   @Nullable
   public IGlyphInfo getGlyph(int p_212248_1_) {
      if (p_212248_1_ >= 0 && p_212248_1_ <= 65535) {
         byte b0 = this.sizes[p_212248_1_];
         if (b0 != 0) {
            NativeImage nativeimage = this.textures.computeIfAbsent(this.getSheetLocation(p_212248_1_), this::loadTexture);
            if (nativeimage != null) {
               int i = getLeft(b0);
               return new UnicodeTextureGlyphProvider.GlpyhInfo(p_212248_1_ % 16 * 16 + i, (p_212248_1_ & 255) / 16 * 16, getRight(b0) - i, 16, nativeimage);
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public IntSet getSupportedGlyphs() {
      IntSet intset = new IntOpenHashSet();

      for(int i = 0; i < 65535; ++i) {
         if (this.sizes[i] != 0) {
            intset.add(i);
         }
      }

      return intset;
   }

   @Nullable
   private NativeImage loadTexture(ResourceLocation p_211255_1_) {
      try (IResource iresource = this.resourceManager.getResource(p_211255_1_)) {
         return NativeImage.read(NativeImage.PixelFormat.RGBA, iresource.getInputStream());
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't load texture {}", p_211255_1_, ioexception);
         return null;
      }
   }

   private static int getLeft(byte p_212453_0_) {
      return p_212453_0_ >> 4 & 15;
   }

   private static int getRight(byte p_212454_0_) {
      return (p_212454_0_ & 15) + 1;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IGlyphProviderFactory {
      private final ResourceLocation metadata;
      private final String texturePattern;

      public Factory(ResourceLocation p_i49760_1_, String p_i49760_2_) {
         this.metadata = p_i49760_1_;
         this.texturePattern = p_i49760_2_;
      }

      public static IGlyphProviderFactory fromJson(JsonObject p_211629_0_) {
         return new UnicodeTextureGlyphProvider.Factory(new ResourceLocation(JSONUtils.getAsString(p_211629_0_, "sizes")), JSONUtils.getAsString(p_211629_0_, "template"));
      }

      @Nullable
      public IGlyphProvider create(IResourceManager p_211246_1_) {
         try (IResource iresource = Minecraft.getInstance().getResourceManager().getResource(this.metadata)) {
            byte[] abyte = new byte[65536];
            iresource.getInputStream().read(abyte);
            return new UnicodeTextureGlyphProvider(p_211246_1_, abyte, this.texturePattern);
         } catch (IOException ioexception) {
            UnicodeTextureGlyphProvider.LOGGER.error("Cannot load {}, unicode glyphs will not render correctly", (Object)this.metadata);
            return null;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class GlpyhInfo implements IGlyphInfo {
      private final int width;
      private final int height;
      private final int sourceX;
      private final int sourceY;
      private final NativeImage source;

      private GlpyhInfo(int p_i49758_1_, int p_i49758_2_, int p_i49758_3_, int p_i49758_4_, NativeImage p_i49758_5_) {
         this.width = p_i49758_3_;
         this.height = p_i49758_4_;
         this.sourceX = p_i49758_1_;
         this.sourceY = p_i49758_2_;
         this.source = p_i49758_5_;
      }

      public float getOversample() {
         return 2.0F;
      }

      public int getPixelWidth() {
         return this.width;
      }

      public int getPixelHeight() {
         return this.height;
      }

      public float getAdvance() {
         return (float)(this.width / 2 + 1);
      }

      public void upload(int p_211573_1_, int p_211573_2_) {
         this.source.upload(0, p_211573_1_, p_211573_2_, this.sourceX, this.sourceY, this.width, this.height, false, false);
      }

      public boolean isColored() {
         return this.source.format().components() > 1;
      }

      public float getShadowOffset() {
         return 0.5F;
      }

      public float getBoldOffset() {
         return 0.5F;
      }
   }
}
