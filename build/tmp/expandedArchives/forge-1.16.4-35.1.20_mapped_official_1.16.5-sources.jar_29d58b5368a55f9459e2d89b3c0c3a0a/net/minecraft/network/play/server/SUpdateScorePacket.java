package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SUpdateScorePacket implements IPacket<IClientPlayNetHandler> {
   private String owner = "";
   @Nullable
   private String objectiveName;
   private int score;
   private ServerScoreboard.Action method;

   public SUpdateScorePacket() {
   }

   public SUpdateScorePacket(ServerScoreboard.Action p_i47930_1_, @Nullable String p_i47930_2_, String p_i47930_3_, int p_i47930_4_) {
      if (p_i47930_1_ != ServerScoreboard.Action.REMOVE && p_i47930_2_ == null) {
         throw new IllegalArgumentException("Need an objective name");
      } else {
         this.owner = p_i47930_3_;
         this.objectiveName = p_i47930_2_;
         this.score = p_i47930_4_;
         this.method = p_i47930_1_;
      }
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.owner = p_148837_1_.readUtf(40);
      this.method = p_148837_1_.readEnum(ServerScoreboard.Action.class);
      String s = p_148837_1_.readUtf(16);
      this.objectiveName = Objects.equals(s, "") ? null : s;
      if (this.method != ServerScoreboard.Action.REMOVE) {
         this.score = p_148837_1_.readVarInt();
      }

   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeUtf(this.owner);
      p_148840_1_.writeEnum(this.method);
      p_148840_1_.writeUtf(this.objectiveName == null ? "" : this.objectiveName);
      if (this.method != ServerScoreboard.Action.REMOVE) {
         p_148840_1_.writeVarInt(this.score);
      }

   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSetScore(this);
   }

   @OnlyIn(Dist.CLIENT)
   public String getOwner() {
      return this.owner;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public String getObjectiveName() {
      return this.objectiveName;
   }

   @OnlyIn(Dist.CLIENT)
   public int getScore() {
      return this.score;
   }

   @OnlyIn(Dist.CLIENT)
   public ServerScoreboard.Action getMethod() {
      return this.method;
   }
}
