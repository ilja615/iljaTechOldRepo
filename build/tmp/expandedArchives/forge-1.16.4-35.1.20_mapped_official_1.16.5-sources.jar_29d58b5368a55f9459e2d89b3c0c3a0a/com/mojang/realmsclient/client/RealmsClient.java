package com.mojang.realmsclient.client;

import com.mojang.realmsclient.dto.BackupList;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.dto.PendingInvitesList;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsDescriptionDto;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.dto.RealmsServerList;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.RealmsWorldResetDto;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsHttpException;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.realms.PersistenceSerializer;
import net.minecraft.util.SharedConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsClient {
   public static RealmsClient.Environment currentEnvironment = RealmsClient.Environment.PRODUCTION;
   private static boolean initialized;
   private static final Logger LOGGER = LogManager.getLogger();
   private final String sessionId;
   private final String username;
   private final Minecraft minecraft;
   private static final PersistenceSerializer GSON = new PersistenceSerializer();

   public static RealmsClient create() {
      Minecraft minecraft = Minecraft.getInstance();
      String s = minecraft.getUser().getName();
      String s1 = minecraft.getUser().getSessionId();
      if (!initialized) {
         initialized = true;
         String s2 = System.getenv("realms.environment");
         if (s2 == null) {
            s2 = System.getProperty("realms.environment");
         }

         if (s2 != null) {
            if ("LOCAL".equals(s2)) {
               switchToLocal();
            } else if ("STAGE".equals(s2)) {
               switchToStage();
            }
         }
      }

      return new RealmsClient(s1, s, minecraft);
   }

   public static void switchToStage() {
      currentEnvironment = RealmsClient.Environment.STAGE;
   }

   public static void switchToProd() {
      currentEnvironment = RealmsClient.Environment.PRODUCTION;
   }

   public static void switchToLocal() {
      currentEnvironment = RealmsClient.Environment.LOCAL;
   }

   public RealmsClient(String p_i244721_1_, String p_i244721_2_, Minecraft p_i244721_3_) {
      this.sessionId = p_i244721_1_;
      this.username = p_i244721_2_;
      this.minecraft = p_i244721_3_;
      RealmsClientConfig.setProxy(p_i244721_3_.getProxy());
   }

   public RealmsServerList listWorlds() throws RealmsServiceException {
      String s = this.url("worlds");
      String s1 = this.execute(Request.get(s));
      return RealmsServerList.parse(s1);
   }

   public RealmsServer getOwnWorld(long p_224935_1_) throws RealmsServiceException {
      String s = this.url("worlds" + "/$ID".replace("$ID", String.valueOf(p_224935_1_)));
      String s1 = this.execute(Request.get(s));
      return RealmsServer.parse(s1);
   }

   public RealmsServerPlayerLists getLiveStats() throws RealmsServiceException {
      String s = this.url("activities/liveplayerlist");
      String s1 = this.execute(Request.get(s));
      return RealmsServerPlayerLists.parse(s1);
   }

   public RealmsServerAddress join(long p_224904_1_) throws RealmsServiceException {
      String s = this.url("worlds" + "/v1/$ID/join/pc".replace("$ID", "" + p_224904_1_));
      String s1 = this.execute(Request.get(s, 5000, 30000));
      return RealmsServerAddress.parse(s1);
   }

   public void initializeWorld(long p_224900_1_, String p_224900_3_, String p_224900_4_) throws RealmsServiceException {
      RealmsDescriptionDto realmsdescriptiondto = new RealmsDescriptionDto(p_224900_3_, p_224900_4_);
      String s = this.url("worlds" + "/$WORLD_ID/initialize".replace("$WORLD_ID", String.valueOf(p_224900_1_)));
      String s1 = GSON.toJson(realmsdescriptiondto);
      this.execute(Request.post(s, s1, 5000, 10000));
   }

   public Boolean mcoEnabled() throws RealmsServiceException {
      String s = this.url("mco/available");
      String s1 = this.execute(Request.get(s));
      return Boolean.valueOf(s1);
   }

   public Boolean stageAvailable() throws RealmsServiceException {
      String s = this.url("mco/stageAvailable");
      String s1 = this.execute(Request.get(s));
      return Boolean.valueOf(s1);
   }

   public RealmsClient.CompatibleVersionResponse clientCompatible() throws RealmsServiceException {
      String s = this.url("mco/client/compatible");
      String s1 = this.execute(Request.get(s));

      try {
         return RealmsClient.CompatibleVersionResponse.valueOf(s1);
      } catch (IllegalArgumentException illegalargumentexception) {
         throw new RealmsServiceException(500, "Could not check compatible version, got response: " + s1, -1, "");
      }
   }

   public void uninvite(long p_224908_1_, String p_224908_3_) throws RealmsServiceException {
      String s = this.url("invites" + "/$WORLD_ID/invite/$UUID".replace("$WORLD_ID", String.valueOf(p_224908_1_)).replace("$UUID", p_224908_3_));
      this.execute(Request.delete(s));
   }

   public void uninviteMyselfFrom(long p_224912_1_) throws RealmsServiceException {
      String s = this.url("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_224912_1_)));
      this.execute(Request.delete(s));
   }

   public RealmsServer invite(long p_224910_1_, String p_224910_3_) throws RealmsServiceException {
      PlayerInfo playerinfo = new PlayerInfo();
      playerinfo.setName(p_224910_3_);
      String s = this.url("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_224910_1_)));
      String s1 = this.execute(Request.post(s, GSON.toJson(playerinfo)));
      return RealmsServer.parse(s1);
   }

   public BackupList backupsFor(long p_224923_1_) throws RealmsServiceException {
      String s = this.url("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(p_224923_1_)));
      String s1 = this.execute(Request.get(s));
      return BackupList.parse(s1);
   }

   public void update(long p_224922_1_, String p_224922_3_, String p_224922_4_) throws RealmsServiceException {
      RealmsDescriptionDto realmsdescriptiondto = new RealmsDescriptionDto(p_224922_3_, p_224922_4_);
      String s = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_224922_1_)));
      this.execute(Request.post(s, GSON.toJson(realmsdescriptiondto)));
   }

   public void updateSlot(long p_224925_1_, int p_224925_3_, RealmsWorldOptions p_224925_4_) throws RealmsServiceException {
      String s = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(p_224925_1_)).replace("$SLOT_ID", String.valueOf(p_224925_3_)));
      String s1 = p_224925_4_.toJson();
      this.execute(Request.post(s, s1));
   }

   public boolean switchSlot(long p_224927_1_, int p_224927_3_) throws RealmsServiceException {
      String s = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(p_224927_1_)).replace("$SLOT_ID", String.valueOf(p_224927_3_)));
      String s1 = this.execute(Request.put(s, ""));
      return Boolean.valueOf(s1);
   }

   public void restoreWorld(long p_224928_1_, String p_224928_3_) throws RealmsServiceException {
      String s = this.url("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(p_224928_1_)), "backupId=" + p_224928_3_);
      this.execute(Request.put(s, "", 40000, 600000));
   }

   public WorldTemplatePaginatedList fetchWorldTemplates(int p_224930_1_, int p_224930_2_, RealmsServer.ServerType p_224930_3_) throws RealmsServiceException {
      String s = this.url("worlds" + "/templates/$WORLD_TYPE".replace("$WORLD_TYPE", p_224930_3_.toString()), String.format("page=%d&pageSize=%d", p_224930_1_, p_224930_2_));
      String s1 = this.execute(Request.get(s));
      return WorldTemplatePaginatedList.parse(s1);
   }

   public Boolean putIntoMinigameMode(long p_224905_1_, String p_224905_3_) throws RealmsServiceException {
      String s = "/minigames/$MINIGAME_ID/$WORLD_ID".replace("$MINIGAME_ID", p_224905_3_).replace("$WORLD_ID", String.valueOf(p_224905_1_));
      String s1 = this.url("worlds" + s);
      return Boolean.valueOf(this.execute(Request.put(s1, "")));
   }

   public Ops op(long p_224906_1_, String p_224906_3_) throws RealmsServiceException {
      String s = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(p_224906_1_)).replace("$PROFILE_UUID", p_224906_3_);
      String s1 = this.url("ops" + s);
      return Ops.parse(this.execute(Request.post(s1, "")));
   }

   public Ops deop(long p_224929_1_, String p_224929_3_) throws RealmsServiceException {
      String s = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(p_224929_1_)).replace("$PROFILE_UUID", p_224929_3_);
      String s1 = this.url("ops" + s);
      return Ops.parse(this.execute(Request.delete(s1)));
   }

   public Boolean open(long p_224942_1_) throws RealmsServiceException {
      String s = this.url("worlds" + "/$WORLD_ID/open".replace("$WORLD_ID", String.valueOf(p_224942_1_)));
      String s1 = this.execute(Request.put(s, ""));
      return Boolean.valueOf(s1);
   }

   public Boolean close(long p_224932_1_) throws RealmsServiceException {
      String s = this.url("worlds" + "/$WORLD_ID/close".replace("$WORLD_ID", String.valueOf(p_224932_1_)));
      String s1 = this.execute(Request.put(s, ""));
      return Boolean.valueOf(s1);
   }

   public Boolean resetWorldWithSeed(long p_224943_1_, String p_224943_3_, Integer p_224943_4_, boolean p_224943_5_) throws RealmsServiceException {
      RealmsWorldResetDto realmsworldresetdto = new RealmsWorldResetDto(p_224943_3_, -1L, p_224943_4_, p_224943_5_);
      String s = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(p_224943_1_)));
      String s1 = this.execute(Request.post(s, GSON.toJson(realmsworldresetdto), 30000, 80000));
      return Boolean.valueOf(s1);
   }

   public Boolean resetWorldWithTemplate(long p_224924_1_, String p_224924_3_) throws RealmsServiceException {
      RealmsWorldResetDto realmsworldresetdto = new RealmsWorldResetDto((String)null, Long.valueOf(p_224924_3_), -1, false);
      String s = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(p_224924_1_)));
      String s1 = this.execute(Request.post(s, GSON.toJson(realmsworldresetdto), 30000, 80000));
      return Boolean.valueOf(s1);
   }

   public Subscription subscriptionFor(long p_224933_1_) throws RealmsServiceException {
      String s = this.url("subscriptions" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_224933_1_)));
      String s1 = this.execute(Request.get(s));
      return Subscription.parse(s1);
   }

   public int pendingInvitesCount() throws RealmsServiceException {
      return this.pendingInvites().pendingInvites.size();
   }

   public PendingInvitesList pendingInvites() throws RealmsServiceException {
      String s = this.url("invites/pending");
      String s1 = this.execute(Request.get(s));
      PendingInvitesList pendinginviteslist = PendingInvitesList.parse(s1);
      pendinginviteslist.pendingInvites.removeIf(this::isBlocked);
      return pendinginviteslist;
   }

   private boolean isBlocked(PendingInvite p_244733_1_) {
      try {
         UUID uuid = UUID.fromString(p_244733_1_.worldOwnerUuid);
         return this.minecraft.getPlayerSocialManager().isBlocked(uuid);
      } catch (IllegalArgumentException illegalargumentexception) {
         return false;
      }
   }

   public void acceptInvitation(String p_224901_1_) throws RealmsServiceException {
      String s = this.url("invites" + "/accept/$INVITATION_ID".replace("$INVITATION_ID", p_224901_1_));
      this.execute(Request.put(s, ""));
   }

   public WorldDownload requestDownloadInfo(long p_224917_1_, int p_224917_3_) throws RealmsServiceException {
      String s = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID/download".replace("$WORLD_ID", String.valueOf(p_224917_1_)).replace("$SLOT_ID", String.valueOf(p_224917_3_)));
      String s1 = this.execute(Request.get(s));
      return WorldDownload.parse(s1);
   }

   @Nullable
   public UploadInfo requestUploadInfo(long p_224934_1_, @Nullable String p_224934_3_) throws RealmsServiceException {
      String s = this.url("worlds" + "/$WORLD_ID/backups/upload".replace("$WORLD_ID", String.valueOf(p_224934_1_)));
      return UploadInfo.parse(this.execute(Request.put(s, UploadInfo.createRequest(p_224934_3_))));
   }

   public void rejectInvitation(String p_224913_1_) throws RealmsServiceException {
      String s = this.url("invites" + "/reject/$INVITATION_ID".replace("$INVITATION_ID", p_224913_1_));
      this.execute(Request.put(s, ""));
   }

   public void agreeToTos() throws RealmsServiceException {
      String s = this.url("mco/tos/agreed");
      this.execute(Request.post(s, ""));
   }

   public RealmsNews getNews() throws RealmsServiceException {
      String s = this.url("mco/v1/news");
      String s1 = this.execute(Request.get(s, 5000, 10000));
      return RealmsNews.parse(s1);
   }

   public void sendPingResults(PingResult p_224903_1_) throws RealmsServiceException {
      String s = this.url("regions/ping/stat");
      this.execute(Request.post(s, GSON.toJson(p_224903_1_)));
   }

   public Boolean trialAvailable() throws RealmsServiceException {
      String s = this.url("trial");
      String s1 = this.execute(Request.get(s));
      return Boolean.valueOf(s1);
   }

   public void deleteWorld(long p_224916_1_) throws RealmsServiceException {
      String s = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_224916_1_)));
      this.execute(Request.delete(s));
   }

   @Nullable
   private String url(String p_224926_1_) {
      return this.url(p_224926_1_, (String)null);
   }

   @Nullable
   private String url(String p_224907_1_, @Nullable String p_224907_2_) {
      try {
         return (new URI(currentEnvironment.protocol, currentEnvironment.baseUrl, "/" + p_224907_1_, p_224907_2_, (String)null)).toASCIIString();
      } catch (URISyntaxException urisyntaxexception) {
         urisyntaxexception.printStackTrace();
         return null;
      }
   }

   private String execute(Request<?> p_224938_1_) throws RealmsServiceException {
      p_224938_1_.cookie("sid", this.sessionId);
      p_224938_1_.cookie("user", this.username);
      p_224938_1_.cookie("version", SharedConstants.getCurrentVersion().getName());

      try {
         int i = p_224938_1_.responseCode();
         if (i != 503 && i != 277) {
            String s = p_224938_1_.text();
            if (i >= 200 && i < 300) {
               return s;
            } else if (i == 401) {
               String s1 = p_224938_1_.getHeader("WWW-Authenticate");
               LOGGER.info("Could not authorize you against Realms server: " + s1);
               throw new RealmsServiceException(i, s1, -1, s1);
            } else if (s != null && s.length() != 0) {
               RealmsError realmserror = RealmsError.create(s);
               LOGGER.error("Realms http code: " + i + " -  error code: " + realmserror.getErrorCode() + " -  message: " + realmserror.getErrorMessage() + " - raw body: " + s);
               throw new RealmsServiceException(i, s, realmserror);
            } else {
               LOGGER.error("Realms error code: " + i + " message: " + s);
               throw new RealmsServiceException(i, s, i, "");
            }
         } else {
            int j = p_224938_1_.getRetryAfterHeader();
            throw new RetryCallException(j, i);
         }
      } catch (RealmsHttpException realmshttpexception) {
         throw new RealmsServiceException(500, "Could not connect to Realms: " + realmshttpexception.getMessage(), -1, "");
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum CompatibleVersionResponse {
      COMPATIBLE,
      OUTDATED,
      OTHER;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Environment {
      PRODUCTION("pc.realms.minecraft.net", "https"),
      STAGE("pc-stage.realms.minecraft.net", "https"),
      LOCAL("localhost:8080", "http");

      public String baseUrl;
      public String protocol;

      private Environment(String p_i51584_3_, String p_i51584_4_) {
         this.baseUrl = p_i51584_3_;
         this.protocol = p_i51584_4_;
      }
   }
}
