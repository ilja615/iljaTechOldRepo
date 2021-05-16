package net.minecraft.network.rcon;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.dedicated.ServerProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainThread extends RConThread {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ServerSocket socket;
   private final String rconPassword;
   private final List<ClientThread> clients = Lists.newArrayList();
   private final IServer serverInterface;

   private MainThread(IServer p_i241891_1_, ServerSocket p_i241891_2_, String p_i241891_3_) {
      super("RCON Listener");
      this.serverInterface = p_i241891_1_;
      this.socket = p_i241891_2_;
      this.rconPassword = p_i241891_3_;
   }

   private void clearClients() {
      this.clients.removeIf((p_232654_0_) -> {
         return !p_232654_0_.isRunning();
      });
   }

   public void run() {
      try {
         while(this.running) {
            try {
               Socket socket = this.socket.accept();
               ClientThread clientthread = new ClientThread(this.serverInterface, this.rconPassword, socket);
               clientthread.start();
               this.clients.add(clientthread);
               this.clearClients();
            } catch (SocketTimeoutException sockettimeoutexception) {
               this.clearClients();
            } catch (IOException ioexception) {
               if (this.running) {
                  LOGGER.info("IO exception: ", (Throwable)ioexception);
               }
            }
         }
      } finally {
         this.closeSocket(this.socket);
      }

   }

   @Nullable
   public static MainThread create(IServer p_242130_0_) {
      ServerProperties serverproperties = p_242130_0_.getProperties();
      String s = p_242130_0_.getServerIp();
      if (s.isEmpty()) {
         s = "0.0.0.0";
      }

      int i = serverproperties.rconPort;
      if (0 < i && 65535 >= i) {
         String s1 = serverproperties.rconPassword;
         if (s1.isEmpty()) {
            LOGGER.warn("No rcon password set in server.properties, rcon disabled!");
            return null;
         } else {
            try {
               ServerSocket serversocket = new ServerSocket(i, 0, InetAddress.getByName(s));
               serversocket.setSoTimeout(500);
               MainThread mainthread = new MainThread(p_242130_0_, serversocket, s1);
               if (!mainthread.start()) {
                  return null;
               } else {
                  LOGGER.info("RCON running on {}:{}", s, i);
                  return mainthread;
               }
            } catch (IOException ioexception) {
               LOGGER.warn("Unable to initialise RCON on {}:{}", s, i, ioexception);
               return null;
            }
         }
      } else {
         LOGGER.warn("Invalid rcon port {} found in server.properties, rcon disabled!", (int)i);
         return null;
      }
   }

   public void stop() {
      this.running = false;
      this.closeSocket(this.socket);
      super.stop();

      for(ClientThread clientthread : this.clients) {
         if (clientthread.isRunning()) {
            clientthread.stop();
         }
      }

      this.clients.clear();
   }

   private void closeSocket(ServerSocket p_232655_1_) {
      LOGGER.debug("closeSocket: {}", (Object)p_232655_1_);

      try {
         p_232655_1_.close();
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to close socket", (Throwable)ioexception);
      }

   }
}
