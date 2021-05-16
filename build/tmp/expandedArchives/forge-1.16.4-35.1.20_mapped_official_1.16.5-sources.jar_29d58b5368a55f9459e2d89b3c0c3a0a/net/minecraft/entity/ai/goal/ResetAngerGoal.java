package net.minecraft.entity.ai.goal;

import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.GameRules;

public class ResetAngerGoal<T extends MobEntity & IAngerable> extends Goal {
   private final T mob;
   private final boolean alertOthersOfSameType;
   private int lastHurtByPlayerTimestamp;

   public ResetAngerGoal(T p_i241234_1_, boolean p_i241234_2_) {
      this.mob = p_i241234_1_;
      this.alertOthersOfSameType = p_i241234_2_;
   }

   public boolean canUse() {
      return this.mob.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER) && this.wasHurtByPlayer();
   }

   private boolean wasHurtByPlayer() {
      return this.mob.getLastHurtByMob() != null && this.mob.getLastHurtByMob().getType() == EntityType.PLAYER && this.mob.getLastHurtByMobTimestamp() > this.lastHurtByPlayerTimestamp;
   }

   public void start() {
      this.lastHurtByPlayerTimestamp = this.mob.getLastHurtByMobTimestamp();
      this.mob.forgetCurrentTargetAndRefreshUniversalAnger();
      if (this.alertOthersOfSameType) {
         this.getNearbyMobsOfSameType().stream().filter((p_241387_1_) -> {
            return p_241387_1_ != this.mob;
         }).map((p_241386_0_) -> {
            return (IAngerable)p_241386_0_;
         }).forEach(IAngerable::forgetCurrentTargetAndRefreshUniversalAnger);
      }

      super.start();
   }

   private List<MobEntity> getNearbyMobsOfSameType() {
      double d0 = this.mob.getAttributeValue(Attributes.FOLLOW_RANGE);
      AxisAlignedBB axisalignedbb = AxisAlignedBB.unitCubeFromLowerCorner(this.mob.position()).inflate(d0, 10.0D, d0);
      return this.mob.level.getLoadedEntitiesOfClass(this.mob.getClass(), axisalignedbb);
   }
}
