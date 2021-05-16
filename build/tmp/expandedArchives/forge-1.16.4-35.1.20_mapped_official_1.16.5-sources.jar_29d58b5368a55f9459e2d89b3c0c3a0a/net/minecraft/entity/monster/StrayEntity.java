package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class StrayEntity extends AbstractSkeletonEntity {
   public StrayEntity(EntityType<? extends StrayEntity> p_i50191_1_, World p_i50191_2_) {
      super(p_i50191_1_, p_i50191_2_);
   }

   public static boolean checkStraySpawnRules(EntityType<StrayEntity> p_223327_0_, IServerWorld p_223327_1_, SpawnReason p_223327_2_, BlockPos p_223327_3_, Random p_223327_4_) {
      return checkMonsterSpawnRules(p_223327_0_, p_223327_1_, p_223327_2_, p_223327_3_, p_223327_4_) && (p_223327_2_ == SpawnReason.SPAWNER || p_223327_1_.canSeeSky(p_223327_3_));
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.STRAY_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.STRAY_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.STRAY_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.STRAY_STEP;
   }

   protected AbstractArrowEntity getArrow(ItemStack p_213624_1_, float p_213624_2_) {
      AbstractArrowEntity abstractarrowentity = super.getArrow(p_213624_1_, p_213624_2_);
      if (abstractarrowentity instanceof ArrowEntity) {
         ((ArrowEntity)abstractarrowentity).addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 600));
      }

      return abstractarrowentity;
   }
}
