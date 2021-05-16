package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.lighting.WorldLightManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkPrimer implements IChunk {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ChunkPos chunkPos;
   private volatile boolean isDirty;
   @Nullable
   private BiomeContainer biomes;
   @Nullable
   private volatile WorldLightManager lightEngine;
   private final Map<Heightmap.Type, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Type.class);
   private volatile ChunkStatus status = ChunkStatus.EMPTY;
   private final Map<BlockPos, TileEntity> blockEntities = Maps.newHashMap();
   private final Map<BlockPos, CompoundNBT> blockEntityNbts = Maps.newHashMap();
   private final ChunkSection[] sections = new ChunkSection[16];
   private final List<CompoundNBT> entities = Lists.newArrayList();
   private final List<BlockPos> lights = Lists.newArrayList();
   private final ShortList[] postProcessing = new ShortList[16];
   private final Map<Structure<?>, StructureStart<?>> structureStarts = Maps.newHashMap();
   private final Map<Structure<?>, LongSet> structuresRefences = Maps.newHashMap();
   private final UpgradeData upgradeData;
   private final ChunkPrimerTickList<Block> blockTicks;
   private final ChunkPrimerTickList<Fluid> liquidTicks;
   private long inhabitedTime;
   private final Map<GenerationStage.Carving, BitSet> carvingMasks = new Object2ObjectArrayMap<>();
   private volatile boolean isLightCorrect;

   public ChunkPrimer(ChunkPos p_i48700_1_, UpgradeData p_i48700_2_) {
      this(p_i48700_1_, p_i48700_2_, (ChunkSection[])null, new ChunkPrimerTickList<>((p_205332_0_) -> {
         return p_205332_0_ == null || p_205332_0_.defaultBlockState().isAir();
      }, p_i48700_1_), new ChunkPrimerTickList<>((p_205766_0_) -> {
         return p_205766_0_ == null || p_205766_0_ == Fluids.EMPTY;
      }, p_i48700_1_));
   }

   public ChunkPrimer(ChunkPos p_i49941_1_, UpgradeData p_i49941_2_, @Nullable ChunkSection[] p_i49941_3_, ChunkPrimerTickList<Block> p_i49941_4_, ChunkPrimerTickList<Fluid> p_i49941_5_) {
      this.chunkPos = p_i49941_1_;
      this.upgradeData = p_i49941_2_;
      this.blockTicks = p_i49941_4_;
      this.liquidTicks = p_i49941_5_;
      if (p_i49941_3_ != null) {
         if (this.sections.length == p_i49941_3_.length) {
            System.arraycopy(p_i49941_3_, 0, this.sections, 0, this.sections.length);
         } else {
            LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", p_i49941_3_.length, this.sections.length);
         }
      }

   }

   public BlockState getBlockState(BlockPos p_180495_1_) {
      int i = p_180495_1_.getY();
      if (World.isOutsideBuildHeight(i)) {
         return Blocks.VOID_AIR.defaultBlockState();
      } else {
         ChunkSection chunksection = this.getSections()[i >> 4];
         return ChunkSection.isEmpty(chunksection) ? Blocks.AIR.defaultBlockState() : chunksection.getBlockState(p_180495_1_.getX() & 15, i & 15, p_180495_1_.getZ() & 15);
      }
   }

   public FluidState getFluidState(BlockPos p_204610_1_) {
      int i = p_204610_1_.getY();
      if (World.isOutsideBuildHeight(i)) {
         return Fluids.EMPTY.defaultFluidState();
      } else {
         ChunkSection chunksection = this.getSections()[i >> 4];
         return ChunkSection.isEmpty(chunksection) ? Fluids.EMPTY.defaultFluidState() : chunksection.getFluidState(p_204610_1_.getX() & 15, i & 15, p_204610_1_.getZ() & 15);
      }
   }

   public Stream<BlockPos> getLights() {
      return this.lights.stream();
   }

   public ShortList[] getPackedLights() {
      ShortList[] ashortlist = new ShortList[16];

      for(BlockPos blockpos : this.lights) {
         IChunk.getOrCreateOffsetList(ashortlist, blockpos.getY() >> 4).add(packOffsetCoordinates(blockpos));
      }

      return ashortlist;
   }

   public void addLight(short p_201646_1_, int p_201646_2_) {
      this.addLight(unpackOffsetCoordinates(p_201646_1_, p_201646_2_, this.chunkPos));
   }

   public void addLight(BlockPos p_201637_1_) {
      this.lights.add(p_201637_1_.immutable());
   }

   @Nullable
   public BlockState setBlockState(BlockPos p_177436_1_, BlockState p_177436_2_, boolean p_177436_3_) {
      int i = p_177436_1_.getX();
      int j = p_177436_1_.getY();
      int k = p_177436_1_.getZ();
      if (j >= 0 && j < 256) {
         if (this.sections[j >> 4] == Chunk.EMPTY_SECTION && p_177436_2_.is(Blocks.AIR)) {
            return p_177436_2_;
         } else {
            if (p_177436_2_.getLightValue(this, p_177436_1_) > 0) {
               this.lights.add(new BlockPos((i & 15) + this.getPos().getMinBlockX(), j, (k & 15) + this.getPos().getMinBlockZ()));
            }

            ChunkSection chunksection = this.getOrCreateSection(j >> 4);
            BlockState blockstate = chunksection.setBlockState(i & 15, j & 15, k & 15, p_177436_2_);
            if (this.status.isOrAfter(ChunkStatus.FEATURES) && p_177436_2_ != blockstate && (p_177436_2_.getLightBlock(this, p_177436_1_) != blockstate.getLightBlock(this, p_177436_1_) || p_177436_2_.getLightValue(this, p_177436_1_) != blockstate.getLightValue(this, p_177436_1_) || p_177436_2_.useShapeForLightOcclusion() || blockstate.useShapeForLightOcclusion())) {
               WorldLightManager worldlightmanager = this.getLightEngine();
               worldlightmanager.checkBlock(p_177436_1_);
            }

            EnumSet<Heightmap.Type> enumset1 = this.getStatus().heightmapsAfter();
            EnumSet<Heightmap.Type> enumset = null;

            for(Heightmap.Type heightmap$type : enumset1) {
               Heightmap heightmap = this.heightmaps.get(heightmap$type);
               if (heightmap == null) {
                  if (enumset == null) {
                     enumset = EnumSet.noneOf(Heightmap.Type.class);
                  }

                  enumset.add(heightmap$type);
               }
            }

            if (enumset != null) {
               Heightmap.primeHeightmaps(this, enumset);
            }

            for(Heightmap.Type heightmap$type1 : enumset1) {
               this.heightmaps.get(heightmap$type1).update(i & 15, j, k & 15, p_177436_2_);
            }

            return blockstate;
         }
      } else {
         return Blocks.VOID_AIR.defaultBlockState();
      }
   }

   public ChunkSection getOrCreateSection(int p_217332_1_) {
      if (this.sections[p_217332_1_] == Chunk.EMPTY_SECTION) {
         this.sections[p_217332_1_] = new ChunkSection(p_217332_1_ << 4);
      }

      return this.sections[p_217332_1_];
   }

   public void setBlockEntity(BlockPos p_177426_1_, TileEntity p_177426_2_) {
      p_177426_2_.setPosition(p_177426_1_);
      this.blockEntities.put(p_177426_1_, p_177426_2_);
   }

   public Set<BlockPos> getBlockEntitiesPos() {
      Set<BlockPos> set = Sets.newHashSet(this.blockEntityNbts.keySet());
      set.addAll(this.blockEntities.keySet());
      return set;
   }

   @Nullable
   public TileEntity getBlockEntity(BlockPos p_175625_1_) {
      return this.blockEntities.get(p_175625_1_);
   }

   public Map<BlockPos, TileEntity> getBlockEntities() {
      return this.blockEntities;
   }

   public void addEntity(CompoundNBT p_201626_1_) {
      this.entities.add(p_201626_1_);
   }

   public void addEntity(Entity p_76612_1_) {
      if (!p_76612_1_.isPassenger()) {
         CompoundNBT compoundnbt = new CompoundNBT();
         p_76612_1_.save(compoundnbt);
         this.addEntity(compoundnbt);
      }
   }

   public List<CompoundNBT> getEntities() {
      return this.entities;
   }

   public void setBiomes(BiomeContainer p_225548_1_) {
      this.biomes = p_225548_1_;
   }

   @Nullable
   public BiomeContainer getBiomes() {
      return this.biomes;
   }

   public void setUnsaved(boolean p_177427_1_) {
      this.isDirty = p_177427_1_;
   }

   public boolean isUnsaved() {
      return this.isDirty;
   }

   public ChunkStatus getStatus() {
      return this.status;
   }

   public void setStatus(ChunkStatus p_201574_1_) {
      this.status = p_201574_1_;
      this.setUnsaved(true);
   }

   public ChunkSection[] getSections() {
      return this.sections;
   }

   @Nullable
   public WorldLightManager getLightEngine() {
      return this.lightEngine;
   }

   public Collection<Entry<Heightmap.Type, Heightmap>> getHeightmaps() {
      return Collections.unmodifiableSet(this.heightmaps.entrySet());
   }

   public void setHeightmap(Heightmap.Type p_201607_1_, long[] p_201607_2_) {
      this.getOrCreateHeightmapUnprimed(p_201607_1_).setRawData(p_201607_2_);
   }

   public Heightmap getOrCreateHeightmapUnprimed(Heightmap.Type p_217303_1_) {
      return this.heightmaps.computeIfAbsent(p_217303_1_, (p_217333_1_) -> {
         return new Heightmap(this, p_217333_1_);
      });
   }

   public int getHeight(Heightmap.Type p_201576_1_, int p_201576_2_, int p_201576_3_) {
      Heightmap heightmap = this.heightmaps.get(p_201576_1_);
      if (heightmap == null) {
         Heightmap.primeHeightmaps(this, EnumSet.of(p_201576_1_));
         heightmap = this.heightmaps.get(p_201576_1_);
      }

      return heightmap.getFirstAvailable(p_201576_2_ & 15, p_201576_3_ & 15) - 1;
   }

   public ChunkPos getPos() {
      return this.chunkPos;
   }

   public void setLastSaveTime(long p_177432_1_) {
   }

   @Nullable
   public StructureStart<?> getStartForFeature(Structure<?> p_230342_1_) {
      return this.structureStarts.get(p_230342_1_);
   }

   public void setStartForFeature(Structure<?> p_230344_1_, StructureStart<?> p_230344_2_) {
      this.structureStarts.put(p_230344_1_, p_230344_2_);
      this.isDirty = true;
   }

   public Map<Structure<?>, StructureStart<?>> getAllStarts() {
      return Collections.unmodifiableMap(this.structureStarts);
   }

   public void setAllStarts(Map<Structure<?>, StructureStart<?>> p_201612_1_) {
      this.structureStarts.clear();
      this.structureStarts.putAll(p_201612_1_);
      this.isDirty = true;
   }

   public LongSet getReferencesForFeature(Structure<?> p_230346_1_) {
      return this.structuresRefences.computeIfAbsent(p_230346_1_, (p_235966_0_) -> {
         return new LongOpenHashSet();
      });
   }

   public void addReferenceForFeature(Structure<?> p_230343_1_, long p_230343_2_) {
      this.structuresRefences.computeIfAbsent(p_230343_1_, (p_235965_0_) -> {
         return new LongOpenHashSet();
      }).add(p_230343_2_);
      this.isDirty = true;
   }

   public Map<Structure<?>, LongSet> getAllReferences() {
      return Collections.unmodifiableMap(this.structuresRefences);
   }

   public void setAllReferences(Map<Structure<?>, LongSet> p_201606_1_) {
      this.structuresRefences.clear();
      this.structuresRefences.putAll(p_201606_1_);
      this.isDirty = true;
   }

   public static short packOffsetCoordinates(BlockPos p_201651_0_) {
      int i = p_201651_0_.getX();
      int j = p_201651_0_.getY();
      int k = p_201651_0_.getZ();
      int l = i & 15;
      int i1 = j & 15;
      int j1 = k & 15;
      return (short)(l | i1 << 4 | j1 << 8);
   }

   public static BlockPos unpackOffsetCoordinates(short p_201635_0_, int p_201635_1_, ChunkPos p_201635_2_) {
      int i = (p_201635_0_ & 15) + (p_201635_2_.x << 4);
      int j = (p_201635_0_ >>> 4 & 15) + (p_201635_1_ << 4);
      int k = (p_201635_0_ >>> 8 & 15) + (p_201635_2_.z << 4);
      return new BlockPos(i, j, k);
   }

   public void markPosForPostprocessing(BlockPos p_201594_1_) {
      if (!World.isOutsideBuildHeight(p_201594_1_)) {
         IChunk.getOrCreateOffsetList(this.postProcessing, p_201594_1_.getY() >> 4).add(packOffsetCoordinates(p_201594_1_));
      }

   }

   public ShortList[] getPostProcessing() {
      return this.postProcessing;
   }

   public void addPackedPostProcess(short p_201636_1_, int p_201636_2_) {
      IChunk.getOrCreateOffsetList(this.postProcessing, p_201636_2_).add(p_201636_1_);
   }

   public ChunkPrimerTickList<Block> getBlockTicks() {
      return this.blockTicks;
   }

   public ChunkPrimerTickList<Fluid> getLiquidTicks() {
      return this.liquidTicks;
   }

   public UpgradeData getUpgradeData() {
      return this.upgradeData;
   }

   public void setInhabitedTime(long p_177415_1_) {
      this.inhabitedTime = p_177415_1_;
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void setBlockEntityNbt(CompoundNBT p_201591_1_) {
      this.blockEntityNbts.put(new BlockPos(p_201591_1_.getInt("x"), p_201591_1_.getInt("y"), p_201591_1_.getInt("z")), p_201591_1_);
   }

   public Map<BlockPos, CompoundNBT> getBlockEntityNbts() {
      return Collections.unmodifiableMap(this.blockEntityNbts);
   }

   public CompoundNBT getBlockEntityNbt(BlockPos p_201579_1_) {
      return this.blockEntityNbts.get(p_201579_1_);
   }

   @Nullable
   public CompoundNBT getBlockEntityNbtForSaving(BlockPos p_223134_1_) {
      TileEntity tileentity = this.getBlockEntity(p_223134_1_);
      return tileentity != null ? tileentity.save(new CompoundNBT()) : this.blockEntityNbts.get(p_223134_1_);
   }

   public void removeBlockEntity(BlockPos p_177425_1_) {
      this.blockEntities.remove(p_177425_1_);
      this.blockEntityNbts.remove(p_177425_1_);
   }

   @Nullable
   public BitSet getCarvingMask(GenerationStage.Carving p_205749_1_) {
      return this.carvingMasks.get(p_205749_1_);
   }

   public BitSet getOrCreateCarvingMask(GenerationStage.Carving p_230345_1_) {
      return this.carvingMasks.computeIfAbsent(p_230345_1_, (p_235964_0_) -> {
         return new BitSet(65536);
      });
   }

   public void setCarvingMask(GenerationStage.Carving p_205767_1_, BitSet p_205767_2_) {
      this.carvingMasks.put(p_205767_1_, p_205767_2_);
   }

   public void setLightEngine(WorldLightManager p_217306_1_) {
      this.lightEngine = p_217306_1_;
   }

   public boolean isLightCorrect() {
      return this.isLightCorrect;
   }

   public void setLightCorrect(boolean p_217305_1_) {
      this.isLightCorrect = p_217305_1_;
      this.setUnsaved(true);
   }
}
