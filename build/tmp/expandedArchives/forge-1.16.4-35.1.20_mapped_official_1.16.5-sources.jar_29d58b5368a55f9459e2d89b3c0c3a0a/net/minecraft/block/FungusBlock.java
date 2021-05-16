package net.minecraft.block;

import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.HugeFungusConfig;
import net.minecraft.world.server.ServerWorld;

public class FungusBlock extends BushBlock implements IGrowable {
   protected static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 9.0D, 12.0D);
   private final Supplier<ConfiguredFeature<HugeFungusConfig, ?>> feature;

   public FungusBlock(AbstractBlock.Properties p_i241177_1_, Supplier<ConfiguredFeature<HugeFungusConfig, ?>> p_i241177_2_) {
      super(p_i241177_1_);
      this.feature = p_i241177_2_;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState p_200014_1_, IBlockReader p_200014_2_, BlockPos p_200014_3_) {
      return p_200014_1_.is(BlockTags.NYLIUM) || p_200014_1_.is(Blocks.MYCELIUM) || p_200014_1_.is(Blocks.SOUL_SOIL) || super.mayPlaceOn(p_200014_1_, p_200014_2_, p_200014_3_);
   }

   public boolean isValidBonemealTarget(IBlockReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_) {
      Block block = ((HugeFungusConfig)(this.feature.get()).config).validBaseState.getBlock();
      Block block1 = p_176473_1_.getBlockState(p_176473_2_.below()).getBlock();
      return block1 == block;
   }

   public boolean isBonemealSuccess(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, BlockState p_180670_4_) {
      return (double)p_180670_2_.nextFloat() < 0.4D;
   }

   public void performBonemeal(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_) {
      this.feature.get().place(p_225535_1_, p_225535_1_.getChunkSource().getGenerator(), p_225535_2_, p_225535_3_);
   }
}
