package net.minecraft.entity.item.minecart;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TNTMinecartEntity extends AbstractMinecartEntity {
   private int fuse = -1;

   public TNTMinecartEntity(EntityType<? extends TNTMinecartEntity> p_i50112_1_, World p_i50112_2_) {
      super(p_i50112_1_, p_i50112_2_);
   }

   public TNTMinecartEntity(World p_i1728_1_, double p_i1728_2_, double p_i1728_4_, double p_i1728_6_) {
      super(EntityType.TNT_MINECART, p_i1728_1_, p_i1728_2_, p_i1728_4_, p_i1728_6_);
   }

   public AbstractMinecartEntity.Type getMinecartType() {
      return AbstractMinecartEntity.Type.TNT;
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.TNT.defaultBlockState();
   }

   public void tick() {
      super.tick();
      if (this.fuse > 0) {
         --this.fuse;
         this.level.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
      } else if (this.fuse == 0) {
         this.explode(getHorizontalDistanceSqr(this.getDeltaMovement()));
      }

      if (this.horizontalCollision) {
         double d0 = getHorizontalDistanceSqr(this.getDeltaMovement());
         if (d0 >= (double)0.01F) {
            this.explode(d0);
         }
      }

   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      Entity entity = p_70097_1_.getDirectEntity();
      if (entity instanceof AbstractArrowEntity) {
         AbstractArrowEntity abstractarrowentity = (AbstractArrowEntity)entity;
         if (abstractarrowentity.isOnFire()) {
            this.explode(abstractarrowentity.getDeltaMovement().lengthSqr());
         }
      }

      return super.hurt(p_70097_1_, p_70097_2_);
   }

   public void destroy(DamageSource p_94095_1_) {
      double d0 = getHorizontalDistanceSqr(this.getDeltaMovement());
      if (!p_94095_1_.isFire() && !p_94095_1_.isExplosion() && !(d0 >= (double)0.01F)) {
         super.destroy(p_94095_1_);
         if (!p_94095_1_.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.spawnAtLocation(Blocks.TNT);
         }

      } else {
         if (this.fuse < 0) {
            this.primeFuse();
            this.fuse = this.random.nextInt(20) + this.random.nextInt(20);
         }

      }
   }

   protected void explode(double p_94103_1_) {
      if (!this.level.isClientSide) {
         double d0 = Math.sqrt(p_94103_1_);
         if (d0 > 5.0D) {
            d0 = 5.0D;
         }

         this.level.explode(this, this.getX(), this.getY(), this.getZ(), (float)(4.0D + this.random.nextDouble() * 1.5D * d0), Explosion.Mode.BREAK);
         this.remove();
      }

   }

   public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
      if (p_225503_1_ >= 3.0F) {
         float f = p_225503_1_ / 10.0F;
         this.explode((double)(f * f));
      }

      return super.causeFallDamage(p_225503_1_, p_225503_2_);
   }

   public void activateMinecart(int p_96095_1_, int p_96095_2_, int p_96095_3_, boolean p_96095_4_) {
      if (p_96095_4_ && this.fuse < 0) {
         this.primeFuse();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 10) {
         this.primeFuse();
      } else {
         super.handleEntityEvent(p_70103_1_);
      }

   }

   public void primeFuse() {
      this.fuse = 80;
      if (!this.level.isClientSide) {
         this.level.broadcastEntityEvent(this, (byte)10);
         if (!this.isSilent()) {
            this.level.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public int getFuse() {
      return this.fuse;
   }

   public boolean isPrimed() {
      return this.fuse > -1;
   }

   public float getBlockExplosionResistance(Explosion p_180428_1_, IBlockReader p_180428_2_, BlockPos p_180428_3_, BlockState p_180428_4_, FluidState p_180428_5_, float p_180428_6_) {
      return !this.isPrimed() || !p_180428_4_.is(BlockTags.RAILS) && !p_180428_2_.getBlockState(p_180428_3_.above()).is(BlockTags.RAILS) ? super.getBlockExplosionResistance(p_180428_1_, p_180428_2_, p_180428_3_, p_180428_4_, p_180428_5_, p_180428_6_) : 0.0F;
   }

   public boolean shouldBlockExplode(Explosion p_174816_1_, IBlockReader p_174816_2_, BlockPos p_174816_3_, BlockState p_174816_4_, float p_174816_5_) {
      return !this.isPrimed() || !p_174816_4_.is(BlockTags.RAILS) && !p_174816_2_.getBlockState(p_174816_3_.above()).is(BlockTags.RAILS) ? super.shouldBlockExplode(p_174816_1_, p_174816_2_, p_174816_3_, p_174816_4_, p_174816_5_) : false;
   }

   protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      if (p_70037_1_.contains("TNTFuse", 99)) {
         this.fuse = p_70037_1_.getInt("TNTFuse");
      }

   }

   protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("TNTFuse", this.fuse);
   }
}
