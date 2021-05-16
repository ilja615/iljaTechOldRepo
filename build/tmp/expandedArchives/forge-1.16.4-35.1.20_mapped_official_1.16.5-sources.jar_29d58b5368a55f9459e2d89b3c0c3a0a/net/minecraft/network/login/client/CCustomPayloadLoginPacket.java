package net.minecraft.network.login.client;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.IServerLoginNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CCustomPayloadLoginPacket implements IPacket<IServerLoginNetHandler>, net.minecraftforge.fml.network.ICustomPacket<CCustomPayloadLoginPacket> {
   private int transactionId;
   private PacketBuffer data;

   public CCustomPayloadLoginPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CCustomPayloadLoginPacket(int p_i49516_1_, @Nullable PacketBuffer p_i49516_2_) {
      this.transactionId = p_i49516_1_;
      this.data = p_i49516_2_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.transactionId = p_148837_1_.readVarInt();
      if (p_148837_1_.readBoolean()) {
         int i = p_148837_1_.readableBytes();
         if (i < 0 || i > 1048576) {
            throw new IOException("Payload may not be larger than 1048576 bytes");
         }

         this.data = new PacketBuffer(p_148837_1_.readBytes(i));
      } else {
         this.data = null;
      }

   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.transactionId);
      if (this.data != null) {
         p_148840_1_.writeBoolean(true);
         p_148840_1_.writeBytes(this.data.copy());
      } else {
         p_148840_1_.writeBoolean(false);
      }

   }

   public void handle(IServerLoginNetHandler p_148833_1_) {
      p_148833_1_.handleCustomQueryPacket(this);
   }
}
