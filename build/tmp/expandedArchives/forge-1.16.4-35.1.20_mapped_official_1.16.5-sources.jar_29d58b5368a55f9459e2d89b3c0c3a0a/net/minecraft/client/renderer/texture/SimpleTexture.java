package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.Closeable;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SimpleTexture extends Texture {
   private static final Logger LOGGER = LogManager.getLogger();
   protected final ResourceLocation location;

   public SimpleTexture(ResourceLocation p_i1275_1_) {
      this.location = p_i1275_1_;
   }

   public void load(IResourceManager p_195413_1_) throws IOException {
      SimpleTexture.TextureData simpletexture$texturedata = this.getTextureImage(p_195413_1_);
      simpletexture$texturedata.throwIfError();
      TextureMetadataSection texturemetadatasection = simpletexture$texturedata.getTextureMetadata();
      boolean flag;
      boolean flag1;
      if (texturemetadatasection != null) {
         flag = texturemetadatasection.isBlur();
         flag1 = texturemetadatasection.isClamp();
      } else {
         flag = false;
         flag1 = false;
      }

      NativeImage nativeimage = simpletexture$texturedata.getImage();
      if (!RenderSystem.isOnRenderThreadOrInit()) {
         RenderSystem.recordRenderCall(() -> {
            this.doLoad(nativeimage, flag, flag1);
         });
      } else {
         this.doLoad(nativeimage, flag, flag1);
      }

   }

   private void doLoad(NativeImage p_229207_1_, boolean p_229207_2_, boolean p_229207_3_) {
      TextureUtil.prepareImage(this.getId(), 0, p_229207_1_.getWidth(), p_229207_1_.getHeight());
      p_229207_1_.upload(0, 0, 0, 0, 0, p_229207_1_.getWidth(), p_229207_1_.getHeight(), p_229207_2_, p_229207_3_, false, true);
   }

   protected SimpleTexture.TextureData getTextureImage(IResourceManager p_215246_1_) {
      return SimpleTexture.TextureData.load(p_215246_1_, this.location);
   }

   @OnlyIn(Dist.CLIENT)
   public static class TextureData implements Closeable {
      @Nullable
      private final TextureMetadataSection metadata;
      @Nullable
      private final NativeImage image;
      @Nullable
      private final IOException exception;

      public TextureData(IOException p_i50473_1_) {
         this.exception = p_i50473_1_;
         this.metadata = null;
         this.image = null;
      }

      public TextureData(@Nullable TextureMetadataSection p_i50474_1_, NativeImage p_i50474_2_) {
         this.exception = null;
         this.metadata = p_i50474_1_;
         this.image = p_i50474_2_;
      }

      public static SimpleTexture.TextureData load(IResourceManager p_217799_0_, ResourceLocation p_217799_1_) {
         try (IResource iresource = p_217799_0_.getResource(p_217799_1_)) {
            NativeImage nativeimage = NativeImage.read(iresource.getInputStream());
            TextureMetadataSection texturemetadatasection = null;

            try {
               texturemetadatasection = iresource.getMetadata(TextureMetadataSection.SERIALIZER);
            } catch (RuntimeException runtimeexception) {
               SimpleTexture.LOGGER.warn("Failed reading metadata of: {}", p_217799_1_, runtimeexception);
            }

            return new SimpleTexture.TextureData(texturemetadatasection, nativeimage);
         } catch (IOException ioexception) {
            return new SimpleTexture.TextureData(ioexception);
         }
      }

      @Nullable
      public TextureMetadataSection getTextureMetadata() {
         return this.metadata;
      }

      public NativeImage getImage() throws IOException {
         if (this.exception != null) {
            throw this.exception;
         } else {
            return this.image;
         }
      }

      public void close() {
         if (this.image != null) {
            this.image.close();
         }

      }

      public void throwIfError() throws IOException {
         if (this.exception != null) {
            throw this.exception;
         }
      }
   }
}
