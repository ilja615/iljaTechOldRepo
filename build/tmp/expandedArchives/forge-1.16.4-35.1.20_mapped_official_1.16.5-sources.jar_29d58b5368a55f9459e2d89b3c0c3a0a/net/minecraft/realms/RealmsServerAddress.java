package net.minecraft.realms;

import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsServerAddress {
   private final String host;
   private final int port;

   protected RealmsServerAddress(String p_i1121_1_, int p_i1121_2_) {
      this.host = p_i1121_1_;
      this.port = p_i1121_2_;
   }

   public String getHost() {
      return this.host;
   }

   public int getPort() {
      return this.port;
   }

   public static RealmsServerAddress parseString(String p_231413_0_) {
      ServerAddress serveraddress = ServerAddress.parseString(p_231413_0_);
      return new RealmsServerAddress(serveraddress.getHost(), serveraddress.getPort());
   }
}
