package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SScoreboardObjectivePacket implements IPacket<IClientPlayNetHandler> {
   private String objectiveName;
   private ITextComponent displayName;
   private ScoreCriteria.RenderType renderType;
   private int method;

   public SScoreboardObjectivePacket() {
   }

   public SScoreboardObjectivePacket(ScoreObjective p_i46910_1_, int p_i46910_2_) {
      this.objectiveName = p_i46910_1_.getName();
      this.displayName = p_i46910_1_.getDisplayName();
      this.renderType = p_i46910_1_.getRenderType();
      this.method = p_i46910_2_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.objectiveName = p_148837_1_.readUtf(16);
      this.method = p_148837_1_.readByte();
      if (this.method == 0 || this.method == 2) {
         this.displayName = p_148837_1_.readComponent();
         this.renderType = p_148837_1_.readEnum(ScoreCriteria.RenderType.class);
      }

   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeUtf(this.objectiveName);
      p_148840_1_.writeByte(this.method);
      if (this.method == 0 || this.method == 2) {
         p_148840_1_.writeComponent(this.displayName);
         p_148840_1_.writeEnum(this.renderType);
      }

   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleAddObjective(this);
   }

   @OnlyIn(Dist.CLIENT)
   public String getObjectiveName() {
      return this.objectiveName;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getDisplayName() {
      return this.displayName;
   }

   @OnlyIn(Dist.CLIENT)
   public int getMethod() {
      return this.method;
   }

   @OnlyIn(Dist.CLIENT)
   public ScoreCriteria.RenderType getRenderType() {
      return this.renderType;
   }
}
