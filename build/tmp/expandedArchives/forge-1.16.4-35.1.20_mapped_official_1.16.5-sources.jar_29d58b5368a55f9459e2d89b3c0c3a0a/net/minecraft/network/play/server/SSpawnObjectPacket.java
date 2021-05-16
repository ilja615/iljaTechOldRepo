package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSpawnObjectPacket implements IPacket<IClientPlayNetHandler> {
   private int id;
   private UUID uuid;
   private double x;
   private double y;
   private double z;
   private int xa;
   private int ya;
   private int za;
   private int xRot;
   private int yRot;
   private EntityType<?> type;
   private int data;

   public SSpawnObjectPacket() {
   }

   public SSpawnObjectPacket(int p_i50777_1_, UUID p_i50777_2_, double p_i50777_3_, double p_i50777_5_, double p_i50777_7_, float p_i50777_9_, float p_i50777_10_, EntityType<?> p_i50777_11_, int p_i50777_12_, Vector3d p_i50777_13_) {
      this.id = p_i50777_1_;
      this.uuid = p_i50777_2_;
      this.x = p_i50777_3_;
      this.y = p_i50777_5_;
      this.z = p_i50777_7_;
      this.xRot = MathHelper.floor(p_i50777_9_ * 256.0F / 360.0F);
      this.yRot = MathHelper.floor(p_i50777_10_ * 256.0F / 360.0F);
      this.type = p_i50777_11_;
      this.data = p_i50777_12_;
      this.xa = (int)(MathHelper.clamp(p_i50777_13_.x, -3.9D, 3.9D) * 8000.0D);
      this.ya = (int)(MathHelper.clamp(p_i50777_13_.y, -3.9D, 3.9D) * 8000.0D);
      this.za = (int)(MathHelper.clamp(p_i50777_13_.z, -3.9D, 3.9D) * 8000.0D);
   }

   public SSpawnObjectPacket(Entity p_i50778_1_) {
      this(p_i50778_1_, 0);
   }

   public SSpawnObjectPacket(Entity p_i46976_1_, int p_i46976_2_) {
      this(p_i46976_1_.getId(), p_i46976_1_.getUUID(), p_i46976_1_.getX(), p_i46976_1_.getY(), p_i46976_1_.getZ(), p_i46976_1_.xRot, p_i46976_1_.yRot, p_i46976_1_.getType(), p_i46976_2_, p_i46976_1_.getDeltaMovement());
   }

   public SSpawnObjectPacket(Entity p_i50779_1_, EntityType<?> p_i50779_2_, int p_i50779_3_, BlockPos p_i50779_4_) {
      this(p_i50779_1_.getId(), p_i50779_1_.getUUID(), (double)p_i50779_4_.getX(), (double)p_i50779_4_.getY(), (double)p_i50779_4_.getZ(), p_i50779_1_.xRot, p_i50779_1_.yRot, p_i50779_2_, p_i50779_3_, p_i50779_1_.getDeltaMovement());
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.id = p_148837_1_.readVarInt();
      this.uuid = p_148837_1_.readUUID();
      this.type = Registry.ENTITY_TYPE.byId(p_148837_1_.readVarInt());
      this.x = p_148837_1_.readDouble();
      this.y = p_148837_1_.readDouble();
      this.z = p_148837_1_.readDouble();
      this.xRot = p_148837_1_.readByte();
      this.yRot = p_148837_1_.readByte();
      this.data = p_148837_1_.readInt();
      this.xa = p_148837_1_.readShort();
      this.ya = p_148837_1_.readShort();
      this.za = p_148837_1_.readShort();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.id);
      p_148840_1_.writeUUID(this.uuid);
      p_148840_1_.writeVarInt(Registry.ENTITY_TYPE.getId(this.type));
      p_148840_1_.writeDouble(this.x);
      p_148840_1_.writeDouble(this.y);
      p_148840_1_.writeDouble(this.z);
      p_148840_1_.writeByte(this.xRot);
      p_148840_1_.writeByte(this.yRot);
      p_148840_1_.writeInt(this.data);
      p_148840_1_.writeShort(this.xa);
      p_148840_1_.writeShort(this.ya);
      p_148840_1_.writeShort(this.za);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleAddEntity(this);
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
   public double getXa() {
      return (double)this.xa / 8000.0D;
   }

   @OnlyIn(Dist.CLIENT)
   public double getYa() {
      return (double)this.ya / 8000.0D;
   }

   @OnlyIn(Dist.CLIENT)
   public double getZa() {
      return (double)this.za / 8000.0D;
   }

   @OnlyIn(Dist.CLIENT)
   public int getxRot() {
      return this.xRot;
   }

   @OnlyIn(Dist.CLIENT)
   public int getyRot() {
      return this.yRot;
   }

   @OnlyIn(Dist.CLIENT)
   public EntityType<?> getType() {
      return this.type;
   }

   @OnlyIn(Dist.CLIENT)
   public int getData() {
      return this.data;
   }
}
