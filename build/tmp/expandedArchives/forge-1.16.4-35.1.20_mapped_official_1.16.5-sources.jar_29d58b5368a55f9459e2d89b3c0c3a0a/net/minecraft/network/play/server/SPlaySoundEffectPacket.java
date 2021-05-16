package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;

public class SPlaySoundEffectPacket implements IPacket<IClientPlayNetHandler> {
   private SoundEvent sound;
   private SoundCategory source;
   private int x;
   private int y;
   private int z;
   private float volume;
   private float pitch;

   public SPlaySoundEffectPacket() {
   }

   public SPlaySoundEffectPacket(SoundEvent p_i46896_1_, SoundCategory p_i46896_2_, double p_i46896_3_, double p_i46896_5_, double p_i46896_7_, float p_i46896_9_, float p_i46896_10_) {
      Validate.notNull(p_i46896_1_, "sound");
      this.sound = p_i46896_1_;
      this.source = p_i46896_2_;
      this.x = (int)(p_i46896_3_ * 8.0D);
      this.y = (int)(p_i46896_5_ * 8.0D);
      this.z = (int)(p_i46896_7_ * 8.0D);
      this.volume = p_i46896_9_;
      this.pitch = p_i46896_10_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.sound = Registry.SOUND_EVENT.byId(p_148837_1_.readVarInt());
      this.source = p_148837_1_.readEnum(SoundCategory.class);
      this.x = p_148837_1_.readInt();
      this.y = p_148837_1_.readInt();
      this.z = p_148837_1_.readInt();
      this.volume = p_148837_1_.readFloat();
      this.pitch = p_148837_1_.readFloat();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(Registry.SOUND_EVENT.getId(this.sound));
      p_148840_1_.writeEnum(this.source);
      p_148840_1_.writeInt(this.x);
      p_148840_1_.writeInt(this.y);
      p_148840_1_.writeInt(this.z);
      p_148840_1_.writeFloat(this.volume);
      p_148840_1_.writeFloat(this.pitch);
   }

   @OnlyIn(Dist.CLIENT)
   public SoundEvent getSound() {
      return this.sound;
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
      p_148833_1_.handleSoundEvent(this);
   }
}
