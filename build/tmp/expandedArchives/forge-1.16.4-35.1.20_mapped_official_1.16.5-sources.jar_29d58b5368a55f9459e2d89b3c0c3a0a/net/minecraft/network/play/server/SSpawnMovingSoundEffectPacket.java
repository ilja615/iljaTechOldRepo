package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;

public class SSpawnMovingSoundEffectPacket implements IPacket<IClientPlayNetHandler> {
   private SoundEvent sound;
   private SoundCategory source;
   private int id;
   private float volume;
   private float pitch;

   public SSpawnMovingSoundEffectPacket() {
   }

   public SSpawnMovingSoundEffectPacket(SoundEvent p_i50763_1_, SoundCategory p_i50763_2_, Entity p_i50763_3_, float p_i50763_4_, float p_i50763_5_) {
      Validate.notNull(p_i50763_1_, "sound");
      this.sound = p_i50763_1_;
      this.source = p_i50763_2_;
      this.id = p_i50763_3_.getId();
      this.volume = p_i50763_4_;
      this.pitch = p_i50763_5_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.sound = Registry.SOUND_EVENT.byId(p_148837_1_.readVarInt());
      this.source = p_148837_1_.readEnum(SoundCategory.class);
      this.id = p_148837_1_.readVarInt();
      this.volume = p_148837_1_.readFloat();
      this.pitch = p_148837_1_.readFloat();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(Registry.SOUND_EVENT.getId(this.sound));
      p_148840_1_.writeEnum(this.source);
      p_148840_1_.writeVarInt(this.id);
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
   public int getId() {
      return this.id;
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
      p_148833_1_.handleSoundEntityEvent(this);
   }
}
