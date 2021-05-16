package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class WalkToHouseTask extends Task<LivingEntity> {
   private final float speedModifier;
   private final Long2LongMap batchCache = new Long2LongOpenHashMap();
   private int triedCount;
   private long lastUpdate;

   public WalkToHouseTask(float p_i50353_1_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.HOME, MemoryModuleStatus.VALUE_ABSENT));
      this.speedModifier = p_i50353_1_;
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, LivingEntity p_212832_2_) {
      if (p_212832_1_.getGameTime() - this.lastUpdate < 20L) {
         return false;
      } else {
         CreatureEntity creatureentity = (CreatureEntity)p_212832_2_;
         PointOfInterestManager pointofinterestmanager = p_212832_1_.getPoiManager();
         Optional<BlockPos> optional = pointofinterestmanager.findClosest(PointOfInterestType.HOME.getPredicate(), p_212832_2_.blockPosition(), 48, PointOfInterestManager.Status.ANY);
         return optional.isPresent() && !(optional.get().distSqr(creatureentity.blockPosition()) <= 4.0D);
      }
   }

   protected void start(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      this.triedCount = 0;
      this.lastUpdate = p_212831_1_.getGameTime() + (long)p_212831_1_.getRandom().nextInt(20);
      CreatureEntity creatureentity = (CreatureEntity)p_212831_2_;
      PointOfInterestManager pointofinterestmanager = p_212831_1_.getPoiManager();
      Predicate<BlockPos> predicate = (p_225453_1_) -> {
         long i = p_225453_1_.asLong();
         if (this.batchCache.containsKey(i)) {
            return false;
         } else if (++this.triedCount >= 5) {
            return false;
         } else {
            this.batchCache.put(i, this.lastUpdate + 40L);
            return true;
         }
      };
      Stream<BlockPos> stream = pointofinterestmanager.findAll(PointOfInterestType.HOME.getPredicate(), predicate, p_212831_2_.blockPosition(), 48, PointOfInterestManager.Status.ANY);
      Path path = creatureentity.getNavigation().createPath(stream, PointOfInterestType.HOME.getValidRange());
      if (path != null && path.canReach()) {
         BlockPos blockpos = path.getTarget();
         Optional<PointOfInterestType> optional = pointofinterestmanager.getType(blockpos);
         if (optional.isPresent()) {
            p_212831_2_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(blockpos, this.speedModifier, 1));
            DebugPacketSender.sendPoiTicketCountPacket(p_212831_1_, blockpos);
         }
      } else if (this.triedCount < 5) {
         this.batchCache.long2LongEntrySet().removeIf((p_225454_1_) -> {
            return p_225454_1_.getLongValue() < this.lastUpdate;
         });
      }

   }
}
