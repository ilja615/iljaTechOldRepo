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
import net.minecraft.server.management.BanList;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;

public class PardonCommand {
   private static final SimpleCommandExceptionType ERROR_NOT_BANNED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.pardon.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198547_0_) {
      p_198547_0_.register(Commands.literal("pardon").requires((p_198551_0_) -> {
         return p_198551_0_.hasPermission(3);
      }).then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((p_198549_0_, p_198549_1_) -> {
         return ISuggestionProvider.suggest(p_198549_0_.getSource().getServer().getPlayerList().getBans().getUserList(), p_198549_1_);
      }).executes((p_198550_0_) -> {
         return pardonPlayers(p_198550_0_.getSource(), GameProfileArgument.getGameProfiles(p_198550_0_, "targets"));
      })));
   }

   private static int pardonPlayers(CommandSource p_198548_0_, Collection<GameProfile> p_198548_1_) throws CommandSyntaxException {
      BanList banlist = p_198548_0_.getServer().getPlayerList().getBans();
      int i = 0;

      for(GameProfile gameprofile : p_198548_1_) {
         if (banlist.isBanned(gameprofile)) {
            banlist.remove(gameprofile);
            ++i;
            p_198548_0_.sendSuccess(new TranslationTextComponent("commands.pardon.success", TextComponentUtils.getDisplayName(gameprofile)), true);
         }
      }

      if (i == 0) {
         throw ERROR_NOT_BANNED.create();
      } else {
         return i;
      }
   }
}
