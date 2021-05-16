package net.minecraft.client.network.login;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InsufficientPrivilegesException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.login.client.CCustomPayloadLoginPacket;
import net.minecraft.network.login.client.CEncryptionResponsePacket;
import net.minecraft.network.login.server.SCustomPayloadLoginPacket;
import net.minecraft.network.login.server.SDisconnectLoginPacket;
import net.minecraft.network.login.server.SEnableCompressionPacket;
import net.minecraft.network.login.server.SEncryptionRequestPacket;
import net.minecraft.network.login.server.SLoginSuccessPacket;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.CryptException;
import net.minecraft.util.CryptManager;
import net.minecraft.util.HTTPUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientLoginNetHandler implements IClientLoginNetHandler {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   @Nullable
   private final Screen parent;
   private final Consumer<ITextComponent> updateStatus;
   private final NetworkManager connection;
   private GameProfile localGameProfile;

   public ClientLoginNetHandler(NetworkManager p_i49527_1_, Minecraft p_i49527_2_, @Nullable Screen p_i49527_3_, Consumer<ITextComponent> p_i49527_4_) {
      this.connection = p_i49527_1_;
      this.minecraft = p_i49527_2_;
      this.parent = p_i49527_3_;
      this.updateStatus = p_i49527_4_;
   }

   public void handleHello(SEncryptionRequestPacket p_147389_1_) {
      Cipher cipher;
      Cipher cipher1;
      String s;
      CEncryptionResponsePacket cencryptionresponsepacket;
      try {
         SecretKey secretkey = CryptManager.generateSecretKey();
         PublicKey publickey = p_147389_1_.getPublicKey();
         s = (new BigInteger(CryptManager.digestData(p_147389_1_.getServerId(), publickey, secretkey))).toString(16);
         cipher = CryptManager.getCipher(2, secretkey);
         cipher1 = CryptManager.getCipher(1, secretkey);
         cencryptionresponsepacket = new CEncryptionResponsePacket(secretkey, publickey, p_147389_1_.getNonce());
      } catch (CryptException cryptexception) {
         throw new IllegalStateException("Protocol error", cryptexception);
      }

      this.updateStatus.accept(new TranslationTextComponent("connect.authorizing"));
      HTTPUtil.DOWNLOAD_EXECUTOR.submit(() -> {
         ITextComponent itextcomponent = this.authenticateServer(s);
         if (itextcomponent != null) {
            if (this.minecraft.getCurrentServer() == null || !this.minecraft.getCurrentServer().isLan()) {
               this.connection.disconnect(itextcomponent);
               return;
            }

            LOGGER.warn(itextcomponent.getString());
         }

         this.updateStatus.accept(new TranslationTextComponent("connect.encrypting"));
         this.connection.send(cencryptionresponsepacket, (p_244776_3_) -> {
            this.connection.setEncryptionKey(cipher, cipher1);
         });
      });
   }

   @Nullable
   private ITextComponent authenticateServer(String p_209522_1_) {
      try {
         this.getMinecraftSessionService().joinServer(this.minecraft.getUser().getGameProfile(), this.minecraft.getUser().getAccessToken(), p_209522_1_);
         return null;
      } catch (AuthenticationUnavailableException authenticationunavailableexception) {
         return new TranslationTextComponent("disconnect.loginFailedInfo", new TranslationTextComponent("disconnect.loginFailedInfo.serversUnavailable"));
      } catch (InvalidCredentialsException invalidcredentialsexception) {
         return new TranslationTextComponent("disconnect.loginFailedInfo", new TranslationTextComponent("disconnect.loginFailedInfo.invalidSession"));
      } catch (InsufficientPrivilegesException insufficientprivilegesexception) {
         return new TranslationTextComponent("disconnect.loginFailedInfo", new TranslationTextComponent("disconnect.loginFailedInfo.insufficientPrivileges"));
      } catch (AuthenticationException authenticationexception) {
         return new TranslationTextComponent("disconnect.loginFailedInfo", authenticationexception.getMessage());
      }
   }

   private MinecraftSessionService getMinecraftSessionService() {
      return this.minecraft.getMinecraftSessionService();
   }

   public void handleGameProfile(SLoginSuccessPacket p_147390_1_) {
      this.updateStatus.accept(new TranslationTextComponent("connect.joining"));
      this.localGameProfile = p_147390_1_.getGameProfile();
      this.connection.setProtocol(ProtocolType.PLAY);
      net.minecraftforge.fml.network.NetworkHooks.handleClientLoginSuccess(this.connection);
      this.connection.setListener(new ClientPlayNetHandler(this.minecraft, this.parent, this.connection, this.localGameProfile));
   }

   public void onDisconnect(ITextComponent p_147231_1_) {
      if (this.parent != null && this.parent instanceof RealmsScreen) {
         this.minecraft.setScreen(new DisconnectedRealmsScreen(this.parent, DialogTexts.CONNECT_FAILED, p_147231_1_));
      } else {
         this.minecraft.setScreen(new DisconnectedScreen(this.parent, DialogTexts.CONNECT_FAILED, p_147231_1_));
      }

   }

   public NetworkManager getConnection() {
      return this.connection;
   }

   public void handleDisconnect(SDisconnectLoginPacket p_147388_1_) {
      this.connection.disconnect(p_147388_1_.getReason());
   }

   public void handleCompression(SEnableCompressionPacket p_180464_1_) {
      if (!this.connection.isMemoryConnection()) {
         this.connection.setupCompression(p_180464_1_.getCompressionThreshold());
      }

   }

   public void handleCustomQuery(SCustomPayloadLoginPacket p_209521_1_) {
      if (net.minecraftforge.fml.network.NetworkHooks.onCustomPayload(p_209521_1_, this.connection)) return;
      this.updateStatus.accept(new TranslationTextComponent("connect.negotiating"));
      this.connection.send(new CCustomPayloadLoginPacket(p_209521_1_.getTransactionId(), (PacketBuffer)null));
   }
}
