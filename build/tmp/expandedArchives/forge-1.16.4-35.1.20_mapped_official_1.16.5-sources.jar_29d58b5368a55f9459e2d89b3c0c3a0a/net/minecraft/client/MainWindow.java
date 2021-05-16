package net.minecraft.client;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Optional;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.IWindowEventListener;
import net.minecraft.client.renderer.MonitorHandler;
import net.minecraft.client.renderer.ScreenSize;
import net.minecraft.client.renderer.VideoMode;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.UndeclaredException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

@OnlyIn(Dist.CLIENT)
public final class MainWindow implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final GLFWErrorCallback defaultErrorCallback = GLFWErrorCallback.create(this::defaultErrorCallback);
   private final IWindowEventListener eventHandler;
   private final MonitorHandler screenManager;
   private final long window;
   private int windowedX;
   private int windowedY;
   private int windowedWidth;
   private int windowedHeight;
   private Optional<VideoMode> preferredFullscreenVideoMode;
   private boolean fullscreen;
   private boolean actuallyFullscreen;
   private int x;
   private int y;
   private int width;
   private int height;
   private int framebufferWidth;
   private int framebufferHeight;
   private int guiScaledWidth;
   private int guiScaledHeight;
   private double guiScale;
   private String errorSection = "";
   private boolean dirty;
   private int framerateLimit;
   private boolean vsync;

   public MainWindow(IWindowEventListener p_i51170_1_, MonitorHandler p_i51170_2_, ScreenSize p_i51170_3_, @Nullable String p_i51170_4_, String p_i51170_5_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      this.screenManager = p_i51170_2_;
      this.setBootErrorCallback();
      this.setErrorSection("Pre startup");
      this.eventHandler = p_i51170_1_;
      Optional<VideoMode> optional = VideoMode.read(p_i51170_4_);
      if (optional.isPresent()) {
         this.preferredFullscreenVideoMode = optional;
      } else if (p_i51170_3_.fullscreenWidth.isPresent() && p_i51170_3_.fullscreenHeight.isPresent()) {
         this.preferredFullscreenVideoMode = Optional.of(new VideoMode(p_i51170_3_.fullscreenWidth.getAsInt(), p_i51170_3_.fullscreenHeight.getAsInt(), 8, 8, 8, 60));
      } else {
         this.preferredFullscreenVideoMode = Optional.empty();
      }

      this.actuallyFullscreen = this.fullscreen = p_i51170_3_.isFullscreen;
      Monitor monitor = p_i51170_2_.getMonitor(GLFW.glfwGetPrimaryMonitor());
      this.windowedWidth = this.width = p_i51170_3_.width > 0 ? p_i51170_3_.width : 1;
      this.windowedHeight = this.height = p_i51170_3_.height > 0 ? p_i51170_3_.height : 1;
      GLFW.glfwDefaultWindowHints();
      GLFW.glfwWindowHint(139265, 196609);
      GLFW.glfwWindowHint(139275, 221185);
      GLFW.glfwWindowHint(139266, 2);
      GLFW.glfwWindowHint(139267, 0);
      GLFW.glfwWindowHint(139272, 0);
      this.window = net.minecraftforge.fml.loading.progress.EarlyProgressVisualization.INSTANCE.handOffWindow(()->this.width, ()->this.height, ()->p_i51170_5_, ()->this.fullscreen && monitor != null ? monitor.getMonitor() : 0L);
      if (monitor != null) {
         VideoMode videomode = monitor.getPreferredVidMode(this.fullscreen ? this.preferredFullscreenVideoMode : Optional.empty());
         this.windowedX = this.x = monitor.getX() + videomode.getWidth() / 2 - this.width / 2;
         this.windowedY = this.y = monitor.getY() + videomode.getHeight() / 2 - this.height / 2;
      } else {
         int[] aint1 = new int[1];
         int[] aint = new int[1];
         GLFW.glfwGetWindowPos(this.window, aint1, aint);
         this.windowedX = this.x = aint1[0];
         this.windowedY = this.y = aint[0];
      }

      GLFW.glfwMakeContextCurrent(this.window);
      GL.createCapabilities();
      this.setMode();
      this.refreshFramebufferSize();
      GLFW.glfwSetFramebufferSizeCallback(this.window, this::onFramebufferResize);
      GLFW.glfwSetWindowPosCallback(this.window, this::onMove);
      GLFW.glfwSetWindowSizeCallback(this.window, this::onResize);
      GLFW.glfwSetWindowFocusCallback(this.window, this::onFocus);
      GLFW.glfwSetCursorEnterCallback(this.window, this::onEnter);
   }

   public int getRefreshRate() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GLX._getRefreshRate(this);
   }

   public boolean shouldClose() {
      return GLX._shouldClose(this);
   }

   public static void checkGlfwError(BiConsumer<Integer, String> p_211162_0_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);

      try (MemoryStack memorystack = MemoryStack.stackPush()) {
         PointerBuffer pointerbuffer = memorystack.mallocPointer(1);
         int i = GLFW.glfwGetError(pointerbuffer);
         if (i != 0) {
            long j = pointerbuffer.get();
            String s = j == 0L ? "" : MemoryUtil.memUTF8(j);
            p_211162_0_.accept(i, s);
         }
      }

   }

   public void setIcon(InputStream p_216529_1_, InputStream p_216529_2_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);

      try (MemoryStack memorystack = MemoryStack.stackPush()) {
         if (p_216529_1_ == null) {
            throw new FileNotFoundException("icons/icon_16x16.png");
         }

         if (p_216529_2_ == null) {
            throw new FileNotFoundException("icons/icon_32x32.png");
         }

         IntBuffer intbuffer = memorystack.mallocInt(1);
         IntBuffer intbuffer1 = memorystack.mallocInt(1);
         IntBuffer intbuffer2 = memorystack.mallocInt(1);
         Buffer buffer = GLFWImage.mallocStack(2, memorystack);
         ByteBuffer bytebuffer = this.readIconPixels(p_216529_1_, intbuffer, intbuffer1, intbuffer2);
         if (bytebuffer == null) {
            throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
         }

         buffer.position(0);
         buffer.width(intbuffer.get(0));
         buffer.height(intbuffer1.get(0));
         buffer.pixels(bytebuffer);
         ByteBuffer bytebuffer1 = this.readIconPixels(p_216529_2_, intbuffer, intbuffer1, intbuffer2);
         if (bytebuffer1 == null) {
            throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
         }

         buffer.position(1);
         buffer.width(intbuffer.get(0));
         buffer.height(intbuffer1.get(0));
         buffer.pixels(bytebuffer1);
         buffer.position(0);
         GLFW.glfwSetWindowIcon(this.window, buffer);
         STBImage.stbi_image_free(bytebuffer);
         STBImage.stbi_image_free(bytebuffer1);
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't set icon", (Throwable)ioexception);
      }

   }

   @Nullable
   private ByteBuffer readIconPixels(InputStream p_198111_1_, IntBuffer p_198111_2_, IntBuffer p_198111_3_, IntBuffer p_198111_4_) throws IOException {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      ByteBuffer bytebuffer = null;

      ByteBuffer bytebuffer1;
      try {
         bytebuffer = TextureUtil.readResource(p_198111_1_);
         ((java.nio.Buffer)bytebuffer).rewind();
         bytebuffer1 = STBImage.stbi_load_from_memory(bytebuffer, p_198111_2_, p_198111_3_, p_198111_4_, 0);
      } finally {
         if (bytebuffer != null) {
            MemoryUtil.memFree(bytebuffer);
         }

      }

      return bytebuffer1;
   }

   public void setErrorSection(String p_227799_1_) {
      this.errorSection = p_227799_1_;
   }

   private void setBootErrorCallback() {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      GLFW.glfwSetErrorCallback(MainWindow::bootCrash);
   }

   private static void bootCrash(int p_208034_0_, long p_208034_1_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      String s = "GLFW error " + p_208034_0_ + ": " + MemoryUtil.memUTF8(p_208034_1_);
      TinyFileDialogs.tinyfd_messageBox("Minecraft", s + ".\n\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).", "ok", "error", false);
      throw new MainWindow.GlException(s);
   }

   public void defaultErrorCallback(int p_198084_1_, long p_198084_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      String s = MemoryUtil.memUTF8(p_198084_2_);
      LOGGER.error("########## GL ERROR ##########");
      LOGGER.error("@ {}", (Object)this.errorSection);
      LOGGER.error("{}: {}", p_198084_1_, s);
   }

   public void setDefaultErrorCallback() {
      GLFWErrorCallback glfwerrorcallback = GLFW.glfwSetErrorCallback(this.defaultErrorCallback);
      if (glfwerrorcallback != null) {
         glfwerrorcallback.free();
      }

   }

   public void updateVsync(boolean p_216523_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      this.vsync = p_216523_1_;
      GLFW.glfwSwapInterval(p_216523_1_ ? 1 : 0);
   }

   public void close() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      Callbacks.glfwFreeCallbacks(this.window);
      this.defaultErrorCallback.close();
      GLFW.glfwDestroyWindow(this.window);
      GLFW.glfwTerminate();
   }

   private void onMove(long p_198080_1_, int p_198080_3_, int p_198080_4_) {
      this.x = p_198080_3_;
      this.y = p_198080_4_;
   }

   private void onFramebufferResize(long p_198102_1_, int p_198102_3_, int p_198102_4_) {
      if (p_198102_1_ == this.window) {
         int i = this.getWidth();
         int j = this.getHeight();
         if (p_198102_3_ != 0 && p_198102_4_ != 0) {
            this.framebufferWidth = p_198102_3_;
            this.framebufferHeight = p_198102_4_;
            if (this.getWidth() != i || this.getHeight() != j) {
               this.eventHandler.resizeDisplay();
            }

         }
      }
   }

   private void refreshFramebufferSize() {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      int[] aint = new int[1];
      int[] aint1 = new int[1];
      GLFW.glfwGetFramebufferSize(this.window, aint, aint1);
      this.framebufferWidth = aint[0];
      this.framebufferHeight = aint1[0];
      if (this.framebufferHeight == 0 || this.framebufferWidth==0) net.minecraftforge.fml.loading.progress.EarlyProgressVisualization.INSTANCE.updateFBSize(w->this.framebufferWidth=w, h->this.framebufferHeight=h);
   }

   private void onResize(long p_198089_1_, int p_198089_3_, int p_198089_4_) {
      this.width = p_198089_3_;
      this.height = p_198089_4_;
   }

   private void onFocus(long p_198095_1_, boolean p_198095_3_) {
      if (p_198095_1_ == this.window) {
         this.eventHandler.setWindowActive(p_198095_3_);
      }

   }

   private void onEnter(long p_241553_1_, boolean p_241553_3_) {
      if (p_241553_3_) {
         this.eventHandler.cursorEntered();
      }

   }

   public void setFramerateLimit(int p_216526_1_) {
      this.framerateLimit = p_216526_1_;
   }

   public int getFramerateLimit() {
      return this.framerateLimit;
   }

   public void updateDisplay() {
      RenderSystem.flipFrame(this.window);
      if (this.fullscreen != this.actuallyFullscreen) {
         this.actuallyFullscreen = this.fullscreen;
         this.updateFullscreen(this.vsync);
      }

   }

   public Optional<VideoMode> getPreferredFullscreenVideoMode() {
      return this.preferredFullscreenVideoMode;
   }

   public void setPreferredFullscreenVideoMode(Optional<VideoMode> p_224797_1_) {
      boolean flag = !p_224797_1_.equals(this.preferredFullscreenVideoMode);
      this.preferredFullscreenVideoMode = p_224797_1_;
      if (flag) {
         this.dirty = true;
      }

   }

   public void changeFullscreenVideoMode() {
      if (this.fullscreen && this.dirty) {
         this.dirty = false;
         this.setMode();
         this.eventHandler.resizeDisplay();
      }

   }

   private void setMode() {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      boolean flag = GLFW.glfwGetWindowMonitor(this.window) != 0L;
      if (this.fullscreen) {
         Monitor monitor = this.screenManager.findBestMonitor(this);
         if (monitor == null) {
            LOGGER.warn("Failed to find suitable monitor for fullscreen mode");
            this.fullscreen = false;
         } else {
            VideoMode videomode = monitor.getPreferredVidMode(this.preferredFullscreenVideoMode);
            if (!flag) {
               this.windowedX = this.x;
               this.windowedY = this.y;
               this.windowedWidth = this.width;
               this.windowedHeight = this.height;
            }

            this.x = 0;
            this.y = 0;
            this.width = videomode.getWidth();
            this.height = videomode.getHeight();
            GLFW.glfwSetWindowMonitor(this.window, monitor.getMonitor(), this.x, this.y, this.width, this.height, videomode.getRefreshRate());
         }
      } else {
         this.x = this.windowedX;
         this.y = this.windowedY;
         this.width = this.windowedWidth;
         this.height = this.windowedHeight;
         GLFW.glfwSetWindowMonitor(this.window, 0L, this.x, this.y, this.width, this.height, -1);
      }

   }

   public void toggleFullScreen() {
      this.fullscreen = !this.fullscreen;
   }

   private void updateFullscreen(boolean p_216527_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);

      try {
         this.setMode();
         this.eventHandler.resizeDisplay();
         this.updateVsync(p_216527_1_);
         this.updateDisplay();
      } catch (Exception exception) {
         LOGGER.error("Couldn't toggle fullscreen", (Throwable)exception);
      }

   }

   public int calculateScale(int p_216521_1_, boolean p_216521_2_) {
      int i;
      for(i = 1; i != p_216521_1_ && i < this.framebufferWidth && i < this.framebufferHeight && this.framebufferWidth / (i + 1) >= 320 && this.framebufferHeight / (i + 1) >= 240; ++i) {
      }

      if (p_216521_2_ && i % 2 != 0) {
         ++i;
      }

      return i;
   }

   public void setGuiScale(double p_216525_1_) {
      this.guiScale = p_216525_1_;
      int i = (int)((double)this.framebufferWidth / p_216525_1_);
      this.guiScaledWidth = (double)this.framebufferWidth / p_216525_1_ > (double)i ? i + 1 : i;
      int j = (int)((double)this.framebufferHeight / p_216525_1_);
      this.guiScaledHeight = (double)this.framebufferHeight / p_216525_1_ > (double)j ? j + 1 : j;
   }

   public void setTitle(String p_230148_1_) {
      GLFW.glfwSetWindowTitle(this.window, p_230148_1_);
   }

   public long getWindow() {
      return this.window;
   }

   public boolean isFullscreen() {
      return this.fullscreen;
   }

   public int getWidth() {
      return this.framebufferWidth;
   }

   public int getHeight() {
      return this.framebufferHeight;
   }

   public int getScreenWidth() {
      return this.width;
   }

   public int getScreenHeight() {
      return this.height;
   }

   public int getGuiScaledWidth() {
      return this.guiScaledWidth;
   }

   public int getGuiScaledHeight() {
      return this.guiScaledHeight;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public double getGuiScale() {
      return this.guiScale;
   }

   @Nullable
   public Monitor findBestMonitor() {
      return this.screenManager.findBestMonitor(this);
   }

   public void updateRawMouseInput(boolean p_224798_1_) {
      InputMappings.updateRawMouseInput(this.window, p_224798_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class GlException extends UndeclaredException {
      private GlException(String p_i225902_1_) {
         super(p_i225902_1_);
      }
   }
}
