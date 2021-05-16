package net.minecraft.client.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class ShaderUniform extends ShaderDefault implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private int location;
   private final int count;
   private final int type;
   private final IntBuffer intValues;
   private final FloatBuffer floatValues;
   private final String name;
   private boolean dirty;
   private final IShaderManager parent;

   public ShaderUniform(String p_i45092_1_, int p_i45092_2_, int p_i45092_3_, IShaderManager p_i45092_4_) {
      this.name = p_i45092_1_;
      this.count = p_i45092_3_;
      this.type = p_i45092_2_;
      this.parent = p_i45092_4_;
      if (p_i45092_2_ <= 3) {
         this.intValues = MemoryUtil.memAllocInt(p_i45092_3_);
         this.floatValues = null;
      } else {
         this.intValues = null;
         this.floatValues = MemoryUtil.memAllocFloat(p_i45092_3_);
      }

      this.location = -1;
      this.markDirty();
   }

   public static int glGetUniformLocation(int p_227806_0_, CharSequence p_227806_1_) {
      return GlStateManager._glGetUniformLocation(p_227806_0_, p_227806_1_);
   }

   public static void uploadInteger(int p_227805_0_, int p_227805_1_) {
      RenderSystem.glUniform1i(p_227805_0_, p_227805_1_);
   }

   public static int glGetAttribLocation(int p_227807_0_, CharSequence p_227807_1_) {
      return GlStateManager._glGetAttribLocation(p_227807_0_, p_227807_1_);
   }

   public void close() {
      if (this.intValues != null) {
         MemoryUtil.memFree(this.intValues);
      }

      if (this.floatValues != null) {
         MemoryUtil.memFree(this.floatValues);
      }

   }

   private void markDirty() {
      this.dirty = true;
      if (this.parent != null) {
         this.parent.markDirty();
      }

   }

   public static int getTypeFromString(String p_148085_0_) {
      int i = -1;
      if ("int".equals(p_148085_0_)) {
         i = 0;
      } else if ("float".equals(p_148085_0_)) {
         i = 4;
      } else if (p_148085_0_.startsWith("matrix")) {
         if (p_148085_0_.endsWith("2x2")) {
            i = 8;
         } else if (p_148085_0_.endsWith("3x3")) {
            i = 9;
         } else if (p_148085_0_.endsWith("4x4")) {
            i = 10;
         }
      }

      return i;
   }

   public void setLocation(int p_148084_1_) {
      this.location = p_148084_1_;
   }

   public String getName() {
      return this.name;
   }

   public void set(float p_148090_1_) {
      ((Buffer)this.floatValues).position(0);
      this.floatValues.put(0, p_148090_1_);
      this.markDirty();
   }

   public void set(float p_148087_1_, float p_148087_2_) {
      ((Buffer)this.floatValues).position(0);
      this.floatValues.put(0, p_148087_1_);
      this.floatValues.put(1, p_148087_2_);
      this.markDirty();
   }

   public void set(float p_148095_1_, float p_148095_2_, float p_148095_3_) {
      ((Buffer)this.floatValues).position(0);
      this.floatValues.put(0, p_148095_1_);
      this.floatValues.put(1, p_148095_2_);
      this.floatValues.put(2, p_148095_3_);
      this.markDirty();
   }

   public void set(float p_148081_1_, float p_148081_2_, float p_148081_3_, float p_148081_4_) {
      ((Buffer)this.floatValues).position(0);
      this.floatValues.put(p_148081_1_);
      this.floatValues.put(p_148081_2_);
      this.floatValues.put(p_148081_3_);
      this.floatValues.put(p_148081_4_);
      ((Buffer)this.floatValues).flip();
      this.markDirty();
   }

   public void setSafe(float p_148092_1_, float p_148092_2_, float p_148092_3_, float p_148092_4_) {
      ((Buffer)this.floatValues).position(0);
      if (this.type >= 4) {
         this.floatValues.put(0, p_148092_1_);
      }

      if (this.type >= 5) {
         this.floatValues.put(1, p_148092_2_);
      }

      if (this.type >= 6) {
         this.floatValues.put(2, p_148092_3_);
      }

      if (this.type >= 7) {
         this.floatValues.put(3, p_148092_4_);
      }

      this.markDirty();
   }

   public void setSafe(int p_148083_1_, int p_148083_2_, int p_148083_3_, int p_148083_4_) {
      ((Buffer)this.intValues).position(0);
      if (this.type >= 0) {
         this.intValues.put(0, p_148083_1_);
      }

      if (this.type >= 1) {
         this.intValues.put(1, p_148083_2_);
      }

      if (this.type >= 2) {
         this.intValues.put(2, p_148083_3_);
      }

      if (this.type >= 3) {
         this.intValues.put(3, p_148083_4_);
      }

      this.markDirty();
   }

   public void set(float[] p_148097_1_) {
      if (p_148097_1_.length < this.count) {
         LOGGER.warn("Uniform.set called with a too-small value array (expected {}, got {}). Ignoring.", this.count, p_148097_1_.length);
      } else {
         ((Buffer)this.floatValues).position(0);
         this.floatValues.put(p_148097_1_);
         ((Buffer)this.floatValues).position(0);
         this.markDirty();
      }
   }

   public void set(Matrix4f p_195652_1_) {
      ((Buffer)this.floatValues).position(0);
      p_195652_1_.store(this.floatValues);
      this.markDirty();
   }

   public void upload() {
      if (!this.dirty) {
      }

      this.dirty = false;
      if (this.type <= 3) {
         this.uploadAsInteger();
      } else if (this.type <= 7) {
         this.uploadAsFloat();
      } else {
         if (this.type > 10) {
            LOGGER.warn("Uniform.upload called, but type value ({}) is not a valid type. Ignoring.", (int)this.type);
            return;
         }

         this.uploadAsMatrix();
      }

   }

   private void uploadAsInteger() {
      ((Buffer)this.floatValues).clear();
      switch(this.type) {
      case 0:
         RenderSystem.glUniform1(this.location, this.intValues);
         break;
      case 1:
         RenderSystem.glUniform2(this.location, this.intValues);
         break;
      case 2:
         RenderSystem.glUniform3(this.location, this.intValues);
         break;
      case 3:
         RenderSystem.glUniform4(this.location, this.intValues);
         break;
      default:
         LOGGER.warn("Uniform.upload called, but count value ({}) is  not in the range of 1 to 4. Ignoring.", (int)this.count);
      }

   }

   private void uploadAsFloat() {
      ((Buffer)this.floatValues).clear();
      switch(this.type) {
      case 4:
         RenderSystem.glUniform1(this.location, this.floatValues);
         break;
      case 5:
         RenderSystem.glUniform2(this.location, this.floatValues);
         break;
      case 6:
         RenderSystem.glUniform3(this.location, this.floatValues);
         break;
      case 7:
         RenderSystem.glUniform4(this.location, this.floatValues);
         break;
      default:
         LOGGER.warn("Uniform.upload called, but count value ({}) is not in the range of 1 to 4. Ignoring.", (int)this.count);
      }

   }

   private void uploadAsMatrix() {
      ((Buffer)this.floatValues).clear();
      switch(this.type) {
      case 8:
         RenderSystem.glUniformMatrix2(this.location, false, this.floatValues);
         break;
      case 9:
         RenderSystem.glUniformMatrix3(this.location, false, this.floatValues);
         break;
      case 10:
         RenderSystem.glUniformMatrix4(this.location, false, this.floatValues);
      }

   }
}
