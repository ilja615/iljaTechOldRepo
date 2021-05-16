package net.minecraft.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.renderer.VideoMode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

@OnlyIn(Dist.CLIENT)
public final class Monitor {
   private final long monitor;
   private final List<VideoMode> videoModes;
   private VideoMode currentMode;
   private int x;
   private int y;

   public Monitor(long p_i51795_1_) {
      this.monitor = p_i51795_1_;
      this.videoModes = Lists.newArrayList();
      this.refreshVideoModes();
   }

   public void refreshVideoModes() {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      this.videoModes.clear();
      Buffer buffer = GLFW.glfwGetVideoModes(this.monitor);

      for(int i = buffer.limit() - 1; i >= 0; --i) {
         buffer.position(i);
         VideoMode videomode = new VideoMode(buffer);
         if (videomode.getRedBits() >= 8 && videomode.getGreenBits() >= 8 && videomode.getBlueBits() >= 8) {
            this.videoModes.add(videomode);
         }
      }

      int[] aint = new int[1];
      int[] aint1 = new int[1];
      GLFW.glfwGetMonitorPos(this.monitor, aint, aint1);
      this.x = aint[0];
      this.y = aint1[0];
      GLFWVidMode glfwvidmode = GLFW.glfwGetVideoMode(this.monitor);
      this.currentMode = new VideoMode(glfwvidmode);
   }

   public VideoMode getPreferredVidMode(Optional<VideoMode> p_197992_1_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      if (p_197992_1_.isPresent()) {
         VideoMode videomode = p_197992_1_.get();

         for(VideoMode videomode1 : this.videoModes) {
            if (videomode1.equals(videomode)) {
               return videomode1;
            }
         }
      }

      return this.getCurrentMode();
   }

   public int getVideoModeIndex(VideoMode p_224794_1_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      return this.videoModes.indexOf(p_224794_1_);
   }

   public VideoMode getCurrentMode() {
      return this.currentMode;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public VideoMode getMode(int p_197991_1_) {
      return this.videoModes.get(p_197991_1_);
   }

   public int getModeCount() {
      return this.videoModes.size();
   }

   public long getMonitor() {
      return this.monitor;
   }

   public String toString() {
      return String.format("Monitor[%s %sx%s %s]", this.monitor, this.x, this.y, this.currentMode);
   }
}
