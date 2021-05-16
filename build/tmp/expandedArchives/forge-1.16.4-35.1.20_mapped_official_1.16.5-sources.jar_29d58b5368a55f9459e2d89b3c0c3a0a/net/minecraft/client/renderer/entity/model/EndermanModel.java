package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndermanModel<T extends LivingEntity> extends BipedModel<T> {
   public boolean carrying;
   public boolean creepy;

   public EndermanModel(float p_i46305_1_) {
      super(0.0F, -14.0F, 64, 32);
      float f = -14.0F;
      this.hat = new ModelRenderer(this, 0, 16);
      this.hat.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, p_i46305_1_ - 0.5F);
      this.hat.setPos(0.0F, -14.0F, 0.0F);
      this.body = new ModelRenderer(this, 32, 16);
      this.body.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, p_i46305_1_);
      this.body.setPos(0.0F, -14.0F, 0.0F);
      this.rightArm = new ModelRenderer(this, 56, 0);
      this.rightArm.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F, p_i46305_1_);
      this.rightArm.setPos(-3.0F, -12.0F, 0.0F);
      this.leftArm = new ModelRenderer(this, 56, 0);
      this.leftArm.mirror = true;
      this.leftArm.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F, p_i46305_1_);
      this.leftArm.setPos(5.0F, -12.0F, 0.0F);
      this.rightLeg = new ModelRenderer(this, 56, 0);
      this.rightLeg.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F, p_i46305_1_);
      this.rightLeg.setPos(-2.0F, -2.0F, 0.0F);
      this.leftLeg = new ModelRenderer(this, 56, 0);
      this.leftLeg.mirror = true;
      this.leftLeg.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F, p_i46305_1_);
      this.leftLeg.setPos(2.0F, -2.0F, 0.0F);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      super.setupAnim(p_225597_1_, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
      this.head.visible = true;
      float f = -14.0F;
      this.body.xRot = 0.0F;
      this.body.y = -14.0F;
      this.body.z = -0.0F;
      this.rightLeg.xRot -= 0.0F;
      this.leftLeg.xRot -= 0.0F;
      this.rightArm.xRot = (float)((double)this.rightArm.xRot * 0.5D);
      this.leftArm.xRot = (float)((double)this.leftArm.xRot * 0.5D);
      this.rightLeg.xRot = (float)((double)this.rightLeg.xRot * 0.5D);
      this.leftLeg.xRot = (float)((double)this.leftLeg.xRot * 0.5D);
      float f1 = 0.4F;
      if (this.rightArm.xRot > 0.4F) {
         this.rightArm.xRot = 0.4F;
      }

      if (this.leftArm.xRot > 0.4F) {
         this.leftArm.xRot = 0.4F;
      }

      if (this.rightArm.xRot < -0.4F) {
         this.rightArm.xRot = -0.4F;
      }

      if (this.leftArm.xRot < -0.4F) {
         this.leftArm.xRot = -0.4F;
      }

      if (this.rightLeg.xRot > 0.4F) {
         this.rightLeg.xRot = 0.4F;
      }

      if (this.leftLeg.xRot > 0.4F) {
         this.leftLeg.xRot = 0.4F;
      }

      if (this.rightLeg.xRot < -0.4F) {
         this.rightLeg.xRot = -0.4F;
      }

      if (this.leftLeg.xRot < -0.4F) {
         this.leftLeg.xRot = -0.4F;
      }

      if (this.carrying) {
         this.rightArm.xRot = -0.5F;
         this.leftArm.xRot = -0.5F;
         this.rightArm.zRot = 0.05F;
         this.leftArm.zRot = -0.05F;
      }

      this.rightArm.z = 0.0F;
      this.leftArm.z = 0.0F;
      this.rightLeg.z = 0.0F;
      this.leftLeg.z = 0.0F;
      this.rightLeg.y = -5.0F;
      this.leftLeg.y = -5.0F;
      this.head.z = -0.0F;
      this.head.y = -13.0F;
      this.hat.x = this.head.x;
      this.hat.y = this.head.y;
      this.hat.z = this.head.z;
      this.hat.xRot = this.head.xRot;
      this.hat.yRot = this.head.yRot;
      this.hat.zRot = this.head.zRot;
      if (this.creepy) {
         float f2 = 1.0F;
         this.head.y -= 5.0F;
      }

      float f3 = -14.0F;
      this.rightArm.setPos(-5.0F, -12.0F, 0.0F);
      this.leftArm.setPos(5.0F, -12.0F, 0.0F);
   }
}
