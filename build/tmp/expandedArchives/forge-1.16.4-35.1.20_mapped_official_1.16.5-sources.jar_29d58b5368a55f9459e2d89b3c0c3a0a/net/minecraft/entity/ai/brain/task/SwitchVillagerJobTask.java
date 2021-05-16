package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class SwitchVillagerJobTask extends Task<VillagerEntity> {
   final VillagerProfession profession;

   public SwitchVillagerJobTask(VillagerProfession p_i231525_1_) {
      super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.LIVING_ENTITIES, MemoryModuleStatus.VALUE_PRESENT));
      this.profession = p_i231525_1_;
   }

   protected void start(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      GlobalPos globalpos = p_212831_2_.getBrain().getMemory(MemoryModuleType.JOB_SITE).get();
      p_212831_1_.getPoiManager().getType(globalpos.pos()).ifPresent((p_233933_3_) -> {
         BrainUtil.getNearbyVillagersWithCondition(p_212831_2_, (p_233935_3_) -> {
            return this.competesForSameJobsite(globalpos, p_233933_3_, p_233935_3_);
         }).reduce(p_212831_2_, SwitchVillagerJobTask::selectWinner);
      });
   }

   private static VillagerEntity selectWinner(VillagerEntity p_233932_0_, VillagerEntity p_233932_1_) {
      VillagerEntity villagerentity;
      VillagerEntity villagerentity1;
      if (p_233932_0_.getVillagerXp() > p_233932_1_.getVillagerXp()) {
         villagerentity = p_233932_0_;
         villagerentity1 = p_233932_1_;
      } else {
         villagerentity = p_233932_1_;
         villagerentity1 = p_233932_0_;
      }

      villagerentity1.getBrain().eraseMemory(MemoryModuleType.JOB_SITE);
      return villagerentity;
   }

   private boolean competesForSameJobsite(GlobalPos p_233934_1_, PointOfInterestType p_233934_2_, VillagerEntity p_233934_3_) {
      return this.hasJobSite(p_233934_3_) && p_233934_1_.equals(p_233934_3_.getBrain().getMemory(MemoryModuleType.JOB_SITE).get()) && this.hasMatchingProfession(p_233934_2_, p_233934_3_.getVillagerData().getProfession());
   }

   private boolean hasMatchingProfession(PointOfInterestType p_233930_1_, VillagerProfession p_233930_2_) {
      return p_233930_2_.getJobPoiType().getPredicate().test(p_233930_1_);
   }

   private boolean hasJobSite(VillagerEntity p_233931_1_) {
      return p_233931_1_.getBrain().getMemory(MemoryModuleType.JOB_SITE).isPresent();
   }
}
