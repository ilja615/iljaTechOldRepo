package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EvokerFangsModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer base = new ModelRenderer(this, 0, 0);
   private final ModelRenderer upperJaw;
   private final ModelRenderer lowerJaw;

   public EvokerFangsModel() {
      this.base.setPos(-5.0F, 22.0F, -5.0F);
      this.base.addBox(0.0F, 0.0F, 0.0F, 10.0F, 12.0F, 10.0F);
      this.upperJaw = new ModelRenderer(this, 40, 0);
      this.upperJaw.setPos(1.5F, 22.0F, -4.0F);
      this.upperJaw.addBox(0.0F, 0.0F, 0.0F, 4.0F, 14.0F, 8.0F);
      this.lowerJaw = new ModelRenderer(this, 40, 0);
      this.lowerJaw.setPos(-1.5F, 22.0F, 4.0F);
      this.lowerJaw.addBox(0.0F, 0.0F, 0.0F, 4.0F, 14.0F, 8.0F);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      float f = p_225597_2_ * 2.0F;
      if (f > 1.0F) {
         f = 1.0F;
      }

      f = 1.0F - f * f * f;
      this.upperJaw.zRot = (float)Math.PI - f * 0.35F * (float)Math.PI;
      this.lowerJaw.zRot = (float)Math.PI + f * 0.35F * (float)Math.PI;
      this.lowerJaw.yRot = (float)Math.PI;
      float f1 = (p_225597_2_ + MathHelper.sin(p_225597_2_ * 2.7F)) * 0.6F * 12.0F;
      this.upperJaw.y = 24.0F - f1;
      this.lowerJaw.y = this.upperJaw.y;
      this.base.y = this.upperJaw.y;
   }

   public Iterable<ModelRenderer> parts() {
      return ImmutableList.of(this.base, this.upperJaw, this.lowerJaw);
   }
}
