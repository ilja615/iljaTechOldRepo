package com.mojang.blaze3d.vertex;

import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IVertexConsumer extends IVertexBuilder {
   VertexFormatElement currentElement();

   void nextElement();

   void putByte(int p_225589_1_, byte p_225589_2_);

   void putShort(int p_225591_1_, short p_225591_2_);

   void putFloat(int p_225590_1_, float p_225590_2_);

   default IVertexBuilder vertex(double p_225582_1_, double p_225582_3_, double p_225582_5_) {
      if (this.currentElement().getType() != VertexFormatElement.Type.FLOAT) {
         throw new IllegalStateException();
      } else {
         this.putFloat(0, (float)p_225582_1_);
         this.putFloat(4, (float)p_225582_3_);
         this.putFloat(8, (float)p_225582_5_);
         this.nextElement();
         return this;
      }
   }

   default IVertexBuilder color(int p_225586_1_, int p_225586_2_, int p_225586_3_, int p_225586_4_) {
      VertexFormatElement vertexformatelement = this.currentElement();
      if (vertexformatelement.getUsage() != VertexFormatElement.Usage.COLOR) {
         return this;
      } else if (vertexformatelement.getType() != VertexFormatElement.Type.UBYTE) {
         throw new IllegalStateException();
      } else {
         this.putByte(0, (byte)p_225586_1_);
         this.putByte(1, (byte)p_225586_2_);
         this.putByte(2, (byte)p_225586_3_);
         this.putByte(3, (byte)p_225586_4_);
         this.nextElement();
         return this;
      }
   }

   default IVertexBuilder uv(float p_225583_1_, float p_225583_2_) {
      VertexFormatElement vertexformatelement = this.currentElement();
      if (vertexformatelement.getUsage() == VertexFormatElement.Usage.UV && vertexformatelement.getIndex() == 0) {
         if (vertexformatelement.getType() != VertexFormatElement.Type.FLOAT) {
            throw new IllegalStateException();
         } else {
            this.putFloat(0, p_225583_1_);
            this.putFloat(4, p_225583_2_);
            this.nextElement();
            return this;
         }
      } else {
         return this;
      }
   }

   default IVertexBuilder overlayCoords(int p_225585_1_, int p_225585_2_) {
      return this.uvShort((short)p_225585_1_, (short)p_225585_2_, 1);
   }

   default IVertexBuilder uv2(int p_225587_1_, int p_225587_2_) {
      return this.uvShort((short)p_225587_1_, (short)p_225587_2_, 2);
   }

   default IVertexBuilder uvShort(short p_227847_1_, short p_227847_2_, int p_227847_3_) {
      VertexFormatElement vertexformatelement = this.currentElement();
      if (vertexformatelement.getUsage() == VertexFormatElement.Usage.UV && vertexformatelement.getIndex() == p_227847_3_) {
         if (vertexformatelement.getType() != VertexFormatElement.Type.SHORT) {
            throw new IllegalStateException();
         } else {
            this.putShort(0, p_227847_1_);
            this.putShort(2, p_227847_2_);
            this.nextElement();
            return this;
         }
      } else {
         return this;
      }
   }

   default IVertexBuilder normal(float p_225584_1_, float p_225584_2_, float p_225584_3_) {
      VertexFormatElement vertexformatelement = this.currentElement();
      if (vertexformatelement.getUsage() != VertexFormatElement.Usage.NORMAL) {
         return this;
      } else if (vertexformatelement.getType() != VertexFormatElement.Type.BYTE) {
         throw new IllegalStateException();
      } else {
         this.putByte(0, normalIntValue(p_225584_1_));
         this.putByte(1, normalIntValue(p_225584_2_));
         this.putByte(2, normalIntValue(p_225584_3_));
         this.nextElement();
         return this;
      }
   }

   static byte normalIntValue(float p_227846_0_) {
      return (byte)((int)(MathHelper.clamp(p_227846_0_, -1.0F, 1.0F) * 127.0F) & 255);
   }
}
