package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SOpenHorseWindowPacket implements IPacket<IClientPlayNetHandler> {
   private int containerId;
   private int size;
   private int entityId;

   public SOpenHorseWindowPacket() {
   }

   public SOpenHorseWindowPacket(int p_i50776_1_, int p_i50776_2_, int p_i50776_3_) {
      this.containerId = p_i50776_1_;
      this.size = p_i50776_2_;
      this.entityId = p_i50776_3_;
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleHorseScreenOpen(this);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.containerId = p_148837_1_.readUnsignedByte();
      this.size = p_148837_1_.readVarInt();
      this.entityId = p_148837_1_.readInt();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.containerId);
      p_148840_1_.writeVarInt(this.size);
      p_148840_1_.writeInt(this.entityId);
   }

   @OnlyIn(Dist.CLIENT)
   public int getContainerId() {
      return this.containerId;
   }

   @OnlyIn(Dist.CLIENT)
   public int getSize() {
      return this.size;
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityId() {
      return this.entityId;
   }
}
