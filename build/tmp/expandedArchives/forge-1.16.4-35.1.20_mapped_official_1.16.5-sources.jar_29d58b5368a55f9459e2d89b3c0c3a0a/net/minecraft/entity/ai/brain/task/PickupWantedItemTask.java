package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.world.server.ServerWorld;

public class PickupWantedItemTask<E extends LivingEntity> extends Task<E> {
   private final Predicate<E> predicate;
   private final int maxDistToWalk;
   private final float speedModifier;

   public PickupWantedItemTask(float p_i231520_1_, boolean p_i231520_2_, int p_i231520_3_) {
      this((p_233910_0_) -> {
         return true;
      }, p_i231520_1_, p_i231520_2_, p_i231520_3_);
   }

   public PickupWantedItemTask(Predicate<E> p_i231521_1_, float p_i231521_2_, boolean p_i231521_3_, int p_i231521_4_) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.WALK_TARGET, p_i231521_3_ ? MemoryModuleStatus.REGISTERED : MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleStatus.VALUE_PRESENT));
      this.predicate = p_i231521_1_;
      this.maxDistToWalk = p_i231521_4_;
      this.speedModifier = p_i231521_2_;
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, E p_212832_2_) {
      return this.predicate.test(p_212832_2_) && this.getClosestLovedItem(p_212832_2_).closerThan(p_212832_2_, (double)this.maxDistToWalk);
   }

   protected void start(ServerWorld p_212831_1_, E p_212831_2_, long p_212831_3_) {
      BrainUtil.setWalkAndLookTargetMemories(p_212831_2_, this.getClosestLovedItem(p_212831_2_), this.speedModifier, 0);
   }

   private ItemEntity getClosestLovedItem(E p_233909_1_) {
      return p_233909_1_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM).get();
   }
}
