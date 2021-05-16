package net.minecraft.world;

import net.minecraft.util.math.BlockPos;

public class EmptyTickList<T> implements ITickList<T> {
   private static final EmptyTickList<Object> INSTANCE = new EmptyTickList<>();

   public static <T> EmptyTickList<T> empty() {
      return (EmptyTickList<T>) INSTANCE;
   }

   public boolean hasScheduledTick(BlockPos p_205359_1_, T p_205359_2_) {
      return false;
   }

   public void scheduleTick(BlockPos p_205360_1_, T p_205360_2_, int p_205360_3_) {
   }

   public void scheduleTick(BlockPos p_205362_1_, T p_205362_2_, int p_205362_3_, TickPriority p_205362_4_) {
   }

   public boolean willTickThisTick(BlockPos p_205361_1_, T p_205361_2_) {
      return false;
   }
}
