package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class StemBlock extends BushBlock implements IGrowable {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
   protected static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(7.0D, 0.0D, 7.0D, 9.0D, 2.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 4.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 6.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 8.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 10.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 12.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 14.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D)};
   private final StemGrownBlock fruit;

   public StemBlock(StemGrownBlock p_i48318_1_, AbstractBlock.Properties p_i48318_2_) {
      super(p_i48318_2_);
      this.fruit = p_i48318_1_;
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE_BY_AGE[p_220053_1_.getValue(AGE)];
   }

   protected boolean mayPlaceOn(BlockState p_200014_1_, IBlockReader p_200014_2_, BlockPos p_200014_3_) {
      return p_200014_1_.is(Blocks.FARMLAND);
   }

   public void randomTick(BlockState p_225542_1_, ServerWorld p_225542_2_, BlockPos p_225542_3_, Random p_225542_4_) {
      if (!p_225542_2_.isAreaLoaded(p_225542_3_, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
      if (p_225542_2_.getRawBrightness(p_225542_3_, 0) >= 9) {
         float f = CropsBlock.getGrowthSpeed(this, p_225542_2_, p_225542_3_);
         if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_225542_2_, p_225542_3_, p_225542_1_, p_225542_4_.nextInt((int)(25.0F / f) + 1) == 0)) {
            int i = p_225542_1_.getValue(AGE);
            if (i < 7) {
               p_225542_2_.setBlock(p_225542_3_, p_225542_1_.setValue(AGE, Integer.valueOf(i + 1)), 2);
            } else {
               Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(p_225542_4_);
               BlockPos blockpos = p_225542_3_.relative(direction);
               BlockState blockstate = p_225542_2_.getBlockState(blockpos.below());
               Block block = blockstate.getBlock();
               if (p_225542_2_.isEmptyBlock(blockpos) && (blockstate.canSustainPlant(p_225542_2_, blockpos.below(), Direction.UP, this) || block == Blocks.FARMLAND || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.GRASS_BLOCK)) {
                  p_225542_2_.setBlockAndUpdate(blockpos, this.fruit.defaultBlockState());
                  p_225542_2_.setBlockAndUpdate(p_225542_3_, this.fruit.getAttachedStem().defaultBlockState().setValue(HorizontalBlock.FACING, direction));
               }
            }
            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_225542_2_, p_225542_3_, p_225542_1_);
         }

      }
   }

   @Nullable
   protected Item getSeedItem() {
      if (this.fruit == Blocks.PUMPKIN) {
         return Items.PUMPKIN_SEEDS;
      } else {
         return this.fruit == Blocks.MELON ? Items.MELON_SEEDS : null;
      }
   }

   public ItemStack getCloneItemStack(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      Item item = this.getSeedItem();
      return item == null ? ItemStack.EMPTY : new ItemStack(item);
   }

   public boolean isValidBonemealTarget(IBlockReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_) {
      return p_176473_3_.getValue(AGE) != 7;
   }

   public boolean isBonemealSuccess(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, BlockState p_180670_4_) {
      return true;
   }

   public void performBonemeal(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_) {
      int i = Math.min(7, p_225535_4_.getValue(AGE) + MathHelper.nextInt(p_225535_1_.random, 2, 5));
      BlockState blockstate = p_225535_4_.setValue(AGE, Integer.valueOf(i));
      p_225535_1_.setBlock(p_225535_3_, blockstate, 2);
      if (i == 7) {
         blockstate.randomTick(p_225535_1_, p_225535_3_, p_225535_1_.random);
      }

   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(AGE);
   }

   public StemGrownBlock getFruit() {
      return this.fruit;
   }

   //FORGE START
   @Override
   public net.minecraftforge.common.PlantType getPlantType(IBlockReader world, BlockPos pos) {
      return net.minecraftforge.common.PlantType.CROP;
   }
}
