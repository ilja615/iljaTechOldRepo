package net.minecraft.world.raid;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;

public class Raid {
   private static final ITextComponent RAID_NAME_COMPONENT = new TranslationTextComponent("event.minecraft.raid");
   private static final ITextComponent VICTORY = new TranslationTextComponent("event.minecraft.raid.victory");
   private static final ITextComponent DEFEAT = new TranslationTextComponent("event.minecraft.raid.defeat");
   private static final ITextComponent RAID_BAR_VICTORY_COMPONENT = RAID_NAME_COMPONENT.copy().append(" - ").append(VICTORY);
   private static final ITextComponent RAID_BAR_DEFEAT_COMPONENT = RAID_NAME_COMPONENT.copy().append(" - ").append(DEFEAT);
   private final Map<Integer, AbstractRaiderEntity> groupToLeaderMap = Maps.newHashMap();
   private final Map<Integer, Set<AbstractRaiderEntity>> groupRaiderMap = Maps.newHashMap();
   private final Set<UUID> heroesOfTheVillage = Sets.newHashSet();
   private long ticksActive;
   private BlockPos center;
   private final ServerWorld level;
   private boolean started;
   private final int id;
   private float totalHealth;
   private int badOmenLevel;
   private boolean active;
   private int groupsSpawned;
   private final ServerBossInfo raidEvent = new ServerBossInfo(RAID_NAME_COMPONENT, BossInfo.Color.RED, BossInfo.Overlay.NOTCHED_10);
   private int postRaidTicks;
   private int raidCooldownTicks;
   private final Random random = new Random();
   private final int numGroups;
   private Raid.Status status;
   private int celebrationTicks;
   private Optional<BlockPos> waveSpawnPos = Optional.empty();

   public Raid(int p_i50144_1_, ServerWorld p_i50144_2_, BlockPos p_i50144_3_) {
      this.id = p_i50144_1_;
      this.level = p_i50144_2_;
      this.active = true;
      this.raidCooldownTicks = 300;
      this.raidEvent.setPercent(0.0F);
      this.center = p_i50144_3_;
      this.numGroups = this.getNumGroups(p_i50144_2_.getDifficulty());
      this.status = Raid.Status.ONGOING;
   }

   public Raid(ServerWorld p_i50145_1_, CompoundNBT p_i50145_2_) {
      this.level = p_i50145_1_;
      this.id = p_i50145_2_.getInt("Id");
      this.started = p_i50145_2_.getBoolean("Started");
      this.active = p_i50145_2_.getBoolean("Active");
      this.ticksActive = p_i50145_2_.getLong("TicksActive");
      this.badOmenLevel = p_i50145_2_.getInt("BadOmenLevel");
      this.groupsSpawned = p_i50145_2_.getInt("GroupsSpawned");
      this.raidCooldownTicks = p_i50145_2_.getInt("PreRaidTicks");
      this.postRaidTicks = p_i50145_2_.getInt("PostRaidTicks");
      this.totalHealth = p_i50145_2_.getFloat("TotalHealth");
      this.center = new BlockPos(p_i50145_2_.getInt("CX"), p_i50145_2_.getInt("CY"), p_i50145_2_.getInt("CZ"));
      this.numGroups = p_i50145_2_.getInt("NumGroups");
      this.status = Raid.Status.getByName(p_i50145_2_.getString("Status"));
      this.heroesOfTheVillage.clear();
      if (p_i50145_2_.contains("HeroesOfTheVillage", 9)) {
         ListNBT listnbt = p_i50145_2_.getList("HeroesOfTheVillage", 11);

         for(int i = 0; i < listnbt.size(); ++i) {
            this.heroesOfTheVillage.add(NBTUtil.loadUUID(listnbt.get(i)));
         }
      }

   }

   public boolean isOver() {
      return this.isVictory() || this.isLoss();
   }

   public boolean isBetweenWaves() {
      return this.hasFirstWaveSpawned() && this.getTotalRaidersAlive() == 0 && this.raidCooldownTicks > 0;
   }

   public boolean hasFirstWaveSpawned() {
      return this.groupsSpawned > 0;
   }

   public boolean isStopped() {
      return this.status == Raid.Status.STOPPED;
   }

   public boolean isVictory() {
      return this.status == Raid.Status.VICTORY;
   }

   public boolean isLoss() {
      return this.status == Raid.Status.LOSS;
   }

