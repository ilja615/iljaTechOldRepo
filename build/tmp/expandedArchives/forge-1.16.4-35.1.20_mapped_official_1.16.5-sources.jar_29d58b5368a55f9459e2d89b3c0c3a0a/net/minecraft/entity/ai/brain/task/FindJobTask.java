package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class FindJobTask extends Task<VillagerEntity> {
   private final float speedModifier;

   public FindJobTask(float p_i231545_1_) {
      super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.JOB_SITE, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.LIVING_ENTITIES, MemoryModuleStatus.VALUE_PRESENT));
      this.speedModifier = p_i231545_1_;
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, VillagerEntity p_212832_2_) {
      if (p_212832_2_.isBaby()) {
         return false;
      } else {
         return p_212832_2_.getVillagerData().getProfession() == VillagerProfession.NONE;
      }
   }

   protected void start(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      BlockPos blockpos = p_212831_2_.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().pos();
      Optional<PointOfInterestType> optional = p_212831_1_.getPoiManager().getType(blockpos);
      if (optional.isPresent()) {
         BrainUtil.getNearbyVillagersWithCondition(p_212831_2_, (p_234021_3_) -> {
            return this.nearbyWantsJobsite(optional.get(), p_234021_3_, blockpos);
         }).findFirst().ifPresent((p_234023_4_) -> {
            this.yieldJobSite(p_212831_1_, p_212831_2_, p_234023_4_, blockpos, p_234023_4_.getBrain().getMemory(MemoryModuleType.JOB_SITE).isPresent());
         });
      }
   }

   private boolean nearbyWantsJobsite(PointOfInterestType p_234018_1_, VillagerEntity p_234018_2_, BlockPos p_234018_3_) {
      boolean flag = p_234018_2_.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).isPresent();
      if (flag) {
         return false;
      } else {
         Optional<GlobalPos> optional = p_234018_2_.getBrain().getMemory(MemoryModuleType.JOB_SITE);
         VillagerProfession villagerprofession = p_234018_2_.getVillagerData().getProfession();
         if (p_234018_2_.getVillagerData().getProfession() != VillagerProfession.NONE && villagerprofession.getJobPoiType().getPredicate().test(p_234018_1_)) {
            return !optional.isPresent() ? this.canReachPos(p_234018_2_, p_234018_3_, p_234018_1_) : optional.get().pos().equals(p_234018_3_);
         } else {
            return false;
         }
      }
   }

   private void yieldJobSite(ServerWorld p_234022_1_, VillagerEntity p_234022_2_, VillagerEntity p_234022_3_, BlockPos p_234022_4_, boolean p_234022_5_) {
      this.eraseMemories(p_234022_2_);
      if (!p_234022_5_) {
         BrainUtil.setWalkAndLookTargetMemories(p_234022_3_, p_234022_4_, this.speedModifier, 1);
         p_234022_3_.getBrain().setMemory(MemoryModuleType.POTENTIAL_JOB_SITE, GlobalPos.of(p_234022_1_.dimension(), p_234022_4_));
         DebugPacketSender.sendPoiTicketCountPacket(p_234022_1_, p_234022_4_);
      }

   }

   private boolean canReachPos(VillagerEntity p_234020_1_, BlockPos p_234020_2_, PointOfInterestType p_234020_3_) {
      Path path = p_234020_1_.getNavigation().createPath(p_234020_2_, p_234020_3_.getValidRange());
      return path != null && path.canReach();
   }

   private void eraseMemories(VillagerEntity p_234019_1_) {
      p_234019_1_.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      p_234019_1_.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
      p_234019_1_.getBrain().eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
   }
}
