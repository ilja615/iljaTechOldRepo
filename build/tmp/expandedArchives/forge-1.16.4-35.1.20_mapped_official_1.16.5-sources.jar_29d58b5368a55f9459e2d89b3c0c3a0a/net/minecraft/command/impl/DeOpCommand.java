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

public class DeOpCommand {
   private static final SimpleCommandExceptionType ERROR_NOT_OP = new SimpleCommandExceptionType(new TranslationTextComponent("commands.deop.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198321_0_) {
      p_198321_0_.register(Commands.literal("deop").requires((p_198325_0_) -> {
         return p_198325_0_.hasPermission(3);
      }).then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((p_198323_0_, p_198323_1_) -> {
         return ISuggestionProvider.suggest(p_198323_0_.getSource().getServer().getPlayerList().getOpNames(), p_198323_1_);
      }).executes((p_198324_0_) -> {
         return deopPlayers(p_198324_0_.getSource(), GameProfileArgument.getGameProfiles(p_198324_0_, "targets"));
      })));
   }

   private static int deopPlayers(CommandSource p_198322_0_, Collection<GameProfile> p_198322_1_) throws CommandSyntaxException {
      PlayerList playerlist = p_198322_0_.getServer().getPlayerList();
      int i = 0;

      for(GameProfile gameprofile : p_198322_1_) {
         if (playerlist.isOp(gameprofile)) {
            playerlist.deop(gameprofile);
            ++i;
            p_198322_0_.sendSuccess(new TranslationTextComponent("commands.deop.success", p_198322_1_.iterator().next().getName()), true);
         }
      }

      if (i == 0) {
         throw ERROR_NOT_OP.create();
      } else {
         p_198322_0_.getServer().kickUnlistedPlayers(p_198322_0_);
         return i;
      }
   }
}
