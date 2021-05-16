package net.minecraft.world.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.world.chunk.NibbleArray;

public abstract class LightDataMap<M extends LightDataMap<M>> {
   private final long[] lastSectionKeys = new long[2];
   private final NibbleArray[] lastSections = new NibbleArray[2];
   private boolean cacheEnabled;
   protected final Long2ObjectOpenHashMap<NibbleArray> map;

   protected LightDataMap(Long2ObjectOpenHashMap<NibbleArray> p_i51299_1_) {
      this.map = p_i51299_1_;
      this.clearCache();
      this.cacheEnabled = true;
   }

   public abstract M copy();

   public void copyDataLayer(long p_215641_1_) {
      this.map.put(p_215641_1_, this.map.get(p_215641_1_).copy());
      this.clearCache();
   }

   public boolean hasLayer(long p_215642_1_) {
      return this.map.containsKey(p_215642_1_);
   }

   @Nullable
   public NibbleArray getLayer(long p_215638_1_) {
      if (this.cacheEnabled) {
         for(int i = 0; i < 2; ++i) {
            if (p_215638_1_ == this.lastSectionKeys[i]) {
               return this.lastSections[i];
            }
         }
      }

      NibbleArray nibblearray = this.map.get(p_215638_1_);
      if (nibblearray == null) {
         return null;
      } else {
         if (this.cacheEnabled) {
            for(int j = 1; j > 0; --j) {
               this.lastSectionKeys[j] = this.lastSectionKeys[j - 1];
               this.lastSections[j] = this.lastSections[j - 1];
            }

            this.lastSectionKeys[0] = p_215638_1_;
            this.lastSections[0] = nibblearray;
         }

         return nibblearray;
      }
   }

   @Nullable
   public NibbleArray removeLayer(long p_223130_1_) {
      return this.map.remove(p_223130_1_);
   }

   public void setLayer(long p_215640_1_, NibbleArray p_215640_3_) {
      this.map.put(p_215640_1_, p_215640_3_);
   }

   public void clearCache() {
      for(int i = 0; i < 2; ++i) {
         this.lastSectionKeys[i] = Long.MAX_VALUE;
         this.lastSections[i] = null;
      }

   }

   public void disableCache() {
      this.cacheEnabled = false;
   }
}
