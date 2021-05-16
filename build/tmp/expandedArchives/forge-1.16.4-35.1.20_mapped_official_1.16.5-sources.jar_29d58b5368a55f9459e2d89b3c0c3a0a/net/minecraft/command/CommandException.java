package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.text.ITextComponent;

public class CommandException extends RuntimeException {
   private final ITextComponent message;

   public CommandException(ITextComponent p_i47972_1_) {
      super(p_i47972_1_.getString(), (Throwable)null, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES);
      this.message = p_i47972_1_;
   }

   public ITextComponent getComponent() {
      return this.message;
   }
}
