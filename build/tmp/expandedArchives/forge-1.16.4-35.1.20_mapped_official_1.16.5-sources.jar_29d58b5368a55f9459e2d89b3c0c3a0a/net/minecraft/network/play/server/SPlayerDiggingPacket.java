package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SPlayerDiggingPacket implements IPacket<IClientPlayNetHandler> {
   private static final Logger LOGGER = LogManager.getLogger();
   private BlockPos pos;
   private BlockState state;
   CPlayerDiggingPacket.Action action;
   private boolean allGood;

   public SPlayerDiggingPacket() {
   }

   public SPlayerDiggingPacket(BlockPos p_i226088_1_, BlockState p_i226088_2_, CPlayerDiggingPacket.Action p_i226088_3_, boolean p_i226088_4_, String p_i226088_5_) {
      this.pos = p_i226088_1_.immutable();
      this.state = p_i226088_2_;
      this.action = p_i226088_3_;
      this.allGood = p_i226088_4_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.pos = p_148837_1_.readBlockPos();
      this.state = Block.BLOCK_STATE_REGISTRY.byId(p_148837_1_.readVarInt());
      this.action = p_148837_1_.readEnum(CPlayerDiggingPacket.Action.class);
      this.allGood = p_148837_1_.readBoolean();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.pos);
      p_148840_1_.writeVarInt(Block.getId(this.state));
      p_148840_1_.writeEnum(this.action);
      p_148840_1_.writeBoolean(this.allGood);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleBlockBreakAck(this);
   }

   @OnlyIn(Dist.CLIENT)
   public BlockState getState() {
      return this.state;
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getPos() {
      return this.pos;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean allGood() {
      return this.allGood;
   }

   @OnlyIn(Dist.CLIENT)
   public CPlayerDiggingPacket.Action action() {
      return this.action;
   }
}
