package net.minecraft.client.gui.advancements;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum AdvancementState {
   OBTAINED(0),
   UNOBTAINED(1);

   private final int y;

   private AdvancementState(int p_i47384_3_) {
      this.y = p_i47384_3_;
   }

   public int getIndex() {
      return this.y;
   }
}
