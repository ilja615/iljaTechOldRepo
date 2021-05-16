package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.Queue;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpongeBlock extends Block {
   public SpongeBlock(AbstractBlock.Properties p_i48325_1_) {
      super(p_i48325_1_);
   }

   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (!p_220082_4_.is(p_220082_1_.getBlock())) {
         this.tryAbsorbWater(p_220082_2_, p_220082_3_);
      }
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      this.tryAbsorbWater(p_220069_2_, p_220069_3_);
      super.neighborChanged(p_220069_1_, p_220069_2_, p_220069_3_, p_220069_4_, p_220069_5_, p_220069_6_);
   }

   protected void tryAbsorbWater(World p_196510_1_, BlockPos p_196510_2_) {
      if (this.removeWaterBreadthFirstSearch(p_196510_1_, p_196510_2_)) {
         p_196510_1_.setBlock(p_196510_2_, Blocks.WET_SPONGE.defaultBlockState(), 2);
         p_196510_1_.levelEvent(2001, p_196510_2_, Block.getId(Blocks.WATER.defaultBlockState()));
      }

   }

   private boolean removeWaterBreadthFirstSearch(World p_176312_1_, BlockPos p_176312_2_) {
      Queue<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();
      queue.add(new Tuple<>(p_176312_2_, 0));
      int i = 0;

      while(!queue.isEmpty()) {
         Tuple<BlockPos, Integer> tuple = queue.poll();
         BlockPos blockpos = tuple.getA();
         int j = tuple.getB();

         for(Direction direction : Direction.values()) {
            BlockPos blockpos1 = blockpos.relative(direction);
            BlockState blockstate = p_176312_1_.getBlockState(blockpos1);
            FluidState fluidstate = p_176312_1_.getFluidState(blockpos1);
            Material material = blockstate.getMaterial();
            if (fluidstate.is(FluidTags.WATER)) {
               if (blockstate.getBlock() instanceof IBucketPickupHandler && ((IBucketPickupHandler)blockstate.getBlock()).takeLiquid(p_176312_1_, blockpos1, blockstate) != Fluids.EMPTY) {
                  ++i;
                  if (j < 6) {
                     queue.add(new Tuple<>(blockpos1, j + 1));
                  }
               } else if (blockstate.getBlock() instanceof FlowingFluidBlock) {
                  p_176312_1_.setBlock(blockpos1, Blocks.AIR.defaultBlockState(), 3);
                  ++i;
                  if (j < 6) {
                     queue.add(new Tuple<>(blockpos1, j + 1));
                  }
               } else if (material == Material.WATER_PLANT || material == Material.REPLACEABLE_WATER_PLANT) {
                  TileEntity tileentity = blockstate.hasTileEntity() ? p_176312_1_.getBlockEntity(blockpos1) : null;
                  dropResources(blockstate, p_176312_1_, blockpos1, tileentity);
                  p_176312_1_.setBlock(blockpos1, Blocks.AIR.defaultBlockState(), 3);
                  ++i;
                  if (j < 6) {
                     queue.add(new Tuple<>(blockpos1, j + 1));
                  }
               }
            }
         }

         if (i > 64) {
            break;
         }
      }

      return i > 0;
   }
}
