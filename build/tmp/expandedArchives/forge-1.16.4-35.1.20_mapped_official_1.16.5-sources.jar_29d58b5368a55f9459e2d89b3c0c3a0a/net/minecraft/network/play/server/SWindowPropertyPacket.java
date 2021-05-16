package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SWindowPropertyPacket implements IPacket<IClientPlayNetHandler> {
   private int containerId;
   private int id;
   private int value;

   public SWindowPropertyPacket() {
   }

   public SWindowPropertyPacket(int p_i46952_1_, int p_i46952_2_, int p_i46952_3_) {
      this.containerId = p_i46952_1_;
      this.id = p_i46952_2_;
      this.value = p_i46952_3_;
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleContainerSetData(this);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.containerId = p_148837_1_.readUnsignedByte();
      this.id = p_148837_1_.readShort();
      this.value = p_148837_1_.readShort();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.containerId);
      p_148840_1_.writeShort(this.id);
      p_148840_1_.writeShort(this.value);
   }

   @OnlyIn(Dist.CLIENT)
   public int getContainerId() {
      return this.containerId;
   }

   @OnlyIn(Dist.CLIENT)
   public int getId() {
      return this.id;
   }

   @OnlyIn(Dist.CLIENT)
   public int getValue() {
      return this.value;
   }
}
