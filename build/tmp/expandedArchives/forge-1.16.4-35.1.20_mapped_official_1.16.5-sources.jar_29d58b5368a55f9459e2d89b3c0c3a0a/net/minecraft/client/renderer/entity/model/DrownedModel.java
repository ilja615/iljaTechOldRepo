package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DrownedModel<T extends ZombieEntity> extends ZombieModel<T> {
   public DrownedModel(float p_i48915_1_, float p_i48915_2_, int p_i48915_3_, int p_i48915_4_) {
      super(p_i48915_1_, p_i48915_2_, p_i48915_3_, p_i48915_4_);
      this.rightArm = new ModelRenderer(this, 32, 48);
      this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i48915_1_);
      this.rightArm.setPos(-5.0F, 2.0F + p_i48915_2_, 0.0F);
      this.rightLeg = new ModelRenderer(this, 16, 48);
      this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i48915_1_);
      this.rightLeg.setPos(-1.9F, 12.0F + p_i48915_2_, 0.0F);
   }

   public DrownedModel(float p_i49398_1_, boolean p_i49398_2_) {
      super(p_i49398_1_, 0.0F, 64, p_i49398_2_ ? 32 : 64);
   }

   public void prepareMobModel(T p_212843_1_, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
      this.rightArmPose = BipedModel.ArmPose.EMPTY;
      this.leftArmPose = BipedModel.ArmPose.EMPTY;
      ItemStack itemstack = p_212843_1_.getItemInHand(Hand.MAIN_HAND);
      if (itemstack.getItem() == Items.TRIDENT && p_212843_1_.isAggressive()) {
         if (p_212843_1_.getMainArm() == HandSide.RIGHT) {
            this.rightArmPose = BipedModel.ArmPose.THROW_SPEAR;
         } else {
            this.leftArmPose = BipedModel.ArmPose.THROW_SPEAR;
         }
      }

      super.prepareMobModel(p_212843_1_, p_212843_2_, p_212843_3_, p_212843_4_);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      super.setupAnim(p_225597_1_, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
      if (this.leftArmPose == BipedModel.ArmPose.THROW_SPEAR) {
         this.leftArm.xRot = this.leftArm.xRot * 0.5F - (float)Math.PI;
         this.leftArm.yRot = 0.0F;
      }

      if (this.rightArmPose == BipedModel.ArmPose.THROW_SPEAR) {
         this.rightArm.xRot = this.rightArm.xRot * 0.5F - (float)Math.PI;
         this.rightArm.yRot = 0.0F;
      }

      if (this.swimAmount > 0.0F) {
         this.rightArm.xRot = this.rotlerpRad(this.swimAmount, this.rightArm.xRot, -2.5132742F) + this.swimAmount * 0.35F * MathHelper.sin(0.1F * p_225597_4_);
         this.leftArm.xRot = this.rotlerpRad(this.swimAmount, this.leftArm.xRot, -2.5132742F) - this.swimAmount * 0.35F * MathHelper.sin(0.1F * p_225597_4_);
         this.rightArm.zRot = this.rotlerpRad(this.swimAmount, this.rightArm.zRot, -0.15F);
         this.leftArm.zRot = this.rotlerpRad(this.swimAmount, this.leftArm.zRot, 0.15F);
         this.leftLeg.xRot -= this.swimAmount * 0.55F * MathHelper.sin(0.1F * p_225597_4_);
         this.rightLeg.xRot += this.swimAmount * 0.55F * MathHelper.sin(0.1F * p_225597_4_);
         this.head.xRot = 0.0F;
      }

   }
}
