package net.minecraft.network.rcon;

import java.util.UUID;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

public class RConConsoleSource implements ICommandSource {
   private static final StringTextComponent RCON_COMPONENT = new StringTextComponent("Rcon");
   private final StringBuffer buffer = new StringBuffer();
   private final MinecraftServer server;

   public RConConsoleSource(MinecraftServer p_i46835_1_) {
      this.server = p_i46835_1_;
   }

   public void prepareForCommand() {
      this.buffer.setLength(0);
   }

   public String getCommandResponse() {
      return this.buffer.toString();
   }

   public CommandSource createCommandSourceStack() {
      ServerWorld serverworld = this.server.overworld();
      return new CommandSource(this, Vector3d.atLowerCornerOf(serverworld.getSharedSpawnPos()), Vector2f.ZERO, serverworld, 4, "Rcon", RCON_COMPONENT, this.server, (Entity)null);
   }

   public void sendMessage(ITextComponent p_145747_1_, UUID p_145747_2_) {
      this.buffer.append(p_145747_1_.getString()).append("\n"); // FIX MC-7569 - RCON has no newlines in multiline output
   }

   public boolean acceptsSuccess() {
      return true;
   }

   public boolean acceptsFailure() {
      return true;
   }

   public boolean shouldInformAdmins() {
      return this.server.shouldRconBroadcast();
   }
}
