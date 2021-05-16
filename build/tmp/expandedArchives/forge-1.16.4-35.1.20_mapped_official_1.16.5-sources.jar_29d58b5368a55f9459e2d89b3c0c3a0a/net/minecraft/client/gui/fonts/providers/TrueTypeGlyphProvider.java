package net.minecraft.client.gui.fonts.providers;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.fonts.IGlyphInfo;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class TrueTypeGlyphProvider implements IGlyphProvider {
   private final ByteBuffer fontMemory;
   private final STBTTFontinfo font;
   private final float oversample;
   private final IntSet skip = new IntArraySet();
   private final float shiftX;
   private final float shiftY;
   private final float pointScale;
   private final float ascent;

   public TrueTypeGlyphProvider(ByteBuffer p_i230051_1_, STBTTFontinfo p_i230051_2_, float p_i230051_3_, float p_i230051_4_, float p_i230051_5_, float p_i230051_6_, String p_i230051_7_) {
      this.fontMemory = p_i230051_1_;
      this.font = p_i230051_2_;
      this.oversample = p_i230051_4_;
      p_i230051_7_.codePoints().forEach(this.skip::add);
      this.shiftX = p_i230051_5_ * p_i230051_4_;
      this.shiftY = p_i230051_6_ * p_i230051_4_;
      this.pointScale = STBTruetype.stbtt_ScaleForPixelHeight(p_i230051_2_, p_i230051_3_ * p_i230051_4_);

      try (MemoryStack memorystack = MemoryStack.stackPush()) {
         IntBuffer intbuffer = memorystack.mallocInt(1);
         IntBuffer intbuffer1 = memorystack.mallocInt(1);
         IntBuffer intbuffer2 = memorystack.mallocInt(1);
         STBTruetype.stbtt_GetFontVMetrics(p_i230051_2_, intbuffer, intbuffer1, intbuffer2);
         this.ascent = (float)intbuffer.get(0) * this.pointScale;
      }

   }

   @Nullable
   public TrueTypeGlyphProvider.GlpyhInfo getGlyph(int p_212248_1_) {
      if (this.skip.contains(p_212248_1_)) {
         return null;
      } else {
         Object lvt_9_1_;
         try (MemoryStack memorystack = MemoryStack.stackPush()) {
            IntBuffer intbuffer = memorystack.mallocInt(1);
            IntBuffer intbuffer1 = memorystack.mallocInt(1);
            IntBuffer intbuffer2 = memorystack.mallocInt(1);
            IntBuffer intbuffer3 = memorystack.mallocInt(1);
            int i = STBTruetype.stbtt_FindGlyphIndex(this.font, p_212248_1_);
            if (i != 0) {
               STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel(this.font, i, this.pointScale, this.pointScale, this.shiftX, this.shiftY, intbuffer, intbuffer1, intbuffer2, intbuffer3);
               int k = intbuffer2.get(0) - intbuffer.get(0);
               int j = intbuffer3.get(0) - intbuffer1.get(0);
               if (k != 0 && j != 0) {
                  IntBuffer intbuffer5 = memorystack.mallocInt(1);
                  IntBuffer intbuffer4 = memorystack.mallocInt(1);
                  STBTruetype.stbtt_GetGlyphHMetrics(this.font, i, intbuffer5, intbuffer4);
                  return new TrueTypeGlyphProvider.GlpyhInfo(intbuffer.get(0), intbuffer2.get(0), -intbuffer1.get(0), -intbuffer3.get(0), (float)intbuffer5.get(0) * this.pointScale, (float)intbuffer4.get(0) * this.pointScale, i);
               }

               return null;
            }

            lvt_9_1_ = null;
         }

         return (TrueTypeGlyphProvider.GlpyhInfo)lvt_9_1_;
      }
   }

   public void close() {
      this.font.free();
      MemoryUtil.memFree(this.fontMemory);
   }

   public IntSet getSupportedGlyphs() {
      return IntStream.range(0, 65535).filter((p_237505_1_) -> {
         return !this.skip.contains(p_237505_1_);
      }).collect(IntOpenHashSet::new, IntCollection::add, IntCollection::addAll);
   }

   @OnlyIn(Dist.CLIENT)
   class GlpyhInfo implements IGlyphInfo {
      private final int width;
      private final int height;
      private final float bearingX;
      private final float bearingY;
      private final float advance;
      private final int index;

      private GlpyhInfo(int p_i49751_2_, int p_i49751_3_, int p_i49751_4_, int p_i49751_5_, float p_i49751_6_, float p_i49751_7_, int p_i49751_8_) {
         this.width = p_i49751_3_ - p_i49751_2_;
         this.height = p_i49751_4_ - p_i49751_5_;
         this.advance = p_i49751_6_ / TrueTypeGlyphProvider.this.oversample;
         this.bearingX = (p_i49751_7_ + (float)p_i49751_2_ + TrueTypeGlyphProvider.this.shiftX) / TrueTypeGlyphProvider.this.oversample;
         this.bearingY = (TrueTypeGlyphProvider.this.ascent - (float)p_i49751_4_ + TrueTypeGlyphProvider.this.shiftY) / TrueTypeGlyphProvider.this.oversample;
         this.index = p_i49751_8_;
      }

      public int getPixelWidth() {
         return this.width;
      }

      public int getPixelHeight() {
         return this.height;
      }

      public float getOversample() {
         return TrueTypeGlyphProvider.this.oversample;
      }

      public float getAdvance() {
         return this.advance;
      }

      public float getBearingX() {
         return this.bearingX;
      }

      public float getBearingY() {
         return this.bearingY;
      }

      public void upload(int p_211573_1_, int p_211573_2_) {
         NativeImage nativeimage = new NativeImage(NativeImage.PixelFormat.LUMINANCE, this.width, this.height, false);
         nativeimage.copyFromFont(TrueTypeGlyphProvider.this.font, this.index, this.width, this.height, TrueTypeGlyphProvider.this.pointScale, TrueTypeGlyphProvider.this.pointScale, TrueTypeGlyphProvider.this.shiftX, TrueTypeGlyphProvider.this.shiftY, 0, 0);
         nativeimage.upload(0, p_211573_1_, p_211573_2_, 0, 0, this.width, this.height, false, true);
      }

      public boolean isColored() {
         return false;
      }
   }
}
