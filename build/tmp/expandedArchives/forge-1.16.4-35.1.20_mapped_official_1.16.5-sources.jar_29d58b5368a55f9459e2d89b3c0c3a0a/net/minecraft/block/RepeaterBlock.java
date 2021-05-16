package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RepeaterBlock extends RedstoneDiodeBlock {
   public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;
   public static final IntegerProperty DELAY = BlockStateProperties.DELAY;

   public RepeaterBlock(AbstractBlock.Properties p_i48340_1_) {
      super(p_i48340_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(DELAY, Integer.valueOf(1)).setValue(LOCKED, Boolean.valueOf(false)).setValue(POWERED, Boolean.valueOf(false)));
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (!p_225533_4_.abilities.mayBuild) {
         return ActionResultType.PASS;
      } else {
         p_225533_2_.setBlock(p_225533_3_, p_225533_1_.cycle(DELAY), 3);
         return ActionResultType.sidedSuccess(p_225533_2_.isClientSide);
      }
   }

   protected int getDelay(BlockState p_196346_1_) {
      return p_196346_1_.getValue(DELAY) * 2;
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState blockstate = super.getStateForPlacement(p_196258_1_);
      return blockstate.setValue(LOCKED, Boolean.valueOf(this.isLocked(p_196258_1_.getLevel(), p_196258_1_.getClickedPos(), blockstate)));
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return !p_196271_4_.isClientSide() && p_196271_2_.getAxis() != p_196271_1_.getValue(FACING).getAxis() ? p_196271_1_.setValue(LOCKED, Boolean.valueOf(this.isLocked(p_196271_4_, p_196271_5_, p_196271_1_))) : super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean isLocked(IWorldReader p_176405_1_, BlockPos p_176405_2_, BlockState p_176405_3_) {
      return this.getAlternateSignal(p_176405_1_, p_176405_2_, p_176405_3_) > 0;
   }

   protected boolean isAlternateInput(BlockState p_185545_1_) {
      return isDiode(p_185545_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if (p_180655_1_.getValue(POWERED)) {
         Direction direction = p_180655_1_.getValue(FACING);
         double d0 = (double)p_180655_3_.getX() + 0.5D + (p_180655_4_.nextDouble() - 0.5D) * 0.2D;
         double d1 = (double)p_180655_3_.getY() + 0.4D + (p_180655_4_.nextDouble() - 0.5D) * 0.2D;
         double d2 = (double)p_180655_3_.getZ() + 0.5D + (p_180655_4_.nextDouble() - 0.5D) * 0.2D;
         float f = -5.0F;
         if (p_180655_4_.nextBoolean()) {
            f = (float)(p_180655_1_.getValue(DELAY) * 2 - 1);
         }

         f = f / 16.0F;
         double d3 = (double)(f * (float)direction.getStepX());
         double d4 = (double)(f * (float)direction.getStepZ());
         p_180655_2_.addParticle(RedstoneParticleData.REDSTONE, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
      }
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, DELAY, LOCKED, POWERED);
   }
}
