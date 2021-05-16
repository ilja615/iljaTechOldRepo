package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CodModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer body;
   private final ModelRenderer topFin;
   private final ModelRenderer head;
   private final ModelRenderer nose;
   private final ModelRenderer sideFin0;
   private final ModelRenderer sideFin1;
   private final ModelRenderer tailFin;

   public CodModel() {
      this.texWidth = 32;
      this.texHeight = 32;
      int i = 22;
      this.body = new ModelRenderer(this, 0, 0);
      this.body.addBox(-1.0F, -2.0F, 0.0F, 2.0F, 4.0F, 7.0F);
      this.body.setPos(0.0F, 22.0F, 0.0F);
      this.head = new ModelRenderer(this, 11, 0);
      this.head.addBox(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 3.0F);
      this.head.setPos(0.0F, 22.0F, 0.0F);
      this.nose = new ModelRenderer(this, 0, 0);
      this.nose.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 3.0F, 1.0F);
      this.nose.setPos(0.0F, 22.0F, -3.0F);
      this.sideFin0 = new ModelRenderer(this, 22, 1);
      this.sideFin0.addBox(-2.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F);
      this.sideFin0.setPos(-1.0F, 23.0F, 0.0F);
      this.sideFin0.zRot = (-(float)Math.PI / 4F);
      this.sideFin1 = new ModelRenderer(this, 22, 4);
      this.sideFin1.addBox(0.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F);
      this.sideFin1.setPos(1.0F, 23.0F, 0.0F);
      this.sideFin1.zRot = ((float)Math.PI / 4F);
      this.tailFin = new ModelRenderer(this, 22, 3);
      this.tailFin.addBox(0.0F, -2.0F, 0.0F, 0.0F, 4.0F, 4.0F);
      this.tailFin.setPos(0.0F, 22.0F, 7.0F);
      this.topFin = new ModelRenderer(this, 20, -6);
      this.topFin.addBox(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 6.0F);
      this.topFin.setPos(0.0F, 20.0F, 0.0F);
   }

   public Iterable<ModelRenderer> parts() {
      return ImmutableList.of(this.body, this.head, this.nose, this.sideFin0, this.sideFin1, this.tailFin, this.topFin);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      float f = 1.0F;
      if (!p_225597_1_.isInWater()) {
         f = 1.5F;
      }

      this.tailFin.yRot = -f * 0.45F * MathHelper.sin(0.6F * p_225597_4_);
   }
}
