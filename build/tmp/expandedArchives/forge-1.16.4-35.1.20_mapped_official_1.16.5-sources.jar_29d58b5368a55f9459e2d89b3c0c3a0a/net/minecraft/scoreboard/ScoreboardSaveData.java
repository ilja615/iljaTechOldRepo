package net.minecraft.scoreboard;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.storage.WorldSavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScoreboardSaveData extends WorldSavedData {
   private static final Logger LOGGER = LogManager.getLogger();
   private Scoreboard scoreboard;
   private CompoundNBT delayLoad;

   public ScoreboardSaveData() {
      super("scoreboard");
   }

   public void setScoreboard(Scoreboard p_96499_1_) {
      this.scoreboard = p_96499_1_;
      if (this.delayLoad != null) {
         this.load(this.delayLoad);
      }

   }

   public void load(CompoundNBT p_76184_1_) {
      if (this.scoreboard == null) {
         this.delayLoad = p_76184_1_;
      } else {
         this.loadObjectives(p_76184_1_.getList("Objectives", 10));
         this.scoreboard.loadPlayerScores(p_76184_1_.getList("PlayerScores", 10));
         if (p_76184_1_.contains("DisplaySlots", 10)) {
            this.loadDisplaySlots(p_76184_1_.getCompound("DisplaySlots"));
         }

         if (p_76184_1_.contains("Teams", 9)) {
            this.loadTeams(p_76184_1_.getList("Teams", 10));
         }

      }
   }

   protected void loadTeams(ListNBT p_96498_1_) {
      for(int i = 0; i < p_96498_1_.size(); ++i) {
         CompoundNBT compoundnbt = p_96498_1_.getCompound(i);
         String s = compoundnbt.getString("Name");
         if (s.length() > 16) {
            s = s.substring(0, 16);
         }

         ScorePlayerTeam scoreplayerteam = this.scoreboard.addPlayerTeam(s);
         ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(compoundnbt.getString("DisplayName"));
         if (itextcomponent != null) {
            scoreplayerteam.setDisplayName(itextcomponent);
         }

         if (compoundnbt.contains("TeamColor", 8)) {
            scoreplayerteam.setColor(TextFormatting.getByName(compoundnbt.getString("TeamColor")));
         }

         if (compoundnbt.contains("AllowFriendlyFire", 99)) {
            scoreplayerteam.setAllowFriendlyFire(compoundnbt.getBoolean("AllowFriendlyFire"));
         }

         if (compoundnbt.contains("SeeFriendlyInvisibles", 99)) {
            scoreplayerteam.setSeeFriendlyInvisibles(compoundnbt.getBoolean("SeeFriendlyInvisibles"));
         }

         if (compoundnbt.contains("MemberNamePrefix", 8)) {
            ITextComponent itextcomponent1 = ITextComponent.Serializer.fromJson(compoundnbt.getString("MemberNamePrefix"));
            if (itextcomponent1 != null) {
               scoreplayerteam.setPlayerPrefix(itextcomponent1);
            }
         }

         if (compoundnbt.contains("MemberNameSuffix", 8)) {
            ITextComponent itextcomponent2 = ITextComponent.Serializer.fromJson(compoundnbt.getString("MemberNameSuffix"));
            if (itextcomponent2 != null) {
               scoreplayerteam.setPlayerSuffix(itextcomponent2);
            }
         }

         if (compoundnbt.contains("NameTagVisibility", 8)) {
            Team.Visible team$visible = Team.Visible.byName(compoundnbt.getString("NameTagVisibility"));
            if (team$visible != null) {
               scoreplayerteam.setNameTagVisibility(team$visible);
            }
         }

         if (compoundnbt.contains("DeathMessageVisibility", 8)) {
            Team.Visible team$visible1 = Team.Visible.byName(compoundnbt.getString("DeathMessageVisibility"));
            if (team$visible1 != null) {
               scoreplayerteam.setDeathMessageVisibility(team$visible1);
            }
         }

         if (compoundnbt.contains("CollisionRule", 8)) {
            Team.CollisionRule team$collisionrule = Team.CollisionRule.byName(compoundnbt.getString("CollisionRule"));
            if (team$collisionrule != null) {
               scoreplayerteam.setCollisionRule(team$collisionrule);
            }
         }

         this.loadTeamPlayers(scoreplayerteam, compoundnbt.getList("Players", 8));
      }

   }

   protected void loadTeamPlayers(ScorePlayerTeam p_96502_1_, ListNBT p_96502_2_) {
      for(int i = 0; i < p_96502_2_.size(); ++i) {
         this.scoreboard.addPlayerToTeam(p_96502_2_.getString(i), p_96502_1_);
      }

   }

   protected void loadDisplaySlots(CompoundNBT p_96504_1_) {
      for(int i = 0; i < 19; ++i) {
         if (p_96504_1_.contains("slot_" + i, 8)) {
            String s = p_96504_1_.getString("slot_" + i);
            ScoreObjective scoreobjective = this.scoreboard.getObjective(s);
            this.scoreboard.setDisplayObjective(i, scoreobjective);
         }
      }

   }

   protected void loadObjectives(ListNBT p_96501_1_) {
      for(int i = 0; i < p_96501_1_.size(); ++i) {
         CompoundNBT compoundnbt = p_96501_1_.getCompound(i);
         ScoreCriteria.byName(compoundnbt.getString("CriteriaName")).ifPresent((p_215164_2_) -> {
            String s = compoundnbt.getString("Name");
            if (s.length() > 16) {
               s = s.substring(0, 16);
            }

            ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(compoundnbt.getString("DisplayName"));
            ScoreCriteria.RenderType scorecriteria$rendertype = ScoreCriteria.RenderType.byId(compoundnbt.getString("RenderType"));
            this.scoreboard.addObjective(s, p_215164_2_, itextcomponent, scorecriteria$rendertype);
         });
      }

   }

   public CompoundNBT save(CompoundNBT p_189551_1_) {
      if (this.scoreboard == null) {
         LOGGER.warn("Tried to save scoreboard without having a scoreboard...");
         return p_189551_1_;
      } else {
         p_189551_1_.put("Objectives", this.saveObjectives());
         p_189551_1_.put("PlayerScores", this.scoreboard.savePlayerScores());
         p_189551_1_.put("Teams", this.saveTeams());
         this.saveDisplaySlots(p_189551_1_);
         return p_189551_1_;
      }
   }

   protected ListNBT saveTeams() {
      ListNBT listnbt = new ListNBT();

      for(ScorePlayerTeam scoreplayerteam : this.scoreboard.getPlayerTeams()) {
         CompoundNBT compoundnbt = new CompoundNBT();
         compoundnbt.putString("Name", scoreplayerteam.getName());
         compoundnbt.putString("DisplayName", ITextComponent.Serializer.toJson(scoreplayerteam.getDisplayName()));
         if (scoreplayerteam.getColor().getId() >= 0) {
            compoundnbt.putString("TeamColor", scoreplayerteam.getColor().getName());
         }

         compoundnbt.putBoolean("AllowFriendlyFire", scoreplayerteam.isAllowFriendlyFire());
         compoundnbt.putBoolean("SeeFriendlyInvisibles", scoreplayerteam.canSeeFriendlyInvisibles());
         compoundnbt.putString("MemberNamePrefix", ITextComponent.Serializer.toJson(scoreplayerteam.getPlayerPrefix()));
         compoundnbt.putString("MemberNameSuffix", ITextComponent.Serializer.toJson(scoreplayerteam.getPlayerSuffix()));
         compoundnbt.putString("NameTagVisibility", scoreplayerteam.getNameTagVisibility().name);
         compoundnbt.putString("DeathMessageVisibility", scoreplayerteam.getDeathMessageVisibility().name);
         compoundnbt.putString("CollisionRule", scoreplayerteam.getCollisionRule().name);
         ListNBT listnbt1 = new ListNBT();

         for(String s : scoreplayerteam.getPlayers()) {
            listnbt1.add(StringNBT.valueOf(s));
         }

         compoundnbt.put("Players", listnbt1);
         listnbt.add(compoundnbt);
      }

      return listnbt;
   }

   protected void saveDisplaySlots(CompoundNBT p_96497_1_) {
      CompoundNBT compoundnbt = new CompoundNBT();
      boolean flag = false;

      for(int i = 0; i < 19; ++i) {
         ScoreObjective scoreobjective = this.scoreboard.getDisplayObjective(i);
         if (scoreobjective != null) {
            compoundnbt.putString("slot_" + i, scoreobjective.getName());
            flag = true;
         }
      }

      if (flag) {
         p_96497_1_.put("DisplaySlots", compoundnbt);
      }

   }

   protected ListNBT saveObjectives() {
      ListNBT listnbt = new ListNBT();

      for(ScoreObjective scoreobjective : this.scoreboard.getObjectives()) {
         if (scoreobjective.getCriteria() != null) {
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putString("Name", scoreobjective.getName());
            compoundnbt.putString("CriteriaName", scoreobjective.getCriteria().getName());
            compoundnbt.putString("DisplayName", ITextComponent.Serializer.toJson(scoreobjective.getDisplayName()));
            compoundnbt.putString("RenderType", scoreobjective.getRenderType().getId());
            listnbt.add(compoundnbt);
         }
      }

      return listnbt;
   }
}
