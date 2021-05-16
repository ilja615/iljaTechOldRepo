package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WitchModel<T extends Entity> extends VillagerModel<T> {
   private boolean holdingItem;
   private final ModelRenderer mole = (new ModelRenderer(this)).setTexSize(64, 128);

   public WitchModel(float p_i46361_1_) {
      super(p_i46361_1_, 64, 128);
      this.mole.setPos(0.0F, -2.0F, 0.0F);
      this.mole.texOffs(0, 0).addBox(0.0F, 3.0F, -6.75F, 1.0F, 1.0F, 1.0F, -0.25F);
      this.nose.addChild(this.mole);
      this.head = (new ModelRenderer(this)).setTexSize(64, 128);
      this.head.setPos(0.0F, 0.0F, 0.0F);
      this.head.texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, p_i46361_1_);
      this.hat = (new ModelRenderer(this)).setTexSize(64, 128);
      this.hat.setPos(-5.0F, -10.03125F, -5.0F);
      this.hat.texOffs(0, 64).addBox(0.0F, 0.0F, 0.0F, 10.0F, 2.0F, 10.0F);
      this.head.addChild(this.hat);
      this.head.addChild(this.nose);
      ModelRenderer modelrenderer = (new ModelRenderer(this)).setTexSize(64, 128);
      modelrenderer.setPos(1.75F, -4.0F, 2.0F);
      modelrenderer.texOffs(0, 76).addBox(0.0F, 0.0F, 0.0F, 7.0F, 4.0F, 7.0F);
      modelrenderer.xRot = -0.05235988F;
      modelrenderer.zRot = 0.02617994F;
      this.hat.addChild(modelrenderer);
      ModelRenderer modelrenderer1 = (new ModelRenderer(this)).setTexSize(64, 128);
      modelrenderer1.setPos(1.75F, -4.0F, 2.0F);
      modelrenderer1.texOffs(0, 87).addBox(0.0F, 0.0F, 0.0F, 4.0F, 4.0F, 4.0F);
      modelrenderer1.xRot = -0.10471976F;
      modelrenderer1.zRot = 0.05235988F;
      modelrenderer.addChild(modelrenderer1);
      ModelRenderer modelrenderer2 = (new ModelRenderer(this)).setTexSize(64, 128);
      modelrenderer2.setPos(1.75F, -2.0F, 2.0F);
      modelrenderer2.texOffs(0, 95).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.25F);
      modelrenderer2.xRot = -0.20943952F;
      modelrenderer2.zRot = 0.10471976F;
      modelrenderer1.addChild(modelrenderer2);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      super.setupAnim(p_225597_1_, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
      this.nose.setPos(0.0F, -2.0F, 0.0F);
      float f = 0.01F * (float)(p_225597_1_.getId() % 10);
      this.nose.xRot = MathHelper.sin((float)p_225597_1_.tickCount * f) * 4.5F * ((float)Math.PI / 180F);
      this.nose.yRot = 0.0F;
      this.nose.zRot = MathHelper.cos((float)p_225597_1_.tickCount * f) * 2.5F * ((float)Math.PI / 180F);
      if (this.holdingItem) {
         this.nose.setPos(0.0F, 1.0F, -1.5F);
         this.nose.xRot = -0.9F;
      }

   }

   public ModelRenderer getNose() {
      return this.nose;
   }

   public void setHoldingItem(boolean p_205074_1_) {
      this.holdingItem = p_205074_1_;
   }
}
