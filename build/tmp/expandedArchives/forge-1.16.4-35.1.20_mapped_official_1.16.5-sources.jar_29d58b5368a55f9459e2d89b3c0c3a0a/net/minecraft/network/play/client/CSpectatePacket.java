package net.minecraft.network.play.client;

import java.io.IOException;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.world.server.ServerWorld;

public class CSpectatePacket implements IPacket<IServerPlayNetHandler> {
   private UUID uuid;

   public CSpectatePacket() {
   }

   public CSpectatePacket(UUID p_i46859_1_) {
      this.uuid = p_i46859_1_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.uuid = p_148837_1_.readUUID();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeUUID(this.uuid);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleTeleportToEntityPacket(this);
   }

   @Nullable
   public Entity getEntity(ServerWorld p_179727_1_) {
      return p_179727_1_.getEntity(this.uuid);
   }
}
