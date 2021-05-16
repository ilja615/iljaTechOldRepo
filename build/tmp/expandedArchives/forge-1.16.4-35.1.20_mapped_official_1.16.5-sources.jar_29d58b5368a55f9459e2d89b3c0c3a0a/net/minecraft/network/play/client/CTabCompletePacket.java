package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CTabCompletePacket implements IPacket<IServerPlayNetHandler> {
   private int id;
   private String command;

   public CTabCompletePacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CTabCompletePacket(int p_i47928_1_, String p_i47928_2_) {
      this.id = p_i47928_1_;
      this.command = p_i47928_2_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.id = p_148837_1_.readVarInt();
      this.command = p_148837_1_.readUtf(32500);
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.id);
      p_148840_1_.writeUtf(this.command, 32500);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleCustomCommandSuggestions(this);
   }

   public int getId() {
      return this.id;
   }

   public String getCommand() {
      return this.command;
   }
}
