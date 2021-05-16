package net.minecraft.block;

import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.ProxyBlockSource;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.DropperTileEntity;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

public class DropperBlock extends DispenserBlock {
   private static final IDispenseItemBehavior DISPENSE_BEHAVIOUR = new DefaultDispenseItemBehavior();

   public DropperBlock(AbstractBlock.Properties p_i48410_1_) {
      super(p_i48410_1_);
   }

   protected IDispenseItemBehavior getDispenseMethod(ItemStack p_149940_1_) {
      return DISPENSE_BEHAVIOUR;
   }

   public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
      return new DropperTileEntity();
   }

   protected void dispenseFrom(ServerWorld p_176439_1_, BlockPos p_176439_2_) {
      ProxyBlockSource proxyblocksource = new ProxyBlockSource(p_176439_1_, p_176439_2_);
      DispenserTileEntity dispensertileentity = proxyblocksource.getEntity();
      int i = dispensertileentity.getRandomSlot();
      if (i < 0) {
         p_176439_1_.levelEvent(1001, p_176439_2_, 0);
      } else {
         ItemStack itemstack = dispensertileentity.getItem(i);
         if (!itemstack.isEmpty() && net.minecraftforge.items.VanillaInventoryCodeHooks.dropperInsertHook(p_176439_1_, p_176439_2_, dispensertileentity, i, itemstack)) {
            Direction direction = p_176439_1_.getBlockState(p_176439_2_).getValue(FACING);
            IInventory iinventory = HopperTileEntity.getContainerAt(p_176439_1_, p_176439_2_.relative(direction));
            ItemStack itemstack1;
            if (iinventory == null) {
               itemstack1 = DISPENSE_BEHAVIOUR.dispense(proxyblocksource, itemstack);
            } else {
               itemstack1 = HopperTileEntity.addItem(dispensertileentity, iinventory, itemstack.copy().split(1), direction.getOpposite());
               if (itemstack1.isEmpty()) {
                  itemstack1 = itemstack.copy();
                  itemstack1.shrink(1);
               } else {
                  itemstack1 = itemstack.copy();
               }
            }

            dispensertileentity.setItem(i, itemstack1);
         }
      }
   }
}
