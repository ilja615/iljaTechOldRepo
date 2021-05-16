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
import net.minecraft.server.management.WhiteList;
import net.minecraft.server.management.WhitelistEntry;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;

public class WhitelistCommand {
   private static final SimpleCommandExceptionType ERROR_ALREADY_ENABLED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.whitelist.alreadyOn"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_DISABLED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.whitelist.alreadyOff"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_WHITELISTED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.whitelist.add.failed"));
   private static final SimpleCommandExceptionType ERROR_NOT_WHITELISTED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.whitelist.remove.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198873_0_) {
      p_198873_0_.register(Commands.literal("whitelist").requires((p_198877_0_) -> {
         return p_198877_0_.hasPermission(3);
      }).then(Commands.literal("on").executes((p_198872_0_) -> {
         return enableWhitelist(p_198872_0_.getSource());
      })).then(Commands.literal("off").executes((p_198874_0_) -> {
         return disableWhitelist(p_198874_0_.getSource());
      })).then(Commands.literal("list").executes((p_198878_0_) -> {
         return showList(p_198878_0_.getSource());
      })).then(Commands.literal("add").then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((p_198879_0_, p_198879_1_) -> {
         PlayerList playerlist = p_198879_0_.getSource().getServer().getPlayerList();
         return ISuggestionProvider.suggest(playerlist.getPlayers().stream().filter((p_198871_1_) -> {
            return !playerlist.getWhiteList().isWhiteListed(p_198871_1_.getGameProfile());
         }).map((p_200567_0_) -> {
            return p_200567_0_.getGameProfile().getName();
         }), p_198879_1_);
      }).executes((p_198875_0_) -> {
         return addPlayers(p_198875_0_.getSource(), GameProfileArgument.getGameProfiles(p_198875_0_, "targets"));
      }))).then(Commands.literal("remove").then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((p_198881_0_, p_198881_1_) -> {
         return ISuggestionProvider.suggest(p_198881_0_.getSource().getServer().getPlayerList().getWhiteListNames(), p_198881_1_);
      }).executes((p_198870_0_) -> {
         return removePlayers(p_198870_0_.getSource(), GameProfileArgument.getGameProfiles(p_198870_0_, "targets"));
      }))).then(Commands.literal("reload").executes((p_198882_0_) -> {
         return reload(p_198882_0_.getSource());
      })));
   }

   private static int reload(CommandSource p_198883_0_) {
      p_198883_0_.getServer().getPlayerList().reloadWhiteList();
      p_198883_0_.sendSuccess(new TranslationTextComponent("commands.whitelist.reloaded"), true);
      p_198883_0_.getServer().kickUnlistedPlayers(p_198883_0_);
      return 1;
   }

   private static int addPlayers(CommandSource p_198880_0_, Collection<GameProfile> p_198880_1_) throws CommandSyntaxException {
      WhiteList whitelist = p_198880_0_.getServer().getPlayerList().getWhiteList();
      int i = 0;

      for(GameProfile gameprofile : p_198880_1_) {
         if (!whitelist.isWhiteListed(gameprofile)) {
            WhitelistEntry whitelistentry = new WhitelistEntry(gameprofile);
            whitelist.add(whitelistentry);
            p_198880_0_.sendSuccess(new TranslationTextComponent("commands.whitelist.add.success", TextComponentUtils.getDisplayName(gameprofile)), true);
            ++i;
         }
      }

      if (i == 0) {
         throw ERROR_ALREADY_WHITELISTED.create();
      } else {
         return i;
      }
   }

   private static int removePlayers(CommandSource p_198876_0_, Collection<GameProfile> p_198876_1_) throws CommandSyntaxException {
      WhiteList whitelist = p_198876_0_.getServer().getPlayerList().getWhiteList();
      int i = 0;

      for(GameProfile gameprofile : p_198876_1_) {
         if (whitelist.isWhiteListed(gameprofile)) {
            WhitelistEntry whitelistentry = new WhitelistEntry(gameprofile);
            whitelist.remove(whitelistentry);
            p_198876_0_.sendSuccess(new TranslationTextComponent("commands.whitelist.remove.success", TextComponentUtils.getDisplayName(gameprofile)), true);
            ++i;
         }
      }

      if (i == 0) {
         throw ERROR_NOT_WHITELISTED.create();
      } else {
         p_198876_0_.getServer().kickUnlistedPlayers(p_198876_0_);
         return i;
      }
   }

   private static int enableWhitelist(CommandSource p_198884_0_) throws CommandSyntaxException {
      PlayerList playerlist = p_198884_0_.getServer().getPlayerList();
      if (playerlist.isUsingWhitelist()) {
         throw ERROR_ALREADY_ENABLED.create();
      } else {
         playerlist.setUsingWhiteList(true);
         p_198884_0_.sendSuccess(new TranslationTextComponent("commands.whitelist.enabled"), true);
         p_198884_0_.getServer().kickUnlistedPlayers(p_198884_0_);
         return 1;
      }
   }

   private static int disableWhitelist(CommandSource p_198885_0_) throws CommandSyntaxException {
      PlayerList playerlist = p_198885_0_.getServer().getPlayerList();
      if (!playerlist.isUsingWhitelist()) {
         throw ERROR_ALREADY_DISABLED.create();
      } else {
         playerlist.setUsingWhiteList(false);
         p_198885_0_.sendSuccess(new TranslationTextComponent("commands.whitelist.disabled"), true);
         return 1;
      }
   }

   private static int showList(CommandSource p_198886_0_) {
      String[] astring = p_198886_0_.getServer().getPlayerList().getWhiteListNames();
      if (astring.length == 0) {
         p_198886_0_.sendSuccess(new TranslationTextComponent("commands.whitelist.none"), false);
      } else {
         p_198886_0_.sendSuccess(new TranslationTextComponent("commands.whitelist.list", astring.length, String.join(", ", astring)), false);
      }

      return astring.length;
   }
}
