package net.minecraft.world.storage;

import net.minecraft.util.math.BlockPos;

public interface ISpawnWorldInfo extends IWorldInfo {
   void setXSpawn(int p_76058_1_);

   void setYSpawn(int p_76056_1_);

   void setZSpawn(int p_76087_1_);

   void setSpawnAngle(float p_241859_1_);

   default void setSpawn(BlockPos p_176143_1_, float p_176143_2_) {
      this.setXSpawn(p_176143_1_.getX());
      this.setYSpawn(p_176143_1_.getY());
      this.setZSpawn(p_176143_1_.getZ());
      this.setSpawnAngle(p_176143_2_);
   }
}
