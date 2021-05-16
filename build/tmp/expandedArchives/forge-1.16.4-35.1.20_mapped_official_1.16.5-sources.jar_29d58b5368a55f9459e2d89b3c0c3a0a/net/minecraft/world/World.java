package net.minecraft.world;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.profiler.IProfiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.storage.ISpawnWorldInfo;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class World extends net.minecraftforge.common.capabilities.CapabilityProvider<World> implements IWorld, AutoCloseable, net.minecraftforge.common.extensions.IForgeWorld {
   protected static final Logger LOGGER = LogManager.getLogger();
   public static final Codec<RegistryKey<World>> RESOURCE_KEY_CODEC = ResourceLocation.CODEC.xmap(RegistryKey.elementKey(Registry.DIMENSION_REGISTRY), RegistryKey::location);
   public static final RegistryKey<World> OVERWORLD = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("overworld"));
   public static final RegistryKey<World> NETHER = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("the_nether"));
   public static final RegistryKey<World> END = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("the_end"));
   private static final Direction[] DIRECTIONS = Direction.values();
   public final List<TileEntity> blockEntityList = Lists.newArrayList();
   public final List<TileEntity> tickableBlockEntities = Lists.newArrayList();
   protected final List<TileEntity> pendingBlockEntities = Lists.newArrayList();
   protected final java.util.Set<TileEntity> blockEntitiesToUnload = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>()); // Forge: faster "contains" makes removal much more efficient
   private final Thread thread;
   private final boolean isDebug;
   private int skyDarken;
   protected int randValue = (new Random()).nextInt();
   protected final int addend = 1013904223;
   public float oRainLevel;
   public float rainLevel;
   public float oThunderLevel;
   public float thunderLevel;
   public final Random random = new Random();
   private final DimensionType dimensionType;
   protected final ISpawnWorldInfo levelData;
   private final Supplier<IProfiler> profiler;
   public final boolean isClientSide;
   protected boolean updatingBlockEntities;
   private final WorldBorder worldBorder;
   private final BiomeManager biomeManager;
   private final RegistryKey<World> dimension;
   public boolean restoringBlockSnapshots = false;
   public boolean captureBlockSnapshots = false;
   public java.util.ArrayList<net.minecraftforge.common.util.BlockSnapshot> capturedBlockSnapshots = new java.util.ArrayList<>();

   protected World(ISpawnWorldInfo p_i241925_1_, RegistryKey<World> p_i241925_2_, final DimensionType p_i241925_3_, Supplier<IProfiler> p_i241925_4_, boolean p_i241925_5_, boolean p_i241925_6_, long p_i241925_7_) {
      super(World.class);
      this.profiler = p_i241925_4_;
      this.levelData = p_i241925_1_;
      this.dimensionType = p_i241925_3_;
      this.dimension = p_i241925_2_;
      this.isClientSide = p_i241925_5_;
      if (p_i241925_3_.coordinateScale() != 1.0D) {
         this.worldBorder = new WorldBorder() {
            public double getCenterX() {
               return super.getCenterX() / p_i241925_3_.coordinateScale();
            }

            public double getCenterZ() {
               return super.getCenterZ() / p_i241925_3_.coordinateScale();
            }
         };
      } else {
         this.worldBorder = new WorldBorder();
      }

      this.thread = Thread.currentThread();
      this.biomeManager = new BiomeManager(this, p_i241925_7_, p_i241925_3_.getBiomeZoomer());
      this.isDebug = p_i241925_6_;
   }

   public boolean isClientSide() {
      return this.isClientSide;
   }

   @Nullable
   public MinecraftServer getServer() {
      return null;
   }

   public static boolean isInWorldBounds(BlockPos p_175701_0_) {
      return !isOutsideBuildHeight(p_175701_0_) && isInWorldBoundsHorizontal(p_175701_0_);
   }

   public static boolean isInSpawnableBounds(BlockPos p_234935_0_) {
      return !isOutsideSpawnableHeight(p_234935_0_.getY()) && isInWorldBoundsHorizontal(p_234935_0_);
   }

   private static boolean isInWorldBoundsHorizontal(BlockPos p_234934_0_) {
      return p_234934_0_.getX() >= -30000000 && p_234934_0_.getZ() >= -30000000 && p_234934_0_.getX() < 30000000 && p_234934_0_.getZ() < 30000000;
   }

   private static boolean isOutsideSpawnableHeight(int p_234933_0_) {
      return p_234933_0_ < -20000000 || p_234933_0_ >= 20000000;
   }

   public static boolean isOutsideBuildHeight(BlockPos p_189509_0_) {
      return isOutsideBuildHeight(p_189509_0_.getY());
   }

   public static boolean isOutsideBuildHeight(int p_217405_0_) {
      return p_217405_0_ < 0 || p_217405_0_ >= 256;
   }

   public Chunk getChunkAt(BlockPos p_175726_1_) {
      return this.getChunk(p_175726_1_.getX() >> 4, p_175726_1_.getZ() >> 4);
   }

   public Chunk getChunk(int p_212866_1_, int p_212866_2_) {
      return (Chunk)this.getChunk(p_212866_1_, p_212866_2_, ChunkStatus.FULL);
   }

   public IChunk getChunk(int p_217353_1_, int p_217353_2_, ChunkStatus p_217353_3_, boolean p_217353_4_) {
      IChunk ichunk = this.getChunkSource().getChunk(p_217353_1_, p_217353_2_, p_217353_3_, p_217353_4_);
      if (ichunk == null && p_217353_4_) {
         throw new IllegalStateException("Should always be able to create a chunk!");
      } else {
         return ichunk;
      }
   }

   public boolean setBlock(BlockPos p_180501_1_, BlockState p_180501_2_, int p_180501_3_) {
      return this.setBlock(p_180501_1_, p_180501_2_, p_180501_3_, 512);
   }

   public boolean setBlock(BlockPos p_241211_1_, BlockState p_241211_2_, int p_241211_3_, int p_241211_4_) {
      if (isOutsideBuildHeight(p_241211_1_)) {
         return false;
      } else if (!this.isClientSide && this.isDebug()) {
         return false;
      } else {
         Chunk chunk = this.getChunkAt(p_241211_1_);
         Block block = p_241211_2_.getBlock();

         p_241211_1_ = p_241211_1_.immutable(); // Forge - prevent mutable BlockPos leaks
         net.minecraftforge.common.util.BlockSnapshot blockSnapshot = null;
         if (this.captureBlockSnapshots && !this.isClientSide) {
             blockSnapshot = net.minecraftforge.common.util.BlockSnapshot.create(this.dimension, this, p_241211_1_, p_241211_3_);
             this.capturedBlockSnapshots.add(blockSnapshot);
         }

         BlockState old = getBlockState(p_241211_1_);
         int oldLight = old.getLightValue(this, p_241211_1_);
         int oldOpacity = old.getLightBlock(this, p_241211_1_);

         BlockState blockstate = chunk.setBlockState(p_241211_1_, p_241211_2_, (p_241211_3_ & 64) != 0);
         if (blockstate == null) {
            if (blockSnapshot != null) this.capturedBlockSnapshots.remove(blockSnapshot);
            return false;
         } else {
            BlockState blockstate1 = this.getBlockState(p_241211_1_);
            if ((p_241211_3_ & 128) == 0 && blockstate1 != blockstate && (blockstate1.getLightBlock(this, p_241211_1_) != oldOpacity || blockstate1.getLightValue(this, p_241211_1_) != oldLight || blockstate1.useShapeForLightOcclusion() || blockstate.useShapeForLightOcclusion())) {
               this.getProfiler().push("queueCheckLight");
               this.getChunkSource().getLightEngine().checkBlock(p_241211_1_);
               this.getProfiler().pop();
            }

            if (blockSnapshot == null) { // Don't notify clients or update physics while capturing blockstates
               this.markAndNotifyBlock(p_241211_1_, chunk, blockstate, p_241211_2_, p_241211_3_, p_241211_4_);
            }
            return true;
         }
      }
   }

   // Split off from original setBlockState(BlockPos, BlockState, int, int) method in order to directly send client and physic updates
   public void markAndNotifyBlock(BlockPos p_241211_1_, @Nullable Chunk chunk, BlockState blockstate, BlockState p_241211_2_, int p_241211_3_, int p_241211_4_)
   {
       Block block = p_241211_2_.getBlock();
       BlockState blockstate1 = getBlockState(p_241211_1_);
       {
           {
            if (blockstate1 == p_241211_2_) {
               if (blockstate != blockstate1) {
                  this.setBlocksDirty(p_241211_1_, blockstate, blockstate1);
               }

               if ((p_241211_3_ & 2) != 0 && (!this.isClientSide || (p_241211_3_ & 4) == 0) && (this.isClientSide || chunk.getFullStatus() != null && chunk.getFullStatus().isOrAfter(ChunkHolder.LocationType.TICKING))) {
                  this.sendBlockUpdated(p_241211_1_, blockstate, p_241211_2_, p_241211_3_);
               }

               if ((p_241211_3_ & 1) != 0) {
                  this.blockUpdated(p_241211_1_, blockstate.getBlock());
                  if (!this.isClientSide && p_241211_2_.hasAnalogOutputSignal()) {
                     this.updateNeighbourForOutputSignal(p_241211_1_, block);
                  }
               }

               if ((p_241211_3_ & 16) == 0 && p_241211_4_ > 0) {
                  int i = p_241211_3_ & -34;
                  blockstate.updateIndirectNeighbourShapes(this, p_241211_1_, i, p_241211_4_ - 1);
                  p_241211_2_.updateNeighbourShapes(this, p_241211_1_, i, p_241211_4_ - 1);
                  p_241211_2_.updateIndirectNeighbourShapes(this, p_241211_1_, i, p_241211_4_ - 1);
               }

               this.onBlockStateChange(p_241211_1_, blockstate, blockstate1);
            }
         }
      }
   }

   public void onBlockStateChange(BlockPos p_217393_1_, BlockState p_217393_2_, BlockState p_217393_3_) {
   }

   public boolean removeBlock(BlockPos p_217377_1_, boolean p_217377_2_) {
      FluidState fluidstate = this.getFluidState(p_217377_1_);
      return this.setBlock(p_217377_1_, fluidstate.createLegacyBlock(), 3 | (p_217377_2_ ? 64 : 0));
   }

   public boolean destroyBlock(BlockPos p_241212_1_, boolean p_241212_2_, @Nullable Entity p_241212_3_, int p_241212_4_) {
      BlockState blockstate = this.getBlockState(p_241212_1_);
      if (blockstate.isAir(this, p_241212_1_)) {
         return false;
      } else {
         FluidState fluidstate = this.getFluidState(p_241212_1_);
         if (!(blockstate.getBlock() instanceof AbstractFireBlock)) {
            this.levelEvent(2001, p_241212_1_, Block.getId(blockstate));
         }

         if (p_241212_2_) {
            TileEntity tileentity = blockstate.hasTileEntity() ? this.getBlockEntity(p_241212_1_) : null;
            Block.dropResources(blockstate, this, p_241212_1_, tileentity, p_241212_3_, ItemStack.EMPTY);
         }

         return this.setBlock(p_241212_1_, fluidstate.createLegacyBlock(), 3, p_241212_4_);
      }
   }

   public boolean setBlockAndUpdate(BlockPos p_175656_1_, BlockState p_175656_2_) {
      return this.setBlock(p_175656_1_, p_175656_2_, 3);
   }

   public abstract void sendBlockUpdated(BlockPos p_184138_1_, BlockState p_184138_2_, BlockState p_184138_3_, int p_184138_4_);

   public void setBlocksDirty(BlockPos p_225319_1_, BlockState p_225319_2_, BlockState p_225319_3_) {
   }

   public void updateNeighborsAt(BlockPos p_195593_1_, Block p_195593_2_) {
      if (net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(this, p_195593_1_, this.getBlockState(p_195593_1_), java.util.EnumSet.allOf(Direction.class), false).isCanceled())
         return;
      this.neighborChanged(p_195593_1_.west(), p_195593_2_, p_195593_1_);
      this.neighborChanged(p_195593_1_.east(), p_195593_2_, p_195593_1_);
      this.neighborChanged(p_195593_1_.below(), p_195593_2_, p_195593_1_);
      this.neighborChanged(p_195593_1_.above(), p_195593_2_, p_195593_1_);
      this.neighborChanged(p_195593_1_.north(), p_195593_2_, p_195593_1_);
      this.neighborChanged(p_195593_1_.south(), p_195593_2_, p_195593_1_);
   }

   public void updateNeighborsAtExceptFromFacing(BlockPos p_175695_1_, Block p_175695_2_, Direction p_175695_3_) {
      java.util.EnumSet<Direction> directions = java.util.EnumSet.allOf(Direction.class);
      directions.remove(p_175695_3_);
      if (net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(this, p_175695_1_, this.getBlockState(p_175695_1_), directions, false).isCanceled())
         return;

      if (p_175695_3_ != Direction.WEST) {
         this.neighborChanged(p_175695_1_.west(), p_175695_2_, p_175695_1_);
      }

      if (p_175695_3_ != Direction.EAST) {
         this.neighborChanged(p_175695_1_.east(), p_175695_2_, p_175695_1_);
      }

      if (p_175695_3_ != Direction.DOWN) {
         this.neighborChanged(p_175695_1_.below(), p_175695_2_, p_175695_1_);
      }

      if (p_175695_3_ != Direction.UP) {
         this.neighborChanged(p_175695_1_.above(), p_175695_2_, p_175695_1_);
      }

      if (p_175695_3_ != Direction.NORTH) {
         this.neighborChanged(p_175695_1_.north(), p_175695_2_, p_175695_1_);
      }

      if (p_175695_3_ != Direction.SOUTH) {
         this.neighborChanged(p_175695_1_.south(), p_175695_2_, p_175695_1_);
      }

   }

   public void neighborChanged(BlockPos p_190524_1_, Block p_190524_2_, BlockPos p_190524_3_) {
      if (!this.isClientSide) {
         BlockState blockstate = this.getBlockState(p_190524_1_);

         try {
            blockstate.neighborChanged(this, p_190524_1_, p_190524_2_, p_190524_3_, false);
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Exception while updating neighbours");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Block being updated");
            crashreportcategory.setDetail("Source block type", () -> {
               try {
                  return String.format("ID #%s (%s // %s)", p_190524_2_.getRegistryName(), p_190524_2_.getDescriptionId(), p_190524_2_.getClass().getCanonicalName());
               } catch (Throwable throwable1) {
                  return "ID #" + p_190524_2_.getRegistryName();
               }
            });
            CrashReportCategory.populateBlockDetails(crashreportcategory, p_190524_1_, blockstate);
            throw new ReportedException(crashreport);
         }
      }
   }

   public int getHeight(Heightmap.Type p_201676_1_, int p_201676_2_, int p_201676_3_) {
      int i;
      if (p_201676_2_ >= -30000000 && p_201676_3_ >= -30000000 && p_201676_2_ < 30000000 && p_201676_3_ < 30000000) {
         if (this.hasChunk(p_201676_2_ >> 4, p_201676_3_ >> 4)) {
            i = this.getChunk(p_201676_2_ >> 4, p_201676_3_ >> 4).getHeight(p_201676_1_, p_201676_2_ & 15, p_201676_3_ & 15) + 1;
         } else {
            i = 0;
         }
      } else {
         i = this.getSeaLevel() + 1;
      }

      return i;
   }

   public WorldLightManager getLightEngine() {
      return this.getChunkSource().getLightEngine();
   }

   public BlockState getBlockState(BlockPos p_180495_1_) {
      if (isOutsideBuildHeight(p_180495_1_)) {
         return Blocks.VOID_AIR.defaultBlockState();
      } else {
         Chunk chunk = this.getChunk(p_180495_1_.getX() >> 4, p_180495_1_.getZ() >> 4);
         return chunk.getBlockState(p_180495_1_);
      }
   }

   public FluidState getFluidState(BlockPos p_204610_1_) {
      if (isOutsideBuildHeight(p_204610_1_)) {
         return Fluids.EMPTY.defaultFluidState();
      } else {
         Chunk chunk = this.getChunkAt(p_204610_1_);
         return chunk.getFluidState(p_204610_1_);
      }
   }

   public boolean isDay() {
      return !this.dimensionType().hasFixedTime() && this.skyDarken < 4;
   }

   public boolean isNight() {
      return !this.dimensionType().hasFixedTime() && !this.isDay();
   }

   public void playSound(@Nullable PlayerEntity p_184133_1_, BlockPos p_184133_2_, SoundEvent p_184133_3_, SoundCategory p_184133_4_, float p_184133_5_, float p_184133_6_) {
      this.playSound(p_184133_1_, (double)p_184133_2_.getX() + 0.5D, (double)p_184133_2_.getY() + 0.5D, (double)p_184133_2_.getZ() + 0.5D, p_184133_3_, p_184133_4_, p_184133_5_, p_184133_6_);
   }

   public abstract void playSound(@Nullable PlayerEntity p_184148_1_, double p_184148_2_, double p_184148_4_, double p_184148_6_, SoundEvent p_184148_8_, SoundCategory p_184148_9_, float p_184148_10_, float p_184148_11_);

   public abstract void playSound(@Nullable PlayerEntity p_217384_1_, Entity p_217384_2_, SoundEvent p_217384_3_, SoundCategory p_217384_4_, float p_217384_5_, float p_217384_6_);

   public void playLocalSound(double p_184134_1_, double p_184134_3_, double p_184134_5_, SoundEvent p_184134_7_, SoundCategory p_184134_8_, float p_184134_9_, float p_184134_10_, boolean p_184134_11_) {
   }

   public void addParticle(IParticleData p_195594_1_, double p_195594_2_, double p_195594_4_, double p_195594_6_, double p_195594_8_, double p_195594_10_, double p_195594_12_) {
   }

   @OnlyIn(Dist.CLIENT)
   public void addParticle(IParticleData p_195590_1_, boolean p_195590_2_, double p_195590_3_, double p_195590_5_, double p_195590_7_, double p_195590_9_, double p_195590_11_, double p_195590_13_) {
   }

   public void addAlwaysVisibleParticle(IParticleData p_195589_1_, double p_195589_2_, double p_195589_4_, double p_195589_6_, double p_195589_8_, double p_195589_10_, double p_195589_12_) {
   }

   public void addAlwaysVisibleParticle(IParticleData p_217404_1_, boolean p_217404_2_, double p_217404_3_, double p_217404_5_, double p_217404_7_, double p_217404_9_, double p_217404_11_, double p_217404_13_) {
   }

   public float getSunAngle(float p_72929_1_) {
      float f = this.getTimeOfDay(p_72929_1_);
      return f * ((float)Math.PI * 2F);
   }

   public boolean addBlockEntity(TileEntity p_175700_1_) {
      if (p_175700_1_.getLevel() != this) p_175700_1_.setLevelAndPosition(this, p_175700_1_.getBlockPos()); // Forge - set the world early as vanilla doesn't set it until next tick
      if (this.updatingBlockEntities) {
         LOGGER.error("Adding block entity while ticking: {} @ {}", () -> {
            return Registry.BLOCK_ENTITY_TYPE.getKey(p_175700_1_.getType());
         }, p_175700_1_::getBlockPos);
         return pendingBlockEntities.add(p_175700_1_); // Forge: wait to add new TE if we're currently processing existing ones
      }

      boolean flag = this.blockEntityList.add(p_175700_1_);
      if (flag && p_175700_1_ instanceof ITickableTileEntity) {
         this.tickableBlockEntities.add(p_175700_1_);
      }

      p_175700_1_.onLoad();

      if (this.isClientSide) {
         BlockPos blockpos = p_175700_1_.getBlockPos();
         BlockState blockstate = this.getBlockState(blockpos);
         this.sendBlockUpdated(blockpos, blockstate, blockstate, 2);
      }

      return flag;
   }

   public void addAllPendingBlockEntities(Collection<TileEntity> p_147448_1_) {
      if (this.updatingBlockEntities) {
         p_147448_1_.stream().filter(te -> te.getLevel() != this).forEach(te -> te.setLevelAndPosition(this, te.getBlockPos())); // Forge - set the world early as vanilla doesn't set it until next tick
         this.pendingBlockEntities.addAll(p_147448_1_);
      } else {
         for(TileEntity tileentity : p_147448_1_) {
            this.addBlockEntity(tileentity);
         }
      }

   }

   public void tickBlockEntities() {
      IProfiler iprofiler = this.getProfiler();
      iprofiler.push("blockEntities");
      this.updatingBlockEntities = true;// Forge: Move above remove to prevent CMEs
      if (!this.blockEntitiesToUnload.isEmpty()) {
         this.blockEntitiesToUnload.forEach(e -> e.onChunkUnloaded());
         this.tickableBlockEntities.removeAll(this.blockEntitiesToUnload);
         this.blockEntityList.removeAll(this.blockEntitiesToUnload);
         this.blockEntitiesToUnload.clear();
      }

      Iterator<TileEntity> iterator = this.tickableBlockEntities.iterator();

      while(iterator.hasNext()) {
         TileEntity tileentity = iterator.next();
         if (!tileentity.isRemoved() && tileentity.hasLevel()) {
            BlockPos blockpos = tileentity.getBlockPos();
            if (this.getChunkSource().isTickingChunk(blockpos) && this.getWorldBorder().isWithinBounds(blockpos)) {
               try {
                  net.minecraftforge.server.timings.TimeTracker.TILE_ENTITY_UPDATE.trackStart(tileentity);
                  iprofiler.push(() -> {
                     return String.valueOf(tileentity.getType().getRegistryName());
                  });
                  if (tileentity.getType().isValid(this.getBlockState(blockpos).getBlock())) {
                     ((ITickableTileEntity)tileentity).tick();
                  } else {
                     tileentity.logInvalidState();
                  }

                  iprofiler.pop();
               } catch (Throwable throwable) {
                  CrashReport crashreport = CrashReport.forThrowable(throwable, "Ticking block entity");
                  CrashReportCategory crashreportcategory = crashreport.addCategory("Block entity being ticked");
                  tileentity.fillCrashReportCategory(crashreportcategory);
                  if (net.minecraftforge.common.ForgeConfig.SERVER.removeErroringTileEntities.get()) {
                     LogManager.getLogger().fatal("{}", crashreport.getFriendlyReport());
                     tileentity.setRemoved();
                     this.removeBlockEntity(tileentity.getBlockPos());
                  } else
                  throw new ReportedException(crashreport);
               }
               finally {
                  net.minecraftforge.server.timings.TimeTracker.TILE_ENTITY_UPDATE.trackEnd(tileentity);
               }
            }
         }

         if (tileentity.isRemoved()) {
            iterator.remove();
            this.blockEntityList.remove(tileentity);
            if (this.hasChunkAt(tileentity.getBlockPos())) {
               //Forge: Bugfix: If we set the tile entity it immediately sets it in the chunk, so we could be desyned
               Chunk chunk = this.getChunkAt(tileentity.getBlockPos());
               if (chunk.getBlockEntity(tileentity.getBlockPos(), Chunk.CreateEntityType.CHECK) == tileentity)
                  chunk.removeBlockEntity(tileentity.getBlockPos());
            }
         }
      }

      this.updatingBlockEntities = false;
      iprofiler.popPush("pendingBlockEntities");
      if (!this.pendingBlockEntities.isEmpty()) {
         for(int i = 0; i < this.pendingBlockEntities.size(); ++i) {
            TileEntity tileentity1 = this.pendingBlockEntities.get(i);
            if (!tileentity1.isRemoved()) {
               if (!this.blockEntityList.contains(tileentity1)) {
                  this.addBlockEntity(tileentity1);
               }

               if (this.hasChunkAt(tileentity1.getBlockPos())) {
                  Chunk chunk = this.getChunkAt(tileentity1.getBlockPos());
                  BlockState blockstate = chunk.getBlockState(tileentity1.getBlockPos());
                  chunk.setBlockEntity(tileentity1.getBlockPos(), tileentity1);
                  this.sendBlockUpdated(tileentity1.getBlockPos(), blockstate, blockstate, 3);
               }
            }
         }

         this.pendingBlockEntities.clear();
      }

      iprofiler.pop();
   }

   public void guardEntityTick(Consumer<Entity> p_217390_1_, Entity p_217390_2_) {
      try {
         net.minecraftforge.server.timings.TimeTracker.ENTITY_UPDATE.trackStart(p_217390_2_);
         p_217390_1_.accept(p_217390_2_);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Ticking entity");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being ticked");
         p_217390_2_.fillCrashReportCategory(crashreportcategory);
         throw new ReportedException(crashreport);
      } finally {
         net.minecraftforge.server.timings.TimeTracker.ENTITY_UPDATE.trackEnd(p_217390_2_);
      }
   }

   public Explosion explode(@Nullable Entity p_217385_1_, double p_217385_2_, double p_217385_4_, double p_217385_6_, float p_217385_8_, Explosion.Mode p_217385_9_) {
      return this.explode(p_217385_1_, (DamageSource)null, (ExplosionContext)null, p_217385_2_, p_217385_4_, p_217385_6_, p_217385_8_, false, p_217385_9_);
   }

   public Explosion explode(@Nullable Entity p_217398_1_, double p_217398_2_, double p_217398_4_, double p_217398_6_, float p_217398_8_, boolean p_217398_9_, Explosion.Mode p_217398_10_) {
      return this.explode(p_217398_1_, (DamageSource)null, (ExplosionContext)null, p_217398_2_, p_217398_4_, p_217398_6_, p_217398_8_, p_217398_9_, p_217398_10_);
   }

   public Explosion explode(@Nullable Entity p_230546_1_, @Nullable DamageSource p_230546_2_, @Nullable ExplosionContext p_230546_3_, double p_230546_4_, double p_230546_6_, double p_230546_8_, float p_230546_10_, boolean p_230546_11_, Explosion.Mode p_230546_12_) {
      Explosion explosion = new Explosion(this, p_230546_1_, p_230546_2_, p_230546_3_, p_230546_4_, p_230546_6_, p_230546_8_, p_230546_10_, p_230546_11_, p_230546_12_);
      if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(this, explosion)) return explosion;
      explosion.explode();
      explosion.finalizeExplosion(true);
      return explosion;
   }

   public String gatherChunkSourceStats() {
      return this.getChunkSource().gatherStats();
   }

   @Nullable
   public TileEntity getBlockEntity(BlockPos p_175625_1_) {
      if (isOutsideBuildHeight(p_175625_1_)) {
         return null;
      } else if (!this.isClientSide && Thread.currentThread() != this.thread) {
         return null;
      } else {
         TileEntity tileentity = null;
         if (this.updatingBlockEntities) {
            tileentity = this.getPendingBlockEntityAt(p_175625_1_);
         }

         if (tileentity == null) {
            tileentity = this.getChunkAt(p_175625_1_).getBlockEntity(p_175625_1_, Chunk.CreateEntityType.IMMEDIATE);
         }

         if (tileentity == null) {
            tileentity = this.getPendingBlockEntityAt(p_175625_1_);
         }

         return tileentity;
      }
   }

   @Nullable
   private TileEntity getPendingBlockEntityAt(BlockPos p_189508_1_) {
      for(int i = 0; i < this.pendingBlockEntities.size(); ++i) {
         TileEntity tileentity = this.pendingBlockEntities.get(i);
         if (!tileentity.isRemoved() && tileentity.getBlockPos().equals(p_189508_1_)) {
            return tileentity;
         }
      }

      return null;
   }

   public void setBlockEntity(BlockPos p_175690_1_, @Nullable TileEntity p_175690_2_) {
      if (!isOutsideBuildHeight(p_175690_1_)) {
         p_175690_1_ = p_175690_1_.immutable(); // Forge - prevent mutable BlockPos leaks
         if (p_175690_2_ != null && !p_175690_2_.isRemoved()) {
            if (this.updatingBlockEntities) {
               p_175690_2_.setLevelAndPosition(this, p_175690_1_);
               Iterator<TileEntity> iterator = this.pendingBlockEntities.iterator();

               while(iterator.hasNext()) {
                  TileEntity tileentity = iterator.next();
                  if (tileentity.getBlockPos().equals(p_175690_1_)) {
                     tileentity.setRemoved();
                     iterator.remove();
                  }
               }

               this.pendingBlockEntities.add(p_175690_2_);
            } else {
               Chunk chunk = this.getChunkAt(p_175690_1_);
               if (chunk != null) chunk.setBlockEntity(p_175690_1_, p_175690_2_);
               this.addBlockEntity(p_175690_2_);
            }
         }

      }
   }

   public void removeBlockEntity(BlockPos p_175713_1_) {
      TileEntity tileentity = this.getBlockEntity(p_175713_1_);
      if (tileentity != null && this.updatingBlockEntities) {
         tileentity.setRemoved();
         this.pendingBlockEntities.remove(tileentity);
         if (!(tileentity instanceof ITickableTileEntity)) //Forge: If they are not tickable they wont be removed in the update loop.
            this.blockEntityList.remove(tileentity);
      } else {
         if (tileentity != null) {
            this.pendingBlockEntities.remove(tileentity);
            this.blockEntityList.remove(tileentity);
            this.tickableBlockEntities.remove(tileentity);
         }

         this.getChunkAt(p_175713_1_).removeBlockEntity(p_175713_1_);
      }
      this.updateNeighbourForOutputSignal(p_175713_1_, getBlockState(p_175713_1_).getBlock()); //Notify neighbors of changes
   }

   public boolean isLoaded(BlockPos p_195588_1_) {
      return isOutsideBuildHeight(p_195588_1_) ? false : this.getChunkSource().hasChunk(p_195588_1_.getX() >> 4, p_195588_1_.getZ() >> 4);
   }

   public boolean loadedAndEntityCanStandOnFace(BlockPos p_234929_1_, Entity p_234929_2_, Direction p_234929_3_) {
      if (isOutsideBuildHeight(p_234929_1_)) {
         return false;
      } else {
         IChunk ichunk = this.getChunk(p_234929_1_.getX() >> 4, p_234929_1_.getZ() >> 4, ChunkStatus.FULL, false);
         return ichunk == null ? false : ichunk.getBlockState(p_234929_1_).entityCanStandOnFace(this, p_234929_1_, p_234929_2_, p_234929_3_);
      }
   }

   public boolean loadedAndEntityCanStandOn(BlockPos p_217400_1_, Entity p_217400_2_) {
      return this.loadedAndEntityCanStandOnFace(p_217400_1_, p_217400_2_, Direction.UP);
   }

   public void updateSkyBrightness() {
      double d0 = 1.0D - (double)(this.getRainLevel(1.0F) * 5.0F) / 16.0D;
      double d1 = 1.0D - (double)(this.getThunderLevel(1.0F) * 5.0F) / 16.0D;
      double d2 = 0.5D + 2.0D * MathHelper.clamp((double)MathHelper.cos(this.getTimeOfDay(1.0F) * ((float)Math.PI * 2F)), -0.25D, 0.25D);
      this.skyDarken = (int)((1.0D - d2 * d0 * d1) * 11.0D);
   }

   public void setSpawnSettings(boolean p_72891_1_, boolean p_72891_2_) {
      this.getChunkSource().setSpawnSettings(p_72891_1_, p_72891_2_);
   }

   protected void prepareWeather() {
      if (this.levelData.isRaining()) {
         this.rainLevel = 1.0F;
         if (this.levelData.isThundering()) {
            this.thunderLevel = 1.0F;
         }
      }

   }

   public void close() throws IOException {
      this.getChunkSource().close();
   }

   @Nullable
   public IBlockReader getChunkForCollisions(int p_225522_1_, int p_225522_2_) {
      return this.getChunk(p_225522_1_, p_225522_2_, ChunkStatus.FULL, false);
   }

   public List<Entity> getEntities(@Nullable Entity p_175674_1_, AxisAlignedBB p_175674_2_, @Nullable Predicate<? super Entity> p_175674_3_) {
      this.getProfiler().incrementCounter("getEntities");
      List<Entity> list = Lists.newArrayList();
      int i = MathHelper.floor((p_175674_2_.minX - getMaxEntityRadius()) / 16.0D);
      int j = MathHelper.floor((p_175674_2_.maxX + getMaxEntityRadius()) / 16.0D);
      int k = MathHelper.floor((p_175674_2_.minZ - getMaxEntityRadius()) / 16.0D);
      int l = MathHelper.floor((p_175674_2_.maxZ + getMaxEntityRadius()) / 16.0D);
      AbstractChunkProvider abstractchunkprovider = this.getChunkSource();

      for(int i1 = i; i1 <= j; ++i1) {
         for(int j1 = k; j1 <= l; ++j1) {
            Chunk chunk = abstractchunkprovider.getChunk(i1, j1, false);
            if (chunk != null) {
               chunk.getEntities(p_175674_1_, p_175674_2_, list, p_175674_3_);
            }
         }
      }

      return list;
   }

   public <T extends Entity> List<T> getEntities(@Nullable EntityType<T> p_217394_1_, AxisAlignedBB p_217394_2_, Predicate<? super T> p_217394_3_) {
      this.getProfiler().incrementCounter("getEntities");
      int i = MathHelper.floor((p_217394_2_.minX - getMaxEntityRadius()) / 16.0D);
      int j = MathHelper.ceil((p_217394_2_.maxX + getMaxEntityRadius()) / 16.0D);
      int k = MathHelper.floor((p_217394_2_.minZ - getMaxEntityRadius()) / 16.0D);
      int l = MathHelper.ceil((p_217394_2_.maxZ + getMaxEntityRadius()) / 16.0D);
      List<T> list = Lists.newArrayList();

      for(int i1 = i; i1 < j; ++i1) {
         for(int j1 = k; j1 < l; ++j1) {
            Chunk chunk = this.getChunkSource().getChunk(i1, j1, false);
            if (chunk != null) {
               chunk.getEntities(p_217394_1_, p_217394_2_, list, p_217394_3_);
            }
         }
      }

      return list;
   }

   public <T extends Entity> List<T> getEntitiesOfClass(Class<? extends T> p_175647_1_, AxisAlignedBB p_175647_2_, @Nullable Predicate<? super T> p_175647_3_) {
      this.getProfiler().incrementCounter("getEntities");
      int i = MathHelper.floor((p_175647_2_.minX - getMaxEntityRadius()) / 16.0D);
      int j = MathHelper.ceil((p_175647_2_.maxX + getMaxEntityRadius()) / 16.0D);
      int k = MathHelper.floor((p_175647_2_.minZ - getMaxEntityRadius()) / 16.0D);
      int l = MathHelper.ceil((p_175647_2_.maxZ + getMaxEntityRadius()) / 16.0D);
      List<T> list = Lists.newArrayList();
      AbstractChunkProvider abstractchunkprovider = this.getChunkSource();

      for(int i1 = i; i1 < j; ++i1) {
         for(int j1 = k; j1 < l; ++j1) {
            Chunk chunk = abstractchunkprovider.getChunk(i1, j1, false);
            if (chunk != null) {
               chunk.getEntitiesOfClass(p_175647_1_, p_175647_2_, list, p_175647_3_);
            }
         }
      }

      return list;
   }

   public <T extends Entity> List<T> getLoadedEntitiesOfClass(Class<? extends T> p_225316_1_, AxisAlignedBB p_225316_2_, @Nullable Predicate<? super T> p_225316_3_) {
      this.getProfiler().incrementCounter("getLoadedEntities");
      int i = MathHelper.floor((p_225316_2_.minX - getMaxEntityRadius()) / 16.0D);
      int j = MathHelper.ceil((p_225316_2_.maxX + getMaxEntityRadius()) / 16.0D);
      int k = MathHelper.floor((p_225316_2_.minZ - getMaxEntityRadius()) / 16.0D);
      int l = MathHelper.ceil((p_225316_2_.maxZ + getMaxEntityRadius()) / 16.0D);
      List<T> list = Lists.newArrayList();
      AbstractChunkProvider abstractchunkprovider = this.getChunkSource();

      for(int i1 = i; i1 < j; ++i1) {
         for(int j1 = k; j1 < l; ++j1) {
            Chunk chunk = abstractchunkprovider.getChunkNow(i1, j1);
            if (chunk != null) {
               chunk.getEntitiesOfClass(p_225316_1_, p_225316_2_, list, p_225316_3_);
            }
         }
      }

      return list;
   }

   @Nullable
   public abstract Entity getEntity(int p_73045_1_);

   public void blockEntityChanged(BlockPos p_175646_1_, TileEntity p_175646_2_) {
      if (this.hasChunkAt(p_175646_1_)) {
         this.getChunkAt(p_175646_1_).markUnsaved();
      }

      this.updateNeighbourForOutputSignal(p_175646_1_, getBlockState(p_175646_1_).getBlock()); //Notify neighbors of changes
   }

   public int getSeaLevel() {
      return 63;
   }

   public int getDirectSignalTo(BlockPos p_175676_1_) {
      int i = 0;
      i = Math.max(i, this.getDirectSignal(p_175676_1_.below(), Direction.DOWN));
      if (i >= 15) {
         return i;
      } else {
         i = Math.max(i, this.getDirectSignal(p_175676_1_.above(), Direction.UP));
         if (i >= 15) {
            return i;
         } else {
            i = Math.max(i, this.getDirectSignal(p_175676_1_.north(), Direction.NORTH));
            if (i >= 15) {
               return i;
            } else {
               i = Math.max(i, this.getDirectSignal(p_175676_1_.south(), Direction.SOUTH));
               if (i >= 15) {
                  return i;
               } else {
                  i = Math.max(i, this.getDirectSignal(p_175676_1_.west(), Direction.WEST));
                  if (i >= 15) {
                     return i;
                  } else {
                     i = Math.max(i, this.getDirectSignal(p_175676_1_.east(), Direction.EAST));
                     return i >= 15 ? i : i;
                  }
               }
            }
         }
      }
   }

   public boolean hasSignal(BlockPos p_175709_1_, Direction p_175709_2_) {
      return this.getSignal(p_175709_1_, p_175709_2_) > 0;
   }

   public int getSignal(BlockPos p_175651_1_, Direction p_175651_2_) {
      BlockState blockstate = this.getBlockState(p_175651_1_);
      int i = blockstate.getSignal(this, p_175651_1_, p_175651_2_);
      return blockstate.shouldCheckWeakPower(this, p_175651_1_, p_175651_2_) ? Math.max(i, this.getDirectSignalTo(p_175651_1_)) : i;
   }

   public boolean hasNeighborSignal(BlockPos p_175640_1_) {
      if (this.getSignal(p_175640_1_.below(), Direction.DOWN) > 0) {
         return true;
      } else if (this.getSignal(p_175640_1_.above(), Direction.UP) > 0) {
         return true;
      } else if (this.getSignal(p_175640_1_.north(), Direction.NORTH) > 0) {
         return true;
      } else if (this.getSignal(p_175640_1_.south(), Direction.SOUTH) > 0) {
         return true;
      } else if (this.getSignal(p_175640_1_.west(), Direction.WEST) > 0) {
         return true;
      } else {
         return this.getSignal(p_175640_1_.east(), Direction.EAST) > 0;
      }
   }

   public int getBestNeighborSignal(BlockPos p_175687_1_) {
      int i = 0;

      for(Direction direction : DIRECTIONS) {
         int j = this.getSignal(p_175687_1_.relative(direction), direction);
         if (j >= 15) {
            return 15;
         }

         if (j > i) {
            i = j;
         }
      }

      return i;
   }

   @OnlyIn(Dist.CLIENT)
   public void disconnect() {
   }

   public long getGameTime() {
      return this.levelData.getGameTime();
   }

   public long getDayTime() {
      return this.levelData.getDayTime();
   }

   public boolean mayInteract(PlayerEntity p_175660_1_, BlockPos p_175660_2_) {
      return true;
   }

   public void broadcastEntityEvent(Entity p_72960_1_, byte p_72960_2_) {
   }

   public void blockEvent(BlockPos p_175641_1_, Block p_175641_2_, int p_175641_3_, int p_175641_4_) {
      this.getBlockState(p_175641_1_).triggerEvent(this, p_175641_1_, p_175641_3_, p_175641_4_);
   }

   public IWorldInfo getLevelData() {
      return this.levelData;
   }

   public GameRules getGameRules() {
      return this.levelData.getGameRules();
   }

   public float getThunderLevel(float p_72819_1_) {
      return MathHelper.lerp(p_72819_1_, this.oThunderLevel, this.thunderLevel) * this.getRainLevel(p_72819_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void setThunderLevel(float p_147442_1_) {
      this.oThunderLevel = p_147442_1_;
      this.thunderLevel = p_147442_1_;
   }

   public float getRainLevel(float p_72867_1_) {
      return MathHelper.lerp(p_72867_1_, this.oRainLevel, this.rainLevel);
   }

   @OnlyIn(Dist.CLIENT)
   public void setRainLevel(float p_72894_1_) {
      this.oRainLevel = p_72894_1_;
      this.rainLevel = p_72894_1_;
   }

   public boolean isThundering() {
      if (this.dimensionType().hasSkyLight() && !this.dimensionType().hasCeiling()) {
         return (double)this.getThunderLevel(1.0F) > 0.9D;
      } else {
         return false;
      }
   }

   public boolean isRaining() {
      return (double)this.getRainLevel(1.0F) > 0.2D;
   }

   public boolean isRainingAt(BlockPos p_175727_1_) {
      if (!this.isRaining()) {
         return false;
      } else if (!this.canSeeSky(p_175727_1_)) {
         return false;
      } else if (this.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, p_175727_1_).getY() > p_175727_1_.getY()) {
         return false;
      } else {
         Biome biome = this.getBiome(p_175727_1_);
         return biome.getPrecipitation() == Biome.RainType.RAIN && biome.getTemperature(p_175727_1_) >= 0.15F;
      }
   }

   public boolean isHumidAt(BlockPos p_180502_1_) {
      Biome biome = this.getBiome(p_180502_1_);
      return biome.isHumid();
   }

   @Nullable
   public abstract MapData getMapData(String p_217406_1_);

   public abstract void setMapData(MapData p_217399_1_);

   public abstract int getFreeMapId();

   public void globalLevelEvent(int p_175669_1_, BlockPos p_175669_2_, int p_175669_3_) {
   }

   public CrashReportCategory fillReportDetails(CrashReport p_72914_1_) {
      CrashReportCategory crashreportcategory = p_72914_1_.addCategory("Affected level", 1);
      crashreportcategory.setDetail("All players", () -> {
         return this.players().size() + " total; " + this.players();
      });
      crashreportcategory.setDetail("Chunk stats", this.getChunkSource()::gatherStats);
      crashreportcategory.setDetail("Level dimension", () -> {
         return this.dimension().location().toString();
      });

      try {
         this.levelData.fillCrashReportCategory(crashreportcategory);
      } catch (Throwable throwable) {
         crashreportcategory.setDetailError("Level Data Unobtainable", throwable);
      }

      return crashreportcategory;
   }

   public abstract void destroyBlockProgress(int p_175715_1_, BlockPos p_175715_2_, int p_175715_3_);

   @OnlyIn(Dist.CLIENT)
   public void createFireworks(double p_92088_1_, double p_92088_3_, double p_92088_5_, double p_92088_7_, double p_92088_9_, double p_92088_11_, @Nullable CompoundNBT p_92088_13_) {
   }

   public abstract Scoreboard getScoreboard();

   public void updateNeighbourForOutputSignal(BlockPos p_175666_1_, Block p_175666_2_) {
      for(Direction direction : Direction.values()) {
         BlockPos blockpos = p_175666_1_.relative(direction);
         if (this.hasChunkAt(blockpos)) {
            BlockState blockstate = this.getBlockState(blockpos);
            blockstate.onNeighborChange(this, blockpos, p_175666_1_);
            if (blockstate.isRedstoneConductor(this, blockpos)) {
               blockpos = blockpos.relative(direction);
               blockstate = this.getBlockState(blockpos);
               if (blockstate.getWeakChanges(this, blockpos)) {
                  blockstate.neighborChanged(this, blockpos, p_175666_2_, p_175666_1_, false);
               }
            }
         }
      }

   }

   public DifficultyInstance getCurrentDifficultyAt(BlockPos p_175649_1_) {
      long i = 0L;
      float f = 0.0F;
      if (this.hasChunkAt(p_175649_1_)) {
         f = this.getMoonBrightness();
         i = this.getChunkAt(p_175649_1_).getInhabitedTime();
      }

      return new DifficultyInstance(this.getDifficulty(), this.getDayTime(), i, f);
   }

   public int getSkyDarken() {
      return this.skyDarken;
   }

   public void setSkyFlashTime(int p_225605_1_) {
   }

   public WorldBorder getWorldBorder() {
      return this.worldBorder;
   }

   public void sendPacketToServer(IPacket<?> p_184135_1_) {
      throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
   }

   public DimensionType dimensionType() {
      return this.dimensionType;
   }

   public RegistryKey<World> dimension() {
      return this.dimension;
   }

   public Random getRandom() {
      return this.random;
   }

   public boolean isStateAtPosition(BlockPos p_217375_1_, Predicate<BlockState> p_217375_2_) {
      return p_217375_2_.test(this.getBlockState(p_217375_1_));
   }

   public abstract RecipeManager getRecipeManager();

   public abstract ITagCollectionSupplier getTagManager();

   public BlockPos getBlockRandomPos(int p_217383_1_, int p_217383_2_, int p_217383_3_, int p_217383_4_) {
      this.randValue = this.randValue * 3 + 1013904223;
      int i = this.randValue >> 2;
      return new BlockPos(p_217383_1_ + (i & 15), p_217383_2_ + (i >> 16 & p_217383_4_), p_217383_3_ + (i >> 8 & 15));
   }

   public boolean noSave() {
      return false;
   }

   public IProfiler getProfiler() {
      return this.profiler.get();
   }

   public Supplier<IProfiler> getProfilerSupplier() {
      return this.profiler;
   }

   public BiomeManager getBiomeManager() {
      return this.biomeManager;
   }

   private double maxEntityRadius = 2.0D;
   @Override
   public double getMaxEntityRadius() {
      return maxEntityRadius;
   }
   @Override
   public double increaseMaxEntityRadius(double value) {
      if (value > maxEntityRadius)
         maxEntityRadius = value;
      return maxEntityRadius;
   }

   public final boolean isDebug() {
      return this.isDebug;
   }
}
