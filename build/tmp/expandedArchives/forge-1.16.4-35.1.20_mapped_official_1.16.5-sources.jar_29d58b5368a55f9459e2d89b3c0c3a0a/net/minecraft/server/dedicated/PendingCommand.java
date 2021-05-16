package net.minecraft.server.dedicated;

import net.minecraft.command.CommandSource;

public class PendingCommand {
   public final String msg;
   public final CommandSource source;

   public PendingCommand(String p_i48147_1_, CommandSource p_i48147_2_) {
      this.msg = p_i48147_1_;
      this.source = p_i48147_2_;
   }
}
