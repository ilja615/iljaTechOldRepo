package net.minecraft.entity.ai.controller;

import net.minecraft.entity.MobEntity;

public class JumpController {
   private final MobEntity mob;
   protected boolean jump;

   public JumpController(MobEntity p_i1612_1_) {
      this.mob = p_i1612_1_;
   }

   public void jump() {
      this.jump = true;
   }

   public void tick() {
      this.mob.setJumping(this.jump);
      this.jump = false;
   }
}
