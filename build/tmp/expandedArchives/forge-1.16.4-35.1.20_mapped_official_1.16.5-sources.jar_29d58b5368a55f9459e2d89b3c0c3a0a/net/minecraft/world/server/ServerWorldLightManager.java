package net.minecraft.world.server;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.DelegatedTaskExecutor;
import net.minecraft.util.concurrent.ITaskExecutor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkTaskPriorityQueueSorter;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.lighting.WorldLightManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerWorldLightManager extends WorldLightManager implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final DelegatedTaskExecutor<Runnable> taskMailbox;
   private final ObjectList<Pair<ServerWorldLightManager.Phase, Runnable>> lightTasks = new ObjectArrayList<>();
   private final ChunkManager chunkMap;
   private final ITaskExecutor<ChunkTaskPriorityQueueSorter.FunctionEntry<Runnable>> sorterMailbox;
   private volatile int taskPerBatch = 5;
   private final AtomicBoolean scheduled = new AtomicBoolean();

   public ServerWorldLightManager(IChunkLightProvider p_i50701_1_, ChunkManager p_i50701_2_, boolean p_i50701_3_, DelegatedTaskExecutor<Runnable> p_i50701_4_, ITaskExecutor<ChunkTaskPriorityQueueSorter.FunctionEntry<Runnable>> p_i50701_5_) {
      super(p_i50701_1_, true, p_i50701_3_);
      this.chunkMap = p_i50701_2_;
      this.sorterMailbox = p_i50701_5_;
      this.taskMailbox = p_i50701_4_;
   }

   public void close() {
   }

   public int runUpdates(int p_215575_1_, boolean p_215575_2_, boolean p_215575_3_) {
      throw (UnsupportedOperationException)Util.pauseInIde(new UnsupportedOperationException("Ran authomatically on a different thread!"));
   }

   public void onBlockEmissionIncrease(BlockPos p_215573_1_, int p_215573_2_) {
      throw (UnsupportedOperationException)Util.pauseInIde(new UnsupportedOperationException("Ran authomatically on a different thread!"));
   }

   public void checkBlock(BlockPos p_215568_1_) {
      BlockPos blockpos = p_215568_1_.immutable();
      this.addTask(p_215568_1_.getX() >> 4, p_215568_1_.getZ() >> 4, ServerWorldLightManager.Phase.POST_UPDATE, Util.name(() -> {
         super.checkBlock(blockpos);
      }, () -> {
         return "checkBlock " + blockpos;
      }));
   }

   protected void updateChunkStatus(ChunkPos p_215581_1_) {
      this.addTask(p_215581_1_.x, p_215581_1_.z, () -> {
         return 0;
      }, ServerWorldLightManager.Phase.PRE_UPDATE, Util.name(() -> {
         super.retainData(p_215581_1_, false);
         super.enableLightSources(p_215581_1_, false);

         for(int i = -1; i < 17; ++i) {
            super.queueSectionData(LightType.BLOCK, SectionPos.of(p_215581_1_, i), (NibbleArray)null, true);
            super.queueSectionData(LightType.SKY, SectionPos.of(p_215581_1_, i), (NibbleArray)null, true);
         }

         for(int j = 0; j < 16; ++j) {
            super.updateSectionStatus(SectionPos.of(p_215581_1_, j), true);
         }

      }, () -> {
         return "updateChunkStatus " + p_215581_1_ + " " + true;
      }));
   }

   public void updateSectionStatus(SectionPos p_215566_1_, boolean p_215566_2_) {
      this.addTask(p_215566_1_.x(), p_215566_1_.z(), () -> {
         return 0;
      }, ServerWorldLightManager.Phase.PRE_UPDATE, Util.name(() -> {
         super.updateSectionStatus(p_215566_1_, p_215566_2_);
      }, () -> {
         return "updateSectionStatus " + p_215566_1_ + " " + p_215566_2_;
      }));
   }

   public void enableLightSources(ChunkPos p_215571_1_, boolean p_215571_2_) {
      this.addTask(p_215571_1_.x, p_215571_1_.z, ServerWorldLightManager.Phase.PRE_UPDATE, Util.name(() -> {
         super.enableLightSources(p_215571_1_, p_215571_2_);
      }, () -> {
         return "enableLight " + p_215571_1_ + " " + p_215571_2_;
      }));
   }

   public void queueSectionData(LightType p_215574_1_, SectionPos p_215574_2_, @Nullable NibbleArray p_215574_3_, boolean p_215574_4_) {
      this.addTask(p_215574_2_.x(), p_215574_2_.z(), () -> {
         return 0;
      }, ServerWorldLightManager.Phase.PRE_UPDATE, Util.name(() -> {
         super.queueSectionData(p_215574_1_, p_215574_2_, p_215574_3_, p_215574_4_);
      }, () -> {
         return "queueData " + p_215574_2_;
      }));
   }

   private void addTask(int p_215586_1_, int p_215586_2_, ServerWorldLightManager.Phase p_215586_3_, Runnable p_215586_4_) {
      this.addTask(p_215586_1_, p_215586_2_, this.chunkMap.getChunkQueueLevel(ChunkPos.asLong(p_215586_1_, p_215586_2_)), p_215586_3_, p_215586_4_);
   }

   private void addTask(int p_215600_1_, int p_215600_2_, IntSupplier p_215600_3_, ServerWorldLightManager.Phase p_215600_4_, Runnable p_215600_5_) {
      this.sorterMailbox.tell(ChunkTaskPriorityQueueSorter.message(() -> {
         this.lightTasks.add(Pair.of(p_215600_4_, p_215600_5_));
         if (this.lightTasks.size() >= this.taskPerBatch) {
            this.runUpdate();
         }

      }, ChunkPos.asLong(p_215600_1_, p_215600_2_), p_215600_3_));
   }

   public void retainData(ChunkPos p_223115_1_, boolean p_223115_2_) {
      this.addTask(p_223115_1_.x, p_223115_1_.z, () -> {
         return 0;
      }, ServerWorldLightManager.Phase.PRE_UPDATE, Util.name(() -> {
         super.retainData(p_223115_1_, p_223115_2_);
      }, () -> {
         return "retainData " + p_223115_1_;
      }));
   }

   public CompletableFuture<IChunk> lightChunk(IChunk p_215593_1_, boolean p_215593_2_) {
      ChunkPos chunkpos = p_215593_1_.getPos();
      p_215593_1_.setLightCorrect(false);
      this.addTask(chunkpos.x, chunkpos.z, ServerWorldLightManager.Phase.PRE_UPDATE, Util.name(() -> {
         ChunkSection[] achunksection = p_215593_1_.getSections();

         for(int i = 0; i < 16; ++i) {
            ChunkSection chunksection = achunksection[i];
            if (!ChunkSection.isEmpty(chunksection)) {
               super.updateSectionStatus(SectionPos.of(chunkpos, i), false);
            }
         }

         super.enableLightSources(chunkpos, true);
         if (!p_215593_2_) {
            p_215593_1_.getLights().forEach((p_215579_2_) -> {
               super.onBlockEmissionIncrease(p_215579_2_, p_215593_1_.getLightEmission(p_215579_2_));
            });
         }

         this.chunkMap.releaseLightTicket(chunkpos);
      }, () -> {
         return "lightChunk " + chunkpos + " " + p_215593_2_;
      }));
      return CompletableFuture.supplyAsync(() -> {
         p_215593_1_.setLightCorrect(true);
         super.retainData(chunkpos, false);
         return p_215593_1_;
      }, (p_215597_2_) -> {
         this.addTask(chunkpos.x, chunkpos.z, ServerWorldLightManager.Phase.POST_UPDATE, p_215597_2_);
      });
   }

   public void tryScheduleUpdate() {
      if ((!this.lightTasks.isEmpty() || super.hasLightWork()) && this.scheduled.compareAndSet(false, true)) {
         this.taskMailbox.tell(() -> {
            this.runUpdate();
            this.scheduled.set(false);
         });
      }

   }

   private void runUpdate() {
      int i = Math.min(this.lightTasks.size(), this.taskPerBatch);
      ObjectListIterator<Pair<ServerWorldLightManager.Phase, Runnable>> objectlistiterator = this.lightTasks.iterator();

      int j;
      for(j = 0; objectlistiterator.hasNext() && j < i; ++j) {
         Pair<ServerWorldLightManager.Phase, Runnable> pair = objectlistiterator.next();
         if (pair.getFirst() == ServerWorldLightManager.Phase.PRE_UPDATE) {
            pair.getSecond().run();
         }
      }

      objectlistiterator.back(j);
      super.runUpdates(Integer.MAX_VALUE, true, true);

      for(int k = 0; objectlistiterator.hasNext() && k < i; ++k) {
         Pair<ServerWorldLightManager.Phase, Runnable> pair1 = objectlistiterator.next();
         if (pair1.getFirst() == ServerWorldLightManager.Phase.POST_UPDATE) {
            pair1.getSecond().run();
         }

         objectlistiterator.remove();
      }

   }

   public void setTaskPerBatch(int p_215598_1_) {
      this.taskPerBatch = p_215598_1_;
   }

   static enum Phase {
      PRE_UPDATE,
      POST_UPDATE;
   }
}
