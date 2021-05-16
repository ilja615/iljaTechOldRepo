package com.mojang.realmsclient.dto;

import com.google.common.base.Joiner;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsServer extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public long id;
   public String remoteSubscriptionId;
   public String name;
   public String motd;
   public RealmsServer.Status state;
   public String owner;
   public String ownerUUID;
   public List<PlayerInfo> players;
   public Map<Integer, RealmsWorldOptions> slots;
   public boolean expired;
   public boolean expiredTrial;
   public int daysLeft;
   public RealmsServer.ServerType worldType;
   public int activeSlot;
   public String minigameName;
   public int minigameId;
   public String minigameImage;
   public RealmsServerPing serverPing = new RealmsServerPing();

   public String getDescription() {
      return this.motd;
   }

   public String getName() {
      return this.name;
   }

   public String getMinigameName() {
      return this.minigameName;
   }

   public void setName(String p_230773_1_) {
      this.name = p_230773_1_;
   }

   public void setDescription(String p_230777_1_) {
      this.motd = p_230777_1_;
   }

   public void updateServerPing(RealmsServerPlayerList p_230772_1_) {
      List<String> list = Lists.newArrayList();
      int i = 0;

      for(String s : p_230772_1_.players) {
         if (!s.equals(Minecraft.getInstance().getUser().getUuid())) {
            String s1 = "";

            try {
               s1 = RealmsUtil.uuidToName(s);
            } catch (Exception exception) {
               LOGGER.error("Could not get name for " + s, (Throwable)exception);
               continue;
            }

            list.add(s1);
            ++i;
         }
      }

      this.serverPing.nrOfPlayers = String.valueOf(i);
      this.serverPing.playerList = Joiner.on('\n').join(list);
   }

   public static RealmsServer parse(JsonObject p_230770_0_) {
      RealmsServer realmsserver = new RealmsServer();

      try {
         realmsserver.id = JsonUtils.getLongOr("id", p_230770_0_, -1L);
         realmsserver.remoteSubscriptionId = JsonUtils.getStringOr("remoteSubscriptionId", p_230770_0_, (String)null);
         realmsserver.name = JsonUtils.getStringOr("name", p_230770_0_, (String)null);
         realmsserver.motd = JsonUtils.getStringOr("motd", p_230770_0_, (String)null);
         realmsserver.state = getState(JsonUtils.getStringOr("state", p_230770_0_, RealmsServer.Status.CLOSED.name()));
         realmsserver.owner = JsonUtils.getStringOr("owner", p_230770_0_, (String)null);
         if (p_230770_0_.get("players") != null && p_230770_0_.get("players").isJsonArray()) {
            realmsserver.players = parseInvited(p_230770_0_.get("players").getAsJsonArray());
            sortInvited(realmsserver);
         } else {
            realmsserver.players = Lists.newArrayList();
         }

         realmsserver.daysLeft = JsonUtils.getIntOr("daysLeft", p_230770_0_, 0);
         realmsserver.expired = JsonUtils.getBooleanOr("expired", p_230770_0_, false);
         realmsserver.expiredTrial = JsonUtils.getBooleanOr("expiredTrial", p_230770_0_, false);
         realmsserver.worldType = getWorldType(JsonUtils.getStringOr("worldType", p_230770_0_, RealmsServer.ServerType.NORMAL.name()));
         realmsserver.ownerUUID = JsonUtils.getStringOr("ownerUUID", p_230770_0_, "");
         if (p_230770_0_.get("slots") != null && p_230770_0_.get("slots").isJsonArray()) {
            realmsserver.slots = parseSlots(p_230770_0_.get("slots").getAsJsonArray());
         } else {
            realmsserver.slots = createEmptySlots();
         }

         realmsserver.minigameName = JsonUtils.getStringOr("minigameName", p_230770_0_, (String)null);
         realmsserver.activeSlot = JsonUtils.getIntOr("activeSlot", p_230770_0_, -1);
         realmsserver.minigameId = JsonUtils.getIntOr("minigameId", p_230770_0_, -1);
         realmsserver.minigameImage = JsonUtils.getStringOr("minigameImage", p_230770_0_, (String)null);
      } catch (Exception exception) {
         LOGGER.error("Could not parse McoServer: " + exception.getMessage());
      }

      return realmsserver;
   }

   private static void sortInvited(RealmsServer p_230771_0_) {
      p_230771_0_.players.sort((p_229951_0_, p_229951_1_) -> {
         return ComparisonChain.start().compareFalseFirst(p_229951_1_.getAccepted(), p_229951_0_.getAccepted()).compare(p_229951_0_.getName().toLowerCase(Locale.ROOT), p_229951_1_.getName().toLowerCase(Locale.ROOT)).result();
      });
   }

   private static List<PlayerInfo> parseInvited(JsonArray p_230769_0_) {
      List<PlayerInfo> list = Lists.newArrayList();

      for(JsonElement jsonelement : p_230769_0_) {
         try {
            JsonObject jsonobject = jsonelement.getAsJsonObject();
            PlayerInfo playerinfo = new PlayerInfo();
            playerinfo.setName(JsonUtils.getStringOr("name", jsonobject, (String)null));
            playerinfo.setUuid(JsonUtils.getStringOr("uuid", jsonobject, (String)null));
            playerinfo.setOperator(JsonUtils.getBooleanOr("operator", jsonobject, false));
            playerinfo.setAccepted(JsonUtils.getBooleanOr("accepted", jsonobject, false));
            playerinfo.setOnline(JsonUtils.getBooleanOr("online", jsonobject, false));
            list.add(playerinfo);
         } catch (Exception exception) {
         }
      }

      return list;
   }

   private static Map<Integer, RealmsWorldOptions> parseSlots(JsonArray p_230776_0_) {
      Map<Integer, RealmsWorldOptions> map = Maps.newHashMap();

      for(JsonElement jsonelement : p_230776_0_) {
         try {
            JsonObject jsonobject = jsonelement.getAsJsonObject();
            JsonParser jsonparser = new JsonParser();
            JsonElement jsonelement1 = jsonparser.parse(jsonobject.get("options").getAsString());
            RealmsWorldOptions realmsworldoptions;
            if (jsonelement1 == null) {
               realmsworldoptions = RealmsWorldOptions.createDefaults();
            } else {
               realmsworldoptions = RealmsWorldOptions.parse(jsonelement1.getAsJsonObject());
            }

            int i = JsonUtils.getIntOr("slotId", jsonobject, -1);
            map.put(i, realmsworldoptions);
         } catch (Exception exception) {
         }
      }

      for(int j = 1; j <= 3; ++j) {
         if (!map.containsKey(j)) {
            map.put(j, RealmsWorldOptions.createEmptyDefaults());
         }
      }

      return map;
   }

   private static Map<Integer, RealmsWorldOptions> createEmptySlots() {
      Map<Integer, RealmsWorldOptions> map = Maps.newHashMap();
      map.put(1, RealmsWorldOptions.createEmptyDefaults());
      map.put(2, RealmsWorldOptions.createEmptyDefaults());
      map.put(3, RealmsWorldOptions.createEmptyDefaults());
      return map;
   }

   public static RealmsServer parse(String p_230779_0_) {
      try {
         return parse((new JsonParser()).parse(p_230779_0_).getAsJsonObject());
      } catch (Exception exception) {
         LOGGER.error("Could not parse McoServer: " + exception.getMessage());
         return new RealmsServer();
      }
   }

   private static RealmsServer.Status getState(String p_230780_0_) {
      try {
         return RealmsServer.Status.valueOf(p_230780_0_);
      } catch (Exception exception) {
         return RealmsServer.Status.CLOSED;
      }
   }

   private static RealmsServer.ServerType getWorldType(String p_230781_0_) {
      try {
         return RealmsServer.ServerType.valueOf(p_230781_0_);
      } catch (Exception exception) {
         return RealmsServer.ServerType.NORMAL;
      }
   }

   public int hashCode() {
      return Objects.hash(this.id, this.name, this.motd, this.state, this.owner, this.expired);
   }

   public boolean equals(Object p_equals_1_) {
      if (p_equals_1_ == null) {
         return false;
      } else if (p_equals_1_ == this) {
         return true;
      } else if (p_equals_1_.getClass() != this.getClass()) {
         return false;
      } else {
         RealmsServer realmsserver = (RealmsServer)p_equals_1_;
         return (new EqualsBuilder()).append(this.id, realmsserver.id).append((Object)this.name, (Object)realmsserver.name).append((Object)this.motd, (Object)realmsserver.motd).append((Object)this.state, (Object)realmsserver.state).append((Object)this.owner, (Object)realmsserver.owner).append(this.expired, realmsserver.expired).append((Object)this.worldType, (Object)this.worldType).isEquals();
      }
   }

   public RealmsServer clone() {
      RealmsServer realmsserver = new RealmsServer();
      realmsserver.id = this.id;
      realmsserver.remoteSubscriptionId = this.remoteSubscriptionId;
      realmsserver.name = this.name;
      realmsserver.motd = this.motd;
      realmsserver.state = this.state;
      realmsserver.owner = this.owner;
      realmsserver.players = this.players;
      realmsserver.slots = this.cloneSlots(this.slots);
      realmsserver.expired = this.expired;
      realmsserver.expiredTrial = this.expiredTrial;
      realmsserver.daysLeft = this.daysLeft;
      realmsserver.serverPing = new RealmsServerPing();
      realmsserver.serverPing.nrOfPlayers = this.serverPing.nrOfPlayers;
      realmsserver.serverPing.playerList = this.serverPing.playerList;
      realmsserver.worldType = this.worldType;
      realmsserver.ownerUUID = this.ownerUUID;
      realmsserver.minigameName = this.minigameName;
      realmsserver.activeSlot = this.activeSlot;
      realmsserver.minigameId = this.minigameId;
      realmsserver.minigameImage = this.minigameImage;
      return realmsserver;
   }

   public Map<Integer, RealmsWorldOptions> cloneSlots(Map<Integer, RealmsWorldOptions> p_230774_1_) {
      Map<Integer, RealmsWorldOptions> map = Maps.newHashMap();

      for(Entry<Integer, RealmsWorldOptions> entry : p_230774_1_.entrySet()) {
         map.put(entry.getKey(), entry.getValue().clone());
      }

      return map;
   }

   public String getWorldName(int p_237696_1_) {
      return this.name + " (" + this.slots.get(p_237696_1_).getSlotName(p_237696_1_) + ")";
   }

   public ServerData toServerData(String p_244783_1_) {
      return new ServerData(this.name, p_244783_1_, false);
   }

   @OnlyIn(Dist.CLIENT)
   public static class ServerComparator implements Comparator<RealmsServer> {
      private final String refOwner;

      public ServerComparator(String p_i51687_1_) {
         this.refOwner = p_i51687_1_;
      }

      public int compare(RealmsServer p_compare_1_, RealmsServer p_compare_2_) {
         return ComparisonChain.start().compareTrueFirst(p_compare_1_.state == RealmsServer.Status.UNINITIALIZED, p_compare_2_.state == RealmsServer.Status.UNINITIALIZED).compareTrueFirst(p_compare_1_.expiredTrial, p_compare_2_.expiredTrial).compareTrueFirst(p_compare_1_.owner.equals(this.refOwner), p_compare_2_.owner.equals(this.refOwner)).compareFalseFirst(p_compare_1_.expired, p_compare_2_.expired).compareTrueFirst(p_compare_1_.state == RealmsServer.Status.OPEN, p_compare_2_.state == RealmsServer.Status.OPEN).compare(p_compare_1_.id, p_compare_2_.id).result();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum ServerType {
      NORMAL,
      MINIGAME,
      ADVENTUREMAP,
      EXPERIENCE,
      INSPIRATION;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Status {
      CLOSED,
      OPEN,
      UNINITIALIZED;
   }
}
