package net.minecraft.entity.projectile;

import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(
   value = Dist.CLIENT,
   _interface = IRendersAsItem.class
)
public class FireworkRocketEntity extends ProjectileEntity implements IRendersAsItem {
   private static final DataParameter<ItemStack> DATA_ID_FIREWORKS_ITEM = EntityDataManager.defineId(FireworkRocketEntity.class, DataSerializers.ITEM_STACK);
   private static final DataParameter<OptionalInt> DATA_ATTACHED_TO_TARGET = EntityDataManager.defineId(FireworkRocketEntity.class, DataSerializers.OPTIONAL_UNSIGNED_INT);
   private static final DataParameter<Boolean> DATA_SHOT_AT_ANGLE = EntityDataManager.defineId(FireworkRocketEntity.class, DataSerializers.BOOLEAN);
   private int life;
   private int lifetime;
   private LivingEntity attachedToEntity;

   public FireworkRocketEntity(EntityType<? extends FireworkRocketEntity> p_i50164_1_, World p_i50164_2_) {
      super(p_i50164_1_, p_i50164_2_);
   }

   public FireworkRocketEntity(World p_i1763_1_, double p_i1763_2_, double p_i1763_4_, double p_i1763_6_, ItemStack p_i1763_8_) {
      super(EntityType.FIREWORK_ROCKET, p_i1763_1_);
      this.life = 0;
      this.setPos(p_i1763_2_, p_i1763_4_, p_i1763_6_);
      int i = 1;
      if (!p_i1763_8_.isEmpty() && p_i1763_8_.hasTag()) {
         this.entityData.set(DATA_ID_FIREWORKS_ITEM, p_i1763_8_.copy());
         i += p_i1763_8_.getOrCreateTagElement("Fireworks").getByte("Flight");
      }

      this.setDeltaMovement(this.random.nextGaussian() * 0.001D, 0.05D, this.random.nextGaussian() * 0.001D);
      this.lifetime = 10 * i + this.random.nextInt(6) + this.random.nextInt(7);
   }

   public FireworkRocketEntity(World p_i231581_1_, @Nullable Entity p_i231581_2_, double p_i231581_3_, double p_i231581_5_, double p_i231581_7_, ItemStack p_i231581_9_) {
      this(p_i231581_1_, p_i231581_3_, p_i231581_5_, p_i231581_7_, p_i231581_9_);
      this.setOwner(p_i231581_2_);
   }

   public FireworkRocketEntity(World p_i47367_1_, ItemStack p_i47367_2_, LivingEntity p_i47367_3_) {
      this(p_i47367_1_, p_i47367_3_, p_i47367_3_.getX(), p_i47367_3_.getY(), p_i47367_3_.getZ(), p_i47367_2_);
      this.entityData.set(DATA_ATTACHED_TO_TARGET, OptionalInt.of(p_i47367_3_.getId()));
      this.attachedToEntity = p_i47367_3_;
   }

   public FireworkRocketEntity(World p_i50165_1_, ItemStack p_i50165_2_, double p_i50165_3_, double p_i50165_5_, double p_i50165_7_, boolean p_i50165_9_) {
      this(p_i50165_1_, p_i50165_3_, p_i50165_5_, p_i50165_7_, p_i50165_2_);
      this.entityData.set(DATA_SHOT_AT_ANGLE, p_i50165_9_);
   }

   public FireworkRocketEntity(World p_i231582_1_, ItemStack p_i231582_2_, Entity p_i231582_3_, double p_i231582_4_, double p_i231582_6_, double p_i231582_8_, boolean p_i231582_10_) {
      this(p_i231582_1_, p_i231582_2_, p_i231582_4_, p_i231582_6_, p_i231582_8_, p_i231582_10_);
      this.setOwner(p_i231582_3_);
   }

