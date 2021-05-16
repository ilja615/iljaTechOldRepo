package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SDisconnectPacket implements IPacket<IClientPlayNetHandler> {
   private ITextComponent reason;

   public SDisconnectPacket() {
   }

   public SDisconnectPacket(ITextComponent p_i46947_1_) {
      this.reason = p_i46947_1_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.reason = p_148837_1_.readComponent();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeComponent(this.reason);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleDisconnect(this);
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getReason() {
      return this.reason;
   }
}
