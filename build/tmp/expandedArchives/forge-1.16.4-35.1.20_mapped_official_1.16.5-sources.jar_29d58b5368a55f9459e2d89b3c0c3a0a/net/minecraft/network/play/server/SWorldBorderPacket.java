package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.border.WorldBorder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SWorldBorderPacket implements IPacket<IClientPlayNetHandler> {
   private SWorldBorderPacket.Action type;
   private int newAbsoluteMaxSize;
   private double newCenterX;
   private double newCenterZ;
   private double newSize;
   private double oldSize;
   private long lerpTime;
   private int warningTime;
   private int warningBlocks;

   public SWorldBorderPacket() {
   }

   public SWorldBorderPacket(WorldBorder p_i46921_1_, SWorldBorderPacket.Action p_i46921_2_) {
      this.type = p_i46921_2_;
      this.newCenterX = p_i46921_1_.getCenterX();
      this.newCenterZ = p_i46921_1_.getCenterZ();
      this.oldSize = p_i46921_1_.getSize();
      this.newSize = p_i46921_1_.getLerpTarget();
      this.lerpTime = p_i46921_1_.getLerpRemainingTime();
      this.newAbsoluteMaxSize = p_i46921_1_.getAbsoluteMaxSize();
      this.warningBlocks = p_i46921_1_.getWarningBlocks();
      this.warningTime = p_i46921_1_.getWarningTime();
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.type = p_148837_1_.readEnum(SWorldBorderPacket.Action.class);
      switch(this.type) {
      case SET_SIZE:
         this.newSize = p_148837_1_.readDouble();
         break;
      case LERP_SIZE:
         this.oldSize = p_148837_1_.readDouble();
         this.newSize = p_148837_1_.readDouble();
         this.lerpTime = p_148837_1_.readVarLong();
         break;
      case SET_CENTER:
         this.newCenterX = p_148837_1_.readDouble();
         this.newCenterZ = p_148837_1_.readDouble();
         break;
      case SET_WARNING_BLOCKS:
         this.warningBlocks = p_148837_1_.readVarInt();
         break;
      case SET_WARNING_TIME:
         this.warningTime = p_148837_1_.readVarInt();
         break;
      case INITIALIZE:
         this.newCenterX = p_148837_1_.readDouble();
         this.newCenterZ = p_148837_1_.readDouble();
         this.oldSize = p_148837_1_.readDouble();
         this.newSize = p_148837_1_.readDouble();
         this.lerpTime = p_148837_1_.readVarLong();
         this.newAbsoluteMaxSize = p_148837_1_.readVarInt();
         this.warningBlocks = p_148837_1_.readVarInt();
         this.warningTime = p_148837_1_.readVarInt();
      }

   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnum(this.type);
      switch(this.type) {
      case SET_SIZE:
         p_148840_1_.writeDouble(this.newSize);
         break;
      case LERP_SIZE:
         p_148840_1_.writeDouble(this.oldSize);
         p_148840_1_.writeDouble(this.newSize);
         p_148840_1_.writeVarLong(this.lerpTime);
         break;
      case SET_CENTER:
         p_148840_1_.writeDouble(this.newCenterX);
         p_148840_1_.writeDouble(this.newCenterZ);
         break;
      case SET_WARNING_BLOCKS:
         p_148840_1_.writeVarInt(this.warningBlocks);
         break;
      case SET_WARNING_TIME:
         p_148840_1_.writeVarInt(this.warningTime);
         break;
      case INITIALIZE:
         p_148840_1_.writeDouble(this.newCenterX);
         p_148840_1_.writeDouble(this.newCenterZ);
         p_148840_1_.writeDouble(this.oldSize);
         p_148840_1_.writeDouble(this.newSize);
         p_148840_1_.writeVarLong(this.lerpTime);
         p_148840_1_.writeVarInt(this.newAbsoluteMaxSize);
         p_148840_1_.writeVarInt(this.warningBlocks);
         p_148840_1_.writeVarInt(this.warningTime);
      }

   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSetBorder(this);
   }

   @OnlyIn(Dist.CLIENT)
   public void applyChanges(WorldBorder p_179788_1_) {
      switch(this.type) {
      case SET_SIZE:
         p_179788_1_.setSize(this.newSize);
         break;
      case LERP_SIZE:
         p_179788_1_.lerpSizeBetween(this.oldSize, this.newSize, this.lerpTime);
         break;
      case SET_CENTER:
         p_179788_1_.setCenter(this.newCenterX, this.newCenterZ);
         break;
      case SET_WARNING_BLOCKS:
         p_179788_1_.setWarningBlocks(this.warningBlocks);
         break;
      case SET_WARNING_TIME:
         p_179788_1_.setWarningTime(this.warningTime);
         break;
      case INITIALIZE:
         p_179788_1_.setCenter(this.newCenterX, this.newCenterZ);
         if (this.lerpTime > 0L) {
            p_179788_1_.lerpSizeBetween(this.oldSize, this.newSize, this.lerpTime);
         } else {
            p_179788_1_.setSize(this.newSize);
         }

         p_179788_1_.setAbsoluteMaxSize(this.newAbsoluteMaxSize);
         p_179788_1_.setWarningBlocks(this.warningBlocks);
         p_179788_1_.setWarningTime(this.warningTime);
      }

   }

   public static enum Action {
      SET_SIZE,
      LERP_SIZE,
      SET_CENTER,
      INITIALIZE,
      SET_WARNING_TIME,
      SET_WARNING_BLOCKS;
   }
}
