package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class NearestBedSensor extends Sensor<MobEntity> {
   private final Long2LongMap batchCache = new Long2LongOpenHashMap();
   private int triedCount;
   private long lastUpdate;

   public NearestBedSensor() {
      super(20);
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_BED);
   }

   protected void doTick(ServerWorld p_212872_1_, MobEntity p_212872_2_) {
      if (p_212872_2_.isBaby()) {
         this.triedCount = 0;
         this.lastUpdate = p_212872_1_.getGameTime() + (long)p_212872_1_.getRandom().nextInt(20);
         PointOfInterestManager pointofinterestmanager = p_212872_1_.getPoiManager();
         Predicate<BlockPos> predicate = (p_225469_1_) -> {
            long i = p_225469_1_.asLong();
            if (this.batchCache.containsKey(i)) {
               return false;
            } else if (++this.triedCount >= 5) {
               return false;
            } else {
               this.batchCache.put(i, this.lastUpdate + 40L);
               return true;
            }
         };
         Stream<BlockPos> stream = pointofinterestmanager.findAll(PointOfInterestType.HOME.getPredicate(), predicate, p_212872_2_.blockPosition(), 48, PointOfInterestManager.Status.ANY);
         Path path = p_212872_2_.getNavigation().createPath(stream, PointOfInterestType.HOME.getValidRange());
         if (path != null && path.canReach()) {
            BlockPos blockpos = path.getTarget();
            Optional<PointOfInterestType> optional = pointofinterestmanager.getType(blockpos);
            if (optional.isPresent()) {
               p_212872_2_.getBrain().setMemory(MemoryModuleType.NEAREST_BED, blockpos);
            }
         } else if (this.triedCount < 5) {
            this.batchCache.long2LongEntrySet().removeIf((p_225470_1_) -> {
               return p_225470_1_.getLongValue() < this.lastUpdate;
            });
         }

      }
   }
}
