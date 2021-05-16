package net.minecraft.client;

import com.google.common.base.Charsets;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.minecraft.util.text.TextProcessing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class ClipboardHelper {
   private final ByteBuffer clipboardScratchBuffer = BufferUtils.createByteBuffer(8192);

   public String getClipboard(long p_216487_1_, GLFWErrorCallbackI p_216487_3_) {
      GLFWErrorCallback glfwerrorcallback = GLFW.glfwSetErrorCallback(p_216487_3_);
      String s = GLFW.glfwGetClipboardString(p_216487_1_);
      s = s != null ? TextProcessing.filterBrokenSurrogates(s) : "";
      GLFWErrorCallback glfwerrorcallback1 = GLFW.glfwSetErrorCallback(glfwerrorcallback);
      if (glfwerrorcallback1 != null) {
         glfwerrorcallback1.free();
      }

      return s;
   }

   private static void pushClipboard(long p_230147_0_, ByteBuffer p_230147_2_, byte[] p_230147_3_) {
      ((Buffer)p_230147_2_).clear();
      p_230147_2_.put(p_230147_3_);
      p_230147_2_.put((byte)0);
      ((Buffer)p_230147_2_).flip();
      GLFW.glfwSetClipboardString(p_230147_0_, p_230147_2_);
   }

   public void setClipboard(long p_216489_1_, String p_216489_3_) {
      byte[] abyte = p_216489_3_.getBytes(Charsets.UTF_8);
      int i = abyte.length + 1;
      if (i < this.clipboardScratchBuffer.capacity()) {
         pushClipboard(p_216489_1_, this.clipboardScratchBuffer, abyte);
      } else {
         ByteBuffer bytebuffer = MemoryUtil.memAlloc(i);

         try {
            pushClipboard(p_216489_1_, bytebuffer, abyte);
         } finally {
            MemoryUtil.memFree(bytebuffer);
         }
      }

   }
}
