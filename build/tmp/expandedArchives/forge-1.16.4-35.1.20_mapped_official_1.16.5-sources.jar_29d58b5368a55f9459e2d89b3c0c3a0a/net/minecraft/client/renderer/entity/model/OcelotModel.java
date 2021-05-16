package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OcelotModel<T extends Entity> extends AgeableModel<T> {
   protected final ModelRenderer backLegL;
   protected final ModelRenderer backLegR;
   protected final ModelRenderer frontLegL;
   protected final ModelRenderer frontLegR;
   protected final ModelRenderer tail1;
   protected final ModelRenderer tail2;
   protected final ModelRenderer head;
   protected final ModelRenderer body;
   protected int state = 1;

   public OcelotModel(float p_i51064_1_) {
      super(true, 10.0F, 4.0F);
      this.head = new ModelRenderer(this);
      this.head.addBox("main", -2.5F, -2.0F, -3.0F, 5, 4, 5, p_i51064_1_, 0, 0);
      this.head.addBox("nose", -1.5F, 0.0F, -4.0F, 3, 2, 2, p_i51064_1_, 0, 24);
      this.head.addBox("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2, p_i51064_1_, 0, 10);
      this.head.addBox("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2, p_i51064_1_, 6, 10);
      this.head.setPos(0.0F, 15.0F, -9.0F);
      this.body = new ModelRenderer(this, 20, 0);
      this.body.addBox(-2.0F, 3.0F, -8.0F, 4.0F, 16.0F, 6.0F, p_i51064_1_);
      this.body.setPos(0.0F, 12.0F, -10.0F);
      this.tail1 = new ModelRenderer(this, 0, 15);
      this.tail1.addBox(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, p_i51064_1_);
      this.tail1.xRot = 0.9F;
      this.tail1.setPos(0.0F, 15.0F, 8.0F);
      this.tail2 = new ModelRenderer(this, 4, 15);
      this.tail2.addBox(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, p_i51064_1_);
      this.tail2.setPos(0.0F, 20.0F, 14.0F);
      this.backLegL = new ModelRenderer(this, 8, 13);
      this.backLegL.addBox(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 2.0F, p_i51064_1_);
      this.backLegL.setPos(1.1F, 18.0F, 5.0F);
      this.backLegR = new ModelRenderer(this, 8, 13);
      this.backLegR.addBox(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 2.0F, p_i51064_1_);
      this.backLegR.setPos(-1.1F, 18.0F, 5.0F);
      this.frontLegL = new ModelRenderer(this, 40, 0);
      this.frontLegL.addBox(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 2.0F, p_i51064_1_);
      this.frontLegL.setPos(1.2F, 14.1F, -5.0F);
      this.frontLegR = new ModelRenderer(this, 40, 0);
      this.frontLegR.addBox(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 2.0F, p_i51064_1_);
      this.frontLegR.setPos(-1.2F, 14.1F, -5.0F);
   }

   protected Iterable<ModelRenderer> headParts() {
      return ImmutableList.of(this.head);
   }

   protected Iterable<ModelRenderer> bodyParts() {
      return ImmutableList.of(this.body, this.backLegL, this.backLegR, this.frontLegL, this.frontLegR, this.tail1, this.tail2);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      this.head.xRot = p_225597_6_ * ((float)Math.PI / 180F);
      this.head.yRot = p_225597_5_ * ((float)Math.PI / 180F);
      if (this.state != 3) {
         this.body.xRot = ((float)Math.PI / 2F);
         if (this.state == 2) {
            this.backLegL.xRot = MathHelper.cos(p_225597_2_ * 0.6662F) * p_225597_3_;
            this.backLegR.xRot = MathHelper.cos(p_225597_2_ * 0.6662F + 0.3F) * p_225597_3_;
            this.frontLegL.xRot = MathHelper.cos(p_225597_2_ * 0.6662F + (float)Math.PI + 0.3F) * p_225597_3_;
            this.frontLegR.xRot = MathHelper.cos(p_225597_2_ * 0.6662F + (float)Math.PI) * p_225597_3_;
            this.tail2.xRot = 1.7278761F + ((float)Math.PI / 10F) * MathHelper.cos(p_225597_2_) * p_225597_3_;
         } else {
            this.backLegL.xRot = MathHelper.cos(p_225597_2_ * 0.6662F) * p_225597_3_;
            this.backLegR.xRot = MathHelper.cos(p_225597_2_ * 0.6662F + (float)Math.PI) * p_225597_3_;
            this.frontLegL.xRot = MathHelper.cos(p_225597_2_ * 0.6662F + (float)Math.PI) * p_225597_3_;
            this.frontLegR.xRot = MathHelper.cos(p_225597_2_ * 0.6662F) * p_225597_3_;
            if (this.state == 1) {
               this.tail2.xRot = 1.7278761F + ((float)Math.PI / 4F) * MathHelper.cos(p_225597_2_) * p_225597_3_;
            } else {
               this.tail2.xRot = 1.7278761F + 0.47123894F * MathHelper.cos(p_225597_2_) * p_225597_3_;
            }
         }
      }

   }

   public void prepareMobModel(T p_212843_1_, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
      this.body.y = 12.0F;
      this.body.z = -10.0F;
      this.head.y = 15.0F;
      this.head.z = -9.0F;
      this.tail1.y = 15.0F;
      this.tail1.z = 8.0F;
      this.tail2.y = 20.0F;
      this.tail2.z = 14.0F;
      this.frontLegL.y = 14.1F;
      this.frontLegL.z = -5.0F;
      this.frontLegR.y = 14.1F;
      this.frontLegR.z = -5.0F;
      this.backLegL.y = 18.0F;
      this.backLegL.z = 5.0F;
      this.backLegR.y = 18.0F;
      this.backLegR.z = 5.0F;
      this.tail1.xRot = 0.9F;
      if (p_212843_1_.isCrouching()) {
         ++this.body.y;
         this.head.y += 2.0F;
         ++this.tail1.y;
         this.tail2.y += -4.0F;
         this.tail2.z += 2.0F;
         this.tail1.xRot = ((float)Math.PI / 2F);
         this.tail2.xRot = ((float)Math.PI / 2F);
         this.state = 0;
      } else if (p_212843_1_.isSprinting()) {
         this.tail2.y = this.tail1.y;
         this.tail2.z += 2.0F;
         this.tail1.xRot = ((float)Math.PI / 2F);
         this.tail2.xRot = ((float)Math.PI / 2F);
         this.state = 2;
      } else {
         this.state = 1;
      }

   }
}
