package net.minecraft.world;

public enum LightType {
   SKY(15),
   BLOCK(0);

   public final int surrounding;

   private LightType(int p_i1961_3_) {
      this.surrounding = p_i1961_3_;
   }
}
