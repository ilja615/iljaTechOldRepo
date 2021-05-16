package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class DefendVillageTargetGoal extends TargetGoal {
   private final IronGolemEntity golem;
   private LivingEntity potentialTarget;
   private final EntityPredicate attackTargeting = (new EntityPredicate()).range(64.0D);

   public DefendVillageTargetGoal(IronGolemEntity p_i1659_1_) {
      super(p_i1659_1_, false, true);
      this.golem = p_i1659_1_;
      this.setFlags(EnumSet.of(Goal.Flag.TARGET));
   }

   public boolean canUse() {
      AxisAlignedBB axisalignedbb = this.golem.getBoundingBox().inflate(10.0D, 8.0D, 10.0D);
      List<LivingEntity> list = this.golem.level.getNearbyEntities(VillagerEntity.class, this.attackTargeting, this.golem, axisalignedbb);
      List<PlayerEntity> list1 = this.golem.level.getNearbyPlayers(this.attackTargeting, this.golem, axisalignedbb);

      for(LivingEntity livingentity : list) {
         VillagerEntity villagerentity = (VillagerEntity)livingentity;

         for(PlayerEntity playerentity : list1) {
            int i = villagerentity.getPlayerReputation(playerentity);
            if (i <= -100) {
               this.potentialTarget = playerentity;
            }
         }
      }

      if (this.potentialTarget == null) {
         return false;
      } else {
         return !(this.potentialTarget instanceof PlayerEntity) || !this.potentialTarget.isSpectator() && !((PlayerEntity)this.potentialTarget).isCreative();
      }
   }

   public void start() {
      this.golem.setTarget(this.potentialTarget);
      super.start();
   }
}
