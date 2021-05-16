package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SConfirmTransactionPacket implements IPacket<IClientPlayNetHandler> {
   private int containerId;
   private short uid;
   private boolean accepted;

   public SConfirmTransactionPacket() {
   }

   public SConfirmTransactionPacket(int p_i46958_1_, short p_i46958_2_, boolean p_i46958_3_) {
      this.containerId = p_i46958_1_;
      this.uid = p_i46958_2_;
      this.accepted = p_i46958_3_;
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleContainerAck(this);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.containerId = p_148837_1_.readUnsignedByte();
      this.uid = p_148837_1_.readShort();
      this.accepted = p_148837_1_.readBoolean();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.containerId);
      p_148840_1_.writeShort(this.uid);
      p_148840_1_.writeBoolean(this.accepted);
   }

   @OnlyIn(Dist.CLIENT)
   public int getContainerId() {
      return this.containerId;
   }

   @OnlyIn(Dist.CLIENT)
   public short getUid() {
      return this.uid;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isAccepted() {
      return this.accepted;
   }
}
