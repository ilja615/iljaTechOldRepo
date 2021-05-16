package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PufferFishMediumModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer cube;
   private final ModelRenderer finBlue0;
   private final ModelRenderer finBlue1;
   private final ModelRenderer finTop0;
   private final ModelRenderer finTop1;
   private final ModelRenderer finSide0;
   private final ModelRenderer finSide1;
   private final ModelRenderer finSide2;
   private final ModelRenderer finSide3;
   private final ModelRenderer finBottom0;
   private final ModelRenderer finBottom1;

   public PufferFishMediumModel() {
      this.texWidth = 32;
      this.texHeight = 32;
      int i = 22;
      this.cube = new ModelRenderer(this, 12, 22);
      this.cube.addBox(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F);
      this.cube.setPos(0.0F, 22.0F, 0.0F);
      this.finBlue0 = new ModelRenderer(this, 24, 0);
      this.finBlue0.addBox(-2.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F);
      this.finBlue0.setPos(-2.5F, 17.0F, -1.5F);
      this.finBlue1 = new ModelRenderer(this, 24, 3);
      this.finBlue1.addBox(0.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F);
      this.finBlue1.setPos(2.5F, 17.0F, -1.5F);
      this.finTop0 = new ModelRenderer(this, 15, 16);
      this.finTop0.addBox(-2.5F, -1.0F, 0.0F, 5.0F, 1.0F, 1.0F);
      this.finTop0.setPos(0.0F, 17.0F, -2.5F);
      this.finTop0.xRot = ((float)Math.PI / 4F);
      this.finTop1 = new ModelRenderer(this, 10, 16);
      this.finTop1.addBox(-2.5F, -1.0F, -1.0F, 5.0F, 1.0F, 1.0F);
      this.finTop1.setPos(0.0F, 17.0F, 2.5F);
      this.finTop1.xRot = (-(float)Math.PI / 4F);
      this.finSide0 = new ModelRenderer(this, 8, 16);
      this.finSide0.addBox(-1.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F);
      this.finSide0.setPos(-2.5F, 22.0F, -2.5F);
      this.finSide0.yRot = (-(float)Math.PI / 4F);
      this.finSide1 = new ModelRenderer(this, 8, 16);
      this.finSide1.addBox(-1.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F);
      this.finSide1.setPos(-2.5F, 22.0F, 2.5F);
      this.finSide1.yRot = ((float)Math.PI / 4F);
      this.finSide2 = new ModelRenderer(this, 4, 16);
      this.finSide2.addBox(0.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F);
      this.finSide2.setPos(2.5F, 22.0F, 2.5F);
      this.finSide2.yRot = (-(float)Math.PI / 4F);
      this.finSide3 = new ModelRenderer(this, 0, 16);
      this.finSide3.addBox(0.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F);
      this.finSide3.setPos(2.5F, 22.0F, -2.5F);
      this.finSide3.yRot = ((float)Math.PI / 4F);
      this.finBottom0 = new ModelRenderer(this, 8, 22);
      this.finBottom0.addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      this.finBottom0.setPos(0.5F, 22.0F, 2.5F);
      this.finBottom0.xRot = ((float)Math.PI / 4F);
      this.finBottom1 = new ModelRenderer(this, 17, 21);
      this.finBottom1.addBox(-2.5F, 0.0F, 0.0F, 5.0F, 1.0F, 1.0F);
      this.finBottom1.setPos(0.0F, 22.0F, -2.5F);
      this.finBottom1.xRot = (-(float)Math.PI / 4F);
   }

   public Iterable<ModelRenderer> parts() {
      return ImmutableList.of(this.cube, this.finBlue0, this.finBlue1, this.finTop0, this.finTop1, this.finSide0, this.finSide1, this.finSide2, this.finSide3, this.finBottom0, this.finBottom1);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      this.finBlue0.zRot = -0.2F + 0.4F * MathHelper.sin(p_225597_4_ * 0.2F);
      this.finBlue1.zRot = 0.2F - 0.4F * MathHelper.sin(p_225597_4_ * 0.2F);
   }
}
