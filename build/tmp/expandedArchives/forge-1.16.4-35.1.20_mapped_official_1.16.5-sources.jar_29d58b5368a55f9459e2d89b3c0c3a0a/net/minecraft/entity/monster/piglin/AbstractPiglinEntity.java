package net.minecraft.entity.monster.piglin;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraft.item.TieredItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.GroundPathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractPiglinEntity extends MonsterEntity {
   protected static final DataParameter<Boolean> DATA_IMMUNE_TO_ZOMBIFICATION = EntityDataManager.defineId(AbstractPiglinEntity.class, DataSerializers.BOOLEAN);
   protected int timeInOverworld = 0;

   public AbstractPiglinEntity(EntityType<? extends AbstractPiglinEntity> p_i241915_1_, World p_i241915_2_) {
      super(p_i241915_1_, p_i241915_2_);
      this.setCanPickUpLoot(true);
      this.applyOpenDoorsAbility();
      this.setPathfindingMalus(PathNodeType.DANGER_FIRE, 16.0F);
      this.setPathfindingMalus(PathNodeType.DAMAGE_FIRE, -1.0F);
   }

   private void applyOpenDoorsAbility() {
      if (GroundPathHelper.hasGroundPathNavigation(this)) {
         ((GroundPathNavigator)this.getNavigation()).setCanOpenDoors(true);
      }

   }

   protected abstract boolean canHunt();

   public void setImmuneToZombification(boolean p_242340_1_) {
      this.getEntityData().set(DATA_IMMUNE_TO_ZOMBIFICATION, p_242340_1_);
   }

   protected boolean isImmuneToZombification() {
      return this.getEntityData().get(DATA_IMMUNE_TO_ZOMBIFICATION);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_IMMUNE_TO_ZOMBIFICATION, false);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      if (this.isImmuneToZombification()) {
         p_213281_1_.putBoolean("IsImmuneToZombification", true);
      }

      p_213281_1_.putInt("TimeInOverworld", this.timeInOverworld);
   }

   public double getMyRidingOffset() {
      return this.isBaby() ? -0.05D : -0.45D;
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.setImmuneToZombification(p_70037_1_.getBoolean("IsImmuneToZombification"));
      this.timeInOverworld = p_70037_1_.getInt("TimeInOverworld");
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      if (this.isConverting()) {
         ++this.timeInOverworld;
      } else {
         this.timeInOverworld = 0;
      }

      if (this.timeInOverworld > 300 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, EntityType.ZOMBIFIED_PIGLIN, (timer) -> this.timeInOverworld = timer)) {
         this.playConvertedSound();
         this.finishConversion((ServerWorld)this.level);
      }

   }

   public boolean isConverting() {
      return !this.level.dimensionType().piglinSafe() && !this.isImmuneToZombification() && !this.isNoAi();
   }

   protected void finishConversion(ServerWorld p_234416_1_) {
      ZombifiedPiglinEntity zombifiedpiglinentity = this.convertTo(EntityType.ZOMBIFIED_PIGLIN, true);
      if (zombifiedpiglinentity != null) {
         zombifiedpiglinentity.addEffect(new EffectInstance(Effects.CONFUSION, 200, 0));
         net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, zombifiedpiglinentity);
      }

   }

   public boolean isAdult() {
      return !this.isBaby();
   }

   @OnlyIn(Dist.CLIENT)
   public abstract PiglinAction getArmPose();

   @Nullable
   public LivingEntity getTarget() {
      return this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).orElse((LivingEntity)null);
   }

   protected boolean isHoldingMeleeWeapon() {
      return this.getMainHandItem().getItem() instanceof TieredItem;
   }

   public void playAmbientSound() {
      if (PiglinTasks.isIdle(this)) {
         super.playAmbientSound();
      }

   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPacketSender.sendEntityBrain(this);
   }

   protected abstract void playConvertedSound();
}
