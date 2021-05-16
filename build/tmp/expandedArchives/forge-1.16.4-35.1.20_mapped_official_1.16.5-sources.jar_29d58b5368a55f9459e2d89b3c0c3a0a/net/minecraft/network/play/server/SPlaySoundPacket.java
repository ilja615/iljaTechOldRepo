package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPlaySoundPacket implements IPacket<IClientPlayNetHandler> {
   private ResourceLocation name;
   private SoundCategory source;
   private int x;
   private int y = Integer.MAX_VALUE;
   private int z;
   private float volume;
   private float pitch;

   public SPlaySoundPacket() {
   }

   public SPlaySoundPacket(ResourceLocation p_i47939_1_, SoundCategory p_i47939_2_, Vector3d p_i47939_3_, float p_i47939_4_, float p_i47939_5_) {
      this.name = p_i47939_1_;
      this.source = p_i47939_2_;
      this.x = (int)(p_i47939_3_.x * 8.0D);
      this.y = (int)(p_i47939_3_.y * 8.0D);
      this.z = (int)(p_i47939_3_.z * 8.0D);
      this.volume = p_i47939_4_;
      this.pitch = p_i47939_5_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.name = p_148837_1_.readResourceLocation();
      this.source = p_148837_1_.readEnum(SoundCategory.class);
      this.x = p_148837_1_.readInt();
      this.y = p_148837_1_.readInt();
      this.z = p_148837_1_.readInt();
      this.volume = p_148837_1_.readFloat();
      this.pitch = p_148837_1_.readFloat();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeResourceLocation(this.name);
      p_148840_1_.writeEnum(this.source);
      p_148840_1_.writeInt(this.x);
      p_148840_1_.writeInt(this.y);
      p_148840_1_.writeInt(this.z);
      p_148840_1_.writeFloat(this.volume);
      p_148840_1_.writeFloat(this.pitch);
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getName() {
      return this.name;
   }

   @OnlyIn(Dist.CLIENT)
   public SoundCategory getSource() {
      return this.source;
   }

   @OnlyIn(Dist.CLIENT)
   public double getX() {
      return (double)((float)this.x / 8.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public double getY() {
      return (double)((float)this.y / 8.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public double getZ() {
      return (double)((float)this.z / 8.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public float getVolume() {
      return this.volume;
   }

   @OnlyIn(Dist.CLIENT)
   public float getPitch() {
      return this.pitch;
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleCustomSoundEvent(this);
   }
}
