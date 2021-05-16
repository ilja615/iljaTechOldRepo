package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.RangedInteger;

public class RangedCrossbowAttackGoal<T extends MonsterEntity & IRangedAttackMob & ICrossbowUser> extends Goal {
   public static final RangedInteger PATHFINDING_DELAY_RANGE = new RangedInteger(20, 40);
   private final T mob;
   private RangedCrossbowAttackGoal.CrossbowState crossbowState = RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
   private final double speedModifier;
   private final float attackRadiusSqr;
   private int seeTime;
   private int attackDelay;
   private int updatePathDelay;

   public RangedCrossbowAttackGoal(T p_i50322_1_, double p_i50322_2_, float p_i50322_4_) {
      this.mob = p_i50322_1_;
      this.speedModifier = p_i50322_2_;
      this.attackRadiusSqr = p_i50322_4_ * p_i50322_4_;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   public boolean canUse() {
      return this.isValidTarget() && this.isHoldingCrossbow();
   }

   private boolean isHoldingCrossbow() {
      return this.mob.isHolding(Items.CROSSBOW);
   }

   public boolean canContinueToUse() {
      return this.isValidTarget() && (this.canUse() || !this.mob.getNavigation().isDone()) && this.isHoldingCrossbow();
   }

   private boolean isValidTarget() {
      return this.mob.getTarget() != null && this.mob.getTarget().isAlive();
   }

   public void stop() {
      super.stop();
      this.mob.setAggressive(false);
      this.mob.setTarget((LivingEntity)null);
      this.seeTime = 0;
      if (this.mob.isUsingItem()) {
         this.mob.stopUsingItem();
         this.mob.setChargingCrossbow(false);
         CrossbowItem.setCharged(this.mob.getUseItem(), false);
      }

   }

   public void tick() {
      LivingEntity livingentity = this.mob.getTarget();
      if (livingentity != null) {
         boolean flag = this.mob.getSensing().canSee(livingentity);
         boolean flag1 = this.seeTime > 0;
         if (flag != flag1) {
            this.seeTime = 0;
         }

         if (flag) {
            ++this.seeTime;
         } else {
            --this.seeTime;
         }

         double d0 = this.mob.distanceToSqr(livingentity);
         boolean flag2 = (d0 > (double)this.attackRadiusSqr || this.seeTime < 5) && this.attackDelay == 0;
         if (flag2) {
            --this.updatePathDelay;
            if (this.updatePathDelay <= 0) {
               this.mob.getNavigation().moveTo(livingentity, this.canRun() ? this.speedModifier : this.speedModifier * 0.5D);
               this.updatePathDelay = PATHFINDING_DELAY_RANGE.randomValue(this.mob.getRandom());
            }
         } else {
            this.updatePathDelay = 0;
            this.mob.getNavigation().stop();
         }

         this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
         if (this.crossbowState == RangedCrossbowAttackGoal.CrossbowState.UNCHARGED) {
            if (!flag2) {
               this.mob.startUsingItem(ProjectileHelper.getWeaponHoldingHand(this.mob, Items.CROSSBOW));
               this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.CHARGING;
               this.mob.setChargingCrossbow(true);
            }
         } else if (this.crossbowState == RangedCrossbowAttackGoal.CrossbowState.CHARGING) {
            if (!this.mob.isUsingItem()) {
               this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
            }

            int i = this.mob.getTicksUsingItem();
            ItemStack itemstack = this.mob.getUseItem();
            if (i >= CrossbowItem.getChargeDuration(itemstack)) {
               this.mob.releaseUsingItem();
               this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.CHARGED;
               this.attackDelay = 20 + this.mob.getRandom().nextInt(20);
               this.mob.setChargingCrossbow(false);
            }
         } else if (this.crossbowState == RangedCrossbowAttackGoal.CrossbowState.CHARGED) {
            --this.attackDelay;
            if (this.attackDelay == 0) {
               this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.READY_TO_ATTACK;
            }
         } else if (this.crossbowState == RangedCrossbowAttackGoal.CrossbowState.READY_TO_ATTACK && flag) {
            this.mob.performRangedAttack(livingentity, 1.0F);
            ItemStack itemstack1 = this.mob.getItemInHand(ProjectileHelper.getWeaponHoldingHand(this.mob, Items.CROSSBOW));
            CrossbowItem.setCharged(itemstack1, false);
            this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
         }

      }
   }

   private boolean canRun() {
      return this.crossbowState == RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
   }

   static enum CrossbowState {
      UNCHARGED,
      CHARGING,
      CHARGED,
      READY_TO_ATTACK;
   }
}
