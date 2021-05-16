package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;

public class BeginRaidTask extends Task<LivingEntity> {
   public BeginRaidTask() {
      super(ImmutableMap.of());
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, LivingEntity p_212832_2_) {
      return p_212832_1_.random.nextInt(20) == 0;
   }

   protected void start(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      Brain<?> brain = p_212831_2_.getBrain();
      Raid raid = p_212831_1_.getRaidAt(p_212831_2_.blockPosition());
      if (raid != null) {
         if (raid.hasFirstWaveSpawned() && !raid.isBetweenWaves()) {
            brain.setDefaultActivity(Activity.RAID);
            brain.setActiveActivityIfPossible(Activity.RAID);
         } else {
            brain.setDefaultActivity(Activity.PRE_RAID);
            brain.setActiveActivityIfPossible(Activity.PRE_RAID);
         }
      }

   }
}
