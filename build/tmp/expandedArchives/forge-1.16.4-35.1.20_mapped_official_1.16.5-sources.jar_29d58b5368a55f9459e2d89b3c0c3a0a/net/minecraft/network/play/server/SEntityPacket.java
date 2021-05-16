package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SEntityPacket implements IPacket<IClientPlayNetHandler> {
   protected int entityId;
   protected short xa;
   protected short ya;
   protected short za;
   protected byte yRot;
   protected byte xRot;
   protected boolean onGround;
   protected boolean hasRot;
   protected boolean hasPos;

   public static long entityToPacket(double p_218743_0_) {
      return MathHelper.lfloor(p_218743_0_ * 4096.0D);
   }

   @OnlyIn(Dist.CLIENT)
   public static double packetToEntity(long p_244299_0_) {
      return (double)p_244299_0_ / 4096.0D;
   }

   @OnlyIn(Dist.CLIENT)
   public Vector3d updateEntityPosition(Vector3d p_244300_1_) {
      double d0 = this.xa == 0 ? p_244300_1_.x : packetToEntity(entityToPacket(p_244300_1_.x) + (long)this.xa);
      double d1 = this.ya == 0 ? p_244300_1_.y : packetToEntity(entityToPacket(p_244300_1_.y) + (long)this.ya);
      double d2 = this.za == 0 ? p_244300_1_.z : packetToEntity(entityToPacket(p_244300_1_.z) + (long)this.za);
      return new Vector3d(d0, d1, d2);
   }

   public static Vector3d packetToEntity(long p_218744_0_, long p_218744_2_, long p_218744_4_) {
      return (new Vector3d((double)p_218744_0_, (double)p_218744_2_, (double)p_218744_4_)).scale((double)2.4414062E-4F);
   }

   public SEntityPacket() {
   }

   public SEntityPacket(int p_i46936_1_) {
      this.entityId = p_i46936_1_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleMoveEntity(this);
   }

   public String toString() {
      return "Entity_" + super.toString();
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Entity getEntity(World p_149065_1_) {
      return p_149065_1_.getEntity(this.entityId);
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
   public boolean hasRotation() {
      return this.hasRot;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasPosition() {
      return this.hasPos;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isOnGround() {
      return this.onGround;
   }

   public static class LookPacket extends SEntityPacket {
      public LookPacket() {
         this.hasRot = true;
      }

      public LookPacket(int p_i47081_1_, byte p_i47081_2_, byte p_i47081_3_, boolean p_i47081_4_) {
         super(p_i47081_1_);
         this.yRot = p_i47081_2_;
         this.xRot = p_i47081_3_;
         this.hasRot = true;
         this.onGround = p_i47081_4_;
      }

      public void read(PacketBuffer p_148837_1_) throws IOException {
         super.read(p_148837_1_);
         this.yRot = p_148837_1_.readByte();
         this.xRot = p_148837_1_.readByte();
         this.onGround = p_148837_1_.readBoolean();
      }

      public void write(PacketBuffer p_148840_1_) throws IOException {
         super.write(p_148840_1_);
         p_148840_1_.writeByte(this.yRot);
         p_148840_1_.writeByte(this.xRot);
         p_148840_1_.writeBoolean(this.onGround);
      }
   }

   public static class MovePacket extends SEntityPacket {
      public MovePacket() {
         this.hasRot = true;
         this.hasPos = true;
      }

      public MovePacket(int p_i49988_1_, short p_i49988_2_, short p_i49988_3_, short p_i49988_4_, byte p_i49988_5_, byte p_i49988_6_, boolean p_i49988_7_) {
         super(p_i49988_1_);
         this.xa = p_i49988_2_;
         this.ya = p_i49988_3_;
         this.za = p_i49988_4_;
         this.yRot = p_i49988_5_;
         this.xRot = p_i49988_6_;
         this.onGround = p_i49988_7_;
         this.hasRot = true;
         this.hasPos = true;
      }

      public void read(PacketBuffer p_148837_1_) throws IOException {
         super.read(p_148837_1_);
         this.xa = p_148837_1_.readShort();
         this.ya = p_148837_1_.readShort();
         this.za = p_148837_1_.readShort();
         this.yRot = p_148837_1_.readByte();
         this.xRot = p_148837_1_.readByte();
         this.onGround = p_148837_1_.readBoolean();
      }

      public void write(PacketBuffer p_148840_1_) throws IOException {
         super.write(p_148840_1_);
         p_148840_1_.writeShort(this.xa);
         p_148840_1_.writeShort(this.ya);
         p_148840_1_.writeShort(this.za);
         p_148840_1_.writeByte(this.yRot);
         p_148840_1_.writeByte(this.xRot);
         p_148840_1_.writeBoolean(this.onGround);
      }
   }

   public static class RelativeMovePacket extends SEntityPacket {
      public RelativeMovePacket() {
         this.hasPos = true;
      }

      public RelativeMovePacket(int p_i49990_1_, short p_i49990_2_, short p_i49990_3_, short p_i49990_4_, boolean p_i49990_5_) {
         super(p_i49990_1_);
         this.xa = p_i49990_2_;
         this.ya = p_i49990_3_;
         this.za = p_i49990_4_;
         this.onGround = p_i49990_5_;
         this.hasPos = true;
      }

      public void read(PacketBuffer p_148837_1_) throws IOException {
         super.read(p_148837_1_);
         this.xa = p_148837_1_.readShort();
         this.ya = p_148837_1_.readShort();
         this.za = p_148837_1_.readShort();
         this.onGround = p_148837_1_.readBoolean();
      }

      public void write(PacketBuffer p_148840_1_) throws IOException {
         super.write(p_148840_1_);
         p_148840_1_.writeShort(this.xa);
         p_148840_1_.writeShort(this.ya);
         p_148840_1_.writeShort(this.za);
         p_148840_1_.writeBoolean(this.onGround);
      }
   }
}
