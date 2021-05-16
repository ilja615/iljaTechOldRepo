package net.minecraft.client.audio;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AudioStreamManager {
   private final IResourceManager resourceManager;
   private final Map<ResourceLocation, CompletableFuture<AudioStreamBuffer>> cache = Maps.newHashMap();

   public AudioStreamManager(IResourceManager p_i50893_1_) {
      this.resourceManager = p_i50893_1_;
   }

   public CompletableFuture<AudioStreamBuffer> getCompleteBuffer(ResourceLocation p_217909_1_) {
      return this.cache.computeIfAbsent(p_217909_1_, (p_217913_1_) -> {
         return CompletableFuture.supplyAsync(() -> {
            try (
               IResource iresource = this.resourceManager.getResource(p_217913_1_);
               InputStream inputstream = iresource.getInputStream();
               OggAudioStream oggaudiostream = new OggAudioStream(inputstream);
            ) {
               ByteBuffer bytebuffer = oggaudiostream.readAll();
               return new AudioStreamBuffer(bytebuffer, oggaudiostream.getFormat());
            } catch (IOException ioexception) {
               throw new CompletionException(ioexception);
            }
         }, Util.backgroundExecutor());
      });
   }

   public CompletableFuture<IAudioStream> getStream(ResourceLocation p_217917_1_, boolean p_217917_2_) {
      return CompletableFuture.supplyAsync(() -> {
         try {
            IResource iresource = this.resourceManager.getResource(p_217917_1_);
            InputStream inputstream = iresource.getInputStream();
            return (IAudioStream)(p_217917_2_ ? new OggAudioStreamWrapper(OggAudioStream::new, inputstream) : new OggAudioStream(inputstream));
         } catch (IOException ioexception) {
            throw new CompletionException(ioexception);
         }
      }, Util.backgroundExecutor());
   }

   public void clear() {
      this.cache.values().forEach((p_217910_0_) -> {
         p_217910_0_.thenAccept(AudioStreamBuffer::discardAlBuffer);
      });
      this.cache.clear();
   }

   public CompletableFuture<?> preload(Collection<Sound> p_217908_1_) {
      return CompletableFuture.allOf(p_217908_1_.stream().map((p_217911_1_) -> {
         return this.getCompleteBuffer(p_217911_1_.getPath());
      }).toArray((p_217916_0_) -> {
         return new CompletableFuture[p_217916_0_];
      }));
   }
}
