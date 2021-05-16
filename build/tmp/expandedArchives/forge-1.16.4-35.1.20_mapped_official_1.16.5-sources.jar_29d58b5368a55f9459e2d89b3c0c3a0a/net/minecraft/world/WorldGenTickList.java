package net.minecraft.world;

import java.util.function.Function;
import net.minecraft.util.math.BlockPos;

public class WorldGenTickList<T> implements ITickList<T> {
   private final Function<BlockPos, ITickList<T>> index;

   public WorldGenTickList(Function<BlockPos, ITickList<T>> p_i48981_1_) {
      this.index = p_i48981_1_;
   }

   public boolean hasScheduledTick(BlockPos p_205359_1_, T p_205359_2_) {
      return this.index.apply(p_205359_1_).hasScheduledTick(p_205359_1_, p_205359_2_);
   }

   public void scheduleTick(BlockPos p_205362_1_, T p_205362_2_, int p_205362_3_, TickPriority p_205362_4_) {
      this.index.apply(p_205362_1_).scheduleTick(p_205362_1_, p_205362_2_, p_205362_3_, p_205362_4_);
   }

   public boolean willTickThisTick(BlockPos p_205361_1_, T p_205361_2_) {
      return false;
   }
}
