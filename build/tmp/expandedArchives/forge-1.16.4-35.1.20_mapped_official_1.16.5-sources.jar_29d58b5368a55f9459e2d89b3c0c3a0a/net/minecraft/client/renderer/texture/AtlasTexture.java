package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class AtlasTexture extends Texture implements ITickable {
   private static final Logger LOGGER = LogManager.getLogger();
   @Deprecated
   public static final ResourceLocation LOCATION_BLOCKS = PlayerContainer.BLOCK_ATLAS;
   @Deprecated
   public static final ResourceLocation LOCATION_PARTICLES = new ResourceLocation("textures/atlas/particles.png");
   private final List<TextureAtlasSprite> animatedTextures = Lists.newArrayList();
   private final Set<ResourceLocation> sprites = Sets.newHashSet();
   private final Map<ResourceLocation, TextureAtlasSprite> texturesByName = Maps.newHashMap();
   private final ResourceLocation location;
   private final int maxSupportedTextureSize;

   public AtlasTexture(ResourceLocation p_i226047_1_) {
      this.location = p_i226047_1_;
      this.maxSupportedTextureSize = RenderSystem.maxSupportedTextureSize();
   }

   public void load(IResourceManager p_195413_1_) throws IOException {
   }

   public void reload(AtlasTexture.SheetData p_215260_1_) {
      this.sprites.clear();
      this.sprites.addAll(p_215260_1_.sprites);
      LOGGER.info("Created: {}x{}x{} {}-atlas", p_215260_1_.width, p_215260_1_.height, p_215260_1_.mipLevel, this.location);
      TextureUtil.prepareImage(this.getId(), p_215260_1_.mipLevel, p_215260_1_.width, p_215260_1_.height);
      this.clearTextureData();

      for(TextureAtlasSprite textureatlassprite : p_215260_1_.regions) {
         this.texturesByName.put(textureatlassprite.getName(), textureatlassprite);

         try {
            textureatlassprite.uploadFirstFrame();
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Stitching texture atlas");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Texture being stitched together");
            crashreportcategory.setDetail("Atlas path", this.location);
            crashreportcategory.setDetail("Sprite", textureatlassprite);
            throw new ReportedException(crashreport);
         }

         if (textureatlassprite.isAnimation()) {
            this.animatedTextures.add(textureatlassprite);
         }
      }

      net.minecraftforge.client.ForgeHooksClient.onTextureStitchedPost(this);
   }

   public AtlasTexture.SheetData prepareToStitch(IResourceManager p_229220_1_, Stream<ResourceLocation> p_229220_2_, IProfiler p_229220_3_, int p_229220_4_) {
      p_229220_3_.push("preparing");
      Set<ResourceLocation> set = p_229220_2_.peek((p_229222_0_) -> {
         if (p_229222_0_ == null) {
            throw new IllegalArgumentException("Location cannot be null!");
         }
      }).collect(Collectors.toSet());
      int i = this.maxSupportedTextureSize;
      Stitcher stitcher = new Stitcher(i, i, p_229220_4_);
      int j = Integer.MAX_VALUE;
      int k = 1 << p_229220_4_;
      p_229220_3_.popPush("extracting_frames");
      net.minecraftforge.client.ForgeHooksClient.onTextureStitchedPre(this, set);

      for(TextureAtlasSprite.Info textureatlassprite$info : this.getBasicSpriteInfos(p_229220_1_, set)) {
         j = Math.min(j, Math.min(textureatlassprite$info.width(), textureatlassprite$info.height()));
         int l = Math.min(Integer.lowestOneBit(textureatlassprite$info.width()), Integer.lowestOneBit(textureatlassprite$info.height()));
         if (l < k) {
            LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", textureatlassprite$info.name(), textureatlassprite$info.width(), textureatlassprite$info.height(), MathHelper.log2(k), MathHelper.log2(l));
            k = l;
         }

         stitcher.registerSprite(textureatlassprite$info);
      }

      int i1 = Math.min(j, k);
      int j1 = MathHelper.log2(i1);
      int k1 = p_229220_4_;
      if (false) // FORGE: do not lower the mipmap level
      if (j1 < p_229220_4_) {
         LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.location, p_229220_4_, j1, i1);
         k1 = j1;
      } else {
         k1 = p_229220_4_;
      }

      p_229220_3_.popPush("register");
      stitcher.registerSprite(MissingTextureSprite.info());
      p_229220_3_.popPush("stitching");

      try {
         stitcher.stitch();
      } catch (StitcherException stitcherexception) {
         CrashReport crashreport = CrashReport.forThrowable(stitcherexception, "Stitching");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Stitcher");
         crashreportcategory.setDetail("Sprites", stitcherexception.getAllSprites().stream().map((p_229216_0_) -> {
            return String.format("%s[%dx%d]", p_229216_0_.name(), p_229216_0_.width(), p_229216_0_.height());
         }).collect(Collectors.joining(",")));
         crashreportcategory.setDetail("Max Texture Size", i);
         throw new ReportedException(crashreport);
      }

      p_229220_3_.popPush("loading");
      List<TextureAtlasSprite> list = this.getLoadedSprites(p_229220_1_, stitcher, k1);
      p_229220_3_.pop();
      return new AtlasTexture.SheetData(set, stitcher.getWidth(), stitcher.getHeight(), k1, list);
   }

   private Collection<TextureAtlasSprite.Info> getBasicSpriteInfos(IResourceManager p_215256_1_, Set<ResourceLocation> p_215256_2_) {
      List<CompletableFuture<?>> list = Lists.newArrayList();
      ConcurrentLinkedQueue<TextureAtlasSprite.Info> concurrentlinkedqueue = new ConcurrentLinkedQueue<>();

      for(ResourceLocation resourcelocation : p_215256_2_) {
         if (!MissingTextureSprite.getLocation().equals(resourcelocation)) {
            list.add(CompletableFuture.runAsync(() -> {
               ResourceLocation resourcelocation1 = this.getResourceLocation(resourcelocation);

               TextureAtlasSprite.Info textureatlassprite$info;
               try (IResource iresource = p_215256_1_.getResource(resourcelocation1)) {
                  PngSizeInfo pngsizeinfo = new PngSizeInfo(iresource.toString(), iresource.getInputStream());
                  AnimationMetadataSection animationmetadatasection = iresource.getMetadata(AnimationMetadataSection.SERIALIZER);
                  if (animationmetadatasection == null) {
                     animationmetadatasection = AnimationMetadataSection.EMPTY;
                  }

                  Pair<Integer, Integer> pair = animationmetadatasection.getFrameSize(pngsizeinfo.width, pngsizeinfo.height);
                  textureatlassprite$info = new TextureAtlasSprite.Info(resourcelocation, pair.getFirst(), pair.getSecond(), animationmetadatasection);
               } catch (RuntimeException runtimeexception) {
                  LOGGER.error("Unable to parse metadata from {} : {}", resourcelocation1, runtimeexception);
                  return;
               } catch (IOException ioexception) {
                  LOGGER.error("Using missing texture, unable to load {} : {}", resourcelocation1, ioexception);
                  return;
               }

               concurrentlinkedqueue.add(textureatlassprite$info);
            }, Util.backgroundExecutor()));
         }
      }

      CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
      return concurrentlinkedqueue;
   }

   private List<TextureAtlasSprite> getLoadedSprites(IResourceManager p_229217_1_, Stitcher p_229217_2_, int p_229217_3_) {
      ConcurrentLinkedQueue<TextureAtlasSprite> concurrentlinkedqueue = new ConcurrentLinkedQueue<>();
      List<CompletableFuture<?>> list = Lists.newArrayList();
      p_229217_2_.gatherSprites((p_229215_5_, p_229215_6_, p_229215_7_, p_229215_8_, p_229215_9_) -> {
         if (p_229215_5_ == MissingTextureSprite.info()) {
            MissingTextureSprite missingtexturesprite = MissingTextureSprite.newInstance(this, p_229217_3_, p_229215_6_, p_229215_7_, p_229215_8_, p_229215_9_);
            concurrentlinkedqueue.add(missingtexturesprite);
         } else {
            list.add(CompletableFuture.runAsync(() -> {
               TextureAtlasSprite textureatlassprite = this.load(p_229217_1_, p_229215_5_, p_229215_6_, p_229215_7_, p_229217_3_, p_229215_8_, p_229215_9_);
               if (textureatlassprite != null) {
                  concurrentlinkedqueue.add(textureatlassprite);
               }

            }, Util.backgroundExecutor()));
         }

      });
      CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
      return Lists.newArrayList(concurrentlinkedqueue);
   }

   @Nullable
   private TextureAtlasSprite load(IResourceManager p_229218_1_, TextureAtlasSprite.Info p_229218_2_, int p_229218_3_, int p_229218_4_, int p_229218_5_, int p_229218_6_, int p_229218_7_) {
      ResourceLocation resourcelocation = this.getResourceLocation(p_229218_2_.name());

      try (IResource iresource = p_229218_1_.getResource(resourcelocation)) {
         NativeImage nativeimage = NativeImage.read(iresource.getInputStream());
         return new TextureAtlasSprite(this, p_229218_2_, p_229218_5_, p_229218_3_, p_229218_4_, p_229218_6_, p_229218_7_, nativeimage);
      } catch (RuntimeException runtimeexception) {
         LOGGER.error("Unable to parse metadata from {}", resourcelocation, runtimeexception);
         return null;
      } catch (IOException ioexception) {
         LOGGER.error("Using missing texture, unable to load {}", resourcelocation, ioexception);
         return null;
      }
   }

   private ResourceLocation getResourceLocation(ResourceLocation p_195420_1_) {
      return new ResourceLocation(p_195420_1_.getNamespace(), String.format("textures/%s%s", p_195420_1_.getPath(), ".png"));
   }

   public void cycleAnimationFrames() {
      this.bind();

      for(TextureAtlasSprite textureatlassprite : this.animatedTextures) {
         textureatlassprite.cycleFrames();
      }

   }

   public void tick() {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(this::cycleAnimationFrames);
      } else {
         this.cycleAnimationFrames();
      }

   }

   public TextureAtlasSprite getSprite(ResourceLocation p_195424_1_) {
      TextureAtlasSprite textureatlassprite = this.texturesByName.get(p_195424_1_);
      return textureatlassprite == null ? this.texturesByName.get(MissingTextureSprite.getLocation()) : textureatlassprite;
   }

   public void clearTextureData() {
      for(TextureAtlasSprite textureatlassprite : this.texturesByName.values()) {
         textureatlassprite.close();
      }

      this.texturesByName.clear();
      this.animatedTextures.clear();
   }

   public ResourceLocation location() {
      return this.location;
   }

   public void updateFilter(AtlasTexture.SheetData p_229221_1_) {
      this.setFilter(false, p_229221_1_.mipLevel > 0);
   }

   @OnlyIn(Dist.CLIENT)
   public static class SheetData {
      final Set<ResourceLocation> sprites;
      final int width;
      final int height;
      final int mipLevel;
      final List<TextureAtlasSprite> regions;

      public SheetData(Set<ResourceLocation> p_i226048_1_, int p_i226048_2_, int p_i226048_3_, int p_i226048_4_, List<TextureAtlasSprite> p_i226048_5_) {
         this.sprites = p_i226048_1_;
         this.width = p_i226048_2_;
         this.height = p_i226048_3_;
         this.mipLevel = p_i226048_4_;
         this.regions = p_i226048_5_;
      }
   }
}
