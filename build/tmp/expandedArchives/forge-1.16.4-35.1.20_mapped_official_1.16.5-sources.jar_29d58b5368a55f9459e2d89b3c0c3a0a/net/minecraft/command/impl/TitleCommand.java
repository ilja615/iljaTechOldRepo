package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Locale;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;

public class TitleCommand {
   public static void register(CommandDispatcher<CommandSource> p_198839_0_) {
      p_198839_0_.register(Commands.literal("title").requires((p_198847_0_) -> {
         return p_198847_0_.hasPermission(2);
      }).then(Commands.argument("targets", EntityArgument.players()).then(Commands.literal("clear").executes((p_198838_0_) -> {
         return clearTitle(p_198838_0_.getSource(), EntityArgument.getPlayers(p_198838_0_, "targets"));
      })).then(Commands.literal("reset").executes((p_198841_0_) -> {
         return resetTitle(p_198841_0_.getSource(), EntityArgument.getPlayers(p_198841_0_, "targets"));
      })).then(Commands.literal("title").then(Commands.argument("title", ComponentArgument.textComponent()).executes((p_198837_0_) -> {
         return showTitle(p_198837_0_.getSource(), EntityArgument.getPlayers(p_198837_0_, "targets"), ComponentArgument.getComponent(p_198837_0_, "title"), STitlePacket.Type.TITLE);
      }))).then(Commands.literal("subtitle").then(Commands.argument("title", ComponentArgument.textComponent()).executes((p_198842_0_) -> {
         return showTitle(p_198842_0_.getSource(), EntityArgument.getPlayers(p_198842_0_, "targets"), ComponentArgument.getComponent(p_198842_0_, "title"), STitlePacket.Type.SUBTITLE);
      }))).then(Commands.literal("actionbar").then(Commands.argument("title", ComponentArgument.textComponent()).executes((p_198836_0_) -> {
         return showTitle(p_198836_0_.getSource(), EntityArgument.getPlayers(p_198836_0_, "targets"), ComponentArgument.getComponent(p_198836_0_, "title"), STitlePacket.Type.ACTIONBAR);
      }))).then(Commands.literal("times").then(Commands.argument("fadeIn", IntegerArgumentType.integer(0)).then(Commands.argument("stay", IntegerArgumentType.integer(0)).then(Commands.argument("fadeOut", IntegerArgumentType.integer(0)).executes((p_198843_0_) -> {
         return setTimes(p_198843_0_.getSource(), EntityArgument.getPlayers(p_198843_0_, "targets"), IntegerArgumentType.getInteger(p_198843_0_, "fadeIn"), IntegerArgumentType.getInteger(p_198843_0_, "stay"), IntegerArgumentType.getInteger(p_198843_0_, "fadeOut"));
      })))))));
   }

   private static int clearTitle(CommandSource p_198840_0_, Collection<ServerPlayerEntity> p_198840_1_) {
      STitlePacket stitlepacket = new STitlePacket(STitlePacket.Type.CLEAR, (ITextComponent)null);

      for(ServerPlayerEntity serverplayerentity : p_198840_1_) {
         serverplayerentity.connection.send(stitlepacket);
      }

      if (p_198840_1_.size() == 1) {
         p_198840_0_.sendSuccess(new TranslationTextComponent("commands.title.cleared.single", p_198840_1_.iterator().next().getDisplayName()), true);
      } else {
         p_198840_0_.sendSuccess(new TranslationTextComponent("commands.title.cleared.multiple", p_198840_1_.size()), true);
      }

      return p_198840_1_.size();
   }

   private static int resetTitle(CommandSource p_198844_0_, Collection<ServerPlayerEntity> p_198844_1_) {
      STitlePacket stitlepacket = new STitlePacket(STitlePacket.Type.RESET, (ITextComponent)null);

      for(ServerPlayerEntity serverplayerentity : p_198844_1_) {
         serverplayerentity.connection.send(stitlepacket);
      }

      if (p_198844_1_.size() == 1) {
         p_198844_0_.sendSuccess(new TranslationTextComponent("commands.title.reset.single", p_198844_1_.iterator().next().getDisplayName()), true);
      } else {
         p_198844_0_.sendSuccess(new TranslationTextComponent("commands.title.reset.multiple", p_198844_1_.size()), true);
      }

      return p_198844_1_.size();
   }

   private static int showTitle(CommandSource p_198846_0_, Collection<ServerPlayerEntity> p_198846_1_, ITextComponent p_198846_2_, STitlePacket.Type p_198846_3_) throws CommandSyntaxException {
      for(ServerPlayerEntity serverplayerentity : p_198846_1_) {
         serverplayerentity.connection.send(new STitlePacket(p_198846_3_, TextComponentUtils.updateForEntity(p_198846_0_, p_198846_2_, serverplayerentity, 0)));
      }

      if (p_198846_1_.size() == 1) {
         p_198846_0_.sendSuccess(new TranslationTextComponent("commands.title.show." + p_198846_3_.name().toLowerCase(Locale.ROOT) + ".single", p_198846_1_.iterator().next().getDisplayName()), true);
      } else {
         p_198846_0_.sendSuccess(new TranslationTextComponent("commands.title.show." + p_198846_3_.name().toLowerCase(Locale.ROOT) + ".multiple", p_198846_1_.size()), true);
      }

      return p_198846_1_.size();
   }

   private static int setTimes(CommandSource p_198845_0_, Collection<ServerPlayerEntity> p_198845_1_, int p_198845_2_, int p_198845_3_, int p_198845_4_) {
      STitlePacket stitlepacket = new STitlePacket(p_198845_2_, p_198845_3_, p_198845_4_);

      for(ServerPlayerEntity serverplayerentity : p_198845_1_) {
         serverplayerentity.connection.send(stitlepacket);
      }

      if (p_198845_1_.size() == 1) {
         p_198845_0_.sendSuccess(new TranslationTextComponent("commands.title.times.single", p_198845_1_.iterator().next().getDisplayName()), true);
      } else {
         p_198845_0_.sendSuccess(new TranslationTextComponent("commands.title.times.multiple", p_198845_1_.size()), true);
      }

      return p_198845_1_.size();
   }
}
