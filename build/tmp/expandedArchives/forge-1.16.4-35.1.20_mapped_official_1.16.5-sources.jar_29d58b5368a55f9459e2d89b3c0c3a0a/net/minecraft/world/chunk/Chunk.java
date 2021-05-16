package net.minecraft.world.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.EmptyTickList;
import net.minecraft.world.ITickList;
import net.minecraft.world.SerializableTickList;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.gen.DebugChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk extends net.minecraftforge.common.capabilities.CapabilityProvider<Chunk> implements IChunk, net.minecraftforge.common.extensions.IForgeChunk {
   private static final Logger LOGGER = LogManager.getLogger();
   @Nullable
   public static final ChunkSection EMPTY_SECTION = null;
   private final ChunkSection[] sections = new ChunkSection[16];
   private BiomeContainer biomes;
   private final Map<BlockPos, CompoundNBT> pendingBlockEntities = Maps.newHashMap();
   private boolean loaded;
   private final World level;
   private final Map<Heightmap.Type, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Type.class);
   private final UpgradeData upgradeData;
   private final Map<BlockPos, TileEntity> blockEntities = Maps.newHashMap();
   private final ClassInheritanceMultiMap<Entity>[] entitySections;
   private final Map<Structure<?>, StructureStart<?>> structureStarts = Maps.newHashMap();
   private final Map<Structure<?>, LongSet> structuresRefences = Maps.newHashMap();
   private final ShortList[] postProcessing = new ShortList[16];
   private ITickList<Block> blockTicks;
   private ITickList<Fluid> liquidTicks;
   private boolean lastSaveHadEntities;
   private long lastSaveTime;
   private volatile boolean unsaved;
   private long inhabitedTime;
   @Nullable
   private Supplier<ChunkHolder.LocationType> fullStatus;
   @Nullable
   private Consumer<Chunk> postLoad;
   private final ChunkPos chunkPos;
   private volatile boolean isLightCorrect;

   public Chunk(World p_i225780_1_, ChunkPos p_i225780_2_, BiomeContainer p_i225780_3_) {
      this(p_i225780_1_, p_i225780_2_, p_i225780_3_, UpgradeData.EMPTY, EmptyTickList.empty(), EmptyTickList.empty(), 0L, (ChunkSection[])null, (Consumer<Chunk>)null);
   }

   public Chunk(World p_i225781_1_, ChunkPos p_i225781_2_, BiomeContainer p_i225781_3_, UpgradeData p_i225781_4_, ITickList<Block> p_i225781_5_, ITickList<Fluid> p_i225781_6_, long p_i225781_7_, @Nullable ChunkSection[] p_i225781_9_, @Nullable Consumer<Chunk> p_i225781_10_) {
      super(Chunk.class);
      this.entitySections = new ClassInheritanceMultiMap[16];
      this.level = p_i225781_1_;
      this.chunkPos = p_i225781_2_;
      this.upgradeData = p_i225781_4_;

      for(Heightmap.Type heightmap$type : Heightmap.Type.values()) {
         if (ChunkStatus.FULL.heightmapsAfter().contains(heightmap$type)) {
            this.heightmaps.put(heightmap$type, new Heightmap(this, heightmap$type));
         }
      }

      for(int i = 0; i < this.entitySections.length; ++i) {
         this.entitySections[i] = new ClassInheritanceMultiMap<>(Entity.class);
      }

      this.biomes = p_i225781_3_;
      this.blockTicks = p_i225781_5_;
      this.liquidTicks = p_i225781_6_;
      this.inhabitedTime = p_i225781_7_;
      this.postLoad = p_i225781_10_;
      if (p_i225781_9_ != null) {
         if (this.sections.length == p_i225781_9_.length) {
            System.arraycopy(p_i225781_9_, 0, this.sections, 0, this.sections.length);
         } else {
            LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", p_i225781_9_.length, this.sections.length);
         }
      }
      this.gatherCapabilities();
   }

   public Chunk(World p_i49947_1_, ChunkPrimer p_i49947_2_) {
      this(p_i49947_1_, p_i49947_2_.getPos(), p_i49947_2_.getBiomes(), p_i49947_2_.getUpgradeData(), p_i49947_2_.getBlockTicks(), p_i49947_2_.getLiquidTicks(), p_i49947_2_.getInhabitedTime(), p_i49947_2_.getSections(), (Consumer<Chunk>)null);

      for(CompoundNBT compoundnbt : p_i49947_2_.getEntities()) {
         EntityType.loadEntityRecursive(compoundnbt, p_i49947_1_, (p_217325_1_) -> {
            this.addEntity(p_217325_1_);
            return p_217325_1_;
         });
      }

      for(TileEntity tileentity : p_i49947_2_.getBlockEntities().values()) {
         this.addBlockEntity(tileentity);
      }

      this.pendingBlockEntities.putAll(p_i49947_2_.getBlockEntityNbts());

      for(int i = 0; i < p_i49947_2_.getPostProcessing().length; ++i) {
         this.postProcessing[i] = p_i49947_2_.getPostProcessing()[i];
      }

      this.setAllStarts(p_i49947_2_.getAllStarts());
      this.setAllReferences(p_i49947_2_.getAllReferences());

      for(Entry<Heightmap.Type, Heightmap> entry : p_i49947_2_.getHeightmaps()) {
         if (ChunkStatus.FULL.heightmapsAfter().contains(entry.getKey())) {
            this.getOrCreateHeightmapUnprimed(entry.getKey()).setRawData(entry.getValue().getRawData());
         }
      }

      this.setLightCorrect(p_i49947_2_.isLightCorrect());
      this.unsaved = true;
   }

   public Heightmap getOrCreateHeightmapUnprimed(Heightmap.Type p_217303_1_) {
      return this.heightmaps.computeIfAbsent(p_217303_1_, (p_217319_1_) -> {
         return new Heightmap(this, p_217319_1_);
      });
   }

   public Set<BlockPos> getBlockEntitiesPos() {
      Set<BlockPos> set = Sets.newHashSet(this.pendingBlockEntities.keySet());
      set.addAll(this.blockEntities.keySet());
      return set;
   }

   public ChunkSection[] getSections() {
      return this.sections;
   }

   public BlockState getBlockState(BlockPos p_180495_1_) {
      int i = p_180495_1_.getX();
      int j = p_180495_1_.getY();
      int k = p_180495_1_.getZ();
      if (this.level.isDebug()) {
         BlockState blockstate = null;
         if (j == 60) {
            blockstate = Blocks.BARRIER.defaultBlockState();
         }

         if (j == 70) {
            blockstate = DebugChunkGenerator.getBlockStateFor(i, k);
         }

         return blockstate == null ? Blocks.AIR.defaultBlockState() : blockstate;
      } else {
         try {
            if (j >= 0 && j >> 4 < this.sections.length) {
               ChunkSection chunksection = this.sections[j >> 4];
               if (!ChunkSection.isEmpty(chunksection)) {
                  return chunksection.getBlockState(i & 15, j & 15, k & 15);
               }
            }

            return Blocks.AIR.defaultBlockState();
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Getting block state");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Block being got");
            crashreportcategory.setDetail("Location", () -> {
               return CrashReportCategory.formatLocation(i, j, k);
            });
            throw new ReportedException(crashreport);
         }
      }
   }

   public FluidState getFluidState(BlockPos p_204610_1_) {
      return this.getFluidState(p_204610_1_.getX(), p_204610_1_.getY(), p_204610_1_.getZ());
   }

   public FluidState getFluidState(int p_205751_1_, int p_205751_2_, int p_205751_3_) {
      try {
         if (p_205751_2_ >= 0 && p_205751_2_ >> 4 < this.sections.length) {
            ChunkSection chunksection = this.sections[p_205751_2_ >> 4];
            if (!ChunkSection.isEmpty(chunksection)) {
               return chunksection.getFluidState(p_205751_1_ & 15, p_205751_2_ & 15, p_205751_3_ & 15);
            }
         }

         return Fluids.EMPTY.defaultFluidState();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Getting fluid state");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Block being got");
         crashreportcategory.setDetail("Location", () -> {
            return CrashReportCategory.formatLocation(p_205751_1_, p_205751_2_, p_205751_3_);
         });
         throw new ReportedException(crashreport);
      }
   }

   @Nullable
   public BlockState setBlockState(BlockPos p_177436_1_, BlockState p_177436_2_, boolean p_177436_3_) {
      int i = p_177436_1_.getX() & 15;
      int j = p_177436_1_.getY();
      int k = p_177436_1_.getZ() & 15;
      ChunkSection chunksection = this.sections[j >> 4];
      if (chunksection == EMPTY_SECTION) {
         if (p_177436_2_.isAir()) {
            return null;
         }

         chunksection = new ChunkSection(j >> 4 << 4);
         this.sections[j >> 4] = chunksection;
      }

      boolean flag = chunksection.isEmpty();
      BlockState blockstate = chunksection.setBlockState(i, j & 15, k, p_177436_2_);
      if (blockstate == p_177436_2_) {
         return null;
      } else {
         Block block = p_177436_2_.getBlock();
         Block block1 = blockstate.getBlock();
         this.heightmaps.get(Heightmap.Type.MOTION_BLOCKING).update(i, j, k, p_177436_2_);
         this.heightmaps.get(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES).update(i, j, k, p_177436_2_);
         this.heightmaps.get(Heightmap.Type.OCEAN_FLOOR).update(i, j, k, p_177436_2_);
         this.heightmaps.get(Heightmap.Type.WORLD_SURFACE).update(i, j, k, p_177436_2_);
         boolean flag1 = chunksection.isEmpty();
         if (flag != flag1) {
            this.level.getChunkSource().getLightEngine().updateSectionStatus(p_177436_1_, flag1);
         }

         if (!this.level.isClientSide) {
            blockstate.onRemove(this.level, p_177436_1_, p_177436_2_, p_177436_3_);
         } else if ((block1 != block || !p_177436_2_.hasTileEntity()) && blockstate.hasTileEntity()) {
            this.level.removeBlockEntity(p_177436_1_);
         }

         if (!chunksection.getBlockState(i, j & 15, k).is(block)) {
            return null;
         } else {
            if (blockstate.hasTileEntity()) {
               TileEntity tileentity = this.getBlockEntity(p_177436_1_, Chunk.CreateEntityType.CHECK);
               if (tileentity != null) {
                  tileentity.clearCache();
               }
            }

            if (!this.level.isClientSide) {
               p_177436_2_.onPlace(this.level, p_177436_1_, blockstate, p_177436_3_);
            }

            if (p_177436_2_.hasTileEntity()) {
               TileEntity tileentity1 = this.getBlockEntity(p_177436_1_, Chunk.CreateEntityType.CHECK);
               if (tileentity1 == null) {
                  tileentity1 = p_177436_2_.createTileEntity(this.level);
                  this.level.setBlockEntity(p_177436_1_, tileentity1);
               } else {
                  tileentity1.clearCache();
               }
            }

            this.unsaved = true;
            return blockstate;
         }
      }
   }

   @Nullable
   public WorldLightManager getLightEngine() {
      return this.level.getChunkSource().getLightEngine();
   }

   public void addEntity(Entity p_76612_1_) {
      this.lastSaveHadEntities = true;
      int i = MathHelper.floor(p_76612_1_.getX() / 16.0D);
      int j = MathHelper.floor(p_76612_1_.getZ() / 16.0D);
      if (i != this.chunkPos.x || j != this.chunkPos.z) {
         LOGGER.warn("Wrong location! ({}, {}) should be ({}, {}), {}", i, j, this.chunkPos.x, this.chunkPos.z, p_76612_1_);
         p_76612_1_.removed = true;
      }

      int k = MathHelper.floor(p_76612_1_.getY() / 16.0D);
      if (k < 0) {
         k = 0;
      }

      if (k >= this.entitySections.length) {
         k = this.entitySections.length - 1;
      }

      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityEvent.EnteringChunk(p_76612_1_, this.chunkPos.x, this.chunkPos.z, p_76612_1_.xChunk, p_76612_1_.zChunk));
      p_76612_1_.inChunk = true;
      p_76612_1_.xChunk = this.chunkPos.x;
      p_76612_1_.yChunk = k;
      p_76612_1_.zChunk = this.chunkPos.z;
      this.entitySections[k].add(p_76612_1_);
      this.markUnsaved(); // Forge - ensure chunks are marked to save after an entity add
   }

   public void setHeightmap(Heightmap.Type p_201607_1_, long[] p_201607_2_) {
      this.heightmaps.get(p_201607_1_).setRawData(p_201607_2_);
   }

   public void removeEntity(Entity p_76622_1_) {
      this.removeEntity(p_76622_1_, p_76622_1_.yChunk);
   }

   public void removeEntity(Entity p_76608_1_, int p_76608_2_) {
      if (p_76608_2_ < 0) {
         p_76608_2_ = 0;
      }

      if (p_76608_2_ >= this.entitySections.length) {
         p_76608_2_ = this.entitySections.length - 1;
      }

      this.entitySections[p_76608_2_].remove(p_76608_1_);
      this.markUnsaved(); // Forge - ensure chunks are marked to save after entity removals
   }

   public int getHeight(Heightmap.Type p_201576_1_, int p_201576_2_, int p_201576_3_) {
      return this.heightmaps.get(p_201576_1_).getFirstAvailable(p_201576_2_ & 15, p_201576_3_ & 15) - 1;
   }

   @Nullable
   private TileEntity createBlockEntity(BlockPos p_177422_1_) {
      BlockState blockstate = this.getBlockState(p_177422_1_);
      Block block = blockstate.getBlock();
      return !blockstate.hasTileEntity() ? null : blockstate.createTileEntity(this.level);
   }

   @Nullable
   public TileEntity getBlockEntity(BlockPos p_175625_1_) {
      return this.getBlockEntity(p_175625_1_, Chunk.CreateEntityType.CHECK);
   }

   @Nullable
   public TileEntity getBlockEntity(BlockPos p_177424_1_, Chunk.CreateEntityType p_177424_2_) {
      TileEntity tileentity = this.blockEntities.get(p_177424_1_);
      if (tileentity != null && tileentity.isRemoved()) {
         blockEntities.remove(p_177424_1_);
         tileentity = null;
      }
      if (tileentity == null) {
         CompoundNBT compoundnbt = this.pendingBlockEntities.remove(p_177424_1_);
         if (compoundnbt != null) {
            TileEntity tileentity1 = this.promotePendingBlockEntity(p_177424_1_, compoundnbt);
            if (tileentity1 != null) {
               return tileentity1;
            }
         }
      }

      if (tileentity == null) {
         if (p_177424_2_ == Chunk.CreateEntityType.IMMEDIATE) {
            tileentity = this.createBlockEntity(p_177424_1_);
            this.level.setBlockEntity(p_177424_1_, tileentity);
         }
      }

      return tileentity;
   }

   public void addBlockEntity(TileEntity p_150813_1_) {
      this.setBlockEntity(p_150813_1_.getBlockPos(), p_150813_1_);
      if (this.loaded || this.level.isClientSide()) {
         this.level.setBlockEntity(p_150813_1_.getBlockPos(), p_150813_1_);
      }

   }

   public void setBlockEntity(BlockPos p_177426_1_, TileEntity p_177426_2_) {
      if (this.getBlockState(p_177426_1_).hasTileEntity()) {
         p_177426_2_.setLevelAndPosition(this.level, p_177426_1_);
         p_177426_2_.clearRemoved();
         TileEntity tileentity = this.blockEntities.put(p_177426_1_.immutable(), p_177426_2_);
         if (tileentity != null && tileentity != p_177426_2_) {
            tileentity.setRemoved();
         }

      }
   }

   public void setBlockEntityNbt(CompoundNBT p_201591_1_) {
      this.pendingBlockEntities.put(new BlockPos(p_201591_1_.getInt("x"), p_201591_1_.getInt("y"), p_201591_1_.getInt("z")), p_201591_1_);
   }

   @Nullable
   public CompoundNBT getBlockEntityNbtForSaving(BlockPos p_223134_1_) {
      TileEntity tileentity = this.getBlockEntity(p_223134_1_);
      if (tileentity != null && !tileentity.isRemoved()) {
         try {
         CompoundNBT compoundnbt1 = tileentity.save(new CompoundNBT());
         compoundnbt1.putBoolean("keepPacked", false);
         return compoundnbt1;
         } catch (Exception e) {
            LogManager.getLogger().error("A TileEntity type {} has thrown an exception trying to write state. It will not persist, Report this to the mod author", tileentity.getClass().getName(), e);
            return null;
         }
      } else {
         CompoundNBT compoundnbt = this.pendingBlockEntities.get(p_223134_1_);
         if (compoundnbt != null) {
            compoundnbt = compoundnbt.copy();
            compoundnbt.putBoolean("keepPacked", true);
         }

         return compoundnbt;
      }
   }

   public void removeBlockEntity(BlockPos p_177425_1_) {
      if (this.loaded || this.level.isClientSide()) {
         TileEntity tileentity = this.blockEntities.remove(p_177425_1_);
         if (tileentity != null) {
            tileentity.setRemoved();
         }
      }

   }

   public void runPostLoad() {
      if (this.postLoad != null) {
         this.postLoad.accept(this);
         this.postLoad = null;
      }

   }

   public void markUnsaved() {
      this.unsaved = true;
   }

   public void getEntities(@Nullable Entity p_177414_1_, AxisAlignedBB p_177414_2_, List<Entity> p_177414_3_, @Nullable Predicate<? super Entity> p_177414_4_) {
      int i = MathHelper.floor((p_177414_2_.minY - this.level.getMaxEntityRadius()) / 16.0D);
      int j = MathHelper.floor((p_177414_2_.maxY + this.level.getMaxEntityRadius()) / 16.0D);
      i = MathHelper.clamp(i, 0, this.entitySections.length - 1);
      j = MathHelper.clamp(j, 0, this.entitySections.length - 1);

      for(int k = i; k <= j; ++k) {
         ClassInheritanceMultiMap<Entity> classinheritancemultimap = this.entitySections[k];
         List<Entity> list = classinheritancemultimap.getAllInstances();
         int l = list.size();

         for(int i1 = 0; i1 < l; ++i1) {
            Entity entity = list.get(i1);
            if (entity.getBoundingBox().intersects(p_177414_2_) && entity != p_177414_1_) {
               if (p_177414_4_ == null || p_177414_4_.test(entity)) {
                  p_177414_3_.add(entity);
               }

               if (entity instanceof EnderDragonEntity) {
                  for(EnderDragonPartEntity enderdragonpartentity : ((EnderDragonEntity)entity).getSubEntities()) {
                     if (enderdragonpartentity != p_177414_1_ && enderdragonpartentity.getBoundingBox().intersects(p_177414_2_) && (p_177414_4_ == null || p_177414_4_.test(enderdragonpartentity))) {
                        p_177414_3_.add(enderdragonpartentity);
                     }
                  }
               }
            }
         }
      }

   }

   public <T extends Entity> void getEntities(@Nullable EntityType<?> p_217313_1_, AxisAlignedBB p_217313_2_, List<? super T> p_217313_3_, Predicate<? super T> p_217313_4_) {
      int i = MathHelper.floor((p_217313_2_.minY - this.level.getMaxEntityRadius()) / 16.0D);
      int j = MathHelper.floor((p_217313_2_.maxY + this.level.getMaxEntityRadius()) / 16.0D);
      i = MathHelper.clamp(i, 0, this.entitySections.length - 1);
      j = MathHelper.clamp(j, 0, this.entitySections.length - 1);

      for(int k = i; k <= j; ++k) {
         for(Entity entity : this.entitySections[k].find(Entity.class)) {
            if ((p_217313_1_ == null || entity.getType() == p_217313_1_) && entity.getBoundingBox().intersects(p_217313_2_) && p_217313_4_.test((T)entity)) {
               p_217313_3_.add((T)entity);
            }
         }
      }

   }

   public <T extends Entity> void getEntitiesOfClass(Class<? extends T> p_177430_1_, AxisAlignedBB p_177430_2_, List<T> p_177430_3_, @Nullable Predicate<? super T> p_177430_4_) {
      int i = MathHelper.floor((p_177430_2_.minY - this.level.getMaxEntityRadius()) / 16.0D);
      int j = MathHelper.floor((p_177430_2_.maxY + this.level.getMaxEntityRadius()) / 16.0D);
      i = MathHelper.clamp(i, 0, this.entitySections.length - 1);
      j = MathHelper.clamp(j, 0, this.entitySections.length - 1);

      for(int k = i; k <= j; ++k) {
         for(T t : this.entitySections[k].find(p_177430_1_)) {
            if (t.getBoundingBox().intersects(p_177430_2_) && (p_177430_4_ == null || p_177430_4_.test(t))) {
               p_177430_3_.add(t);
            }
         }
      }

   }

   public boolean isEmpty() {
      return false;
   }

   public ChunkPos getPos() {
      return this.chunkPos;
   }

   @OnlyIn(Dist.CLIENT)
   public void replaceWithPacketData(@Nullable BiomeContainer p_227073_1_, PacketBuffer p_227073_2_, CompoundNBT p_227073_3_, int p_227073_4_) {
      boolean flag = p_227073_1_ != null;
      Predicate<BlockPos> predicate = flag ? (p_217315_0_) -> {
         return true;
      } : (p_217323_1_) -> {
         return (p_227073_4_ & 1 << (p_217323_1_.getY() >> 4)) != 0;
      };
      Sets.newHashSet(this.blockEntities.keySet()).stream().filter(predicate).forEach(this.level::removeBlockEntity);

      for (TileEntity tileEntity : blockEntities.values()) {
         tileEntity.clearCache();
         tileEntity.getBlockState();
      }

      for(int i = 0; i < this.sections.length; ++i) {
         ChunkSection chunksection = this.sections[i];
         if ((p_227073_4_ & 1 << i) == 0) {
            if (flag && chunksection != EMPTY_SECTION) {
               this.sections[i] = EMPTY_SECTION;
            }
         } else {
            if (chunksection == EMPTY_SECTION) {
               chunksection = new ChunkSection(i << 4);
               this.sections[i] = chunksection;
            }

            chunksection.read(p_227073_2_);
         }
      }

      if (p_227073_1_ != null) {
         this.biomes = p_227073_1_;
      }

      for(Heightmap.Type heightmap$type : Heightmap.Type.values()) {
         String s = heightmap$type.getSerializationKey();
         if (p_227073_3_.contains(s, 12)) {
            this.setHeightmap(heightmap$type, p_227073_3_.getLongArray(s));
         }
      }

      for(TileEntity tileentity : this.blockEntities.values()) {
         tileentity.clearCache();
      }

   }

   public BiomeContainer getBiomes() {
      return this.biomes;
   }

   public void setLoaded(boolean p_177417_1_) {
      this.loaded = p_177417_1_;
   }

   public World getLevel() {
      return this.level;
   }

   public Collection<Entry<Heightmap.Type, Heightmap>> getHeightmaps() {
      return Collections.unmodifiableSet(this.heightmaps.entrySet());
   }

   public Map<BlockPos, TileEntity> getBlockEntities() {
      return this.blockEntities;
   }

   public ClassInheritanceMultiMap<Entity>[] getEntitySections() {
      return this.entitySections;
   }

   public CompoundNBT getBlockEntityNbt(BlockPos p_201579_1_) {
      return this.pendingBlockEntities.get(p_201579_1_);
   }

   public Stream<BlockPos> getLights() {
      return StreamSupport.stream(BlockPos.betweenClosed(this.chunkPos.getMinBlockX(), 0, this.chunkPos.getMinBlockZ(), this.chunkPos.getMaxBlockX(), 255, this.chunkPos.getMaxBlockZ()).spliterator(), false).filter((p_217312_1_) -> {
         return this.getBlockState(p_217312_1_).getLightValue(getLevel(), p_217312_1_) != 0;
      });
   }

   public ITickList<Block> getBlockTicks() {
      return this.blockTicks;
   }

   public ITickList<Fluid> getLiquidTicks() {
      return this.liquidTicks;
   }

   public void setUnsaved(boolean p_177427_1_) {
      this.unsaved = p_177427_1_;
   }

   public boolean isUnsaved() {
      return this.unsaved || this.lastSaveHadEntities && this.level.getGameTime() != this.lastSaveTime;
   }

   public void setLastSaveHadEntities(boolean p_177409_1_) {
      this.lastSaveHadEntities = p_177409_1_;
   }

   public void setLastSaveTime(long p_177432_1_) {
      this.lastSaveTime = p_177432_1_;
   }

   @Nullable
   public StructureStart<?> getStartForFeature(Structure<?> p_230342_1_) {
      return this.structureStarts.get(p_230342_1_);
   }

   public void setStartForFeature(Structure<?> p_230344_1_, StructureStart<?> p_230344_2_) {
      this.structureStarts.put(p_230344_1_, p_230344_2_);
   }

   public Map<Structure<?>, StructureStart<?>> getAllStarts() {
      return this.structureStarts;
   }

   public void setAllStarts(Map<Structure<?>, StructureStart<?>> p_201612_1_) {
      this.structureStarts.clear();
      this.structureStarts.putAll(p_201612_1_);
   }

   public LongSet getReferencesForFeature(Structure<?> p_230346_1_) {
      return this.structuresRefences.computeIfAbsent(p_230346_1_, (p_235961_0_) -> {
         return new LongOpenHashSet();
      });
   }

   public void addReferenceForFeature(Structure<?> p_230343_1_, long p_230343_2_) {
      this.structuresRefences.computeIfAbsent(p_230343_1_, (p_235960_0_) -> {
         return new LongOpenHashSet();
      }).add(p_230343_2_);
   }

   public Map<Structure<?>, LongSet> getAllReferences() {
      return this.structuresRefences;
   }

   public void setAllReferences(Map<Structure<?>, LongSet> p_201606_1_) {
      this.structuresRefences.clear();
      this.structuresRefences.putAll(p_201606_1_);
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void setInhabitedTime(long p_177415_1_) {
      this.inhabitedTime = p_177415_1_;
   }

   public void postProcessGeneration() {
      ChunkPos chunkpos = this.getPos();

      for(int i = 0; i < this.postProcessing.length; ++i) {
         if (this.postProcessing[i] != null) {
            for(Short oshort : this.postProcessing[i]) {
               BlockPos blockpos = ChunkPrimer.unpackOffsetCoordinates(oshort, i, chunkpos);
               BlockState blockstate = this.getBlockState(blockpos);
               BlockState blockstate1 = Block.updateFromNeighbourShapes(blockstate, this.level, blockpos);
               this.level.setBlock(blockpos, blockstate1, 20);
            }

            this.postProcessing[i].clear();
         }
      }

      this.unpackTicks();

      for(BlockPos blockpos1 : Sets.newHashSet(this.pendingBlockEntities.keySet())) {
         this.getBlockEntity(blockpos1);
      }

      this.pendingBlockEntities.clear();
      this.upgradeData.upgrade(this);
   }

   @Nullable
   private TileEntity promotePendingBlockEntity(BlockPos p_212815_1_, CompoundNBT p_212815_2_) {
      BlockState blockstate = this.getBlockState(p_212815_1_);
      TileEntity tileentity;
      if ("DUMMY".equals(p_212815_2_.getString("id"))) {
         if (blockstate.hasTileEntity()) {
            tileentity = blockstate.createTileEntity(this.level);
         } else {
            tileentity = null;
            LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", p_212815_1_, blockstate);
         }
      } else {
         tileentity = TileEntity.loadStatic(blockstate, p_212815_2_);
      }

      if (tileentity != null) {
         tileentity.setLevelAndPosition(this.level, p_212815_1_);
         this.addBlockEntity(tileentity);
      } else {
         LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", blockstate, p_212815_1_);
      }

      return tileentity;
   }

   public UpgradeData getUpgradeData() {
      return this.upgradeData;
   }

   public ShortList[] getPostProcessing() {
      return this.postProcessing;
   }

   public void unpackTicks() {
      if (this.blockTicks instanceof ChunkPrimerTickList) {
         ((ChunkPrimerTickList<Block>)this.blockTicks).copyOut(this.level.getBlockTicks(), (p_222881_1_) -> {
            return this.getBlockState(p_222881_1_).getBlock();
         });
         this.blockTicks = EmptyTickList.empty();
      } else if (this.blockTicks instanceof SerializableTickList) {
         ((SerializableTickList)this.blockTicks).copyOut(this.level.getBlockTicks());
         this.blockTicks = EmptyTickList.empty();
      }

      if (this.liquidTicks instanceof ChunkPrimerTickList) {
         ((ChunkPrimerTickList<Fluid>)this.liquidTicks).copyOut(this.level.getLiquidTicks(), (p_222878_1_) -> {
            return this.getFluidState(p_222878_1_).getType();
         });
         this.liquidTicks = EmptyTickList.empty();
      } else if (this.liquidTicks instanceof SerializableTickList) {
         ((SerializableTickList)this.liquidTicks).copyOut(this.level.getLiquidTicks());
         this.liquidTicks = EmptyTickList.empty();
      }

   }

   public void packTicks(ServerWorld p_222880_1_) {
      if (this.blockTicks == EmptyTickList.<Block>empty()) {
         this.blockTicks = new SerializableTickList<>(Registry.BLOCK::getKey, p_222880_1_.getBlockTicks().fetchTicksInChunk(this.chunkPos, true, false), p_222880_1_.getGameTime());
         this.setUnsaved(true);
      }

      if (this.liquidTicks == EmptyTickList.<Fluid>empty()) {
         this.liquidTicks = new SerializableTickList<>(Registry.FLUID::getKey, p_222880_1_.getLiquidTicks().fetchTicksInChunk(this.chunkPos, true, false), p_222880_1_.getGameTime());
         this.setUnsaved(true);
      }

   }

   public ChunkStatus getStatus() {
      return ChunkStatus.FULL;
   }

   public ChunkHolder.LocationType getFullStatus() {
      return this.fullStatus == null ? ChunkHolder.LocationType.BORDER : this.fullStatus.get();
   }

   public void setFullStatus(Supplier<ChunkHolder.LocationType> p_217314_1_) {
      this.fullStatus = p_217314_1_;
   }

   public boolean isLightCorrect() {
      return this.isLightCorrect;
   }

   public void setLightCorrect(boolean p_217305_1_) {
      this.isLightCorrect = p_217305_1_;
      this.setUnsaved(true);
   }

   public static enum CreateEntityType {
      IMMEDIATE,
      QUEUED,
      CHECK;
   }

   /**
    * <strong>FOR INTERNAL USE ONLY</strong>
    * <p>
    * Only public for use in {@link AnvilChunkLoader}.
    */
   @java.lang.Deprecated
   @javax.annotation.Nullable
   public final CompoundNBT writeCapsToNBT() {
      return this.serializeCaps();
   }

   /**
    * <strong>FOR INTERNAL USE ONLY</strong>
    * <p>
    * Only public for use in {@link AnvilChunkLoader}.
    */
   @java.lang.Deprecated
   public final void readCapsFromNBT(CompoundNBT tag) {
      this.deserializeCaps(tag);
   }

   @Override
   public World getWorldForge() {
      return getLevel();
   }
}
