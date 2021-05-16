package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSpawnExperienceOrbPacket implements IPacket<IClientPlayNetHandler> {
   private int id;
   private double x;
   private double y;
   private double z;
   private int value;

   public SSpawnExperienceOrbPacket() {
   }

   public SSpawnExperienceOrbPacket(ExperienceOrbEntity p_i46975_1_) {
      this.id = p_i46975_1_.getId();
      this.x = p_i46975_1_.getX();
      this.y = p_i46975_1_.getY();
      this.z = p_i46975_1_.getZ();
      this.value = p_i46975_1_.getValue();
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.id = p_148837_1_.readVarInt();
      this.x = p_148837_1_.readDouble();
      this.y = p_148837_1_.readDouble();
      this.z = p_148837_1_.readDouble();
      this.value = p_148837_1_.readShort();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.id);
      p_148840_1_.writeDouble(this.x);
      p_148840_1_.writeDouble(this.y);
      p_148840_1_.writeDouble(this.z);
      p_148840_1_.writeShort(this.value);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleAddExperienceOrb(this);
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
   public int getValue() {
      return this.value;
   }
}
