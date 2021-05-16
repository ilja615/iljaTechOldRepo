package net.minecraft.entity.monster;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class SpellcastingIllagerEntity extends AbstractIllagerEntity {
   private static final DataParameter<Byte> DATA_SPELL_CASTING_ID = EntityDataManager.defineId(SpellcastingIllagerEntity.class, DataSerializers.BYTE);
   protected int spellCastingTickCount;
   private SpellcastingIllagerEntity.SpellType currentSpell = SpellcastingIllagerEntity.SpellType.NONE;

   protected SpellcastingIllagerEntity(EntityType<? extends SpellcastingIllagerEntity> p_i48551_1_, World p_i48551_2_) {
      super(p_i48551_1_, p_i48551_2_);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_SPELL_CASTING_ID, (byte)0);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.spellCastingTickCount = p_70037_1_.getInt("SpellTicks");
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("SpellTicks", this.spellCastingTickCount);
   }

   @OnlyIn(Dist.CLIENT)
   public AbstractIllagerEntity.ArmPose getArmPose() {
      if (this.isCastingSpell()) {
         return AbstractIllagerEntity.ArmPose.SPELLCASTING;
      } else {
         return this.isCelebrating() ? AbstractIllagerEntity.ArmPose.CELEBRATING : AbstractIllagerEntity.ArmPose.CROSSED;
      }
   }

   public boolean isCastingSpell() {
      if (this.level.isClientSide) {
         return this.entityData.get(DATA_SPELL_CASTING_ID) > 0;
      } else {
         return this.spellCastingTickCount > 0;
      }
   }

   public void setIsCastingSpell(SpellcastingIllagerEntity.SpellType p_193081_1_) {
      this.currentSpell = p_193081_1_;
      this.entityData.set(DATA_SPELL_CASTING_ID, (byte)p_193081_1_.id);
   }

   protected SpellcastingIllagerEntity.SpellType getCurrentSpell() {
      return !this.level.isClientSide ? this.currentSpell : SpellcastingIllagerEntity.SpellType.byId(this.entityData.get(DATA_SPELL_CASTING_ID));
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      if (this.spellCastingTickCount > 0) {
         --this.spellCastingTickCount;
      }

   }

   public void tick() {
      super.tick();
      if (this.level.isClientSide && this.isCastingSpell()) {
         SpellcastingIllagerEntity.SpellType spellcastingillagerentity$spelltype = this.getCurrentSpell();
         double d0 = spellcastingillagerentity$spelltype.spellColor[0];
         double d1 = spellcastingillagerentity$spelltype.spellColor[1];
         double d2 = spellcastingillagerentity$spelltype.spellColor[2];
         float f = this.yBodyRot * ((float)Math.PI / 180F) + MathHelper.cos((float)this.tickCount * 0.6662F) * 0.25F;
         float f1 = MathHelper.cos(f);
         float f2 = MathHelper.sin(f);
         this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + (double)f1 * 0.6D, this.getY() + 1.8D, this.getZ() + (double)f2 * 0.6D, d0, d1, d2);
         this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() - (double)f1 * 0.6D, this.getY() + 1.8D, this.getZ() - (double)f2 * 0.6D, d0, d1, d2);
      }

   }

   protected int getSpellCastingTime() {
      return this.spellCastingTickCount;
   }

   protected abstract SoundEvent getCastingSoundEvent();

   public class CastingASpellGoal extends Goal {
      public CastingASpellGoal() {
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         return SpellcastingIllagerEntity.this.getSpellCastingTime() > 0;
      }

      public void start() {
         super.start();
         SpellcastingIllagerEntity.this.navigation.stop();
      }

      public void stop() {
         super.stop();
         SpellcastingIllagerEntity.this.setIsCastingSpell(SpellcastingIllagerEntity.SpellType.NONE);
      }

      public void tick() {
         if (SpellcastingIllagerEntity.this.getTarget() != null) {
            SpellcastingIllagerEntity.this.getLookControl().setLookAt(SpellcastingIllagerEntity.this.getTarget(), (float)SpellcastingIllagerEntity.this.getMaxHeadYRot(), (float)SpellcastingIllagerEntity.this.getMaxHeadXRot());
         }

      }
   }

   public static enum SpellType {
      NONE(0, 0.0D, 0.0D, 0.0D),
      SUMMON_VEX(1, 0.7D, 0.7D, 0.8D),
      FANGS(2, 0.4D, 0.3D, 0.35D),
      WOLOLO(3, 0.7D, 0.5D, 0.2D),
      DISAPPEAR(4, 0.3D, 0.3D, 0.8D),
      BLINDNESS(5, 0.1D, 0.1D, 0.2D);

      private final int id;
      private final double[] spellColor;

      private SpellType(int p_i47561_3_, double p_i47561_4_, double p_i47561_6_, double p_i47561_8_) {
         this.id = p_i47561_3_;
         this.spellColor = new double[]{p_i47561_4_, p_i47561_6_, p_i47561_8_};
      }

      public static SpellcastingIllagerEntity.SpellType byId(int p_193337_0_) {
         for(SpellcastingIllagerEntity.SpellType spellcastingillagerentity$spelltype : values()) {
            if (p_193337_0_ == spellcastingillagerentity$spelltype.id) {
               return spellcastingillagerentity$spelltype;
            }
         }

         return NONE;
      }
   }

   public abstract class UseSpellGoal extends Goal {
      protected int attackWarmupDelay;
      protected int nextAttackTickCount;

      protected UseSpellGoal() {
      }

      public boolean canUse() {
         LivingEntity livingentity = SpellcastingIllagerEntity.this.getTarget();
         if (livingentity != null && livingentity.isAlive()) {
            if (SpellcastingIllagerEntity.this.isCastingSpell()) {
               return false;
            } else {
               return SpellcastingIllagerEntity.this.tickCount >= this.nextAttackTickCount;
            }
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         LivingEntity livingentity = SpellcastingIllagerEntity.this.getTarget();
         return livingentity != null && livingentity.isAlive() && this.attackWarmupDelay > 0;
      }

      public void start() {
         this.attackWarmupDelay = this.getCastWarmupTime();
         SpellcastingIllagerEntity.this.spellCastingTickCount = this.getCastingTime();
         this.nextAttackTickCount = SpellcastingIllagerEntity.this.tickCount + this.getCastingInterval();
         SoundEvent soundevent = this.getSpellPrepareSound();
         if (soundevent != null) {
            SpellcastingIllagerEntity.this.playSound(soundevent, 1.0F, 1.0F);
         }

         SpellcastingIllagerEntity.this.setIsCastingSpell(this.getSpell());
      }

      public void tick() {
         --this.attackWarmupDelay;
         if (this.attackWarmupDelay == 0) {
            this.performSpellCasting();
            SpellcastingIllagerEntity.this.playSound(SpellcastingIllagerEntity.this.getCastingSoundEvent(), 1.0F, 1.0F);
         }

      }

      protected abstract void performSpellCasting();

      protected int getCastWarmupTime() {
         return 20;
      }

      protected abstract int getCastingTime();

      protected abstract int getCastingInterval();

      @Nullable
      protected abstract SoundEvent getSpellPrepareSound();

      protected abstract SpellcastingIllagerEntity.SpellType getSpell();
   }
}
