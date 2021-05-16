package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.filter.IChatFilter;

public class MeCommand {
   public static void register(CommandDispatcher<CommandSource> p_198364_0_) {
      p_198364_0_.register(Commands.literal("me").then(Commands.argument("action", StringArgumentType.greedyString()).executes((p_198365_0_) -> {
         String s = StringArgumentType.getString(p_198365_0_, "action");
         Entity entity = p_198365_0_.getSource().getEntity();
         MinecraftServer minecraftserver = p_198365_0_.getSource().getServer();
         if (entity != null) {
            if (entity instanceof ServerPlayerEntity) {
               IChatFilter ichatfilter = ((ServerPlayerEntity)entity).getTextFilter();
               if (ichatfilter != null) {
                  ichatfilter.processStreamMessage(s).thenAcceptAsync((p_244713_3_) -> {
                     p_244713_3_.ifPresent((p_244712_3_) -> {
                        minecraftserver.getPlayerList().broadcastMessage(createMessage(p_198365_0_, p_244712_3_), ChatType.CHAT, entity.getUUID());
                     });
                  }, minecraftserver);
                  return 1;
               }
            }

            minecraftserver.getPlayerList().broadcastMessage(createMessage(p_198365_0_, s), ChatType.CHAT, entity.getUUID());
         } else {
            minecraftserver.getPlayerList().broadcastMessage(createMessage(p_198365_0_, s), ChatType.SYSTEM, Util.NIL_UUID);
         }

         return 1;
      })));
   }

   private static ITextComponent createMessage(CommandContext<CommandSource> p_244711_0_, String p_244711_1_) {
      return new TranslationTextComponent("chat.type.emote", p_244711_0_.getSource().getDisplayName(), p_244711_1_);
   }
}
