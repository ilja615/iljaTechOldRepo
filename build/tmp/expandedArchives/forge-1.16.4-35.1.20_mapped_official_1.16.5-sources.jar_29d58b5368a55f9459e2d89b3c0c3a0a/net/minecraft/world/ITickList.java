package net.minecraft.world;

import net.minecraft.util.math.BlockPos;

public interface ITickList<T> {
   boolean hasScheduledTick(BlockPos p_205359_1_, T p_205359_2_);

   default void scheduleTick(BlockPos p_205360_1_, T p_205360_2_, int p_205360_3_) {
      this.scheduleTick(p_205360_1_, p_205360_2_, p_205360_3_, TickPriority.NORMAL);
   }

   void scheduleTick(BlockPos p_205362_1_, T p_205362_2_, int p_205362_3_, TickPriority p_205362_4_);

   boolean willTickThisTick(BlockPos p_205361_1_, T p_205361_2_);
}
