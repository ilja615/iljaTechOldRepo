package net.minecraft.world.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;

public class BlockLightStorage extends SectionLightStorage<BlockLightStorage.StorageMap> {
   protected BlockLightStorage(IChunkLightProvider p_i51300_1_) {
      super(LightType.BLOCK, p_i51300_1_, new BlockLightStorage.StorageMap(new Long2ObjectOpenHashMap<>()));
   }

   protected int getLightValue(long p_215525_1_) {
      long i = SectionPos.blockToSection(p_215525_1_);
      NibbleArray nibblearray = this.getDataLayer(i, false);
      return nibblearray == null ? 0 : nibblearray.get(SectionPos.sectionRelative(BlockPos.getX(p_215525_1_)), SectionPos.sectionRelative(BlockPos.getY(p_215525_1_)), SectionPos.sectionRelative(BlockPos.getZ(p_215525_1_)));
   }

   public static final class StorageMap extends LightDataMap<BlockLightStorage.StorageMap> {
      public StorageMap(Long2ObjectOpenHashMap<NibbleArray> p_i50064_1_) {
         super(p_i50064_1_);
      }

      public BlockLightStorage.StorageMap copy() {
         return new BlockLightStorage.StorageMap(this.map.clone());
      }
   }
}
