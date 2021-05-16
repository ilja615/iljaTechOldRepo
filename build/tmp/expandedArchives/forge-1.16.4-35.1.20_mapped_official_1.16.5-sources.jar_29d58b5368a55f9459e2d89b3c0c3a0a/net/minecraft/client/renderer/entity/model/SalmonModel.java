package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SalmonModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer bodyFront;
   private final ModelRenderer bodyBack;
   private final ModelRenderer head;
   private final ModelRenderer sideFin0;
   private final ModelRenderer sideFin1;

   public SalmonModel() {
      this.texWidth = 32;
      this.texHeight = 32;
      int i = 20;
      this.bodyFront = new ModelRenderer(this, 0, 0);
      this.bodyFront.addBox(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F);
      this.bodyFront.setPos(0.0F, 20.0F, 0.0F);
      this.bodyBack = new ModelRenderer(this, 0, 13);
      this.bodyBack.addBox(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F);
      this.bodyBack.setPos(0.0F, 20.0F, 8.0F);
      this.head = new ModelRenderer(this, 22, 0);
      this.head.addBox(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 3.0F);
      this.head.setPos(0.0F, 20.0F, 0.0F);
      ModelRenderer modelrenderer = new ModelRenderer(this, 20, 10);
      modelrenderer.addBox(0.0F, -2.5F, 0.0F, 0.0F, 5.0F, 6.0F);
      modelrenderer.setPos(0.0F, 0.0F, 8.0F);
      this.bodyBack.addChild(modelrenderer);
      ModelRenderer modelrenderer1 = new ModelRenderer(this, 2, 1);
      modelrenderer1.addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 3.0F);
      modelrenderer1.setPos(0.0F, -4.5F, 5.0F);
      this.bodyFront.addChild(modelrenderer1);
      ModelRenderer modelrenderer2 = new ModelRenderer(this, 0, 2);
      modelrenderer2.addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 4.0F);
      modelrenderer2.setPos(0.0F, -4.5F, -1.0F);
      this.bodyBack.addChild(modelrenderer2);
      this.sideFin0 = new ModelRenderer(this, -4, 0);
      this.sideFin0.addBox(-2.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F);
      this.sideFin0.setPos(-1.5F, 21.5F, 0.0F);
      this.sideFin0.zRot = (-(float)Math.PI / 4F);
      this.sideFin1 = new ModelRenderer(this, 0, 0);
      this.sideFin1.addBox(0.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F);
      this.sideFin1.setPos(1.5F, 21.5F, 0.0F);
      this.sideFin1.zRot = ((float)Math.PI / 4F);
   }

   public Iterable<ModelRenderer> parts() {
      return ImmutableList.of(this.bodyFront, this.bodyBack, this.head, this.sideFin0, this.sideFin1);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      float f = 1.0F;
      float f1 = 1.0F;
      if (!p_225597_1_.isInWater()) {
         f = 1.3F;
         f1 = 1.7F;
      }

      this.bodyBack.yRot = -f * 0.25F * MathHelper.sin(f1 * 0.6F * p_225597_4_);
   }
}
