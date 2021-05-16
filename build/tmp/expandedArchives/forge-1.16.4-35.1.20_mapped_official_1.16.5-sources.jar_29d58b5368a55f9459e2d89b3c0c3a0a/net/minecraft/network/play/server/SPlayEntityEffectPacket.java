package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPlayEntityEffectPacket implements IPacket<IClientPlayNetHandler> {
   private int entityId;
   private byte effectId;
   private byte effectAmplifier;
   private int effectDurationTicks;
   private byte flags;

   public SPlayEntityEffectPacket() {
   }

   public SPlayEntityEffectPacket(int p_i46891_1_, EffectInstance p_i46891_2_) {
      this.entityId = p_i46891_1_;
      this.effectId = (byte)(Effect.getId(p_i46891_2_.getEffect()) & 255);
      this.effectAmplifier = (byte)(p_i46891_2_.getAmplifier() & 255);
      if (p_i46891_2_.getDuration() > 32767) {
         this.effectDurationTicks = 32767;
      } else {
         this.effectDurationTicks = p_i46891_2_.getDuration();
      }

      this.flags = 0;
      if (p_i46891_2_.isAmbient()) {
         this.flags = (byte)(this.flags | 1);
      }

      if (p_i46891_2_.isVisible()) {
         this.flags = (byte)(this.flags | 2);
      }

      if (p_i46891_2_.showIcon()) {
         this.flags = (byte)(this.flags | 4);
      }

   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
      this.effectId = p_148837_1_.readByte();
      this.effectAmplifier = p_148837_1_.readByte();
      this.effectDurationTicks = p_148837_1_.readVarInt();
      this.flags = p_148837_1_.readByte();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
      p_148840_1_.writeByte(this.effectId);
      p_148840_1_.writeByte(this.effectAmplifier);
      p_148840_1_.writeVarInt(this.effectDurationTicks);
      p_148840_1_.writeByte(this.flags);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isSuperLongDuration() {
      return this.effectDurationTicks == 32767;
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleUpdateMobEffect(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityId() {
      return this.entityId;
   }

   @OnlyIn(Dist.CLIENT)
   public byte getEffectId() {
      return this.effectId;
   }

   @OnlyIn(Dist.CLIENT)
   public byte getEffectAmplifier() {
      return this.effectAmplifier;
   }

   @OnlyIn(Dist.CLIENT)
   public int getEffectDurationTicks() {
      return this.effectDurationTicks;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isEffectVisible() {
      return (this.flags & 2) == 2;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isEffectAmbient() {
      return (this.flags & 1) == 1;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean effectShowsIcon() {
      return (this.flags & 4) == 4;
   }
}
