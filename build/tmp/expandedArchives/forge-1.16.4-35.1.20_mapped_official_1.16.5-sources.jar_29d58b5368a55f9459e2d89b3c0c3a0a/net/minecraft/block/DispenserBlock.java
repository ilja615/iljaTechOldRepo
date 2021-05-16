package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.Position;
import net.minecraft.dispenser.ProxyBlockSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.DropperTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DispenserBlock extends ContainerBlock {
   public static final DirectionProperty FACING = DirectionalBlock.FACING;
   public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
   private static final Map<Item, IDispenseItemBehavior> DISPENSER_REGISTRY = Util.make(new Object2ObjectOpenHashMap<>(), (p_212564_0_) -> {
      p_212564_0_.defaultReturnValue(new DefaultDispenseItemBehavior());
   });

   public static void registerBehavior(IItemProvider p_199774_0_, IDispenseItemBehavior p_199774_1_) {
      DISPENSER_REGISTRY.put(p_199774_0_.asItem(), p_199774_1_);
   }

   public DispenserBlock(AbstractBlock.Properties p_i48414_1_) {
      super(p_i48414_1_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TRIGGERED, Boolean.valueOf(false)));
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isClientSide) {
         return ActionResultType.SUCCESS;
      } else {
         TileEntity tileentity = p_225533_2_.getBlockEntity(p_225533_3_);
         if (tileentity instanceof DispenserTileEntity) {
            p_225533_4_.openMenu((DispenserTileEntity)tileentity);
            if (tileentity instanceof DropperTileEntity) {
               p_225533_4_.awardStat(Stats.INSPECT_DROPPER);
            } else {
               p_225533_4_.awardStat(Stats.INSPECT_DISPENSER);
            }
         }

         return ActionResultType.CONSUME;
      }
   }

   protected void dispenseFrom(ServerWorld p_176439_1_, BlockPos p_176439_2_) {
      ProxyBlockSource proxyblocksource = new ProxyBlockSource(p_176439_1_, p_176439_2_);
      DispenserTileEntity dispensertileentity = proxyblocksource.getEntity();
      int i = dispensertileentity.getRandomSlot();
      if (i < 0) {
         p_176439_1_.levelEvent(1001, p_176439_2_, 0);
      } else {
         ItemStack itemstack = dispensertileentity.getItem(i);
         IDispenseItemBehavior idispenseitembehavior = this.getDispenseMethod(itemstack);
         if (idispenseitembehavior != IDispenseItemBehavior.NOOP) {
            dispensertileentity.setItem(i, idispenseitembehavior.dispense(proxyblocksource, itemstack));
         }

      }
   }

   protected IDispenseItemBehavior getDispenseMethod(ItemStack p_149940_1_) {
      return DISPENSER_REGISTRY.get(p_149940_1_.getItem());
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      boolean flag = p_220069_2_.hasNeighborSignal(p_220069_3_) || p_220069_2_.hasNeighborSignal(p_220069_3_.above());
      boolean flag1 = p_220069_1_.getValue(TRIGGERED);
      if (flag && !flag1) {
         p_220069_2_.getBlockTicks().scheduleTick(p_220069_3_, this, 4);
         p_220069_2_.setBlock(p_220069_3_, p_220069_1_.setValue(TRIGGERED, Boolean.valueOf(true)), 4);
      } else if (!flag && flag1) {
         p_220069_2_.setBlock(p_220069_3_, p_220069_1_.setValue(TRIGGERED, Boolean.valueOf(false)), 4);
      }

   }

   public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      this.dispenseFrom(p_225534_2_, p_225534_3_);
   }

   public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
      return new DispenserTileEntity();
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.defaultBlockState().setValue(FACING, p_196258_1_.getNearestLookingDirection().getOpposite());
   }

   public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      if (p_180633_5_.hasCustomHoverName()) {
         TileEntity tileentity = p_180633_1_.getBlockEntity(p_180633_2_);
         if (tileentity instanceof DispenserTileEntity) {
            ((DispenserTileEntity)tileentity).setCustomName(p_180633_5_.getHoverName());
         }
      }

   }

   public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_1_.is(p_196243_4_.getBlock())) {
         TileEntity tileentity = p_196243_2_.getBlockEntity(p_196243_3_);
         if (tileentity instanceof DispenserTileEntity) {
            InventoryHelper.dropContents(p_196243_2_, p_196243_3_, (DispenserTileEntity)tileentity);
            p_196243_2_.updateNeighbourForOutputSignal(p_196243_3_, this);
         }

         super.onRemove(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   public static IPosition getDispensePosition(IBlockSource p_149939_0_) {
      Direction direction = p_149939_0_.getBlockState().getValue(FACING);
      double d0 = p_149939_0_.x() + 0.7D * (double)direction.getStepX();
      double d1 = p_149939_0_.y() + 0.7D * (double)direction.getStepY();
      double d2 = p_149939_0_.z() + 0.7D * (double)direction.getStepZ();
      return new Position(d0, d1, d2);
   }

   public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return Container.getRedstoneSignalFromBlockEntity(p_180641_2_.getBlockEntity(p_180641_3_));
   }

   public BlockRenderType getRenderShape(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return p_185499_1_.setValue(FACING, p_185499_2_.rotate(p_185499_1_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.getRotation(p_185471_1_.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, TRIGGERED);
   }
}
