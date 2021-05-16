package net.minecraft.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FoodStats {
   private int foodLevel = 20;
   private float saturationLevel;
   private float exhaustionLevel;
   private int tickTimer;
   private int lastFoodLevel = 20;

   public FoodStats() {
      this.saturationLevel = 5.0F;
   }

   public void eat(int p_75122_1_, float p_75122_2_) {
      this.foodLevel = Math.min(p_75122_1_ + this.foodLevel, 20);
      this.saturationLevel = Math.min(this.saturationLevel + (float)p_75122_1_ * p_75122_2_ * 2.0F, (float)this.foodLevel);
   }

   public void eat(Item p_221410_1_, ItemStack p_221410_2_) {
      if (p_221410_1_.isEdible()) {
         Food food = p_221410_1_.getFoodProperties();
         this.eat(food.getNutrition(), food.getSaturationModifier());
      }

   }

   public void tick(PlayerEntity p_75118_1_) {
      Difficulty difficulty = p_75118_1_.level.getDifficulty();
      this.lastFoodLevel = this.foodLevel;
      if (this.exhaustionLevel > 4.0F) {
         this.exhaustionLevel -= 4.0F;
         if (this.saturationLevel > 0.0F) {
            this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
         } else if (difficulty != Difficulty.PEACEFUL) {
            this.foodLevel = Math.max(this.foodLevel - 1, 0);
         }
      }

      boolean flag = p_75118_1_.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
      if (flag && this.saturationLevel > 0.0F && p_75118_1_.isHurt() && this.foodLevel >= 20) {
         ++this.tickTimer;
         if (this.tickTimer >= 10) {
            float f = Math.min(this.saturationLevel, 6.0F);
            p_75118_1_.heal(f / 6.0F);
            this.addExhaustion(f);
            this.tickTimer = 0;
         }
      } else if (flag && this.foodLevel >= 18 && p_75118_1_.isHurt()) {
         ++this.tickTimer;
         if (this.tickTimer >= 80) {
            p_75118_1_.heal(1.0F);
            this.addExhaustion(6.0F);
            this.tickTimer = 0;
         }
      } else if (this.foodLevel <= 0) {
         ++this.tickTimer;
         if (this.tickTimer >= 80) {
            if (p_75118_1_.getHealth() > 10.0F || difficulty == Difficulty.HARD || p_75118_1_.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
               p_75118_1_.hurt(DamageSource.STARVE, 1.0F);
            }

            this.tickTimer = 0;
         }
      } else {
         this.tickTimer = 0;
      }

   }

   public void readAdditionalSaveData(CompoundNBT p_75112_1_) {
      if (p_75112_1_.contains("foodLevel", 99)) {
         this.foodLevel = p_75112_1_.getInt("foodLevel");
         this.tickTimer = p_75112_1_.getInt("foodTickTimer");
         this.saturationLevel = p_75112_1_.getFloat("foodSaturationLevel");
         this.exhaustionLevel = p_75112_1_.getFloat("foodExhaustionLevel");
      }

   }

   public void addAdditionalSaveData(CompoundNBT p_75117_1_) {
      p_75117_1_.putInt("foodLevel", this.foodLevel);
      p_75117_1_.putInt("foodTickTimer", this.tickTimer);
      p_75117_1_.putFloat("foodSaturationLevel", this.saturationLevel);
      p_75117_1_.putFloat("foodExhaustionLevel", this.exhaustionLevel);
   }

   public int getFoodLevel() {
      return this.foodLevel;
   }

   public boolean needsFood() {
      return this.foodLevel < 20;
   }

   public void addExhaustion(float p_75113_1_) {
      this.exhaustionLevel = Math.min(this.exhaustionLevel + p_75113_1_, 40.0F);
   }

   public float getSaturationLevel() {
      return this.saturationLevel;
   }

   public void setFoodLevel(int p_75114_1_) {
      this.foodLevel = p_75114_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public void setSaturation(float p_75119_1_) {
      this.saturationLevel = p_75119_1_;
   }
}
