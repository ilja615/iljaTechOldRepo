package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ObjectiveArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.TranslationTextComponent;

public class TriggerCommand {
   private static final SimpleCommandExceptionType ERROR_NOT_PRIMED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.trigger.failed.unprimed"));
   private static final SimpleCommandExceptionType ERROR_INVALID_OBJECTIVE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.trigger.failed.invalid"));

   public static void register(CommandDispatcher<CommandSource> p_198852_0_) {
      p_198852_0_.register(Commands.literal("trigger").then(Commands.argument("objective", ObjectiveArgument.objective()).suggests((p_198853_0_, p_198853_1_) -> {
         return suggestObjectives(p_198853_0_.getSource(), p_198853_1_);
      }).executes((p_198854_0_) -> {
         return simpleTrigger(p_198854_0_.getSource(), getScore(p_198854_0_.getSource().getPlayerOrException(), ObjectiveArgument.getObjective(p_198854_0_, "objective")));
      }).then(Commands.literal("add").then(Commands.argument("value", IntegerArgumentType.integer()).executes((p_198849_0_) -> {
         return addValue(p_198849_0_.getSource(), getScore(p_198849_0_.getSource().getPlayerOrException(), ObjectiveArgument.getObjective(p_198849_0_, "objective")), IntegerArgumentType.getInteger(p_198849_0_, "value"));
      }))).then(Commands.literal("set").then(Commands.argument("value", IntegerArgumentType.integer()).executes((p_198855_0_) -> {
         return setValue(p_198855_0_.getSource(), getScore(p_198855_0_.getSource().getPlayerOrException(), ObjectiveArgument.getObjective(p_198855_0_, "objective")), IntegerArgumentType.getInteger(p_198855_0_, "value"));
      })))));
   }

   public static CompletableFuture<Suggestions> suggestObjectives(CommandSource p_198850_0_, SuggestionsBuilder p_198850_1_) {
      Entity entity = p_198850_0_.getEntity();
      List<String> list = Lists.newArrayList();
      if (entity != null) {
         Scoreboard scoreboard = p_198850_0_.getServer().getScoreboard();
         String s = entity.getScoreboardName();

         for(ScoreObjective scoreobjective : scoreboard.getObjectives()) {
            if (scoreobjective.getCriteria() == ScoreCriteria.TRIGGER && scoreboard.hasPlayerScore(s, scoreobjective)) {
               Score score = scoreboard.getOrCreatePlayerScore(s, scoreobjective);
               if (!score.isLocked()) {
                  list.add(scoreobjective.getName());
               }
            }
         }
      }

      return ISuggestionProvider.suggest(list, p_198850_1_);
   }

   private static int addValue(CommandSource p_201479_0_, Score p_201479_1_, int p_201479_2_) {
      p_201479_1_.add(p_201479_2_);
      p_201479_0_.sendSuccess(new TranslationTextComponent("commands.trigger.add.success", p_201479_1_.getObjective().getFormattedDisplayName(), p_201479_2_), true);
      return p_201479_1_.getScore();
   }

   private static int setValue(CommandSource p_201478_0_, Score p_201478_1_, int p_201478_2_) {
      p_201478_1_.setScore(p_201478_2_);
      p_201478_0_.sendSuccess(new TranslationTextComponent("commands.trigger.set.success", p_201478_1_.getObjective().getFormattedDisplayName(), p_201478_2_), true);
      return p_201478_2_;
   }

   private static int simpleTrigger(CommandSource p_201477_0_, Score p_201477_1_) {
      p_201477_1_.add(1);
      p_201477_0_.sendSuccess(new TranslationTextComponent("commands.trigger.simple.success", p_201477_1_.getObjective().getFormattedDisplayName()), true);
      return p_201477_1_.getScore();
   }

   private static Score getScore(ServerPlayerEntity p_198848_0_, ScoreObjective p_198848_1_) throws CommandSyntaxException {
      if (p_198848_1_.getCriteria() != ScoreCriteria.TRIGGER) {
         throw ERROR_INVALID_OBJECTIVE.create();
      } else {
         Scoreboard scoreboard = p_198848_0_.getScoreboard();
         String s = p_198848_0_.getScoreboardName();
         if (!scoreboard.hasPlayerScore(s, p_198848_1_)) {
            throw ERROR_NOT_PRIMED.create();
         } else {
            Score score = scoreboard.getOrCreatePlayerScore(s, p_198848_1_);
            if (score.isLocked()) {
               throw ERROR_NOT_PRIMED.create();
            } else {
               score.setLocked(true);
               return score;
            }
         }
      }
   }
}
