package net.minecraft.world.gen.layer.traits;

public interface IDimOffset1Transformer extends IDimTransformer {
   default int getParentX(int p_215721_1_) {
      return p_215721_1_ - 1;
   }

   default int getParentY(int p_215722_1_) {
      return p_215722_1_ - 1;
   }
}
