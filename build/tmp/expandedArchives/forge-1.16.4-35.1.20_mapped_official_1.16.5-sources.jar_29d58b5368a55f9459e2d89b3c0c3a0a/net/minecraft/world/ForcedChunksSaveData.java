package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;

public class ForcedChunksSaveData extends WorldSavedData {
   private LongSet chunks = new LongOpenHashSet();

   public ForcedChunksSaveData() {
      super("chunks");
   }

   public void load(CompoundNBT p_76184_1_) {
      this.chunks = new LongOpenHashSet(p_76184_1_.getLongArray("Forced"));
   }

   public CompoundNBT save(CompoundNBT p_189551_1_) {
      p_189551_1_.putLongArray("Forced", this.chunks.toLongArray());
      return p_189551_1_;
   }

   public LongSet getChunks() {
      return this.chunks;
   }
}
