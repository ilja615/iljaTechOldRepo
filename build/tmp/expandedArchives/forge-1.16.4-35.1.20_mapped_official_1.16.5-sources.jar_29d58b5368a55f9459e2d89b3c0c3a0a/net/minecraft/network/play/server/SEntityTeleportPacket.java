package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SEntityTeleportPacket implements IPacket<IClientPlayNetHandler> {
   private int id;
   private double x;
   private double y;
   private double z;
   private byte yRot;
   private byte xRot;
   private boolean onGround;

   public SEntityTeleportPacket() {
   }

   public SEntityTeleportPacket(Entity p_i46893_1_) {
      this.id = p_i46893_1_.getId();
      this.x = p_i46893_1_.getX();
      this.y = p_i46893_1_.getY();
      this.z = p_i46893_1_.getZ();
      this.yRot = (byte)((int)(p_i46893_1_.yRot * 256.0F / 360.0F));
      this.xRot = (byte)((int)(p_i46893_1_.xRot * 256.0F / 360.0F));
      this.onGround = p_i46893_1_.isOnGround();
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.id = p_148837_1_.readVarInt();
      this.x = p_148837_1_.readDouble();
      this.y = p_148837_1_.readDouble();
      this.z = p_148837_1_.readDouble();
      this.yRot = p_148837_1_.readByte();
      this.xRot = p_148837_1_.readByte();
      this.onGround = p_148837_1_.readBoolean();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.id);
      p_148840_1_.writeDouble(this.x);
      p_148840_1_.writeDouble(this.y);
      p_148840_1_.writeDouble(this.z);
      p_148840_1_.writeByte(this.yRot);
      p_148840_1_.writeByte(this.xRot);
      p_148840_1_.writeBoolean(this.onGround);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleTeleportEntity(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getId() {
      return this.id;
   }

   @OnlyIn(Dist.CLIENT)
   public double getX() {
      return this.x;
   }

   @OnlyIn(Dist.CLIENT)
   public double getY() {
      return this.y;
   }

   @OnlyIn(Dist.CLIENT)
   public double getZ() {
      return this.z;
   }

   @OnlyIn(Dist.CLIENT)
   public byte getyRot() {
      return this.yRot;
   }

   @OnlyIn(Dist.CLIENT)
   public byte getxRot() {
      return this.xRot;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isOnGround() {
      return this.onGround;
   }
}
