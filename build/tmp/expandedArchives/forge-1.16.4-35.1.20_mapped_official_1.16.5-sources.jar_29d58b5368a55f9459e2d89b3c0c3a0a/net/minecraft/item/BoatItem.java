package net.minecraft.item;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class BoatItem extends Item {
   private static final Predicate<Entity> ENTITY_PREDICATE = EntityPredicates.NO_SPECTATORS.and(Entity::isPickable);
   private final BoatEntity.Type type;

   public BoatItem(BoatEntity.Type p_i48526_1_, Item.Properties p_i48526_2_) {
      super(p_i48526_2_);
      this.type = p_i48526_1_;
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
      RayTraceResult raytraceresult = getPlayerPOVHitResult(p_77659_1_, p_77659_2_, RayTraceContext.FluidMode.ANY);
      if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
         return ActionResult.pass(itemstack);
      } else {
         Vector3d vector3d = p_77659_2_.getViewVector(1.0F);
         double d0 = 5.0D;
         List<Entity> list = p_77659_1_.getEntities(p_77659_2_, p_77659_2_.getBoundingBox().expandTowards(vector3d.scale(5.0D)).inflate(1.0D), ENTITY_PREDICATE);
         if (!list.isEmpty()) {
            Vector3d vector3d1 = p_77659_2_.getEyePosition(1.0F);

            for(Entity entity : list) {
               AxisAlignedBB axisalignedbb = entity.getBoundingBox().inflate((double)entity.getPickRadius());
               if (axisalignedbb.contains(vector3d1)) {
                  return ActionResult.pass(itemstack);
               }
            }
         }

         if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
            BoatEntity boatentity = new BoatEntity(p_77659_1_, raytraceresult.getLocation().x, raytraceresult.getLocation().y, raytraceresult.getLocation().z);
            boatentity.setType(this.type);
            boatentity.yRot = p_77659_2_.yRot;
            if (!p_77659_1_.noCollision(boatentity, boatentity.getBoundingBox().inflate(-0.1D))) {
               return ActionResult.fail(itemstack);
            } else {
               if (!p_77659_1_.isClientSide) {
                  p_77659_1_.addFreshEntity(boatentity);
                  if (!p_77659_2_.abilities.instabuild) {
                     itemstack.shrink(1);
                  }
               }

               p_77659_2_.awardStat(Stats.ITEM_USED.get(this));
               return ActionResult.sidedSuccess(itemstack, p_77659_1_.isClientSide());
            }
         } else {
            return ActionResult.pass(itemstack);
         }
      }
   }
}
