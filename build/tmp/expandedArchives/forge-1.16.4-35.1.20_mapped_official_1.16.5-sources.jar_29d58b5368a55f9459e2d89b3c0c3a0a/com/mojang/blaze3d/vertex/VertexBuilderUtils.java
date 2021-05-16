package com.mojang.blaze3d.vertex;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VertexBuilderUtils {
   public static IVertexBuilder create(IVertexBuilder p_227915_0_, IVertexBuilder p_227915_1_) {
      return new VertexBuilderUtils.DelegatingVertexBuilder(p_227915_0_, p_227915_1_);
   }

   @OnlyIn(Dist.CLIENT)
   static class DelegatingVertexBuilder implements IVertexBuilder {
      private final IVertexBuilder first;
      private final IVertexBuilder second;

      public DelegatingVertexBuilder(IVertexBuilder p_i225913_1_, IVertexBuilder p_i225913_2_) {
         if (p_i225913_1_ == p_i225913_2_) {
            throw new IllegalArgumentException("Duplicate delegates");
         } else {
            this.first = p_i225913_1_;
            this.second = p_i225913_2_;
         }
      }

      public IVertexBuilder vertex(double p_225582_1_, double p_225582_3_, double p_225582_5_) {
         this.first.vertex(p_225582_1_, p_225582_3_, p_225582_5_);
         this.second.vertex(p_225582_1_, p_225582_3_, p_225582_5_);
         return this;
      }

      public IVertexBuilder color(int p_225586_1_, int p_225586_2_, int p_225586_3_, int p_225586_4_) {
         this.first.color(p_225586_1_, p_225586_2_, p_225586_3_, p_225586_4_);
         this.second.color(p_225586_1_, p_225586_2_, p_225586_3_, p_225586_4_);
         return this;
      }

      public IVertexBuilder uv(float p_225583_1_, float p_225583_2_) {
         this.first.uv(p_225583_1_, p_225583_2_);
         this.second.uv(p_225583_1_, p_225583_2_);
         return this;
      }

      public IVertexBuilder overlayCoords(int p_225585_1_, int p_225585_2_) {
         this.first.overlayCoords(p_225585_1_, p_225585_2_);
         this.second.overlayCoords(p_225585_1_, p_225585_2_);
         return this;
      }

      public IVertexBuilder uv2(int p_225587_1_, int p_225587_2_) {
         this.first.uv2(p_225587_1_, p_225587_2_);
         this.second.uv2(p_225587_1_, p_225587_2_);
         return this;
      }

      public IVertexBuilder normal(float p_225584_1_, float p_225584_2_, float p_225584_3_) {
         this.first.normal(p_225584_1_, p_225584_2_, p_225584_3_);
         this.second.normal(p_225584_1_, p_225584_2_, p_225584_3_);
         return this;
      }

      public void vertex(float p_225588_1_, float p_225588_2_, float p_225588_3_, float p_225588_4_, float p_225588_5_, float p_225588_6_, float p_225588_7_, float p_225588_8_, float p_225588_9_, int p_225588_10_, int p_225588_11_, float p_225588_12_, float p_225588_13_, float p_225588_14_) {
         this.first.vertex(p_225588_1_, p_225588_2_, p_225588_3_, p_225588_4_, p_225588_5_, p_225588_6_, p_225588_7_, p_225588_8_, p_225588_9_, p_225588_10_, p_225588_11_, p_225588_12_, p_225588_13_, p_225588_14_);
         this.second.vertex(p_225588_1_, p_225588_2_, p_225588_3_, p_225588_4_, p_225588_5_, p_225588_6_, p_225588_7_, p_225588_8_, p_225588_9_, p_225588_10_, p_225588_11_, p_225588_12_, p_225588_13_, p_225588_14_);
      }

      public void endVertex() {
         this.first.endVertex();
         this.second.endVertex();
      }
   }
}
