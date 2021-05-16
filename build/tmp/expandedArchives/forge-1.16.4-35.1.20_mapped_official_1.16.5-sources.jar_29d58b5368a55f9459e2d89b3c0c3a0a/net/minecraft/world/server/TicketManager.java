package net.minecraft.world.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntMaps;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SortedArraySet;
import net.minecraft.util.concurrent.ITaskExecutor;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkDistanceGraph;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ChunkTaskPriorityQueueSorter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TicketManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int PLAYER_TICKET_LEVEL = 33 + ChunkStatus.getDistance(ChunkStatus.FULL) - 2;
   private final Long2ObjectMap<ObjectSet<ServerPlayerEntity>> playersPerChunk = new Long2ObjectOpenHashMap<>();
   private final Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> tickets = new Long2ObjectOpenHashMap<>();
   private final TicketManager.ChunkTicketTracker ticketTracker = new TicketManager.ChunkTicketTracker();
   private final TicketManager.PlayerChunkTracker naturalSpawnChunkCounter = new TicketManager.PlayerChunkTracker(8);
   private final TicketManager.PlayerTicketTracker playerTicketManager = new TicketManager.PlayerTicketTracker(33);
   private final Set<ChunkHolder> chunksToUpdateFutures = Sets.newHashSet();
   private final ChunkTaskPriorityQueueSorter ticketThrottler;
   private final ITaskExecutor<ChunkTaskPriorityQueueSorter.FunctionEntry<Runnable>> ticketThrottlerInput;
   private final ITaskExecutor<ChunkTaskPriorityQueueSorter.RunnableEntry> ticketThrottlerReleaser;
   private final LongSet ticketsToRelease = new LongOpenHashSet();
   private final Executor mainThreadExecutor;
   private long ticketTickCounter;

   protected TicketManager(Executor p_i50707_1_, Executor p_i50707_2_) {
      ITaskExecutor<Runnable> itaskexecutor = ITaskExecutor.of("player ticket throttler", p_i50707_2_::execute);
      ChunkTaskPriorityQueueSorter chunktaskpriorityqueuesorter = new ChunkTaskPriorityQueueSorter(ImmutableList.of(itaskexecutor), p_i50707_1_, 4);
      this.ticketThrottler = chunktaskpriorityqueuesorter;
      this.ticketThrottlerInput = chunktaskpriorityqueuesorter.getProcessor(itaskexecutor, true);
      this.ticketThrottlerReleaser = chunktaskpriorityqueuesorter.getReleaseProcessor(itaskexecutor);
      this.mainThreadExecutor = p_i50707_2_;
   }

   protected void purgeStaleTickets() {
      ++this.ticketTickCounter;
      ObjectIterator<Entry<SortedArraySet<Ticket<?>>>> objectiterator = this.tickets.long2ObjectEntrySet().fastIterator();

      while(objectiterator.hasNext()) {
         Entry<SortedArraySet<Ticket<?>>> entry = objectiterator.next();
         if (entry.getValue().removeIf((p_219370_1_) -> {
            return p_219370_1_.timedOut(this.ticketTickCounter);
         })) {
            this.ticketTracker.update(entry.getLongKey(), getTicketLevelAt(entry.getValue()), false);
         }

         if (entry.getValue().isEmpty()) {
            objectiterator.remove();
         }
      }

   }

   private static int getTicketLevelAt(SortedArraySet<Ticket<?>> p_229844_0_) {
      return !p_229844_0_.isEmpty() ? p_229844_0_.first().getTicketLevel() : ChunkManager.MAX_CHUNK_DISTANCE + 1;
   }

   protected abstract boolean isChunkToRemove(long p_219371_1_);

   @Nullable
   protected abstract ChunkHolder getChunk(long p_219335_1_);

   @Nullable
   protected abstract ChunkHolder updateChunkScheduling(long p_219372_1_, int p_219372_3_, @Nullable ChunkHolder p_219372_4_, int p_219372_5_);

   public boolean runAllUpdates(ChunkManager p_219353_1_) {
      this.naturalSpawnChunkCounter.runAllUpdates();
      this.playerTicketManager.runAllUpdates();
      int i = Integer.MAX_VALUE - this.ticketTracker.runDistanceUpdates(Integer.MAX_VALUE);
      boolean flag = i != 0;
      if (flag) {
      }

      if (!this.chunksToUpdateFutures.isEmpty()) {
         this.chunksToUpdateFutures.forEach((p_219343_1_) -> {
            p_219343_1_.updateFutures(p_219353_1_);
         });
         this.chunksToUpdateFutures.clear();
         return true;
      } else {
         if (!this.ticketsToRelease.isEmpty()) {
            LongIterator longiterator = this.ticketsToRelease.iterator();

            while(longiterator.hasNext()) {
               long j = longiterator.nextLong();
               if (this.getTickets(j).stream().anyMatch((p_219369_0_) -> {
                  return p_219369_0_.getType() == TicketType.PLAYER;
               })) {
                  ChunkHolder chunkholder = p_219353_1_.getUpdatingChunkIfPresent(j);
                  if (chunkholder == null) {
                     throw new IllegalStateException();
                  }

                  CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> completablefuture = chunkholder.getEntityTickingChunkFuture();
                  completablefuture.thenAccept((p_219363_3_) -> {
                     this.mainThreadExecutor.execute(() -> {
                        this.ticketThrottlerReleaser.tell(ChunkTaskPriorityQueueSorter.release(() -> {
                        }, j, false));
                     });
                  });
               }
            }

            this.ticketsToRelease.clear();
         }

         return flag;
      }
   }

   private void addTicket(long p_219347_1_, Ticket<?> p_219347_3_) {
      SortedArraySet<Ticket<?>> sortedarrayset = this.getTickets(p_219347_1_);
      int i = getTicketLevelAt(sortedarrayset);
      Ticket<?> ticket = sortedarrayset.addOrGet(p_219347_3_);
      ticket.setCreatedTick(this.ticketTickCounter);
      if (p_219347_3_.getTicketLevel() < i) {
         this.ticketTracker.update(p_219347_1_, p_219347_3_.getTicketLevel(), true);
      }

   }

   private void removeTicket(long p_219349_1_, Ticket<?> p_219349_3_) {
      SortedArraySet<Ticket<?>> sortedarrayset = this.getTickets(p_219349_1_);
      if (sortedarrayset.remove(p_219349_3_)) {
      }

      if (sortedarrayset.isEmpty()) {
         this.tickets.remove(p_219349_1_);
      }

      this.ticketTracker.update(p_219349_1_, getTicketLevelAt(sortedarrayset), false);
   }

   public <T> void addTicket(TicketType<T> p_219356_1_, ChunkPos p_219356_2_, int p_219356_3_, T p_219356_4_) {
      this.addTicket(p_219356_2_.toLong(), new Ticket<>(p_219356_1_, p_219356_3_, p_219356_4_));
   }

   public <T> void removeTicket(TicketType<T> p_219345_1_, ChunkPos p_219345_2_, int p_219345_3_, T p_219345_4_) {
      Ticket<T> ticket = new Ticket<>(p_219345_1_, p_219345_3_, p_219345_4_);
      this.removeTicket(p_219345_2_.toLong(), ticket);
   }

   public <T> void addRegionTicket(TicketType<T> p_219331_1_, ChunkPos p_219331_2_, int p_219331_3_, T p_219331_4_) {
      this.addTicket(p_219331_2_.toLong(), new Ticket<>(p_219331_1_, 33 - p_219331_3_, p_219331_4_));
   }

   public <T> void removeRegionTicket(TicketType<T> p_219362_1_, ChunkPos p_219362_2_, int p_219362_3_, T p_219362_4_) {
      Ticket<T> ticket = new Ticket<>(p_219362_1_, 33 - p_219362_3_, p_219362_4_);
      this.removeTicket(p_219362_2_.toLong(), ticket);
   }

   private SortedArraySet<Ticket<?>> getTickets(long p_229848_1_) {
      return this.tickets.computeIfAbsent(p_229848_1_, (p_229851_0_) -> {
         return SortedArraySet.create(4);
      });
   }

   protected void updateChunkForced(ChunkPos p_219364_1_, boolean p_219364_2_) {
      Ticket<ChunkPos> ticket = new Ticket<>(TicketType.FORCED, 31, p_219364_1_);
      if (p_219364_2_) {
         this.addTicket(p_219364_1_.toLong(), ticket);
      } else {
         this.removeTicket(p_219364_1_.toLong(), ticket);
      }

   }

   public void addPlayer(SectionPos p_219341_1_, ServerPlayerEntity p_219341_2_) {
      long i = p_219341_1_.chunk().toLong();
      this.playersPerChunk.computeIfAbsent(i, (p_219361_0_) -> {
         return new ObjectOpenHashSet();
      }).add(p_219341_2_);
      this.naturalSpawnChunkCounter.update(i, 0, true);
      this.playerTicketManager.update(i, 0, true);
   }

   public void removePlayer(SectionPos p_219367_1_, ServerPlayerEntity p_219367_2_) {
      long i = p_219367_1_.chunk().toLong();
      ObjectSet<ServerPlayerEntity> objectset = this.playersPerChunk.get(i);
      objectset.remove(p_219367_2_);
      if (objectset.isEmpty()) {
         this.playersPerChunk.remove(i);
         this.naturalSpawnChunkCounter.update(i, Integer.MAX_VALUE, false);
         this.playerTicketManager.update(i, Integer.MAX_VALUE, false);
      }

   }

   protected String getTicketDebugString(long p_225413_1_) {
      SortedArraySet<Ticket<?>> sortedarrayset = this.tickets.get(p_225413_1_);
      String s;
      if (sortedarrayset != null && !sortedarrayset.isEmpty()) {
         s = sortedarrayset.first().toString();
      } else {
         s = "no_ticket";
      }

      return s;
   }

   protected void updatePlayerTickets(int p_219354_1_) {
      this.playerTicketManager.updateViewDistance(p_219354_1_);
   }

   public int getNaturalSpawnChunkCount() {
      this.naturalSpawnChunkCounter.runAllUpdates();
      return this.naturalSpawnChunkCounter.chunks.size();
   }

   public boolean hasPlayersNearby(long p_223494_1_) {
      this.naturalSpawnChunkCounter.runAllUpdates();
      return this.naturalSpawnChunkCounter.chunks.containsKey(p_223494_1_);
   }

   public String getDebugStatus() {
      return this.ticketThrottler.getDebugStatus();
   }

   class ChunkTicketTracker extends ChunkDistanceGraph {
      public ChunkTicketTracker() {
         super(ChunkManager.MAX_CHUNK_DISTANCE + 2, 16, 256);
      }

      protected int getLevelFromSource(long p_215492_1_) {
         SortedArraySet<Ticket<?>> sortedarrayset = TicketManager.this.tickets.get(p_215492_1_);
         if (sortedarrayset == null) {
            return Integer.MAX_VALUE;
         } else {
            return sortedarrayset.isEmpty() ? Integer.MAX_VALUE : sortedarrayset.first().getTicketLevel();
         }
      }

      protected int getLevel(long p_215471_1_) {
         if (!TicketManager.this.isChunkToRemove(p_215471_1_)) {
            ChunkHolder chunkholder = TicketManager.this.getChunk(p_215471_1_);
            if (chunkholder != null) {
               return chunkholder.getTicketLevel();
            }
         }

         return ChunkManager.MAX_CHUNK_DISTANCE + 1;
      }

      protected void setLevel(long p_215476_1_, int p_215476_3_) {
         ChunkHolder chunkholder = TicketManager.this.getChunk(p_215476_1_);
         int i = chunkholder == null ? ChunkManager.MAX_CHUNK_DISTANCE + 1 : chunkholder.getTicketLevel();
         if (i != p_215476_3_) {
            chunkholder = TicketManager.this.updateChunkScheduling(p_215476_1_, p_215476_3_, chunkholder, i);
            if (chunkholder != null) {
               TicketManager.this.chunksToUpdateFutures.add(chunkholder);
            }

         }
      }

      public int runDistanceUpdates(int p_215493_1_) {
         return this.runUpdates(p_215493_1_);
      }
   }

   class PlayerChunkTracker extends ChunkDistanceGraph {
      protected final Long2ByteMap chunks = new Long2ByteOpenHashMap();
      protected final int maxDistance;

      protected PlayerChunkTracker(int p_i50684_2_) {
         super(p_i50684_2_ + 2, 16, 256);
         this.maxDistance = p_i50684_2_;
         this.chunks.defaultReturnValue((byte)(p_i50684_2_ + 2));
      }

      protected int getLevel(long p_215471_1_) {
         return this.chunks.get(p_215471_1_);
      }

      protected void setLevel(long p_215476_1_, int p_215476_3_) {
         byte b0;
         if (p_215476_3_ > this.maxDistance) {
            b0 = this.chunks.remove(p_215476_1_);
         } else {
            b0 = this.chunks.put(p_215476_1_, (byte)p_215476_3_);
         }

         this.onLevelChange(p_215476_1_, b0, p_215476_3_);
      }

      protected void onLevelChange(long p_215495_1_, int p_215495_3_, int p_215495_4_) {
      }

      protected int getLevelFromSource(long p_215492_1_) {
         return this.havePlayer(p_215492_1_) ? 0 : Integer.MAX_VALUE;
      }

      private boolean havePlayer(long p_215496_1_) {
         ObjectSet<ServerPlayerEntity> objectset = TicketManager.this.playersPerChunk.get(p_215496_1_);
         return objectset != null && !objectset.isEmpty();
      }

      public void runAllUpdates() {
         this.runUpdates(Integer.MAX_VALUE);
      }
   }

   class PlayerTicketTracker extends TicketManager.PlayerChunkTracker {
      private int viewDistance;
      private final Long2IntMap queueLevels = Long2IntMaps.synchronize(new Long2IntOpenHashMap());
      private final LongSet toUpdate = new LongOpenHashSet();

      protected PlayerTicketTracker(int p_i50682_2_) {
         super(p_i50682_2_);
         this.viewDistance = 0;
         this.queueLevels.defaultReturnValue(p_i50682_2_ + 2);
      }

      protected void onLevelChange(long p_215495_1_, int p_215495_3_, int p_215495_4_) {
         this.toUpdate.add(p_215495_1_);
      }

      public void updateViewDistance(int p_215508_1_) {
         for(it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry entry : this.chunks.long2ByteEntrySet()) {
            byte b0 = entry.getByteValue();
            long i = entry.getLongKey();
            this.onLevelChange(i, b0, this.haveTicketFor(b0), b0 <= p_215508_1_ - 2);
         }

         this.viewDistance = p_215508_1_;
      }

      private void onLevelChange(long p_215504_1_, int p_215504_3_, boolean p_215504_4_, boolean p_215504_5_) {
         if (p_215504_4_ != p_215504_5_) {
            Ticket<?> ticket = new Ticket<>(TicketType.PLAYER, TicketManager.PLAYER_TICKET_LEVEL, new ChunkPos(p_215504_1_));
            if (p_215504_5_) {
               TicketManager.this.ticketThrottlerInput.tell(ChunkTaskPriorityQueueSorter.message(() -> {
                  TicketManager.this.mainThreadExecutor.execute(() -> {
                     if (this.haveTicketFor(this.getLevel(p_215504_1_))) {
                        TicketManager.this.addTicket(p_215504_1_, ticket);
                        TicketManager.this.ticketsToRelease.add(p_215504_1_);
                     } else {
                        TicketManager.this.ticketThrottlerReleaser.tell(ChunkTaskPriorityQueueSorter.release(() -> {
                        }, p_215504_1_, false));
                     }

                  });
               }, p_215504_1_, () -> {
                  return p_215504_3_;
               }));
            } else {
               TicketManager.this.ticketThrottlerReleaser.tell(ChunkTaskPriorityQueueSorter.release(() -> {
                  TicketManager.this.mainThreadExecutor.execute(() -> {
                     TicketManager.this.removeTicket(p_215504_1_, ticket);
                  });
               }, p_215504_1_, true));
            }
         }

      }

      public void runAllUpdates() {
         super.runAllUpdates();
         if (!this.toUpdate.isEmpty()) {
            LongIterator longiterator = this.toUpdate.iterator();

            while(longiterator.hasNext()) {
               long i = longiterator.nextLong();
               int j = this.queueLevels.get(i);
               int k = this.getLevel(i);
               if (j != k) {
                  TicketManager.this.ticketThrottler.onLevelChange(new ChunkPos(i), () -> {
                     return this.queueLevels.get(i);
                  }, k, (p_215506_3_) -> {
                     if (p_215506_3_ >= this.queueLevels.defaultReturnValue()) {
                        this.queueLevels.remove(i);
                     } else {
                        this.queueLevels.put(i, p_215506_3_);
                     }

                  });
                  this.onLevelChange(i, k, this.haveTicketFor(j), this.haveTicketFor(k));
               }
            }

            this.toUpdate.clear();
         }

      }

      private boolean haveTicketFor(int p_215505_1_) {
         return p_215505_1_ <= this.viewDistance - 2;
      }
   }
}
