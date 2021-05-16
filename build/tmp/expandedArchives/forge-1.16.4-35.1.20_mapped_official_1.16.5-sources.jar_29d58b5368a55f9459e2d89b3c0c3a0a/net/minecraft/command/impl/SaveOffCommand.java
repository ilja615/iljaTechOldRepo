package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class SaveOffCommand {
   private static final SimpleCommandExceptionType ERROR_ALREADY_OFF = new SimpleCommandExceptionType(new TranslationTextComponent("commands.save.alreadyOff"));

   public static void register(CommandDispatcher<CommandSource> p_198617_0_) {
      p_198617_0_.register(Commands.literal("save-off").requires((p_198619_0_) -> {
         return p_198619_0_.hasPermission(4);
      }).executes((p_198618_0_) -> {
         CommandSource commandsource = p_198618_0_.getSource();
         boolean flag = false;

         for(ServerWorld serverworld : commandsource.getServer().getAllLevels()) {
            if (serverworld != null && !serverworld.noSave) {
               serverworld.noSave = true;
               flag = true;
            }
         }

         if (!flag) {
            throw ERROR_ALREADY_OFF.create();
         } else {
            commandsource.sendSuccess(new TranslationTextComponent("commands.save.disabled"), true);
            return 1;
         }
      }));
   }
}
