package net.minecraft.command.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.BanList;
import net.minecraft.server.management.ProfileBanEntry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;

public class BanCommand {
   private static final SimpleCommandExceptionType ERROR_ALREADY_BANNED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.ban.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198235_0_) {
      p_198235_0_.register(Commands.literal("ban").requires((p_198238_0_) -> {
         return p_198238_0_.hasPermission(3);
      }).then(Commands.argument("targets", GameProfileArgument.gameProfile()).executes((p_198234_0_) -> {
         return banPlayers(p_198234_0_.getSource(), GameProfileArgument.getGameProfiles(p_198234_0_, "targets"), (ITextComponent)null);
      }).then(Commands.argument("reason", MessageArgument.message()).executes((p_198237_0_) -> {
         return banPlayers(p_198237_0_.getSource(), GameProfileArgument.getGameProfiles(p_198237_0_, "targets"), MessageArgument.getMessage(p_198237_0_, "reason"));
      }))));
   }

   private static int banPlayers(CommandSource p_198236_0_, Collection<GameProfile> p_198236_1_, @Nullable ITextComponent p_198236_2_) throws CommandSyntaxException {
      BanList banlist = p_198236_0_.getServer().getPlayerList().getBans();
      int i = 0;

      for(GameProfile gameprofile : p_198236_1_) {
         if (!banlist.isBanned(gameprofile)) {
            ProfileBanEntry profilebanentry = new ProfileBanEntry(gameprofile, (Date)null, p_198236_0_.getTextName(), (Date)null, p_198236_2_ == null ? null : p_198236_2_.getString());
            banlist.add(profilebanentry);
            ++i;
            p_198236_0_.sendSuccess(new TranslationTextComponent("commands.ban.success", TextComponentUtils.getDisplayName(gameprofile), profilebanentry.getReason()), true);
            ServerPlayerEntity serverplayerentity = p_198236_0_.getServer().getPlayerList().getPlayer(gameprofile.getId());
            if (serverplayerentity != null) {
               serverplayerentity.connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.banned"));
            }
         }
      }

      if (i == 0) {
         throw ERROR_ALREADY_BANNED.create();
      } else {
         return i;
      }
   }
}
