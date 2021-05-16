package net.minecraft.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.world.server.ServerWorld;

public class ForgetAdmiredItemTask<E extends PiglinEntity> extends Task<E> {
   private final int maxDistanceToItem;

   public ForgetAdmiredItemTask(int p_i231574_1_) {
      super(ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleStatus.REGISTERED));
      this.maxDistanceToItem = p_i231574_1_;
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, E p_212832_2_) {
      if (!p_212832_2_.getOffhandItem().isEmpty()) {
         return false;
      } else {
         Optional<ItemEntity> optional = p_212832_2_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
         if (!optional.isPresent()) {
            return true;
         } else {
            return !optional.get().closerThan(p_212832_2_, (double)this.maxDistanceToItem);
         }
      }
   }

   protected void start(ServerWorld p_212831_1_, E p_212831_2_, long p_212831_3_) {
      p_212831_2_.getBrain().eraseMemory(MemoryModuleType.ADMIRING_ITEM);
   }
}
