package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.client.network.login.IClientLoginNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SDisconnectLoginPacket implements IPacket<IClientLoginNetHandler> {
   private ITextComponent reason;

   public SDisconnectLoginPacket() {
   }

   public SDisconnectLoginPacket(ITextComponent p_i46853_1_) {
      this.reason = p_i46853_1_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.reason = ITextComponent.Serializer.fromJsonLenient(p_148837_1_.readUtf(262144));
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeComponent(this.reason);
   }

   public void handle(IClientLoginNetHandler p_148833_1_) {
      p_148833_1_.handleDisconnect(this);
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getReason() {
      return this.reason;
   }
}
