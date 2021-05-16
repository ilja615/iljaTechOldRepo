package net.minecraft.entity.ai;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;

public class EntitySenses {
   private final MobEntity mob;
   private final List<Entity> seen = Lists.newArrayList();
   private final List<Entity> unseen = Lists.newArrayList();

   public EntitySenses(MobEntity p_i1672_1_) {
      this.mob = p_i1672_1_;
   }

   public void tick() {
      this.seen.clear();
      this.unseen.clear();
   }

   public boolean canSee(Entity p_75522_1_) {
      if (this.seen.contains(p_75522_1_)) {
         return true;
      } else if (this.unseen.contains(p_75522_1_)) {
         return false;
      } else {
         this.mob.level.getProfiler().push("canSee");
         boolean flag = this.mob.canSee(p_75522_1_);
         this.mob.level.getProfiler().pop();
         if (flag) {
            this.seen.add(p_75522_1_);
         } else {
            this.unseen.add(p_75522_1_);
         }

         return flag;
      }
   }
}
