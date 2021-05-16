package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;

public class AssignProfessionTask extends Task<VillagerEntity> {
   public AssignProfessionTask() {
      super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleStatus.VALUE_PRESENT));
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, VillagerEntity p_212832_2_) {
      BlockPos blockpos = p_212832_2_.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().pos();
      return blockpos.closerThan(p_212832_2_.position(), 2.0D) || p_212832_2_.assignProfessionWhenSpawned();
   }

   protected void start(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      GlobalPos globalpos = p_212831_2_.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get();
      p_212831_2_.getBrain().eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
      p_212831_2_.getBrain().setMemory(MemoryModuleType.JOB_SITE, globalpos);
      p_212831_1_.broadcastEntityEvent(p_212831_2_, (byte)14);
      if (p_212831_2_.getVillagerData().getProfession() == VillagerProfession.NONE) {
         MinecraftServer minecraftserver = p_212831_1_.getServer();
         Optional.ofNullable(minecraftserver.getLevel(globalpos.dimension())).flatMap((p_241376_1_) -> {
            return p_241376_1_.getPoiManager().getType(globalpos.pos());
         }).flatMap((p_220390_0_) -> {
            return Registry.VILLAGER_PROFESSION.stream().filter((p_220389_1_) -> {
               return p_220389_1_.getJobPoiType() == p_220390_0_;
            }).findFirst();
         }).ifPresent((p_220388_2_) -> {
            p_212831_2_.setVillagerData(p_212831_2_.getVillagerData().setProfession(p_220388_2_));
            p_212831_2_.refreshBrain(p_212831_1_);
         });
      }
   }
}
