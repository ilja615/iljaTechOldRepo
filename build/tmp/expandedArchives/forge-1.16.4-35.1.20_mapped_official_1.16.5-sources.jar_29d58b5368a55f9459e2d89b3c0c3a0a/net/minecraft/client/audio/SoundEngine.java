package net.minecraft.client.audio;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

@OnlyIn(Dist.CLIENT)
public class SoundEngine {
   private static final Marker MARKER = MarkerManager.getMarker("SOUNDS");
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Set<ResourceLocation> ONLY_WARN_ONCE = Sets.newHashSet();
   public final SoundHandler soundManager;
   private final GameSettings options;
   private boolean loaded;
   private final SoundSystem library = new SoundSystem();
   private final Listener listener = this.library.getListener();
   private final AudioStreamManager soundBuffers;
   private final SoundEngineExecutor executor = new SoundEngineExecutor();
   private final ChannelManager channelAccess = new ChannelManager(this.library, this.executor);
   private int tickCount;
   private final Map<ISound, ChannelManager.Entry> instanceToChannel = Maps.newHashMap();
   private final Multimap<SoundCategory, ISound> instanceBySource = HashMultimap.create();
   private final List<ITickableSound> tickingSounds = Lists.newArrayList();
   private final Map<ISound, Integer> queuedSounds = Maps.newHashMap();
   private final Map<ISound, Integer> soundDeleteTime = Maps.newHashMap();
   private final List<ISoundEventListener> listeners = Lists.newArrayList();
   private final List<ITickableSound> queuedTickableSounds = Lists.newArrayList();
   private final List<Sound> preloadQueue = Lists.newArrayList();

   public SoundEngine(SoundHandler p_i50892_1_, GameSettings p_i50892_2_, IResourceManager p_i50892_3_) {
      this.soundManager = p_i50892_1_;
      this.options = p_i50892_2_;
      this.soundBuffers = new AudioStreamManager(p_i50892_3_);
      net.minecraftforge.fml.ModLoader.get().postEvent(new net.minecraftforge.client.event.sound.SoundLoadEvent(this));
   }

