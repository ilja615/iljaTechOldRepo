package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public interface IWorldWriter {
   boolean setBlock(BlockPos p_241211_1_, BlockState p_241211_2_, int p_241211_3_, int p_241211_4_);

   default boolean setBlock(BlockPos p_180501_1_, BlockState p_180501_2_, int p_180501_3_) {
      return this.setBlock(p_180501_1_, p_180501_2_, p_180501_3_, 512);
   }

   boolean removeBlock(BlockPos p_217377_1_, boolean p_217377_2_);

   default boolean destroyBlock(BlockPos p_175655_1_, boolean p_175655_2_) {
      return this.destroyBlock(p_175655_1_, p_175655_2_, (Entity)null);
   }

   default boolean destroyBlock(BlockPos p_225521_1_, boolean p_225521_2_, @Nullable Entity p_225521_3_) {
      return this.destroyBlock(p_225521_1_, p_225521_2_, p_225521_3_, 512);
   }

   boolean destroyBlock(BlockPos p_241212_1_, boolean p_241212_2_, @Nullable Entity p_241212_3_, int p_241212_4_);

   default boolean addFreshEntity(Entity p_217376_1_) {
      return false;
   }
}
