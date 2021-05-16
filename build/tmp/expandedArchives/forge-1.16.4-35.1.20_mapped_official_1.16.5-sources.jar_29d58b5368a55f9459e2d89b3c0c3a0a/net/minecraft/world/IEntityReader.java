package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public interface IEntityReader {
   List<Entity> getEntities(@Nullable Entity p_175674_1_, AxisAlignedBB p_175674_2_, @Nullable Predicate<? super Entity> p_175674_3_);

   <T extends Entity> List<T> getEntitiesOfClass(Class<? extends T> p_175647_1_, AxisAlignedBB p_175647_2_, @Nullable Predicate<? super T> p_175647_3_);

   default <T extends Entity> List<T> getLoadedEntitiesOfClass(Class<? extends T> p_225316_1_, AxisAlignedBB p_225316_2_, @Nullable Predicate<? super T> p_225316_3_) {
      return this.getEntitiesOfClass(p_225316_1_, p_225316_2_, p_225316_3_);
   }

   List<? extends PlayerEntity> players();

   default List<Entity> getEntities(@Nullable Entity p_72839_1_, AxisAlignedBB p_72839_2_) {
      return this.getEntities(p_72839_1_, p_72839_2_, EntityPredicates.NO_SPECTATORS);
   }

   default boolean isUnobstructed(@Nullable Entity p_195585_1_, VoxelShape p_195585_2_) {
      if (p_195585_2_.isEmpty()) {
         return true;
      } else {
         for(Entity entity : this.getEntities(p_195585_1_, p_195585_2_.bounds())) {
            if (!entity.removed && entity.blocksBuilding && (p_195585_1_ == null || !entity.isPassengerOfSameVehicle(p_195585_1_)) && VoxelShapes.joinIsNotEmpty(p_195585_2_, VoxelShapes.create(entity.getBoundingBox()), IBooleanFunction.AND)) {
               return false;
            }
         }

         return true;
      }
   }

   default <T extends Entity> List<T> getEntitiesOfClass(Class<? extends T> p_217357_1_, AxisAlignedBB p_217357_2_) {
      return this.getEntitiesOfClass(p_217357_1_, p_217357_2_, EntityPredicates.NO_SPECTATORS);
   }

   default <T extends Entity> List<T> getLoadedEntitiesOfClass(Class<? extends T> p_225317_1_, AxisAlignedBB p_225317_2_) {
      return this.getLoadedEntitiesOfClass(p_225317_1_, p_225317_2_, EntityPredicates.NO_SPECTATORS);
   }

   default Stream<VoxelShape> getEntityCollisions(@Nullable Entity p_230318_1_, AxisAlignedBB p_230318_2_, Predicate<Entity> p_230318_3_) {
      if (p_230318_2_.getSize() < 1.0E-7D) {
         return Stream.empty();
      } else {
         AxisAlignedBB axisalignedbb = p_230318_2_.inflate(1.0E-7D);
         return this.getEntities(p_230318_1_, axisalignedbb, p_230318_3_.and((p_234892_2_) -> {
            if (p_234892_2_.getBoundingBox().intersects(axisalignedbb)) {
               if (p_230318_1_ == null) {
                  if (p_234892_2_.canBeCollidedWith()) {
                     return true;
                  }
               } else if (p_230318_1_.canCollideWith(p_234892_2_)) {
                  return true;
               }
            }

            return false;
         })).stream().map(Entity::getBoundingBox).map(VoxelShapes::create);
      }
   }

   @Nullable
   default PlayerEntity getNearestPlayer(double p_190525_1_, double p_190525_3_, double p_190525_5_, double p_190525_7_, @Nullable Predicate<Entity> p_190525_9_) {
      double d0 = -1.0D;
      PlayerEntity playerentity = null;

      for(PlayerEntity playerentity1 : this.players()) {
         if (p_190525_9_ == null || p_190525_9_.test(playerentity1)) {
            double d1 = playerentity1.distanceToSqr(p_190525_1_, p_190525_3_, p_190525_5_);
            if ((p_190525_7_ < 0.0D || d1 < p_190525_7_ * p_190525_7_) && (d0 == -1.0D || d1 < d0)) {
               d0 = d1;
               playerentity = playerentity1;
            }
         }
      }

      return playerentity;
   }

   @Nullable
   default PlayerEntity getNearestPlayer(Entity p_217362_1_, double p_217362_2_) {
      return this.getNearestPlayer(p_217362_1_.getX(), p_217362_1_.getY(), p_217362_1_.getZ(), p_217362_2_, false);
   }

   @Nullable
   default PlayerEntity getNearestPlayer(double p_217366_1_, double p_217366_3_, double p_217366_5_, double p_217366_7_, boolean p_217366_9_) {
      Predicate<Entity> predicate = p_217366_9_ ? EntityPredicates.NO_CREATIVE_OR_SPECTATOR : EntityPredicates.NO_SPECTATORS;
      return this.getNearestPlayer(p_217366_1_, p_217366_3_, p_217366_5_, p_217366_7_, predicate);
   }

   default boolean hasNearbyAlivePlayer(double p_217358_1_, double p_217358_3_, double p_217358_5_, double p_217358_7_) {
      for(PlayerEntity playerentity : this.players()) {
         if (EntityPredicates.NO_SPECTATORS.test(playerentity) && EntityPredicates.LIVING_ENTITY_STILL_ALIVE.test(playerentity)) {
            double d0 = playerentity.distanceToSqr(p_217358_1_, p_217358_3_, p_217358_5_);
            if (p_217358_7_ < 0.0D || d0 < p_217358_7_ * p_217358_7_) {
               return true;
            }
         }
      }

      return false;
   }

   @Nullable
   default PlayerEntity getNearestPlayer(EntityPredicate p_217370_1_, LivingEntity p_217370_2_) {
      return this.getNearestEntity(this.players(), p_217370_1_, p_217370_2_, p_217370_2_.getX(), p_217370_2_.getY(), p_217370_2_.getZ());
   }

   @Nullable
   default PlayerEntity getNearestPlayer(EntityPredicate p_217372_1_, LivingEntity p_217372_2_, double p_217372_3_, double p_217372_5_, double p_217372_7_) {
      return this.getNearestEntity(this.players(), p_217372_1_, p_217372_2_, p_217372_3_, p_217372_5_, p_217372_7_);
   }

   @Nullable
   default PlayerEntity getNearestPlayer(EntityPredicate p_217359_1_, double p_217359_2_, double p_217359_4_, double p_217359_6_) {
      return this.getNearestEntity(this.players(), p_217359_1_, (LivingEntity)null, p_217359_2_, p_217359_4_, p_217359_6_);
   }

   @Nullable
   default <T extends LivingEntity> T getNearestEntity(Class<? extends T> p_217360_1_, EntityPredicate p_217360_2_, @Nullable LivingEntity p_217360_3_, double p_217360_4_, double p_217360_6_, double p_217360_8_, AxisAlignedBB p_217360_10_) {
      return this.getNearestEntity(this.getEntitiesOfClass(p_217360_1_, p_217360_10_, (Predicate<? super T>)null), p_217360_2_, p_217360_3_, p_217360_4_, p_217360_6_, p_217360_8_);
   }

   @Nullable
   default <T extends LivingEntity> T getNearestLoadedEntity(Class<? extends T> p_225318_1_, EntityPredicate p_225318_2_, @Nullable LivingEntity p_225318_3_, double p_225318_4_, double p_225318_6_, double p_225318_8_, AxisAlignedBB p_225318_10_) {
      return this.getNearestEntity(this.getLoadedEntitiesOfClass(p_225318_1_, p_225318_10_, (Predicate<? super T>)null), p_225318_2_, p_225318_3_, p_225318_4_, p_225318_6_, p_225318_8_);
   }

   @Nullable
   default <T extends LivingEntity> T getNearestEntity(List<? extends T> p_217361_1_, EntityPredicate p_217361_2_, @Nullable LivingEntity p_217361_3_, double p_217361_4_, double p_217361_6_, double p_217361_8_) {
      double d0 = -1.0D;
      T t = null;

      for(T t1 : p_217361_1_) {
         if (p_217361_2_.test(p_217361_3_, t1)) {
            double d1 = t1.distanceToSqr(p_217361_4_, p_217361_6_, p_217361_8_);
            if (d0 == -1.0D || d1 < d0) {
               d0 = d1;
               t = t1;
            }
         }
      }

      return t;
   }

   default List<PlayerEntity> getNearbyPlayers(EntityPredicate p_217373_1_, LivingEntity p_217373_2_, AxisAlignedBB p_217373_3_) {
      List<PlayerEntity> list = Lists.newArrayList();

      for(PlayerEntity playerentity : this.players()) {
         if (p_217373_3_.contains(playerentity.getX(), playerentity.getY(), playerentity.getZ()) && p_217373_1_.test(p_217373_2_, playerentity)) {
            list.add(playerentity);
         }
      }

      return list;
   }

   default <T extends LivingEntity> List<T> getNearbyEntities(Class<? extends T> p_217374_1_, EntityPredicate p_217374_2_, LivingEntity p_217374_3_, AxisAlignedBB p_217374_4_) {
      List<T> list = this.getEntitiesOfClass(p_217374_1_, p_217374_4_, (Predicate<? super T>)null);
      List<T> list1 = Lists.newArrayList();

      for(T t : list) {
         if (p_217374_2_.test(p_217374_3_, t)) {
            list1.add(t);
         }
      }

      return list1;
   }

   @Nullable
   default PlayerEntity getPlayerByUUID(UUID p_217371_1_) {
      for(int i = 0; i < this.players().size(); ++i) {
         PlayerEntity playerentity = this.players().get(i);
         if (p_217371_1_.equals(playerentity.getUUID())) {
            return playerentity;
         }
      }

      return null;
   }
}
