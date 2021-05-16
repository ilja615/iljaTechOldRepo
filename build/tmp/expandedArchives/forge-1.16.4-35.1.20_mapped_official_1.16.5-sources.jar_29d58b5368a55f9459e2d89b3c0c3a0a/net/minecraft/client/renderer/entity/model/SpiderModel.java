package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpiderModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer head;
   private final ModelRenderer body0;
   private final ModelRenderer body1;
   private final ModelRenderer leg0;
   private final ModelRenderer leg1;
   private final ModelRenderer leg2;
   private final ModelRenderer leg3;
   private final ModelRenderer leg4;
   private final ModelRenderer leg5;
   private final ModelRenderer leg6;
   private final ModelRenderer leg7;

   public SpiderModel() {
      float f = 0.0F;
      int i = 15;
      this.head = new ModelRenderer(this, 32, 4);
      this.head.addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, 0.0F);
      this.head.setPos(0.0F, 15.0F, -3.0F);
      this.body0 = new ModelRenderer(this, 0, 0);
      this.body0.addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, 0.0F);
      this.body0.setPos(0.0F, 15.0F, 0.0F);
      this.body1 = new ModelRenderer(this, 0, 12);
      this.body1.addBox(-5.0F, -4.0F, -6.0F, 10.0F, 8.0F, 12.0F, 0.0F);
      this.body1.setPos(0.0F, 15.0F, 9.0F);
      this.leg0 = new ModelRenderer(this, 18, 0);
      this.leg0.addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
      this.leg0.setPos(-4.0F, 15.0F, 2.0F);
      this.leg1 = new ModelRenderer(this, 18, 0);
      this.leg1.addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
      this.leg1.setPos(4.0F, 15.0F, 2.0F);
      this.leg2 = new ModelRenderer(this, 18, 0);
      this.leg2.addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
      this.leg2.setPos(-4.0F, 15.0F, 1.0F);
      this.leg3 = new ModelRenderer(this, 18, 0);
      this.leg3.addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
      this.leg3.setPos(4.0F, 15.0F, 1.0F);
      this.leg4 = new ModelRenderer(this, 18, 0);
      this.leg4.addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
      this.leg4.setPos(-4.0F, 15.0F, 0.0F);
      this.leg5 = new ModelRenderer(this, 18, 0);
      this.leg5.addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
      this.leg5.setPos(4.0F, 15.0F, 0.0F);
      this.leg6 = new ModelRenderer(this, 18, 0);
      this.leg6.addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
      this.leg6.setPos(-4.0F, 15.0F, -1.0F);
      this.leg7 = new ModelRenderer(this, 18, 0);
      this.leg7.addBox(-1.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F, 0.0F);
      this.leg7.setPos(4.0F, 15.0F, -1.0F);
   }

   public Iterable<ModelRenderer> parts() {
      return ImmutableList.of(this.head, this.body0, this.body1, this.leg0, this.leg1, this.leg2, this.leg3, this.leg4, this.leg5, this.leg6, this.leg7);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      this.head.yRot = p_225597_5_ * ((float)Math.PI / 180F);
      this.head.xRot = p_225597_6_ * ((float)Math.PI / 180F);
      float f = ((float)Math.PI / 4F);
      this.leg0.zRot = (-(float)Math.PI / 4F);
      this.leg1.zRot = ((float)Math.PI / 4F);
      this.leg2.zRot = -0.58119464F;
      this.leg3.zRot = 0.58119464F;
      this.leg4.zRot = -0.58119464F;
      this.leg5.zRot = 0.58119464F;
      this.leg6.zRot = (-(float)Math.PI / 4F);
      this.leg7.zRot = ((float)Math.PI / 4F);
      float f1 = -0.0F;
      float f2 = ((float)Math.PI / 8F);
      this.leg0.yRot = ((float)Math.PI / 4F);
      this.leg1.yRot = (-(float)Math.PI / 4F);
      this.leg2.yRot = ((float)Math.PI / 8F);
      this.leg3.yRot = (-(float)Math.PI / 8F);
      this.leg4.yRot = (-(float)Math.PI / 8F);
      this.leg5.yRot = ((float)Math.PI / 8F);
      this.leg6.yRot = (-(float)Math.PI / 4F);
      this.leg7.yRot = ((float)Math.PI / 4F);
      float f3 = -(MathHelper.cos(p_225597_2_ * 0.6662F * 2.0F + 0.0F) * 0.4F) * p_225597_3_;
      float f4 = -(MathHelper.cos(p_225597_2_ * 0.6662F * 2.0F + (float)Math.PI) * 0.4F) * p_225597_3_;
      float f5 = -(MathHelper.cos(p_225597_2_ * 0.6662F * 2.0F + ((float)Math.PI / 2F)) * 0.4F) * p_225597_3_;
      float f6 = -(MathHelper.cos(p_225597_2_ * 0.6662F * 2.0F + ((float)Math.PI * 1.5F)) * 0.4F) * p_225597_3_;
      float f7 = Math.abs(MathHelper.sin(p_225597_2_ * 0.6662F + 0.0F) * 0.4F) * p_225597_3_;
      float f8 = Math.abs(MathHelper.sin(p_225597_2_ * 0.6662F + (float)Math.PI) * 0.4F) * p_225597_3_;
      float f9 = Math.abs(MathHelper.sin(p_225597_2_ * 0.6662F + ((float)Math.PI / 2F)) * 0.4F) * p_225597_3_;
      float f10 = Math.abs(MathHelper.sin(p_225597_2_ * 0.6662F + ((float)Math.PI * 1.5F)) * 0.4F) * p_225597_3_;
      this.leg0.yRot += f3;
      this.leg1.yRot += -f3;
      this.leg2.yRot += f4;
      this.leg3.yRot += -f4;
      this.leg4.yRot += f5;
      this.leg5.yRot += -f5;
      this.leg6.yRot += f6;
      this.leg7.yRot += -f6;
      this.leg0.zRot += f7;
      this.leg1.zRot += -f7;
      this.leg2.zRot += f8;
      this.leg3.zRot += -f8;
      this.leg4.zRot += f9;
      this.leg5.zRot += -f9;
      this.leg6.zRot += f10;
      this.leg7.zRot += -f10;
   }
}
