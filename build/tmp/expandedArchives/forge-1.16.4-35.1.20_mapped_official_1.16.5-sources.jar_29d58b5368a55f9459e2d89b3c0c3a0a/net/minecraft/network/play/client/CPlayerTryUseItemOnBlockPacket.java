package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPlayerTryUseItemOnBlockPacket implements IPacket<IServerPlayNetHandler> {
   private BlockRayTraceResult blockHit;
   private Hand hand;

   public CPlayerTryUseItemOnBlockPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPlayerTryUseItemOnBlockPacket(Hand p_i50756_1_, BlockRayTraceResult p_i50756_2_) {
      this.hand = p_i50756_1_;
      this.blockHit = p_i50756_2_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.hand = p_148837_1_.readEnum(Hand.class);
      this.blockHit = p_148837_1_.readBlockHitResult();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnum(this.hand);
      p_148840_1_.writeBlockHitResult(this.blockHit);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleUseItemOn(this);
   }

   public Hand getHand() {
      return this.hand;
   }

   public BlockRayTraceResult getHitResult() {
      return this.blockHit;
   }
}
