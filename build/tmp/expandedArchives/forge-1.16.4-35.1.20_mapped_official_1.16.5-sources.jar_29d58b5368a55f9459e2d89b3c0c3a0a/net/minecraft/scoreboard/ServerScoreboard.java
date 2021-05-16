package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SDisplayObjectivePacket;
import net.minecraft.network.play.server.SScoreboardObjectivePacket;
import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.network.play.server.SUpdateScorePacket;
import net.minecraft.server.MinecraftServer;

public class ServerScoreboard extends Scoreboard {
   private final MinecraftServer server;
   private final Set<ScoreObjective> trackedObjectives = Sets.newHashSet();
   private Runnable[] dirtyListeners = new Runnable[0];

   public ServerScoreboard(MinecraftServer p_i1501_1_) {
      this.server = p_i1501_1_;
   }

   public void onScoreChanged(Score p_96536_1_) {
      super.onScoreChanged(p_96536_1_);
      if (this.trackedObjectives.contains(p_96536_1_.getObjective())) {
         this.server.getPlayerList().broadcastAll(new SUpdateScorePacket(ServerScoreboard.Action.CHANGE, p_96536_1_.getObjective().getName(), p_96536_1_.getOwner(), p_96536_1_.getScore()));
      }

      this.setDirty();
   }

   public void onPlayerRemoved(String p_96516_1_) {
      super.onPlayerRemoved(p_96516_1_);
      this.server.getPlayerList().broadcastAll(new SUpdateScorePacket(ServerScoreboard.Action.REMOVE, (String)null, p_96516_1_, 0));
      this.setDirty();
   }

   public void onPlayerScoreRemoved(String p_178820_1_, ScoreObjective p_178820_2_) {
      super.onPlayerScoreRemoved(p_178820_1_, p_178820_2_);
      if (this.trackedObjectives.contains(p_178820_2_)) {
         this.server.getPlayerList().broadcastAll(new SUpdateScorePacket(ServerScoreboard.Action.REMOVE, p_178820_2_.getName(), p_178820_1_, 0));
      }

      this.setDirty();
   }

   public void setDisplayObjective(int p_96530_1_, @Nullable ScoreObjective p_96530_2_) {
      ScoreObjective scoreobjective = this.getDisplayObjective(p_96530_1_);
      super.setDisplayObjective(p_96530_1_, p_96530_2_);
      if (scoreobjective != p_96530_2_ && scoreobjective != null) {
         if (this.getObjectiveDisplaySlotCount(scoreobjective) > 0) {
            this.server.getPlayerList().broadcastAll(new SDisplayObjectivePacket(p_96530_1_, p_96530_2_));
         } else {
            this.stopTrackingObjective(scoreobjective);
         }
      }

      if (p_96530_2_ != null) {
         if (this.trackedObjectives.contains(p_96530_2_)) {
            this.server.getPlayerList().broadcastAll(new SDisplayObjectivePacket(p_96530_1_, p_96530_2_));
         } else {
            this.startTrackingObjective(p_96530_2_);
         }
      }

      this.setDirty();
   }

   public boolean addPlayerToTeam(String p_197901_1_, ScorePlayerTeam p_197901_2_) {
      if (super.addPlayerToTeam(p_197901_1_, p_197901_2_)) {
         this.server.getPlayerList().broadcastAll(new STeamsPacket(p_197901_2_, Arrays.asList(p_197901_1_), 3));
         this.setDirty();
         return true;
      } else {
         return false;
      }
   }

   public void removePlayerFromTeam(String p_96512_1_, ScorePlayerTeam p_96512_2_) {
      super.removePlayerFromTeam(p_96512_1_, p_96512_2_);
      this.server.getPlayerList().broadcastAll(new STeamsPacket(p_96512_2_, Arrays.asList(p_96512_1_), 4));
      this.setDirty();
   }

   public void onObjectiveAdded(ScoreObjective p_96522_1_) {
      super.onObjectiveAdded(p_96522_1_);
      this.setDirty();
   }

