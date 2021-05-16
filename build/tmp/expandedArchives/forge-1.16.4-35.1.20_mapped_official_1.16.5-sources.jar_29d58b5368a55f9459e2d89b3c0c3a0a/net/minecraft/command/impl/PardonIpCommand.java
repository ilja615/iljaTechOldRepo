package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.regex.Matcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.server.management.IPBanList;
import net.minecraft.util.text.TranslationTextComponent;

public class PardonIpCommand {
   private static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(new TranslationTextComponent("commands.pardonip.invalid"));
   private static final SimpleCommandExceptionType ERROR_NOT_BANNED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.pardonip.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198553_0_) {
      p_198553_0_.register(Commands.literal("pardon-ip").requires((p_198556_0_) -> {
         return p_198556_0_.hasPermission(3);
      }).then(Commands.argument("target", StringArgumentType.word()).suggests((p_198554_0_, p_198554_1_) -> {
         return ISuggestionProvider.suggest(p_198554_0_.getSource().getServer().getPlayerList().getIpBans().getUserList(), p_198554_1_);
      }).executes((p_198555_0_) -> {
         return unban(p_198555_0_.getSource(), StringArgumentType.getString(p_198555_0_, "target"));
      })));
   }

   private static int unban(CommandSource p_198557_0_, String p_198557_1_) throws CommandSyntaxException {
      Matcher matcher = BanIpCommand.IP_ADDRESS_PATTERN.matcher(p_198557_1_);
      if (!matcher.matches()) {
         throw ERROR_INVALID.create();
      } else {
         IPBanList ipbanlist = p_198557_0_.getServer().getPlayerList().getIpBans();
         if (!ipbanlist.isBanned(p_198557_1_)) {
            throw ERROR_NOT_BANNED.create();
         } else {
            ipbanlist.remove(p_198557_1_);
            p_198557_0_.sendSuccess(new TranslationTextComponent("commands.pardonip.success", p_198557_1_), true);
            return 1;
         }
      }
   }
}
