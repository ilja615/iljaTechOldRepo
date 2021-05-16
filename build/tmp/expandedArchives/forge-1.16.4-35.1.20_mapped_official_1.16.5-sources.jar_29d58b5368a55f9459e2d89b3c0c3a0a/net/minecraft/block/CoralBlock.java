package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;

public class CoralBlock extends Block {
   private final Block deadBlock;

   public CoralBlock(Block p_i48893_1_, AbstractBlock.Properties p_i48893_2_) {
      super(p_i48893_2_);
      this.deadBlock = p_i48893_1_;
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (!this.scanForWater(p_225534_2_, p_225534_3_)) {
         p_225534_2_.setBlock(p_225534_3_, this.deadBlock.defaultBlockState(), 2);
      }

   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (!this.scanForWater(p_196271_4_, p_196271_5_)) {
         p_196271_4_.getBlockTicks().scheduleTick(p_196271_5_, this, 60 + p_196271_4_.getRandom().nextInt(40));
      }

      return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   protected boolean scanForWater(IBlockReader p_203943_1_, BlockPos p_203943_2_) {
      for(Direction direction : Direction.values()) {
         FluidState fluidstate = p_203943_1_.getFluidState(p_203943_2_.relative(direction));
         if (fluidstate.is(FluidTags.WATER)) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      if (!this.scanForWater(p_196258_1_.getLevel(), p_196258_1_.getClickedPos())) {
         p_196258_1_.getLevel().getBlockTicks().scheduleTick(p_196258_1_.getClickedPos(), this, 60 + p_196258_1_.getLevel().getRandom().nextInt(40));
      }

      return this.defaultBlockState();
   }
}
