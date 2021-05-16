package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.server.ServerWorld;

public class AnimalBreedTask extends Task<AnimalEntity> {
   private final EntityType<? extends AnimalEntity> partnerType;
   private final float speedModifier;
   private long spawnChildAtTime;

   public AnimalBreedTask(EntityType<? extends AnimalEntity> p_i231506_1_, float p_i231506_2_) {
      super(ImmutableMap.of(MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.BREED_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED), 325);
      this.partnerType = p_i231506_1_;
      this.speedModifier = p_i231506_2_;
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, AnimalEntity p_212832_2_) {
      return p_212832_2_.isInLove() && this.findValidBreedPartner(p_212832_2_).isPresent();
   }

   protected void start(ServerWorld p_212831_1_, AnimalEntity p_212831_2_, long p_212831_3_) {
      AnimalEntity animalentity = this.findValidBreedPartner(p_212831_2_).get();
      p_212831_2_.getBrain().setMemory(MemoryModuleType.BREED_TARGET, animalentity);
      animalentity.getBrain().setMemory(MemoryModuleType.BREED_TARGET, p_212831_2_);
      BrainUtil.lockGazeAndWalkToEachOther(p_212831_2_, animalentity, this.speedModifier);
      int i = 275 + p_212831_2_.getRandom().nextInt(50);
      this.spawnChildAtTime = p_212831_3_ + (long)i;
   }

   protected boolean canStillUse(ServerWorld p_212834_1_, AnimalEntity p_212834_2_, long p_212834_3_) {
      if (!this.hasBreedTargetOfRightType(p_212834_2_)) {
         return false;
      } else {
         AnimalEntity animalentity = this.getBreedTarget(p_212834_2_);
         return animalentity.isAlive() && p_212834_2_.canMate(animalentity) && BrainUtil.entityIsVisible(p_212834_2_.getBrain(), animalentity) && p_212834_3_ <= this.spawnChildAtTime;
      }
   }

   protected void tick(ServerWorld p_212833_1_, AnimalEntity p_212833_2_, long p_212833_3_) {
      AnimalEntity animalentity = this.getBreedTarget(p_212833_2_);
      BrainUtil.lockGazeAndWalkToEachOther(p_212833_2_, animalentity, this.speedModifier);
      if (p_212833_2_.closerThan(animalentity, 3.0D)) {
         if (p_212833_3_ >= this.spawnChildAtTime) {
            p_212833_2_.spawnChildFromBreeding(p_212833_1_, animalentity);
            p_212833_2_.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
            animalentity.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
         }

      }
   }

   protected void stop(ServerWorld p_212835_1_, AnimalEntity p_212835_2_, long p_212835_3_) {
      p_212835_2_.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
      p_212835_2_.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      p_212835_2_.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
      this.spawnChildAtTime = 0L;
   }

   private AnimalEntity getBreedTarget(AnimalEntity p_233846_1_) {
      return (AnimalEntity)p_233846_1_.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();
   }

   private boolean hasBreedTargetOfRightType(AnimalEntity p_233848_1_) {
      Brain<?> brain = p_233848_1_.getBrain();
      return brain.hasMemoryValue(MemoryModuleType.BREED_TARGET) && brain.getMemory(MemoryModuleType.BREED_TARGET).get().getType() == this.partnerType;
   }

   private Optional<? extends AnimalEntity> findValidBreedPartner(AnimalEntity p_233849_1_) {
      return p_233849_1_.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).get().stream().filter((p_233847_1_) -> {
         return p_233847_1_.getType() == this.partnerType;
      }).map((p_233845_0_) -> {
         return (AnimalEntity)p_233845_0_;
      }).filter(p_233849_1_::canMate).findFirst();
   }
}
