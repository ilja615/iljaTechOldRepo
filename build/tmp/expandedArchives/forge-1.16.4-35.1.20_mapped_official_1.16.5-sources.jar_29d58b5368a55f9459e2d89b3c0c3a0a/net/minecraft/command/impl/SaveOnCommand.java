package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class SaveOnCommand {
   private static final SimpleCommandExceptionType ERROR_ALREADY_ON = new SimpleCommandExceptionType(new TranslationTextComponent("commands.save.alreadyOn"));

   public static void register(CommandDispatcher<CommandSource> p_198621_0_) {
      p_198621_0_.register(Commands.literal("save-on").requires((p_198623_0_) -> {
         return p_198623_0_.hasPermission(4);
      }).executes((p_198622_0_) -> {
         CommandSource commandsource = p_198622_0_.getSource();
         boolean flag = false;

         for(ServerWorld serverworld : commandsource.getServer().getAllLevels()) {
            if (serverworld != null && serverworld.noSave) {
               serverworld.noSave = false;
               flag = true;
            }
         }

         if (!flag) {
            throw ERROR_ALREADY_ON.create();
         } else {
            commandsource.sendSuccess(new TranslationTextComponent("commands.save.enabled"), true);
            return 1;
         }
      }));
   }
}
