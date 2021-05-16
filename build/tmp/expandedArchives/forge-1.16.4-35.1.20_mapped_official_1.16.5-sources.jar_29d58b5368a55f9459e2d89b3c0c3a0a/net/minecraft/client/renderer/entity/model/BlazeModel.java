package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlazeModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer[] upperBodyParts;
   private final ModelRenderer head = new ModelRenderer(this, 0, 0);
   private final ImmutableList<ModelRenderer> parts;

   public BlazeModel() {
      this.head.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
      this.upperBodyParts = new ModelRenderer[12];

      for(int i = 0; i < this.upperBodyParts.length; ++i) {
         this.upperBodyParts[i] = new ModelRenderer(this, 0, 16);
         this.upperBodyParts[i].addBox(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F);
      }

      Builder<ModelRenderer> builder = ImmutableList.builder();
      builder.add(this.head);
      builder.addAll(Arrays.asList(this.upperBodyParts));
      this.parts = builder.build();
   }

   public Iterable<ModelRenderer> parts() {
      return this.parts;
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      float f = p_225597_4_ * (float)Math.PI * -0.1F;

      for(int i = 0; i < 4; ++i) {
         this.upperBodyParts[i].y = -2.0F + MathHelper.cos(((float)(i * 2) + p_225597_4_) * 0.25F);
         this.upperBodyParts[i].x = MathHelper.cos(f) * 9.0F;
         this.upperBodyParts[i].z = MathHelper.sin(f) * 9.0F;
         ++f;
      }

      f = ((float)Math.PI / 4F) + p_225597_4_ * (float)Math.PI * 0.03F;

      for(int j = 4; j < 8; ++j) {
         this.upperBodyParts[j].y = 2.0F + MathHelper.cos(((float)(j * 2) + p_225597_4_) * 0.25F);
         this.upperBodyParts[j].x = MathHelper.cos(f) * 7.0F;
         this.upperBodyParts[j].z = MathHelper.sin(f) * 7.0F;
         ++f;
      }

      f = 0.47123894F + p_225597_4_ * (float)Math.PI * -0.05F;

      for(int k = 8; k < 12; ++k) {
         this.upperBodyParts[k].y = 11.0F + MathHelper.cos(((float)k * 1.5F + p_225597_4_) * 0.5F);
         this.upperBodyParts[k].x = MathHelper.cos(f) * 5.0F;
         this.upperBodyParts[k].z = MathHelper.sin(f) * 5.0F;
         ++f;
      }

      this.head.yRot = p_225597_5_ * ((float)Math.PI / 180F);
      this.head.xRot = p_225597_6_ * ((float)Math.PI / 180F);
   }
}
