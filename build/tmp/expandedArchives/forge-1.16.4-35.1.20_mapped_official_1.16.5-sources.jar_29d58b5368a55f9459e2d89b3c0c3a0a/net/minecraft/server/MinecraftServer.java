package net.minecraft.server;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ICommandSource;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.SpawnLocationHelper;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.loot.LootPredicateManager;
import net.minecraft.loot.LootTableManager;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.profiler.EmptyProfiler;
import net.minecraft.profiler.IProfileResult;
import net.minecraft.profiler.IProfiler;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.profiler.LongTickDetector;
import net.minecraft.profiler.Snooper;
import net.minecraft.profiler.TimeTracker;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.management.OpEntry;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.server.management.WhiteList;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.test.TestCollection;
import net.minecraft.util.CryptException;
import net.minecraft.util.CryptManager;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.RecursiveEventLoop;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.filter.IChatFilter;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.ForcedChunksSaveData;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.chunk.listener.IChunkStatusListenerFactory;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraft.world.spawner.CatSpawner;
import net.minecraft.world.spawner.ISpecialSpawner;
import net.minecraft.world.spawner.PatrolSpawner;
import net.minecraft.world.spawner.PhantomSpawner;
import net.minecraft.world.spawner.WanderingTraderSpawner;
import net.minecraft.world.storage.CommandStorage;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.FolderName;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraft.world.storage.PlayerData;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldSavedDataCallableSave;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer extends RecursiveEventLoop<TickDelayedTask> implements ISnooperInfo, ICommandSource, AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final File USERID_CACHE_FILE = new File("usercache.json");
   public static final WorldSettings DEMO_SETTINGS = new WorldSettings("Demo World", GameType.SURVIVAL, false, Difficulty.NORMAL, false, new GameRules(), DatapackCodec.DEFAULT);
   protected final SaveFormat.LevelSave storageSource;
   protected final PlayerData playerDataStorage;
   private final Snooper snooper = new Snooper("server", this, Util.getMillis());
   private final List<Runnable> tickables = Lists.newArrayList();
   private final TimeTracker continousProfiler = new TimeTracker(Util.timeSource, this::getTickCount);
   private IProfiler profiler = EmptyProfiler.INSTANCE;
   private final NetworkSystem connection;
   private final IChunkStatusListenerFactory progressListenerFactory;
   private final ServerStatusResponse status = new ServerStatusResponse();
   private final Random random = new Random();
   private final DataFixer fixerUpper;
   private String localIp;
   private int port = -1;
   protected final DynamicRegistries.Impl registryHolder;
   private final Map<RegistryKey<World>, ServerWorld> levels = Maps.newLinkedHashMap();
   private PlayerList playerList;
   private volatile boolean running = true;
   private boolean stopped;
   private int tickCount;
   protected final Proxy proxy;
   private boolean onlineMode;
   private boolean preventProxyConnections;
   private boolean pvp;
   private boolean allowFlight;
   @Nullable
   private String motd;
   private int maxBuildHeight;
   private int playerIdleTimeout;
   public final long[] tickTimes = new long[100];
   @Nullable
   private KeyPair keyPair;
   @Nullable
   private String singleplayerName;
   private boolean isDemo;
   private String resourcePack = "";
   private String resourcePackHash = "";
   private volatile boolean isReady;
   private long lastOverloadWarning;
   private boolean delayProfilerStart;
   private boolean forceGameType;
   private final MinecraftSessionService sessionService;
   private final GameProfileRepository profileRepository;
   private final PlayerProfileCache profileCache;
   private long lastServerStatus;
   private final Thread serverThread;
   protected long nextTickTime = Util.getMillis();
   private long delayedTasksMaxNextTickTime;
   private boolean mayHaveDelayedTasks;
   @OnlyIn(Dist.CLIENT)
   private boolean hasWorldScreenshot;
   private final ResourcePackList packRepository;
   private final ServerScoreboard scoreboard = new ServerScoreboard(this);
   @Nullable
   private CommandStorage commandStorage;
   private final CustomServerBossInfoManager customBossEvents = new CustomServerBossInfoManager();
   private final FunctionManager functionManager;
   private final FrameTimer frameTimer = new FrameTimer();
   private boolean enforceWhitelist;
   private float averageTickTime;
   private final Executor executor;
   @Nullable
   private String serverId;
   private DataPackRegistries resources;
   private final TemplateManager structureManager;
   protected final IServerConfiguration worldData;

   public static <S extends MinecraftServer> S spin(Function<Thread, S> p_240784_0_) {
      AtomicReference<S> atomicreference = new AtomicReference<>();
      Thread thread = new Thread(net.minecraftforge.fml.common.thread.SidedThreadGroups.SERVER, () -> {
         atomicreference.get().runServer();
      }, "Server thread");
      thread.setUncaughtExceptionHandler((p_240779_0_, p_240779_1_) -> {
         LOGGER.error(p_240779_1_);
      });
      S s = p_240784_0_.apply(thread);
      atomicreference.set(s);
      thread.start();
      return s;
   }

   public MinecraftServer(Thread p_i232576_1_, DynamicRegistries.Impl p_i232576_2_, SaveFormat.LevelSave p_i232576_3_, IServerConfiguration p_i232576_4_, ResourcePackList p_i232576_5_, Proxy p_i232576_6_, DataFixer p_i232576_7_, DataPackRegistries p_i232576_8_, MinecraftSessionService p_i232576_9_, GameProfileRepository p_i232576_10_, PlayerProfileCache p_i232576_11_, IChunkStatusListenerFactory p_i232576_12_) {
      super("Server");
      this.registryHolder = p_i232576_2_;
      this.worldData = p_i232576_4_;
      this.proxy = p_i232576_6_;
      this.packRepository = p_i232576_5_;
      this.resources = p_i232576_8_;
      this.sessionService = p_i232576_9_;
      this.profileRepository = p_i232576_10_;
      this.profileCache = p_i232576_11_;
      this.connection = new NetworkSystem(this);
      this.progressListenerFactory = p_i232576_12_;
      this.storageSource = p_i232576_3_;
      this.playerDataStorage = p_i232576_3_.createPlayerStorage();
      this.fixerUpper = p_i232576_7_;
      this.functionManager = new FunctionManager(this, p_i232576_8_.getFunctionLibrary());
      this.structureManager = new TemplateManager(p_i232576_8_.getResourceManager(), p_i232576_3_, p_i232576_7_);
      this.serverThread = p_i232576_1_;
      this.executor = Util.backgroundExecutor();
   }

   private void readScoreboard(DimensionSavedDataManager p_213204_1_) {
      ScoreboardSaveData scoreboardsavedata = p_213204_1_.computeIfAbsent(ScoreboardSaveData::new, "scoreboard");
      scoreboardsavedata.setScoreboard(this.getScoreboard());
      this.getScoreboard().addDirtyListener(new WorldSavedDataCallableSave(scoreboardsavedata));
   }

   protected abstract boolean initServer() throws IOException;

   public static void convertFromRegionFormatIfNeeded(SaveFormat.LevelSave p_240777_0_) {
      if (p_240777_0_.requiresConversion()) {
         LOGGER.info("Converting map!");
         p_240777_0_.convertLevel(new IProgressUpdate() {
            private long timeStamp = Util.getMillis();

            public void progressStartNoAbort(ITextComponent p_200210_1_) {
            }

            @OnlyIn(Dist.CLIENT)
            public void progressStart(ITextComponent p_200211_1_) {
            }

            public void progressStagePercentage(int p_73718_1_) {
               if (Util.getMillis() - this.timeStamp >= 1000L) {
                  this.timeStamp = Util.getMillis();
                  MinecraftServer.LOGGER.info("Converting... {}%", (int)p_73718_1_);
               }

            }

            @OnlyIn(Dist.CLIENT)
            public void stop() {
            }

            public void progressStage(ITextComponent p_200209_1_) {
            }
         });
      }

   }

   protected void loadLevel() {
      this.detectBundledResources();
      this.worldData.setModdedInfo(this.getServerModName(), this.getModdedStatus().isPresent());
      IChunkStatusListener ichunkstatuslistener = this.progressListenerFactory.create(11);
      this.createLevels(ichunkstatuslistener);
      this.forceDifficulty();
      this.prepareLevels(ichunkstatuslistener);
   }

   protected void forceDifficulty() {
   }

   protected void createLevels(IChunkStatusListener p_240787_1_) {
      IServerWorldInfo iserverworldinfo = this.worldData.overworldData();
      DimensionGeneratorSettings dimensiongeneratorsettings = this.worldData.worldGenSettings();
      boolean flag = dimensiongeneratorsettings.isDebug();
      long i = dimensiongeneratorsettings.seed();
      long j = BiomeManager.obfuscateSeed(i);
      List<ISpecialSpawner> list = ImmutableList.of(new PhantomSpawner(), new PatrolSpawner(), new CatSpawner(), new VillageSiege(), new WanderingTraderSpawner(iserverworldinfo));
      SimpleRegistry<Dimension> simpleregistry = dimensiongeneratorsettings.dimensions();
      Dimension dimension = simpleregistry.get(Dimension.OVERWORLD);
      ChunkGenerator chunkgenerator;
      DimensionType dimensiontype;
      if (dimension == null) {
         dimensiontype = this.registryHolder.dimensionTypes().getOrThrow(DimensionType.OVERWORLD_LOCATION);
         chunkgenerator = DimensionGeneratorSettings.makeDefaultOverworld(this.registryHolder.registryOrThrow(Registry.BIOME_REGISTRY), this.registryHolder.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY), (new Random()).nextLong());
      } else {
         dimensiontype = dimension.type();
         chunkgenerator = dimension.generator();
      }

      ServerWorld serverworld = new ServerWorld(this, this.executor, this.storageSource, iserverworldinfo, World.OVERWORLD, dimensiontype, p_240787_1_, chunkgenerator, flag, j, list, true);
      this.levels.put(World.OVERWORLD, serverworld);
      DimensionSavedDataManager dimensionsaveddatamanager = serverworld.getDataStorage();
      this.readScoreboard(dimensionsaveddatamanager);
      this.commandStorage = new CommandStorage(dimensionsaveddatamanager);
      WorldBorder worldborder = serverworld.getWorldBorder();
      worldborder.applySettings(iserverworldinfo.getWorldBorder());
      if (!iserverworldinfo.isInitialized()) {
         try {
            setInitialSpawn(serverworld, iserverworldinfo, dimensiongeneratorsettings.generateBonusChest(), flag, true);
            iserverworldinfo.setInitialized(true);
            if (flag) {
               this.setupDebugLevel(this.worldData);
            }
         } catch (Throwable throwable1) {
            CrashReport crashreport = CrashReport.forThrowable(throwable1, "Exception initializing level");

            try {
               serverworld.fillReportDetails(crashreport);
            } catch (Throwable throwable) {
            }

            throw new ReportedException(crashreport);
         }

         iserverworldinfo.setInitialized(true);
      }

      this.getPlayerList().setLevel(serverworld);
      if (this.worldData.getCustomBossEvents() != null) {
         this.getCustomBossEvents().load(this.worldData.getCustomBossEvents());
      }

      for(Entry<RegistryKey<Dimension>, Dimension> entry : simpleregistry.entrySet()) {
         RegistryKey<Dimension> registrykey = entry.getKey();
         if (registrykey != Dimension.OVERWORLD) {
            RegistryKey<World> registrykey1 = RegistryKey.create(Registry.DIMENSION_REGISTRY, registrykey.location());
            DimensionType dimensiontype1 = entry.getValue().type();
            ChunkGenerator chunkgenerator1 = entry.getValue().generator();
            DerivedWorldInfo derivedworldinfo = new DerivedWorldInfo(this.worldData, iserverworldinfo);
            ServerWorld serverworld1 = new ServerWorld(this, this.executor, this.storageSource, derivedworldinfo, registrykey1, dimensiontype1, p_240787_1_, chunkgenerator1, flag, j, ImmutableList.of(), false);
            worldborder.addListener(new IBorderListener.Impl(serverworld1.getWorldBorder()));
            this.levels.put(registrykey1, serverworld1);
         }
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Load(levels.get(registrykey)));
      }

   }

   private static void setInitialSpawn(ServerWorld p_240786_0_, IServerWorldInfo p_240786_1_, boolean p_240786_2_, boolean p_240786_3_, boolean p_240786_4_) {
      ChunkGenerator chunkgenerator = p_240786_0_.getChunkSource().getGenerator();
      if (!p_240786_4_) {
         p_240786_1_.setSpawn(BlockPos.ZERO.above(chunkgenerator.getSpawnHeight()), 0.0F);
      } else if (p_240786_3_) {
         p_240786_1_.setSpawn(BlockPos.ZERO.above(), 0.0F);
      } else {
         if (net.minecraftforge.event.ForgeEventFactory.onCreateWorldSpawn(p_240786_0_, p_240786_1_)) return;
         BiomeProvider biomeprovider = chunkgenerator.getBiomeSource();
         Random random = new Random(p_240786_0_.getSeed());
         BlockPos blockpos = biomeprovider.findBiomeHorizontal(0, p_240786_0_.getSeaLevel(), 0, 256, (p_244265_0_) -> {
            return p_244265_0_.getMobSettings().playerSpawnFriendly();
         }, random);
         ChunkPos chunkpos = blockpos == null ? new ChunkPos(0, 0) : new ChunkPos(blockpos);
         if (blockpos == null) {
            LOGGER.warn("Unable to find spawn biome");
         }

         boolean flag = false;

         for(Block block : BlockTags.VALID_SPAWN.getValues()) {
            if (biomeprovider.getSurfaceBlocks().contains(block.defaultBlockState())) {
               flag = true;
               break;
            }
         }

         p_240786_1_.setSpawn(chunkpos.getWorldPosition().offset(8, chunkgenerator.getSpawnHeight(), 8), 0.0F);
         int i1 = 0;
         int j1 = 0;
         int i = 0;
         int j = -1;
         int k = 32;

         for(int l = 0; l < 1024; ++l) {
            if (i1 > -16 && i1 <= 16 && j1 > -16 && j1 <= 16) {
               BlockPos blockpos1 = SpawnLocationHelper.getSpawnPosInChunk(p_240786_0_, new ChunkPos(chunkpos.x + i1, chunkpos.z + j1), flag);
               if (blockpos1 != null) {
                  p_240786_1_.setSpawn(blockpos1, 0.0F);
                  break;
               }
            }

            if (i1 == j1 || i1 < 0 && i1 == -j1 || i1 > 0 && i1 == 1 - j1) {
               int k1 = i;
               i = -j;
               j = k1;
            }

            i1 += i;
            j1 += j;
         }

         if (p_240786_2_) {
            ConfiguredFeature<?, ?> configuredfeature = Features.BONUS_CHEST;
            configuredfeature.place(p_240786_0_, chunkgenerator, p_240786_0_.random, new BlockPos(p_240786_1_.getXSpawn(), p_240786_1_.getYSpawn(), p_240786_1_.getZSpawn()));
         }

      }
   }

   private void setupDebugLevel(IServerConfiguration p_240778_1_) {
      p_240778_1_.setDifficulty(Difficulty.PEACEFUL);
      p_240778_1_.setDifficultyLocked(true);
      IServerWorldInfo iserverworldinfo = p_240778_1_.overworldData();
      iserverworldinfo.setRaining(false);
      iserverworldinfo.setThundering(false);
      iserverworldinfo.setClearWeatherTime(1000000000);
      iserverworldinfo.setDayTime(6000L);
      iserverworldinfo.setGameType(GameType.SPECTATOR);
   }

   private void prepareLevels(IChunkStatusListener p_213186_1_) {
      net.minecraftforge.common.world.StructureSpawnManager.gatherEntitySpawns();
      ServerWorld serverworld = this.overworld();
      LOGGER.info("Preparing start region for dimension {}", (Object)serverworld.dimension().location());
      BlockPos blockpos = serverworld.getSharedSpawnPos();
      p_213186_1_.updateSpawnPos(new ChunkPos(blockpos));
      ServerChunkProvider serverchunkprovider = serverworld.getChunkSource();
      serverchunkprovider.getLightEngine().setTaskPerBatch(500);
      this.nextTickTime = Util.getMillis();
      serverchunkprovider.addRegionTicket(TicketType.START, new ChunkPos(blockpos), 11, Unit.INSTANCE);

      while(serverchunkprovider.getTickingGenerated() != 441) {
         this.nextTickTime = Util.getMillis() + 10L;
         this.waitUntilNextTick();
      }

      this.nextTickTime = Util.getMillis() + 10L;
      this.waitUntilNextTick();

      for(ServerWorld serverworld1 : this.levels.values()) {
         ForcedChunksSaveData forcedchunkssavedata = serverworld1.getDataStorage().get(ForcedChunksSaveData::new, "chunks");
         if (forcedchunkssavedata != null) {
            LongIterator longiterator = forcedchunkssavedata.getChunks().iterator();

            while(longiterator.hasNext()) {
               long i = longiterator.nextLong();
               ChunkPos chunkpos = new ChunkPos(i);
               serverworld1.getChunkSource().updateChunkForced(chunkpos, true);
            }
         }
      }

      this.nextTickTime = Util.getMillis() + 10L;
      this.waitUntilNextTick();
      p_213186_1_.stop();
      serverchunkprovider.getLightEngine().setTaskPerBatch(5);
      this.updateMobSpawningFlags();
   }

   protected void detectBundledResources() {
      File file1 = this.storageSource.getLevelPath(FolderName.MAP_RESOURCE_FILE).toFile();
      if (file1.isFile()) {
         String s = this.storageSource.getLevelId();

         try {
            this.setResourcePack("level://" + URLEncoder.encode(s, StandardCharsets.UTF_8.toString()) + "/" + "resources.zip", "");
         } catch (UnsupportedEncodingException unsupportedencodingexception) {
            LOGGER.warn("Something went wrong url encoding {}", (Object)s);
         }
      }

   }

   public GameType getDefaultGameType() {
      return this.worldData.getGameType();
   }

   public boolean isHardcore() {
      return this.worldData.isHardcore();
   }

   public abstract int getOperatorUserPermissionLevel();

   public abstract int getFunctionCompilationLevel();

   public abstract boolean shouldRconBroadcast();

   public boolean saveAllChunks(boolean p_213211_1_, boolean p_213211_2_, boolean p_213211_3_) {
      boolean flag = false;

      for(ServerWorld serverworld : this.getAllLevels()) {
         if (!p_213211_1_) {
            LOGGER.info("Saving chunks for level '{}'/{}", serverworld, serverworld.dimension().location());
         }

         serverworld.save((IProgressUpdate)null, p_213211_2_, serverworld.noSave && !p_213211_3_);
         flag = true;
      }

      ServerWorld serverworld1 = this.overworld();
      IServerWorldInfo iserverworldinfo = this.worldData.overworldData();
      iserverworldinfo.setWorldBorder(serverworld1.getWorldBorder().createSettings());
      this.worldData.setCustomBossEvents(this.getCustomBossEvents().save());
      this.storageSource.saveDataTag(this.registryHolder, this.worldData, this.getPlayerList().getSingleplayerData());
      return flag;
   }

   public void close() {
      this.stopServer();
   }

   protected void stopServer() {
      LOGGER.info("Stopping server");
      if (this.getConnection() != null) {
         this.getConnection().stop();
      }

      if (this.playerList != null) {
         LOGGER.info("Saving players");
         this.playerList.saveAll();
         this.playerList.removeAll();
      }

      LOGGER.info("Saving worlds");

      for(ServerWorld serverworld : this.getAllLevels()) {
         if (serverworld != null) {
            serverworld.noSave = false;
         }
      }

      this.saveAllChunks(false, true, false);

      for(ServerWorld serverworld1 : this.getAllLevels()) {
         if (serverworld1 != null) {
            try {
               net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.WorldEvent.Unload(serverworld1));
               serverworld1.close();
            } catch (IOException ioexception1) {
               LOGGER.error("Exception closing the level", (Throwable)ioexception1);
            }
         }
      }

      if (this.snooper.isStarted()) {
         this.snooper.interrupt();
      }

      this.resources.close();

      try {
         this.storageSource.close();
      } catch (IOException ioexception) {
         LOGGER.error("Failed to unlock level {}", this.storageSource.getLevelId(), ioexception);
      }

   }

   public String getLocalIp() {
      return this.localIp;
   }

   public void setLocalIp(String p_71189_1_) {
      this.localIp = p_71189_1_;
   }

   public boolean isRunning() {
      return this.running;
   }

   public void halt(boolean p_71263_1_) {
      this.running = false;
      if (p_71263_1_) {
         try {
            this.serverThread.join();
         } catch (InterruptedException interruptedexception) {
            LOGGER.error("Error while shutting down", (Throwable)interruptedexception);
         }
      }

   }

   protected void runServer() {
      try {
         if (this.initServer()) {
            net.minecraftforge.fml.server.ServerLifecycleHooks.handleServerStarted(this);
            this.nextTickTime = Util.getMillis();
            this.status.setDescription(new StringTextComponent(this.motd));
            this.status.setVersion(new ServerStatusResponse.Version(SharedConstants.getCurrentVersion().getName(), SharedConstants.getCurrentVersion().getProtocolVersion()));
            this.updateStatusIcon(this.status);

            while(this.running) {
               long i = Util.getMillis() - this.nextTickTime;
               if (i > 2000L && this.nextTickTime - this.lastOverloadWarning >= 15000L) {
                  long j = i / 50L;
                  LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", i, j);
                  this.nextTickTime += j * 50L;
                  this.lastOverloadWarning = this.nextTickTime;
               }

               this.nextTickTime += 50L;
               LongTickDetector longtickdetector = LongTickDetector.createTickProfiler("Server");
               this.startProfilerTick(longtickdetector);
               this.profiler.startTick();
               this.profiler.push("tick");
               this.tickServer(this::haveTime);
               this.profiler.popPush("nextTickWait");
               this.mayHaveDelayedTasks = true;
               this.delayedTasksMaxNextTickTime = Math.max(Util.getMillis() + 50L, this.nextTickTime);
               this.waitUntilNextTick();
               this.profiler.pop();
               this.profiler.endTick();
               this.endProfilerTick(longtickdetector);
               this.isReady = true;
            }
            net.minecraftforge.fml.server.ServerLifecycleHooks.handleServerStopping(this);
            net.minecraftforge.fml.server.ServerLifecycleHooks.expectServerStopped(); // has to come before finalTick to avoid race conditions
         } else {
            net.minecraftforge.fml.server.ServerLifecycleHooks.expectServerStopped(); // has to come before finalTick to avoid race conditions
            this.onServerCrash((CrashReport)null);
         }
      } catch (Throwable throwable1) {
         LOGGER.error("Encountered an unexpected exception", throwable1);
         CrashReport crashreport;
         if (throwable1 instanceof ReportedException) {
            crashreport = this.fillReport(((ReportedException)throwable1).getReport());
         } else {
            crashreport = this.fillReport(new CrashReport("Exception in server tick loop", throwable1));
         }

         File file1 = new File(new File(this.getServerDirectory(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");
         if (crashreport.saveToFile(file1)) {
            LOGGER.error("This crash report has been saved to: {}", (Object)file1.getAbsolutePath());
         } else {
            LOGGER.error("We were unable to save this crash report to disk.");
         }

         net.minecraftforge.fml.server.ServerLifecycleHooks.expectServerStopped(); // has to come before finalTick to avoid race conditions
         this.onServerCrash(crashreport);
      } finally {
         try {
            this.stopped = true;
            this.stopServer();
         } catch (Throwable throwable) {
            LOGGER.error("Exception stopping the server", throwable);
         } finally {
            net.minecraftforge.fml.server.ServerLifecycleHooks.handleServerStopped(this);
            this.onServerExit();
         }

      }

   }

   private boolean haveTime() {
      return this.runningTask() || Util.getMillis() < (this.mayHaveDelayedTasks ? this.delayedTasksMaxNextTickTime : this.nextTickTime);
   }

   protected void waitUntilNextTick() {
      this.runAllTasks();
      this.managedBlock(() -> {
         return !this.haveTime();
      });
   }

   protected TickDelayedTask wrapRunnable(Runnable p_212875_1_) {
      return new TickDelayedTask(this.tickCount, p_212875_1_);
   }

   protected boolean shouldRun(TickDelayedTask p_212874_1_) {
      return p_212874_1_.getTick() + 3 < this.tickCount || this.haveTime();
   }

   public boolean pollTask() {
      boolean flag = this.pollTaskInternal();
      this.mayHaveDelayedTasks = flag;
      return flag;
   }

   private boolean pollTaskInternal() {
      if (super.pollTask()) {
         return true;
      } else {
         if (this.haveTime()) {
            for(ServerWorld serverworld : this.getAllLevels()) {
               if (serverworld.getChunkSource().pollTask()) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   protected void doRunTask(TickDelayedTask p_213166_1_) {
      this.getProfiler().incrementCounter("runTask");
      super.doRunTask(p_213166_1_);
   }

   private void updateStatusIcon(ServerStatusResponse p_184107_1_) {
      File file1 = this.getFile("server-icon.png");
      if (!file1.exists()) {
         file1 = this.storageSource.getIconFile();
      }

      if (file1.isFile()) {
         ByteBuf bytebuf = Unpooled.buffer();

         try {
            BufferedImage bufferedimage = ImageIO.read(file1);
            Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide");
            Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high");
            ImageIO.write(bufferedimage, "PNG", new ByteBufOutputStream(bytebuf));
            ByteBuffer bytebuffer = Base64.getEncoder().encode(bytebuf.nioBuffer());
            p_184107_1_.setFavicon("data:image/png;base64," + StandardCharsets.UTF_8.decode(bytebuffer));
         } catch (Exception exception) {
            LOGGER.error("Couldn't load server icon", (Throwable)exception);
         } finally {
            bytebuf.release();
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasWorldScreenshot() {
      this.hasWorldScreenshot = this.hasWorldScreenshot || this.getWorldScreenshotFile().isFile();
      return this.hasWorldScreenshot;
   }

   @OnlyIn(Dist.CLIENT)
   public File getWorldScreenshotFile() {
      return this.storageSource.getIconFile();
   }

   public File getServerDirectory() {
      return new File(".");
   }

   protected void onServerCrash(CrashReport p_71228_1_) {
   }

   protected void onServerExit() {
   }

   protected void tickServer(BooleanSupplier p_71217_1_) {
      long i = Util.getNanos();
      net.minecraftforge.fml.hooks.BasicEventHooks.onPreServerTick();
      ++this.tickCount;
      this.tickChildren(p_71217_1_);
      if (i - this.lastServerStatus >= 5000000000L) {
         this.lastServerStatus = i;
         this.status.setPlayers(new ServerStatusResponse.Players(this.getMaxPlayers(), this.getPlayerCount()));
         GameProfile[] agameprofile = new GameProfile[Math.min(this.getPlayerCount(), 12)];
         int j = MathHelper.nextInt(this.random, 0, this.getPlayerCount() - agameprofile.length);

         for(int k = 0; k < agameprofile.length; ++k) {
            agameprofile[k] = this.playerList.getPlayers().get(j + k).getGameProfile();
         }

         Collections.shuffle(Arrays.asList(agameprofile));
         this.status.getPlayers().setSample(agameprofile);
         this.status.invalidateJson();
      }

      if (this.tickCount % 6000 == 0) {
         LOGGER.debug("Autosave started");
         this.profiler.push("save");
         this.playerList.saveAll();
         this.saveAllChunks(true, false, false);
         this.profiler.pop();
         LOGGER.debug("Autosave finished");
      }

      this.profiler.push("snooper");
      if (!this.snooper.isStarted() && this.tickCount > 100) {
         this.snooper.start();
      }

      if (this.tickCount % 6000 == 0) {
         this.snooper.prepare();
      }

      this.profiler.pop();
      this.profiler.push("tallying");
      long l = this.tickTimes[this.tickCount % 100] = Util.getNanos() - i;
      this.averageTickTime = this.averageTickTime * 0.8F + (float)l / 1000000.0F * 0.19999999F;
      long i1 = Util.getNanos();
      this.frameTimer.logFrameDuration(i1 - i);
      this.profiler.pop();
      net.minecraftforge.fml.hooks.BasicEventHooks.onPostServerTick();
   }

   protected void tickChildren(BooleanSupplier p_71190_1_) {
      this.profiler.push("commandFunctions");
      this.getFunctions().tick();
      this.profiler.popPush("levels");

      for(ServerWorld serverworld : this.getWorldArray()) {
         long tickStart = Util.getNanos();
         this.profiler.push(() -> {
            return serverworld + " " + serverworld.dimension().location();
         });
         if (this.tickCount % 20 == 0) {
            this.profiler.push("timeSync");
            this.playerList.broadcastAll(new SUpdateTimePacket(serverworld.getGameTime(), serverworld.getDayTime(), serverworld.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)), serverworld.dimension());
            this.profiler.pop();
         }

         this.profiler.push("tick");
         net.minecraftforge.fml.hooks.BasicEventHooks.onPreWorldTick(serverworld);

         try {
            serverworld.tick(p_71190_1_);
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Exception ticking world");
            serverworld.fillReportDetails(crashreport);
            throw new ReportedException(crashreport);
         }
         net.minecraftforge.fml.hooks.BasicEventHooks.onPostWorldTick(serverworld);

         this.profiler.pop();
         this.profiler.pop();
         perWorldTickTimes.computeIfAbsent(serverworld.dimension(), k -> new long[100])[this.tickCount % 100] = Util.getNanos() - tickStart;
      }

      this.profiler.popPush("connection");
      this.getConnection().tick();
      this.profiler.popPush("players");
      this.playerList.tick();
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         TestCollection.singleton.tick();
      }

      this.profiler.popPush("server gui refresh");

      for(int i = 0; i < this.tickables.size(); ++i) {
         this.tickables.get(i).run();
      }

      this.profiler.pop();
   }

   public boolean isNetherEnabled() {
      return true;
   }

   public void addTickable(Runnable p_82010_1_) {
      this.tickables.add(p_82010_1_);
   }

   protected void setId(String p_213208_1_) {
      this.serverId = p_213208_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isShutdown() {
      return !this.serverThread.isAlive();
   }

   public File getFile(String p_71209_1_) {
      return new File(this.getServerDirectory(), p_71209_1_);
   }

   public final ServerWorld overworld() {
      return this.levels.get(World.OVERWORLD);
   }

   @Nullable
   public ServerWorld getLevel(RegistryKey<World> p_71218_1_) {
      return this.levels.get(p_71218_1_);
   }

   public Set<RegistryKey<World>> levelKeys() {
      return this.levels.keySet();
   }

   public Iterable<ServerWorld> getAllLevels() {
      return this.levels.values();
   }

   public String getServerVersion() {
      return SharedConstants.getCurrentVersion().getName();
   }

   public int getPlayerCount() {
      return this.playerList.getPlayerCount();
   }

   public int getMaxPlayers() {
      return this.playerList.getMaxPlayers();
   }

   public String[] getPlayerNames() {
      return this.playerList.getPlayerNamesArray();
   }

   public String getServerModName() {
      return net.minecraftforge.fml.BrandingControl.getServerBranding();
   }

   public CrashReport fillReport(CrashReport p_71230_1_) {
      if (this.playerList != null) {
         p_71230_1_.getSystemDetails().setDetail("Player Count", () -> {
            return this.playerList.getPlayerCount() + " / " + this.playerList.getMaxPlayers() + "; " + this.playerList.getPlayers();
         });
      }

      p_71230_1_.getSystemDetails().setDetail("Data Packs", () -> {
         StringBuilder stringbuilder = new StringBuilder();

               LogManager.shutdown(); // we're manually managing the logging shutdown on the server. Make sure we do it here at the end.
         for(ResourcePackInfo resourcepackinfo : this.packRepository.getSelectedPacks()) {
            if (stringbuilder.length() > 0) {
               stringbuilder.append(", ");
            }

            stringbuilder.append(resourcepackinfo.getId());
            if (!resourcepackinfo.getCompatibility().isCompatible()) {
               stringbuilder.append(" (incompatible)");
            }
         }

         return stringbuilder.toString();
      });
      if (this.serverId != null) {
         p_71230_1_.getSystemDetails().setDetail("Server Id", () -> {
            return this.serverId;
         });
      }

      return p_71230_1_;
   }

   public abstract Optional<String> getModdedStatus();

   public void sendMessage(ITextComponent p_145747_1_, UUID p_145747_2_) {
      LOGGER.info(p_145747_1_.getString());
   }

   public KeyPair getKeyPair() {
      return this.keyPair;
   }

   public int getPort() {
      return this.port;
   }

   public void setPort(int p_71208_1_) {
      this.port = p_71208_1_;
   }

   public String getSingleplayerName() {
      return this.singleplayerName;
   }

   public void setSingleplayerName(String p_71224_1_) {
      this.singleplayerName = p_71224_1_;
   }

   public boolean isSingleplayer() {
      return this.singleplayerName != null;
   }

   protected void initializeKeyPair() {
      LOGGER.info("Generating keypair");

      try {
         this.keyPair = CryptManager.generateKeyPair();
      } catch (CryptException cryptexception) {
         throw new IllegalStateException("Failed to generate key pair", cryptexception);
      }
   }

   public void setDifficulty(Difficulty p_147139_1_, boolean p_147139_2_) {
      if (p_147139_2_ || !this.worldData.isDifficultyLocked()) {
         this.worldData.setDifficulty(this.worldData.isHardcore() ? Difficulty.HARD : p_147139_1_);
         this.updateMobSpawningFlags();
         this.getPlayerList().getPlayers().forEach(this::sendDifficultyUpdate);
      }
   }

   public int getScaledTrackingDistance(int p_230512_1_) {
      return p_230512_1_;
   }

   private void updateMobSpawningFlags() {
      for(ServerWorld serverworld : this.getAllLevels()) {
         serverworld.setSpawnSettings(this.isSpawningMonsters(), this.isSpawningAnimals());
      }

   }

   public void setDifficultyLocked(boolean p_213209_1_) {
      this.worldData.setDifficultyLocked(p_213209_1_);
      this.getPlayerList().getPlayers().forEach(this::sendDifficultyUpdate);
   }

   private void sendDifficultyUpdate(ServerPlayerEntity p_213189_1_) {
      IWorldInfo iworldinfo = p_213189_1_.getLevel().getLevelData();
      p_213189_1_.connection.send(new SServerDifficultyPacket(iworldinfo.getDifficulty(), iworldinfo.isDifficultyLocked()));
   }

   protected boolean isSpawningMonsters() {
      return this.worldData.getDifficulty() != Difficulty.PEACEFUL;
   }

   public boolean isDemo() {
      return this.isDemo;
   }

   public void setDemo(boolean p_71204_1_) {
      this.isDemo = p_71204_1_;
   }

   public String getResourcePack() {
      return this.resourcePack;
   }

   public String getResourcePackHash() {
      return this.resourcePackHash;
   }

   public void setResourcePack(String p_180507_1_, String p_180507_2_) {
      this.resourcePack = p_180507_1_;
      this.resourcePackHash = p_180507_2_;
   }

   public void populateSnooper(Snooper p_70000_1_) {
      p_70000_1_.setDynamicData("whitelist_enabled", false);
      p_70000_1_.setDynamicData("whitelist_count", 0);
      if (this.playerList != null) {
         p_70000_1_.setDynamicData("players_current", this.getPlayerCount());
         p_70000_1_.setDynamicData("players_max", this.getMaxPlayers());
         p_70000_1_.setDynamicData("players_seen", this.playerDataStorage.getSeenPlayers().length);
      }

      p_70000_1_.setDynamicData("uses_auth", this.onlineMode);
      p_70000_1_.setDynamicData("gui_state", this.hasGui() ? "enabled" : "disabled");
      p_70000_1_.setDynamicData("run_time", (Util.getMillis() - p_70000_1_.getStartupTime()) / 60L * 1000L);
      p_70000_1_.setDynamicData("avg_tick_ms", (int)(MathHelper.average(this.tickTimes) * 1.0E-6D));
      int i = 0;

      for(ServerWorld serverworld : this.getAllLevels()) {
         if (serverworld != null) {
            p_70000_1_.setDynamicData("world[" + i + "][dimension]", serverworld.dimension().location());
            p_70000_1_.setDynamicData("world[" + i + "][mode]", this.worldData.getGameType());
            p_70000_1_.setDynamicData("world[" + i + "][difficulty]", serverworld.getDifficulty());
            p_70000_1_.setDynamicData("world[" + i + "][hardcore]", this.worldData.isHardcore());
            p_70000_1_.setDynamicData("world[" + i + "][height]", this.maxBuildHeight);
            p_70000_1_.setDynamicData("world[" + i + "][chunks_loaded]", serverworld.getChunkSource().getLoadedChunksCount());
            ++i;
         }
      }

      p_70000_1_.setDynamicData("worlds", i);
   }

   public abstract boolean isDedicatedServer();

   public abstract int getRateLimitPacketsPerSecond();

   public boolean usesAuthentication() {
      return this.onlineMode;
   }

   public void setUsesAuthentication(boolean p_71229_1_) {
      this.onlineMode = p_71229_1_;
   }

   public boolean getPreventProxyConnections() {
      return this.preventProxyConnections;
   }

   public void setPreventProxyConnections(boolean p_190517_1_) {
      this.preventProxyConnections = p_190517_1_;
   }

   public boolean isSpawningAnimals() {
      return true;
   }

   public boolean areNpcsEnabled() {
      return true;
   }

   public abstract boolean isEpollEnabled();

   public boolean isPvpAllowed() {
      return this.pvp;
   }

   public void setPvpAllowed(boolean p_71188_1_) {
      this.pvp = p_71188_1_;
   }

   public boolean isFlightAllowed() {
      return this.allowFlight;
   }

   public void setFlightAllowed(boolean p_71245_1_) {
      this.allowFlight = p_71245_1_;
   }

   public abstract boolean isCommandBlockEnabled();

   public String getMotd() {
      return this.motd;
   }

   public void setMotd(String p_71205_1_) {
      this.motd = p_71205_1_;
   }

   public int getMaxBuildHeight() {
      return this.maxBuildHeight;
   }

   public void setMaxBuildHeight(int p_71191_1_) {
      this.maxBuildHeight = p_71191_1_;
   }

   public boolean isStopped() {
      return this.stopped;
   }

   public PlayerList getPlayerList() {
      return this.playerList;
   }

   public void setPlayerList(PlayerList p_184105_1_) {
      this.playerList = p_184105_1_;
   }

   public abstract boolean isPublished();

   public void setDefaultGameType(GameType p_71235_1_) {
      this.worldData.setGameType(p_71235_1_);
   }

   @Nullable
   public NetworkSystem getConnection() {
      return this.connection;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isReady() {
      return this.isReady;
   }

   public boolean hasGui() {
      return false;
   }

   public abstract boolean publishServer(GameType p_195565_1_, boolean p_195565_2_, int p_195565_3_);

   public int getTickCount() {
      return this.tickCount;
   }

   @OnlyIn(Dist.CLIENT)
   public Snooper getSnooper() {
      return this.snooper;
   }

   public int getSpawnProtectionRadius() {
      return 16;
   }

   public boolean isUnderSpawnProtection(ServerWorld p_175579_1_, BlockPos p_175579_2_, PlayerEntity p_175579_3_) {
      return false;
   }

   public void setForceGameType(boolean p_104055_1_) {
      this.forceGameType = p_104055_1_;
   }

   public boolean getForceGameType() {
      return this.forceGameType;
   }

   public boolean repliesToStatus() {
      return true;
   }

   public int getPlayerIdleTimeout() {
      return this.playerIdleTimeout;
   }

   public void setPlayerIdleTimeout(int p_143006_1_) {
      this.playerIdleTimeout = p_143006_1_;
   }

   public MinecraftSessionService getSessionService() {
      return this.sessionService;
   }

   public GameProfileRepository getProfileRepository() {
      return this.profileRepository;
   }

   public PlayerProfileCache getProfileCache() {
      return this.profileCache;
   }

   public ServerStatusResponse getStatus() {
      return this.status;
   }

   public void invalidateStatus() {
      this.lastServerStatus = 0L;
   }

   public int getAbsoluteMaxWorldSize() {
      return 29999984;
   }

   public boolean scheduleExecutables() {
      return super.scheduleExecutables() && !this.isStopped();
   }

   public Thread getRunningThread() {
      return this.serverThread;
   }

   public int getCompressionThreshold() {
      return 256;
   }

   public long getNextTickTime() {
      return this.nextTickTime;
   }

   public DataFixer getFixerUpper() {
      return this.fixerUpper;
   }

   public int getSpawnRadius(@Nullable ServerWorld p_184108_1_) {
      return p_184108_1_ != null ? p_184108_1_.getGameRules().getInt(GameRules.RULE_SPAWN_RADIUS) : 10;
   }

   public AdvancementManager getAdvancements() {
      return this.resources.getAdvancements();
   }

   public FunctionManager getFunctions() {
      return this.functionManager;
   }

   public CompletableFuture<Void> reloadResources(Collection<String> p_240780_1_) {
      CompletableFuture<Void> completablefuture = CompletableFuture.supplyAsync(() -> {
         return p_240780_1_.stream().map(this.packRepository::getPack).filter(Objects::nonNull).map(ResourcePackInfo::open).collect(ImmutableList.toImmutableList());
      }, this).thenCompose((p_240775_1_) -> {
         return DataPackRegistries.loadResources(p_240775_1_, this.isDedicatedServer() ? Commands.EnvironmentType.DEDICATED : Commands.EnvironmentType.INTEGRATED, this.getFunctionCompilationLevel(), this.executor, this);
      }).thenAcceptAsync((p_240782_2_) -> {
         this.resources.close();
         this.resources = p_240782_2_;
         this.packRepository.setSelected(p_240780_1_);
         this.worldData.setDataPackConfig(getSelectedPacks(this.packRepository));
         p_240782_2_.updateGlobals();
         this.getPlayerList().saveAll();
         this.getPlayerList().reloadResources();
         this.functionManager.replaceLibrary(this.resources.getFunctionLibrary());
         this.structureManager.onResourceManagerReload(this.resources.getResourceManager());
         this.getPlayerList().getPlayers().forEach(this.getPlayerList()::sendPlayerPermissionLevel); //Forge: Fix newly added/modified commands not being sent to the client when commands reload.
      }, this);
      if (this.isSameThread()) {
         this.managedBlock(completablefuture::isDone);
      }

      return completablefuture;
   }

   public static DatapackCodec configurePackRepository(ResourcePackList p_240772_0_, DatapackCodec p_240772_1_, boolean p_240772_2_) {
      net.minecraftforge.fml.packs.ResourcePackLoader.loadResourcePacks(p_240772_0_, net.minecraftforge.fml.server.ServerLifecycleHooks::buildPackFinder);
      p_240772_0_.reload();
      DatapackCodec.DEFAULT.addModPacks(net.minecraftforge.common.ForgeHooks.getModPacks());
      p_240772_1_.addModPacks(net.minecraftforge.common.ForgeHooks.getModPacks());
      if (p_240772_2_) {
         p_240772_0_.setSelected(net.minecraftforge.common.ForgeHooks.getModPacksWithVanilla());
         return new DatapackCodec(net.minecraftforge.common.ForgeHooks.getModPacksWithVanilla(), ImmutableList.of());
      } else {
         Set<String> set = Sets.newLinkedHashSet();

         for(String s : p_240772_1_.getEnabled()) {
            if (p_240772_0_.isAvailable(s)) {
               set.add(s);
            } else {
               LOGGER.warn("Missing data pack {}", (Object)s);
            }
         }

         for(ResourcePackInfo resourcepackinfo : p_240772_0_.getAvailablePacks()) {
            String s1 = resourcepackinfo.getId();
            if (!p_240772_1_.getDisabled().contains(s1) && !set.contains(s1)) {
               LOGGER.info("Found new data pack {}, loading it automatically", (Object)s1);
               set.add(s1);
            }
         }

         if (set.isEmpty()) {
            LOGGER.info("No datapacks selected, forcing vanilla");
            set.add("vanilla");
         }

         p_240772_0_.setSelected(set);
         return getSelectedPacks(p_240772_0_);
      }
   }

   private static DatapackCodec getSelectedPacks(ResourcePackList p_240771_0_) {
      Collection<String> collection = p_240771_0_.getSelectedIds();
      List<String> list = ImmutableList.copyOf(collection);
      List<String> list1 = p_240771_0_.getAvailableIds().stream().filter((p_240781_1_) -> {
         return !collection.contains(p_240781_1_);
      }).collect(ImmutableList.toImmutableList());
      return new DatapackCodec(list, list1);
   }

   public void kickUnlistedPlayers(CommandSource p_205743_1_) {
      if (this.isEnforceWhitelist()) {
         PlayerList playerlist = p_205743_1_.getServer().getPlayerList();
         WhiteList whitelist = playerlist.getWhiteList();

         for(ServerPlayerEntity serverplayerentity : Lists.newArrayList(playerlist.getPlayers())) {
            if (!whitelist.isWhiteListed(serverplayerentity.getGameProfile())) {
               serverplayerentity.connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.not_whitelisted"));
            }
         }

      }
   }

   public ResourcePackList getPackRepository() {
      return this.packRepository;
   }

   public Commands getCommands() {
      return this.resources.getCommands();
   }

   public CommandSource createCommandSourceStack() {
      ServerWorld serverworld = this.overworld();
      return new CommandSource(this, serverworld == null ? Vector3d.ZERO : Vector3d.atLowerCornerOf(serverworld.getSharedSpawnPos()), Vector2f.ZERO, serverworld, 4, "Server", new StringTextComponent("Server"), this, (Entity)null);
   }

   public boolean acceptsSuccess() {
      return true;
   }

   public boolean acceptsFailure() {
      return true;
   }

   public RecipeManager getRecipeManager() {
      return this.resources.getRecipeManager();
   }

   public ITagCollectionSupplier getTags() {
      return this.resources.getTags();
   }

   public ServerScoreboard getScoreboard() {
      return this.scoreboard;
   }

   public CommandStorage getCommandStorage() {
      if (this.commandStorage == null) {
         throw new NullPointerException("Called before server init");
      } else {
         return this.commandStorage;
      }
   }

   public LootTableManager getLootTables() {
      return this.resources.getLootTables();
   }

   public LootPredicateManager getPredicateManager() {
      return this.resources.getPredicateManager();
   }

   public GameRules getGameRules() {
      return this.overworld().getGameRules();
   }

   public CustomServerBossInfoManager getCustomBossEvents() {
      return this.customBossEvents;
   }

   public boolean isEnforceWhitelist() {
      return this.enforceWhitelist;
   }

   public void setEnforceWhitelist(boolean p_205741_1_) {
      this.enforceWhitelist = p_205741_1_;
   }

   public float getAverageTickTime() {
      return this.averageTickTime;
   }

   public int getProfilePermissions(GameProfile p_211833_1_) {
      if (this.getPlayerList().isOp(p_211833_1_)) {
         OpEntry opentry = this.getPlayerList().getOps().get(p_211833_1_);
         if (opentry != null) {
            return opentry.getLevel();
         } else if (this.isSingleplayerOwner(p_211833_1_)) {
            return 4;
         } else if (this.isSingleplayer()) {
            return this.getPlayerList().isAllowCheatsForAllPlayers() ? 4 : 0;
         } else {
            return this.getOperatorUserPermissionLevel();
         }
      } else {
         return 0;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public FrameTimer getFrameTimer() {
      return this.frameTimer;
   }

   public IProfiler getProfiler() {
      return this.profiler;
   }

   public abstract boolean isSingleplayerOwner(GameProfile p_213199_1_);

   private Map<RegistryKey<World>, long[]> perWorldTickTimes = Maps.newIdentityHashMap();
   @Nullable
   public long[] getTickTime(RegistryKey<World> dim) {
      return perWorldTickTimes.get(dim);
   }

   @Deprecated //Forge Internal use Only, You can screw up a lot of things if you mess with this map.
   public synchronized Map<RegistryKey<World>, ServerWorld> forgeGetWorldMap() {
      return this.levels;
   }
   private int worldArrayMarker = 0;
   private int worldArrayLast = -1;
   private ServerWorld[] worldArray;
   @Deprecated //Forge Internal use Only, use to protect against concurrent modifications in the world tick loop.
   public synchronized void markWorldsDirty() {
      worldArrayMarker++;
   }
   private ServerWorld[] getWorldArray() {
      if (worldArrayMarker == worldArrayLast && worldArray != null)
         return worldArray;
      worldArray = this.levels.values().stream().toArray(x -> new ServerWorld[x]);
      worldArrayLast = worldArrayMarker;
      return worldArray;
   }

   public void saveDebugReport(Path p_223711_1_) throws IOException {
      Path path = p_223711_1_.resolve("levels");

      for(Entry<RegistryKey<World>, ServerWorld> entry : this.levels.entrySet()) {
         ResourceLocation resourcelocation = entry.getKey().location();
         Path path1 = path.resolve(resourcelocation.getNamespace()).resolve(resourcelocation.getPath());
         Files.createDirectories(path1);
         entry.getValue().saveDebugReport(path1);
      }

      this.dumpGameRules(p_223711_1_.resolve("gamerules.txt"));
      this.dumpClasspath(p_223711_1_.resolve("classpath.txt"));
      this.dumpCrashCategory(p_223711_1_.resolve("example_crash.txt"));
      this.dumpMiscStats(p_223711_1_.resolve("stats.txt"));
      this.dumpThreads(p_223711_1_.resolve("threads.txt"));
   }

   private void dumpMiscStats(Path p_223710_1_) throws IOException {
      try (Writer writer = Files.newBufferedWriter(p_223710_1_)) {
         writer.write(String.format("pending_tasks: %d\n", this.getPendingTasksCount()));
         writer.write(String.format("average_tick_time: %f\n", this.getAverageTickTime()));
         writer.write(String.format("tick_times: %s\n", Arrays.toString(this.tickTimes)));
         writer.write(String.format("queue: %s\n", Util.backgroundExecutor()));
      }

   }

   private void dumpCrashCategory(Path p_223709_1_) throws IOException {
      CrashReport crashreport = new CrashReport("Server dump", new Exception("dummy"));
      this.fillReport(crashreport);

      try (Writer writer = Files.newBufferedWriter(p_223709_1_)) {
         writer.write(crashreport.getFriendlyReport());
      }

   }

   private void dumpGameRules(Path p_223708_1_) throws IOException {
      try (Writer writer = Files.newBufferedWriter(p_223708_1_)) {
         final List<String> list = Lists.newArrayList();
         final GameRules gamerules = this.getGameRules();
         GameRules.visitGameRuleTypes(new GameRules.IRuleEntryVisitor() {
            public <T extends GameRules.RuleValue<T>> void visit(GameRules.RuleKey<T> p_223481_1_, GameRules.RuleType<T> p_223481_2_) {
               list.add(String.format("%s=%s\n", p_223481_1_.getId(), gamerules.<T>getRule(p_223481_1_).toString()));
            }
         });

         for(String s : list) {
            writer.write(s);
         }
      }

   }

   private void dumpClasspath(Path p_223706_1_) throws IOException {
      try (Writer writer = Files.newBufferedWriter(p_223706_1_)) {
         String s = System.getProperty("java.class.path");
         String s1 = System.getProperty("path.separator");

         for(String s2 : Splitter.on(s1).split(s)) {
            writer.write(s2);
            writer.write("\n");
         }
      }

   }

   private void dumpThreads(Path p_223712_1_) throws IOException {
      ThreadMXBean threadmxbean = ManagementFactory.getThreadMXBean();
      ThreadInfo[] athreadinfo = threadmxbean.dumpAllThreads(true, true);
      Arrays.sort(athreadinfo, Comparator.comparing(ThreadInfo::getThreadName));

      try (Writer writer = Files.newBufferedWriter(p_223712_1_)) {
         for(ThreadInfo threadinfo : athreadinfo) {
            writer.write(threadinfo.toString());
            writer.write(10);
         }
      }

   }

   private void startProfilerTick(@Nullable LongTickDetector p_240773_1_) {
      if (this.delayProfilerStart) {
         this.delayProfilerStart = false;
         this.continousProfiler.enable();
      }

      this.profiler = LongTickDetector.decorateFiller(this.continousProfiler.getFiller(), p_240773_1_);
   }

   private void endProfilerTick(@Nullable LongTickDetector p_240795_1_) {
      if (p_240795_1_ != null) {
         p_240795_1_.endTick();
      }

      this.profiler = this.continousProfiler.getFiller();
   }

   public boolean isProfiling() {
      return this.continousProfiler.isEnabled();
   }

   public void startProfiling() {
      this.delayProfilerStart = true;
   }

   public IProfileResult finishProfiling() {
      IProfileResult iprofileresult = this.continousProfiler.getResults();
      this.continousProfiler.disable();
      return iprofileresult;
   }

   public Path getWorldPath(FolderName p_240776_1_) {
      return this.storageSource.getLevelPath(p_240776_1_);
   }

   public boolean forceSynchronousWrites() {
      return true;
   }

   public TemplateManager getStructureManager() {
      return this.structureManager;
   }

   public IServerConfiguration getWorldData() {
      return this.worldData;
   }

   public DataPackRegistries getDataPackRegistries() {
       return resources;
   }

   public DynamicRegistries registryAccess() {
      return this.registryHolder;
   }

   @Nullable
   public IChatFilter createTextFilterForPlayer(ServerPlayerEntity p_244435_1_) {
      return null;
   }
}
