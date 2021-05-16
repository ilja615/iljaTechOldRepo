package com.mojang.blaze3d.systems;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.GraphicsFanciness;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallbackI;

@OnlyIn(Dist.CLIENT)
public class RenderSystem {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ConcurrentLinkedQueue<IRenderCall> recordingQueue = Queues.newConcurrentLinkedQueue();
   private static final Tessellator RENDER_THREAD_TESSELATOR = new Tessellator();
   public static final float DEFAULTALPHACUTOFF = 0.1F;
   private static final int MINIMUM_ATLAS_TEXTURE_SIZE = 1024;
   private static boolean isReplayingQueue;
   private static Thread gameThread;
   private static Thread renderThread;
   private static int MAX_SUPPORTED_TEXTURE_SIZE = -1;
   private static boolean isInInit;
   private static double lastDrawTime = Double.MIN_VALUE;

   public static void initRenderThread() {
      if (renderThread == null && gameThread != Thread.currentThread()) {
         renderThread = Thread.currentThread();
      } else {
         throw new IllegalStateException("Could not initialize render thread");
      }
   }

   public static boolean isOnRenderThread() {
      return Thread.currentThread() == renderThread;
   }

   public static boolean isOnRenderThreadOrInit() {
      return isInInit || isOnRenderThread();
   }

   public static void initGameThread(boolean p_initGameThread_0_) {
      boolean flag = renderThread == Thread.currentThread();
      if (gameThread == null && renderThread != null && flag != p_initGameThread_0_) {
         gameThread = Thread.currentThread();
      } else {
         throw new IllegalStateException("Could not initialize tick thread");
      }
   }

   public static boolean isOnGameThread() {
      return true;
   }

   public static boolean isOnGameThreadOrInit() {
      return isInInit || isOnGameThread();
   }

   public static void assertThread(Supplier<Boolean> p_assertThread_0_) {
      if (!p_assertThread_0_.get()) {
         throw new IllegalStateException("Rendersystem called from wrong thread");
      }
   }

   public static boolean isInInitPhase() {
      return true;
   }

   public static void recordRenderCall(IRenderCall p_recordRenderCall_0_) {
      recordingQueue.add(p_recordRenderCall_0_);
   }

   public static void flipFrame(long p_flipFrame_0_) {
      GLFW.glfwPollEvents();
      replayQueue();
      Tessellator.getInstance().getBuilder().clear();
      GLFW.glfwSwapBuffers(p_flipFrame_0_);
      GLFW.glfwPollEvents();
   }

   public static void replayQueue() {
      isReplayingQueue = true;

      while(!recordingQueue.isEmpty()) {
         IRenderCall irendercall = recordingQueue.poll();
         irendercall.execute();
      }

      isReplayingQueue = false;
   }

   public static void limitDisplayFPS(int p_limitDisplayFPS_0_) {
      double d0 = lastDrawTime + 1.0D / (double)p_limitDisplayFPS_0_;

      double d1;
      for(d1 = GLFW.glfwGetTime(); d1 < d0; d1 = GLFW.glfwGetTime()) {
         GLFW.glfwWaitEventsTimeout(d0 - d1);
      }

      lastDrawTime = d1;
   }

