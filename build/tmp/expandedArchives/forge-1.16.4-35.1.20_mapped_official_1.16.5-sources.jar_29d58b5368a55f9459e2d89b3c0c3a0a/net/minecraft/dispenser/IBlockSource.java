package net.minecraft.dispenser;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public interface IBlockSource extends IPosition {
   double x();

   double y();

   double z();

   BlockPos getPos();

   BlockState getBlockState();

   <T extends TileEntity> T getEntity();

   ServerWorld getLevel();
}
