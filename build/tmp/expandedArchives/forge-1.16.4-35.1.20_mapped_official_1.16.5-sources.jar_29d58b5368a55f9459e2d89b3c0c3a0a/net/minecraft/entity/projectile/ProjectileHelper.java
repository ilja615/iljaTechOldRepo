package net.minecraft.entity.projectile;

import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class ProjectileHelper {
   public static RayTraceResult getHitResult(Entity p_234618_0_, Predicate<Entity> p_234618_1_) {
      Vector3d vector3d = p_234618_0_.getDeltaMovement();
      World world = p_234618_0_.level;
      Vector3d vector3d1 = p_234618_0_.position();
      Vector3d vector3d2 = vector3d1.add(vector3d);
      RayTraceResult raytraceresult = world.clip(new RayTraceContext(vector3d1, vector3d2, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, p_234618_0_));
      if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
         vector3d2 = raytraceresult.getLocation();
      }

      RayTraceResult raytraceresult1 = getEntityHitResult(world, p_234618_0_, vector3d1, vector3d2, p_234618_0_.getBoundingBox().expandTowards(p_234618_0_.getDeltaMovement()).inflate(1.0D), p_234618_1_);
      if (raytraceresult1 != null) {
         raytraceresult = raytraceresult1;
      }

      return raytraceresult;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static EntityRayTraceResult getEntityHitResult(Entity p_221273_0_, Vector3d p_221273_1_, Vector3d p_221273_2_, AxisAlignedBB p_221273_3_, Predicate<Entity> p_221273_4_, double p_221273_5_) {
      World world = p_221273_0_.level;
      double d0 = p_221273_5_;
      Entity entity = null;
      Vector3d vector3d = null;

      for(Entity entity1 : world.getEntities(p_221273_0_, p_221273_3_, p_221273_4_)) {
         AxisAlignedBB axisalignedbb = entity1.getBoundingBox().inflate((double)entity1.getPickRadius());
         Optional<Vector3d> optional = axisalignedbb.clip(p_221273_1_, p_221273_2_);
         if (axisalignedbb.contains(p_221273_1_)) {
            if (d0 >= 0.0D) {
               entity = entity1;
               vector3d = optional.orElse(p_221273_1_);
               d0 = 0.0D;
            }
         } else if (optional.isPresent()) {
            Vector3d vector3d1 = optional.get();
            double d1 = p_221273_1_.distanceToSqr(vector3d1);
            if (d1 < d0 || d0 == 0.0D) {
               if (entity1.getRootVehicle() == p_221273_0_.getRootVehicle() && !entity1.canRiderInteract()) {
                  if (d0 == 0.0D) {
                     entity = entity1;
                     vector3d = vector3d1;
                  }
               } else {
                  entity = entity1;
                  vector3d = vector3d1;
                  d0 = d1;
               }
            }
         }
      }

      return entity == null ? null : new EntityRayTraceResult(entity, vector3d);
   }

   @Nullable
   public static EntityRayTraceResult getEntityHitResult(World p_221269_0_, Entity p_221269_1_, Vector3d p_221269_2_, Vector3d p_221269_3_, AxisAlignedBB p_221269_4_, Predicate<Entity> p_221269_5_) {
      double d0 = Double.MAX_VALUE;
      Entity entity = null;

      for(Entity entity1 : p_221269_0_.getEntities(p_221269_1_, p_221269_4_, p_221269_5_)) {
         AxisAlignedBB axisalignedbb = entity1.getBoundingBox().inflate((double)0.3F);
         Optional<Vector3d> optional = axisalignedbb.clip(p_221269_2_, p_221269_3_);
         if (optional.isPresent()) {
            double d1 = p_221269_2_.distanceToSqr(optional.get());
            if (d1 < d0) {
               entity = entity1;
               d0 = d1;
            }
         }
      }

      return entity == null ? null : new EntityRayTraceResult(entity);
   }

   public static final void rotateTowardsMovement(Entity p_188803_0_, float p_188803_1_) {
      Vector3d vector3d = p_188803_0_.getDeltaMovement();
      if (vector3d.lengthSqr() != 0.0D) {
         float f = MathHelper.sqrt(Entity.getHorizontalDistanceSqr(vector3d));
         p_188803_0_.yRot = (float)(MathHelper.atan2(vector3d.z, vector3d.x) * (double)(180F / (float)Math.PI)) + 90.0F;

         for(p_188803_0_.xRot = (float)(MathHelper.atan2((double)f, vector3d.y) * (double)(180F / (float)Math.PI)) - 90.0F; p_188803_0_.xRot - p_188803_0_.xRotO < -180.0F; p_188803_0_.xRotO -= 360.0F) {
         }

         while(p_188803_0_.xRot - p_188803_0_.xRotO >= 180.0F) {
            p_188803_0_.xRotO += 360.0F;
         }

         while(p_188803_0_.yRot - p_188803_0_.yRotO < -180.0F) {
            p_188803_0_.yRotO -= 360.0F;
         }

         while(p_188803_0_.yRot - p_188803_0_.yRotO >= 180.0F) {
            p_188803_0_.yRotO += 360.0F;
         }

         p_188803_0_.xRot = MathHelper.lerp(p_188803_1_, p_188803_0_.xRotO, p_188803_0_.xRot);
         p_188803_0_.yRot = MathHelper.lerp(p_188803_1_, p_188803_0_.yRotO, p_188803_0_.yRot);
      }
   }

   public static Hand getWeaponHoldingHand(LivingEntity p_221274_0_, Item p_221274_1_) {
      return p_221274_0_.getMainHandItem().getItem() == p_221274_1_ ? Hand.MAIN_HAND : Hand.OFF_HAND;
   }

   public static AbstractArrowEntity getMobArrow(LivingEntity p_221272_0_, ItemStack p_221272_1_, float p_221272_2_) {
      ArrowItem arrowitem = (ArrowItem)(p_221272_1_.getItem() instanceof ArrowItem ? p_221272_1_.getItem() : Items.ARROW);
      AbstractArrowEntity abstractarrowentity = arrowitem.createArrow(p_221272_0_.level, p_221272_1_, p_221272_0_);
      abstractarrowentity.setEnchantmentEffectsFromEntity(p_221272_0_, p_221272_2_);
      if (p_221272_1_.getItem() == Items.TIPPED_ARROW && abstractarrowentity instanceof ArrowEntity) {
         ((ArrowEntity)abstractarrowentity).setEffectsFromItem(p_221272_1_);
      }

      return abstractarrowentity;
   }
}
