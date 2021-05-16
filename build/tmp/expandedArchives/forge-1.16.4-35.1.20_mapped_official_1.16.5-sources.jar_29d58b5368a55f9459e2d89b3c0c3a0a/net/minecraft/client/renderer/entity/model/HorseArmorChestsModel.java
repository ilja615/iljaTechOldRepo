package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.horse.AbstractChestedHorseEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HorseArmorChestsModel<T extends AbstractChestedHorseEntity> extends HorseModel<T> {
   private final ModelRenderer boxL = new ModelRenderer(this, 26, 21);
   private final ModelRenderer boxR;

   public HorseArmorChestsModel(float p_i51068_1_) {
      super(p_i51068_1_);
      this.boxL.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 8.0F, 3.0F);
      this.boxR = new ModelRenderer(this, 26, 21);
      this.boxR.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 8.0F, 3.0F);
      this.boxL.yRot = (-(float)Math.PI / 2F);
      this.boxR.yRot = ((float)Math.PI / 2F);
      this.boxL.setPos(6.0F, -8.0F, 0.0F);
      this.boxR.setPos(-6.0F, -8.0F, 0.0F);
      this.body.addChild(this.boxL);
      this.body.addChild(this.boxR);
   }

   protected void addEarModels(ModelRenderer p_199047_1_) {
      ModelRenderer modelrenderer = new ModelRenderer(this, 0, 12);
      modelrenderer.addBox(-1.0F, -7.0F, 0.0F, 2.0F, 7.0F, 1.0F);
      modelrenderer.setPos(1.25F, -10.0F, 4.0F);
      ModelRenderer modelrenderer1 = new ModelRenderer(this, 0, 12);
      modelrenderer1.addBox(-1.0F, -7.0F, 0.0F, 2.0F, 7.0F, 1.0F);
      modelrenderer1.setPos(-1.25F, -10.0F, 4.0F);
      modelrenderer.xRot = 0.2617994F;
      modelrenderer.zRot = 0.2617994F;
      modelrenderer1.xRot = 0.2617994F;
      modelrenderer1.zRot = -0.2617994F;
      p_199047_1_.addChild(modelrenderer);
      p_199047_1_.addChild(modelrenderer1);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      super.setupAnim(p_225597_1_, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
      if (p_225597_1_.hasChest()) {
         this.boxL.visible = true;
         this.boxR.visible = true;
      } else {
         this.boxL.visible = false;
         this.boxR.visible = false;
      }

   }
}
