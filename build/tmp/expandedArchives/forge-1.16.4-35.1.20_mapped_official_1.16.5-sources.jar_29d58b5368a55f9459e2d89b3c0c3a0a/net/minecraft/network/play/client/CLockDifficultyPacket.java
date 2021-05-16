package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CLockDifficultyPacket implements IPacket<IServerPlayNetHandler> {
   private boolean locked;

   public CLockDifficultyPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CLockDifficultyPacket(boolean p_i50760_1_) {
      this.locked = p_i50760_1_;
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleLockDifficulty(this);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.locked = p_148837_1_.readBoolean();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBoolean(this.locked);
   }

   public boolean isLocked() {
      return this.locked;
   }
}
