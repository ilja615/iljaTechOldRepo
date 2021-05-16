package net.minecraft.item;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MinecartItem extends Item {
   private static final IDispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
      private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

      public ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
         Direction direction = p_82487_1_.getBlockState().getValue(DispenserBlock.FACING);
         World world = p_82487_1_.getLevel();
         double d0 = p_82487_1_.x() + (double)direction.getStepX() * 1.125D;
         double d1 = Math.floor(p_82487_1_.y()) + (double)direction.getStepY();
         double d2 = p_82487_1_.z() + (double)direction.getStepZ() * 1.125D;
         BlockPos blockpos = p_82487_1_.getPos().relative(direction);
         BlockState blockstate = world.getBlockState(blockpos);
         RailShape railshape = blockstate.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock)blockstate.getBlock()).getRailDirection(blockstate, world, blockpos, null) : RailShape.NORTH_SOUTH;
         double d3;
         if (blockstate.is(BlockTags.RAILS)) {
            if (railshape.isAscending()) {
               d3 = 0.6D;
            } else {
               d3 = 0.1D;
            }
         } else {
            if (!blockstate.isAir() || !world.getBlockState(blockpos.below()).is(BlockTags.RAILS)) {
               return this.defaultDispenseItemBehavior.dispense(p_82487_1_, p_82487_2_);
            }

            BlockState blockstate1 = world.getBlockState(blockpos.below());
            RailShape railshape1 = blockstate1.getBlock() instanceof AbstractRailBlock ? blockstate1.getValue(((AbstractRailBlock)blockstate1.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
            if (direction != Direction.DOWN && railshape1.isAscending()) {
               d3 = -0.4D;
            } else {
               d3 = -0.9D;
            }
         }

         AbstractMinecartEntity abstractminecartentity = AbstractMinecartEntity.createMinecart(world, d0, d1 + d3, d2, ((MinecartItem)p_82487_2_.getItem()).type);
         if (p_82487_2_.hasCustomHoverName()) {
            abstractminecartentity.setCustomName(p_82487_2_.getHoverName());
         }

         world.addFreshEntity(abstractminecartentity);
         p_82487_2_.shrink(1);
         return p_82487_2_;
      }

      protected void playSound(IBlockSource p_82485_1_) {
         p_82485_1_.getLevel().levelEvent(1000, p_82485_1_.getPos(), 0);
      }
   };
   private final AbstractMinecartEntity.Type type;

   public MinecartItem(AbstractMinecartEntity.Type p_i48480_1_, Item.Properties p_i48480_2_) {
      super(p_i48480_2_);
      this.type = p_i48480_1_;
      DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
   }

   public ActionResultType useOn(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getLevel();
      BlockPos blockpos = p_195939_1_.getClickedPos();
      BlockState blockstate = world.getBlockState(blockpos);
      if (!blockstate.is(BlockTags.RAILS)) {
         return ActionResultType.FAIL;
      } else {
         ItemStack itemstack = p_195939_1_.getItemInHand();
         if (!world.isClientSide) {
            RailShape railshape = blockstate.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock)blockstate.getBlock()).getRailDirection(blockstate, world, blockpos, null) : RailShape.NORTH_SOUTH;
            double d0 = 0.0D;
            if (railshape.isAscending()) {
               d0 = 0.5D;
            }

            AbstractMinecartEntity abstractminecartentity = AbstractMinecartEntity.createMinecart(world, (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.0625D + d0, (double)blockpos.getZ() + 0.5D, this.type);
            if (itemstack.hasCustomHoverName()) {
               abstractminecartentity.setCustomName(itemstack.getHoverName());
            }

            world.addFreshEntity(abstractminecartentity);
         }

         itemstack.shrink(1);
         return ActionResultType.sidedSuccess(world.isClientSide);
      }
   }
}
