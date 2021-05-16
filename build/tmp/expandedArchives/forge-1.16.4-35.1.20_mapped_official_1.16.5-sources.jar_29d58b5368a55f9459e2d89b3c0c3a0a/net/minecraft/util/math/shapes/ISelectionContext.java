package net.minecraft.util.math.shapes;

import net.minecraft.entity.Entity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

public interface ISelectionContext extends net.minecraftforge.common.extensions.IForgeSelectionContext {
   static ISelectionContext empty() {
      return EntitySelectionContext.EMPTY;
   }

   static ISelectionContext of(Entity p_216374_0_) {
      return new EntitySelectionContext(p_216374_0_);
   }

   boolean isDescending();

   boolean isAbove(VoxelShape p_216378_1_, BlockPos p_216378_2_, boolean p_216378_3_);

   boolean isHoldingItem(Item p_216375_1_);

   boolean canStandOnFluid(FluidState p_230426_1_, FlowingFluid p_230426_2_);
}
