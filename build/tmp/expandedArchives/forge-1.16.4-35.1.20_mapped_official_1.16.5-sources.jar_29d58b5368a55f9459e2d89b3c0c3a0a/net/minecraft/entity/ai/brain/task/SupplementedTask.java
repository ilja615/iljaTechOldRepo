package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public class SupplementedTask<E extends LivingEntity> extends Task<E> {
   private final Predicate<E> predicate;
   private final Task<? super E> wrappedBehavior;
   private final boolean checkWhileRunningAlso;

   public SupplementedTask(Map<MemoryModuleType<?>, MemoryModuleStatus> p_i231528_1_, Predicate<E> p_i231528_2_, Task<? super E> p_i231528_3_, boolean p_i231528_4_) {
      super(mergeMaps(p_i231528_1_, p_i231528_3_.entryCondition));
      this.predicate = p_i231528_2_;
      this.wrappedBehavior = p_i231528_3_;
      this.checkWhileRunningAlso = p_i231528_4_;
   }

   private static Map<MemoryModuleType<?>, MemoryModuleStatus> mergeMaps(Map<MemoryModuleType<?>, MemoryModuleStatus> p_233943_0_, Map<MemoryModuleType<?>, MemoryModuleStatus> p_233943_1_) {
      Map<MemoryModuleType<?>, MemoryModuleStatus> map = Maps.newHashMap();
      map.putAll(p_233943_0_);
      map.putAll(p_233943_1_);
      return map;
   }

   public SupplementedTask(Predicate<E> p_i231529_1_, Task<? super E> p_i231529_2_) {
      this(ImmutableMap.of(), p_i231529_1_, p_i231529_2_, false);
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, E p_212832_2_) {
      return this.predicate.test(p_212832_2_) && this.wrappedBehavior.checkExtraStartConditions(p_212832_1_, p_212832_2_);
   }

   protected boolean canStillUse(ServerWorld p_212834_1_, E p_212834_2_, long p_212834_3_) {
      return this.checkWhileRunningAlso && this.predicate.test(p_212834_2_) && this.wrappedBehavior.canStillUse(p_212834_1_, p_212834_2_, p_212834_3_);
   }

   protected boolean timedOut(long p_220383_1_) {
      return false;
   }

   protected void start(ServerWorld p_212831_1_, E p_212831_2_, long p_212831_3_) {
      this.wrappedBehavior.start(p_212831_1_, p_212831_2_, p_212831_3_);
   }

   protected void tick(ServerWorld p_212833_1_, E p_212833_2_, long p_212833_3_) {
      this.wrappedBehavior.tick(p_212833_1_, p_212833_2_, p_212833_3_);
   }

   protected void stop(ServerWorld p_212835_1_, E p_212835_2_, long p_212835_3_) {
      this.wrappedBehavior.stop(p_212835_1_, p_212835_2_, p_212835_3_);
   }

   public String toString() {
      return "RunIf: " + this.wrappedBehavior;
   }
}
