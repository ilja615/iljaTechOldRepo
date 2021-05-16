package net.minecraft.entity.player;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.PortalInfo;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.HorseInventoryContainer;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.AbstractMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffers;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ServerRecipeBook;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.client.CClientSettingsPacket;
import net.minecraft.network.play.server.SAnimateHandPacket;
import net.minecraft.network.play.server.SCameraPacket;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SCloseWindowPacket;
import net.minecraft.network.play.server.SCombatPacket;
import net.minecraft.network.play.server.SDestroyEntitiesPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SMerchantOffersPacket;
import net.minecraft.network.play.server.SOpenBookWindowPacket;
import net.minecraft.network.play.server.SOpenHorseWindowPacket;
import net.minecraft.network.play.server.SOpenSignMenuPacket;
import net.minecraft.network.play.server.SOpenWindowPacket;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEventPacket;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.network.play.server.SPlayerLookPacket;
import net.minecraft.network.play.server.SRemoveEntityEffectPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SSendResourcePackPacket;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.network.play.server.SSetExperiencePacket;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.network.play.server.SSpawnPlayerPacket;
import net.minecraft.network.play.server.SUnloadChunkPacket;
import net.minecraft.network.play.server.SUpdateHealthPacket;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.network.play.server.SWindowItemsPacket;
import net.minecraft.network.play.server.SWindowPropertyPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.server.management.PlayerList;
import net.minecraft.stats.ServerStatisticsManager;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ServerCooldownTracker;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.filter.IChatFilter;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IWorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayerEntity extends PlayerEntity implements IContainerListener {
   private static final Logger LOGGER = LogManager.getLogger();
   public ServerPlayNetHandler connection;
   public final MinecraftServer server;
   public final PlayerInteractionManager gameMode;
   private final List<Integer> entitiesToRemove = Lists.newLinkedList();
   private final PlayerAdvancements advancements;
   private final ServerStatisticsManager stats;
   private float lastRecordedHealthAndAbsorption = Float.MIN_VALUE;
   private int lastRecordedFoodLevel = Integer.MIN_VALUE;
   private int lastRecordedAirLevel = Integer.MIN_VALUE;
   private int lastRecordedArmor = Integer.MIN_VALUE;
   private int lastRecordedLevel = Integer.MIN_VALUE;
   private int lastRecordedExperience = Integer.MIN_VALUE;
   private float lastSentHealth = -1.0E8F;
   private int lastSentFood = -99999999;
   private boolean lastFoodSaturationZero = true;
   private int lastSentExp = -99999999;
   private int spawnInvulnerableTime = 60;
   private ChatVisibility chatVisibility;
   private boolean canChatColor = true;
   private long lastActionTime = Util.getMillis();
   private Entity camera;
   private boolean isChangingDimension;
   private boolean seenCredits;
   private final ServerRecipeBook recipeBook = new ServerRecipeBook();
   private Vector3d levitationStartPos;
   private int levitationStartTime;
   private boolean disconnected;
   @Nullable
   private Vector3d enteredNetherPosition;
   private SectionPos lastSectionPos = SectionPos.of(0, 0, 0);
   private RegistryKey<World> respawnDimension = World.OVERWORLD;
   @Nullable
   private BlockPos respawnPosition;
   private boolean respawnForced;
   private float respawnAngle;
   @Nullable
   private final IChatFilter textFilter;
   public int containerCounter;
   public boolean ignoreSlotUpdateHack;
   public int latency;
   public boolean wonGame;

   public ServerPlayerEntity(MinecraftServer p_i45285_1_, ServerWorld p_i45285_2_, GameProfile p_i45285_3_, PlayerInteractionManager p_i45285_4_) {
      super(p_i45285_2_, p_i45285_2_.getSharedSpawnPos(), p_i45285_2_.getSharedSpawnAngle(), p_i45285_3_);
      p_i45285_4_.player = this;
      this.gameMode = p_i45285_4_;
      this.server = p_i45285_1_;
      this.stats = p_i45285_1_.getPlayerList().getPlayerStats(this);
      this.advancements = p_i45285_1_.getPlayerList().getPlayerAdvancements(this);
      this.maxUpStep = 1.0F;
      this.fudgeSpawnLocation(p_i45285_2_);
      this.textFilter = p_i45285_1_.createTextFilterForPlayer(this);
   }

   private void fudgeSpawnLocation(ServerWorld p_205734_1_) {
      BlockPos blockpos = p_205734_1_.getSharedSpawnPos();
      if (p_205734_1_.dimensionType().hasSkyLight() && p_205734_1_.getServer().getWorldData().getGameType() != GameType.ADVENTURE) {
         int i = Math.max(0, this.server.getSpawnRadius(p_205734_1_));
         int j = MathHelper.floor(p_205734_1_.getWorldBorder().getDistanceToBorder((double)blockpos.getX(), (double)blockpos.getZ()));
         if (j < i) {
            i = j;
         }

         if (j <= 1) {
            i = 1;
         }

         long k = (long)(i * 2 + 1);
         long l = k * k;
         int i1 = l > 2147483647L ? Integer.MAX_VALUE : (int)l;
         int j1 = this.getCoprime(i1);
         int k1 = (new Random()).nextInt(i1);

         for(int l1 = 0; l1 < i1; ++l1) {
            int i2 = (k1 + j1 * l1) % i1;
            int j2 = i2 % (i * 2 + 1);
            int k2 = i2 / (i * 2 + 1);
            BlockPos blockpos1 = SpawnLocationHelper.getOverworldRespawnPos(p_205734_1_, blockpos.getX() + j2 - i, blockpos.getZ() + k2 - i, false);
            if (blockpos1 != null) {
               this.moveTo(blockpos1, 0.0F, 0.0F);
               if (p_205734_1_.noCollision(this)) {
                  break;
               }
            }
         }
      } else {
         this.moveTo(blockpos, 0.0F, 0.0F);

         while(!p_205734_1_.noCollision(this) && this.getY() < 255.0D) {
            this.setPos(this.getX(), this.getY() + 1.0D, this.getZ());
         }
      }

   }

   private int getCoprime(int p_205735_1_) {
      return p_205735_1_ <= 16 ? p_205735_1_ - 1 : 17;
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      if (p_70037_1_.contains("playerGameType", 99)) {
         if (this.getServer().getForceGameType()) {
            this.gameMode.setGameModeForPlayer(this.getServer().getDefaultGameType(), GameType.NOT_SET);
         } else {
            this.gameMode.setGameModeForPlayer(GameType.byId(p_70037_1_.getInt("playerGameType")), p_70037_1_.contains("previousPlayerGameType", 3) ? GameType.byId(p_70037_1_.getInt("previousPlayerGameType")) : GameType.NOT_SET);
         }
      }

      if (p_70037_1_.contains("enteredNetherPosition", 10)) {
         CompoundNBT compoundnbt = p_70037_1_.getCompound("enteredNetherPosition");
         this.enteredNetherPosition = new Vector3d(compoundnbt.getDouble("x"), compoundnbt.getDouble("y"), compoundnbt.getDouble("z"));
      }

      this.seenCredits = p_70037_1_.getBoolean("seenCredits");
      if (p_70037_1_.contains("recipeBook", 10)) {
         this.recipeBook.fromNbt(p_70037_1_.getCompound("recipeBook"), this.server.getRecipeManager());
      }

      if (this.isSleeping()) {
         this.stopSleeping();
      }

      if (p_70037_1_.contains("SpawnX", 99) && p_70037_1_.contains("SpawnY", 99) && p_70037_1_.contains("SpawnZ", 99)) {
         this.respawnPosition = new BlockPos(p_70037_1_.getInt("SpawnX"), p_70037_1_.getInt("SpawnY"), p_70037_1_.getInt("SpawnZ"));
         this.respawnForced = p_70037_1_.getBoolean("SpawnForced");
         this.respawnAngle = p_70037_1_.getFloat("SpawnAngle");
         if (p_70037_1_.contains("SpawnDimension")) {
            this.respawnDimension = World.RESOURCE_KEY_CODEC.parse(NBTDynamicOps.INSTANCE, p_70037_1_.get("SpawnDimension")).resultOrPartial(LOGGER::error).orElse(World.OVERWORLD);
         }
      }

   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putInt("playerGameType", this.gameMode.getGameModeForPlayer().getId());
      p_213281_1_.putInt("previousPlayerGameType", this.gameMode.getPreviousGameModeForPlayer().getId());
      p_213281_1_.putBoolean("seenCredits", this.seenCredits);
      if (this.enteredNetherPosition != null) {
         CompoundNBT compoundnbt = new CompoundNBT();
         compoundnbt.putDouble("x", this.enteredNetherPosition.x);
         compoundnbt.putDouble("y", this.enteredNetherPosition.y);
         compoundnbt.putDouble("z", this.enteredNetherPosition.z);
         p_213281_1_.put("enteredNetherPosition", compoundnbt);
      }

      Entity entity1 = this.getRootVehicle();
      Entity entity = this.getVehicle();
      if (entity != null && entity1 != this && entity1.hasOnePlayerPassenger()) {
         CompoundNBT compoundnbt1 = new CompoundNBT();
         CompoundNBT compoundnbt2 = new CompoundNBT();
         entity1.save(compoundnbt2);
         compoundnbt1.putUUID("Attach", entity.getUUID());
         compoundnbt1.put("Entity", compoundnbt2);
         p_213281_1_.put("RootVehicle", compoundnbt1);
      }

      p_213281_1_.put("recipeBook", this.recipeBook.toNbt());
      p_213281_1_.putString("Dimension", this.level.dimension().location().toString());
      if (this.respawnPosition != null) {
         p_213281_1_.putInt("SpawnX", this.respawnPosition.getX());
         p_213281_1_.putInt("SpawnY", this.respawnPosition.getY());
         p_213281_1_.putInt("SpawnZ", this.respawnPosition.getZ());
         p_213281_1_.putBoolean("SpawnForced", this.respawnForced);
         p_213281_1_.putFloat("SpawnAngle", this.respawnAngle);
         ResourceLocation.CODEC.encodeStart(NBTDynamicOps.INSTANCE, this.respawnDimension.location()).resultOrPartial(LOGGER::error).ifPresent((p_241148_1_) -> {
            p_213281_1_.put("SpawnDimension", p_241148_1_);
         });
      }

   }

   public void setExperiencePoints(int p_195394_1_) {
      float f = (float)this.getXpNeededForNextLevel();
      float f1 = (f - 1.0F) / f;
      this.experienceProgress = MathHelper.clamp((float)p_195394_1_ / f, 0.0F, f1);
      this.lastSentExp = -1;
   }

   public void setExperienceLevels(int p_195399_1_) {
      this.experienceLevel = p_195399_1_;
      this.lastSentExp = -1;
   }

   public void giveExperienceLevels(int p_82242_1_) {
      super.giveExperienceLevels(p_82242_1_);
      this.lastSentExp = -1;
   }

   public void onEnchantmentPerformed(ItemStack p_192024_1_, int p_192024_2_) {
      super.onEnchantmentPerformed(p_192024_1_, p_192024_2_);
      this.lastSentExp = -1;
   }

   public void initMenu() {
      this.containerMenu.addSlotListener(this);
   }

   public void onEnterCombat() {
      super.onEnterCombat();
      this.connection.send(new SCombatPacket(this.getCombatTracker(), SCombatPacket.Event.ENTER_COMBAT));
   }

   public void onLeaveCombat() {
      super.onLeaveCombat();
      this.connection.send(new SCombatPacket(this.getCombatTracker(), SCombatPacket.Event.END_COMBAT));
   }

   protected void onInsideBlock(BlockState p_191955_1_) {
      CriteriaTriggers.ENTER_BLOCK.trigger(this, p_191955_1_);
   }

   protected CooldownTracker createItemCooldowns() {
      return new ServerCooldownTracker(this);
   }

   public void tick() {
      this.gameMode.tick();
      --this.spawnInvulnerableTime;
      if (this.invulnerableTime > 0) {
         --this.invulnerableTime;
      }

      this.containerMenu.broadcastChanges();
      if (!this.level.isClientSide && !this.containerMenu.stillValid(this)) {
         this.closeContainer();
         this.containerMenu = this.inventoryMenu;
      }

      while(!this.entitiesToRemove.isEmpty()) {
         int i = Math.min(this.entitiesToRemove.size(), Integer.MAX_VALUE);
         int[] aint = new int[i];
         Iterator<Integer> iterator = this.entitiesToRemove.iterator();
         int j = 0;

         while(iterator.hasNext() && j < i) {
            aint[j++] = iterator.next();
            iterator.remove();
         }

         this.connection.send(new SDestroyEntitiesPacket(aint));
      }

      Entity entity = this.getCamera();
      if (entity != this) {
         if (entity.isAlive()) {
            this.absMoveTo(entity.getX(), entity.getY(), entity.getZ(), entity.yRot, entity.xRot);
            this.getLevel().getChunkSource().move(this);
            if (this.wantsToStopRiding()) {
               this.setCamera(this);
            }
         } else {
            this.setCamera(this);
         }
      }

      CriteriaTriggers.TICK.trigger(this);
      if (this.levitationStartPos != null) {
         CriteriaTriggers.LEVITATION.trigger(this, this.levitationStartPos, this.tickCount - this.levitationStartTime);
      }

      this.advancements.flushDirty(this);
   }

   public void doTick() {
      try {
         if (!this.isSpectator() || this.level.hasChunkAt(this.blockPosition())) {
            super.tick();
         }

         for(int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (itemstack.getItem().isComplex()) {
               IPacket<?> ipacket = ((AbstractMapItem)itemstack.getItem()).getUpdatePacket(itemstack, this.level, this);
               if (ipacket != null) {
                  this.connection.send(ipacket);
               }
            }
         }

         if (this.getHealth() != this.lastSentHealth || this.lastSentFood != this.foodData.getFoodLevel() || this.foodData.getSaturationLevel() == 0.0F != this.lastFoodSaturationZero) {
            this.connection.send(new SUpdateHealthPacket(this.getHealth(), this.foodData.getFoodLevel(), this.foodData.getSaturationLevel()));
            this.lastSentHealth = this.getHealth();
            this.lastSentFood = this.foodData.getFoodLevel();
            this.lastFoodSaturationZero = this.foodData.getSaturationLevel() == 0.0F;
         }

         if (this.getHealth() + this.getAbsorptionAmount() != this.lastRecordedHealthAndAbsorption) {
            this.lastRecordedHealthAndAbsorption = this.getHealth() + this.getAbsorptionAmount();
            this.updateScoreForCriteria(ScoreCriteria.HEALTH, MathHelper.ceil(this.lastRecordedHealthAndAbsorption));
         }

         if (this.foodData.getFoodLevel() != this.lastRecordedFoodLevel) {
            this.lastRecordedFoodLevel = this.foodData.getFoodLevel();
            this.updateScoreForCriteria(ScoreCriteria.FOOD, MathHelper.ceil((float)this.lastRecordedFoodLevel));
         }

         if (this.getAirSupply() != this.lastRecordedAirLevel) {
            this.lastRecordedAirLevel = this.getAirSupply();
            this.updateScoreForCriteria(ScoreCriteria.AIR, MathHelper.ceil((float)this.lastRecordedAirLevel));
         }

         if (this.getArmorValue() != this.lastRecordedArmor) {
            this.lastRecordedArmor = this.getArmorValue();
            this.updateScoreForCriteria(ScoreCriteria.ARMOR, MathHelper.ceil((float)this.lastRecordedArmor));
         }

         if (this.totalExperience != this.lastRecordedExperience) {
            this.lastRecordedExperience = this.totalExperience;
            this.updateScoreForCriteria(ScoreCriteria.EXPERIENCE, MathHelper.ceil((float)this.lastRecordedExperience));
         }

         if (this.experienceLevel != this.lastRecordedLevel) {
            this.lastRecordedLevel = this.experienceLevel;
            this.updateScoreForCriteria(ScoreCriteria.LEVEL, MathHelper.ceil((float)this.lastRecordedLevel));
         }

         if (this.totalExperience != this.lastSentExp) {
            this.lastSentExp = this.totalExperience;
            this.connection.send(new SSetExperiencePacket(this.experienceProgress, this.totalExperience, this.experienceLevel));
         }

         if (this.tickCount % 20 == 0) {
            CriteriaTriggers.LOCATION.trigger(this);
         }

      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Ticking player");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Player being ticked");
         this.fillCrashReportCategory(crashreportcategory);
         throw new ReportedException(crashreport);
      }
   }

   private void updateScoreForCriteria(ScoreCriteria p_184849_1_, int p_184849_2_) {
      this.getScoreboard().forAllObjectives(p_184849_1_, this.getScoreboardName(), (p_195397_1_) -> {
         p_195397_1_.setScore(p_184849_2_);
      });
   }

   public void die(DamageSource p_70645_1_) {
      if (net.minecraftforge.common.ForgeHooks.onLivingDeath(this, p_70645_1_)) return;
      boolean flag = this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES);
      if (flag) {
         ITextComponent itextcomponent = this.getCombatTracker().getDeathMessage();
         this.connection.send(new SCombatPacket(this.getCombatTracker(), SCombatPacket.Event.ENTITY_DIED, itextcomponent), (p_212356_2_) -> {
            if (!p_212356_2_.isSuccess()) {
               int i = 256;
               String s = itextcomponent.getString(256);
               ITextComponent itextcomponent1 = new TranslationTextComponent("death.attack.message_too_long", (new StringTextComponent(s)).withStyle(TextFormatting.YELLOW));
               ITextComponent itextcomponent2 = (new TranslationTextComponent("death.attack.even_more_magic", this.getDisplayName())).withStyle((p_212357_1_) -> {
                  return p_212357_1_.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, itextcomponent1));
               });
               this.connection.send(new SCombatPacket(this.getCombatTracker(), SCombatPacket.Event.ENTITY_DIED, itextcomponent2));
            }

         });
         Team team = this.getTeam();
         if (team != null && team.getDeathMessageVisibility() != Team.Visible.ALWAYS) {
            if (team.getDeathMessageVisibility() == Team.Visible.HIDE_FOR_OTHER_TEAMS) {
               this.server.getPlayerList().broadcastToTeam(this, itextcomponent);
            } else if (team.getDeathMessageVisibility() == Team.Visible.HIDE_FOR_OWN_TEAM) {
               this.server.getPlayerList().broadcastToAllExceptTeam(this, itextcomponent);
            }
         } else {
            this.server.getPlayerList().broadcastMessage(itextcomponent, ChatType.SYSTEM, Util.NIL_UUID);
         }
      } else {
         this.connection.send(new SCombatPacket(this.getCombatTracker(), SCombatPacket.Event.ENTITY_DIED));
      }

      this.removeEntitiesOnShoulder();
      if (this.level.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
         this.tellNeutralMobsThatIDied();
      }

      if (!this.isSpectator()) {
         this.dropAllDeathLoot(p_70645_1_);
      }

      this.getScoreboard().forAllObjectives(ScoreCriteria.DEATH_COUNT, this.getScoreboardName(), Score::increment);
      LivingEntity livingentity = this.getKillCredit();
      if (livingentity != null) {
         this.awardStat(Stats.ENTITY_KILLED_BY.get(livingentity.getType()));
         livingentity.awardKillScore(this, this.deathScore, p_70645_1_);
         this.createWitherRose(livingentity);
      }

      this.level.broadcastEntityEvent(this, (byte)3);
      this.awardStat(Stats.DEATHS);
      this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
      this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
      this.clearFire();
      this.setSharedFlag(0, false);
      this.getCombatTracker().recheckStatus();
   }

   private void tellNeutralMobsThatIDied() {
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.blockPosition())).inflate(32.0D, 10.0D, 32.0D);
      this.level.getLoadedEntitiesOfClass(MobEntity.class, axisalignedbb).stream().filter((p_241155_0_) -> {
         return p_241155_0_ instanceof IAngerable;
      }).forEach((p_241145_1_) -> {
         ((IAngerable)p_241145_1_).playerDied(this);
      });
   }

   public void awardKillScore(Entity p_191956_1_, int p_191956_2_, DamageSource p_191956_3_) {
      if (p_191956_1_ != this) {
         super.awardKillScore(p_191956_1_, p_191956_2_, p_191956_3_);
         this.increaseScore(p_191956_2_);
         String s = this.getScoreboardName();
         String s1 = p_191956_1_.getScoreboardName();
         this.getScoreboard().forAllObjectives(ScoreCriteria.KILL_COUNT_ALL, s, Score::increment);
         if (p_191956_1_ instanceof PlayerEntity) {
            this.awardStat(Stats.PLAYER_KILLS);
            this.getScoreboard().forAllObjectives(ScoreCriteria.KILL_COUNT_PLAYERS, s, Score::increment);
         } else {
            this.awardStat(Stats.MOB_KILLS);
         }

         this.handleTeamKill(s, s1, ScoreCriteria.TEAM_KILL);
         this.handleTeamKill(s1, s, ScoreCriteria.KILLED_BY_TEAM);
         CriteriaTriggers.PLAYER_KILLED_ENTITY.trigger(this, p_191956_1_, p_191956_3_);
      }
   }

   private void handleTeamKill(String p_195398_1_, String p_195398_2_, ScoreCriteria[] p_195398_3_) {
      ScorePlayerTeam scoreplayerteam = this.getScoreboard().getPlayersTeam(p_195398_2_);
      if (scoreplayerteam != null) {
         int i = scoreplayerteam.getColor().getId();
         if (i >= 0 && i < p_195398_3_.length) {
            this.getScoreboard().forAllObjectives(p_195398_3_[i], p_195398_1_, Score::increment);
         }
      }

   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         boolean flag = this.server.isDedicatedServer() && this.isPvpAllowed() && "fall".equals(p_70097_1_.msgId);
         if (!flag && this.spawnInvulnerableTime > 0 && p_70097_1_ != DamageSource.OUT_OF_WORLD) {
            return false;
         } else {
            if (p_70097_1_ instanceof EntityDamageSource) {
               Entity entity = p_70097_1_.getEntity();
               if (entity instanceof PlayerEntity && !this.canHarmPlayer((PlayerEntity)entity)) {
                  return false;
               }

               if (entity instanceof AbstractArrowEntity) {
                  AbstractArrowEntity abstractarrowentity = (AbstractArrowEntity)entity;
                  Entity entity1 = abstractarrowentity.getOwner();
                  if (entity1 instanceof PlayerEntity && !this.canHarmPlayer((PlayerEntity)entity1)) {
                     return false;
                  }
               }
            }

            return super.hurt(p_70097_1_, p_70097_2_);
         }
      }
   }

   public boolean canHarmPlayer(PlayerEntity p_96122_1_) {
      return !this.isPvpAllowed() ? false : super.canHarmPlayer(p_96122_1_);
   }

   private boolean isPvpAllowed() {
      return this.server.isPvpAllowed();
   }

   @Nullable
   protected PortalInfo findDimensionEntryPoint(ServerWorld p_241829_1_) {
      PortalInfo portalinfo = super.findDimensionEntryPoint(p_241829_1_);
      if (portalinfo != null && this.level.dimension() == World.OVERWORLD && p_241829_1_.dimension() == World.END) {
         Vector3d vector3d = portalinfo.pos.add(0.0D, -1.0D, 0.0D);
         return new PortalInfo(vector3d, Vector3d.ZERO, 90.0F, 0.0F);
      } else {
         return portalinfo;
      }
   }

   @Nullable
   public Entity changeDimension(ServerWorld p_241206_1_, net.minecraftforge.common.util.ITeleporter teleporter) {
      if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(this, p_241206_1_.dimension())) return null;
      this.isChangingDimension = true;
      ServerWorld serverworld = this.getLevel();
      RegistryKey<World> registrykey = serverworld.dimension();
      if (registrykey == World.END && p_241206_1_.dimension() == World.OVERWORLD && teleporter.isVanilla()) { //Forge: Fix non-vanilla teleporters triggering end credits
         this.unRide();
         this.getLevel().removePlayer(this, true); //Forge: The player entity is cloned so keep the data until after cloning calls copyFrom
         if (!this.wonGame) {
            this.wonGame = true;
            this.connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.WIN_GAME, this.seenCredits ? 0.0F : 1.0F));
            this.seenCredits = true;
         }

         return this;
      } else {
         IWorldInfo iworldinfo = p_241206_1_.getLevelData();
         this.connection.send(new SRespawnPacket(p_241206_1_.dimensionType(), p_241206_1_.dimension(), BiomeManager.obfuscateSeed(p_241206_1_.getSeed()), this.gameMode.getGameModeForPlayer(), this.gameMode.getPreviousGameModeForPlayer(), p_241206_1_.isDebug(), p_241206_1_.isFlat(), true));
         this.connection.send(new SServerDifficultyPacket(iworldinfo.getDifficulty(), iworldinfo.isDifficultyLocked()));
         PlayerList playerlist = this.server.getPlayerList();
         playerlist.sendPlayerPermissionLevel(this);
         serverworld.removeEntity(this, true); //Forge: the player entity is moved to the new world, NOT cloned. So keep the data alive with no matching invalidate call.
         this.revive();
         PortalInfo portalinfo = teleporter.getPortalInfo(this, p_241206_1_, this::findDimensionEntryPoint);
         if (portalinfo != null) {
            Entity e = teleporter.placeEntity(this, serverworld, p_241206_1_, this.yRot, spawnPortal -> {//Forge: Start vanilla logic
            serverworld.getProfiler().push("moving");
            if (registrykey == World.OVERWORLD && p_241206_1_.dimension() == World.NETHER) {
               this.enteredNetherPosition = this.position();
            } else if (spawnPortal && p_241206_1_.dimension() == World.END) {
               this.createEndPlatform(p_241206_1_, new BlockPos(portalinfo.pos));
            }

            serverworld.getProfiler().pop();
            serverworld.getProfiler().push("placing");
            this.setLevel(p_241206_1_);
            p_241206_1_.addDuringPortalTeleport(this);
            this.setRot(portalinfo.yRot, portalinfo.xRot);
            this.moveTo(portalinfo.pos.x, portalinfo.pos.y, portalinfo.pos.z);
            serverworld.getProfiler().pop();
            this.triggerDimensionChangeTriggers(serverworld);
            return this;//forge: this is part of the ITeleporter patch
            });//Forge: End vanilla logic
            if (e != this) throw new java.lang.IllegalArgumentException(String.format("Teleporter %s returned not the player entity but instead %s, expected PlayerEntity %s", teleporter, e, this));
            this.gameMode.setLevel(p_241206_1_);
            this.connection.send(new SPlayerAbilitiesPacket(this.abilities));
            playerlist.sendLevelInfo(this, p_241206_1_);
            playerlist.sendAllPlayerInfo(this);

            for(EffectInstance effectinstance : this.getActiveEffects()) {
               this.connection.send(new SPlayEntityEffectPacket(this.getId(), effectinstance));
            }

            this.connection.send(new SPlaySoundEventPacket(1032, BlockPos.ZERO, 0, false));
            this.lastSentExp = -1;
            this.lastSentHealth = -1.0F;
            this.lastSentFood = -1;
            net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerChangedDimensionEvent(this, registrykey, p_241206_1_.dimension());
         }

         return this;
      }
   }

   private void createEndPlatform(ServerWorld p_242110_1_, BlockPos p_242110_2_) {
      BlockPos.Mutable blockpos$mutable = p_242110_2_.mutable();

      for(int i = -2; i <= 2; ++i) {
         for(int j = -2; j <= 2; ++j) {
            for(int k = -1; k < 3; ++k) {
               BlockState blockstate = k == -1 ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.AIR.defaultBlockState();
               p_242110_1_.setBlockAndUpdate(blockpos$mutable.set(p_242110_2_).move(j, k, i), blockstate);
            }
         }
      }

   }

   protected Optional<TeleportationRepositioner.Result> getExitPortal(ServerWorld p_241830_1_, BlockPos p_241830_2_, boolean p_241830_3_) {
      Optional<TeleportationRepositioner.Result> optional = super.getExitPortal(p_241830_1_, p_241830_2_, p_241830_3_);
      if (optional.isPresent()) {
         return optional;
      } else {
         Direction.Axis direction$axis = this.level.getBlockState(this.portalEntrancePos).getOptionalValue(NetherPortalBlock.AXIS).orElse(Direction.Axis.X);
         Optional<TeleportationRepositioner.Result> optional1 = p_241830_1_.getPortalForcer().createPortal(p_241830_2_, direction$axis);
         if (!optional1.isPresent()) {
            LOGGER.error("Unable to create a portal, likely target out of worldborder");
         }

         return optional1;
      }
   }

   private void triggerDimensionChangeTriggers(ServerWorld p_213846_1_) {
      RegistryKey<World> registrykey = p_213846_1_.dimension();
      RegistryKey<World> registrykey1 = this.level.dimension();
      CriteriaTriggers.CHANGED_DIMENSION.trigger(this, registrykey, registrykey1);
      if (registrykey == World.NETHER && registrykey1 == World.OVERWORLD && this.enteredNetherPosition != null) {
         CriteriaTriggers.NETHER_TRAVEL.trigger(this, this.enteredNetherPosition);
      }

      if (registrykey1 != World.NETHER) {
         this.enteredNetherPosition = null;
      }

   }

   public boolean broadcastToPlayer(ServerPlayerEntity p_174827_1_) {
      if (p_174827_1_.isSpectator()) {
         return this.getCamera() == this;
      } else {
         return this.isSpectator() ? false : super.broadcastToPlayer(p_174827_1_);
      }
   }

   private void broadcast(TileEntity p_147097_1_) {
      if (p_147097_1_ != null) {
         SUpdateTileEntityPacket supdatetileentitypacket = p_147097_1_.getUpdatePacket();
         if (supdatetileentitypacket != null) {
            this.connection.send(supdatetileentitypacket);
         }
      }

   }

   public void take(Entity p_71001_1_, int p_71001_2_) {
      super.take(p_71001_1_, p_71001_2_);
      this.containerMenu.broadcastChanges();
   }

   public Either<PlayerEntity.SleepResult, Unit> startSleepInBed(BlockPos p_213819_1_) {
      java.util.Optional<BlockPos> optAt = java.util.Optional.of(p_213819_1_);
      PlayerEntity.SleepResult ret = net.minecraftforge.event.ForgeEventFactory.onPlayerSleepInBed(this, optAt);
      if (ret != null) return Either.left(ret);
      Direction direction = this.level.getBlockState(p_213819_1_).getValue(HorizontalBlock.FACING);
      if (!this.isSleeping() && this.isAlive()) {
         if (!this.level.dimensionType().natural()) {
            return Either.left(PlayerEntity.SleepResult.NOT_POSSIBLE_HERE);
         } else if (!this.bedInRange(p_213819_1_, direction)) {
            return Either.left(PlayerEntity.SleepResult.TOO_FAR_AWAY);
         } else if (this.bedBlocked(p_213819_1_, direction)) {
            return Either.left(PlayerEntity.SleepResult.OBSTRUCTED);
         } else {
            this.setRespawnPosition(this.level.dimension(), p_213819_1_, this.yRot, false, true);
            if (!net.minecraftforge.event.ForgeEventFactory.fireSleepingTimeCheck(this, optAt)) {
               return Either.left(PlayerEntity.SleepResult.NOT_POSSIBLE_NOW);
            } else {
               if (!this.isCreative()) {
                  double d0 = 8.0D;
                  double d1 = 5.0D;
                  Vector3d vector3d = Vector3d.atBottomCenterOf(p_213819_1_);
                  List<MonsterEntity> list = this.level.getEntitiesOfClass(MonsterEntity.class, new AxisAlignedBB(vector3d.x() - 8.0D, vector3d.y() - 5.0D, vector3d.z() - 8.0D, vector3d.x() + 8.0D, vector3d.y() + 5.0D, vector3d.z() + 8.0D), (p_241146_1_) -> {
                     return p_241146_1_.isPreventingPlayerRest(this);
                  });
                  if (!list.isEmpty()) {
                     return Either.left(PlayerEntity.SleepResult.NOT_SAFE);
                  }
               }

               Either<PlayerEntity.SleepResult, Unit> either = super.startSleepInBed(p_213819_1_).ifRight((p_241144_1_) -> {
                  this.awardStat(Stats.SLEEP_IN_BED);
                  CriteriaTriggers.SLEPT_IN_BED.trigger(this);
               });
               ((ServerWorld)this.level).updateSleepingPlayerList();
               return either;
            }
         }
      } else {
         return Either.left(PlayerEntity.SleepResult.OTHER_PROBLEM);
      }
   }

   public void startSleeping(BlockPos p_213342_1_) {
      this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
      super.startSleeping(p_213342_1_);
   }

   private boolean bedInRange(BlockPos p_241147_1_, Direction p_241147_2_) {
      if (p_241147_2_ == null) return false;
      return this.isReachableBedBlock(p_241147_1_) || this.isReachableBedBlock(p_241147_1_.relative(p_241147_2_.getOpposite()));
   }

   private boolean isReachableBedBlock(BlockPos p_241158_1_) {
      Vector3d vector3d = Vector3d.atBottomCenterOf(p_241158_1_);
      return Math.abs(this.getX() - vector3d.x()) <= 3.0D && Math.abs(this.getY() - vector3d.y()) <= 2.0D && Math.abs(this.getZ() - vector3d.z()) <= 3.0D;
   }

   private boolean bedBlocked(BlockPos p_241156_1_, Direction p_241156_2_) {
      BlockPos blockpos = p_241156_1_.above();
      return !this.freeAt(blockpos) || !this.freeAt(blockpos.relative(p_241156_2_.getOpposite()));
   }

   public void stopSleepInBed(boolean p_225652_1_, boolean p_225652_2_) {
      if (this.isSleeping()) {
         this.getLevel().getChunkSource().broadcastAndSend(this, new SAnimateHandPacket(this, 2));
      }

      super.stopSleepInBed(p_225652_1_, p_225652_2_);
      if (this.connection != null) {
         this.connection.teleport(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
      }

   }

   public boolean startRiding(Entity p_184205_1_, boolean p_184205_2_) {
      Entity entity = this.getVehicle();
      if (!super.startRiding(p_184205_1_, p_184205_2_)) {
         return false;
      } else {
         Entity entity1 = this.getVehicle();
         if (entity1 != entity && this.connection != null) {
            this.connection.teleport(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
         }

         return true;
      }
   }

   public void stopRiding() {
      Entity entity = this.getVehicle();
      super.stopRiding();
      Entity entity1 = this.getVehicle();
      if (entity1 != entity && this.connection != null) {
         this.connection.teleport(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
      }

   }

   public boolean isInvulnerableTo(DamageSource p_180431_1_) {
      return super.isInvulnerableTo(p_180431_1_) || this.isChangingDimension() || this.abilities.invulnerable && p_180431_1_ == DamageSource.WITHER;
   }

   protected void checkFallDamage(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
   }

   protected void onChangedBlock(BlockPos p_184594_1_) {
      if (!this.isSpectator()) {
         super.onChangedBlock(p_184594_1_);
      }

   }

   public void doCheckFallDamage(double p_71122_1_, boolean p_71122_3_) {
      BlockPos blockpos = this.getOnPos();
      if (this.level.hasChunkAt(blockpos)) {
         super.checkFallDamage(p_71122_1_, p_71122_3_, this.level.getBlockState(blockpos), blockpos);
      }
   }

   public void openTextEdit(SignTileEntity p_175141_1_) {
      p_175141_1_.setAllowedPlayerEditor(this);
      this.connection.send(new SOpenSignMenuPacket(p_175141_1_.getBlockPos()));
   }

   public void nextContainerCounter() {
      this.containerCounter = this.containerCounter % 100 + 1;
   }

   public OptionalInt openMenu(@Nullable INamedContainerProvider p_213829_1_) {
      if (p_213829_1_ == null) {
         return OptionalInt.empty();
      } else {
         if (this.containerMenu != this.inventoryMenu) {
            this.closeContainer();
         }

         this.nextContainerCounter();
         Container container = p_213829_1_.createMenu(this.containerCounter, this.inventory, this);
         if (container == null) {
            if (this.isSpectator()) {
               this.displayClientMessage((new TranslationTextComponent("container.spectatorCantOpen")).withStyle(TextFormatting.RED), true);
            }

            return OptionalInt.empty();
         } else {
            this.connection.send(new SOpenWindowPacket(container.containerId, container.getType(), p_213829_1_.getDisplayName()));
            container.addSlotListener(this);
            this.containerMenu = container;
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(this, this.containerMenu));
            return OptionalInt.of(this.containerCounter);
         }
      }
   }

   public void sendMerchantOffers(int p_213818_1_, MerchantOffers p_213818_2_, int p_213818_3_, int p_213818_4_, boolean p_213818_5_, boolean p_213818_6_) {
      this.connection.send(new SMerchantOffersPacket(p_213818_1_, p_213818_2_, p_213818_3_, p_213818_4_, p_213818_5_, p_213818_6_));
   }

   public void openHorseInventory(AbstractHorseEntity p_184826_1_, IInventory p_184826_2_) {
      if (this.containerMenu != this.inventoryMenu) {
         this.closeContainer();
      }

      this.nextContainerCounter();
      this.connection.send(new SOpenHorseWindowPacket(this.containerCounter, p_184826_2_.getContainerSize(), p_184826_1_.getId()));
      this.containerMenu = new HorseInventoryContainer(this.containerCounter, this.inventory, p_184826_2_, p_184826_1_);
      this.containerMenu.addSlotListener(this);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(this, this.containerMenu));
   }

   public void openItemGui(ItemStack p_184814_1_, Hand p_184814_2_) {
      Item item = p_184814_1_.getItem();
      if (item == Items.WRITTEN_BOOK) {
         if (WrittenBookItem.resolveBookComponents(p_184814_1_, this.createCommandSourceStack(), this)) {
            this.containerMenu.broadcastChanges();
         }

         this.connection.send(new SOpenBookWindowPacket(p_184814_2_));
      }

   }

   public void openCommandBlock(CommandBlockTileEntity p_184824_1_) {
      p_184824_1_.setSendToClient(true);
      this.broadcast(p_184824_1_);
   }

   public void slotChanged(Container p_71111_1_, int p_71111_2_, ItemStack p_71111_3_) {
      if (!(p_71111_1_.getSlot(p_71111_2_) instanceof CraftingResultSlot)) {
         if (p_71111_1_ == this.inventoryMenu) {
            CriteriaTriggers.INVENTORY_CHANGED.trigger(this, this.inventory, p_71111_3_);
         }

         if (!this.ignoreSlotUpdateHack) {
            this.connection.send(new SSetSlotPacket(p_71111_1_.containerId, p_71111_2_, p_71111_3_));
         }
      }
   }

   public void refreshContainer(Container p_71120_1_) {
      this.refreshContainer(p_71120_1_, p_71120_1_.getItems());
   }

   public void refreshContainer(Container p_71110_1_, NonNullList<ItemStack> p_71110_2_) {
      this.connection.send(new SWindowItemsPacket(p_71110_1_.containerId, p_71110_2_));
      this.connection.send(new SSetSlotPacket(-1, -1, this.inventory.getCarried()));
   }

   public void setContainerData(Container p_71112_1_, int p_71112_2_, int p_71112_3_) {
      this.connection.send(new SWindowPropertyPacket(p_71112_1_.containerId, p_71112_2_, p_71112_3_));
   }

   public void closeContainer() {
      this.connection.send(new SCloseWindowPacket(this.containerMenu.containerId));
      this.doCloseContainer();
   }

   public void broadcastCarriedItem() {
      if (!this.ignoreSlotUpdateHack) {
         this.connection.send(new SSetSlotPacket(-1, -1, this.inventory.getCarried()));
      }
   }

   public void doCloseContainer() {
      this.containerMenu.removed(this);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Close(this, this.containerMenu));
      this.containerMenu = this.inventoryMenu;
   }

   public void setPlayerInput(float p_110430_1_, float p_110430_2_, boolean p_110430_3_, boolean p_110430_4_) {
      if (this.isPassenger()) {
         if (p_110430_1_ >= -1.0F && p_110430_1_ <= 1.0F) {
            this.xxa = p_110430_1_;
         }

         if (p_110430_2_ >= -1.0F && p_110430_2_ <= 1.0F) {
            this.zza = p_110430_2_;
         }

         this.jumping = p_110430_3_;
         this.setShiftKeyDown(p_110430_4_);
      }

   }

   public void awardStat(Stat<?> p_71064_1_, int p_71064_2_) {
      this.stats.increment(this, p_71064_1_, p_71064_2_);
      this.getScoreboard().forAllObjectives(p_71064_1_, this.getScoreboardName(), (p_195396_1_) -> {
         p_195396_1_.add(p_71064_2_);
      });
   }

   public void resetStat(Stat<?> p_175145_1_) {
      this.stats.setValue(this, p_175145_1_, 0);
      this.getScoreboard().forAllObjectives(p_175145_1_, this.getScoreboardName(), Score::reset);
   }

   public int awardRecipes(Collection<IRecipe<?>> p_195065_1_) {
      return this.recipeBook.addRecipes(p_195065_1_, this);
   }

   public void awardRecipesByKey(ResourceLocation[] p_193102_1_) {
      List<IRecipe<?>> list = Lists.newArrayList();

      for(ResourceLocation resourcelocation : p_193102_1_) {
         this.server.getRecipeManager().byKey(resourcelocation).ifPresent(list::add);
      }

      this.awardRecipes(list);
   }

   public int resetRecipes(Collection<IRecipe<?>> p_195069_1_) {
      return this.recipeBook.removeRecipes(p_195069_1_, this);
   }

   public void giveExperiencePoints(int p_195068_1_) {
      super.giveExperiencePoints(p_195068_1_);
      this.lastSentExp = -1;
   }

   public void disconnect() {
      this.disconnected = true;
      this.ejectPassengers();
      if (this.isSleeping()) {
         this.stopSleepInBed(true, false);
      }

   }

   public boolean hasDisconnected() {
      return this.disconnected;
   }

   public void resetSentInfo() {
      this.lastSentHealth = -1.0E8F;
   }

   public void displayClientMessage(ITextComponent p_146105_1_, boolean p_146105_2_) {
      this.connection.send(new SChatPacket(p_146105_1_, p_146105_2_ ? ChatType.GAME_INFO : ChatType.CHAT, Util.NIL_UUID));
   }

   protected void completeUsingItem() {
      if (!this.useItem.isEmpty() && this.isUsingItem()) {
         this.connection.send(new SEntityStatusPacket(this, (byte)9));
         super.completeUsingItem();
      }

   }

   public void lookAt(EntityAnchorArgument.Type p_200602_1_, Vector3d p_200602_2_) {
      super.lookAt(p_200602_1_, p_200602_2_);
      this.connection.send(new SPlayerLookPacket(p_200602_1_, p_200602_2_.x, p_200602_2_.y, p_200602_2_.z));
   }

   public void lookAt(EntityAnchorArgument.Type p_200618_1_, Entity p_200618_2_, EntityAnchorArgument.Type p_200618_3_) {
      Vector3d vector3d = p_200618_3_.apply(p_200618_2_);
      super.lookAt(p_200618_1_, vector3d);
      this.connection.send(new SPlayerLookPacket(p_200618_1_, p_200618_2_, p_200618_3_));
   }

   public void restoreFrom(ServerPlayerEntity p_193104_1_, boolean p_193104_2_) {
      if (p_193104_2_) {
         this.inventory.replaceWith(p_193104_1_.inventory);
         this.setHealth(p_193104_1_.getHealth());
         this.foodData = p_193104_1_.foodData;
         this.experienceLevel = p_193104_1_.experienceLevel;
         this.totalExperience = p_193104_1_.totalExperience;
         this.experienceProgress = p_193104_1_.experienceProgress;
         this.setScore(p_193104_1_.getScore());
         this.portalEntrancePos = p_193104_1_.portalEntrancePos;
      } else if (this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || p_193104_1_.isSpectator()) {
         this.inventory.replaceWith(p_193104_1_.inventory);
         this.experienceLevel = p_193104_1_.experienceLevel;
         this.totalExperience = p_193104_1_.totalExperience;
         this.experienceProgress = p_193104_1_.experienceProgress;
         this.setScore(p_193104_1_.getScore());
      }

      this.enchantmentSeed = p_193104_1_.enchantmentSeed;
      this.enderChestInventory = p_193104_1_.enderChestInventory;
      this.getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, p_193104_1_.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION));
      this.lastSentExp = -1;
      this.lastSentHealth = -1.0F;
      this.lastSentFood = -1;
      this.recipeBook.copyOverData(p_193104_1_.recipeBook);
      this.entitiesToRemove.addAll(p_193104_1_.entitiesToRemove);
      this.seenCredits = p_193104_1_.seenCredits;
      this.enteredNetherPosition = p_193104_1_.enteredNetherPosition;
      this.setShoulderEntityLeft(p_193104_1_.getShoulderEntityLeft());
      this.setShoulderEntityRight(p_193104_1_.getShoulderEntityRight());

      //Copy over a section of the Entity Data from the old player.
      //Allows mods to specify data that persists after players respawn.
      CompoundNBT old = p_193104_1_.getPersistentData();
      if (old.contains(PERSISTED_NBT_TAG))
          getPersistentData().put(PERSISTED_NBT_TAG, old.get(PERSISTED_NBT_TAG));
      net.minecraftforge.event.ForgeEventFactory.onPlayerClone(this, p_193104_1_, !p_193104_2_);
   }

   protected void onEffectAdded(EffectInstance p_70670_1_) {
      super.onEffectAdded(p_70670_1_);
      this.connection.send(new SPlayEntityEffectPacket(this.getId(), p_70670_1_));
      if (p_70670_1_.getEffect() == Effects.LEVITATION) {
         this.levitationStartTime = this.tickCount;
         this.levitationStartPos = this.position();
      }

      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   protected void onEffectUpdated(EffectInstance p_70695_1_, boolean p_70695_2_) {
      super.onEffectUpdated(p_70695_1_, p_70695_2_);
      this.connection.send(new SPlayEntityEffectPacket(this.getId(), p_70695_1_));
      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   protected void onEffectRemoved(EffectInstance p_70688_1_) {
      super.onEffectRemoved(p_70688_1_);
      this.connection.send(new SRemoveEntityEffectPacket(this.getId(), p_70688_1_.getEffect()));
      if (p_70688_1_.getEffect() == Effects.LEVITATION) {
         this.levitationStartPos = null;
      }

      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   public void teleportTo(double p_70634_1_, double p_70634_3_, double p_70634_5_) {
      this.connection.teleport(p_70634_1_, p_70634_3_, p_70634_5_, this.yRot, this.xRot);
   }

   public void moveTo(double p_225653_1_, double p_225653_3_, double p_225653_5_) {
      this.teleportTo(p_225653_1_, p_225653_3_, p_225653_5_);
      this.connection.resetPosition();
   }

   public void crit(Entity p_71009_1_) {
      this.getLevel().getChunkSource().broadcastAndSend(this, new SAnimateHandPacket(p_71009_1_, 4));
   }

   public void magicCrit(Entity p_71047_1_) {
      this.getLevel().getChunkSource().broadcastAndSend(this, new SAnimateHandPacket(p_71047_1_, 5));
   }

   public void onUpdateAbilities() {
      if (this.connection != null) {
         this.connection.send(new SPlayerAbilitiesPacket(this.abilities));
         this.updateInvisibilityStatus();
      }
   }

   public ServerWorld getLevel() {
      return (ServerWorld)this.level;
   }

   public void setGameMode(GameType p_71033_1_) {
      if (!net.minecraftforge.common.ForgeHooks.onChangeGameMode(this, this.gameMode.getGameModeForPlayer(), p_71033_1_)) return;
      this.gameMode.setGameModeForPlayer(p_71033_1_);
      this.connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.CHANGE_GAME_MODE, (float)p_71033_1_.getId()));
      if (p_71033_1_ == GameType.SPECTATOR) {
         this.removeEntitiesOnShoulder();
         this.stopRiding();
      } else {
         this.setCamera(this);
      }

      this.onUpdateAbilities();
      this.updateEffectVisibility();
   }

   public boolean isSpectator() {
      return this.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
   }

   public boolean isCreative() {
      return this.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
   }

   public void sendMessage(ITextComponent p_145747_1_, UUID p_145747_2_) {
      this.sendMessage(p_145747_1_, ChatType.SYSTEM, p_145747_2_);
   }

   public void sendMessage(ITextComponent p_241151_1_, ChatType p_241151_2_, UUID p_241151_3_) {
      this.connection.send(new SChatPacket(p_241151_1_, p_241151_2_, p_241151_3_), (p_241149_4_) -> {
         if (!p_241149_4_.isSuccess() && (p_241151_2_ == ChatType.GAME_INFO || p_241151_2_ == ChatType.SYSTEM)) {
            int i = 256;
            String s = p_241151_1_.getString(256);
            ITextComponent itextcomponent = (new StringTextComponent(s)).withStyle(TextFormatting.YELLOW);
            this.connection.send(new SChatPacket((new TranslationTextComponent("multiplayer.message_not_delivered", itextcomponent)).withStyle(TextFormatting.RED), ChatType.SYSTEM, p_241151_3_));
         }

      });
   }

   public String getIpAddress() {
      String s = this.connection.connection.getRemoteAddress().toString();
      s = s.substring(s.indexOf("/") + 1);
      return s.substring(0, s.indexOf(":"));
   }

   public void updateOptions(CClientSettingsPacket p_147100_1_) {
      this.chatVisibility = p_147100_1_.getChatVisibility();
      this.canChatColor = p_147100_1_.getChatColors();
      this.getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, (byte)p_147100_1_.getModelCustomisation());
      this.getEntityData().set(DATA_PLAYER_MAIN_HAND, (byte)(p_147100_1_.getMainHand() == HandSide.LEFT ? 0 : 1));
      this.language = p_147100_1_.getLanguage();
   }

   public ChatVisibility getChatVisibility() {
      return this.chatVisibility;
   }

   public void sendTexturePack(String p_175397_1_, String p_175397_2_) {
      this.connection.send(new SSendResourcePackPacket(p_175397_1_, p_175397_2_));
   }

   protected int getPermissionLevel() {
      return this.server.getProfilePermissions(this.getGameProfile());
   }

   public void resetLastActionTime() {
      this.lastActionTime = Util.getMillis();
   }

   public ServerStatisticsManager getStats() {
      return this.stats;
   }

   public ServerRecipeBook getRecipeBook() {
      return this.recipeBook;
   }

   public void sendRemoveEntity(Entity p_152339_1_) {
      if (p_152339_1_ instanceof PlayerEntity) {
         this.connection.send(new SDestroyEntitiesPacket(p_152339_1_.getId()));
      } else {
         this.entitiesToRemove.add(p_152339_1_.getId());
      }

   }

   public void cancelRemoveEntity(Entity p_184848_1_) {
      this.entitiesToRemove.remove(Integer.valueOf(p_184848_1_.getId()));
   }

   protected void updateInvisibilityStatus() {
      if (this.isSpectator()) {
         this.removeEffectParticles();
         this.setInvisible(true);
      } else {
         super.updateInvisibilityStatus();
      }

   }

   public Entity getCamera() {
      return (Entity)(this.camera == null ? this : this.camera);
   }

   public void setCamera(Entity p_175399_1_) {
      Entity entity = this.getCamera();
      this.camera = (Entity)(p_175399_1_ == null ? this : p_175399_1_);
      if (entity != this.camera) {
         this.connection.send(new SCameraPacket(this.camera));
         this.teleportTo(this.camera.getX(), this.camera.getY(), this.camera.getZ());
      }

   }

   protected void processPortalCooldown() {
      if (!this.isChangingDimension) {
         super.processPortalCooldown();
      }

   }

   public void attack(Entity p_71059_1_) {
      if (this.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
         this.setCamera(p_71059_1_);
      } else {
         super.attack(p_71059_1_);
      }

   }

   public long getLastActionTime() {
      return this.lastActionTime;
   }

   @Nullable
   public ITextComponent getTabListDisplayName() {
      return null;
   }

   public void swing(Hand p_184609_1_) {
      super.swing(p_184609_1_);
      this.resetAttackStrengthTicker();
   }

   public boolean isChangingDimension() {
      return this.isChangingDimension;
   }

   public void hasChangedDimension() {
      this.isChangingDimension = false;
   }

   public PlayerAdvancements getAdvancements() {
      return this.advancements;
   }

   public void teleportTo(ServerWorld p_200619_1_, double p_200619_2_, double p_200619_4_, double p_200619_6_, float p_200619_8_, float p_200619_9_) {
      this.setCamera(this);
      this.stopRiding();
      if (p_200619_1_ == this.level) {
         this.connection.teleport(p_200619_2_, p_200619_4_, p_200619_6_, p_200619_8_, p_200619_9_);
      } else if (net.minecraftforge.common.ForgeHooks.onTravelToDimension(this, p_200619_1_.dimension())) {
         ServerWorld serverworld = this.getLevel();
         IWorldInfo iworldinfo = p_200619_1_.getLevelData();
         this.connection.send(new SRespawnPacket(p_200619_1_.dimensionType(), p_200619_1_.dimension(), BiomeManager.obfuscateSeed(p_200619_1_.getSeed()), this.gameMode.getGameModeForPlayer(), this.gameMode.getPreviousGameModeForPlayer(), p_200619_1_.isDebug(), p_200619_1_.isFlat(), true));
         this.connection.send(new SServerDifficultyPacket(iworldinfo.getDifficulty(), iworldinfo.isDifficultyLocked()));
         this.server.getPlayerList().sendPlayerPermissionLevel(this);
         serverworld.removePlayer(this, true); //Forge: The player entity itself is moved, and not cloned. So we need to keep the data alive with no matching invalidate call later.
         this.revive();
         this.moveTo(p_200619_2_, p_200619_4_, p_200619_6_, p_200619_8_, p_200619_9_);
         this.setLevel(p_200619_1_);
         p_200619_1_.addDuringCommandTeleport(this);
         this.triggerDimensionChangeTriggers(serverworld);
         this.connection.teleport(p_200619_2_, p_200619_4_, p_200619_6_, p_200619_8_, p_200619_9_);
         this.gameMode.setLevel(p_200619_1_);
         this.server.getPlayerList().sendLevelInfo(this, p_200619_1_);
         this.server.getPlayerList().sendAllPlayerInfo(this);
         net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerChangedDimensionEvent(this, serverworld.dimension(), p_200619_1_.dimension());
      }

   }

   @Nullable
   public BlockPos getRespawnPosition() {
      return this.respawnPosition;
   }

   public float getRespawnAngle() {
      return this.respawnAngle;
   }

   public RegistryKey<World> getRespawnDimension() {
      return this.respawnDimension;
   }

   public boolean isRespawnForced() {
      return this.respawnForced;
   }

   public void setRespawnPosition(RegistryKey<World> p_242111_1_, @Nullable BlockPos p_242111_2_, float p_242111_3_, boolean p_242111_4_, boolean p_242111_5_) {
      if (net.minecraftforge.event.ForgeEventFactory.onPlayerSpawnSet(this, p_242111_2_ == null ? World.OVERWORLD : p_242111_1_, p_242111_2_, p_242111_4_)) return;
      if (p_242111_2_ != null) {
         boolean flag = p_242111_2_.equals(this.respawnPosition) && p_242111_1_.equals(this.respawnDimension);
         if (p_242111_5_ && !flag) {
            this.sendMessage(new TranslationTextComponent("block.minecraft.set_spawn"), Util.NIL_UUID);
         }

         this.respawnPosition = p_242111_2_;
         this.respawnDimension = p_242111_1_;
         this.respawnAngle = p_242111_3_;
         this.respawnForced = p_242111_4_;
      } else {
         this.respawnPosition = null;
         this.respawnDimension = World.OVERWORLD;
         this.respawnAngle = 0.0F;
         this.respawnForced = false;
      }

   }

   public void trackChunk(ChunkPos p_213844_1_, IPacket<?> p_213844_2_, IPacket<?> p_213844_3_) {
      this.connection.send(p_213844_3_);
      this.connection.send(p_213844_2_);
   }

   public void untrackChunk(ChunkPos p_213845_1_) {
      if (this.isAlive()) {
         this.connection.send(new SUnloadChunkPacket(p_213845_1_.x, p_213845_1_.z));
      }

   }

   public SectionPos getLastSectionPos() {
      return this.lastSectionPos;
   }

   public void setLastSectionPos(SectionPos p_213850_1_) {
      this.lastSectionPos = p_213850_1_;
   }

   public void playNotifySound(SoundEvent p_213823_1_, SoundCategory p_213823_2_, float p_213823_3_, float p_213823_4_) {
      this.connection.send(new SPlaySoundEffectPacket(p_213823_1_, p_213823_2_, this.getX(), this.getY(), this.getZ(), p_213823_3_, p_213823_4_));
   }

   public IPacket<?> getAddEntityPacket() {
      return new SSpawnPlayerPacket(this);
   }

   public ItemEntity drop(ItemStack p_146097_1_, boolean p_146097_2_, boolean p_146097_3_) {
      ItemEntity itementity = super.drop(p_146097_1_, p_146097_2_, p_146097_3_);
      if (itementity == null) {
         return null;
      } else {
         if (captureDrops() != null) captureDrops().add(itementity);
         else
         this.level.addFreshEntity(itementity);
         ItemStack itemstack = itementity.getItem();
         if (p_146097_3_) {
            if (!itemstack.isEmpty()) {
               this.awardStat(Stats.ITEM_DROPPED.get(itemstack.getItem()), p_146097_1_.getCount());
            }

            this.awardStat(Stats.DROP);
         }

         return itementity;
      }
   }

   private String language = "en_us";
   /**
    * Returns the language last reported by the player as their local language.
    * Defaults to en_us if the value is unknown.
    */
   public String getLanguage() {
      return this.language;
   }

   @Nullable
   public IChatFilter getTextFilter() {
      return this.textFilter;
   }
}
