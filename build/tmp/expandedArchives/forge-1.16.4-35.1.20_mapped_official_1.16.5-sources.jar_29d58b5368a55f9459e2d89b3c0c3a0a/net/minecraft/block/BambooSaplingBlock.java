package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.state.properties.BambooLeaves;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BambooSaplingBlock extends Block implements IGrowable {
   protected static final VoxelShape SAPLING_SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D);

   public BambooSaplingBlock(AbstractBlock.Properties p_i49997_1_) {
      super(p_i49997_1_);
   }

   public AbstractBlock.OffsetType getOffsetType() {
      return AbstractBlock.OffsetType.XZ;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      Vector3d vector3d = p_220053_1_.getOffset(p_220053_2_, p_220053_3_);
      return SAPLING_SHAPE.move(vector3d.x, vector3d.y, vector3d.z);
   }

   public void randomTick(BlockState p_225542_1_, ServerWorld p_225542_2_, BlockPos p_225542_3_, Random p_225542_4_) {
      if (p_225542_4_.nextInt(3) == 0 && p_225542_2_.isEmptyBlock(p_225542_3_.above()) && p_225542_2_.getRawBrightness(p_225542_3_.above(), 0) >= 9) {
         this.growBamboo(p_225542_2_, p_225542_3_);
      }

   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return p_196260_2_.getBlockState(p_196260_3_.below()).is(BlockTags.BAMBOO_PLANTABLE_ON);
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (!p_196271_1_.canSurvive(p_196271_4_, p_196271_5_)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if (p_196271_2_ == Direction.UP && p_196271_3_.is(Blocks.BAMBOO)) {
            p_196271_4_.setBlock(p_196271_5_, Blocks.BAMBOO.defaultBlockState(), 2);
         }

         return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      }
   }

   public ItemStack getCloneItemStack(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      return new ItemStack(Items.BAMBOO);
   }

   public boolean isValidBonemealTarget(IBlockReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_) {
      return p_176473_1_.getBlockState(p_176473_2_.above()).isAir();
   }

   public boolean isBonemealSuccess(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, BlockState p_180670_4_) {
      return true;
   }

   public void performBonemeal(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_) {
      this.growBamboo(p_225535_1_, p_225535_3_);
   }

   public float getDestroyProgress(BlockState p_180647_1_, PlayerEntity p_180647_2_, IBlockReader p_180647_3_, BlockPos p_180647_4_) {
      return p_180647_2_.getMainHandItem().getItem() instanceof SwordItem ? 1.0F : super.getDestroyProgress(p_180647_1_, p_180647_2_, p_180647_3_, p_180647_4_);
   }

   protected void growBamboo(World p_220087_1_, BlockPos p_220087_2_) {
      p_220087_1_.setBlock(p_220087_2_.above(), Blocks.BAMBOO.defaultBlockState().setValue(BambooBlock.LEAVES, BambooLeaves.SMALL), 3);
   }
}
