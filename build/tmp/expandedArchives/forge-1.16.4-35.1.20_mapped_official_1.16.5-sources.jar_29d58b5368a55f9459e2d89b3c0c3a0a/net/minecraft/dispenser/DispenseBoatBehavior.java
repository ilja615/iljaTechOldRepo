package net.minecraft.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DispenseBoatBehavior extends DefaultDispenseItemBehavior {
   private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
   private final BoatEntity.Type type;

   public DispenseBoatBehavior(BoatEntity.Type p_i50793_1_) {
      this.type = p_i50793_1_;
   }

   public ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
      Direction direction = p_82487_1_.getBlockState().getValue(DispenserBlock.FACING);
      World world = p_82487_1_.getLevel();
      double d0 = p_82487_1_.x() + (double)((float)direction.getStepX() * 1.125F);
      double d1 = p_82487_1_.y() + (double)((float)direction.getStepY() * 1.125F);
      double d2 = p_82487_1_.z() + (double)((float)direction.getStepZ() * 1.125F);
      BlockPos blockpos = p_82487_1_.getPos().relative(direction);
      double d3;
      if (world.getFluidState(blockpos).is(FluidTags.WATER)) {
         d3 = 1.0D;
      } else {
         if (!world.getBlockState(blockpos).isAir() || !world.getFluidState(blockpos.below()).is(FluidTags.WATER)) {
            return this.defaultDispenseItemBehavior.dispense(p_82487_1_, p_82487_2_);
         }

         d3 = 0.0D;
      }

      BoatEntity boatentity = new BoatEntity(world, d0, d1 + d3, d2);
      boatentity.setType(this.type);
      boatentity.yRot = direction.toYRot();
      world.addFreshEntity(boatentity);
      p_82487_2_.shrink(1);
      return p_82487_2_;
   }

   protected void playSound(IBlockSource p_82485_1_) {
      p_82485_1_.getLevel().levelEvent(1000, p_82485_1_.getPos(), 0);
   }
}
