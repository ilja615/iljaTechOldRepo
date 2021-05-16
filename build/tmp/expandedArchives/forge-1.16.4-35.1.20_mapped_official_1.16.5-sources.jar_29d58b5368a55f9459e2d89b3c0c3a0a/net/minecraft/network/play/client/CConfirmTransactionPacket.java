package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CConfirmTransactionPacket implements IPacket<IServerPlayNetHandler> {
   private int containerId;
   private short uid;
   private boolean accepted;

   public CConfirmTransactionPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CConfirmTransactionPacket(int p_i46884_1_, short p_i46884_2_, boolean p_i46884_3_) {
      this.containerId = p_i46884_1_;
      this.uid = p_i46884_2_;
      this.accepted = p_i46884_3_;
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleContainerAck(this);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.containerId = p_148837_1_.readByte();
      this.uid = p_148837_1_.readShort();
      this.accepted = p_148837_1_.readByte() != 0;
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.containerId);
      p_148840_1_.writeShort(this.uid);
      p_148840_1_.writeByte(this.accepted ? 1 : 0);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public short getUid() {
      return this.uid;
   }
}
