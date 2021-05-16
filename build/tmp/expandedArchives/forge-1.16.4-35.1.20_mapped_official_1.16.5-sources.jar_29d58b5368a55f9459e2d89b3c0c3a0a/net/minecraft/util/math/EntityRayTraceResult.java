package net.minecraft.util.math;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;

public class EntityRayTraceResult extends RayTraceResult {
   private final Entity entity;

   public EntityRayTraceResult(Entity p_i51184_1_) {
      this(p_i51184_1_, p_i51184_1_.position());
   }

   public EntityRayTraceResult(Entity p_i51185_1_, Vector3d p_i51185_2_) {
      super(p_i51185_2_);
      this.entity = p_i51185_1_;
   }

   public Entity getEntity() {
      return this.entity;
   }

   public RayTraceResult.Type getType() {
      return RayTraceResult.Type.ENTITY;
   }
}
