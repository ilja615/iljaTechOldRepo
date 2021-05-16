package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;

public class SpectateCommand {
   private static final SimpleCommandExceptionType ERROR_SELF = new SimpleCommandExceptionType(new TranslationTextComponent("commands.spectate.self"));
   private static final DynamicCommandExceptionType ERROR_NOT_SPECTATOR = new DynamicCommandExceptionType((p_229830_0_) -> {
      return new TranslationTextComponent("commands.spectate.not_spectator", p_229830_0_);
   });

   public static void register(CommandDispatcher<CommandSource> p_229826_0_) {
      p_229826_0_.register(Commands.literal("spectate").requires((p_229828_0_) -> {
         return p_229828_0_.hasPermission(2);
      }).executes((p_229832_0_) -> {
         return spectate(p_229832_0_.getSource(), (Entity)null, p_229832_0_.getSource().getPlayerOrException());
      }).then(Commands.argument("target", EntityArgument.entity()).executes((p_229831_0_) -> {
         return spectate(p_229831_0_.getSource(), EntityArgument.getEntity(p_229831_0_, "target"), p_229831_0_.getSource().getPlayerOrException());
      }).then(Commands.argument("player", EntityArgument.player()).executes((p_229827_0_) -> {
         return spectate(p_229827_0_.getSource(), EntityArgument.getEntity(p_229827_0_, "target"), EntityArgument.getPlayer(p_229827_0_, "player"));
      }))));
   }

   private static int spectate(CommandSource p_229829_0_, @Nullable Entity p_229829_1_, ServerPlayerEntity p_229829_2_) throws CommandSyntaxException {
      if (p_229829_2_ == p_229829_1_) {
         throw ERROR_SELF.create();
      } else if (p_229829_2_.gameMode.getGameModeForPlayer() != GameType.SPECTATOR) {
         throw ERROR_NOT_SPECTATOR.create(p_229829_2_.getDisplayName());
      } else {
         p_229829_2_.setCamera(p_229829_1_);
         if (p_229829_1_ != null) {
            p_229829_0_.sendSuccess(new TranslationTextComponent("commands.spectate.success.started", p_229829_1_.getDisplayName()), false);
         } else {
            p_229829_0_.sendSuccess(new TranslationTextComponent("commands.spectate.success.stopped"), false);
         }

         return 1;
      }
   }
}
