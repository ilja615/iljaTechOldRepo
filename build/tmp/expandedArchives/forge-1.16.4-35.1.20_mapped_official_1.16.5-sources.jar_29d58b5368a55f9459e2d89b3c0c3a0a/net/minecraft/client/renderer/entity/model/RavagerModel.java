package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RavagerModel extends SegmentedModel<RavagerEntity> {
   private final ModelRenderer head;
   private final ModelRenderer mouth;
   private final ModelRenderer body;
   private final ModelRenderer leg0;
   private final ModelRenderer leg1;
   private final ModelRenderer leg2;
   private final ModelRenderer leg3;
   private final ModelRenderer neck;

   public RavagerModel() {
      this.texWidth = 128;
      this.texHeight = 128;
      int i = 16;
      float f = 0.0F;
      this.neck = new ModelRenderer(this);
      this.neck.setPos(0.0F, -7.0F, -1.5F);
      this.neck.texOffs(68, 73).addBox(-5.0F, -1.0F, -18.0F, 10.0F, 10.0F, 18.0F, 0.0F);
      this.head = new ModelRenderer(this);
      this.head.setPos(0.0F, 16.0F, -17.0F);
      this.head.texOffs(0, 0).addBox(-8.0F, -20.0F, -14.0F, 16.0F, 20.0F, 16.0F, 0.0F);
      this.head.texOffs(0, 0).addBox(-2.0F, -6.0F, -18.0F, 4.0F, 8.0F, 4.0F, 0.0F);
      ModelRenderer modelrenderer = new ModelRenderer(this);
      modelrenderer.setPos(-10.0F, -14.0F, -8.0F);
      modelrenderer.texOffs(74, 55).addBox(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F, 0.0F);
      modelrenderer.xRot = 1.0995574F;
      this.head.addChild(modelrenderer);
      ModelRenderer modelrenderer1 = new ModelRenderer(this);
      modelrenderer1.mirror = true;
      modelrenderer1.setPos(8.0F, -14.0F, -8.0F);
      modelrenderer1.texOffs(74, 55).addBox(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F, 0.0F);
      modelrenderer1.xRot = 1.0995574F;
      this.head.addChild(modelrenderer1);
      this.mouth = new ModelRenderer(this);
      this.mouth.setPos(0.0F, -2.0F, 2.0F);
      this.mouth.texOffs(0, 36).addBox(-8.0F, 0.0F, -16.0F, 16.0F, 3.0F, 16.0F, 0.0F);
      this.head.addChild(this.mouth);
      this.neck.addChild(this.head);
      this.body = new ModelRenderer(this);
      this.body.texOffs(0, 55).addBox(-7.0F, -10.0F, -7.0F, 14.0F, 16.0F, 20.0F, 0.0F);
      this.body.texOffs(0, 91).addBox(-6.0F, 6.0F, -7.0F, 12.0F, 13.0F, 18.0F, 0.0F);
      this.body.setPos(0.0F, 1.0F, 2.0F);
      this.leg0 = new ModelRenderer(this, 96, 0);
      this.leg0.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F, 0.0F);
      this.leg0.setPos(-8.0F, -13.0F, 18.0F);
      this.leg1 = new ModelRenderer(this, 96, 0);
      this.leg1.mirror = true;
      this.leg1.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F, 0.0F);
      this.leg1.setPos(8.0F, -13.0F, 18.0F);
      this.leg2 = new ModelRenderer(this, 64, 0);
      this.leg2.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F, 0.0F);
      this.leg2.setPos(-8.0F, -13.0F, -5.0F);
      this.leg3 = new ModelRenderer(this, 64, 0);
      this.leg3.mirror = true;
      this.leg3.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F, 0.0F);
      this.leg3.setPos(8.0F, -13.0F, -5.0F);
   }

   public Iterable<ModelRenderer> parts() {
      return ImmutableList.of(this.neck, this.body, this.leg0, this.leg1, this.leg2, this.leg3);
   }

   public void setupAnim(RavagerEntity p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      this.head.xRot = p_225597_6_ * ((float)Math.PI / 180F);
      this.head.yRot = p_225597_5_ * ((float)Math.PI / 180F);
      this.body.xRot = ((float)Math.PI / 2F);
      float f = 0.4F * p_225597_3_;
      this.leg0.xRot = MathHelper.cos(p_225597_2_ * 0.6662F) * f;
      this.leg1.xRot = MathHelper.cos(p_225597_2_ * 0.6662F + (float)Math.PI) * f;
      this.leg2.xRot = MathHelper.cos(p_225597_2_ * 0.6662F + (float)Math.PI) * f;
      this.leg3.xRot = MathHelper.cos(p_225597_2_ * 0.6662F) * f;
   }

   public void prepareMobModel(RavagerEntity p_212843_1_, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
      super.prepareMobModel(p_212843_1_, p_212843_2_, p_212843_3_, p_212843_4_);
      int i = p_212843_1_.getStunnedTick();
      int j = p_212843_1_.getRoarTick();
      int k = 20;
      int l = p_212843_1_.getAttackTick();
      int i1 = 10;
      if (l > 0) {
         float f = MathHelper.triangleWave((float)l - p_212843_4_, 10.0F);
         float f1 = (1.0F + f) * 0.5F;
         float f2 = f1 * f1 * f1 * 12.0F;
         float f3 = f2 * MathHelper.sin(this.neck.xRot);
         this.neck.z = -6.5F + f2;
         this.neck.y = -7.0F - f3;
         float f4 = MathHelper.sin(((float)l - p_212843_4_) / 10.0F * (float)Math.PI * 0.25F);
         this.mouth.xRot = ((float)Math.PI / 2F) * f4;
         if (l > 5) {
            this.mouth.xRot = MathHelper.sin(((float)(-4 + l) - p_212843_4_) / 4.0F) * (float)Math.PI * 0.4F;
         } else {
            this.mouth.xRot = 0.15707964F * MathHelper.sin((float)Math.PI * ((float)l - p_212843_4_) / 10.0F);
         }
      } else {
         float f5 = -1.0F;
         float f6 = -1.0F * MathHelper.sin(this.neck.xRot);
         this.neck.x = 0.0F;
         this.neck.y = -7.0F - f6;
         this.neck.z = 5.5F;
         boolean flag = i > 0;
         this.neck.xRot = flag ? 0.21991149F : 0.0F;
         this.mouth.xRot = (float)Math.PI * (flag ? 0.05F : 0.01F);
         if (flag) {
            double d0 = (double)i / 40.0D;
            this.neck.x = (float)Math.sin(d0 * 10.0D) * 3.0F;
         } else if (j > 0) {
            float f7 = MathHelper.sin(((float)(20 - j) - p_212843_4_) / 20.0F * (float)Math.PI * 0.25F);
            this.mouth.xRot = ((float)Math.PI / 2F) * f7;
         }
      }

   }
}
