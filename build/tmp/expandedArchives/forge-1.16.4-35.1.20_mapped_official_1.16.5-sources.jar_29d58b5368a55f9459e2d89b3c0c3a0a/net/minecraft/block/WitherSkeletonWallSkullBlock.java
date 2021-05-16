package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WitherSkeletonWallSkullBlock extends WallSkullBlock {
   public WitherSkeletonWallSkullBlock(AbstractBlock.Properties p_i48292_1_) {
      super(SkullBlock.Types.WITHER_SKELETON, p_i48292_1_);
   }

   public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, @Nullable LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      Blocks.WITHER_SKELETON_SKULL.setPlacedBy(p_180633_1_, p_180633_2_, p_180633_3_, p_180633_4_, p_180633_5_);
   }
}
