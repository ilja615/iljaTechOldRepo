package com.mojang.realmsclient.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UploadTokenCache {
   private static final Long2ObjectMap<String> TOKEN_CACHE = new Long2ObjectOpenHashMap<>();

   public static String get(long p_225235_0_) {
      return TOKEN_CACHE.get(p_225235_0_);
   }

   public static void invalidate(long p_225233_0_) {
      TOKEN_CACHE.remove(p_225233_0_);
   }

   public static void put(long p_225234_0_, String p_225234_2_) {
      TOKEN_CACHE.put(p_225234_0_, p_225234_2_);
   }
}
