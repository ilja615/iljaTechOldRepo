package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class HorizontalFaceBlock extends HorizontalBlock {
   public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;

   public HorizontalFaceBlock(AbstractBlock.Properties p_i48402_1_) {
      super(p_i48402_1_);
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return canAttach(p_196260_2_, p_196260_3_, getConnectedDirection(p_196260_1_).getOpposite());
   }

   public static boolean canAttach(IWorldReader p_220185_0_, BlockPos p_220185_1_, Direction p_220185_2_) {
      BlockPos blockpos = p_220185_1_.relative(p_220185_2_);
      return p_220185_0_.getBlockState(blockpos).isFaceSturdy(p_220185_0_, blockpos, p_220185_2_.getOpposite());
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      for(Direction direction : p_196258_1_.getNearestLookingDirections()) {
         BlockState blockstate;
         if (direction.getAxis() == Direction.Axis.Y) {
            blockstate = this.defaultBlockState().setValue(FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR).setValue(FACING, p_196258_1_.getHorizontalDirection());
         } else {
            blockstate = this.defaultBlockState().setValue(FACE, AttachFace.WALL).setValue(FACING, direction.getOpposite());
         }

         if (blockstate.canSurvive(p_196258_1_.getLevel(), p_196258_1_.getClickedPos())) {
            return blockstate;
         }
      }

      return null;
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return getConnectedDirection(p_196271_1_).getOpposite() == p_196271_2_ && !p_196271_1_.canSurvive(p_196271_4_, p_196271_5_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   protected static Direction getConnectedDirection(BlockState p_196365_0_) {
      switch((AttachFace)p_196365_0_.getValue(FACE)) {
      case CEILING:
         return Direction.DOWN;
      case FLOOR:
         return Direction.UP;
      default:
         return p_196365_0_.getValue(FACING);
      }
   }
}
