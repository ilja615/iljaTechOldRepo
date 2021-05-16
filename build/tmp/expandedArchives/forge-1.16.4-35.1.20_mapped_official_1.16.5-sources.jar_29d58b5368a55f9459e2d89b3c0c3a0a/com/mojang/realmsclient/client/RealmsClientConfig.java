package com.mojang.realmsclient.client;

import java.net.Proxy;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsClientConfig {
   private static Proxy proxy;

   public static Proxy getProxy() {
      return proxy;
   }

   public static void setProxy(Proxy p_224896_0_) {
      if (proxy == null) {
         proxy = p_224896_0_;
      }

   }
}
