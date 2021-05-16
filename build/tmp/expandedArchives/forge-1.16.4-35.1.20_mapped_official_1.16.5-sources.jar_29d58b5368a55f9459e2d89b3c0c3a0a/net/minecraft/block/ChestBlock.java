package net.minecraft.block;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.DoubleSidedInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ChestType;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChestBlock extends AbstractChestBlock<ChestTileEntity> implements IWaterLoggable {
   public static final DirectionProperty FACING = HorizontalBlock.FACING;
   public static final EnumProperty<ChestType> TYPE = BlockStateProperties.CHEST_TYPE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape NORTH_AABB = Block.box(1.0D, 0.0D, 0.0D, 15.0D, 14.0D, 15.0D);
   protected static final VoxelShape SOUTH_AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 16.0D);
   protected static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
   protected static final VoxelShape EAST_AABB = Block.box(1.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D);
   protected static final VoxelShape AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
   private static final TileEntityMerger.ICallback<ChestTileEntity, Optional<IInventory>> CHEST_COMBINER = new TileEntityMerger.ICallback<ChestTileEntity, Optional<IInventory>>() {
      public Optional<IInventory> acceptDouble(ChestTileEntity p_225539_1_, ChestTileEntity p_225539_2_) {
         return Optional.of(new DoubleSidedInventory(p_225539_1_, p_225539_2_));
      }

      public Optional<IInventory> acceptSingle(ChestTileEntity p_225538_1_) {
         return Optional.of(p_225538_1_);
      }

      public Optional<IInventory> acceptNone() {
         return Optional.empty();
      }
   };
   private static final TileEntityMerger.ICallback<ChestTileEntity, Optional<INamedContainerProvider>> MENU_PROVIDER_COMBINER = new TileEntityMerger.ICallback<ChestTileEntity, Optional<INamedContainerProvider>>() {
      public Optional<INamedContainerProvider> acceptDouble(final ChestTileEntity p_225539_1_, final ChestTileEntity p_225539_2_) {
         final IInventory iinventory = new DoubleSidedInventory(p_225539_1_, p_225539_2_);
         return Optional.of(new INamedContainerProvider() {
            @Nullable
            public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
               if (p_225539_1_.canOpen(p_createMenu_3_) && p_225539_2_.canOpen(p_createMenu_3_)) {
                  p_225539_1_.unpackLootTable(p_createMenu_2_.player);
                  p_225539_2_.unpackLootTable(p_createMenu_2_.player);
                  return ChestContainer.sixRows(p_createMenu_1_, p_createMenu_2_, iinventory);
               } else {
                  return null;
               }
            }

            public ITextComponent getDisplayName() {
               if (p_225539_1_.hasCustomName()) {
                  return p_225539_1_.getDisplayName();
               } else {
                  return (ITextComponent)(p_225539_2_.hasCustomName() ? p_225539_2_.getDisplayName() : new TranslationTextComponent("container.chestDouble"));
               }
            }
         });
      }

      public Optional<INamedContainerProvider> acceptSingle(ChestTileEntity p_225538_1_) {
         return Optional.of(p_225538_1_);
      }

      public Optional<INamedContainerProvider> acceptNone() {
         return Optional.empty();
      }
   };

   public ChestBlock(AbstractBlock.Properties p_i225757_1_, Supplier<TileEntityType<? extends ChestTileEntity>> p_i225757_2_) {
      super(p_i225757_1_, p_i225757_2_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TYPE, ChestType.SINGLE).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public static TileEntityMerger.Type getBlockType(BlockState p_226919_0_) {
      ChestType chesttype = p_226919_0_.getValue(TYPE);
      if (chesttype == ChestType.SINGLE) {
         return TileEntityMerger.Type.SINGLE;
      } else {
         return chesttype == ChestType.RIGHT ? TileEntityMerger.Type.FIRST : TileEntityMerger.Type.SECOND;
      }
   }

   public BlockRenderType getRenderShape(BlockState p_149645_1_) {
      return BlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_1_.getValue(WATERLOGGED)) {
         p_196271_4_.getLiquidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickDelay(p_196271_4_));
      }

      if (p_196271_3_.is(this) && p_196271_2_.getAxis().isHorizontal()) {
         ChestType chesttype = p_196271_3_.getValue(TYPE);
         if (p_196271_1_.getValue(TYPE) == ChestType.SINGLE && chesttype != ChestType.SINGLE && p_196271_1_.getValue(FACING) == p_196271_3_.getValue(FACING) && getConnectedDirection(p_196271_3_) == p_196271_2_.getOpposite()) {
            return p_196271_1_.setValue(TYPE, chesttype.getOpposite());
         }
      } else if (getConnectedDirection(p_196271_1_) == p_196271_2_) {
         return p_196271_1_.setValue(TYPE, ChestType.SINGLE);
      }

      return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      if (p_220053_1_.getValue(TYPE) == ChestType.SINGLE) {
         return AABB;
      } else {
         switch(getConnectedDirection(p_220053_1_)) {
         case NORTH:
         default:
            return NORTH_AABB;
         case SOUTH:
            return SOUTH_AABB;
         case WEST:
            return WEST_AABB;
         case EAST:
            return EAST_AABB;
         }
      }
   }

   public static Direction getConnectedDirection(BlockState p_196311_0_) {
      Direction direction = p_196311_0_.getValue(FACING);
      return p_196311_0_.getValue(TYPE) == ChestType.LEFT ? direction.getClockWise() : direction.getCounterClockWise();
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      ChestType chesttype = ChestType.SINGLE;
      Direction direction = p_196258_1_.getHorizontalDirection().getOpposite();
      FluidState fluidstate = p_196258_1_.getLevel().getFluidState(p_196258_1_.getClickedPos());
      boolean flag = p_196258_1_.isSecondaryUseActive();
      Direction direction1 = p_196258_1_.getClickedFace();
      if (direction1.getAxis().isHorizontal() && flag) {
         Direction direction2 = this.candidatePartnerFacing(p_196258_1_, direction1.getOpposite());
         if (direction2 != null && direction2.getAxis() != direction1.getAxis()) {
            direction = direction2;
            chesttype = direction2.getCounterClockWise() == direction1.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
         }
      }

      if (chesttype == ChestType.SINGLE && !flag) {
         if (direction == this.candidatePartnerFacing(p_196258_1_, direction.getClockWise())) {
            chesttype = ChestType.LEFT;
         } else if (direction == this.candidatePartnerFacing(p_196258_1_, direction.getCounterClockWise())) {
            chesttype = ChestType.RIGHT;
         }
      }

      return this.defaultBlockState().setValue(FACING, direction).setValue(TYPE, chesttype).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
   }

   public FluidState getFluidState(BlockState p_204507_1_) {
      return p_204507_1_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_204507_1_);
   }

   @Nullable
   private Direction candidatePartnerFacing(BlockItemUseContext p_196312_1_, Direction p_196312_2_) {
      BlockState blockstate = p_196312_1_.getLevel().getBlockState(p_196312_1_.getClickedPos().relative(p_196312_2_));
      return blockstate.is(this) && blockstate.getValue(TYPE) == ChestType.SINGLE ? blockstate.getValue(FACING) : null;
   }

   public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      if (p_180633_5_.hasCustomHoverName()) {
         TileEntity tileentity = p_180633_1_.getBlockEntity(p_180633_2_);
         if (tileentity instanceof ChestTileEntity) {
            ((ChestTileEntity)tileentity).setCustomName(p_180633_5_.getHoverName());
         }
      }

   }

   public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_1_.is(p_196243_4_.getBlock())) {
         TileEntity tileentity = p_196243_2_.getBlockEntity(p_196243_3_);
         if (tileentity instanceof IInventory) {
            InventoryHelper.dropContents(p_196243_2_, p_196243_3_, (IInventory)tileentity);
            p_196243_2_.updateNeighbourForOutputSignal(p_196243_3_, this);
         }

         super.onRemove(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isClientSide) {
         return ActionResultType.SUCCESS;
      } else {
         INamedContainerProvider inamedcontainerprovider = this.getMenuProvider(p_225533_1_, p_225533_2_, p_225533_3_);
         if (inamedcontainerprovider != null) {
            p_225533_4_.openMenu(inamedcontainerprovider);
            p_225533_4_.awardStat(this.getOpenChestStat());
            PiglinTasks.angerNearbyPiglins(p_225533_4_, true);
         }

         return ActionResultType.CONSUME;
      }
   }

   protected Stat<ResourceLocation> getOpenChestStat() {
      return Stats.CUSTOM.get(Stats.OPEN_CHEST);
   }

   @Nullable
   public static IInventory getContainer(ChestBlock p_226916_0_, BlockState p_226916_1_, World p_226916_2_, BlockPos p_226916_3_, boolean p_226916_4_) {
      return p_226916_0_.combine(p_226916_1_, p_226916_2_, p_226916_3_, p_226916_4_).<Optional<IInventory>>apply(CHEST_COMBINER).orElse((IInventory)null);
   }

   public TileEntityMerger.ICallbackWrapper<? extends ChestTileEntity> combine(BlockState p_225536_1_, World p_225536_2_, BlockPos p_225536_3_, boolean p_225536_4_) {
      BiPredicate<IWorld, BlockPos> bipredicate;
      if (p_225536_4_) {
         bipredicate = (p_226918_0_, p_226918_1_) -> {
            return false;
         };
      } else {
         bipredicate = ChestBlock::isChestBlockedAt;
      }

      return TileEntityMerger.combineWithNeigbour(this.blockEntityType.get(), ChestBlock::getBlockType, ChestBlock::getConnectedDirection, FACING, p_225536_1_, p_225536_2_, p_225536_3_, bipredicate);
   }

   @Nullable
   public INamedContainerProvider getMenuProvider(BlockState p_220052_1_, World p_220052_2_, BlockPos p_220052_3_) {
      return this.combine(p_220052_1_, p_220052_2_, p_220052_3_, false).<Optional<INamedContainerProvider>>apply(MENU_PROVIDER_COMBINER).orElse((INamedContainerProvider)null);
   }

   @OnlyIn(Dist.CLIENT)
   public static TileEntityMerger.ICallback<ChestTileEntity, Float2FloatFunction> opennessCombiner(final IChestLid p_226917_0_) {
      return new TileEntityMerger.ICallback<ChestTileEntity, Float2FloatFunction>() {
         public Float2FloatFunction acceptDouble(ChestTileEntity p_225539_1_, ChestTileEntity p_225539_2_) {
            return (p_226921_2_) -> {
               return Math.max(p_225539_1_.getOpenNess(p_226921_2_), p_225539_2_.getOpenNess(p_226921_2_));
            };
         }

         public Float2FloatFunction acceptSingle(ChestTileEntity p_225538_1_) {
            return p_225538_1_::getOpenNess;
         }

         public Float2FloatFunction acceptNone() {
            return p_226917_0_::getOpenNess;
         }
      };
   }

   public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
      return new ChestTileEntity();
   }

   public static boolean isChestBlockedAt(IWorld p_220108_0_, BlockPos p_220108_1_) {
      return isBlockedChestByBlock(p_220108_0_, p_220108_1_) || isCatSittingOnChest(p_220108_0_, p_220108_1_);
   }

   private static boolean isBlockedChestByBlock(IBlockReader p_176456_0_, BlockPos p_176456_1_) {
      BlockPos blockpos = p_176456_1_.above();
      return p_176456_0_.getBlockState(blockpos).isRedstoneConductor(p_176456_0_, blockpos);
   }

   private static boolean isCatSittingOnChest(IWorld p_220107_0_, BlockPos p_220107_1_) {
      List<CatEntity> list = p_220107_0_.getEntitiesOfClass(CatEntity.class, new AxisAlignedBB((double)p_220107_1_.getX(), (double)(p_220107_1_.getY() + 1), (double)p_220107_1_.getZ(), (double)(p_220107_1_.getX() + 1), (double)(p_220107_1_.getY() + 2), (double)(p_220107_1_.getZ() + 1)));
      if (!list.isEmpty()) {
         for(CatEntity catentity : list) {
            if (catentity.isInSittingPose()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return Container.getRedstoneSignalFromContainer(getContainer(this, p_180641_1_, p_180641_2_, p_180641_3_, false));
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.setValue(FACING, p_185499_2_.rotate(p_185499_1_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.getRotation(p_185471_1_.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, TYPE, WATERLOGGED);
   }

   public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}
