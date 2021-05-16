package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.DefaultColorVertexBuilder;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import java.util.Optional;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OutlineLayerBuffer implements IRenderTypeBuffer {
   private final IRenderTypeBuffer.Impl bufferSource;
   private final IRenderTypeBuffer.Impl outlineBufferSource = IRenderTypeBuffer.immediate(new BufferBuilder(256));
   private int teamR = 255;
   private int teamG = 255;
   private int teamB = 255;
   private int teamA = 255;

   public OutlineLayerBuffer(IRenderTypeBuffer.Impl p_i225970_1_) {
      this.bufferSource = p_i225970_1_;
   }

   public IVertexBuilder getBuffer(RenderType p_getBuffer_1_) {
      if (p_getBuffer_1_.isOutline()) {
         IVertexBuilder ivertexbuilder2 = this.outlineBufferSource.getBuffer(p_getBuffer_1_);
         return new OutlineLayerBuffer.ColoredOutline(ivertexbuilder2, this.teamR, this.teamG, this.teamB, this.teamA);
      } else {
         IVertexBuilder ivertexbuilder = this.bufferSource.getBuffer(p_getBuffer_1_);
         Optional<RenderType> optional = p_getBuffer_1_.outline();
         if (optional.isPresent()) {
            IVertexBuilder ivertexbuilder1 = this.outlineBufferSource.getBuffer(optional.get());
            OutlineLayerBuffer.ColoredOutline outlinelayerbuffer$coloredoutline = new OutlineLayerBuffer.ColoredOutline(ivertexbuilder1, this.teamR, this.teamG, this.teamB, this.teamA);
            return VertexBuilderUtils.create(outlinelayerbuffer$coloredoutline, ivertexbuilder);
         } else {
            return ivertexbuilder;
         }
      }
   }

   public void setColor(int p_228472_1_, int p_228472_2_, int p_228472_3_, int p_228472_4_) {
      this.teamR = p_228472_1_;
      this.teamG = p_228472_2_;
      this.teamB = p_228472_3_;
      this.teamA = p_228472_4_;
   }

   public void endOutlineBatch() {
      this.outlineBufferSource.endBatch();
   }

   @OnlyIn(Dist.CLIENT)
   static class ColoredOutline extends DefaultColorVertexBuilder {
      private final IVertexBuilder delegate;
      private double x;
      private double y;
      private double z;
      private float u;
      private float v;

      private ColoredOutline(IVertexBuilder p_i225971_1_, int p_i225971_2_, int p_i225971_3_, int p_i225971_4_, int p_i225971_5_) {
         this.delegate = p_i225971_1_;
         super.defaultColor(p_i225971_2_, p_i225971_3_, p_i225971_4_, p_i225971_5_);
      }

      public void defaultColor(int p_225611_1_, int p_225611_2_, int p_225611_3_, int p_225611_4_) {
      }

      public IVertexBuilder vertex(double p_225582_1_, double p_225582_3_, double p_225582_5_) {
         this.x = p_225582_1_;
         this.y = p_225582_3_;
         this.z = p_225582_5_;
         return this;
      }

      public IVertexBuilder color(int p_225586_1_, int p_225586_2_, int p_225586_3_, int p_225586_4_) {
         return this;
      }

      public IVertexBuilder uv(float p_225583_1_, float p_225583_2_) {
         this.u = p_225583_1_;
         this.v = p_225583_2_;
         return this;
      }

      public IVertexBuilder overlayCoords(int p_225585_1_, int p_225585_2_) {
         return this;
      }

      public IVertexBuilder uv2(int p_225587_1_, int p_225587_2_) {
         return this;
      }

      public IVertexBuilder normal(float p_225584_1_, float p_225584_2_, float p_225584_3_) {
         return this;
      }

      public void vertex(float p_225588_1_, float p_225588_2_, float p_225588_3_, float p_225588_4_, float p_225588_5_, float p_225588_6_, float p_225588_7_, float p_225588_8_, float p_225588_9_, int p_225588_10_, int p_225588_11_, float p_225588_12_, float p_225588_13_, float p_225588_14_) {
         this.delegate.vertex((double)p_225588_1_, (double)p_225588_2_, (double)p_225588_3_).color(this.defaultR, this.defaultG, this.defaultB, this.defaultA).uv(p_225588_8_, p_225588_9_).endVertex();
      }

      public void endVertex() {
         this.delegate.vertex(this.x, this.y, this.z).color(this.defaultR, this.defaultG, this.defaultB, this.defaultA).uv(this.u, this.v).endVertex();
      }
   }
}
