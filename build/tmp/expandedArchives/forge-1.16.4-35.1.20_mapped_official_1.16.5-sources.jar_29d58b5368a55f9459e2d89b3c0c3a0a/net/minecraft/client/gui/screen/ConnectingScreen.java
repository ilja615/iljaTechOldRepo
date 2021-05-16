package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.login.ClientLoginNetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.client.CLoginStartPacket;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ConnectingScreen extends Screen {
   private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   private static final Logger LOGGER = LogManager.getLogger();
   private NetworkManager connection;
   private boolean aborted;
   private final Screen parent;
   private ITextComponent status = new TranslationTextComponent("connect.connecting");
   private long lastNarration = -1L;

   public ConnectingScreen(Screen p_i1181_1_, Minecraft p_i1181_2_, ServerData p_i1181_3_) {
      super(NarratorChatListener.NO_TITLE);
      this.minecraft = p_i1181_2_;
      this.parent = p_i1181_1_;
      ServerAddress serveraddress = ServerAddress.parseString(p_i1181_3_.ip);
      p_i1181_2_.clearLevel();
      p_i1181_2_.setCurrentServer(p_i1181_3_);
      this.connect(serveraddress.getHost(), serveraddress.getPort());
   }

   public ConnectingScreen(Screen p_i1182_1_, Minecraft p_i1182_2_, String p_i1182_3_, int p_i1182_4_) {
      super(NarratorChatListener.NO_TITLE);
      this.minecraft = p_i1182_2_;
      this.parent = p_i1182_1_;
      p_i1182_2_.clearLevel();
      this.connect(p_i1182_3_, p_i1182_4_);
   }

   private void connect(final String p_146367_1_, final int p_146367_2_) {
      LOGGER.info("Connecting to {}, {}", p_146367_1_, p_146367_2_);
      Thread thread = new Thread("Server Connector #" + UNIQUE_THREAD_ID.incrementAndGet()) {
         public void run() {
            InetAddress inetaddress = null;

            try {
               if (ConnectingScreen.this.aborted) {
                  return;
               }

               inetaddress = InetAddress.getByName(p_146367_1_);
               ConnectingScreen.this.connection = NetworkManager.connectToServer(inetaddress, p_146367_2_, ConnectingScreen.this.minecraft.options.useNativeTransport());
               ConnectingScreen.this.connection.setListener(new ClientLoginNetHandler(ConnectingScreen.this.connection, ConnectingScreen.this.minecraft, ConnectingScreen.this.parent, (p_209549_1_) -> {
                  ConnectingScreen.this.updateStatus(p_209549_1_);
               }));
               ConnectingScreen.this.connection.send(new CHandshakePacket(p_146367_1_, p_146367_2_, ProtocolType.LOGIN));
               ConnectingScreen.this.connection.send(new CLoginStartPacket(ConnectingScreen.this.minecraft.getUser().getGameProfile()));
            } catch (UnknownHostException unknownhostexception) {
               if (ConnectingScreen.this.aborted) {
                  return;
               }

               ConnectingScreen.LOGGER.error("Couldn't connect to server", (Throwable)unknownhostexception);
               ConnectingScreen.this.minecraft.execute(() -> {
                  ConnectingScreen.this.minecraft.setScreen(new DisconnectedScreen(ConnectingScreen.this.parent, DialogTexts.CONNECT_FAILED, new TranslationTextComponent("disconnect.genericReason", "Unknown host")));
               });
            } catch (Exception exception) {
               if (ConnectingScreen.this.aborted) {
                  return;
               }

               ConnectingScreen.LOGGER.error("Couldn't connect to server", (Throwable)exception);
               String s = inetaddress == null ? exception.toString() : exception.toString().replaceAll(inetaddress + ":" + p_146367_2_, "");
               ConnectingScreen.this.minecraft.execute(() -> {
                  ConnectingScreen.this.minecraft.setScreen(new DisconnectedScreen(ConnectingScreen.this.parent, DialogTexts.CONNECT_FAILED, new TranslationTextComponent("disconnect.genericReason", s)));
               });
            }

         }
      };
      thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      thread.start();
   }

   private void updateStatus(ITextComponent p_209514_1_) {
      this.status = p_209514_1_;
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

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected void init() {
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, DialogTexts.GUI_CANCEL, (p_212999_1_) -> {
         this.aborted = true;
         if (this.connection != null) {
            this.connection.disconnect(new TranslationTextComponent("connect.aborted"));
         }

         this.minecraft.setScreen(this.parent);
      }));
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      long i = Util.getMillis();
      if (i - this.lastNarration > 2000L) {
         this.lastNarration = i;
         NarratorChatListener.INSTANCE.sayNow((new TranslationTextComponent("narrator.joining")).getString());
      }

      drawCenteredString(p_230430_1_, this.font, this.status, this.width / 2, this.height / 2 - 50, 16777215);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }
}
