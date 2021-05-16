package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class NettyPacketDecoder extends ByteToMessageDecoder {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Marker MARKER = MarkerManager.getMarker("PACKET_RECEIVED", NetworkManager.PACKET_MARKER);
   private final PacketDirection flow;

   public NettyPacketDecoder(PacketDirection p_i45999_1_) {
      this.flow = p_i45999_1_;
   }

   protected void decode(ChannelHandlerContext p_decode_1_, ByteBuf p_decode_2_, List<Object> p_decode_3_) throws Exception {
      if (p_decode_2_.readableBytes() != 0) {
         PacketBuffer packetbuffer = new PacketBuffer(p_decode_2_);
         int i = packetbuffer.readVarInt();
         IPacket<?> ipacket = p_decode_1_.channel().attr(NetworkManager.ATTRIBUTE_PROTOCOL).get().createPacket(this.flow, i);
         if (ipacket == null) {
            throw new IOException("Bad packet id " + i);
         } else {
            ipacket.read(packetbuffer);
            if (packetbuffer.readableBytes() > 0) {
               throw new IOException("Packet " + p_decode_1_.channel().attr(NetworkManager.ATTRIBUTE_PROTOCOL).get().getId() + "/" + i + " (" + ipacket.getClass().getSimpleName() + ") was larger than I expected, found " + packetbuffer.readableBytes() + " bytes extra whilst reading packet " + i);
            } else {
               p_decode_3_.add(ipacket);
               if (LOGGER.isDebugEnabled()) {
                  LOGGER.debug(MARKER, " IN: [{}:{}] {}", p_decode_1_.channel().attr(NetworkManager.ATTRIBUTE_PROTOCOL).get(), i, ipacket.getClass().getName());
               }

            }
         }
      }
   }
}
