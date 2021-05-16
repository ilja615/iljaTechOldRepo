package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;

public class LookAtWithoutMovingGoal extends LookAtGoal {
   public LookAtWithoutMovingGoal(MobEntity p_i1629_1_, Class<? extends LivingEntity> p_i1629_2_, float p_i1629_3_, float p_i1629_4_) {
      super(p_i1629_1_, p_i1629_2_, p_i1629_3_, p_i1629_4_);
      this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
   }
}
