package net.minecraft.client.gui.social;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.SocialInteractionsService;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FilterManager {
   private final Minecraft minecraft;
   private final Set<UUID> hiddenPlayers = Sets.newHashSet();
   private final SocialInteractionsService service;
   private final Map<String, UUID> discoveredNamesToUUID = Maps.newHashMap();

   public FilterManager(Minecraft p_i244725_1_, SocialInteractionsService p_i244725_2_) {
      this.minecraft = p_i244725_1_;
      this.service = p_i244725_2_;
   }

   public void hidePlayer(UUID p_244646_1_) {
      this.hiddenPlayers.add(p_244646_1_);
   }

   public void showPlayer(UUID p_244647_1_) {
      this.hiddenPlayers.remove(p_244647_1_);
   }

   public boolean shouldHideMessageFrom(UUID p_244756_1_) {
      return this.isHidden(p_244756_1_) || this.isBlocked(p_244756_1_);
   }

   public boolean isHidden(UUID p_244648_1_) {
      return this.hiddenPlayers.contains(p_244648_1_);
   }

   public boolean isBlocked(UUID p_244757_1_) {
      return this.service.isBlockedPlayer(p_244757_1_);
   }

   public Set<UUID> getHiddenPlayers() {
      return this.hiddenPlayers;
   }

   public UUID getDiscoveredUUID(String p_244797_1_) {
      return this.discoveredNamesToUUID.getOrDefault(p_244797_1_, Util.NIL_UUID);
   }

   public void addPlayer(NetworkPlayerInfo p_244645_1_) {
      GameProfile gameprofile = p_244645_1_.getProfile();
      if (gameprofile.isComplete()) {
         this.discoveredNamesToUUID.put(gameprofile.getName(), gameprofile.getId());
      }

      Screen screen = this.minecraft.screen;
      if (screen instanceof SocialInteractionsScreen) {
         SocialInteractionsScreen socialinteractionsscreen = (SocialInteractionsScreen)screen;
         socialinteractionsscreen.onAddPlayer(p_244645_1_);
      }

   }

   public void removePlayer(UUID p_244649_1_) {
      Screen screen = this.minecraft.screen;
      if (screen instanceof SocialInteractionsScreen) {
         SocialInteractionsScreen socialinteractionsscreen = (SocialInteractionsScreen)screen;
         socialinteractionsscreen.onRemovePlayer(p_244649_1_);
      }

   }
}
