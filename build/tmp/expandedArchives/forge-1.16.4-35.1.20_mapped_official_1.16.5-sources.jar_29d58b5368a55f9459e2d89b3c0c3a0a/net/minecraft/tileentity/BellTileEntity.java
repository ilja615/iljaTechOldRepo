package net.minecraft.tileentity;

import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableInt;

public class BellTileEntity extends TileEntity implements ITickableTileEntity {
   private long lastRingTimestamp;
   public int ticks;
   public boolean shaking;
   public Direction clickDirection;
   private List<LivingEntity> nearbyEntities;
   private boolean resonating;
   private int resonationTicks;

   public BellTileEntity() {
      super(TileEntityType.BELL);
   }

   public boolean triggerEvent(int p_145842_1_, int p_145842_2_) {
      if (p_145842_1_ == 1) {
         this.updateEntities();
         this.resonationTicks = 0;
         this.clickDirection = Direction.from3DDataValue(p_145842_2_);
         this.ticks = 0;
         this.shaking = true;
         return true;
      } else {
         return super.triggerEvent(p_145842_1_, p_145842_2_);
      }
   }

   public void tick() {
      if (this.shaking) {
         ++this.ticks;
      }

      if (this.ticks >= 50) {
         this.shaking = false;
         this.ticks = 0;
      }

      if (this.ticks >= 5 && this.resonationTicks == 0 && this.areRaidersNearby()) {
         this.resonating = true;
         this.playResonateSound();
      }

      if (this.resonating) {
         if (this.resonationTicks < 40) {
            ++this.resonationTicks;
         } else {
            this.makeRaidersGlow(this.level);
            this.showBellParticles(this.level);
            this.resonating = false;
         }
      }

   }

   private void playResonateSound() {
      this.level.playSound((PlayerEntity)null, this.getBlockPos(), SoundEvents.BELL_RESONATE, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }

   public void onHit(Direction p_213939_1_) {
      BlockPos blockpos = this.getBlockPos();
      this.clickDirection = p_213939_1_;
      if (this.shaking) {
         this.ticks = 0;
      } else {
         this.shaking = true;
      }

      this.level.blockEvent(blockpos, this.getBlockState().getBlock(), 1, p_213939_1_.get3DDataValue());
   }

   private void updateEntities() {
      BlockPos blockpos = this.getBlockPos();
      if (this.level.getGameTime() > this.lastRingTimestamp + 60L || this.nearbyEntities == null) {
         this.lastRingTimestamp = this.level.getGameTime();
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(blockpos)).inflate(48.0D);
         this.nearbyEntities = this.level.getEntitiesOfClass(LivingEntity.class, axisalignedbb);
      }

      if (!this.level.isClientSide) {
         for(LivingEntity livingentity : this.nearbyEntities) {
            if (livingentity.isAlive() && !livingentity.removed && blockpos.closerThan(livingentity.position(), 32.0D)) {
               livingentity.getBrain().setMemory(MemoryModuleType.HEARD_BELL_TIME, this.level.getGameTime());
            }
         }
      }

   }

   private boolean areRaidersNearby() {
      BlockPos blockpos = this.getBlockPos();

      for(LivingEntity livingentity : this.nearbyEntities) {
         if (livingentity.isAlive() && !livingentity.removed && blockpos.closerThan(livingentity.position(), 32.0D) && livingentity.getType().is(EntityTypeTags.RAIDERS)) {
            return true;
         }
      }

      return false;
   }

   private void makeRaidersGlow(World p_222828_1_) {
      if (!p_222828_1_.isClientSide) {
         this.nearbyEntities.stream().filter(this::isRaiderWithinRange).forEach(this::glow);
      }
   }

   private void showBellParticles(World p_222826_1_) {
      if (p_222826_1_.isClientSide) {
         BlockPos blockpos = this.getBlockPos();
         MutableInt mutableint = new MutableInt(16700985);
         int i = (int)this.nearbyEntities.stream().filter((p_222829_1_) -> {
            return blockpos.closerThan(p_222829_1_.position(), 48.0D);
         }).count();
         this.nearbyEntities.stream().filter(this::isRaiderWithinRange).forEach((p_235655_4_) -> {
            float f = 1.0F;
            float f1 = MathHelper.sqrt((p_235655_4_.getX() - (double)blockpos.getX()) * (p_235655_4_.getX() - (double)blockpos.getX()) + (p_235655_4_.getZ() - (double)blockpos.getZ()) * (p_235655_4_.getZ() - (double)blockpos.getZ()));
            double d0 = (double)((float)blockpos.getX() + 0.5F) + (double)(1.0F / f1) * (p_235655_4_.getX() - (double)blockpos.getX());
            double d1 = (double)((float)blockpos.getZ() + 0.5F) + (double)(1.0F / f1) * (p_235655_4_.getZ() - (double)blockpos.getZ());
            int j = MathHelper.clamp((i - 21) / -2, 3, 15);

            for(int k = 0; k < j; ++k) {
               int l = mutableint.addAndGet(5);
               double d2 = (double)ColorHelper.PackedColor.red(l) / 255.0D;
               double d3 = (double)ColorHelper.PackedColor.green(l) / 255.0D;
               double d4 = (double)ColorHelper.PackedColor.blue(l) / 255.0D;
               p_222826_1_.addParticle(ParticleTypes.ENTITY_EFFECT, d0, (double)((float)blockpos.getY() + 0.5F), d1, d2, d3, d4);
            }

         });
      }
   }

   private boolean isRaiderWithinRange(LivingEntity p_222832_1_) {
      return p_222832_1_.isAlive() && !p_222832_1_.removed && this.getBlockPos().closerThan(p_222832_1_.position(), 48.0D) && p_222832_1_.getType().is(EntityTypeTags.RAIDERS);
   }

   private void glow(LivingEntity p_222827_1_) {
      p_222827_1_.addEffect(new EffectInstance(Effects.GLOWING, 60));
   }
}
