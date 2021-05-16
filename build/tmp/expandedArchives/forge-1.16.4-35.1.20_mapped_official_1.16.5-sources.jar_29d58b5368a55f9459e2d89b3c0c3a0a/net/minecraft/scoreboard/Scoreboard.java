package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Scoreboard {
   private final Map<String, ScoreObjective> objectivesByName = Maps.newHashMap();
   private final Map<ScoreCriteria, List<ScoreObjective>> objectivesByCriteria = Maps.newHashMap();
   private final Map<String, Map<ScoreObjective, Score>> playerScores = Maps.newHashMap();
   private final ScoreObjective[] displayObjectives = new ScoreObjective[19];
   private final Map<String, ScorePlayerTeam> teamsByName = Maps.newHashMap();
   private final Map<String, ScorePlayerTeam> teamsByPlayer = Maps.newHashMap();
   private static String[] displaySlotNames;

   @OnlyIn(Dist.CLIENT)
   public boolean hasObjective(String p_197900_1_) {
      return this.objectivesByName.containsKey(p_197900_1_);
   }

   public ScoreObjective getOrCreateObjective(String p_197899_1_) {
      return this.objectivesByName.get(p_197899_1_);
   }

   @Nullable
   public ScoreObjective getObjective(@Nullable String p_96518_1_) {
      return this.objectivesByName.get(p_96518_1_);
   }

   public ScoreObjective addObjective(String p_199868_1_, ScoreCriteria p_199868_2_, ITextComponent p_199868_3_, ScoreCriteria.RenderType p_199868_4_) {
      if (p_199868_1_.length() > 16) {
         throw new IllegalArgumentException("The objective name '" + p_199868_1_ + "' is too long!");
      } else if (this.objectivesByName.containsKey(p_199868_1_)) {
         throw new IllegalArgumentException("An objective with the name '" + p_199868_1_ + "' already exists!");
      } else {
         ScoreObjective scoreobjective = new ScoreObjective(this, p_199868_1_, p_199868_2_, p_199868_3_, p_199868_4_);
         this.objectivesByCriteria.computeIfAbsent(p_199868_2_, (p_197903_0_) -> {
            return Lists.newArrayList();
         }).add(scoreobjective);
         this.objectivesByName.put(p_199868_1_, scoreobjective);
         this.onObjectiveAdded(scoreobjective);
         return scoreobjective;
      }
   }

   public final void forAllObjectives(ScoreCriteria p_197893_1_, String p_197893_2_, Consumer<Score> p_197893_3_) {
      this.objectivesByCriteria.getOrDefault(p_197893_1_, Collections.emptyList()).forEach((p_197906_3_) -> {
         p_197893_3_.accept(this.getOrCreatePlayerScore(p_197893_2_, p_197906_3_));
      });
   }

   public boolean hasPlayerScore(String p_178819_1_, ScoreObjective p_178819_2_) {
      Map<ScoreObjective, Score> map = this.playerScores.get(p_178819_1_);
      if (map == null) {
         return false;
      } else {
         Score score = map.get(p_178819_2_);
         return score != null;
      }
   }

   public Score getOrCreatePlayerScore(String p_96529_1_, ScoreObjective p_96529_2_) {
      if (p_96529_1_.length() > 40) {
         throw new IllegalArgumentException("The player name '" + p_96529_1_ + "' is too long!");
      } else {
         Map<ScoreObjective, Score> map = this.playerScores.computeIfAbsent(p_96529_1_, (p_197898_0_) -> {
            return Maps.newHashMap();
         });
         return map.computeIfAbsent(p_96529_2_, (p_197904_2_) -> {
            Score score = new Score(this, p_197904_2_, p_96529_1_);
            score.setScore(0);
            return score;
         });
      }
   }

   public Collection<Score> getPlayerScores(ScoreObjective p_96534_1_) {
      List<Score> list = Lists.newArrayList();

      for(Map<ScoreObjective, Score> map : this.playerScores.values()) {
         Score score = map.get(p_96534_1_);
         if (score != null) {
            list.add(score);
         }
      }

      list.sort(Score.SCORE_COMPARATOR);
      return list;
   }

   public Collection<ScoreObjective> getObjectives() {
      return this.objectivesByName.values();
   }

   public Collection<String> getObjectiveNames() {
      return this.objectivesByName.keySet();
   }

   public Collection<String> getTrackedPlayers() {
      return Lists.newArrayList(this.playerScores.keySet());
   }

   public void resetPlayerScore(String p_178822_1_, @Nullable ScoreObjective p_178822_2_) {
      if (p_178822_2_ == null) {
         Map<ScoreObjective, Score> map = this.playerScores.remove(p_178822_1_);
         if (map != null) {
            this.onPlayerRemoved(p_178822_1_);
         }
      } else {
         Map<ScoreObjective, Score> map2 = this.playerScores.get(p_178822_1_);
         if (map2 != null) {
            Score score = map2.remove(p_178822_2_);
            if (map2.size() < 1) {
               Map<ScoreObjective, Score> map1 = this.playerScores.remove(p_178822_1_);
               if (map1 != null) {
                  this.onPlayerRemoved(p_178822_1_);
               }
            } else if (score != null) {
               this.onPlayerScoreRemoved(p_178822_1_, p_178822_2_);
            }
         }
      }

   }

   public Map<ScoreObjective, Score> getPlayerScores(String p_96510_1_) {
      Map<ScoreObjective, Score> map = this.playerScores.get(p_96510_1_);
      if (map == null) {
         map = Maps.newHashMap();
      }

      return map;
   }

   public void removeObjective(ScoreObjective p_96519_1_) {
      this.objectivesByName.remove(p_96519_1_.getName());

      for(int i = 0; i < 19; ++i) {
         if (this.getDisplayObjective(i) == p_96519_1_) {
            this.setDisplayObjective(i, (ScoreObjective)null);
         }
      }

      List<ScoreObjective> list = this.objectivesByCriteria.get(p_96519_1_.getCriteria());
      if (list != null) {
         list.remove(p_96519_1_);
      }

      for(Map<ScoreObjective, Score> map : this.playerScores.values()) {
         map.remove(p_96519_1_);
      }

      this.onObjectiveRemoved(p_96519_1_);
   }

   public void setDisplayObjective(int p_96530_1_, @Nullable ScoreObjective p_96530_2_) {
      this.displayObjectives[p_96530_1_] = p_96530_2_;
   }

   @Nullable
   public ScoreObjective getDisplayObjective(int p_96539_1_) {
      return this.displayObjectives[p_96539_1_];
   }

   public ScorePlayerTeam getPlayerTeam(String p_96508_1_) {
      return this.teamsByName.get(p_96508_1_);
   }

   public ScorePlayerTeam addPlayerTeam(String p_96527_1_) {
      if (p_96527_1_.length() > 16) {
         throw new IllegalArgumentException("The team name '" + p_96527_1_ + "' is too long!");
      } else {
         ScorePlayerTeam scoreplayerteam = this.getPlayerTeam(p_96527_1_);
         if (scoreplayerteam != null) {
            throw new IllegalArgumentException("A team with the name '" + p_96527_1_ + "' already exists!");
         } else {
            scoreplayerteam = new ScorePlayerTeam(this, p_96527_1_);
            this.teamsByName.put(p_96527_1_, scoreplayerteam);
            this.onTeamAdded(scoreplayerteam);
            return scoreplayerteam;
         }
      }
   }

   public void removePlayerTeam(ScorePlayerTeam p_96511_1_) {
      this.teamsByName.remove(p_96511_1_.getName());

      for(String s : p_96511_1_.getPlayers()) {
         this.teamsByPlayer.remove(s);
      }

      this.onTeamRemoved(p_96511_1_);
   }

   public boolean addPlayerToTeam(String p_197901_1_, ScorePlayerTeam p_197901_2_) {
      if (p_197901_1_.length() > 40) {
         throw new IllegalArgumentException("The player name '" + p_197901_1_ + "' is too long!");
      } else {
         if (this.getPlayersTeam(p_197901_1_) != null) {
            this.removePlayerFromTeam(p_197901_1_);
         }

         this.teamsByPlayer.put(p_197901_1_, p_197901_2_);
         return p_197901_2_.getPlayers().add(p_197901_1_);
      }
   }

   public boolean removePlayerFromTeam(String p_96524_1_) {
      ScorePlayerTeam scoreplayerteam = this.getPlayersTeam(p_96524_1_);
      if (scoreplayerteam != null) {
         this.removePlayerFromTeam(p_96524_1_, scoreplayerteam);
         return true;
      } else {
         return false;
      }
   }

   public void removePlayerFromTeam(String p_96512_1_, ScorePlayerTeam p_96512_2_) {
      if (this.getPlayersTeam(p_96512_1_) != p_96512_2_) {
         throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + p_96512_2_.getName() + "'.");
      } else {
         this.teamsByPlayer.remove(p_96512_1_);
         p_96512_2_.getPlayers().remove(p_96512_1_);
      }
   }

   public Collection<String> getTeamNames() {
      return this.teamsByName.keySet();
   }

   public Collection<ScorePlayerTeam> getPlayerTeams() {
      return this.teamsByName.values();
   }

   @Nullable
   public ScorePlayerTeam getPlayersTeam(String p_96509_1_) {
      return this.teamsByPlayer.get(p_96509_1_);
   }

   public void onObjectiveAdded(ScoreObjective p_96522_1_) {
   }

   public void onObjectiveChanged(ScoreObjective p_199869_1_) {
   }

   public void onObjectiveRemoved(ScoreObjective p_96533_1_) {
   }

   public void onScoreChanged(Score p_96536_1_) {
   }

   public void onPlayerRemoved(String p_96516_1_) {
   }

   public void onPlayerScoreRemoved(String p_178820_1_, ScoreObjective p_178820_2_) {
   }

   public void onTeamAdded(ScorePlayerTeam p_96523_1_) {
   }

   public void onTeamChanged(ScorePlayerTeam p_96538_1_) {
   }

   public void onTeamRemoved(ScorePlayerTeam p_96513_1_) {
   }

   public static String getDisplaySlotName(int p_96517_0_) {
      switch(p_96517_0_) {
      case 0:
         return "list";
      case 1:
         return "sidebar";
      case 2:
         return "belowName";
      default:
         if (p_96517_0_ >= 3 && p_96517_0_ <= 18) {
            TextFormatting textformatting = TextFormatting.getById(p_96517_0_ - 3);
            if (textformatting != null && textformatting != TextFormatting.RESET) {
               return "sidebar.team." + textformatting.getName();
            }
         }

         return null;
      }
   }

   public static int getDisplaySlotByName(String p_96537_0_) {
      if ("list".equalsIgnoreCase(p_96537_0_)) {
         return 0;
      } else if ("sidebar".equalsIgnoreCase(p_96537_0_)) {
         return 1;
      } else if ("belowName".equalsIgnoreCase(p_96537_0_)) {
         return 2;
      } else {
         if (p_96537_0_.startsWith("sidebar.team.")) {
            String s = p_96537_0_.substring("sidebar.team.".length());
            TextFormatting textformatting = TextFormatting.getByName(s);
            if (textformatting != null && textformatting.getId() >= 0) {
               return textformatting.getId() + 3;
            }
         }

         return -1;
      }
   }

   public static String[] getDisplaySlotNames() {
      if (displaySlotNames == null) {
         displaySlotNames = new String[19];

         for(int i = 0; i < 19; ++i) {
            displaySlotNames[i] = getDisplaySlotName(i);
         }
      }

      return displaySlotNames;
   }

   public void entityRemoved(Entity p_181140_1_) {
      if (p_181140_1_ != null && !(p_181140_1_ instanceof PlayerEntity) && !p_181140_1_.isAlive()) {
         String s = p_181140_1_.getStringUUID();
         this.resetPlayerScore(s, (ScoreObjective)null);
         this.removePlayerFromTeam(s);
      }
   }

   protected ListNBT savePlayerScores() {
      ListNBT listnbt = new ListNBT();
      this.playerScores.values().stream().map(Map::values).forEach((p_197894_1_) -> {
         p_197894_1_.stream().filter((p_209546_0_) -> {
            return p_209546_0_.getObjective() != null;
         }).forEach((p_197896_1_) -> {
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putString("Name", p_197896_1_.getOwner());
            compoundnbt.putString("Objective", p_197896_1_.getObjective().getName());
            compoundnbt.putInt("Score", p_197896_1_.getScore());
            compoundnbt.putBoolean("Locked", p_197896_1_.isLocked());
            listnbt.add(compoundnbt);
         });
      });
      return listnbt;
   }

   protected void loadPlayerScores(ListNBT p_197905_1_) {
      for(int i = 0; i < p_197905_1_.size(); ++i) {
         CompoundNBT compoundnbt = p_197905_1_.getCompound(i);
         ScoreObjective scoreobjective = this.getOrCreateObjective(compoundnbt.getString("Objective"));
         String s = compoundnbt.getString("Name");
         if (s.length() > 40) {
            s = s.substring(0, 40);
         }

         Score score = this.getOrCreatePlayerScore(s, scoreobjective);
         score.setScore(compoundnbt.getInt("Score"));
         if (compoundnbt.contains("Locked")) {
            score.setLocked(compoundnbt.getBoolean("Locked"));
         }
      }

   }
}
