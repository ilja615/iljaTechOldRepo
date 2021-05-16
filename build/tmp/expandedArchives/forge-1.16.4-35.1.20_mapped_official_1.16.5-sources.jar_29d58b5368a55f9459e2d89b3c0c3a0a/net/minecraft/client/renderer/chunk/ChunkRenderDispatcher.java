package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.crash.CrashReport;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.DelegatedTaskExecutor;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ChunkRenderDispatcher {
   private static final Logger LOGGER = LogManager.getLogger();
   private final PriorityQueue<ChunkRenderDispatcher.ChunkRender.ChunkRenderTask> toBatch = Queues.newPriorityQueue();
   private final Queue<RegionRenderCacheBuilder> freeBuffers;
   private final Queue<Runnable> toUpload = Queues.newConcurrentLinkedQueue();
   private volatile int toBatchCount;
   private volatile int freeBufferCount;
   private final RegionRenderCacheBuilder fixedBuffers;
   private final DelegatedTaskExecutor<Runnable> mailbox;
   private final Executor executor;
   private World level;
   private final WorldRenderer renderer;
   private Vector3d camera = Vector3d.ZERO;

   public ChunkRenderDispatcher(World p_i226020_1_, WorldRenderer p_i226020_2_, Executor p_i226020_3_, boolean p_i226020_4_, RegionRenderCacheBuilder p_i226020_5_) {
      this(p_i226020_1_, p_i226020_2_, p_i226020_3_, p_i226020_4_, p_i226020_5_, -1);
   }
   public ChunkRenderDispatcher(World p_i226020_1_, WorldRenderer p_i226020_2_, Executor p_i226020_3_, boolean p_i226020_4_, RegionRenderCacheBuilder p_i226020_5_, int countRenderBuilders) {
      this.level = p_i226020_1_;
      this.renderer = p_i226020_2_;
      int i = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3D) / (RenderType.chunkBufferLayers().stream().mapToInt(RenderType::bufferSize).sum() * 4) - 1);
      int j = Runtime.getRuntime().availableProcessors();
      int k = p_i226020_4_ ? j : Math.min(j, 4);
      int l = countRenderBuilders < 0 ? Math.max(1, Math.min(k, i)) : countRenderBuilders;
      this.fixedBuffers = p_i226020_5_;
      List<RegionRenderCacheBuilder> list = Lists.newArrayListWithExpectedSize(l);

      try {
         for(int i1 = 0; i1 < l; ++i1) {
            list.add(new RegionRenderCacheBuilder());
         }
      } catch (OutOfMemoryError outofmemoryerror) {
         LOGGER.warn("Allocated only {}/{} buffers", list.size(), l);
         int j1 = Math.min(list.size() * 2 / 3, list.size() - 1);

         for(int k1 = 0; k1 < j1; ++k1) {
            list.remove(list.size() - 1);
         }

         System.gc();
      }

      this.freeBuffers = Queues.newArrayDeque(list);
      this.freeBufferCount = this.freeBuffers.size();
      this.executor = p_i226020_3_;
      this.mailbox = DelegatedTaskExecutor.create(p_i226020_3_, "Chunk Renderer");
      this.mailbox.tell(this::runTask);
   }

   public void setLevel(World p_228895_1_) {
      this.level = p_228895_1_;
   }

   private void runTask() {
      if (!this.freeBuffers.isEmpty()) {
         ChunkRenderDispatcher.ChunkRender.ChunkRenderTask chunkrenderdispatcher$chunkrender$chunkrendertask = this.toBatch.poll();
         if (chunkrenderdispatcher$chunkrender$chunkrendertask != null) {
            RegionRenderCacheBuilder regionrendercachebuilder = this.freeBuffers.poll();
            this.toBatchCount = this.toBatch.size();
            this.freeBufferCount = this.freeBuffers.size();
            CompletableFuture.runAsync(() -> {
            }, this.executor).thenCompose((p_228901_2_) -> {
               return chunkrenderdispatcher$chunkrender$chunkrendertask.doTask(regionrendercachebuilder);
            }).whenComplete((p_228898_2_, p_228898_3_) -> {
               if (p_228898_3_ != null) {
                  CrashReport crashreport = CrashReport.forThrowable(p_228898_3_, "Batching chunks");
                  Minecraft.getInstance().delayCrash(Minecraft.getInstance().fillReport(crashreport));
               } else {
                  this.mailbox.tell(() -> {
                     if (p_228898_2_ == ChunkRenderDispatcher.ChunkTaskResult.SUCCESSFUL) {
                        regionrendercachebuilder.clearAll();
                     } else {
                        regionrendercachebuilder.discardAll();
                     }

                     this.freeBuffers.add(regionrendercachebuilder);
                     this.freeBufferCount = this.freeBuffers.size();
                     this.runTask();
                  });
               }
            });
         }
      }
   }

   public String getStats() {
      return String.format("pC: %03d, pU: %02d, aB: %02d", this.toBatchCount, this.toUpload.size(), this.freeBufferCount);
   }

   public void setCamera(Vector3d p_217669_1_) {
      this.camera = p_217669_1_;
   }

   public Vector3d getCameraPosition() {
      return this.camera;
   }

   public boolean uploadAllPendingUploads() {
      boolean flag;
      Runnable runnable;
      for(flag = false; (runnable = this.toUpload.poll()) != null; flag = true) {
         runnable.run();
      }

      return flag;
   }

   public void rebuildChunkSync(ChunkRenderDispatcher.ChunkRender p_228902_1_) {
      p_228902_1_.compileSync();
   }

   public void blockUntilClear() {
      this.clearBatchQueue();
   }

   public void schedule(ChunkRenderDispatcher.ChunkRender.ChunkRenderTask p_228900_1_) {
      this.mailbox.tell(() -> {
         this.toBatch.offer(p_228900_1_);
         this.toBatchCount = this.toBatch.size();
         this.runTask();
      });
   }

   public CompletableFuture<Void> uploadChunkLayer(BufferBuilder p_228896_1_, VertexBuffer p_228896_2_) {
      return CompletableFuture.runAsync(() -> {
      }, this.toUpload::add).thenCompose((p_228897_3_) -> {
         return this.doUploadChunkLayer(p_228896_1_, p_228896_2_);
      });
   }

   private CompletableFuture<Void> doUploadChunkLayer(BufferBuilder p_228904_1_, VertexBuffer p_228904_2_) {
      return p_228904_2_.uploadLater(p_228904_1_);
   }

   private void clearBatchQueue() {
      while(!this.toBatch.isEmpty()) {
         ChunkRenderDispatcher.ChunkRender.ChunkRenderTask chunkrenderdispatcher$chunkrender$chunkrendertask = this.toBatch.poll();
         if (chunkrenderdispatcher$chunkrender$chunkrendertask != null) {
            chunkrenderdispatcher$chunkrender$chunkrendertask.cancel();
         }
      }

      this.toBatchCount = 0;
   }

   public boolean isQueueEmpty() {
      return this.toBatchCount == 0 && this.toUpload.isEmpty();
   }

   public void dispose() {
      this.clearBatchQueue();
      this.mailbox.close();
      this.freeBuffers.clear();
   }

   @OnlyIn(Dist.CLIENT)
   public class ChunkRender implements net.minecraftforge.client.extensions.IForgeRenderChunk {
      public final AtomicReference<ChunkRenderDispatcher.CompiledChunk> compiled = new AtomicReference<>(ChunkRenderDispatcher.CompiledChunk.UNCOMPILED);
      @Nullable
      private ChunkRenderDispatcher.ChunkRender.RebuildTask lastRebuildTask;
      @Nullable
      private ChunkRenderDispatcher.ChunkRender.SortTransparencyTask lastResortTransparencyTask;
      private final Set<TileEntity> globalBlockEntities = Sets.newHashSet();
      private final Map<RenderType, VertexBuffer> buffers = RenderType.chunkBufferLayers().stream().collect(Collectors.toMap((p_228934_0_) -> {
         return p_228934_0_;
      }, (p_228933_0_) -> {
         return new VertexBuffer(DefaultVertexFormats.BLOCK);
      }));
      public AxisAlignedBB bb;
      private int lastFrame = -1;
      private boolean dirty = true;
      private final BlockPos.Mutable origin = new BlockPos.Mutable(-1, -1, -1);
      private final BlockPos.Mutable[] relativeOrigins = Util.make(new BlockPos.Mutable[6], (p_228932_0_) -> {
         for(int i = 0; i < p_228932_0_.length; ++i) {
            p_228932_0_[i] = new BlockPos.Mutable();
         }

      });
      private boolean playerChanged;

      private boolean doesChunkExistAt(BlockPos p_228930_1_) {
         return ChunkRenderDispatcher.this.level.getChunk(p_228930_1_.getX() >> 4, p_228930_1_.getZ() >> 4, ChunkStatus.FULL, false) != null;
      }

      public boolean hasAllNeighbors() {
         int i = 24;
         if (!(this.getDistToPlayerSqr() > 576.0D)) {
            return true;
         } else {
            return this.doesChunkExistAt(this.relativeOrigins[Direction.WEST.ordinal()]) && this.doesChunkExistAt(this.relativeOrigins[Direction.NORTH.ordinal()]) && this.doesChunkExistAt(this.relativeOrigins[Direction.EAST.ordinal()]) && this.doesChunkExistAt(this.relativeOrigins[Direction.SOUTH.ordinal()]);
         }
      }

      public boolean setFrame(int p_178577_1_) {
         if (this.lastFrame == p_178577_1_) {
            return false;
         } else {
            this.lastFrame = p_178577_1_;
            return true;
         }
      }

      public VertexBuffer getBuffer(RenderType p_228924_1_) {
         return this.buffers.get(p_228924_1_);
      }

      public void setOrigin(int p_189562_1_, int p_189562_2_, int p_189562_3_) {
         if (p_189562_1_ != this.origin.getX() || p_189562_2_ != this.origin.getY() || p_189562_3_ != this.origin.getZ()) {
            this.reset();
            this.origin.set(p_189562_1_, p_189562_2_, p_189562_3_);
            this.bb = new AxisAlignedBB((double)p_189562_1_, (double)p_189562_2_, (double)p_189562_3_, (double)(p_189562_1_ + 16), (double)(p_189562_2_ + 16), (double)(p_189562_3_ + 16));

            for(Direction direction : Direction.values()) {
               this.relativeOrigins[direction.ordinal()].set(this.origin).move(direction, 16);
            }

         }
      }

      protected double getDistToPlayerSqr() {
         ActiveRenderInfo activerenderinfo = Minecraft.getInstance().gameRenderer.getMainCamera();
         double d0 = this.bb.minX + 8.0D - activerenderinfo.getPosition().x;
         double d1 = this.bb.minY + 8.0D - activerenderinfo.getPosition().y;
         double d2 = this.bb.minZ + 8.0D - activerenderinfo.getPosition().z;
         return d0 * d0 + d1 * d1 + d2 * d2;
      }

      private void beginLayer(BufferBuilder p_228923_1_) {
         p_228923_1_.begin(7, DefaultVertexFormats.BLOCK);
      }

      public ChunkRenderDispatcher.CompiledChunk getCompiledChunk() {
         return this.compiled.get();
      }

      private void reset() {
         this.cancelTasks();
         this.compiled.set(ChunkRenderDispatcher.CompiledChunk.UNCOMPILED);
         this.dirty = true;
      }

      public void releaseBuffers() {
         this.reset();
         this.buffers.values().forEach(VertexBuffer::close);
      }

      public BlockPos getOrigin() {
         return this.origin;
      }

      public void setDirty(boolean p_178575_1_) {
         boolean flag = this.dirty;
         this.dirty = true;
         this.playerChanged = p_178575_1_ | (flag && this.playerChanged);
      }

      public void setNotDirty() {
         this.dirty = false;
         this.playerChanged = false;
      }

      public boolean isDirty() {
         return this.dirty;
      }

      public boolean isDirtyFromPlayer() {
         return this.dirty && this.playerChanged;
      }

      public BlockPos getRelativeOrigin(Direction p_181701_1_) {
         return this.relativeOrigins[p_181701_1_.ordinal()];
      }

      public boolean resortTransparency(RenderType p_228925_1_, ChunkRenderDispatcher p_228925_2_) {
         ChunkRenderDispatcher.CompiledChunk chunkrenderdispatcher$compiledchunk = this.getCompiledChunk();
         if (this.lastResortTransparencyTask != null) {
            this.lastResortTransparencyTask.cancel();
         }

         if (!chunkrenderdispatcher$compiledchunk.hasLayer.contains(p_228925_1_)) {
            return false;
         } else {
            this.lastResortTransparencyTask = new ChunkRenderDispatcher.ChunkRender.SortTransparencyTask(new net.minecraft.util.math.ChunkPos(getOrigin()), this.getDistToPlayerSqr(), chunkrenderdispatcher$compiledchunk);
            p_228925_2_.schedule(this.lastResortTransparencyTask);
            return true;
         }
      }

      protected void cancelTasks() {
         if (this.lastRebuildTask != null) {
            this.lastRebuildTask.cancel();
            this.lastRebuildTask = null;
         }

         if (this.lastResortTransparencyTask != null) {
            this.lastResortTransparencyTask.cancel();
            this.lastResortTransparencyTask = null;
         }

      }

      public ChunkRenderDispatcher.ChunkRender.ChunkRenderTask createCompileTask() {
         this.cancelTasks();
         BlockPos blockpos = this.origin.immutable();
         int i = 1;
         ChunkRenderCache chunkrendercache = createRegionRenderCache(ChunkRenderDispatcher.this.level, blockpos.offset(-1, -1, -1), blockpos.offset(16, 16, 16), 1);
         this.lastRebuildTask = new ChunkRenderDispatcher.ChunkRender.RebuildTask(new net.minecraft.util.math.ChunkPos(getOrigin()), this.getDistToPlayerSqr(), chunkrendercache);
         return this.lastRebuildTask;
      }

      public void rebuildChunkAsync(ChunkRenderDispatcher p_228929_1_) {
         ChunkRenderDispatcher.ChunkRender.ChunkRenderTask chunkrenderdispatcher$chunkrender$chunkrendertask = this.createCompileTask();
         p_228929_1_.schedule(chunkrenderdispatcher$chunkrender$chunkrendertask);
      }

      private void updateGlobalBlockEntities(Set<TileEntity> p_228931_1_) {
         Set<TileEntity> set = Sets.newHashSet(p_228931_1_);
         Set<TileEntity> set1 = Sets.newHashSet(this.globalBlockEntities);
         set.removeAll(this.globalBlockEntities);
         set1.removeAll(p_228931_1_);
         this.globalBlockEntities.clear();
         this.globalBlockEntities.addAll(p_228931_1_);
         ChunkRenderDispatcher.this.renderer.updateGlobalBlockEntities(set1, set);
      }

      public void compileSync() {
         ChunkRenderDispatcher.ChunkRender.ChunkRenderTask chunkrenderdispatcher$chunkrender$chunkrendertask = this.createCompileTask();
         chunkrenderdispatcher$chunkrender$chunkrendertask.doTask(ChunkRenderDispatcher.this.fixedBuffers);
      }

      @OnlyIn(Dist.CLIENT)
      abstract class ChunkRenderTask implements Comparable<ChunkRenderDispatcher.ChunkRender.ChunkRenderTask> {
         protected final double distAtCreation;
         protected final AtomicBoolean isCancelled = new AtomicBoolean(false);
         protected java.util.Map<net.minecraft.util.math.BlockPos, net.minecraftforge.client.model.data.IModelData> modelData;

         public ChunkRenderTask(double p_i226023_2_) {
            this(null, p_i226023_2_);
         }

         public ChunkRenderTask(@Nullable net.minecraft.util.math.ChunkPos pos, double p_i226023_2_) {
            this.distAtCreation = p_i226023_2_;
            if (pos == null) {
               this.modelData = java.util.Collections.emptyMap();
            } else {
               this.modelData = net.minecraftforge.client.model.ModelDataManager.getModelData(net.minecraft.client.Minecraft.getInstance().level, pos);
            }
         }

         public abstract CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> doTask(RegionRenderCacheBuilder p_225618_1_);

         public abstract void cancel();

         public int compareTo(ChunkRenderDispatcher.ChunkRender.ChunkRenderTask p_compareTo_1_) {
            return Doubles.compare(this.distAtCreation, p_compareTo_1_.distAtCreation);
         }

         public net.minecraftforge.client.model.data.IModelData getModelData(net.minecraft.util.math.BlockPos pos) {
            return modelData.getOrDefault(pos, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
         }
      }

      @OnlyIn(Dist.CLIENT)
      class RebuildTask extends ChunkRenderDispatcher.ChunkRender.ChunkRenderTask {
         @Nullable
         protected ChunkRenderCache region;

         @Deprecated
         public RebuildTask(double p_i226024_2_, @Nullable ChunkRenderCache p_i226024_4_) {
            this(null, p_i226024_2_, p_i226024_4_);
         }

         public RebuildTask(@Nullable net.minecraft.util.math.ChunkPos pos, double p_i226024_2_, @Nullable ChunkRenderCache p_i226024_4_) {
            super(pos, p_i226024_2_);
            this.region = p_i226024_4_;
         }

         public CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> doTask(RegionRenderCacheBuilder p_225618_1_) {
            if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (!ChunkRender.this.hasAllNeighbors()) {
               this.region = null;
               ChunkRender.this.setDirty(false);
               this.isCancelled.set(true);
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else {
               Vector3d vector3d = ChunkRenderDispatcher.this.getCameraPosition();
               float f = (float)vector3d.x;
               float f1 = (float)vector3d.y;
               float f2 = (float)vector3d.z;
               ChunkRenderDispatcher.CompiledChunk chunkrenderdispatcher$compiledchunk = new ChunkRenderDispatcher.CompiledChunk();
               Set<TileEntity> set = this.compile(f, f1, f2, chunkrenderdispatcher$compiledchunk, p_225618_1_);
               ChunkRender.this.updateGlobalBlockEntities(set);
               if (this.isCancelled.get()) {
                  return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
               } else {
                  List<CompletableFuture<Void>> list = Lists.newArrayList();
                  chunkrenderdispatcher$compiledchunk.hasLayer.forEach((p_228943_3_) -> {
                     list.add(ChunkRenderDispatcher.this.uploadChunkLayer(p_225618_1_.builder(p_228943_3_), ChunkRender.this.getBuffer(p_228943_3_)));
                  });
                  return Util.sequence(list).handle((p_228941_2_, p_228941_3_) -> {
                     if (p_228941_3_ != null && !(p_228941_3_ instanceof CancellationException) && !(p_228941_3_ instanceof InterruptedException)) {
                        Minecraft.getInstance().delayCrash(CrashReport.forThrowable(p_228941_3_, "Rendering chunk"));
                     }

                     if (this.isCancelled.get()) {
                        return ChunkRenderDispatcher.ChunkTaskResult.CANCELLED;
                     } else {
                        ChunkRender.this.compiled.set(chunkrenderdispatcher$compiledchunk);
                        return ChunkRenderDispatcher.ChunkTaskResult.SUCCESSFUL;
                     }
                  });
               }
            }
         }

         private Set<TileEntity> compile(float p_228940_1_, float p_228940_2_, float p_228940_3_, ChunkRenderDispatcher.CompiledChunk p_228940_4_, RegionRenderCacheBuilder p_228940_5_) {
            int i = 1;
            BlockPos blockpos = ChunkRender.this.origin.immutable();
            BlockPos blockpos1 = blockpos.offset(15, 15, 15);
            VisGraph visgraph = new VisGraph();
            Set<TileEntity> set = Sets.newHashSet();
            ChunkRenderCache chunkrendercache = this.region;
            this.region = null;
            MatrixStack matrixstack = new MatrixStack();
            if (chunkrendercache != null) {
               BlockModelRenderer.enableCaching();
               Random random = new Random();
               BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();

               for(BlockPos blockpos2 : BlockPos.betweenClosed(blockpos, blockpos1)) {
                  BlockState blockstate = chunkrendercache.getBlockState(blockpos2);
                  Block block = blockstate.getBlock();
                  if (blockstate.isSolidRender(chunkrendercache, blockpos2)) {
                     visgraph.setOpaque(blockpos2);
                  }

                  if (blockstate.hasTileEntity()) {
                     TileEntity tileentity = chunkrendercache.getBlockEntity(blockpos2, Chunk.CreateEntityType.CHECK);
                     if (tileentity != null) {
                        this.handleBlockEntity(p_228940_4_, set, tileentity);
                     }
                  }

                  FluidState fluidstate = chunkrendercache.getFluidState(blockpos2);
                  net.minecraftforge.client.model.data.IModelData modelData = getModelData(blockpos2);
                  for (RenderType rendertype : RenderType.chunkBufferLayers()) {
                     net.minecraftforge.client.ForgeHooksClient.setRenderLayer(rendertype);
                  if (!fluidstate.isEmpty() && RenderTypeLookup.canRenderInLayer(fluidstate, rendertype)) {
                     BufferBuilder bufferbuilder = p_228940_5_.builder(rendertype);
                     if (p_228940_4_.hasLayer.add(rendertype)) {
                        ChunkRender.this.beginLayer(bufferbuilder);
                     }

                     if (blockrendererdispatcher.renderLiquid(blockpos2, chunkrendercache, bufferbuilder, fluidstate)) {
                        p_228940_4_.isCompletelyEmpty = false;
                        p_228940_4_.hasBlocks.add(rendertype);
                     }
                  }

                  if (blockstate.getRenderShape() != BlockRenderType.INVISIBLE && RenderTypeLookup.canRenderInLayer(blockstate, rendertype)) {
                     RenderType rendertype1 = rendertype;
                     BufferBuilder bufferbuilder2 = p_228940_5_.builder(rendertype1);
                     if (p_228940_4_.hasLayer.add(rendertype1)) {
                        ChunkRender.this.beginLayer(bufferbuilder2);
                     }

                     matrixstack.pushPose();
                     matrixstack.translate((double)(blockpos2.getX() & 15), (double)(blockpos2.getY() & 15), (double)(blockpos2.getZ() & 15));
                     if (blockrendererdispatcher.renderModel(blockstate, blockpos2, chunkrendercache, matrixstack, bufferbuilder2, true, random, modelData)) {
                        p_228940_4_.isCompletelyEmpty = false;
                        p_228940_4_.hasBlocks.add(rendertype1);
                     }

                     matrixstack.popPose();
                  }
                  }
               }
               net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);

               if (p_228940_4_.hasBlocks.contains(RenderType.translucent())) {
                  BufferBuilder bufferbuilder1 = p_228940_5_.builder(RenderType.translucent());
                  bufferbuilder1.sortQuads(p_228940_1_ - (float)blockpos.getX(), p_228940_2_ - (float)blockpos.getY(), p_228940_3_ - (float)blockpos.getZ());
                  p_228940_4_.transparencyState = bufferbuilder1.getState();
               }

               p_228940_4_.hasLayer.stream().map(p_228940_5_::builder).forEach(BufferBuilder::end);
               BlockModelRenderer.clearCache();
            }

            p_228940_4_.visibilitySet = visgraph.resolve();
            return set;
         }

         private <E extends TileEntity> void handleBlockEntity(ChunkRenderDispatcher.CompiledChunk p_228942_1_, Set<TileEntity> p_228942_2_, E p_228942_3_) {
            TileEntityRenderer<E> tileentityrenderer = TileEntityRendererDispatcher.instance.getRenderer(p_228942_3_);
            if (tileentityrenderer != null) {
               if (tileentityrenderer.shouldRenderOffScreen(p_228942_3_)) {
                  p_228942_2_.add(p_228942_3_);
               }
               else p_228942_1_.renderableBlockEntities.add(p_228942_3_); //FORGE: Fix MC-112730
            }

         }

         public void cancel() {
            this.region = null;
            if (this.isCancelled.compareAndSet(false, true)) {
               ChunkRender.this.setDirty(false);
            }

         }
      }

      @OnlyIn(Dist.CLIENT)
      class SortTransparencyTask extends ChunkRenderDispatcher.ChunkRender.ChunkRenderTask {
         private final ChunkRenderDispatcher.CompiledChunk compiledChunk;

         @Deprecated
         public SortTransparencyTask(double p_i226025_2_, ChunkRenderDispatcher.CompiledChunk p_i226025_4_) {
            this(null, p_i226025_2_, p_i226025_4_);
         }

         public SortTransparencyTask(@Nullable net.minecraft.util.math.ChunkPos pos, double p_i226025_2_, ChunkRenderDispatcher.CompiledChunk p_i226025_4_) {
            super(pos, p_i226025_2_);
            this.compiledChunk = p_i226025_4_;
         }

         public CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> doTask(RegionRenderCacheBuilder p_225618_1_) {
            if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (!ChunkRender.this.hasAllNeighbors()) {
               this.isCancelled.set(true);
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else {
               Vector3d vector3d = ChunkRenderDispatcher.this.getCameraPosition();
               float f = (float)vector3d.x;
               float f1 = (float)vector3d.y;
               float f2 = (float)vector3d.z;
               BufferBuilder.State bufferbuilder$state = this.compiledChunk.transparencyState;
               if (bufferbuilder$state != null && this.compiledChunk.hasBlocks.contains(RenderType.translucent())) {
                  BufferBuilder bufferbuilder = p_225618_1_.builder(RenderType.translucent());
                  ChunkRender.this.beginLayer(bufferbuilder);
                  bufferbuilder.restoreState(bufferbuilder$state);
                  bufferbuilder.sortQuads(f - (float)ChunkRender.this.origin.getX(), f1 - (float)ChunkRender.this.origin.getY(), f2 - (float)ChunkRender.this.origin.getZ());
                  this.compiledChunk.transparencyState = bufferbuilder.getState();
                  bufferbuilder.end();
                  if (this.isCancelled.get()) {
                     return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
                  } else {
                     CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> completablefuture = ChunkRenderDispatcher.this.uploadChunkLayer(p_225618_1_.builder(RenderType.translucent()), ChunkRender.this.getBuffer(RenderType.translucent())).thenApply((p_228947_0_) -> {
                        return ChunkRenderDispatcher.ChunkTaskResult.CANCELLED;
                     });
                     return completablefuture.handle((p_228946_1_, p_228946_2_) -> {
                        if (p_228946_2_ != null && !(p_228946_2_ instanceof CancellationException) && !(p_228946_2_ instanceof InterruptedException)) {
                           Minecraft.getInstance().delayCrash(CrashReport.forThrowable(p_228946_2_, "Rendering chunk"));
                        }

                        return this.isCancelled.get() ? ChunkRenderDispatcher.ChunkTaskResult.CANCELLED : ChunkRenderDispatcher.ChunkTaskResult.SUCCESSFUL;
                     });
                  }
               } else {
                  return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
               }
            }
         }

         public void cancel() {
            this.isCancelled.set(true);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   static enum ChunkTaskResult {
      SUCCESSFUL,
      CANCELLED;
   }

   @OnlyIn(Dist.CLIENT)
   public static class CompiledChunk {
      public static final ChunkRenderDispatcher.CompiledChunk UNCOMPILED = new ChunkRenderDispatcher.CompiledChunk() {
         public boolean facesCanSeeEachother(Direction p_178495_1_, Direction p_178495_2_) {
            return false;
         }
      };
      private final Set<RenderType> hasBlocks = new ObjectArraySet<>();
      private final Set<RenderType> hasLayer = new ObjectArraySet<>();
      private boolean isCompletelyEmpty = true;
      private final List<TileEntity> renderableBlockEntities = Lists.newArrayList();
      private SetVisibility visibilitySet = new SetVisibility();
      @Nullable
      private BufferBuilder.State transparencyState;

      public boolean hasNoRenderableLayers() {
         return this.isCompletelyEmpty;
      }

      public boolean isEmpty(RenderType p_228912_1_) {
         return !this.hasBlocks.contains(p_228912_1_);
      }

      public List<TileEntity> getRenderableBlockEntities() {
         return this.renderableBlockEntities;
      }

      public boolean facesCanSeeEachother(Direction p_178495_1_, Direction p_178495_2_) {
         return this.visibilitySet.visibilityBetween(p_178495_1_, p_178495_2_);
      }
   }
}
