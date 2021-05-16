package net.minecraft.world.storage;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.nbt.CompoundNBT;

public class MapIdTracker extends WorldSavedData {
   private final Object2IntMap<String> usedAuxIds = new Object2IntOpenHashMap<>();

   public MapIdTracker() {
      super("idcounts");
      this.usedAuxIds.defaultReturnValue(-1);
   }

   public void load(CompoundNBT p_76184_1_) {
      this.usedAuxIds.clear();

      for(String s : p_76184_1_.getAllKeys()) {
         if (p_76184_1_.contains(s, 99)) {
            this.usedAuxIds.put(s, p_76184_1_.getInt(s));
         }
      }

   }

   public CompoundNBT save(CompoundNBT p_189551_1_) {
      for(Entry<String> entry : this.usedAuxIds.object2IntEntrySet()) {
         p_189551_1_.putInt(entry.getKey(), entry.getIntValue());
      }

      return p_189551_1_;
   }

   public int getFreeAuxValueForMap() {
      int i = this.usedAuxIds.getInt("map") + 1;
      this.usedAuxIds.put("map", i);
      this.setDirty();
      return i;
   }
}
