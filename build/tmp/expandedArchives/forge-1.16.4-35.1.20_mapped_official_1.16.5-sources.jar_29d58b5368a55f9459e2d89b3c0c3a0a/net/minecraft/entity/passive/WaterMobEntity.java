package net.minecraft.entity.passive;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class WaterMobEntity extends CreatureEntity {
   protected WaterMobEntity(EntityType<? extends WaterMobEntity> p_i48565_1_, World p_i48565_2_) {
      super(p_i48565_1_, p_i48565_2_);
      this.setPathfindingMalus(PathNodeType.WATER, 0.0F);
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public CreatureAttribute getMobType() {
      return CreatureAttribute.WATER;
   }

   public boolean checkSpawnObstruction(IWorldReader p_205019_1_) {
      return p_205019_1_.isUnobstructed(this);
   }

   public int getAmbientSoundInterval() {
      return 120;
   }

   protected int getExperienceReward(PlayerEntity p_70693_1_) {
      return 1 + this.level.random.nextInt(3);
   }

   protected void handleAirSupply(int p_209207_1_) {
      if (this.isAlive() && !this.isInWaterOrBubble()) {
         this.setAirSupply(p_209207_1_ - 1);
         if (this.getAirSupply() == -20) {
            this.setAirSupply(0);
            this.hurt(DamageSource.DROWN, 2.0F);
         }
      } else {
         this.setAirSupply(300);
      }

   }

   public void baseTick() {
      int i = this.getAirSupply();
      super.baseTick();
      this.handleAirSupply(i);
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public boolean canBeLeashed(PlayerEntity p_184652_1_) {
      return false;
   }
}
