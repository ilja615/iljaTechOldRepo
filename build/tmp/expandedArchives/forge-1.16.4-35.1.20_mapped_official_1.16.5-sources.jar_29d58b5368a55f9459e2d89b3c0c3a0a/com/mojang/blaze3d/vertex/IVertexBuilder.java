package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryStack;

@OnlyIn(Dist.CLIENT)
public interface IVertexBuilder extends net.minecraftforge.client.extensions.IForgeVertexBuilder {
   Logger LOGGER = LogManager.getLogger();

   IVertexBuilder vertex(double p_225582_1_, double p_225582_3_, double p_225582_5_);

   IVertexBuilder color(int p_225586_1_, int p_225586_2_, int p_225586_3_, int p_225586_4_);

   IVertexBuilder uv(float p_225583_1_, float p_225583_2_);

   IVertexBuilder overlayCoords(int p_225585_1_, int p_225585_2_);

   IVertexBuilder uv2(int p_225587_1_, int p_225587_2_);

   IVertexBuilder normal(float p_225584_1_, float p_225584_2_, float p_225584_3_);

   void endVertex();

   default void vertex(float p_225588_1_, float p_225588_2_, float p_225588_3_, float p_225588_4_, float p_225588_5_, float p_225588_6_, float p_225588_7_, float p_225588_8_, float p_225588_9_, int p_225588_10_, int p_225588_11_, float p_225588_12_, float p_225588_13_, float p_225588_14_) {
      this.vertex((double)p_225588_1_, (double)p_225588_2_, (double)p_225588_3_);
      this.color(p_225588_4_, p_225588_5_, p_225588_6_, p_225588_7_);
      this.uv(p_225588_8_, p_225588_9_);
      this.overlayCoords(p_225588_10_);
      this.uv2(p_225588_11_);
      this.normal(p_225588_12_, p_225588_13_, p_225588_14_);
      this.endVertex();
   }

   default IVertexBuilder color(float p_227885_1_, float p_227885_2_, float p_227885_3_, float p_227885_4_) {
      return this.color((int)(p_227885_1_ * 255.0F), (int)(p_227885_2_ * 255.0F), (int)(p_227885_3_ * 255.0F), (int)(p_227885_4_ * 255.0F));
   }

   default IVertexBuilder uv2(int p_227886_1_) {
      return this.uv2(p_227886_1_ & '\uffff', p_227886_1_ >> 16 & '\uffff');
   }

   default IVertexBuilder overlayCoords(int p_227891_1_) {
      return this.overlayCoords(p_227891_1_ & '\uffff', p_227891_1_ >> 16 & '\uffff');
   }

   default void putBulkData(MatrixStack.Entry p_227889_1_, BakedQuad p_227889_2_, float p_227889_3_, float p_227889_4_, float p_227889_5_, int p_227889_6_, int p_227889_7_) {
      this.putBulkData(p_227889_1_, p_227889_2_, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, p_227889_3_, p_227889_4_, p_227889_5_, new int[]{p_227889_6_, p_227889_6_, p_227889_6_, p_227889_6_}, p_227889_7_, false);
   }

   default void putBulkData(MatrixStack.Entry p_227890_1_, BakedQuad p_227890_2_, float[] p_227890_3_, float p_227890_4_, float p_227890_5_, float p_227890_6_, int[] p_227890_7_, int p_227890_8_, boolean p_227890_9_) {
      int[] aint = p_227890_2_.getVertices();
      Vector3i vector3i = p_227890_2_.getDirection().getNormal();
      Vector3f vector3f = new Vector3f((float)vector3i.getX(), (float)vector3i.getY(), (float)vector3i.getZ());
      Matrix4f matrix4f = p_227890_1_.pose();
      vector3f.transform(p_227890_1_.normal());
      int i = 8;
      int j = aint.length / 8;

      try (MemoryStack memorystack = MemoryStack.stackPush()) {
         ByteBuffer bytebuffer = memorystack.malloc(DefaultVertexFormats.BLOCK.getVertexSize());
         IntBuffer intbuffer = bytebuffer.asIntBuffer();

         for(int k = 0; k < j; ++k) {
            ((Buffer)intbuffer).clear();
            intbuffer.put(aint, k * 8, 8);
            float f = bytebuffer.getFloat(0);
            float f1 = bytebuffer.getFloat(4);
            float f2 = bytebuffer.getFloat(8);
            float f3;
            float f4;
            float f5;
            if (p_227890_9_) {
               float f6 = (float)(bytebuffer.get(12) & 255) / 255.0F;
               float f7 = (float)(bytebuffer.get(13) & 255) / 255.0F;
               float f8 = (float)(bytebuffer.get(14) & 255) / 255.0F;
               f3 = f6 * p_227890_3_[k] * p_227890_4_;
               f4 = f7 * p_227890_3_[k] * p_227890_5_;
               f5 = f8 * p_227890_3_[k] * p_227890_6_;
            } else {
               f3 = p_227890_3_[k] * p_227890_4_;
               f4 = p_227890_3_[k] * p_227890_5_;
               f5 = p_227890_3_[k] * p_227890_6_;
            }

            int l = applyBakedLighting(p_227890_7_[k], bytebuffer);
            float f9 = bytebuffer.getFloat(16);
            float f10 = bytebuffer.getFloat(20);
            Vector4f vector4f = new Vector4f(f, f1, f2, 1.0F);
            vector4f.transform(matrix4f);
            applyBakedNormals(vector3f, bytebuffer, p_227890_1_.normal());
            this.vertex(vector4f.x(), vector4f.y(), vector4f.z(), f3, f4, f5, 1.0F, f9, f10, p_227890_8_, l, vector3f.x(), vector3f.y(), vector3f.z());
         }
      }

   }

   default IVertexBuilder vertex(Matrix4f p_227888_1_, float p_227888_2_, float p_227888_3_, float p_227888_4_) {
      Vector4f vector4f = new Vector4f(p_227888_2_, p_227888_3_, p_227888_4_, 1.0F);
      vector4f.transform(p_227888_1_);
      return this.vertex((double)vector4f.x(), (double)vector4f.y(), (double)vector4f.z());
   }

   default IVertexBuilder normal(Matrix3f p_227887_1_, float p_227887_2_, float p_227887_3_, float p_227887_4_) {
      Vector3f vector3f = new Vector3f(p_227887_2_, p_227887_3_, p_227887_4_);
      vector3f.transform(p_227887_1_);
      return this.normal(vector3f.x(), vector3f.y(), vector3f.z());
   }
}
