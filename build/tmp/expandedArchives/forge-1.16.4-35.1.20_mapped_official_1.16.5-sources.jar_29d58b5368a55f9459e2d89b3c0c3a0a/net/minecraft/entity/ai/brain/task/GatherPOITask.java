package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class GatherPOITask extends Task<CreatureEntity> {
   private final PointOfInterestType poiType;
   private final MemoryModuleType<GlobalPos> memoryToAcquire;
   private final boolean onlyIfAdult;
   private final Optional<Byte> onPoiAcquisitionEvent;
   private long nextScheduledStart;
   private final Long2ObjectMap<GatherPOITask.RetryMarker> batchCache = new Long2ObjectOpenHashMap<>();

   public GatherPOITask(PointOfInterestType p_i241906_1_, MemoryModuleType<GlobalPos> p_i241906_2_, MemoryModuleType<GlobalPos> p_i241906_3_, boolean p_i241906_4_, Optional<Byte> p_i241906_5_) {
      super(constructEntryConditionMap(p_i241906_2_, p_i241906_3_));
      this.poiType = p_i241906_1_;
      this.memoryToAcquire = p_i241906_3_;
      this.onlyIfAdult = p_i241906_4_;
      this.onPoiAcquisitionEvent = p_i241906_5_;
   }

   public GatherPOITask(PointOfInterestType p_i241907_1_, MemoryModuleType<GlobalPos> p_i241907_2_, boolean p_i241907_3_, Optional<Byte> p_i241907_4_) {
      this(p_i241907_1_, p_i241907_2_, p_i241907_2_, p_i241907_3_, p_i241907_4_);
   }

   private static ImmutableMap<MemoryModuleType<?>, MemoryModuleStatus> constructEntryConditionMap(MemoryModuleType<GlobalPos> p_233841_0_, MemoryModuleType<GlobalPos> p_233841_1_) {
      Builder<MemoryModuleType<?>, MemoryModuleStatus> builder = ImmutableMap.builder();
      builder.put(p_233841_0_, MemoryModuleStatus.VALUE_ABSENT);
      if (p_233841_1_ != p_233841_0_) {
         builder.put(p_233841_1_, MemoryModuleStatus.VALUE_ABSENT);
      }

      return builder.build();
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, CreatureEntity p_212832_2_) {
      if (this.onlyIfAdult && p_212832_2_.isBaby()) {
         return false;
      } else if (this.nextScheduledStart == 0L) {
         this.nextScheduledStart = p_212832_2_.level.getGameTime() + (long)p_212832_1_.random.nextInt(20);
         return false;
      } else {
         return p_212832_1_.getGameTime() >= this.nextScheduledStart;
      }
   }

   protected void start(ServerWorld p_212831_1_, CreatureEntity p_212831_2_, long p_212831_3_) {
      this.nextScheduledStart = p_212831_3_ + 20L + (long)p_212831_1_.getRandom().nextInt(20);
      PointOfInterestManager pointofinterestmanager = p_212831_1_.getPoiManager();
      this.batchCache.long2ObjectEntrySet().removeIf((p_241362_2_) -> {
         return !p_241362_2_.getValue().isStillValid(p_212831_3_);
      });
      Predicate<BlockPos> predicate = (p_220603_3_) -> {
         GatherPOITask.RetryMarker gatherpoitask$retrymarker = this.batchCache.get(p_220603_3_.asLong());
         if (gatherpoitask$retrymarker == null) {
            return true;
         } else if (!gatherpoitask$retrymarker.shouldRetry(p_212831_3_)) {
            return false;
         } else {
            gatherpoitask$retrymarker.markAttempt(p_212831_3_);
            return true;
         }
      };
      Set<BlockPos> set = pointofinterestmanager.findAllClosestFirst(this.poiType.getPredicate(), predicate, p_212831_2_.blockPosition(), 48, PointOfInterestManager.Status.HAS_SPACE).limit(5L).collect(Collectors.toSet());
      Path path = p_212831_2_.getNavigation().createPath(set, this.poiType.getValidRange());
      if (path != null && path.canReach()) {
         BlockPos blockpos1 = path.getTarget();
         pointofinterestmanager.getType(blockpos1).ifPresent((p_225441_5_) -> {
            pointofinterestmanager.take(this.poiType.getPredicate(), (p_225442_1_) -> {
               return p_225442_1_.equals(blockpos1);
            }, blockpos1, 1);
            p_212831_2_.getBrain().setMemory(this.memoryToAcquire, GlobalPos.of(p_212831_1_.dimension(), blockpos1));
            this.onPoiAcquisitionEvent.ifPresent((p_242291_2_) -> {
               p_212831_1_.broadcastEntityEvent(p_212831_2_, p_242291_2_);
            });
            this.batchCache.clear();
            DebugPacketSender.sendPoiTicketCountPacket(p_212831_1_, blockpos1);
         });
      } else {
         for(BlockPos blockpos : set) {
            this.batchCache.computeIfAbsent(blockpos.asLong(), (p_241363_3_) -> {
               return new GatherPOITask.RetryMarker(p_212831_2_.level.random, p_212831_3_);
            });
         }
      }

   }

   static class RetryMarker {
      private final Random random;
      private long previousAttemptTimestamp;
      private long nextScheduledAttemptTimestamp;
      private int currentDelay;

      RetryMarker(Random p_i241233_1_, long p_i241233_2_) {
         this.random = p_i241233_1_;
         this.markAttempt(p_i241233_2_);
      }

      public void markAttempt(long p_241370_1_) {
         this.previousAttemptTimestamp = p_241370_1_;
         int i = this.currentDelay + this.random.nextInt(40) + 40;
         this.currentDelay = Math.min(i, 400);
         this.nextScheduledAttemptTimestamp = p_241370_1_ + (long)this.currentDelay;
      }

      public boolean isStillValid(long p_241371_1_) {
         return p_241371_1_ - this.previousAttemptTimestamp < 400L;
      }

      public boolean shouldRetry(long p_241372_1_) {
         return p_241372_1_ >= this.nextScheduledAttemptTimestamp;
      }

      public String toString() {
         return "RetryMarker{, previousAttemptAt=" + this.previousAttemptTimestamp + ", nextScheduledAttemptAt=" + this.nextScheduledAttemptTimestamp + ", currentDelay=" + this.currentDelay + '}';
      }
   }
}
