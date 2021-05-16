package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SUpdateHealthPacket implements IPacket<IClientPlayNetHandler> {
   private float health;
   private int food;
   private float saturation;

   public SUpdateHealthPacket() {
   }

   public SUpdateHealthPacket(float p_i46911_1_, int p_i46911_2_, float p_i46911_3_) {
      this.health = p_i46911_1_;
      this.food = p_i46911_2_;
      this.saturation = p_i46911_3_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.health = p_148837_1_.readFloat();
      this.food = p_148837_1_.readVarInt();
      this.saturation = p_148837_1_.readFloat();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeFloat(this.health);
      p_148840_1_.writeVarInt(this.food);
      p_148840_1_.writeFloat(this.saturation);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSetHealth(this);
   }

   @OnlyIn(Dist.CLIENT)
   public float getHealth() {
      return this.health;
   }

   @OnlyIn(Dist.CLIENT)
   public int getFood() {
      return this.food;
   }

   @OnlyIn(Dist.CLIENT)
   public float getSaturation() {
      return this.saturation;
   }
}
