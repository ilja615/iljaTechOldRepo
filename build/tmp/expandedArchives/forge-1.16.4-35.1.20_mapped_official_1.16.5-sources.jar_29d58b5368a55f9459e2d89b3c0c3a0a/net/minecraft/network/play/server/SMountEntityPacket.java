package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SMountEntityPacket implements IPacket<IClientPlayNetHandler> {
   private int sourceId;
   private int destId;

   public SMountEntityPacket() {
   }

   public SMountEntityPacket(Entity p_i46916_1_, @Nullable Entity p_i46916_2_) {
      this.sourceId = p_i46916_1_.getId();
      this.destId = p_i46916_2_ != null ? p_i46916_2_.getId() : 0;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.sourceId = p_148837_1_.readInt();
      this.destId = p_148837_1_.readInt();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeInt(this.sourceId);
      p_148840_1_.writeInt(this.destId);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleEntityLinkPacket(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getSourceId() {
      return this.sourceId;
   }

   @OnlyIn(Dist.CLIENT)
   public int getDestId() {
      return this.destId;
   }
}
