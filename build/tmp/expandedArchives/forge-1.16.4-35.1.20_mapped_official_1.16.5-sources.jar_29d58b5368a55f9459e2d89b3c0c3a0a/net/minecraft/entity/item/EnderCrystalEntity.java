package net.minecraft.entity.item;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EnderCrystalEntity extends Entity {
   private static final DataParameter<Optional<BlockPos>> DATA_BEAM_TARGET = EntityDataManager.defineId(EnderCrystalEntity.class, DataSerializers.OPTIONAL_BLOCK_POS);
   private static final DataParameter<Boolean> DATA_SHOW_BOTTOM = EntityDataManager.defineId(EnderCrystalEntity.class, DataSerializers.BOOLEAN);
   public int time;

   public EnderCrystalEntity(EntityType<? extends EnderCrystalEntity> p_i50231_1_, World p_i50231_2_) {
      super(p_i50231_1_, p_i50231_2_);
      this.blocksBuilding = true;
      this.time = this.random.nextInt(100000);
   }

   public EnderCrystalEntity(World p_i1699_1_, double p_i1699_2_, double p_i1699_4_, double p_i1699_6_) {
      this(EntityType.END_CRYSTAL, p_i1699_1_);
      this.setPos(p_i1699_2_, p_i1699_4_, p_i1699_6_);
   }

   protected boolean isMovementNoisy() {
      return false;
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_BEAM_TARGET, Optional.empty());
      this.getEntityData().define(DATA_SHOW_BOTTOM, true);
   }

   public void tick() {
      ++this.time;
      if (this.level instanceof ServerWorld) {
         BlockPos blockpos = this.blockPosition();
         if (((ServerWorld)this.level).dragonFight() != null && this.level.getBlockState(blockpos).isAir()) {
            this.level.setBlockAndUpdate(blockpos, AbstractFireBlock.getState(this.level, blockpos));
         }
      }

   }

   protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      if (this.getBeamTarget() != null) {
         p_213281_1_.put("BeamTarget", NBTUtil.writeBlockPos(this.getBeamTarget()));
      }

      p_213281_1_.putBoolean("ShowBottom", this.showsBottom());
   }

   protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      if (p_70037_1_.contains("BeamTarget", 10)) {
         this.setBeamTarget(NBTUtil.readBlockPos(p_70037_1_.getCompound("BeamTarget")));
      }

      if (p_70037_1_.contains("ShowBottom", 1)) {
         this.setShowBottom(p_70037_1_.getBoolean("ShowBottom"));
      }

   }

   public boolean isPickable() {
      return true;
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else if (p_70097_1_.getEntity() instanceof EnderDragonEntity) {
         return false;
      } else {
         if (!this.removed && !this.level.isClientSide) {
            this.remove();
            if (!p_70097_1_.isExplosion()) {
               this.level.explode((Entity)null, this.getX(), this.getY(), this.getZ(), 6.0F, Explosion.Mode.DESTROY);
            }

            this.onDestroyedBy(p_70097_1_);
         }

         return true;
      }
   }

   public void kill() {
      this.onDestroyedBy(DamageSource.GENERIC);
      super.kill();
   }

   private void onDestroyedBy(DamageSource p_184519_1_) {
      if (this.level instanceof ServerWorld) {
         DragonFightManager dragonfightmanager = ((ServerWorld)this.level).dragonFight();
         if (dragonfightmanager != null) {
            dragonfightmanager.onCrystalDestroyed(this, p_184519_1_);
         }
      }

   }

   public void setBeamTarget(@Nullable BlockPos p_184516_1_) {
      this.getEntityData().set(DATA_BEAM_TARGET, Optional.ofNullable(p_184516_1_));
   }

   @Nullable
   public BlockPos getBeamTarget() {
      return this.getEntityData().get(DATA_BEAM_TARGET).orElse((BlockPos)null);
   }

   public void setShowBottom(boolean p_184517_1_) {
      this.getEntityData().set(DATA_SHOW_BOTTOM, p_184517_1_);
   }

   public boolean showsBottom() {
      return this.getEntityData().get(DATA_SHOW_BOTTOM);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldRenderAtSqrDistance(double p_70112_1_) {
      return super.shouldRenderAtSqrDistance(p_70112_1_) || this.getBeamTarget() != null;
   }

   public IPacket<?> getAddEntityPacket() {
      return new SSpawnObjectPacket(this);
   }
}
