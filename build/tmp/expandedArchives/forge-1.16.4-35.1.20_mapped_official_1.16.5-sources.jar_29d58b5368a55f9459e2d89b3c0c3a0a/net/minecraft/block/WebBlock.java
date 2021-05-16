package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class WebBlock extends Block implements net.minecraftforge.common.IForgeShearable {
   public WebBlock(AbstractBlock.Properties p_i48296_1_) {
      super(p_i48296_1_);
   }

   public void entityInside(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      p_196262_4_.makeStuckInBlock(p_196262_1_, new Vector3d(0.25D, (double)0.05F, 0.25D));
   }
}
