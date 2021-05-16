package net.minecraft.client.gui.fonts.providers;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
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
public class TextureGlyphProvider implements IGlyphProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final NativeImage image;
   private final Int2ObjectMap<TextureGlyphProvider.GlyphInfo> glyphs;

   private TextureGlyphProvider(NativeImage p_i232266_1_, Int2ObjectMap<TextureGlyphProvider.GlyphInfo> p_i232266_2_) {
      this.image = p_i232266_1_;
      this.glyphs = p_i232266_2_;
   }

   public void close() {
      this.image.close();
   }

   @Nullable
   public IGlyphInfo getGlyph(int p_212248_1_) {
      return this.glyphs.get(p_212248_1_);
   }

   public IntSet getSupportedGlyphs() {
      return IntSets.unmodifiable(this.glyphs.keySet());
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IGlyphProviderFactory {
      private final ResourceLocation texture;
      private final List<int[]> chars;
      private final int height;
      private final int ascent;

      public Factory(ResourceLocation p_i49750_1_, int p_i49750_2_, int p_i49750_3_, List<int[]> p_i49750_4_) {
         this.texture = new ResourceLocation(p_i49750_1_.getNamespace(), "textures/" + p_i49750_1_.getPath());
         this.chars = p_i49750_4_;
         this.height = p_i49750_2_;
         this.ascent = p_i49750_3_;
      }

      public static TextureGlyphProvider.Factory fromJson(JsonObject p_211633_0_) {
         int i = JSONUtils.getAsInt(p_211633_0_, "height", 8);
         int j = JSONUtils.getAsInt(p_211633_0_, "ascent");
         if (j > i) {
            throw new JsonParseException("Ascent " + j + " higher than height " + i);
         } else {
            List<int[]> list = Lists.newArrayList();
            JsonArray jsonarray = JSONUtils.getAsJsonArray(p_211633_0_, "chars");

            for(int k = 0; k < jsonarray.size(); ++k) {
               String s = JSONUtils.convertToString(jsonarray.get(k), "chars[" + k + "]");
               int[] aint = s.codePoints().toArray();
               if (k > 0) {
                  int l = ((int[])list.get(0)).length;
                  if (aint.length != l) {
                     throw new JsonParseException("Elements of chars have to be the same length (found: " + aint.length + ", expected: " + l + "), pad with space or \\u0000");
                  }
               }

               list.add(aint);
            }

            if (!list.isEmpty() && ((int[])list.get(0)).length != 0) {
               return new TextureGlyphProvider.Factory(new ResourceLocation(JSONUtils.getAsString(p_211633_0_, "file")), i, j, list);
            } else {
               throw new JsonParseException("Expected to find data in chars, found none.");
            }
         }
      }

      @Nullable
      public IGlyphProvider create(IResourceManager p_211246_1_) {
         try (IResource iresource = p_211246_1_.getResource(this.texture)) {
            NativeImage nativeimage = NativeImage.read(NativeImage.PixelFormat.RGBA, iresource.getInputStream());
            int i = nativeimage.getWidth();
            int j = nativeimage.getHeight();
            int k = i / ((int[])this.chars.get(0)).length;
            int l = j / this.chars.size();
            float f = (float)this.height / (float)l;
            Int2ObjectMap<TextureGlyphProvider.GlyphInfo> int2objectmap = new Int2ObjectOpenHashMap<>();

            for(int i1 = 0; i1 < this.chars.size(); ++i1) {
               int j1 = 0;

               for(int k1 : this.chars.get(i1)) {
                  int l1 = j1++;
                  if (k1 != 0 && k1 != 32) {
                     int i2 = this.getActualGlyphWidth(nativeimage, k, l, l1, i1);
                     TextureGlyphProvider.GlyphInfo textureglyphprovider$glyphinfo = int2objectmap.put(k1, new TextureGlyphProvider.GlyphInfo(f, nativeimage, l1 * k, i1 * l, k, l, (int)(0.5D + (double)((float)i2 * f)) + 1, this.ascent));
                     if (textureglyphprovider$glyphinfo != null) {
                        TextureGlyphProvider.LOGGER.warn("Codepoint '{}' declared multiple times in {}", Integer.toHexString(k1), this.texture);
                     }
                  }
               }
            }

            return new TextureGlyphProvider(nativeimage, int2objectmap);
         } catch (IOException ioexception) {
            throw new RuntimeException(ioexception.getMessage());
         }
      }

      private int getActualGlyphWidth(NativeImage p_211632_1_, int p_211632_2_, int p_211632_3_, int p_211632_4_, int p_211632_5_) {
         int i;
         for(i = p_211632_2_ - 1; i >= 0; --i) {
            int j = p_211632_4_ * p_211632_2_ + i;

            for(int k = 0; k < p_211632_3_; ++k) {
               int l = p_211632_5_ * p_211632_3_ + k;
               if (p_211632_1_.getLuminanceOrAlpha(j, l) != 0) {
                  return i + 1;
               }
            }
         }

         return i + 1;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static final class GlyphInfo implements IGlyphInfo {
      private final float scale;
      private final NativeImage image;
      private final int offsetX;
      private final int offsetY;
      private final int width;
      private final int height;
      private final int advance;
      private final int ascent;

      private GlyphInfo(float p_i49748_1_, NativeImage p_i49748_2_, int p_i49748_3_, int p_i49748_4_, int p_i49748_5_, int p_i49748_6_, int p_i49748_7_, int p_i49748_8_) {
         this.scale = p_i49748_1_;
         this.image = p_i49748_2_;
         this.offsetX = p_i49748_3_;
         this.offsetY = p_i49748_4_;
         this.width = p_i49748_5_;
         this.height = p_i49748_6_;
         this.advance = p_i49748_7_;
         this.ascent = p_i49748_8_;
      }

      public float getOversample() {
         return 1.0F / this.scale;
      }

      public int getPixelWidth() {
         return this.width;
      }

      public int getPixelHeight() {
         return this.height;
      }

      public float getAdvance() {
         return (float)this.advance;
      }

      public float getBearingY() {
         return IGlyphInfo.super.getBearingY() + 7.0F - (float)this.ascent;
      }

      public void upload(int p_211573_1_, int p_211573_2_) {
         this.image.upload(0, p_211573_1_, p_211573_2_, this.offsetX, this.offsetY, this.width, this.height, false, false);
      }

      public boolean isColored() {
         return this.image.format().components() > 1;
      }
   }
}
