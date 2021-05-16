package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ColorArgument;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.command.arguments.ScoreHolderArgument;
import net.minecraft.command.arguments.TeamArgument;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class TeamCommand {
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_EXISTS = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.add.duplicate"));
   private static final DynamicCommandExceptionType ERROR_TEAM_NAME_TOO_LONG = new DynamicCommandExceptionType((p_208916_0_) -> {
      return new TranslationTextComponent("commands.team.add.longName", p_208916_0_);
   });
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_EMPTY = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.empty.unchanged"));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_NAME = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.name.unchanged"));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_COLOR = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.color.unchanged"));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYFIRE_ENABLED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.friendlyfire.alreadyEnabled"));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYFIRE_DISABLED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.friendlyfire.alreadyDisabled"));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_ENABLED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.seeFriendlyInvisibles.alreadyEnabled"));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_DISABLED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.seeFriendlyInvisibles.alreadyDisabled"));
   private static final SimpleCommandExceptionType ERROR_TEAM_NAMETAG_VISIBLITY_UNCHANGED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.nametagVisibility.unchanged"));
   private static final SimpleCommandExceptionType ERROR_TEAM_DEATH_MESSAGE_VISIBLITY_UNCHANGED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.deathMessageVisibility.unchanged"));
   private static final SimpleCommandExceptionType ERROR_TEAM_COLLISION_UNCHANGED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.collisionRule.unchanged"));

   public static void register(CommandDispatcher<CommandSource> p_198771_0_) {
      p_198771_0_.register(Commands.literal("team").requires((p_198780_0_) -> {
         return p_198780_0_.hasPermission(2);
      }).then(Commands.literal("list").executes((p_198760_0_) -> {
         return listTeams(p_198760_0_.getSource());
      }).then(Commands.argument("team", TeamArgument.team()).executes((p_198763_0_) -> {
         return listMembers(p_198763_0_.getSource(), TeamArgument.getTeam(p_198763_0_, "team"));
      }))).then(Commands.literal("add").then(Commands.argument("team", StringArgumentType.word()).executes((p_198767_0_) -> {
         return createTeam(p_198767_0_.getSource(), StringArgumentType.getString(p_198767_0_, "team"));
      }).then(Commands.argument("displayName", ComponentArgument.textComponent()).executes((p_198779_0_) -> {
         return createTeam(p_198779_0_.getSource(), StringArgumentType.getString(p_198779_0_, "team"), ComponentArgument.getComponent(p_198779_0_, "displayName"));
      })))).then(Commands.literal("remove").then(Commands.argument("team", TeamArgument.team()).executes((p_198773_0_) -> {
         return deleteTeam(p_198773_0_.getSource(), TeamArgument.getTeam(p_198773_0_, "team"));
      }))).then(Commands.literal("empty").then(Commands.argument("team", TeamArgument.team()).executes((p_198785_0_) -> {
         return emptyTeam(p_198785_0_.getSource(), TeamArgument.getTeam(p_198785_0_, "team"));
      }))).then(Commands.literal("join").then(Commands.argument("team", TeamArgument.team()).executes((p_198758_0_) -> {
         return joinTeam(p_198758_0_.getSource(), TeamArgument.getTeam(p_198758_0_, "team"), Collections.singleton(p_198758_0_.getSource().getEntityOrException().getScoreboardName()));
      }).then(Commands.argument("members", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes((p_198755_0_) -> {
         return joinTeam(p_198755_0_.getSource(), TeamArgument.getTeam(p_198755_0_, "team"), ScoreHolderArgument.getNamesWithDefaultWildcard(p_198755_0_, "members"));
      })))).then(Commands.literal("leave").then(Commands.argument("members", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes((p_198765_0_) -> {
         return leaveTeam(p_198765_0_.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(p_198765_0_, "members"));
      }))).then(Commands.literal("modify").then(Commands.argument("team", TeamArgument.team()).then(Commands.literal("displayName").then(Commands.argument("displayName", ComponentArgument.textComponent()).executes((p_211919_0_) -> {
         return setDisplayName(p_211919_0_.getSource(), TeamArgument.getTeam(p_211919_0_, "team"), ComponentArgument.getComponent(p_211919_0_, "displayName"));
      }))).then(Commands.literal("color").then(Commands.argument("value", ColorArgument.color()).executes((p_198762_0_) -> {
         return setColor(p_198762_0_.getSource(), TeamArgument.getTeam(p_198762_0_, "team"), ColorArgument.getColor(p_198762_0_, "value"));
      }))).then(Commands.literal("friendlyFire").then(Commands.argument("allowed", BoolArgumentType.bool()).executes((p_198775_0_) -> {
         return setFriendlyFire(p_198775_0_.getSource(), TeamArgument.getTeam(p_198775_0_, "team"), BoolArgumentType.getBool(p_198775_0_, "allowed"));
      }))).then(Commands.literal("seeFriendlyInvisibles").then(Commands.argument("allowed", BoolArgumentType.bool()).executes((p_198770_0_) -> {
         return setFriendlySight(p_198770_0_.getSource(), TeamArgument.getTeam(p_198770_0_, "team"), BoolArgumentType.getBool(p_198770_0_, "allowed"));
      }))).then(Commands.literal("nametagVisibility").then(Commands.literal("never").executes((p_198778_0_) -> {
         return setNametagVisibility(p_198778_0_.getSource(), TeamArgument.getTeam(p_198778_0_, "team"), Team.Visible.NEVER);
      })).then(Commands.literal("hideForOtherTeams").executes((p_198764_0_) -> {
         return setNametagVisibility(p_198764_0_.getSource(), TeamArgument.getTeam(p_198764_0_, "team"), Team.Visible.HIDE_FOR_OTHER_TEAMS);
      })).then(Commands.literal("hideForOwnTeam").executes((p_198766_0_) -> {
         return setNametagVisibility(p_198766_0_.getSource(), TeamArgument.getTeam(p_198766_0_, "team"), Team.Visible.HIDE_FOR_OWN_TEAM);
      })).then(Commands.literal("always").executes((p_198759_0_) -> {
         return setNametagVisibility(p_198759_0_.getSource(), TeamArgument.getTeam(p_198759_0_, "team"), Team.Visible.ALWAYS);
      }))).then(Commands.literal("deathMessageVisibility").then(Commands.literal("never").executes((p_198789_0_) -> {
         return setDeathMessageVisibility(p_198789_0_.getSource(), TeamArgument.getTeam(p_198789_0_, "team"), Team.Visible.NEVER);
      })).then(Commands.literal("hideForOtherTeams").executes((p_198791_0_) -> {
         return setDeathMessageVisibility(p_198791_0_.getSource(), TeamArgument.getTeam(p_198791_0_, "team"), Team.Visible.HIDE_FOR_OTHER_TEAMS);
      })).then(Commands.literal("hideForOwnTeam").executes((p_198769_0_) -> {
         return setDeathMessageVisibility(p_198769_0_.getSource(), TeamArgument.getTeam(p_198769_0_, "team"), Team.Visible.HIDE_FOR_OWN_TEAM);
      })).then(Commands.literal("always").executes((p_198774_0_) -> {
         return setDeathMessageVisibility(p_198774_0_.getSource(), TeamArgument.getTeam(p_198774_0_, "team"), Team.Visible.ALWAYS);
      }))).then(Commands.literal("collisionRule").then(Commands.literal("never").executes((p_198761_0_) -> {
         return setCollision(p_198761_0_.getSource(), TeamArgument.getTeam(p_198761_0_, "team"), Team.CollisionRule.NEVER);
      })).then(Commands.literal("pushOwnTeam").executes((p_198756_0_) -> {
         return setCollision(p_198756_0_.getSource(), TeamArgument.getTeam(p_198756_0_, "team"), Team.CollisionRule.PUSH_OWN_TEAM);
      })).then(Commands.literal("pushOtherTeams").executes((p_198754_0_) -> {
         return setCollision(p_198754_0_.getSource(), TeamArgument.getTeam(p_198754_0_, "team"), Team.CollisionRule.PUSH_OTHER_TEAMS);
      })).then(Commands.literal("always").executes((p_198790_0_) -> {
         return setCollision(p_198790_0_.getSource(), TeamArgument.getTeam(p_198790_0_, "team"), Team.CollisionRule.ALWAYS);
      }))).then(Commands.literal("prefix").then(Commands.argument("prefix", ComponentArgument.textComponent()).executes((p_207514_0_) -> {
         return setPrefix(p_207514_0_.getSource(), TeamArgument.getTeam(p_207514_0_, "team"), ComponentArgument.getComponent(p_207514_0_, "prefix"));
      }))).then(Commands.literal("suffix").then(Commands.argument("suffix", ComponentArgument.textComponent()).executes((p_207516_0_) -> {
         return setSuffix(p_207516_0_.getSource(), TeamArgument.getTeam(p_207516_0_, "team"), ComponentArgument.getComponent(p_207516_0_, "suffix"));
      }))))));
   }

   private static int leaveTeam(CommandSource p_198786_0_, Collection<String> p_198786_1_) {
      Scoreboard scoreboard = p_198786_0_.getServer().getScoreboard();

      for(String s : p_198786_1_) {
         scoreboard.removePlayerFromTeam(s);
      }

      if (p_198786_1_.size() == 1) {
         p_198786_0_.sendSuccess(new TranslationTextComponent("commands.team.leave.success.single", p_198786_1_.iterator().next()), true);
      } else {
         p_198786_0_.sendSuccess(new TranslationTextComponent("commands.team.leave.success.multiple", p_198786_1_.size()), true);
      }

      return p_198786_1_.size();
   }

   private static int joinTeam(CommandSource p_198768_0_, ScorePlayerTeam p_198768_1_, Collection<String> p_198768_2_) {
      Scoreboard scoreboard = p_198768_0_.getServer().getScoreboard();

      for(String s : p_198768_2_) {
         scoreboard.addPlayerToTeam(s, p_198768_1_);
      }

      if (p_198768_2_.size() == 1) {
         p_198768_0_.sendSuccess(new TranslationTextComponent("commands.team.join.success.single", p_198768_2_.iterator().next(), p_198768_1_.getFormattedDisplayName()), true);
      } else {
         p_198768_0_.sendSuccess(new TranslationTextComponent("commands.team.join.success.multiple", p_198768_2_.size(), p_198768_1_.getFormattedDisplayName()), true);
      }

      return p_198768_2_.size();
   }

   private static int setNametagVisibility(CommandSource p_198777_0_, ScorePlayerTeam p_198777_1_, Team.Visible p_198777_2_) throws CommandSyntaxException {
      if (p_198777_1_.getNameTagVisibility() == p_198777_2_) {
         throw ERROR_TEAM_NAMETAG_VISIBLITY_UNCHANGED.create();
      } else {
         p_198777_1_.setNameTagVisibility(p_198777_2_);
         p_198777_0_.sendSuccess(new TranslationTextComponent("commands.team.option.nametagVisibility.success", p_198777_1_.getFormattedDisplayName(), p_198777_2_.getDisplayName()), true);
         return 0;
      }
   }

   private static int setDeathMessageVisibility(CommandSource p_198776_0_, ScorePlayerTeam p_198776_1_, Team.Visible p_198776_2_) throws CommandSyntaxException {
      if (p_198776_1_.getDeathMessageVisibility() == p_198776_2_) {
         throw ERROR_TEAM_DEATH_MESSAGE_VISIBLITY_UNCHANGED.create();
      } else {
         p_198776_1_.setDeathMessageVisibility(p_198776_2_);
         p_198776_0_.sendSuccess(new TranslationTextComponent("commands.team.option.deathMessageVisibility.success", p_198776_1_.getFormattedDisplayName(), p_198776_2_.getDisplayName()), true);
         return 0;
      }
   }

   private static int setCollision(CommandSource p_198787_0_, ScorePlayerTeam p_198787_1_, Team.CollisionRule p_198787_2_) throws CommandSyntaxException {
      if (p_198787_1_.getCollisionRule() == p_198787_2_) {
         throw ERROR_TEAM_COLLISION_UNCHANGED.create();
      } else {
         p_198787_1_.setCollisionRule(p_198787_2_);
         p_198787_0_.sendSuccess(new TranslationTextComponent("commands.team.option.collisionRule.success", p_198787_1_.getFormattedDisplayName(), p_198787_2_.getDisplayName()), true);
         return 0;
      }
   }

   private static int setFriendlySight(CommandSource p_198783_0_, ScorePlayerTeam p_198783_1_, boolean p_198783_2_) throws CommandSyntaxException {
      if (p_198783_1_.canSeeFriendlyInvisibles() == p_198783_2_) {
         if (p_198783_2_) {
            throw ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_ENABLED.create();
         } else {
            throw ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_DISABLED.create();
         }
      } else {
         p_198783_1_.setSeeFriendlyInvisibles(p_198783_2_);
         p_198783_0_.sendSuccess(new TranslationTextComponent("commands.team.option.seeFriendlyInvisibles." + (p_198783_2_ ? "enabled" : "disabled"), p_198783_1_.getFormattedDisplayName()), true);
         return 0;
      }
   }

   private static int setFriendlyFire(CommandSource p_198781_0_, ScorePlayerTeam p_198781_1_, boolean p_198781_2_) throws CommandSyntaxException {
      if (p_198781_1_.isAllowFriendlyFire() == p_198781_2_) {
         if (p_198781_2_) {
            throw ERROR_TEAM_ALREADY_FRIENDLYFIRE_ENABLED.create();
         } else {
            throw ERROR_TEAM_ALREADY_FRIENDLYFIRE_DISABLED.create();
         }
      } else {
         p_198781_1_.setAllowFriendlyFire(p_198781_2_);
         p_198781_0_.sendSuccess(new TranslationTextComponent("commands.team.option.friendlyfire." + (p_198781_2_ ? "enabled" : "disabled"), p_198781_1_.getFormattedDisplayName()), true);
         return 0;
      }
   }

   private static int setDisplayName(CommandSource p_211920_0_, ScorePlayerTeam p_211920_1_, ITextComponent p_211920_2_) throws CommandSyntaxException {
      if (p_211920_1_.getDisplayName().equals(p_211920_2_)) {
         throw ERROR_TEAM_ALREADY_NAME.create();
      } else {
         p_211920_1_.setDisplayName(p_211920_2_);
         p_211920_0_.sendSuccess(new TranslationTextComponent("commands.team.option.name.success", p_211920_1_.getFormattedDisplayName()), true);
         return 0;
      }
   }

   private static int setColor(CommandSource p_198757_0_, ScorePlayerTeam p_198757_1_, TextFormatting p_198757_2_) throws CommandSyntaxException {
      if (p_198757_1_.getColor() == p_198757_2_) {
         throw ERROR_TEAM_ALREADY_COLOR.create();
      } else {
         p_198757_1_.setColor(p_198757_2_);
         p_198757_0_.sendSuccess(new TranslationTextComponent("commands.team.option.color.success", p_198757_1_.getFormattedDisplayName(), p_198757_2_.getName()), true);
         return 0;
      }
   }

   private static int emptyTeam(CommandSource p_198788_0_, ScorePlayerTeam p_198788_1_) throws CommandSyntaxException {
      Scoreboard scoreboard = p_198788_0_.getServer().getScoreboard();
      Collection<String> collection = Lists.newArrayList(p_198788_1_.getPlayers());
      if (collection.isEmpty()) {
         throw ERROR_TEAM_ALREADY_EMPTY.create();
      } else {
         for(String s : collection) {
            scoreboard.removePlayerFromTeam(s, p_198788_1_);
         }

         p_198788_0_.sendSuccess(new TranslationTextComponent("commands.team.empty.success", collection.size(), p_198788_1_.getFormattedDisplayName()), true);
         return collection.size();
      }
   }

   private static int deleteTeam(CommandSource p_198784_0_, ScorePlayerTeam p_198784_1_) {
      Scoreboard scoreboard = p_198784_0_.getServer().getScoreboard();
      scoreboard.removePlayerTeam(p_198784_1_);
      p_198784_0_.sendSuccess(new TranslationTextComponent("commands.team.remove.success", p_198784_1_.getFormattedDisplayName()), true);
      return scoreboard.getPlayerTeams().size();
   }

   private static int createTeam(CommandSource p_211916_0_, String p_211916_1_) throws CommandSyntaxException {
      return createTeam(p_211916_0_, p_211916_1_, new StringTextComponent(p_211916_1_));
   }

   private static int createTeam(CommandSource p_211917_0_, String p_211917_1_, ITextComponent p_211917_2_) throws CommandSyntaxException {
      Scoreboard scoreboard = p_211917_0_.getServer().getScoreboard();
      if (scoreboard.getPlayerTeam(p_211917_1_) != null) {
         throw ERROR_TEAM_ALREADY_EXISTS.create();
      } else if (p_211917_1_.length() > 16) {
         throw ERROR_TEAM_NAME_TOO_LONG.create(16);
      } else {
         ScorePlayerTeam scoreplayerteam = scoreboard.addPlayerTeam(p_211917_1_);
         scoreplayerteam.setDisplayName(p_211917_2_);
         p_211917_0_.sendSuccess(new TranslationTextComponent("commands.team.add.success", scoreplayerteam.getFormattedDisplayName()), true);
         return scoreboard.getPlayerTeams().size();
      }
   }

   private static int listMembers(CommandSource p_198782_0_, ScorePlayerTeam p_198782_1_) {
      Collection<String> collection = p_198782_1_.getPlayers();
      if (collection.isEmpty()) {
         p_198782_0_.sendSuccess(new TranslationTextComponent("commands.team.list.members.empty", p_198782_1_.getFormattedDisplayName()), false);
      } else {
         p_198782_0_.sendSuccess(new TranslationTextComponent("commands.team.list.members.success", p_198782_1_.getFormattedDisplayName(), collection.size(), TextComponentUtils.formatList(collection)), false);
      }

      return collection.size();
   }

   private static int listTeams(CommandSource p_198792_0_) {
      Collection<ScorePlayerTeam> collection = p_198792_0_.getServer().getScoreboard().getPlayerTeams();
      if (collection.isEmpty()) {
         p_198792_0_.sendSuccess(new TranslationTextComponent("commands.team.list.teams.empty"), false);
      } else {
         p_198792_0_.sendSuccess(new TranslationTextComponent("commands.team.list.teams.success", collection.size(), TextComponentUtils.formatList(collection, ScorePlayerTeam::getFormattedDisplayName)), false);
      }

      return collection.size();
   }

   private static int setPrefix(CommandSource p_207515_0_, ScorePlayerTeam p_207515_1_, ITextComponent p_207515_2_) {
      p_207515_1_.setPlayerPrefix(p_207515_2_);
      p_207515_0_.sendSuccess(new TranslationTextComponent("commands.team.option.prefix.success", p_207515_2_), false);
      return 1;
   }

   private static int setSuffix(CommandSource p_207517_0_, ScorePlayerTeam p_207517_1_, ITextComponent p_207517_2_) {
      p_207517_1_.setPlayerSuffix(p_207517_2_);
      p_207517_0_.sendSuccess(new TranslationTextComponent("commands.team.option.suffix.success", p_207517_2_), false);
      return 1;
   }
}
