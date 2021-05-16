package net.minecraft.entity;

import java.util.Random;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;

public class BoostHelper {
   private final EntityDataManager entityData;
   private final DataParameter<Integer> boostTimeAccessor;
   private final DataParameter<Boolean> hasSaddleAccessor;
   public boolean boosting;
   public int boostTime;
   public int boostTimeTotal;

   public BoostHelper(EntityDataManager p_i231490_1_, DataParameter<Integer> p_i231490_2_, DataParameter<Boolean> p_i231490_3_) {
      this.entityData = p_i231490_1_;
      this.boostTimeAccessor = p_i231490_2_;
      this.hasSaddleAccessor = p_i231490_3_;
   }

   public void onSynced() {
      this.boosting = true;
      this.boostTime = 0;
      this.boostTimeTotal = this.entityData.get(this.boostTimeAccessor);
   }

   public boolean boost(Random p_233617_1_) {
      if (this.boosting) {
         return false;
      } else {
         this.boosting = true;
         this.boostTime = 0;
         this.boostTimeTotal = p_233617_1_.nextInt(841) + 140;
         this.entityData.set(this.boostTimeAccessor, this.boostTimeTotal);
         return true;
      }
   }

   public void addAdditionalSaveData(CompoundNBT p_233618_1_) {
      p_233618_1_.putBoolean("Saddle", this.hasSaddle());
   }

   public void readAdditionalSaveData(CompoundNBT p_233621_1_) {
      this.setSaddle(p_233621_1_.getBoolean("Saddle"));
   }

   public void setSaddle(boolean p_233619_1_) {
      this.entityData.set(this.hasSaddleAccessor, p_233619_1_);
   }

   public boolean hasSaddle() {
      return this.entityData.get(this.hasSaddleAccessor);
   }
}
