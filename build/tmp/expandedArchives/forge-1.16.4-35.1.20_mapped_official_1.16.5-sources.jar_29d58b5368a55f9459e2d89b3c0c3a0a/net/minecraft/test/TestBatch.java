package net.minecraft.test;

import java.util.Collection;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.world.server.ServerWorld;

public class TestBatch {
   private final String name;
   private final Collection<TestFunctionInfo> testFunctions;
   @Nullable
   private final Consumer<ServerWorld> beforeBatchFunction;

   public TestBatch(String p_i226065_1_, Collection<TestFunctionInfo> p_i226065_2_, @Nullable Consumer<ServerWorld> p_i226065_3_) {
      if (p_i226065_2_.isEmpty()) {
         throw new IllegalArgumentException("A GameTestBatch must include at least one TestFunction!");
      } else {
         this.name = p_i226065_1_;
         this.testFunctions = p_i226065_2_;
         this.beforeBatchFunction = p_i226065_3_;
      }
   }

   public String getName() {
      return this.name;
   }

   public Collection<TestFunctionInfo> getTestFunctions() {
      return this.testFunctions;
   }

   public void runBeforeBatchFunction(ServerWorld p_229464_1_) {
      if (this.beforeBatchFunction != null) {
         this.beforeBatchFunction.accept(p_229464_1_);
      }

   }
}
