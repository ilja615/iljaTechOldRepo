package net.minecraft.test;

import javax.annotation.Nullable;

class TestTickResult {
   @Nullable
   public final Long expectedDelay = null;
   public final Runnable assertion = null;

   private TestTickResult() {
      throw new RuntimeException("Synthetic constructor added by MCP, do not call");
   }
}
