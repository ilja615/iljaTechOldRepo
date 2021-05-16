package net.minecraft.entity.ai.brain.task;

import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public abstract class Task<E extends LivingEntity> {
   protected final Map<MemoryModuleType<?>, MemoryModuleStatus> entryCondition;
   private Task.Status status = Task.Status.STOPPED;
   private long endTimestamp;
   private final int minDuration;
   private final int maxDuration;

   public Task(Map<MemoryModuleType<?>, MemoryModuleStatus> p_i51504_1_) {
      this(p_i51504_1_, 60);
   }

   public Task(Map<MemoryModuleType<?>, MemoryModuleStatus> p_i51505_1_, int p_i51505_2_) {
      this(p_i51505_1_, p_i51505_2_, p_i51505_2_);
   }

   public Task(Map<MemoryModuleType<?>, MemoryModuleStatus> p_i51506_1_, int p_i51506_2_, int p_i51506_3_) {
      this.minDuration = p_i51506_2_;
      this.maxDuration = p_i51506_3_;
      this.entryCondition = p_i51506_1_;
   }

   public Task.Status getStatus() {
      return this.status;
   }

   public final boolean tryStart(ServerWorld p_220378_1_, E p_220378_2_, long p_220378_3_) {
      if (this.hasRequiredMemories(p_220378_2_) && this.checkExtraStartConditions(p_220378_1_, p_220378_2_)) {
         this.status = Task.Status.RUNNING;
         int i = this.minDuration + p_220378_1_.getRandom().nextInt(this.maxDuration + 1 - this.minDuration);
         this.endTimestamp = p_220378_3_ + (long)i;
         this.start(p_220378_1_, p_220378_2_, p_220378_3_);
         return true;
      } else {
         return false;
      }
   }

   protected void start(ServerWorld p_212831_1_, E p_212831_2_, long p_212831_3_) {
   }

   public final void tickOrStop(ServerWorld p_220377_1_, E p_220377_2_, long p_220377_3_) {
      if (!this.timedOut(p_220377_3_) && this.canStillUse(p_220377_1_, p_220377_2_, p_220377_3_)) {
         this.tick(p_220377_1_, p_220377_2_, p_220377_3_);
      } else {
         this.doStop(p_220377_1_, p_220377_2_, p_220377_3_);
      }

   }

   protected void tick(ServerWorld p_212833_1_, E p_212833_2_, long p_212833_3_) {
   }

   public final void doStop(ServerWorld p_220380_1_, E p_220380_2_, long p_220380_3_) {
      this.status = Task.Status.STOPPED;
      this.stop(p_220380_1_, p_220380_2_, p_220380_3_);
   }

   protected void stop(ServerWorld p_212835_1_, E p_212835_2_, long p_212835_3_) {
   }

   protected boolean canStillUse(ServerWorld p_212834_1_, E p_212834_2_, long p_212834_3_) {
      return false;
   }

   protected boolean timedOut(long p_220383_1_) {
      return p_220383_1_ > this.endTimestamp;
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, E p_212832_2_) {
      return true;
   }

   public String toString() {
      return this.getClass().getSimpleName();
   }

   private boolean hasRequiredMemories(E p_220382_1_) {
      for(Entry<MemoryModuleType<?>, MemoryModuleStatus> entry : this.entryCondition.entrySet()) {
         MemoryModuleType<?> memorymoduletype = entry.getKey();
         MemoryModuleStatus memorymodulestatus = entry.getValue();
         if (!p_220382_1_.getBrain().checkMemory(memorymoduletype, memorymodulestatus)) {
            return false;
         }
      }

      return true;
   }

   public static enum Status {
      STOPPED,
      RUNNING;
   }
}
