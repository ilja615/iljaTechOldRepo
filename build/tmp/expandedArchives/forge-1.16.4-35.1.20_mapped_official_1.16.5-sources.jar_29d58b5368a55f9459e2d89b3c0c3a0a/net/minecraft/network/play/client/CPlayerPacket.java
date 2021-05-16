package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPlayerPacket implements IPacket<IServerPlayNetHandler> {
   protected double x;
   protected double y;
   protected double z;
   protected float yRot;
   protected float xRot;
   protected boolean onGround;
   protected boolean hasPos;
   protected boolean hasRot;

   public CPlayerPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPlayerPacket(boolean p_i46875_1_) {
      this.onGround = p_i46875_1_;
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleMovePlayer(this);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.onGround = p_148837_1_.readUnsignedByte() != 0;
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.onGround ? 1 : 0);
   }

   public double getX(double p_186997_1_) {
      return this.hasPos ? this.x : p_186997_1_;
   }

   public double getY(double p_186996_1_) {
      return this.hasPos ? this.y : p_186996_1_;
   }

   public double getZ(double p_187000_1_) {
      return this.hasPos ? this.z : p_187000_1_;
   }

   public float getYRot(float p_186999_1_) {
      return this.hasRot ? this.yRot : p_186999_1_;
   }

   public float getXRot(float p_186998_1_) {
      return this.hasRot ? this.xRot : p_186998_1_;
   }

   public boolean isOnGround() {
      return this.onGround;
   }

   public static class PositionPacket extends CPlayerPacket {
      public PositionPacket() {
         this.hasPos = true;
      }

      @OnlyIn(Dist.CLIENT)
      public PositionPacket(double p_i46867_1_, double p_i46867_3_, double p_i46867_5_, boolean p_i46867_7_) {
         this.x = p_i46867_1_;
         this.y = p_i46867_3_;
         this.z = p_i46867_5_;
         this.onGround = p_i46867_7_;
         this.hasPos = true;
      }

      public void read(PacketBuffer p_148837_1_) throws IOException {
         this.x = p_148837_1_.readDouble();
         this.y = p_148837_1_.readDouble();
         this.z = p_148837_1_.readDouble();
         super.read(p_148837_1_);
      }

      public void write(PacketBuffer p_148840_1_) throws IOException {
         p_148840_1_.writeDouble(this.x);
         p_148840_1_.writeDouble(this.y);
         p_148840_1_.writeDouble(this.z);
         super.write(p_148840_1_);
      }
   }

   public static class PositionRotationPacket extends CPlayerPacket {
      public PositionRotationPacket() {
         this.hasPos = true;
         this.hasRot = true;
      }

      @OnlyIn(Dist.CLIENT)
      public PositionRotationPacket(double p_i46865_1_, double p_i46865_3_, double p_i46865_5_, float p_i46865_7_, float p_i46865_8_, boolean p_i46865_9_) {
         this.x = p_i46865_1_;
         this.y = p_i46865_3_;
         this.z = p_i46865_5_;
         this.yRot = p_i46865_7_;
         this.xRot = p_i46865_8_;
         this.onGround = p_i46865_9_;
         this.hasRot = true;
         this.hasPos = true;
      }

      public void read(PacketBuffer p_148837_1_) throws IOException {
         this.x = p_148837_1_.readDouble();
         this.y = p_148837_1_.readDouble();
         this.z = p_148837_1_.readDouble();
         this.yRot = p_148837_1_.readFloat();
         this.xRot = p_148837_1_.readFloat();
         super.read(p_148837_1_);
      }

      public void write(PacketBuffer p_148840_1_) throws IOException {
         p_148840_1_.writeDouble(this.x);
         p_148840_1_.writeDouble(this.y);
         p_148840_1_.writeDouble(this.z);
         p_148840_1_.writeFloat(this.yRot);
         p_148840_1_.writeFloat(this.xRot);
         super.write(p_148840_1_);
      }
   }

   public static class RotationPacket extends CPlayerPacket {
      public RotationPacket() {
         this.hasRot = true;
      }

      @OnlyIn(Dist.CLIENT)
      public RotationPacket(float p_i46863_1_, float p_i46863_2_, boolean p_i46863_3_) {
         this.yRot = p_i46863_1_;
         this.xRot = p_i46863_2_;
         this.onGround = p_i46863_3_;
         this.hasRot = true;
      }

      public void read(PacketBuffer p_148837_1_) throws IOException {
         this.yRot = p_148837_1_.readFloat();
         this.xRot = p_148837_1_.readFloat();
         super.read(p_148837_1_);
      }

      public void write(PacketBuffer p_148840_1_) throws IOException {
         p_148840_1_.writeFloat(this.yRot);
         p_148840_1_.writeFloat(this.xRot);
         super.write(p_148840_1_);
      }
   }
}
