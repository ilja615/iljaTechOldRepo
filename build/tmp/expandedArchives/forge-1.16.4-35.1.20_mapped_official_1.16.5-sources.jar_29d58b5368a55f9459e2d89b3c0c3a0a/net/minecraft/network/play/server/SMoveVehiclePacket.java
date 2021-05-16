package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SMoveVehiclePacket implements IPacket<IClientPlayNetHandler> {
   private double x;
   private double y;
   private double z;
   private float yRot;
   private float xRot;

   public SMoveVehiclePacket() {
   }

   public SMoveVehiclePacket(Entity p_i46935_1_) {
      this.x = p_i46935_1_.getX();
      this.y = p_i46935_1_.getY();
      this.z = p_i46935_1_.getZ();
      this.yRot = p_i46935_1_.yRot;
      this.xRot = p_i46935_1_.xRot;
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

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleMoveVehicle(this);
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
   public float getYRot() {
      return this.yRot;
   }

   @OnlyIn(Dist.CLIENT)
   public float getXRot() {
      return this.xRot;
   }
}
