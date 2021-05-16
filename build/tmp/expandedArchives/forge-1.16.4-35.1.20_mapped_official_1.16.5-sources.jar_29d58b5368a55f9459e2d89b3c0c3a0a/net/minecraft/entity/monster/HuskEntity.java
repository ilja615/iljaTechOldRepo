package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class HuskEntity extends ZombieEntity {
   public HuskEntity(EntityType<? extends HuskEntity> p_i50204_1_, World p_i50204_2_) {
      super(p_i50204_1_, p_i50204_2_);
   }

   public static boolean checkHuskSpawnRules(EntityType<HuskEntity> p_223334_0_, IServerWorld p_223334_1_, SpawnReason p_223334_2_, BlockPos p_223334_3_, Random p_223334_4_) {
      return checkMonsterSpawnRules(p_223334_0_, p_223334_1_, p_223334_2_, p_223334_3_, p_223334_4_) && (p_223334_2_ == SpawnReason.SPAWNER || p_223334_1_.canSeeSky(p_223334_3_));
   }

   protected boolean isSunSensitive() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.HUSK_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.HUSK_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.HUSK_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.HUSK_STEP;
   }

   public boolean doHurtTarget(Entity p_70652_1_) {
      boolean flag = super.doHurtTarget(p_70652_1_);
      if (flag && this.getMainHandItem().isEmpty() && p_70652_1_ instanceof LivingEntity) {
         float f = this.level.getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
         ((LivingEntity)p_70652_1_).addEffect(new EffectInstance(Effects.HUNGER, 140 * (int)f));
      }

      return flag;
   }

   protected boolean convertsInWater() {
      return true;
   }

   protected void doUnderWaterConversion() {
      this.convertToZombieType(EntityType.ZOMBIE);
      if (!this.isSilent()) {
         this.level.levelEvent((PlayerEntity)null, 1041, this.blockPosition(), 0);
      }

   }

   protected ItemStack getSkull() {
      return ItemStack.EMPTY;
   }
}
