package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArmorStandArmorModel extends BipedModel<ArmorStandEntity> {
   public ArmorStandArmorModel(float p_i46307_1_) {
      this(p_i46307_1_, 64, 32);
   }

   protected ArmorStandArmorModel(float p_i46308_1_, int p_i46308_2_, int p_i46308_3_) {
      super(p_i46308_1_, 0.0F, p_i46308_2_, p_i46308_3_);
   }

   public void setupAnim(ArmorStandEntity p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      this.head.xRot = ((float)Math.PI / 180F) * p_225597_1_.getHeadPose().getX();
      this.head.yRot = ((float)Math.PI / 180F) * p_225597_1_.getHeadPose().getY();
      this.head.zRot = ((float)Math.PI / 180F) * p_225597_1_.getHeadPose().getZ();
      this.head.setPos(0.0F, 1.0F, 0.0F);
      this.body.xRot = ((float)Math.PI / 180F) * p_225597_1_.getBodyPose().getX();
      this.body.yRot = ((float)Math.PI / 180F) * p_225597_1_.getBodyPose().getY();
      this.body.zRot = ((float)Math.PI / 180F) * p_225597_1_.getBodyPose().getZ();
      this.leftArm.xRot = ((float)Math.PI / 180F) * p_225597_1_.getLeftArmPose().getX();
      this.leftArm.yRot = ((float)Math.PI / 180F) * p_225597_1_.getLeftArmPose().getY();
      this.leftArm.zRot = ((float)Math.PI / 180F) * p_225597_1_.getLeftArmPose().getZ();
      this.rightArm.xRot = ((float)Math.PI / 180F) * p_225597_1_.getRightArmPose().getX();
      this.rightArm.yRot = ((float)Math.PI / 180F) * p_225597_1_.getRightArmPose().getY();
      this.rightArm.zRot = ((float)Math.PI / 180F) * p_225597_1_.getRightArmPose().getZ();
      this.leftLeg.xRot = ((float)Math.PI / 180F) * p_225597_1_.getLeftLegPose().getX();
      this.leftLeg.yRot = ((float)Math.PI / 180F) * p_225597_1_.getLeftLegPose().getY();
      this.leftLeg.zRot = ((float)Math.PI / 180F) * p_225597_1_.getLeftLegPose().getZ();
      this.leftLeg.setPos(1.9F, 11.0F, 0.0F);
      this.rightLeg.xRot = ((float)Math.PI / 180F) * p_225597_1_.getRightLegPose().getX();
      this.rightLeg.yRot = ((float)Math.PI / 180F) * p_225597_1_.getRightLegPose().getY();
      this.rightLeg.zRot = ((float)Math.PI / 180F) * p_225597_1_.getRightLegPose().getZ();
      this.rightLeg.setPos(-1.9F, 11.0F, 0.0F);
      this.hat.copyFrom(this.head);
   }
}
