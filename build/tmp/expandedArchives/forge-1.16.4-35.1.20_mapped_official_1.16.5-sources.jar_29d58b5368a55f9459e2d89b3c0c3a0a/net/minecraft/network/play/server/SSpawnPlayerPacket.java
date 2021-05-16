package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSpawnPlayerPacket implements IPacket<IClientPlayNetHandler> {
   private int entityId;
   private UUID playerId;
   private double x;
   private double y;
   private double z;
   private byte yRot;
   private byte xRot;

   public SSpawnPlayerPacket() {
   }

   public SSpawnPlayerPacket(PlayerEntity p_i46971_1_) {
      this.entityId = p_i46971_1_.getId();
      this.playerId = p_i46971_1_.getGameProfile().getId();
      this.x = p_i46971_1_.getX();
      this.y = p_i46971_1_.getY();
      this.z = p_i46971_1_.getZ();
      this.yRot = (byte)((int)(p_i46971_1_.yRot * 256.0F / 360.0F));
      this.xRot = (byte)((int)(p_i46971_1_.xRot * 256.0F / 360.0F));
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
      this.playerId = p_148837_1_.readUUID();
      this.x = p_148837_1_.readDouble();
      this.y = p_148837_1_.readDouble();
      this.z = p_148837_1_.readDouble();
      this.yRot = p_148837_1_.readByte();
      this.xRot = p_148837_1_.readByte();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
      p_148840_1_.writeUUID(this.playerId);
      p_148840_1_.writeDouble(this.x);
      p_148840_1_.writeDouble(this.y);
      p_148840_1_.writeDouble(this.z);
      p_148840_1_.writeByte(this.yRot);
      p_148840_1_.writeByte(this.xRot);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleAddPlayer(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityId() {
      return this.entityId;
   }

   @OnlyIn(Dist.CLIENT)
   public UUID getPlayerId() {
      return this.playerId;
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
}
