package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.server.ServerWorld;

public class FindPotentialJobTask extends Task<VillagerEntity> {
   final float speedModifier;

   public FindPotentialJobTask(float p_i231519_1_) {
      super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleStatus.VALUE_PRESENT), 1200);
      this.speedModifier = p_i231519_1_;
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, VillagerEntity p_212832_2_) {
      return p_212832_2_.getBrain().getActiveNonCoreActivity().map((p_233904_0_) -> {
         return p_233904_0_ == Activity.IDLE || p_233904_0_ == Activity.WORK || p_233904_0_ == Activity.PLAY;
      }).orElse(true);
   }

   protected boolean canStillUse(ServerWorld p_212834_1_, VillagerEntity p_212834_2_, long p_212834_3_) {
      return p_212834_2_.getBrain().hasMemoryValue(MemoryModuleType.POTENTIAL_JOB_SITE);
   }

   protected void tick(ServerWorld p_212833_1_, VillagerEntity p_212833_2_, long p_212833_3_) {
      BrainUtil.setWalkAndLookTargetMemories(p_212833_2_, p_212833_2_.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().pos(), this.speedModifier, 1);
   }

   protected void stop(ServerWorld p_212835_1_, VillagerEntity p_212835_2_, long p_212835_3_) {
      Optional<GlobalPos> optional = p_212835_2_.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
      optional.ifPresent((p_233905_1_) -> {
         BlockPos blockpos = p_233905_1_.pos();
         ServerWorld serverworld = p_212835_1_.getServer().getLevel(p_233905_1_.dimension());
         if (serverworld != null) {
            PointOfInterestManager pointofinterestmanager = serverworld.getPoiManager();
            if (pointofinterestmanager.exists(blockpos, (p_241377_0_) -> {
               return true;
            })) {
               pointofinterestmanager.release(blockpos);
            }

            DebugPacketSender.sendPoiTicketCountPacket(p_212835_1_, blockpos);
         }
      });
      p_212835_2_.getBrain().eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
   }
}
