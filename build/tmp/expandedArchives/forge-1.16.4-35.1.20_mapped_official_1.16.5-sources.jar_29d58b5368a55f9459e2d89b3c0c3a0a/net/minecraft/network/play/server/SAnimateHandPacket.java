package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SAnimateHandPacket implements IPacket<IClientPlayNetHandler> {
   private int id;
   private int action;

   public SAnimateHandPacket() {
   }

   public SAnimateHandPacket(Entity p_i46970_1_, int p_i46970_2_) {
      this.id = p_i46970_1_.getId();
      this.action = p_i46970_2_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.id = p_148837_1_.readVarInt();
      this.action = p_148837_1_.readUnsignedByte();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.id);
      p_148840_1_.writeByte(this.action);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleAnimate(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getId() {
      return this.id;
   }

   @OnlyIn(Dist.CLIENT)
   public int getAction() {
      return this.action;
   }
}
