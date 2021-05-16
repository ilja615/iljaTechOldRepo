package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Random;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GhastModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer[] tentacles = new ModelRenderer[9];
   private final ImmutableList<ModelRenderer> parts;

   public GhastModel() {
      Builder<ModelRenderer> builder = ImmutableList.builder();
      ModelRenderer modelrenderer = new ModelRenderer(this, 0, 0);
      modelrenderer.addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F);
      modelrenderer.y = 17.6F;
      builder.add(modelrenderer);
      Random random = new Random(1660L);

      for(int i = 0; i < this.tentacles.length; ++i) {
         this.tentacles[i] = new ModelRenderer(this, 0, 0);
         float f = (((float)(i % 3) - (float)(i / 3 % 2) * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
         float f1 = ((float)(i / 3) / 2.0F * 2.0F - 1.0F) * 5.0F;
         int j = random.nextInt(7) + 8;
         this.tentacles[i].addBox(-1.0F, 0.0F, -1.0F, 2.0F, (float)j, 2.0F);
         this.tentacles[i].x = f;
         this.tentacles[i].z = f1;
         this.tentacles[i].y = 24.6F;
         builder.add(this.tentacles[i]);
      }

      this.parts = builder.build();
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      for(int i = 0; i < this.tentacles.length; ++i) {
         this.tentacles[i].xRot = 0.2F * MathHelper.sin(p_225597_4_ * 0.3F + (float)i) + 0.4F;
      }

   }

   public Iterable<ModelRenderer> parts() {
      return this.parts;
   }
}
