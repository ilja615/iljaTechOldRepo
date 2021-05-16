package net.minecraft.block;

import net.minecraft.util.math.vector.Vector3d;

public class PortalInfo {
   public final Vector3d pos;
   public final Vector3d speed;
   public final float yRot;
   public final float xRot;

   public PortalInfo(Vector3d p_i242042_1_, Vector3d p_i242042_2_, float p_i242042_3_, float p_i242042_4_) {
      this.pos = p_i242042_1_;
      this.speed = p_i242042_2_;
      this.yRot = p_i242042_3_;
      this.xRot = p_i242042_4_;
   }
}