   protected void defineSynchedData() {
      this.entityData.define(DATA_ID_FIREWORKS_ITEM, ItemStack.EMPTY);
      this.entityData.define(DATA_ATTACHED_TO_TARGET, OptionalInt.empty());
      this.entityData.define(DATA_SHOT_AT_ANGLE, false);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldRenderAtSqrDistance(double p_70112_1_) {
      return p_70112_1_ < 4096.0D && !this.isAttachedToEntity();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldRender(double p_145770_1_, double p_145770_3_, double p_145770_5_) {
      return super.shouldRender(p_145770_1_, p_145770_3_, p_145770_5_) && !this.isAttachedToEntity();
   }

   public void tick() {
      super.tick();
      if (this.isAttachedToEntity()) {
         if (this.attachedToEntity == null) {
            this.entityData.get(DATA_ATTACHED_TO_TARGET).ifPresent((p_213891_1_) -> {
               Entity entity = this.level.getEntity(p_213891_1_);
               if (entity instanceof LivingEntity) {
                  this.attachedToEntity = (LivingEntity)entity;
               }

            });
         }

         if (this.attachedToEntity != null) {
            if (this.attachedToEntity.isFallFlying()) {
               Vector3d vector3d = this.attachedToEntity.getLookAngle();
               double d0 = 1.5D;
               double d1 = 0.1D;
               Vector3d vector3d1 = this.attachedToEntity.getDeltaMovement();
               this.attachedToEntity.setDeltaMovement(vector3d1.add(vector3d.x * 0.1D + (vector3d.x * 1.5D - vector3d1.x) * 0.5D, vector3d.y * 0.1D + (vector3d.y * 1.5D - vector3d1.y) * 0.5D, vector3d.z * 0.1D + (vector3d.z * 1.5D - vector3d1.z) * 0.5D));
            }

            this.setPos(this.attachedToEntity.getX(), this.attachedToEntity.getY(), this.attachedToEntity.getZ());
            this.setDeltaMovement(this.attachedToEntity.getDeltaMovement());
         }
      } else {
         if (!this.isShotAtAngle()) {
            double d2 = this.horizontalCollision ? 1.0D : 1.15D;
            this.setDeltaMovement(this.getDeltaMovement().multiply(d2, 1.0D, d2).add(0.0D, 0.04D, 0.0D));
         }

         Vector3d vector3d2 = this.getDeltaMovement();
         this.move(MoverType.SELF, vector3d2);
         this.setDeltaMovement(vector3d2);
      }

      RayTraceResult raytraceresult = ProjectileHelper.getHitResult(this, this::canHitEntity);
      if (!this.noPhysics) {
         this.onHit(raytraceresult);
         this.hasImpulse = true;
      }

      this.updateRotation();
      if (this.life == 0 && !this.isSilent()) {
         this.level.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 3.0F, 1.0F);
      }

      ++this.life;
      if (this.level.isClientSide && this.life % 2 < 2) {
         this.level.addParticle(ParticleTypes.FIREWORK, this.getX(), this.getY() - 0.3D, this.getZ(), this.random.nextGaussian() * 0.05D, -this.getDeltaMovement().y * 0.5D, this.random.nextGaussian() * 0.05D);
      }

      if (!this.level.isClientSide && this.life > this.lifetime) {
         this.explode();
      }

   }

   @Override
   protected void onHit(RayTraceResult result) {
      if (result.getType() == RayTraceResult.Type.MISS || !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, result)) {
         super.onHit(result);
      }
   }

   private void explode() {
      this.level.broadcastEntityEvent(this, (byte)17);
      this.dealExplosionDamage();
      this.remove();
   }

   protected void onHitEntity(EntityRayTraceResult p_213868_1_) {
      super.onHitEntity(p_213868_1_);
      if (!this.level.isClientSide) {
         this.explode();
      }
   }

   protected void onHitBlock(BlockRayTraceResult p_230299_1_) {
      BlockPos blockpos = new BlockPos(p_230299_1_.getBlockPos());
      this.level.getBlockState(blockpos).entityInside(this.level, blockpos, this);
      if (!this.level.isClientSide() && this.hasExplosion()) {
         this.explode();
      }

      super.onHitBlock(p_230299_1_);
   }

   private boolean hasExplosion() {
      ItemStack itemstack = this.entityData.get(DATA_ID_FIREWORKS_ITEM);
      CompoundNBT compoundnbt = itemstack.isEmpty() ? null : itemstack.getTagElement("Fireworks");
      ListNBT listnbt = compoundnbt != null ? compoundnbt.getList("Explosions", 10) : null;
      return listnbt != null && !listnbt.isEmpty();
   }

