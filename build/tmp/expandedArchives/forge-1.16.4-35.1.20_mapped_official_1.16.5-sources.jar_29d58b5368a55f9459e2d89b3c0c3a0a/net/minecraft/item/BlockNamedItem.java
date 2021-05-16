package net.minecraft.item;

import net.minecraft.block.Block;

public class BlockNamedItem extends BlockItem {
   public BlockNamedItem(Block p_i50041_1_, Item.Properties p_i50041_2_) {
      super(p_i50041_1_, p_i50041_2_);
   }

   public String getDescriptionId() {
      return this.getOrCreateDescriptionId();
   }
}
