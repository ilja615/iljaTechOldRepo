package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.IPBanEntry;
import net.minecraft.server.management.IPBanList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class BanIpCommand {
   public static final Pattern IP_ADDRESS_PATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
   private static final SimpleCommandExceptionType ERROR_INVALID_IP = new SimpleCommandExceptionType(new TranslationTextComponent("commands.banip.invalid"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_BANNED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.banip.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198220_0_) {
      p_198220_0_.register(Commands.literal("ban-ip").requires((p_198222_0_) -> {
         return p_198222_0_.hasPermission(3);
      }).then(Commands.argument("target", StringArgumentType.word()).executes((p_198219_0_) -> {
         return banIpOrName(p_198219_0_.getSource(), StringArgumentType.getString(p_198219_0_, "target"), (ITextComponent)null);
      }).then(Commands.argument("reason", MessageArgument.message()).executes((p_198221_0_) -> {
         return banIpOrName(p_198221_0_.getSource(), StringArgumentType.getString(p_198221_0_, "target"), MessageArgument.getMessage(p_198221_0_, "reason"));
      }))));
   }

   private static int banIpOrName(CommandSource p_198223_0_, String p_198223_1_, @Nullable ITextComponent p_198223_2_) throws CommandSyntaxException {
      Matcher matcher = IP_ADDRESS_PATTERN.matcher(p_198223_1_);
      if (matcher.matches()) {
         return banIp(p_198223_0_, p_198223_1_, p_198223_2_);
      } else {
         ServerPlayerEntity serverplayerentity = p_198223_0_.getServer().getPlayerList().getPlayerByName(p_198223_1_);
         if (serverplayerentity != null) {
            return banIp(p_198223_0_, serverplayerentity.getIpAddress(), p_198223_2_);
         } else {
            throw ERROR_INVALID_IP.create();
         }
      }
   }

   private static int banIp(CommandSource p_198224_0_, String p_198224_1_, @Nullable ITextComponent p_198224_2_) throws CommandSyntaxException {
      IPBanList ipbanlist = p_198224_0_.getServer().getPlayerList().getIpBans();
      if (ipbanlist.isBanned(p_198224_1_)) {
         throw ERROR_ALREADY_BANNED.create();
      } else {
         List<ServerPlayerEntity> list = p_198224_0_.getServer().getPlayerList().getPlayersWithAddress(p_198224_1_);
         IPBanEntry ipbanentry = new IPBanEntry(p_198224_1_, (Date)null, p_198224_0_.getTextName(), (Date)null, p_198224_2_ == null ? null : p_198224_2_.getString());
         ipbanlist.add(ipbanentry);
         p_198224_0_.sendSuccess(new TranslationTextComponent("commands.banip.success", p_198224_1_, ipbanentry.getReason()), true);
         if (!list.isEmpty()) {
            p_198224_0_.sendSuccess(new TranslationTextComponent("commands.banip.info", list.size(), EntitySelector.joinNames(list)), true);
         }

         for(ServerPlayerEntity serverplayerentity : list) {
            serverplayerentity.connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.ip_banned"));
         }

         return list.size();
      }
   }
}
