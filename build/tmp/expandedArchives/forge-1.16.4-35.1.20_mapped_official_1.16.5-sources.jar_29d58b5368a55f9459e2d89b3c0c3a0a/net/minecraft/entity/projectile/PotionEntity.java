package net.minecraft.entity.projectile;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(
   value = Dist.CLIENT,
   _interface = IRendersAsItem.class
)
public class PotionEntity extends ProjectileItemEntity implements IRendersAsItem {
   public static final Predicate<LivingEntity> WATER_SENSITIVE = LivingEntity::isSensitiveToWater;

   public PotionEntity(EntityType<? extends PotionEntity> p_i50149_1_, World p_i50149_2_) {
      super(p_i50149_1_, p_i50149_2_);
   }

   public PotionEntity(World p_i50150_1_, LivingEntity p_i50150_2_) {
      super(EntityType.POTION, p_i50150_2_, p_i50150_1_);
   }

   public PotionEntity(World p_i50151_1_, double p_i50151_2_, double p_i50151_4_, double p_i50151_6_) {
      super(EntityType.POTION, p_i50151_2_, p_i50151_4_, p_i50151_6_, p_i50151_1_);
   }

   protected Item getDefaultItem() {
      return Items.SPLASH_POTION;
   }

   protected float getGravity() {
      return 0.05F;
   }

   protected void onHitBlock(BlockRayTraceResult p_230299_1_) {
      super.onHitBlock(p_230299_1_);
      if (!this.level.isClientSide) {
         ItemStack itemstack = this.getItem();
         Potion potion = PotionUtils.getPotion(itemstack);
         List<EffectInstance> list = PotionUtils.getMobEffects(itemstack);
         boolean flag = potion == Potions.WATER && list.isEmpty();
         Direction direction = p_230299_1_.getDirection();
         BlockPos blockpos = p_230299_1_.getBlockPos();
         BlockPos blockpos1 = blockpos.relative(direction);
         if (flag) {
            this.dowseFire(blockpos1, direction);
            this.dowseFire(blockpos1.relative(direction.getOpposite()), direction);

            for(Direction direction1 : Direction.Plane.HORIZONTAL) {
               this.dowseFire(blockpos1.relative(direction1), direction1);
            }
         }

      }
   }

   protected void onHit(RayTraceResult p_70227_1_) {
      super.onHit(p_70227_1_);
      if (!this.level.isClientSide) {
         ItemStack itemstack = this.getItem();
         Potion potion = PotionUtils.getPotion(itemstack);
         List<EffectInstance> list = PotionUtils.getMobEffects(itemstack);
         boolean flag = potion == Potions.WATER && list.isEmpty();
         if (flag) {
            this.applyWater();
         } else if (!list.isEmpty()) {
            if (this.isLingering()) {
               this.makeAreaOfEffectCloud(itemstack, potion);
            } else {
               this.applySplash(list, p_70227_1_.getType() == RayTraceResult.Type.ENTITY ? ((EntityRayTraceResult)p_70227_1_).getEntity() : null);
            }
         }

         int i = potion.hasInstantEffects() ? 2007 : 2002;
         this.level.levelEvent(i, this.blockPosition(), PotionUtils.getColor(itemstack));
         this.remove();
      }
   }

   private void applyWater() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
      List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, axisalignedbb, WATER_SENSITIVE);
      if (!list.isEmpty()) {
         for(LivingEntity livingentity : list) {
            double d0 = this.distanceToSqr(livingentity);
            if (d0 < 16.0D && livingentity.isSensitiveToWater()) {
               livingentity.hurt(DamageSource.indirectMagic(livingentity, this.getOwner()), 1.0F);
            }
         }
      }

   }

   private void applySplash(List<EffectInstance> p_213888_1_, @Nullable Entity p_213888_2_) {
      AxisAlignedBB axisalignedbb = this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
      List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, axisalignedbb);
      if (!list.isEmpty()) {
         for(LivingEntity livingentity : list) {
            if (livingentity.isAffectedByPotions()) {
               double d0 = this.distanceToSqr(livingentity);
               if (d0 < 16.0D) {
                  double d1 = 1.0D - Math.sqrt(d0) / 4.0D;
                  if (livingentity == p_213888_2_) {
                     d1 = 1.0D;
                  }

                  for(EffectInstance effectinstance : p_213888_1_) {
                     Effect effect = effectinstance.getEffect();
                     if (effect.isInstantenous()) {
                        effect.applyInstantenousEffect(this, this.getOwner(), livingentity, effectinstance.getAmplifier(), d1);
                     } else {
                        int i = (int)(d1 * (double)effectinstance.getDuration() + 0.5D);
                        if (i > 20) {
                           livingentity.addEffect(new EffectInstance(effect, i, effectinstance.getAmplifier(), effectinstance.isAmbient(), effectinstance.isVisible()));
                        }
                     }
                  }
               }
            }
         }
      }

   }

   private void makeAreaOfEffectCloud(ItemStack p_190542_1_, Potion p_190542_2_) {
      AreaEffectCloudEntity areaeffectcloudentity = new AreaEffectCloudEntity(this.level, this.getX(), this.getY(), this.getZ());
      Entity entity = this.getOwner();
      if (entity instanceof LivingEntity) {
         areaeffectcloudentity.setOwner((LivingEntity)entity);
      }

      areaeffectcloudentity.setRadius(3.0F);
      areaeffectcloudentity.setRadiusOnUse(-0.5F);
      areaeffectcloudentity.setWaitTime(10);
      areaeffectcloudentity.setRadiusPerTick(-areaeffectcloudentity.getRadius() / (float)areaeffectcloudentity.getDuration());
      areaeffectcloudentity.setPotion(p_190542_2_);

      for(EffectInstance effectinstance : PotionUtils.getCustomEffects(p_190542_1_)) {
         areaeffectcloudentity.addEffect(new EffectInstance(effectinstance));
      }

      CompoundNBT compoundnbt = p_190542_1_.getTag();
      if (compoundnbt != null && compoundnbt.contains("CustomPotionColor", 99)) {
         areaeffectcloudentity.setFixedColor(compoundnbt.getInt("CustomPotionColor"));
      }

      this.level.addFreshEntity(areaeffectcloudentity);
   }

   private boolean isLingering() {
      return this.getItem().getItem() == Items.LINGERING_POTION;
   }

   private void dowseFire(BlockPos p_184542_1_, Direction p_184542_2_) {
      BlockState blockstate = this.level.getBlockState(p_184542_1_);
      if (blockstate.is(BlockTags.FIRE)) {
         this.level.removeBlock(p_184542_1_, false);
      } else if (CampfireBlock.isLitCampfire(blockstate)) {
         this.level.levelEvent((PlayerEntity)null, 1009, p_184542_1_, 0);
         CampfireBlock.dowse(this.level, p_184542_1_, blockstate);
         this.level.setBlockAndUpdate(p_184542_1_, blockstate.setValue(CampfireBlock.LIT, Boolean.valueOf(false)));
      }

   }
}
