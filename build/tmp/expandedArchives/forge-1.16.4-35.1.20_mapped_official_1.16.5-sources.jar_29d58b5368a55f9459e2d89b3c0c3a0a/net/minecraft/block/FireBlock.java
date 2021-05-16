package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class FireBlock extends AbstractFireBlock {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_15;
   public static final BooleanProperty NORTH = SixWayBlock.NORTH;
   public static final BooleanProperty EAST = SixWayBlock.EAST;
   public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
   public static final BooleanProperty WEST = SixWayBlock.WEST;
   public static final BooleanProperty UP = SixWayBlock.UP;
   private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = SixWayBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((p_199776_0_) -> {
      return p_199776_0_.getKey() != Direction.DOWN;
   }).collect(Util.toMap());
   private static final VoxelShape UP_AABB = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   private static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
   private static final VoxelShape EAST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   private static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
   private static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
   private final Map<BlockState, VoxelShape> shapesCache;
   private final Object2IntMap<Block> flameOdds = new Object2IntOpenHashMap<>();
   private final Object2IntMap<Block> burnOdds = new Object2IntOpenHashMap<>();

   public FireBlock(AbstractBlock.Properties p_i48397_1_) {
      super(p_i48397_1_, 1.0F);
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)).setValue(NORTH, Boolean.valueOf(false)).setValue(EAST, Boolean.valueOf(false)).setValue(SOUTH, Boolean.valueOf(false)).setValue(WEST, Boolean.valueOf(false)).setValue(UP, Boolean.valueOf(false)));
      this.shapesCache = ImmutableMap.copyOf(this.stateDefinition.getPossibleStates().stream().filter((p_242674_0_) -> {
         return p_242674_0_.getValue(AGE) == 0;
      }).collect(Collectors.toMap(Function.identity(), FireBlock::calculateShape)));
   }

   private static VoxelShape calculateShape(BlockState p_242673_0_) {
      VoxelShape voxelshape = VoxelShapes.empty();
      if (p_242673_0_.getValue(UP)) {
         voxelshape = UP_AABB;
      }

      if (p_242673_0_.getValue(NORTH)) {
         voxelshape = VoxelShapes.or(voxelshape, NORTH_AABB);
      }

      if (p_242673_0_.getValue(SOUTH)) {
         voxelshape = VoxelShapes.or(voxelshape, SOUTH_AABB);
      }

      if (p_242673_0_.getValue(EAST)) {
         voxelshape = VoxelShapes.or(voxelshape, EAST_AABB);
      }

      if (p_242673_0_.getValue(WEST)) {
         voxelshape = VoxelShapes.or(voxelshape, WEST_AABB);
      }

      return voxelshape.isEmpty() ? DOWN_AABB : voxelshape;
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return this.canSurvive(p_196271_1_, p_196271_4_, p_196271_5_) ? this.getStateWithAge(p_196271_4_, p_196271_5_, p_196271_1_.getValue(AGE)) : Blocks.AIR.defaultBlockState();
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return this.shapesCache.get(p_220053_1_.setValue(AGE, Integer.valueOf(0)));
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.getStateForPlacement(p_196258_1_.getLevel(), p_196258_1_.getClickedPos());
   }

   protected BlockState getStateForPlacement(IBlockReader p_196448_1_, BlockPos p_196448_2_) {
      BlockPos blockpos = p_196448_2_.below();
      BlockState blockstate = p_196448_1_.getBlockState(blockpos);
      if (!this.canCatchFire(p_196448_1_, p_196448_2_, Direction.UP) && !blockstate.isFaceSturdy(p_196448_1_, blockpos, Direction.UP)) {
         BlockState blockstate1 = this.defaultBlockState();

         for(Direction direction : Direction.values()) {
            BooleanProperty booleanproperty = PROPERTY_BY_DIRECTION.get(direction);
            if (booleanproperty != null) {
               blockstate1 = blockstate1.setValue(booleanproperty, Boolean.valueOf(this.canCatchFire(p_196448_1_, p_196448_2_.relative(direction), direction.getOpposite())));
            }
         }

         return blockstate1;
      } else {
         return this.defaultBlockState();
      }
   }

   public boolean canSurvive(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockPos blockpos = p_196260_3_.below();
      return p_196260_2_.getBlockState(blockpos).isFaceSturdy(p_196260_2_, blockpos, Direction.UP) || this.isValidFireLocation(p_196260_2_, p_196260_3_);
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      p_225534_2_.getBlockTicks().scheduleTick(p_225534_3_, this, getFireTickDelay(p_225534_2_.random));
      if (p_225534_2_.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
         if (!p_225534_1_.canSurvive(p_225534_2_, p_225534_3_)) {
            p_225534_2_.removeBlock(p_225534_3_, false);
         }

         BlockState blockstate = p_225534_2_.getBlockState(p_225534_3_.below());
         boolean flag = blockstate.isFireSource(p_225534_2_, p_225534_3_, Direction.UP);
         int i = p_225534_1_.getValue(AGE);
         if (!flag && p_225534_2_.isRaining() && this.isNearRain(p_225534_2_, p_225534_3_) && p_225534_4_.nextFloat() < 0.2F + (float)i * 0.03F) {
            p_225534_2_.removeBlock(p_225534_3_, false);
         } else {
            int j = Math.min(15, i + p_225534_4_.nextInt(3) / 2);
            if (i != j) {
               p_225534_1_ = p_225534_1_.setValue(AGE, Integer.valueOf(j));
               p_225534_2_.setBlock(p_225534_3_, p_225534_1_, 4);
            }

            if (!flag) {
               if (!this.isValidFireLocation(p_225534_2_, p_225534_3_)) {
                  BlockPos blockpos = p_225534_3_.below();
                  if (!p_225534_2_.getBlockState(blockpos).isFaceSturdy(p_225534_2_, blockpos, Direction.UP) || i > 3) {
                     p_225534_2_.removeBlock(p_225534_3_, false);
                  }

                  return;
               }

               if (i == 15 && p_225534_4_.nextInt(4) == 0 && !this.canCatchFire(p_225534_2_, p_225534_3_.below(), Direction.UP)) {
                  p_225534_2_.removeBlock(p_225534_3_, false);
                  return;
               }
            }

            boolean flag1 = p_225534_2_.isHumidAt(p_225534_3_);
            int k = flag1 ? -50 : 0;
            this.tryCatchFire(p_225534_2_, p_225534_3_.east(), 300 + k, p_225534_4_, i, Direction.WEST);
            this.tryCatchFire(p_225534_2_, p_225534_3_.west(), 300 + k, p_225534_4_, i, Direction.EAST);
            this.tryCatchFire(p_225534_2_, p_225534_3_.below(), 250 + k, p_225534_4_, i, Direction.UP);
            this.tryCatchFire(p_225534_2_, p_225534_3_.above(), 250 + k, p_225534_4_, i, Direction.DOWN);
            this.tryCatchFire(p_225534_2_, p_225534_3_.north(), 300 + k, p_225534_4_, i, Direction.SOUTH);
            this.tryCatchFire(p_225534_2_, p_225534_3_.south(), 300 + k, p_225534_4_, i, Direction.NORTH);
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

            for(int l = -1; l <= 1; ++l) {
               for(int i1 = -1; i1 <= 1; ++i1) {
                  for(int j1 = -1; j1 <= 4; ++j1) {
                     if (l != 0 || j1 != 0 || i1 != 0) {
                        int k1 = 100;
                        if (j1 > 1) {
                           k1 += (j1 - 1) * 100;
                        }

                        blockpos$mutable.setWithOffset(p_225534_3_, l, j1, i1);
                        int l1 = this.getFireOdds(p_225534_2_, blockpos$mutable);
                        if (l1 > 0) {
                           int i2 = (l1 + 40 + p_225534_2_.getDifficulty().getId() * 7) / (i + 30);
                           if (flag1) {
                              i2 /= 2;
                           }

                           if (i2 > 0 && p_225534_4_.nextInt(k1) <= i2 && (!p_225534_2_.isRaining() || !this.isNearRain(p_225534_2_, blockpos$mutable))) {
                              int j2 = Math.min(15, i + p_225534_4_.nextInt(5) / 4);
                              p_225534_2_.setBlock(blockpos$mutable, this.getStateWithAge(p_225534_2_, blockpos$mutable, j2), 3);
                           }
                        }
                     }
                  }
               }
            }

         }
      }
   }

   protected boolean isNearRain(World p_176537_1_, BlockPos p_176537_2_) {
      return p_176537_1_.isRainingAt(p_176537_2_) || p_176537_1_.isRainingAt(p_176537_2_.west()) || p_176537_1_.isRainingAt(p_176537_2_.east()) || p_176537_1_.isRainingAt(p_176537_2_.north()) || p_176537_1_.isRainingAt(p_176537_2_.south());
   }

   @Deprecated //Forge: Use IForgeBlockState.getFlammability, Public for default implementation only.
   public int getBurnOdd(BlockState p_220274_1_) {
      return p_220274_1_.hasProperty(BlockStateProperties.WATERLOGGED) && p_220274_1_.getValue(BlockStateProperties.WATERLOGGED) ? 0 : this.burnOdds.getInt(p_220274_1_.getBlock());
   }

   @Deprecated //Forge: Use IForgeBlockState.getFireSpreadSpeed
   public int getFlameOdds(BlockState p_220275_1_) {
      return p_220275_1_.hasProperty(BlockStateProperties.WATERLOGGED) && p_220275_1_.getValue(BlockStateProperties.WATERLOGGED) ? 0 : this.flameOdds.getInt(p_220275_1_.getBlock());
   }

   private void tryCatchFire(World p_176536_1_, BlockPos p_176536_2_, int p_176536_3_, Random p_176536_4_, int p_176536_5_, Direction face) {
      int i = p_176536_1_.getBlockState(p_176536_2_).getFlammability(p_176536_1_, p_176536_2_, face);
      if (p_176536_4_.nextInt(p_176536_3_) < i) {
         BlockState blockstate = p_176536_1_.getBlockState(p_176536_2_);
         if (p_176536_4_.nextInt(p_176536_5_ + 10) < 5 && !p_176536_1_.isRainingAt(p_176536_2_)) {
            int j = Math.min(p_176536_5_ + p_176536_4_.nextInt(5) / 4, 15);
            p_176536_1_.setBlock(p_176536_2_, this.getStateWithAge(p_176536_1_, p_176536_2_, j), 3);
         } else {
            p_176536_1_.removeBlock(p_176536_2_, false);
         }

         blockstate.catchFire(p_176536_1_, p_176536_2_, face, null);
      }

   }

   private BlockState getStateWithAge(IWorld p_235494_1_, BlockPos p_235494_2_, int p_235494_3_) {
      BlockState blockstate = getState(p_235494_1_, p_235494_2_);
      return blockstate.is(Blocks.FIRE) ? blockstate.setValue(AGE, Integer.valueOf(p_235494_3_)) : blockstate;
   }

   private boolean isValidFireLocation(IBlockReader p_196447_1_, BlockPos p_196447_2_) {
      for(Direction direction : Direction.values()) {
         if (this.canCatchFire(p_196447_1_, p_196447_2_.relative(direction), direction.getOpposite())) {
            return true;
         }
      }

      return false;
   }

   private int getFireOdds(IWorldReader p_176538_1_, BlockPos p_176538_2_) {
      if (!p_176538_1_.isEmptyBlock(p_176538_2_)) {
         return 0;
      } else {
         int i = 0;

         for(Direction direction : Direction.values()) {
            BlockState blockstate = p_176538_1_.getBlockState(p_176538_2_.relative(direction));
            i = Math.max(blockstate.getFireSpreadSpeed(p_176538_1_, p_176538_2_.relative(direction), direction.getOpposite()), i);
         }

         return i;
      }
   }

   @Deprecated //Forge: Use canCatchFire with more context
   protected boolean canBurn(BlockState p_196446_1_) {
      return this.getFlameOdds(p_196446_1_) > 0;
   }

   public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      super.onPlace(p_220082_1_, p_220082_2_, p_220082_3_, p_220082_4_, p_220082_5_);
      p_220082_2_.getBlockTicks().scheduleTick(p_220082_3_, this, getFireTickDelay(p_220082_2_.random));
   }

   private static int getFireTickDelay(Random p_235495_0_) {
      return 30 + p_235495_0_.nextInt(10);
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(AGE, NORTH, EAST, SOUTH, WEST, UP);
   }

   private void setFlammable(Block p_180686_1_, int p_180686_2_, int p_180686_3_) {
      if (p_180686_1_ == Blocks.AIR) throw new IllegalArgumentException("Tried to set air on fire... This is bad.");
      this.flameOdds.put(p_180686_1_, p_180686_2_);
      this.burnOdds.put(p_180686_1_, p_180686_3_);
   }

   /**
    * Side sensitive version that calls the block function.
    *
    * @param world The current world
    * @param pos Block position
    * @param face The side the fire is coming from
    * @return True if the face can catch fire.
    */
   public boolean canCatchFire(IBlockReader world, BlockPos pos, Direction face) {
      return world.getBlockState(pos).isFlammable(world, pos, face);
   }

   public static void bootStrap() {
      FireBlock fireblock = (FireBlock)Blocks.FIRE;
      fireblock.setFlammable(Blocks.OAK_PLANKS, 5, 20);
      fireblock.setFlammable(Blocks.SPRUCE_PLANKS, 5, 20);
      fireblock.setFlammable(Blocks.BIRCH_PLANKS, 5, 20);
      fireblock.setFlammable(Blocks.JUNGLE_PLANKS, 5, 20);
      fireblock.setFlammable(Blocks.ACACIA_PLANKS, 5, 20);
      fireblock.setFlammable(Blocks.DARK_OAK_PLANKS, 5, 20);
      fireblock.setFlammable(Blocks.OAK_SLAB, 5, 20);
      fireblock.setFlammable(Blocks.SPRUCE_SLAB, 5, 20);
      fireblock.setFlammable(Blocks.BIRCH_SLAB, 5, 20);
      fireblock.setFlammable(Blocks.JUNGLE_SLAB, 5, 20);
      fireblock.setFlammable(Blocks.ACACIA_SLAB, 5, 20);
      fireblock.setFlammable(Blocks.DARK_OAK_SLAB, 5, 20);
      fireblock.setFlammable(Blocks.OAK_FENCE_GATE, 5, 20);
      fireblock.setFlammable(Blocks.SPRUCE_FENCE_GATE, 5, 20);
      fireblock.setFlammable(Blocks.BIRCH_FENCE_GATE, 5, 20);
      fireblock.setFlammable(Blocks.JUNGLE_FENCE_GATE, 5, 20);
      fireblock.setFlammable(Blocks.DARK_OAK_FENCE_GATE, 5, 20);
      fireblock.setFlammable(Blocks.ACACIA_FENCE_GATE, 5, 20);
      fireblock.setFlammable(Blocks.OAK_FENCE, 5, 20);
      fireblock.setFlammable(Blocks.SPRUCE_FENCE, 5, 20);
      fireblock.setFlammable(Blocks.BIRCH_FENCE, 5, 20);
      fireblock.setFlammable(Blocks.JUNGLE_FENCE, 5, 20);
      fireblock.setFlammable(Blocks.DARK_OAK_FENCE, 5, 20);
      fireblock.setFlammable(Blocks.ACACIA_FENCE, 5, 20);
      fireblock.setFlammable(Blocks.OAK_STAIRS, 5, 20);
      fireblock.setFlammable(Blocks.BIRCH_STAIRS, 5, 20);
      fireblock.setFlammable(Blocks.SPRUCE_STAIRS, 5, 20);
      fireblock.setFlammable(Blocks.JUNGLE_STAIRS, 5, 20);
      fireblock.setFlammable(Blocks.ACACIA_STAIRS, 5, 20);
      fireblock.setFlammable(Blocks.DARK_OAK_STAIRS, 5, 20);
      fireblock.setFlammable(Blocks.OAK_LOG, 5, 5);
      fireblock.setFlammable(Blocks.SPRUCE_LOG, 5, 5);
      fireblock.setFlammable(Blocks.BIRCH_LOG, 5, 5);
      fireblock.setFlammable(Blocks.JUNGLE_LOG, 5, 5);
      fireblock.setFlammable(Blocks.ACACIA_LOG, 5, 5);
      fireblock.setFlammable(Blocks.DARK_OAK_LOG, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_OAK_LOG, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_SPRUCE_LOG, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_BIRCH_LOG, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_JUNGLE_LOG, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_ACACIA_LOG, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_DARK_OAK_LOG, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_OAK_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_SPRUCE_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_BIRCH_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_JUNGLE_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_ACACIA_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.STRIPPED_DARK_OAK_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.OAK_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.SPRUCE_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.BIRCH_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.JUNGLE_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.ACACIA_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.DARK_OAK_WOOD, 5, 5);
      fireblock.setFlammable(Blocks.OAK_LEAVES, 30, 60);
      fireblock.setFlammable(Blocks.SPRUCE_LEAVES, 30, 60);
      fireblock.setFlammable(Blocks.BIRCH_LEAVES, 30, 60);
      fireblock.setFlammable(Blocks.JUNGLE_LEAVES, 30, 60);
      fireblock.setFlammable(Blocks.ACACIA_LEAVES, 30, 60);
      fireblock.setFlammable(Blocks.DARK_OAK_LEAVES, 30, 60);
      fireblock.setFlammable(Blocks.BOOKSHELF, 30, 20);
      fireblock.setFlammable(Blocks.TNT, 15, 100);
      fireblock.setFlammable(Blocks.GRASS, 60, 100);
      fireblock.setFlammable(Blocks.FERN, 60, 100);
      fireblock.setFlammable(Blocks.DEAD_BUSH, 60, 100);
      fireblock.setFlammable(Blocks.SUNFLOWER, 60, 100);
      fireblock.setFlammable(Blocks.LILAC, 60, 100);
      fireblock.setFlammable(Blocks.ROSE_BUSH, 60, 100);
      fireblock.setFlammable(Blocks.PEONY, 60, 100);
      fireblock.setFlammable(Blocks.TALL_GRASS, 60, 100);
      fireblock.setFlammable(Blocks.LARGE_FERN, 60, 100);
      fireblock.setFlammable(Blocks.DANDELION, 60, 100);
      fireblock.setFlammable(Blocks.POPPY, 60, 100);
      fireblock.setFlammable(Blocks.BLUE_ORCHID, 60, 100);
      fireblock.setFlammable(Blocks.ALLIUM, 60, 100);
      fireblock.setFlammable(Blocks.AZURE_BLUET, 60, 100);
      fireblock.setFlammable(Blocks.RED_TULIP, 60, 100);
      fireblock.setFlammable(Blocks.ORANGE_TULIP, 60, 100);
      fireblock.setFlammable(Blocks.WHITE_TULIP, 60, 100);
      fireblock.setFlammable(Blocks.PINK_TULIP, 60, 100);
      fireblock.setFlammable(Blocks.OXEYE_DAISY, 60, 100);
      fireblock.setFlammable(Blocks.CORNFLOWER, 60, 100);
      fireblock.setFlammable(Blocks.LILY_OF_THE_VALLEY, 60, 100);
      fireblock.setFlammable(Blocks.WITHER_ROSE, 60, 100);
      fireblock.setFlammable(Blocks.WHITE_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.ORANGE_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.MAGENTA_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.LIGHT_BLUE_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.YELLOW_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.LIME_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.PINK_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.GRAY_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.LIGHT_GRAY_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.CYAN_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.PURPLE_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.BLUE_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.BROWN_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.GREEN_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.RED_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.BLACK_WOOL, 30, 60);
      fireblock.setFlammable(Blocks.VINE, 15, 100);
      fireblock.setFlammable(Blocks.COAL_BLOCK, 5, 5);
      fireblock.setFlammable(Blocks.HAY_BLOCK, 60, 20);
      fireblock.setFlammable(Blocks.TARGET, 15, 20);
      fireblock.setFlammable(Blocks.WHITE_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.ORANGE_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.MAGENTA_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.LIGHT_BLUE_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.YELLOW_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.LIME_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.PINK_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.GRAY_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.LIGHT_GRAY_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.CYAN_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.PURPLE_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.BLUE_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.BROWN_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.GREEN_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.RED_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.BLACK_CARPET, 60, 20);
      fireblock.setFlammable(Blocks.DRIED_KELP_BLOCK, 30, 60);
      fireblock.setFlammable(Blocks.BAMBOO, 60, 60);
      fireblock.setFlammable(Blocks.SCAFFOLDING, 60, 60);
      fireblock.setFlammable(Blocks.LECTERN, 30, 20);
      fireblock.setFlammable(Blocks.COMPOSTER, 5, 20);
      fireblock.setFlammable(Blocks.SWEET_BERRY_BUSH, 60, 100);
      fireblock.setFlammable(Blocks.BEEHIVE, 5, 20);
      fireblock.setFlammable(Blocks.BEE_NEST, 30, 20);
   }
}
