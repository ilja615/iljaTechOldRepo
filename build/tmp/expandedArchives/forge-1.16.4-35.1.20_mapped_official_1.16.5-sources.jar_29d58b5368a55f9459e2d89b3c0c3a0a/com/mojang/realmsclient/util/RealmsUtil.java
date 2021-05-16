package com.mojang.realmsclient.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsUtil {
   private static final YggdrasilAuthenticationService AUTHENTICATION_SERVICE = new YggdrasilAuthenticationService(Minecraft.getInstance().getProxy());
   private static final MinecraftSessionService SESSION_SERVICE = AUTHENTICATION_SERVICE.createMinecraftSessionService();
   public static LoadingCache<String, GameProfile> gameProfileCache = CacheBuilder.newBuilder().expireAfterWrite(60L, TimeUnit.MINUTES).build(new CacheLoader<String, GameProfile>() {
      public GameProfile load(String p_load_1_) throws Exception {
         GameProfile gameprofile = RealmsUtil.SESSION_SERVICE.fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(p_load_1_), (String)null), false);
         if (gameprofile == null) {
            throw new Exception("Couldn't get profile");
         } else {
            return gameprofile;
         }
      }
   });

   public static String uuidToName(String p_225193_0_) throws Exception {
      GameProfile gameprofile = gameProfileCache.get(p_225193_0_);
      return gameprofile.getName();
   }

   public static Map<Type, MinecraftProfileTexture> getTextures(String p_225191_0_) {
      try {
         GameProfile gameprofile = gameProfileCache.get(p_225191_0_);
         return SESSION_SERVICE.getTextures(gameprofile, false);
      } catch (Exception exception) {
         return Maps.newHashMap();
      }
   }

   public static String convertToAgePresentation(long p_225192_0_) {
      if (p_225192_0_ < 0L) {
         return "right now";
      } else {
         long i = p_225192_0_ / 1000L;
         if (i < 60L) {
            return (i == 1L ? "1 second" : i + " seconds") + " ago";
         } else if (i < 3600L) {
            long l = i / 60L;
            return (l == 1L ? "1 minute" : l + " minutes") + " ago";
         } else if (i < 86400L) {
            long k = i / 3600L;
            return (k == 1L ? "1 hour" : k + " hours") + " ago";
         } else {
            long j = i / 86400L;
            return (j == 1L ? "1 day" : j + " days") + " ago";
         }
      }
   }

   public static String convertToAgePresentationFromInstant(Date p_238105_0_) {
      return convertToAgePresentation(System.currentTimeMillis() - p_238105_0_.getTime());
   }
}
