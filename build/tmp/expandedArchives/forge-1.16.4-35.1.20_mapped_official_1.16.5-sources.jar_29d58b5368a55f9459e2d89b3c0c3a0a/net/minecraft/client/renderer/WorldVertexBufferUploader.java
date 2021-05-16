package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class WorldVertexBufferUploader {
   public static void end(BufferBuilder p_181679_0_) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            Pair<BufferBuilder.DrawState, ByteBuffer> pair1 = p_181679_0_.popNextBuffer();
            BufferBuilder.DrawState bufferbuilder$drawstate1 = pair1.getFirst();
            _end(pair1.getSecond(), bufferbuilder$drawstate1.mode(), bufferbuilder$drawstate1.format(), bufferbuilder$drawstate1.vertexCount());
         });
      } else {
         Pair<BufferBuilder.DrawState, ByteBuffer> pair = p_181679_0_.popNextBuffer();
         BufferBuilder.DrawState bufferbuilder$drawstate = pair.getFirst();
         _end(pair.getSecond(), bufferbuilder$drawstate.mode(), bufferbuilder$drawstate.format(), bufferbuilder$drawstate.vertexCount());
      }

   }

   private static void _end(ByteBuffer p_227844_0_, int p_227844_1_, VertexFormat p_227844_2_, int p_227844_3_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      ((Buffer)p_227844_0_).clear();
      if (p_227844_3_ > 0) {
         p_227844_2_.setupBufferState(MemoryUtil.memAddress(p_227844_0_));
         GlStateManager._drawArrays(p_227844_1_, 0, p_227844_3_);
         p_227844_2_.clearBufferState();
      }
   }
}
