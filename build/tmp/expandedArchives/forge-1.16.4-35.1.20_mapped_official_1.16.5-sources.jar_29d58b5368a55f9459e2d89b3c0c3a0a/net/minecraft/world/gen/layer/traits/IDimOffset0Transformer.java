package net.minecraft.world.gen.layer.traits;

public interface IDimOffset0Transformer extends IDimTransformer {
   default int getParentX(int p_215721_1_) {
      return p_215721_1_;
   }

   default int getParentY(int p_215722_1_) {
      return p_215722_1_;
   }
}
