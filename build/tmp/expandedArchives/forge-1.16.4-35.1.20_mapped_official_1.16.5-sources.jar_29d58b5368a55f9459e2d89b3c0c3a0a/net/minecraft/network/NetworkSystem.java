package net.minecraft.network;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.network.handshake.ClientHandshakeNetHandler;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.network.handshake.ServerHandshakeNetHandler;
import net.minecraft.network.play.server.SDisconnectPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LazyValue;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkSystem {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int READ_TIMEOUT = Integer.parseInt(System.getProperty("forge.readTimeout", "30"));
   public static final LazyValue<NioEventLoopGroup> SERVER_EVENT_GROUP = new LazyValue<>(() -> {
      return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Server IO #%d").setDaemon(true).setThreadFactory(net.minecraftforge.fml.common.thread.SidedThreadGroups.SERVER).build());
   });
   public static final LazyValue<EpollEventLoopGroup> SERVER_EPOLL_EVENT_GROUP = new LazyValue<>(() -> {
      return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).setThreadFactory(net.minecraftforge.fml.common.thread.SidedThreadGroups.SERVER).build());
   });
   private final MinecraftServer server;
   public volatile boolean running;
   private final List<ChannelFuture> channels = Collections.synchronizedList(Lists.newArrayList());
   private final List<NetworkManager> connections = Collections.synchronizedList(Lists.newArrayList());

   public NetworkSystem(MinecraftServer p_i45292_1_) {
      this.server = p_i45292_1_;
      this.running = true;
   }

   public void startTcpServerListener(@Nullable InetAddress p_151265_1_, int p_151265_2_) throws IOException {
      if (p_151265_1_ instanceof java.net.Inet6Address) System.setProperty("java.net.preferIPv4Stack", "false");
      synchronized(this.channels) {
         Class<? extends ServerSocketChannel> oclass;
         LazyValue<? extends EventLoopGroup> lazyvalue;
         if (Epoll.isAvailable() && this.server.isEpollEnabled()) {
            oclass = EpollServerSocketChannel.class;
            lazyvalue = SERVER_EPOLL_EVENT_GROUP;
            LOGGER.info("Using epoll channel type");
         } else {
            oclass = NioServerSocketChannel.class;
            lazyvalue = SERVER_EVENT_GROUP;
            LOGGER.info("Using default channel type");
         }

         this.channels.add((new ServerBootstrap()).channel(oclass).childHandler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel p_initChannel_1_) throws Exception {
               try {
                  p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, true);
               } catch (ChannelException channelexception) {
               }

               p_initChannel_1_.pipeline().addLast("timeout", new ReadTimeoutHandler(READ_TIMEOUT)).addLast("legacy_query", new LegacyPingHandler(NetworkSystem.this)).addLast("splitter", new NettyVarint21FrameDecoder()).addLast("decoder", new NettyPacketDecoder(PacketDirection.SERVERBOUND)).addLast("prepender", new NettyVarint21FrameEncoder()).addLast("encoder", new NettyPacketEncoder(PacketDirection.CLIENTBOUND));
               int i = NetworkSystem.this.server.getRateLimitPacketsPerSecond();
               NetworkManager networkmanager = (NetworkManager)(i > 0 ? new RateLimitedNetworkManager(i) : new NetworkManager(PacketDirection.SERVERBOUND));
               NetworkSystem.this.connections.add(networkmanager);
               p_initChannel_1_.pipeline().addLast("packet_handler", networkmanager);
               networkmanager.setListener(new ServerHandshakeNetHandler(NetworkSystem.this.server, networkmanager));
            }
         }).group(lazyvalue.get()).localAddress(p_151265_1_, p_151265_2_).bind().syncUninterruptibly());
      }
   }

   @OnlyIn(Dist.CLIENT)
   public SocketAddress startMemoryChannel() {
      ChannelFuture channelfuture;
      synchronized(this.channels) {
         channelfuture = (new ServerBootstrap()).channel(LocalServerChannel.class).childHandler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel p_initChannel_1_) throws Exception {
               NetworkManager networkmanager = new NetworkManager(PacketDirection.SERVERBOUND);
               networkmanager.setListener(new ClientHandshakeNetHandler(NetworkSystem.this.server, networkmanager));
               NetworkSystem.this.connections.add(networkmanager);
               p_initChannel_1_.pipeline().addLast("packet_handler", networkmanager);
            }
         }).group(SERVER_EVENT_GROUP.get()).localAddress(LocalAddress.ANY).bind().syncUninterruptibly();
         this.channels.add(channelfuture);
      }

      return channelfuture.channel().localAddress();
   }

   public void stop() {
      this.running = false;

      for(ChannelFuture channelfuture : this.channels) {
         try {
            channelfuture.channel().close().sync();
         } catch (InterruptedException interruptedexception) {
            LOGGER.error("Interrupted whilst closing channel");
         }
      }

   }

   public void tick() {
      synchronized(this.connections) {
         Iterator<NetworkManager> iterator = this.connections.iterator();

         while(iterator.hasNext()) {
            NetworkManager networkmanager = iterator.next();
            if (!networkmanager.isConnecting()) {
               if (networkmanager.isConnected()) {
                  try {
                     networkmanager.tick();
                  } catch (Exception exception) {
                     if (networkmanager.isMemoryConnection()) {
                        throw new ReportedException(CrashReport.forThrowable(exception, "Ticking memory connection"));
                     }

                     LOGGER.warn("Failed to handle packet for {}", networkmanager.getRemoteAddress(), exception);
                     ITextComponent itextcomponent = new StringTextComponent("Internal server error");
                     networkmanager.send(new SDisconnectPacket(itextcomponent), (p_210474_2_) -> {
                        networkmanager.disconnect(itextcomponent);
                     });
                     networkmanager.setReadOnly();
                  }
               } else {
                  iterator.remove();
                  networkmanager.handleDisconnection();
               }
            }
         }

      }
   }

   public MinecraftServer getServer() {
      return this.server;
   }
}
