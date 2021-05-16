package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PandaModel<T extends PandaEntity> extends QuadrupedModel<T> {
   private float sitAmount;
   private float lieOnBackAmount;
   private float rollAmount;

   public PandaModel(int p_i51063_1_, float p_i51063_2_) {
      super(p_i51063_1_, p_i51063_2_, true, 23.0F, 4.8F, 2.7F, 3.0F, 49);
      this.texWidth = 64;
      this.texHeight = 64;
      this.head = new ModelRenderer(this, 0, 6);
      this.head.addBox(-6.5F, -5.0F, -4.0F, 13.0F, 10.0F, 9.0F);
      this.head.setPos(0.0F, 11.5F, -17.0F);
      this.head.texOffs(45, 16).addBox(-3.5F, 0.0F, -6.0F, 7.0F, 5.0F, 2.0F);
      this.head.texOffs(52, 25).addBox(-8.5F, -8.0F, -1.0F, 5.0F, 4.0F, 1.0F);
      this.head.texOffs(52, 25).addBox(3.5F, -8.0F, -1.0F, 5.0F, 4.0F, 1.0F);
      this.body = new ModelRenderer(this, 0, 25);
      this.body.addBox(-9.5F, -13.0F, -6.5F, 19.0F, 26.0F, 13.0F);
      this.body.setPos(0.0F, 10.0F, 0.0F);
      int i = 9;
      int j = 6;
      this.leg0 = new ModelRenderer(this, 40, 0);
      this.leg0.addBox(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F);
      this.leg0.setPos(-5.5F, 15.0F, 9.0F);
      this.leg1 = new ModelRenderer(this, 40, 0);
      this.leg1.addBox(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F);
      this.leg1.setPos(5.5F, 15.0F, 9.0F);
      this.leg2 = new ModelRenderer(this, 40, 0);
      this.leg2.addBox(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F);
      this.leg2.setPos(-5.5F, 15.0F, -9.0F);
      this.leg3 = new ModelRenderer(this, 40, 0);
      this.leg3.addBox(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F);
      this.leg3.setPos(5.5F, 15.0F, -9.0F);
   }

   public void prepareMobModel(T p_212843_1_, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
      super.prepareMobModel(p_212843_1_, p_212843_2_, p_212843_3_, p_212843_4_);
      this.sitAmount = p_212843_1_.getSitAmount(p_212843_4_);
      this.lieOnBackAmount = p_212843_1_.getLieOnBackAmount(p_212843_4_);
      this.rollAmount = p_212843_1_.isBaby() ? 0.0F : p_212843_1_.getRollAmount(p_212843_4_);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      super.setupAnim(p_225597_1_, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
      boolean flag = p_225597_1_.getUnhappyCounter() > 0;
      boolean flag1 = p_225597_1_.isSneezing();
      int i = p_225597_1_.getSneezeCounter();
      boolean flag2 = p_225597_1_.isEating();
      boolean flag3 = p_225597_1_.isScared();
      if (flag) {
         this.head.yRot = 0.35F * MathHelper.sin(0.6F * p_225597_4_);
         this.head.zRot = 0.35F * MathHelper.sin(0.6F * p_225597_4_);
         this.leg2.xRot = -0.75F * MathHelper.sin(0.3F * p_225597_4_);
         this.leg3.xRot = 0.75F * MathHelper.sin(0.3F * p_225597_4_);
      } else {
         this.head.zRot = 0.0F;
      }

      if (flag1) {
         if (i < 15) {
            this.head.xRot = (-(float)Math.PI / 4F) * (float)i / 14.0F;
         } else if (i < 20) {
            float f = (float)((i - 15) / 5);
            this.head.xRot = (-(float)Math.PI / 4F) + ((float)Math.PI / 4F) * f;
         }
      }

      if (this.sitAmount > 0.0F) {
         this.body.xRot = ModelUtils.rotlerpRad(this.body.xRot, 1.7407963F, this.sitAmount);
         this.head.xRot = ModelUtils.rotlerpRad(this.head.xRot, ((float)Math.PI / 2F), this.sitAmount);
         this.leg2.zRot = -0.27079642F;
         this.leg3.zRot = 0.27079642F;
         this.leg0.zRot = 0.5707964F;
         this.leg1.zRot = -0.5707964F;
         if (flag2) {
            this.head.xRot = ((float)Math.PI / 2F) + 0.2F * MathHelper.sin(p_225597_4_ * 0.6F);
            this.leg2.xRot = -0.4F - 0.2F * MathHelper.sin(p_225597_4_ * 0.6F);
            this.leg3.xRot = -0.4F - 0.2F * MathHelper.sin(p_225597_4_ * 0.6F);
         }

         if (flag3) {
            this.head.xRot = 2.1707964F;
            this.leg2.xRot = -0.9F;
            this.leg3.xRot = -0.9F;
         }
      } else {
         this.leg0.zRot = 0.0F;
         this.leg1.zRot = 0.0F;
         this.leg2.zRot = 0.0F;
         this.leg3.zRot = 0.0F;
      }

      if (this.lieOnBackAmount > 0.0F) {
         this.leg0.xRot = -0.6F * MathHelper.sin(p_225597_4_ * 0.15F);
         this.leg1.xRot = 0.6F * MathHelper.sin(p_225597_4_ * 0.15F);
         this.leg2.xRot = 0.3F * MathHelper.sin(p_225597_4_ * 0.25F);
         this.leg3.xRot = -0.3F * MathHelper.sin(p_225597_4_ * 0.25F);
         this.head.xRot = ModelUtils.rotlerpRad(this.head.xRot, ((float)Math.PI / 2F), this.lieOnBackAmount);
      }

      if (this.rollAmount > 0.0F) {
         this.head.xRot = ModelUtils.rotlerpRad(this.head.xRot, 2.0561945F, this.rollAmount);
         this.leg0.xRot = -0.5F * MathHelper.sin(p_225597_4_ * 0.5F);
         this.leg1.xRot = 0.5F * MathHelper.sin(p_225597_4_ * 0.5F);
         this.leg2.xRot = 0.5F * MathHelper.sin(p_225597_4_ * 0.5F);
         this.leg3.xRot = -0.5F * MathHelper.sin(p_225597_4_ * 0.5F);
      }

   }
}
