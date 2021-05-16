package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class CombatTracker {
   private final List<CombatEntry> entries = Lists.newArrayList();
   private final LivingEntity mob;
   private int lastDamageTime;
   private int combatStartTime;
   private int combatEndTime;
   private boolean inCombat;
   private boolean takingDamage;
   private String nextLocation;

   public CombatTracker(LivingEntity p_i1565_1_) {
      this.mob = p_i1565_1_;
   }

   public void prepareForDamage() {
      this.resetPreparedStatus();
      Optional<BlockPos> optional = this.mob.getLastClimbablePos();
      if (optional.isPresent()) {
         BlockState blockstate = this.mob.level.getBlockState(optional.get());
         if (!blockstate.is(Blocks.LADDER) && !blockstate.is(BlockTags.TRAPDOORS)) {
            if (blockstate.is(Blocks.VINE)) {
               this.nextLocation = "vines";
            } else if (!blockstate.is(Blocks.WEEPING_VINES) && !blockstate.is(Blocks.WEEPING_VINES_PLANT)) {
               if (!blockstate.is(Blocks.TWISTING_VINES) && !blockstate.is(Blocks.TWISTING_VINES_PLANT)) {
                  if (blockstate.is(Blocks.SCAFFOLDING)) {
                     this.nextLocation = "scaffolding";
                  } else {
                     this.nextLocation = "other_climbable";
                  }
               } else {
                  this.nextLocation = "twisting_vines";
               }
            } else {
               this.nextLocation = "weeping_vines";
            }
         } else {
            this.nextLocation = "ladder";
         }
      } else if (this.mob.isInWater()) {
         this.nextLocation = "water";
      }

   }

   public void recordDamage(DamageSource p_94547_1_, float p_94547_2_, float p_94547_3_) {
      this.recheckStatus();
      this.prepareForDamage();
      CombatEntry combatentry = new CombatEntry(p_94547_1_, this.mob.tickCount, p_94547_2_, p_94547_3_, this.nextLocation, this.mob.fallDistance);
      this.entries.add(combatentry);
      this.lastDamageTime = this.mob.tickCount;
      this.takingDamage = true;
      if (combatentry.isCombatRelated() && !this.inCombat && this.mob.isAlive()) {
         this.inCombat = true;
         this.combatStartTime = this.mob.tickCount;
         this.combatEndTime = this.combatStartTime;
         this.mob.onEnterCombat();
      }

   }

   public ITextComponent getDeathMessage() {
      if (this.entries.isEmpty()) {
         return new TranslationTextComponent("death.attack.generic", this.mob.getDisplayName());
      } else {
         CombatEntry combatentry = this.getMostSignificantFall();
         CombatEntry combatentry1 = this.entries.get(this.entries.size() - 1);
         ITextComponent itextcomponent1 = combatentry1.getAttackerName();
         Entity entity = combatentry1.getSource().getEntity();
         ITextComponent itextcomponent;
         if (combatentry != null && combatentry1.getSource() == DamageSource.FALL) {
            ITextComponent itextcomponent2 = combatentry.getAttackerName();
            if (combatentry.getSource() != DamageSource.FALL && combatentry.getSource() != DamageSource.OUT_OF_WORLD) {
               if (itextcomponent2 != null && (itextcomponent1 == null || !itextcomponent2.equals(itextcomponent1))) {
                  Entity entity1 = combatentry.getSource().getEntity();
                  ItemStack itemstack1 = entity1 instanceof LivingEntity ? ((LivingEntity)entity1).getMainHandItem() : ItemStack.EMPTY;
                  if (!itemstack1.isEmpty() && itemstack1.hasCustomHoverName()) {
                     itextcomponent = new TranslationTextComponent("death.fell.assist.item", this.mob.getDisplayName(), itextcomponent2, itemstack1.getDisplayName());
                  } else {
                     itextcomponent = new TranslationTextComponent("death.fell.assist", this.mob.getDisplayName(), itextcomponent2);
                  }
               } else if (itextcomponent1 != null) {
                  ItemStack itemstack = entity instanceof LivingEntity ? ((LivingEntity)entity).getMainHandItem() : ItemStack.EMPTY;
                  if (!itemstack.isEmpty() && itemstack.hasCustomHoverName()) {
                     itextcomponent = new TranslationTextComponent("death.fell.finish.item", this.mob.getDisplayName(), itextcomponent1, itemstack.getDisplayName());
                  } else {
                     itextcomponent = new TranslationTextComponent("death.fell.finish", this.mob.getDisplayName(), itextcomponent1);
                  }
               } else {
                  itextcomponent = new TranslationTextComponent("death.fell.killer", this.mob.getDisplayName());
               }
            } else {
               itextcomponent = new TranslationTextComponent("death.fell.accident." + this.getFallLocation(combatentry), this.mob.getDisplayName());
            }
         } else {
            itextcomponent = combatentry1.getSource().getLocalizedDeathMessage(this.mob);
         }

         return itextcomponent;
      }
   }

   @Nullable
   public LivingEntity getKiller() {
      LivingEntity livingentity = null;
      PlayerEntity playerentity = null;
      float f = 0.0F;
      float f1 = 0.0F;

      for(CombatEntry combatentry : this.entries) {
         if (combatentry.getSource().getEntity() instanceof PlayerEntity && (playerentity == null || combatentry.getDamage() > f1)) {
            f1 = combatentry.getDamage();
            playerentity = (PlayerEntity)combatentry.getSource().getEntity();
         }

         if (combatentry.getSource().getEntity() instanceof LivingEntity && (livingentity == null || combatentry.getDamage() > f)) {
            f = combatentry.getDamage();
            livingentity = (LivingEntity)combatentry.getSource().getEntity();
         }
      }

      return (LivingEntity)(playerentity != null && f1 >= f / 3.0F ? playerentity : livingentity);
   }

   @Nullable
   private CombatEntry getMostSignificantFall() {
      CombatEntry combatentry = null;
      CombatEntry combatentry1 = null;
      float f = 0.0F;
      float f1 = 0.0F;

      for(int i = 0; i < this.entries.size(); ++i) {
         CombatEntry combatentry2 = this.entries.get(i);
         CombatEntry combatentry3 = i > 0 ? this.entries.get(i - 1) : null;
         if ((combatentry2.getSource() == DamageSource.FALL || combatentry2.getSource() == DamageSource.OUT_OF_WORLD) && combatentry2.getFallDistance() > 0.0F && (combatentry == null || combatentry2.getFallDistance() > f1)) {
            if (i > 0) {
               combatentry = combatentry3;
            } else {
               combatentry = combatentry2;
            }

            f1 = combatentry2.getFallDistance();
         }

         if (combatentry2.getLocation() != null && (combatentry1 == null || combatentry2.getDamage() > f)) {
            combatentry1 = combatentry2;
            f = combatentry2.getDamage();
         }
      }

      if (f1 > 5.0F && combatentry != null) {
         return combatentry;
      } else {
         return f > 5.0F && combatentry1 != null ? combatentry1 : null;
      }
   }

   private String getFallLocation(CombatEntry p_94548_1_) {
      return p_94548_1_.getLocation() == null ? "generic" : p_94548_1_.getLocation();
   }

   public int getCombatDuration() {
      return this.inCombat ? this.mob.tickCount - this.combatStartTime : this.combatEndTime - this.combatStartTime;
   }

   private void resetPreparedStatus() {
      this.nextLocation = null;
   }

   public void recheckStatus() {
      int i = this.inCombat ? 300 : 100;
      if (this.takingDamage && (!this.mob.isAlive() || this.mob.tickCount - this.lastDamageTime > i)) {
         boolean flag = this.inCombat;
         this.takingDamage = false;
         this.inCombat = false;
         this.combatEndTime = this.mob.tickCount;
         if (flag) {
            this.mob.onLeaveCombat();
         }

         this.entries.clear();
      }

   }

   public LivingEntity getMob() {
      return this.mob;
   }
}
