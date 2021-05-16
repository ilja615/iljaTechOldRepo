package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Monitor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallback;
import org.lwjgl.glfw.GLFWMonitorCallbackI;

@OnlyIn(Dist.CLIENT)
public class MonitorHandler {
   private final Long2ObjectMap<Monitor> monitors = new Long2ObjectOpenHashMap<>();
   private final IMonitorFactory monitorCreator;

   public MonitorHandler(IMonitorFactory p_i51171_1_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      this.monitorCreator = p_i51171_1_;
      GLFW.glfwSetMonitorCallback(this::onMonitorChange);
      PointerBuffer pointerbuffer = GLFW.glfwGetMonitors();
      if (pointerbuffer != null) {
         for(int i = 0; i < pointerbuffer.limit(); ++i) {
            long j = pointerbuffer.get(i);
            this.monitors.put(j, p_i51171_1_.createMonitor(j));
         }
      }

   }

   private void onMonitorChange(long p_216516_1_, int p_216516_3_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (p_216516_3_ == 262145) {
         this.monitors.put(p_216516_1_, this.monitorCreator.createMonitor(p_216516_1_));
      } else if (p_216516_3_ == 262146) {
         this.monitors.remove(p_216516_1_);
      }

   }

   @Nullable
   public Monitor getMonitor(long p_216512_1_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      return this.monitors.get(p_216512_1_);
   }

   @Nullable
   public Monitor findBestMonitor(MainWindow p_216515_1_) {
      long i = GLFW.glfwGetWindowMonitor(p_216515_1_.getWindow());
      if (i != 0L) {
         return this.getMonitor(i);
      } else {
         int j = p_216515_1_.getX();
         int k = j + p_216515_1_.getScreenWidth();
         int l = p_216515_1_.getY();
         int i1 = l + p_216515_1_.getScreenHeight();
         int j1 = -1;
         Monitor monitor = null;

         for(Monitor monitor1 : this.monitors.values()) {
            int k1 = monitor1.getX();
            int l1 = k1 + monitor1.getCurrentMode().getWidth();
            int i2 = monitor1.getY();
            int j2 = i2 + monitor1.getCurrentMode().getHeight();
            int k2 = clamp(j, k1, l1);
            int l2 = clamp(k, k1, l1);
            int i3 = clamp(l, i2, j2);
            int j3 = clamp(i1, i2, j2);
            int k3 = Math.max(0, l2 - k2);
            int l3 = Math.max(0, j3 - i3);
            int i4 = k3 * l3;
            if (i4 > j1) {
               monitor = monitor1;
               j1 = i4;
            }
         }

         return monitor;
      }
   }

   public static int clamp(int p_216513_0_, int p_216513_1_, int p_216513_2_) {
      if (p_216513_0_ < p_216513_1_) {
         return p_216513_1_;
      } else {
         return p_216513_0_ > p_216513_2_ ? p_216513_2_ : p_216513_0_;
      }
   }

   public void shutdown() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GLFWMonitorCallback glfwmonitorcallback = GLFW.glfwSetMonitorCallback((GLFWMonitorCallbackI)null);
      if (glfwmonitorcallback != null) {
         glfwmonitorcallback.free();
      }

   }
}
