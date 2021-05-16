package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SUpdateTileEntityPacket implements IPacket<IClientPlayNetHandler> {
   private BlockPos pos;
   private int type;
   private CompoundNBT tag;

   public SUpdateTileEntityPacket() {
   }

   public SUpdateTileEntityPacket(BlockPos p_i46967_1_, int p_i46967_2_, CompoundNBT p_i46967_3_) {
      this.pos = p_i46967_1_;
      this.type = p_i46967_2_;
      this.tag = p_i46967_3_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.pos = p_148837_1_.readBlockPos();
      this.type = p_148837_1_.readUnsignedByte();
      this.tag = p_148837_1_.readNbt();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.pos);
      p_148840_1_.writeByte((byte)this.type);
      p_148840_1_.writeNbt(this.tag);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleBlockEntityData(this);
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getPos() {
      return this.pos;
   }

   @OnlyIn(Dist.CLIENT)
   public int getType() {
      return this.type;
   }

   @OnlyIn(Dist.CLIENT)
   public CompoundNBT getTag() {
      return this.tag;
   }
}
