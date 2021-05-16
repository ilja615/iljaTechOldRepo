package net.minecraft.entity;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.EntityPredicates;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public interface IAngerable {
   int getRemainingPersistentAngerTime();

   void setRemainingPersistentAngerTime(int p_230260_1_);

   @Nullable
   UUID getPersistentAngerTarget();

   void setPersistentAngerTarget(@Nullable UUID p_230259_1_);

   void startPersistentAngerTimer();

   default void addPersistentAngerSaveData(CompoundNBT p_233682_1_) {
      p_233682_1_.putInt("AngerTime", this.getRemainingPersistentAngerTime());
      if (this.getPersistentAngerTarget() != null) {
         p_233682_1_.putUUID("AngryAt", this.getPersistentAngerTarget());
      }

   }

   default void readPersistentAngerSaveData(ServerWorld p_241358_1_, CompoundNBT p_241358_2_) {
      this.setRemainingPersistentAngerTime(p_241358_2_.getInt("AngerTime"));
      if (!p_241358_2_.hasUUID("AngryAt")) {
         this.setPersistentAngerTarget((UUID)null);
      } else {
         UUID uuid = p_241358_2_.getUUID("AngryAt");
         this.setPersistentAngerTarget(uuid);
         Entity entity = p_241358_1_.getEntity(uuid);
         if (entity != null) {
            if (entity instanceof MobEntity) {
               this.setLastHurtByMob((MobEntity)entity);
            }

            if (entity.getType() == EntityType.PLAYER) {
               this.setLastHurtByPlayer((PlayerEntity)entity);
            }

         }
      }
   }

   default void updatePersistentAnger(ServerWorld p_241359_1_, boolean p_241359_2_) {
      LivingEntity livingentity = this.getTarget();
      UUID uuid = this.getPersistentAngerTarget();
      if ((livingentity == null || livingentity.isDeadOrDying()) && uuid != null && p_241359_1_.getEntity(uuid) instanceof MobEntity) {
         this.stopBeingAngry();
      } else {
         if (livingentity != null && !Objects.equals(uuid, livingentity.getUUID())) {
            this.setPersistentAngerTarget(livingentity.getUUID());
            this.startPersistentAngerTimer();
         }

         if (this.getRemainingPersistentAngerTime() > 0 && (livingentity == null || livingentity.getType() != EntityType.PLAYER || !p_241359_2_)) {
            this.setRemainingPersistentAngerTime(this.getRemainingPersistentAngerTime() - 1);
            if (this.getRemainingPersistentAngerTime() == 0) {
               this.stopBeingAngry();
            }
         }

      }
   }

   default boolean isAngryAt(LivingEntity p_233680_1_) {
      if (!EntityPredicates.ATTACK_ALLOWED.test(p_233680_1_)) {
         return false;
      } else {
         return p_233680_1_.getType() == EntityType.PLAYER && this.isAngryAtAllPlayers(p_233680_1_.level) ? true : p_233680_1_.getUUID().equals(this.getPersistentAngerTarget());
      }
   }

   default boolean isAngryAtAllPlayers(World p_241357_1_) {
      return p_241357_1_.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER) && this.isAngry() && this.getPersistentAngerTarget() == null;
   }

   default boolean isAngry() {
      return this.getRemainingPersistentAngerTime() > 0;
   }

   default void playerDied(PlayerEntity p_233681_1_) {
      if (p_233681_1_.level.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
         if (p_233681_1_.getUUID().equals(this.getPersistentAngerTarget())) {
            this.stopBeingAngry();
         }
      }
   }

   default void forgetCurrentTargetAndRefreshUniversalAnger() {
      this.stopBeingAngry();
      this.startPersistentAngerTimer();
   }

   default void stopBeingAngry() {
      this.setLastHurtByMob((LivingEntity)null);
      this.setPersistentAngerTarget((UUID)null);
      this.setTarget((LivingEntity)null);
      this.setRemainingPersistentAngerTime(0);
   }

   void setLastHurtByMob(@Nullable LivingEntity p_70604_1_);

   void setLastHurtByPlayer(@Nullable PlayerEntity p_230246_1_);

   void setTarget(@Nullable LivingEntity p_70624_1_);

   @Nullable
   LivingEntity getTarget();
}
