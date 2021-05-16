package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class BegGoal extends Goal {
   private final WolfEntity wolf;
   private PlayerEntity player;
   private final World level;
   private final float lookDistance;
   private int lookTime;
   private final EntityPredicate begTargeting;

   public BegGoal(WolfEntity p_i1617_1_, float p_i1617_2_) {
      this.wolf = p_i1617_1_;
      this.level = p_i1617_1_.level;
      this.lookDistance = p_i1617_2_;
      this.begTargeting = (new EntityPredicate()).range((double)p_i1617_2_).allowInvulnerable().allowSameTeam().allowNonAttackable();
      this.setFlags(EnumSet.of(Goal.Flag.LOOK));
   }

   public boolean canUse() {
      this.player = this.level.getNearestPlayer(this.begTargeting, this.wolf);
      return this.player == null ? false : this.playerHoldingInteresting(this.player);
   }

   public boolean canContinueToUse() {
      if (!this.player.isAlive()) {
         return false;
      } else if (this.wolf.distanceToSqr(this.player) > (double)(this.lookDistance * this.lookDistance)) {
         return false;
      } else {
         return this.lookTime > 0 && this.playerHoldingInteresting(this.player);
      }
   }

   public void start() {
      this.wolf.setIsInterested(true);
      this.lookTime = 40 + this.wolf.getRandom().nextInt(40);
   }

   public void stop() {
      this.wolf.setIsInterested(false);
      this.player = null;
   }

   public void tick() {
      this.wolf.getLookControl().setLookAt(this.player.getX(), this.player.getEyeY(), this.player.getZ(), 10.0F, (float)this.wolf.getMaxHeadXRot());
      --this.lookTime;
   }

   private boolean playerHoldingInteresting(PlayerEntity p_75382_1_) {
      for(Hand hand : Hand.values()) {
         ItemStack itemstack = p_75382_1_.getItemInHand(hand);
         if (this.wolf.isTame() && itemstack.getItem() == Items.BONE) {
            return true;
         }

         if (this.wolf.isFood(itemstack)) {
            return true;
         }
      }

      return false;
   }
}
