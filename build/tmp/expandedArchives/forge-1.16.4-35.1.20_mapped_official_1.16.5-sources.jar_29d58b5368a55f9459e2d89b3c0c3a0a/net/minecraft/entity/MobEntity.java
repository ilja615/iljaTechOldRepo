package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShootableItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.loot.LootContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SMountEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.ITag;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class MobEntity extends LivingEntity {
   private static final DataParameter<Byte> DATA_MOB_FLAGS_ID = EntityDataManager.defineId(MobEntity.class, DataSerializers.BYTE);
   public int ambientSoundTime;
   protected int xpReward;
   protected LookController lookControl;
   protected MovementController moveControl;
   protected JumpController jumpControl;
   private final BodyController bodyRotationControl;
   protected PathNavigator navigation;
   public final GoalSelector goalSelector;
   public final GoalSelector targetSelector;
   private LivingEntity target;
   private final EntitySenses sensing;
   private final NonNullList<ItemStack> handItems = NonNullList.withSize(2, ItemStack.EMPTY);
   protected final float[] handDropChances = new float[2];
   private final NonNullList<ItemStack> armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
   protected final float[] armorDropChances = new float[4];
   private boolean canPickUpLoot;
   private boolean persistenceRequired;
   private final Map<PathNodeType, Float> pathfindingMalus = Maps.newEnumMap(PathNodeType.class);
   private ResourceLocation lootTable;
   private long lootTableSeed;
   @Nullable
   private Entity leashHolder;
   private int delayedLeashHolderId;
   @Nullable
   private CompoundNBT leashInfoTag;
   private BlockPos restrictCenter = BlockPos.ZERO;
   private float restrictRadius = -1.0F;

   protected MobEntity(EntityType<? extends MobEntity> p_i48576_1_, World p_i48576_2_) {
      super(p_i48576_1_, p_i48576_2_);
      this.goalSelector = new GoalSelector(p_i48576_2_.getProfilerSupplier());
      this.targetSelector = new GoalSelector(p_i48576_2_.getProfilerSupplier());
      this.lookControl = new LookController(this);
      this.moveControl = new MovementController(this);
      this.jumpControl = new JumpController(this);
      this.bodyRotationControl = this.createBodyControl();
      this.navigation = this.createNavigation(p_i48576_2_);
      this.sensing = new EntitySenses(this);
      Arrays.fill(this.armorDropChances, 0.085F);
      Arrays.fill(this.handDropChances, 0.085F);
      if (p_i48576_2_ != null && !p_i48576_2_.isClientSide) {
         this.registerGoals();
      }

   }

   protected void registerGoals() {
   }

   public static AttributeModifierMap.MutableAttribute createMobAttributes() {
      return LivingEntity.createLivingAttributes().add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.ATTACK_KNOCKBACK);
   }

   protected PathNavigator createNavigation(World p_175447_1_) {
      return new GroundPathNavigator(this, p_175447_1_);
   }

   protected boolean shouldPassengersInheritMalus() {
      return false;
   }

   public float getPathfindingMalus(PathNodeType p_184643_1_) {
      MobEntity mobentity;
      if (this.getVehicle() instanceof MobEntity && ((MobEntity)this.getVehicle()).shouldPassengersInheritMalus()) {
         mobentity = (MobEntity)this.getVehicle();
      } else {
         mobentity = this;
      }

      Float f = mobentity.pathfindingMalus.get(p_184643_1_);
      return f == null ? p_184643_1_.getMalus() : f;
   }

   public void setPathfindingMalus(PathNodeType p_184644_1_, float p_184644_2_) {
      this.pathfindingMalus.put(p_184644_1_, p_184644_2_);
   }

   public boolean canCutCorner(PathNodeType p_233660_1_) {
      return p_233660_1_ != PathNodeType.DANGER_FIRE && p_233660_1_ != PathNodeType.DANGER_CACTUS && p_233660_1_ != PathNodeType.DANGER_OTHER && p_233660_1_ != PathNodeType.WALKABLE_DOOR;
   }

   protected BodyController createBodyControl() {
      return new BodyController(this);
   }

   public LookController getLookControl() {
      return this.lookControl;
   }

   public MovementController getMoveControl() {
      if (this.isPassenger() && this.getVehicle() instanceof MobEntity) {
         MobEntity mobentity = (MobEntity)this.getVehicle();
         return mobentity.getMoveControl();
      } else {
         return this.moveControl;
      }
   }

   public JumpController getJumpControl() {
      return this.jumpControl;
   }

   public PathNavigator getNavigation() {
      if (this.isPassenger() && this.getVehicle() instanceof MobEntity) {
         MobEntity mobentity = (MobEntity)this.getVehicle();
         return mobentity.getNavigation();
      } else {
         return this.navigation;
      }
   }

   public EntitySenses getSensing() {
      return this.sensing;
   }

   @Nullable
   public LivingEntity getTarget() {
      return this.target;
   }

   public void setTarget(@Nullable LivingEntity p_70624_1_) {
      this.target = p_70624_1_;
      net.minecraftforge.common.ForgeHooks.onLivingSetAttackTarget(this, p_70624_1_);
   }

   public boolean canAttackType(EntityType<?> p_213358_1_) {
      return p_213358_1_ != EntityType.GHAST;
   }

   public boolean canFireProjectileWeapon(ShootableItem p_230280_1_) {
      return false;
   }

   public void ate() {
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_MOB_FLAGS_ID, (byte)0);
   }

   public int getAmbientSoundInterval() {
      return 80;
   }

   public void playAmbientSound() {
      SoundEvent soundevent = this.getAmbientSound();
      if (soundevent != null) {
         this.playSound(soundevent, this.getSoundVolume(), this.getVoicePitch());
      }

   }

   public void baseTick() {
      super.baseTick();
      this.level.getProfiler().push("mobBaseTick");
      if (this.isAlive() && this.random.nextInt(1000) < this.ambientSoundTime++) {
         this.resetAmbientSoundTime();
         this.playAmbientSound();
      }

      this.level.getProfiler().pop();
   }

   protected void playHurtSound(DamageSource p_184581_1_) {
      this.resetAmbientSoundTime();
      super.playHurtSound(p_184581_1_);
   }

   private void resetAmbientSoundTime() {
      this.ambientSoundTime = -this.getAmbientSoundInterval();
   }

   protected int getExperienceReward(PlayerEntity p_70693_1_) {
      if (this.xpReward > 0) {
         int i = this.xpReward;

         for(int j = 0; j < this.armorItems.size(); ++j) {
            if (!this.armorItems.get(j).isEmpty() && this.armorDropChances[j] <= 1.0F) {
               i += 1 + this.random.nextInt(3);
            }
         }

         for(int k = 0; k < this.handItems.size(); ++k) {
            if (!this.handItems.get(k).isEmpty() && this.handDropChances[k] <= 1.0F) {
               i += 1 + this.random.nextInt(3);
            }
         }

         return i;
      } else {
         return this.xpReward;
      }
   }

   public void spawnAnim() {
      if (this.level.isClientSide) {
         for(int i = 0; i < 20; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            double d3 = 10.0D;
            this.level.addParticle(ParticleTypes.POOF, this.getX(1.0D) - d0 * 10.0D, this.getRandomY() - d1 * 10.0D, this.getRandomZ(1.0D) - d2 * 10.0D, d0, d1, d2);
         }
      } else {
         this.level.broadcastEntityEvent(this, (byte)20);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 20) {
         this.spawnAnim();
      } else {
         super.handleEntityEvent(p_70103_1_);
      }

   }

   public void tick() {
      super.tick();
      if (!this.level.isClientSide) {
         this.tickLeash();
         if (this.tickCount % 5 == 0) {
            this.updateControlFlags();
         }
      }

   }

   protected void updateControlFlags() {
      boolean flag = !(this.getControllingPassenger() instanceof MobEntity);
      boolean flag1 = !(this.getVehicle() instanceof BoatEntity);
      this.goalSelector.setControlFlag(Goal.Flag.MOVE, flag);
      this.goalSelector.setControlFlag(Goal.Flag.JUMP, flag && flag1);
      this.goalSelector.setControlFlag(Goal.Flag.LOOK, flag);
   }

   protected float tickHeadTurn(float p_110146_1_, float p_110146_2_) {
      this.bodyRotationControl.clientTick();
      return p_110146_2_;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return null;
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putBoolean("CanPickUpLoot", this.canPickUpLoot());
      p_213281_1_.putBoolean("PersistenceRequired", this.persistenceRequired);
      ListNBT listnbt = new ListNBT();

      for(ItemStack itemstack : this.armorItems) {
         CompoundNBT compoundnbt = new CompoundNBT();
         if (!itemstack.isEmpty()) {
            itemstack.save(compoundnbt);
         }

         listnbt.add(compoundnbt);
      }

      p_213281_1_.put("ArmorItems", listnbt);
      ListNBT listnbt1 = new ListNBT();

      for(ItemStack itemstack1 : this.handItems) {
         CompoundNBT compoundnbt1 = new CompoundNBT();
         if (!itemstack1.isEmpty()) {
            itemstack1.save(compoundnbt1);
         }

         listnbt1.add(compoundnbt1);
      }

      p_213281_1_.put("HandItems", listnbt1);
      ListNBT listnbt2 = new ListNBT();

      for(float f : this.armorDropChances) {
         listnbt2.add(FloatNBT.valueOf(f));
      }

      p_213281_1_.put("ArmorDropChances", listnbt2);
      ListNBT listnbt3 = new ListNBT();

      for(float f1 : this.handDropChances) {
         listnbt3.add(FloatNBT.valueOf(f1));
      }

      p_213281_1_.put("HandDropChances", listnbt3);
      if (this.leashHolder != null) {
         CompoundNBT compoundnbt2 = new CompoundNBT();
         if (this.leashHolder instanceof LivingEntity) {
            UUID uuid = this.leashHolder.getUUID();
            compoundnbt2.putUUID("UUID", uuid);
         } else if (this.leashHolder instanceof HangingEntity) {
            BlockPos blockpos = ((HangingEntity)this.leashHolder).getPos();
            compoundnbt2.putInt("X", blockpos.getX());
            compoundnbt2.putInt("Y", blockpos.getY());
            compoundnbt2.putInt("Z", blockpos.getZ());
         }

         p_213281_1_.put("Leash", compoundnbt2);
      } else if (this.leashInfoTag != null) {
         p_213281_1_.put("Leash", this.leashInfoTag.copy());
      }

      p_213281_1_.putBoolean("LeftHanded", this.isLeftHanded());
      if (this.lootTable != null) {
         p_213281_1_.putString("DeathLootTable", this.lootTable.toString());
         if (this.lootTableSeed != 0L) {
            p_213281_1_.putLong("DeathLootTableSeed", this.lootTableSeed);
         }
      }

      if (this.isNoAi()) {
         p_213281_1_.putBoolean("NoAI", this.isNoAi());
      }

   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      if (p_70037_1_.contains("CanPickUpLoot", 1)) {
         this.setCanPickUpLoot(p_70037_1_.getBoolean("CanPickUpLoot"));
      }

      this.persistenceRequired = p_70037_1_.getBoolean("PersistenceRequired");
      if (p_70037_1_.contains("ArmorItems", 9)) {
         ListNBT listnbt = p_70037_1_.getList("ArmorItems", 10);

         for(int i = 0; i < this.armorItems.size(); ++i) {
            this.armorItems.set(i, ItemStack.of(listnbt.getCompound(i)));
         }
      }

      if (p_70037_1_.contains("HandItems", 9)) {
         ListNBT listnbt1 = p_70037_1_.getList("HandItems", 10);

         for(int j = 0; j < this.handItems.size(); ++j) {
            this.handItems.set(j, ItemStack.of(listnbt1.getCompound(j)));
         }
      }

      if (p_70037_1_.contains("ArmorDropChances", 9)) {
         ListNBT listnbt2 = p_70037_1_.getList("ArmorDropChances", 5);

         for(int k = 0; k < listnbt2.size(); ++k) {
            this.armorDropChances[k] = listnbt2.getFloat(k);
         }
      }

      if (p_70037_1_.contains("HandDropChances", 9)) {
         ListNBT listnbt3 = p_70037_1_.getList("HandDropChances", 5);

         for(int l = 0; l < listnbt3.size(); ++l) {
            this.handDropChances[l] = listnbt3.getFloat(l);
         }
      }

      if (p_70037_1_.contains("Leash", 10)) {
         this.leashInfoTag = p_70037_1_.getCompound("Leash");
      }

      this.setLeftHanded(p_70037_1_.getBoolean("LeftHanded"));
      if (p_70037_1_.contains("DeathLootTable", 8)) {
         this.lootTable = new ResourceLocation(p_70037_1_.getString("DeathLootTable"));
         this.lootTableSeed = p_70037_1_.getLong("DeathLootTableSeed");
      }

      this.setNoAi(p_70037_1_.getBoolean("NoAI"));
   }

   protected void dropFromLootTable(DamageSource p_213354_1_, boolean p_213354_2_) {
      super.dropFromLootTable(p_213354_1_, p_213354_2_);
      this.lootTable = null;
   }

   protected LootContext.Builder createLootContext(boolean p_213363_1_, DamageSource p_213363_2_) {
      return super.createLootContext(p_213363_1_, p_213363_2_).withOptionalRandomSeed(this.lootTableSeed, this.random);
   }

   public final ResourceLocation getLootTable() {
      return this.lootTable == null ? this.getDefaultLootTable() : this.lootTable;
   }

   protected ResourceLocation getDefaultLootTable() {
      return super.getLootTable();
   }

   public void setZza(float p_191989_1_) {
      this.zza = p_191989_1_;
   }

   public void setYya(float p_70657_1_) {
      this.yya = p_70657_1_;
   }

   public void setXxa(float p_184646_1_) {
      this.xxa = p_184646_1_;
   }

   public void setSpeed(float p_70659_1_) {
      super.setSpeed(p_70659_1_);
      this.setZza(p_70659_1_);
   }

   public void aiStep() {
      super.aiStep();
      this.level.getProfiler().push("looting");
      if (!this.level.isClientSide && this.canPickUpLoot() && this.isAlive() && !this.dead && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this)) {
         for(ItemEntity itementity : this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(1.0D, 0.0D, 1.0D))) {
            if (!itementity.removed && !itementity.getItem().isEmpty() && !itementity.hasPickUpDelay() && this.wantsToPickUp(itementity.getItem())) {
               this.pickUpItem(itementity);
            }
         }
      }

      this.level.getProfiler().pop();
   }

   protected void pickUpItem(ItemEntity p_175445_1_) {
      ItemStack itemstack = p_175445_1_.getItem();
      if (this.equipItemIfPossible(itemstack)) {
         this.onItemPickup(p_175445_1_);
         this.take(p_175445_1_, itemstack.getCount());
         p_175445_1_.remove();
      }

   }

   public boolean equipItemIfPossible(ItemStack p_233665_1_) {
      EquipmentSlotType equipmentslottype = getEquipmentSlotForItem(p_233665_1_);
      ItemStack itemstack = this.getItemBySlot(equipmentslottype);
      boolean flag = this.canReplaceCurrentItem(p_233665_1_, itemstack);
      if (flag && this.canHoldItem(p_233665_1_)) {
         double d0 = (double)this.getEquipmentDropChance(equipmentslottype);
         if (!itemstack.isEmpty() && (double)Math.max(this.random.nextFloat() - 0.1F, 0.0F) < d0) {
            this.spawnAtLocation(itemstack);
         }

         this.setItemSlotAndDropWhenKilled(equipmentslottype, p_233665_1_);
         this.playEquipSound(p_233665_1_);
         return true;
      } else {
         return false;
      }
   }

   protected void setItemSlotAndDropWhenKilled(EquipmentSlotType p_233657_1_, ItemStack p_233657_2_) {
      this.setItemSlot(p_233657_1_, p_233657_2_);
      this.setGuaranteedDrop(p_233657_1_);
      this.persistenceRequired = true;
   }

   public void setGuaranteedDrop(EquipmentSlotType p_233663_1_) {
      switch(p_233663_1_.getType()) {
      case HAND:
         this.handDropChances[p_233663_1_.getIndex()] = 2.0F;
         break;
      case ARMOR:
         this.armorDropChances[p_233663_1_.getIndex()] = 2.0F;
      }

   }

   protected boolean canReplaceCurrentItem(ItemStack p_208003_1_, ItemStack p_208003_2_) {
      if (p_208003_2_.isEmpty()) {
         return true;
      } else if (p_208003_1_.getItem() instanceof SwordItem) {
         if (!(p_208003_2_.getItem() instanceof SwordItem)) {
            return true;
         } else {
            SwordItem sworditem = (SwordItem)p_208003_1_.getItem();
            SwordItem sworditem1 = (SwordItem)p_208003_2_.getItem();
            if (sworditem.getDamage() != sworditem1.getDamage()) {
               return sworditem.getDamage() > sworditem1.getDamage();
            } else {
               return this.canReplaceEqualItem(p_208003_1_, p_208003_2_);
            }
         }
      } else if (p_208003_1_.getItem() instanceof BowItem && p_208003_2_.getItem() instanceof BowItem) {
         return this.canReplaceEqualItem(p_208003_1_, p_208003_2_);
      } else if (p_208003_1_.getItem() instanceof CrossbowItem && p_208003_2_.getItem() instanceof CrossbowItem) {
         return this.canReplaceEqualItem(p_208003_1_, p_208003_2_);
      } else if (p_208003_1_.getItem() instanceof ArmorItem) {
         if (EnchantmentHelper.hasBindingCurse(p_208003_2_)) {
            return false;
         } else if (!(p_208003_2_.getItem() instanceof ArmorItem)) {
            return true;
         } else {
            ArmorItem armoritem = (ArmorItem)p_208003_1_.getItem();
            ArmorItem armoritem1 = (ArmorItem)p_208003_2_.getItem();
            if (armoritem.getDefense() != armoritem1.getDefense()) {
               return armoritem.getDefense() > armoritem1.getDefense();
            } else if (armoritem.getToughness() != armoritem1.getToughness()) {
               return armoritem.getToughness() > armoritem1.getToughness();
            } else {
               return this.canReplaceEqualItem(p_208003_1_, p_208003_2_);
            }
         }
      } else {
         if (p_208003_1_.getItem() instanceof ToolItem) {
            if (p_208003_2_.getItem() instanceof BlockItem) {
               return true;
            }

            if (p_208003_2_.getItem() instanceof ToolItem) {
               ToolItem toolitem = (ToolItem)p_208003_1_.getItem();
               ToolItem toolitem1 = (ToolItem)p_208003_2_.getItem();
               if (toolitem.getAttackDamage() != toolitem1.getAttackDamage()) {
                  return toolitem.getAttackDamage() > toolitem1.getAttackDamage();
               }

               return this.canReplaceEqualItem(p_208003_1_, p_208003_2_);
            }
         }

         return false;
      }
   }

   public boolean canReplaceEqualItem(ItemStack p_233659_1_, ItemStack p_233659_2_) {
      if (p_233659_1_.getDamageValue() >= p_233659_2_.getDamageValue() && (!p_233659_1_.hasTag() || p_233659_2_.hasTag())) {
         if (p_233659_1_.hasTag() && p_233659_2_.hasTag()) {
            return p_233659_1_.getTag().getAllKeys().stream().anyMatch((p_233664_0_) -> {
               return !p_233664_0_.equals("Damage");
            }) && !p_233659_2_.getTag().getAllKeys().stream().anyMatch((p_233662_0_) -> {
               return !p_233662_0_.equals("Damage");
            });
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public boolean canHoldItem(ItemStack p_175448_1_) {
      return true;
   }

   public boolean wantsToPickUp(ItemStack p_230293_1_) {
      return this.canHoldItem(p_230293_1_);
   }

   public boolean removeWhenFarAway(double p_213397_1_) {
      return true;
   }

   public boolean requiresCustomPersistence() {
      return this.isPassenger();
   }

   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   public void checkDespawn() {
      if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
         this.remove();
      } else if (!this.isPersistenceRequired() && !this.requiresCustomPersistence()) {
         Entity entity = this.level.getNearestPlayer(this, -1.0D);
         net.minecraftforge.eventbus.api.Event.Result result = net.minecraftforge.event.ForgeEventFactory.canEntityDespawn(this);
         if (result == net.minecraftforge.eventbus.api.Event.Result.DENY) {
            noActionTime = 0;
            entity = null;
         } else if (result == net.minecraftforge.eventbus.api.Event.Result.ALLOW) {
            this.remove();
            entity = null;
         }
         if (entity != null) {
            double d0 = entity.distanceToSqr(this);
            int i = this.getType().getCategory().getDespawnDistance();
            int j = i * i;
            if (d0 > (double)j && this.removeWhenFarAway(d0)) {
               this.remove();
            }

            int k = this.getType().getCategory().getNoDespawnDistance();
            int l = k * k;
            if (this.noActionTime > 600 && this.random.nextInt(800) == 0 && d0 > (double)l && this.removeWhenFarAway(d0)) {
               this.remove();
            } else if (d0 < (double)l) {
               this.noActionTime = 0;
            }
         }

      } else {
         this.noActionTime = 0;
      }
   }

   protected final void serverAiStep() {
      ++this.noActionTime;
      this.level.getProfiler().push("sensing");
      this.sensing.tick();
      this.level.getProfiler().pop();
      this.level.getProfiler().push("targetSelector");
      this.targetSelector.tick();
      this.level.getProfiler().pop();
      this.level.getProfiler().push("goalSelector");
      this.goalSelector.tick();
      this.level.getProfiler().pop();
      this.level.getProfiler().push("navigation");
      this.navigation.tick();
      this.level.getProfiler().pop();
      this.level.getProfiler().push("mob tick");
      this.customServerAiStep();
      this.level.getProfiler().pop();
      this.level.getProfiler().push("controls");
      this.level.getProfiler().push("move");
      this.moveControl.tick();
      this.level.getProfiler().popPush("look");
      this.lookControl.tick();
      this.level.getProfiler().popPush("jump");
      this.jumpControl.tick();
      this.level.getProfiler().pop();
      this.level.getProfiler().pop();
      this.sendDebugPackets();
   }

   protected void sendDebugPackets() {
      DebugPacketSender.sendGoalSelector(this.level, this, this.goalSelector);
   }

   protected void customServerAiStep() {
   }

   public int getMaxHeadXRot() {
      return 40;
   }

   public int getMaxHeadYRot() {
      return 75;
   }

   public int getHeadRotSpeed() {
      return 10;
   }

   public void lookAt(Entity p_70625_1_, float p_70625_2_, float p_70625_3_) {
      double d0 = p_70625_1_.getX() - this.getX();
      double d2 = p_70625_1_.getZ() - this.getZ();
      double d1;
      if (p_70625_1_ instanceof LivingEntity) {
         LivingEntity livingentity = (LivingEntity)p_70625_1_;
         d1 = livingentity.getEyeY() - this.getEyeY();
      } else {
         d1 = (p_70625_1_.getBoundingBox().minY + p_70625_1_.getBoundingBox().maxY) / 2.0D - this.getEyeY();
      }

      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      float f = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
      float f1 = (float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI)));
      this.xRot = this.rotlerp(this.xRot, f1, p_70625_3_);
      this.yRot = this.rotlerp(this.yRot, f, p_70625_2_);
   }

   private float rotlerp(float p_70663_1_, float p_70663_2_, float p_70663_3_) {
      float f = MathHelper.wrapDegrees(p_70663_2_ - p_70663_1_);
      if (f > p_70663_3_) {
         f = p_70663_3_;
      }

      if (f < -p_70663_3_) {
         f = -p_70663_3_;
      }

      return p_70663_1_ + f;
   }

   public static boolean checkMobSpawnRules(EntityType<? extends MobEntity> p_223315_0_, IWorld p_223315_1_, SpawnReason p_223315_2_, BlockPos p_223315_3_, Random p_223315_4_) {
      BlockPos blockpos = p_223315_3_.below();
      return p_223315_2_ == SpawnReason.SPAWNER || p_223315_1_.getBlockState(blockpos).isValidSpawn(p_223315_1_, blockpos, p_223315_0_);
   }

   public boolean checkSpawnRules(IWorld p_213380_1_, SpawnReason p_213380_2_) {
      return true;
   }

   public boolean checkSpawnObstruction(IWorldReader p_205019_1_) {
      return !p_205019_1_.containsAnyLiquid(this.getBoundingBox()) && p_205019_1_.isUnobstructed(this);
   }

   public int getMaxSpawnClusterSize() {
      return 4;
   }

   public boolean isMaxGroupSizeReached(int p_204209_1_) {
      return false;
   }

   public int getMaxFallDistance() {
      if (this.getTarget() == null) {
         return 3;
      } else {
         int i = (int)(this.getHealth() - this.getMaxHealth() * 0.33F);
         i = i - (3 - this.level.getDifficulty().getId()) * 4;
         if (i < 0) {
            i = 0;
         }

         return i + 3;
      }
   }

   public Iterable<ItemStack> getHandSlots() {
      return this.handItems;
   }

   public Iterable<ItemStack> getArmorSlots() {
      return this.armorItems;
   }

   public ItemStack getItemBySlot(EquipmentSlotType p_184582_1_) {
      switch(p_184582_1_.getType()) {
      case HAND:
         return this.handItems.get(p_184582_1_.getIndex());
      case ARMOR:
         return this.armorItems.get(p_184582_1_.getIndex());
      default:
         return ItemStack.EMPTY;
      }
   }

   public void setItemSlot(EquipmentSlotType p_184201_1_, ItemStack p_184201_2_) {
      switch(p_184201_1_.getType()) {
      case HAND:
         this.handItems.set(p_184201_1_.getIndex(), p_184201_2_);
         break;
      case ARMOR:
         this.armorItems.set(p_184201_1_.getIndex(), p_184201_2_);
      }

   }

   protected void dropCustomDeathLoot(DamageSource p_213333_1_, int p_213333_2_, boolean p_213333_3_) {
      super.dropCustomDeathLoot(p_213333_1_, p_213333_2_, p_213333_3_);

      for(EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
         ItemStack itemstack = this.getItemBySlot(equipmentslottype);
         float f = this.getEquipmentDropChance(equipmentslottype);
         boolean flag = f > 1.0F;
         if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack) && (p_213333_3_ || flag) && Math.max(this.random.nextFloat() - (float)p_213333_2_ * 0.01F, 0.0F) < f) {
            if (!flag && itemstack.isDamageableItem()) {
               itemstack.setDamageValue(itemstack.getMaxDamage() - this.random.nextInt(1 + this.random.nextInt(Math.max(itemstack.getMaxDamage() - 3, 1))));
            }

            this.spawnAtLocation(itemstack);
            this.setItemSlot(equipmentslottype, ItemStack.EMPTY);
         }
      }

   }

   protected float getEquipmentDropChance(EquipmentSlotType p_205712_1_) {
      float f;
      switch(p_205712_1_.getType()) {
      case HAND:
         f = this.handDropChances[p_205712_1_.getIndex()];
         break;
      case ARMOR:
         f = this.armorDropChances[p_205712_1_.getIndex()];
         break;
      default:
         f = 0.0F;
      }

      return f;
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance p_180481_1_) {
      if (this.random.nextFloat() < 0.15F * p_180481_1_.getSpecialMultiplier()) {
         int i = this.random.nextInt(2);
         float f = this.level.getDifficulty() == Difficulty.HARD ? 0.1F : 0.25F;
         if (this.random.nextFloat() < 0.095F) {
            ++i;
         }

         if (this.random.nextFloat() < 0.095F) {
            ++i;
         }

         if (this.random.nextFloat() < 0.095F) {
            ++i;
         }

         boolean flag = true;

         for(EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
            if (equipmentslottype.getType() == EquipmentSlotType.Group.ARMOR) {
               ItemStack itemstack = this.getItemBySlot(equipmentslottype);
               if (!flag && this.random.nextFloat() < f) {
                  break;
               }

               flag = false;
               if (itemstack.isEmpty()) {
                  Item item = getEquipmentForSlot(equipmentslottype, i);
                  if (item != null) {
                     this.setItemSlot(equipmentslottype, new ItemStack(item));
                  }
               }
            }
         }
      }

   }

   public static EquipmentSlotType getEquipmentSlotForItem(ItemStack p_184640_0_) {
      final EquipmentSlotType slot = p_184640_0_.getEquipmentSlot();
      if (slot != null) return slot; // FORGE: Allow modders to set a non-default equipment slot for a stack; e.g. a non-armor chestplate-slot item
      Item item = p_184640_0_.getItem();
      if (item != Blocks.CARVED_PUMPKIN.asItem() && (!(item instanceof BlockItem) || !(((BlockItem)item).getBlock() instanceof AbstractSkullBlock))) {
         if (item instanceof ArmorItem) {
            return ((ArmorItem)item).getSlot();
         } else if (item == Items.ELYTRA) {
            return EquipmentSlotType.CHEST;
         } else {
            return p_184640_0_.isShield(null) ? EquipmentSlotType.OFFHAND : EquipmentSlotType.MAINHAND;
         }
      } else {
         return EquipmentSlotType.HEAD;
      }
   }

   @Nullable
   public static Item getEquipmentForSlot(EquipmentSlotType p_184636_0_, int p_184636_1_) {
      switch(p_184636_0_) {
      case HEAD:
         if (p_184636_1_ == 0) {
            return Items.LEATHER_HELMET;
         } else if (p_184636_1_ == 1) {
            return Items.GOLDEN_HELMET;
         } else if (p_184636_1_ == 2) {
            return Items.CHAINMAIL_HELMET;
         } else if (p_184636_1_ == 3) {
            return Items.IRON_HELMET;
         } else if (p_184636_1_ == 4) {
            return Items.DIAMOND_HELMET;
         }
      case CHEST:
         if (p_184636_1_ == 0) {
            return Items.LEATHER_CHESTPLATE;
         } else if (p_184636_1_ == 1) {
            return Items.GOLDEN_CHESTPLATE;
         } else if (p_184636_1_ == 2) {
            return Items.CHAINMAIL_CHESTPLATE;
         } else if (p_184636_1_ == 3) {
            return Items.IRON_CHESTPLATE;
         } else if (p_184636_1_ == 4) {
            return Items.DIAMOND_CHESTPLATE;
         }
      case LEGS:
         if (p_184636_1_ == 0) {
            return Items.LEATHER_LEGGINGS;
         } else if (p_184636_1_ == 1) {
            return Items.GOLDEN_LEGGINGS;
         } else if (p_184636_1_ == 2) {
            return Items.CHAINMAIL_LEGGINGS;
         } else if (p_184636_1_ == 3) {
            return Items.IRON_LEGGINGS;
         } else if (p_184636_1_ == 4) {
            return Items.DIAMOND_LEGGINGS;
         }
      case FEET:
         if (p_184636_1_ == 0) {
            return Items.LEATHER_BOOTS;
         } else if (p_184636_1_ == 1) {
            return Items.GOLDEN_BOOTS;
         } else if (p_184636_1_ == 2) {
            return Items.CHAINMAIL_BOOTS;
         } else if (p_184636_1_ == 3) {
            return Items.IRON_BOOTS;
         } else if (p_184636_1_ == 4) {
            return Items.DIAMOND_BOOTS;
         }
      default:
         return null;
      }
   }

   protected void populateDefaultEquipmentEnchantments(DifficultyInstance p_180483_1_) {
      float f = p_180483_1_.getSpecialMultiplier();
      this.enchantSpawnedWeapon(f);

      for(EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
         if (equipmentslottype.getType() == EquipmentSlotType.Group.ARMOR) {
            this.enchantSpawnedArmor(f, equipmentslottype);
         }
      }

   }

   protected void enchantSpawnedWeapon(float p_241844_1_) {
      if (!this.getMainHandItem().isEmpty() && this.random.nextFloat() < 0.25F * p_241844_1_) {
         this.setItemSlot(EquipmentSlotType.MAINHAND, EnchantmentHelper.enchantItem(this.random, this.getMainHandItem(), (int)(5.0F + p_241844_1_ * (float)this.random.nextInt(18)), false));
      }

   }

   protected void enchantSpawnedArmor(float p_242289_1_, EquipmentSlotType p_242289_2_) {
      ItemStack itemstack = this.getItemBySlot(p_242289_2_);
      if (!itemstack.isEmpty() && this.random.nextFloat() < 0.5F * p_242289_1_) {
         this.setItemSlot(p_242289_2_, EnchantmentHelper.enchantItem(this.random, itemstack, (int)(5.0F + p_242289_1_ * (float)this.random.nextInt(18)), false));
      }

   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.getAttribute(Attributes.FOLLOW_RANGE).addPermanentModifier(new AttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05D, AttributeModifier.Operation.MULTIPLY_BASE));
      if (this.random.nextFloat() < 0.05F) {
         this.setLeftHanded(true);
      } else {
         this.setLeftHanded(false);
      }

      return p_213386_4_;
   }

   public boolean canBeControlledByRider() {
      return false;
   }

   public void setPersistenceRequired() {
      this.persistenceRequired = true;
   }

   public void setDropChance(EquipmentSlotType p_184642_1_, float p_184642_2_) {
      switch(p_184642_1_.getType()) {
      case HAND:
         this.handDropChances[p_184642_1_.getIndex()] = p_184642_2_;
         break;
      case ARMOR:
         this.armorDropChances[p_184642_1_.getIndex()] = p_184642_2_;
      }

   }

   public boolean canPickUpLoot() {
      return this.canPickUpLoot;
   }

   public void setCanPickUpLoot(boolean p_98053_1_) {
      this.canPickUpLoot = p_98053_1_;
   }

   public boolean canTakeItem(ItemStack p_213365_1_) {
      EquipmentSlotType equipmentslottype = getEquipmentSlotForItem(p_213365_1_);
      return this.getItemBySlot(equipmentslottype).isEmpty() && this.canPickUpLoot();
   }

   public boolean isPersistenceRequired() {
      return this.persistenceRequired;
   }

   public final ActionResultType interact(PlayerEntity p_184230_1_, Hand p_184230_2_) {
      if (!this.isAlive()) {
         return ActionResultType.PASS;
      } else if (this.getLeashHolder() == p_184230_1_) {
         this.dropLeash(true, !p_184230_1_.abilities.instabuild);
         return ActionResultType.sidedSuccess(this.level.isClientSide);
      } else {
         ActionResultType actionresulttype = this.checkAndHandleImportantInteractions(p_184230_1_, p_184230_2_);
         if (actionresulttype.consumesAction()) {
            return actionresulttype;
         } else {
            actionresulttype = this.mobInteract(p_184230_1_, p_184230_2_);
            return actionresulttype.consumesAction() ? actionresulttype : super.interact(p_184230_1_, p_184230_2_);
         }
      }
   }

   private ActionResultType checkAndHandleImportantInteractions(PlayerEntity p_233661_1_, Hand p_233661_2_) {
      ItemStack itemstack = p_233661_1_.getItemInHand(p_233661_2_);
      if (itemstack.getItem() == Items.LEAD && this.canBeLeashed(p_233661_1_)) {
         this.setLeashedTo(p_233661_1_, true);
         itemstack.shrink(1);
         return ActionResultType.sidedSuccess(this.level.isClientSide);
      } else {
         if (itemstack.getItem() == Items.NAME_TAG) {
            ActionResultType actionresulttype = itemstack.interactLivingEntity(p_233661_1_, this, p_233661_2_);
            if (actionresulttype.consumesAction()) {
               return actionresulttype;
            }
         }

         if (itemstack.getItem() instanceof SpawnEggItem) {
            if (this.level instanceof ServerWorld) {
               SpawnEggItem spawneggitem = (SpawnEggItem)itemstack.getItem();
               Optional<MobEntity> optional = spawneggitem.spawnOffspringFromSpawnEgg(p_233661_1_, this, (EntityType)this.getType(), (ServerWorld)this.level, this.position(), itemstack);
               optional.ifPresent((p_233658_2_) -> {
                  this.onOffspringSpawnedFromEgg(p_233661_1_, p_233658_2_);
               });
               return optional.isPresent() ? ActionResultType.SUCCESS : ActionResultType.PASS;
            } else {
               return ActionResultType.CONSUME;
            }
         } else {
            return ActionResultType.PASS;
         }
      }
   }

   protected void onOffspringSpawnedFromEgg(PlayerEntity p_213406_1_, MobEntity p_213406_2_) {
   }

   protected ActionResultType mobInteract(PlayerEntity p_230254_1_, Hand p_230254_2_) {
      return ActionResultType.PASS;
   }

   public boolean isWithinRestriction() {
      return this.isWithinRestriction(this.blockPosition());
   }

   public boolean isWithinRestriction(BlockPos p_213389_1_) {
      if (this.restrictRadius == -1.0F) {
         return true;
      } else {
         return this.restrictCenter.distSqr(p_213389_1_) < (double)(this.restrictRadius * this.restrictRadius);
      }
   }

   public void restrictTo(BlockPos p_213390_1_, int p_213390_2_) {
      this.restrictCenter = p_213390_1_;
      this.restrictRadius = (float)p_213390_2_;
   }

   public BlockPos getRestrictCenter() {
      return this.restrictCenter;
   }

   public float getRestrictRadius() {
      return this.restrictRadius;
   }

   public boolean hasRestriction() {
      return this.restrictRadius != -1.0F;
   }

   @Nullable
   public <T extends MobEntity> T convertTo(EntityType<T> p_233656_1_, boolean p_233656_2_) {
      if (this.removed) {
         return (T)null;
      } else {
         T t = p_233656_1_.create(this.level);
         t.copyPosition(this);
         t.setBaby(this.isBaby());
         t.setNoAi(this.isNoAi());
         if (this.hasCustomName()) {
            t.setCustomName(this.getCustomName());
            t.setCustomNameVisible(this.isCustomNameVisible());
         }

         if (this.isPersistenceRequired()) {
            t.setPersistenceRequired();
         }

         t.setInvulnerable(this.isInvulnerable());
         if (p_233656_2_) {
            t.setCanPickUpLoot(this.canPickUpLoot());

            for(EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
               ItemStack itemstack = this.getItemBySlot(equipmentslottype);
               if (!itemstack.isEmpty()) {
                  t.setItemSlot(equipmentslottype, itemstack.copy());
                  t.setDropChance(equipmentslottype, this.getEquipmentDropChance(equipmentslottype));
                  itemstack.setCount(0);
               }
            }
         }

         this.level.addFreshEntity(t);
         if (this.isPassenger()) {
            Entity entity = this.getVehicle();
            this.stopRiding();
            t.startRiding(entity, true);
         }

         this.remove();
         return t;
      }
   }

   protected void tickLeash() {
      if (this.leashInfoTag != null) {
         this.restoreLeashFromSave();
      }

      if (this.leashHolder != null) {
         if (!this.isAlive() || !this.leashHolder.isAlive()) {
            this.dropLeash(true, true);
         }

      }
   }

   public void dropLeash(boolean p_110160_1_, boolean p_110160_2_) {
      if (this.leashHolder != null) {
         this.forcedLoading = false;
         if (!(this.leashHolder instanceof PlayerEntity)) {
            this.leashHolder.forcedLoading = false;
         }

         this.leashHolder = null;
         this.leashInfoTag = null;
         if (!this.level.isClientSide && p_110160_2_) {
            this.spawnAtLocation(Items.LEAD);
         }

         if (!this.level.isClientSide && p_110160_1_ && this.level instanceof ServerWorld) {
            ((ServerWorld)this.level).getChunkSource().broadcast(this, new SMountEntityPacket(this, (Entity)null));
         }
      }

   }

   public boolean canBeLeashed(PlayerEntity p_184652_1_) {
      return !this.isLeashed() && !(this instanceof IMob);
   }

   public boolean isLeashed() {
      return this.leashHolder != null;
   }

   @Nullable
   public Entity getLeashHolder() {
      if (this.leashHolder == null && this.delayedLeashHolderId != 0 && this.level.isClientSide) {
         this.leashHolder = this.level.getEntity(this.delayedLeashHolderId);
      }

      return this.leashHolder;
   }

   public void setLeashedTo(Entity p_110162_1_, boolean p_110162_2_) {
      this.leashHolder = p_110162_1_;
      this.leashInfoTag = null;
      this.forcedLoading = true;
      if (!(this.leashHolder instanceof PlayerEntity)) {
         this.leashHolder.forcedLoading = true;
      }

      if (!this.level.isClientSide && p_110162_2_ && this.level instanceof ServerWorld) {
         ((ServerWorld)this.level).getChunkSource().broadcast(this, new SMountEntityPacket(this, this.leashHolder));
      }

      if (this.isPassenger()) {
         this.stopRiding();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void setDelayedLeashHolderId(int p_213381_1_) {
      this.delayedLeashHolderId = p_213381_1_;
      this.dropLeash(false, false);
   }

   public boolean startRiding(Entity p_184205_1_, boolean p_184205_2_) {
      boolean flag = super.startRiding(p_184205_1_, p_184205_2_);
      if (flag && this.isLeashed()) {
         this.dropLeash(true, true);
      }

      return flag;
   }

   private void restoreLeashFromSave() {
      if (this.leashInfoTag != null && this.level instanceof ServerWorld) {
         if (this.leashInfoTag.hasUUID("UUID")) {
            UUID uuid = this.leashInfoTag.getUUID("UUID");
            Entity entity = ((ServerWorld)this.level).getEntity(uuid);
            if (entity != null) {
               this.setLeashedTo(entity, true);
               return;
            }
         } else if (this.leashInfoTag.contains("X", 99) && this.leashInfoTag.contains("Y", 99) && this.leashInfoTag.contains("Z", 99)) {
            BlockPos blockpos = new BlockPos(this.leashInfoTag.getInt("X"), this.leashInfoTag.getInt("Y"), this.leashInfoTag.getInt("Z"));
            this.setLeashedTo(LeashKnotEntity.getOrCreateKnot(this.level, blockpos), true);
            return;
         }

         if (this.tickCount > 100) {
            this.spawnAtLocation(Items.LEAD);
            this.leashInfoTag = null;
         }
      }

   }

   public boolean setSlot(int p_174820_1_, ItemStack p_174820_2_) {
      EquipmentSlotType equipmentslottype;
      if (p_174820_1_ == 98) {
         equipmentslottype = EquipmentSlotType.MAINHAND;
      } else if (p_174820_1_ == 99) {
         equipmentslottype = EquipmentSlotType.OFFHAND;
      } else if (p_174820_1_ == 100 + EquipmentSlotType.HEAD.getIndex()) {
         equipmentslottype = EquipmentSlotType.HEAD;
      } else if (p_174820_1_ == 100 + EquipmentSlotType.CHEST.getIndex()) {
         equipmentslottype = EquipmentSlotType.CHEST;
      } else if (p_174820_1_ == 100 + EquipmentSlotType.LEGS.getIndex()) {
         equipmentslottype = EquipmentSlotType.LEGS;
      } else {
         if (p_174820_1_ != 100 + EquipmentSlotType.FEET.getIndex()) {
            return false;
         }

         equipmentslottype = EquipmentSlotType.FEET;
      }

      if (!p_174820_2_.isEmpty() && !isValidSlotForItem(equipmentslottype, p_174820_2_) && equipmentslottype != EquipmentSlotType.HEAD) {
         return false;
      } else {
         this.setItemSlot(equipmentslottype, p_174820_2_);
         return true;
      }
   }

   public boolean isControlledByLocalInstance() {
      return this.canBeControlledByRider() && super.isControlledByLocalInstance();
   }

   public static boolean isValidSlotForItem(EquipmentSlotType p_184648_0_, ItemStack p_184648_1_) {
      EquipmentSlotType equipmentslottype = getEquipmentSlotForItem(p_184648_1_);
      return equipmentslottype == p_184648_0_ || equipmentslottype == EquipmentSlotType.MAINHAND && p_184648_0_ == EquipmentSlotType.OFFHAND || equipmentslottype == EquipmentSlotType.OFFHAND && p_184648_0_ == EquipmentSlotType.MAINHAND;
   }

   public boolean isEffectiveAi() {
      return super.isEffectiveAi() && !this.isNoAi();
   }

   public void setNoAi(boolean p_94061_1_) {
      byte b0 = this.entityData.get(DATA_MOB_FLAGS_ID);
      this.entityData.set(DATA_MOB_FLAGS_ID, p_94061_1_ ? (byte)(b0 | 1) : (byte)(b0 & -2));
   }

   public void setLeftHanded(boolean p_184641_1_) {
      byte b0 = this.entityData.get(DATA_MOB_FLAGS_ID);
      this.entityData.set(DATA_MOB_FLAGS_ID, p_184641_1_ ? (byte)(b0 | 2) : (byte)(b0 & -3));
   }

   public void setAggressive(boolean p_213395_1_) {
      byte b0 = this.entityData.get(DATA_MOB_FLAGS_ID);
      this.entityData.set(DATA_MOB_FLAGS_ID, p_213395_1_ ? (byte)(b0 | 4) : (byte)(b0 & -5));
   }

   public boolean isNoAi() {
      return (this.entityData.get(DATA_MOB_FLAGS_ID) & 1) != 0;
   }

   public boolean isLeftHanded() {
      return (this.entityData.get(DATA_MOB_FLAGS_ID) & 2) != 0;
   }

   public boolean isAggressive() {
      return (this.entityData.get(DATA_MOB_FLAGS_ID) & 4) != 0;
   }

   public void setBaby(boolean p_82227_1_) {
   }

   public HandSide getMainArm() {
      return this.isLeftHanded() ? HandSide.LEFT : HandSide.RIGHT;
   }

   public boolean canAttack(LivingEntity p_213336_1_) {
      return p_213336_1_.getType() == EntityType.PLAYER && ((PlayerEntity)p_213336_1_).abilities.invulnerable ? false : super.canAttack(p_213336_1_);
   }

   public boolean doHurtTarget(Entity p_70652_1_) {
      float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
      float f1 = (float)this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
      if (p_70652_1_ instanceof LivingEntity) {
         f += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)p_70652_1_).getMobType());
         f1 += (float)EnchantmentHelper.getKnockbackBonus(this);
      }

      int i = EnchantmentHelper.getFireAspect(this);
      if (i > 0) {
         p_70652_1_.setSecondsOnFire(i * 4);
      }

      boolean flag = p_70652_1_.hurt(DamageSource.mobAttack(this), f);
      if (flag) {
         if (f1 > 0.0F && p_70652_1_ instanceof LivingEntity) {
            ((LivingEntity)p_70652_1_).knockback(f1 * 0.5F, (double)MathHelper.sin(this.yRot * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(this.yRot * ((float)Math.PI / 180F))));
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
         }

         if (p_70652_1_ instanceof PlayerEntity) {
            PlayerEntity playerentity = (PlayerEntity)p_70652_1_;
            this.maybeDisableShield(playerentity, this.getMainHandItem(), playerentity.isUsingItem() ? playerentity.getUseItem() : ItemStack.EMPTY);
         }

         this.doEnchantDamageEffects(this, p_70652_1_);
         this.setLastHurtMob(p_70652_1_);
      }

      return flag;
   }

   private void maybeDisableShield(PlayerEntity p_233655_1_, ItemStack p_233655_2_, ItemStack p_233655_3_) {
      if (!p_233655_2_.isEmpty() && !p_233655_3_.isEmpty() && p_233655_2_.getItem() instanceof AxeItem && p_233655_3_.getItem() == Items.SHIELD) {
         float f = 0.25F + (float)EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
         if (this.random.nextFloat() < f) {
            p_233655_1_.getCooldowns().addCooldown(Items.SHIELD, 100);
            this.level.broadcastEntityEvent(p_233655_1_, (byte)30);
         }
      }

   }

   protected boolean isSunBurnTick() {
      if (this.level.isDay() && !this.level.isClientSide) {
         float f = this.getBrightness();
         BlockPos blockpos = this.getVehicle() instanceof BoatEntity ? (new BlockPos(this.getX(), (double)Math.round(this.getY()), this.getZ())).above() : new BlockPos(this.getX(), (double)Math.round(this.getY()), this.getZ());
         if (f > 0.5F && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.level.canSeeSky(blockpos)) {
            return true;
         }
      }

      return false;
   }

   protected void jumpInLiquid(ITag<Fluid> p_180466_1_) {
      if (this.getNavigation().canFloat()) {
         super.jumpInLiquid(p_180466_1_);
      } else {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.3D, 0.0D));
      }

   }

   protected void removeAfterChangingDimensions() {
      super.removeAfterChangingDimensions();
      this.dropLeash(true, false);
   }
}
