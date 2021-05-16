package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;

public class GameModeCommand {
   public static void register(CommandDispatcher<CommandSource> p_198482_0_) {
      LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("gamemode").requires((p_198485_0_) -> {
         return p_198485_0_.hasPermission(2);
      });

      for(GameType gametype : GameType.values()) {
         if (gametype != GameType.NOT_SET) {
            literalargumentbuilder.then(Commands.literal(gametype.getName()).executes((p_198483_1_) -> {
               return setMode(p_198483_1_, Collections.singleton(p_198483_1_.getSource().getPlayerOrException()), gametype);
            }).then(Commands.argument("target", EntityArgument.players()).executes((p_198486_1_) -> {
               return setMode(p_198486_1_, EntityArgument.getPlayers(p_198486_1_, "target"), gametype);
            })));
         }
      }

      p_198482_0_.register(literalargumentbuilder);
   }

   private static void logGamemodeChange(CommandSource p_208517_0_, ServerPlayerEntity p_208517_1_, GameType p_208517_2_) {
      ITextComponent itextcomponent = new TranslationTextComponent("gameMode." + p_208517_2_.getName());
      if (p_208517_0_.getEntity() == p_208517_1_) {
         p_208517_0_.sendSuccess(new TranslationTextComponent("commands.gamemode.success.self", itextcomponent), true);
      } else {
         if (p_208517_0_.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
            p_208517_1_.sendMessage(new TranslationTextComponent("gameMode.changed", itextcomponent), Util.NIL_UUID);
         }

         p_208517_0_.sendSuccess(new TranslationTextComponent("commands.gamemode.success.other", p_208517_1_.getDisplayName(), itextcomponent), true);
      }

   }

   private static int setMode(CommandContext<CommandSource> p_198484_0_, Collection<ServerPlayerEntity> p_198484_1_, GameType p_198484_2_) {
      int i = 0;

      for(ServerPlayerEntity serverplayerentity : p_198484_1_) {
         if (serverplayerentity.gameMode.getGameModeForPlayer() != p_198484_2_) {
            serverplayerentity.setGameMode(p_198484_2_);
            logGamemodeChange(p_198484_0_.getSource(), serverplayerentity, p_198484_2_);
            ++i;
         }
      }

      return i;
   }
}
