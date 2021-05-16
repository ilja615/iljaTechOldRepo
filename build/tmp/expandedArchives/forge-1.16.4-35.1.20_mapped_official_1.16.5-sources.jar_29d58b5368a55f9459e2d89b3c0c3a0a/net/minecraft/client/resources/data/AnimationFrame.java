package net.minecraft.client.resources.data;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnimationFrame {
   private final int index;
   private final int time;

   public AnimationFrame(int p_i1307_1_) {
      this(p_i1307_1_, -1);
   }

   public AnimationFrame(int p_i1308_1_, int p_i1308_2_) {
      this.index = p_i1308_1_;
      this.time = p_i1308_2_;
   }

   public boolean isTimeUnknown() {
      return this.time == -1;
   }

   public int getTime() {
      return this.time;
   }

   public int getIndex() {
      return this.index;
   }
}
