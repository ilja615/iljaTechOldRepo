package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.world.Difficulty;

public class CSetDifficultyPacket implements IPacket<IServerPlayNetHandler> {
   private Difficulty difficulty;

   public CSetDifficultyPacket() {
   }

   public CSetDifficultyPacket(Difficulty p_i50762_1_) {
      this.difficulty = p_i50762_1_;
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleChangeDifficulty(this);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.difficulty = Difficulty.byId(p_148837_1_.readUnsignedByte());
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.difficulty.getId());
   }

   public Difficulty getDifficulty() {
      return this.difficulty;
   }
}
