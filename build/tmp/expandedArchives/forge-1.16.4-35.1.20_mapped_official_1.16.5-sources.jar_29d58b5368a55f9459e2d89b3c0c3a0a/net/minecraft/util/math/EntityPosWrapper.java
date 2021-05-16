package net.minecraft.util.math;

import java.util.List;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.math.vector.Vector3d;

public class EntityPosWrapper implements IPosWrapper {
   private final Entity entity;
   private final boolean trackEyeHeight;

   public EntityPosWrapper(Entity p_i231516_1_, boolean p_i231516_2_) {
      this.entity = p_i231516_1_;
      this.trackEyeHeight = p_i231516_2_;
   }

   public Vector3d currentPosition() {
      return this.trackEyeHeight ? this.entity.position().add(0.0D, (double)this.entity.getEyeHeight(), 0.0D) : this.entity.position();
   }

   public BlockPos currentBlockPosition() {
      return this.entity.blockPosition();
   }

   public boolean isVisibleBy(LivingEntity p_220610_1_) {
      if (!(this.entity instanceof LivingEntity)) {
         return true;
      } else {
         Optional<List<LivingEntity>> optional = p_220610_1_.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES);
         return this.entity.isAlive() && optional.isPresent() && optional.get().contains(this.entity);
      }
   }

   public String toString() {
      return "EntityTracker for " + this.entity;
   }
}
