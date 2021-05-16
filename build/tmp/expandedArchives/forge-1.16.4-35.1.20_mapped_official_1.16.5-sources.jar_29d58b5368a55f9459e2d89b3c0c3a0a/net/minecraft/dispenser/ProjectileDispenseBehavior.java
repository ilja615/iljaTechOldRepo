package net.minecraft.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public abstract class ProjectileDispenseBehavior extends DefaultDispenseItemBehavior {
   public ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
      World world = p_82487_1_.getLevel();
      IPosition iposition = DispenserBlock.getDispensePosition(p_82487_1_);
      Direction direction = p_82487_1_.getBlockState().getValue(DispenserBlock.FACING);
      ProjectileEntity projectileentity = this.getProjectile(world, iposition, p_82487_2_);
      projectileentity.shoot((double)direction.getStepX(), (double)((float)direction.getStepY() + 0.1F), (double)direction.getStepZ(), this.getPower(), this.getUncertainty());
      world.addFreshEntity(projectileentity);
      p_82487_2_.shrink(1);
      return p_82487_2_;
   }

   protected void playSound(IBlockSource p_82485_1_) {
      p_82485_1_.getLevel().levelEvent(1002, p_82485_1_.getPos(), 0);
   }

   protected abstract ProjectileEntity getProjectile(World p_82499_1_, IPosition p_82499_2_, ItemStack p_82499_3_);

   protected float getUncertainty() {
      return 6.0F;
   }

   protected float getPower() {
      return 1.1F;
   }
}
