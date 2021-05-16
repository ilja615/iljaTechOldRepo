package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

public class CRenameItemPacket implements IPacket<IServerPlayNetHandler> {
   private String name;

   public CRenameItemPacket() {
   }

   public CRenameItemPacket(String p_i49546_1_) {
      this.name = p_i49546_1_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.name = p_148837_1_.readUtf(32767);
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeUtf(this.name);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleRenameItem(this);
   }

   public String getName() {
      return this.name;
   }
}
