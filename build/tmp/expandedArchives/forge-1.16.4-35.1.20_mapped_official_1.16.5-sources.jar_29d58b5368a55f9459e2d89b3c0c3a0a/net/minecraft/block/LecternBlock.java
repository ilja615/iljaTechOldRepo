package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.LecternTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class LecternBlock extends ContainerBlock {
   public static final DirectionProperty FACING = HorizontalBlock.FACING;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty HAS_BOOK = BlockStateProperties.HAS_BOOK;
   public static final VoxelShape SHAPE_BASE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   public static final VoxelShape SHAPE_POST = Block.box(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D);
   public static final VoxelShape SHAPE_COMMON = VoxelShapes.or(SHAPE_BASE, SHAPE_POST);
   public static final VoxelShape SHAPE_TOP_PLATE = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 15.0D, 16.0D);
   public static final VoxelShape SHAPE_COLLISION = VoxelShapes.or(SHAPE_COMMON, SHAPE_TOP_PLATE);
   public static final VoxelShape SHAPE_WEST = VoxelShapes.or(Block.box(1.0D, 10.0D, 0.0D, 5.333333D, 14.0D, 16.0D), Block.box(5.333333D, 12.0D, 0.0D, 9.666667D, 16.0D, 16.0D), Block.box(9.666667D, 14.0D, 0.0D, 14.0D, 18.0D, 16.0D), SHAPE_COMMON);
   public static final VoxelShape SHAPE_NORTH = VoxelShapes.or(Block.box(0.0D, 10.0D, 1.0D, 16.0D, 14.0D, 5.333333D), Block.box(0.0D, 12.0D, 5.333333D, 16.0D, 16.0D, 9.666667D), Block.box(0.0D, 14.0D, 9.666667D, 16.0D, 18.0D, 14.0D), SHAPE_COMMON);
   public static final VoxelShape SHAPE_EAST = VoxelShapes.or(Block.box(15.0D, 10.0D, 0.0D, 10.666667D, 14.0D, 16.0D), Block.box(10.666667D, 12.0D, 0.0D, 6.333333D, 16.0D, 16.0D), Block.box(6.333333D, 14.0D, 0.0D, 2.0D, 18.0D, 16.0D), SHAPE_COMMON);
   public static final VoxelShape SHAPE_SOUTH = VoxelShapes.or(Block.box(0.0D, 10.0D, 15.0D, 16.0D, 14.0D, 10.666667D), Block.box(0.0D, 12.0D, 10.666667D, 16.0D, 16.0D, 6.333333D), Block.box(0.0D, 14.0D, 6.333333D, 16.0D, 18.0D, 2.0D), SHAPE_COMMON);

   public LecternBlock(AbstractBlock.Properties p_i49979_1_) {
      super(p_i49979_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, Boolean.valueOf(false)).setValue(HAS_BOOK, Boolean.valueOf(false)));
   }

   public BlockRenderType getRenderShape(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   public VoxelShape getOcclusionShape(BlockState p_196247_1_, IBlockReader p_196247_2_, BlockPos p_196247_3_) {
      return SHAPE_COMMON;
   }

   public boolean useShapeForLightOcclusion(BlockState p_220074_1_) {
      return true;
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      World world = p_196258_1_.getLevel();
      ItemStack itemstack = p_196258_1_.getItemInHand();
      CompoundNBT compoundnbt = itemstack.getTag();
      PlayerEntity playerentity = p_196258_1_.getPlayer();
      boolean flag = false;
      if (!world.isClientSide && playerentity != null && compoundnbt != null && playerentity.canUseGameMasterBlocks() && compoundnbt.contains("BlockEntityTag")) {
         CompoundNBT compoundnbt1 = compoundnbt.getCompound("BlockEntityTag");
         if (compoundnbt1.contains("Book")) {
            flag = true;
         }
      }

      return this.defaultBlockState().setValue(FACING, p_196258_1_.getHorizontalDirection().getOpposite()).setValue(HAS_BOOK, Boolean.valueOf(flag));
   }

   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      return SHAPE_COLLISION;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      switch((Direction)p_220053_1_.getValue(FACING)) {
      case NORTH:
         return SHAPE_NORTH;
      case SOUTH:
         return SHAPE_SOUTH;
      case EAST:
         return SHAPE_EAST;
      case WEST:
         return SHAPE_WEST;
      default:
         return SHAPE_COMMON;
      }
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.setValue(FACING, p_185499_2_.rotate(p_185499_1_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.getRotation(p_185471_1_.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, POWERED, HAS_BOOK);
   }

   @Nullable
   public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
      return new LecternTileEntity();
   }

   public static boolean tryPlaceBook(World p_220151_0_, BlockPos p_220151_1_, BlockState p_220151_2_, ItemStack p_220151_3_) {
      if (!p_220151_2_.getValue(HAS_BOOK)) {
         if (!p_220151_0_.isClientSide) {
            placeBook(p_220151_0_, p_220151_1_, p_220151_2_, p_220151_3_);
         }

         return true;
      } else {
         return false;
      }
   }

   private static void placeBook(World p_220148_0_, BlockPos p_220148_1_, BlockState p_220148_2_, ItemStack p_220148_3_) {
      TileEntity tileentity = p_220148_0_.getBlockEntity(p_220148_1_);
      if (tileentity instanceof LecternTileEntity) {
         LecternTileEntity lecterntileentity = (LecternTileEntity)tileentity;
         lecterntileentity.setBook(p_220148_3_.split(1));
         resetBookState(p_220148_0_, p_220148_1_, p_220148_2_, true);
         p_220148_0_.playSound((PlayerEntity)null, p_220148_1_, SoundEvents.BOOK_PUT, SoundCategory.BLOCKS, 1.0F, 1.0F);
      }

   }

   public static void resetBookState(World p_220155_0_, BlockPos p_220155_1_, BlockState p_220155_2_, boolean p_220155_3_) {
      p_220155_0_.setBlock(p_220155_1_, p_220155_2_.setValue(POWERED, Boolean.valueOf(false)).setValue(HAS_BOOK, Boolean.valueOf(p_220155_3_)), 3);
      updateBelow(p_220155_0_, p_220155_1_, p_220155_2_);
   }

   public static void signalPageChange(World p_220154_0_, BlockPos p_220154_1_, BlockState p_220154_2_) {
      changePowered(p_220154_0_, p_220154_1_, p_220154_2_, true);
      p_220154_0_.getBlockTicks().scheduleTick(p_220154_1_, p_220154_2_.getBlock(), 2);
      p_220154_0_.levelEvent(1043, p_220154_1_, 0);
   }

   private static void changePowered(World p_220149_0_, BlockPos p_220149_1_, BlockState p_220149_2_, boolean p_220149_3_) {
      p_220149_0_.setBlock(p_220149_1_, p_220149_2_.setValue(POWERED, Boolean.valueOf(p_220149_3_)), 3);
      updateBelow(p_220149_0_, p_220149_1_, p_220149_2_);
   }

   private static void updateBelow(World p_220153_0_, BlockPos p_220153_1_, BlockState p_220153_2_) {
      p_220153_0_.updateNeighborsAt(p_220153_1_.below(), p_220153_2_.getBlock());
   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      changePowered(p_225534_2_, p_225534_3_, p_225534_1_, false);
   }

   public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_1_.is(p_196243_4_.getBlock())) {
         if (p_196243_1_.getValue(HAS_BOOK)) {
            this.popBook(p_196243_1_, p_196243_2_, p_196243_3_);
         }

         if (p_196243_1_.getValue(POWERED)) {
            p_196243_2_.updateNeighborsAt(p_196243_3_.below(), this);
         }

         super.onRemove(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   private void popBook(BlockState p_220150_1_, World p_220150_2_, BlockPos p_220150_3_) {
      TileEntity tileentity = p_220150_2_.getBlockEntity(p_220150_3_);
      if (tileentity instanceof LecternTileEntity) {
         LecternTileEntity lecterntileentity = (LecternTileEntity)tileentity;
         Direction direction = p_220150_1_.getValue(FACING);
         ItemStack itemstack = lecterntileentity.getBook().copy();
         float f = 0.25F * (float)direction.getStepX();
         float f1 = 0.25F * (float)direction.getStepZ();
         ItemEntity itementity = new ItemEntity(p_220150_2_, (double)p_220150_3_.getX() + 0.5D + (double)f, (double)(p_220150_3_.getY() + 1), (double)p_220150_3_.getZ() + 0.5D + (double)f1, itemstack);
         itementity.setDefaultPickUpDelay();
         p_220150_2_.addFreshEntity(itementity);
         lecterntileentity.clearContent();
      }

   }

   public boolean isSignalSource(BlockState p_149744_1_) {
      return true;
   }

   public int getSignal(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      return p_180656_1_.getValue(POWERED) ? 15 : 0;
   }

   public int getDirectSignal(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
      return p_176211_4_ == Direction.UP && p_176211_1_.getValue(POWERED) ? 15 : 0;
   }

   public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      if (p_180641_1_.getValue(HAS_BOOK)) {
         TileEntity tileentity = p_180641_2_.getBlockEntity(p_180641_3_);
         if (tileentity instanceof LecternTileEntity) {
            return ((LecternTileEntity)tileentity).getRedstoneSignal();
         }
      }

      return 0;
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_1_.getValue(HAS_BOOK)) {
         if (!p_225533_2_.isClientSide) {
            this.openScreen(p_225533_2_, p_225533_3_, p_225533_4_);
         }

         return ActionResultType.sidedSuccess(p_225533_2_.isClientSide);
      } else {
         ItemStack itemstack = p_225533_4_.getItemInHand(p_225533_5_);
         return !itemstack.isEmpty() && !itemstack.getItem().is(ItemTags.LECTERN_BOOKS) ? ActionResultType.CONSUME : ActionResultType.PASS;
      }
   }

   @Nullable
   public INamedContainerProvider getMenuProvider(BlockState p_220052_1_, World p_220052_2_, BlockPos p_220052_3_) {
      return !p_220052_1_.getValue(HAS_BOOK) ? null : super.getMenuProvider(p_220052_1_, p_220052_2_, p_220052_3_);
   }

   private void openScreen(World p_220152_1_, BlockPos p_220152_2_, PlayerEntity p_220152_3_) {
      TileEntity tileentity = p_220152_1_.getBlockEntity(p_220152_2_);
      if (tileentity instanceof LecternTileEntity) {
         p_220152_3_.openMenu((LecternTileEntity)tileentity);
         p_220152_3_.awardStat(Stats.INTERACT_WITH_LECTERN);
      }

   }

   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}