   public World getLevel() {
      return this.level;
   }

   public boolean isStarted() {
      return this.started;
   }

   public int getGroupsSpawned() {
      return this.groupsSpawned;
   }

   private Predicate<ServerPlayerEntity> validPlayer() {
      return (p_221302_1_) -> {
         BlockPos blockpos = p_221302_1_.blockPosition();
         return p_221302_1_.isAlive() && this.level.getRaidAt(blockpos) == this;
      };
   }

   private void updatePlayers() {
      Set<ServerPlayerEntity> set = Sets.newHashSet(this.raidEvent.getPlayers());
      List<ServerPlayerEntity> list = this.level.getPlayers(this.validPlayer());

      for(ServerPlayerEntity serverplayerentity : list) {
         if (!set.contains(serverplayerentity)) {
            this.raidEvent.addPlayer(serverplayerentity);
         }
      }

      for(ServerPlayerEntity serverplayerentity1 : set) {
         if (!list.contains(serverplayerentity1)) {
            this.raidEvent.removePlayer(serverplayerentity1);
         }
      }

   }

   public int getMaxBadOmenLevel() {
      return 5;
   }

   public int getBadOmenLevel() {
      return this.badOmenLevel;
   }

   public void absorbBadOmen(PlayerEntity p_221309_1_) {
      if (p_221309_1_.hasEffect(Effects.BAD_OMEN)) {
         this.badOmenLevel += p_221309_1_.getEffect(Effects.BAD_OMEN).getAmplifier() + 1;
         this.badOmenLevel = MathHelper.clamp(this.badOmenLevel, 0, this.getMaxBadOmenLevel());
      }

      p_221309_1_.removeEffect(Effects.BAD_OMEN);
   }

   public void stop() {
      this.active = false;
      this.raidEvent.removeAllPlayers();
      this.status = Raid.Status.STOPPED;
   }

