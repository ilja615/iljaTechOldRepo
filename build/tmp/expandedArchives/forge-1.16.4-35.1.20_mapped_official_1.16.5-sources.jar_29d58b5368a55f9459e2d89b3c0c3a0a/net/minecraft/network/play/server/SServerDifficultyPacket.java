package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.Difficulty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SServerDifficultyPacket implements IPacket<IClientPlayNetHandler> {
   private Difficulty difficulty;
   private boolean locked;

   public SServerDifficultyPacket() {
   }

   public SServerDifficultyPacket(Difficulty p_i46963_1_, boolean p_i46963_2_) {
      this.difficulty = p_i46963_1_;
      this.locked = p_i46963_2_;
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleChangeDifficulty(this);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.difficulty = Difficulty.byId(p_148837_1_.readUnsignedByte());
      this.locked = p_148837_1_.readBoolean();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.difficulty.getId());
      p_148840_1_.writeBoolean(this.locked);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isLocked() {
      return this.locked;
   }

   @OnlyIn(Dist.CLIENT)
   public Difficulty getDifficulty() {
      return this.difficulty;
   }
}
