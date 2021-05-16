package net.minecraft.command.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TranslationTextComponent;

public class OpCommand {
   private static final SimpleCommandExceptionType ERROR_ALREADY_OP = new SimpleCommandExceptionType(new TranslationTextComponent("commands.op.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198541_0_) {
      p_198541_0_.register(Commands.literal("op").requires((p_198545_0_) -> {
         return p_198545_0_.hasPermission(3);
      }).then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((p_198543_0_, p_198543_1_) -> {
         PlayerList playerlist = p_198543_0_.getSource().getServer().getPlayerList();
         return ISuggestionProvider.suggest(playerlist.getPlayers().stream().filter((p_198540_1_) -> {
            return !playerlist.isOp(p_198540_1_.getGameProfile());
         }).map((p_200545_0_) -> {
            return p_200545_0_.getGameProfile().getName();
         }), p_198543_1_);
      }).executes((p_198544_0_) -> {
         return opPlayers(p_198544_0_.getSource(), GameProfileArgument.getGameProfiles(p_198544_0_, "targets"));
      })));
   }

   private static int opPlayers(CommandSource p_198542_0_, Collection<GameProfile> p_198542_1_) throws CommandSyntaxException {
      PlayerList playerlist = p_198542_0_.getServer().getPlayerList();
      int i = 0;

      for(GameProfile gameprofile : p_198542_1_) {
         if (!playerlist.isOp(gameprofile)) {
            playerlist.op(gameprofile);
            ++i;
            p_198542_0_.sendSuccess(new TranslationTextComponent("commands.op.success", p_198542_1_.iterator().next().getName()), true);
         }
      }

      if (i == 0) {
         throw ERROR_ALREADY_OP.create();
      } else {
         return i;
      }
   }
}