   @Deprecated
   public static void pushLightingAttributes() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._pushLightingAttributes();
   }

   @Deprecated
   public static void pushTextureAttributes() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._pushTextureAttributes();
   }

   @Deprecated
   public static void popAttributes() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._popAttributes();
   }

   @Deprecated
   public static void disableAlphaTest() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disableAlphaTest();
   }

   @Deprecated
   public static void enableAlphaTest() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._enableAlphaTest();
   }

   @Deprecated
   public static void alphaFunc(int p_alphaFunc_0_, float p_alphaFunc_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._alphaFunc(p_alphaFunc_0_, p_alphaFunc_1_);
   }

   @Deprecated
   public static void enableLighting() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._enableLighting();
   }

   @Deprecated
   public static void disableLighting() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disableLighting();
   }

   @Deprecated
   public static void enableColorMaterial() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._enableColorMaterial();
   }

   @Deprecated
   public static void disableColorMaterial() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disableColorMaterial();
   }

   @Deprecated
   public static void colorMaterial(int p_colorMaterial_0_, int p_colorMaterial_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._colorMaterial(p_colorMaterial_0_, p_colorMaterial_1_);
   }

   @Deprecated
   public static void normal3f(float p_normal3f_0_, float p_normal3f_1_, float p_normal3f_2_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._normal3f(p_normal3f_0_, p_normal3f_1_, p_normal3f_2_);
   }

   public static void disableDepthTest() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disableDepthTest();
   }

   public static void enableDepthTest() {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager._enableDepthTest();
   }

   public static void enableScissor(int p_enableScissor_0_, int p_enableScissor_1_, int p_enableScissor_2_, int p_enableScissor_3_) {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager._enableScissorTest();
      GlStateManager._scissorBox(p_enableScissor_0_, p_enableScissor_1_, p_enableScissor_2_, p_enableScissor_3_);
   }

   public static void disableScissor() {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager._disableScissorTest();
   }

   public static void depthFunc(int p_depthFunc_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._depthFunc(p_depthFunc_0_);
   }

   public static void depthMask(boolean p_depthMask_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._depthMask(p_depthMask_0_);
   }

   public static void enableBlend() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._enableBlend();
   }

   public static void disableBlend() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disableBlend();
   }

   public static void blendFunc(GlStateManager.SourceFactor p_blendFunc_0_, GlStateManager.DestFactor p_blendFunc_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._blendFunc(p_blendFunc_0_.value, p_blendFunc_1_.value);
   }

   public static void blendFunc(int p_blendFunc_0_, int p_blendFunc_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._blendFunc(p_blendFunc_0_, p_blendFunc_1_);
   }

   public static void blendFuncSeparate(GlStateManager.SourceFactor p_blendFuncSeparate_0_, GlStateManager.DestFactor p_blendFuncSeparate_1_, GlStateManager.SourceFactor p_blendFuncSeparate_2_, GlStateManager.DestFactor p_blendFuncSeparate_3_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._blendFuncSeparate(p_blendFuncSeparate_0_.value, p_blendFuncSeparate_1_.value, p_blendFuncSeparate_2_.value, p_blendFuncSeparate_3_.value);
   }

   public static void blendFuncSeparate(int p_blendFuncSeparate_0_, int p_blendFuncSeparate_1_, int p_blendFuncSeparate_2_, int p_blendFuncSeparate_3_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._blendFuncSeparate(p_blendFuncSeparate_0_, p_blendFuncSeparate_1_, p_blendFuncSeparate_2_, p_blendFuncSeparate_3_);
   }

   public static void blendEquation(int p_blendEquation_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._blendEquation(p_blendEquation_0_);
   }

   public static void blendColor(float p_blendColor_0_, float p_blendColor_1_, float p_blendColor_2_, float p_blendColor_3_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._blendColor(p_blendColor_0_, p_blendColor_1_, p_blendColor_2_, p_blendColor_3_);
   }

   @Deprecated
   public static void enableFog() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._enableFog();
   }

   @Deprecated
   public static void disableFog() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disableFog();
   }

   @Deprecated
   public static void fogMode(GlStateManager.FogMode p_fogMode_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._fogMode(p_fogMode_0_.value);
   }

   @Deprecated
   public static void fogMode(int p_fogMode_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._fogMode(p_fogMode_0_);
   }

   @Deprecated
   public static void fogDensity(float p_fogDensity_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._fogDensity(p_fogDensity_0_);
   }

   @Deprecated
   public static void fogStart(float p_fogStart_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._fogStart(p_fogStart_0_);
   }

   @Deprecated
   public static void fogEnd(float p_fogEnd_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._fogEnd(p_fogEnd_0_);
   }

   @Deprecated
   public static void fog(int p_fog_0_, float p_fog_1_, float p_fog_2_, float p_fog_3_, float p_fog_4_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._fog(p_fog_0_, new float[]{p_fog_1_, p_fog_2_, p_fog_3_, p_fog_4_});
   }

   @Deprecated
   public static void fogi(int p_fogi_0_, int p_fogi_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._fogi(p_fogi_0_, p_fogi_1_);
   }

   public static void enableCull() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._enableCull();
   }

   public static void disableCull() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disableCull();
   }

   public static void polygonMode(int p_polygonMode_0_, int p_polygonMode_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._polygonMode(p_polygonMode_0_, p_polygonMode_1_);
   }

   public static void enablePolygonOffset() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._enablePolygonOffset();
   }

   public static void disablePolygonOffset() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disablePolygonOffset();
   }

   public static void enableLineOffset() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._enableLineOffset();
   }

   public static void disableLineOffset() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disableLineOffset();
   }

   public static void polygonOffset(float p_polygonOffset_0_, float p_polygonOffset_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._polygonOffset(p_polygonOffset_0_, p_polygonOffset_1_);
   }

   public static void enableColorLogicOp() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._enableColorLogicOp();
   }

   public static void disableColorLogicOp() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disableColorLogicOp();
   }

   public static void logicOp(GlStateManager.LogicOp p_logicOp_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._logicOp(p_logicOp_0_.value);
   }

   public static void activeTexture(int p_activeTexture_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._activeTexture(p_activeTexture_0_);
   }

   public static void enableTexture() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._enableTexture();
   }

   public static void disableTexture() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disableTexture();
   }

   public static void texParameter(int p_texParameter_0_, int p_texParameter_1_, int p_texParameter_2_) {
      GlStateManager._texParameter(p_texParameter_0_, p_texParameter_1_, p_texParameter_2_);
   }

   public static void deleteTexture(int p_deleteTexture_0_) {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager._deleteTexture(p_deleteTexture_0_);
   }

   public static void bindTexture(int p_bindTexture_0_) {
      GlStateManager._bindTexture(p_bindTexture_0_);
   }

   @Deprecated
   public static void shadeModel(int p_shadeModel_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._shadeModel(p_shadeModel_0_);
   }

   @Deprecated
   public static void enableRescaleNormal() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._enableRescaleNormal();
   }

   @Deprecated
   public static void disableRescaleNormal() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disableRescaleNormal();
   }

   public static void viewport(int p_viewport_0_, int p_viewport_1_, int p_viewport_2_, int p_viewport_3_) {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager._viewport(p_viewport_0_, p_viewport_1_, p_viewport_2_, p_viewport_3_);
   }

   public static void colorMask(boolean p_colorMask_0_, boolean p_colorMask_1_, boolean p_colorMask_2_, boolean p_colorMask_3_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._colorMask(p_colorMask_0_, p_colorMask_1_, p_colorMask_2_, p_colorMask_3_);
   }

   public static void stencilFunc(int p_stencilFunc_0_, int p_stencilFunc_1_, int p_stencilFunc_2_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._stencilFunc(p_stencilFunc_0_, p_stencilFunc_1_, p_stencilFunc_2_);
   }

   public static void stencilMask(int p_stencilMask_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._stencilMask(p_stencilMask_0_);
   }

   public static void stencilOp(int p_stencilOp_0_, int p_stencilOp_1_, int p_stencilOp_2_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._stencilOp(p_stencilOp_0_, p_stencilOp_1_, p_stencilOp_2_);
   }

   public static void clearDepth(double p_clearDepth_0_) {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager._clearDepth(p_clearDepth_0_);
   }

   public static void clearColor(float p_clearColor_0_, float p_clearColor_1_, float p_clearColor_2_, float p_clearColor_3_) {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager._clearColor(p_clearColor_0_, p_clearColor_1_, p_clearColor_2_, p_clearColor_3_);
   }

   public static void clearStencil(int p_clearStencil_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._clearStencil(p_clearStencil_0_);
   }

   public static void clear(int p_clear_0_, boolean p_clear_1_) {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager._clear(p_clear_0_, p_clear_1_);
   }

   @Deprecated
   public static void matrixMode(int p_matrixMode_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._matrixMode(p_matrixMode_0_);
   }

   @Deprecated
   public static void loadIdentity() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._loadIdentity();
   }

   @Deprecated
   public static void pushMatrix() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._pushMatrix();
   }

   @Deprecated
   public static void popMatrix() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._popMatrix();
   }

   @Deprecated
   public static void ortho(double p_ortho_0_, double p_ortho_2_, double p_ortho_4_, double p_ortho_6_, double p_ortho_8_, double p_ortho_10_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._ortho(p_ortho_0_, p_ortho_2_, p_ortho_4_, p_ortho_6_, p_ortho_8_, p_ortho_10_);
   }

   @Deprecated
   public static void rotatef(float p_rotatef_0_, float p_rotatef_1_, float p_rotatef_2_, float p_rotatef_3_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._rotatef(p_rotatef_0_, p_rotatef_1_, p_rotatef_2_, p_rotatef_3_);
   }

   @Deprecated
   public static void scalef(float p_scalef_0_, float p_scalef_1_, float p_scalef_2_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._scalef(p_scalef_0_, p_scalef_1_, p_scalef_2_);
   }

   @Deprecated
   public static void scaled(double p_scaled_0_, double p_scaled_2_, double p_scaled_4_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._scaled(p_scaled_0_, p_scaled_2_, p_scaled_4_);
   }

   @Deprecated
   public static void translatef(float p_translatef_0_, float p_translatef_1_, float p_translatef_2_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._translatef(p_translatef_0_, p_translatef_1_, p_translatef_2_);
   }

   @Deprecated
   public static void translated(double p_translated_0_, double p_translated_2_, double p_translated_4_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._translated(p_translated_0_, p_translated_2_, p_translated_4_);
   }

   @Deprecated
   public static void multMatrix(Matrix4f p_multMatrix_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._multMatrix(p_multMatrix_0_);
   }

   @Deprecated
   public static void color4f(float p_color4f_0_, float p_color4f_1_, float p_color4f_2_, float p_color4f_3_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._color4f(p_color4f_0_, p_color4f_1_, p_color4f_2_, p_color4f_3_);
   }

   @Deprecated
   public static void color3f(float p_color3f_0_, float p_color3f_1_, float p_color3f_2_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._color4f(p_color3f_0_, p_color3f_1_, p_color3f_2_, 1.0F);
   }

   @Deprecated
   public static void clearCurrentColor() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._clearCurrentColor();
   }

   public static void drawArrays(int p_drawArrays_0_, int p_drawArrays_1_, int p_drawArrays_2_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._drawArrays(p_drawArrays_0_, p_drawArrays_1_, p_drawArrays_2_);
   }

   public static void lineWidth(float p_lineWidth_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._lineWidth(p_lineWidth_0_);
   }

   public static void pixelStore(int p_pixelStore_0_, int p_pixelStore_1_) {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager._pixelStore(p_pixelStore_0_, p_pixelStore_1_);
   }

   public static void pixelTransfer(int p_pixelTransfer_0_, float p_pixelTransfer_1_) {
      GlStateManager._pixelTransfer(p_pixelTransfer_0_, p_pixelTransfer_1_);
   }

   public static void readPixels(int p_readPixels_0_, int p_readPixels_1_, int p_readPixels_2_, int p_readPixels_3_, int p_readPixels_4_, int p_readPixels_5_, ByteBuffer p_readPixels_6_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._readPixels(p_readPixels_0_, p_readPixels_1_, p_readPixels_2_, p_readPixels_3_, p_readPixels_4_, p_readPixels_5_, p_readPixels_6_);
   }

   public static void getString(int p_getString_0_, Consumer<String> p_getString_1_) {
      assertThread(RenderSystem::isOnGameThread);
      p_getString_1_.accept(GlStateManager._getString(p_getString_0_));
   }

   public static String getBackendDescription() {
      assertThread(RenderSystem::isInInitPhase);
      return String.format("LWJGL version %s", GLX._getLWJGLVersion());
   }

   public static String getApiDescription() {
      assertThread(RenderSystem::isInInitPhase);
      return GLX.getOpenGLVersionString();
   }

   public static LongSupplier initBackendSystem() {
      assertThread(RenderSystem::isInInitPhase);
      return GLX._initGlfw();
   }

   public static void initRenderer(int p_initRenderer_0_, boolean p_initRenderer_1_) {
      assertThread(RenderSystem::isInInitPhase);
      GLX._init(p_initRenderer_0_, p_initRenderer_1_);
   }

   public static void setErrorCallback(GLFWErrorCallbackI p_setErrorCallback_0_) {
      assertThread(RenderSystem::isInInitPhase);
      GLX._setGlfwErrorCallback(p_setErrorCallback_0_);
   }

   public static void renderCrosshair(int p_renderCrosshair_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GLX._renderCrosshair(p_renderCrosshair_0_, true, true, true);
   }

   public static void setupNvFogDistance() {
      assertThread(RenderSystem::isOnGameThread);
      GLX._setupNvFogDistance();
   }

   @Deprecated
   public static void glMultiTexCoord2f(int p_glMultiTexCoord2f_0_, float p_glMultiTexCoord2f_1_, float p_glMultiTexCoord2f_2_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glMultiTexCoord2f(p_glMultiTexCoord2f_0_, p_glMultiTexCoord2f_1_, p_glMultiTexCoord2f_2_);
   }

   public static String getCapsString() {
      assertThread(RenderSystem::isOnGameThread);
      return GLX._getCapsString();
   }

   public static void setupDefaultState(int p_setupDefaultState_0_, int p_setupDefaultState_1_, int p_setupDefaultState_2_, int p_setupDefaultState_3_) {
      assertThread(RenderSystem::isInInitPhase);
      GlStateManager._enableTexture();
      GlStateManager._shadeModel(7425);
      GlStateManager._clearDepth(1.0D);
      GlStateManager._enableDepthTest();
      GlStateManager._depthFunc(515);
      GlStateManager._enableAlphaTest();
      GlStateManager._alphaFunc(516, 0.1F);
      GlStateManager._matrixMode(5889);
      GlStateManager._loadIdentity();
      GlStateManager._matrixMode(5888);
      GlStateManager._viewport(p_setupDefaultState_0_, p_setupDefaultState_1_, p_setupDefaultState_2_, p_setupDefaultState_3_);
   }

   public static int maxSupportedTextureSize() {
      assertThread(RenderSystem::isInInitPhase);
      if (MAX_SUPPORTED_TEXTURE_SIZE == -1) {
         int i = GlStateManager._getInteger(3379);

         for(int j = Math.max(32768, i); j >= 1024; j >>= 1) {
            GlStateManager._texImage2D(32868, 0, 6408, j, j, 0, 6408, 5121, (IntBuffer)null);
            int k = GlStateManager._getTexLevelParameter(32868, 0, 4096);
            if (k != 0) {
               MAX_SUPPORTED_TEXTURE_SIZE = j;
               return j;
            }
         }

         MAX_SUPPORTED_TEXTURE_SIZE = Math.max(i, 1024);
         LOGGER.info("Failed to determine maximum texture size by probing, trying GL_MAX_TEXTURE_SIZE = {}", (int)MAX_SUPPORTED_TEXTURE_SIZE);
      }

      return MAX_SUPPORTED_TEXTURE_SIZE;
   }

   public static void glBindBuffer(int p_glBindBuffer_0_, Supplier<Integer> p_glBindBuffer_1_) {
      GlStateManager._glBindBuffer(p_glBindBuffer_0_, p_glBindBuffer_1_.get());
   }

   public static void glBufferData(int p_glBufferData_0_, ByteBuffer p_glBufferData_1_, int p_glBufferData_2_) {
      assertThread(RenderSystem::isOnRenderThreadOrInit);
      GlStateManager._glBufferData(p_glBufferData_0_, p_glBufferData_1_, p_glBufferData_2_);
   }

   public static void glDeleteBuffers(int p_glDeleteBuffers_0_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glDeleteBuffers(p_glDeleteBuffers_0_);
   }

   public static void glUniform1i(int p_glUniform1i_0_, int p_glUniform1i_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniform1i(p_glUniform1i_0_, p_glUniform1i_1_);
   }

   public static void glUniform1(int p_glUniform1_0_, IntBuffer p_glUniform1_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniform1(p_glUniform1_0_, p_glUniform1_1_);
   }

   public static void glUniform2(int p_glUniform2_0_, IntBuffer p_glUniform2_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniform2(p_glUniform2_0_, p_glUniform2_1_);
   }

   public static void glUniform3(int p_glUniform3_0_, IntBuffer p_glUniform3_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniform3(p_glUniform3_0_, p_glUniform3_1_);
   }

   public static void glUniform4(int p_glUniform4_0_, IntBuffer p_glUniform4_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniform4(p_glUniform4_0_, p_glUniform4_1_);
   }

   public static void glUniform1(int p_glUniform1_0_, FloatBuffer p_glUniform1_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniform1(p_glUniform1_0_, p_glUniform1_1_);
   }

   public static void glUniform2(int p_glUniform2_0_, FloatBuffer p_glUniform2_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniform2(p_glUniform2_0_, p_glUniform2_1_);
   }

   public static void glUniform3(int p_glUniform3_0_, FloatBuffer p_glUniform3_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniform3(p_glUniform3_0_, p_glUniform3_1_);
   }

   public static void glUniform4(int p_glUniform4_0_, FloatBuffer p_glUniform4_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniform4(p_glUniform4_0_, p_glUniform4_1_);
   }

   public static void glUniformMatrix2(int p_glUniformMatrix2_0_, boolean p_glUniformMatrix2_1_, FloatBuffer p_glUniformMatrix2_2_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniformMatrix2(p_glUniformMatrix2_0_, p_glUniformMatrix2_1_, p_glUniformMatrix2_2_);
   }

   public static void glUniformMatrix3(int p_glUniformMatrix3_0_, boolean p_glUniformMatrix3_1_, FloatBuffer p_glUniformMatrix3_2_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniformMatrix3(p_glUniformMatrix3_0_, p_glUniformMatrix3_1_, p_glUniformMatrix3_2_);
   }

   public static void glUniformMatrix4(int p_glUniformMatrix4_0_, boolean p_glUniformMatrix4_1_, FloatBuffer p_glUniformMatrix4_2_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniformMatrix4(p_glUniformMatrix4_0_, p_glUniformMatrix4_1_, p_glUniformMatrix4_2_);
   }

   public static void setupOutline() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.setupOutline();
   }

   public static void teardownOutline() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.teardownOutline();
   }

   public static void setupOverlayColor(IntSupplier p_setupOverlayColor_0_, int p_setupOverlayColor_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.setupOverlayColor(p_setupOverlayColor_0_.getAsInt(), p_setupOverlayColor_1_);
   }

   public static void teardownOverlayColor() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.teardownOverlayColor();
   }

   public static void setupLevelDiffuseLighting(Vector3f p_setupLevelDiffuseLighting_0_, Vector3f p_setupLevelDiffuseLighting_1_, Matrix4f p_setupLevelDiffuseLighting_2_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.setupLevelDiffuseLighting(p_setupLevelDiffuseLighting_0_, p_setupLevelDiffuseLighting_1_, p_setupLevelDiffuseLighting_2_);
   }

   public static void setupGuiFlatDiffuseLighting(Vector3f p_setupGuiFlatDiffuseLighting_0_, Vector3f p_setupGuiFlatDiffuseLighting_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.setupGuiFlatDiffuseLighting(p_setupGuiFlatDiffuseLighting_0_, p_setupGuiFlatDiffuseLighting_1_);
   }

   public static void setupGui3DDiffuseLighting(Vector3f p_setupGui3DDiffuseLighting_0_, Vector3f p_setupGui3DDiffuseLighting_1_) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.setupGui3DDiffuseLighting(p_setupGui3DDiffuseLighting_0_, p_setupGui3DDiffuseLighting_1_);
   }

   public static void mulTextureByProjModelView() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.mulTextureByProjModelView();
   }

   public static void setupEndPortalTexGen() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.setupEndPortalTexGen();
   }

   public static void clearTexGen() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.clearTexGen();
   }

   public static void beginInitialization() {
      isInInit = true;
   }

   public static void finishInitialization() {
      isInInit = false;
      if (!recordingQueue.isEmpty()) {
         replayQueue();
      }

      if (!recordingQueue.isEmpty()) {
         throw new IllegalStateException("Recorded to render queue during initialization");
      }
   }

   public static void glGenBuffers(Consumer<Integer> p_glGenBuffers_0_) {
      if (!isOnRenderThread()) {
         recordRenderCall(() -> {
            p_glGenBuffers_0_.accept(GlStateManager._glGenBuffers());
         });
      } else {
         p_glGenBuffers_0_.accept(GlStateManager._glGenBuffers());
      }

   }

   public static Tessellator renderThreadTesselator() {
      assertThread(RenderSystem::isOnRenderThread);
      return RENDER_THREAD_TESSELATOR;
   }

   public static void defaultBlendFunc() {
      blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
   }

   public static void defaultAlphaFunc() {
      alphaFunc(516, 0.1F);
   }

   @Deprecated
   public static void runAsFancy(Runnable p_runAsFancy_0_) {
      boolean flag = Minecraft.useShaderTransparency();
      if (!flag) {
         p_runAsFancy_0_.run();
      } else {
         GameSettings gamesettings = Minecraft.getInstance().options;
         GraphicsFanciness graphicsfanciness = gamesettings.graphicsMode;
         gamesettings.graphicsMode = GraphicsFanciness.FANCY;
         p_runAsFancy_0_.run();
         gamesettings.graphicsMode = graphicsfanciness;
      }
   }
}
