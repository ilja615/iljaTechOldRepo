package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.world.server.ServerWorld;

public class ShootTargetTask<E extends MobEntity & ICrossbowUser, T extends LivingEntity> extends Task<E> {
   private int attackDelay;
   private ShootTargetTask.Status crossbowState = ShootTargetTask.Status.UNCHARGED;

   public ShootTargetTask() {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_PRESENT), 1200);
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, E p_212832_2_) {
      LivingEntity livingentity = getAttackTarget(p_212832_2_);
      return p_212832_2_.isHolding(Items.CROSSBOW) && BrainUtil.canSee(p_212832_2_, livingentity) && BrainUtil.isWithinAttackRange(p_212832_2_, livingentity, 0);
   }

   protected boolean canStillUse(ServerWorld p_212834_1_, E p_212834_2_, long p_212834_3_) {
      return p_212834_2_.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && this.checkExtraStartConditions(p_212834_1_, p_212834_2_);
   }

   protected void tick(ServerWorld p_212833_1_, E p_212833_2_, long p_212833_3_) {
      LivingEntity livingentity = getAttackTarget(p_212833_2_);
      this.lookAtTarget(p_212833_2_, livingentity);
      this.crossbowAttack(p_212833_2_, livingentity);
   }

   protected void stop(ServerWorld p_212835_1_, E p_212835_2_, long p_212835_3_) {
      if (p_212835_2_.isUsingItem()) {
         p_212835_2_.stopUsingItem();
      }

      if (p_212835_2_.isHolding(Items.CROSSBOW)) {
         p_212835_2_.setChargingCrossbow(false);
         CrossbowItem.setCharged(p_212835_2_.getUseItem(), false);
      }

   }

   private void crossbowAttack(E p_233888_1_, LivingEntity p_233888_2_) {
      if (this.crossbowState == ShootTargetTask.Status.UNCHARGED) {
         p_233888_1_.startUsingItem(ProjectileHelper.getWeaponHoldingHand(p_233888_1_, Items.CROSSBOW));
         this.crossbowState = ShootTargetTask.Status.CHARGING;
         p_233888_1_.setChargingCrossbow(true);
      } else if (this.crossbowState == ShootTargetTask.Status.CHARGING) {
         if (!p_233888_1_.isUsingItem()) {
            this.crossbowState = ShootTargetTask.Status.UNCHARGED;
         }

         int i = p_233888_1_.getTicksUsingItem();
         ItemStack itemstack = p_233888_1_.getUseItem();
         if (i >= CrossbowItem.getChargeDuration(itemstack)) {
            p_233888_1_.releaseUsingItem();
            this.crossbowState = ShootTargetTask.Status.CHARGED;
            this.attackDelay = 20 + p_233888_1_.getRandom().nextInt(20);
            p_233888_1_.setChargingCrossbow(false);
         }
      } else if (this.crossbowState == ShootTargetTask.Status.CHARGED) {
         --this.attackDelay;
         if (this.attackDelay == 0) {
            this.crossbowState = ShootTargetTask.Status.READY_TO_ATTACK;
         }
      } else if (this.crossbowState == ShootTargetTask.Status.READY_TO_ATTACK) {
         p_233888_1_.performRangedAttack(p_233888_2_, 1.0F);
         ItemStack itemstack1 = p_233888_1_.getItemInHand(ProjectileHelper.getWeaponHoldingHand(p_233888_1_, Items.CROSSBOW));
         CrossbowItem.setCharged(itemstack1, false);
         this.crossbowState = ShootTargetTask.Status.UNCHARGED;
      }

   }

   private void lookAtTarget(MobEntity p_233889_1_, LivingEntity p_233889_2_) {
      p_233889_1_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(p_233889_2_, true));
   }

   private static LivingEntity getAttackTarget(LivingEntity p_233887_0_) {
      return p_233887_0_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
   }

   static enum Status {
      UNCHARGED,
      CHARGING,
      CHARGED,
      READY_TO_ATTACK;
   }
}
