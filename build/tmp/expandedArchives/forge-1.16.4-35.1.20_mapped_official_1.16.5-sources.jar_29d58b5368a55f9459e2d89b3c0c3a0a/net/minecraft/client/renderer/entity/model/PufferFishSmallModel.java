package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PufferFishSmallModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer cube;
   private final ModelRenderer eye0;
   private final ModelRenderer eye1;
   private final ModelRenderer fin0;
   private final ModelRenderer fin1;
   private final ModelRenderer finBack;

   public PufferFishSmallModel() {
      this.texWidth = 32;
      this.texHeight = 32;
      int i = 23;
      this.cube = new ModelRenderer(this, 0, 27);
      this.cube.addBox(-1.5F, -2.0F, -1.5F, 3.0F, 2.0F, 3.0F);
      this.cube.setPos(0.0F, 23.0F, 0.0F);
      this.eye0 = new ModelRenderer(this, 24, 6);
      this.eye0.addBox(-1.5F, 0.0F, -1.5F, 1.0F, 1.0F, 1.0F);
      this.eye0.setPos(0.0F, 20.0F, 0.0F);
      this.eye1 = new ModelRenderer(this, 28, 6);
      this.eye1.addBox(0.5F, 0.0F, -1.5F, 1.0F, 1.0F, 1.0F);
      this.eye1.setPos(0.0F, 20.0F, 0.0F);
      this.finBack = new ModelRenderer(this, -3, 0);
      this.finBack.addBox(-1.5F, 0.0F, 0.0F, 3.0F, 0.0F, 3.0F);
      this.finBack.setPos(0.0F, 22.0F, 1.5F);
      this.fin0 = new ModelRenderer(this, 25, 0);
      this.fin0.addBox(-1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 2.0F);
      this.fin0.setPos(-1.5F, 22.0F, -1.5F);
      this.fin1 = new ModelRenderer(this, 25, 0);
      this.fin1.addBox(0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 2.0F);
      this.fin1.setPos(1.5F, 22.0F, -1.5F);
   }

   public Iterable<ModelRenderer> parts() {
      return ImmutableList.of(this.cube, this.eye0, this.eye1, this.finBack, this.fin0, this.fin1);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      this.fin0.zRot = -0.2F + 0.4F * MathHelper.sin(p_225597_4_ * 0.2F);
      this.fin1.zRot = 0.2F - 0.4F * MathHelper.sin(p_225597_4_ * 0.2F);
   }
}
