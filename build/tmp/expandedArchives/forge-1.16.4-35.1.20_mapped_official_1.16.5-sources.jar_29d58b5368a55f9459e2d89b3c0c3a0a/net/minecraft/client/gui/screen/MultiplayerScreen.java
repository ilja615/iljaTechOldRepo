package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.ServerPinger;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class MultiplayerScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ServerPinger pinger = new ServerPinger();
   private final Screen lastScreen;
   protected ServerSelectionList serverSelectionList;
   private ServerList servers;
   private Button editButton;
   private Button selectButton;
   private Button deleteButton;
   private List<ITextComponent> toolTip;
   private ServerData editingServer;
   private LanServerDetector.LanServerList lanServerList;
   private LanServerDetector.LanServerFindThread lanServerDetector;
   private boolean initedOnce;

   public MultiplayerScreen(Screen p_i1040_1_) {
      super(new TranslationTextComponent("multiplayer.title"));
      this.lastScreen = p_i1040_1_;
   }

   protected void init() {
      super.init();
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      if (this.initedOnce) {
         this.serverSelectionList.updateSize(this.width, this.height, 32, this.height - 64);
      } else {
         this.initedOnce = true;
         this.servers = new ServerList(this.minecraft);
         this.servers.load();
         this.lanServerList = new LanServerDetector.LanServerList();

         try {
            this.lanServerDetector = new LanServerDetector.LanServerFindThread(this.lanServerList);
            this.lanServerDetector.start();
         } catch (Exception exception) {
            LOGGER.warn("Unable to start LAN server detection: {}", (Object)exception.getMessage());
         }

         this.serverSelectionList = new ServerSelectionList(this, this.minecraft, this.width, this.height, 32, this.height - 64, 36);
         this.serverSelectionList.updateOnlineServers(this.servers);
      }

      this.children.add(this.serverSelectionList);
      this.selectButton = this.addButton(new Button(this.width / 2 - 154, this.height - 52, 100, 20, new TranslationTextComponent("selectServer.select"), (p_214293_1_) -> {
         this.joinSelectedServer();
      }));
      this.addButton(new Button(this.width / 2 - 50, this.height - 52, 100, 20, new TranslationTextComponent("selectServer.direct"), (p_214286_1_) -> {
         this.editingServer = new ServerData(I18n.get("selectServer.defaultName"), "", false);
         this.minecraft.setScreen(new ServerListScreen(this, this::directJoinCallback, this.editingServer));
      }));
      this.addButton(new Button(this.width / 2 + 4 + 50, this.height - 52, 100, 20, new TranslationTextComponent("selectServer.add"), (p_214288_1_) -> {
         this.editingServer = new ServerData(I18n.get("selectServer.defaultName"), "", false);
         this.minecraft.setScreen(new AddServerScreen(this, this::addServerCallback, this.editingServer));
      }));
      this.editButton = this.addButton(new Button(this.width / 2 - 154, this.height - 28, 70, 20, new TranslationTextComponent("selectServer.edit"), (p_214283_1_) -> {
         ServerSelectionList.Entry serverselectionlist$entry = this.serverSelectionList.getSelected();
         if (serverselectionlist$entry instanceof ServerSelectionList.NormalEntry) {
            ServerData serverdata = ((ServerSelectionList.NormalEntry)serverselectionlist$entry).getServerData();
            this.editingServer = new ServerData(serverdata.name, serverdata.ip, false);
            this.editingServer.copyFrom(serverdata);
            this.minecraft.setScreen(new AddServerScreen(this, this::editServerCallback, this.editingServer));
         }

      }));
      this.deleteButton = this.addButton(new Button(this.width / 2 - 74, this.height - 28, 70, 20, new TranslationTextComponent("selectServer.delete"), (p_214294_1_) -> {
         ServerSelectionList.Entry serverselectionlist$entry = this.serverSelectionList.getSelected();
         if (serverselectionlist$entry instanceof ServerSelectionList.NormalEntry) {
            String s = ((ServerSelectionList.NormalEntry)serverselectionlist$entry).getServerData().name;
            if (s != null) {
               ITextComponent itextcomponent = new TranslationTextComponent("selectServer.deleteQuestion");
               ITextComponent itextcomponent1 = new TranslationTextComponent("selectServer.deleteWarning", s);
               ITextComponent itextcomponent2 = new TranslationTextComponent("selectServer.deleteButton");
               ITextComponent itextcomponent3 = DialogTexts.GUI_CANCEL;
               this.minecraft.setScreen(new ConfirmScreen(this::deleteCallback, itextcomponent, itextcomponent1, itextcomponent2, itextcomponent3));
            }
         }

      }));
      this.addButton(new Button(this.width / 2 + 4, this.height - 28, 70, 20, new TranslationTextComponent("selectServer.refresh"), (p_214291_1_) -> {
         this.refreshServerList();
      }));
      this.addButton(new Button(this.width / 2 + 4 + 76, this.height - 28, 75, 20, DialogTexts.GUI_CANCEL, (p_214289_1_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.onSelectedChange();
   }

   public void tick() {
      super.tick();
      if (this.lanServerList.isDirty()) {
         List<LanServerInfo> list = this.lanServerList.getServers();
         this.lanServerList.markClean();
         this.serverSelectionList.updateNetworkServers(list);
      }

      this.pinger.tick();
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
      if (this.lanServerDetector != null) {
         this.lanServerDetector.interrupt();
         this.lanServerDetector = null;
      }

      this.pinger.removeAll();
   }

   private void refreshServerList() {
      this.minecraft.setScreen(new MultiplayerScreen(this.lastScreen));
   }

   private void deleteCallback(boolean p_214285_1_) {
      ServerSelectionList.Entry serverselectionlist$entry = this.serverSelectionList.getSelected();
      if (p_214285_1_ && serverselectionlist$entry instanceof ServerSelectionList.NormalEntry) {
         this.servers.remove(((ServerSelectionList.NormalEntry)serverselectionlist$entry).getServerData());
         this.servers.save();
         this.serverSelectionList.setSelected((ServerSelectionList.Entry)null);
         this.serverSelectionList.updateOnlineServers(this.servers);
      }

      this.minecraft.setScreen(this);
   }

   private void editServerCallback(boolean p_214292_1_) {
      ServerSelectionList.Entry serverselectionlist$entry = this.serverSelectionList.getSelected();
      if (p_214292_1_ && serverselectionlist$entry instanceof ServerSelectionList.NormalEntry) {
         ServerData serverdata = ((ServerSelectionList.NormalEntry)serverselectionlist$entry).getServerData();
         serverdata.name = this.editingServer.name;
         serverdata.ip = this.editingServer.ip;
         serverdata.copyFrom(this.editingServer);
         this.servers.save();
         this.serverSelectionList.updateOnlineServers(this.servers);
      }

      this.minecraft.setScreen(this);
   }

   private void addServerCallback(boolean p_214284_1_) {
      if (p_214284_1_) {
         this.servers.add(this.editingServer);
         this.servers.save();
         this.serverSelectionList.setSelected((ServerSelectionList.Entry)null);
         this.serverSelectionList.updateOnlineServers(this.servers);
      }

      this.minecraft.setScreen(this);
   }

   private void directJoinCallback(boolean p_214290_1_) {
      if (p_214290_1_) {
         this.join(this.editingServer);
      } else {
         this.minecraft.setScreen(this);
      }

   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      if (super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_)) {
         return true;
      } else if (p_231046_1_ == 294) {
         this.refreshServerList();
         return true;
      } else if (this.serverSelectionList.getSelected() != null) {
         if (p_231046_1_ != 257 && p_231046_1_ != 335) {
            return this.serverSelectionList.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
         } else {
            this.joinSelectedServer();
            return true;
         }
      } else {
         return false;
      }
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.toolTip = null;
      this.renderBackground(p_230430_1_);
      this.serverSelectionList.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 20, 16777215);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      if (this.toolTip != null) {
         this.renderComponentTooltip(p_230430_1_, this.toolTip, p_230430_2_, p_230430_3_);
      }

   }

   public void joinSelectedServer() {
      ServerSelectionList.Entry serverselectionlist$entry = this.serverSelectionList.getSelected();
      if (serverselectionlist$entry instanceof ServerSelectionList.NormalEntry) {
         this.join(((ServerSelectionList.NormalEntry)serverselectionlist$entry).getServerData());
      } else if (serverselectionlist$entry instanceof ServerSelectionList.LanDetectedEntry) {
         LanServerInfo lanserverinfo = ((ServerSelectionList.LanDetectedEntry)serverselectionlist$entry).getServerData();
         this.join(new ServerData(lanserverinfo.getMotd(), lanserverinfo.getAddress(), true));
      }

   }

   private void join(ServerData p_146791_1_) {
      this.minecraft.setScreen(new ConnectingScreen(this, this.minecraft, p_146791_1_));
   }

   public void setSelected(ServerSelectionList.Entry p_214287_1_) {
      this.serverSelectionList.setSelected(p_214287_1_);
      this.onSelectedChange();
   }

   protected void onSelectedChange() {
      this.selectButton.active = false;
      this.editButton.active = false;
      this.deleteButton.active = false;
      ServerSelectionList.Entry serverselectionlist$entry = this.serverSelectionList.getSelected();
      if (serverselectionlist$entry != null && !(serverselectionlist$entry instanceof ServerSelectionList.LanScanEntry)) {
         this.selectButton.active = true;
         if (serverselectionlist$entry instanceof ServerSelectionList.NormalEntry) {
            this.editButton.active = true;
            this.deleteButton.active = true;
         }
      }

   }

   @Override
   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public ServerPinger getPinger() {
      return this.pinger;
   }

   public void setToolTip(List<ITextComponent> p_238854_1_) {
      this.toolTip = p_238854_1_;
   }

   public ServerList getServers() {
      return this.servers;
   }
}
