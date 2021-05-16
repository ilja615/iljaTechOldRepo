package net.minecraft.client.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ShaderLinkHelper {
   private static final Logger LOGGER = LogManager.getLogger();

   public static void glUseProgram(int p_227804_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GlStateManager._glUseProgram(p_227804_0_);
   }

   public static void releaseProgram(IShaderManager p_148077_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      p_148077_0_.getFragmentProgram().close();
      p_148077_0_.getVertexProgram().close();
      GlStateManager.glDeleteProgram(p_148077_0_.getId());
   }

   public static int createProgram() throws IOException {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      int i = GlStateManager.glCreateProgram();
      if (i <= 0) {
         throw new IOException("Could not create shader program (returned program ID " + i + ")");
      } else {
         return i;
      }
   }

   public static void linkProgram(IShaderManager p_148075_0_) throws IOException {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      p_148075_0_.getFragmentProgram().attachToEffect(p_148075_0_);
      p_148075_0_.getVertexProgram().attachToEffect(p_148075_0_);
      GlStateManager.glLinkProgram(p_148075_0_.getId());
      int i = GlStateManager.glGetProgrami(p_148075_0_.getId(), 35714);
      if (i == 0) {
         LOGGER.warn("Error encountered when linking program containing VS {} and FS {}. Log output:", p_148075_0_.getVertexProgram().getName(), p_148075_0_.getFragmentProgram().getName());
         LOGGER.warn(GlStateManager.glGetProgramInfoLog(p_148075_0_.getId(), 32768));
      }

   }
}
