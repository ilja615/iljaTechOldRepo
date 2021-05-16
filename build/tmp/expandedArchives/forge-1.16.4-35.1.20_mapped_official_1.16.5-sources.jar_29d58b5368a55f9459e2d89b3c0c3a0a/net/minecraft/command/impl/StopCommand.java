package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;

public class StopCommand {
   public static void register(CommandDispatcher<CommandSource> p_198725_0_) {
      p_198725_0_.register(Commands.literal("stop").requires((p_198727_0_) -> {
         return p_198727_0_.hasPermission(4);
      }).executes((p_198726_0_) -> {
         p_198726_0_.getSource().sendSuccess(new TranslationTextComponent("commands.stop.stopping"), true);
         p_198726_0_.getSource().getServer().halt(false);
         return 1;
      }));
   }
}
