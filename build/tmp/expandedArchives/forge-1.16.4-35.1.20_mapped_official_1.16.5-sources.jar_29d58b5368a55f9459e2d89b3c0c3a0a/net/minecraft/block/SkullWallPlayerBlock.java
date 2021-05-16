package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SkullWallPlayerBlock extends WallSkullBlock {
   public SkullWallPlayerBlock(AbstractBlock.Properties p_i48353_1_) {
      super(SkullBlock.Types.PLAYER, p_i48353_1_);
   }

   public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, @Nullable LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      Blocks.PLAYER_HEAD.setPlacedBy(p_180633_1_, p_180633_2_, p_180633_3_, p_180633_4_, p_180633_5_);
   }

   public List<ItemStack> getDrops(BlockState p_220076_1_, LootContext.Builder p_220076_2_) {
      return Blocks.PLAYER_HEAD.getDrops(p_220076_1_, p_220076_2_);
   }
}
