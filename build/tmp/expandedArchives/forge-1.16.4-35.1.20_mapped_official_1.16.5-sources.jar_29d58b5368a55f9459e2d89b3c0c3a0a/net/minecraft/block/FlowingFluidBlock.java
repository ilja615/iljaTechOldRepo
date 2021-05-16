package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FlowingFluidBlock extends Block implements IBucketPickupHandler {
   public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL;
   private final FlowingFluid fluid;
   private final List<FluidState> stateCache;
   public static final VoxelShape STABLE_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);

   @Deprecated  // Forge: Use the constructor that takes a supplier
   public FlowingFluidBlock(FlowingFluid p_i49014_1_, AbstractBlock.Properties p_i49014_2_) {
      super(p_i49014_2_);
      this.fluid = p_i49014_1_;
      this.stateCache = Lists.newArrayList();
      this.stateCache.add(p_i49014_1_.getSource(false));

      for(int i = 1; i < 8; ++i) {
         this.stateCache.add(p_i49014_1_.getFlowing(8 - i, false));
      }

      this.stateCache.add(p_i49014_1_.getFlowing(8, true));
      this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, Integer.valueOf(0)));
      fluidStateCacheInitialized = true;
      supplier = p_i49014_1_.delegate;
   }

   /**
    * @param supplier A fluid supplier such as {@link net.minecraftforge.fml.RegistryObject<Fluid>}
    */
   public FlowingFluidBlock(java.util.function.Supplier<? extends FlowingFluid> supplier, AbstractBlock.Properties p_i48368_1_) {
      super(p_i48368_1_);
      this.fluid = null;
      this.stateCache = Lists.newArrayList();
      this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, Integer.valueOf(0)));
      this.supplier = supplier;
   }

   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      return p_220071_4_.isAbove(STABLE_SHAPE, p_220071_3_, true) && p_220071_1_.getValue(LEVEL) == 0 && p_220071_4_.canStandOnFluid(p_220071_2_.getFluidState(p_220071_3_.above()), this.fluid) ? STABLE_SHAPE : VoxelShapes.empty();
   }

   public boolean isRandomlyTicking(BlockState p_149653_1_) {
      return p_149653_1_.getFluidState().isRandomlyTicking();
   }

   public void randomTick(BlockState p_225542_1_, ServerWorld p_225542_2_, BlockPos p_225542_3_, Random p_225542_4_) {
      p_225542_1_.getFluidState().randomTick(p_225542_2_, p_225542_3_, p_225542_4_);
   }

   public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
      return false;
   }

   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return !this.fluid.is(FluidTags.LAVA);
   }

   public FluidState getFluidState(BlockState p_204507_1_) {
      int i = p_204507_1_.getValue(LEVEL);
      if (!fluidStateCacheInitialized) initFluidStateCache();
      return this.stateCache.get(Math.min(i, 8));
   }

   @OnlyIn(Dist.CLIENT)
   public boolean skipRendering(BlockState p_200122_1_, BlockState p_200122_2_, Direction p_200122_3_) {
      return p_200122_2_.getFluidState().getType().isSame(this.fluid);
   }

   public BlockRenderType getRenderShape(BlockState p_149645_1_) {
      return BlockRenderType.INVISIBLE;
   }

   public List<ItemStack> getDrops(BlockState p_220076_1_, LootContext.Builder p_220076_2_) {
      return Collections.emptyList();
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return VoxelShapes.empty();
   }

   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (this.shouldSpreadLiquid(p_220082_2_, p_220082_3_, p_220082_1_)) {
         p_220082_2_.getLiquidTicks().scheduleTick(p_220082_3_, p_220082_1_.getFluidState().getType(), this.fluid.getTickDelay(p_220082_2_));
      }

   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_1_.getFluidState().isSource() || p_196271_3_.getFluidState().isSource()) {
         p_196271_4_.getLiquidTicks().scheduleTick(p_196271_5_, p_196271_1_.getFluidState().getType(), this.fluid.getTickDelay(p_196271_4_));
      }

      return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (this.shouldSpreadLiquid(p_220069_2_, p_220069_3_, p_220069_1_)) {
         p_220069_2_.getLiquidTicks().scheduleTick(p_220069_3_, p_220069_1_.getFluidState().getType(), this.fluid.getTickDelay(p_220069_2_));
      }

   }

   private boolean shouldSpreadLiquid(World p_204515_1_, BlockPos p_204515_2_, BlockState p_204515_3_) {
      if (this.fluid.is(FluidTags.LAVA)) {
         boolean flag = p_204515_1_.getBlockState(p_204515_2_.below()).is(Blocks.SOUL_SOIL);

         for(Direction direction : Direction.values()) {
            if (direction != Direction.DOWN) {
               BlockPos blockpos = p_204515_2_.relative(direction);
               if (p_204515_1_.getFluidState(blockpos).is(FluidTags.WATER)) {
                  Block block = p_204515_1_.getFluidState(p_204515_2_).isSource() ? Blocks.OBSIDIAN : Blocks.COBBLESTONE;
                  p_204515_1_.setBlockAndUpdate(p_204515_2_, net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(p_204515_1_, p_204515_2_, p_204515_2_, block.defaultBlockState()));
                  this.fizz(p_204515_1_, p_204515_2_);
                  return false;
               }

               if (flag && p_204515_1_.getBlockState(blockpos).is(Blocks.BLUE_ICE)) {
                  p_204515_1_.setBlockAndUpdate(p_204515_2_, net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(p_204515_1_, p_204515_2_, p_204515_2_, Blocks.BASALT.defaultBlockState()));
                  this.fizz(p_204515_1_, p_204515_2_);
                  return false;
               }
            }
         }
      }

      return true;
   }

   private void fizz(IWorld p_180688_1_, BlockPos p_180688_2_) {
      p_180688_1_.levelEvent(1501, p_180688_2_, 0);
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(LEVEL);
   }

   public Fluid takeLiquid(IWorld p_204508_1_, BlockPos p_204508_2_, BlockState p_204508_3_) {
      if (p_204508_3_.getValue(LEVEL) == 0) {
         p_204508_1_.setBlock(p_204508_2_, Blocks.AIR.defaultBlockState(), 11);
         return this.fluid;
      } else {
         return Fluids.EMPTY;
      }
   }

   // Forge start
   private final java.util.function.Supplier<? extends Fluid> supplier;
   public FlowingFluid getFluid() {
      return (FlowingFluid)supplier.get();
   }

   private boolean fluidStateCacheInitialized = false;
   protected synchronized void initFluidStateCache() {
      if (fluidStateCacheInitialized == false) {
         this.stateCache.add(getFluid().getSource(false));

         for (int i = 1; i < 8; ++i)
            this.stateCache.add(getFluid().getFlowing(8 - i, false));

         this.stateCache.add(getFluid().getFlowing(8, true));
         fluidStateCacheInitialized = true;
      }
   }
}
