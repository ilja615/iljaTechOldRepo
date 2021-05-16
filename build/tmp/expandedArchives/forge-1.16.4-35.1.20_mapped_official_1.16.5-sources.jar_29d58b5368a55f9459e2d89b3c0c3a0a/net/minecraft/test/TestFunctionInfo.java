package net.minecraft.test;

import java.util.function.Consumer;
import net.minecraft.util.Rotation;

public class TestFunctionInfo {
   private final String batchName = null;
   private final String testName = null;
   private final String structureName = null;
   private final boolean required = false;
   private final Consumer<TestTrackerHolder> function = null;
   private final int maxTicks = 0;
   private final long setupTicks = 0L;
   private final Rotation rotation = null;

   public void run(TestTrackerHolder p_229658_1_) {
      this.function.accept(p_229658_1_);
   }

   public String getTestName() {
      return this.testName;
   }

   public String getStructureName() {
      return this.structureName;
   }

   public String toString() {
      return this.testName;
   }

   public int getMaxTicks() {
      return this.maxTicks;
   }

   public boolean isRequired() {
      return this.required;
   }

   public String getBatchName() {
      return this.batchName;
   }

   public long getSetupTicks() {
      return this.setupTicks;
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   private TestFunctionInfo() {
      throw new RuntimeException("Synthetic constructor added by MCP, do not call");
   }
}