   public void tick() {
      if (!this.isStopped()) {
         if (this.status == Raid.Status.ONGOING) {
            boolean flag = this.active;
            this.active = this.level.hasChunkAt(this.center);
            if (this.level.getDifficulty() == Difficulty.PEACEFUL) {
               this.stop();
               return;
            }

            if (flag != this.active) {
               this.raidEvent.setVisible(this.active);
            }

            if (!this.active) {
               return;
            }

            if (!this.level.isVillage(this.center)) {
               this.moveRaidCenterToNearbyVillageSection();
            }

            if (!this.level.isVillage(this.center)) {
               if (this.groupsSpawned > 0) {
                  this.status = Raid.Status.LOSS;
               } else {
                  this.stop();
               }
            }

            ++this.ticksActive;
            if (this.ticksActive >= 48000L) {
               this.stop();
               return;
            }

            int i = this.getTotalRaidersAlive();
            if (i == 0 && this.hasMoreWaves()) {
               if (this.raidCooldownTicks <= 0) {
                  if (this.raidCooldownTicks == 0 && this.groupsSpawned > 0) {
                     this.raidCooldownTicks = 300;
                     this.raidEvent.setName(RAID_NAME_COMPONENT);
                     return;
                  }
               } else {
                  boolean flag1 = this.waveSpawnPos.isPresent();
                  boolean flag2 = !flag1 && this.raidCooldownTicks % 5 == 0;
                  if (flag1 && !this.level.getChunkSource().isEntityTickingChunk(new ChunkPos(this.waveSpawnPos.get()))) {
                     flag2 = true;
                  }

                  if (flag2) {
                     int j = 0;
                     if (this.raidCooldownTicks < 100) {
                        j = 1;
                     } else if (this.raidCooldownTicks < 40) {
                        j = 2;
                     }

                     this.waveSpawnPos = this.getValidSpawnPos(j);
                  }

                  if (this.raidCooldownTicks == 300 || this.raidCooldownTicks % 20 == 0) {
                     this.updatePlayers();
                  }

                  --this.raidCooldownTicks;
                  this.raidEvent.setPercent(MathHelper.clamp((float)(300 - this.raidCooldownTicks) / 300.0F, 0.0F, 1.0F));
               }
            }

            if (this.ticksActive % 20L == 0L) {
               this.updatePlayers();
               this.updateRaiders();
               if (i > 0) {
                  if (i <= 2) {
                     this.raidEvent.setName(RAID_NAME_COMPONENT.copy().append(" - ").append(new TranslationTextComponent("event.minecraft.raid.raiders_remaining", i)));
                  } else {
                     this.raidEvent.setName(RAID_NAME_COMPONENT);
                  }
               } else {
                  this.raidEvent.setName(RAID_NAME_COMPONENT);
               }
            }

            boolean flag3 = false;
            int k = 0;

            while(this.shouldSpawnGroup()) {
               BlockPos blockpos = this.waveSpawnPos.isPresent() ? this.waveSpawnPos.get() : this.findRandomSpawnPos(k, 20);
               if (blockpos != null) {
                  this.started = true;
                  this.spawnGroup(blockpos);
                  if (!flag3) {
                     this.playSound(blockpos);
                     flag3 = true;
                  }
               } else {
                  ++k;
               }

               if (k > 3) {
                  this.stop();
                  break;
               }
            }

            if (this.isStarted() && !this.hasMoreWaves() && i == 0) {
               if (this.postRaidTicks < 40) {
                  ++this.postRaidTicks;
               } else {
                  this.status = Raid.Status.VICTORY;

                  for(UUID uuid : this.heroesOfTheVillage) {
                     Entity entity = this.level.getEntity(uuid);
                     if (entity instanceof LivingEntity && !entity.isSpectator()) {
                        LivingEntity livingentity = (LivingEntity)entity;
                        livingentity.addEffect(new EffectInstance(Effects.HERO_OF_THE_VILLAGE, 48000, this.badOmenLevel - 1, false, false, true));
                        if (livingentity instanceof ServerPlayerEntity) {
                           ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)livingentity;
                           serverplayerentity.awardStat(Stats.RAID_WIN);
                           CriteriaTriggers.RAID_WIN.trigger(serverplayerentity);
                        }
                     }
                  }
               }
            }

            this.setDirty();
         } else if (this.isOver()) {
            ++this.celebrationTicks;
            if (this.celebrationTicks >= 600) {
               this.stop();
               return;
            }

            if (this.celebrationTicks % 20 == 0) {
               this.updatePlayers();
               this.raidEvent.setVisible(true);
               if (this.isVictory()) {
                  this.raidEvent.setPercent(0.0F);
                  this.raidEvent.setName(RAID_BAR_VICTORY_COMPONENT);
               } else {
                  this.raidEvent.setName(RAID_BAR_DEFEAT_COMPONENT);
               }
            }
         }

      }
   }

   private void moveRaidCenterToNearbyVillageSection() {
      Stream<SectionPos> stream = SectionPos.cube(SectionPos.of(this.center), 2);
      stream.filter(this.level::isVillage).map(SectionPos::center).min(Comparator.comparingDouble((p_223025_1_) -> {
         return p_223025_1_.distSqr(this.center);
      })).ifPresent(this::setCenter);
   }

   private Optional<BlockPos> getValidSpawnPos(int p_221313_1_) {
      for(int i = 0; i < 3; ++i) {
         BlockPos blockpos = this.findRandomSpawnPos(p_221313_1_, 1);
         if (blockpos != null) {
            return Optional.of(blockpos);
         }
      }

      return Optional.empty();
   }

   private boolean hasMoreWaves() {
      if (this.hasBonusWave()) {
         return !this.hasSpawnedBonusWave();
      } else {
         return !this.isFinalWave();
      }
   }

   private boolean isFinalWave() {
      return this.getGroupsSpawned() == this.numGroups;
   }

   private boolean hasBonusWave() {
      return this.badOmenLevel > 1;
   }

   private boolean hasSpawnedBonusWave() {
      return this.getGroupsSpawned() > this.numGroups;
   }

   private boolean shouldSpawnBonusGroup() {
      return this.isFinalWave() && this.getTotalRaidersAlive() == 0 && this.hasBonusWave();
   }

   private void updateRaiders() {
      Iterator<Set<AbstractRaiderEntity>> iterator = this.groupRaiderMap.values().iterator();
      Set<AbstractRaiderEntity> set = Sets.newHashSet();

      while(iterator.hasNext()) {
         Set<AbstractRaiderEntity> set1 = iterator.next();

         for(AbstractRaiderEntity abstractraiderentity : set1) {
            BlockPos blockpos = abstractraiderentity.blockPosition();
            if (!abstractraiderentity.removed && abstractraiderentity.level.dimension() == this.level.dimension() && !(this.center.distSqr(blockpos) >= 12544.0D)) {
               if (abstractraiderentity.tickCount > 600) {
                  if (this.level.getEntity(abstractraiderentity.getUUID()) == null) {
                     set.add(abstractraiderentity);
                  }

                  if (!this.level.isVillage(blockpos) && abstractraiderentity.getNoActionTime() > 2400) {
                     abstractraiderentity.setTicksOutsideRaid(abstractraiderentity.getTicksOutsideRaid() + 1);
                  }

                  if (abstractraiderentity.getTicksOutsideRaid() >= 30) {
                     set.add(abstractraiderentity);
                  }
               }
            } else {
               set.add(abstractraiderentity);
            }
         }
      }

      for(AbstractRaiderEntity abstractraiderentity1 : set) {
         this.removeFromRaid(abstractraiderentity1, true);
      }

   }

   private void playSound(BlockPos p_221293_1_) {
      float f = 13.0F;
      int i = 64;
      Collection<ServerPlayerEntity> collection = this.raidEvent.getPlayers();

      for(ServerPlayerEntity serverplayerentity : this.level.players()) {
         Vector3d vector3d = serverplayerentity.position();
         Vector3d vector3d1 = Vector3d.atCenterOf(p_221293_1_);
         float f1 = MathHelper.sqrt((vector3d1.x - vector3d.x) * (vector3d1.x - vector3d.x) + (vector3d1.z - vector3d.z) * (vector3d1.z - vector3d.z));
         double d0 = vector3d.x + (double)(13.0F / f1) * (vector3d1.x - vector3d.x);
         double d1 = vector3d.z + (double)(13.0F / f1) * (vector3d1.z - vector3d.z);
         if (f1 <= 64.0F || collection.contains(serverplayerentity)) {
            serverplayerentity.connection.send(new SPlaySoundEffectPacket(SoundEvents.RAID_HORN, SoundCategory.NEUTRAL, d0, serverplayerentity.getY(), d1, 64.0F, 1.0F));
         }
      }

   }

   private void spawnGroup(BlockPos p_221294_1_) {
      boolean flag = false;
      int i = this.groupsSpawned + 1;
      this.totalHealth = 0.0F;
      DifficultyInstance difficultyinstance = this.level.getCurrentDifficultyAt(p_221294_1_);
      boolean flag1 = this.shouldSpawnBonusGroup();

      for(Raid.WaveMember raid$wavemember : Raid.WaveMember.VALUES) {
         int j = this.getDefaultNumSpawns(raid$wavemember, i, flag1) + this.getPotentialBonusSpawns(raid$wavemember, this.random, i, difficultyinstance, flag1);
         int k = 0;

         for(int l = 0; l < j; ++l) {
            AbstractRaiderEntity abstractraiderentity = raid$wavemember.entityType.create(this.level);
            if (!flag && abstractraiderentity.canBeLeader()) {
               abstractraiderentity.setPatrolLeader(true);
               this.setLeader(i, abstractraiderentity);
               flag = true;
            }

            this.joinRaid(i, abstractraiderentity, p_221294_1_, false);
            if (raid$wavemember.entityType == EntityType.RAVAGER) {
               AbstractRaiderEntity abstractraiderentity1 = null;
               if (i == this.getNumGroups(Difficulty.NORMAL)) {
                  abstractraiderentity1 = EntityType.PILLAGER.create(this.level);
               } else if (i >= this.getNumGroups(Difficulty.HARD)) {
                  if (k == 0) {
                     abstractraiderentity1 = EntityType.EVOKER.create(this.level);
                  } else {
                     abstractraiderentity1 = EntityType.VINDICATOR.create(this.level);
                  }
               }

               ++k;
               if (abstractraiderentity1 != null) {
                  this.joinRaid(i, abstractraiderentity1, p_221294_1_, false);
                  abstractraiderentity1.moveTo(p_221294_1_, 0.0F, 0.0F);
                  abstractraiderentity1.startRiding(abstractraiderentity);
               }
            }
         }
      }

      this.waveSpawnPos = Optional.empty();
      ++this.groupsSpawned;
      this.updateBossbar();
      this.setDirty();
   }

   public void joinRaid(int p_221317_1_, AbstractRaiderEntity p_221317_2_, @Nullable BlockPos p_221317_3_, boolean p_221317_4_) {
      boolean flag = this.addWaveMob(p_221317_1_, p_221317_2_);
      if (flag) {
         p_221317_2_.setCurrentRaid(this);
         p_221317_2_.setWave(p_221317_1_);
         p_221317_2_.setCanJoinRaid(true);
         p_221317_2_.setTicksOutsideRaid(0);
         if (!p_221317_4_ && p_221317_3_ != null) {
            p_221317_2_.setPos((double)p_221317_3_.getX() + 0.5D, (double)p_221317_3_.getY() + 1.0D, (double)p_221317_3_.getZ() + 0.5D);
            p_221317_2_.finalizeSpawn(this.level, this.level.getCurrentDifficultyAt(p_221317_3_), SpawnReason.EVENT, (ILivingEntityData)null, (CompoundNBT)null);
            p_221317_2_.applyRaidBuffs(p_221317_1_, false);
            p_221317_2_.setOnGround(true);
            this.level.addFreshEntityWithPassengers(p_221317_2_);
         }
      }

   }

   public void updateBossbar() {
      this.raidEvent.setPercent(MathHelper.clamp(this.getHealthOfLivingRaiders() / this.totalHealth, 0.0F, 1.0F));
   }

   public float getHealthOfLivingRaiders() {
      float f = 0.0F;

      for(Set<AbstractRaiderEntity> set : this.groupRaiderMap.values()) {
         for(AbstractRaiderEntity abstractraiderentity : set) {
            f += abstractraiderentity.getHealth();
         }
      }

      return f;
   }

   private boolean shouldSpawnGroup() {
      return this.raidCooldownTicks == 0 && (this.groupsSpawned < this.numGroups || this.shouldSpawnBonusGroup()) && this.getTotalRaidersAlive() == 0;
   }

   public int getTotalRaidersAlive() {
      return this.groupRaiderMap.values().stream().mapToInt(Set::size).sum();
   }

   public void removeFromRaid(AbstractRaiderEntity p_221322_1_, boolean p_221322_2_) {
      Set<AbstractRaiderEntity> set = this.groupRaiderMap.get(p_221322_1_.getWave());
      if (set != null) {
         boolean flag = set.remove(p_221322_1_);
         if (flag) {
            if (p_221322_2_) {
               this.totalHealth -= p_221322_1_.getHealth();
            }

            p_221322_1_.setCurrentRaid((Raid)null);
            this.updateBossbar();
            this.setDirty();
         }
      }

   }

   private void setDirty() {
      this.level.getRaids().setDirty();
   }

   public static ItemStack getLeaderBannerInstance() {
      ItemStack itemstack = new ItemStack(Items.WHITE_BANNER);
      CompoundNBT compoundnbt = itemstack.getOrCreateTagElement("BlockEntityTag");
      ListNBT listnbt = (new BannerPattern.Builder()).addPattern(BannerPattern.RHOMBUS_MIDDLE, DyeColor.CYAN).addPattern(BannerPattern.STRIPE_BOTTOM, DyeColor.LIGHT_GRAY).addPattern(BannerPattern.STRIPE_CENTER, DyeColor.GRAY).addPattern(BannerPattern.BORDER, DyeColor.LIGHT_GRAY).addPattern(BannerPattern.STRIPE_MIDDLE, DyeColor.BLACK).addPattern(BannerPattern.HALF_HORIZONTAL, DyeColor.LIGHT_GRAY).addPattern(BannerPattern.CIRCLE_MIDDLE, DyeColor.LIGHT_GRAY).addPattern(BannerPattern.BORDER, DyeColor.BLACK).toListTag();
      compoundnbt.put("Patterns", listnbt);
      itemstack.hideTooltipPart(ItemStack.TooltipDisplayFlags.ADDITIONAL);
      itemstack.setHoverName((new TranslationTextComponent("block.minecraft.ominous_banner")).withStyle(TextFormatting.GOLD));
      return itemstack;
   }

   @Nullable
   public AbstractRaiderEntity getLeader(int p_221332_1_) {
      return this.groupToLeaderMap.get(p_221332_1_);
   }

   @Nullable
   private BlockPos findRandomSpawnPos(int p_221298_1_, int p_221298_2_) {
      int i = p_221298_1_ == 0 ? 2 : 2 - p_221298_1_;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int i1 = 0; i1 < p_221298_2_; ++i1) {
         float f = this.level.random.nextFloat() * ((float)Math.PI * 2F);
         int j = this.center.getX() + MathHelper.floor(MathHelper.cos(f) * 32.0F * (float)i) + this.level.random.nextInt(5);
         int l = this.center.getZ() + MathHelper.floor(MathHelper.sin(f) * 32.0F * (float)i) + this.level.random.nextInt(5);
         int k = this.level.getHeight(Heightmap.Type.WORLD_SURFACE, j, l);
         blockpos$mutable.set(j, k, l);
         if ((!this.level.isVillage(blockpos$mutable) || p_221298_1_ >= 2) && this.level.hasChunksAt(blockpos$mutable.getX() - 10, blockpos$mutable.getY() - 10, blockpos$mutable.getZ() - 10, blockpos$mutable.getX() + 10, blockpos$mutable.getY() + 10, blockpos$mutable.getZ() + 10) && this.level.getChunkSource().isEntityTickingChunk(new ChunkPos(blockpos$mutable)) && (WorldEntitySpawner.isSpawnPositionOk(EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, this.level, blockpos$mutable, EntityType.RAVAGER) || this.level.getBlockState(blockpos$mutable.below()).is(Blocks.SNOW) && this.level.getBlockState(blockpos$mutable).isAir())) {
            return blockpos$mutable;
         }
      }

      return null;
   }

   private boolean addWaveMob(int p_221287_1_, AbstractRaiderEntity p_221287_2_) {
      return this.addWaveMob(p_221287_1_, p_221287_2_, true);
   }

   public boolean addWaveMob(int p_221300_1_, AbstractRaiderEntity p_221300_2_, boolean p_221300_3_) {
      this.groupRaiderMap.computeIfAbsent(p_221300_1_, (p_221323_0_) -> {
         return Sets.newHashSet();
      });
      Set<AbstractRaiderEntity> set = this.groupRaiderMap.get(p_221300_1_);
      AbstractRaiderEntity abstractraiderentity = null;

      for(AbstractRaiderEntity abstractraiderentity1 : set) {
         if (abstractraiderentity1.getUUID().equals(p_221300_2_.getUUID())) {
            abstractraiderentity = abstractraiderentity1;
            break;
         }
      }

      if (abstractraiderentity != null) {
         set.remove(abstractraiderentity);
         set.add(p_221300_2_);
      }

      set.add(p_221300_2_);
      if (p_221300_3_) {
         this.totalHealth += p_221300_2_.getHealth();
      }

      this.updateBossbar();
      this.setDirty();
      return true;
   }

   public void setLeader(int p_221324_1_, AbstractRaiderEntity p_221324_2_) {
      this.groupToLeaderMap.put(p_221324_1_, p_221324_2_);
      p_221324_2_.setItemSlot(EquipmentSlotType.HEAD, getLeaderBannerInstance());
      p_221324_2_.setDropChance(EquipmentSlotType.HEAD, 2.0F);
   }

   public void removeLeader(int p_221296_1_) {
      this.groupToLeaderMap.remove(p_221296_1_);
   }

   public BlockPos getCenter() {
      return this.center;
   }

   private void setCenter(BlockPos p_223024_1_) {
      this.center = p_223024_1_;
   }

   public int getId() {
      return this.id;
   }

   private int getDefaultNumSpawns(Raid.WaveMember p_221330_1_, int p_221330_2_, boolean p_221330_3_) {
      return p_221330_3_ ? p_221330_1_.spawnsPerWaveBeforeBonus[this.numGroups] : p_221330_1_.spawnsPerWaveBeforeBonus[p_221330_2_];
   }

   private int getPotentialBonusSpawns(Raid.WaveMember p_221335_1_, Random p_221335_2_, int p_221335_3_, DifficultyInstance p_221335_4_, boolean p_221335_5_) {
      Difficulty difficulty = p_221335_4_.getDifficulty();
      boolean flag = difficulty == Difficulty.EASY;
      boolean flag1 = difficulty == Difficulty.NORMAL;
      int i;
      switch(p_221335_1_) {
      case WITCH:
         if (flag || p_221335_3_ <= 2 || p_221335_3_ == 4) {
            return 0;
         }

         i = 1;
         break;
      case PILLAGER:
      case VINDICATOR:
         if (flag) {
            i = p_221335_2_.nextInt(2);
         } else if (flag1) {
            i = 1;
         } else {
            i = 2;
         }
         break;
      case RAVAGER:
         i = !flag && p_221335_5_ ? 1 : 0;
         break;
      default:
         return 0;
      }

      return i > 0 ? p_221335_2_.nextInt(i + 1) : 0;
   }

   public boolean isActive() {
      return this.active;
   }

   public CompoundNBT save(CompoundNBT p_221326_1_) {
      p_221326_1_.putInt("Id", this.id);
      p_221326_1_.putBoolean("Started", this.started);
      p_221326_1_.putBoolean("Active", this.active);
      p_221326_1_.putLong("TicksActive", this.ticksActive);
      p_221326_1_.putInt("BadOmenLevel", this.badOmenLevel);
      p_221326_1_.putInt("GroupsSpawned", this.groupsSpawned);
      p_221326_1_.putInt("PreRaidTicks", this.raidCooldownTicks);
      p_221326_1_.putInt("PostRaidTicks", this.postRaidTicks);
      p_221326_1_.putFloat("TotalHealth", this.totalHealth);
      p_221326_1_.putInt("NumGroups", this.numGroups);
      p_221326_1_.putString("Status", this.status.getName());
      p_221326_1_.putInt("CX", this.center.getX());
      p_221326_1_.putInt("CY", this.center.getY());
      p_221326_1_.putInt("CZ", this.center.getZ());
      ListNBT listnbt = new ListNBT();

      for(UUID uuid : this.heroesOfTheVillage) {
         listnbt.add(NBTUtil.createUUID(uuid));
      }

      p_221326_1_.put("HeroesOfTheVillage", listnbt);
      return p_221326_1_;
   }

   public int getNumGroups(Difficulty p_221306_1_) {
      switch(p_221306_1_) {
      case EASY:
         return 3;
      case NORMAL:
         return 5;
      case HARD:
         return 7;
      default:
         return 0;
      }
   }

   public float getEnchantOdds() {
      int i = this.getBadOmenLevel();
      if (i == 2) {
         return 0.1F;
      } else if (i == 3) {
         return 0.25F;
      } else if (i == 4) {
         return 0.5F;
      } else {
         return i == 5 ? 0.75F : 0.0F;
      }
   }

   public void addHeroOfTheVillage(Entity p_221311_1_) {
      this.heroesOfTheVillage.add(p_221311_1_.getUUID());
   }

   static enum Status {
      ONGOING,
      VICTORY,
      LOSS,
      STOPPED;

      private static final Raid.Status[] VALUES = values();

      private static Raid.Status getByName(String p_221275_0_) {
         for(Raid.Status raid$status : VALUES) {
            if (p_221275_0_.equalsIgnoreCase(raid$status.name())) {
               return raid$status;
            }
         }

         return ONGOING;
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }

   public static enum WaveMember implements net.minecraftforge.common.IExtensibleEnum {
      VINDICATOR(EntityType.VINDICATOR, new int[]{0, 0, 2, 0, 1, 4, 2, 5}),
      EVOKER(EntityType.EVOKER, new int[]{0, 0, 0, 0, 0, 1, 1, 2}),
      PILLAGER(EntityType.PILLAGER, new int[]{0, 4, 3, 3, 4, 4, 4, 2}),
      WITCH(EntityType.WITCH, new int[]{0, 0, 0, 0, 3, 0, 0, 1}),
      RAVAGER(EntityType.RAVAGER, new int[]{0, 0, 0, 1, 0, 1, 0, 2});

      private static Raid.WaveMember[] VALUES = values();
      private final EntityType<? extends AbstractRaiderEntity> entityType;
      private final int[] spawnsPerWaveBeforeBonus;

      private WaveMember(EntityType<? extends AbstractRaiderEntity> p_i50602_3_, int[] p_i50602_4_) {
         this.entityType = p_i50602_3_;
         this.spawnsPerWaveBeforeBonus = p_i50602_4_;
      }
      
      /**
       * The waveCountsIn integer decides how many entities of the EntityType defined in typeIn will spawn in each wave.
       * For example, one ravager will always spawn in wave 3.
       */
      public static WaveMember create(String name, EntityType<? extends AbstractRaiderEntity> typeIn, int[] waveCountsIn) {
         throw new IllegalStateException("Enum not extended");
      }
      
      @Override
      @Deprecated
      public void init() {
         VALUES = values();
      }
   }
}
