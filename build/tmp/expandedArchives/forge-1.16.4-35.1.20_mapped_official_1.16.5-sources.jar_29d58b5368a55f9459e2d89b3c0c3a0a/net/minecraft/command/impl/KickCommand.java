package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class KickCommand {
   public static void register(CommandDispatcher<CommandSource> p_198514_0_) {
      p_198514_0_.register(Commands.literal("kick").requires((p_198517_0_) -> {
         return p_198517_0_.hasPermission(3);
      }).then(Commands.argument("targets", EntityArgument.players()).executes((p_198513_0_) -> {
         return kickPlayers(p_198513_0_.getSource(), EntityArgument.getPlayers(p_198513_0_, "targets"), new TranslationTextComponent("multiplayer.disconnect.kicked"));
      }).then(Commands.argument("reason", MessageArgument.message()).executes((p_198516_0_) -> {
         return kickPlayers(p_198516_0_.getSource(), EntityArgument.getPlayers(p_198516_0_, "targets"), MessageArgument.getMessage(p_198516_0_, "reason"));
      }))));
   }

   private static int kickPlayers(CommandSource p_198515_0_, Collection<ServerPlayerEntity> p_198515_1_, ITextComponent p_198515_2_) {
      for(ServerPlayerEntity serverplayerentity : p_198515_1_) {
         serverplayerentity.connection.disconnect(p_198515_2_);
         p_198515_0_.sendSuccess(new TranslationTextComponent("commands.kick.success", serverplayerentity.getDisplayName(), p_198515_2_), true);
      }

      return p_198515_1_.size();
   }
}
