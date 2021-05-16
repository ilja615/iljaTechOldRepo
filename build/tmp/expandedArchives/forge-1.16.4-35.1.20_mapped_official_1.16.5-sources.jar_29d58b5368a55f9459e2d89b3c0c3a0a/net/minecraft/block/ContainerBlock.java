package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ContainerBlock extends Block implements ITileEntityProvider {
   protected ContainerBlock(AbstractBlock.Properties p_i48446_1_) {
      super(p_i48446_1_);
   }

   public BlockRenderType getRenderShape(BlockState p_149645_1_) {
      return BlockRenderType.INVISIBLE;
   }

   public boolean triggerEvent(BlockState p_189539_1_, World p_189539_2_, BlockPos p_189539_3_, int p_189539_4_, int p_189539_5_) {
      super.triggerEvent(p_189539_1_, p_189539_2_, p_189539_3_, p_189539_4_, p_189539_5_);
      TileEntity tileentity = p_189539_2_.getBlockEntity(p_189539_3_);
      return tileentity == null ? false : tileentity.triggerEvent(p_189539_4_, p_189539_5_);
   }

   @Nullable
   public INamedContainerProvider getMenuProvider(BlockState p_220052_1_, World p_220052_2_, BlockPos p_220052_3_) {
      TileEntity tileentity = p_220052_2_.getBlockEntity(p_220052_3_);
      return tileentity instanceof INamedContainerProvider ? (INamedContainerProvider)tileentity : null;
   }
}
