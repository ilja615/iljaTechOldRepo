package net.minecraft.client.audio;

import com.google.common.collect.Sets;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.MemoryStack;

@OnlyIn(Dist.CLIENT)
public class SoundSystem {
   private static final Logger LOGGER = LogManager.getLogger();
   private long device;
   private long context;
   private static final SoundSystem.IHandler EMPTY = new SoundSystem.IHandler() {
      @Nullable
      public SoundSource acquire() {
         return null;
      }

      public boolean release(SoundSource p_216396_1_) {
         return false;
      }

      public void cleanup() {
      }

      public int getMaxCount() {
         return 0;
      }

      public int getUsedCount() {
         return 0;
      }
   };
   private SoundSystem.IHandler staticChannels = EMPTY;
   private SoundSystem.IHandler streamingChannels = EMPTY;
   private final Listener listener = new Listener();

   public void init() {
      this.device = tryOpenDevice();
      ALCCapabilities alccapabilities = ALC.createCapabilities(this.device);
      if (ALUtils.checkALCError(this.device, "Get capabilities")) {
         throw new IllegalStateException("Failed to get OpenAL capabilities");
      } else if (!alccapabilities.OpenALC11) {
         throw new IllegalStateException("OpenAL 1.1 not supported");
      } else {
         this.context = ALC10.alcCreateContext(this.device, (IntBuffer)null);
         ALC10.alcMakeContextCurrent(this.context);
         int i = this.getChannelCount();
         int j = MathHelper.clamp((int)MathHelper.sqrt((float)i), 2, 8);
         int k = MathHelper.clamp(i - j, 8, 255);
         this.staticChannels = new SoundSystem.HandlerImpl(k);
         this.streamingChannels = new SoundSystem.HandlerImpl(j);
         ALCapabilities alcapabilities = AL.createCapabilities(alccapabilities);
         ALUtils.checkALError("Initialization");
         if (!alcapabilities.AL_EXT_source_distance_model) {
            throw new IllegalStateException("AL_EXT_source_distance_model is not supported");
         } else {
            AL10.alEnable(512);
            if (!alcapabilities.AL_EXT_LINEAR_DISTANCE) {
               throw new IllegalStateException("AL_EXT_LINEAR_DISTANCE is not supported");
            } else {
               ALUtils.checkALError("Enable per-source distance models");
               LOGGER.info("OpenAL initialized.");
            }
         }
      }
   }

   private int getChannelCount() {
      int i1;
      try (MemoryStack memorystack = MemoryStack.stackPush()) {
         int i = ALC10.alcGetInteger(this.device, 4098);
         if (ALUtils.checkALCError(this.device, "Get attributes size")) {
            throw new IllegalStateException("Failed to get OpenAL attributes");
         }

         IntBuffer intbuffer = memorystack.mallocInt(i);
         ALC10.alcGetIntegerv(this.device, 4099, intbuffer);
         if (ALUtils.checkALCError(this.device, "Get attributes")) {
            throw new IllegalStateException("Failed to get OpenAL attributes");
         }

         int j = 0;

         int k;
         int l;
         do {
            if (j >= i) {
               return 30;
            }

            k = intbuffer.get(j++);
            if (k == 0) {
               return 30;
            }

            l = intbuffer.get(j++);
         } while(k != 4112);

         i1 = l;
      }

      return i1;
   }

   private static long tryOpenDevice() {
      for(int i = 0; i < 3; ++i) {
         long j = ALC10.alcOpenDevice((ByteBuffer)null);
         if (j != 0L && !ALUtils.checkALCError(j, "Open device")) {
            return j;
         }
      }

      throw new IllegalStateException("Failed to open OpenAL device");
   }

   public void cleanup() {
      this.staticChannels.cleanup();
      this.streamingChannels.cleanup();
      ALC10.alcDestroyContext(this.context);
      if (this.device != 0L) {
         ALC10.alcCloseDevice(this.device);
      }

   }

   public Listener getListener() {
      return this.listener;
   }

   @Nullable
   public SoundSource acquireChannel(SoundSystem.Mode p_216403_1_) {
      return (p_216403_1_ == SoundSystem.Mode.STREAMING ? this.streamingChannels : this.staticChannels).acquire();
   }

   public void releaseChannel(SoundSource p_216408_1_) {
      if (!this.staticChannels.release(p_216408_1_) && !this.streamingChannels.release(p_216408_1_)) {
         throw new IllegalStateException("Tried to release unknown channel");
      }
   }

   public String getDebugString() {
      return String.format("Sounds: %d/%d + %d/%d", this.staticChannels.getUsedCount(), this.staticChannels.getMaxCount(), this.streamingChannels.getUsedCount(), this.streamingChannels.getMaxCount());
   }

   @OnlyIn(Dist.CLIENT)
   static class HandlerImpl implements SoundSystem.IHandler {
      private final int limit;
      private final Set<SoundSource> activeChannels = Sets.newIdentityHashSet();

      public HandlerImpl(int p_i50804_1_) {
         this.limit = p_i50804_1_;
      }

      @Nullable
      public SoundSource acquire() {
         if (this.activeChannels.size() >= this.limit) {
            SoundSystem.LOGGER.warn("Maximum sound pool size {} reached", (int)this.limit);
            return null;
         } else {
            SoundSource soundsource = SoundSource.create();
            if (soundsource != null) {
               this.activeChannels.add(soundsource);
            }

            return soundsource;
         }
      }

      public boolean release(SoundSource p_216396_1_) {
         if (!this.activeChannels.remove(p_216396_1_)) {
            return false;
         } else {
            p_216396_1_.destroy();
            return true;
         }
      }

      public void cleanup() {
         this.activeChannels.forEach(SoundSource::destroy);
         this.activeChannels.clear();
      }

      public int getMaxCount() {
         return this.limit;
      }

      public int getUsedCount() {
         return this.activeChannels.size();
      }
   }

   @OnlyIn(Dist.CLIENT)
   interface IHandler {
      @Nullable
      SoundSource acquire();

      boolean release(SoundSource p_216396_1_);

      void cleanup();

      int getMaxCount();

      int getUsedCount();
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Mode {
      STATIC,
      STREAMING;
   }
}
