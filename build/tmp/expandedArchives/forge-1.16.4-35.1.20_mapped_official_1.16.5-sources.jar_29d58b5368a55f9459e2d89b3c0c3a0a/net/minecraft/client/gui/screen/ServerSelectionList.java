package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ServerSelectionList extends ExtendedList<ServerSelectionList.Entry> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ThreadPoolExecutor THREAD_POOL = new ScheduledThreadPoolExecutor(5, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).build());
   private static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
   private static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/server_selection.png");
   private static final ITextComponent SCANNING_LABEL = new TranslationTextComponent("lanServer.scanning");
   private static final ITextComponent CANT_RESOLVE_TEXT = (new TranslationTextComponent("multiplayer.status.cannot_resolve")).withStyle(TextFormatting.DARK_RED);
   private static final ITextComponent CANT_CONNECT_TEXT = (new TranslationTextComponent("multiplayer.status.cannot_connect")).withStyle(TextFormatting.DARK_RED);
   private static final ITextComponent INCOMPATIBLE_TOOLTIP = new TranslationTextComponent("multiplayer.status.incompatible");
   private static final ITextComponent NO_CONNECTION_TOOLTIP = new TranslationTextComponent("multiplayer.status.no_connection");
   private static final ITextComponent PINGING_TOOLTIP = new TranslationTextComponent("multiplayer.status.pinging");
   private final MultiplayerScreen screen;
   private final List<ServerSelectionList.NormalEntry> onlineServers = Lists.newArrayList();
   private final ServerSelectionList.Entry lanHeader = new ServerSelectionList.LanScanEntry();
   private final List<ServerSelectionList.LanDetectedEntry> networkServers = Lists.newArrayList();

   public ServerSelectionList(MultiplayerScreen p_i45049_1_, Minecraft p_i45049_2_, int p_i45049_3_, int p_i45049_4_, int p_i45049_5_, int p_i45049_6_, int p_i45049_7_) {
      super(p_i45049_2_, p_i45049_3_, p_i45049_4_, p_i45049_5_, p_i45049_6_, p_i45049_7_);
      this.screen = p_i45049_1_;
   }

   private void refreshEntries() {
      this.clearEntries();
      this.onlineServers.forEach(this::addEntry);
      this.addEntry(this.lanHeader);
      this.networkServers.forEach(this::addEntry);
   }

   public void setSelected(@Nullable ServerSelectionList.Entry p_241215_1_) {
      super.setSelected(p_241215_1_);
      if (this.getSelected() instanceof ServerSelectionList.NormalEntry) {
         NarratorChatListener.INSTANCE.sayNow((new TranslationTextComponent("narrator.select", ((ServerSelectionList.NormalEntry)this.getSelected()).serverData.name)).getString());
      }

      this.screen.onSelectedChange();
   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      ServerSelectionList.Entry serverselectionlist$entry = this.getSelected();
      return serverselectionlist$entry != null && serverselectionlist$entry.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_) || super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
   }

   protected void moveSelection(AbstractList.Ordering p_241219_1_) {
      this.moveSelection(p_241219_1_, (p_241612_0_) -> {
         return !(p_241612_0_ instanceof ServerSelectionList.LanScanEntry);
      });
   }

   public void updateOnlineServers(ServerList p_148195_1_) {
      this.onlineServers.clear();

      for(int i = 0; i < p_148195_1_.size(); ++i) {
         this.onlineServers.add(new ServerSelectionList.NormalEntry(this.screen, p_148195_1_.get(i)));
      }

      this.refreshEntries();
   }

   public void updateNetworkServers(List<LanServerInfo> p_148194_1_) {
      this.networkServers.clear();

      for(LanServerInfo lanserverinfo : p_148194_1_) {
         this.networkServers.add(new ServerSelectionList.LanDetectedEntry(this.screen, lanserverinfo));
      }

      this.refreshEntries();
   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 30;
   }

   public int getRowWidth() {
      return super.getRowWidth() + 85;
   }

   protected boolean isFocused() {
      return this.screen.getFocused() == this;
   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class Entry extends ExtendedList.AbstractListEntry<ServerSelectionList.Entry> {
   }

   @OnlyIn(Dist.CLIENT)
   public static class LanDetectedEntry extends ServerSelectionList.Entry {
      private static final ITextComponent LAN_SERVER_HEADER = new TranslationTextComponent("lanServer.title");
      private static final ITextComponent HIDDEN_ADDRESS_TEXT = new TranslationTextComponent("selectServer.hiddenAddress");
      private final MultiplayerScreen screen;
      protected final Minecraft minecraft;
      protected final LanServerInfo serverData;
      private long lastClickTime;

      protected LanDetectedEntry(MultiplayerScreen p_i47141_1_, LanServerInfo p_i47141_2_) {
         this.screen = p_i47141_1_;
         this.serverData = p_i47141_2_;
         this.minecraft = Minecraft.getInstance();
      }

      public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
         this.minecraft.font.draw(p_230432_1_, LAN_SERVER_HEADER, (float)(p_230432_4_ + 32 + 3), (float)(p_230432_3_ + 1), 16777215);
         this.minecraft.font.draw(p_230432_1_, this.serverData.getMotd(), (float)(p_230432_4_ + 32 + 3), (float)(p_230432_3_ + 12), 8421504);
         if (this.minecraft.options.hideServerAddress) {
            this.minecraft.font.draw(p_230432_1_, HIDDEN_ADDRESS_TEXT, (float)(p_230432_4_ + 32 + 3), (float)(p_230432_3_ + 12 + 11), 3158064);
         } else {
            this.minecraft.font.draw(p_230432_1_, this.serverData.getAddress(), (float)(p_230432_4_ + 32 + 3), (float)(p_230432_3_ + 12 + 11), 3158064);
         }

      }

      public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
         this.screen.setSelected(this);
         if (Util.getMillis() - this.lastClickTime < 250L) {
            this.screen.joinSelectedServer();
         }

         this.lastClickTime = Util.getMillis();
         return false;
      }

      public LanServerInfo getServerData() {
         return this.serverData;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LanScanEntry extends ServerSelectionList.Entry {
      private final Minecraft minecraft = Minecraft.getInstance();

      public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
         int i = p_230432_3_ + p_230432_6_ / 2 - 9 / 2;
         this.minecraft.font.draw(p_230432_1_, ServerSelectionList.SCANNING_LABEL, (float)(this.minecraft.screen.width / 2 - this.minecraft.font.width(ServerSelectionList.SCANNING_LABEL) / 2), (float)i, 16777215);
         String s;
         switch((int)(Util.getMillis() / 300L % 4L)) {
         case 0:
         default:
            s = "O o o";
            break;
         case 1:
         case 3:
            s = "o O o";
            break;
         case 2:
            s = "o o O";
         }

         this.minecraft.font.draw(p_230432_1_, s, (float)(this.minecraft.screen.width / 2 - this.minecraft.font.width(s) / 2), (float)(i + 9), 8421504);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public class NormalEntry extends ServerSelectionList.Entry {
      private final MultiplayerScreen screen;
      private final Minecraft minecraft;
      private final ServerData serverData;
      private final ResourceLocation iconLocation;
      private String lastIconB64;
      private DynamicTexture icon;
      private long lastClickTime;

      protected NormalEntry(MultiplayerScreen p_i50669_2_, ServerData p_i50669_3_) {
         this.screen = p_i50669_2_;
         this.serverData = p_i50669_3_;
         this.minecraft = Minecraft.getInstance();
         this.iconLocation = new ResourceLocation("servers/" + Hashing.sha1().hashUnencodedChars(p_i50669_3_.ip) + "/icon");
         this.icon = (DynamicTexture)this.minecraft.getTextureManager().getTexture(this.iconLocation);
      }

      public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
         if (!this.serverData.pinged) {
            this.serverData.pinged = true;
            this.serverData.ping = -2L;
            this.serverData.motd = StringTextComponent.EMPTY;
            this.serverData.status = StringTextComponent.EMPTY;
            ServerSelectionList.THREAD_POOL.submit(() -> {
               try {
                  this.screen.getPinger().pingServer(this.serverData, () -> {
                     this.minecraft.execute(this::updateServerList);
                  });
               } catch (UnknownHostException unknownhostexception) {
                  this.serverData.ping = -1L;
                  this.serverData.motd = ServerSelectionList.CANT_RESOLVE_TEXT;
               } catch (Exception exception) {
                  this.serverData.ping = -1L;
                  this.serverData.motd = ServerSelectionList.CANT_CONNECT_TEXT;
               }

            });
         }

         boolean flag = this.serverData.protocol != SharedConstants.getCurrentVersion().getProtocolVersion();
         this.minecraft.font.draw(p_230432_1_, this.serverData.name, (float)(p_230432_4_ + 32 + 3), (float)(p_230432_3_ + 1), 16777215);
         List<IReorderingProcessor> list = this.minecraft.font.split(this.serverData.motd, p_230432_5_ - 32 - 2);

         for(int i = 0; i < Math.min(list.size(), 2); ++i) {
            this.minecraft.font.draw(p_230432_1_, list.get(i), (float)(p_230432_4_ + 32 + 3), (float)(p_230432_3_ + 12 + 9 * i), 8421504);
         }

         ITextComponent itextcomponent1 = (ITextComponent)(flag ? this.serverData.version.copy().withStyle(TextFormatting.RED) : this.serverData.status);
         int j = this.minecraft.font.width(itextcomponent1);
         this.minecraft.font.draw(p_230432_1_, itextcomponent1, (float)(p_230432_4_ + p_230432_5_ - j - 15 - 2), (float)(p_230432_3_ + 1), 8421504);
         int k = 0;
         int l;
         List<ITextComponent> list1;
         ITextComponent itextcomponent;
         if (flag) {
            l = 5;
            itextcomponent = ServerSelectionList.INCOMPATIBLE_TOOLTIP;
            list1 = this.serverData.playerList;
         } else if (this.serverData.pinged && this.serverData.ping != -2L) {
            if (this.serverData.ping < 0L) {
               l = 5;
            } else if (this.serverData.ping < 150L) {
               l = 0;
            } else if (this.serverData.ping < 300L) {
               l = 1;
            } else if (this.serverData.ping < 600L) {
               l = 2;
            } else if (this.serverData.ping < 1000L) {
               l = 3;
            } else {
               l = 4;
            }

            if (this.serverData.ping < 0L) {
               itextcomponent = ServerSelectionList.NO_CONNECTION_TOOLTIP;
               list1 = Collections.emptyList();
            } else {
               itextcomponent = new TranslationTextComponent("multiplayer.status.ping", this.serverData.ping);
               list1 = this.serverData.playerList;
            }
         } else {
            k = 1;
            l = (int)(Util.getMillis() / 100L + (long)(p_230432_2_ * 2) & 7L);
            if (l > 4) {
               l = 8 - l;
            }

            itextcomponent = ServerSelectionList.PINGING_TOOLTIP;
            list1 = Collections.emptyList();
         }

         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.minecraft.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
         AbstractGui.blit(p_230432_1_, p_230432_4_ + p_230432_5_ - 15, p_230432_3_, (float)(k * 10), (float)(176 + l * 8), 10, 8, 256, 256);
         String s = this.serverData.getIconB64();
         if (!Objects.equals(s, this.lastIconB64)) {
            if (this.uploadServerIcon(s)) {
               this.lastIconB64 = s;
            } else {
               this.serverData.setIconB64((String)null);
               this.updateServerList();
            }
         }

         if (this.icon != null) {
            this.drawIcon(p_230432_1_, p_230432_4_, p_230432_3_, this.iconLocation);
         } else {
            this.drawIcon(p_230432_1_, p_230432_4_, p_230432_3_, ServerSelectionList.ICON_MISSING);
         }

         int i1 = p_230432_7_ - p_230432_4_;
         int j1 = p_230432_8_ - p_230432_3_;
         if (i1 >= p_230432_5_ - 15 && i1 <= p_230432_5_ - 5 && j1 >= 0 && j1 <= 8) {
            this.screen.setToolTip(Collections.singletonList(itextcomponent));
         } else if (i1 >= p_230432_5_ - j - 15 - 2 && i1 <= p_230432_5_ - 15 - 2 && j1 >= 0 && j1 <= 8) {
            this.screen.setToolTip(list1);
         }

         net.minecraftforge.fml.client.ClientHooks.drawForgePingInfo(this.screen, serverData, p_230432_1_, p_230432_4_, p_230432_3_, p_230432_5_, i1, j1);

         if (this.minecraft.options.touchscreen || p_230432_9_) {
            this.minecraft.getTextureManager().bind(ServerSelectionList.ICON_OVERLAY_LOCATION);
            AbstractGui.fill(p_230432_1_, p_230432_4_, p_230432_3_, p_230432_4_ + 32, p_230432_3_ + 32, -1601138544);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int k1 = p_230432_7_ - p_230432_4_;
            int l1 = p_230432_8_ - p_230432_3_;
            if (this.canJoin()) {
               if (k1 < 32 && k1 > 16) {
                  AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 0.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 0.0F, 0.0F, 32, 32, 256, 256);
               }
            }

            if (p_230432_2_ > 0) {
               if (k1 < 16 && l1 < 16) {
                  AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 96.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 96.0F, 0.0F, 32, 32, 256, 256);
               }
            }

            if (p_230432_2_ < this.screen.getServers().size() - 1) {
               if (k1 < 16 && l1 > 16) {
                  AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 64.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 64.0F, 0.0F, 32, 32, 256, 256);
               }
            }
         }

      }

      public void updateServerList() {
         this.screen.getServers().save();
      }

      protected void drawIcon(MatrixStack p_238859_1_, int p_238859_2_, int p_238859_3_, ResourceLocation p_238859_4_) {
         this.minecraft.getTextureManager().bind(p_238859_4_);
         RenderSystem.enableBlend();
         AbstractGui.blit(p_238859_1_, p_238859_2_, p_238859_3_, 0.0F, 0.0F, 32, 32, 32, 32);
         RenderSystem.disableBlend();
      }

      private boolean canJoin() {
         return true;
      }

      private boolean uploadServerIcon(@Nullable String p_241614_1_) {
         if (p_241614_1_ == null) {
            this.minecraft.getTextureManager().release(this.iconLocation);
            if (this.icon != null && this.icon.getPixels() != null) {
               this.icon.getPixels().close();
            }

            this.icon = null;
         } else {
            try {
               NativeImage nativeimage = NativeImage.fromBase64(p_241614_1_);
               Validate.validState(nativeimage.getWidth() == 64, "Must be 64 pixels wide");
               Validate.validState(nativeimage.getHeight() == 64, "Must be 64 pixels high");
               if (this.icon == null) {
                  this.icon = new DynamicTexture(nativeimage);
               } else {
                  this.icon.setPixels(nativeimage);
                  this.icon.upload();
               }

               this.minecraft.getTextureManager().register(this.iconLocation, this.icon);
            } catch (Throwable throwable) {
               ServerSelectionList.LOGGER.error("Invalid icon for server {} ({})", this.serverData.name, this.serverData.ip, throwable);
               return false;
            }
         }

         return true;
      }

      public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
         if (Screen.hasShiftDown()) {
            ServerSelectionList serverselectionlist = this.screen.serverSelectionList;
            int i = serverselectionlist.children().indexOf(this);
            if (p_231046_1_ == 264 && i < this.screen.getServers().size() - 1 || p_231046_1_ == 265 && i > 0) {
               this.swap(i, p_231046_1_ == 264 ? i + 1 : i - 1);
               return true;
            }
         }

         return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
      }

      private void swap(int p_228196_1_, int p_228196_2_) {
         this.screen.getServers().swap(p_228196_1_, p_228196_2_);
         this.screen.serverSelectionList.updateOnlineServers(this.screen.getServers());
         ServerSelectionList.Entry serverselectionlist$entry = this.screen.serverSelectionList.children().get(p_228196_2_);
         this.screen.serverSelectionList.setSelected(serverselectionlist$entry);
         ServerSelectionList.this.ensureVisible(serverselectionlist$entry);
      }

      public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
         double d0 = p_231044_1_ - (double)ServerSelectionList.this.getRowLeft();
         double d1 = p_231044_3_ - (double)ServerSelectionList.this.getRowTop(ServerSelectionList.this.children().indexOf(this));
         if (d0 <= 32.0D) {
            if (d0 < 32.0D && d0 > 16.0D && this.canJoin()) {
               this.screen.setSelected(this);
               this.screen.joinSelectedServer();
               return true;
            }

            int i = this.screen.serverSelectionList.children().indexOf(this);
            if (d0 < 16.0D && d1 < 16.0D && i > 0) {
               this.swap(i, i - 1);
               return true;
            }

            if (d0 < 16.0D && d1 > 16.0D && i < this.screen.getServers().size() - 1) {
               this.swap(i, i + 1);
               return true;
            }
         }

         this.screen.setSelected(this);
         if (Util.getMillis() - this.lastClickTime < 250L) {
            this.screen.joinSelectedServer();
         }

         this.lastClickTime = Util.getMillis();
         return false;
      }

      public ServerData getServerData() {
         return this.serverData;
      }
   }
}
