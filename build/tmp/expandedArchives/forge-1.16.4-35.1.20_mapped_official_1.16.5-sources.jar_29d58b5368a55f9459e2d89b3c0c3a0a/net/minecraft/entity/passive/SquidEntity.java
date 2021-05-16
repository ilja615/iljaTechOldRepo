package net.minecraft.entity.passive;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.fluid.FluidState;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SquidEntity extends WaterMobEntity {
   public float xBodyRot;
   public float xBodyRotO;
   public float zBodyRot;
   public float zBodyRotO;
   public float tentacleMovement;
   public float oldTentacleMovement;
   public float tentacleAngle;
   public float oldTentacleAngle;
   private float speed;
   private float tentacleSpeed;
   private float rotateSpeed;
   private float tx;
   private float ty;
   private float tz;

   public SquidEntity(EntityType<? extends SquidEntity> p_i50243_1_, World p_i50243_2_) {
      super(p_i50243_1_, p_i50243_2_);
      this.random.setSeed((long)this.getId());
      this.tentacleSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new SquidEntity.MoveRandomGoal(this));
      this.goalSelector.addGoal(1, new SquidEntity.FleeGoal());
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D);
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return p_213348_2_.height * 0.5F;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SQUID_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.SQUID_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SQUID_DEATH;
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   protected boolean isMovementNoisy() {
      return false;
   }

   public void aiStep() {
      super.aiStep();
      this.xBodyRotO = this.xBodyRot;
      this.zBodyRotO = this.zBodyRot;
      this.oldTentacleMovement = this.tentacleMovement;
      this.oldTentacleAngle = this.tentacleAngle;
      this.tentacleMovement += this.tentacleSpeed;
      if ((double)this.tentacleMovement > (Math.PI * 2D)) {
         if (this.level.isClientSide) {
            this.tentacleMovement = ((float)Math.PI * 2F);
         } else {
            this.tentacleMovement = (float)((double)this.tentacleMovement - (Math.PI * 2D));
            if (this.random.nextInt(10) == 0) {
               this.tentacleSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
            }

            this.level.broadcastEntityEvent(this, (byte)19);
         }
      }

      if (this.isInWaterOrBubble()) {
         if (this.tentacleMovement < (float)Math.PI) {
            float f = this.tentacleMovement / (float)Math.PI;
            this.tentacleAngle = MathHelper.sin(f * f * (float)Math.PI) * (float)Math.PI * 0.25F;
            if ((double)f > 0.75D) {
               this.speed = 1.0F;
               this.rotateSpeed = 1.0F;
            } else {
               this.rotateSpeed *= 0.8F;
            }
         } else {
            this.tentacleAngle = 0.0F;
            this.speed *= 0.9F;
            this.rotateSpeed *= 0.99F;
         }

         if (!this.level.isClientSide) {
            this.setDeltaMovement((double)(this.tx * this.speed), (double)(this.ty * this.speed), (double)(this.tz * this.speed));
         }

         Vector3d vector3d = this.getDeltaMovement();
         float f1 = MathHelper.sqrt(getHorizontalDistanceSqr(vector3d));
         this.yBodyRot += (-((float)MathHelper.atan2(vector3d.x, vector3d.z)) * (180F / (float)Math.PI) - this.yBodyRot) * 0.1F;
         this.yRot = this.yBodyRot;
         this.zBodyRot = (float)((double)this.zBodyRot + Math.PI * (double)this.rotateSpeed * 1.5D);
         this.xBodyRot += (-((float)MathHelper.atan2((double)f1, vector3d.y)) * (180F / (float)Math.PI) - this.xBodyRot) * 0.1F;
      } else {
         this.tentacleAngle = MathHelper.abs(MathHelper.sin(this.tentacleMovement)) * (float)Math.PI * 0.25F;
         if (!this.level.isClientSide) {
            double d0 = this.getDeltaMovement().y;
            if (this.hasEffect(Effects.LEVITATION)) {
               d0 = 0.05D * (double)(this.getEffect(Effects.LEVITATION).getAmplifier() + 1);
            } else if (!this.isNoGravity()) {
               d0 -= 0.08D;
            }

            this.setDeltaMovement(0.0D, d0 * (double)0.98F, 0.0D);
         }

         this.xBodyRot = (float)((double)this.xBodyRot + (double)(-90.0F - this.xBodyRot) * 0.02D);
      }

   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (super.hurt(p_70097_1_, p_70097_2_) && this.getLastHurtByMob() != null) {
         this.spawnInk();
         return true;
      } else {
         return false;
      }
   }

   private Vector3d rotateVector(Vector3d p_207400_1_) {
      Vector3d vector3d = p_207400_1_.xRot(this.xBodyRotO * ((float)Math.PI / 180F));
      return vector3d.yRot(-this.yBodyRotO * ((float)Math.PI / 180F));
   }

   private void spawnInk() {
      this.playSound(SoundEvents.SQUID_SQUIRT, this.getSoundVolume(), this.getVoicePitch());
      Vector3d vector3d = this.rotateVector(new Vector3d(0.0D, -1.0D, 0.0D)).add(this.getX(), this.getY(), this.getZ());

      for(int i = 0; i < 30; ++i) {
         Vector3d vector3d1 = this.rotateVector(new Vector3d((double)this.random.nextFloat() * 0.6D - 0.3D, -1.0D, (double)this.random.nextFloat() * 0.6D - 0.3D));
         Vector3d vector3d2 = vector3d1.scale(0.3D + (double)(this.random.nextFloat() * 2.0F));
         ((ServerWorld)this.level).sendParticles(ParticleTypes.SQUID_INK, vector3d.x, vector3d.y + 0.5D, vector3d.z, 0, vector3d2.x, vector3d2.y, vector3d2.z, (double)0.1F);
      }

   }

   public void travel(Vector3d p_213352_1_) {
      this.move(MoverType.SELF, this.getDeltaMovement());
   }

   public static boolean checkSquidSpawnRules(EntityType<SquidEntity> p_223365_0_, IWorld p_223365_1_, SpawnReason p_223365_2_, BlockPos p_223365_3_, Random p_223365_4_) {
      return p_223365_3_.getY() > 45 && p_223365_3_.getY() < p_223365_1_.getSeaLevel();
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 19) {
         this.tentacleMovement = 0.0F;
      } else {
         super.handleEntityEvent(p_70103_1_);
      }

   }

   public void setMovementVector(float p_175568_1_, float p_175568_2_, float p_175568_3_) {
      this.tx = p_175568_1_;
      this.ty = p_175568_2_;
      this.tz = p_175568_3_;
   }

   public boolean hasMovementVector() {
      return this.tx != 0.0F || this.ty != 0.0F || this.tz != 0.0F;
   }

   class FleeGoal extends Goal {
      private int fleeTicks;

      private FleeGoal() {
      }

      public boolean canUse() {
         LivingEntity livingentity = SquidEntity.this.getLastHurtByMob();
         if (SquidEntity.this.isInWater() && livingentity != null) {
            return SquidEntity.this.distanceToSqr(livingentity) < 100.0D;
         } else {
            return false;
         }
      }

      public void start() {
         this.fleeTicks = 0;
      }

      public void tick() {
         ++this.fleeTicks;
         LivingEntity livingentity = SquidEntity.this.getLastHurtByMob();
         if (livingentity != null) {
            Vector3d vector3d = new Vector3d(SquidEntity.this.getX() - livingentity.getX(), SquidEntity.this.getY() - livingentity.getY(), SquidEntity.this.getZ() - livingentity.getZ());
            BlockState blockstate = SquidEntity.this.level.getBlockState(new BlockPos(SquidEntity.this.getX() + vector3d.x, SquidEntity.this.getY() + vector3d.y, SquidEntity.this.getZ() + vector3d.z));
            FluidState fluidstate = SquidEntity.this.level.getFluidState(new BlockPos(SquidEntity.this.getX() + vector3d.x, SquidEntity.this.getY() + vector3d.y, SquidEntity.this.getZ() + vector3d.z));
            if (fluidstate.is(FluidTags.WATER) || blockstate.isAir()) {
               double d0 = vector3d.length();
               if (d0 > 0.0D) {
                  vector3d.normalize();
                  float f = 3.0F;
                  if (d0 > 5.0D) {
                     f = (float)((double)f - (d0 - 5.0D) / 5.0D);
                  }

                  if (f > 0.0F) {
                     vector3d = vector3d.scale((double)f);
                  }
               }

               if (blockstate.isAir()) {
                  vector3d = vector3d.subtract(0.0D, vector3d.y, 0.0D);
               }

               SquidEntity.this.setMovementVector((float)vector3d.x / 20.0F, (float)vector3d.y / 20.0F, (float)vector3d.z / 20.0F);
            }

            if (this.fleeTicks % 10 == 5) {
               SquidEntity.this.level.addParticle(ParticleTypes.BUBBLE, SquidEntity.this.getX(), SquidEntity.this.getY(), SquidEntity.this.getZ(), 0.0D, 0.0D, 0.0D);
            }

         }
      }
   }

   class MoveRandomGoal extends Goal {
      private final SquidEntity squid;

      public MoveRandomGoal(SquidEntity p_i48823_2_) {
         this.squid = p_i48823_2_;
      }

      public boolean canUse() {
         return true;
      }

      public void tick() {
         int i = this.squid.getNoActionTime();
         if (i > 100) {
            this.squid.setMovementVector(0.0F, 0.0F, 0.0F);
         } else if (this.squid.getRandom().nextInt(50) == 0 || !this.squid.wasTouchingWater || !this.squid.hasMovementVector()) {
            float f = this.squid.getRandom().nextFloat() * ((float)Math.PI * 2F);
            float f1 = MathHelper.cos(f) * 0.2F;
            float f2 = -0.1F + this.squid.getRandom().nextFloat() * 0.2F;
            float f3 = MathHelper.sin(f) * 0.2F;
            this.squid.setMovementVector(f1, f2, f3);
         }

      }
   }
}
