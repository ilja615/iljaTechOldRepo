package net.minecraft.network.login.server;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.client.network.login.IClientLoginNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.UUIDCodec;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SLoginSuccessPacket implements IPacket<IClientLoginNetHandler> {
   private GameProfile gameProfile;

   public SLoginSuccessPacket() {
   }

   public SLoginSuccessPacket(GameProfile p_i46856_1_) {
      this.gameProfile = p_i46856_1_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      int[] aint = new int[4];

      for(int i = 0; i < aint.length; ++i) {
         aint[i] = p_148837_1_.readInt();
      }

      UUID uuid = UUIDCodec.uuidFromIntArray(aint);
      String s = p_148837_1_.readUtf(16);
      this.gameProfile = new GameProfile(uuid, s);
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      for(int i : UUIDCodec.uuidToIntArray(this.gameProfile.getId())) {
         p_148840_1_.writeInt(i);
      }

      p_148840_1_.writeUtf(this.gameProfile.getName());
   }

   public void handle(IClientLoginNetHandler p_148833_1_) {
      p_148833_1_.handleGameProfile(this);
   }

   @OnlyIn(Dist.CLIENT)
   public GameProfile getGameProfile() {
      return this.gameProfile;
   }
}
