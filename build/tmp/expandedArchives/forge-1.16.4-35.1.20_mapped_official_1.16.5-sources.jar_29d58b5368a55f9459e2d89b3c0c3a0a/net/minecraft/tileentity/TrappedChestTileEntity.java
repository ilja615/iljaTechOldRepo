package net.minecraft.tileentity;

public class TrappedChestTileEntity extends ChestTileEntity {
   public TrappedChestTileEntity() {
      super(TileEntityType.TRAPPED_CHEST);
   }

   protected void signalOpenCount() {
      super.signalOpenCount();
      this.level.updateNeighborsAt(this.worldPosition.below(), this.getBlockState().getBlock());
   }
}
