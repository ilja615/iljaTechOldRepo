package net.minecraft.client.renderer.entity.model;

import java.util.Arrays;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MinecartModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer[] cubes = new ModelRenderer[6];

   public MinecartModel() {
      this.cubes[0] = new ModelRenderer(this, 0, 10);
      this.cubes[1] = new ModelRenderer(this, 0, 0);
      this.cubes[2] = new ModelRenderer(this, 0, 0);
      this.cubes[3] = new ModelRenderer(this, 0, 0);
      this.cubes[4] = new ModelRenderer(this, 0, 0);
      this.cubes[5] = new ModelRenderer(this, 44, 10);
      int i = 20;
      int j = 8;
      int k = 16;
      int l = 4;
      this.cubes[0].addBox(-10.0F, -8.0F, -1.0F, 20.0F, 16.0F, 2.0F, 0.0F);
      this.cubes[0].setPos(0.0F, 4.0F, 0.0F);
      this.cubes[5].addBox(-9.0F, -7.0F, -1.0F, 18.0F, 14.0F, 1.0F, 0.0F);
      this.cubes[5].setPos(0.0F, 4.0F, 0.0F);
      this.cubes[1].addBox(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F, 0.0F);
      this.cubes[1].setPos(-9.0F, 4.0F, 0.0F);
      this.cubes[2].addBox(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F, 0.0F);
      this.cubes[2].setPos(9.0F, 4.0F, 0.0F);
      this.cubes[3].addBox(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F, 0.0F);
      this.cubes[3].setPos(0.0F, 4.0F, -7.0F);
      this.cubes[4].addBox(-8.0F, -9.0F, -1.0F, 16.0F, 8.0F, 2.0F, 0.0F);
      this.cubes[4].setPos(0.0F, 4.0F, 7.0F);
      this.cubes[0].xRot = ((float)Math.PI / 2F);
      this.cubes[1].yRot = ((float)Math.PI * 1.5F);
      this.cubes[2].yRot = ((float)Math.PI / 2F);
      this.cubes[3].yRot = (float)Math.PI;
      this.cubes[5].xRot = (-(float)Math.PI / 2F);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      this.cubes[5].y = 4.0F - p_225597_4_;
   }

   public Iterable<ModelRenderer> parts() {
      return Arrays.asList(this.cubes);
   }
}
