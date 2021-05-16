package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DolphinModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer body;
   private final ModelRenderer tail;
   private final ModelRenderer tailFin;

   public DolphinModel() {
      this.texWidth = 64;
      this.texHeight = 64;
      float f = 18.0F;
      float f1 = -8.0F;
      this.body = new ModelRenderer(this, 22, 0);
      this.body.addBox(-4.0F, -7.0F, 0.0F, 8.0F, 7.0F, 13.0F);
      this.body.setPos(0.0F, 22.0F, -5.0F);
      ModelRenderer modelrenderer = new ModelRenderer(this, 51, 0);
      modelrenderer.addBox(-0.5F, 0.0F, 8.0F, 1.0F, 4.0F, 5.0F);
      modelrenderer.xRot = ((float)Math.PI / 3F);
      this.body.addChild(modelrenderer);
      ModelRenderer modelrenderer1 = new ModelRenderer(this, 48, 20);
      modelrenderer1.mirror = true;
      modelrenderer1.addBox(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 7.0F);
      modelrenderer1.setPos(2.0F, -2.0F, 4.0F);
      modelrenderer1.xRot = ((float)Math.PI / 3F);
      modelrenderer1.zRot = 2.0943952F;
      this.body.addChild(modelrenderer1);
      ModelRenderer modelrenderer2 = new ModelRenderer(this, 48, 20);
      modelrenderer2.addBox(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 7.0F);
      modelrenderer2.setPos(-2.0F, -2.0F, 4.0F);
      modelrenderer2.xRot = ((float)Math.PI / 3F);
      modelrenderer2.zRot = -2.0943952F;
      this.body.addChild(modelrenderer2);
      this.tail = new ModelRenderer(this, 0, 19);
      this.tail.addBox(-2.0F, -2.5F, 0.0F, 4.0F, 5.0F, 11.0F);
      this.tail.setPos(0.0F, -2.5F, 11.0F);
      this.tail.xRot = -0.10471976F;
      this.body.addChild(this.tail);
      this.tailFin = new ModelRenderer(this, 19, 20);
      this.tailFin.addBox(-5.0F, -0.5F, 0.0F, 10.0F, 1.0F, 6.0F);
      this.tailFin.setPos(0.0F, 0.0F, 9.0F);
      this.tailFin.xRot = 0.0F;
      this.tail.addChild(this.tailFin);
      ModelRenderer modelrenderer3 = new ModelRenderer(this, 0, 0);
      modelrenderer3.addBox(-4.0F, -3.0F, -3.0F, 8.0F, 7.0F, 6.0F);
      modelrenderer3.setPos(0.0F, -4.0F, -3.0F);
      ModelRenderer modelrenderer4 = new ModelRenderer(this, 0, 13);
      modelrenderer4.addBox(-1.0F, 2.0F, -7.0F, 2.0F, 2.0F, 4.0F);
      modelrenderer3.addChild(modelrenderer4);
      this.body.addChild(modelrenderer3);
   }

   public Iterable<ModelRenderer> parts() {
      return ImmutableList.of(this.body);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      this.body.xRot = p_225597_6_ * ((float)Math.PI / 180F);
      this.body.yRot = p_225597_5_ * ((float)Math.PI / 180F);
      if (Entity.getHorizontalDistanceSqr(p_225597_1_.getDeltaMovement()) > 1.0E-7D) {
         this.body.xRot += -0.05F + -0.05F * MathHelper.cos(p_225597_4_ * 0.3F);
         this.tail.xRot = -0.1F * MathHelper.cos(p_225597_4_ * 0.3F);
         this.tailFin.xRot = -0.2F * MathHelper.cos(p_225597_4_ * 0.3F);
      }

   }
}
