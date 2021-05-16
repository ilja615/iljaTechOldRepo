package net.minecraft.client.renderer.vertex;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.function.IntConsumer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class VertexFormatElement {
   private static final Logger LOGGER = LogManager.getLogger();
   private final VertexFormatElement.Type type;
   private final VertexFormatElement.Usage usage;
   private final int index;
   private final int count;
   private final int byteSize;

   public VertexFormatElement(int p_i46096_1_, VertexFormatElement.Type p_i46096_2_, VertexFormatElement.Usage p_i46096_3_, int p_i46096_4_) {
      if (this.supportsUsage(p_i46096_1_, p_i46096_3_)) {
         this.usage = p_i46096_3_;
      } else {
         LOGGER.warn("Multiple vertex elements of the same type other than UVs are not supported. Forcing type to UV.");
         this.usage = VertexFormatElement.Usage.UV;
      }

      this.type = p_i46096_2_;
      this.index = p_i46096_1_;
      this.count = p_i46096_4_;
      this.byteSize = p_i46096_2_.getSize() * this.count;
   }

   private boolean supportsUsage(int p_177372_1_, VertexFormatElement.Usage p_177372_2_) {
      return p_177372_1_ == 0 || p_177372_2_ == VertexFormatElement.Usage.UV;
   }

   public final VertexFormatElement.Type getType() {
      return this.type;
   }

   public final VertexFormatElement.Usage getUsage() {
      return this.usage;
   }

   public final int getIndex() {
      return this.index;
   }

   public String toString() {
      return this.count + "," + this.usage.getName() + "," + this.type.getName();
   }

   public final int getByteSize() {
      return this.byteSize;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         VertexFormatElement vertexformatelement = (VertexFormatElement)p_equals_1_;
         if (this.count != vertexformatelement.count) {
            return false;
         } else if (this.index != vertexformatelement.index) {
            return false;
         } else if (this.type != vertexformatelement.type) {
            return false;
         } else {
            return this.usage == vertexformatelement.usage;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int i = this.type.hashCode();
      i = 31 * i + this.usage.hashCode();
      i = 31 * i + this.index;
      return 31 * i + this.count;
   }

   public void setupBufferState(long p_227897_1_, int p_227897_3_) {
      this.usage.setupBufferState(this.count, this.type.getGlType(), p_227897_3_, p_227897_1_, this.index);
   }

   public void clearBufferState() {
      this.usage.clearBufferState(this.index);
   }

    //Forge Start
    public int getElementCount() {
       return count;
    }
    //Forge End

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      FLOAT(4, "Float", 5126),
      UBYTE(1, "Unsigned Byte", 5121),
      BYTE(1, "Byte", 5120),
      USHORT(2, "Unsigned Short", 5123),
      SHORT(2, "Short", 5122),
      UINT(4, "Unsigned Int", 5125),
      INT(4, "Int", 5124);

      private final int size;
      private final String name;
      private final int glType;

      private Type(int p_i46095_3_, String p_i46095_4_, int p_i46095_5_) {
         this.size = p_i46095_3_;
         this.name = p_i46095_4_;
         this.glType = p_i46095_5_;
      }

      public int getSize() {
         return this.size;
      }

      public String getName() {
         return this.name;
      }

      public int getGlType() {
         return this.glType;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Usage {
      POSITION("Position", (p_227914_0_, p_227914_1_, p_227914_2_, p_227914_3_, p_227914_5_) -> {
         GlStateManager._vertexPointer(p_227914_0_, p_227914_1_, p_227914_2_, p_227914_3_);
         GlStateManager._enableClientState(32884);
      }, (p_227912_0_) -> {
         GlStateManager._disableClientState(32884);
      }),
      NORMAL("Normal", (p_227913_0_, p_227913_1_, p_227913_2_, p_227913_3_, p_227913_5_) -> {
         GlStateManager._normalPointer(p_227913_1_, p_227913_2_, p_227913_3_);
         GlStateManager._enableClientState(32885);
      }, (p_227910_0_) -> {
         GlStateManager._disableClientState(32885);
      }),
      COLOR("Vertex Color", (p_227911_0_, p_227911_1_, p_227911_2_, p_227911_3_, p_227911_5_) -> {
         GlStateManager._colorPointer(p_227911_0_, p_227911_1_, p_227911_2_, p_227911_3_);
         GlStateManager._enableClientState(32886);
      }, (p_227908_0_) -> {
         GlStateManager._disableClientState(32886);
         GlStateManager._clearCurrentColor();
      }),
      UV("UV", (p_227909_0_, p_227909_1_, p_227909_2_, p_227909_3_, p_227909_5_) -> {
         GlStateManager._glClientActiveTexture('\u84c0' + p_227909_5_);
         GlStateManager._texCoordPointer(p_227909_0_, p_227909_1_, p_227909_2_, p_227909_3_);
         GlStateManager._enableClientState(32888);
         GlStateManager._glClientActiveTexture(33984);
      }, (p_227906_0_) -> {
         GlStateManager._glClientActiveTexture('\u84c0' + p_227906_0_);
         GlStateManager._disableClientState(32888);
         GlStateManager._glClientActiveTexture(33984);
      }),
      PADDING("Padding", (p_227907_0_, p_227907_1_, p_227907_2_, p_227907_3_, p_227907_5_) -> {
      }, (p_227904_0_) -> {
      }),
      GENERIC("Generic", (p_227905_0_, p_227905_1_, p_227905_2_, p_227905_3_, p_227905_5_) -> {
         GlStateManager._enableVertexAttribArray(p_227905_5_);
         GlStateManager._vertexAttribPointer(p_227905_5_, p_227905_0_, p_227905_1_, false, p_227905_2_, p_227905_3_);
      }, GlStateManager::_disableVertexAttribArray);

      private final String name;
      private final VertexFormatElement.Usage.ISetupState setupState;
      private final IntConsumer clearState;

      private Usage(String p_i225912_3_, VertexFormatElement.Usage.ISetupState p_i225912_4_, IntConsumer p_i225912_5_) {
         this.name = p_i225912_3_;
         this.setupState = p_i225912_4_;
         this.clearState = p_i225912_5_;
      }

      private void setupBufferState(int p_227902_1_, int p_227902_2_, int p_227902_3_, long p_227902_4_, int p_227902_6_) {
         this.setupState.setupBufferState(p_227902_1_, p_227902_2_, p_227902_3_, p_227902_4_, p_227902_6_);
      }

      public void clearBufferState(int p_227901_1_) {
         this.clearState.accept(p_227901_1_);
      }

      public String getName() {
         return this.name;
      }

      @OnlyIn(Dist.CLIENT)
      interface ISetupState {
         void setupBufferState(int p_setupBufferState_1_, int p_setupBufferState_2_, int p_setupBufferState_3_, long p_setupBufferState_4_, int p_setupBufferState_6_);
      }
   }
}
