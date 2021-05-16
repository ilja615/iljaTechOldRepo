package net.minecraft.entity.ai.brain;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShootableItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPosWrapper;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class BrainUtil {
   public static void lockGazeAndWalkToEachOther(LivingEntity p_220618_0_, LivingEntity p_220618_1_, float p_220618_2_) {
      lookAtEachOther(p_220618_0_, p_220618_1_);
      setWalkAndLookTargetMemoriesToEachOther(p_220618_0_, p_220618_1_, p_220618_2_);
   }

   public static boolean entityIsVisible(Brain<?> p_220619_0_, LivingEntity p_220619_1_) {
      return p_220619_0_.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).filter((p_220614_1_) -> {
         return p_220614_1_.contains(p_220619_1_);
      }).isPresent();
   }

   public static boolean targetIsValid(Brain<?> p_220623_0_, MemoryModuleType<? extends LivingEntity> p_220623_1_, EntityType<?> p_220623_2_) {
      return targetIsValid(p_220623_0_, p_220623_1_, (p_220622_1_) -> {
         return p_220622_1_.getType() == p_220623_2_;
      });
   }

   private static boolean targetIsValid(Brain<?> p_233870_0_, MemoryModuleType<? extends LivingEntity> p_233870_1_, Predicate<LivingEntity> p_233870_2_) {
      return p_233870_0_.getMemory(p_233870_1_).filter(p_233870_2_).filter(LivingEntity::isAlive).filter((p_220615_1_) -> {
         return entityIsVisible(p_233870_0_, p_220615_1_);
      }).isPresent();
   }

   private static void lookAtEachOther(LivingEntity p_220616_0_, LivingEntity p_220616_1_) {
      lookAtEntity(p_220616_0_, p_220616_1_);
      lookAtEntity(p_220616_1_, p_220616_0_);
   }

   public static void lookAtEntity(LivingEntity p_220625_0_, LivingEntity p_220625_1_) {
      p_220625_0_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(p_220625_1_, true));
   }

   private static void setWalkAndLookTargetMemoriesToEachOther(LivingEntity p_220626_0_, LivingEntity p_220626_1_, float p_220626_2_) {
      int i = 2;
      setWalkAndLookTargetMemories(p_220626_0_, p_220626_1_, p_220626_2_, 2);
      setWalkAndLookTargetMemories(p_220626_1_, p_220626_0_, p_220626_2_, 2);
   }

   public static void setWalkAndLookTargetMemories(LivingEntity p_233860_0_, Entity p_233860_1_, float p_233860_2_, int p_233860_3_) {
      WalkTarget walktarget = new WalkTarget(new EntityPosWrapper(p_233860_1_, false), p_233860_2_, p_233860_3_);
      p_233860_0_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(p_233860_1_, true));
      p_233860_0_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, walktarget);
   }

   public static void setWalkAndLookTargetMemories(LivingEntity p_233866_0_, BlockPos p_233866_1_, float p_233866_2_, int p_233866_3_) {
      WalkTarget walktarget = new WalkTarget(new BlockPosWrapper(p_233866_1_), p_233866_2_, p_233866_3_);
      p_233866_0_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosWrapper(p_233866_1_));
      p_233866_0_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, walktarget);
   }

   public static void throwItem(LivingEntity p_233865_0_, ItemStack p_233865_1_, Vector3d p_233865_2_) {
      double d0 = p_233865_0_.getEyeY() - (double)0.3F;
      ItemEntity itementity = new ItemEntity(p_233865_0_.level, p_233865_0_.getX(), d0, p_233865_0_.getZ(), p_233865_1_);
      float f = 0.3F;
      Vector3d vector3d = p_233865_2_.subtract(p_233865_0_.position());
      vector3d = vector3d.normalize().scale((double)0.3F);
      itementity.setDeltaMovement(vector3d);
      itementity.setDefaultPickUpDelay();
      p_233865_0_.level.addFreshEntity(itementity);
   }

   public static SectionPos findSectionClosestToVillage(ServerWorld p_220617_0_, SectionPos p_220617_1_, int p_220617_2_) {
      int i = p_220617_0_.sectionsToVillage(p_220617_1_);
      return SectionPos.cube(p_220617_1_, p_220617_2_).filter((p_220620_2_) -> {
         return p_220617_0_.sectionsToVillage(p_220620_2_) < i;
      }).min(Comparator.comparingInt(p_220617_0_::sectionsToVillage)).orElse(p_220617_1_);
   }

   public static boolean isWithinAttackRange(MobEntity p_233869_0_, LivingEntity p_233869_1_, int p_233869_2_) {
      Item item = p_233869_0_.getMainHandItem().getItem();
      if (item instanceof ShootableItem && p_233869_0_.canFireProjectileWeapon((ShootableItem)item)) {
         int i = ((ShootableItem)item).getDefaultProjectileRange() - p_233869_2_;
         return p_233869_0_.closerThan(p_233869_1_, (double)i);
      } else {
         return isWithinMeleeAttackRange(p_233869_0_, p_233869_1_);
      }
   }

   public static boolean isWithinMeleeAttackRange(LivingEntity p_233874_0_, LivingEntity p_233874_1_) {
      double d0 = p_233874_0_.distanceToSqr(p_233874_1_.getX(), p_233874_1_.getY(), p_233874_1_.getZ());
      double d1 = (double)(p_233874_0_.getBbWidth() * 2.0F * p_233874_0_.getBbWidth() * 2.0F + p_233874_1_.getBbWidth());
      return d0 <= d1;
   }

   public static boolean isOtherTargetMuchFurtherAwayThanCurrentAttackTarget(LivingEntity p_233861_0_, LivingEntity p_233861_1_, double p_233861_2_) {
      Optional<LivingEntity> optional = p_233861_0_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
      if (!optional.isPresent()) {
         return false;
      } else {
         double d0 = p_233861_0_.distanceToSqr(optional.get().position());
         double d1 = p_233861_0_.distanceToSqr(p_233861_1_.position());
         return d1 > d0 + p_233861_2_ * p_233861_2_;
      }
   }

   public static boolean canSee(LivingEntity p_233876_0_, LivingEntity p_233876_1_) {
      Brain<?> brain = p_233876_0_.getBrain();
      return !brain.hasMemoryValue(MemoryModuleType.VISIBLE_LIVING_ENTITIES) ? false : brain.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).get().contains(p_233876_1_);
   }

   public static LivingEntity getNearestTarget(LivingEntity p_233867_0_, Optional<LivingEntity> p_233867_1_, LivingEntity p_233867_2_) {
      return !p_233867_1_.isPresent() ? p_233867_2_ : getTargetNearestMe(p_233867_0_, p_233867_1_.get(), p_233867_2_);
   }

   public static LivingEntity getTargetNearestMe(LivingEntity p_233863_0_, LivingEntity p_233863_1_, LivingEntity p_233863_2_) {
      Vector3d vector3d = p_233863_1_.position();
      Vector3d vector3d1 = p_233863_2_.position();
      return p_233863_0_.distanceToSqr(vector3d) < p_233863_0_.distanceToSqr(vector3d1) ? p_233863_1_ : p_233863_2_;
   }

   public static Optional<LivingEntity> getLivingEntityFromUUIDMemory(LivingEntity p_233864_0_, MemoryModuleType<UUID> p_233864_1_) {
      Optional<UUID> optional = p_233864_0_.getBrain().getMemory(p_233864_1_);
      return optional.map((p_233868_1_) -> {
         return (LivingEntity)((ServerWorld)p_233864_0_.level).getEntity(p_233868_1_);
      });
   }

   public static Stream<VillagerEntity> getNearbyVillagersWithCondition(VillagerEntity p_233872_0_, Predicate<VillagerEntity> p_233872_1_) {
      return p_233872_0_.getBrain().getMemory(MemoryModuleType.LIVING_ENTITIES).map((p_233873_2_) -> {
         return p_233873_2_.stream().filter((p_233871_1_) -> {
            return p_233871_1_ instanceof VillagerEntity && p_233871_1_ != p_233872_0_;
         }).map((p_233859_0_) -> {
            return (VillagerEntity)p_233859_0_;
         }).filter(LivingEntity::isAlive).filter(p_233872_1_);
      }).orElseGet(Stream::empty);
   }
}
