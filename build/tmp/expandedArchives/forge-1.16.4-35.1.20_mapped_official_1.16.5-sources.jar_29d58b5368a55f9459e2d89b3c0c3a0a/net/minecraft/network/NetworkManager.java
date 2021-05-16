package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.server.SDisconnectPacket;
import net.minecraft.util.LazyValue;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class NetworkManager extends SimpleChannelInboundHandler<IPacket<?>> {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Marker ROOT_MARKER = MarkerManager.getMarker("NETWORK");
   public static final Marker PACKET_MARKER = MarkerManager.getMarker("NETWORK_PACKETS", ROOT_MARKER);
   public static final AttributeKey<ProtocolType> ATTRIBUTE_PROTOCOL = AttributeKey.valueOf("protocol");
   public static final LazyValue<NioEventLoopGroup> NETWORK_WORKER_GROUP = new LazyValue<>(() -> {
      return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());
   });
   public static final LazyValue<EpollEventLoopGroup> NETWORK_EPOLL_WORKER_GROUP = new LazyValue<>(() -> {
      return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
   });
   public static final LazyValue<DefaultEventLoopGroup> LOCAL_WORKER_GROUP = new LazyValue<>(() -> {
      return new DefaultEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
   });
   private final PacketDirection receiving;
   private final Queue<NetworkManager.QueuedPacket> queue = Queues.newConcurrentLinkedQueue();
   private Channel channel;
   private SocketAddress address;
   private INetHandler packetListener;
   private ITextComponent disconnectedReason;
   private boolean encrypted;
   private boolean disconnectionHandled;
   private int receivedPackets;
   private int sentPackets;
   private float averageReceivedPackets;
   private float averageSentPackets;
   private int tickCount;
   private boolean handlingFault;
   private java.util.function.Consumer<NetworkManager> activationHandler;

   public NetworkManager(PacketDirection p_i46004_1_) {
      this.receiving = p_i46004_1_;
   }

   public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception {
      super.channelActive(p_channelActive_1_);
      this.channel = p_channelActive_1_.channel();
      this.address = this.channel.remoteAddress();
      if (activationHandler != null) activationHandler.accept(this);

      try {
         this.setProtocol(ProtocolType.HANDSHAKING);
      } catch (Throwable throwable) {
         LOGGER.fatal(throwable);
      }

   }

   public void setProtocol(ProtocolType p_150723_1_) {
      this.channel.attr(ATTRIBUTE_PROTOCOL).set(p_150723_1_);
      this.channel.config().setAutoRead(true);
      LOGGER.debug("Enabled auto read");
   }

   public void channelInactive(ChannelHandlerContext p_channelInactive_1_) throws Exception {
      this.disconnect(new TranslationTextComponent("disconnect.endOfStream"));
   }

   public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_) {
      if (p_exceptionCaught_2_ instanceof SkipableEncoderException) {
         LOGGER.debug("Skipping packet due to errors", p_exceptionCaught_2_.getCause());
      } else {
         boolean flag = !this.handlingFault;
         this.handlingFault = true;
         if (this.channel.isOpen()) {
            if (p_exceptionCaught_2_ instanceof TimeoutException) {
               LOGGER.debug("Timeout", p_exceptionCaught_2_);
               this.disconnect(new TranslationTextComponent("disconnect.timeout"));
            } else {
               ITextComponent itextcomponent = new TranslationTextComponent("disconnect.genericReason", "Internal Exception: " + p_exceptionCaught_2_);
               if (flag) {
                  LOGGER.debug("Failed to sent packet", p_exceptionCaught_2_);
                  this.send(new SDisconnectPacket(itextcomponent), (p_211391_2_) -> {
                     this.disconnect(itextcomponent);
                  });
                  this.setReadOnly();
               } else {
                  LOGGER.debug("Double fault", p_exceptionCaught_2_);
                  this.disconnect(itextcomponent);
               }
            }

         }
      }
   }

   protected void channelRead0(ChannelHandlerContext p_channelRead0_1_, IPacket<?> p_channelRead0_2_) throws Exception {
      if (this.channel.isOpen()) {
         try {
            genericsFtw(p_channelRead0_2_, this.packetListener);
         } catch (ThreadQuickExitException threadquickexitexception) {
         }

         ++this.receivedPackets;
      }

   }

   private static <T extends INetHandler> void genericsFtw(IPacket<T> p_197664_0_, INetHandler p_197664_1_) {
      p_197664_0_.handle((T)p_197664_1_);
   }

   public void setListener(INetHandler p_150719_1_) {
      Validate.notNull(p_150719_1_, "packetListener");
      this.packetListener = p_150719_1_;
   }

   public void send(IPacket<?> p_179290_1_) {
      this.send(p_179290_1_, (GenericFutureListener<? extends Future<? super Void>>)null);
   }

   public void send(IPacket<?> p_201058_1_, @Nullable GenericFutureListener<? extends Future<? super Void>> p_201058_2_) {
      if (this.isConnected()) {
         this.flushQueue();
         this.sendPacket(p_201058_1_, p_201058_2_);
      } else {
         this.queue.add(new NetworkManager.QueuedPacket(p_201058_1_, p_201058_2_));
      }

   }

   private void sendPacket(IPacket<?> p_150732_1_, @Nullable GenericFutureListener<? extends Future<? super Void>> p_150732_2_) {
      ProtocolType protocoltype = ProtocolType.getProtocolForPacket(p_150732_1_);
      ProtocolType protocoltype1 = this.channel.attr(ATTRIBUTE_PROTOCOL).get();
      ++this.sentPackets;
      if (protocoltype1 != protocoltype) {
         LOGGER.debug("Disabled auto read");
         this.channel.eventLoop().execute(()->this.channel.config().setAutoRead(false));
      }

      if (this.channel.eventLoop().inEventLoop()) {
         if (protocoltype != protocoltype1) {
            this.setProtocol(protocoltype);
         }

         ChannelFuture channelfuture = this.channel.writeAndFlush(p_150732_1_);
         if (p_150732_2_ != null) {
            channelfuture.addListener(p_150732_2_);
         }

         channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
      } else {
         this.channel.eventLoop().execute(() -> {
            if (protocoltype != protocoltype1) {
               this.setProtocol(protocoltype);
            }

            ChannelFuture channelfuture1 = this.channel.writeAndFlush(p_150732_1_);
            if (p_150732_2_ != null) {
               channelfuture1.addListener(p_150732_2_);
            }

            channelfuture1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
         });
      }

   }

   private void flushQueue() {
      if (this.channel != null && this.channel.isOpen()) {
         synchronized(this.queue) {
            NetworkManager.QueuedPacket networkmanager$queuedpacket;
            while((networkmanager$queuedpacket = this.queue.poll()) != null) {
               this.sendPacket(networkmanager$queuedpacket.packet, networkmanager$queuedpacket.listener);
            }

         }
      }
   }

   public void tick() {
      this.flushQueue();
      if (this.packetListener instanceof ServerLoginNetHandler) {
         ((ServerLoginNetHandler)this.packetListener).tick();
      }

      if (this.packetListener instanceof ServerPlayNetHandler) {
         ((ServerPlayNetHandler)this.packetListener).tick();
      }

      if (this.channel != null) {
         this.channel.flush();
      }

      if (this.tickCount++ % 20 == 0) {
         this.tickSecond();
      }

   }

   protected void tickSecond() {
      this.averageSentPackets = MathHelper.lerp(0.75F, (float)this.sentPackets, this.averageSentPackets);
      this.averageReceivedPackets = MathHelper.lerp(0.75F, (float)this.receivedPackets, this.averageReceivedPackets);
      this.sentPackets = 0;
      this.receivedPackets = 0;
   }

   public SocketAddress getRemoteAddress() {
      return this.address;
   }

   public void disconnect(ITextComponent p_150718_1_) {
      if (this.channel.isOpen()) {
         this.channel.close().awaitUninterruptibly();
         this.disconnectedReason = p_150718_1_;
      }

   }

   public boolean isMemoryConnection() {
      return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
   }

   @OnlyIn(Dist.CLIENT)
   public static NetworkManager connectToServer(InetAddress p_181124_0_, int p_181124_1_, boolean p_181124_2_) {
      if (p_181124_0_ instanceof java.net.Inet6Address) System.setProperty("java.net.preferIPv4Stack", "false");
      final NetworkManager networkmanager = new NetworkManager(PacketDirection.CLIENTBOUND);
      networkmanager.activationHandler = net.minecraftforge.fml.network.NetworkHooks::registerClientLoginChannel;
      Class<? extends SocketChannel> oclass;
      LazyValue<? extends EventLoopGroup> lazyvalue;
      if (Epoll.isAvailable() && p_181124_2_) {
         oclass = EpollSocketChannel.class;
         lazyvalue = NETWORK_EPOLL_WORKER_GROUP;
      } else {
         oclass = NioSocketChannel.class;
         lazyvalue = NETWORK_WORKER_GROUP;
      }

      (new Bootstrap()).group(lazyvalue.get()).handler(new ChannelInitializer<Channel>() {
         protected void initChannel(Channel p_initChannel_1_) throws Exception {
            try {
               p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, true);
            } catch (ChannelException channelexception) {
            }

            p_initChannel_1_.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("splitter", new NettyVarint21FrameDecoder()).addLast("decoder", new NettyPacketDecoder(PacketDirection.CLIENTBOUND)).addLast("prepender", new NettyVarint21FrameEncoder()).addLast("encoder", new NettyPacketEncoder(PacketDirection.SERVERBOUND)).addLast("packet_handler", networkmanager);
         }
      }).channel(oclass).connect(p_181124_0_, p_181124_1_).syncUninterruptibly();
      return networkmanager;
   }

   @OnlyIn(Dist.CLIENT)
   public static NetworkManager connectToLocalServer(SocketAddress p_150722_0_) {
      final NetworkManager networkmanager = new NetworkManager(PacketDirection.CLIENTBOUND);
      networkmanager.activationHandler = net.minecraftforge.fml.network.NetworkHooks::registerClientLoginChannel;
      (new Bootstrap()).group(LOCAL_WORKER_GROUP.get()).handler(new ChannelInitializer<Channel>() {
         protected void initChannel(Channel p_initChannel_1_) throws Exception {
            p_initChannel_1_.pipeline().addLast("packet_handler", networkmanager);
         }
      }).channel(LocalChannel.class).connect(p_150722_0_).syncUninterruptibly();
      return networkmanager;
   }

   public void setEncryptionKey(Cipher p_244777_1_, Cipher p_244777_2_) {
      this.encrypted = true;
      this.channel.pipeline().addBefore("splitter", "decrypt", new NettyEncryptingDecoder(p_244777_1_));
      this.channel.pipeline().addBefore("prepender", "encrypt", new NettyEncryptingEncoder(p_244777_2_));
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isEncrypted() {
      return this.encrypted;
   }

   public boolean isConnected() {
      return this.channel != null && this.channel.isOpen();
   }

   public boolean isConnecting() {
      return this.channel == null;
   }

   public INetHandler getPacketListener() {
      return this.packetListener;
   }

   @Nullable
   public ITextComponent getDisconnectedReason() {
      return this.disconnectedReason;
   }

   public void setReadOnly() {
      this.channel.config().setAutoRead(false);
   }

   public void setupCompression(int p_179289_1_) {
      if (p_179289_1_ >= 0) {
         if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
            ((NettyCompressionDecoder)this.channel.pipeline().get("decompress")).setThreshold(p_179289_1_);
         } else {
            this.channel.pipeline().addBefore("decoder", "decompress", new NettyCompressionDecoder(p_179289_1_));
         }

         if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
            ((NettyCompressionEncoder)this.channel.pipeline().get("compress")).setThreshold(p_179289_1_);
         } else {
            this.channel.pipeline().addBefore("encoder", "compress", new NettyCompressionEncoder(p_179289_1_));
         }
      } else {
         if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
            this.channel.pipeline().remove("decompress");
         }

         if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
            this.channel.pipeline().remove("compress");
         }
      }

   }

   public void handleDisconnection() {
      if (this.channel != null && !this.channel.isOpen()) {
         if (this.disconnectionHandled) {
            LOGGER.warn("handleDisconnection() called twice");
         } else {
            this.disconnectionHandled = true;
            if (this.getDisconnectedReason() != null) {
               this.getPacketListener().onDisconnect(this.getDisconnectedReason());
            } else if (this.getPacketListener() != null) {
               this.getPacketListener().onDisconnect(new TranslationTextComponent("multiplayer.disconnect.generic"));
            }
         }

      }
   }

   public float getAverageReceivedPackets() {
      return this.averageReceivedPackets;
   }

   @OnlyIn(Dist.CLIENT)
   public float getAverageSentPackets() {
      return this.averageSentPackets;
   }

   public Channel channel() {
      return channel;
   }

   public PacketDirection getDirection() {
      return this.receiving;
   }

   static class QueuedPacket {
      private final IPacket<?> packet;
      @Nullable
      private final GenericFutureListener<? extends Future<? super Void>> listener;

      public QueuedPacket(IPacket<?> p_i48604_1_, @Nullable GenericFutureListener<? extends Future<? super Void>> p_i48604_2_) {
         this.packet = p_i48604_1_;
         this.listener = p_i48604_2_;
      }
   }
}
