package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CEnchantItemPacket implements IPacket<IServerPlayNetHandler> {
   private int containerId;
   private int buttonId;

   public CEnchantItemPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CEnchantItemPacket(int p_i46883_1_, int p_i46883_2_) {
      this.containerId = p_i46883_1_;
      this.buttonId = p_i46883_2_;
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleContainerButtonClick(this);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.containerId = p_148837_1_.readByte();
      this.buttonId = p_148837_1_.readByte();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.containerId);
      p_148840_1_.writeByte(this.buttonId);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public int getButtonId() {
      return this.buttonId;
   }
}
