package net.minecraft.util.math;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;

public abstract class RayTraceResult {
   protected final Vector3d location;
   /** Used to determine what sub-segment is hit */
   public int subHit = -1;

   /** Used to add extra hit info */
   public Object hitInfo = null;

   protected RayTraceResult(Vector3d p_i51183_1_) {
      this.location = p_i51183_1_;
   }

   public double distanceTo(Entity p_237486_1_) {
      double d0 = this.location.x - p_237486_1_.getX();
      double d1 = this.location.y - p_237486_1_.getY();
      double d2 = this.location.z - p_237486_1_.getZ();
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   public abstract RayTraceResult.Type getType();

   public Vector3d getLocation() {
      return this.location;
   }

   public static enum Type {
      MISS,
      BLOCK,
      ENTITY;
   }
}
