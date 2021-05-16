package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.world.server.ServerWorld;

public class WantedItemsSensor extends Sensor<MobEntity> {
   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
   }

   protected void doTick(ServerWorld p_212872_1_, MobEntity p_212872_2_) {
      Brain<?> brain = p_212872_2_.getBrain();
      List<ItemEntity> list = p_212872_1_.getEntitiesOfClass(ItemEntity.class, p_212872_2_.getBoundingBox().inflate(8.0D, 4.0D, 8.0D), (p_234123_0_) -> {
         return true;
      });
      list.sort(Comparator.comparingDouble(p_212872_2_::distanceToSqr));
      Optional<ItemEntity> optional = list.stream().filter((p_234124_1_) -> {
         return p_212872_2_.wantsToPickUp(p_234124_1_.getItem());
      }).filter((p_234122_1_) -> {
         return p_234122_1_.closerThan(p_212872_2_, 9.0D);
      }).filter(p_212872_2_::canSee).findFirst();
      brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, optional);
   }
}
