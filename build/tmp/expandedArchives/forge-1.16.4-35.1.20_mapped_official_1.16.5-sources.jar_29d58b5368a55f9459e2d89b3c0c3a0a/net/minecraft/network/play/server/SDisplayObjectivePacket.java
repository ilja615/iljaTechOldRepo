package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SDisplayObjectivePacket implements IPacket<IClientPlayNetHandler> {
   private int slot;
   private String objectiveName;

   public SDisplayObjectivePacket() {
   }

   public SDisplayObjectivePacket(int p_i46918_1_, @Nullable ScoreObjective p_i46918_2_) {
      this.slot = p_i46918_1_;
      if (p_i46918_2_ == null) {
         this.objectiveName = "";
      } else {
         this.objectiveName = p_i46918_2_.getName();
      }

   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.slot = p_148837_1_.readByte();
      this.objectiveName = p_148837_1_.readUtf(16);
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.slot);
      p_148840_1_.writeUtf(this.objectiveName);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSetDisplayObjective(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getSlot() {
      return this.slot;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public String getObjectiveName() {
      return Objects.equals(this.objectiveName, "") ? null : this.objectiveName;
   }
}
