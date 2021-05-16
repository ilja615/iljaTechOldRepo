package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BreedGoal extends Goal {
   private static final EntityPredicate PARTNER_TARGETING = (new EntityPredicate()).range(8.0D).allowInvulnerable().allowSameTeam().allowUnseeable();
   protected final AnimalEntity animal;
   private final Class<? extends AnimalEntity> partnerClass;
   protected final World level;
   protected AnimalEntity partner;
   private int loveTime;
   private final double speedModifier;

   public BreedGoal(AnimalEntity p_i1619_1_, double p_i1619_2_) {
      this(p_i1619_1_, p_i1619_2_, p_i1619_1_.getClass());
   }

   public BreedGoal(AnimalEntity p_i47306_1_, double p_i47306_2_, Class<? extends AnimalEntity> p_i47306_4_) {
      this.animal = p_i47306_1_;
      this.level = p_i47306_1_.level;
      this.partnerClass = p_i47306_4_;
      this.speedModifier = p_i47306_2_;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   public boolean canUse() {
      if (!this.animal.isInLove()) {
         return false;
      } else {
         this.partner = this.getFreePartner();
         return this.partner != null;
      }
   }

   public boolean canContinueToUse() {
      return this.partner.isAlive() && this.partner.isInLove() && this.loveTime < 60;
   }

   public void stop() {
      this.partner = null;
      this.loveTime = 0;
   }

   public void tick() {
      this.animal.getLookControl().setLookAt(this.partner, 10.0F, (float)this.animal.getMaxHeadXRot());
      this.animal.getNavigation().moveTo(this.partner, this.speedModifier);
      ++this.loveTime;
      if (this.loveTime >= 60 && this.animal.distanceToSqr(this.partner) < 9.0D) {
         this.breed();
      }

   }

   @Nullable
   private AnimalEntity getFreePartner() {
      List<AnimalEntity> list = this.level.getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(8.0D));
      double d0 = Double.MAX_VALUE;
      AnimalEntity animalentity = null;

      for(AnimalEntity animalentity1 : list) {
         if (this.animal.canMate(animalentity1) && this.animal.distanceToSqr(animalentity1) < d0) {
            animalentity = animalentity1;
            d0 = this.animal.distanceToSqr(animalentity1);
         }
      }

      return animalentity;
   }

   protected void breed() {
      this.animal.spawnChildFromBreeding((ServerWorld)this.level, this.partner);
   }
}