   public void onObjectiveChanged(ScoreObjective p_199869_1_) {
      super.onObjectiveChanged(p_199869_1_);
      if (this.trackedObjectives.contains(p_199869_1_)) {
         this.server.getPlayerList().broadcastAll(new SScoreboardObjectivePacket(p_199869_1_, 2));
      }

      this.setDirty();
   }

   public void onObjectiveRemoved(ScoreObjective p_96533_1_) {
      super.onObjectiveRemoved(p_96533_1_);
      if (this.trackedObjectives.contains(p_96533_1_)) {
         this.stopTrackingObjective(p_96533_1_);
      }

      this.setDirty();
   }

   public void onTeamAdded(ScorePlayerTeam p_96523_1_) {
      super.onTeamAdded(p_96523_1_);
      this.server.getPlayerList().broadcastAll(new STeamsPacket(p_96523_1_, 0));
      this.setDirty();
   }

   public void onTeamChanged(ScorePlayerTeam p_96538_1_) {
      super.onTeamChanged(p_96538_1_);
      this.server.getPlayerList().broadcastAll(new STeamsPacket(p_96538_1_, 2));
      this.setDirty();
   }

   public void onTeamRemoved(ScorePlayerTeam p_96513_1_) {
      super.onTeamRemoved(p_96513_1_);
      this.server.getPlayerList().broadcastAll(new STeamsPacket(p_96513_1_, 1));
      this.setDirty();
   }

   public void addDirtyListener(Runnable p_186684_1_) {
      this.dirtyListeners = Arrays.copyOf(this.dirtyListeners, this.dirtyListeners.length + 1);
      this.dirtyListeners[this.dirtyListeners.length - 1] = p_186684_1_;
   }

   protected void setDirty() {
      for(Runnable runnable : this.dirtyListeners) {
         runnable.run();
      }

   }

   public List<IPacket<?>> getStartTrackingPackets(ScoreObjective p_96550_1_) {
      List<IPacket<?>> list = Lists.newArrayList();
      list.add(new SScoreboardObjectivePacket(p_96550_1_, 0));

      for(int i = 0; i < 19; ++i) {
         if (this.getDisplayObjective(i) == p_96550_1_) {
            list.add(new SDisplayObjectivePacket(i, p_96550_1_));
         }
      }

      for(Score score : this.getPlayerScores(p_96550_1_)) {
         list.add(new SUpdateScorePacket(ServerScoreboard.Action.CHANGE, score.getObjective().getName(), score.getOwner(), score.getScore()));
      }

      return list;
   }

   public void startTrackingObjective(ScoreObjective p_96549_1_) {
      List<IPacket<?>> list = this.getStartTrackingPackets(p_96549_1_);

      for(ServerPlayerEntity serverplayerentity : this.server.getPlayerList().getPlayers()) {
         for(IPacket<?> ipacket : list) {
            serverplayerentity.connection.send(ipacket);
         }
      }

      this.trackedObjectives.add(p_96549_1_);
   }

   public List<IPacket<?>> getStopTrackingPackets(ScoreObjective p_96548_1_) {
      List<IPacket<?>> list = Lists.newArrayList();
      list.add(new SScoreboardObjectivePacket(p_96548_1_, 1));

      for(int i = 0; i < 19; ++i) {
         if (this.getDisplayObjective(i) == p_96548_1_) {
            list.add(new SDisplayObjectivePacket(i, p_96548_1_));
         }
      }

      return list;
   }

   public void stopTrackingObjective(ScoreObjective p_96546_1_) {
      List<IPacket<?>> list = this.getStopTrackingPackets(p_96546_1_);

      for(ServerPlayerEntity serverplayerentity : this.server.getPlayerList().getPlayers()) {
         for(IPacket<?> ipacket : list) {
            serverplayerentity.connection.send(ipacket);
         }
      }

      this.trackedObjectives.remove(p_96546_1_);
   }

   public int getObjectiveDisplaySlotCount(ScoreObjective p_96552_1_) {
      int i = 0;

      for(int j = 0; j < 19; ++j) {
         if (this.getDisplayObjective(j) == p_96552_1_) {
            ++i;
         }
      }

      return i;
   }

   public static enum Action {
      CHANGE,
      REMOVE;
   }
}
