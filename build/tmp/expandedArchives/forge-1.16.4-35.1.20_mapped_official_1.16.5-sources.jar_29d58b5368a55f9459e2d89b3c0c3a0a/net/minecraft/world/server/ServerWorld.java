package net.minecraft.world.server;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.INPC;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.merchant.IReputationTracking;
import net.minecraft.entity.merchant.IReputationType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SAnimateBlockBreakPacket;
import net.minecraft.network.play.server.SBlockActionPacket;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEventPacket;
import net.minecraft.network.play.server.SSpawnMovingSoundEffectPacket;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.network.play.server.SWorldSpawnChangedPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.profiler.IProfiler;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CSVWriter;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Explosion;
import net.minecraft.world.ExplosionContext;
import net.minecraft.world.ForcedChunksSaveData;
import net.minecraft.world.GameRules;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.raid.RaidManager;
import net.minecraft.world.spawner.ISpecialSpawner;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapIdTracker;
import net.minecraft.world.storage.SaveFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerWorld extends World implements ISeedReader, net.minecraftforge.common.extensions.IForgeWorldServer {
   public static final BlockPos END_SPAWN_POINT = new BlockPos(100, 50, 0);
   private static final Logger LOGGER = LogManager.getLogger();
   private final Int2ObjectMap<Entity> entitiesById = new Int2ObjectLinkedOpenHashMap<>();
   private final Map<UUID, Entity> entitiesByUuid = Maps.newHashMap();
   private final Queue<Entity> toAddAfterTick = Queues.newArrayDeque();
   private final List<ServerPlayerEntity> players = Lists.newArrayList();
   private final ServerChunkProvider chunkSource;
   boolean tickingEntities;
   private final MinecraftServer server;
   private final IServerWorldInfo serverLevelData;
   public boolean noSave;
   private boolean allPlayersSleeping;
   private int emptyTime;
   private final Teleporter portalForcer;
   private final ServerTickList<Block> blockTicks = new ServerTickList<>(this, (p_205341_0_) -> {
      return p_205341_0_ == null || p_205341_0_.defaultBlockState().isAir();
   }, Registry.BLOCK::getKey, this::tickBlock);
   private final ServerTickList<Fluid> liquidTicks = new ServerTickList<>(this, (p_205774_0_) -> {
      return p_205774_0_ == null || p_205774_0_ == Fluids.EMPTY;
   }, Registry.FLUID::getKey, this::tickLiquid);
   private final Set<PathNavigator> navigations = Sets.newHashSet();
   protected final RaidManager raids;
   private final ObjectLinkedOpenHashSet<BlockEventData> blockEvents = new ObjectLinkedOpenHashSet<>();
   private boolean handlingTick;
   private final List<ISpecialSpawner> customSpawners;
   @Nullable
   private final DragonFightManager dragonFight;
   private final StructureManager structureFeatureManager;
   private final boolean tickTime;
   private net.minecraftforge.common.util.WorldCapabilityData capabilityData;

   public ServerWorld(MinecraftServer p_i241885_1_, Executor p_i241885_2_, SaveFormat.LevelSave p_i241885_3_, IServerWorldInfo p_i241885_4_, RegistryKey<World> p_i241885_5_, DimensionType p_i241885_6_, IChunkStatusListener p_i241885_7_, ChunkGenerator p_i241885_8_, boolean p_i241885_9_, long p_i241885_10_, List<ISpecialSpawner> p_i241885_12_, boolean p_i241885_13_) {
      super(p_i241885_4_, p_i241885_5_, p_i241885_6_, p_i241885_1_::getProfiler, false, p_i241885_9_, p_i241885_10_);
      this.tickTime = p_i241885_13_;
      this.server = p_i241885_1_;
      this.customSpawners = p_i241885_12_;
      this.serverLevelData = p_i241885_4_;
      this.chunkSource = new ServerChunkProvider(this, p_i241885_3_, p_i241885_1_.getFixerUpper(), p_i241885_1_.getStructureManager(), p_i241885_2_, p_i241885_8_, p_i241885_1_.getPlayerList().getViewDistance(), p_i241885_1_.forceSynchronousWrites(), p_i241885_7_, () -> {
         return p_i241885_1_.overworld().getDataStorage();
      });
      this.portalForcer = new Teleporter(this);
      this.updateSkyBrightness();
      this.prepareWeather();
      this.getWorldBorder().setAbsoluteMaxSize(p_i241885_1_.getAbsoluteMaxWorldSize());
      this.raids = this.getDataStorage().computeIfAbsent(() -> {
         return new RaidManager(this);
      }, RaidManager.getFileId(this.dimensionType()));
      if (!p_i241885_1_.isSingleplayer()) {
         p_i241885_4_.setGameType(p_i241885_1_.getDefaultGameType());
      }

      this.structureFeatureManager = new StructureManager(this, p_i241885_1_.getWorldData().worldGenSettings());
      if (this.dimensionType().createDragonFight()) {
         this.dragonFight = new DragonFightManager(this, p_i241885_1_.getWorldData().worldGenSettings().seed(), p_i241885_1_.getWorldData().endDragonFightData());
      } else {
         this.dragonFight = null;
      }
      this.initCapabilities();
   }

   public void setWeatherParameters(int p_241113_1_, int p_241113_2_, boolean p_241113_3_, boolean p_241113_4_) {
      this.serverLevelData.setClearWeatherTime(p_241113_1_);
      this.serverLevelData.setRainTime(p_241113_2_);
      this.serverLevelData.setThunderTime(p_241113_2_);
      this.serverLevelData.setRaining(p_241113_3_);
      this.serverLevelData.setThundering(p_241113_4_);
   }

   public Biome getUncachedNoiseBiome(int p_225604_1_, int p_225604_2_, int p_225604_3_) {
      return this.getChunkSource().getGenerator().getBiomeSource().getNoiseBiome(p_225604_1_, p_225604_2_, p_225604_3_);
   }

   public StructureManager structureFeatureManager() {
      return this.structureFeatureManager;
   }

   public void tick(BooleanSupplier p_72835_1_) {
      IProfiler iprofiler = this.getProfiler();
      this.handlingTick = true;
      iprofiler.push("world border");
      this.getWorldBorder().tick();
      iprofiler.popPush("weather");
      boolean flag = this.isRaining();
      if (this.dimensionType().hasSkyLight()) {
         if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
            int i = this.serverLevelData.getClearWeatherTime();
            int j = this.serverLevelData.getThunderTime();
            int k = this.serverLevelData.getRainTime();
            boolean flag1 = this.levelData.isThundering();
            boolean flag2 = this.levelData.isRaining();
            if (i > 0) {
               --i;
               j = flag1 ? 0 : 1;
               k = flag2 ? 0 : 1;
               flag1 = false;
               flag2 = false;
            } else {
               if (j > 0) {
                  --j;
                  if (j == 0) {
                     flag1 = !flag1;
                  }
               } else if (flag1) {
                  j = this.random.nextInt(12000) + 3600;
               } else {
                  j = this.random.nextInt(168000) + 12000;
               }

               if (k > 0) {
                  --k;
                  if (k == 0) {
                     flag2 = !flag2;
                  }
               } else if (flag2) {
                  k = this.random.nextInt(12000) + 12000;
               } else {
                  k = this.random.nextInt(168000) + 12000;
               }
            }

            this.serverLevelData.setThunderTime(j);
            this.serverLevelData.setRainTime(k);
            this.serverLevelData.setClearWeatherTime(i);
            this.serverLevelData.setThundering(flag1);
            this.serverLevelData.setRaining(flag2);
         }

         this.oThunderLevel = this.thunderLevel;
         if (this.levelData.isThundering()) {
            this.thunderLevel = (float)((double)this.thunderLevel + 0.01D);
         } else {
            this.thunderLevel = (float)((double)this.thunderLevel - 0.01D);
         }

         this.thunderLevel = MathHelper.clamp(this.thunderLevel, 0.0F, 1.0F);
         this.oRainLevel = this.rainLevel;
         if (this.levelData.isRaining()) {
            this.rainLevel = (float)((double)this.rainLevel + 0.01D);
         } else {
            this.rainLevel = (float)((double)this.rainLevel - 0.01D);
         }

         this.rainLevel = MathHelper.clamp(this.rainLevel, 0.0F, 1.0F);
      }

      if (this.oRainLevel != this.rainLevel) {
         this.server.getPlayerList().broadcastAll(new SChangeGameStatePacket(SChangeGameStatePacket.RAIN_LEVEL_CHANGE, this.rainLevel), this.dimension());
      }

      if (this.oThunderLevel != this.thunderLevel) {
         this.server.getPlayerList().broadcastAll(new SChangeGameStatePacket(SChangeGameStatePacket.THUNDER_LEVEL_CHANGE, this.thunderLevel), this.dimension());
      }

      /* The function in use here has been replaced in order to only send the weather info to players in the correct dimension,
       * rather than to all players on the server. This is what causes the client-side rain, as the
       * client believes that it has started raining locally, rather than in another dimension.
       */
      if (flag != this.isRaining()) {
         if (flag) {
            this.server.getPlayerList().broadcastAll(new SChangeGameStatePacket(SChangeGameStatePacket.STOP_RAINING, 0.0F), this.dimension());
         } else {
            this.server.getPlayerList().broadcastAll(new SChangeGameStatePacket(SChangeGameStatePacket.START_RAINING, 0.0F), this.dimension());
         }

         this.server.getPlayerList().broadcastAll(new SChangeGameStatePacket(SChangeGameStatePacket.RAIN_LEVEL_CHANGE, this.rainLevel), this.dimension());
         this.server.getPlayerList().broadcastAll(new SChangeGameStatePacket(SChangeGameStatePacket.THUNDER_LEVEL_CHANGE, this.thunderLevel), this.dimension());
      }

      if (this.allPlayersSleeping && this.players.stream().noneMatch((p_241132_0_) -> {
         return !p_241132_0_.isSpectator() && !p_241132_0_.isSleepingLongEnough();
      })) {
         this.allPlayersSleeping = false;
         if (this.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            long l = this.getDayTime() + 24000L;
            this.setDayTime(net.minecraftforge.event.ForgeEventFactory.onSleepFinished(this, l - l % 24000L, this.getDayTime()));
         }

         this.wakeUpAllPlayers();
         if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
            this.stopWeather();
         }
      }

      this.updateSkyBrightness();
      this.tickTime();
      iprofiler.popPush("chunkSource");
      this.getChunkSource().tick(p_72835_1_);
      iprofiler.popPush("tickPending");
      if (!this.isDebug()) {
         this.blockTicks.tick();
         this.liquidTicks.tick();
      }

      iprofiler.popPush("raid");
      this.raids.tick();
      iprofiler.popPush("blockEvents");
      this.runBlockEvents();
      this.handlingTick = false;
      iprofiler.popPush("entities");
      boolean flag3 = !this.players.isEmpty() || !this.getForcedChunks().isEmpty();
      if (flag3) {
         this.resetEmptyTime();
      }

      if (flag3 || this.emptyTime++ < 300) {
         if (this.dragonFight != null) {
            this.dragonFight.tick();
         }

         this.tickingEntities = true;
         ObjectIterator<Entry<Entity>> objectiterator = this.entitiesById.int2ObjectEntrySet().iterator();

         label164:
         while(true) {
            Entity entity1;
            while(true) {
               if (!objectiterator.hasNext()) {
                  this.tickingEntities = false;

                  Entity entity;
                  while((entity = this.toAddAfterTick.poll()) != null) {
                     this.add(entity);
                  }

                  this.tickBlockEntities();
                  break label164;
               }

               Entry<Entity> entry = objectiterator.next();
               entity1 = entry.getValue();
               Entity entity2 = entity1.getVehicle();
               if (!this.server.isSpawningAnimals() && (entity1 instanceof AnimalEntity || entity1 instanceof WaterMobEntity)) {
                  entity1.remove();
               }

               if (!this.server.areNpcsEnabled() && entity1 instanceof INPC) {
                  entity1.remove();
               }

               iprofiler.push("checkDespawn");
               if (!entity1.removed) {
                  entity1.checkDespawn();
               }

               iprofiler.pop();
               if (entity2 == null) {
                  break;
               }

               if (entity2.removed || !entity2.hasPassenger(entity1)) {
                  entity1.stopRiding();
                  break;
               }
            }

            iprofiler.push("tick");
            if (!entity1.removed && !(entity1 instanceof EnderDragonPartEntity)) {
               this.guardEntityTick(this::tickNonPassenger, entity1);
            }

            iprofiler.pop();
            iprofiler.push("remove");
            if (entity1.removed) {
               this.removeFromChunk(entity1);
               objectiterator.remove();
               this.removeEntityComplete(entity1, entity1 instanceof ServerPlayerEntity); //Forge: Keep cap data until revive. Every other entity removes directly.
            }

            iprofiler.pop();
         }
      }

      iprofiler.pop();
   }

   protected void tickTime() {
      if (this.tickTime) {
         long i = this.levelData.getGameTime() + 1L;
         this.serverLevelData.setGameTime(i);
         this.serverLevelData.getScheduledEvents().tick(this.server, i);
         if (this.levelData.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            this.setDayTime(this.levelData.getDayTime() + 1L);
         }

      }
   }

   public void setDayTime(long p_241114_1_) {
      this.serverLevelData.setDayTime(p_241114_1_);
   }

   public void tickCustomSpawners(boolean p_241123_1_, boolean p_241123_2_) {
      for(ISpecialSpawner ispecialspawner : this.customSpawners) {
         ispecialspawner.tick(this, p_241123_1_, p_241123_2_);
      }

   }

   private void wakeUpAllPlayers() {
      this.players.stream().filter(LivingEntity::isSleeping).collect(Collectors.toList()).forEach((p_241131_0_) -> {
         p_241131_0_.stopSleepInBed(false, false);
      });
   }

   public void tickChunk(Chunk p_217441_1_, int p_217441_2_) {
      ChunkPos chunkpos = p_217441_1_.getPos();
      boolean flag = this.isRaining();
      int i = chunkpos.getMinBlockX();
      int j = chunkpos.getMinBlockZ();
      IProfiler iprofiler = this.getProfiler();
      iprofiler.push("thunder");
      if (flag && this.isThundering() && this.random.nextInt(100000) == 0) {
         BlockPos blockpos = this.findLightingTargetAround(this.getBlockRandomPos(i, 0, j, 15));
         if (this.isRainingAt(blockpos)) {
            DifficultyInstance difficultyinstance = this.getCurrentDifficultyAt(blockpos);
            boolean flag1 = this.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && this.random.nextDouble() < (double)difficultyinstance.getEffectiveDifficulty() * 0.01D;
            if (flag1) {
               SkeletonHorseEntity skeletonhorseentity = EntityType.SKELETON_HORSE.create(this);
               skeletonhorseentity.setTrap(true);
               skeletonhorseentity.setAge(0);
               skeletonhorseentity.setPos((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
               this.addFreshEntity(skeletonhorseentity);
            }

            LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(this);
            lightningboltentity.moveTo(Vector3d.atBottomCenterOf(blockpos));
            lightningboltentity.setVisualOnly(flag1);
            this.addFreshEntity(lightningboltentity);
         }
      }

      iprofiler.popPush("iceandsnow");
      if (this.random.nextInt(16) == 0) {
         BlockPos blockpos2 = this.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, this.getBlockRandomPos(i, 0, j, 15));
         BlockPos blockpos3 = blockpos2.below();
         Biome biome = this.getBiome(blockpos2);
         if (this.isAreaLoaded(blockpos2, 1)) // Forge: check area to avoid loading neighbors in unloaded chunks
         if (biome.shouldFreeze(this, blockpos3)) {
            this.setBlockAndUpdate(blockpos3, Blocks.ICE.defaultBlockState());
         }

         if (flag && biome.shouldSnow(this, blockpos2)) {
            this.setBlockAndUpdate(blockpos2, Blocks.SNOW.defaultBlockState());
         }

         if (flag && this.getBiome(blockpos3).getPrecipitation() == Biome.RainType.RAIN) {
            this.getBlockState(blockpos3).getBlock().handleRain(this, blockpos3);
         }
      }

      iprofiler.popPush("tickBlocks");
      if (p_217441_2_ > 0) {
         for(ChunkSection chunksection : p_217441_1_.getSections()) {
            if (chunksection != Chunk.EMPTY_SECTION && chunksection.isRandomlyTicking()) {
               int k = chunksection.bottomBlockY();

               for(int l = 0; l < p_217441_2_; ++l) {
                  BlockPos blockpos1 = this.getBlockRandomPos(i, k, j, 15);
                  iprofiler.push("randomTick");
                  BlockState blockstate = chunksection.getBlockState(blockpos1.getX() - i, blockpos1.getY() - k, blockpos1.getZ() - j);
                  if (blockstate.isRandomlyTicking()) {
                     blockstate.randomTick(this, blockpos1, this.random);
                  }

                  FluidState fluidstate = blockstate.getFluidState();
                  if (fluidstate.isRandomlyTicking()) {
                     fluidstate.randomTick(this, blockpos1, this.random);
                  }

                  iprofiler.pop();
               }
            }
         }
      }

      iprofiler.pop();
   }

   protected BlockPos findLightingTargetAround(BlockPos p_175736_1_) {
      BlockPos blockpos = this.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, p_175736_1_);
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(blockpos, new BlockPos(blockpos.getX(), this.getMaxBuildHeight(), blockpos.getZ()))).inflate(3.0D);
      List<LivingEntity> list = this.getEntitiesOfClass(LivingEntity.class, axisalignedbb, (p_241115_1_) -> {
         return p_241115_1_ != null && p_241115_1_.isAlive() && this.canSeeSky(p_241115_1_.blockPosition());
      });
      if (!list.isEmpty()) {
         return list.get(this.random.nextInt(list.size())).blockPosition();
      } else {
         if (blockpos.getY() == -1) {
            blockpos = blockpos.above(2);
         }

         return blockpos;
      }
   }

   public boolean isHandlingTick() {
      return this.handlingTick;
   }

   public void updateSleepingPlayerList() {
      this.allPlayersSleeping = false;
      if (!this.players.isEmpty()) {
         int i = 0;
         int j = 0;

         for(ServerPlayerEntity serverplayerentity : this.players) {
            if (serverplayerentity.isSpectator()) {
               ++i;
            } else if (serverplayerentity.isSleeping()) {
               ++j;
            }
         }

         this.allPlayersSleeping = j > 0 && j >= this.players.size() - i;
      }

   }

   public ServerScoreboard getScoreboard() {
      return this.server.getScoreboard();
   }

   private void stopWeather() {
      this.serverLevelData.setRainTime(0);
      this.serverLevelData.setRaining(false);
      this.serverLevelData.setThunderTime(0);
      this.serverLevelData.setThundering(false);
   }

   public void resetEmptyTime() {
      this.emptyTime = 0;
   }

   private void tickLiquid(NextTickListEntry<Fluid> p_205339_1_) {
      FluidState fluidstate = this.getFluidState(p_205339_1_.pos);
      if (fluidstate.getType() == p_205339_1_.getType()) {
         fluidstate.tick(this, p_205339_1_.pos);
      }

   }

   private void tickBlock(NextTickListEntry<Block> p_205338_1_) {
      BlockState blockstate = this.getBlockState(p_205338_1_.pos);
      if (blockstate.is(p_205338_1_.getType())) {
         blockstate.tick(this, p_205338_1_.pos, this.random);
      }

   }

   public void tickNonPassenger(Entity p_217479_1_) {
      if (!(p_217479_1_ instanceof PlayerEntity) && !this.getChunkSource().isEntityTickingChunk(p_217479_1_)) {
         this.updateChunkPos(p_217479_1_);
      } else {
         p_217479_1_.setPosAndOldPos(p_217479_1_.getX(), p_217479_1_.getY(), p_217479_1_.getZ());
         p_217479_1_.yRotO = p_217479_1_.yRot;
         p_217479_1_.xRotO = p_217479_1_.xRot;
         if (p_217479_1_.inChunk) {
            ++p_217479_1_.tickCount;
            IProfiler iprofiler = this.getProfiler();
            iprofiler.push(() -> {
               return p_217479_1_.getType().getRegistryName() == null ? p_217479_1_.getType().toString() : p_217479_1_.getType().getRegistryName().toString();
            });
            iprofiler.incrementCounter("tickNonPassenger");
            if (p_217479_1_.canUpdate())
            p_217479_1_.tick();
            iprofiler.pop();
         }

         this.updateChunkPos(p_217479_1_);
         if (p_217479_1_.inChunk) {
            for(Entity entity : p_217479_1_.getPassengers()) {
               this.tickPassenger(p_217479_1_, entity);
            }
         }

      }
   }

   public void tickPassenger(Entity p_217459_1_, Entity p_217459_2_) {
      if (!p_217459_2_.removed && p_217459_2_.getVehicle() == p_217459_1_) {
         if (p_217459_2_ instanceof PlayerEntity || this.getChunkSource().isEntityTickingChunk(p_217459_2_)) {
            p_217459_2_.setPosAndOldPos(p_217459_2_.getX(), p_217459_2_.getY(), p_217459_2_.getZ());
            p_217459_2_.yRotO = p_217459_2_.yRot;
            p_217459_2_.xRotO = p_217459_2_.xRot;
            if (p_217459_2_.inChunk) {
               ++p_217459_2_.tickCount;
               IProfiler iprofiler = this.getProfiler();
               iprofiler.push(() -> {
                  return Registry.ENTITY_TYPE.getKey(p_217459_2_.getType()).toString();
               });
               iprofiler.incrementCounter("tickPassenger");
               p_217459_2_.rideTick();
               iprofiler.pop();
            }

            this.updateChunkPos(p_217459_2_);
            if (p_217459_2_.inChunk) {
               for(Entity entity : p_217459_2_.getPassengers()) {
                  this.tickPassenger(p_217459_2_, entity);
               }
            }

         }
      } else {
         p_217459_2_.stopRiding();
      }
   }

   public void updateChunkPos(Entity p_217464_1_) {
      if (p_217464_1_.checkAndResetUpdateChunkPos()) {
         this.getProfiler().push("chunkCheck");
         int i = MathHelper.floor(p_217464_1_.getX() / 16.0D);
         int j = MathHelper.floor(p_217464_1_.getY() / 16.0D);
         int k = MathHelper.floor(p_217464_1_.getZ() / 16.0D);
         if (!p_217464_1_.inChunk || p_217464_1_.xChunk != i || p_217464_1_.yChunk != j || p_217464_1_.zChunk != k) {
            if (p_217464_1_.inChunk && this.hasChunk(p_217464_1_.xChunk, p_217464_1_.zChunk)) {
               this.getChunk(p_217464_1_.xChunk, p_217464_1_.zChunk).removeEntity(p_217464_1_, p_217464_1_.yChunk);
            }

            if (!p_217464_1_.checkAndResetForcedChunkAdditionFlag() && !this.hasChunk(i, k)) {
               if (p_217464_1_.inChunk) {
                  LOGGER.warn("Entity {} left loaded chunk area", (Object)p_217464_1_);
               }

               p_217464_1_.inChunk = false;
            } else {
               this.getChunk(i, k).addEntity(p_217464_1_);
            }
         }

         this.getProfiler().pop();
      }
   }

   public boolean mayInteract(PlayerEntity p_175660_1_, BlockPos p_175660_2_) {
      return !this.server.isUnderSpawnProtection(this, p_175660_2_, p_175660_1_) && this.getWorldBorder().isWithinBounds(p_175660_2_);
   }

   public void save(@Nullable IProgressUpdate p_217445_1_, boolean p_217445_2_, boolean p_217445_3_) {
      ServerChunkProvider serverchunkprovider = this.getChunkSource();
      if (!p_217445_3_) {
         if (p_217445_1_ != null) {
            p_217445_1_.progressStartNoAbort(new TranslationTextComponent("menu.savingLevel"));
         }

         this.saveLevelData();
         if (p_217445_1_ != null) {
            p_217445_1_.progressStage(new TranslationTextComponent("menu.savingChunks"));
         }

         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Save(this));
         serverchunkprovider.save(p_217445_2_);
      }
   }

   private void saveLevelData() {
      if (this.dragonFight != null) {
         this.server.getWorldData().setEndDragonFightData(this.dragonFight.saveData());
      }

      this.getChunkSource().getDataStorage().save();
   }

   public List<Entity> getEntities(@Nullable EntityType<?> p_217482_1_, Predicate<? super Entity> p_217482_2_) {
      List<Entity> list = Lists.newArrayList();
      ServerChunkProvider serverchunkprovider = this.getChunkSource();

      for(Entity entity : this.entitiesById.values()) {
         if ((p_217482_1_ == null || entity.getType() == p_217482_1_) && serverchunkprovider.hasChunk(MathHelper.floor(entity.getX()) >> 4, MathHelper.floor(entity.getZ()) >> 4) && p_217482_2_.test(entity)) {
            list.add(entity);
         }
      }

      return list;
   }

   public List<EnderDragonEntity> getDragons() {
      List<EnderDragonEntity> list = Lists.newArrayList();

      for(Entity entity : this.entitiesById.values()) {
         if (entity instanceof EnderDragonEntity && entity.isAlive()) {
            list.add((EnderDragonEntity)entity);
         }
      }

      return list;
   }

   public List<ServerPlayerEntity> getPlayers(Predicate<? super ServerPlayerEntity> p_217490_1_) {
      List<ServerPlayerEntity> list = Lists.newArrayList();

      for(ServerPlayerEntity serverplayerentity : this.players) {
         if (p_217490_1_.test(serverplayerentity)) {
            list.add(serverplayerentity);
         }
      }

      return list;
   }

   @Nullable
   public ServerPlayerEntity getRandomPlayer() {
      List<ServerPlayerEntity> list = this.getPlayers(LivingEntity::isAlive);
      return list.isEmpty() ? null : list.get(this.random.nextInt(list.size()));
   }

   public boolean addFreshEntity(Entity p_217376_1_) {
      return this.addEntity(p_217376_1_);
   }

   public boolean addWithUUID(Entity p_217470_1_) {
      return this.addEntity(p_217470_1_);
   }

   public void addFromAnotherDimension(Entity p_217460_1_) {
      boolean flag = p_217460_1_.forcedLoading;
      p_217460_1_.forcedLoading = true;
      this.addWithUUID(p_217460_1_);
      p_217460_1_.forcedLoading = flag;
      this.updateChunkPos(p_217460_1_);
   }

   public void addDuringCommandTeleport(ServerPlayerEntity p_217446_1_) {
      this.addPlayer(p_217446_1_);
      this.updateChunkPos(p_217446_1_);
   }

   public void addDuringPortalTeleport(ServerPlayerEntity p_217447_1_) {
      this.addPlayer(p_217447_1_);
      this.updateChunkPos(p_217447_1_);
   }

   public void addNewPlayer(ServerPlayerEntity p_217435_1_) {
      this.addPlayer(p_217435_1_);
   }

   public void addRespawnedPlayer(ServerPlayerEntity p_217433_1_) {
      this.addPlayer(p_217433_1_);
   }

   private void addPlayer(ServerPlayerEntity p_217448_1_) {
      if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityJoinWorldEvent(p_217448_1_, this))) return;
      Entity entity = this.entitiesByUuid.get(p_217448_1_.getUUID());
      if (entity != null) {
         LOGGER.warn("Force-added player with duplicate UUID {}", (Object)p_217448_1_.getUUID().toString());
         entity.unRide();
         this.removePlayerImmediately((ServerPlayerEntity)entity);
      }

      this.players.add(p_217448_1_);
      this.updateSleepingPlayerList();
      IChunk ichunk = this.getChunk(MathHelper.floor(p_217448_1_.getX() / 16.0D), MathHelper.floor(p_217448_1_.getZ() / 16.0D), ChunkStatus.FULL, true);
      if (ichunk instanceof Chunk) {
         ichunk.addEntity(p_217448_1_);
      }

      this.add(p_217448_1_);
   }

   private boolean addEntity(Entity p_72838_1_) {
      if (p_72838_1_.removed) {
         LOGGER.warn("Tried to add entity {} but it was marked as removed already", (Object)EntityType.getKey(p_72838_1_.getType()));
         return false;
      } else if (this.isUUIDUsed(p_72838_1_)) {
         return false;
      } else {
         if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityJoinWorldEvent(p_72838_1_, this))) return false;
         IChunk ichunk = this.getChunk(MathHelper.floor(p_72838_1_.getX() / 16.0D), MathHelper.floor(p_72838_1_.getZ() / 16.0D), ChunkStatus.FULL, p_72838_1_.forcedLoading);
         if (!(ichunk instanceof Chunk)) {
            return false;
         } else {
            ichunk.addEntity(p_72838_1_);
            this.add(p_72838_1_);
            return true;
         }
      }
   }

   public boolean loadFromChunk(Entity p_217440_1_) {
      if (this.isUUIDUsed(p_217440_1_)) {
         return false;
      } else {
         if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityJoinWorldEvent(p_217440_1_, this))) return false;
         this.add(p_217440_1_);
         return true;
      }
   }

   private boolean isUUIDUsed(Entity p_217478_1_) {
      UUID uuid = p_217478_1_.getUUID();
      Entity entity = this.findAddedOrPendingEntity(uuid);
      if (entity == null) {
         return false;
      } else {
         LOGGER.warn("Trying to add entity with duplicated UUID {}. Existing {}#{}, new: {}#{}", uuid, EntityType.getKey(entity.getType()), entity.getId(), EntityType.getKey(p_217478_1_.getType()), p_217478_1_.getId());
         return true;
      }
   }

   @Nullable
   private Entity findAddedOrPendingEntity(UUID p_242105_1_) {
      Entity entity = this.entitiesByUuid.get(p_242105_1_);
      if (entity != null) {
         return entity;
      } else {
         if (this.tickingEntities) {
            for(Entity entity1 : this.toAddAfterTick) {
               if (entity1.getUUID().equals(p_242105_1_)) {
                  return entity1;
               }
            }
         }

         return null;
      }
   }

   public boolean tryAddFreshEntityWithPassengers(Entity p_242106_1_) {
      if (p_242106_1_.getSelfAndPassengers().anyMatch(this::isUUIDUsed)) {
         return false;
      } else {
         this.addFreshEntityWithPassengers(p_242106_1_);
         return true;
      }
   }

   public void unload(Chunk p_217466_1_) {
      this.blockEntitiesToUnload.addAll(p_217466_1_.getBlockEntities().values());
      ClassInheritanceMultiMap<Entity>[] aclassinheritancemultimap = p_217466_1_.getEntitySections();
      int i = aclassinheritancemultimap.length;

      for(int j = 0; j < i; ++j) {
         for(Entity entity : aclassinheritancemultimap[j]) {
            if (!(entity instanceof ServerPlayerEntity)) {
               if (this.tickingEntities) {
                  throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Removing entity while ticking!"));
               }

               this.entitiesById.remove(entity.getId());
               this.onEntityRemoved(entity);
            }
         }
      }

   }

   @Deprecated //Forge: Use removeEntityComplete(entity,boolean)
   public void onEntityRemoved(Entity p_217484_1_) {
      removeEntityComplete(p_217484_1_, false);
   }
   public void removeEntityComplete(Entity p_217484_1_, boolean keepData) {
      if (p_217484_1_ instanceof EnderDragonEntity) {
         for(EnderDragonPartEntity enderdragonpartentity : ((EnderDragonEntity)p_217484_1_).getSubEntities()) {
            enderdragonpartentity.remove(keepData);
         }
      }
      p_217484_1_.remove(keepData);

      this.entitiesByUuid.remove(p_217484_1_.getUUID());
      this.getChunkSource().removeEntity(p_217484_1_);
      if (p_217484_1_ instanceof ServerPlayerEntity) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)p_217484_1_;
         this.players.remove(serverplayerentity);
      }

      this.getScoreboard().entityRemoved(p_217484_1_);
      if (p_217484_1_ instanceof MobEntity) {
         this.navigations.remove(((MobEntity)p_217484_1_).getNavigation());
      }

      p_217484_1_.onRemovedFromWorld();
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityLeaveWorldEvent(p_217484_1_, this));
   }

   private void add(Entity p_217465_1_) {
      if (this.tickingEntities) {
         this.toAddAfterTick.add(p_217465_1_);
      } else {
         this.entitiesById.put(p_217465_1_.getId(), p_217465_1_);
         if (p_217465_1_ instanceof EnderDragonEntity) {
            for(EnderDragonPartEntity enderdragonpartentity : ((EnderDragonEntity)p_217465_1_).getSubEntities()) {
               this.entitiesById.put(enderdragonpartentity.getId(), enderdragonpartentity);
            }
         }

         this.entitiesByUuid.put(p_217465_1_.getUUID(), p_217465_1_);
         this.getChunkSource().addEntity(p_217465_1_);
         if (p_217465_1_ instanceof MobEntity) {
            this.navigations.add(((MobEntity)p_217465_1_).getNavigation());
         }
      }

      p_217465_1_.onAddedToWorld();
   }

   public void despawn(Entity p_217467_1_) {
      removeEntity(p_217467_1_, false);
   }
   public void removeEntity(Entity p_217467_1_, boolean keepData) {
      if (this.tickingEntities) {
         throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Removing entity while ticking!"));
      } else {
         this.removeFromChunk(p_217467_1_);
         this.entitiesById.remove(p_217467_1_.getId());
         this.removeEntityComplete(p_217467_1_, keepData);
      }
   }

   private void removeFromChunk(Entity p_217454_1_) {
      IChunk ichunk = this.getChunk(p_217454_1_.xChunk, p_217454_1_.zChunk, ChunkStatus.FULL, false);
      if (ichunk instanceof Chunk) {
         ((Chunk)ichunk).removeEntity(p_217454_1_);
      }

   }

   public void removePlayerImmediately(ServerPlayerEntity p_217434_1_) {
      removePlayer(p_217434_1_, false);
   }
   public void removePlayer(ServerPlayerEntity p_217434_1_, boolean keepData) {
      p_217434_1_.remove(keepData);
      this.removeEntity(p_217434_1_, keepData);
      this.updateSleepingPlayerList();
   }

   public void destroyBlockProgress(int p_175715_1_, BlockPos p_175715_2_, int p_175715_3_) {
      for(ServerPlayerEntity serverplayerentity : this.server.getPlayerList().getPlayers()) {
         if (serverplayerentity != null && serverplayerentity.level == this && serverplayerentity.getId() != p_175715_1_) {
            double d0 = (double)p_175715_2_.getX() - serverplayerentity.getX();
            double d1 = (double)p_175715_2_.getY() - serverplayerentity.getY();
            double d2 = (double)p_175715_2_.getZ() - serverplayerentity.getZ();
            if (d0 * d0 + d1 * d1 + d2 * d2 < 1024.0D) {
               serverplayerentity.connection.send(new SAnimateBlockBreakPacket(p_175715_1_, p_175715_2_, p_175715_3_));
            }
         }
      }

   }

   public void playSound(@Nullable PlayerEntity p_184148_1_, double p_184148_2_, double p_184148_4_, double p_184148_6_, SoundEvent p_184148_8_, SoundCategory p_184148_9_, float p_184148_10_, float p_184148_11_) {
      net.minecraftforge.event.entity.PlaySoundAtEntityEvent event = net.minecraftforge.event.ForgeEventFactory.onPlaySoundAtEntity(p_184148_1_, p_184148_8_, p_184148_9_, p_184148_10_, p_184148_11_);
      if (event.isCanceled() || event.getSound() == null) return;
      p_184148_8_ = event.getSound();
      p_184148_9_ = event.getCategory();
      p_184148_10_ = event.getVolume();
      this.server.getPlayerList().broadcast(p_184148_1_, p_184148_2_, p_184148_4_, p_184148_6_, p_184148_10_ > 1.0F ? (double)(16.0F * p_184148_10_) : 16.0D, this.dimension(), new SPlaySoundEffectPacket(p_184148_8_, p_184148_9_, p_184148_2_, p_184148_4_, p_184148_6_, p_184148_10_, p_184148_11_));
   }

   public void playSound(@Nullable PlayerEntity p_217384_1_, Entity p_217384_2_, SoundEvent p_217384_3_, SoundCategory p_217384_4_, float p_217384_5_, float p_217384_6_) {
      net.minecraftforge.event.entity.PlaySoundAtEntityEvent event = net.minecraftforge.event.ForgeEventFactory.onPlaySoundAtEntity(p_217384_1_, p_217384_3_, p_217384_4_, p_217384_5_, p_217384_6_);
      if (event.isCanceled() || event.getSound() == null) return;
      p_217384_3_ = event.getSound();
      p_217384_4_ = event.getCategory();
      p_217384_5_ = event.getVolume();
      this.server.getPlayerList().broadcast(p_217384_1_, p_217384_2_.getX(), p_217384_2_.getY(), p_217384_2_.getZ(), p_217384_5_ > 1.0F ? (double)(16.0F * p_217384_5_) : 16.0D, this.dimension(), new SSpawnMovingSoundEffectPacket(p_217384_3_, p_217384_4_, p_217384_2_, p_217384_5_, p_217384_6_));
   }

   public void globalLevelEvent(int p_175669_1_, BlockPos p_175669_2_, int p_175669_3_) {
      this.server.getPlayerList().broadcastAll(new SPlaySoundEventPacket(p_175669_1_, p_175669_2_, p_175669_3_, true));
   }

   public void levelEvent(@Nullable PlayerEntity p_217378_1_, int p_217378_2_, BlockPos p_217378_3_, int p_217378_4_) {
      this.server.getPlayerList().broadcast(p_217378_1_, (double)p_217378_3_.getX(), (double)p_217378_3_.getY(), (double)p_217378_3_.getZ(), 64.0D, this.dimension(), new SPlaySoundEventPacket(p_217378_2_, p_217378_3_, p_217378_4_, false));
   }

   public void sendBlockUpdated(BlockPos p_184138_1_, BlockState p_184138_2_, BlockState p_184138_3_, int p_184138_4_) {
      this.getChunkSource().blockChanged(p_184138_1_);
      VoxelShape voxelshape = p_184138_2_.getCollisionShape(this, p_184138_1_);
      VoxelShape voxelshape1 = p_184138_3_.getCollisionShape(this, p_184138_1_);
      if (VoxelShapes.joinIsNotEmpty(voxelshape, voxelshape1, IBooleanFunction.NOT_SAME)) {
         for(PathNavigator pathnavigator : this.navigations) {
            if (!pathnavigator.hasDelayedRecomputation()) {
               pathnavigator.recomputePath(p_184138_1_);
            }
         }

      }
   }

   public void broadcastEntityEvent(Entity p_72960_1_, byte p_72960_2_) {
      this.getChunkSource().broadcastAndSend(p_72960_1_, new SEntityStatusPacket(p_72960_1_, p_72960_2_));
   }

   public ServerChunkProvider getChunkSource() {
      return this.chunkSource;
   }

   public Explosion explode(@Nullable Entity p_230546_1_, @Nullable DamageSource p_230546_2_, @Nullable ExplosionContext p_230546_3_, double p_230546_4_, double p_230546_6_, double p_230546_8_, float p_230546_10_, boolean p_230546_11_, Explosion.Mode p_230546_12_) {
      Explosion explosion = new Explosion(this, p_230546_1_, p_230546_2_, p_230546_3_, p_230546_4_, p_230546_6_, p_230546_8_, p_230546_10_, p_230546_11_, p_230546_12_);
      if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(this, explosion)) return explosion;
      explosion.explode();
      explosion.finalizeExplosion(false);
      if (p_230546_12_ == Explosion.Mode.NONE) {
         explosion.clearToBlow();
      }

      for(ServerPlayerEntity serverplayerentity : this.players) {
         if (serverplayerentity.distanceToSqr(p_230546_4_, p_230546_6_, p_230546_8_) < 4096.0D) {
            serverplayerentity.connection.send(new SExplosionPacket(p_230546_4_, p_230546_6_, p_230546_8_, p_230546_10_, explosion.getToBlow(), explosion.getHitPlayers().get(serverplayerentity)));
         }
      }

      return explosion;
   }

   public void blockEvent(BlockPos p_175641_1_, Block p_175641_2_, int p_175641_3_, int p_175641_4_) {
      this.blockEvents.add(new BlockEventData(p_175641_1_, p_175641_2_, p_175641_3_, p_175641_4_));
   }

   private void runBlockEvents() {
      while(!this.blockEvents.isEmpty()) {
         BlockEventData blockeventdata = this.blockEvents.removeFirst();
         if (this.doBlockEvent(blockeventdata)) {
            this.server.getPlayerList().broadcast((PlayerEntity)null, (double)blockeventdata.getPos().getX(), (double)blockeventdata.getPos().getY(), (double)blockeventdata.getPos().getZ(), 64.0D, this.dimension(), new SBlockActionPacket(blockeventdata.getPos(), blockeventdata.getBlock(), blockeventdata.getParamA(), blockeventdata.getParamB()));
         }
      }

   }

   private boolean doBlockEvent(BlockEventData p_147485_1_) {
      BlockState blockstate = this.getBlockState(p_147485_1_.getPos());
      return blockstate.is(p_147485_1_.getBlock()) ? blockstate.triggerEvent(this, p_147485_1_.getPos(), p_147485_1_.getParamA(), p_147485_1_.getParamB()) : false;
   }

   public ServerTickList<Block> getBlockTicks() {
      return this.blockTicks;
   }

   public ServerTickList<Fluid> getLiquidTicks() {
      return this.liquidTicks;
   }

   @Nonnull
   public MinecraftServer getServer() {
      return this.server;
   }

   public Teleporter getPortalForcer() {
      return this.portalForcer;
   }

   public TemplateManager getStructureManager() {
      return this.server.getStructureManager();
   }

   public <T extends IParticleData> int sendParticles(T p_195598_1_, double p_195598_2_, double p_195598_4_, double p_195598_6_, int p_195598_8_, double p_195598_9_, double p_195598_11_, double p_195598_13_, double p_195598_15_) {
      SSpawnParticlePacket sspawnparticlepacket = new SSpawnParticlePacket(p_195598_1_, false, p_195598_2_, p_195598_4_, p_195598_6_, (float)p_195598_9_, (float)p_195598_11_, (float)p_195598_13_, (float)p_195598_15_, p_195598_8_);
      int i = 0;

      for(int j = 0; j < this.players.size(); ++j) {
         ServerPlayerEntity serverplayerentity = this.players.get(j);
         if (this.sendParticles(serverplayerentity, false, p_195598_2_, p_195598_4_, p_195598_6_, sspawnparticlepacket)) {
            ++i;
         }
      }

      return i;
   }

   public <T extends IParticleData> boolean sendParticles(ServerPlayerEntity p_195600_1_, T p_195600_2_, boolean p_195600_3_, double p_195600_4_, double p_195600_6_, double p_195600_8_, int p_195600_10_, double p_195600_11_, double p_195600_13_, double p_195600_15_, double p_195600_17_) {
      IPacket<?> ipacket = new SSpawnParticlePacket(p_195600_2_, p_195600_3_, p_195600_4_, p_195600_6_, p_195600_8_, (float)p_195600_11_, (float)p_195600_13_, (float)p_195600_15_, (float)p_195600_17_, p_195600_10_);
      return this.sendParticles(p_195600_1_, p_195600_3_, p_195600_4_, p_195600_6_, p_195600_8_, ipacket);
   }

   private boolean sendParticles(ServerPlayerEntity p_195601_1_, boolean p_195601_2_, double p_195601_3_, double p_195601_5_, double p_195601_7_, IPacket<?> p_195601_9_) {
      if (p_195601_1_.getLevel() != this) {
         return false;
      } else {
         BlockPos blockpos = p_195601_1_.blockPosition();
         if (blockpos.closerThan(new Vector3d(p_195601_3_, p_195601_5_, p_195601_7_), p_195601_2_ ? 512.0D : 32.0D)) {
            p_195601_1_.connection.send(p_195601_9_);
            return true;
         } else {
            return false;
         }
      }
   }

   @Nullable
   public Entity getEntity(int p_73045_1_) {
      return this.entitiesById.get(p_73045_1_);
   }

   @Nullable
   public Entity getEntity(UUID p_217461_1_) {
      return this.entitiesByUuid.get(p_217461_1_);
   }

   @Nullable
   public BlockPos findNearestMapFeature(Structure<?> p_241117_1_, BlockPos p_241117_2_, int p_241117_3_, boolean p_241117_4_) {
      return !this.server.getWorldData().worldGenSettings().generateFeatures() ? null : this.getChunkSource().getGenerator().findNearestMapFeature(this, p_241117_1_, p_241117_2_, p_241117_3_, p_241117_4_);
   }

   @Nullable
   public BlockPos findNearestBiome(Biome p_241116_1_, BlockPos p_241116_2_, int p_241116_3_, int p_241116_4_) {
      return this.getChunkSource().getGenerator().getBiomeSource().findBiomeHorizontal(p_241116_2_.getX(), p_241116_2_.getY(), p_241116_2_.getZ(), p_241116_3_, p_241116_4_, (p_242102_1_) -> {
         return p_242102_1_ == p_241116_1_;
      }, this.random, true);
   }

   public RecipeManager getRecipeManager() {
      return this.server.getRecipeManager();
   }

   public ITagCollectionSupplier getTagManager() {
      return this.server.getTags();
   }

   public boolean noSave() {
      return this.noSave;
   }

   public DynamicRegistries registryAccess() {
      return this.server.registryAccess();
   }

   public DimensionSavedDataManager getDataStorage() {
      return this.getChunkSource().getDataStorage();
   }

   @Nullable
   public MapData getMapData(String p_217406_1_) {
      return this.getServer().overworld().getDataStorage().get(() -> {
         return new MapData(p_217406_1_);
      }, p_217406_1_);
   }

   public void setMapData(MapData p_217399_1_) {
      this.getServer().overworld().getDataStorage().set(p_217399_1_);
   }

   public int getFreeMapId() {
      return this.getServer().overworld().getDataStorage().computeIfAbsent(MapIdTracker::new, "idcounts").getFreeAuxValueForMap();
   }

   public void setDefaultSpawnPos(BlockPos p_241124_1_, float p_241124_2_) {
      ChunkPos chunkpos = new ChunkPos(new BlockPos(this.levelData.getXSpawn(), 0, this.levelData.getZSpawn()));
      this.levelData.setSpawn(p_241124_1_, p_241124_2_);
      this.getChunkSource().removeRegionTicket(TicketType.START, chunkpos, 11, Unit.INSTANCE);
      this.getChunkSource().addRegionTicket(TicketType.START, new ChunkPos(p_241124_1_), 11, Unit.INSTANCE);
      this.getServer().getPlayerList().broadcastAll(new SWorldSpawnChangedPacket(p_241124_1_, p_241124_2_));
   }

   public BlockPos getSharedSpawnPos() {
      BlockPos blockpos = new BlockPos(this.levelData.getXSpawn(), this.levelData.getYSpawn(), this.levelData.getZSpawn());
      if (!this.getWorldBorder().isWithinBounds(blockpos)) {
         blockpos = this.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0D, this.getWorldBorder().getCenterZ()));
      }

      return blockpos;
   }

   public float getSharedSpawnAngle() {
      return this.levelData.getSpawnAngle();
   }

   public LongSet getForcedChunks() {
      ForcedChunksSaveData forcedchunkssavedata = this.getDataStorage().get(ForcedChunksSaveData::new, "chunks");
      return (LongSet)(forcedchunkssavedata != null ? LongSets.unmodifiable(forcedchunkssavedata.getChunks()) : LongSets.EMPTY_SET);
   }

   public boolean setChunkForced(int p_217458_1_, int p_217458_2_, boolean p_217458_3_) {
      ForcedChunksSaveData forcedchunkssavedata = this.getDataStorage().computeIfAbsent(ForcedChunksSaveData::new, "chunks");
      ChunkPos chunkpos = new ChunkPos(p_217458_1_, p_217458_2_);
      long i = chunkpos.toLong();
      boolean flag;
      if (p_217458_3_) {
         flag = forcedchunkssavedata.getChunks().add(i);
         if (flag) {
            this.getChunk(p_217458_1_, p_217458_2_);
         }
      } else {
         flag = forcedchunkssavedata.getChunks().remove(i);
      }

      forcedchunkssavedata.setDirty(flag);
      if (flag) {
         this.getChunkSource().updateChunkForced(chunkpos, p_217458_3_);
      }

      return flag;
   }

   public List<ServerPlayerEntity> players() {
      return this.players;
   }

   public void onBlockStateChange(BlockPos p_217393_1_, BlockState p_217393_2_, BlockState p_217393_3_) {
      Optional<PointOfInterestType> optional = PointOfInterestType.forState(p_217393_2_);
      Optional<PointOfInterestType> optional1 = PointOfInterestType.forState(p_217393_3_);
      if (!Objects.equals(optional, optional1)) {
         BlockPos blockpos = p_217393_1_.immutable();
         optional.ifPresent((p_241130_2_) -> {
            this.getServer().execute(() -> {
               this.getPoiManager().remove(blockpos);
               DebugPacketSender.sendPoiRemovedPacket(this, blockpos);
            });
         });
         optional1.ifPresent((p_217476_2_) -> {
            this.getServer().execute(() -> {
               this.getPoiManager().add(blockpos, p_217476_2_);
               DebugPacketSender.sendPoiAddedPacket(this, blockpos);
            });
         });
      }
   }

   public PointOfInterestManager getPoiManager() {
      return this.getChunkSource().getPoiManager();
   }

   public boolean isVillage(BlockPos p_217483_1_) {
      return this.isCloseToVillage(p_217483_1_, 1);
   }

   public boolean isVillage(SectionPos p_222887_1_) {
      return this.isVillage(p_222887_1_.center());
   }

   public boolean isCloseToVillage(BlockPos p_241119_1_, int p_241119_2_) {
      if (p_241119_2_ > 6) {
         return false;
      } else {
         return this.sectionsToVillage(SectionPos.of(p_241119_1_)) <= p_241119_2_;
      }
   }

   public int sectionsToVillage(SectionPos p_217486_1_) {
      return this.getPoiManager().sectionsToVillage(p_217486_1_);
   }

   public RaidManager getRaids() {
      return this.raids;
   }

   @Nullable
   public Raid getRaidAt(BlockPos p_217475_1_) {
      return this.raids.getNearbyRaid(p_217475_1_, 9216);
   }

   public boolean isRaided(BlockPos p_217455_1_) {
      return this.getRaidAt(p_217455_1_) != null;
   }

   public void onReputationEvent(IReputationType p_217489_1_, Entity p_217489_2_, IReputationTracking p_217489_3_) {
      p_217489_3_.onReputationEventFrom(p_217489_1_, p_217489_2_);
   }

   public void saveDebugReport(Path p_225322_1_) throws IOException {
      ChunkManager chunkmanager = this.getChunkSource().chunkMap;

      try (Writer writer = Files.newBufferedWriter(p_225322_1_.resolve("stats.txt"))) {
         writer.write(String.format("spawning_chunks: %d\n", chunkmanager.getDistanceManager().getNaturalSpawnChunkCount()));
         WorldEntitySpawner.EntityDensityManager worldentityspawner$entitydensitymanager = this.getChunkSource().getLastSpawnState();
         if (worldentityspawner$entitydensitymanager != null) {
            for(it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<EntityClassification> entry : worldentityspawner$entitydensitymanager.getMobCategoryCounts().object2IntEntrySet()) {
               writer.write(String.format("spawn_count.%s: %d\n", entry.getKey().getName(), entry.getIntValue()));
            }
         }

         writer.write(String.format("entities: %d\n", this.entitiesById.size()));
         writer.write(String.format("block_entities: %d\n", this.blockEntityList.size()));
         writer.write(String.format("block_ticks: %d\n", this.getBlockTicks().size()));
         writer.write(String.format("fluid_ticks: %d\n", this.getLiquidTicks().size()));
         writer.write("distance_manager: " + chunkmanager.getDistanceManager().getDebugStatus() + "\n");
         writer.write(String.format("pending_tasks: %d\n", this.getChunkSource().getPendingTasksCount()));
      }

      CrashReport crashreport = new CrashReport("Level dump", new Exception("dummy"));
      this.fillReportDetails(crashreport);

      try (Writer writer1 = Files.newBufferedWriter(p_225322_1_.resolve("example_crash.txt"))) {
         writer1.write(crashreport.getFriendlyReport());
      }

      Path path = p_225322_1_.resolve("chunks.csv");

      try (Writer writer2 = Files.newBufferedWriter(path)) {
         chunkmanager.dumpChunks(writer2);
      }

      Path path1 = p_225322_1_.resolve("entities.csv");

      try (Writer writer3 = Files.newBufferedWriter(path1)) {
         dumpEntities(writer3, this.entitiesById.values());
      }

      Path path2 = p_225322_1_.resolve("block_entities.csv");

      try (Writer writer4 = Files.newBufferedWriter(path2)) {
         this.dumpBlockEntities(writer4);
      }

   }

   private static void dumpEntities(Writer p_225320_0_, Iterable<Entity> p_225320_1_) throws IOException {
      CSVWriter csvwriter = CSVWriter.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("uuid").addColumn("type").addColumn("alive").addColumn("display_name").addColumn("custom_name").build(p_225320_0_);

      for(Entity entity : p_225320_1_) {
         ITextComponent itextcomponent = entity.getCustomName();
         ITextComponent itextcomponent1 = entity.getDisplayName();
         csvwriter.writeRow(entity.getX(), entity.getY(), entity.getZ(), entity.getUUID(), Registry.ENTITY_TYPE.getKey(entity.getType()), entity.isAlive(), itextcomponent1.getString(), itextcomponent != null ? itextcomponent.getString() : null);
      }

   }

   private void dumpBlockEntities(Writer p_225321_1_) throws IOException {
      CSVWriter csvwriter = CSVWriter.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("type").build(p_225321_1_);

      for(TileEntity tileentity : this.blockEntityList) {
         BlockPos blockpos = tileentity.getBlockPos();
         csvwriter.writeRow(blockpos.getX(), blockpos.getY(), blockpos.getZ(), Registry.BLOCK_ENTITY_TYPE.getKey(tileentity.getType()));
      }

   }

   @VisibleForTesting
   public void clearBlockEvents(MutableBoundingBox p_229854_1_) {
      this.blockEvents.removeIf((p_241118_1_) -> {
         return p_229854_1_.isInside(p_241118_1_.getPos());
      });
   }

   public void blockUpdated(BlockPos p_230547_1_, Block p_230547_2_) {
      if (!this.isDebug()) {
         this.updateNeighborsAt(p_230547_1_, p_230547_2_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float getShade(Direction p_230487_1_, boolean p_230487_2_) {
      return 1.0F;
   }

   public Iterable<Entity> getAllEntities() {
      return Iterables.unmodifiableIterable(this.entitiesById.values());
   }

   public String toString() {
      return "ServerLevel[" + this.serverLevelData.getLevelName() + "]";
   }

   public boolean isFlat() {
      return this.server.getWorldData().worldGenSettings().isFlatWorld();
   }

   public long getSeed() {
      return this.server.getWorldData().worldGenSettings().seed();
   }

   @Nullable
   public DragonFightManager dragonFight() {
      return this.dragonFight;
   }

   public Stream<? extends StructureStart<?>> startsForFeature(SectionPos p_241827_1_, Structure<?> p_241827_2_) {
      return this.structureFeatureManager().startsForFeature(p_241827_1_, p_241827_2_);
   }

   public ServerWorld getLevel() {
      return this;
   }

   @VisibleForTesting
   public String getWatchdogStats() {
      return String.format("players: %s, entities: %d [%s], block_entities: %d [%s], block_ticks: %d, fluid_ticks: %d, chunk_source: %s", this.players.size(), this.entitiesById.size(), getTypeCount(this.entitiesById.values(), (p_244527_0_) -> {
         return Registry.ENTITY_TYPE.getKey(p_244527_0_.getType());
      }), this.tickableBlockEntities.size(), getTypeCount(this.tickableBlockEntities, (p_244526_0_) -> {
         return Registry.BLOCK_ENTITY_TYPE.getKey(p_244526_0_.getType());
      }), this.getBlockTicks().size(), this.getLiquidTicks().size(), this.gatherChunkSourceStats());
   }

   private static <T> String getTypeCount(Collection<T> p_244524_0_, Function<T, ResourceLocation> p_244524_1_) {
      try {
         Object2IntOpenHashMap<ResourceLocation> object2intopenhashmap = new Object2IntOpenHashMap<>();

         for(T t : p_244524_0_) {
            ResourceLocation resourcelocation = p_244524_1_.apply(t);
            object2intopenhashmap.addTo(resourcelocation, 1);
         }

         return object2intopenhashmap.object2IntEntrySet().stream().sorted(Comparator.comparing(it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<ResourceLocation>::getIntValue).reversed()).limit(5L).map((p_244523_0_) -> {
            return p_244523_0_.getKey() + ":" + p_244523_0_.getIntValue();
         }).collect(Collectors.joining(","));
      } catch (Exception exception) {
         return "";
      }
   }

   public static void makeObsidianPlatform(ServerWorld p_241121_0_) {
      BlockPos blockpos = END_SPAWN_POINT;
      int i = blockpos.getX();
      int j = blockpos.getY() - 2;
      int k = blockpos.getZ();
      BlockPos.betweenClosed(i - 2, j + 1, k - 2, i + 2, j + 3, k + 2).forEach((p_244430_1_) -> {
         p_241121_0_.setBlockAndUpdate(p_244430_1_, Blocks.AIR.defaultBlockState());
      });
      BlockPos.betweenClosed(i - 2, j, k - 2, i + 2, j, k + 2).forEach((p_241122_1_) -> {
         p_241121_0_.setBlockAndUpdate(p_241122_1_, Blocks.OBSIDIAN.defaultBlockState());
      });
   }

   protected void initCapabilities() {
      this.gatherCapabilities();
      capabilityData = this.getDataStorage().computeIfAbsent(() -> new net.minecraftforge.common.util.WorldCapabilityData(getCapabilities()), net.minecraftforge.common.util.WorldCapabilityData.ID);
      capabilityData.setCapabilities(getCapabilities());
   }

   public java.util.stream.Stream<Entity> getEntities() {
       return entitiesById.values().stream();
   }
}
