package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ITickList;
import net.minecraft.world.TickPriority;
import net.minecraft.world.chunk.storage.ChunkSerializer;

public class ChunkPrimerTickList<T> implements ITickList<T> {
   protected final Predicate<T> ignore;
   private final ChunkPos chunkPos;
   private final ShortList[] toBeTicked = new ShortList[16];

   public ChunkPrimerTickList(Predicate<T> p_i51495_1_, ChunkPos p_i51495_2_) {
      this(p_i51495_1_, p_i51495_2_, new ListNBT());
   }

   public ChunkPrimerTickList(Predicate<T> p_i51496_1_, ChunkPos p_i51496_2_, ListNBT p_i51496_3_) {
      this.ignore = p_i51496_1_;
      this.chunkPos = p_i51496_2_;

      for(int i = 0; i < p_i51496_3_.size(); ++i) {
         ListNBT listnbt = p_i51496_3_.getList(i);

         for(int j = 0; j < listnbt.size(); ++j) {
            IChunk.getOrCreateOffsetList(this.toBeTicked, i).add(listnbt.getShort(j));
         }
      }

   }

   public ListNBT save() {
      return ChunkSerializer.packOffsets(this.toBeTicked);
   }

   public void copyOut(ITickList<T> p_205381_1_, Function<BlockPos, T> p_205381_2_) {
      for(int i = 0; i < this.toBeTicked.length; ++i) {
         if (this.toBeTicked[i] != null) {
            for(Short oshort : this.toBeTicked[i]) {
               BlockPos blockpos = ChunkPrimer.unpackOffsetCoordinates(oshort, i, this.chunkPos);
               p_205381_1_.scheduleTick(blockpos, p_205381_2_.apply(blockpos), 0);
            }

            this.toBeTicked[i].clear();
         }
      }

   }

   public boolean hasScheduledTick(BlockPos p_205359_1_, T p_205359_2_) {
      return false;
   }

   public void scheduleTick(BlockPos p_205362_1_, T p_205362_2_, int p_205362_3_, TickPriority p_205362_4_) {
      IChunk.getOrCreateOffsetList(this.toBeTicked, p_205362_1_.getY() >> 4).add(ChunkPrimer.packOffsetCoordinates(p_205362_1_));
   }

   public boolean willTickThisTick(BlockPos p_205361_1_, T p_205361_2_) {
      return false;
   }
}
