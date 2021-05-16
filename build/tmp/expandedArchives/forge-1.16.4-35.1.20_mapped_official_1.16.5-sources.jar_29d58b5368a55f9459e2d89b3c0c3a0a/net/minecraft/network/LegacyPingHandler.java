package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LegacyPingHandler extends ChannelInboundHandlerAdapter {
   private static final Logger LOGGER = LogManager.getLogger();
   private final NetworkSystem serverConnectionListener;

   public LegacyPingHandler(NetworkSystem p_i45286_1_) {
      this.serverConnectionListener = p_i45286_1_;
   }

   public void channelRead(ChannelHandlerContext p_channelRead_1_, Object p_channelRead_2_) throws Exception {
      ByteBuf bytebuf = (ByteBuf)p_channelRead_2_;
      bytebuf.markReaderIndex();
      boolean flag = true;

      try {
         if (bytebuf.readUnsignedByte() == 254) {
            InetSocketAddress inetsocketaddress = (InetSocketAddress)p_channelRead_1_.channel().remoteAddress();
            MinecraftServer minecraftserver = this.serverConnectionListener.getServer();
            int i = bytebuf.readableBytes();
            switch(i) {
            case 0:
               LOGGER.debug("Ping: (<1.3.x) from {}:{}", inetsocketaddress.getAddress(), inetsocketaddress.getPort());
               String s2 = String.format("%s\u00a7%d\u00a7%d", minecraftserver.getMotd(), minecraftserver.getPlayerCount(), minecraftserver.getMaxPlayers());
               this.sendFlushAndClose(p_channelRead_1_, this.createReply(s2));
               break;
            case 1:
               if (bytebuf.readUnsignedByte() != 1) {
                  return;
               }

               LOGGER.debug("Ping: (1.4-1.5.x) from {}:{}", inetsocketaddress.getAddress(), inetsocketaddress.getPort());
               String s = String.format("\u00a71\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", 127, minecraftserver.getServerVersion(), minecraftserver.getMotd(), minecraftserver.getPlayerCount(), minecraftserver.getMaxPlayers());
               this.sendFlushAndClose(p_channelRead_1_, this.createReply(s));
               break;
            default:
               boolean flag1 = bytebuf.readUnsignedByte() == 1;
               flag1 = flag1 & bytebuf.readUnsignedByte() == 250;
               flag1 = flag1 & "MC|PingHost".equals(new String(bytebuf.readBytes(bytebuf.readShort() * 2).array(), StandardCharsets.UTF_16BE));
               int j = bytebuf.readUnsignedShort();
               flag1 = flag1 & bytebuf.readUnsignedByte() >= 73;
               flag1 = flag1 & 3 + bytebuf.readBytes(bytebuf.readShort() * 2).array().length + 4 == j;
               flag1 = flag1 & bytebuf.readInt() <= 65535;
               flag1 = flag1 & bytebuf.readableBytes() == 0;
               if (!flag1) {
                  return;
               }

               LOGGER.debug("Ping: (1.6) from {}:{}", inetsocketaddress.getAddress(), inetsocketaddress.getPort());
               String s1 = String.format("\u00a71\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", 127, minecraftserver.getServerVersion(), minecraftserver.getMotd(), minecraftserver.getPlayerCount(), minecraftserver.getMaxPlayers());
               ByteBuf bytebuf1 = this.createReply(s1);

               try {
                  this.sendFlushAndClose(p_channelRead_1_, bytebuf1);
               } finally {
                  bytebuf1.release();
               }
            }

            bytebuf.release();
            flag = false;
            return;
         }
      } catch (RuntimeException runtimeexception) {
         return;
      } finally {
         if (flag) {
            bytebuf.resetReaderIndex();
            p_channelRead_1_.channel().pipeline().remove("legacy_query");
            p_channelRead_1_.fireChannelRead(p_channelRead_2_);
         }

      }

   }

   private void sendFlushAndClose(ChannelHandlerContext p_151256_1_, ByteBuf p_151256_2_) {
      p_151256_1_.pipeline().firstContext().writeAndFlush(p_151256_2_).addListener(ChannelFutureListener.CLOSE);
   }

   private ByteBuf createReply(String p_151255_1_) {
      ByteBuf bytebuf = Unpooled.buffer();
      bytebuf.writeByte(255);
      char[] achar = p_151255_1_.toCharArray();
      bytebuf.writeShort(achar.length);

      for(char c0 : achar) {
         bytebuf.writeChar(c0);
      }

      return bytebuf;
   }
}
