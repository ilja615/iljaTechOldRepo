package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class MessageCommand {
   public static void register(CommandDispatcher<CommandSource> p_198537_0_) {
      LiteralCommandNode<CommandSource> literalcommandnode = p_198537_0_.register(Commands.literal("msg").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("message", MessageArgument.message()).executes((p_198539_0_) -> {
         return sendMessage(p_198539_0_.getSource(), EntityArgument.getPlayers(p_198539_0_, "targets"), MessageArgument.getMessage(p_198539_0_, "message"));
      }))));
      p_198537_0_.register(Commands.literal("tell").redirect(literalcommandnode));
      p_198537_0_.register(Commands.literal("w").redirect(literalcommandnode));
   }

   private static int sendMessage(CommandSource p_198538_0_, Collection<ServerPlayerEntity> p_198538_1_, ITextComponent p_198538_2_) {
      UUID uuid = p_198538_0_.getEntity() == null ? Util.NIL_UUID : p_198538_0_.getEntity().getUUID();
      Entity entity = p_198538_0_.getEntity();
      Consumer<ITextComponent> consumer;
      if (entity instanceof ServerPlayerEntity) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entity;
         consumer = (p_244374_2_) -> {
            serverplayerentity.sendMessage((new TranslationTextComponent("commands.message.display.outgoing", p_244374_2_, p_198538_2_)).withStyle(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC}), serverplayerentity.getUUID());
         };
      } else {
         consumer = (p_244375_2_) -> {
            p_198538_0_.sendSuccess((new TranslationTextComponent("commands.message.display.outgoing", p_244375_2_, p_198538_2_)).withStyle(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC}), false);
         };
      }

      for(ServerPlayerEntity serverplayerentity1 : p_198538_1_) {
         consumer.accept(serverplayerentity1.getDisplayName());
         serverplayerentity1.sendMessage((new TranslationTextComponent("commands.message.display.incoming", p_198538_0_.getDisplayName(), p_198538_2_)).withStyle(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC}), uuid);
      }

      return p_198538_1_.size();
   }
}
