package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SquidModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer body;
   private final ModelRenderer[] tentacles = new ModelRenderer[8];
   private final ImmutableList<ModelRenderer> parts;

   public SquidModel() {
      int i = -16;
      this.body = new ModelRenderer(this, 0, 0);
      this.body.addBox(-6.0F, -8.0F, -6.0F, 12.0F, 16.0F, 12.0F);
      this.body.y += 8.0F;

      for(int j = 0; j < this.tentacles.length; ++j) {
         this.tentacles[j] = new ModelRenderer(this, 48, 0);
         double d0 = (double)j * Math.PI * 2.0D / (double)this.tentacles.length;
         float f = (float)Math.cos(d0) * 5.0F;
         float f1 = (float)Math.sin(d0) * 5.0F;
         this.tentacles[j].addBox(-1.0F, 0.0F, -1.0F, 2.0F, 18.0F, 2.0F);
         this.tentacles[j].x = f;
         this.tentacles[j].z = f1;
         this.tentacles[j].y = 15.0F;
         d0 = (double)j * Math.PI * -2.0D / (double)this.tentacles.length + (Math.PI / 2D);
         this.tentacles[j].yRot = (float)d0;
      }

      Builder<ModelRenderer> builder = ImmutableList.builder();
      builder.add(this.body);
      builder.addAll(Arrays.asList(this.tentacles));
      this.parts = builder.build();
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      for(ModelRenderer modelrenderer : this.tentacles) {
         modelrenderer.xRot = p_225597_4_;
      }

   }

   public Iterable<ModelRenderer> parts() {
      return this.parts;
   }
}
