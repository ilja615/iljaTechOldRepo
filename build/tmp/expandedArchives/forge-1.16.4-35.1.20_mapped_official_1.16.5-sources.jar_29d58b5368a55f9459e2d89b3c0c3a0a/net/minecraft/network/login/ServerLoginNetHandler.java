package net.minecraft.network.login;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.client.CCustomPayloadLoginPacket;
import net.minecraft.network.login.client.CEncryptionResponsePacket;
import net.minecraft.network.login.client.CLoginStartPacket;
import net.minecraft.network.login.server.SDisconnectLoginPacket;
import net.minecraft.network.login.server.SEnableCompressionPacket;
import net.minecraft.network.login.server.SEncryptionRequestPacket;
import net.minecraft.network.login.server.SLoginSuccessPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.CryptException;
import net.minecraft.util.CryptManager;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerLoginNetHandler implements IServerLoginNetHandler {
   private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Random RANDOM = new Random();
   private final byte[] nonce = new byte[4];
   private final MinecraftServer server;
   public final NetworkManager connection;
   private ServerLoginNetHandler.State state = ServerLoginNetHandler.State.HELLO;
   private int tick;
   private GameProfile gameProfile;
   private final String serverId = "";
   private SecretKey secretKey;
   private ServerPlayerEntity delayedAcceptPlayer;

   public ServerLoginNetHandler(MinecraftServer p_i45298_1_, NetworkManager p_i45298_2_) {
      this.server = p_i45298_1_;
      this.connection = p_i45298_2_;
      RANDOM.nextBytes(this.nonce);
   }

   public void tick() {
      if (this.state == State.NEGOTIATING) {
         // We force the state into "NEGOTIATING" which is otherwise unused. Once we're completed we move the negotiation onto "READY_TO_ACCEPT"
         // Might want to promote player object creation to here as well..
         boolean negotiationComplete = net.minecraftforge.fml.network.NetworkHooks.tickNegotiation(this, this.connection, this.delayedAcceptPlayer);
         if (negotiationComplete)
            this.state = State.READY_TO_ACCEPT;
      } else if (this.state == ServerLoginNetHandler.State.READY_TO_ACCEPT) {
         this.handleAcceptedLogin();
      } else if (this.state == ServerLoginNetHandler.State.DELAY_ACCEPT) {
         ServerPlayerEntity serverplayerentity = this.server.getPlayerList().getPlayer(this.gameProfile.getId());
         if (serverplayerentity == null) {
            this.state = ServerLoginNetHandler.State.READY_TO_ACCEPT;
            this.server.getPlayerList().placeNewPlayer(this.connection, this.delayedAcceptPlayer);
            this.delayedAcceptPlayer = null;
         }
      }

      if (this.tick++ == 600) {
         this.disconnect(new TranslationTextComponent("multiplayer.disconnect.slow_login"));
      }

   }

   public NetworkManager getConnection() {
      return this.connection;
   }

   public void disconnect(ITextComponent p_194026_1_) {
      try {
         LOGGER.info("Disconnecting {}: {}", this.getUserName(), p_194026_1_.getString());
         this.connection.send(new SDisconnectLoginPacket(p_194026_1_));
         this.connection.disconnect(p_194026_1_);
      } catch (Exception exception) {
         LOGGER.error("Error whilst disconnecting player", (Throwable)exception);
      }

   }

   public void handleAcceptedLogin() {
      if (!this.gameProfile.isComplete()) {
         this.gameProfile = this.createFakeProfile(this.gameProfile);
      }

      ITextComponent itextcomponent = this.server.getPlayerList().canPlayerLogin(this.connection.getRemoteAddress(), this.gameProfile);
      if (itextcomponent != null) {
         this.disconnect(itextcomponent);
      } else {
         this.state = ServerLoginNetHandler.State.ACCEPTED;
         if (this.server.getCompressionThreshold() >= 0 && !this.connection.isMemoryConnection()) {
            this.connection.send(new SEnableCompressionPacket(this.server.getCompressionThreshold()), (p_210149_1_) -> {
               this.connection.setupCompression(this.server.getCompressionThreshold());
            });
         }

         this.connection.send(new SLoginSuccessPacket(this.gameProfile));
         ServerPlayerEntity serverplayerentity = this.server.getPlayerList().getPlayer(this.gameProfile.getId());
         if (serverplayerentity != null) {
            this.state = ServerLoginNetHandler.State.DELAY_ACCEPT;
            this.delayedAcceptPlayer = this.server.getPlayerList().getPlayerForLogin(this.gameProfile);
         } else {
            this.server.getPlayerList().placeNewPlayer(this.connection, this.server.getPlayerList().getPlayerForLogin(this.gameProfile));
         }
      }

   }

   public void onDisconnect(ITextComponent p_147231_1_) {
      LOGGER.info("{} lost connection: {}", this.getUserName(), p_147231_1_.getString());
   }

   public String getUserName() {
      return this.gameProfile != null ? this.gameProfile + " (" + this.connection.getRemoteAddress() + ")" : String.valueOf((Object)this.connection.getRemoteAddress());
   }

   public void handleHello(CLoginStartPacket p_147316_1_) {
      Validate.validState(this.state == ServerLoginNetHandler.State.HELLO, "Unexpected hello packet");
      this.gameProfile = p_147316_1_.getGameProfile();
      if (this.server.usesAuthentication() && !this.connection.isMemoryConnection()) {
         this.state = ServerLoginNetHandler.State.KEY;
         this.connection.send(new SEncryptionRequestPacket("", this.server.getKeyPair().getPublic().getEncoded(), this.nonce));
      } else {
         this.state = ServerLoginNetHandler.State.NEGOTIATING;
      }

   }

   public void handleKey(CEncryptionResponsePacket p_147315_1_) {
      Validate.validState(this.state == ServerLoginNetHandler.State.KEY, "Unexpected key packet");
      PrivateKey privatekey = this.server.getKeyPair().getPrivate();

      final String s;
      try {
         if (!Arrays.equals(this.nonce, p_147315_1_.getNonce(privatekey))) {
            throw new IllegalStateException("Protocol error");
         }

         this.secretKey = p_147315_1_.getSecretKey(privatekey);
         Cipher cipher = CryptManager.getCipher(2, this.secretKey);
         Cipher cipher1 = CryptManager.getCipher(1, this.secretKey);
         s = (new BigInteger(CryptManager.digestData("", this.server.getKeyPair().getPublic(), this.secretKey))).toString(16);
         this.state = ServerLoginNetHandler.State.AUTHENTICATING;
         this.connection.setEncryptionKey(cipher, cipher1);
      } catch (CryptException cryptexception) {
         throw new IllegalStateException("Protocol error", cryptexception);
      }

         Thread thread = new Thread(net.minecraftforge.fml.common.thread.SidedThreadGroups.SERVER, "User Authenticator #" + UNIQUE_THREAD_ID.incrementAndGet()) {
         public void run() {
            GameProfile gameprofile = ServerLoginNetHandler.this.gameProfile;

            try {
               ServerLoginNetHandler.this.gameProfile = ServerLoginNetHandler.this.server.getSessionService().hasJoinedServer(new GameProfile((UUID)null, gameprofile.getName()), s, this.getAddress());
               if (ServerLoginNetHandler.this.gameProfile != null) {
                  ServerLoginNetHandler.LOGGER.info("UUID of player {} is {}", ServerLoginNetHandler.this.gameProfile.getName(), ServerLoginNetHandler.this.gameProfile.getId());
                     ServerLoginNetHandler.this.state = ServerLoginNetHandler.State.NEGOTIATING;
               } else if (ServerLoginNetHandler.this.server.isSingleplayer()) {
                  ServerLoginNetHandler.LOGGER.warn("Failed to verify username but will let them in anyway!");
                  ServerLoginNetHandler.this.gameProfile = ServerLoginNetHandler.this.createFakeProfile(gameprofile);
                     ServerLoginNetHandler.this.state = ServerLoginNetHandler.State.NEGOTIATING;
               } else {
                  ServerLoginNetHandler.this.disconnect(new TranslationTextComponent("multiplayer.disconnect.unverified_username"));
                  ServerLoginNetHandler.LOGGER.error("Username '{}' tried to join with an invalid session", (Object)gameprofile.getName());
               }
            } catch (AuthenticationUnavailableException authenticationunavailableexception) {
               if (ServerLoginNetHandler.this.server.isSingleplayer()) {
                  ServerLoginNetHandler.LOGGER.warn("Authentication servers are down but will let them in anyway!");
                  ServerLoginNetHandler.this.gameProfile = ServerLoginNetHandler.this.createFakeProfile(gameprofile);
                     ServerLoginNetHandler.this.state = ServerLoginNetHandler.State.NEGOTIATING;
               } else {
                  ServerLoginNetHandler.this.disconnect(new TranslationTextComponent("multiplayer.disconnect.authservers_down"));
                  ServerLoginNetHandler.LOGGER.error("Couldn't verify username because servers are unavailable");
               }
            }

         }

         @Nullable
         private InetAddress getAddress() {
            SocketAddress socketaddress = ServerLoginNetHandler.this.connection.getRemoteAddress();
            return ServerLoginNetHandler.this.server.getPreventProxyConnections() && socketaddress instanceof InetSocketAddress ? ((InetSocketAddress)socketaddress).getAddress() : null;
         }
      };
      thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      thread.start();
   }

   public void handleCustomQueryPacket(CCustomPayloadLoginPacket p_209526_1_) {
      if (!net.minecraftforge.fml.network.NetworkHooks.onCustomPayload(p_209526_1_, this.connection))
      this.disconnect(new TranslationTextComponent("multiplayer.disconnect.unexpected_query_response"));
   }

   protected GameProfile createFakeProfile(GameProfile p_152506_1_) {
      UUID uuid = PlayerEntity.createPlayerUUID(p_152506_1_.getName());
      return new GameProfile(uuid, p_152506_1_.getName());
   }

   static enum State {
      HELLO,
      KEY,
      AUTHENTICATING,
      NEGOTIATING,
      READY_TO_ACCEPT,
      DELAY_ACCEPT,
      ACCEPTED;
   }
}
