package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RedstoneWallTorchBlock extends RedstoneTorchBlock {
   public static final DirectionProperty FACING = HorizontalBlock.FACING;
   public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

   public RedstoneWallTorchBlock(AbstractBlock.Properties p_i48341_1_) {
      super(p_i48341_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, Boolean.valueOf(true)));
   }

   public String getDescriptionId() {
      return this.asItem().getDescriptionId();
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return WallTorchBlock.getShape(p_220053_1_);
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return Blocks.WALL_TORCH.canSurvive(p_196260_1_, p_196260_2_, p_196260_3_);
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return Blocks.WALL_TORCH.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState blockstate = Blocks.WALL_TORCH.getStateForPlacement(p_196258_1_);
      return blockstate == null ? null : this.defaultBlockState().setValue(FACING, blockstate.getValue(FACING));
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (p_180655_1_.getValue(LIT)) {
         Direction direction = p_180655_1_.getValue(FACING).getOpposite();
         double d0 = 0.27D;
         double d1 = (double)p_180655_3_.getX() + 0.5D + (p_180655_4_.nextDouble() - 0.5D) * 0.2D + 0.27D * (double)direction.getStepX();
         double d2 = (double)p_180655_3_.getY() + 0.7D + (p_180655_4_.nextDouble() - 0.5D) * 0.2D + 0.22D;
         double d3 = (double)p_180655_3_.getZ() + 0.5D + (p_180655_4_.nextDouble() - 0.5D) * 0.2D + 0.27D * (double)direction.getStepZ();
         p_180655_2_.addParticle(this.flameParticle, d1, d2, d3, 0.0D, 0.0D, 0.0D);
      }
   }

   protected boolean hasNeighborSignal(World p_176597_1_, BlockPos p_176597_2_, BlockState p_176597_3_) {
      Direction direction = p_176597_3_.getValue(FACING).getOpposite();
      return p_176597_1_.hasSignal(p_176597_2_.relative(direction), direction);
   }

   public int getSignal(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      return p_180656_1_.getValue(LIT) && p_180656_1_.getValue(FACING) != p_180656_4_ ? 15 : 0;
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return Blocks.WALL_TORCH.rotate(p_185499_1_, p_185499_2_);
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return Blocks.WALL_TORCH.mirror(p_185471_1_, p_185471_2_);
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, LIT);
   }
}
