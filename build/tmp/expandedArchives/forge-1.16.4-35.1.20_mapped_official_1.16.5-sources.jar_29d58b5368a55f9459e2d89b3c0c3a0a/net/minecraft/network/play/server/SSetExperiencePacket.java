package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSetExperiencePacket implements IPacket<IClientPlayNetHandler> {
   private float experienceProgress;
   private int totalExperience;
   private int experienceLevel;

   public SSetExperiencePacket() {
   }

   public SSetExperiencePacket(float p_i46912_1_, int p_i46912_2_, int p_i46912_3_) {
      this.experienceProgress = p_i46912_1_;
      this.totalExperience = p_i46912_2_;
      this.experienceLevel = p_i46912_3_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.experienceProgress = p_148837_1_.readFloat();
      this.experienceLevel = p_148837_1_.readVarInt();
      this.totalExperience = p_148837_1_.readVarInt();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeFloat(this.experienceProgress);
      p_148840_1_.writeVarInt(this.experienceLevel);
      p_148840_1_.writeVarInt(this.totalExperience);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSetExperience(this);
   }

   @OnlyIn(Dist.CLIENT)
   public float getExperienceProgress() {
      return this.experienceProgress;
   }

   @OnlyIn(Dist.CLIENT)
   public int getTotalExperience() {
      return this.totalExperience;
   }

   @OnlyIn(Dist.CLIENT)
   public int getExperienceLevel() {
      return this.experienceLevel;
   }
}
