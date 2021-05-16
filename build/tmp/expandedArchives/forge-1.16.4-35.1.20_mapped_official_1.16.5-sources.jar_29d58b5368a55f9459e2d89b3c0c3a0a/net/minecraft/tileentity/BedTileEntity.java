package net.minecraft.tileentity;

import net.minecraft.block.BedBlock;
import net.minecraft.item.DyeColor;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BedTileEntity extends TileEntity {
   private DyeColor color;

   public BedTileEntity() {
      super(TileEntityType.BED);
   }

   public BedTileEntity(DyeColor p_i47730_1_) {
      this();
      this.setColor(p_i47730_1_);
   }

   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.worldPosition, 11, this.getUpdateTag());
   }

   @OnlyIn(Dist.CLIENT)
   public DyeColor getColor() {
      if (this.color == null) {
         this.color = ((BedBlock)this.getBlockState().getBlock()).getColor();
      }

      return this.color;
   }

   public void setColor(DyeColor p_193052_1_) {
      this.color = p_193052_1_;
   }
}
