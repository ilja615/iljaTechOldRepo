package net.minecraft.entity.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ExperienceBottleEntity extends ProjectileItemEntity {
   public ExperienceBottleEntity(EntityType<? extends ExperienceBottleEntity> p_i50152_1_, World p_i50152_2_) {
      super(p_i50152_1_, p_i50152_2_);
   }

   public ExperienceBottleEntity(World p_i1786_1_, LivingEntity p_i1786_2_) {
      super(EntityType.EXPERIENCE_BOTTLE, p_i1786_2_, p_i1786_1_);
   }

   public ExperienceBottleEntity(World p_i1787_1_, double p_i1787_2_, double p_i1787_4_, double p_i1787_6_) {
      super(EntityType.EXPERIENCE_BOTTLE, p_i1787_2_, p_i1787_4_, p_i1787_6_, p_i1787_1_);
   }

   protected Item getDefaultItem() {
      return Items.EXPERIENCE_BOTTLE;
   }

   protected float getGravity() {
      return 0.07F;
   }

   protected void onHit(RayTraceResult p_70227_1_) {
      super.onHit(p_70227_1_);
      if (!this.level.isClientSide) {
         this.level.levelEvent(2002, this.blockPosition(), PotionUtils.getColor(Potions.WATER));
         int i = 3 + this.level.random.nextInt(5) + this.level.random.nextInt(5);

         while(i > 0) {
            int j = ExperienceOrbEntity.getExperienceValue(i);
            i -= j;
            this.level.addFreshEntity(new ExperienceOrbEntity(this.level, this.getX(), this.getY(), this.getZ(), j));
         }

         this.remove();
      }

   }
}