   public void reload() {
      ONLY_WARN_ONCE.clear();

      for(SoundEvent soundevent : Registry.SOUND_EVENT) {
         ResourceLocation resourcelocation = soundevent.getLocation();
         if (this.soundManager.getSoundEvent(resourcelocation) == null) {
            LOGGER.warn("Missing sound for event: {}", (Object)Registry.SOUND_EVENT.getKey(soundevent));
            ONLY_WARN_ONCE.add(resourcelocation);
         }
      }

      this.destroy();
      this.loadLibrary();
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.sound.SoundLoadEvent(this));
   }

   private synchronized void loadLibrary() {
      if (!this.loaded) {
         try {
            this.library.init();
            this.listener.reset();
            this.listener.setGain(this.options.getSoundSourceVolume(SoundCategory.MASTER));
            this.soundBuffers.preload(this.preloadQueue).thenRun(this.preloadQueue::clear);
            this.loaded = true;
            LOGGER.info(MARKER, "Sound engine started");
         } catch (RuntimeException runtimeexception) {
            LOGGER.error(MARKER, "Error starting SoundSystem. Turning off sounds & music", (Throwable)runtimeexception);
         }

      }
   }

   private float getVolume(@Nullable SoundCategory p_188769_1_) {
      return p_188769_1_ != null && p_188769_1_ != SoundCategory.MASTER ? this.options.getSoundSourceVolume(p_188769_1_) : 1.0F;
   }

   public void updateCategoryVolume(SoundCategory p_188771_1_, float p_188771_2_) {
      if (this.loaded) {
         if (p_188771_1_ == SoundCategory.MASTER) {
            this.listener.setGain(p_188771_2_);
         } else {
            this.instanceToChannel.forEach((p_217926_1_, p_217926_2_) -> {
               float f = this.calculateVolume(p_217926_1_);
               p_217926_2_.execute((p_217923_1_) -> {
                  if (f <= 0.0F) {
                     p_217923_1_.stop();
                  } else {
                     p_217923_1_.setVolume(f);
                  }

               });
            });
         }
      }
   }

   public void destroy() {
      if (this.loaded) {
         this.stopAll();
         this.soundBuffers.clear();
         this.library.cleanup();
         this.loaded = false;
      }

   }

   public void stop(ISound p_148602_1_) {
      if (this.loaded) {
         ChannelManager.Entry channelmanager$entry = this.instanceToChannel.get(p_148602_1_);
         if (channelmanager$entry != null) {
            channelmanager$entry.execute(SoundSource::stop);
         }
      }

   }

   public void stopAll() {
      if (this.loaded) {
         this.executor.flush();
         this.instanceToChannel.values().forEach((p_217922_0_) -> {
            p_217922_0_.execute(SoundSource::stop);
         });
         this.instanceToChannel.clear();
         this.channelAccess.clear();
         this.queuedSounds.clear();
         this.tickingSounds.clear();
         this.instanceBySource.clear();
         this.soundDeleteTime.clear();
         this.queuedTickableSounds.clear();
      }

   }

   public void addEventListener(ISoundEventListener p_188774_1_) {
      this.listeners.add(p_188774_1_);
   }

   public void removeEventListener(ISoundEventListener p_188773_1_) {
      this.listeners.remove(p_188773_1_);
   }

   public void tick(boolean p_217921_1_) {
      if (!p_217921_1_) {
         this.tickNonPaused();
      }

      this.channelAccess.scheduleTick();
   }

   private void tickNonPaused() {
      ++this.tickCount;
      this.queuedTickableSounds.stream().filter(ISound::canPlaySound).forEach(this::play);
      this.queuedTickableSounds.clear();

      for(ITickableSound itickablesound : this.tickingSounds) {
         if (!itickablesound.canPlaySound()) {
            this.stop(itickablesound);
         }

         itickablesound.tick();
         if (itickablesound.isStopped()) {
            this.stop(itickablesound);
         } else {
            float f = this.calculateVolume(itickablesound);
            float f1 = this.calculatePitch(itickablesound);
            Vector3d vector3d = new Vector3d(itickablesound.getX(), itickablesound.getY(), itickablesound.getZ());
            ChannelManager.Entry channelmanager$entry = this.instanceToChannel.get(itickablesound);
            if (channelmanager$entry != null) {
               channelmanager$entry.execute((p_217924_3_) -> {
                  p_217924_3_.setVolume(f);
                  p_217924_3_.setPitch(f1);
                  p_217924_3_.setSelfPosition(vector3d);
               });
            }
         }
      }

      Iterator<Entry<ISound, ChannelManager.Entry>> iterator = this.instanceToChannel.entrySet().iterator();

      while(iterator.hasNext()) {
         Entry<ISound, ChannelManager.Entry> entry = iterator.next();
         ChannelManager.Entry channelmanager$entry1 = entry.getValue();
         ISound isound = entry.getKey();
         float f2 = this.options.getSoundSourceVolume(isound.getSource());
         if (f2 <= 0.0F) {
            channelmanager$entry1.execute(SoundSource::stop);
            iterator.remove();
         } else if (channelmanager$entry1.isStopped()) {
            int i = this.soundDeleteTime.get(isound);
            if (i <= this.tickCount) {
               if (shouldLoopManually(isound)) {
                  this.queuedSounds.put(isound, this.tickCount + isound.getDelay());
               }

               iterator.remove();
               LOGGER.debug(MARKER, "Removed channel {} because it's not playing anymore", (Object)channelmanager$entry1);
               this.soundDeleteTime.remove(isound);

               try {
                  this.instanceBySource.remove(isound.getSource(), isound);
               } catch (RuntimeException runtimeexception) {
               }

               if (isound instanceof ITickableSound) {
                  this.tickingSounds.remove(isound);
               }
            }
         }
      }

      Iterator<Entry<ISound, Integer>> iterator1 = this.queuedSounds.entrySet().iterator();

      while(iterator1.hasNext()) {
         Entry<ISound, Integer> entry1 = iterator1.next();
         if (this.tickCount >= entry1.getValue()) {
            ISound isound1 = entry1.getKey();
            if (isound1 instanceof ITickableSound) {
               ((ITickableSound)isound1).tick();
            }

            this.play(isound1);
            iterator1.remove();
         }
      }

   }

   private static boolean requiresManualLooping(ISound p_239544_0_) {
      return p_239544_0_.getDelay() > 0;
   }

   private static boolean shouldLoopManually(ISound p_239545_0_) {
      return p_239545_0_.isLooping() && requiresManualLooping(p_239545_0_);
   }

   private static boolean shouldLoopAutomatically(ISound p_239546_0_) {
      return p_239546_0_.isLooping() && !requiresManualLooping(p_239546_0_);
   }

   public boolean isActive(ISound p_217933_1_) {
      if (!this.loaded) {
         return false;
      } else {
         return this.soundDeleteTime.containsKey(p_217933_1_) && this.soundDeleteTime.get(p_217933_1_) <= this.tickCount ? true : this.instanceToChannel.containsKey(p_217933_1_);
      }
   }

   public void play(ISound p_148611_1_) {
      if (this.loaded) {
         p_148611_1_ = net.minecraftforge.client.ForgeHooksClient.playSound(this, p_148611_1_);
         if (p_148611_1_ != null && p_148611_1_.canPlaySound()) {
            SoundEventAccessor soundeventaccessor = p_148611_1_.resolve(this.soundManager);
            ResourceLocation resourcelocation = p_148611_1_.getLocation();
            if (soundeventaccessor == null) {
               if (ONLY_WARN_ONCE.add(resourcelocation)) {
                  LOGGER.warn(MARKER, "Unable to play unknown soundEvent: {}", (Object)resourcelocation);
               }

            } else {
               Sound sound = p_148611_1_.getSound();
               if (sound == SoundHandler.EMPTY_SOUND) {
                  if (ONLY_WARN_ONCE.add(resourcelocation)) {
                     LOGGER.warn(MARKER, "Unable to play empty soundEvent: {}", (Object)resourcelocation);
                  }

               } else {
                  float f = p_148611_1_.getVolume();
                  float f1 = Math.max(f, 1.0F) * (float)sound.getAttenuationDistance();
                  SoundCategory soundcategory = p_148611_1_.getSource();
                  float f2 = this.calculateVolume(p_148611_1_);
                  float f3 = this.calculatePitch(p_148611_1_);
                  ISound.AttenuationType isound$attenuationtype = p_148611_1_.getAttenuation();
                  boolean flag = p_148611_1_.isRelative();
                  if (f2 == 0.0F && !p_148611_1_.canStartSilent()) {
                     LOGGER.debug(MARKER, "Skipped playing sound {}, volume was zero.", (Object)sound.getLocation());
                  } else {
                     Vector3d vector3d = new Vector3d(p_148611_1_.getX(), p_148611_1_.getY(), p_148611_1_.getZ());
                     if (!this.listeners.isEmpty()) {
                        boolean flag1 = flag || isound$attenuationtype == ISound.AttenuationType.NONE || this.listener.getListenerPosition().distanceToSqr(vector3d) < (double)(f1 * f1);
                        if (flag1) {
                           for(ISoundEventListener isoundeventlistener : this.listeners) {
                              isoundeventlistener.onPlaySound(p_148611_1_, soundeventaccessor);
                           }
                        } else {
                           LOGGER.debug(MARKER, "Did not notify listeners of soundEvent: {}, it is too far away to hear", (Object)resourcelocation);
                        }
                     }

                     if (this.listener.getGain() <= 0.0F) {
                        LOGGER.debug(MARKER, "Skipped playing soundEvent: {}, master volume was zero", (Object)resourcelocation);
                     } else {
                        boolean flag2 = shouldLoopAutomatically(p_148611_1_);
                        boolean flag3 = sound.shouldStream();
                        CompletableFuture<ChannelManager.Entry> completablefuture = this.channelAccess.createHandle(sound.shouldStream() ? SoundSystem.Mode.STREAMING : SoundSystem.Mode.STATIC);
                        ChannelManager.Entry channelmanager$entry = completablefuture.join();
                        if (channelmanager$entry == null) {
                           LOGGER.warn("Failed to create new sound handle");
                        } else {
                           LOGGER.debug(MARKER, "Playing sound {} for event {}", sound.getLocation(), resourcelocation);
                           this.soundDeleteTime.put(p_148611_1_, this.tickCount + 20);
                           this.instanceToChannel.put(p_148611_1_, channelmanager$entry);
                           this.instanceBySource.put(soundcategory, p_148611_1_);
                           channelmanager$entry.execute((p_239543_8_) -> {
                              p_239543_8_.setPitch(f3);
                              p_239543_8_.setVolume(f2);
                              if (isound$attenuationtype == ISound.AttenuationType.LINEAR) {
                                 p_239543_8_.linearAttenuation(f1);
                              } else {
                                 p_239543_8_.disableAttenuation();
                              }

                              p_239543_8_.setLooping(flag2 && !flag3);
                              p_239543_8_.setSelfPosition(vector3d);
                              p_239543_8_.setRelative(flag);
                           });
                           final ISound isound = p_148611_1_;
                           if (!flag3) {
                              this.soundBuffers.getCompleteBuffer(sound.getPath()).thenAccept((p_217934_1_) -> {
                                 channelmanager$entry.execute((p_217925_1_) -> {
                                    p_217925_1_.attachStaticBuffer(p_217934_1_);
                                    p_217925_1_.play();
                                    net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.sound.PlaySoundSourceEvent(this, isound, p_217925_1_));
                                 });
                              });
                           } else {
                              this.soundBuffers.getStream(sound.getPath(), flag2).thenAccept((p_217928_1_) -> {
                                 channelmanager$entry.execute((p_217935_1_) -> {
                                    p_217935_1_.attachBufferStream(p_217928_1_);
                                    p_217935_1_.play();
                                    net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.sound.PlayStreamingSourceEvent(this, isound, p_217935_1_));
                                 });
                              });
                           }

                           if (p_148611_1_ instanceof ITickableSound) {
                              this.tickingSounds.add((ITickableSound)p_148611_1_);
                           }

                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void queueTickingSound(ITickableSound p_229363_1_) {
      this.queuedTickableSounds.add(p_229363_1_);
   }

   public void requestPreload(Sound p_204259_1_) {
      this.preloadQueue.add(p_204259_1_);
   }

   private float calculatePitch(ISound p_188772_1_) {
      return MathHelper.clamp(p_188772_1_.getPitch(), 0.5F, 2.0F);
   }

   private float calculateVolume(ISound p_188770_1_) {
      return MathHelper.clamp(p_188770_1_.getVolume() * this.getVolume(p_188770_1_.getSource()), 0.0F, 1.0F);
   }

   public void pause() {
      if (this.loaded) {
         this.channelAccess.executeOnChannels((p_217929_0_) -> {
            p_217929_0_.forEach(SoundSource::pause);
         });
      }

   }

   public void resume() {
      if (this.loaded) {
         this.channelAccess.executeOnChannels((p_217936_0_) -> {
            p_217936_0_.forEach(SoundSource::unpause);
         });
      }

   }

   public void playDelayed(ISound p_148599_1_, int p_148599_2_) {
      this.queuedSounds.put(p_148599_1_, this.tickCount + p_148599_2_);
   }

   public void updateSource(ActiveRenderInfo p_217920_1_) {
      if (this.loaded && p_217920_1_.isInitialized()) {
         Vector3d vector3d = p_217920_1_.getPosition();
         Vector3f vector3f = p_217920_1_.getLookVector();
         Vector3f vector3f1 = p_217920_1_.getUpVector();
         this.executor.execute(() -> {
            this.listener.setListenerPosition(vector3d);
            this.listener.setListenerOrientation(vector3f, vector3f1);
         });
      }
   }

   public void stop(@Nullable ResourceLocation p_195855_1_, @Nullable SoundCategory p_195855_2_) {
      if (p_195855_2_ != null) {
         for(ISound isound : this.instanceBySource.get(p_195855_2_)) {
            if (p_195855_1_ == null || isound.getLocation().equals(p_195855_1_)) {
               this.stop(isound);
            }
         }
      } else if (p_195855_1_ == null) {
         this.stopAll();
      } else {
         for(ISound isound1 : this.instanceToChannel.keySet()) {
            if (isound1.getLocation().equals(p_195855_1_)) {
               this.stop(isound1);
            }
         }
      }

   }

   public String getDebugString() {
      return this.library.getDebugString();
   }
}
