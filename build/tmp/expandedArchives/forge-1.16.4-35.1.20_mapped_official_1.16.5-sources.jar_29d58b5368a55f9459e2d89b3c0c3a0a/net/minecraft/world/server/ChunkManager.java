package net.minecraft.world.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.network.play.server.SMountEntityPacket;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.network.play.server.SUpdateChunkPositionPacket;
import net.minecraft.network.play.server.SUpdateLightPacket;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.CSVWriter;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.DelegatedTaskExecutor;
import net.minecraft.util.concurrent.ITaskExecutor;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.GameRules;
import net.minecraft.world.TrackedEntity;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkPrimerWrapper;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ChunkTaskPriorityQueue;
import net.minecraft.world.chunk.ChunkTaskPriorityQueueSorter;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.PlayerGenerationTracker;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.chunk.storage.ChunkLoader;
import net.minecraft.world.chunk.storage.ChunkSerializer;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.SaveFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkManager extends ChunkLoader implements ChunkHolder.IPlayerProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final int MAX_CHUNK_DISTANCE = 33 + ChunkStatus.maxDistance();
   private final Long2ObjectLinkedOpenHashMap<ChunkHolder> updatingChunkMap = new Long2ObjectLinkedOpenHashMap<>();
   private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> visibleChunkMap = this.updatingChunkMap.clone();
   private final Long2ObjectLinkedOpenHashMap<ChunkHolder> pendingUnloads = new Long2ObjectLinkedOpenHashMap<>();
   private final LongSet entitiesInLevel = new LongOpenHashSet();
   private final ServerWorld level;
   private final ServerWorldLightManager lightEngine;
   private final ThreadTaskExecutor<Runnable> mainThreadExecutor;
   private final ChunkGenerator generator;
   private final Supplier<DimensionSavedDataManager> overworldDataStorage;
   private final PointOfInterestManager poiManager;
   private final LongSet toDrop = new LongOpenHashSet();
   private boolean modified;
   private final ChunkTaskPriorityQueueSorter queueSorter;
   private final ITaskExecutor<ChunkTaskPriorityQueueSorter.FunctionEntry<Runnable>> worldgenMailbox;
   private final ITaskExecutor<ChunkTaskPriorityQueueSorter.FunctionEntry<Runnable>> mainThreadMailbox;
   private final IChunkStatusListener progressListener;
   private final ChunkManager.ProxyTicketManager distanceManager;
   private final AtomicInteger tickingGenerated = new AtomicInteger();
   private final TemplateManager structureManager;
   private final File storageFolder;
   private final PlayerGenerationTracker playerMap = new PlayerGenerationTracker();
   private final Int2ObjectMap<ChunkManager.EntityTracker> entityMap = new Int2ObjectOpenHashMap<>();
   private final Long2ByteMap chunkTypeCache = new Long2ByteOpenHashMap();
   private final Queue<Runnable> unloadQueue = Queues.newConcurrentLinkedQueue();
   private int viewDistance;

   public ChunkManager(ServerWorld p_i232602_1_, SaveFormat.LevelSave p_i232602_2_, DataFixer p_i232602_3_, TemplateManager p_i232602_4_, Executor p_i232602_5_, ThreadTaskExecutor<Runnable> p_i232602_6_, IChunkLightProvider p_i232602_7_, ChunkGenerator p_i232602_8_, IChunkStatusListener p_i232602_9_, Supplier<DimensionSavedDataManager> p_i232602_10_, int p_i232602_11_, boolean p_i232602_12_) {
      super(new File(p_i232602_2_.getDimensionPath(p_i232602_1_.dimension()), "region"), p_i232602_3_, p_i232602_12_);
      this.structureManager = p_i232602_4_;
      this.storageFolder = p_i232602_2_.getDimensionPath(p_i232602_1_.dimension());
      this.level = p_i232602_1_;
      this.generator = p_i232602_8_;
      this.mainThreadExecutor = p_i232602_6_;
      DelegatedTaskExecutor<Runnable> delegatedtaskexecutor = DelegatedTaskExecutor.create(p_i232602_5_, "worldgen");
      ITaskExecutor<Runnable> itaskexecutor = ITaskExecutor.of("main", p_i232602_6_::tell);
      this.progressListener = p_i232602_9_;
      DelegatedTaskExecutor<Runnable> delegatedtaskexecutor1 = DelegatedTaskExecutor.create(p_i232602_5_, "light");
      this.queueSorter = new ChunkTaskPriorityQueueSorter(ImmutableList.of(delegatedtaskexecutor, itaskexecutor, delegatedtaskexecutor1), p_i232602_5_, Integer.MAX_VALUE);
      this.worldgenMailbox = this.queueSorter.getProcessor(delegatedtaskexecutor, false);
      this.mainThreadMailbox = this.queueSorter.getProcessor(itaskexecutor, false);
      this.lightEngine = new ServerWorldLightManager(p_i232602_7_, this, this.level.dimensionType().hasSkyLight(), delegatedtaskexecutor1, this.queueSorter.getProcessor(delegatedtaskexecutor1, false));
      this.distanceManager = new ChunkManager.ProxyTicketManager(p_i232602_5_, p_i232602_6_);
      this.overworldDataStorage = p_i232602_10_;
      this.poiManager = new PointOfInterestManager(new File(this.storageFolder, "poi"), p_i232602_3_, p_i232602_12_);
      this.setViewDistance(p_i232602_11_);
   }

   private static double euclideanDistanceSquared(ChunkPos p_219217_0_, Entity p_219217_1_) {
      double d0 = (double)(p_219217_0_.x * 16 + 8);
      double d1 = (double)(p_219217_0_.z * 16 + 8);
      double d2 = d0 - p_219217_1_.getX();
      double d3 = d1 - p_219217_1_.getZ();
      return d2 * d2 + d3 * d3;
   }

   private static int checkerboardDistance(ChunkPos p_219215_0_, ServerPlayerEntity p_219215_1_, boolean p_219215_2_) {
      int i;
      int j;
      if (p_219215_2_) {
         SectionPos sectionpos = p_219215_1_.getLastSectionPos();
         i = sectionpos.x();
         j = sectionpos.z();
      } else {
         i = MathHelper.floor(p_219215_1_.getX() / 16.0D);
         j = MathHelper.floor(p_219215_1_.getZ() / 16.0D);
      }

      return checkerboardDistance(p_219215_0_, i, j);
   }

   private static int checkerboardDistance(ChunkPos p_219232_0_, int p_219232_1_, int p_219232_2_) {
      int i = p_219232_0_.x - p_219232_1_;
      int j = p_219232_0_.z - p_219232_2_;
      return Math.max(Math.abs(i), Math.abs(j));
   }

   protected ServerWorldLightManager getLightEngine() {
      return this.lightEngine;
   }

   @Nullable
   protected ChunkHolder getUpdatingChunkIfPresent(long p_219220_1_) {
      return this.updatingChunkMap.get(p_219220_1_);
   }

   @Nullable
   protected ChunkHolder getVisibleChunkIfPresent(long p_219219_1_) {
      return this.visibleChunkMap.get(p_219219_1_);
   }

   protected IntSupplier getChunkQueueLevel(long p_219191_1_) {
      return () -> {
         ChunkHolder chunkholder = this.getVisibleChunkIfPresent(p_219191_1_);
         return chunkholder == null ? ChunkTaskPriorityQueue.PRIORITY_LEVEL_COUNT - 1 : Math.min(chunkholder.getQueueLevel(), ChunkTaskPriorityQueue.PRIORITY_LEVEL_COUNT - 1);
      };
   }

   @OnlyIn(Dist.CLIENT)
   public String getChunkDebugData(ChunkPos p_219170_1_) {
      ChunkHolder chunkholder = this.getVisibleChunkIfPresent(p_219170_1_.toLong());
      if (chunkholder == null) {
         return "null";
      } else {
         String s = chunkholder.getTicketLevel() + "\n";
         ChunkStatus chunkstatus = chunkholder.getLastAvailableStatus();
         IChunk ichunk = chunkholder.getLastAvailable();
         if (chunkstatus != null) {
            s = s + "St: \u00a7" + chunkstatus.getIndex() + chunkstatus + '\u00a7' + "r\n";
         }

         if (ichunk != null) {
            s = s + "Ch: \u00a7" + ichunk.getStatus().getIndex() + ichunk.getStatus() + '\u00a7' + "r\n";
         }

         ChunkHolder.LocationType chunkholder$locationtype = chunkholder.getFullStatus();
         s = s + "\u00a7" + chunkholder$locationtype.ordinal() + chunkholder$locationtype;
         return s + '\u00a7' + "r";
      }
   }

   private CompletableFuture<Either<List<IChunk>, ChunkHolder.IChunkLoadingError>> getChunkRangeFuture(ChunkPos p_219236_1_, int p_219236_2_, IntFunction<ChunkStatus> p_219236_3_) {
      List<CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> list = Lists.newArrayList();
      int i = p_219236_1_.x;
      int j = p_219236_1_.z;

      for(int k = -p_219236_2_; k <= p_219236_2_; ++k) {
         for(int l = -p_219236_2_; l <= p_219236_2_; ++l) {
            int i1 = Math.max(Math.abs(l), Math.abs(k));
            final ChunkPos chunkpos = new ChunkPos(i + l, j + k);
            long j1 = chunkpos.toLong();
            ChunkHolder chunkholder = this.getUpdatingChunkIfPresent(j1);
            if (chunkholder == null) {
               return CompletableFuture.completedFuture(Either.right(new ChunkHolder.IChunkLoadingError() {
                  public String toString() {
                     return "Unloaded " + chunkpos.toString();
                  }
               }));
            }

            ChunkStatus chunkstatus = p_219236_3_.apply(i1);
            CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = chunkholder.getOrScheduleFuture(chunkstatus, this);
            list.add(completablefuture);
         }
      }

      CompletableFuture<List<Either<IChunk, ChunkHolder.IChunkLoadingError>>> completablefuture1 = Util.sequence(list);
      return completablefuture1.thenApply((p_219227_4_) -> {
         List<IChunk> list1 = Lists.newArrayList();
         int k1 = 0;

         for(final Either<IChunk, ChunkHolder.IChunkLoadingError> either : p_219227_4_) {
            Optional<IChunk> optional = either.left();
            if (!optional.isPresent()) {
               final int l1 = k1;
               return Either.right(new ChunkHolder.IChunkLoadingError() {
                  public String toString() {
                     return "Unloaded " + new ChunkPos(i + l1 % (p_219236_2_ * 2 + 1), j + l1 / (p_219236_2_ * 2 + 1)) + " " + either.right().get().toString();
                  }
               });
            }

            list1.add(optional.get());
            ++k1;
         }

         return Either.left(list1);
      });
   }

   public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> getEntityTickingRangeFuture(ChunkPos p_219188_1_) {
      return this.getChunkRangeFuture(p_219188_1_, 2, (p_219218_0_) -> {
         return ChunkStatus.FULL;
      }).thenApplyAsync((p_219242_0_) -> {
         return p_219242_0_.mapLeft((p_219238_0_) -> {
            return (Chunk)p_219238_0_.get(p_219238_0_.size() / 2);
         });
      }, this.mainThreadExecutor);
   }

   @Nullable
   private ChunkHolder updateChunkScheduling(long p_219213_1_, int p_219213_3_, @Nullable ChunkHolder p_219213_4_, int p_219213_5_) {
      if (p_219213_5_ > MAX_CHUNK_DISTANCE && p_219213_3_ > MAX_CHUNK_DISTANCE) {
         return p_219213_4_;
      } else {
         if (p_219213_4_ != null) {
            p_219213_4_.setTicketLevel(p_219213_3_);
         }

         if (p_219213_4_ != null) {
            if (p_219213_3_ > MAX_CHUNK_DISTANCE) {
               this.toDrop.add(p_219213_1_);
            } else {
               this.toDrop.remove(p_219213_1_);
            }
         }

         if (p_219213_3_ <= MAX_CHUNK_DISTANCE && p_219213_4_ == null) {
            p_219213_4_ = this.pendingUnloads.remove(p_219213_1_);
            if (p_219213_4_ != null) {
               p_219213_4_.setTicketLevel(p_219213_3_);
            } else {
               p_219213_4_ = new ChunkHolder(new ChunkPos(p_219213_1_), p_219213_3_, this.lightEngine, this.queueSorter, this);
            }

            this.updatingChunkMap.put(p_219213_1_, p_219213_4_);
            this.modified = true;
         }

         return p_219213_4_;
      }
   }

   public void close() throws IOException {
      try {
         this.queueSorter.close();
         this.poiManager.close();
      } finally {
         super.close();
      }

   }

   protected void saveAllChunks(boolean p_219177_1_) {
      if (p_219177_1_) {
         List<ChunkHolder> list = this.visibleChunkMap.values().stream().filter(ChunkHolder::wasAccessibleSinceLastSave).peek(ChunkHolder::refreshAccessibility).collect(Collectors.toList());
         MutableBoolean mutableboolean = new MutableBoolean();

         do {
            mutableboolean.setFalse();
            list.stream().map((p_222974_1_) -> {
               CompletableFuture<IChunk> completablefuture;
               do {
                  completablefuture = p_222974_1_.getChunkToSave();
                  this.mainThreadExecutor.managedBlock(completablefuture::isDone);
               } while(completablefuture != p_222974_1_.getChunkToSave());

               return completablefuture.join();
            }).filter((p_222952_0_) -> {
               return p_222952_0_ instanceof ChunkPrimerWrapper || p_222952_0_ instanceof Chunk;
            }).filter(this::save).forEach((p_222959_1_) -> {
               mutableboolean.setTrue();
            });
         } while(mutableboolean.isTrue());

         this.processUnloads(() -> {
            return true;
         });
         this.flushWorker();
         LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", (Object)this.storageFolder.getName());
      } else {
         this.visibleChunkMap.values().stream().filter(ChunkHolder::wasAccessibleSinceLastSave).forEach((p_222965_1_) -> {
            IChunk ichunk = p_222965_1_.getChunkToSave().getNow((IChunk)null);
            if (ichunk instanceof ChunkPrimerWrapper || ichunk instanceof Chunk) {
               this.save(ichunk);
               p_222965_1_.refreshAccessibility();
            }

         });
      }

   }

   protected void tick(BooleanSupplier p_219204_1_) {
      IProfiler iprofiler = this.level.getProfiler();
      iprofiler.push("poi");
      this.poiManager.tick(p_219204_1_);
      iprofiler.popPush("chunk_unload");
      if (!this.level.noSave()) {
         this.processUnloads(p_219204_1_);
      }

      iprofiler.pop();
   }

   private void processUnloads(BooleanSupplier p_223155_1_) {
      LongIterator longiterator = this.toDrop.iterator();

      for(int i = 0; longiterator.hasNext() && (p_223155_1_.getAsBoolean() || i < 200 || this.toDrop.size() > 2000); longiterator.remove()) {
         long j = longiterator.nextLong();
         ChunkHolder chunkholder = this.updatingChunkMap.remove(j);
         if (chunkholder != null) {
            this.pendingUnloads.put(j, chunkholder);
            this.modified = true;
            ++i;
            this.scheduleUnload(j, chunkholder);
         }
      }

      Runnable runnable;
      while((p_223155_1_.getAsBoolean() || this.unloadQueue.size() > 2000) && (runnable = this.unloadQueue.poll()) != null) {
         runnable.run();
      }

   }

   private void scheduleUnload(long p_219212_1_, ChunkHolder p_219212_3_) {
      CompletableFuture<IChunk> completablefuture = p_219212_3_.getChunkToSave();
      completablefuture.thenAcceptAsync((p_219185_5_) -> {
         CompletableFuture<IChunk> completablefuture1 = p_219212_3_.getChunkToSave();
         if (completablefuture1 != completablefuture) {
            this.scheduleUnload(p_219212_1_, p_219212_3_);
         } else {
            if (this.pendingUnloads.remove(p_219212_1_, p_219212_3_) && p_219185_5_ != null) {
               if (p_219185_5_ instanceof Chunk) {
                  ((Chunk)p_219185_5_).setLoaded(false);
                  net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Unload((Chunk)p_219185_5_));
               }

               this.save(p_219185_5_);
               if (this.entitiesInLevel.remove(p_219212_1_) && p_219185_5_ instanceof Chunk) {
                  Chunk chunk = (Chunk)p_219185_5_;
                  this.level.unload(chunk);
               }

               this.lightEngine.updateChunkStatus(p_219185_5_.getPos());
               this.lightEngine.tryScheduleUpdate();
               this.progressListener.onStatusChange(p_219185_5_.getPos(), (ChunkStatus)null);
            }

         }
      }, this.unloadQueue::add).whenComplete((p_223171_1_, p_223171_2_) -> {
         if (p_223171_2_ != null) {
            LOGGER.error("Failed to save chunk " + p_219212_3_.getPos(), p_223171_2_);
         }

      });
   }

   protected boolean promoteChunkMap() {
      if (!this.modified) {
         return false;
      } else {
         this.visibleChunkMap = this.updatingChunkMap.clone();
         this.modified = false;
         return true;
      }
   }

   public CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> schedule(ChunkHolder p_219244_1_, ChunkStatus p_219244_2_) {
      ChunkPos chunkpos = p_219244_1_.getPos();
      if (p_219244_2_ == ChunkStatus.EMPTY) {
         return this.scheduleChunkLoad(chunkpos);
      } else {
         CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = p_219244_1_.getOrScheduleFuture(p_219244_2_.getParent(), this);
         return completablefuture.thenComposeAsync((p_223180_4_) -> {
            Optional<IChunk> optional = p_223180_4_.left();
            if (!optional.isPresent()) {
               return CompletableFuture.completedFuture(p_223180_4_);
            } else {
               if (p_219244_2_ == ChunkStatus.LIGHT) {
                  this.distanceManager.addTicket(TicketType.LIGHT, chunkpos, 33 + ChunkStatus.getDistance(ChunkStatus.FEATURES), chunkpos);
               }

               IChunk ichunk = optional.get();
               if (ichunk.getStatus().isOrAfter(p_219244_2_)) {
                  CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture1;
                  if (p_219244_2_ == ChunkStatus.LIGHT) {
                     completablefuture1 = this.scheduleChunkGeneration(p_219244_1_, p_219244_2_);
                  } else {
                     completablefuture1 = p_219244_2_.load(this.level, this.structureManager, this.lightEngine, (p_223175_2_) -> {
                        return this.protoChunkToFullChunk(p_219244_1_);
                     }, ichunk);
                  }

                  this.progressListener.onStatusChange(chunkpos, p_219244_2_);
                  return completablefuture1;
               } else {
                  return this.scheduleChunkGeneration(p_219244_1_, p_219244_2_);
               }
            }
         }, this.mainThreadExecutor);
      }
   }

   private CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> scheduleChunkLoad(ChunkPos p_223172_1_) {
      return CompletableFuture.supplyAsync(() -> {
         try {
            this.level.getProfiler().incrementCounter("chunkLoad");
            CompoundNBT compoundnbt = this.readChunk(p_223172_1_);
            if (compoundnbt != null) {
               boolean flag = compoundnbt.contains("Level", 10) && compoundnbt.getCompound("Level").contains("Status", 8);
               if (flag) {
                  IChunk ichunk = ChunkSerializer.read(this.level, this.structureManager, this.poiManager, p_223172_1_, compoundnbt);
                  ichunk.setLastSaveTime(this.level.getGameTime());
                  this.markPosition(p_223172_1_, ichunk.getStatus().getChunkType());
                  return Either.left(ichunk);
               }

               LOGGER.error("Chunk file at {} is missing level data, skipping", (Object)p_223172_1_);
            }
         } catch (ReportedException reportedexception) {
            Throwable throwable = reportedexception.getCause();
            if (!(throwable instanceof IOException)) {
               this.markPositionReplaceable(p_223172_1_);
               throw reportedexception;
            }

            LOGGER.error("Couldn't load chunk {}", p_223172_1_, throwable);
         } catch (Exception exception) {
            LOGGER.error("Couldn't load chunk {}", p_223172_1_, exception);
         }

         this.markPositionReplaceable(p_223172_1_);
         return Either.left(new ChunkPrimer(p_223172_1_, UpgradeData.EMPTY));
      }, this.mainThreadExecutor);
   }

   private void markPositionReplaceable(ChunkPos p_241089_1_) {
      this.chunkTypeCache.put(p_241089_1_.toLong(), (byte)-1);
   }

   private byte markPosition(ChunkPos p_241088_1_, ChunkStatus.Type p_241088_2_) {
      return this.chunkTypeCache.put(p_241088_1_.toLong(), (byte)(p_241088_2_ == ChunkStatus.Type.PROTOCHUNK ? -1 : 1));
   }

   private CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> scheduleChunkGeneration(ChunkHolder p_223156_1_, ChunkStatus p_223156_2_) {
      ChunkPos chunkpos = p_223156_1_.getPos();
      CompletableFuture<Either<List<IChunk>, ChunkHolder.IChunkLoadingError>> completablefuture = this.getChunkRangeFuture(chunkpos, p_223156_2_.getRange(), (p_219195_2_) -> {
         return this.getDependencyStatus(p_223156_2_, p_219195_2_);
      });
      this.level.getProfiler().incrementCounter(() -> {
         return "chunkGenerate " + p_223156_2_.getName();
      });
      return completablefuture.thenComposeAsync((p_219235_4_) -> {
         return p_219235_4_.map((p_223148_4_) -> {
            try {
               CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture1 = p_223156_2_.generate(this.level, this.generator, this.structureManager, this.lightEngine, (p_222954_2_) -> {
                  return this.protoChunkToFullChunk(p_223156_1_);
               }, p_223148_4_);
               this.progressListener.onStatusChange(chunkpos, p_223156_2_);
               return completablefuture1;
            } catch (Exception exception) {
               CrashReport crashreport = CrashReport.forThrowable(exception, "Exception generating new chunk");
               CrashReportCategory crashreportcategory = crashreport.addCategory("Chunk to be generated");
               crashreportcategory.setDetail("Location", String.format("%d,%d", chunkpos.x, chunkpos.z));
               crashreportcategory.setDetail("Position hash", ChunkPos.asLong(chunkpos.x, chunkpos.z));
               crashreportcategory.setDetail("Generator", this.generator);
               throw new ReportedException(crashreport);
            }
         }, (p_219211_2_) -> {
            this.releaseLightTicket(chunkpos);
            return CompletableFuture.completedFuture(Either.right(p_219211_2_));
         });
      }, (p_219216_2_) -> {
         this.worldgenMailbox.tell(ChunkTaskPriorityQueueSorter.message(p_223156_1_, p_219216_2_));
      });
   }

   protected void releaseLightTicket(ChunkPos p_219209_1_) {
      this.mainThreadExecutor.tell(Util.name(() -> {
         this.distanceManager.removeTicket(TicketType.LIGHT, p_219209_1_, 33 + ChunkStatus.getDistance(ChunkStatus.FEATURES), p_219209_1_);
      }, () -> {
         return "release light ticket " + p_219209_1_;
      }));
   }

   private ChunkStatus getDependencyStatus(ChunkStatus p_219205_1_, int p_219205_2_) {
      ChunkStatus chunkstatus;
      if (p_219205_2_ == 0) {
         chunkstatus = p_219205_1_.getParent();
      } else {
         chunkstatus = ChunkStatus.getStatus(ChunkStatus.getDistance(p_219205_1_) + p_219205_2_);
      }

      return chunkstatus;
   }

   private CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> protoChunkToFullChunk(ChunkHolder p_219200_1_) {
      CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = p_219200_1_.getFutureIfPresentUnchecked(ChunkStatus.FULL.getParent());
      return completablefuture.thenApplyAsync((p_219193_2_) -> {
         ChunkStatus chunkstatus = ChunkHolder.getStatus(p_219200_1_.getTicketLevel());
         return !chunkstatus.isOrAfter(ChunkStatus.FULL) ? ChunkHolder.UNLOADED_CHUNK : p_219193_2_.mapLeft((p_219237_2_) -> {
            ChunkPos chunkpos = p_219200_1_.getPos();
            Chunk chunk;
            if (p_219237_2_ instanceof ChunkPrimerWrapper) {
               chunk = ((ChunkPrimerWrapper)p_219237_2_).getWrapped();
            } else {
               chunk = new Chunk(this.level, (ChunkPrimer)p_219237_2_);
               p_219200_1_.replaceProtoChunk(new ChunkPrimerWrapper(chunk));
            }

            chunk.setFullStatus(() -> {
               return ChunkHolder.getFullChunkStatus(p_219200_1_.getTicketLevel());
            });
            chunk.runPostLoad();
            if (this.entitiesInLevel.add(chunkpos.toLong())) {
               chunk.setLoaded(true);
               this.level.addAllPendingBlockEntities(chunk.getBlockEntities().values());
               List<Entity> list = null;
               ClassInheritanceMultiMap<Entity>[] aclassinheritancemultimap = chunk.getEntitySections();
               int i = aclassinheritancemultimap.length;

               for(int j = 0; j < i; ++j) {
                  for(Entity entity : aclassinheritancemultimap[j]) {
                     if (!(entity instanceof PlayerEntity) && !this.level.loadFromChunk(entity)) {
                        if (list == null) {
                           list = Lists.newArrayList(entity);
                        } else {
                           list.add(entity);
                        }
                     }
                  }
               }

               if (list != null) {
                  list.forEach(chunk::removeEntity);
               }
               net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Load(chunk));
            }

            return chunk;
         });
      }, (p_219228_2_) -> {
         this.mainThreadMailbox.tell(ChunkTaskPriorityQueueSorter.message(p_219228_2_, p_219200_1_.getPos().toLong(), p_219200_1_::getTicketLevel));
      });
   }

   public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> postProcess(ChunkHolder p_219179_1_) {
      ChunkPos chunkpos = p_219179_1_.getPos();
      CompletableFuture<Either<List<IChunk>, ChunkHolder.IChunkLoadingError>> completablefuture = this.getChunkRangeFuture(chunkpos, 1, (p_219172_0_) -> {
         return ChunkStatus.FULL;
      });
      CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> completablefuture1 = completablefuture.thenApplyAsync((p_219239_0_) -> {
         return p_219239_0_.flatMap((p_219208_0_) -> {
            Chunk chunk = (Chunk)p_219208_0_.get(p_219208_0_.size() / 2);
            chunk.postProcessGeneration();
            return Either.left(chunk);
         });
      }, (p_219230_2_) -> {
         this.mainThreadMailbox.tell(ChunkTaskPriorityQueueSorter.message(p_219179_1_, p_219230_2_));
      });
      completablefuture1.thenAcceptAsync((p_219176_2_) -> {
         p_219176_2_.mapLeft((p_219196_2_) -> {
            this.tickingGenerated.getAndIncrement();
            IPacket<?>[] ipacket = new IPacket[2];
            this.getPlayers(chunkpos, false).forEach((p_219233_3_) -> {
               this.playerLoadedChunk(p_219233_3_, ipacket, p_219196_2_);
            });
            return Either.left(p_219196_2_);
         });
      }, (p_219202_2_) -> {
         this.mainThreadMailbox.tell(ChunkTaskPriorityQueueSorter.message(p_219179_1_, p_219202_2_));
      });
      return completablefuture1;
   }

   public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> unpackTicks(ChunkHolder p_222961_1_) {
      return p_222961_1_.getOrScheduleFuture(ChunkStatus.FULL, this).thenApplyAsync((p_222976_0_) -> {
         return p_222976_0_.mapLeft((p_222955_0_) -> {
            Chunk chunk = (Chunk)p_222955_0_;
            chunk.unpackTicks();
            return chunk;
         });
      }, (p_222962_2_) -> {
         this.mainThreadMailbox.tell(ChunkTaskPriorityQueueSorter.message(p_222961_1_, p_222962_2_));
      });
   }

   public int getTickingGenerated() {
      return this.tickingGenerated.get();
   }

   private boolean save(IChunk p_219229_1_) {
      this.poiManager.flush(p_219229_1_.getPos());
      if (!p_219229_1_.isUnsaved()) {
         return false;
      } else {
         p_219229_1_.setLastSaveTime(this.level.getGameTime());
         p_219229_1_.setUnsaved(false);
         ChunkPos chunkpos = p_219229_1_.getPos();

         try {
            ChunkStatus chunkstatus = p_219229_1_.getStatus();
            if (chunkstatus.getChunkType() != ChunkStatus.Type.LEVELCHUNK) {
               if (this.isExistingChunkFull(chunkpos)) {
                  return false;
               }

               if (chunkstatus == ChunkStatus.EMPTY && p_219229_1_.getAllStarts().values().stream().noneMatch(StructureStart::isValid)) {
                  return false;
               }
            }

            this.level.getProfiler().incrementCounter("chunkSave");
            CompoundNBT compoundnbt = ChunkSerializer.write(this.level, p_219229_1_);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkDataEvent.Save(p_219229_1_, p_219229_1_.getWorldForge() != null ? p_219229_1_.getWorldForge() : this.level, compoundnbt));
            this.write(chunkpos, compoundnbt);
            this.markPosition(chunkpos, chunkstatus.getChunkType());
            return true;
         } catch (Exception exception) {
            LOGGER.error("Failed to save chunk {},{}", chunkpos.x, chunkpos.z, exception);
            return false;
         }
      }
   }

   private boolean isExistingChunkFull(ChunkPos p_241090_1_) {
      byte b0 = this.chunkTypeCache.get(p_241090_1_.toLong());
      if (b0 != 0) {
         return b0 == 1;
      } else {
         CompoundNBT compoundnbt;
         try {
            compoundnbt = this.readChunk(p_241090_1_);
            if (compoundnbt == null) {
               this.markPositionReplaceable(p_241090_1_);
               return false;
            }
         } catch (Exception exception) {
            LOGGER.error("Failed to read chunk {}", p_241090_1_, exception);
            this.markPositionReplaceable(p_241090_1_);
            return false;
         }

         ChunkStatus.Type chunkstatus$type = ChunkSerializer.getChunkTypeFromTag(compoundnbt);
         return this.markPosition(p_241090_1_, chunkstatus$type) == 1;
      }
   }

   protected void setViewDistance(int p_219175_1_) {
      int i = MathHelper.clamp(p_219175_1_ + 1, 3, 33);
      if (i != this.viewDistance) {
         int j = this.viewDistance;
         this.viewDistance = i;
         this.distanceManager.updatePlayerTickets(this.viewDistance);

         for(ChunkHolder chunkholder : this.updatingChunkMap.values()) {
            ChunkPos chunkpos = chunkholder.getPos();
            IPacket<?>[] ipacket = new IPacket[2];
            this.getPlayers(chunkpos, false).forEach((p_219224_4_) -> {
               int k = checkerboardDistance(chunkpos, p_219224_4_, true);
               boolean flag = k <= j;
               boolean flag1 = k <= this.viewDistance;
               this.updateChunkTracking(p_219224_4_, chunkpos, ipacket, flag, flag1);
            });
         }
      }

   }

   protected void updateChunkTracking(ServerPlayerEntity p_219199_1_, ChunkPos p_219199_2_, IPacket<?>[] p_219199_3_, boolean p_219199_4_, boolean p_219199_5_) {
      if (p_219199_1_.level == this.level) {
         net.minecraftforge.event.ForgeEventFactory.fireChunkWatch(p_219199_4_, p_219199_5_, p_219199_1_, p_219199_2_, this.level);
         if (p_219199_5_ && !p_219199_4_) {
            ChunkHolder chunkholder = this.getVisibleChunkIfPresent(p_219199_2_.toLong());
            if (chunkholder != null) {
               Chunk chunk = chunkholder.getTickingChunk();
               if (chunk != null) {
                  this.playerLoadedChunk(p_219199_1_, p_219199_3_, chunk);
               }

               DebugPacketSender.sendPoiPacketsForChunk(this.level, p_219199_2_);
            }
         }

         if (!p_219199_5_ && p_219199_4_) {
            p_219199_1_.untrackChunk(p_219199_2_);
         }

      }
   }

   public int size() {
      return this.visibleChunkMap.size();
   }

   protected ChunkManager.ProxyTicketManager getDistanceManager() {
      return this.distanceManager;
   }

   protected Iterable<ChunkHolder> getChunks() {
      return Iterables.unmodifiableIterable(this.visibleChunkMap.values());
   }

   void dumpChunks(Writer p_225406_1_) throws IOException {
      CSVWriter csvwriter = CSVWriter.builder().addColumn("x").addColumn("z").addColumn("level").addColumn("in_memory").addColumn("status").addColumn("full_status").addColumn("accessible_ready").addColumn("ticking_ready").addColumn("entity_ticking_ready").addColumn("ticket").addColumn("spawning").addColumn("entity_count").addColumn("block_entity_count").build(p_225406_1_);

      for(Entry<ChunkHolder> entry : this.visibleChunkMap.long2ObjectEntrySet()) {
         ChunkPos chunkpos = new ChunkPos(entry.getLongKey());
         ChunkHolder chunkholder = entry.getValue();
         Optional<IChunk> optional = Optional.ofNullable(chunkholder.getLastAvailable());
         Optional<Chunk> optional1 = optional.flatMap((p_225407_0_) -> {
            return p_225407_0_ instanceof Chunk ? Optional.of((Chunk)p_225407_0_) : Optional.empty();
         });
         csvwriter.writeRow(chunkpos.x, chunkpos.z, chunkholder.getTicketLevel(), optional.isPresent(), optional.map(IChunk::getStatus).orElse((ChunkStatus)null), optional1.map(Chunk::getFullStatus).orElse((ChunkHolder.LocationType)null), printFuture(chunkholder.getFullChunkFuture()), printFuture(chunkholder.getTickingChunkFuture()), printFuture(chunkholder.getEntityTickingChunkFuture()), this.distanceManager.getTicketDebugString(entry.getLongKey()), !this.noPlayersCloseForSpawning(chunkpos), optional1.map((p_225401_0_) -> {
            return Stream.of(p_225401_0_.getEntitySections()).mapToInt(ClassInheritanceMultiMap::size).sum();
         }).orElse(0), optional1.map((p_225405_0_) -> {
            return p_225405_0_.getBlockEntities().size();
         }).orElse(0));
      }

   }

   private static String printFuture(CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> p_225402_0_) {
      try {
         Either<Chunk, ChunkHolder.IChunkLoadingError> either = p_225402_0_.getNow((Either<Chunk, ChunkHolder.IChunkLoadingError>)null);
         return either != null ? either.map((p_225408_0_) -> {
            return "done";
         }, (p_225400_0_) -> {
            return "unloaded";
         }) : "not completed";
      } catch (CompletionException completionexception) {
         return "failed " + completionexception.getCause().getMessage();
      } catch (CancellationException cancellationexception) {
         return "cancelled";
      }
   }

   @Nullable
   private CompoundNBT readChunk(ChunkPos p_219178_1_) throws IOException {
      CompoundNBT compoundnbt = this.read(p_219178_1_);
      return compoundnbt == null ? null : this.upgradeChunkTag(this.level.dimension(), this.overworldDataStorage, compoundnbt);
   }

   boolean noPlayersCloseForSpawning(ChunkPos p_219243_1_) {
      long i = p_219243_1_.toLong();
      return !this.distanceManager.hasPlayersNearby(i) ? true : this.playerMap.getPlayers(i).noneMatch((p_219201_1_) -> {
         return !p_219201_1_.isSpectator() && euclideanDistanceSquared(p_219243_1_, p_219201_1_) < 16384.0D;
      });
   }

   private boolean skipPlayer(ServerPlayerEntity p_219187_1_) {
      return p_219187_1_.isSpectator() && !this.level.getGameRules().getBoolean(GameRules.RULE_SPECTATORSGENERATECHUNKS);
   }

   void updatePlayerStatus(ServerPlayerEntity p_219234_1_, boolean p_219234_2_) {
      boolean flag = this.skipPlayer(p_219234_1_);
      boolean flag1 = this.playerMap.ignoredOrUnknown(p_219234_1_);
      int i = MathHelper.floor(p_219234_1_.getX()) >> 4;
      int j = MathHelper.floor(p_219234_1_.getZ()) >> 4;
      if (p_219234_2_) {
         this.playerMap.addPlayer(ChunkPos.asLong(i, j), p_219234_1_, flag);
         this.updatePlayerPos(p_219234_1_);
         if (!flag) {
            this.distanceManager.addPlayer(SectionPos.of(p_219234_1_), p_219234_1_);
         }
      } else {
         SectionPos sectionpos = p_219234_1_.getLastSectionPos();
         this.playerMap.removePlayer(sectionpos.chunk().toLong(), p_219234_1_);
         if (!flag1) {
            this.distanceManager.removePlayer(sectionpos, p_219234_1_);
         }
      }

      for(int l = i - this.viewDistance; l <= i + this.viewDistance; ++l) {
         for(int k = j - this.viewDistance; k <= j + this.viewDistance; ++k) {
            ChunkPos chunkpos = new ChunkPos(l, k);
            this.updateChunkTracking(p_219234_1_, chunkpos, new IPacket[2], !p_219234_2_, p_219234_2_);
         }
      }

   }

   private SectionPos updatePlayerPos(ServerPlayerEntity p_223489_1_) {
      SectionPos sectionpos = SectionPos.of(p_223489_1_);
      p_223489_1_.setLastSectionPos(sectionpos);
      p_223489_1_.connection.send(new SUpdateChunkPositionPacket(sectionpos.x(), sectionpos.z()));
      return sectionpos;
   }

   public void move(ServerPlayerEntity p_219183_1_) {
      for(ChunkManager.EntityTracker chunkmanager$entitytracker : this.entityMap.values()) {
         if (chunkmanager$entitytracker.entity == p_219183_1_) {
            chunkmanager$entitytracker.updatePlayers(this.level.players());
         } else {
            chunkmanager$entitytracker.updatePlayer(p_219183_1_);
         }
      }

      int l1 = MathHelper.floor(p_219183_1_.getX()) >> 4;
      int i2 = MathHelper.floor(p_219183_1_.getZ()) >> 4;
      SectionPos sectionpos = p_219183_1_.getLastSectionPos();
      SectionPos sectionpos1 = SectionPos.of(p_219183_1_);
      long i = sectionpos.chunk().toLong();
      long j = sectionpos1.chunk().toLong();
      boolean flag = this.playerMap.ignored(p_219183_1_);
      boolean flag1 = this.skipPlayer(p_219183_1_);
      boolean flag2 = sectionpos.asLong() != sectionpos1.asLong();
      if (flag2 || flag != flag1) {
         this.updatePlayerPos(p_219183_1_);
         if (!flag) {
            this.distanceManager.removePlayer(sectionpos, p_219183_1_);
         }

         if (!flag1) {
            this.distanceManager.addPlayer(sectionpos1, p_219183_1_);
         }

         if (!flag && flag1) {
            this.playerMap.ignorePlayer(p_219183_1_);
         }

         if (flag && !flag1) {
            this.playerMap.unIgnorePlayer(p_219183_1_);
         }

         if (i != j) {
            this.playerMap.updatePlayer(i, j, p_219183_1_);
         }
      }

      int k = sectionpos.x();
      int l = sectionpos.z();
      if (Math.abs(k - l1) <= this.viewDistance * 2 && Math.abs(l - i2) <= this.viewDistance * 2) {
         int k2 = Math.min(l1, k) - this.viewDistance;
         int i3 = Math.min(i2, l) - this.viewDistance;
         int j3 = Math.max(l1, k) + this.viewDistance;
         int k3 = Math.max(i2, l) + this.viewDistance;

         for(int l3 = k2; l3 <= j3; ++l3) {
            for(int k1 = i3; k1 <= k3; ++k1) {
               ChunkPos chunkpos1 = new ChunkPos(l3, k1);
               boolean flag5 = checkerboardDistance(chunkpos1, k, l) <= this.viewDistance;
               boolean flag6 = checkerboardDistance(chunkpos1, l1, i2) <= this.viewDistance;
               this.updateChunkTracking(p_219183_1_, chunkpos1, new IPacket[2], flag5, flag6);
            }
         }
      } else {
         for(int i1 = k - this.viewDistance; i1 <= k + this.viewDistance; ++i1) {
            for(int j1 = l - this.viewDistance; j1 <= l + this.viewDistance; ++j1) {
               ChunkPos chunkpos = new ChunkPos(i1, j1);
               boolean flag3 = true;
               boolean flag4 = false;
               this.updateChunkTracking(p_219183_1_, chunkpos, new IPacket[2], true, false);
            }
         }

         for(int j2 = l1 - this.viewDistance; j2 <= l1 + this.viewDistance; ++j2) {
            for(int l2 = i2 - this.viewDistance; l2 <= i2 + this.viewDistance; ++l2) {
               ChunkPos chunkpos2 = new ChunkPos(j2, l2);
               boolean flag7 = false;
               boolean flag8 = true;
               this.updateChunkTracking(p_219183_1_, chunkpos2, new IPacket[2], false, true);
            }
         }
      }

   }

   public Stream<ServerPlayerEntity> getPlayers(ChunkPos p_219097_1_, boolean p_219097_2_) {
      return this.playerMap.getPlayers(p_219097_1_.toLong()).filter((p_219192_3_) -> {
         int i = checkerboardDistance(p_219097_1_, p_219192_3_, true);
         if (i > this.viewDistance) {
            return false;
         } else {
            return !p_219097_2_ || i == this.viewDistance;
         }
      });
   }

   protected void addEntity(Entity p_219210_1_) {
      if (!(p_219210_1_ instanceof EnderDragonPartEntity)) {
         EntityType<?> entitytype = p_219210_1_.getType();
         int i = entitytype.clientTrackingRange() * 16;
         int j = entitytype.updateInterval();
         if (this.entityMap.containsKey(p_219210_1_.getId())) {
            throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Entity is already tracked!"));
         } else {
            ChunkManager.EntityTracker chunkmanager$entitytracker = new ChunkManager.EntityTracker(p_219210_1_, i, j, entitytype.trackDeltas());
            this.entityMap.put(p_219210_1_.getId(), chunkmanager$entitytracker);
            chunkmanager$entitytracker.updatePlayers(this.level.players());
            if (p_219210_1_ instanceof ServerPlayerEntity) {
               ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)p_219210_1_;
               this.updatePlayerStatus(serverplayerentity, true);

               for(ChunkManager.EntityTracker chunkmanager$entitytracker1 : this.entityMap.values()) {
                  if (chunkmanager$entitytracker1.entity != serverplayerentity) {
                     chunkmanager$entitytracker1.updatePlayer(serverplayerentity);
                  }
               }
            }

         }
      }
   }

   protected void removeEntity(Entity p_219231_1_) {
      if (p_219231_1_ instanceof ServerPlayerEntity) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)p_219231_1_;
         this.updatePlayerStatus(serverplayerentity, false);

         for(ChunkManager.EntityTracker chunkmanager$entitytracker : this.entityMap.values()) {
            chunkmanager$entitytracker.removePlayer(serverplayerentity);
         }
      }

      ChunkManager.EntityTracker chunkmanager$entitytracker1 = this.entityMap.remove(p_219231_1_.getId());
      if (chunkmanager$entitytracker1 != null) {
         chunkmanager$entitytracker1.broadcastRemoved();
      }

   }

   protected void tick() {
      List<ServerPlayerEntity> list = Lists.newArrayList();
      List<ServerPlayerEntity> list1 = this.level.players();

      for(ChunkManager.EntityTracker chunkmanager$entitytracker : this.entityMap.values()) {
         SectionPos sectionpos = chunkmanager$entitytracker.lastSectionPos;
         SectionPos sectionpos1 = SectionPos.of(chunkmanager$entitytracker.entity);
         if (!Objects.equals(sectionpos, sectionpos1)) {
            chunkmanager$entitytracker.updatePlayers(list1);
            Entity entity = chunkmanager$entitytracker.entity;
            if (entity instanceof ServerPlayerEntity) {
               list.add((ServerPlayerEntity)entity);
            }

            chunkmanager$entitytracker.lastSectionPos = sectionpos1;
         }

         chunkmanager$entitytracker.serverEntity.sendChanges();
      }

      if (!list.isEmpty()) {
         for(ChunkManager.EntityTracker chunkmanager$entitytracker1 : this.entityMap.values()) {
            chunkmanager$entitytracker1.updatePlayers(list);
         }
      }

   }

   protected void broadcast(Entity p_219222_1_, IPacket<?> p_219222_2_) {
      ChunkManager.EntityTracker chunkmanager$entitytracker = this.entityMap.get(p_219222_1_.getId());
      if (chunkmanager$entitytracker != null) {
         chunkmanager$entitytracker.broadcast(p_219222_2_);
      }

   }

   protected void broadcastAndSend(Entity p_219225_1_, IPacket<?> p_219225_2_) {
      ChunkManager.EntityTracker chunkmanager$entitytracker = this.entityMap.get(p_219225_1_.getId());
      if (chunkmanager$entitytracker != null) {
         chunkmanager$entitytracker.broadcastAndSend(p_219225_2_);
      }

   }

   private void playerLoadedChunk(ServerPlayerEntity p_219180_1_, IPacket<?>[] p_219180_2_, Chunk p_219180_3_) {
      if (p_219180_2_[0] == null) {
         p_219180_2_[0] = new SChunkDataPacket(p_219180_3_, 65535);
         p_219180_2_[1] = new SUpdateLightPacket(p_219180_3_.getPos(), this.lightEngine, true);
      }

      p_219180_1_.trackChunk(p_219180_3_.getPos(), p_219180_2_[0], p_219180_2_[1]);
      DebugPacketSender.sendPoiPacketsForChunk(this.level, p_219180_3_.getPos());
      List<Entity> list = Lists.newArrayList();
      List<Entity> list1 = Lists.newArrayList();

      for(ChunkManager.EntityTracker chunkmanager$entitytracker : this.entityMap.values()) {
         Entity entity = chunkmanager$entitytracker.entity;
         if (entity != p_219180_1_ && entity.xChunk == p_219180_3_.getPos().x && entity.zChunk == p_219180_3_.getPos().z) {
            chunkmanager$entitytracker.updatePlayer(p_219180_1_);
            if (entity instanceof MobEntity && ((MobEntity)entity).getLeashHolder() != null) {
               list.add(entity);
            }

            if (!entity.getPassengers().isEmpty()) {
               list1.add(entity);
            }
         }
      }

      if (!list.isEmpty()) {
         for(Entity entity1 : list) {
            p_219180_1_.connection.send(new SMountEntityPacket(entity1, ((MobEntity)entity1).getLeashHolder()));
         }
      }

      if (!list1.isEmpty()) {
         for(Entity entity2 : list1) {
            p_219180_1_.connection.send(new SSetPassengersPacket(entity2));
         }
      }

   }

   protected PointOfInterestManager getPoiManager() {
      return this.poiManager;
   }

   public CompletableFuture<Void> packTicks(Chunk p_222973_1_) {
      return this.mainThreadExecutor.submit(() -> {
         p_222973_1_.packTicks(this.level);
      });
   }

   class EntityTracker {
      private final TrackedEntity serverEntity;
      private final Entity entity;
      private final int range;
      private SectionPos lastSectionPos;
      private final Set<ServerPlayerEntity> seenBy = Sets.newHashSet();

      public EntityTracker(Entity p_i50468_2_, int p_i50468_3_, int p_i50468_4_, boolean p_i50468_5_) {
         this.serverEntity = new TrackedEntity(ChunkManager.this.level, p_i50468_2_, p_i50468_4_, p_i50468_5_, this::broadcast);
         this.entity = p_i50468_2_;
         this.range = p_i50468_3_;
         this.lastSectionPos = SectionPos.of(p_i50468_2_);
      }

      public boolean equals(Object p_equals_1_) {
         if (p_equals_1_ instanceof ChunkManager.EntityTracker) {
            return ((ChunkManager.EntityTracker)p_equals_1_).entity.getId() == this.entity.getId();
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.entity.getId();
      }

      public void broadcast(IPacket<?> p_219391_1_) {
         for(ServerPlayerEntity serverplayerentity : this.seenBy) {
            serverplayerentity.connection.send(p_219391_1_);
         }

      }

      public void broadcastAndSend(IPacket<?> p_219392_1_) {
         this.broadcast(p_219392_1_);
         if (this.entity instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity)this.entity).connection.send(p_219392_1_);
         }

      }

      public void broadcastRemoved() {
         for(ServerPlayerEntity serverplayerentity : this.seenBy) {
            this.serverEntity.removePairing(serverplayerentity);
         }

      }

      public void removePlayer(ServerPlayerEntity p_219399_1_) {
         if (this.seenBy.remove(p_219399_1_)) {
            this.serverEntity.removePairing(p_219399_1_);
         }

      }

      public void updatePlayer(ServerPlayerEntity p_219400_1_) {
         if (p_219400_1_ != this.entity) {
            Vector3d vector3d = p_219400_1_.position().subtract(this.serverEntity.sentPos());
            int i = Math.min(this.getEffectiveRange(), (ChunkManager.this.viewDistance - 1) * 16);
            boolean flag = vector3d.x >= (double)(-i) && vector3d.x <= (double)i && vector3d.z >= (double)(-i) && vector3d.z <= (double)i && this.entity.broadcastToPlayer(p_219400_1_);
            if (flag) {
               boolean flag1 = this.entity.forcedLoading;
               if (!flag1) {
                  ChunkPos chunkpos = new ChunkPos(this.entity.xChunk, this.entity.zChunk);
                  ChunkHolder chunkholder = ChunkManager.this.getVisibleChunkIfPresent(chunkpos.toLong());
                  if (chunkholder != null && chunkholder.getTickingChunk() != null) {
                     flag1 = ChunkManager.checkerboardDistance(chunkpos, p_219400_1_, false) <= ChunkManager.this.viewDistance;
                  }
               }

               if (flag1 && this.seenBy.add(p_219400_1_)) {
                  this.serverEntity.addPairing(p_219400_1_);
               }
            } else if (this.seenBy.remove(p_219400_1_)) {
               this.serverEntity.removePairing(p_219400_1_);
            }

         }
      }

      private int scaledRange(int p_241091_1_) {
         return ChunkManager.this.level.getServer().getScaledTrackingDistance(p_241091_1_);
      }

      private int getEffectiveRange() {
         Collection<Entity> collection = this.entity.getIndirectPassengers();
         int i = this.range;

         for(Entity entity : collection) {
            int j = entity.getType().clientTrackingRange() * 16;
            if (j > i) {
               i = j;
            }
         }

         return this.scaledRange(i);
      }

      public void updatePlayers(List<ServerPlayerEntity> p_219397_1_) {
         for(ServerPlayerEntity serverplayerentity : p_219397_1_) {
            this.updatePlayer(serverplayerentity);
         }

      }
   }

   class ProxyTicketManager extends TicketManager {
      protected ProxyTicketManager(Executor p_i50469_2_, Executor p_i50469_3_) {
         super(p_i50469_2_, p_i50469_3_);
      }

      protected boolean isChunkToRemove(long p_219371_1_) {
         return ChunkManager.this.toDrop.contains(p_219371_1_);
      }

      @Nullable
      protected ChunkHolder getChunk(long p_219335_1_) {
         return ChunkManager.this.getUpdatingChunkIfPresent(p_219335_1_);
      }

      @Nullable
      protected ChunkHolder updateChunkScheduling(long p_219372_1_, int p_219372_3_, @Nullable ChunkHolder p_219372_4_, int p_219372_5_) {
         return ChunkManager.this.updateChunkScheduling(p_219372_1_, p_219372_3_, p_219372_4_, p_219372_5_);
      }
   }
}
