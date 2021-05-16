package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPlaySoundEventPacket implements IPacket<IClientPlayNetHandler> {
   private int type;
   private BlockPos pos;
   private int data;
   private boolean globalEvent;

   public SPlaySoundEventPacket() {
   }

   public SPlaySoundEventPacket(int p_i46940_1_, BlockPos p_i46940_2_, int p_i46940_3_, boolean p_i46940_4_) {
      this.type = p_i46940_1_;
      this.pos = p_i46940_2_.immutable();
      this.data = p_i46940_3_;
      this.globalEvent = p_i46940_4_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.type = p_148837_1_.readInt();
      this.pos = p_148837_1_.readBlockPos();
      this.data = p_148837_1_.readInt();
      this.globalEvent = p_148837_1_.readBoolean();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeInt(this.type);
      p_148840_1_.writeBlockPos(this.pos);
      p_148840_1_.writeInt(this.data);
      p_148840_1_.writeBoolean(this.globalEvent);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleLevelEvent(this);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isGlobalEvent() {
      return this.globalEvent;
   }

   @OnlyIn(Dist.CLIENT)
   public int getType() {
      return this.type;
   }

   @OnlyIn(Dist.CLIENT)
   public int getData() {
      return this.data;
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getPos() {
      return this.pos;
   }
}
