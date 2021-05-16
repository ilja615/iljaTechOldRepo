package net.minecraft.client.renderer.texture;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class TextureManager implements IFutureReloadListener, ITickable, AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ResourceLocation INTENTIONAL_MISSING_TEXTURE = new ResourceLocation("");
   private final Map<ResourceLocation, Texture> byPath = Maps.newHashMap();
   private final Set<ITickable> tickableTextures = Sets.newHashSet();
   private final Map<String, Integer> prefixRegister = Maps.newHashMap();
   private final IResourceManager resourceManager;

   public TextureManager(IResourceManager p_i1284_1_) {
      this.resourceManager = p_i1284_1_;
   }

   public void bind(ResourceLocation p_110577_1_) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            this._bind(p_110577_1_);
         });
      } else {
         this._bind(p_110577_1_);
      }

   }

   private void _bind(ResourceLocation p_229269_1_) {
      Texture texture = this.byPath.get(p_229269_1_);
      if (texture == null) {
         texture = new SimpleTexture(p_229269_1_);
         this.register(p_229269_1_, texture);
      }

      texture.bind();
   }

   public void register(ResourceLocation p_229263_1_, Texture p_229263_2_) {
      p_229263_2_ = this.loadTexture(p_229263_1_, p_229263_2_);
      Texture texture = this.byPath.put(p_229263_1_, p_229263_2_);
      if (texture != p_229263_2_) {
         if (texture != null && texture != MissingTextureSprite.getTexture()) {
            this.tickableTextures.remove(texture);
            this.safeClose(p_229263_1_, texture);
         }

         if (p_229263_2_ instanceof ITickable) {
            this.tickableTextures.add((ITickable)p_229263_2_);
         }
      }

   }

   private void safeClose(ResourceLocation p_243505_1_, Texture p_243505_2_) {
      if (p_243505_2_ != MissingTextureSprite.getTexture()) {
         try {
            p_243505_2_.close();
         } catch (Exception exception) {
            LOGGER.warn("Failed to close texture {}", p_243505_1_, exception);
         }
      }

      p_243505_2_.releaseId();
   }

   private Texture loadTexture(ResourceLocation p_230183_1_, Texture p_230183_2_) {
      try {
         p_230183_2_.load(this.resourceManager);
         return p_230183_2_;
      } catch (IOException ioexception) {
         if (p_230183_1_ != INTENTIONAL_MISSING_TEXTURE) {
            LOGGER.warn("Failed to load texture: {}", p_230183_1_, ioexception);
         }

         return MissingTextureSprite.getTexture();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Registering texture");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Resource location being registered");
         crashreportcategory.setDetail("Resource location", p_230183_1_);
         crashreportcategory.setDetail("Texture object class", () -> {
            return p_230183_2_.getClass().getName();
         });
         throw new ReportedException(crashreport);
      }
   }

   @Nullable
   public Texture getTexture(ResourceLocation p_229267_1_) {
      return this.byPath.get(p_229267_1_);
   }

   public ResourceLocation register(String p_110578_1_, DynamicTexture p_110578_2_) {
      Integer integer = this.prefixRegister.get(p_110578_1_);
      if (integer == null) {
         integer = 1;
      } else {
         integer = integer + 1;
      }

      this.prefixRegister.put(p_110578_1_, integer);
      ResourceLocation resourcelocation = new ResourceLocation(String.format("dynamic/%s_%d", p_110578_1_, integer));
      this.register(resourcelocation, p_110578_2_);
      return resourcelocation;
   }

   public CompletableFuture<Void> preload(ResourceLocation p_215268_1_, Executor p_215268_2_) {
      if (!this.byPath.containsKey(p_215268_1_)) {
         PreloadedTexture preloadedtexture = new PreloadedTexture(this.resourceManager, p_215268_1_, p_215268_2_);
         this.byPath.put(p_215268_1_, preloadedtexture);
         return preloadedtexture.getFuture().thenRunAsync(() -> {
            this.register(p_215268_1_, preloadedtexture);
         }, TextureManager::execute);
      } else {
         return CompletableFuture.completedFuture((Void)null);
      }
   }

   private static void execute(Runnable p_229262_0_) {
      Minecraft.getInstance().execute(() -> {
         RenderSystem.recordRenderCall(p_229262_0_::run);
      });
   }

   public void tick() {
      for(ITickable itickable : this.tickableTextures) {
         itickable.tick();
      }

   }

   public void release(ResourceLocation p_147645_1_) {
      Texture texture = this.getTexture(p_147645_1_);
      if (texture != null) {
         this.byPath.remove(p_147645_1_); // Forge: fix MC-98707
         TextureUtil.releaseTextureId(texture.getId());
      }

   }

   public void close() {
      this.byPath.forEach(this::safeClose);
      this.byPath.clear();
      this.tickableTextures.clear();
      this.prefixRegister.clear();
   }

   public CompletableFuture<Void> reload(IFutureReloadListener.IStage p_215226_1_, IResourceManager p_215226_2_, IProfiler p_215226_3_, IProfiler p_215226_4_, Executor p_215226_5_, Executor p_215226_6_) {
      return CompletableFuture.allOf(MainMenuScreen.preloadResources(this, p_215226_5_), this.preload(Widget.WIDGETS_LOCATION, p_215226_5_)).thenCompose(p_215226_1_::wait).thenAcceptAsync((p_229265_3_) -> {
         MissingTextureSprite.getTexture();
         RealmsMainScreen.updateTeaserImages(this.resourceManager);
         Iterator<Entry<ResourceLocation, Texture>> iterator = this.byPath.entrySet().iterator();

         while(iterator.hasNext()) {
            Entry<ResourceLocation, Texture> entry = iterator.next();
            ResourceLocation resourcelocation = entry.getKey();
            Texture texture = entry.getValue();
            if (texture == MissingTextureSprite.getTexture() && !resourcelocation.equals(MissingTextureSprite.getLocation())) {
               iterator.remove();
            } else {
               texture.reset(this, p_215226_2_, resourcelocation, p_215226_6_);
            }
         }

      }, (p_229266_0_) -> {
         RenderSystem.recordRenderCall(p_229266_0_::run);
      });
   }
}
