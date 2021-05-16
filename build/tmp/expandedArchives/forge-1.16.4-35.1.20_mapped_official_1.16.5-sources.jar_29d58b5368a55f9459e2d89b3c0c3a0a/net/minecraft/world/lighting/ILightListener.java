package net.minecraft.world.lighting;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;

public interface ILightListener {
   default void updateSectionStatus(BlockPos p_215567_1_, boolean p_215567_2_) {
      this.updateSectionStatus(SectionPos.of(p_215567_1_), p_215567_2_);
   }

   void updateSectionStatus(SectionPos p_215566_1_, boolean p_215566_2_);
}
