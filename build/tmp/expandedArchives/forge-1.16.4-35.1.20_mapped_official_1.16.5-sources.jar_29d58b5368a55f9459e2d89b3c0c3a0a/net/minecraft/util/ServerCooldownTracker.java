package net.minecraft.util;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.SCooldownPacket;

public class ServerCooldownTracker extends CooldownTracker {
   private final ServerPlayerEntity player;

   public ServerCooldownTracker(ServerPlayerEntity p_i46741_1_) {
      this.player = p_i46741_1_;
   }

   protected void onCooldownStarted(Item p_185140_1_, int p_185140_2_) {
      super.onCooldownStarted(p_185140_1_, p_185140_2_);
      this.player.connection.send(new SCooldownPacket(p_185140_1_, p_185140_2_));
   }

   protected void onCooldownEnded(Item p_185146_1_) {
      super.onCooldownEnded(p_185146_1_);
      this.player.connection.send(new SCooldownPacket(p_185146_1_, 0));
   }
}
