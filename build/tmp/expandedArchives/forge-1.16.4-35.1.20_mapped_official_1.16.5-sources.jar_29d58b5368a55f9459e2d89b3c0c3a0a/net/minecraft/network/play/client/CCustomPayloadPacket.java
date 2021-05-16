package net.minecraft.network.play.client;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CCustomPayloadPacket implements IPacket<IServerPlayNetHandler>, net.minecraftforge.fml.network.ICustomPacket<CCustomPayloadPacket> {
   public static final ResourceLocation BRAND = new ResourceLocation("brand");
   private ResourceLocation identifier;
   private PacketBuffer data;

   public CCustomPayloadPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CCustomPayloadPacket(ResourceLocation p_i49549_1_, PacketBuffer p_i49549_2_) {
      this.identifier = p_i49549_1_;
      this.data = p_i49549_2_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.identifier = p_148837_1_.readResourceLocation();
      int i = p_148837_1_.readableBytes();
      if (i >= 0 && i <= 32767) {
         this.data = new PacketBuffer(p_148837_1_.readBytes(i));
      } else {
         throw new IOException("Payload may not be larger than 32767 bytes");
      }
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeResourceLocation(this.identifier);
      p_148840_1_.writeBytes((ByteBuf)this.data.copy()); //This may be access multiple times, from multiple threads, lets be safe like the S->C packet
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleCustomPayload(this);
      if (this.data != null) {
         this.data.release();
      }

   }
}
