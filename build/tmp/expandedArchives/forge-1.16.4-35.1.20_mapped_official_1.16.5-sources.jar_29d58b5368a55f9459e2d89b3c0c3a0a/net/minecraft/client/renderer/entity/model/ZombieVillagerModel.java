package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZombieVillagerModel<T extends ZombieEntity> extends BipedModel<T> implements IHeadToggle {
   private ModelRenderer hatRim;

   public ZombieVillagerModel(float p_i51058_1_, boolean p_i51058_2_) {
      super(p_i51058_1_, 0.0F, 64, p_i51058_2_ ? 32 : 64);
      if (p_i51058_2_) {
         this.head = new ModelRenderer(this, 0, 0);
         this.head.addBox(-4.0F, -10.0F, -4.0F, 8.0F, 8.0F, 8.0F, p_i51058_1_);
         this.body = new ModelRenderer(this, 16, 16);
         this.body.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, p_i51058_1_ + 0.1F);
         this.rightLeg = new ModelRenderer(this, 0, 16);
         this.rightLeg.setPos(-2.0F, 12.0F, 0.0F);
         this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i51058_1_ + 0.1F);
         this.leftLeg = new ModelRenderer(this, 0, 16);
         this.leftLeg.mirror = true;
         this.leftLeg.setPos(2.0F, 12.0F, 0.0F);
         this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i51058_1_ + 0.1F);
      } else {
         this.head = new ModelRenderer(this, 0, 0);
         this.head.texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, p_i51058_1_);
         this.head.texOffs(24, 0).addBox(-1.0F, -3.0F, -6.0F, 2.0F, 4.0F, 2.0F, p_i51058_1_);
         this.hat = new ModelRenderer(this, 32, 0);
         this.hat.addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, p_i51058_1_ + 0.5F);
         this.hatRim = new ModelRenderer(this);
         this.hatRim.texOffs(30, 47).addBox(-8.0F, -8.0F, -6.0F, 16.0F, 16.0F, 1.0F, p_i51058_1_);
         this.hatRim.xRot = (-(float)Math.PI / 2F);
         this.hat.addChild(this.hatRim);
         this.body = new ModelRenderer(this, 16, 20);
         this.body.addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, p_i51058_1_);
         this.body.texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, p_i51058_1_ + 0.05F);
         this.rightArm = new ModelRenderer(this, 44, 22);
         this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i51058_1_);
         this.rightArm.setPos(-5.0F, 2.0F, 0.0F);
         this.leftArm = new ModelRenderer(this, 44, 22);
         this.leftArm.mirror = true;
         this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i51058_1_);
         this.leftArm.setPos(5.0F, 2.0F, 0.0F);
         this.rightLeg = new ModelRenderer(this, 0, 22);
         this.rightLeg.setPos(-2.0F, 12.0F, 0.0F);
         this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i51058_1_);
         this.leftLeg = new ModelRenderer(this, 0, 22);
         this.leftLeg.mirror = true;
         this.leftLeg.setPos(2.0F, 12.0F, 0.0F);
         this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i51058_1_);
      }

   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      super.setupAnim(p_225597_1_, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
      ModelHelper.animateZombieArms(this.leftArm, this.rightArm, p_225597_1_.isAggressive(), this.attackTime, p_225597_4_);
   }

   public void hatVisible(boolean p_217146_1_) {
      this.head.visible = p_217146_1_;
      this.hat.visible = p_217146_1_;
      this.hatRim.visible = p_217146_1_;
   }
}
