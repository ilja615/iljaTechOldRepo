package net.minecraft.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.world.server.ServerWorld;

public class TestRegistry {
   private static final Collection<TestFunctionInfo> testFunctions = Lists.newArrayList();
   private static final Set<String> testClassNames = Sets.newHashSet();
   private static final Map<String, Consumer<ServerWorld>> beforeBatchFunctions = Maps.newHashMap();
   private static final Collection<TestFunctionInfo> lastFailedTests = Sets.newHashSet();

   public static Collection<TestFunctionInfo> getTestFunctionsForClassName(String p_229530_0_) {
      return testFunctions.stream().filter((p_229535_1_) -> {
         return isTestFunctionPartOfClass(p_229535_1_, p_229530_0_);
      }).collect(Collectors.toList());
   }

   public static Collection<TestFunctionInfo> getAllTestFunctions() {
      return testFunctions;
   }

   public static Collection<String> getAllTestClassNames() {
      return testClassNames;
   }

   public static boolean isTestClass(String p_229534_0_) {
      return testClassNames.contains(p_229534_0_);
   }

   @Nullable
   public static Consumer<ServerWorld> getBeforeBatchFunction(String p_229536_0_) {
      return beforeBatchFunctions.get(p_229536_0_);
   }

   public static Optional<TestFunctionInfo> findTestFunction(String p_229537_0_) {
      return getAllTestFunctions().stream().filter((p_229531_1_) -> {
         return p_229531_1_.getTestName().equalsIgnoreCase(p_229537_0_);
      }).findFirst();
   }

   public static TestFunctionInfo getTestFunction(String p_229538_0_) {
      Optional<TestFunctionInfo> optional = findTestFunction(p_229538_0_);
      if (!optional.isPresent()) {
         throw new IllegalArgumentException("Can't find the test function for " + p_229538_0_);
      } else {
         return optional.get();
      }
   }

   private static boolean isTestFunctionPartOfClass(TestFunctionInfo p_229532_0_, String p_229532_1_) {
      return p_229532_0_.getTestName().toLowerCase().startsWith(p_229532_1_.toLowerCase() + ".");
   }

   public static Collection<TestFunctionInfo> getLastFailedTests() {
      return lastFailedTests;
   }

   public static void rememberFailedTest(TestFunctionInfo p_240548_0_) {
      lastFailedTests.add(p_240548_0_);
   }

   public static void forgetFailedTests() {
      lastFailedTests.clear();
   }
}
