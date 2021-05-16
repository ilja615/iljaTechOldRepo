package net.minecraft.test;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;

public class TestBlockPosException extends TestRuntimeException {
   private final BlockPos absolutePos = null;
   private final BlockPos relativePos = null;
   private final long tick = 0L;

   public String getMessage() {
      String s = "" + this.absolutePos.getX() + "," + this.absolutePos.getY() + "," + this.absolutePos.getZ() + " (relative: " + this.relativePos.getX() + "," + this.relativePos.getY() + "," + this.relativePos.getZ() + ")";
      return super.getMessage() + " at " + s + " (t=" + this.tick + ")";
   }

   @Nullable
   public String getMessageToShowAtBlock() {
      return super.getMessage() + " here";
   }

   @Nullable
   public BlockPos getAbsolutePos() {
      return this.absolutePos;
   }

   private TestBlockPosException() {
      super("Synthetic constructor added by MCP, do not call");
      throw new RuntimeException("Synthetic constructor added by MCP, do not call");
   }
}
