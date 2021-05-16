package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class QuadrupedModel<T extends Entity> extends AgeableModel<T> {
   protected ModelRenderer head = new ModelRenderer(this, 0, 0);
   protected ModelRenderer body;
   protected ModelRenderer leg0;
   protected ModelRenderer leg1;
   protected ModelRenderer leg2;
   protected ModelRenderer leg3;

   public QuadrupedModel(int p_i225948_1_, float p_i225948_2_, boolean p_i225948_3_, float p_i225948_4_, float p_i225948_5_, float p_i225948_6_, float p_i225948_7_, int p_i225948_8_) {
      super(p_i225948_3_, p_i225948_4_, p_i225948_5_, p_i225948_6_, p_i225948_7_, (float)p_i225948_8_);
      this.head.addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, p_i225948_2_);
      this.head.setPos(0.0F, (float)(18 - p_i225948_1_), -6.0F);
      this.body = new ModelRenderer(this, 28, 8);
      this.body.addBox(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F, p_i225948_2_);
      this.body.setPos(0.0F, (float)(17 - p_i225948_1_), 2.0F);
      this.leg0 = new ModelRenderer(this, 0, 16);
      this.leg0.addBox(-2.0F, 0.0F, -2.0F, 4.0F, (float)p_i225948_1_, 4.0F, p_i225948_2_);
      this.leg0.setPos(-3.0F, (float)(24 - p_i225948_1_), 7.0F);
      this.leg1 = new ModelRenderer(this, 0, 16);
      this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4.0F, (float)p_i225948_1_, 4.0F, p_i225948_2_);
      this.leg1.setPos(3.0F, (float)(24 - p_i225948_1_), 7.0F);
      this.leg2 = new ModelRenderer(this, 0, 16);
      this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4.0F, (float)p_i225948_1_, 4.0F, p_i225948_2_);
      this.leg2.setPos(-3.0F, (float)(24 - p_i225948_1_), -5.0F);
      this.leg3 = new ModelRenderer(this, 0, 16);
      this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4.0F, (float)p_i225948_1_, 4.0F, p_i225948_2_);
      this.leg3.setPos(3.0F, (float)(24 - p_i225948_1_), -5.0F);
   }

   protected Iterable<ModelRenderer> headParts() {
      return ImmutableList.of(this.head);
   }

   protected Iterable<ModelRenderer> bodyParts() {
      return ImmutableList.of(this.body, this.leg0, this.leg1, this.leg2, this.leg3);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      this.head.xRot = p_225597_6_ * ((float)Math.PI / 180F);
      this.head.yRot = p_225597_5_ * ((float)Math.PI / 180F);
      this.body.xRot = ((float)Math.PI / 2F);
      this.leg0.xRot = MathHelper.cos(p_225597_2_ * 0.6662F) * 1.4F * p_225597_3_;
      this.leg1.xRot = MathHelper.cos(p_225597_2_ * 0.6662F + (float)Math.PI) * 1.4F * p_225597_3_;
      this.leg2.xRot = MathHelper.cos(p_225597_2_ * 0.6662F + (float)Math.PI) * 1.4F * p_225597_3_;
      this.leg3.xRot = MathHelper.cos(p_225597_2_ * 0.6662F) * 1.4F * p_225597_3_;
   }
}
