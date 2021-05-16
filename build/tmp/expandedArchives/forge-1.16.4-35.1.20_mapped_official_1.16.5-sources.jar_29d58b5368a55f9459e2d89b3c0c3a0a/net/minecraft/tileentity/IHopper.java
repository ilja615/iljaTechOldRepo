package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;

public interface IHopper extends IInventory {
   VoxelShape INSIDE = Block.box(2.0D, 11.0D, 2.0D, 14.0D, 16.0D, 14.0D);
   VoxelShape ABOVE = Block.box(0.0D, 16.0D, 0.0D, 16.0D, 32.0D, 16.0D);
   VoxelShape SUCK = VoxelShapes.or(INSIDE, ABOVE);

   default VoxelShape getSuckShape() {
      return SUCK;
   }

   @Nullable
   World getLevel();

   double getLevelX();

   double getLevelY();

   double getLevelZ();
}
