package net.minecraft.loot;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;

public class ValidationTracker {
   private final Multimap<String, String> problems;
   private final Supplier<String> context;
   private final LootParameterSet params;
   private final Function<ResourceLocation, ILootCondition> conditionResolver;
   private final Set<ResourceLocation> visitedConditions;
   private final Function<ResourceLocation, LootTable> tableResolver;
   private final Set<ResourceLocation> visitedTables;
   private String contextCache;

   public ValidationTracker(LootParameterSet p_i225889_1_, Function<ResourceLocation, ILootCondition> p_i225889_2_, Function<ResourceLocation, LootTable> p_i225889_3_) {
      this(HashMultimap.create(), () -> {
         return "";
      }, p_i225889_1_, p_i225889_2_, ImmutableSet.of(), p_i225889_3_, ImmutableSet.of());
   }

   public ValidationTracker(Multimap<String, String> p_i225888_1_, Supplier<String> p_i225888_2_, LootParameterSet p_i225888_3_, Function<ResourceLocation, ILootCondition> p_i225888_4_, Set<ResourceLocation> p_i225888_5_, Function<ResourceLocation, LootTable> p_i225888_6_, Set<ResourceLocation> p_i225888_7_) {
      this.problems = p_i225888_1_;
      this.context = p_i225888_2_;
      this.params = p_i225888_3_;
      this.conditionResolver = p_i225888_4_;
      this.visitedConditions = p_i225888_5_;
      this.tableResolver = p_i225888_6_;
      this.visitedTables = p_i225888_7_;
   }

   private String getContext() {
      if (this.contextCache == null) {
         this.contextCache = this.context.get();
      }

      return this.contextCache;
   }

   public void reportProblem(String p_227530_1_) {
      this.problems.put(this.getContext(), p_227530_1_);
   }

   public ValidationTracker forChild(String p_227534_1_) {
      return new ValidationTracker(this.problems, () -> {
         return this.getContext() + p_227534_1_;
      }, this.params, this.conditionResolver, this.visitedConditions, this.tableResolver, this.visitedTables);
   }

   public ValidationTracker enterTable(String p_227531_1_, ResourceLocation p_227531_2_) {
      ImmutableSet<ResourceLocation> immutableset = ImmutableSet.<ResourceLocation>builder().addAll(this.visitedTables).add(p_227531_2_).build();
      return new ValidationTracker(this.problems, () -> {
         return this.getContext() + p_227531_1_;
      }, this.params, this.conditionResolver, this.visitedConditions, this.tableResolver, immutableset);
   }

   public ValidationTracker enterCondition(String p_227535_1_, ResourceLocation p_227535_2_) {
      ImmutableSet<ResourceLocation> immutableset = ImmutableSet.<ResourceLocation>builder().addAll(this.visitedConditions).add(p_227535_2_).build();
      return new ValidationTracker(this.problems, () -> {
         return this.getContext() + p_227535_1_;
      }, this.params, this.conditionResolver, immutableset, this.tableResolver, this.visitedTables);
   }

   public boolean hasVisitedTable(ResourceLocation p_227532_1_) {
      return this.visitedTables.contains(p_227532_1_);
   }

   public boolean hasVisitedCondition(ResourceLocation p_227536_1_) {
      return this.visitedConditions.contains(p_227536_1_);
   }

   public Multimap<String, String> getProblems() {
      return ImmutableMultimap.copyOf(this.problems);
   }

   public void validateUser(IParameterized p_227528_1_) {
      this.params.validateUser(this, p_227528_1_);
   }

   @Nullable
   public LootTable resolveLootTable(ResourceLocation p_227539_1_) {
      return this.tableResolver.apply(p_227539_1_);
   }

   @Nullable
   public ILootCondition resolveCondition(ResourceLocation p_227541_1_) {
      return this.conditionResolver.apply(p_227541_1_);
   }

   public ValidationTracker setParams(LootParameterSet p_227529_1_) {
      return new ValidationTracker(this.problems, this.context, p_227529_1_, this.conditionResolver, this.visitedConditions, this.tableResolver, this.visitedTables);
   }
}
