package com.mojang.realmsclient;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.Ping;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPlayerList;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RegionPingResult;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.screens.RealmsClientOutdatedScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsCreateRealmScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsParentalConsentScreen;
import com.mojang.realmsclient.gui.screens.RealmsPendingInvitesScreen;
import com.mojang.realmsclient.util.RealmsPersistence;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.IScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.KeyCombo;
import net.minecraft.realms.RealmsNarratorHelper;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.action.ConnectingToRealmsAction;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsMainScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation ON_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/on_icon.png");
   private static final ResourceLocation OFF_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/off_icon.png");
   private static final ResourceLocation EXPIRED_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/expired_icon.png");
   private static final ResourceLocation EXPIRES_SOON_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/expires_soon_icon.png");
   private static final ResourceLocation LEAVE_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/leave_icon.png");
   private static final ResourceLocation INVITATION_ICONS_LOCATION = new ResourceLocation("realms", "textures/gui/realms/invitation_icons.png");
   private static final ResourceLocation INVITE_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/invite_icon.png");
   private static final ResourceLocation WORLDICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/world_icon.png");
   private static final ResourceLocation LOGO_LOCATION = new ResourceLocation("realms", "textures/gui/title/realms.png");
   private static final ResourceLocation CONFIGURE_LOCATION = new ResourceLocation("realms", "textures/gui/realms/configure_icon.png");
   private static final ResourceLocation QUESTIONMARK_LOCATION = new ResourceLocation("realms", "textures/gui/realms/questionmark.png");
   private static final ResourceLocation NEWS_LOCATION = new ResourceLocation("realms", "textures/gui/realms/news_icon.png");
   private static final ResourceLocation POPUP_LOCATION = new ResourceLocation("realms", "textures/gui/realms/popup.png");
   private static final ResourceLocation DARKEN_LOCATION = new ResourceLocation("realms", "textures/gui/realms/darken.png");
   private static final ResourceLocation CROSS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/cross_icon.png");
   private static final ResourceLocation TRIAL_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/trial_icon.png");
   private static final ResourceLocation BUTTON_LOCATION = new ResourceLocation("minecraft", "textures/gui/widgets.png");
   private static final ITextComponent NO_PENDING_INVITES_TEXT = new TranslationTextComponent("mco.invites.nopending");
   private static final ITextComponent PENDING_INVITES_TEXT = new TranslationTextComponent("mco.invites.pending");
   private static final List<ITextComponent> TRIAL_MESSAGE_LINES = ImmutableList.of(new TranslationTextComponent("mco.trial.message.line1"), new TranslationTextComponent("mco.trial.message.line2"));
   private static final ITextComponent SERVER_UNITIALIZED_TEXT = new TranslationTextComponent("mco.selectServer.uninitialized");
   private static final ITextComponent SUBSCRIPTION_EXPIRED_TEXT = new TranslationTextComponent("mco.selectServer.expiredList");
   private static final ITextComponent SUBSCRIPTION_RENEW_TEXT = new TranslationTextComponent("mco.selectServer.expiredRenew");
   private static final ITextComponent TRIAL_EXPIRED_TEXT = new TranslationTextComponent("mco.selectServer.expiredTrial");
   private static final ITextComponent SUBSCRIPTION_CREATE_TEXT = new TranslationTextComponent("mco.selectServer.expiredSubscribe");
   private static final ITextComponent SELECT_MINIGAME_PREFIX = (new TranslationTextComponent("mco.selectServer.minigame")).append(" ");
   private static final ITextComponent POPUP_TEXT = new TranslationTextComponent("mco.selectServer.popup");
   private static final ITextComponent SERVER_EXPIRED_TOOLTIP = new TranslationTextComponent("mco.selectServer.expired");
   private static final ITextComponent SERVER_EXPIRES_SOON_TOOLTIP = new TranslationTextComponent("mco.selectServer.expires.soon");
   private static final ITextComponent SERVER_EXPIRES_IN_DAY_TOOLTIP = new TranslationTextComponent("mco.selectServer.expires.day");
   private static final ITextComponent SERVER_OPEN_TOOLTIP = new TranslationTextComponent("mco.selectServer.open");
   private static final ITextComponent SERVER_CLOSED_TOOLTIP = new TranslationTextComponent("mco.selectServer.closed");
   private static final ITextComponent LEAVE_SERVER_TOOLTIP = new TranslationTextComponent("mco.selectServer.leave");
   private static final ITextComponent CONFIGURE_SERVER_TOOLTIP = new TranslationTextComponent("mco.selectServer.configure");
   private static final ITextComponent SERVER_INFO_TOOLTIP = new TranslationTextComponent("mco.selectServer.info");
   private static final ITextComponent NEWS_TOOLTIP = new TranslationTextComponent("mco.news");
   private static List<ResourceLocation> teaserImages = ImmutableList.of();
   private static final RealmsDataFetcher REALMS_DATA_FETCHER = new RealmsDataFetcher();
   private static boolean overrideConfigure;
   private static int lastScrollYPosition = -1;
   private static volatile boolean hasParentalConsent;
   private static volatile boolean checkedParentalConsent;
   private static volatile boolean checkedClientCompatability;
   private static Screen realmsGenericErrorScreen;
   private static boolean regionsPinged;
   private final RateLimiter inviteNarrationLimiter;
   private boolean dontSetConnectedToRealms;
   private final Screen lastScreen;
   private volatile RealmsMainScreen.ServerList realmSelectionList;
   private long selectedServerId = -1L;
   private Button playButton;
   private Button backButton;
   private Button renewButton;
   private Button configureButton;
   private Button leaveButton;
   private List<ITextComponent> toolTip;
   private List<RealmsServer> realmsServers = Lists.newArrayList();
   private volatile int numberOfPendingInvites;
   private int animTick;
   private boolean hasFetchedServers;
   private boolean popupOpenedByUser;
   private boolean justClosedPopup;
   private volatile boolean trialsAvailable;
   private volatile boolean createdTrial;
   private volatile boolean showingPopup;
   private volatile boolean hasUnreadNews;
   private volatile String newsLink;
   private int carouselIndex;
   private int carouselTick;
   private boolean hasSwitchedCarouselImage;
   private List<KeyCombo> keyCombos;
   private int clicks;
   private ReentrantLock connectLock = new ReentrantLock();
   private IBidiRenderer formattedPopup = IBidiRenderer.EMPTY;
   private RealmsMainScreen.ServerState hoveredElement;
   private Button showPopupButton;
   private Button pendingInvitesButton;
   private Button newsButton;
   private Button createTrialButton;
   private Button buyARealmButton;
   private Button closeButton;

   public RealmsMainScreen(Screen p_i232181_1_) {
      this.lastScreen = p_i232181_1_;
      this.inviteNarrationLimiter = RateLimiter.create((double)0.016666668F);
   }

   private boolean shouldShowMessageInList() {
      if (hasParentalConsent() && this.hasFetchedServers) {
         if (this.trialsAvailable && !this.createdTrial) {
            return true;
         } else {
            for(RealmsServer realmsserver : this.realmsServers) {
               if (realmsserver.ownerUUID.equals(this.minecraft.getUser().getUuid())) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public boolean shouldShowPopup() {
      if (hasParentalConsent() && this.hasFetchedServers) {
         if (this.popupOpenedByUser) {
            return true;
         } else {
            return this.trialsAvailable && !this.createdTrial && this.realmsServers.isEmpty() ? true : this.realmsServers.isEmpty();
         }
      } else {
         return false;
      }
   }

   public void init() {
      this.keyCombos = Lists.newArrayList(new KeyCombo(new char[]{'3', '2', '1', '4', '5', '6'}, () -> {
         overrideConfigure = !overrideConfigure;
      }), new KeyCombo(new char[]{'9', '8', '7', '1', '2', '3'}, () -> {
         if (RealmsClient.currentEnvironment == RealmsClient.Environment.STAGE) {
            this.switchToProd();
         } else {
            this.switchToStage();
         }

      }), new KeyCombo(new char[]{'9', '8', '7', '4', '5', '6'}, () -> {
         if (RealmsClient.currentEnvironment == RealmsClient.Environment.LOCAL) {
            this.switchToProd();
         } else {
            this.switchToLocal();
         }

      }));
      if (realmsGenericErrorScreen != null) {
         this.minecraft.setScreen(realmsGenericErrorScreen);
      } else {
         this.connectLock = new ReentrantLock();
         if (checkedClientCompatability && !hasParentalConsent()) {
            this.checkParentalConsent();
         }

         this.checkClientCompatability();
         this.checkUnreadNews();
         if (!this.dontSetConnectedToRealms) {
            this.minecraft.setConnectedToRealms(false);
         }

         this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
         if (hasParentalConsent()) {
            REALMS_DATA_FETCHER.forceUpdate();
         }

         this.showingPopup = false;
         if (hasParentalConsent() && this.hasFetchedServers) {
            this.addButtons();
         }

         this.realmSelectionList = new RealmsMainScreen.ServerList();
         if (lastScrollYPosition != -1) {
            this.realmSelectionList.setScrollAmount((double)lastScrollYPosition);
         }

         this.addWidget(this.realmSelectionList);
         this.magicalSpecialHackyFocus(this.realmSelectionList);
         this.formattedPopup = IBidiRenderer.create(this.font, POPUP_TEXT, 100);
      }
   }

   private static boolean hasParentalConsent() {
      return checkedParentalConsent && hasParentalConsent;
   }

   public void addButtons() {
      this.leaveButton = this.addButton(new Button(this.width / 2 - 202, this.height - 32, 90, 20, new TranslationTextComponent("mco.selectServer.leave"), (p_237624_1_) -> {
         this.leaveClicked(this.findServer(this.selectedServerId));
      }));
      this.configureButton = this.addButton(new Button(this.width / 2 - 190, this.height - 32, 90, 20, new TranslationTextComponent("mco.selectServer.configure"), (p_237637_1_) -> {
         this.configureClicked(this.findServer(this.selectedServerId));
      }));
      this.playButton = this.addButton(new Button(this.width / 2 - 93, this.height - 32, 90, 20, new TranslationTextComponent("mco.selectServer.play"), (p_237635_1_) -> {
         RealmsServer realmsserver1 = this.findServer(this.selectedServerId);
         if (realmsserver1 != null) {
            this.play(realmsserver1, this);
         }
      }));
      this.backButton = this.addButton(new Button(this.width / 2 + 4, this.height - 32, 90, 20, DialogTexts.GUI_BACK, (p_237632_1_) -> {
         if (!this.justClosedPopup) {
            this.minecraft.setScreen(this.lastScreen);
         }

      }));
      this.renewButton = this.addButton(new Button(this.width / 2 + 100, this.height - 32, 90, 20, new TranslationTextComponent("mco.selectServer.expiredRenew"), (p_237629_1_) -> {
         this.onRenew();
      }));
      this.pendingInvitesButton = this.addButton(new RealmsMainScreen.PendingInvitesButton());
      this.newsButton = this.addButton(new RealmsMainScreen.NewsButton());
      this.showPopupButton = this.addButton(new RealmsMainScreen.InfoButton());
      this.closeButton = this.addButton(new RealmsMainScreen.CloseButton());
      this.createTrialButton = this.addButton(new Button(this.width / 2 + 52, this.popupY0() + 137 - 20, 98, 20, new TranslationTextComponent("mco.selectServer.trial"), (p_237618_1_) -> {
         if (this.trialsAvailable && !this.createdTrial) {
            Util.getPlatform().openUri("https://aka.ms/startjavarealmstrial");
            this.minecraft.setScreen(this.lastScreen);
         }
      }));
      this.buyARealmButton = this.addButton(new Button(this.width / 2 + 52, this.popupY0() + 160 - 20, 98, 20, new TranslationTextComponent("mco.selectServer.buy"), (p_237612_0_) -> {
         Util.getPlatform().openUri("https://aka.ms/BuyJavaRealms");
      }));
      RealmsServer realmsserver = this.findServer(this.selectedServerId);
      this.updateButtonStates(realmsserver);
   }

   private void updateButtonStates(@Nullable RealmsServer p_223915_1_) {
      this.playButton.active = this.shouldPlayButtonBeActive(p_223915_1_) && !this.shouldShowPopup();
      this.renewButton.visible = this.shouldRenewButtonBeActive(p_223915_1_);
      this.configureButton.visible = this.shouldConfigureButtonBeVisible(p_223915_1_);
      this.leaveButton.visible = this.shouldLeaveButtonBeVisible(p_223915_1_);
      boolean flag = this.shouldShowPopup() && this.trialsAvailable && !this.createdTrial;
      this.createTrialButton.visible = flag;
      this.createTrialButton.active = flag;
      this.buyARealmButton.visible = this.shouldShowPopup();
      this.closeButton.visible = this.shouldShowPopup() && this.popupOpenedByUser;
      this.renewButton.active = !this.shouldShowPopup();
      this.configureButton.active = !this.shouldShowPopup();
      this.leaveButton.active = !this.shouldShowPopup();
      this.newsButton.active = true;
      this.pendingInvitesButton.active = true;
      this.backButton.active = true;
      this.showPopupButton.active = !this.shouldShowPopup();
   }

   private boolean shouldShowPopupButton() {
      return (!this.shouldShowPopup() || this.popupOpenedByUser) && hasParentalConsent() && this.hasFetchedServers;
   }

   private boolean shouldPlayButtonBeActive(@Nullable RealmsServer p_223897_1_) {
      return p_223897_1_ != null && !p_223897_1_.expired && p_223897_1_.state == RealmsServer.Status.OPEN;
   }

   private boolean shouldRenewButtonBeActive(@Nullable RealmsServer p_223920_1_) {
      return p_223920_1_ != null && p_223920_1_.expired && this.isSelfOwnedServer(p_223920_1_);
   }

   private boolean shouldConfigureButtonBeVisible(@Nullable RealmsServer p_223941_1_) {
      return p_223941_1_ != null && this.isSelfOwnedServer(p_223941_1_);
   }

   private boolean shouldLeaveButtonBeVisible(@Nullable RealmsServer p_223959_1_) {
      return p_223959_1_ != null && !this.isSelfOwnedServer(p_223959_1_);
   }

   public void tick() {
      super.tick();
      this.justClosedPopup = false;
      ++this.animTick;
      --this.clicks;
      if (this.clicks < 0) {
         this.clicks = 0;
      }

      if (hasParentalConsent()) {
         REALMS_DATA_FETCHER.init();
         if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.SERVER_LIST)) {
            List<RealmsServer> list = REALMS_DATA_FETCHER.getServers();
            this.realmSelectionList.clear();
            boolean flag = !this.hasFetchedServers;
            if (flag) {
               this.hasFetchedServers = true;
            }

            if (list != null) {
               boolean flag1 = false;

               for(RealmsServer realmsserver : list) {
                  if (this.isSelfOwnedNonExpiredServer(realmsserver)) {
                     flag1 = true;
                  }
               }

               this.realmsServers = list;
               if (this.shouldShowMessageInList()) {
                  this.realmSelectionList.addMessageEntry(new RealmsMainScreen.TrialServerEntry());
               }

               for(RealmsServer realmsserver1 : this.realmsServers) {
                  this.realmSelectionList.addEntry(new RealmsMainScreen.ServerEntry(realmsserver1));
               }

               if (!regionsPinged && flag1) {
                  regionsPinged = true;
                  this.pingRegions();
               }
            }

            if (flag) {
               this.addButtons();
            }
         }

         if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.PENDING_INVITE)) {
            this.numberOfPendingInvites = REALMS_DATA_FETCHER.getPendingInvitesCount();
            if (this.numberOfPendingInvites > 0 && this.inviteNarrationLimiter.tryAcquire(1)) {
               RealmsNarratorHelper.now(I18n.get("mco.configure.world.invite.narration", this.numberOfPendingInvites));
            }
         }

         if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.TRIAL_AVAILABLE) && !this.createdTrial) {
            boolean flag2 = REALMS_DATA_FETCHER.isTrialAvailable();
            if (flag2 != this.trialsAvailable && this.shouldShowPopup()) {
               this.trialsAvailable = flag2;
               this.showingPopup = false;
            } else {
               this.trialsAvailable = flag2;
            }
         }

         if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.LIVE_STATS)) {
            RealmsServerPlayerLists realmsserverplayerlists = REALMS_DATA_FETCHER.getLivestats();

            for(RealmsServerPlayerList realmsserverplayerlist : realmsserverplayerlists.servers) {
               for(RealmsServer realmsserver2 : this.realmsServers) {
                  if (realmsserver2.id == realmsserverplayerlist.serverId) {
                     realmsserver2.updateServerPing(realmsserverplayerlist);
                     break;
                  }
               }
            }
         }

         if (REALMS_DATA_FETCHER.isFetchedSinceLastTry(RealmsDataFetcher.Task.UNREAD_NEWS)) {
            this.hasUnreadNews = REALMS_DATA_FETCHER.hasUnreadNews();
            this.newsLink = REALMS_DATA_FETCHER.newsLink();
         }

         REALMS_DATA_FETCHER.markClean();
         if (this.shouldShowPopup()) {
            ++this.carouselTick;
         }

         if (this.showPopupButton != null) {
            this.showPopupButton.visible = this.shouldShowPopupButton();
         }

      }
   }

   private void pingRegions() {
      (new Thread(() -> {
         List<RegionPingResult> list = Ping.pingAllRegions();
         RealmsClient realmsclient = RealmsClient.create();
         PingResult pingresult = new PingResult();
         pingresult.pingResults = list;
         pingresult.worldIds = this.getOwnedNonExpiredWorldIds();

         try {
            realmsclient.sendPingResults(pingresult);
         } catch (Throwable throwable) {
            LOGGER.warn("Could not send ping result to Realms: ", throwable);
         }

      })).start();
   }

   private List<Long> getOwnedNonExpiredWorldIds() {
      List<Long> list = Lists.newArrayList();

      for(RealmsServer realmsserver : this.realmsServers) {
         if (this.isSelfOwnedNonExpiredServer(realmsserver)) {
            list.add(realmsserver.id);
         }
      }

      return list;
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
      this.stopRealmsFetcher();
   }

   private void onRenew() {
      RealmsServer realmsserver = this.findServer(this.selectedServerId);
      if (realmsserver != null) {
         String s = "https://aka.ms/ExtendJavaRealms?subscriptionId=" + realmsserver.remoteSubscriptionId + "&profileId=" + this.minecraft.getUser().getUuid() + "&ref=" + (realmsserver.expiredTrial ? "expiredTrial" : "expiredRealm");
         this.minecraft.keyboardHandler.setClipboard(s);
         Util.getPlatform().openUri(s);
      }
   }

   private void checkClientCompatability() {
      if (!checkedClientCompatability) {
         checkedClientCompatability = true;
         (new Thread("MCO Compatability Checker #1") {
            public void run() {
               RealmsClient realmsclient = RealmsClient.create();

               try {
                  RealmsClient.CompatibleVersionResponse realmsclient$compatibleversionresponse = realmsclient.clientCompatible();
                  if (realmsclient$compatibleversionresponse == RealmsClient.CompatibleVersionResponse.OUTDATED) {
                     RealmsMainScreen.realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen, true);
                     RealmsMainScreen.this.minecraft.execute(() -> {
                        RealmsMainScreen.this.minecraft.setScreen(RealmsMainScreen.realmsGenericErrorScreen);
                     });
                     return;
                  }

                  if (realmsclient$compatibleversionresponse == RealmsClient.CompatibleVersionResponse.OTHER) {
                     RealmsMainScreen.realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen, false);
                     RealmsMainScreen.this.minecraft.execute(() -> {
                        RealmsMainScreen.this.minecraft.setScreen(RealmsMainScreen.realmsGenericErrorScreen);
                     });
                     return;
                  }

                  RealmsMainScreen.this.checkParentalConsent();
               } catch (RealmsServiceException realmsserviceexception) {
                  RealmsMainScreen.checkedClientCompatability = false;
                  RealmsMainScreen.LOGGER.error("Couldn't connect to realms", (Throwable)realmsserviceexception);
                  if (realmsserviceexception.httpResultCode == 401) {
                     RealmsMainScreen.realmsGenericErrorScreen = new RealmsGenericErrorScreen(new TranslationTextComponent("mco.error.invalid.session.title"), new TranslationTextComponent("mco.error.invalid.session.message"), RealmsMainScreen.this.lastScreen);
                     RealmsMainScreen.this.minecraft.execute(() -> {
                        RealmsMainScreen.this.minecraft.setScreen(RealmsMainScreen.realmsGenericErrorScreen);
                     });
                  } else {
                     RealmsMainScreen.this.minecraft.execute(() -> {
                        RealmsMainScreen.this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, RealmsMainScreen.this.lastScreen));
                     });
                  }
               }

            }
         }).start();
      }

   }

   private void checkUnreadNews() {
   }

   private void checkParentalConsent() {
      (new Thread("MCO Compatability Checker #1") {
         public void run() {
            RealmsClient realmsclient = RealmsClient.create();

            try {
               Boolean obool = realmsclient.mcoEnabled();
               if (obool) {
                  RealmsMainScreen.LOGGER.info("Realms is available for this user");
                  RealmsMainScreen.hasParentalConsent = true;
               } else {
                  RealmsMainScreen.LOGGER.info("Realms is not available for this user");
                  RealmsMainScreen.hasParentalConsent = false;
                  RealmsMainScreen.this.minecraft.execute(() -> {
                     RealmsMainScreen.this.minecraft.setScreen(new RealmsParentalConsentScreen(RealmsMainScreen.this.lastScreen));
                  });
               }

               RealmsMainScreen.checkedParentalConsent = true;
            } catch (RealmsServiceException realmsserviceexception) {
               RealmsMainScreen.LOGGER.error("Couldn't connect to realms", (Throwable)realmsserviceexception);
               RealmsMainScreen.this.minecraft.execute(() -> {
                  RealmsMainScreen.this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, RealmsMainScreen.this.lastScreen));
               });
            }

         }
      }).start();
   }

   private void switchToStage() {
      if (RealmsClient.currentEnvironment != RealmsClient.Environment.STAGE) {
         (new Thread("MCO Stage Availability Checker #1") {
            public void run() {
               RealmsClient realmsclient = RealmsClient.create();

               try {
                  Boolean obool = realmsclient.stageAvailable();
                  if (obool) {
                     RealmsClient.switchToStage();
                     RealmsMainScreen.LOGGER.info("Switched to stage");
                     RealmsMainScreen.REALMS_DATA_FETCHER.forceUpdate();
                  }
               } catch (RealmsServiceException realmsserviceexception) {
                  RealmsMainScreen.LOGGER.error("Couldn't connect to Realms: " + realmsserviceexception);
               }

            }
         }).start();
      }

   }

   private void switchToLocal() {
      if (RealmsClient.currentEnvironment != RealmsClient.Environment.LOCAL) {
         (new Thread("MCO Local Availability Checker #1") {
            public void run() {
               RealmsClient realmsclient = RealmsClient.create();

               try {
                  Boolean obool = realmsclient.stageAvailable();
                  if (obool) {
                     RealmsClient.switchToLocal();
                     RealmsMainScreen.LOGGER.info("Switched to local");
                     RealmsMainScreen.REALMS_DATA_FETCHER.forceUpdate();
                  }
               } catch (RealmsServiceException realmsserviceexception) {
                  RealmsMainScreen.LOGGER.error("Couldn't connect to Realms: " + realmsserviceexception);
               }

            }
         }).start();
      }

   }

   private void switchToProd() {
      RealmsClient.switchToProd();
      REALMS_DATA_FETCHER.forceUpdate();
   }

   private void stopRealmsFetcher() {
      REALMS_DATA_FETCHER.stop();
   }

   private void configureClicked(RealmsServer p_223966_1_) {
      if (this.minecraft.getUser().getUuid().equals(p_223966_1_.ownerUUID) || overrideConfigure) {
         this.saveListScrollPosition();
         this.minecraft.setScreen(new RealmsConfigureWorldScreen(this, p_223966_1_.id));
      }

   }

   private void leaveClicked(@Nullable RealmsServer p_223906_1_) {
      if (p_223906_1_ != null && !this.minecraft.getUser().getUuid().equals(p_223906_1_.ownerUUID)) {
         this.saveListScrollPosition();
         ITextComponent itextcomponent = new TranslationTextComponent("mco.configure.world.leave.question.line1");
         ITextComponent itextcomponent1 = new TranslationTextComponent("mco.configure.world.leave.question.line2");
         this.minecraft.setScreen(new RealmsLongConfirmationScreen(this::leaveServer, RealmsLongConfirmationScreen.Type.Info, itextcomponent, itextcomponent1, true));
      }

   }

   private void saveListScrollPosition() {
      lastScrollYPosition = (int)this.realmSelectionList.getScrollAmount();
   }

   @Nullable
   private RealmsServer findServer(long p_223967_1_) {
      for(RealmsServer realmsserver : this.realmsServers) {
         if (realmsserver.id == p_223967_1_) {
            return realmsserver;
         }
      }

      return null;
   }

   private void leaveServer(boolean p_237625_1_) {
      if (p_237625_1_) {
         (new Thread("Realms-leave-server") {
            public void run() {
               try {
                  RealmsServer realmsserver = RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId);
                  if (realmsserver != null) {
                     RealmsClient realmsclient = RealmsClient.create();
                     realmsclient.uninviteMyselfFrom(realmsserver.id);
                     RealmsMainScreen.this.minecraft.execute(() -> {
                        RealmsMainScreen.this.removeServer(realmsserver);
                     });
                  }
               } catch (RealmsServiceException realmsserviceexception) {
                  RealmsMainScreen.LOGGER.error("Couldn't configure world");
                  RealmsMainScreen.this.minecraft.execute(() -> {
                     RealmsMainScreen.this.minecraft.setScreen(new RealmsGenericErrorScreen(realmsserviceexception, RealmsMainScreen.this));
                  });
               }

            }
         }).start();
      }

      this.minecraft.setScreen(this);
   }

   private void removeServer(RealmsServer p_243059_1_) {
      REALMS_DATA_FETCHER.removeItem(p_243059_1_);
      this.realmsServers.remove(p_243059_1_);
      this.realmSelectionList.children().removeIf((p_243041_1_) -> {
         return p_243041_1_ instanceof RealmsMainScreen.ServerEntry && ((RealmsMainScreen.ServerEntry)p_243041_1_).serverData.id == this.selectedServerId;
      });
      this.realmSelectionList.setSelected((RealmsMainScreen.ListEntry)null);
      this.updateButtonStates((RealmsServer)null);
      this.selectedServerId = -1L;
      this.playButton.active = false;
   }

   public void removeSelection() {
      this.selectedServerId = -1L;
   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      if (p_231046_1_ == 256) {
         this.keyCombos.forEach(KeyCombo::reset);
         this.onClosePopup();
         return true;
      } else {
         return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
      }
   }

   private void onClosePopup() {
      if (this.shouldShowPopup() && this.popupOpenedByUser) {
         this.popupOpenedByUser = false;
      } else {
         this.minecraft.setScreen(this.lastScreen);
      }

   }

   public boolean charTyped(char p_231042_1_, int p_231042_2_) {
      this.keyCombos.forEach((p_237578_1_) -> {
         p_237578_1_.keyPressed(p_231042_1_);
      });
      return true;
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.hoveredElement = RealmsMainScreen.ServerState.NONE;
      this.toolTip = null;
      this.renderBackground(p_230430_1_);
      this.realmSelectionList.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      this.drawRealmsLogo(p_230430_1_, this.width / 2 - 50, 7);
      if (RealmsClient.currentEnvironment == RealmsClient.Environment.STAGE) {
         this.renderStage(p_230430_1_);
      }

      if (RealmsClient.currentEnvironment == RealmsClient.Environment.LOCAL) {
         this.renderLocal(p_230430_1_);
      }

      if (this.shouldShowPopup()) {
         this.drawPopup(p_230430_1_, p_230430_2_, p_230430_3_);
      } else {
         if (this.showingPopup) {
            this.updateButtonStates((RealmsServer)null);
            if (!this.children.contains(this.realmSelectionList)) {
               this.children.add(this.realmSelectionList);
            }

            RealmsServer realmsserver = this.findServer(this.selectedServerId);
            this.playButton.active = this.shouldPlayButtonBeActive(realmsserver);
         }

         this.showingPopup = false;
      }

      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      if (this.toolTip != null) {
         this.renderMousehoverTooltip(p_230430_1_, this.toolTip, p_230430_2_, p_230430_3_);
      }

      if (this.trialsAvailable && !this.createdTrial && this.shouldShowPopup()) {
         this.minecraft.getTextureManager().bind(TRIAL_ICON_LOCATION);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         int k = 8;
         int i = 8;
         int j = 0;
         if ((Util.getMillis() / 800L & 1L) == 1L) {
            j = 8;
         }

         AbstractGui.blit(p_230430_1_, this.createTrialButton.x + this.createTrialButton.getWidth() - 8 - 4, this.createTrialButton.y + this.createTrialButton.getHeight() / 2 - 4, 0.0F, (float)j, 8, 8, 8, 16);
      }

   }

   private void drawRealmsLogo(MatrixStack p_237579_1_, int p_237579_2_, int p_237579_3_) {
      this.minecraft.getTextureManager().bind(LOGO_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RenderSystem.scalef(0.5F, 0.5F, 0.5F);
      AbstractGui.blit(p_237579_1_, p_237579_2_ * 2, p_237579_3_ * 2 - 5, 0.0F, 0.0F, 200, 50, 200, 50);
      RenderSystem.popMatrix();
   }

   public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
      if (this.isOutsidePopup(p_231044_1_, p_231044_3_) && this.popupOpenedByUser) {
         this.popupOpenedByUser = false;
         this.justClosedPopup = true;
         return true;
      } else {
         return super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
      }
   }

   private boolean isOutsidePopup(double p_223979_1_, double p_223979_3_) {
      int i = this.popupX0();
      int j = this.popupY0();
      return p_223979_1_ < (double)(i - 5) || p_223979_1_ > (double)(i + 315) || p_223979_3_ < (double)(j - 5) || p_223979_3_ > (double)(j + 171);
   }

   private void drawPopup(MatrixStack p_237605_1_, int p_237605_2_, int p_237605_3_) {
      int i = this.popupX0();
      int j = this.popupY0();
      if (!this.showingPopup) {
         this.carouselIndex = 0;
         this.carouselTick = 0;
         this.hasSwitchedCarouselImage = true;
         this.updateButtonStates((RealmsServer)null);
         if (this.children.contains(this.realmSelectionList)) {
            IGuiEventListener iguieventlistener = this.realmSelectionList;
            if (!this.children.remove(iguieventlistener)) {
               LOGGER.error("Unable to remove widget: " + iguieventlistener);
            }
         }

         RealmsNarratorHelper.now(POPUP_TEXT.getString());
      }

      if (this.hasFetchedServers) {
         this.showingPopup = true;
      }

      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.7F);
      RenderSystem.enableBlend();
      this.minecraft.getTextureManager().bind(DARKEN_LOCATION);
      int l = 0;
      int k = 32;
      AbstractGui.blit(p_237605_1_, 0, 32, 0.0F, 0.0F, this.width, this.height - 40 - 32, 310, 166);
      RenderSystem.disableBlend();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(POPUP_LOCATION);
      AbstractGui.blit(p_237605_1_, i, j, 0.0F, 0.0F, 310, 166, 310, 166);
      if (!teaserImages.isEmpty()) {
         this.minecraft.getTextureManager().bind(teaserImages.get(this.carouselIndex));
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         AbstractGui.blit(p_237605_1_, i + 7, j + 7, 0.0F, 0.0F, 195, 152, 195, 152);
         if (this.carouselTick % 95 < 5) {
            if (!this.hasSwitchedCarouselImage) {
               this.carouselIndex = (this.carouselIndex + 1) % teaserImages.size();
               this.hasSwitchedCarouselImage = true;
            }
         } else {
            this.hasSwitchedCarouselImage = false;
         }
      }

      this.formattedPopup.renderLeftAlignedNoShadow(p_237605_1_, this.width / 2 + 52, j + 7, 10, 5000268);
   }

   private int popupX0() {
      return (this.width - 310) / 2;
   }

   private int popupY0() {
      return this.height / 2 - 80;
   }

   private void drawInvitationPendingIcon(MatrixStack p_237581_1_, int p_237581_2_, int p_237581_3_, int p_237581_4_, int p_237581_5_, boolean p_237581_6_, boolean p_237581_7_) {
      int i = this.numberOfPendingInvites;
      boolean flag = this.inPendingInvitationArea((double)p_237581_2_, (double)p_237581_3_);
      boolean flag1 = p_237581_7_ && p_237581_6_;
      if (flag1) {
         float f = 0.25F + (1.0F + MathHelper.sin((float)this.animTick * 0.5F)) * 0.25F;
         int j = -16777216 | (int)(f * 64.0F) << 16 | (int)(f * 64.0F) << 8 | (int)(f * 64.0F) << 0;
         this.fillGradient(p_237581_1_, p_237581_4_ - 2, p_237581_5_ - 2, p_237581_4_ + 18, p_237581_5_ + 18, j, j);
         j = -16777216 | (int)(f * 255.0F) << 16 | (int)(f * 255.0F) << 8 | (int)(f * 255.0F) << 0;
         this.fillGradient(p_237581_1_, p_237581_4_ - 2, p_237581_5_ - 2, p_237581_4_ + 18, p_237581_5_ - 1, j, j);
         this.fillGradient(p_237581_1_, p_237581_4_ - 2, p_237581_5_ - 2, p_237581_4_ - 1, p_237581_5_ + 18, j, j);
         this.fillGradient(p_237581_1_, p_237581_4_ + 17, p_237581_5_ - 2, p_237581_4_ + 18, p_237581_5_ + 18, j, j);
         this.fillGradient(p_237581_1_, p_237581_4_ - 2, p_237581_5_ + 17, p_237581_4_ + 18, p_237581_5_ + 18, j, j);
      }

      this.minecraft.getTextureManager().bind(INVITE_ICON_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      boolean flag3 = p_237581_7_ && p_237581_6_;
      float f2 = flag3 ? 16.0F : 0.0F;
      AbstractGui.blit(p_237581_1_, p_237581_4_, p_237581_5_ - 6, f2, 0.0F, 15, 25, 31, 25);
      boolean flag2 = p_237581_7_ && i != 0;
      if (flag2) {
         int k = (Math.min(i, 6) - 1) * 8;
         int l = (int)(Math.max(0.0F, Math.max(MathHelper.sin((float)(10 + this.animTick) * 0.57F), MathHelper.cos((float)this.animTick * 0.35F))) * -6.0F);
         this.minecraft.getTextureManager().bind(INVITATION_ICONS_LOCATION);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         float f1 = flag ? 8.0F : 0.0F;
         AbstractGui.blit(p_237581_1_, p_237581_4_ + 4, p_237581_5_ + 4 + l, (float)k, f1, 8, 8, 48, 16);
      }

      int j1 = p_237581_2_ + 12;
      boolean flag4 = p_237581_7_ && flag;
      if (flag4) {
         ITextComponent itextcomponent = i == 0 ? NO_PENDING_INVITES_TEXT : PENDING_INVITES_TEXT;
         int i1 = this.font.width(itextcomponent);
         this.fillGradient(p_237581_1_, j1 - 3, p_237581_3_ - 3, j1 + i1 + 3, p_237581_3_ + 8 + 3, -1073741824, -1073741824);
         this.font.drawShadow(p_237581_1_, itextcomponent, (float)j1, (float)p_237581_3_, -1);
      }

   }

   private boolean inPendingInvitationArea(double p_223931_1_, double p_223931_3_) {
      int i = this.width / 2 + 50;
      int j = this.width / 2 + 66;
      int k = 11;
      int l = 23;
      if (this.numberOfPendingInvites != 0) {
         i -= 3;
         j += 3;
         k -= 5;
         l += 5;
      }

      return (double)i <= p_223931_1_ && p_223931_1_ <= (double)j && (double)k <= p_223931_3_ && p_223931_3_ <= (double)l;
   }

   public void play(RealmsServer p_223911_1_, Screen p_223911_2_) {
      if (p_223911_1_ != null) {
         try {
            if (!this.connectLock.tryLock(1L, TimeUnit.SECONDS)) {
               return;
            }

            if (this.connectLock.getHoldCount() > 1) {
               return;
            }
         } catch (InterruptedException interruptedexception) {
            return;
         }

         this.dontSetConnectedToRealms = true;
         this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(p_223911_2_, new ConnectingToRealmsAction(this, p_223911_2_, p_223911_1_, this.connectLock)));
      }

   }

   private boolean isSelfOwnedServer(RealmsServer p_223885_1_) {
      return p_223885_1_.ownerUUID != null && p_223885_1_.ownerUUID.equals(this.minecraft.getUser().getUuid());
   }

   private boolean isSelfOwnedNonExpiredServer(RealmsServer p_223991_1_) {
      return this.isSelfOwnedServer(p_223991_1_) && !p_223991_1_.expired;
   }

   private void drawExpired(MatrixStack p_237614_1_, int p_237614_2_, int p_237614_3_, int p_237614_4_, int p_237614_5_) {
      this.minecraft.getTextureManager().bind(EXPIRED_ICON_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      AbstractGui.blit(p_237614_1_, p_237614_2_, p_237614_3_, 0.0F, 0.0F, 10, 28, 10, 28);
      if (p_237614_4_ >= p_237614_2_ && p_237614_4_ <= p_237614_2_ + 9 && p_237614_5_ >= p_237614_3_ && p_237614_5_ <= p_237614_3_ + 27 && p_237614_5_ < this.height - 40 && p_237614_5_ > 32 && !this.shouldShowPopup()) {
         this.setTooltip(SERVER_EXPIRED_TOOLTIP);
      }

   }

   private void drawExpiring(MatrixStack p_237606_1_, int p_237606_2_, int p_237606_3_, int p_237606_4_, int p_237606_5_, int p_237606_6_) {
      this.minecraft.getTextureManager().bind(EXPIRES_SOON_ICON_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.animTick % 20 < 10) {
         AbstractGui.blit(p_237606_1_, p_237606_2_, p_237606_3_, 0.0F, 0.0F, 10, 28, 20, 28);
      } else {
         AbstractGui.blit(p_237606_1_, p_237606_2_, p_237606_3_, 10.0F, 0.0F, 10, 28, 20, 28);
      }

      if (p_237606_4_ >= p_237606_2_ && p_237606_4_ <= p_237606_2_ + 9 && p_237606_5_ >= p_237606_3_ && p_237606_5_ <= p_237606_3_ + 27 && p_237606_5_ < this.height - 40 && p_237606_5_ > 32 && !this.shouldShowPopup()) {
         if (p_237606_6_ <= 0) {
            this.setTooltip(SERVER_EXPIRES_SOON_TOOLTIP);
         } else if (p_237606_6_ == 1) {
            this.setTooltip(SERVER_EXPIRES_IN_DAY_TOOLTIP);
         } else {
            this.setTooltip(new TranslationTextComponent("mco.selectServer.expires.days", p_237606_6_));
         }
      }

   }

   private void drawOpen(MatrixStack p_237620_1_, int p_237620_2_, int p_237620_3_, int p_237620_4_, int p_237620_5_) {
      this.minecraft.getTextureManager().bind(ON_ICON_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      AbstractGui.blit(p_237620_1_, p_237620_2_, p_237620_3_, 0.0F, 0.0F, 10, 28, 10, 28);
      if (p_237620_4_ >= p_237620_2_ && p_237620_4_ <= p_237620_2_ + 9 && p_237620_5_ >= p_237620_3_ && p_237620_5_ <= p_237620_3_ + 27 && p_237620_5_ < this.height - 40 && p_237620_5_ > 32 && !this.shouldShowPopup()) {
         this.setTooltip(SERVER_OPEN_TOOLTIP);
      }

   }

   private void drawClose(MatrixStack p_237626_1_, int p_237626_2_, int p_237626_3_, int p_237626_4_, int p_237626_5_) {
      this.minecraft.getTextureManager().bind(OFF_ICON_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      AbstractGui.blit(p_237626_1_, p_237626_2_, p_237626_3_, 0.0F, 0.0F, 10, 28, 10, 28);
      if (p_237626_4_ >= p_237626_2_ && p_237626_4_ <= p_237626_2_ + 9 && p_237626_5_ >= p_237626_3_ && p_237626_5_ <= p_237626_3_ + 27 && p_237626_5_ < this.height - 40 && p_237626_5_ > 32 && !this.shouldShowPopup()) {
         this.setTooltip(SERVER_CLOSED_TOOLTIP);
      }

   }

   private void drawLeave(MatrixStack p_237630_1_, int p_237630_2_, int p_237630_3_, int p_237630_4_, int p_237630_5_) {
      boolean flag = false;
      if (p_237630_4_ >= p_237630_2_ && p_237630_4_ <= p_237630_2_ + 28 && p_237630_5_ >= p_237630_3_ && p_237630_5_ <= p_237630_3_ + 28 && p_237630_5_ < this.height - 40 && p_237630_5_ > 32 && !this.shouldShowPopup()) {
         flag = true;
      }

      this.minecraft.getTextureManager().bind(LEAVE_ICON_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float f = flag ? 28.0F : 0.0F;
      AbstractGui.blit(p_237630_1_, p_237630_2_, p_237630_3_, f, 0.0F, 28, 28, 56, 28);
      if (flag) {
         this.setTooltip(LEAVE_SERVER_TOOLTIP);
         this.hoveredElement = RealmsMainScreen.ServerState.LEAVE;
      }

   }

   private void drawConfigure(MatrixStack p_237633_1_, int p_237633_2_, int p_237633_3_, int p_237633_4_, int p_237633_5_) {
      boolean flag = false;
      if (p_237633_4_ >= p_237633_2_ && p_237633_4_ <= p_237633_2_ + 28 && p_237633_5_ >= p_237633_3_ && p_237633_5_ <= p_237633_3_ + 28 && p_237633_5_ < this.height - 40 && p_237633_5_ > 32 && !this.shouldShowPopup()) {
         flag = true;
      }

      this.minecraft.getTextureManager().bind(CONFIGURE_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float f = flag ? 28.0F : 0.0F;
      AbstractGui.blit(p_237633_1_, p_237633_2_, p_237633_3_, f, 0.0F, 28, 28, 56, 28);
      if (flag) {
         this.setTooltip(CONFIGURE_SERVER_TOOLTIP);
         this.hoveredElement = RealmsMainScreen.ServerState.CONFIGURE;
      }

   }

   protected void renderMousehoverTooltip(MatrixStack p_237583_1_, List<ITextComponent> p_237583_2_, int p_237583_3_, int p_237583_4_) {
      if (!p_237583_2_.isEmpty()) {
         int i = 0;
         int j = 0;

         for(ITextComponent itextcomponent : p_237583_2_) {
            int k = this.font.width(itextcomponent);
            if (k > j) {
               j = k;
            }
         }

         int i1 = p_237583_3_ - j - 5;
         int j1 = p_237583_4_;
         if (i1 < 0) {
            i1 = p_237583_3_ + 12;
         }

         for(ITextComponent itextcomponent1 : p_237583_2_) {
            int l = j1 - (i == 0 ? 3 : 0) + i;
            this.fillGradient(p_237583_1_, i1 - 3, l, i1 + j + 3, j1 + 8 + 3 + i, -1073741824, -1073741824);
            this.font.drawShadow(p_237583_1_, itextcomponent1, (float)i1, (float)(j1 + i), 16777215);
            i += 10;
         }

      }
   }

   private void renderMoreInfo(MatrixStack p_237580_1_, int p_237580_2_, int p_237580_3_, int p_237580_4_, int p_237580_5_, boolean p_237580_6_) {
      boolean flag = false;
      if (p_237580_2_ >= p_237580_4_ && p_237580_2_ <= p_237580_4_ + 20 && p_237580_3_ >= p_237580_5_ && p_237580_3_ <= p_237580_5_ + 20) {
         flag = true;
      }

      this.minecraft.getTextureManager().bind(QUESTIONMARK_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float f = p_237580_6_ ? 20.0F : 0.0F;
      AbstractGui.blit(p_237580_1_, p_237580_4_, p_237580_5_, f, 0.0F, 20, 20, 40, 20);
      if (flag) {
         this.setTooltip(SERVER_INFO_TOOLTIP);
      }

   }

   private void renderNews(MatrixStack p_237582_1_, int p_237582_2_, int p_237582_3_, boolean p_237582_4_, int p_237582_5_, int p_237582_6_, boolean p_237582_7_, boolean p_237582_8_) {
      boolean flag = false;
      if (p_237582_2_ >= p_237582_5_ && p_237582_2_ <= p_237582_5_ + 20 && p_237582_3_ >= p_237582_6_ && p_237582_3_ <= p_237582_6_ + 20) {
         flag = true;
      }

      this.minecraft.getTextureManager().bind(NEWS_LOCATION);
      if (p_237582_8_) {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         RenderSystem.color4f(0.5F, 0.5F, 0.5F, 1.0F);
      }

      boolean flag1 = p_237582_8_ && p_237582_7_;
      float f = flag1 ? 20.0F : 0.0F;
      AbstractGui.blit(p_237582_1_, p_237582_5_, p_237582_6_, f, 0.0F, 20, 20, 40, 20);
      if (flag && p_237582_8_) {
         this.setTooltip(NEWS_TOOLTIP);
      }

      if (p_237582_4_ && p_237582_8_) {
         int i = flag ? 0 : (int)(Math.max(0.0F, Math.max(MathHelper.sin((float)(10 + this.animTick) * 0.57F), MathHelper.cos((float)this.animTick * 0.35F))) * -6.0F);
         this.minecraft.getTextureManager().bind(INVITATION_ICONS_LOCATION);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         AbstractGui.blit(p_237582_1_, p_237582_5_ + 10, p_237582_6_ + 2 + i, 40.0F, 0.0F, 8, 8, 48, 16);
      }

   }

   private void renderLocal(MatrixStack p_237604_1_) {
      String s = "LOCAL!";
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RenderSystem.translatef((float)(this.width / 2 - 25), 20.0F, 0.0F);
      RenderSystem.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
      RenderSystem.scalef(1.5F, 1.5F, 1.5F);
      this.font.draw(p_237604_1_, "LOCAL!", 0.0F, 0.0F, 8388479);
      RenderSystem.popMatrix();
   }

   private void renderStage(MatrixStack p_237613_1_) {
      String s = "STAGE!";
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RenderSystem.translatef((float)(this.width / 2 - 25), 20.0F, 0.0F);
      RenderSystem.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
      RenderSystem.scalef(1.5F, 1.5F, 1.5F);
      this.font.draw(p_237613_1_, "STAGE!", 0.0F, 0.0F, -256);
      RenderSystem.popMatrix();
   }

   public RealmsMainScreen newScreen() {
      RealmsMainScreen realmsmainscreen = new RealmsMainScreen(this.lastScreen);
      realmsmainscreen.init(this.minecraft, this.width, this.height);
      return realmsmainscreen;
   }

   public static void updateTeaserImages(IResourceManager p_227932_0_) {
      Collection<ResourceLocation> collection = p_227932_0_.listResources("textures/gui/images", (p_227934_0_) -> {
         return p_227934_0_.endsWith(".png");
      });
      teaserImages = collection.stream().filter((p_227931_0_) -> {
         return p_227931_0_.getNamespace().equals("realms");
      }).collect(ImmutableList.toImmutableList());
   }

   private void setTooltip(ITextComponent... p_237603_1_) {
      this.toolTip = Arrays.asList(p_237603_1_);
   }

   private void pendingButtonPress(Button p_237598_1_) {
      this.minecraft.setScreen(new RealmsPendingInvitesScreen(this.lastScreen));
   }

   @OnlyIn(Dist.CLIENT)
   class CloseButton extends Button {
      public CloseButton() {
         super(RealmsMainScreen.this.popupX0() + 4, RealmsMainScreen.this.popupY0() + 4, 12, 12, new TranslationTextComponent("mco.selectServer.close"), null);
      }

      @Override
      public void onPress() {
            RealmsMainScreen.this.onClosePopup();
      }

      public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
         RealmsMainScreen.this.minecraft.getTextureManager().bind(RealmsMainScreen.CROSS_ICON_LOCATION);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         float f = this.isHovered() ? 12.0F : 0.0F;
         blit(p_230431_1_, this.x, this.y, 0.0F, f, 12, 12, 12, 24);
         if (this.isMouseOver((double)p_230431_2_, (double)p_230431_3_)) {
            RealmsMainScreen.this.setTooltip(this.getMessage());
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   class InfoButton extends Button {
      public InfoButton() {
         super(RealmsMainScreen.this.width - 37, 6, 20, 20, new TranslationTextComponent("mco.selectServer.info"), null);
      }
      @Override
      public void onPress() {
            RealmsMainScreen.this.popupOpenedByUser = !RealmsMainScreen.this.popupOpenedByUser;
      }

      public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
         RealmsMainScreen.this.renderMoreInfo(p_230431_1_, p_230431_2_, p_230431_3_, this.x, this.y, this.isHovered());
      }
   }

   @OnlyIn(Dist.CLIENT)
   abstract class ListEntry extends ExtendedList.AbstractListEntry<RealmsMainScreen.ListEntry> {
      private ListEntry() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   class NewsButton extends Button {
      public NewsButton() {
         super(RealmsMainScreen.this.width - 62, 6, 20, 20, StringTextComponent.EMPTY, null);
         this.setMessage(new TranslationTextComponent("mco.news"));
      }
      @Override
      public void onPress() {
            if (RealmsMainScreen.this.newsLink != null) {
               Util.getPlatform().openUri(RealmsMainScreen.this.newsLink);
               if (RealmsMainScreen.this.hasUnreadNews) {
                  RealmsPersistence.RealmsPersistenceData realmspersistence$realmspersistencedata = RealmsPersistence.readFile();
                  realmspersistence$realmspersistencedata.hasUnreadNews = false;
                  RealmsMainScreen.this.hasUnreadNews = false;
                  RealmsPersistence.writeFile(realmspersistence$realmspersistencedata);
               }

            }
      }

      public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
         RealmsMainScreen.this.renderNews(p_230431_1_, p_230431_2_, p_230431_3_, RealmsMainScreen.this.hasUnreadNews, this.x, this.y, this.isHovered(), this.active);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class PendingInvitesButton extends Button implements IScreen {
      public PendingInvitesButton() {
         super(RealmsMainScreen.this.width / 2 + 47, 6, 22, 22, StringTextComponent.EMPTY, null);
      }

      @Override
      public void onPress() {
            RealmsMainScreen.this.pendingButtonPress(this);
      }

      public void tick() {
         this.setMessage(new TranslationTextComponent(RealmsMainScreen.this.numberOfPendingInvites == 0 ? "mco.invites.nopending" : "mco.invites.pending"));
      }

      public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
         RealmsMainScreen.this.drawInvitationPendingIcon(p_230431_1_, p_230431_2_, p_230431_3_, this.x, this.y, this.isHovered(), this.active);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class ServerEntry extends RealmsMainScreen.ListEntry {
      private final RealmsServer serverData;

      public ServerEntry(RealmsServer p_i51666_2_) {
         this.serverData = p_i51666_2_;
      }

      public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
         this.renderMcoServerItem(this.serverData, p_230432_1_, p_230432_4_, p_230432_3_, p_230432_7_, p_230432_8_);
      }

      public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
         if (this.serverData.state == RealmsServer.Status.UNINITIALIZED) {
            RealmsMainScreen.this.selectedServerId = -1L;
            RealmsMainScreen.this.minecraft.setScreen(new RealmsCreateRealmScreen(this.serverData, RealmsMainScreen.this));
         } else {
            RealmsMainScreen.this.selectedServerId = this.serverData.id;
         }

         return true;
      }

      private void renderMcoServerItem(RealmsServer p_237678_1_, MatrixStack p_237678_2_, int p_237678_3_, int p_237678_4_, int p_237678_5_, int p_237678_6_) {
         this.renderLegacy(p_237678_1_, p_237678_2_, p_237678_3_ + 36, p_237678_4_, p_237678_5_, p_237678_6_);
      }

      private void renderLegacy(RealmsServer p_237679_1_, MatrixStack p_237679_2_, int p_237679_3_, int p_237679_4_, int p_237679_5_, int p_237679_6_) {
         if (p_237679_1_.state == RealmsServer.Status.UNINITIALIZED) {
            RealmsMainScreen.this.minecraft.getTextureManager().bind(RealmsMainScreen.WORLDICON_LOCATION);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableAlphaTest();
            AbstractGui.blit(p_237679_2_, p_237679_3_ + 10, p_237679_4_ + 6, 0.0F, 0.0F, 40, 20, 40, 20);
            float f = 0.5F + (1.0F + MathHelper.sin((float)RealmsMainScreen.this.animTick * 0.25F)) * 0.25F;
            int k2 = -16777216 | (int)(127.0F * f) << 16 | (int)(255.0F * f) << 8 | (int)(127.0F * f);
            AbstractGui.drawCenteredString(p_237679_2_, RealmsMainScreen.this.font, RealmsMainScreen.SERVER_UNITIALIZED_TEXT, p_237679_3_ + 10 + 40 + 75, p_237679_4_ + 12, k2);
         } else {
            int i = 225;
            int j = 2;
            if (p_237679_1_.expired) {
               RealmsMainScreen.this.drawExpired(p_237679_2_, p_237679_3_ + 225 - 14, p_237679_4_ + 2, p_237679_5_, p_237679_6_);
            } else if (p_237679_1_.state == RealmsServer.Status.CLOSED) {
               RealmsMainScreen.this.drawClose(p_237679_2_, p_237679_3_ + 225 - 14, p_237679_4_ + 2, p_237679_5_, p_237679_6_);
            } else if (RealmsMainScreen.this.isSelfOwnedServer(p_237679_1_) && p_237679_1_.daysLeft < 7) {
               RealmsMainScreen.this.drawExpiring(p_237679_2_, p_237679_3_ + 225 - 14, p_237679_4_ + 2, p_237679_5_, p_237679_6_, p_237679_1_.daysLeft);
            } else if (p_237679_1_.state == RealmsServer.Status.OPEN) {
               RealmsMainScreen.this.drawOpen(p_237679_2_, p_237679_3_ + 225 - 14, p_237679_4_ + 2, p_237679_5_, p_237679_6_);
            }

            if (!RealmsMainScreen.this.isSelfOwnedServer(p_237679_1_) && !RealmsMainScreen.overrideConfigure) {
               RealmsMainScreen.this.drawLeave(p_237679_2_, p_237679_3_ + 225, p_237679_4_ + 2, p_237679_5_, p_237679_6_);
            } else {
               RealmsMainScreen.this.drawConfigure(p_237679_2_, p_237679_3_ + 225, p_237679_4_ + 2, p_237679_5_, p_237679_6_);
            }

            if (!"0".equals(p_237679_1_.serverPing.nrOfPlayers)) {
               String s = TextFormatting.GRAY + "" + p_237679_1_.serverPing.nrOfPlayers;
               RealmsMainScreen.this.font.draw(p_237679_2_, s, (float)(p_237679_3_ + 207 - RealmsMainScreen.this.font.width(s)), (float)(p_237679_4_ + 3), 8421504);
               if (p_237679_5_ >= p_237679_3_ + 207 - RealmsMainScreen.this.font.width(s) && p_237679_5_ <= p_237679_3_ + 207 && p_237679_6_ >= p_237679_4_ + 1 && p_237679_6_ <= p_237679_4_ + 10 && p_237679_6_ < RealmsMainScreen.this.height - 40 && p_237679_6_ > 32 && !RealmsMainScreen.this.shouldShowPopup()) {
                  RealmsMainScreen.this.setTooltip(new StringTextComponent(p_237679_1_.serverPing.playerList));
               }
            }

            if (RealmsMainScreen.this.isSelfOwnedServer(p_237679_1_) && p_237679_1_.expired) {
               RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
               RenderSystem.enableBlend();
               RealmsMainScreen.this.minecraft.getTextureManager().bind(RealmsMainScreen.BUTTON_LOCATION);
               RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
               ITextComponent itextcomponent;
               ITextComponent itextcomponent1;
               if (p_237679_1_.expiredTrial) {
                  itextcomponent = RealmsMainScreen.TRIAL_EXPIRED_TEXT;
                  itextcomponent1 = RealmsMainScreen.SUBSCRIPTION_CREATE_TEXT;
               } else {
                  itextcomponent = RealmsMainScreen.SUBSCRIPTION_EXPIRED_TEXT;
                  itextcomponent1 = RealmsMainScreen.SUBSCRIPTION_RENEW_TEXT;
               }

               int l = RealmsMainScreen.this.font.width(itextcomponent1) + 17;
               int i1 = 16;
               int j1 = p_237679_3_ + RealmsMainScreen.this.font.width(itextcomponent) + 8;
               int k1 = p_237679_4_ + 13;
               boolean flag = false;
               if (p_237679_5_ >= j1 && p_237679_5_ < j1 + l && p_237679_6_ > k1 && p_237679_6_ <= k1 + 16 & p_237679_6_ < RealmsMainScreen.this.height - 40 && p_237679_6_ > 32 && !RealmsMainScreen.this.shouldShowPopup()) {
                  flag = true;
                  RealmsMainScreen.this.hoveredElement = RealmsMainScreen.ServerState.EXPIRED;
               }

               int l1 = flag ? 2 : 1;
               AbstractGui.blit(p_237679_2_, j1, k1, 0.0F, (float)(46 + l1 * 20), l / 2, 8, 256, 256);
               AbstractGui.blit(p_237679_2_, j1 + l / 2, k1, (float)(200 - l / 2), (float)(46 + l1 * 20), l / 2, 8, 256, 256);
               AbstractGui.blit(p_237679_2_, j1, k1 + 8, 0.0F, (float)(46 + l1 * 20 + 12), l / 2, 8, 256, 256);
               AbstractGui.blit(p_237679_2_, j1 + l / 2, k1 + 8, (float)(200 - l / 2), (float)(46 + l1 * 20 + 12), l / 2, 8, 256, 256);
               RenderSystem.disableBlend();
               int i2 = p_237679_4_ + 11 + 5;
               int j2 = flag ? 16777120 : 16777215;
               RealmsMainScreen.this.font.draw(p_237679_2_, itextcomponent, (float)(p_237679_3_ + 2), (float)(i2 + 1), 15553363);
               AbstractGui.drawCenteredString(p_237679_2_, RealmsMainScreen.this.font, itextcomponent1, j1 + l / 2, i2 + 1, j2);
            } else {
               if (p_237679_1_.worldType == RealmsServer.ServerType.MINIGAME) {
                  int l2 = 13413468;
                  int k = RealmsMainScreen.this.font.width(RealmsMainScreen.SELECT_MINIGAME_PREFIX);
                  RealmsMainScreen.this.font.draw(p_237679_2_, RealmsMainScreen.SELECT_MINIGAME_PREFIX, (float)(p_237679_3_ + 2), (float)(p_237679_4_ + 12), 13413468);
                  RealmsMainScreen.this.font.draw(p_237679_2_, p_237679_1_.getMinigameName(), (float)(p_237679_3_ + 2 + k), (float)(p_237679_4_ + 12), 7105644);
               } else {
                  RealmsMainScreen.this.font.draw(p_237679_2_, p_237679_1_.getDescription(), (float)(p_237679_3_ + 2), (float)(p_237679_4_ + 12), 7105644);
               }

               if (!RealmsMainScreen.this.isSelfOwnedServer(p_237679_1_)) {
                  RealmsMainScreen.this.font.draw(p_237679_2_, p_237679_1_.owner, (float)(p_237679_3_ + 2), (float)(p_237679_4_ + 12 + 11), 5000268);
               }
            }

            RealmsMainScreen.this.font.draw(p_237679_2_, p_237679_1_.getName(), (float)(p_237679_3_ + 2), (float)(p_237679_4_ + 1), 16777215);
            RealmsTextureManager.withBoundFace(p_237679_1_.ownerUUID, () -> {
               RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
               AbstractGui.blit(p_237679_2_, p_237679_3_ - 36, p_237679_4_, 32, 32, 8.0F, 8.0F, 8, 8, 64, 64);
               AbstractGui.blit(p_237679_2_, p_237679_3_ - 36, p_237679_4_, 32, 32, 40.0F, 8.0F, 8, 8, 64, 64);
            });
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   class ServerList extends RealmsObjectSelectionList<RealmsMainScreen.ListEntry> {
      private boolean showingMessage;

      public ServerList() {
         super(RealmsMainScreen.this.width, RealmsMainScreen.this.height, 32, RealmsMainScreen.this.height - 40, 36);
      }

      public void clear() {
         super.clear();
         this.showingMessage = false;
      }

      public int addMessageEntry(RealmsMainScreen.ListEntry p_241825_1_) {
         this.showingMessage = true;
         return this.addEntry(p_241825_1_);
      }

      public boolean isFocused() {
         return RealmsMainScreen.this.getFocused() == this;
      }

      public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
         if (p_231046_1_ != 257 && p_231046_1_ != 32 && p_231046_1_ != 335) {
            return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
         } else {
            ExtendedList.AbstractListEntry extendedlist$abstractlistentry = this.getSelected();
            return extendedlist$abstractlistentry == null ? super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_) : extendedlist$abstractlistentry.mouseClicked(0.0D, 0.0D, 0);
         }
      }

      public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
         if (p_231044_5_ == 0 && p_231044_1_ < (double)this.getScrollbarPosition() && p_231044_3_ >= (double)this.y0 && p_231044_3_ <= (double)this.y1) {
            int i = RealmsMainScreen.this.realmSelectionList.getRowLeft();
            int j = this.getScrollbarPosition();
            int k = (int)Math.floor(p_231044_3_ - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
            int l = k / this.itemHeight;
            if (p_231044_1_ >= (double)i && p_231044_1_ <= (double)j && l >= 0 && k >= 0 && l < this.getItemCount()) {
               this.itemClicked(k, l, p_231044_1_, p_231044_3_, this.width);
               RealmsMainScreen.this.clicks = RealmsMainScreen.this.clicks + 7;
               this.selectItem(l);
            }

            return true;
         } else {
            return super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
         }
      }

      public void selectItem(int p_231400_1_) {
         this.setSelectedItem(p_231400_1_);
         if (p_231400_1_ != -1) {
            RealmsServer realmsserver;
            if (this.showingMessage) {
               if (p_231400_1_ == 0) {
                  realmsserver = null;
               } else {
                  if (p_231400_1_ - 1 >= RealmsMainScreen.this.realmsServers.size()) {
                     RealmsMainScreen.this.selectedServerId = -1L;
                     return;
                  }

                  realmsserver = RealmsMainScreen.this.realmsServers.get(p_231400_1_ - 1);
               }
            } else {
               if (p_231400_1_ >= RealmsMainScreen.this.realmsServers.size()) {
                  RealmsMainScreen.this.selectedServerId = -1L;
                  return;
               }

               realmsserver = RealmsMainScreen.this.realmsServers.get(p_231400_1_);
            }

            RealmsMainScreen.this.updateButtonStates(realmsserver);
            if (realmsserver == null) {
               RealmsMainScreen.this.selectedServerId = -1L;
            } else if (realmsserver.state == RealmsServer.Status.UNINITIALIZED) {
               RealmsMainScreen.this.selectedServerId = -1L;
            } else {
               RealmsMainScreen.this.selectedServerId = realmsserver.id;
               if (RealmsMainScreen.this.clicks >= 10 && RealmsMainScreen.this.playButton.active) {
                  RealmsMainScreen.this.play(RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId), RealmsMainScreen.this);
               }

            }
         }
      }

      public void setSelected(@Nullable RealmsMainScreen.ListEntry p_241215_1_) {
         super.setSelected(p_241215_1_);
         int i = this.children().indexOf(p_241215_1_);
         if (this.showingMessage && i == 0) {
            RealmsNarratorHelper.now(I18n.get("mco.trial.message.line1"), I18n.get("mco.trial.message.line2"));
         } else if (!this.showingMessage || i > 0) {
            RealmsServer realmsserver = RealmsMainScreen.this.realmsServers.get(i - (this.showingMessage ? 1 : 0));
            RealmsMainScreen.this.selectedServerId = realmsserver.id;
            RealmsMainScreen.this.updateButtonStates(realmsserver);
            if (realmsserver.state == RealmsServer.Status.UNINITIALIZED) {
               RealmsNarratorHelper.now(I18n.get("mco.selectServer.uninitialized") + I18n.get("mco.gui.button"));
            } else {
               RealmsNarratorHelper.now(I18n.get("narrator.select", realmsserver.name));
            }
         }

      }

      public void itemClicked(int p_231401_1_, int p_231401_2_, double p_231401_3_, double p_231401_5_, int p_231401_7_) {
         if (this.showingMessage) {
            if (p_231401_2_ == 0) {
               RealmsMainScreen.this.popupOpenedByUser = true;
               return;
            }

            --p_231401_2_;
         }

         if (p_231401_2_ < RealmsMainScreen.this.realmsServers.size()) {
            RealmsServer realmsserver = RealmsMainScreen.this.realmsServers.get(p_231401_2_);
            if (realmsserver != null) {
               if (realmsserver.state == RealmsServer.Status.UNINITIALIZED) {
                  RealmsMainScreen.this.selectedServerId = -1L;
                  Minecraft.getInstance().setScreen(new RealmsCreateRealmScreen(realmsserver, RealmsMainScreen.this));
               } else {
                  RealmsMainScreen.this.selectedServerId = realmsserver.id;
               }

               if (RealmsMainScreen.this.hoveredElement == RealmsMainScreen.ServerState.CONFIGURE) {
                  RealmsMainScreen.this.selectedServerId = realmsserver.id;
                  RealmsMainScreen.this.configureClicked(realmsserver);
               } else if (RealmsMainScreen.this.hoveredElement == RealmsMainScreen.ServerState.LEAVE) {
                  RealmsMainScreen.this.selectedServerId = realmsserver.id;
                  RealmsMainScreen.this.leaveClicked(realmsserver);
               } else if (RealmsMainScreen.this.hoveredElement == RealmsMainScreen.ServerState.EXPIRED) {
                  RealmsMainScreen.this.onRenew();
               }

            }
         }
      }

      public int getMaxPosition() {
         return this.getItemCount() * 36;
      }

      public int getRowWidth() {
         return 300;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static enum ServerState {
      NONE,
      EXPIRED,
      LEAVE,
      CONFIGURE;
   }

   @OnlyIn(Dist.CLIENT)
   class TrialServerEntry extends RealmsMainScreen.ListEntry {
      private TrialServerEntry() {
      }

      public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
         this.renderTrialItem(p_230432_1_, p_230432_2_, p_230432_4_, p_230432_3_, p_230432_7_, p_230432_8_);
      }

      public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
         RealmsMainScreen.this.popupOpenedByUser = true;
         return true;
      }

      private void renderTrialItem(MatrixStack p_237681_1_, int p_237681_2_, int p_237681_3_, int p_237681_4_, int p_237681_5_, int p_237681_6_) {
         int i = p_237681_4_ + 8;
         int j = 0;
         boolean flag = false;
         if (p_237681_3_ <= p_237681_5_ && p_237681_5_ <= (int)RealmsMainScreen.this.realmSelectionList.getScrollAmount() && p_237681_4_ <= p_237681_6_ && p_237681_6_ <= p_237681_4_ + 32) {
            flag = true;
         }

         int k = 8388479;
         if (flag && !RealmsMainScreen.this.shouldShowPopup()) {
            k = 6077788;
         }

         for(ITextComponent itextcomponent : RealmsMainScreen.TRIAL_MESSAGE_LINES) {
            AbstractGui.drawCenteredString(p_237681_1_, RealmsMainScreen.this.font, itextcomponent, RealmsMainScreen.this.width / 2, i + j, k);
            j += 10;
         }

      }
   }
}
