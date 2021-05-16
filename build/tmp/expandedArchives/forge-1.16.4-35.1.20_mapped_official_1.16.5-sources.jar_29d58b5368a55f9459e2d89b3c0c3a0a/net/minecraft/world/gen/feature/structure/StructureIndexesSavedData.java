package net.minecraft.world.gen.feature.structure;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;

public class StructureIndexesSavedData extends WorldSavedData {
   private LongSet all = new LongOpenHashSet();
   private LongSet remaining = new LongOpenHashSet();

   public StructureIndexesSavedData(String p_i48654_1_) {
      super(p_i48654_1_);
   }

   public void load(CompoundNBT p_76184_1_) {
      this.all = new LongOpenHashSet(p_76184_1_.getLongArray("All"));
      this.remaining = new LongOpenHashSet(p_76184_1_.getLongArray("Remaining"));
   }

   public CompoundNBT save(CompoundNBT p_189551_1_) {
      p_189551_1_.putLongArray("All", this.all.toLongArray());
      p_189551_1_.putLongArray("Remaining", this.remaining.toLongArray());
      return p_189551_1_;
   }

   public void addIndex(long p_201763_1_) {
      this.all.add(p_201763_1_);
      this.remaining.add(p_201763_1_);
   }

   public boolean hasStartIndex(long p_208024_1_) {
      return this.all.contains(p_208024_1_);
   }

   public boolean hasUnhandledIndex(long p_208023_1_) {
      return this.remaining.contains(p_208023_1_);
   }

   public void removeIndex(long p_201762_1_) {
      this.remaining.remove(p_201762_1_);
   }

   public LongSet getAll() {
      return this.all;
   }
}
