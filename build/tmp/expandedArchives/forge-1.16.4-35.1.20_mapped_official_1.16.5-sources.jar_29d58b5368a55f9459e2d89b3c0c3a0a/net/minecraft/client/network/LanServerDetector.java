package net.minecraft.client.network;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.multiplayer.LanServerPingThread;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class LanServerDetector {
   private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   private static final Logger LOGGER = LogManager.getLogger();

   @OnlyIn(Dist.CLIENT)
   public static class LanServerFindThread extends Thread {
      private final LanServerDetector.LanServerList serverList;
      private final InetAddress pingGroup;
      private final MulticastSocket socket;

      public LanServerFindThread(LanServerDetector.LanServerList p_i1320_1_) throws IOException {
         super("LanServerDetector #" + LanServerDetector.UNIQUE_THREAD_ID.incrementAndGet());
         this.serverList = p_i1320_1_;
         this.setDaemon(true);
         this.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LanServerDetector.LOGGER));
         this.socket = new MulticastSocket(4445);
         this.pingGroup = InetAddress.getByName("224.0.2.60");
         this.socket.setSoTimeout(5000);
         this.socket.joinGroup(this.pingGroup);
      }

      public void run() {
         byte[] abyte = new byte[1024];

         while(!this.isInterrupted()) {
            DatagramPacket datagrampacket = new DatagramPacket(abyte, abyte.length);

            try {
               this.socket.receive(datagrampacket);
            } catch (SocketTimeoutException sockettimeoutexception) {
               continue;
            } catch (IOException ioexception1) {
               LanServerDetector.LOGGER.error("Couldn't ping server", (Throwable)ioexception1);
               break;
            }

            String s = new String(datagrampacket.getData(), datagrampacket.getOffset(), datagrampacket.getLength(), StandardCharsets.UTF_8);
            LanServerDetector.LOGGER.debug("{}: {}", datagrampacket.getAddress(), s);
            this.serverList.addServer(s, datagrampacket.getAddress());
         }

         try {
            this.socket.leaveGroup(this.pingGroup);
         } catch (IOException ioexception) {
         }

         this.socket.close();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LanServerList {
      private final List<LanServerInfo> servers = Lists.newArrayList();
      private boolean isDirty;

      public synchronized boolean isDirty() {
         return this.isDirty;
      }

      public synchronized void markClean() {
         this.isDirty = false;
      }

      public synchronized List<LanServerInfo> getServers() {
         return Collections.unmodifiableList(this.servers);
      }

      public synchronized void addServer(String p_77551_1_, InetAddress p_77551_2_) {
         String s = LanServerPingThread.parseMotd(p_77551_1_);
         String s1 = LanServerPingThread.parseAddress(p_77551_1_);
         if (s1 != null) {
            s1 = p_77551_2_.getHostAddress() + ":" + s1;
            boolean flag = false;

            for(LanServerInfo lanserverinfo : this.servers) {
               if (lanserverinfo.getAddress().equals(s1)) {
                  lanserverinfo.updatePingTime();
                  flag = true;
                  break;
               }
            }

            if (!flag) {
               this.servers.add(new LanServerInfo(s, s1));
               this.isDirty = true;
            }

         }
      }
   }
}
