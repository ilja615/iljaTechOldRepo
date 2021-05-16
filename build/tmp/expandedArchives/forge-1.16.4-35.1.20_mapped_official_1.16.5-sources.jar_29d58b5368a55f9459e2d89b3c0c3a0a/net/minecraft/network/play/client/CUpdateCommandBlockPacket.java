package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CUpdateCommandBlockPacket implements IPacket<IServerPlayNetHandler> {
   private BlockPos pos;
   private String command;
   private boolean trackOutput;
   private boolean conditional;
   private boolean automatic;
   private CommandBlockTileEntity.Mode mode;

   public CUpdateCommandBlockPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CUpdateCommandBlockPacket(BlockPos p_i49543_1_, String p_i49543_2_, CommandBlockTileEntity.Mode p_i49543_3_, boolean p_i49543_4_, boolean p_i49543_5_, boolean p_i49543_6_) {
      this.pos = p_i49543_1_;
      this.command = p_i49543_2_;
      this.trackOutput = p_i49543_4_;
      this.conditional = p_i49543_5_;
      this.automatic = p_i49543_6_;
      this.mode = p_i49543_3_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.pos = p_148837_1_.readBlockPos();
      this.command = p_148837_1_.readUtf(32767);
      this.mode = p_148837_1_.readEnum(CommandBlockTileEntity.Mode.class);
      int i = p_148837_1_.readByte();
      this.trackOutput = (i & 1) != 0;
      this.conditional = (i & 2) != 0;
      this.automatic = (i & 4) != 0;
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.pos);
      p_148840_1_.writeUtf(this.command);
      p_148840_1_.writeEnum(this.mode);
      int i = 0;
      if (this.trackOutput) {
         i |= 1;
      }

      if (this.conditional) {
         i |= 2;
      }

      if (this.automatic) {
         i |= 4;
      }

      p_148840_1_.writeByte(i);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSetCommandBlock(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public String getCommand() {
      return this.command;
   }

   public boolean isTrackOutput() {
      return this.trackOutput;
   }

   public boolean isConditional() {
      return this.conditional;
   }

   public boolean isAutomatic() {
      return this.automatic;
   }

   public CommandBlockTileEntity.Mode getMode() {
      return this.mode;
   }
}
