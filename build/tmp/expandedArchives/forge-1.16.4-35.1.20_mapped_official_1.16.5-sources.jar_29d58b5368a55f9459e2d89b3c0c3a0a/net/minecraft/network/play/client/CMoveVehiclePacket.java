package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

public class CMoveVehiclePacket implements IPacket<IServerPlayNetHandler> {
   private double x;
   private double y;
   private double z;
   private float yRot;
   private float xRot;

   public CMoveVehiclePacket() {
   }

   public CMoveVehiclePacket(Entity p_i46874_1_) {
      this.x = p_i46874_1_.getX();
      this.y = p_i46874_1_.getY();
      this.z = p_i46874_1_.getZ();
      this.yRot = p_i46874_1_.yRot;
      this.xRot = p_i46874_1_.xRot;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.x = p_148837_1_.readDouble();
      this.y = p_148837_1_.readDouble();
      this.z = p_148837_1_.readDouble();
      this.yRot = p_148837_1_.readFloat();
      this.xRot = p_148837_1_.readFloat();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeDouble(this.x);
      p_148840_1_.writeDouble(this.y);
      p_148840_1_.writeDouble(this.z);
      p_148840_1_.writeFloat(this.yRot);
      p_148840_1_.writeFloat(this.xRot);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleMoveVehicle(this);
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public float getYRot() {
      return this.yRot;
   }

   public float getXRot() {
      return this.xRot;
   }
}