   private void dealExplosionDamage() {
      float f = 0.0F;
      ItemStack itemstack = this.entityData.get(DATA_ID_FIREWORKS_ITEM);
      CompoundNBT compoundnbt = itemstack.isEmpty() ? null : itemstack.getTagElement("Fireworks");
      ListNBT listnbt = compoundnbt != null ? compoundnbt.getList("Explosions", 10) : null;
      if (listnbt != null && !listnbt.isEmpty()) {
         f = 5.0F + (float)(listnbt.size() * 2);
      }

      if (f > 0.0F) {
         if (this.attachedToEntity != null) {
            this.attachedToEntity.hurt(DamageSource.fireworks(this, this.getOwner()), 5.0F + (float)(listnbt.size() * 2));
         }

         double d0 = 5.0D;
         Vector3d vector3d = this.position();

         for(LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5.0D))) {
            if (livingentity != this.attachedToEntity && !(this.distanceToSqr(livingentity) > 25.0D)) {
               boolean flag = false;

               for(int i = 0; i < 2; ++i) {
                  Vector3d vector3d1 = new Vector3d(livingentity.getX(), livingentity.getY(0.5D * (double)i), livingentity.getZ());
                  RayTraceResult raytraceresult = this.level.clip(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
                  if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
                     flag = true;
                     break;
                  }
               }

               if (flag) {
                  float f1 = f * (float)Math.sqrt((5.0D - (double)this.distanceTo(livingentity)) / 5.0D);
                  livingentity.hurt(DamageSource.fireworks(this, this.getOwner()), f1);
               }
            }
         }
      }

   }

   private boolean isAttachedToEntity() {
      return this.entityData.get(DATA_ATTACHED_TO_TARGET).isPresent();
   }

   public boolean isShotAtAngle() {
      return this.entityData.get(DATA_SHOT_AT_ANGLE);
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 17 && this.level.isClientSide) {
         if (!this.hasExplosion()) {
            for(int i = 0; i < this.random.nextInt(3) + 2; ++i) {
               this.level.addParticle(ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.05D, 0.005D, this.random.nextGaussian() * 0.05D);
            }
         } else {
            ItemStack itemstack = this.entityData.get(DATA_ID_FIREWORKS_ITEM);
            CompoundNBT compoundnbt = itemstack.isEmpty() ? null : itemstack.getTagElement("Fireworks");
            Vector3d vector3d = this.getDeltaMovement();
            this.level.createFireworks(this.getX(), this.getY(), this.getZ(), vector3d.x, vector3d.y, vector3d.z, compoundnbt);
         }
      }

      super.handleEntityEvent(p_70103_1_);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("Life", this.life);
      p_213281_1_.putInt("LifeTime", this.lifetime);
      ItemStack itemstack = this.entityData.get(DATA_ID_FIREWORKS_ITEM);
      if (!itemstack.isEmpty()) {
         p_213281_1_.put("FireworksItem", itemstack.save(new CompoundNBT()));
      }

      p_213281_1_.putBoolean("ShotAtAngle", this.entityData.get(DATA_SHOT_AT_ANGLE));
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.life = p_70037_1_.getInt("Life");
      this.lifetime = p_70037_1_.getInt("LifeTime");
      ItemStack itemstack = ItemStack.of(p_70037_1_.getCompound("FireworksItem"));
      if (!itemstack.isEmpty()) {
         this.entityData.set(DATA_ID_FIREWORKS_ITEM, itemstack);
      }

      if (p_70037_1_.contains("ShotAtAngle")) {
         this.entityData.set(DATA_SHOT_AT_ANGLE, p_70037_1_.getBoolean("ShotAtAngle"));
      }

   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getItem() {
      ItemStack itemstack = this.entityData.get(DATA_ID_FIREWORKS_ITEM);
      return itemstack.isEmpty() ? new ItemStack(Items.FIREWORK_ROCKET) : itemstack;
   }

   public boolean isAttackable() {
      return false;
   }

   public IPacket<?> getAddEntityPacket() {
      return new SSpawnObjectPacket(this);
   }
}
