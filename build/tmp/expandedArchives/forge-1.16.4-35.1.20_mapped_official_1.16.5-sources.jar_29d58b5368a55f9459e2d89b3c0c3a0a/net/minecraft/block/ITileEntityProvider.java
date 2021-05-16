package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

@Deprecated //Forge: Do not use, use BlockState.hasTileEntity/Block.createTileEntity
public interface ITileEntityProvider {
   @Nullable
   TileEntity newBlockEntity(IBlockReader p_196283_1_);
}
