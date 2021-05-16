package net.minecraft.world.border;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum BorderStatus {
   GROWING(4259712),
   SHRINKING(16724016),
   STATIONARY(2138367);

   private final int color;

   private BorderStatus(int p_i45647_3_) {
      this.color = p_i45647_3_;
   }

   public int getColor() {
      return this.color;
   }
}
