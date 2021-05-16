package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TropicalFishBModel<T extends Entity> extends AbstractTropicalFishModel<T> {
   private final ModelRenderer body;
   private final ModelRenderer tail;
   private final ModelRenderer leftFin;
   private final ModelRenderer rightFin;
   private final ModelRenderer topFin;
   private final ModelRenderer bottomFin;

   public TropicalFishBModel(float p_i48891_1_) {
      this.texWidth = 32;
      this.texHeight = 32;
      int i = 19;
      this.body = new ModelRenderer(this, 0, 20);
      this.body.addBox(-1.0F, -3.0F, -3.0F, 2.0F, 6.0F, 6.0F, p_i48891_1_);
      this.body.setPos(0.0F, 19.0F, 0.0F);
      this.tail = new ModelRenderer(this, 21, 16);
      this.tail.addBox(0.0F, -3.0F, 0.0F, 0.0F, 6.0F, 5.0F, p_i48891_1_);
      this.tail.setPos(0.0F, 19.0F, 3.0F);
      this.leftFin = new ModelRenderer(this, 2, 16);
      this.leftFin.addBox(-2.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.0F, p_i48891_1_);
      this.leftFin.setPos(-1.0F, 20.0F, 0.0F);
      this.leftFin.yRot = ((float)Math.PI / 4F);
      this.rightFin = new ModelRenderer(this, 2, 12);
      this.rightFin.addBox(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.0F, p_i48891_1_);
      this.rightFin.setPos(1.0F, 20.0F, 0.0F);
      this.rightFin.yRot = (-(float)Math.PI / 4F);
      this.topFin = new ModelRenderer(this, 20, 11);
      this.topFin.addBox(0.0F, -4.0F, 0.0F, 0.0F, 4.0F, 6.0F, p_i48891_1_);
      this.topFin.setPos(0.0F, 16.0F, -3.0F);
      this.bottomFin = new ModelRenderer(this, 20, 21);
      this.bottomFin.addBox(0.0F, 0.0F, 0.0F, 0.0F, 4.0F, 6.0F, p_i48891_1_);
      this.bottomFin.setPos(0.0F, 22.0F, -3.0F);
   }

   public Iterable<ModelRenderer> parts() {
      return ImmutableList.of(this.body, this.tail, this.leftFin, this.rightFin, this.topFin, this.bottomFin);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      float f = 1.0F;
      if (!p_225597_1_.isInWater()) {
         f = 1.5F;
      }

      this.tail.yRot = -f * 0.45F * MathHelper.sin(0.6F * p_225597_4_);
   }
}
