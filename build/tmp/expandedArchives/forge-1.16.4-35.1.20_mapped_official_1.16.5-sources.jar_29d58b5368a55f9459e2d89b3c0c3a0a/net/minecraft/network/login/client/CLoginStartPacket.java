package net.minecraft.network.login.client;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.IServerLoginNetHandler;

public class CLoginStartPacket implements IPacket<IServerLoginNetHandler> {
   private GameProfile gameProfile;

   public CLoginStartPacket() {
   }

   public CLoginStartPacket(GameProfile p_i46852_1_) {
      this.gameProfile = p_i46852_1_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.gameProfile = new GameProfile((UUID)null, p_148837_1_.readUtf(16));
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeUtf(this.gameProfile.getName());
   }

   public void handle(IServerLoginNetHandler p_148833_1_) {
      p_148833_1_.handleHello(this);
   }

   public GameProfile getGameProfile() {
      return this.gameProfile;
   }
}
