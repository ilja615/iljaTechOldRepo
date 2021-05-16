package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ConduitTileEntity extends TileEntity implements ITickableTileEntity {
   private static final Block[] VALID_BLOCKS = new Block[]{Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS, Blocks.SEA_LANTERN, Blocks.DARK_PRISMARINE};
   public int tickCount;
   private float activeRotation;
   private boolean isActive;
   private boolean isHunting;
   private final List<BlockPos> effectBlocks = Lists.newArrayList();
   @Nullable
   private LivingEntity destroyTarget;
   @Nullable
   private UUID destroyTargetUUID;
   private long nextAmbientSoundActivation;

   public ConduitTileEntity() {
      this(TileEntityType.CONDUIT);
   }

   public ConduitTileEntity(TileEntityType<?> p_i48929_1_) {
      super(p_i48929_1_);
   }

   public void load(BlockState p_230337_1_, CompoundNBT p_230337_2_) {
      super.load(p_230337_1_, p_230337_2_);
      if (p_230337_2_.hasUUID("Target")) {
         this.destroyTargetUUID = p_230337_2_.getUUID("Target");
      } else {
         this.destroyTargetUUID = null;
      }

   }

   public CompoundNBT save(CompoundNBT p_189515_1_) {
      super.save(p_189515_1_);
      if (this.destroyTarget != null) {
         p_189515_1_.putUUID("Target", this.destroyTarget.getUUID());
      }

      return p_189515_1_;
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.worldPosition, 5, this.getUpdateTag());
   }

   public CompoundNBT getUpdateTag() {
      return this.save(new CompoundNBT());
   }

   public void tick() {
      ++this.tickCount;
      long i = this.level.getGameTime();
      if (i % 40L == 0L) {
         this.setActive(this.updateShape());
         if (!this.level.isClientSide && this.isActive()) {
            this.applyEffects();
            this.updateDestroyTarget();
         }
      }

      if (i % 80L == 0L && this.isActive()) {
         this.playSound(SoundEvents.CONDUIT_AMBIENT);
      }

      if (i > this.nextAmbientSoundActivation && this.isActive()) {
         this.nextAmbientSoundActivation = i + 60L + (long)this.level.getRandom().nextInt(40);
         this.playSound(SoundEvents.CONDUIT_AMBIENT_SHORT);
      }

      if (this.level.isClientSide) {
         this.updateClientTarget();
         this.animationTick();
         if (this.isActive()) {
            ++this.activeRotation;
         }
      }

   }

   private boolean updateShape() {
      this.effectBlocks.clear();

      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            for(int k = -1; k <= 1; ++k) {
               BlockPos blockpos = this.worldPosition.offset(i, j, k);
               if (!this.level.isWaterAt(blockpos)) {
                  return false;
               }
            }
         }
      }

      for(int j1 = -2; j1 <= 2; ++j1) {
         for(int k1 = -2; k1 <= 2; ++k1) {
            for(int l1 = -2; l1 <= 2; ++l1) {
               int i2 = Math.abs(j1);
               int l = Math.abs(k1);
               int i1 = Math.abs(l1);
               if ((i2 > 1 || l > 1 || i1 > 1) && (j1 == 0 && (l == 2 || i1 == 2) || k1 == 0 && (i2 == 2 || i1 == 2) || l1 == 0 && (i2 == 2 || l == 2))) {
                  BlockPos blockpos1 = this.worldPosition.offset(j1, k1, l1);
                  BlockState blockstate = this.level.getBlockState(blockpos1);

                  if (blockstate.isConduitFrame(this.level, blockpos1, getBlockPos())) {
                     this.effectBlocks.add(blockpos1);
                  }
               }
            }
         }
      }

      this.setHunting(this.effectBlocks.size() >= 42);
      return this.effectBlocks.size() >= 16;
   }

   private void applyEffects() {
      int i = this.effectBlocks.size();
      int j = i / 7 * 16;
      int k = this.worldPosition.getX();
      int l = this.worldPosition.getY();
      int i1 = this.worldPosition.getZ();
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double)k, (double)l, (double)i1, (double)(k + 1), (double)(l + 1), (double)(i1 + 1))).inflate((double)j).expandTowards(0.0D, (double)this.level.getMaxBuildHeight(), 0.0D);
      List<PlayerEntity> list = this.level.getEntitiesOfClass(PlayerEntity.class, axisalignedbb);
      if (!list.isEmpty()) {
         for(PlayerEntity playerentity : list) {
            if (this.worldPosition.closerThan(playerentity.blockPosition(), (double)j) && playerentity.isInWaterOrRain()) {
               playerentity.addEffect(new EffectInstance(Effects.CONDUIT_POWER, 260, 0, true, true));
            }
         }

      }
   }

   private void updateDestroyTarget() {
      LivingEntity livingentity = this.destroyTarget;
      int i = this.effectBlocks.size();
      if (i < 42) {
         this.destroyTarget = null;
      } else if (this.destroyTarget == null && this.destroyTargetUUID != null) {
         this.destroyTarget = this.findDestroyTarget();
         this.destroyTargetUUID = null;
      } else if (this.destroyTarget == null) {
         List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, this.getDestroyRangeAABB(), (p_205033_0_) -> {
            return p_205033_0_ instanceof IMob && p_205033_0_.isInWaterOrRain();
         });
         if (!list.isEmpty()) {
            this.destroyTarget = list.get(this.level.random.nextInt(list.size()));
         }
      } else if (!this.destroyTarget.isAlive() || !this.worldPosition.closerThan(this.destroyTarget.blockPosition(), 8.0D)) {
         this.destroyTarget = null;
      }

      if (this.destroyTarget != null) {
         this.level.playSound((PlayerEntity)null, this.destroyTarget.getX(), this.destroyTarget.getY(), this.destroyTarget.getZ(), SoundEvents.CONDUIT_ATTACK_TARGET, SoundCategory.BLOCKS, 1.0F, 1.0F);
         this.destroyTarget.hurt(DamageSource.MAGIC, 4.0F);
      }

      if (livingentity != this.destroyTarget) {
         BlockState blockstate = this.getBlockState();
         this.level.sendBlockUpdated(this.worldPosition, blockstate, blockstate, 2);
      }

   }

   private void updateClientTarget() {
      if (this.destroyTargetUUID == null) {
         this.destroyTarget = null;
      } else if (this.destroyTarget == null || !this.destroyTarget.getUUID().equals(this.destroyTargetUUID)) {
         this.destroyTarget = this.findDestroyTarget();
         if (this.destroyTarget == null) {
            this.destroyTargetUUID = null;
         }
      }

   }

   private AxisAlignedBB getDestroyRangeAABB() {
      int i = this.worldPosition.getX();
      int j = this.worldPosition.getY();
      int k = this.worldPosition.getZ();
      return (new AxisAlignedBB((double)i, (double)j, (double)k, (double)(i + 1), (double)(j + 1), (double)(k + 1))).inflate(8.0D);
   }

   @Nullable
   private LivingEntity findDestroyTarget() {
      List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, this.getDestroyRangeAABB(), (p_205032_1_) -> {
         return p_205032_1_.getUUID().equals(this.destroyTargetUUID);
      });
      return list.size() == 1 ? list.get(0) : null;
   }

   private void animationTick() {
      Random random = this.level.random;
      double d0 = (double)(MathHelper.sin((float)(this.tickCount + 35) * 0.1F) / 2.0F + 0.5F);
      d0 = (d0 * d0 + d0) * (double)0.3F;
      Vector3d vector3d = new Vector3d((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 1.5D + d0, (double)this.worldPosition.getZ() + 0.5D);

      for(BlockPos blockpos : this.effectBlocks) {
         if (random.nextInt(50) == 0) {
            float f = -0.5F + random.nextFloat();
            float f1 = -2.0F + random.nextFloat();
            float f2 = -0.5F + random.nextFloat();
            BlockPos blockpos1 = blockpos.subtract(this.worldPosition);
            Vector3d vector3d1 = (new Vector3d((double)f, (double)f1, (double)f2)).add((double)blockpos1.getX(), (double)blockpos1.getY(), (double)blockpos1.getZ());
            this.level.addParticle(ParticleTypes.NAUTILUS, vector3d.x, vector3d.y, vector3d.z, vector3d1.x, vector3d1.y, vector3d1.z);
         }
      }

      if (this.destroyTarget != null) {
         Vector3d vector3d2 = new Vector3d(this.destroyTarget.getX(), this.destroyTarget.getEyeY(), this.destroyTarget.getZ());
         float f3 = (-0.5F + random.nextFloat()) * (3.0F + this.destroyTarget.getBbWidth());
         float f4 = -1.0F + random.nextFloat() * this.destroyTarget.getBbHeight();
         float f5 = (-0.5F + random.nextFloat()) * (3.0F + this.destroyTarget.getBbWidth());
         Vector3d vector3d3 = new Vector3d((double)f3, (double)f4, (double)f5);
         this.level.addParticle(ParticleTypes.NAUTILUS, vector3d2.x, vector3d2.y, vector3d2.z, vector3d3.x, vector3d3.y, vector3d3.z);
      }

   }

   public boolean isActive() {
      return this.isActive;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isHunting() {
      return this.isHunting;
   }

   private void setActive(boolean p_205739_1_) {
      if (p_205739_1_ != this.isActive) {
         this.playSound(p_205739_1_ ? SoundEvents.CONDUIT_ACTIVATE : SoundEvents.CONDUIT_DEACTIVATE);
      }

      this.isActive = p_205739_1_;
   }

   private void setHunting(boolean p_207736_1_) {
      this.isHunting = p_207736_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public float getActiveRotation(float p_205036_1_) {
      return (this.activeRotation + p_205036_1_) * -0.0375F;
   }

   public void playSound(SoundEvent p_205738_1_) {
      this.level.playSound((PlayerEntity)null, this.worldPosition, p_205738_1_, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }
}
