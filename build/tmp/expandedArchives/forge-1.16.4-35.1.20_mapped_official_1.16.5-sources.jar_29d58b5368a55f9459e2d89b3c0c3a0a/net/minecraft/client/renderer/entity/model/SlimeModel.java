package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SlimeModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer cube;
   private final ModelRenderer eye0;
   private final ModelRenderer eye1;
   private final ModelRenderer mouth;

   public SlimeModel(int p_i1157_1_) {
      this.cube = new ModelRenderer(this, 0, p_i1157_1_);
      this.eye0 = new ModelRenderer(this, 32, 0);
      this.eye1 = new ModelRenderer(this, 32, 4);
      this.mouth = new ModelRenderer(this, 32, 8);
      if (p_i1157_1_ > 0) {
         this.cube.addBox(-3.0F, 17.0F, -3.0F, 6.0F, 6.0F, 6.0F);
         this.eye0.addBox(-3.25F, 18.0F, -3.5F, 2.0F, 2.0F, 2.0F);
         this.eye1.addBox(1.25F, 18.0F, -3.5F, 2.0F, 2.0F, 2.0F);
         this.mouth.addBox(0.0F, 21.0F, -3.5F, 1.0F, 1.0F, 1.0F);
      } else {
         this.cube.addBox(-4.0F, 16.0F, -4.0F, 8.0F, 8.0F, 8.0F);
      }

   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
   }

   public Iterable<ModelRenderer> parts() {
      return ImmutableList.of(this.cube, this.eye0, this.eye1, this.mouth);
   }
}
