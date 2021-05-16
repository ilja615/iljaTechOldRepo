package net.minecraft.client.renderer.texture;

import com.google.common.base.Charsets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.util.LWJGLMemoryUntracker;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.stb.STBIWriteCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public final class NativeImage implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Set<StandardOpenOption> OPEN_OPTIONS = EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
   private final NativeImage.PixelFormat format;
   private final int width;
   private final int height;
   private final boolean useStbFree;
   private long pixels;
   private final long size;

   public NativeImage(int p_i48122_1_, int p_i48122_2_, boolean p_i48122_3_) {
      this(NativeImage.PixelFormat.RGBA, p_i48122_1_, p_i48122_2_, p_i48122_3_);
   }

   public NativeImage(NativeImage.PixelFormat p_i49763_1_, int p_i49763_2_, int p_i49763_3_, boolean p_i49763_4_) {
      this.format = p_i49763_1_;
      this.width = p_i49763_2_;
      this.height = p_i49763_3_;
      this.size = (long)p_i49763_2_ * (long)p_i49763_3_ * (long)p_i49763_1_.components();
      this.useStbFree = false;
      if (p_i49763_4_) {
         this.pixels = MemoryUtil.nmemCalloc(1L, this.size);
      } else {
         this.pixels = MemoryUtil.nmemAlloc(this.size);
      }

   }

   private NativeImage(NativeImage.PixelFormat p_i49764_1_, int p_i49764_2_, int p_i49764_3_, boolean p_i49764_4_, long p_i49764_5_) {
      this.format = p_i49764_1_;
      this.width = p_i49764_2_;
      this.height = p_i49764_3_;
      this.useStbFree = p_i49764_4_;
      this.pixels = p_i49764_5_;
      this.size = (long)(p_i49764_2_ * p_i49764_3_ * p_i49764_1_.components());
   }

   public String toString() {
      return "NativeImage[" + this.format + " " + this.width + "x" + this.height + "@" + this.pixels + (this.useStbFree ? "S" : "N") + "]";
   }

   public static NativeImage read(InputStream p_195713_0_) throws IOException {
      return read(NativeImage.PixelFormat.RGBA, p_195713_0_);
   }

   public static NativeImage read(@Nullable NativeImage.PixelFormat p_211679_0_, InputStream p_211679_1_) throws IOException {
      ByteBuffer bytebuffer = null;

      NativeImage nativeimage;
      try {
         bytebuffer = TextureUtil.readResource(p_211679_1_);
         ((Buffer)bytebuffer).rewind();
         nativeimage = read(p_211679_0_, bytebuffer);
      } finally {
         MemoryUtil.memFree(bytebuffer);
         IOUtils.closeQuietly(p_211679_1_);
      }

      return nativeimage;
   }

   public static NativeImage read(ByteBuffer p_195704_0_) throws IOException {
      return read(NativeImage.PixelFormat.RGBA, p_195704_0_);
   }

   public static NativeImage read(@Nullable NativeImage.PixelFormat p_211677_0_, ByteBuffer p_211677_1_) throws IOException {
      if (p_211677_0_ != null && !p_211677_0_.supportedByStb()) {
         throw new UnsupportedOperationException("Don't know how to read format " + p_211677_0_);
      } else if (MemoryUtil.memAddress(p_211677_1_) == 0L) {
         throw new IllegalArgumentException("Invalid buffer");
      } else {
         NativeImage nativeimage;
         try (MemoryStack memorystack = MemoryStack.stackPush()) {
            IntBuffer intbuffer = memorystack.mallocInt(1);
            IntBuffer intbuffer1 = memorystack.mallocInt(1);
            IntBuffer intbuffer2 = memorystack.mallocInt(1);
            ByteBuffer bytebuffer = STBImage.stbi_load_from_memory(p_211677_1_, intbuffer, intbuffer1, intbuffer2, p_211677_0_ == null ? 0 : p_211677_0_.components);
            if (bytebuffer == null) {
               throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
            }

            nativeimage = new NativeImage(p_211677_0_ == null ? NativeImage.PixelFormat.getStbFormat(intbuffer2.get(0)) : p_211677_0_, intbuffer.get(0), intbuffer1.get(0), true, MemoryUtil.memAddress(bytebuffer));
         }

         return nativeimage;
      }
   }

   private static void setClamp(boolean p_195707_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      if (p_195707_0_) {
         GlStateManager._texParameter(3553, 10242, 10496);
         GlStateManager._texParameter(3553, 10243, 10496);
      } else {
         GlStateManager._texParameter(3553, 10242, 10497);
         GlStateManager._texParameter(3553, 10243, 10497);
      }

   }

   private static void setFilter(boolean p_195705_0_, boolean p_195705_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      if (p_195705_0_) {
         GlStateManager._texParameter(3553, 10241, p_195705_1_ ? 9987 : 9729);
         GlStateManager._texParameter(3553, 10240, 9729);
      } else {
         GlStateManager._texParameter(3553, 10241, p_195705_1_ ? 9986 : 9728);
         GlStateManager._texParameter(3553, 10240, 9728);
      }

   }

   private void checkAllocated() {
      if (this.pixels == 0L) {
         throw new IllegalStateException("Image is not allocated.");
      }
   }

   public void close() {
      if (this.pixels != 0L) {
         if (this.useStbFree) {
            STBImage.nstbi_image_free(this.pixels);
         } else {
            MemoryUtil.nmemFree(this.pixels);
         }
      }

      this.pixels = 0L;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public NativeImage.PixelFormat format() {
      return this.format;
   }

   public int getPixelRGBA(int p_195709_1_, int p_195709_2_) {
      if (this.format != NativeImage.PixelFormat.RGBA) {
         throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.format));
      } else if (p_195709_1_ >= 0 && p_195709_2_ >= 0 && p_195709_1_ < this.width && p_195709_2_ < this.height) { //Fix MC-162953 bounds checks in `NativeImage`
         this.checkAllocated();
         long i = (long)((p_195709_1_ + p_195709_2_ * this.width) * 4);
         return MemoryUtil.memGetInt(this.pixels + i);
      } else {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", p_195709_1_, p_195709_2_, this.width, this.height));
      }
   }

   public void setPixelRGBA(int p_195700_1_, int p_195700_2_, int p_195700_3_) {
      if (this.format != NativeImage.PixelFormat.RGBA) {
         throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.format));
      } else if (p_195700_1_ >= 0 && p_195700_2_ >= 0 && p_195700_1_ < this.width && p_195700_2_ < this.height) { //Fix MC-162953 bounds checks in `NativeImage`
         this.checkAllocated();
         long i = (long)((p_195700_1_ + p_195700_2_ * this.width) * 4);
         MemoryUtil.memPutInt(this.pixels + i, p_195700_3_);
      } else {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", p_195700_1_, p_195700_2_, this.width, this.height));
      }
   }

   public byte getLuminanceOrAlpha(int p_211675_1_, int p_211675_2_) {
      if (!this.format.hasLuminanceOrAlpha()) {
         throw new IllegalArgumentException(String.format("no luminance or alpha in %s", this.format));
      } else if (p_211675_1_ >= 0 && p_211675_2_ >= 0 && p_211675_1_ < this.width && p_211675_2_ < this.height) { //Fix MC-162953 bounds checks in `NativeImage`
         int i = (p_211675_1_ + p_211675_2_ * this.width) * this.format.components() + this.format.luminanceOrAlphaOffset() / 8;
         return MemoryUtil.memGetByte(this.pixels + (long)i);
      } else {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", p_211675_1_, p_211675_2_, this.width, this.height));
      }
   }

   @Deprecated
   public int[] makePixelArray() {
      if (this.format != NativeImage.PixelFormat.RGBA) {
         throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
      } else {
         this.checkAllocated();
         int[] aint = new int[this.getWidth() * this.getHeight()];

         for(int i = 0; i < this.getHeight(); ++i) {
            for(int j = 0; j < this.getWidth(); ++j) {
               int k = this.getPixelRGBA(j, i);
               int l = getA(k);
               int i1 = getB(k);
               int j1 = getG(k);
               int k1 = getR(k);
               int l1 = l << 24 | k1 << 16 | j1 << 8 | i1;
               aint[j + i * this.getWidth()] = l1;
            }
         }

         return aint;
      }
   }

   public void upload(int p_195697_1_, int p_195697_2_, int p_195697_3_, boolean p_195697_4_) {
      this.upload(p_195697_1_, p_195697_2_, p_195697_3_, 0, 0, this.width, this.height, false, p_195697_4_);
   }

   public void upload(int p_227788_1_, int p_227788_2_, int p_227788_3_, int p_227788_4_, int p_227788_5_, int p_227788_6_, int p_227788_7_, boolean p_227788_8_, boolean p_227788_9_) {
      this.upload(p_227788_1_, p_227788_2_, p_227788_3_, p_227788_4_, p_227788_5_, p_227788_6_, p_227788_7_, false, false, p_227788_8_, p_227788_9_);
   }

   public void upload(int p_227789_1_, int p_227789_2_, int p_227789_3_, int p_227789_4_, int p_227789_5_, int p_227789_6_, int p_227789_7_, boolean p_227789_8_, boolean p_227789_9_, boolean p_227789_10_, boolean p_227789_11_) {
      if (!RenderSystem.isOnRenderThreadOrInit()) {
         RenderSystem.recordRenderCall(() -> {
            this._upload(p_227789_1_, p_227789_2_, p_227789_3_, p_227789_4_, p_227789_5_, p_227789_6_, p_227789_7_, p_227789_8_, p_227789_9_, p_227789_10_, p_227789_11_);
         });
      } else {
         this._upload(p_227789_1_, p_227789_2_, p_227789_3_, p_227789_4_, p_227789_5_, p_227789_6_, p_227789_7_, p_227789_8_, p_227789_9_, p_227789_10_, p_227789_11_);
      }

   }

   private void _upload(int p_227792_1_, int p_227792_2_, int p_227792_3_, int p_227792_4_, int p_227792_5_, int p_227792_6_, int p_227792_7_, boolean p_227792_8_, boolean p_227792_9_, boolean p_227792_10_, boolean p_227792_11_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      this.checkAllocated();
      setFilter(p_227792_8_, p_227792_10_);
      setClamp(p_227792_9_);
      if (p_227792_6_ == this.getWidth()) {
         GlStateManager._pixelStore(3314, 0);
      } else {
         GlStateManager._pixelStore(3314, this.getWidth());
      }

      GlStateManager._pixelStore(3316, p_227792_4_);
      GlStateManager._pixelStore(3315, p_227792_5_);
      this.format.setUnpackPixelStoreState();
      GlStateManager._texSubImage2D(3553, p_227792_1_, p_227792_2_, p_227792_3_, p_227792_6_, p_227792_7_, this.format.glFormat(), 5121, this.pixels);
      if (p_227792_11_) {
         this.close();
      }

   }

   public void downloadTexture(int p_195717_1_, boolean p_195717_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      this.checkAllocated();
      this.format.setPackPixelStoreState();
      GlStateManager._getTexImage(3553, p_195717_1_, this.format.glFormat(), 5121, this.pixels);
      if (p_195717_2_ && this.format.hasAlpha()) {
         for(int i = 0; i < this.getHeight(); ++i) {
            for(int j = 0; j < this.getWidth(); ++j) {
               this.setPixelRGBA(j, i, this.getPixelRGBA(j, i) | 255 << this.format.alphaOffset());
            }
         }
      }

   }

   public void writeToFile(File p_209271_1_) throws IOException {
      this.writeToFile(p_209271_1_.toPath());
   }

   public void copyFromFont(STBTTFontinfo p_211676_1_, int p_211676_2_, int p_211676_3_, int p_211676_4_, float p_211676_5_, float p_211676_6_, float p_211676_7_, float p_211676_8_, int p_211676_9_, int p_211676_10_) {
      if (p_211676_9_ >= 0 && p_211676_9_ + p_211676_3_ <= this.getWidth() && p_211676_10_ >= 0 && p_211676_10_ + p_211676_4_ <= this.getHeight()) {
         if (this.format.components() != 1) {
            throw new IllegalArgumentException("Can only write fonts into 1-component images.");
         } else {
            STBTruetype.nstbtt_MakeGlyphBitmapSubpixel(p_211676_1_.address(), this.pixels + (long)p_211676_9_ + (long)(p_211676_10_ * this.getWidth()), p_211676_3_, p_211676_4_, this.getWidth(), p_211676_5_, p_211676_6_, p_211676_7_, p_211676_8_, p_211676_2_);
         }
      } else {
         throw new IllegalArgumentException(String.format("Out of bounds: start: (%s, %s) (size: %sx%s); size: %sx%s", p_211676_9_, p_211676_10_, p_211676_3_, p_211676_4_, this.getWidth(), this.getHeight()));
      }
   }

   public void writeToFile(Path p_209270_1_) throws IOException {
      if (!this.format.supportedByStb()) {
         throw new UnsupportedOperationException("Don't know how to write format " + this.format);
      } else {
         this.checkAllocated();

         try (WritableByteChannel writablebytechannel = Files.newByteChannel(p_209270_1_, OPEN_OPTIONS)) {
            if (!this.writeToChannel(writablebytechannel)) {
               throw new IOException("Could not write image to the PNG file \"" + p_209270_1_.toAbsolutePath() + "\": " + STBImage.stbi_failure_reason());
            }
         }

      }
   }

   public byte[] asByteArray() throws IOException {
      byte[] abyte;
      try (
         ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
         WritableByteChannel writablebytechannel = Channels.newChannel(bytearrayoutputstream);
      ) {
         if (!this.writeToChannel(writablebytechannel)) {
            throw new IOException("Could not write image to byte array: " + STBImage.stbi_failure_reason());
         }

         abyte = bytearrayoutputstream.toByteArray();
      }

      return abyte;
   }

   private boolean writeToChannel(WritableByteChannel p_227790_1_) throws IOException {
      NativeImage.WriteCallback nativeimage$writecallback = new NativeImage.WriteCallback(p_227790_1_);

      boolean flag;
      try {
         int i = Math.min(this.getHeight(), Integer.MAX_VALUE / this.getWidth() / this.format.components());
         if (i < this.getHeight()) {
            LOGGER.warn("Dropping image height from {} to {} to fit the size into 32-bit signed int", this.getHeight(), i);
         }

         if (STBImageWrite.nstbi_write_png_to_func(nativeimage$writecallback.address(), 0L, this.getWidth(), i, this.format.components(), this.pixels, 0) != 0) {
            nativeimage$writecallback.throwIfException();
            return true;
         }

         flag = false;
      } finally {
         nativeimage$writecallback.free();
      }

      return flag;
   }

   public void copyFrom(NativeImage p_195703_1_) {
      if (p_195703_1_.format() != this.format) {
         throw new UnsupportedOperationException("Image formats don't match.");
      } else {
         int i = this.format.components();
         this.checkAllocated();
         p_195703_1_.checkAllocated();
         if (this.width == p_195703_1_.width) {
            MemoryUtil.memCopy(p_195703_1_.pixels, this.pixels, Math.min(this.size, p_195703_1_.size));
         } else {
            int j = Math.min(this.getWidth(), p_195703_1_.getWidth());
            int k = Math.min(this.getHeight(), p_195703_1_.getHeight());

            for(int l = 0; l < k; ++l) {
               int i1 = l * p_195703_1_.getWidth() * i;
               int j1 = l * this.getWidth() * i;
               MemoryUtil.memCopy(p_195703_1_.pixels + (long)i1, this.pixels + (long)j1, (long)j);
            }
         }

      }
   }

   public void fillRect(int p_195715_1_, int p_195715_2_, int p_195715_3_, int p_195715_4_, int p_195715_5_) {
      for(int i = p_195715_2_; i < p_195715_2_ + p_195715_4_; ++i) {
         for(int j = p_195715_1_; j < p_195715_1_ + p_195715_3_; ++j) {
            this.setPixelRGBA(j, i, p_195715_5_);
         }
      }

   }

   public void copyRect(int p_195699_1_, int p_195699_2_, int p_195699_3_, int p_195699_4_, int p_195699_5_, int p_195699_6_, boolean p_195699_7_, boolean p_195699_8_) {
      for(int i = 0; i < p_195699_6_; ++i) {
         for(int j = 0; j < p_195699_5_; ++j) {
            int k = p_195699_7_ ? p_195699_5_ - 1 - j : j;
            int l = p_195699_8_ ? p_195699_6_ - 1 - i : i;
            int i1 = this.getPixelRGBA(p_195699_1_ + j, p_195699_2_ + i);
            this.setPixelRGBA(p_195699_1_ + p_195699_3_ + k, p_195699_2_ + p_195699_4_ + l, i1);
         }
      }

   }

   public void flipY() {
      this.checkAllocated();

      try (MemoryStack memorystack = MemoryStack.stackPush()) {
         int i = this.format.components();
         int j = this.getWidth() * i;
         long k = memorystack.nmalloc(j);

         for(int l = 0; l < this.getHeight() / 2; ++l) {
            int i1 = l * this.getWidth() * i;
            int j1 = (this.getHeight() - 1 - l) * this.getWidth() * i;
            MemoryUtil.memCopy(this.pixels + (long)i1, k, (long)j);
            MemoryUtil.memCopy(this.pixels + (long)j1, this.pixels + (long)i1, (long)j);
            MemoryUtil.memCopy(k, this.pixels + (long)j1, (long)j);
         }
      }

   }

   public void resizeSubRectTo(int p_195708_1_, int p_195708_2_, int p_195708_3_, int p_195708_4_, NativeImage p_195708_5_) {
      this.checkAllocated();
      if (p_195708_5_.format() != this.format) {
         throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
      } else {
         int i = this.format.components();
         STBImageResize.nstbir_resize_uint8(this.pixels + (long)((p_195708_1_ + p_195708_2_ * this.getWidth()) * i), p_195708_3_, p_195708_4_, this.getWidth() * i, p_195708_5_.pixels, p_195708_5_.getWidth(), p_195708_5_.getHeight(), 0, i);
      }
   }

   public void untrack() {
      LWJGLMemoryUntracker.untrack(this.pixels);
   }

   public static NativeImage fromBase64(String p_216511_0_) throws IOException {
      byte[] abyte = Base64.getDecoder().decode(p_216511_0_.replaceAll("\n", "").getBytes(Charsets.UTF_8));

      NativeImage nativeimage;
      try (MemoryStack memorystack = MemoryStack.stackPush()) {
         ByteBuffer bytebuffer = memorystack.malloc(abyte.length);
         bytebuffer.put(abyte);
         ((Buffer)bytebuffer).rewind();
         nativeimage = read(bytebuffer);
      }

      return nativeimage;
   }

   public static int getA(int p_227786_0_) {
      return p_227786_0_ >> 24 & 255;
   }

   public static int getR(int p_227791_0_) {
      return p_227791_0_ >> 0 & 255;
   }

   public static int getG(int p_227793_0_) {
      return p_227793_0_ >> 8 & 255;
   }

   public static int getB(int p_227795_0_) {
      return p_227795_0_ >> 16 & 255;
   }

   public static int combine(int p_227787_0_, int p_227787_1_, int p_227787_2_, int p_227787_3_) {
      return (p_227787_0_ & 255) << 24 | (p_227787_1_ & 255) << 16 | (p_227787_2_ & 255) << 8 | (p_227787_3_ & 255) << 0;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum PixelFormat {
      RGBA(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true),
      RGB(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true),
      LUMINANCE_ALPHA(2, 6410, false, false, false, true, true, 255, 255, 255, 0, 8, true),
      LUMINANCE(1, 6409, false, false, false, true, false, 0, 0, 0, 0, 255, true);

      private final int components;
      private final int glFormat;
      private final boolean hasRed;
      private final boolean hasGreen;
      private final boolean hasBlue;
      private final boolean hasLuminance;
      private final boolean hasAlpha;
      private final int redOffset;
      private final int greenOffset;
      private final int blueOffset;
      private final int luminanceOffset;
      private final int alphaOffset;
      private final boolean supportedByStb;

      private PixelFormat(int p_i49762_3_, int p_i49762_4_, boolean p_i49762_5_, boolean p_i49762_6_, boolean p_i49762_7_, boolean p_i49762_8_, boolean p_i49762_9_, int p_i49762_10_, int p_i49762_11_, int p_i49762_12_, int p_i49762_13_, int p_i49762_14_, boolean p_i49762_15_) {
         this.components = p_i49762_3_;
         this.glFormat = p_i49762_4_;
         this.hasRed = p_i49762_5_;
         this.hasGreen = p_i49762_6_;
         this.hasBlue = p_i49762_7_;
         this.hasLuminance = p_i49762_8_;
         this.hasAlpha = p_i49762_9_;
         this.redOffset = p_i49762_10_;
         this.greenOffset = p_i49762_11_;
         this.blueOffset = p_i49762_12_;
         this.luminanceOffset = p_i49762_13_;
         this.alphaOffset = p_i49762_14_;
         this.supportedByStb = p_i49762_15_;
      }

      public int components() {
         return this.components;
      }

      public void setPackPixelStoreState() {
         RenderSystem.assertThread(RenderSystem::isOnRenderThread);
         GlStateManager._pixelStore(3333, this.components());
      }

      public void setUnpackPixelStoreState() {
         RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
         GlStateManager._pixelStore(3317, this.components());
      }

      public int glFormat() {
         return this.glFormat;
      }

      public boolean hasAlpha() {
         return this.hasAlpha;
      }

      public int alphaOffset() {
         return this.alphaOffset;
      }

      public boolean hasLuminanceOrAlpha() {
         return this.hasLuminance || this.hasAlpha;
      }

      public int luminanceOrAlphaOffset() {
         return this.hasLuminance ? this.luminanceOffset : this.alphaOffset;
      }

      public boolean supportedByStb() {
         return this.supportedByStb;
      }

      private static NativeImage.PixelFormat getStbFormat(int p_211646_0_) {
         switch(p_211646_0_) {
         case 1:
            return LUMINANCE;
         case 2:
            return LUMINANCE_ALPHA;
         case 3:
            return RGB;
         case 4:
         default:
            return RGBA;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum PixelFormatGLCode {
      RGBA(6408),
      RGB(6407),
      LUMINANCE_ALPHA(6410),
      LUMINANCE(6409),
      INTENSITY(32841);

      private final int glFormat;

      private PixelFormatGLCode(int p_i49761_3_) {
         this.glFormat = p_i49761_3_;
      }

      int glFormat() {
         return this.glFormat;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class WriteCallback extends STBIWriteCallback {
      private final WritableByteChannel output;
      @Nullable
      private IOException exception;

      private WriteCallback(WritableByteChannel p_i49388_1_) {
         this.output = p_i49388_1_;
      }

      public void invoke(long p_invoke_1_, long p_invoke_3_, int p_invoke_5_) {
         ByteBuffer bytebuffer = getData(p_invoke_3_, p_invoke_5_);

         try {
            this.output.write(bytebuffer);
         } catch (IOException ioexception) {
            this.exception = ioexception;
         }

      }

      public void throwIfException() throws IOException {
         if (this.exception != null) {
            throw this.exception;
         }
      }
   }
}
