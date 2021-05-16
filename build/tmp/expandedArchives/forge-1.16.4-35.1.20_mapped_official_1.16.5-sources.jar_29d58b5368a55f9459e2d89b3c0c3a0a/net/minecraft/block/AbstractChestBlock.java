package net.minecraft.block;

import java.util.function.Supplier;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractChestBlock<E extends TileEntity> extends ContainerBlock {
   protected final Supplier<TileEntityType<? extends E>> blockEntityType;

   protected AbstractChestBlock(AbstractBlock.Properties p_i225754_1_, Supplier<TileEntityType<? extends E>> p_i225754_2_) {
      super(p_i225754_1_);
      this.blockEntityType = p_i225754_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public abstract TileEntityMerger.ICallbackWrapper<? extends ChestTileEntity> combine(BlockState p_225536_1_, World p_225536_2_, BlockPos p_225536_3_, boolean p_225536_4_);
}
