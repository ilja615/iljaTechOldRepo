package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;

public class DifficultyCommand {
   private static final DynamicCommandExceptionType ERROR_ALREADY_DIFFICULT = new DynamicCommandExceptionType((p_208823_0_) -> {
      return new TranslationTextComponent("commands.difficulty.failure", p_208823_0_);
   });

   public static void register(CommandDispatcher<CommandSource> p_198344_0_) {
      LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("difficulty");

      for(Difficulty difficulty : Difficulty.values()) {
         literalargumentbuilder.then(Commands.literal(difficulty.getKey()).executes((p_198347_1_) -> {
            return setDifficulty(p_198347_1_.getSource(), difficulty);
         }));
      }

      p_198344_0_.register(literalargumentbuilder.requires((p_198348_0_) -> {
         return p_198348_0_.hasPermission(2);
      }).executes((p_198346_0_) -> {
         Difficulty difficulty1 = p_198346_0_.getSource().getLevel().getDifficulty();
         p_198346_0_.getSource().sendSuccess(new TranslationTextComponent("commands.difficulty.query", difficulty1.getDisplayName()), false);
         return difficulty1.getId();
      }));
   }

   public static int setDifficulty(CommandSource p_198345_0_, Difficulty p_198345_1_) throws CommandSyntaxException {
      MinecraftServer minecraftserver = p_198345_0_.getServer();
      if (minecraftserver.getWorldData().getDifficulty() == p_198345_1_) {
         throw ERROR_ALREADY_DIFFICULT.create(p_198345_1_.getKey());
      } else {
         minecraftserver.setDifficulty(p_198345_1_, true);
         p_198345_0_.sendSuccess(new TranslationTextComponent("commands.difficulty.success", p_198345_1_.getDisplayName()), true);
         return 0;
      }
   }
}
