package net.minecraft.client.network;

import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LanServerInfo {
   private final String motd;
   private final String address;
   private long pingTime;

   public LanServerInfo(String p_i47130_1_, String p_i47130_2_) {
      this.motd = p_i47130_1_;
      this.address = p_i47130_2_;
      this.pingTime = Util.getMillis();
   }

   public String getMotd() {
      return this.motd;
   }

   public String getAddress() {
      return this.address;
   }

   public void updatePingTime() {
      this.pingTime = Util.getMillis();
   }
}
