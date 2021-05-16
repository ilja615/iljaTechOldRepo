package net.minecraft.world.lighting;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.chunk.NibbleArray;

public interface IWorldLightListener extends ILightListener {
   @Nullable
   NibbleArray getDataLayerData(SectionPos p_215612_1_);

   int getLightValue(BlockPos p_215611_1_);

   public static enum Dummy implements IWorldLightListener {
      INSTANCE;

      @Nullable
      public NibbleArray getDataLayerData(SectionPos p_215612_1_) {
         return null;
      }

      public int getLightValue(BlockPos p_215611_1_) {
         return 0;
      }

      public void updateSectionStatus(SectionPos p_215566_1_, boolean p_215566_2_) {
      }
   }
}
