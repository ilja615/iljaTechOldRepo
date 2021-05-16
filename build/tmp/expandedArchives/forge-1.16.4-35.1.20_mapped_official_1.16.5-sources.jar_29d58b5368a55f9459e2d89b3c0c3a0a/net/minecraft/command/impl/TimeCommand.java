package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.TimeArgument;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class TimeCommand {
   public static void register(CommandDispatcher<CommandSource> p_198823_0_) {
      p_198823_0_.register(Commands.literal("time").requires((p_198828_0_) -> {
         return p_198828_0_.hasPermission(2);
      }).then(Commands.literal("set").then(Commands.literal("day").executes((p_198832_0_) -> {
         return setTime(p_198832_0_.getSource(), 1000);
      })).then(Commands.literal("noon").executes((p_198825_0_) -> {
         return setTime(p_198825_0_.getSource(), 6000);
      })).then(Commands.literal("night").executes((p_198822_0_) -> {
         return setTime(p_198822_0_.getSource(), 13000);
      })).then(Commands.literal("midnight").executes((p_200563_0_) -> {
         return setTime(p_200563_0_.getSource(), 18000);
      })).then(Commands.argument("time", TimeArgument.time()).executes((p_200564_0_) -> {
         return setTime(p_200564_0_.getSource(), IntegerArgumentType.getInteger(p_200564_0_, "time"));
      }))).then(Commands.literal("add").then(Commands.argument("time", TimeArgument.time()).executes((p_198830_0_) -> {
         return addTime(p_198830_0_.getSource(), IntegerArgumentType.getInteger(p_198830_0_, "time"));
      }))).then(Commands.literal("query").then(Commands.literal("daytime").executes((p_198827_0_) -> {
         return queryTime(p_198827_0_.getSource(), getDayTime(p_198827_0_.getSource().getLevel()));
      })).then(Commands.literal("gametime").executes((p_198821_0_) -> {
         return queryTime(p_198821_0_.getSource(), (int)(p_198821_0_.getSource().getLevel().getGameTime() % 2147483647L));
      })).then(Commands.literal("day").executes((p_198831_0_) -> {
         return queryTime(p_198831_0_.getSource(), (int)(p_198831_0_.getSource().getLevel().getDayTime() / 24000L % 2147483647L));
      }))));
   }

   private static int getDayTime(ServerWorld p_198833_0_) {
      return (int)(p_198833_0_.getDayTime() % 24000L);
   }

   private static int queryTime(CommandSource p_198824_0_, int p_198824_1_) {
      p_198824_0_.sendSuccess(new TranslationTextComponent("commands.time.query", p_198824_1_), false);
      return p_198824_1_;
   }

   public static int setTime(CommandSource p_198829_0_, int p_198829_1_) {
      for(ServerWorld serverworld : p_198829_0_.getServer().getAllLevels()) {
         serverworld.setDayTime((long)p_198829_1_);
      }

      p_198829_0_.sendSuccess(new TranslationTextComponent("commands.time.set", p_198829_1_), true);
      return getDayTime(p_198829_0_.getLevel());
   }

   public static int addTime(CommandSource p_198826_0_, int p_198826_1_) {
      for(ServerWorld serverworld : p_198826_0_.getServer().getAllLevels()) {
         serverworld.setDayTime(serverworld.getDayTime() + (long)p_198826_1_);
      }

      int i = getDayTime(p_198826_0_.getLevel());
      p_198826_0_.sendSuccess(new TranslationTextComponent("commands.time.set", i), true);
      return i;
   }
}
