package net.minecraft.entity.projectile;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FishingBobberEntity extends ProjectileEntity {
   private final Random syncronizedRandom = new Random();
   private boolean biting;
   private int outOfWaterTime;
   private static final DataParameter<Integer> DATA_HOOKED_ENTITY = EntityDataManager.defineId(FishingBobberEntity.class, DataSerializers.INT);
   private static final DataParameter<Boolean> DATA_BITING = EntityDataManager.defineId(FishingBobberEntity.class, DataSerializers.BOOLEAN);
   private int life;
   private int nibble;
   private int timeUntilLured;
   private int timeUntilHooked;
   private float fishAngle;
   private boolean openWater = true;
   private Entity hookedIn;
   private FishingBobberEntity.State currentState = FishingBobberEntity.State.FLYING;
   private final int luck;
   private final int lureSpeed;

   private FishingBobberEntity(World p_i50219_1_, PlayerEntity p_i50219_2_, int p_i50219_3_, int p_i50219_4_) {
      super(EntityType.FISHING_BOBBER, p_i50219_1_);
      this.noCulling = true;
      this.setOwner(p_i50219_2_);
      p_i50219_2_.fishing = this;
      this.luck = Math.max(0, p_i50219_3_);
      this.lureSpeed = Math.max(0, p_i50219_4_);
   }

   @OnlyIn(Dist.CLIENT)
   public FishingBobberEntity(World p_i47290_1_, PlayerEntity p_i47290_2_, double p_i47290_3_, double p_i47290_5_, double p_i47290_7_) {
      this(p_i47290_1_, p_i47290_2_, 0, 0);
      this.setPos(p_i47290_3_, p_i47290_5_, p_i47290_7_);
      this.xo = this.getX();
      this.yo = this.getY();
      this.zo = this.getZ();
   }

   public FishingBobberEntity(PlayerEntity p_i50220_1_, World p_i50220_2_, int p_i50220_3_, int p_i50220_4_) {
      this(p_i50220_2_, p_i50220_1_, p_i50220_3_, p_i50220_4_);
      float f = p_i50220_1_.xRot;
      float f1 = p_i50220_1_.yRot;
      float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
      float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
      float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
      float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
      double d0 = p_i50220_1_.getX() - (double)f3 * 0.3D;
      double d1 = p_i50220_1_.getEyeY();
      double d2 = p_i50220_1_.getZ() - (double)f2 * 0.3D;
      this.moveTo(d0, d1, d2, f1, f);
      Vector3d vector3d = new Vector3d((double)(-f3), (double)MathHelper.clamp(-(f5 / f4), -5.0F, 5.0F), (double)(-f2));
      double d3 = vector3d.length();
      vector3d = vector3d.multiply(0.6D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D, 0.6D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D, 0.6D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D);
      this.setDeltaMovement(vector3d);
      this.yRot = (float)(MathHelper.atan2(vector3d.x, vector3d.z) * (double)(180F / (float)Math.PI));
      this.xRot = (float)(MathHelper.atan2(vector3d.y, (double)MathHelper.sqrt(getHorizontalDistanceSqr(vector3d))) * (double)(180F / (float)Math.PI));
      this.yRotO = this.yRot;
      this.xRotO = this.xRot;
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_HOOKED_ENTITY, 0);
      this.getEntityData().define(DATA_BITING, false);
   }

   public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
      if (DATA_HOOKED_ENTITY.equals(p_184206_1_)) {
         int i = this.getEntityData().get(DATA_HOOKED_ENTITY);
         this.hookedIn = i > 0 ? this.level.getEntity(i - 1) : null;
      }

      if (DATA_BITING.equals(p_184206_1_)) {
         this.biting = this.getEntityData().get(DATA_BITING);
         if (this.biting) {
            this.setDeltaMovement(this.getDeltaMovement().x, (double)(-0.4F * MathHelper.nextFloat(this.syncronizedRandom, 0.6F, 1.0F)), this.getDeltaMovement().z);
         }
      }

      super.onSyncedDataUpdated(p_184206_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldRenderAtSqrDistance(double p_70112_1_) {
      double d0 = 64.0D;
      return p_70112_1_ < 4096.0D;
   }

   @OnlyIn(Dist.CLIENT)
   public void lerpTo(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_) {
   }

   public void tick() {
      this.syncronizedRandom.setSeed(this.getUUID().getLeastSignificantBits() ^ this.level.getGameTime());
      super.tick();
      PlayerEntity playerentity = this.getPlayerOwner();
      if (playerentity == null) {
         this.remove();
      } else if (this.level.isClientSide || !this.shouldStopFishing(playerentity)) {
         if (this.onGround) {
            ++this.life;
            if (this.life >= 1200) {
               this.remove();
               return;
            }
         } else {
            this.life = 0;
         }

         float f = 0.0F;
         BlockPos blockpos = this.blockPosition();
         FluidState fluidstate = this.level.getFluidState(blockpos);
         if (fluidstate.is(FluidTags.WATER)) {
            f = fluidstate.getHeight(this.level, blockpos);
         }

         boolean flag = f > 0.0F;
         if (this.currentState == FishingBobberEntity.State.FLYING) {
            if (this.hookedIn != null) {
               this.setDeltaMovement(Vector3d.ZERO);
               this.currentState = FishingBobberEntity.State.HOOKED_IN_ENTITY;
               return;
            }

            if (flag) {
               this.setDeltaMovement(this.getDeltaMovement().multiply(0.3D, 0.2D, 0.3D));
               this.currentState = FishingBobberEntity.State.BOBBING;
               return;
            }

            this.checkCollision();
         } else {
            if (this.currentState == FishingBobberEntity.State.HOOKED_IN_ENTITY) {
               if (this.hookedIn != null) {
                  if (this.hookedIn.removed) {
                     this.hookedIn = null;
                     this.currentState = FishingBobberEntity.State.FLYING;
                  } else {
                     this.setPos(this.hookedIn.getX(), this.hookedIn.getY(0.8D), this.hookedIn.getZ());
                  }
               }

               return;
            }

            if (this.currentState == FishingBobberEntity.State.BOBBING) {
               Vector3d vector3d = this.getDeltaMovement();
               double d0 = this.getY() + vector3d.y - (double)blockpos.getY() - (double)f;
               if (Math.abs(d0) < 0.01D) {
                  d0 += Math.signum(d0) * 0.1D;
               }

               this.setDeltaMovement(vector3d.x * 0.9D, vector3d.y - d0 * (double)this.random.nextFloat() * 0.2D, vector3d.z * 0.9D);
               if (this.nibble <= 0 && this.timeUntilHooked <= 0) {
                  this.openWater = true;
               } else {
                  this.openWater = this.openWater && this.outOfWaterTime < 10 && this.calculateOpenWater(blockpos);
               }

               if (flag) {
                  this.outOfWaterTime = Math.max(0, this.outOfWaterTime - 1);
                  if (this.biting) {
                     this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.1D * (double)this.syncronizedRandom.nextFloat() * (double)this.syncronizedRandom.nextFloat(), 0.0D));
                  }

                  if (!this.level.isClientSide) {
                     this.catchingFish(blockpos);
                  }
               } else {
                  this.outOfWaterTime = Math.min(10, this.outOfWaterTime + 1);
               }
            }
         }

         if (!fluidstate.is(FluidTags.WATER)) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
         }

         this.move(MoverType.SELF, this.getDeltaMovement());
         this.updateRotation();
         if (this.currentState == FishingBobberEntity.State.FLYING && (this.onGround || this.horizontalCollision)) {
            this.setDeltaMovement(Vector3d.ZERO);
         }

         double d1 = 0.92D;
         this.setDeltaMovement(this.getDeltaMovement().scale(0.92D));
         this.reapplyPosition();
      }
   }

   private boolean shouldStopFishing(PlayerEntity p_234600_1_) {
      ItemStack itemstack = p_234600_1_.getMainHandItem();
      ItemStack itemstack1 = p_234600_1_.getOffhandItem();
      boolean flag = itemstack.getItem() instanceof net.minecraft.item.FishingRodItem;
      boolean flag1 = itemstack1.getItem() instanceof net.minecraft.item.FishingRodItem;
      if (!p_234600_1_.removed && p_234600_1_.isAlive() && (flag || flag1) && !(this.distanceToSqr(p_234600_1_) > 1024.0D)) {
         return false;
      } else {
         this.remove();
         return true;
      }
   }

   private void checkCollision() {
      RayTraceResult raytraceresult = ProjectileHelper.getHitResult(this, this::canHitEntity);
      this.onHit(raytraceresult);
   }

   protected boolean canHitEntity(Entity p_230298_1_) {
      return super.canHitEntity(p_230298_1_) || p_230298_1_.isAlive() && p_230298_1_ instanceof ItemEntity;
   }

   protected void onHitEntity(EntityRayTraceResult p_213868_1_) {
      super.onHitEntity(p_213868_1_);
      if (!this.level.isClientSide) {
         this.hookedIn = p_213868_1_.getEntity();
         this.setHookedEntity();
      }

   }

   protected void onHitBlock(BlockRayTraceResult p_230299_1_) {
      super.onHitBlock(p_230299_1_);
      this.setDeltaMovement(this.getDeltaMovement().normalize().scale(p_230299_1_.distanceTo(this)));
   }

   private void setHookedEntity() {
      this.getEntityData().set(DATA_HOOKED_ENTITY, this.hookedIn.getId() + 1);
   }

   private void catchingFish(BlockPos p_190621_1_) {
      ServerWorld serverworld = (ServerWorld)this.level;
      int i = 1;
      BlockPos blockpos = p_190621_1_.above();
      if (this.random.nextFloat() < 0.25F && this.level.isRainingAt(blockpos)) {
         ++i;
      }

      if (this.random.nextFloat() < 0.5F && !this.level.canSeeSky(blockpos)) {
         --i;
      }

      if (this.nibble > 0) {
         --this.nibble;
         if (this.nibble <= 0) {
            this.timeUntilLured = 0;
            this.timeUntilHooked = 0;
            this.getEntityData().set(DATA_BITING, false);
         }
      } else if (this.timeUntilHooked > 0) {
         this.timeUntilHooked -= i;
         if (this.timeUntilHooked > 0) {
            this.fishAngle = (float)((double)this.fishAngle + this.random.nextGaussian() * 4.0D);
            float f = this.fishAngle * ((float)Math.PI / 180F);
            float f1 = MathHelper.sin(f);
            float f2 = MathHelper.cos(f);
            double d0 = this.getX() + (double)(f1 * (float)this.timeUntilHooked * 0.1F);
            double d1 = (double)((float)MathHelper.floor(this.getY()) + 1.0F);
            double d2 = this.getZ() + (double)(f2 * (float)this.timeUntilHooked * 0.1F);
            BlockState blockstate = serverworld.getBlockState(new BlockPos(d0, d1 - 1.0D, d2));
            if (serverworld.getBlockState(new BlockPos((int)d0, (int)d1 - 1, (int)d2)).getMaterial() == net.minecraft.block.material.Material.WATER) {
               if (this.random.nextFloat() < 0.15F) {
                  serverworld.sendParticles(ParticleTypes.BUBBLE, d0, d1 - (double)0.1F, d2, 1, (double)f1, 0.1D, (double)f2, 0.0D);
               }

               float f3 = f1 * 0.04F;
               float f4 = f2 * 0.04F;
               serverworld.sendParticles(ParticleTypes.FISHING, d0, d1, d2, 0, (double)f4, 0.01D, (double)(-f3), 1.0D);
               serverworld.sendParticles(ParticleTypes.FISHING, d0, d1, d2, 0, (double)(-f4), 0.01D, (double)f3, 1.0D);
            }
         } else {
            this.playSound(SoundEvents.FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
            double d3 = this.getY() + 0.5D;
            serverworld.sendParticles(ParticleTypes.BUBBLE, this.getX(), d3, this.getZ(), (int)(1.0F + this.getBbWidth() * 20.0F), (double)this.getBbWidth(), 0.0D, (double)this.getBbWidth(), (double)0.2F);
            serverworld.sendParticles(ParticleTypes.FISHING, this.getX(), d3, this.getZ(), (int)(1.0F + this.getBbWidth() * 20.0F), (double)this.getBbWidth(), 0.0D, (double)this.getBbWidth(), (double)0.2F);
            this.nibble = MathHelper.nextInt(this.random, 20, 40);
            this.getEntityData().set(DATA_BITING, true);
         }
      } else if (this.timeUntilLured > 0) {
         this.timeUntilLured -= i;
         float f5 = 0.15F;
         if (this.timeUntilLured < 20) {
            f5 = (float)((double)f5 + (double)(20 - this.timeUntilLured) * 0.05D);
         } else if (this.timeUntilLured < 40) {
            f5 = (float)((double)f5 + (double)(40 - this.timeUntilLured) * 0.02D);
         } else if (this.timeUntilLured < 60) {
            f5 = (float)((double)f5 + (double)(60 - this.timeUntilLured) * 0.01D);
         }

         if (this.random.nextFloat() < f5) {
            float f6 = MathHelper.nextFloat(this.random, 0.0F, 360.0F) * ((float)Math.PI / 180F);
            float f7 = MathHelper.nextFloat(this.random, 25.0F, 60.0F);
            double d4 = this.getX() + (double)(MathHelper.sin(f6) * f7 * 0.1F);
            double d5 = (double)((float)MathHelper.floor(this.getY()) + 1.0F);
            double d6 = this.getZ() + (double)(MathHelper.cos(f6) * f7 * 0.1F);
            BlockState blockstate1 = serverworld.getBlockState(new BlockPos(d4, d5 - 1.0D, d6));
            if (serverworld.getBlockState(new BlockPos(d4, d5 - 1.0D, d6)).getMaterial() == net.minecraft.block.material.Material.WATER) {
               serverworld.sendParticles(ParticleTypes.SPLASH, d4, d5, d6, 2 + this.random.nextInt(2), (double)0.1F, 0.0D, (double)0.1F, 0.0D);
            }
         }

         if (this.timeUntilLured <= 0) {
            this.fishAngle = MathHelper.nextFloat(this.random, 0.0F, 360.0F);
            this.timeUntilHooked = MathHelper.nextInt(this.random, 20, 80);
         }
      } else {
         this.timeUntilLured = MathHelper.nextInt(this.random, 100, 600);
         this.timeUntilLured -= this.lureSpeed * 20 * 5;
      }

   }

   private boolean calculateOpenWater(BlockPos p_234603_1_) {
      FishingBobberEntity.WaterType fishingbobberentity$watertype = FishingBobberEntity.WaterType.INVALID;

      for(int i = -1; i <= 2; ++i) {
         FishingBobberEntity.WaterType fishingbobberentity$watertype1 = this.getOpenWaterTypeForArea(p_234603_1_.offset(-2, i, -2), p_234603_1_.offset(2, i, 2));
         switch(fishingbobberentity$watertype1) {
         case INVALID:
            return false;
         case ABOVE_WATER:
            if (fishingbobberentity$watertype == FishingBobberEntity.WaterType.INVALID) {
               return false;
            }
            break;
         case INSIDE_WATER:
            if (fishingbobberentity$watertype == FishingBobberEntity.WaterType.ABOVE_WATER) {
               return false;
            }
         }

         fishingbobberentity$watertype = fishingbobberentity$watertype1;
      }

      return true;
   }

   private FishingBobberEntity.WaterType getOpenWaterTypeForArea(BlockPos p_234602_1_, BlockPos p_234602_2_) {
      return BlockPos.betweenClosedStream(p_234602_1_, p_234602_2_).map(this::getOpenWaterTypeForBlock).reduce((p_234601_0_, p_234601_1_) -> {
         return p_234601_0_ == p_234601_1_ ? p_234601_0_ : FishingBobberEntity.WaterType.INVALID;
      }).orElse(FishingBobberEntity.WaterType.INVALID);
   }

   private FishingBobberEntity.WaterType getOpenWaterTypeForBlock(BlockPos p_234604_1_) {
      BlockState blockstate = this.level.getBlockState(p_234604_1_);
      if (!blockstate.isAir() && !blockstate.is(Blocks.LILY_PAD)) {
         FluidState fluidstate = blockstate.getFluidState();
         return fluidstate.is(FluidTags.WATER) && fluidstate.isSource() && blockstate.getCollisionShape(this.level, p_234604_1_).isEmpty() ? FishingBobberEntity.WaterType.INSIDE_WATER : FishingBobberEntity.WaterType.INVALID;
      } else {
         return FishingBobberEntity.WaterType.ABOVE_WATER;
      }
   }

   public boolean isOpenWaterFishing() {
      return this.openWater;
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
   }

   public int retrieve(ItemStack p_146034_1_) {
      PlayerEntity playerentity = this.getPlayerOwner();
      if (!this.level.isClientSide && playerentity != null) {
         int i = 0;
         net.minecraftforge.event.entity.player.ItemFishedEvent event = null;
         if (this.hookedIn != null) {
            this.bringInHookedEntity();
            CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity)playerentity, p_146034_1_, this, Collections.emptyList());
            this.level.broadcastEntityEvent(this, (byte)31);
            i = this.hookedIn instanceof ItemEntity ? 3 : 5;
         } else if (this.nibble > 0) {
            LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld)this.level)).withParameter(LootParameters.ORIGIN, this.position()).withParameter(LootParameters.TOOL, p_146034_1_).withParameter(LootParameters.THIS_ENTITY, this).withRandom(this.random).withLuck((float)this.luck + playerentity.getLuck());
            lootcontext$builder.withParameter(LootParameters.KILLER_ENTITY, this.getOwner()).withParameter(LootParameters.THIS_ENTITY, this);
            LootTable loottable = this.level.getServer().getLootTables().get(LootTables.FISHING);
            List<ItemStack> list = loottable.getRandomItems(lootcontext$builder.create(LootParameterSets.FISHING));
            event = new net.minecraftforge.event.entity.player.ItemFishedEvent(list, this.onGround ? 2 : 1, this);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
               this.remove();
               return event.getRodDamage();
            }
            CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity)playerentity, p_146034_1_, this, list);

            for(ItemStack itemstack : list) {
               ItemEntity itementity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), itemstack);
               double d0 = playerentity.getX() - this.getX();
               double d1 = playerentity.getY() - this.getY();
               double d2 = playerentity.getZ() - this.getZ();
               double d3 = 0.1D;
               itementity.setDeltaMovement(d0 * 0.1D, d1 * 0.1D + Math.sqrt(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)) * 0.08D, d2 * 0.1D);
               this.level.addFreshEntity(itementity);
               playerentity.level.addFreshEntity(new ExperienceOrbEntity(playerentity.level, playerentity.getX(), playerentity.getY() + 0.5D, playerentity.getZ() + 0.5D, this.random.nextInt(6) + 1));
               if (itemstack.getItem().is(ItemTags.FISHES)) {
                  playerentity.awardStat(Stats.FISH_CAUGHT, 1);
               }
            }

            i = 1;
         }

         if (this.onGround) {
            i = 2;
         }

         this.remove();
         return event == null ? i : event.getRodDamage();
      } else {
         return 0;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 31 && this.level.isClientSide && this.hookedIn instanceof PlayerEntity && ((PlayerEntity)this.hookedIn).isLocalPlayer()) {
         this.bringInHookedEntity();
      }

      super.handleEntityEvent(p_70103_1_);
   }

   protected void bringInHookedEntity() {
      Entity entity = this.getOwner();
      if (entity != null) {
         Vector3d vector3d = (new Vector3d(entity.getX() - this.getX(), entity.getY() - this.getY(), entity.getZ() - this.getZ())).scale(0.1D);
         this.hookedIn.setDeltaMovement(this.hookedIn.getDeltaMovement().add(vector3d));
      }
   }

   protected boolean isMovementNoisy() {
      return false;
   }

   @Override
   public void remove(boolean keepData) {
      super.remove(keepData);
      PlayerEntity playerentity = this.getPlayerOwner();
      if (playerentity != null) {
         playerentity.fishing = null;
      }

   }

   @Nullable
   public PlayerEntity getPlayerOwner() {
      Entity entity = this.getOwner();
      return entity instanceof PlayerEntity ? (PlayerEntity)entity : null;
   }

   @Nullable
   public Entity getHookedIn() {
      return this.hookedIn;
   }

   public boolean canChangeDimensions() {
      return false;
   }

   public IPacket<?> getAddEntityPacket() {
      Entity entity = this.getOwner();
      return new SSpawnObjectPacket(this, entity == null ? this.getId() : entity.getId());
   }

   static enum State {
      FLYING,
      HOOKED_IN_ENTITY,
      BOBBING;
   }

   static enum WaterType {
      ABOVE_WATER,
      INSIDE_WATER,
      INVALID;
   }
}
