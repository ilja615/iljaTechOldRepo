package net.minecraft.client.audio;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SoundHandler extends ReloadListener<SoundHandler.Loader> {
   public static final Sound EMPTY_SOUND = new Sound("meta:missing_sound", 1.0F, 1.0F, 1, Sound.Type.FILE, false, false, 16);
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer()).registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
   private static final TypeToken<Map<String, SoundList>> SOUND_EVENT_REGISTRATION_TYPE = new TypeToken<Map<String, SoundList>>() {
   };
   private final Map<ResourceLocation, SoundEventAccessor> registry = Maps.newHashMap();
   private final SoundEngine soundEngine;

   public SoundHandler(IResourceManager p_i45122_1_, GameSettings p_i45122_2_) {
      this.soundEngine = new SoundEngine(this, p_i45122_2_, p_i45122_1_);
   }

   protected SoundHandler.Loader prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
      SoundHandler.Loader soundhandler$loader = new SoundHandler.Loader();
      p_212854_2_.startTick();

      for(String s : p_212854_1_.getNamespaces()) {
         p_212854_2_.push(s);

         try {
            for(IResource iresource : p_212854_1_.getResources(new ResourceLocation(s, "sounds.json"))) {
               p_212854_2_.push(iresource.getSourceName());

               try (
                  InputStream inputstream = iresource.getInputStream();
                  Reader reader = new InputStreamReader(inputstream, StandardCharsets.UTF_8);
               ) {
                  p_212854_2_.push("parse");
                  Map<String, SoundList> map = JSONUtils.fromJson(GSON, reader, SOUND_EVENT_REGISTRATION_TYPE);
                  p_212854_2_.popPush("register");

                  for(Entry<String, SoundList> entry : map.entrySet()) {
                     soundhandler$loader.handleRegistration(new ResourceLocation(s, entry.getKey()), entry.getValue(), p_212854_1_);
                  }

                  p_212854_2_.pop();
               } catch (RuntimeException runtimeexception) {
                  LOGGER.warn("Invalid sounds.json in resourcepack: '{}'", iresource.getSourceName(), runtimeexception);
               }

               p_212854_2_.pop();
            }
         } catch (IOException ioexception) {
         }

         p_212854_2_.pop();
      }

      p_212854_2_.endTick();
      return soundhandler$loader;
   }

   protected void apply(SoundHandler.Loader p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
      p_212853_1_.apply(this.registry, this.soundEngine);

      for(ResourceLocation resourcelocation : this.registry.keySet()) {
         SoundEventAccessor soundeventaccessor = this.registry.get(resourcelocation);
         if (soundeventaccessor.getSubtitle() instanceof TranslationTextComponent) {
            String s = ((TranslationTextComponent)soundeventaccessor.getSubtitle()).getKey();
            if (!I18n.exists(s)) {
               LOGGER.debug("Missing subtitle {} for event: {}", s, resourcelocation);
            }
         }
      }

      if (LOGGER.isDebugEnabled()) {
         for(ResourceLocation resourcelocation1 : this.registry.keySet()) {
            if (!Registry.SOUND_EVENT.containsKey(resourcelocation1)) {
               LOGGER.debug("Not having sound event for: {}", (Object)resourcelocation1);
            }
         }
      }

      this.soundEngine.reload();
   }

   private static boolean validateSoundResource(Sound p_215292_0_, ResourceLocation p_215292_1_, IResourceManager p_215292_2_) {
      ResourceLocation resourcelocation = p_215292_0_.getPath();
      if (!p_215292_2_.hasResource(resourcelocation)) {
         LOGGER.warn("File {} does not exist, cannot add it to event {}", resourcelocation, p_215292_1_);
         return false;
      } else {
         return true;
      }
   }

   @Nullable
   public SoundEventAccessor getSoundEvent(ResourceLocation p_184398_1_) {
      return this.registry.get(p_184398_1_);
   }

   public Collection<ResourceLocation> getAvailableSounds() {
      return this.registry.keySet();
   }

   public void queueTickingSound(ITickableSound p_229364_1_) {
      this.soundEngine.queueTickingSound(p_229364_1_);
   }

   public void play(ISound p_147682_1_) {
      this.soundEngine.play(p_147682_1_);
   }

   public void playDelayed(ISound p_147681_1_, int p_147681_2_) {
      this.soundEngine.playDelayed(p_147681_1_, p_147681_2_);
   }

   public void updateSource(ActiveRenderInfo p_215289_1_) {
      this.soundEngine.updateSource(p_215289_1_);
   }

   public void pause() {
      this.soundEngine.pause();
   }

   public void stop() {
      this.soundEngine.stopAll();
   }

   public void destroy() {
      this.soundEngine.destroy();
   }

   public void tick(boolean p_215290_1_) {
      this.soundEngine.tick(p_215290_1_);
   }

   public void resume() {
      this.soundEngine.resume();
   }

   public void updateSourceVolume(SoundCategory p_184399_1_, float p_184399_2_) {
      if (p_184399_1_ == SoundCategory.MASTER && p_184399_2_ <= 0.0F) {
         this.stop();
      }

      this.soundEngine.updateCategoryVolume(p_184399_1_, p_184399_2_);
   }

   public void stop(ISound p_147683_1_) {
      this.soundEngine.stop(p_147683_1_);
   }

   public boolean isActive(ISound p_215294_1_) {
      return this.soundEngine.isActive(p_215294_1_);
   }

   public void addListener(ISoundEventListener p_184402_1_) {
      this.soundEngine.addEventListener(p_184402_1_);
   }

   public void removeListener(ISoundEventListener p_184400_1_) {
      this.soundEngine.removeEventListener(p_184400_1_);
   }

   public void stop(@Nullable ResourceLocation p_195478_1_, @Nullable SoundCategory p_195478_2_) {
      this.soundEngine.stop(p_195478_1_, p_195478_2_);
   }

   //@Override //TODO: Filtered reload
   public net.minecraftforge.resource.IResourceType getResourceType() {
      return net.minecraftforge.resource.VanillaResourceType.SOUNDS;
   }

   public String getDebugString() {
      return this.soundEngine.getDebugString();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Loader {
      private final Map<ResourceLocation, SoundEventAccessor> registry = Maps.newHashMap();

      protected Loader() {
      }

      private void handleRegistration(ResourceLocation p_217944_1_, SoundList p_217944_2_, IResourceManager p_217944_3_) {
         SoundEventAccessor soundeventaccessor = this.registry.get(p_217944_1_);
         boolean flag = soundeventaccessor == null;
         if (flag || p_217944_2_.isReplace()) {
            if (!flag) {
               SoundHandler.LOGGER.debug("Replaced sound event location {}", (Object)p_217944_1_);
            }

            soundeventaccessor = new SoundEventAccessor(p_217944_1_, p_217944_2_.getSubtitle());
            this.registry.put(p_217944_1_, soundeventaccessor);
         }

         for(final Sound sound : p_217944_2_.getSounds()) {
            final ResourceLocation resourcelocation = sound.getLocation();
            ISoundEventAccessor<Sound> isoundeventaccessor;
            switch(sound.getType()) {
            case FILE:
               if (!SoundHandler.validateSoundResource(sound, p_217944_1_, p_217944_3_)) {
                  continue;
               }

               isoundeventaccessor = sound;
               break;
            case SOUND_EVENT:
               isoundeventaccessor = new ISoundEventAccessor<Sound>() {
                  public int getWeight() {
                     SoundEventAccessor soundeventaccessor1 = Loader.this.registry.get(resourcelocation);
                     return soundeventaccessor1 == null ? 0 : soundeventaccessor1.getWeight();
                  }

                  public Sound getSound() {
                     SoundEventAccessor soundeventaccessor1 = Loader.this.registry.get(resourcelocation);
                     if (soundeventaccessor1 == null) {
                        return SoundHandler.EMPTY_SOUND;
                     } else {
                        Sound sound1 = soundeventaccessor1.getSound();
                        return new Sound(sound1.getLocation().toString(), sound1.getVolume() * sound.getVolume(), sound1.getPitch() * sound.getPitch(), sound.getWeight(), Sound.Type.FILE, sound1.shouldStream() || sound.shouldStream(), sound1.shouldPreload(), sound1.getAttenuationDistance());
                     }
                  }

                  public void preloadIfRequired(SoundEngine p_217867_1_) {
                     SoundEventAccessor soundeventaccessor1 = Loader.this.registry.get(resourcelocation);
                     if (soundeventaccessor1 != null) {
                        soundeventaccessor1.preloadIfRequired(p_217867_1_);
                     }
                  }
               };
               break;
            default:
               throw new IllegalStateException("Unknown SoundEventRegistration type: " + sound.getType());
            }

            soundeventaccessor.addSound(isoundeventaccessor);
         }

      }

      public void apply(Map<ResourceLocation, SoundEventAccessor> p_217946_1_, SoundEngine p_217946_2_) {
         p_217946_1_.clear();

         for(Entry<ResourceLocation, SoundEventAccessor> entry : this.registry.entrySet()) {
            p_217946_1_.put(entry.getKey(), entry.getValue());
            entry.getValue().preloadIfRequired(p_217946_2_);
         }

      }
   }
}
