package net.minecraft.block;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;

public class OreBlock extends Block {
   public OreBlock(AbstractBlock.Properties p_i48357_1_) {
      super(p_i48357_1_);
   }

   protected int xpOnDrop(Random p_220281_1_) {
      if (this == Blocks.COAL_ORE) {
         return MathHelper.nextInt(p_220281_1_, 0, 2);
      } else if (this == Blocks.DIAMOND_ORE) {
         return MathHelper.nextInt(p_220281_1_, 3, 7);
      } else if (this == Blocks.EMERALD_ORE) {
         return MathHelper.nextInt(p_220281_1_, 3, 7);
      } else if (this == Blocks.LAPIS_ORE) {
         return MathHelper.nextInt(p_220281_1_, 2, 5);
      } else if (this == Blocks.NETHER_QUARTZ_ORE) {
         return MathHelper.nextInt(p_220281_1_, 2, 5);
      } else {
         return this == Blocks.NETHER_GOLD_ORE ? MathHelper.nextInt(p_220281_1_, 0, 1) : 0;
      }
   }

   public void spawnAfterBreak(BlockState p_220062_1_, ServerWorld p_220062_2_, BlockPos p_220062_3_, ItemStack p_220062_4_) {
      super.spawnAfterBreak(p_220062_1_, p_220062_2_, p_220062_3_, p_220062_4_);
   }

   @Override
   public int getExpDrop(BlockState state, net.minecraft.world.IWorldReader reader, BlockPos pos, int fortune, int silktouch) {
      return silktouch == 0 ? this.xpOnDrop(RANDOM) : 0;
   }
}
