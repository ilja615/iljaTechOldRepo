package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SEntityHeadLookPacket implements IPacket<IClientPlayNetHandler> {
   private int entityId;
   private byte yHeadRot;

   public SEntityHeadLookPacket() {
   }

   public SEntityHeadLookPacket(Entity p_i46922_1_, byte p_i46922_2_) {
      this.entityId = p_i46922_1_.getId();
      this.yHeadRot = p_i46922_2_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
      this.yHeadRot = p_148837_1_.readByte();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
      p_148840_1_.writeByte(this.yHeadRot);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleRotateMob(this);
   }

   @OnlyIn(Dist.CLIENT)
   public Entity getEntity(World p_149381_1_) {
      return p_149381_1_.getEntity(this.entityId);
   }

   @OnlyIn(Dist.CLIENT)
   public byte getYHeadRot() {
      return this.yHeadRot;
   }
}
