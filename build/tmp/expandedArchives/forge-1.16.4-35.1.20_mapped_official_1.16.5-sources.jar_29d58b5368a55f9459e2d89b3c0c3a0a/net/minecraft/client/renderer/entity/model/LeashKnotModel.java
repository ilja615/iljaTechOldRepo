package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LeashKnotModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer knot;

   public LeashKnotModel() {
      this.texWidth = 32;
      this.texHeight = 32;
      this.knot = new ModelRenderer(this, 0, 0);
      this.knot.addBox(-3.0F, -6.0F, -3.0F, 6.0F, 8.0F, 6.0F, 0.0F);
      this.knot.setPos(0.0F, 0.0F, 0.0F);
   }

   public Iterable<ModelRenderer> parts() {
      return ImmutableList.of(this.knot);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      this.knot.yRot = p_225597_5_ * ((float)Math.PI / 180F);
      this.knot.xRot = p_225597_6_ * ((float)Math.PI / 180F);
   }
}
