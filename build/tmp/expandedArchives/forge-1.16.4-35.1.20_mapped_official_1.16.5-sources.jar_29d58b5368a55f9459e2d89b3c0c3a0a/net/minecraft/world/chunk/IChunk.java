package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IStructureReader;
import net.minecraft.world.ITickList;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import org.apache.logging.log4j.LogManager;

public interface IChunk extends IBlockReader, IStructureReader {
   @Nullable
   BlockState setBlockState(BlockPos p_177436_1_, BlockState p_177436_2_, boolean p_177436_3_);

   void setBlockEntity(BlockPos p_177426_1_, TileEntity p_177426_2_);

   void addEntity(Entity p_76612_1_);

   @Nullable
   default ChunkSection getHighestSection() {
      ChunkSection[] achunksection = this.getSections();

      for(int i = achunksection.length - 1; i >= 0; --i) {
         ChunkSection chunksection = achunksection[i];
         if (!ChunkSection.isEmpty(chunksection)) {
            return chunksection;
         }
      }

      return null;
   }

   default int getHighestSectionPosition() {
      ChunkSection chunksection = this.getHighestSection();
      return chunksection == null ? 0 : chunksection.bottomBlockY();
   }

   Set<BlockPos> getBlockEntitiesPos();

   ChunkSection[] getSections();

   Collection<Entry<Heightmap.Type, Heightmap>> getHeightmaps();

   void setHeightmap(Heightmap.Type p_201607_1_, long[] p_201607_2_);

   Heightmap getOrCreateHeightmapUnprimed(Heightmap.Type p_217303_1_);

   int getHeight(Heightmap.Type p_201576_1_, int p_201576_2_, int p_201576_3_);

   ChunkPos getPos();

   void setLastSaveTime(long p_177432_1_);

   Map<Structure<?>, StructureStart<?>> getAllStarts();

   void setAllStarts(Map<Structure<?>, StructureStart<?>> p_201612_1_);

   default boolean isYSpaceEmpty(int p_76606_1_, int p_76606_2_) {
      if (p_76606_1_ < 0) {
         p_76606_1_ = 0;
      }

      if (p_76606_2_ >= 256) {
         p_76606_2_ = 255;
      }

      for(int i = p_76606_1_; i <= p_76606_2_; i += 16) {
         if (!ChunkSection.isEmpty(this.getSections()[i >> 4])) {
            return false;
         }
      }

      return true;
   }

   @Nullable
   BiomeContainer getBiomes();

   void setUnsaved(boolean p_177427_1_);

   boolean isUnsaved();

   ChunkStatus getStatus();

   void removeBlockEntity(BlockPos p_177425_1_);

   default void markPosForPostprocessing(BlockPos p_201594_1_) {
      LogManager.getLogger().warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", (Object)p_201594_1_);
   }

   ShortList[] getPostProcessing();

   default void addPackedPostProcess(short p_201636_1_, int p_201636_2_) {
      getOrCreateOffsetList(this.getPostProcessing(), p_201636_2_).add(p_201636_1_);
   }

   default void setBlockEntityNbt(CompoundNBT p_201591_1_) {
      LogManager.getLogger().warn("Trying to set a BlockEntity, but this operation is not supported.");
   }

   @Nullable
   CompoundNBT getBlockEntityNbt(BlockPos p_201579_1_);

   @Nullable
   CompoundNBT getBlockEntityNbtForSaving(BlockPos p_223134_1_);

   Stream<BlockPos> getLights();

   ITickList<Block> getBlockTicks();

   ITickList<Fluid> getLiquidTicks();

   UpgradeData getUpgradeData();

   void setInhabitedTime(long p_177415_1_);

   long getInhabitedTime();

   static ShortList getOrCreateOffsetList(ShortList[] p_217308_0_, int p_217308_1_) {
      if (p_217308_0_[p_217308_1_] == null) {
         p_217308_0_[p_217308_1_] = new ShortArrayList();
      }

      return p_217308_0_[p_217308_1_];
   }

   boolean isLightCorrect();

   void setLightCorrect(boolean p_217305_1_);

   @Nullable
   default net.minecraft.world.IWorld getWorldForge() {
      return null;
   }
}
