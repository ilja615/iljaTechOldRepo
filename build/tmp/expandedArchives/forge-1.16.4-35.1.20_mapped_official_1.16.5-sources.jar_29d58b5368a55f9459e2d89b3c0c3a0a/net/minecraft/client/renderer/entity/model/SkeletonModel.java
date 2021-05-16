package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkeletonModel<T extends MobEntity & IRangedAttackMob> extends BipedModel<T> {
   public SkeletonModel() {
      this(0.0F, false);
   }

   public SkeletonModel(float p_i46303_1_, boolean p_i46303_2_) {
      super(p_i46303_1_);
      if (!p_i46303_2_) {
         this.rightArm = new ModelRenderer(this, 40, 16);
         this.rightArm.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, p_i46303_1_);
         this.rightArm.setPos(-5.0F, 2.0F, 0.0F);
         this.leftArm = new ModelRenderer(this, 40, 16);
         this.leftArm.mirror = true;
         this.leftArm.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, p_i46303_1_);
         this.leftArm.setPos(5.0F, 2.0F, 0.0F);
         this.rightLeg = new ModelRenderer(this, 0, 16);
         this.rightLeg.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, p_i46303_1_);
         this.rightLeg.setPos(-2.0F, 12.0F, 0.0F);
         this.leftLeg = new ModelRenderer(this, 0, 16);
         this.leftLeg.mirror = true;
         this.leftLeg.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, p_i46303_1_);
         this.leftLeg.setPos(2.0F, 12.0F, 0.0F);
      }

   }

   public void prepareMobModel(T p_212843_1_, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
      this.rightArmPose = BipedModel.ArmPose.EMPTY;
      this.leftArmPose = BipedModel.ArmPose.EMPTY;
      ItemStack itemstack = p_212843_1_.getItemInHand(Hand.MAIN_HAND);
      if (itemstack.getItem() instanceof net.minecraft.item.BowItem && p_212843_1_.isAggressive()) {
         if (p_212843_1_.getMainArm() == HandSide.RIGHT) {
            this.rightArmPose = BipedModel.ArmPose.BOW_AND_ARROW;
         } else {
            this.leftArmPose = BipedModel.ArmPose.BOW_AND_ARROW;
         }
      }

      super.prepareMobModel(p_212843_1_, p_212843_2_, p_212843_3_, p_212843_4_);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      super.setupAnim(p_225597_1_, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
      ItemStack itemstack = p_225597_1_.getMainHandItem();
      if (p_225597_1_.isAggressive() && (itemstack.isEmpty() || !(itemstack.getItem() instanceof net.minecraft.item.BowItem))) {
         float f = MathHelper.sin(this.attackTime * (float)Math.PI);
         float f1 = MathHelper.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * (float)Math.PI);
         this.rightArm.zRot = 0.0F;
         this.leftArm.zRot = 0.0F;
         this.rightArm.yRot = -(0.1F - f * 0.6F);
         this.leftArm.yRot = 0.1F - f * 0.6F;
         this.rightArm.xRot = (-(float)Math.PI / 2F);
         this.leftArm.xRot = (-(float)Math.PI / 2F);
         this.rightArm.xRot -= f * 1.2F - f1 * 0.4F;
         this.leftArm.xRot -= f * 1.2F - f1 * 0.4F;
         ModelHelper.bobArms(this.rightArm, this.leftArm, p_225597_4_);
      }

   }

   public void translateToHand(HandSide p_225599_1_, MatrixStack p_225599_2_) {
      float f = p_225599_1_ == HandSide.RIGHT ? 1.0F : -1.0F;
      ModelRenderer modelrenderer = this.getArm(p_225599_1_);
      modelrenderer.x += f;
      modelrenderer.translateAndRotate(p_225599_2_);
      modelrenderer.x -= f;
   }
}
