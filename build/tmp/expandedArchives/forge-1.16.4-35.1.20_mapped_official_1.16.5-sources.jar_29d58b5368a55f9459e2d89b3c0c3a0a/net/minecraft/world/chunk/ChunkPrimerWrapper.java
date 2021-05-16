package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.lighting.WorldLightManager;

public class ChunkPrimerWrapper extends ChunkPrimer {
   private final Chunk wrapped;

   public ChunkPrimerWrapper(Chunk p_i49948_1_) {
      super(p_i49948_1_.getPos(), UpgradeData.EMPTY);
      this.wrapped = p_i49948_1_;
   }

   @Nullable
   public TileEntity getBlockEntity(BlockPos p_175625_1_) {
      return this.wrapped.getBlockEntity(p_175625_1_);
   }

   @Nullable
   public BlockState getBlockState(BlockPos p_180495_1_) {
      return this.wrapped.getBlockState(p_180495_1_);
   }

   public FluidState getFluidState(BlockPos p_204610_1_) {
      return this.wrapped.getFluidState(p_204610_1_);
   }

   public int getMaxLightLevel() {
      return this.wrapped.getMaxLightLevel();
   }

   @Nullable
   public BlockState setBlockState(BlockPos p_177436_1_, BlockState p_177436_2_, boolean p_177436_3_) {
      return null;
   }

   public void setBlockEntity(BlockPos p_177426_1_, TileEntity p_177426_2_) {
   }

   public void addEntity(Entity p_76612_1_) {
   }

   public void setStatus(ChunkStatus p_201574_1_) {
   }

   public ChunkSection[] getSections() {
      return this.wrapped.getSections();
   }

   @Nullable
   public WorldLightManager getLightEngine() {
      return this.wrapped.getLightEngine();
   }

   public void setHeightmap(Heightmap.Type p_201607_1_, long[] p_201607_2_) {
   }

   private Heightmap.Type fixType(Heightmap.Type p_209532_1_) {
      if (p_209532_1_ == Heightmap.Type.WORLD_SURFACE_WG) {
         return Heightmap.Type.WORLD_SURFACE;
      } else {
         return p_209532_1_ == Heightmap.Type.OCEAN_FLOOR_WG ? Heightmap.Type.OCEAN_FLOOR : p_209532_1_;
      }
   }

   public int getHeight(Heightmap.Type p_201576_1_, int p_201576_2_, int p_201576_3_) {
      return this.wrapped.getHeight(this.fixType(p_201576_1_), p_201576_2_, p_201576_3_);
   }

   public ChunkPos getPos() {
      return this.wrapped.getPos();
   }

   public void setLastSaveTime(long p_177432_1_) {
   }

   @Nullable
   public StructureStart<?> getStartForFeature(Structure<?> p_230342_1_) {
      return this.wrapped.getStartForFeature(p_230342_1_);
   }

   public void setStartForFeature(Structure<?> p_230344_1_, StructureStart<?> p_230344_2_) {
   }

   public Map<Structure<?>, StructureStart<?>> getAllStarts() {
      return this.wrapped.getAllStarts();
   }

   public void setAllStarts(Map<Structure<?>, StructureStart<?>> p_201612_1_) {
   }

   public LongSet getReferencesForFeature(Structure<?> p_230346_1_) {
      return this.wrapped.getReferencesForFeature(p_230346_1_);
   }

   public void addReferenceForFeature(Structure<?> p_230343_1_, long p_230343_2_) {
   }

   public Map<Structure<?>, LongSet> getAllReferences() {
      return this.wrapped.getAllReferences();
   }

   public void setAllReferences(Map<Structure<?>, LongSet> p_201606_1_) {
   }

   public BiomeContainer getBiomes() {
      return this.wrapped.getBiomes();
   }

   public void setUnsaved(boolean p_177427_1_) {
   }

   public boolean isUnsaved() {
      return false;
   }

   public ChunkStatus getStatus() {
      return this.wrapped.getStatus();
   }

   public void removeBlockEntity(BlockPos p_177425_1_) {
   }

   public void markPosForPostprocessing(BlockPos p_201594_1_) {
   }

   public void setBlockEntityNbt(CompoundNBT p_201591_1_) {
   }

   @Nullable
   public CompoundNBT getBlockEntityNbt(BlockPos p_201579_1_) {
      return this.wrapped.getBlockEntityNbt(p_201579_1_);
   }

   @Nullable
   public CompoundNBT getBlockEntityNbtForSaving(BlockPos p_223134_1_) {
      return this.wrapped.getBlockEntityNbtForSaving(p_223134_1_);
   }

   public void setBiomes(BiomeContainer p_225548_1_) {
   }

   public Stream<BlockPos> getLights() {
      return this.wrapped.getLights();
   }

   public ChunkPrimerTickList<Block> getBlockTicks() {
      return new ChunkPrimerTickList<>((p_209219_0_) -> {
         return p_209219_0_.defaultBlockState().isAir();
      }, this.getPos());
   }

   public ChunkPrimerTickList<Fluid> getLiquidTicks() {
      return new ChunkPrimerTickList<>((p_209218_0_) -> {
         return p_209218_0_ == Fluids.EMPTY;
      }, this.getPos());
   }

   public BitSet getCarvingMask(GenerationStage.Carving p_205749_1_) {
      throw (UnsupportedOperationException)Util.pauseInIde(new UnsupportedOperationException("Meaningless in this context"));
   }

   public BitSet getOrCreateCarvingMask(GenerationStage.Carving p_230345_1_) {
      throw (UnsupportedOperationException)Util.pauseInIde(new UnsupportedOperationException("Meaningless in this context"));
   }

   public Chunk getWrapped() {
      return this.wrapped;
   }

   public boolean isLightCorrect() {
      return this.wrapped.isLightCorrect();
   }

   public void setLightCorrect(boolean p_217305_1_) {
      this.wrapped.setLightCorrect(p_217305_1_);
   }
}
