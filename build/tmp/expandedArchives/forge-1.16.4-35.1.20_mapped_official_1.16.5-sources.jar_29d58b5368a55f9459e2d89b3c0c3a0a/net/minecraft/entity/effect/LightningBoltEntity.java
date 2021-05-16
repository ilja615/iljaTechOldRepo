package net.minecraft.entity.effect;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LightningBoltEntity extends Entity {
   private int life;
   public long seed;
   private int flashes;
   private boolean visualOnly;
   @Nullable
   private ServerPlayerEntity cause;

   public LightningBoltEntity(EntityType<? extends LightningBoltEntity> p_i231491_1_, World p_i231491_2_) {
      super(p_i231491_1_, p_i231491_2_);
      this.noCulling = true;
      this.life = 2;
      this.seed = this.random.nextLong();
      this.flashes = this.random.nextInt(3) + 1;
   }

   public void setVisualOnly(boolean p_233623_1_) {
      this.visualOnly = p_233623_1_;
   }

   public SoundCategory getSoundSource() {
      return SoundCategory.WEATHER;
   }

   public void setCause(@Nullable ServerPlayerEntity p_204809_1_) {
      this.cause = p_204809_1_;
   }

   public void tick() {
      super.tick();
      if (this.life == 2) {
         Difficulty difficulty = this.level.getDifficulty();
         if (difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD) {
            this.spawnFire(4);
         }

         this.level.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F);
         this.level.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F);
      }

      --this.life;
      if (this.life < 0) {
         if (this.flashes == 0) {
            this.remove();
         } else if (this.life < -this.random.nextInt(10)) {
            --this.flashes;
            this.life = 1;
            this.seed = this.random.nextLong();
            this.spawnFire(0);
         }
      }

      if (this.life >= 0) {
         if (!(this.level instanceof ServerWorld)) {
            this.level.setSkyFlashTime(2);
         } else if (!this.visualOnly) {
            double d0 = 3.0D;
            List<Entity> list = this.level.getEntities(this, new AxisAlignedBB(this.getX() - 3.0D, this.getY() - 3.0D, this.getZ() - 3.0D, this.getX() + 3.0D, this.getY() + 6.0D + 3.0D, this.getZ() + 3.0D), Entity::isAlive);

            for(Entity entity : list) {
               if (!net.minecraftforge.event.ForgeEventFactory.onEntityStruckByLightning(entity, this))
               entity.thunderHit((ServerWorld)this.level, this);
            }

            if (this.cause != null) {
               CriteriaTriggers.CHANNELED_LIGHTNING.trigger(this.cause, list);
            }
         }
      }

   }

   private void spawnFire(int p_195053_1_) {
      if (!this.visualOnly && !this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
         BlockPos blockpos = this.blockPosition();
         BlockState blockstate = AbstractFireBlock.getState(this.level, blockpos);
         if (this.level.getBlockState(blockpos).isAir() && blockstate.canSurvive(this.level, blockpos)) {
            this.level.setBlockAndUpdate(blockpos, blockstate);
         }

         for(int i = 0; i < p_195053_1_; ++i) {
            BlockPos blockpos1 = blockpos.offset(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);
            blockstate = AbstractFireBlock.getState(this.level, blockpos1);
            if (this.level.getBlockState(blockpos1).isAir() && blockstate.canSurvive(this.level, blockpos1)) {
               this.level.setBlockAndUpdate(blockpos1, blockstate);
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldRenderAtSqrDistance(double p_70112_1_) {
      double d0 = 64.0D * getViewScale();
      return p_70112_1_ < d0 * d0;
   }

   protected void defineSynchedData() {
   }

   protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {
   }

   protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {
   }

   public IPacket<?> getAddEntityPacket() {
      return new SSpawnObjectPacket(this);
   }
}
