package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CJigsawBlockGeneratePacket implements IPacket<IServerPlayNetHandler> {
   private BlockPos pos;
   private int levels;
   private boolean keepJigsaws;

   public CJigsawBlockGeneratePacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CJigsawBlockGeneratePacket(BlockPos p_i232583_1_, int p_i232583_2_, boolean p_i232583_3_) {
      this.pos = p_i232583_1_;
      this.levels = p_i232583_2_;
      this.keepJigsaws = p_i232583_3_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.pos = p_148837_1_.readBlockPos();
      this.levels = p_148837_1_.readVarInt();
      this.keepJigsaws = p_148837_1_.readBoolean();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.pos);
      p_148840_1_.writeVarInt(this.levels);
      p_148840_1_.writeBoolean(this.keepJigsaws);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleJigsawGenerate(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public int levels() {
      return this.levels;
   }

   public boolean keepJigsaws() {
      return this.keepJigsaws;
   }
}
