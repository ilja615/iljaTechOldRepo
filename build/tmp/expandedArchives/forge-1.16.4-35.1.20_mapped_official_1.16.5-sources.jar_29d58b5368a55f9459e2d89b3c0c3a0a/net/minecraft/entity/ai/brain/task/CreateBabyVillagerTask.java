package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class CreateBabyVillagerTask extends Task<VillagerEntity> {
   private long birthTimestamp;

   public CreateBabyVillagerTask() {
      super(ImmutableMap.of(MemoryModuleType.BREED_TARGET, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleStatus.VALUE_PRESENT), 350, 350);
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, VillagerEntity p_212832_2_) {
      return this.isBreedingPossible(p_212832_2_);
   }

   protected boolean canStillUse(ServerWorld p_212834_1_, VillagerEntity p_212834_2_, long p_212834_3_) {
      return p_212834_3_ <= this.birthTimestamp && this.isBreedingPossible(p_212834_2_);
   }

   protected void start(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      AgeableEntity ageableentity = p_212831_2_.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();
      BrainUtil.lockGazeAndWalkToEachOther(p_212831_2_, ageableentity, 0.5F);
      p_212831_1_.broadcastEntityEvent(ageableentity, (byte)18);
      p_212831_1_.broadcastEntityEvent(p_212831_2_, (byte)18);
      int i = 275 + p_212831_2_.getRandom().nextInt(50);
      this.birthTimestamp = p_212831_3_ + (long)i;
   }

   protected void tick(ServerWorld p_212833_1_, VillagerEntity p_212833_2_, long p_212833_3_) {
      VillagerEntity villagerentity = (VillagerEntity)p_212833_2_.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();
      if (!(p_212833_2_.distanceToSqr(villagerentity) > 5.0D)) {
         BrainUtil.lockGazeAndWalkToEachOther(p_212833_2_, villagerentity, 0.5F);
         if (p_212833_3_ >= this.birthTimestamp) {
            p_212833_2_.eatAndDigestFood();
            villagerentity.eatAndDigestFood();
            this.tryToGiveBirth(p_212833_1_, p_212833_2_, villagerentity);
         } else if (p_212833_2_.getRandom().nextInt(35) == 0) {
            p_212833_1_.broadcastEntityEvent(villagerentity, (byte)12);
            p_212833_1_.broadcastEntityEvent(p_212833_2_, (byte)12);
         }

      }
   }

   private void tryToGiveBirth(ServerWorld p_223521_1_, VillagerEntity p_223521_2_, VillagerEntity p_223521_3_) {
      Optional<BlockPos> optional = this.takeVacantBed(p_223521_1_, p_223521_2_);
      if (!optional.isPresent()) {
         p_223521_1_.broadcastEntityEvent(p_223521_3_, (byte)13);
         p_223521_1_.broadcastEntityEvent(p_223521_2_, (byte)13);
      } else {
         Optional<VillagerEntity> optional1 = this.breed(p_223521_1_, p_223521_2_, p_223521_3_);
         if (optional1.isPresent()) {
            this.giveBedToChild(p_223521_1_, optional1.get(), optional.get());
         } else {
            p_223521_1_.getPoiManager().release(optional.get());
            DebugPacketSender.sendPoiTicketCountPacket(p_223521_1_, optional.get());
         }
      }

   }

   protected void stop(ServerWorld p_212835_1_, VillagerEntity p_212835_2_, long p_212835_3_) {
      p_212835_2_.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
   }

   private boolean isBreedingPossible(VillagerEntity p_220478_1_) {
      Brain<VillagerEntity> brain = p_220478_1_.getBrain();
      Optional<AgeableEntity> optional = brain.getMemory(MemoryModuleType.BREED_TARGET).filter((p_233999_0_) -> {
         return p_233999_0_.getType() == EntityType.VILLAGER;
      });
      if (!optional.isPresent()) {
         return false;
      } else {
         return BrainUtil.targetIsValid(brain, MemoryModuleType.BREED_TARGET, EntityType.VILLAGER) && p_220478_1_.canBreed() && optional.get().canBreed();
      }
   }

   private Optional<BlockPos> takeVacantBed(ServerWorld p_220479_1_, VillagerEntity p_220479_2_) {
      return p_220479_1_.getPoiManager().take(PointOfInterestType.HOME.getPredicate(), (p_220481_2_) -> {
         return this.canReach(p_220479_2_, p_220481_2_);
      }, p_220479_2_.blockPosition(), 48);
   }

   private boolean canReach(VillagerEntity p_223520_1_, BlockPos p_223520_2_) {
      Path path = p_223520_1_.getNavigation().createPath(p_223520_2_, PointOfInterestType.HOME.getValidRange());
      return path != null && path.canReach();
   }

   private Optional<VillagerEntity> breed(ServerWorld p_242307_1_, VillagerEntity p_242307_2_, VillagerEntity p_242307_3_) {
      VillagerEntity villagerentity = p_242307_2_.getBreedOffspring(p_242307_1_, p_242307_3_);
      if (villagerentity == null) {
         return Optional.empty();
      } else {
         p_242307_2_.setAge(6000);
         p_242307_3_.setAge(6000);
         villagerentity.setAge(-24000);
         villagerentity.moveTo(p_242307_2_.getX(), p_242307_2_.getY(), p_242307_2_.getZ(), 0.0F, 0.0F);
         p_242307_1_.addFreshEntityWithPassengers(villagerentity);
         p_242307_1_.broadcastEntityEvent(villagerentity, (byte)12);
         return Optional.of(villagerentity);
      }
   }

   private void giveBedToChild(ServerWorld p_220477_1_, VillagerEntity p_220477_2_, BlockPos p_220477_3_) {
      GlobalPos globalpos = GlobalPos.of(p_220477_1_.dimension(), p_220477_3_);
      p_220477_2_.getBrain().setMemory(MemoryModuleType.HOME, globalpos);
   }
}
