package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CUpdateSignPacket implements IPacket<IServerPlayNetHandler> {
   private BlockPos pos;
   private String[] lines;

   public CUpdateSignPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CUpdateSignPacket(BlockPos p_i232585_1_, String p_i232585_2_, String p_i232585_3_, String p_i232585_4_, String p_i232585_5_) {
      this.pos = p_i232585_1_;
      this.lines = new String[]{p_i232585_2_, p_i232585_3_, p_i232585_4_, p_i232585_5_};
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.pos = p_148837_1_.readBlockPos();
      this.lines = new String[4];

      for(int i = 0; i < 4; ++i) {
         this.lines[i] = p_148837_1_.readUtf(384);
      }

   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.pos);

      for(int i = 0; i < 4; ++i) {
         p_148840_1_.writeUtf(this.lines[i]);
      }

   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSignUpdate(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public String[] getLines() {
      return this.lines;
   }
}
