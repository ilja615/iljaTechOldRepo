package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DownloadingTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkinManager {
   private final TextureManager textureManager;
   private final File skinsDirectory;
   private final MinecraftSessionService sessionService;
   private final LoadingCache<String, Map<Type, MinecraftProfileTexture>> insecureSkinCache;

   public SkinManager(TextureManager p_i1044_1_, File p_i1044_2_, final MinecraftSessionService p_i1044_3_) {
      this.textureManager = p_i1044_1_;
      this.skinsDirectory = p_i1044_2_;
      this.sessionService = p_i1044_3_;
      this.insecureSkinCache = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader<String, Map<Type, MinecraftProfileTexture>>() {
         public Map<Type, MinecraftProfileTexture> load(String p_load_1_) {
            GameProfile gameprofile = new GameProfile((UUID)null, "dummy_mcdummyface");
            gameprofile.getProperties().put("textures", new Property("textures", p_load_1_, ""));

            try {
               return p_i1044_3_.getTextures(gameprofile, false);
            } catch (Throwable throwable) {
               return ImmutableMap.of();
            }
         }
      });
   }

   public ResourceLocation registerTexture(MinecraftProfileTexture p_152792_1_, Type p_152792_2_) {
      return this.registerTexture(p_152792_1_, p_152792_2_, (SkinManager.ISkinAvailableCallback)null);
   }

   private ResourceLocation registerTexture(MinecraftProfileTexture p_152789_1_, Type p_152789_2_, @Nullable SkinManager.ISkinAvailableCallback p_152789_3_) {
      String s = Hashing.sha1().hashUnencodedChars(p_152789_1_.getHash()).toString();
      ResourceLocation resourcelocation = new ResourceLocation("skins/" + s);
      Texture texture = this.textureManager.getTexture(resourcelocation);
      if (texture != null) {
         if (p_152789_3_ != null) {
            p_152789_3_.onSkinTextureAvailable(p_152789_2_, resourcelocation, p_152789_1_);
         }
      } else {
         File file1 = new File(this.skinsDirectory, s.length() > 2 ? s.substring(0, 2) : "xx");
         File file2 = new File(file1, s);
         DownloadingTexture downloadingtexture = new DownloadingTexture(file2, p_152789_1_.getUrl(), DefaultPlayerSkin.getDefaultSkin(), p_152789_2_ == Type.SKIN, () -> {
            if (p_152789_3_ != null) {
               p_152789_3_.onSkinTextureAvailable(p_152789_2_, resourcelocation, p_152789_1_);
            }

         });
         this.textureManager.register(resourcelocation, downloadingtexture);
      }

      return resourcelocation;
   }

   public void registerSkins(GameProfile p_152790_1_, SkinManager.ISkinAvailableCallback p_152790_2_, boolean p_152790_3_) {
      Runnable runnable = () -> {
         Map<Type, MinecraftProfileTexture> map = Maps.newHashMap();

         try {
            map.putAll(this.sessionService.getTextures(p_152790_1_, p_152790_3_));
         } catch (InsecureTextureException insecuretextureexception1) {
         }

         if (map.isEmpty()) {
            p_152790_1_.getProperties().clear();
            if (p_152790_1_.getId().equals(Minecraft.getInstance().getUser().getGameProfile().getId())) {
               p_152790_1_.getProperties().putAll(Minecraft.getInstance().getProfileProperties());
               map.putAll(this.sessionService.getTextures(p_152790_1_, false));
            } else {
               this.sessionService.fillProfileProperties(p_152790_1_, p_152790_3_);

               try {
                  map.putAll(this.sessionService.getTextures(p_152790_1_, p_152790_3_));
               } catch (InsecureTextureException insecuretextureexception) {
               }
            }
         }

         Minecraft.getInstance().execute(() -> {
            RenderSystem.recordRenderCall(() -> {
               ImmutableList.of(Type.SKIN, Type.CAPE).forEach((p_229296_3_) -> {
                  if (map.containsKey(p_229296_3_)) {
                     this.registerTexture(map.get(p_229296_3_), p_229296_3_, p_152790_2_);
                  }

               });
            });
         });
      };
      Util.backgroundExecutor().execute(runnable);
   }

   public Map<Type, MinecraftProfileTexture> getInsecureSkinInformation(GameProfile p_152788_1_) {
      Property property = Iterables.getFirst(p_152788_1_.getProperties().get("textures"), (Property)null);
      return (Map<Type, MinecraftProfileTexture>)(property == null ? ImmutableMap.of() : this.insecureSkinCache.getUnchecked(property.getValue()));
   }

   @OnlyIn(Dist.CLIENT)
   public interface ISkinAvailableCallback {
      void onSkinTextureAvailable(Type p_onSkinTextureAvailable_1_, ResourceLocation p_onSkinTextureAvailable_2_, MinecraftProfileTexture p_onSkinTextureAvailable_3_);
   }
}
