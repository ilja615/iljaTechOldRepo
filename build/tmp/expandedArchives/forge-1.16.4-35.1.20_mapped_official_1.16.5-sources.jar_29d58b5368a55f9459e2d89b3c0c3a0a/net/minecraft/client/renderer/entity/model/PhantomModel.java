package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PhantomModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer body;
   private final ModelRenderer leftWingBase;
   private final ModelRenderer leftWingTip;
   private final ModelRenderer rightWingBase;
   private final ModelRenderer rightWingTip;
   private final ModelRenderer tailBase;
   private final ModelRenderer tailTip;

   public PhantomModel() {
      this.texWidth = 64;
      this.texHeight = 64;
      this.body = new ModelRenderer(this, 0, 8);
      this.body.addBox(-3.0F, -2.0F, -8.0F, 5.0F, 3.0F, 9.0F);
      this.tailBase = new ModelRenderer(this, 3, 20);
      this.tailBase.addBox(-2.0F, 0.0F, 0.0F, 3.0F, 2.0F, 6.0F);
      this.tailBase.setPos(0.0F, -2.0F, 1.0F);
      this.body.addChild(this.tailBase);
      this.tailTip = new ModelRenderer(this, 4, 29);
      this.tailTip.addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 6.0F);
      this.tailTip.setPos(0.0F, 0.5F, 6.0F);
      this.tailBase.addChild(this.tailTip);
      this.leftWingBase = new ModelRenderer(this, 23, 12);
      this.leftWingBase.addBox(0.0F, 0.0F, 0.0F, 6.0F, 2.0F, 9.0F);
      this.leftWingBase.setPos(2.0F, -2.0F, -8.0F);
      this.leftWingTip = new ModelRenderer(this, 16, 24);
      this.leftWingTip.addBox(0.0F, 0.0F, 0.0F, 13.0F, 1.0F, 9.0F);
      this.leftWingTip.setPos(6.0F, 0.0F, 0.0F);
      this.leftWingBase.addChild(this.leftWingTip);
      this.rightWingBase = new ModelRenderer(this, 23, 12);
      this.rightWingBase.mirror = true;
      this.rightWingBase.addBox(-6.0F, 0.0F, 0.0F, 6.0F, 2.0F, 9.0F);
      this.rightWingBase.setPos(-3.0F, -2.0F, -8.0F);
      this.rightWingTip = new ModelRenderer(this, 16, 24);
      this.rightWingTip.mirror = true;
      this.rightWingTip.addBox(-13.0F, 0.0F, 0.0F, 13.0F, 1.0F, 9.0F);
      this.rightWingTip.setPos(-6.0F, 0.0F, 0.0F);
      this.rightWingBase.addChild(this.rightWingTip);
      this.leftWingBase.zRot = 0.1F;
      this.leftWingTip.zRot = 0.1F;
      this.rightWingBase.zRot = -0.1F;
      this.rightWingTip.zRot = -0.1F;
      this.body.xRot = -0.1F;
      ModelRenderer modelrenderer = new ModelRenderer(this, 0, 0);
      modelrenderer.addBox(-4.0F, -2.0F, -5.0F, 7.0F, 3.0F, 5.0F);
      modelrenderer.setPos(0.0F, 1.0F, -7.0F);
      modelrenderer.xRot = 0.2F;
      this.body.addChild(modelrenderer);
      this.body.addChild(this.leftWingBase);
      this.body.addChild(this.rightWingBase);
   }

   public Iterable<ModelRenderer> parts() {
      return ImmutableList.of(this.body);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      float f = ((float)(p_225597_1_.getId() * 3) + p_225597_4_) * 0.13F;
      float f1 = 16.0F;
      this.leftWingBase.zRot = MathHelper.cos(f) * 16.0F * ((float)Math.PI / 180F);
      this.leftWingTip.zRot = MathHelper.cos(f) * 16.0F * ((float)Math.PI / 180F);
      this.rightWingBase.zRot = -this.leftWingBase.zRot;
      this.rightWingTip.zRot = -this.leftWingTip.zRot;
      this.tailBase.xRot = -(5.0F + MathHelper.cos(f * 2.0F) * 5.0F) * ((float)Math.PI / 180F);
      this.tailTip.xRot = -(5.0F + MathHelper.cos(f * 2.0F) * 5.0F) * ((float)Math.PI / 180F);
   }
}
