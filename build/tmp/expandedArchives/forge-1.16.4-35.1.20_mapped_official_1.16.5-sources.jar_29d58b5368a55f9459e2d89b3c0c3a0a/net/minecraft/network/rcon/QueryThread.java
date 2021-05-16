package net.minecraft.network.rcon;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QueryThread extends RConThread {
   private static final Logger LOGGER = LogManager.getLogger();
   private long lastChallengeCheck;
   private final int port;
   private final int serverPort;
   private final int maxPlayers;
   private final String serverName;
   private final String worldName;
   private DatagramSocket socket;
   private final byte[] buffer = new byte[1460];
   private String hostIp;
   private String serverIp;
   private final Map<SocketAddress, QueryThread.Auth> validChallenges;
   private final RConOutputStream rulesResponse;
   private long lastRulesResponse;
   private final IServer serverInterface;

   private QueryThread(IServer p_i241890_1_, int p_i241890_2_) {
      super("Query Listener");
      this.serverInterface = p_i241890_1_;
      this.port = p_i241890_2_;
      this.serverIp = p_i241890_1_.getServerIp();
      this.serverPort = p_i241890_1_.getServerPort();
      this.serverName = p_i241890_1_.getServerName();
      this.maxPlayers = p_i241890_1_.getMaxPlayers();
      this.worldName = p_i241890_1_.getLevelIdName();
      this.lastRulesResponse = 0L;
      this.hostIp = "0.0.0.0";
      if (!this.serverIp.isEmpty() && !this.hostIp.equals(this.serverIp)) {
         this.hostIp = this.serverIp;
      } else {
         this.serverIp = "0.0.0.0";

         try {
            InetAddress inetaddress = InetAddress.getLocalHost();
            this.hostIp = inetaddress.getHostAddress();
         } catch (UnknownHostException unknownhostexception) {
            LOGGER.warn("Unable to determine local host IP, please set server-ip in server.properties", (Throwable)unknownhostexception);
         }
      }

      this.rulesResponse = new RConOutputStream(1460);
      this.validChallenges = Maps.newHashMap();
   }

   @Nullable
   public static QueryThread create(IServer p_242129_0_) {
      int i = p_242129_0_.getProperties().queryPort;
      if (0 < i && 65535 >= i) {
         QueryThread querythread = new QueryThread(p_242129_0_, i);
         return !querythread.start() ? null : querythread;
      } else {
         LOGGER.warn("Invalid query port {} found in server.properties (queries disabled)", (int)i);
         return null;
      }
   }

   private void sendTo(byte[] p_72620_1_, DatagramPacket p_72620_2_) throws IOException {
      this.socket.send(new DatagramPacket(p_72620_1_, p_72620_1_.length, p_72620_2_.getSocketAddress()));
   }

   private boolean processPacket(DatagramPacket p_72621_1_) throws IOException {
      byte[] abyte = p_72621_1_.getData();
      int i = p_72621_1_.getLength();
      SocketAddress socketaddress = p_72621_1_.getSocketAddress();
      LOGGER.debug("Packet len {} [{}]", i, socketaddress);
      if (3 <= i && -2 == abyte[0] && -3 == abyte[1]) {
         LOGGER.debug("Packet '{}' [{}]", RConUtils.toHexString(abyte[2]), socketaddress);
         switch(abyte[2]) {
         case 0:
            if (!this.validChallenge(p_72621_1_)) {
               LOGGER.debug("Invalid challenge [{}]", (Object)socketaddress);
               return false;
            } else if (15 == i) {
               this.sendTo(this.buildRuleResponse(p_72621_1_), p_72621_1_);
               LOGGER.debug("Rules [{}]", (Object)socketaddress);
            } else {
               RConOutputStream rconoutputstream = new RConOutputStream(1460);
               rconoutputstream.write(0);
               rconoutputstream.writeBytes(this.getIdentBytes(p_72621_1_.getSocketAddress()));
               rconoutputstream.writeString(this.serverName);
               rconoutputstream.writeString("SMP");
               rconoutputstream.writeString(this.worldName);
               rconoutputstream.writeString(Integer.toString(this.serverInterface.getPlayerCount()));
               rconoutputstream.writeString(Integer.toString(this.maxPlayers));
               rconoutputstream.writeShort((short)this.serverPort);
               rconoutputstream.writeString(this.hostIp);
               this.sendTo(rconoutputstream.toByteArray(), p_72621_1_);
               LOGGER.debug("Status [{}]", (Object)socketaddress);
            }
         default:
            return true;
         case 9:
            this.sendChallenge(p_72621_1_);
            LOGGER.debug("Challenge [{}]", (Object)socketaddress);
            return true;
         }
      } else {
         LOGGER.debug("Invalid packet [{}]", (Object)socketaddress);
         return false;
      }
   }

   private byte[] buildRuleResponse(DatagramPacket p_72624_1_) throws IOException {
      long i = Util.getMillis();
      if (i < this.lastRulesResponse + 5000L) {
         byte[] abyte = this.rulesResponse.toByteArray();
         byte[] abyte1 = this.getIdentBytes(p_72624_1_.getSocketAddress());
         abyte[1] = abyte1[0];
         abyte[2] = abyte1[1];
         abyte[3] = abyte1[2];
         abyte[4] = abyte1[3];
         return abyte;
      } else {
         this.lastRulesResponse = i;
         this.rulesResponse.reset();
         this.rulesResponse.write(0);
         this.rulesResponse.writeBytes(this.getIdentBytes(p_72624_1_.getSocketAddress()));
         this.rulesResponse.writeString("splitnum");
         this.rulesResponse.write(128);
         this.rulesResponse.write(0);
         this.rulesResponse.writeString("hostname");
         this.rulesResponse.writeString(this.serverName);
         this.rulesResponse.writeString("gametype");
         this.rulesResponse.writeString("SMP");
         this.rulesResponse.writeString("game_id");
         this.rulesResponse.writeString("MINECRAFT");
         this.rulesResponse.writeString("version");
         this.rulesResponse.writeString(this.serverInterface.getServerVersion());
         this.rulesResponse.writeString("plugins");
         this.rulesResponse.writeString(this.serverInterface.getPluginNames());
         this.rulesResponse.writeString("map");
         this.rulesResponse.writeString(this.worldName);
         this.rulesResponse.writeString("numplayers");
         this.rulesResponse.writeString("" + this.serverInterface.getPlayerCount());
         this.rulesResponse.writeString("maxplayers");
         this.rulesResponse.writeString("" + this.maxPlayers);
         this.rulesResponse.writeString("hostport");
         this.rulesResponse.writeString("" + this.serverPort);
         this.rulesResponse.writeString("hostip");
         this.rulesResponse.writeString(this.hostIp);
         this.rulesResponse.write(0);
         this.rulesResponse.write(1);
         this.rulesResponse.writeString("player_");
         this.rulesResponse.write(0);
         String[] astring = this.serverInterface.getPlayerNames();

         for(String s : astring) {
            this.rulesResponse.writeString(s);
         }

         this.rulesResponse.write(0);
         return this.rulesResponse.toByteArray();
      }
   }

   private byte[] getIdentBytes(SocketAddress p_72625_1_) {
      return this.validChallenges.get(p_72625_1_).getIdentBytes();
   }

   private Boolean validChallenge(DatagramPacket p_72627_1_) {
      SocketAddress socketaddress = p_72627_1_.getSocketAddress();
      if (!this.validChallenges.containsKey(socketaddress)) {
         return false;
      } else {
         byte[] abyte = p_72627_1_.getData();
         return this.validChallenges.get(socketaddress).getChallenge() == RConUtils.intFromNetworkByteArray(abyte, 7, p_72627_1_.getLength());
      }
   }

   private void sendChallenge(DatagramPacket p_72622_1_) throws IOException {
      QueryThread.Auth querythread$auth = new QueryThread.Auth(p_72622_1_);
      this.validChallenges.put(p_72622_1_.getSocketAddress(), querythread$auth);
      this.sendTo(querythread$auth.getChallengeBytes(), p_72622_1_);
   }

   private void pruneChallenges() {
      if (this.running) {
         long i = Util.getMillis();
         if (i >= this.lastChallengeCheck + 30000L) {
            this.lastChallengeCheck = i;
            this.validChallenges.values().removeIf((p_232650_2_) -> {
               return p_232650_2_.before(i);
            });
         }
      }
   }

   public void run() {
      LOGGER.info("Query running on {}:{}", this.serverIp, this.port);
      this.lastChallengeCheck = Util.getMillis();
      DatagramPacket datagrampacket = new DatagramPacket(this.buffer, this.buffer.length);

      try {
         while(this.running) {
            try {
               this.socket.receive(datagrampacket);
               this.pruneChallenges();
               this.processPacket(datagrampacket);
            } catch (SocketTimeoutException sockettimeoutexception) {
               this.pruneChallenges();
            } catch (PortUnreachableException portunreachableexception) {
            } catch (IOException ioexception) {
               this.recoverSocketError(ioexception);
            }
         }
      } finally {
         LOGGER.debug("closeSocket: {}:{}", this.serverIp, this.port);
         this.socket.close();
      }

   }

   public boolean start() {
      if (this.running) {
         return true;
      } else {
         return !this.initSocket() ? false : super.start();
      }
   }

   private void recoverSocketError(Exception p_72623_1_) {
      if (this.running) {
         LOGGER.warn("Unexpected exception", (Throwable)p_72623_1_);
         if (!this.initSocket()) {
            LOGGER.error("Failed to recover from exception, shutting down!");
            this.running = false;
         }

      }
   }

   private boolean initSocket() {
      try {
         this.socket = new DatagramSocket(this.port, InetAddress.getByName(this.serverIp));
         this.socket.setSoTimeout(500);
         return true;
      } catch (Exception exception) {
         LOGGER.warn("Unable to initialise query system on {}:{}", this.serverIp, this.port, exception);
         return false;
      }
   }

   static class Auth {
      private final long time = (new Date()).getTime();
      private final int challenge;
      private final byte[] identBytes;
      private final byte[] challengeBytes;
      private final String ident;

      public Auth(DatagramPacket p_i231427_1_) {
         byte[] abyte = p_i231427_1_.getData();
         this.identBytes = new byte[4];
         this.identBytes[0] = abyte[3];
         this.identBytes[1] = abyte[4];
         this.identBytes[2] = abyte[5];
         this.identBytes[3] = abyte[6];
         this.ident = new String(this.identBytes, StandardCharsets.UTF_8);
         this.challenge = (new Random()).nextInt(16777216);
         this.challengeBytes = String.format("\t%s%d\u0000", this.ident, this.challenge).getBytes(StandardCharsets.UTF_8);
      }

      public Boolean before(long p_72593_1_) {
         return this.time < p_72593_1_;
      }

      public int getChallenge() {
         return this.challenge;
      }

      public byte[] getChallengeBytes() {
         return this.challengeBytes;
      }

      public byte[] getIdentBytes() {
         return this.identBytes;
      }
   }
}
