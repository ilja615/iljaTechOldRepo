package net.minecraft.client.network;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.status.IClientStatusNetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.status.client.CPingPacket;
import net.minecraft.network.status.client.CServerQueryPacket;
import net.minecraft.network.status.server.SPongPacket;
import net.minecraft.network.status.server.SServerInfoPacket;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ServerPinger {
   private static final Splitter SPLITTER = Splitter.on('\u0000').limit(6);
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<NetworkManager> connections = Collections.synchronizedList(Lists.newArrayList());

   public void pingServer(final ServerData p_147224_1_, final Runnable p_147224_2_) throws UnknownHostException {
      ServerAddress serveraddress = ServerAddress.parseString(p_147224_1_.ip);
      final NetworkManager networkmanager = NetworkManager.connectToServer(InetAddress.getByName(serveraddress.getHost()), serveraddress.getPort(), false);
      this.connections.add(networkmanager);
      p_147224_1_.motd = new TranslationTextComponent("multiplayer.status.pinging");
      p_147224_1_.ping = -1L;
      p_147224_1_.playerList = null;
      networkmanager.setListener(new IClientStatusNetHandler() {
         private boolean success;
         private boolean receivedPing;
         private long pingStart;

         public void handleStatusResponse(SServerInfoPacket p_147397_1_) {
            if (this.receivedPing) {
               networkmanager.disconnect(new TranslationTextComponent("multiplayer.status.unrequested"));
            } else {
               this.receivedPing = true;
               ServerStatusResponse serverstatusresponse = p_147397_1_.getStatus();
               if (serverstatusresponse.getDescription() != null) {
                  p_147224_1_.motd = serverstatusresponse.getDescription();
               } else {
                  p_147224_1_.motd = StringTextComponent.EMPTY;
               }

               if (serverstatusresponse.getVersion() != null) {
                  p_147224_1_.version = new StringTextComponent(serverstatusresponse.getVersion().getName());
                  p_147224_1_.protocol = serverstatusresponse.getVersion().getProtocol();
               } else {
                  p_147224_1_.version = new TranslationTextComponent("multiplayer.status.old");
                  p_147224_1_.protocol = 0;
               }

               if (serverstatusresponse.getPlayers() != null) {
                  p_147224_1_.status = ServerPinger.formatPlayerCount(serverstatusresponse.getPlayers().getNumPlayers(), serverstatusresponse.getPlayers().getMaxPlayers());
                  List<ITextComponent> list = Lists.newArrayList();
                  if (ArrayUtils.isNotEmpty(serverstatusresponse.getPlayers().getSample())) {
                     for(GameProfile gameprofile : serverstatusresponse.getPlayers().getSample()) {
                        list.add(new StringTextComponent(gameprofile.getName()));
                     }

                     if (serverstatusresponse.getPlayers().getSample().length < serverstatusresponse.getPlayers().getNumPlayers()) {
                        list.add(new TranslationTextComponent("multiplayer.status.and_more", serverstatusresponse.getPlayers().getNumPlayers() - serverstatusresponse.getPlayers().getSample().length));
                     }

                     p_147224_1_.playerList = list;
                  }
               } else {
                  p_147224_1_.status = (new TranslationTextComponent("multiplayer.status.unknown")).withStyle(TextFormatting.DARK_GRAY);
               }

               String s = null;
               if (serverstatusresponse.getFavicon() != null) {
                  String s1 = serverstatusresponse.getFavicon();
                  if (s1.startsWith("data:image/png;base64,")) {
                     s = s1.substring("data:image/png;base64,".length());
                  } else {
                     ServerPinger.LOGGER.error("Invalid server icon (unknown format)");
                  }
               }

               if (!Objects.equals(s, p_147224_1_.getIconB64())) {
                  p_147224_1_.setIconB64(s);
                  p_147224_2_.run();
               }

               net.minecraftforge.fml.client.ClientHooks.processForgeListPingData(serverstatusresponse, p_147224_1_);
               this.pingStart = Util.getMillis();
               networkmanager.send(new CPingPacket(this.pingStart));
               this.success = true;
            }
         }

         public void handlePongResponse(SPongPacket p_147398_1_) {
            long i = this.pingStart;
            long j = Util.getMillis();
            p_147224_1_.ping = j - i;
            networkmanager.disconnect(new TranslationTextComponent("multiplayer.status.finished"));
         }

         public void onDisconnect(ITextComponent p_147231_1_) {
            if (!this.success) {
               ServerPinger.LOGGER.error("Can't ping {}: {}", p_147224_1_.ip, p_147231_1_.getString());
               p_147224_1_.motd = (new TranslationTextComponent("multiplayer.status.cannot_connect")).withStyle(TextFormatting.DARK_RED);
               p_147224_1_.status = StringTextComponent.EMPTY;
               ServerPinger.this.pingLegacyServer(p_147224_1_);
            }

         }

         public NetworkManager getConnection() {
            return networkmanager;
         }
      });

      try {
         networkmanager.send(new CHandshakePacket(serveraddress.getHost(), serveraddress.getPort(), ProtocolType.STATUS));
         networkmanager.send(new CServerQueryPacket());
      } catch (Throwable throwable) {
         LOGGER.error(throwable);
      }

   }

   private void pingLegacyServer(final ServerData p_147225_1_) {
      final ServerAddress serveraddress = ServerAddress.parseString(p_147225_1_.ip);
      (new Bootstrap()).group(NetworkManager.NETWORK_WORKER_GROUP.get()).handler(new ChannelInitializer<Channel>() {
         protected void initChannel(Channel p_initChannel_1_) throws Exception {
            try {
               p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, true);
            } catch (ChannelException channelexception) {
            }

            p_initChannel_1_.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
               public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception {
                  super.channelActive(p_channelActive_1_);
                  ByteBuf bytebuf = Unpooled.buffer();

                  try {
                     bytebuf.writeByte(254);
                     bytebuf.writeByte(1);
                     bytebuf.writeByte(250);
                     char[] achar = "MC|PingHost".toCharArray();
                     bytebuf.writeShort(achar.length);

                     for(char c0 : achar) {
                        bytebuf.writeChar(c0);
                     }

                     bytebuf.writeShort(7 + 2 * serveraddress.getHost().length());
                     bytebuf.writeByte(127);
                     achar = serveraddress.getHost().toCharArray();
                     bytebuf.writeShort(achar.length);

                     for(char c1 : achar) {
                        bytebuf.writeChar(c1);
                     }

                     bytebuf.writeInt(serveraddress.getPort());
                     p_channelActive_1_.channel().writeAndFlush(bytebuf).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                  } finally {
                     bytebuf.release();
                  }

               }

               protected void channelRead0(ChannelHandlerContext p_channelRead0_1_, ByteBuf p_channelRead0_2_) throws Exception {
                  short short1 = p_channelRead0_2_.readUnsignedByte();
                  if (short1 == 255) {
                     String s = new String(p_channelRead0_2_.readBytes(p_channelRead0_2_.readShort() * 2).array(), StandardCharsets.UTF_16BE);
                     String[] astring = Iterables.toArray(ServerPinger.SPLITTER.split(s), String.class);
                     if ("\u00a71".equals(astring[0])) {
                        int i = MathHelper.getInt(astring[1], 0);
                        String s1 = astring[2];
                        String s2 = astring[3];
                        int j = MathHelper.getInt(astring[4], -1);
                        int k = MathHelper.getInt(astring[5], -1);
                        p_147225_1_.protocol = -1;
                        p_147225_1_.version = new StringTextComponent(s1);
                        p_147225_1_.motd = new StringTextComponent(s2);
                        p_147225_1_.status = ServerPinger.formatPlayerCount(j, k);
                     }
                  }

                  p_channelRead0_1_.close();
               }

               public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_) throws Exception {
                  p_exceptionCaught_1_.close();
               }
            });
         }
      }).channel(NioSocketChannel.class).connect(serveraddress.getHost(), serveraddress.getPort());
   }

   private static ITextComponent formatPlayerCount(int p_239171_0_, int p_239171_1_) {
      return (new StringTextComponent(Integer.toString(p_239171_0_))).append((new StringTextComponent("/")).withStyle(TextFormatting.DARK_GRAY)).append(Integer.toString(p_239171_1_)).withStyle(TextFormatting.GRAY);
   }

   public void tick() {
      synchronized(this.connections) {
         Iterator<NetworkManager> iterator = this.connections.iterator();

         while(iterator.hasNext()) {
            NetworkManager networkmanager = iterator.next();
            if (networkmanager.isConnected()) {
               networkmanager.tick();
            } else {
               iterator.remove();
               networkmanager.handleDisconnection();
            }
         }

      }
   }

   public void removeAll() {
      synchronized(this.connections) {
         Iterator<NetworkManager> iterator = this.connections.iterator();

         while(iterator.hasNext()) {
            NetworkManager networkmanager = iterator.next();
            if (networkmanager.isConnected()) {
               iterator.remove();
               networkmanager.disconnect(new TranslationTextComponent("multiplayer.status.cancelled"));
            }
         }

      }
   }
}
