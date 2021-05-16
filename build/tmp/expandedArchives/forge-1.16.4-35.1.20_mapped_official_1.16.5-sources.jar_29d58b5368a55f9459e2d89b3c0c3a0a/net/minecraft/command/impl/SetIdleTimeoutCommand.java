package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;

public class SetIdleTimeoutCommand {
   public static void register(CommandDispatcher<CommandSource> p_198690_0_) {
      p_198690_0_.register(Commands.literal("setidletimeout").requires((p_198692_0_) -> {
         return p_198692_0_.hasPermission(3);
      }).then(Commands.argument("minutes", IntegerArgumentType.integer(0)).executes((p_198691_0_) -> {
         return setIdleTimeout(p_198691_0_.getSource(), IntegerArgumentType.getInteger(p_198691_0_, "minutes"));
      })));
   }

   private static int setIdleTimeout(CommandSource p_198693_0_, int p_198693_1_) {
      p_198693_0_.getServer().setPlayerIdleTimeout(p_198693_1_);
      p_198693_0_.sendSuccess(new TranslationTextComponent("commands.setidletimeout.success", p_198693_1_), true);
      return p_198693_1_;
   }
}
