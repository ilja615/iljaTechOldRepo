package net.minecraft.entity.monster;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ZombifiedPiglinEntity extends ZombieEntity implements IAngerable {
   private static final UUID SPEED_MODIFIER_ATTACKING_UUID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
   private static final AttributeModifier SPEED_MODIFIER_ATTACKING = new AttributeModifier(SPEED_MODIFIER_ATTACKING_UUID, "Attacking speed boost", 0.05D, AttributeModifier.Operation.ADDITION);
   private static final RangedInteger FIRST_ANGER_SOUND_DELAY = TickRangeConverter.rangeOfSeconds(0, 1);
   private int playFirstAngerSoundIn;
   private static final RangedInteger PERSISTENT_ANGER_TIME = TickRangeConverter.rangeOfSeconds(20, 39);
   private int remainingPersistentAngerTime;
   private UUID persistentAngerTarget;
   private static final RangedInteger ALERT_INTERVAL = TickRangeConverter.rangeOfSeconds(4, 6);
   private int ticksUntilNextAlert;

   public ZombifiedPiglinEntity(EntityType<? extends ZombifiedPiglinEntity> p_i231568_1_, World p_i231568_2_) {
      super(p_i231568_1_, p_i231568_2_);
      this.setPathfindingMalus(PathNodeType.LAVA, 8.0F);
   }

   public void setPersistentAngerTarget(@Nullable UUID p_230259_1_) {
      this.persistentAngerTarget = p_230259_1_;
   }

   public double getMyRidingOffset() {
      return this.isBaby() ? -0.05D : -0.45D;
   }

   protected void addBehaviourGoals() {
      this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::isAngryAt));
      this.targetSelector.addGoal(3, new ResetAngerGoal<>(this, true));
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return ZombieEntity.createAttributes().add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.0D).add(Attributes.MOVEMENT_SPEED, (double)0.23F).add(Attributes.ATTACK_DAMAGE, 5.0D);
   }

   protected boolean convertsInWater() {
      return false;
   }

   protected void customServerAiStep() {
      ModifiableAttributeInstance modifiableattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
      if (this.isAngry()) {
         if (!this.isBaby() && !modifiableattributeinstance.hasModifier(SPEED_MODIFIER_ATTACKING)) {
            modifiableattributeinstance.addTransientModifier(SPEED_MODIFIER_ATTACKING);
         }

         this.maybePlayFirstAngerSound();
      } else if (modifiableattributeinstance.hasModifier(SPEED_MODIFIER_ATTACKING)) {
         modifiableattributeinstance.removeModifier(SPEED_MODIFIER_ATTACKING);
      }

      this.updatePersistentAnger((ServerWorld)this.level, true);
      if (this.getTarget() != null) {
         this.maybeAlertOthers();
      }

      if (this.isAngry()) {
         this.lastHurtByPlayerTime = this.tickCount;
      }

      super.customServerAiStep();
   }

   private void maybePlayFirstAngerSound() {
      if (this.playFirstAngerSoundIn > 0) {
         --this.playFirstAngerSoundIn;
         if (this.playFirstAngerSoundIn == 0) {
            this.playAngerSound();
         }
      }

   }

   private void maybeAlertOthers() {
      if (this.ticksUntilNextAlert > 0) {
         --this.ticksUntilNextAlert;
      } else {
         if (this.getSensing().canSee(this.getTarget())) {
            this.alertOthers();
         }

         this.ticksUntilNextAlert = ALERT_INTERVAL.randomValue(this.random);
      }
   }

   private void alertOthers() {
      double d0 = this.getAttributeValue(Attributes.FOLLOW_RANGE);
      AxisAlignedBB axisalignedbb = AxisAlignedBB.unitCubeFromLowerCorner(this.position()).inflate(d0, 10.0D, d0);
      this.level.getLoadedEntitiesOfClass(ZombifiedPiglinEntity.class, axisalignedbb).stream().filter((p_241408_1_) -> {
         return p_241408_1_ != this;
      }).filter((p_241407_0_) -> {
         return p_241407_0_.getTarget() == null;
      }).filter((p_241406_1_) -> {
         return !p_241406_1_.isAlliedTo(this.getTarget());
      }).forEach((p_241405_1_) -> {
         p_241405_1_.setTarget(this.getTarget());
      });
   }

   private void playAngerSound() {
      this.playSound(SoundEvents.ZOMBIFIED_PIGLIN_ANGRY, this.getSoundVolume() * 2.0F, this.getVoicePitch() * 1.8F);
   }

   public void setTarget(@Nullable LivingEntity p_70624_1_) {
      if (this.getTarget() == null && p_70624_1_ != null) {
         this.playFirstAngerSoundIn = FIRST_ANGER_SOUND_DELAY.randomValue(this.random);
         this.ticksUntilNextAlert = ALERT_INTERVAL.randomValue(this.random);
      }

      if (p_70624_1_ instanceof PlayerEntity) {
         this.setLastHurtByPlayer((PlayerEntity)p_70624_1_);
      }

      super.setTarget(p_70624_1_);
   }

   public void startPersistentAngerTimer() {
      this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.randomValue(this.random));
   }

   public static boolean checkZombifiedPiglinSpawnRules(EntityType<ZombifiedPiglinEntity> p_234351_0_, IWorld p_234351_1_, SpawnReason p_234351_2_, BlockPos p_234351_3_, Random p_234351_4_) {
      return p_234351_1_.getDifficulty() != Difficulty.PEACEFUL && p_234351_1_.getBlockState(p_234351_3_.below()).getBlock() != Blocks.NETHER_WART_BLOCK;
   }

   public boolean checkSpawnObstruction(IWorldReader p_205019_1_) {
      return p_205019_1_.isUnobstructed(this) && !p_205019_1_.containsAnyLiquid(this.getBoundingBox());
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      this.addPersistentAngerSaveData(p_213281_1_);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.readPersistentAngerSaveData((ServerWorld)this.level, p_70037_1_);
   }

   public void setRemainingPersistentAngerTime(int p_230260_1_) {
      this.remainingPersistentAngerTime = p_230260_1_;
   }

   public int getRemainingPersistentAngerTime() {
      return this.remainingPersistentAngerTime;
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      return this.isInvulnerableTo(p_70097_1_) ? false : super.hurt(p_70097_1_, p_70097_2_);
   }

   protected SoundEvent getAmbientSound() {
      return this.isAngry() ? SoundEvents.ZOMBIFIED_PIGLIN_ANGRY : SoundEvents.ZOMBIFIED_PIGLIN_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ZOMBIFIED_PIGLIN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ZOMBIFIED_PIGLIN_DEATH;
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance p_180481_1_) {
      this.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
   }

   protected ItemStack getSkull() {
      return ItemStack.EMPTY;
   }

   protected void randomizeReinforcementsChance() {
      this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0.0D);
   }

   public UUID getPersistentAngerTarget() {
      return this.persistentAngerTarget;
   }

   public boolean isPreventingPlayerRest(PlayerEntity p_230292_1_) {
      return this.isAngryAt(p_230292_1_);
   }
}
