package net.minecraft.realms;

import com.mojang.realmsclient.dto.RealmsServer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.login.ClientLoginNetHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.client.CLoginStartPacket;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsConnect {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Screen onlineScreen;
   private volatile boolean aborted;
   private NetworkManager connection;

   public RealmsConnect(Screen p_i232500_1_) {
      this.onlineScreen = p_i232500_1_;
   }

   public void connect(final RealmsServer p_244798_1_, final String p_244798_2_, final int p_244798_3_) {
      final Minecraft minecraft = Minecraft.getInstance();
      minecraft.setConnectedToRealms(true);
      RealmsNarratorHelper.now(I18n.get("mco.connect.success"));
      (new Thread("Realms-connect-task") {
         public void run() {
            InetAddress inetaddress = null;

            try {
               inetaddress = InetAddress.getByName(p_244798_2_);
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection = NetworkManager.connectToServer(inetaddress, p_244798_3_, minecraft.options.useNativeTransport());
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection.setListener(new ClientLoginNetHandler(RealmsConnect.this.connection, minecraft, RealmsConnect.this.onlineScreen, (p_209500_0_) -> {
               }));
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection.send(new CHandshakePacket(p_244798_2_, p_244798_3_, ProtocolType.LOGIN));
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.this.connection.send(new CLoginStartPacket(minecraft.getUser().getGameProfile()));
               minecraft.setCurrentServer(p_244798_1_.toServerData(p_244798_2_));
            } catch (UnknownHostException unknownhostexception) {
               minecraft.getClientPackSource().clearServerPack();
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.LOGGER.error("Couldn't connect to world", (Throwable)unknownhostexception);
               DisconnectedRealmsScreen disconnectedrealmsscreen = new DisconnectedRealmsScreen(RealmsConnect.this.onlineScreen, DialogTexts.CONNECT_FAILED, new TranslationTextComponent("disconnect.genericReason", "Unknown host '" + p_244798_2_ + "'"));
               minecraft.execute(() -> {
                  minecraft.setScreen(disconnectedrealmsscreen);
               });
            } catch (Exception exception) {
               minecraft.getClientPackSource().clearServerPack();
               if (RealmsConnect.this.aborted) {
                  return;
               }

               RealmsConnect.LOGGER.error("Couldn't connect to world", (Throwable)exception);
               String s = exception.toString();
               if (inetaddress != null) {
                  String s1 = inetaddress + ":" + p_244798_3_;
                  s = s.replaceAll(s1, "");
               }

               DisconnectedRealmsScreen disconnectedrealmsscreen1 = new DisconnectedRealmsScreen(RealmsConnect.this.onlineScreen, DialogTexts.CONNECT_FAILED, new TranslationTextComponent("disconnect.genericReason", s));
               minecraft.execute(() -> {
                  minecraft.setScreen(disconnectedrealmsscreen1);
               });
            }

         }
      }).start();
   }

   public void abort() {
      this.aborted = true;
      if (this.connection != null && this.connection.isConnected()) {
         this.connection.disconnect(new TranslationTextComponent("disconnect.genericReason"));
         this.connection.handleDisconnection();
      }

   }

   public void tick() {
      if (this.connection != null) {
         if (this.connection.isConnected()) {
            this.connection.tick();
         } else {
            this.connection.handleDisconnection();
         }
      }

   }
}
