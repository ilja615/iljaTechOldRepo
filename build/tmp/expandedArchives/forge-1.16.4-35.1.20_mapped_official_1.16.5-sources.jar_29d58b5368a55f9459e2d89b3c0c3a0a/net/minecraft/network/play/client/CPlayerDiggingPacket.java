package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPlayerDiggingPacket implements IPacket<IServerPlayNetHandler> {
   private BlockPos pos;
   private Direction direction;
   private CPlayerDiggingPacket.Action action;

   public CPlayerDiggingPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPlayerDiggingPacket(CPlayerDiggingPacket.Action p_i46871_1_, BlockPos p_i46871_2_, Direction p_i46871_3_) {
      this.action = p_i46871_1_;
      this.pos = p_i46871_2_.immutable();
      this.direction = p_i46871_3_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.action = p_148837_1_.readEnum(CPlayerDiggingPacket.Action.class);
      this.pos = p_148837_1_.readBlockPos();
      this.direction = Direction.from3DDataValue(p_148837_1_.readUnsignedByte());
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnum(this.action);
      p_148840_1_.writeBlockPos(this.pos);
      p_148840_1_.writeByte(this.direction.get3DDataValue());
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handlePlayerAction(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Direction getDirection() {
      return this.direction;
   }

   public CPlayerDiggingPacket.Action getAction() {
      return this.action;
   }

   public static enum Action {
      START_DESTROY_BLOCK,
      ABORT_DESTROY_BLOCK,
      STOP_DESTROY_BLOCK,
      DROP_ALL_ITEMS,
      DROP_ITEM,
      RELEASE_USE_ITEM,
      SWAP_ITEM_WITH_OFFHAND;
   }
}
