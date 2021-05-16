package net.minecraft.client.audio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL10;

@OnlyIn(Dist.CLIENT)
public class SoundSource {
   private static final Logger LOGGER = LogManager.getLogger();
   private final int source;
   private final AtomicBoolean initialized = new AtomicBoolean(true);
   private int streamingBufferSize = 16384;
   @Nullable
   private IAudioStream stream;

   @Nullable
   static SoundSource create() {
      int[] aint = new int[1];
      AL10.alGenSources(aint);
      return ALUtils.checkALError("Allocate new source") ? null : new SoundSource(aint[0]);
   }

   private SoundSource(int p_i51178_1_) {
      this.source = p_i51178_1_;
   }

   public void destroy() {
      if (this.initialized.compareAndSet(true, false)) {
         AL10.alSourceStop(this.source);
         ALUtils.checkALError("Stop");
         if (this.stream != null) {
            try {
               this.stream.close();
            } catch (IOException ioexception) {
               LOGGER.error("Failed to close audio stream", (Throwable)ioexception);
            }

            this.removeProcessedBuffers();
            this.stream = null;
         }

         AL10.alDeleteSources(new int[]{this.source});
         ALUtils.checkALError("Cleanup");
      }

   }

   public void play() {
      AL10.alSourcePlay(this.source);
   }

   private int getState() {
      return !this.initialized.get() ? 4116 : AL10.alGetSourcei(this.source, 4112);
   }

   public void pause() {
      if (this.getState() == 4114) {
         AL10.alSourcePause(this.source);
      }

   }

   public void unpause() {
      if (this.getState() == 4115) {
         AL10.alSourcePlay(this.source);
      }

   }

   public void stop() {
      if (this.initialized.get()) {
         AL10.alSourceStop(this.source);
         ALUtils.checkALError("Stop");
      }

   }

   public boolean stopped() {
      return this.getState() == 4116;
   }

   public void setSelfPosition(Vector3d p_216420_1_) {
      AL10.alSourcefv(this.source, 4100, new float[]{(float)p_216420_1_.x, (float)p_216420_1_.y, (float)p_216420_1_.z});
   }

   public void setPitch(float p_216422_1_) {
      AL10.alSourcef(this.source, 4099, p_216422_1_);
   }

   public void setLooping(boolean p_216425_1_) {
      AL10.alSourcei(this.source, 4103, p_216425_1_ ? 1 : 0);
   }

   public void setVolume(float p_216430_1_) {
      AL10.alSourcef(this.source, 4106, p_216430_1_);
   }

   public void disableAttenuation() {
      AL10.alSourcei(this.source, 53248, 0);
   }

   public void linearAttenuation(float p_216423_1_) {
      AL10.alSourcei(this.source, 53248, 53251);
      AL10.alSourcef(this.source, 4131, p_216423_1_);
      AL10.alSourcef(this.source, 4129, 1.0F);
      AL10.alSourcef(this.source, 4128, 0.0F);
   }

   public void setRelative(boolean p_216432_1_) {
      AL10.alSourcei(this.source, 514, p_216432_1_ ? 1 : 0);
   }

   public void attachStaticBuffer(AudioStreamBuffer p_216429_1_) {
      p_216429_1_.getAlBuffer().ifPresent((p_216431_1_) -> {
         AL10.alSourcei(this.source, 4105, p_216431_1_);
      });
   }

   public void attachBufferStream(IAudioStream p_216433_1_) {
      this.stream = p_216433_1_;
      AudioFormat audioformat = p_216433_1_.getFormat();
      this.streamingBufferSize = calculateBufferSize(audioformat, 1);
      this.pumpBuffers(4);
   }

   private static int calculateBufferSize(AudioFormat p_216417_0_, int p_216417_1_) {
      return (int)((float)(p_216417_1_ * p_216417_0_.getSampleSizeInBits()) / 8.0F * (float)p_216417_0_.getChannels() * p_216417_0_.getSampleRate());
   }

   private void pumpBuffers(int p_216421_1_) {
      if (this.stream != null) {
         try {
            for(int i = 0; i < p_216421_1_; ++i) {
               ByteBuffer bytebuffer = this.stream.read(this.streamingBufferSize);
               if (bytebuffer != null) {
                  (new AudioStreamBuffer(bytebuffer, this.stream.getFormat())).releaseAlBuffer().ifPresent((p_216424_1_) -> {
                     AL10.alSourceQueueBuffers(this.source, new int[]{p_216424_1_});
                  });
               }
            }
         } catch (IOException ioexception) {
            LOGGER.error("Failed to read from audio stream", (Throwable)ioexception);
         }
      }

   }

   public void updateStream() {
      if (this.stream != null) {
         int i = this.removeProcessedBuffers();
         this.pumpBuffers(i);
      }

   }

   private int removeProcessedBuffers() {
      int i = AL10.alGetSourcei(this.source, 4118);
      if (i > 0) {
         int[] aint = new int[i];
         AL10.alSourceUnqueueBuffers(this.source, aint);
         ALUtils.checkALError("Unqueue buffers");
         AL10.alDeleteBuffers(aint);
         ALUtils.checkALError("Remove processed buffers");
      }

      return i;
   }
}
