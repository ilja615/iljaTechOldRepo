package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CQueryTileEntityNBTPacket implements IPacket<IServerPlayNetHandler> {
   private int transactionId;
   private BlockPos pos;

   public CQueryTileEntityNBTPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CQueryTileEntityNBTPacket(int p_i49756_1_, BlockPos p_i49756_2_) {
      this.transactionId = p_i49756_1_;
      this.pos = p_i49756_2_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.transactionId = p_148837_1_.readVarInt();
      this.pos = p_148837_1_.readBlockPos();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.transactionId);
      p_148840_1_.writeBlockPos(this.pos);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleBlockEntityTagQuery(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }

   public BlockPos getPos() {
      return this.pos;
   }
}
