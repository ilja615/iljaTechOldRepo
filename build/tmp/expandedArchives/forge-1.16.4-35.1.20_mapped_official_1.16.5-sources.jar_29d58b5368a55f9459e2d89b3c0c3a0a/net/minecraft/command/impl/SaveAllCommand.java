package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TranslationTextComponent;

public class SaveAllCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.save.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198611_0_) {
      p_198611_0_.register(Commands.literal("save-all").requires((p_198615_0_) -> {
         return p_198615_0_.hasPermission(4);
      }).executes((p_198610_0_) -> {
         return saveAll(p_198610_0_.getSource(), false);
      }).then(Commands.literal("flush").executes((p_198613_0_) -> {
         return saveAll(p_198613_0_.getSource(), true);
      })));
   }

   private static int saveAll(CommandSource p_198614_0_, boolean p_198614_1_) throws CommandSyntaxException {
      p_198614_0_.sendSuccess(new TranslationTextComponent("commands.save.saving"), false);
      MinecraftServer minecraftserver = p_198614_0_.getServer();
      minecraftserver.getPlayerList().saveAll();
      boolean flag = minecraftserver.saveAllChunks(true, p_198614_1_, true);
      if (!flag) {
         throw ERROR_FAILED.create();
      } else {
         p_198614_0_.sendSuccess(new TranslationTextComponent("commands.save.success"), true);
         return 1;
      }
   }
}
