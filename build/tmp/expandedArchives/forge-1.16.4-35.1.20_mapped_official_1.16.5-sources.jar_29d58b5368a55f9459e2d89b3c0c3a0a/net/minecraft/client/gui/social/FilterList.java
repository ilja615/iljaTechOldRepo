package net.minecraft.client.gui.social;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FilterList extends AbstractOptionList<FilterListEntry> {
   private final SocialInteractionsScreen socialInteractionsScreen;
   private final Minecraft minecraft;
   private final List<FilterListEntry> players = Lists.newArrayList();
   @Nullable
   private String filter;

   public FilterList(SocialInteractionsScreen p_i244516_1_, Minecraft p_i244516_2_, int p_i244516_3_, int p_i244516_4_, int p_i244516_5_, int p_i244516_6_, int p_i244516_7_) {
      super(p_i244516_2_, p_i244516_3_, p_i244516_4_, p_i244516_5_, p_i244516_6_, p_i244516_7_);
      this.socialInteractionsScreen = p_i244516_1_;
      this.minecraft = p_i244516_2_;
      this.setRenderBackground(false);
      this.setRenderTopAndBottom(false);
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      double d0 = this.minecraft.getWindow().getGuiScale();
      RenderSystem.enableScissor((int)((double)this.getRowLeft() * d0), (int)((double)(this.height - this.y1) * d0), (int)((double)(this.getScrollbarPosition() + 6) * d0), (int)((double)(this.height - (this.height - this.y1) - this.y0 - 4) * d0));
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      RenderSystem.disableScissor();
   }

   public void updatePlayerList(Collection<UUID> p_244759_1_, double p_244759_2_) {
      this.players.clear();

      for(UUID uuid : p_244759_1_) {
         NetworkPlayerInfo networkplayerinfo = this.minecraft.player.connection.getPlayerInfo(uuid);
         if (networkplayerinfo != null) {
            this.players.add(new FilterListEntry(this.minecraft, this.socialInteractionsScreen, networkplayerinfo.getProfile().getId(), networkplayerinfo.getProfile().getName(), networkplayerinfo::getSkinLocation));
         }
      }

      this.updateFilteredPlayers();
      this.players.sort((p_244655_0_, p_244655_1_) -> {
         return p_244655_0_.getPlayerName().compareToIgnoreCase(p_244655_1_.getPlayerName());
      });
      this.replaceEntries(this.players);
      this.setScrollAmount(p_244759_2_);
   }

   private void updateFilteredPlayers() {
      if (this.filter != null) {
         this.players.removeIf((p_244654_1_) -> {
            return !p_244654_1_.getPlayerName().toLowerCase(Locale.ROOT).contains(this.filter);
         });
         this.replaceEntries(this.players);
      }

   }

   public void setFilter(String p_244658_1_) {
      this.filter = p_244658_1_;
   }

   public boolean isEmpty() {
      return this.players.isEmpty();
   }

   public void addPlayer(NetworkPlayerInfo p_244657_1_, SocialInteractionsScreen.Mode p_244657_2_) {
      UUID uuid = p_244657_1_.getProfile().getId();

      for(FilterListEntry filterlistentry : this.players) {
         if (filterlistentry.getPlayerId().equals(uuid)) {
            filterlistentry.setRemoved(false);
            return;
         }
      }

      if ((p_244657_2_ == SocialInteractionsScreen.Mode.ALL || this.minecraft.getPlayerSocialManager().shouldHideMessageFrom(uuid)) && (Strings.isNullOrEmpty(this.filter) || p_244657_1_.getProfile().getName().toLowerCase(Locale.ROOT).contains(this.filter))) {
         FilterListEntry filterlistentry1 = new FilterListEntry(this.minecraft, this.socialInteractionsScreen, p_244657_1_.getProfile().getId(), p_244657_1_.getProfile().getName(), p_244657_1_::getSkinLocation);
         this.addEntry(filterlistentry1);
         this.players.add(filterlistentry1);
      }

   }

   public void removePlayer(UUID p_244659_1_) {
      for(FilterListEntry filterlistentry : this.players) {
         if (filterlistentry.getPlayerId().equals(p_244659_1_)) {
            filterlistentry.setRemoved(true);
            return;
         }
      }

   }
}
