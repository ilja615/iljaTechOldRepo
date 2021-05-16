package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.FunctionObject;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.TimedFunction;
import net.minecraft.command.TimedFunctionTag;
import net.minecraft.command.TimerCallbackManager;
import net.minecraft.command.arguments.FunctionArgument;
import net.minecraft.command.arguments.TimeArgument;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class ScheduleCommand {
   private static final SimpleCommandExceptionType ERROR_SAME_TICK = new SimpleCommandExceptionType(new TranslationTextComponent("commands.schedule.same_tick"));
   private static final DynamicCommandExceptionType ERROR_CANT_REMOVE = new DynamicCommandExceptionType((p_229818_0_) -> {
      return new TranslationTextComponent("commands.schedule.cleared.failure", p_229818_0_);
   });
   private static final SuggestionProvider<CommandSource> SUGGEST_SCHEDULE = (p_229814_0_, p_229814_1_) -> {
      return ISuggestionProvider.suggest(p_229814_0_.getSource().getServer().getWorldData().overworldData().getScheduledEvents().getEventsIds(), p_229814_1_);
   };

   public static void register(CommandDispatcher<CommandSource> p_218909_0_) {
      p_218909_0_.register(Commands.literal("schedule").requires((p_229815_0_) -> {
         return p_229815_0_.hasPermission(2);
      }).then(Commands.literal("function").then(Commands.argument("function", FunctionArgument.functions()).suggests(FunctionCommand.SUGGEST_FUNCTION).then(Commands.argument("time", TimeArgument.time()).executes((p_229823_0_) -> {
         return schedule(p_229823_0_.getSource(), FunctionArgument.getFunctionOrTag(p_229823_0_, "function"), IntegerArgumentType.getInteger(p_229823_0_, "time"), true);
      }).then(Commands.literal("append").executes((p_229822_0_) -> {
         return schedule(p_229822_0_.getSource(), FunctionArgument.getFunctionOrTag(p_229822_0_, "function"), IntegerArgumentType.getInteger(p_229822_0_, "time"), false);
      })).then(Commands.literal("replace").executes((p_229821_0_) -> {
         return schedule(p_229821_0_.getSource(), FunctionArgument.getFunctionOrTag(p_229821_0_, "function"), IntegerArgumentType.getInteger(p_229821_0_, "time"), true);
      }))))).then(Commands.literal("clear").then(Commands.argument("function", StringArgumentType.greedyString()).suggests(SUGGEST_SCHEDULE).executes((p_229813_0_) -> {
         return remove(p_229813_0_.getSource(), StringArgumentType.getString(p_229813_0_, "function"));
      }))));
   }

   private static int schedule(CommandSource p_241063_0_, Pair<ResourceLocation, Either<FunctionObject, ITag<FunctionObject>>> p_241063_1_, int p_241063_2_, boolean p_241063_3_) throws CommandSyntaxException {
      if (p_241063_2_ == 0) {
         throw ERROR_SAME_TICK.create();
      } else {
         long i = p_241063_0_.getLevel().getGameTime() + (long)p_241063_2_;
         ResourceLocation resourcelocation = p_241063_1_.getFirst();
         TimerCallbackManager<MinecraftServer> timercallbackmanager = p_241063_0_.getServer().getWorldData().overworldData().getScheduledEvents();
         p_241063_1_.getSecond().ifLeft((p_229820_7_) -> {
            String s = resourcelocation.toString();
            if (p_241063_3_) {
               timercallbackmanager.remove(s);
            }

            timercallbackmanager.schedule(s, i, new TimedFunction(resourcelocation));
            p_241063_0_.sendSuccess(new TranslationTextComponent("commands.schedule.created.function", resourcelocation, p_241063_2_, i), true);
         }).ifRight((p_229819_7_) -> {
            String s = "#" + resourcelocation.toString();
            if (p_241063_3_) {
               timercallbackmanager.remove(s);
            }

            timercallbackmanager.schedule(s, i, new TimedFunctionTag(resourcelocation));
            p_241063_0_.sendSuccess(new TranslationTextComponent("commands.schedule.created.tag", resourcelocation, p_241063_2_, i), true);
         });
         return (int)Math.floorMod(i, 2147483647L);
      }
   }

   private static int remove(CommandSource p_229817_0_, String p_229817_1_) throws CommandSyntaxException {
      int i = p_229817_0_.getServer().getWorldData().overworldData().getScheduledEvents().remove(p_229817_1_);
      if (i == 0) {
         throw ERROR_CANT_REMOVE.create(p_229817_1_);
      } else {
         p_229817_0_.sendSuccess(new TranslationTextComponent("commands.schedule.cleared.success", i, p_229817_1_), true);
         return i;
      }
   }
}
