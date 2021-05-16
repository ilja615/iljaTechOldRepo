package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;

public class ShowVillagerFlowerGoal extends Goal {
   private static final EntityPredicate OFFER_TARGER_CONTEXT = (new EntityPredicate()).range(6.0D).allowSameTeam().allowInvulnerable();
   private final IronGolemEntity golem;
   private VillagerEntity villager;
   private int tick;

   public ShowVillagerFlowerGoal(IronGolemEntity p_i1643_1_) {
      this.golem = p_i1643_1_;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   public boolean canUse() {
      if (!this.golem.level.isDay()) {
         return false;
      } else if (this.golem.getRandom().nextInt(8000) != 0) {
         return false;
      } else {
         this.villager = this.golem.level.getNearestEntity(VillagerEntity.class, OFFER_TARGER_CONTEXT, this.golem, this.golem.getX(), this.golem.getY(), this.golem.getZ(), this.golem.getBoundingBox().inflate(6.0D, 2.0D, 6.0D));
         return this.villager != null;
      }
   }

   public boolean canContinueToUse() {
      return this.tick > 0;
   }

   public void start() {
      this.tick = 400;
      this.golem.offerFlower(true);
   }

   public void stop() {
      this.golem.offerFlower(false);
      this.villager = null;
   }

   public void tick() {
      this.golem.getLookControl().setLookAt(this.villager, 30.0F, 30.0F);
      --this.tick;
   }
}
