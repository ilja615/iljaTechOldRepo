package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSpawnMobPacket implements IPacket<IClientPlayNetHandler> {
   private int id;
   private UUID uuid;
   private int type;
   private double x;
   private double y;
   private double z;
   private int xd;
   private int yd;
   private int zd;
   private byte yRot;
   private byte xRot;
   private byte yHeadRot;

   public SSpawnMobPacket() {
   }

   public SSpawnMobPacket(LivingEntity p_i46973_1_) {
      this.id = p_i46973_1_.getId();
      this.uuid = p_i46973_1_.getUUID();
      this.type = Registry.ENTITY_TYPE.getId(p_i46973_1_.getType());
      this.x = p_i46973_1_.getX();
      this.y = p_i46973_1_.getY();
      this.z = p_i46973_1_.getZ();
      this.yRot = (byte)((int)(p_i46973_1_.yRot * 256.0F / 360.0F));
      this.xRot = (byte)((int)(p_i46973_1_.xRot * 256.0F / 360.0F));
      this.yHeadRot = (byte)((int)(p_i46973_1_.yHeadRot * 256.0F / 360.0F));
      double d0 = 3.9D;
      Vector3d vector3d = p_i46973_1_.getDeltaMovement();
      double d1 = MathHelper.clamp(vector3d.x, -3.9D, 3.9D);
      double d2 = MathHelper.clamp(vector3d.y, -3.9D, 3.9D);
      double d3 = MathHelper.clamp(vector3d.z, -3.9D, 3.9D);
      this.xd = (int)(d1 * 8000.0D);
      this.yd = (int)(d2 * 8000.0D);
      this.zd = (int)(d3 * 8000.0D);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.id = p_148837_1_.readVarInt();
      this.uuid = p_148837_1_.readUUID();
      this.type = p_148837_1_.readVarInt();
      this.x = p_148837_1_.readDouble();
      this.y = p_148837_1_.readDouble();
      this.z = p_148837_1_.readDouble();
      this.yRot = p_148837_1_.readByte();
      this.xRot = p_148837_1_.readByte();
      this.yHeadRot = p_148837_1_.readByte();
      this.xd = p_148837_1_.readShort();
      this.yd = p_148837_1_.readShort();
      this.zd = p_148837_1_.readShort();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.id);
      p_148840_1_.writeUUID(this.uuid);
      p_148840_1_.writeVarInt(this.type);
      p_148840_1_.writeDouble(this.x);
      p_148840_1_.writeDouble(this.y);
      p_148840_1_.writeDouble(this.z);
      p_148840_1_.writeByte(this.yRot);
      p_148840_1_.writeByte(this.xRot);
      p_148840_1_.writeByte(this.yHeadRot);
      p_148840_1_.writeShort(this.xd);
      p_148840_1_.writeShort(this.yd);
      p_148840_1_.writeShort(this.zd);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleAddMob(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getId() {
      return this.id;
   }

   @OnlyIn(Dist.CLIENT)
   public UUID getUUID() {
      return this.uuid;
   }

   @OnlyIn(Dist.CLIENT)
   public int getType() {
      return this.type;
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
   public int getXd() {
      return this.xd;
   }

   @OnlyIn(Dist.CLIENT)
   public int getYd() {
      return this.yd;
   }

   @OnlyIn(Dist.CLIENT)
   public int getZd() {
      return this.zd;
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
   public byte getyHeadRot() {
      return this.yHeadRot;
   }
}
