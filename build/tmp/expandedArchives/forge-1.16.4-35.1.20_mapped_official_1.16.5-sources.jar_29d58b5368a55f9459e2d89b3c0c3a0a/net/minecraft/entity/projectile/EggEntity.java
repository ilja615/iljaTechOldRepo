package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EggEntity extends ProjectileItemEntity {
   public EggEntity(EntityType<? extends EggEntity> p_i50154_1_, World p_i50154_2_) {
      super(p_i50154_1_, p_i50154_2_);
   }

   public EggEntity(World p_i1780_1_, LivingEntity p_i1780_2_) {
      super(EntityType.EGG, p_i1780_2_, p_i1780_1_);
   }

   public EggEntity(World p_i1781_1_, double p_i1781_2_, double p_i1781_4_, double p_i1781_6_) {
      super(EntityType.EGG, p_i1781_2_, p_i1781_4_, p_i1781_6_, p_i1781_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 3) {
         double d0 = 0.08D;

         for(int i = 0; i < 8; ++i) {
            this.level.addParticle(new ItemParticleData(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D);
         }
      }

   }

   protected void onHitEntity(EntityRayTraceResult p_213868_1_) {
      super.onHitEntity(p_213868_1_);
      p_213868_1_.getEntity().hurt(DamageSource.thrown(this, this.getOwner()), 0.0F);
   }

   protected void onHit(RayTraceResult p_70227_1_) {
      super.onHit(p_70227_1_);
      if (!this.level.isClientSide) {
         if (this.random.nextInt(8) == 0) {
            int i = 1;
            if (this.random.nextInt(32) == 0) {
               i = 4;
            }

            for(int j = 0; j < i; ++j) {
               ChickenEntity chickenentity = EntityType.CHICKEN.create(this.level);
               chickenentity.setAge(-24000);
               chickenentity.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, 0.0F);
               this.level.addFreshEntity(chickenentity);
            }
         }

         this.level.broadcastEntityEvent(this, (byte)3);
         this.remove();
      }

   }

   protected Item getDefaultItem() {
      return Items.EGG;
   }
}
