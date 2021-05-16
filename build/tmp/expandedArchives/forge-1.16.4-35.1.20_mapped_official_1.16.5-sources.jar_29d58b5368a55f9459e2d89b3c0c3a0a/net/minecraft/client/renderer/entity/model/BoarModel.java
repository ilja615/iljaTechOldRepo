package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.IFlinging;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BoarModel<T extends MobEntity & IFlinging> extends AgeableModel<T> {
   private final ModelRenderer head;
   private final ModelRenderer rightEar;
   private final ModelRenderer leftEar;
   private final ModelRenderer body;
   private final ModelRenderer frontRightLeg;
   private final ModelRenderer frontLeftLeg;
   private final ModelRenderer backRightLeg;
   private final ModelRenderer backLeftLeg;
   private final ModelRenderer mane;

   public BoarModel() {
      super(true, 8.0F, 6.0F, 1.9F, 2.0F, 24.0F);
      this.texWidth = 128;
      this.texHeight = 64;
      this.body = new ModelRenderer(this);
      this.body.setPos(0.0F, 7.0F, 0.0F);
      this.body.texOffs(1, 1).addBox(-8.0F, -7.0F, -13.0F, 16.0F, 14.0F, 26.0F);
      this.mane = new ModelRenderer(this);
      this.mane.setPos(0.0F, -14.0F, -5.0F);
      this.mane.texOffs(90, 33).addBox(0.0F, 0.0F, -9.0F, 0.0F, 10.0F, 19.0F, 0.001F);
      this.body.addChild(this.mane);
      this.head = new ModelRenderer(this);
      this.head.setPos(0.0F, 2.0F, -12.0F);
      this.head.texOffs(61, 1).addBox(-7.0F, -3.0F, -19.0F, 14.0F, 6.0F, 19.0F);
      this.rightEar = new ModelRenderer(this);
      this.rightEar.setPos(-6.0F, -2.0F, -3.0F);
      this.rightEar.texOffs(1, 1).addBox(-6.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F);
      this.rightEar.zRot = -0.6981317F;
      this.head.addChild(this.rightEar);
      this.leftEar = new ModelRenderer(this);
      this.leftEar.setPos(6.0F, -2.0F, -3.0F);
      this.leftEar.texOffs(1, 6).addBox(0.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F);
      this.leftEar.zRot = 0.6981317F;
      this.head.addChild(this.leftEar);
      ModelRenderer modelrenderer = new ModelRenderer(this);
      modelrenderer.setPos(-7.0F, 2.0F, -12.0F);
      modelrenderer.texOffs(10, 13).addBox(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F);
      this.head.addChild(modelrenderer);
      ModelRenderer modelrenderer1 = new ModelRenderer(this);
      modelrenderer1.setPos(7.0F, 2.0F, -12.0F);
      modelrenderer1.texOffs(1, 13).addBox(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F);
      this.head.addChild(modelrenderer1);
      this.head.xRot = 0.87266463F;
      int i = 14;
      int j = 11;
      this.frontRightLeg = new ModelRenderer(this);
      this.frontRightLeg.setPos(-4.0F, 10.0F, -8.5F);
      this.frontRightLeg.texOffs(66, 42).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 14.0F, 6.0F);
      this.frontLeftLeg = new ModelRenderer(this);
      this.frontLeftLeg.setPos(4.0F, 10.0F, -8.5F);
      this.frontLeftLeg.texOffs(41, 42).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 14.0F, 6.0F);
      this.backRightLeg = new ModelRenderer(this);
      this.backRightLeg.setPos(-5.0F, 13.0F, 10.0F);
      this.backRightLeg.texOffs(21, 45).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F);
      this.backLeftLeg = new ModelRenderer(this);
      this.backLeftLeg.setPos(5.0F, 13.0F, 10.0F);
      this.backLeftLeg.texOffs(0, 45).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F);
   }

   protected Iterable<ModelRenderer> headParts() {
      return ImmutableList.of(this.head);
   }

   protected Iterable<ModelRenderer> bodyParts() {
      return ImmutableList.of(this.body, this.frontRightLeg, this.frontLeftLeg, this.backRightLeg, this.backLeftLeg);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      this.rightEar.zRot = -0.6981317F - p_225597_3_ * MathHelper.sin(p_225597_2_);
      this.leftEar.zRot = 0.6981317F + p_225597_3_ * MathHelper.sin(p_225597_2_);
      this.head.yRot = p_225597_5_ * ((float)Math.PI / 180F);
      int i = p_225597_1_.getAttackAnimationRemainingTicks();
      float f = 1.0F - (float)MathHelper.abs(10 - 2 * i) / 10.0F;
      this.head.xRot = MathHelper.lerp(f, 0.87266463F, -0.34906584F);
      if (p_225597_1_.isBaby()) {
         this.head.y = MathHelper.lerp(f, 2.0F, 5.0F);
         this.mane.z = -3.0F;
      } else {
         this.head.y = 2.0F;
         this.mane.z = -7.0F;
      }

      float f1 = 1.2F;
      this.frontRightLeg.xRot = MathHelper.cos(p_225597_2_) * 1.2F * p_225597_3_;
      this.frontLeftLeg.xRot = MathHelper.cos(p_225597_2_ + (float)Math.PI) * 1.2F * p_225597_3_;
      this.backRightLeg.xRot = this.frontLeftLeg.xRot;
      this.backLeftLeg.xRot = this.frontRightLeg.xRot;
   }
}
