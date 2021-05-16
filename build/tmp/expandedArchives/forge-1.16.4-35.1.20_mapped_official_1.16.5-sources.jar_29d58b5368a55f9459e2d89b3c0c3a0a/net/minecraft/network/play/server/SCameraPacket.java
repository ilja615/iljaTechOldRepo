package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SCameraPacket implements IPacket<IClientPlayNetHandler> {
   public int cameraId;

   public SCameraPacket() {
   }

   public SCameraPacket(Entity p_i46920_1_) {
      this.cameraId = p_i46920_1_.getId();
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.cameraId = p_148837_1_.readVarInt();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.cameraId);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSetCamera(this);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Entity getEntity(World p_179780_1_) {
      return p_179780_1_.getEntity(this.cameraId);
   }
}
