package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.shader.FramebufferConstants;
import net.minecraft.client.util.LWJGLMemoryUntracker;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferBlit;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class GlStateManager {
   private static final FloatBuffer MATRIX_BUFFER = GLX.make(MemoryUtil.memAllocFloat(16), (p_209238_0_) -> {
      LWJGLMemoryUntracker.untrack(MemoryUtil.memAddress(p_209238_0_));
   });
   private static final GlStateManager.AlphaState ALPHA_TEST = new GlStateManager.AlphaState();
   private static final GlStateManager.BooleanState LIGHTING = new GlStateManager.BooleanState(2896);
   private static final GlStateManager.BooleanState[] LIGHT_ENABLE = IntStream.range(0, 8).mapToObj((p_227620_0_) -> {
      return new GlStateManager.BooleanState(16384 + p_227620_0_);
   }).toArray((p_227618_0_) -> {
      return new GlStateManager.BooleanState[p_227618_0_];
   });
   private static final GlStateManager.ColorMaterialState COLOR_MATERIAL = new GlStateManager.ColorMaterialState();
   private static final GlStateManager.BlendState BLEND = new GlStateManager.BlendState();
   private static final GlStateManager.DepthState DEPTH = new GlStateManager.DepthState();
   private static final GlStateManager.FogState FOG = new GlStateManager.FogState();
   private static final GlStateManager.CullState CULL = new GlStateManager.CullState();
   private static final GlStateManager.PolygonOffsetState POLY_OFFSET = new GlStateManager.PolygonOffsetState();
   private static final GlStateManager.ColorLogicState COLOR_LOGIC = new GlStateManager.ColorLogicState();
   private static final GlStateManager.TexGenState TEX_GEN = new GlStateManager.TexGenState();
   private static final GlStateManager.StencilState STENCIL = new GlStateManager.StencilState();
   private static final GlStateManager.ScissorState SCISSOR = new GlStateManager.ScissorState();
   private static final FloatBuffer FLOAT_ARG_BUFFER = GLAllocation.createFloatBuffer(4);
   private static int activeTexture;
   private static final GlStateManager.TextureState[] TEXTURES = IntStream.range(0, 12).mapToObj((p_227616_0_) -> {
      return new GlStateManager.TextureState();
   }).toArray((p_227614_0_) -> {
      return new GlStateManager.TextureState[p_227614_0_];
   });
   private static int shadeModel = 7425;
   private static final GlStateManager.BooleanState RESCALE_NORMAL = new GlStateManager.BooleanState(32826);
   private static final GlStateManager.ColorMask COLOR_MASK = new GlStateManager.ColorMask();
   private static final GlStateManager.Color COLOR = new GlStateManager.Color();
   private static GlStateManager.FramebufferExtension fboMode;
   private static GlStateManager.SupportType fboBlitMode;

   @Deprecated
   public static void _pushLightingAttributes() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glPushAttrib(8256);
   }

   @Deprecated
   public static void _pushTextureAttributes() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glPushAttrib(270336);
   }

   @Deprecated
   public static void _popAttributes() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glPopAttrib();
   }

   @Deprecated
   public static void _disableAlphaTest() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      ALPHA_TEST.mode.disable();
   }

   @Deprecated
   public static void _enableAlphaTest() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      ALPHA_TEST.mode.enable();
   }

   @Deprecated
   public static void _alphaFunc(int p_227639_0_, float p_227639_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      if (p_227639_0_ != ALPHA_TEST.func || p_227639_1_ != ALPHA_TEST.reference) {
         ALPHA_TEST.func = p_227639_0_;
         ALPHA_TEST.reference = p_227639_1_;
         GL11.glAlphaFunc(p_227639_0_, p_227639_1_);
      }

   }

   @Deprecated
   public static void _enableLighting() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      LIGHTING.enable();
   }

   @Deprecated
   public static void _disableLighting() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      LIGHTING.disable();
   }

   @Deprecated
   public static void _enableLight(int p_227638_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      LIGHT_ENABLE[p_227638_0_].enable();
   }

   @Deprecated
   public static void _enableColorMaterial() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      COLOR_MATERIAL.enable.enable();
   }

   @Deprecated
   public static void _disableColorMaterial() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      COLOR_MATERIAL.enable.disable();
   }

   @Deprecated
   public static void _colorMaterial(int p_227641_0_, int p_227641_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (p_227641_0_ != COLOR_MATERIAL.face || p_227641_1_ != COLOR_MATERIAL.mode) {
         COLOR_MATERIAL.face = p_227641_0_;
         COLOR_MATERIAL.mode = p_227641_1_;
         GL11.glColorMaterial(p_227641_0_, p_227641_1_);
      }

   }

   @Deprecated
   public static void _light(int p_227653_0_, int p_227653_1_, FloatBuffer p_227653_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glLightfv(p_227653_0_, p_227653_1_, p_227653_2_);
   }

   @Deprecated
   public static void _lightModel(int p_227656_0_, FloatBuffer p_227656_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glLightModelfv(p_227656_0_, p_227656_1_);
   }

   @Deprecated
   public static void _normal3f(float p_227636_0_, float p_227636_1_, float p_227636_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glNormal3f(p_227636_0_, p_227636_1_, p_227636_2_);
   }

   public static void _disableScissorTest() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      SCISSOR.mode.disable();
   }

   public static void _enableScissorTest() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      SCISSOR.mode.enable();
   }

   public static void _scissorBox(int p_244592_0_, int p_244592_1_, int p_244592_2_, int p_244592_3_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL20.glScissor(p_244592_0_, p_244592_1_, p_244592_2_, p_244592_3_);
   }

   public static void _disableDepthTest() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      DEPTH.mode.disable();
   }

   public static void _enableDepthTest() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      DEPTH.mode.enable();
   }

   public static void _depthFunc(int p_227674_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      if (p_227674_0_ != DEPTH.func) {
         DEPTH.func = p_227674_0_;
         GL11.glDepthFunc(p_227674_0_);
      }

   }

   public static void _depthMask(boolean p_227667_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (p_227667_0_ != DEPTH.mask) {
         DEPTH.mask = p_227667_0_;
         GL11.glDepthMask(p_227667_0_);
      }

   }

   public static void _disableBlend() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      BLEND.mode.disable();
   }

   public static void _enableBlend() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      BLEND.mode.enable();
   }

   public static void _blendFunc(int p_227676_0_, int p_227676_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (p_227676_0_ != BLEND.srcRgb || p_227676_1_ != BLEND.dstRgb) {
         BLEND.srcRgb = p_227676_0_;
         BLEND.dstRgb = p_227676_1_;
         GL11.glBlendFunc(p_227676_0_, p_227676_1_);
      }

   }

   public static void _blendFuncSeparate(int p_227644_0_, int p_227644_1_, int p_227644_2_, int p_227644_3_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (p_227644_0_ != BLEND.srcRgb || p_227644_1_ != BLEND.dstRgb || p_227644_2_ != BLEND.srcAlpha || p_227644_3_ != BLEND.dstAlpha) {
         BLEND.srcRgb = p_227644_0_;
         BLEND.dstRgb = p_227644_1_;
         BLEND.srcAlpha = p_227644_2_;
         BLEND.dstAlpha = p_227644_3_;
         glBlendFuncSeparate(p_227644_0_, p_227644_1_, p_227644_2_, p_227644_3_);
      }

   }

   public static void _blendColor(float p_227637_0_, float p_227637_1_, float p_227637_2_, float p_227637_3_) {
      GL14.glBlendColor(p_227637_0_, p_227637_1_, p_227637_2_, p_227637_3_);
   }

   public static void _blendEquation(int p_227690_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL14.glBlendEquation(p_227690_0_);
   }

   public static String _init_fbo(GLCapabilities p_227666_0_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      if (p_227666_0_.OpenGL30) {
         fboBlitMode = GlStateManager.SupportType.BASE;
      } else if (p_227666_0_.GL_EXT_framebuffer_blit) {
         fboBlitMode = GlStateManager.SupportType.EXT;
      } else {
         fboBlitMode = GlStateManager.SupportType.NONE;
      }

      if (p_227666_0_.OpenGL30) {
         fboMode = GlStateManager.FramebufferExtension.BASE;
         FramebufferConstants.GL_FRAMEBUFFER = 36160;
         FramebufferConstants.GL_RENDERBUFFER = 36161;
         FramebufferConstants.GL_COLOR_ATTACHMENT0 = 36064;
         FramebufferConstants.GL_DEPTH_ATTACHMENT = 36096;
         FramebufferConstants.GL_FRAMEBUFFER_COMPLETE = 36053;
         FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
         FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
         FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
         FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
         return "OpenGL 3.0";
      } else if (p_227666_0_.GL_ARB_framebuffer_object) {
         fboMode = GlStateManager.FramebufferExtension.ARB;
         FramebufferConstants.GL_FRAMEBUFFER = 36160;
         FramebufferConstants.GL_RENDERBUFFER = 36161;
         FramebufferConstants.GL_COLOR_ATTACHMENT0 = 36064;
         FramebufferConstants.GL_DEPTH_ATTACHMENT = 36096;
         FramebufferConstants.GL_FRAMEBUFFER_COMPLETE = 36053;
         FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
         FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
         FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
         FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
         return "ARB_framebuffer_object extension";
      } else if (p_227666_0_.GL_EXT_framebuffer_object) {
         fboMode = GlStateManager.FramebufferExtension.EXT;
         FramebufferConstants.GL_FRAMEBUFFER = 36160;
         FramebufferConstants.GL_RENDERBUFFER = 36161;
         FramebufferConstants.GL_COLOR_ATTACHMENT0 = 36064;
         FramebufferConstants.GL_DEPTH_ATTACHMENT = 36096;
         FramebufferConstants.GL_FRAMEBUFFER_COMPLETE = 36053;
         FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
         FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
         FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
         FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
         return "EXT_framebuffer_object extension";
      } else {
         throw new IllegalStateException("Could not initialize framebuffer support.");
      }
   }

   public static int glGetProgrami(int p_227691_0_, int p_227691_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GL20.glGetProgrami(p_227691_0_, p_227691_1_);
   }

   public static void glAttachShader(int p_227704_0_, int p_227704_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glAttachShader(p_227704_0_, p_227704_1_);
   }

   public static void glDeleteShader(int p_227703_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glDeleteShader(p_227703_0_);
   }

   public static int glCreateShader(int p_227711_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GL20.glCreateShader(p_227711_0_);
   }

   public static void glShaderSource(int p_227654_0_, CharSequence p_227654_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glShaderSource(p_227654_0_, p_227654_1_);
   }

   public static void glCompileShader(int p_227717_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glCompileShader(p_227717_0_);
   }

   public static int glGetShaderi(int p_227712_0_, int p_227712_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GL20.glGetShaderi(p_227712_0_, p_227712_1_);
   }

   public static void _glUseProgram(int p_227723_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUseProgram(p_227723_0_);
   }

   public static int glCreateProgram() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GL20.glCreateProgram();
   }

   public static void glDeleteProgram(int p_227726_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glDeleteProgram(p_227726_0_);
   }

   public static void glLinkProgram(int p_227729_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glLinkProgram(p_227729_0_);
   }

   public static int _glGetUniformLocation(int p_227680_0_, CharSequence p_227680_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GL20.glGetUniformLocation(p_227680_0_, p_227680_1_);
   }

   public static void _glUniform1(int p_227657_0_, IntBuffer p_227657_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniform1iv(p_227657_0_, p_227657_1_);
   }

   public static void _glUniform1i(int p_227718_0_, int p_227718_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniform1i(p_227718_0_, p_227718_1_);
   }

   public static void _glUniform1(int p_227681_0_, FloatBuffer p_227681_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniform1fv(p_227681_0_, p_227681_1_);
   }

   public static void _glUniform2(int p_227682_0_, IntBuffer p_227682_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniform2iv(p_227682_0_, p_227682_1_);
   }

   public static void _glUniform2(int p_227696_0_, FloatBuffer p_227696_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniform2fv(p_227696_0_, p_227696_1_);
   }

   public static void _glUniform3(int p_227697_0_, IntBuffer p_227697_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniform3iv(p_227697_0_, p_227697_1_);
   }

   public static void _glUniform3(int p_227707_0_, FloatBuffer p_227707_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniform3fv(p_227707_0_, p_227707_1_);
   }

   public static void _glUniform4(int p_227708_0_, IntBuffer p_227708_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniform4iv(p_227708_0_, p_227708_1_);
   }

   public static void _glUniform4(int p_227715_0_, FloatBuffer p_227715_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniform4fv(p_227715_0_, p_227715_1_);
   }

   public static void _glUniformMatrix2(int p_227659_0_, boolean p_227659_1_, FloatBuffer p_227659_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniformMatrix2fv(p_227659_0_, p_227659_1_, p_227659_2_);
   }

   public static void _glUniformMatrix3(int p_227683_0_, boolean p_227683_1_, FloatBuffer p_227683_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniformMatrix3fv(p_227683_0_, p_227683_1_, p_227683_2_);
   }

   public static void _glUniformMatrix4(int p_227698_0_, boolean p_227698_1_, FloatBuffer p_227698_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniformMatrix4fv(p_227698_0_, p_227698_1_, p_227698_2_);
   }

   public static int _glGetAttribLocation(int p_227695_0_, CharSequence p_227695_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GL20.glGetAttribLocation(p_227695_0_, p_227695_1_);
   }

   public static int _glGenBuffers() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      return GL15.glGenBuffers();
   }

   public static void _glBindBuffer(int p_227724_0_, int p_227724_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL15.glBindBuffer(p_227724_0_, p_227724_1_);
   }

   public static void _glBufferData(int p_227655_0_, ByteBuffer p_227655_1_, int p_227655_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL15.glBufferData(p_227655_0_, p_227655_1_, p_227655_2_);
   }

   public static void _glDeleteBuffers(int p_227732_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL15.glDeleteBuffers(p_227732_0_);
   }

   public static void _glCopyTexSubImage2D(int p_237509_0_, int p_237509_1_, int p_237509_2_, int p_237509_3_, int p_237509_4_, int p_237509_5_, int p_237509_6_, int p_237509_7_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL20.glCopyTexSubImage2D(p_237509_0_, p_237509_1_, p_237509_2_, p_237509_3_, p_237509_4_, p_237509_5_, p_237509_6_, p_237509_7_);
   }

   public static void _glBindFramebuffer(int p_227727_0_, int p_227727_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      switch(fboMode) {
      case BASE:
         GL30.glBindFramebuffer(p_227727_0_, p_227727_1_);
         break;
      case ARB:
         ARBFramebufferObject.glBindFramebuffer(p_227727_0_, p_227727_1_);
         break;
      case EXT:
         EXTFramebufferObject.glBindFramebufferEXT(p_227727_0_, p_227727_1_);
      }

   }

   public static int getFramebufferDepthTexture() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      switch(fboMode) {
      case BASE:
         if (GL30.glGetFramebufferAttachmentParameteri(36160, 36096, 36048) == 5890) {
            return GL30.glGetFramebufferAttachmentParameteri(36160, 36096, 36049);
         }
         break;
      case ARB:
         if (ARBFramebufferObject.glGetFramebufferAttachmentParameteri(36160, 36096, 36048) == 5890) {
            return ARBFramebufferObject.glGetFramebufferAttachmentParameteri(36160, 36096, 36049);
         }
         break;
      case EXT:
         if (EXTFramebufferObject.glGetFramebufferAttachmentParameteriEXT(36160, 36096, 36048) == 5890) {
            return EXTFramebufferObject.glGetFramebufferAttachmentParameteriEXT(36160, 36096, 36049);
         }
      }

      return 0;
   }

   public static void _glBlitFrameBuffer(int p_237510_0_, int p_237510_1_, int p_237510_2_, int p_237510_3_, int p_237510_4_, int p_237510_5_, int p_237510_6_, int p_237510_7_, int p_237510_8_, int p_237510_9_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      switch(fboBlitMode) {
      case BASE:
         GL30.glBlitFramebuffer(p_237510_0_, p_237510_1_, p_237510_2_, p_237510_3_, p_237510_4_, p_237510_5_, p_237510_6_, p_237510_7_, p_237510_8_, p_237510_9_);
         break;
      case EXT:
         EXTFramebufferBlit.glBlitFramebufferEXT(p_237510_0_, p_237510_1_, p_237510_2_, p_237510_3_, p_237510_4_, p_237510_5_, p_237510_6_, p_237510_7_, p_237510_8_, p_237510_9_);
      case NONE:
      }

   }

   public static void _glDeleteFramebuffers(int p_227738_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      switch(fboMode) {
      case BASE:
         GL30.glDeleteFramebuffers(p_227738_0_);
         break;
      case ARB:
         ARBFramebufferObject.glDeleteFramebuffers(p_227738_0_);
         break;
      case EXT:
         EXTFramebufferObject.glDeleteFramebuffersEXT(p_227738_0_);
      }

   }

   public static int glGenFramebuffers() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      switch(fboMode) {
      case BASE:
         return GL30.glGenFramebuffers();
      case ARB:
         return ARBFramebufferObject.glGenFramebuffers();
      case EXT:
         return EXTFramebufferObject.glGenFramebuffersEXT();
      default:
         return -1;
      }
   }

   public static int glCheckFramebufferStatus(int p_227741_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      switch(fboMode) {
      case BASE:
         return GL30.glCheckFramebufferStatus(p_227741_0_);
      case ARB:
         return ARBFramebufferObject.glCheckFramebufferStatus(p_227741_0_);
      case EXT:
         return EXTFramebufferObject.glCheckFramebufferStatusEXT(p_227741_0_);
      default:
         return -1;
      }
   }

   public static void _glFramebufferTexture2D(int p_227645_0_, int p_227645_1_, int p_227645_2_, int p_227645_3_, int p_227645_4_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      switch(fboMode) {
      case BASE:
         GL30.glFramebufferTexture2D(p_227645_0_, p_227645_1_, p_227645_2_, p_227645_3_, p_227645_4_);
         break;
      case ARB:
         ARBFramebufferObject.glFramebufferTexture2D(p_227645_0_, p_227645_1_, p_227645_2_, p_227645_3_, p_227645_4_);
         break;
      case EXT:
         EXTFramebufferObject.glFramebufferTexture2DEXT(p_227645_0_, p_227645_1_, p_227645_2_, p_227645_3_, p_227645_4_);
      }

   }

   @Deprecated
   public static int getActiveTextureName() {
      return TEXTURES[activeTexture].binding;
   }

   public static void glActiveTexture(int p_227744_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL13.glActiveTexture(p_227744_0_);
   }

   @Deprecated
   public static void _glClientActiveTexture(int p_227747_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL13.glClientActiveTexture(p_227747_0_);
   }

   /* Stores the last values sent into glMultiTexCoord2f */
   public static float lastBrightnessX = 0.0f;
   public static float lastBrightnessY = 0.0f;
   @Deprecated
   public static void _glMultiTexCoord2f(int p_227640_0_, float p_227640_1_, float p_227640_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL13.glMultiTexCoord2f(p_227640_0_, p_227640_1_, p_227640_2_);
      if (p_227640_0_ == GL13.GL_TEXTURE1) {
          lastBrightnessX = p_227640_1_;
          lastBrightnessY = p_227640_2_;
       }
   }

   public static void glBlendFuncSeparate(int p_227706_0_, int p_227706_1_, int p_227706_2_, int p_227706_3_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL14.glBlendFuncSeparate(p_227706_0_, p_227706_1_, p_227706_2_, p_227706_3_);
   }

   public static String glGetShaderInfoLog(int p_227733_0_, int p_227733_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GL20.glGetShaderInfoLog(p_227733_0_, p_227733_1_);
   }

   public static String glGetProgramInfoLog(int p_227736_0_, int p_227736_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GL20.glGetProgramInfoLog(p_227736_0_, p_227736_1_);
   }

   public static void setupOutline() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      _texEnv(8960, 8704, 34160);
      color1arg(7681, 34168);
   }

   public static void teardownOutline() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      _texEnv(8960, 8704, 8448);
      color3arg(8448, 5890, 34168, 34166);
   }

   public static void setupOverlayColor(int p_227739_0_, int p_227739_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      _activeTexture(33985);
      _enableTexture();
      _matrixMode(5890);
      _loadIdentity();
      float f = 1.0F / (float)(p_227739_1_ - 1);
      _scalef(f, f, f);
      _matrixMode(5888);
      _bindTexture(p_227739_0_);
      _texParameter(3553, 10241, 9728);
      _texParameter(3553, 10240, 9728);
      _texParameter(3553, 10242, 10496);
      _texParameter(3553, 10243, 10496);
      _texEnv(8960, 8704, 34160);
      color3arg(34165, 34168, 5890, 5890);
      alpha1arg(7681, 34168);
      _activeTexture(33984);
   }

   public static void teardownOverlayColor() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      _activeTexture(33985);
      _disableTexture();
      _activeTexture(33984);
   }

   private static void color1arg(int p_227751_0_, int p_227751_1_) {
      _texEnv(8960, 34161, p_227751_0_);
      _texEnv(8960, 34176, p_227751_1_);
      _texEnv(8960, 34192, 768);
   }

   private static void color3arg(int p_227720_0_, int p_227720_1_, int p_227720_2_, int p_227720_3_) {
      _texEnv(8960, 34161, p_227720_0_);
      _texEnv(8960, 34176, p_227720_1_);
      _texEnv(8960, 34192, 768);
      _texEnv(8960, 34177, p_227720_2_);
      _texEnv(8960, 34193, 768);
      _texEnv(8960, 34178, p_227720_3_);
      _texEnv(8960, 34194, 770);
   }

   private static void alpha1arg(int p_227754_0_, int p_227754_1_) {
      _texEnv(8960, 34162, p_227754_0_);
      _texEnv(8960, 34184, p_227754_1_);
      _texEnv(8960, 34200, 770);
   }

   public static void setupLevelDiffuseLighting(Vector3f p_237512_0_, Vector3f p_237512_1_, Matrix4f p_237512_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      _pushMatrix();
      _loadIdentity();
      _enableLight(0);
      _enableLight(1);
      Vector4f vector4f = new Vector4f(p_237512_0_);
      vector4f.transform(p_237512_2_);
      _light(16384, 4611, getBuffer(vector4f.x(), vector4f.y(), vector4f.z(), 0.0F));
      float f = 0.6F;
      _light(16384, 4609, getBuffer(0.6F, 0.6F, 0.6F, 1.0F));
      _light(16384, 4608, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      _light(16384, 4610, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      Vector4f vector4f1 = new Vector4f(p_237512_1_);
      vector4f1.transform(p_237512_2_);
      _light(16385, 4611, getBuffer(vector4f1.x(), vector4f1.y(), vector4f1.z(), 0.0F));
      _light(16385, 4609, getBuffer(0.6F, 0.6F, 0.6F, 1.0F));
      _light(16385, 4608, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      _light(16385, 4610, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      _shadeModel(7424);
      float f1 = 0.4F;
      _lightModel(2899, getBuffer(0.4F, 0.4F, 0.4F, 1.0F));
      _popMatrix();
   }

   public static void setupGuiFlatDiffuseLighting(Vector3f p_237511_0_, Vector3f p_237511_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.setIdentity();
      matrix4f.multiply(Matrix4f.createScaleMatrix(1.0F, -1.0F, 1.0F));
      matrix4f.multiply(Vector3f.YP.rotationDegrees(-22.5F));
      matrix4f.multiply(Vector3f.XP.rotationDegrees(135.0F));
      setupLevelDiffuseLighting(p_237511_0_, p_237511_1_, matrix4f);
   }

   public static void setupGui3DDiffuseLighting(Vector3f p_237513_0_, Vector3f p_237513_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.setIdentity();
      matrix4f.multiply(Vector3f.YP.rotationDegrees(62.0F));
      matrix4f.multiply(Vector3f.XP.rotationDegrees(185.5F));
      matrix4f.multiply(Matrix4f.createScaleMatrix(1.0F, -1.0F, 1.0F));
      matrix4f.multiply(Vector3f.YP.rotationDegrees(-22.5F));
      matrix4f.multiply(Vector3f.XP.rotationDegrees(135.0F));
      setupLevelDiffuseLighting(p_237513_0_, p_237513_1_, matrix4f);
   }

   private static FloatBuffer getBuffer(float p_227710_0_, float p_227710_1_, float p_227710_2_, float p_227710_3_) {
      ((Buffer)FLOAT_ARG_BUFFER).clear();
      FLOAT_ARG_BUFFER.put(p_227710_0_).put(p_227710_1_).put(p_227710_2_).put(p_227710_3_);
      ((Buffer)FLOAT_ARG_BUFFER).flip();
      return FLOAT_ARG_BUFFER;
   }

   public static void setupEndPortalTexGen() {
      _texGenMode(GlStateManager.TexGen.S, 9216);
      _texGenMode(GlStateManager.TexGen.T, 9216);
      _texGenMode(GlStateManager.TexGen.R, 9216);
      _texGenParam(GlStateManager.TexGen.S, 9474, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
      _texGenParam(GlStateManager.TexGen.T, 9474, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
      _texGenParam(GlStateManager.TexGen.R, 9474, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
      _enableTexGen(GlStateManager.TexGen.S);
      _enableTexGen(GlStateManager.TexGen.T);
      _enableTexGen(GlStateManager.TexGen.R);
   }

   public static void clearTexGen() {
      _disableTexGen(GlStateManager.TexGen.S);
      _disableTexGen(GlStateManager.TexGen.T);
      _disableTexGen(GlStateManager.TexGen.R);
   }

   public static void mulTextureByProjModelView() {
      _getMatrix(2983, MATRIX_BUFFER);
      _multMatrix(MATRIX_BUFFER);
      _getMatrix(2982, MATRIX_BUFFER);
      _multMatrix(MATRIX_BUFFER);
   }

   @Deprecated
   public static void _enableFog() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      FOG.enable.enable();
   }

   @Deprecated
   public static void _disableFog() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      FOG.enable.disable();
   }

   @Deprecated
   public static void _fogMode(int p_227750_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (p_227750_0_ != FOG.mode) {
         FOG.mode = p_227750_0_;
         _fogi(2917, p_227750_0_);
      }

   }

   @Deprecated
   public static void _fogDensity(float p_227634_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (p_227634_0_ != FOG.density) {
         FOG.density = p_227634_0_;
         GL11.glFogf(2914, p_227634_0_);
      }

   }

   @Deprecated
   public static void _fogStart(float p_227671_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (p_227671_0_ != FOG.start) {
         FOG.start = p_227671_0_;
         GL11.glFogf(2915, p_227671_0_);
      }

   }

   @Deprecated
   public static void _fogEnd(float p_227687_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (p_227687_0_ != FOG.end) {
         FOG.end = p_227687_0_;
         GL11.glFogf(2916, p_227687_0_);
      }

   }

   @Deprecated
   public static void _fog(int p_227660_0_, float[] p_227660_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glFogfv(p_227660_0_, p_227660_1_);
   }

   @Deprecated
   public static void _fogi(int p_227742_0_, int p_227742_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glFogi(p_227742_0_, p_227742_1_);
   }

   public static void _enableCull() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      CULL.enable.enable();
   }

   public static void _disableCull() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      CULL.enable.disable();
   }

   public static void _polygonMode(int p_227745_0_, int p_227745_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glPolygonMode(p_227745_0_, p_227745_1_);
   }

   public static void _enablePolygonOffset() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      POLY_OFFSET.fill.enable();
   }

   public static void _disablePolygonOffset() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      POLY_OFFSET.fill.disable();
   }

   public static void _enableLineOffset() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      POLY_OFFSET.line.enable();
   }

   public static void _disableLineOffset() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      POLY_OFFSET.line.disable();
   }

   public static void _polygonOffset(float p_227635_0_, float p_227635_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (p_227635_0_ != POLY_OFFSET.factor || p_227635_1_ != POLY_OFFSET.units) {
         POLY_OFFSET.factor = p_227635_0_;
         POLY_OFFSET.units = p_227635_1_;
         GL11.glPolygonOffset(p_227635_0_, p_227635_1_);
      }

   }

   public static void _enableColorLogicOp() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      COLOR_LOGIC.enable.enable();
   }

   public static void _disableColorLogicOp() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      COLOR_LOGIC.enable.disable();
   }

   public static void _logicOp(int p_227753_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (p_227753_0_ != COLOR_LOGIC.op) {
         COLOR_LOGIC.op = p_227753_0_;
         GL11.glLogicOp(p_227753_0_);
      }

   }

   @Deprecated
   public static void _enableTexGen(GlStateManager.TexGen p_227662_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      getTexGen(p_227662_0_).enable.enable();
   }

   @Deprecated
   public static void _disableTexGen(GlStateManager.TexGen p_227685_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      getTexGen(p_227685_0_).enable.disable();
   }

   @Deprecated
   public static void _texGenMode(GlStateManager.TexGen p_227663_0_, int p_227663_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GlStateManager.TexGenCoord glstatemanager$texgencoord = getTexGen(p_227663_0_);
      if (p_227663_1_ != glstatemanager$texgencoord.mode) {
         glstatemanager$texgencoord.mode = p_227663_1_;
         GL11.glTexGeni(glstatemanager$texgencoord.coord, 9472, p_227663_1_);
      }

   }

   @Deprecated
   public static void _texGenParam(GlStateManager.TexGen p_227664_0_, int p_227664_1_, FloatBuffer p_227664_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glTexGenfv(getTexGen(p_227664_0_).coord, p_227664_1_, p_227664_2_);
   }

   @Deprecated
   private static GlStateManager.TexGenCoord getTexGen(GlStateManager.TexGen p_225677_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      switch(p_225677_0_) {
      case S:
         return TEX_GEN.s;
      case T:
         return TEX_GEN.t;
      case R:
         return TEX_GEN.r;
      case Q:
         return TEX_GEN.q;
      default:
         return TEX_GEN.s;
      }
   }

   public static void _activeTexture(int p_227756_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (activeTexture != p_227756_0_ - '\u84c0') {
         activeTexture = p_227756_0_ - '\u84c0';
         glActiveTexture(p_227756_0_);
      }

   }

   public static void _enableTexture() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      TEXTURES[activeTexture].enable.enable();
   }

   public static void _disableTexture() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      TEXTURES[activeTexture].enable.disable();
   }

   @Deprecated
   public static void _texEnv(int p_227643_0_, int p_227643_1_, int p_227643_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glTexEnvi(p_227643_0_, p_227643_1_, p_227643_2_);
   }

   public static void _texParameter(int p_227642_0_, int p_227642_1_, float p_227642_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glTexParameterf(p_227642_0_, p_227642_1_, p_227642_2_);
   }

   public static void _texParameter(int p_227677_0_, int p_227677_1_, int p_227677_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glTexParameteri(p_227677_0_, p_227677_1_, p_227677_2_);
   }

   public static int _getTexLevelParameter(int p_227692_0_, int p_227692_1_, int p_227692_2_) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      return GL11.glGetTexLevelParameteri(p_227692_0_, p_227692_1_, p_227692_2_);
   }

   public static int _genTexture() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      return GL11.glGenTextures();
   }

   public static void _genTextures(int[] p_242998_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glGenTextures(p_242998_0_);
   }

   public static void _deleteTexture(int p_227758_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glDeleteTextures(p_227758_0_);

      for(GlStateManager.TextureState glstatemanager$texturestate : TEXTURES) {
         if (glstatemanager$texturestate.binding == p_227758_0_) {
            glstatemanager$texturestate.binding = -1;
         }
      }

   }

   public static void _deleteTextures(int[] p_242999_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);

      for(GlStateManager.TextureState glstatemanager$texturestate : TEXTURES) {
         for(int i : p_242999_0_) {
            if (glstatemanager$texturestate.binding == i) {
               glstatemanager$texturestate.binding = -1;
            }
         }
      }

      GL11.glDeleteTextures(p_242999_0_);
   }

   public static void _bindTexture(int p_227760_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      if (p_227760_0_ != TEXTURES[activeTexture].binding) {
         TEXTURES[activeTexture].binding = p_227760_0_;
         GL11.glBindTexture(3553, p_227760_0_);
      }

   }

   public static void _texImage2D(int p_227647_0_, int p_227647_1_, int p_227647_2_, int p_227647_3_, int p_227647_4_, int p_227647_5_, int p_227647_6_, int p_227647_7_, @Nullable IntBuffer p_227647_8_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glTexImage2D(p_227647_0_, p_227647_1_, p_227647_2_, p_227647_3_, p_227647_4_, p_227647_5_, p_227647_6_, p_227647_7_, p_227647_8_);
   }

   public static void _texSubImage2D(int p_227646_0_, int p_227646_1_, int p_227646_2_, int p_227646_3_, int p_227646_4_, int p_227646_5_, int p_227646_6_, int p_227646_7_, long p_227646_8_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glTexSubImage2D(p_227646_0_, p_227646_1_, p_227646_2_, p_227646_3_, p_227646_4_, p_227646_5_, p_227646_6_, p_227646_7_, p_227646_8_);
   }

   public static void _getTexImage(int p_227649_0_, int p_227649_1_, int p_227649_2_, int p_227649_3_, long p_227649_4_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glGetTexImage(p_227649_0_, p_227649_1_, p_227649_2_, p_227649_3_, p_227649_4_);
   }

   @Deprecated
   public static void _shadeModel(int p_227762_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      if (p_227762_0_ != shadeModel) {
         shadeModel = p_227762_0_;
         GL11.glShadeModel(p_227762_0_);
      }

   }

   @Deprecated
   public static void _enableRescaleNormal() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      RESCALE_NORMAL.enable();
   }

   @Deprecated
   public static void _disableRescaleNormal() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      RESCALE_NORMAL.disable();
   }

   public static void _viewport(int p_227714_0_, int p_227714_1_, int p_227714_2_, int p_227714_3_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GlStateManager.Viewport.INSTANCE.x = p_227714_0_;
      GlStateManager.Viewport.INSTANCE.y = p_227714_1_;
      GlStateManager.Viewport.INSTANCE.width = p_227714_2_;
      GlStateManager.Viewport.INSTANCE.height = p_227714_3_;
      GL11.glViewport(p_227714_0_, p_227714_1_, p_227714_2_, p_227714_3_);
   }

   public static void _colorMask(boolean p_227668_0_, boolean p_227668_1_, boolean p_227668_2_, boolean p_227668_3_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (p_227668_0_ != COLOR_MASK.red || p_227668_1_ != COLOR_MASK.green || p_227668_2_ != COLOR_MASK.blue || p_227668_3_ != COLOR_MASK.alpha) {
         COLOR_MASK.red = p_227668_0_;
         COLOR_MASK.green = p_227668_1_;
         COLOR_MASK.blue = p_227668_2_;
         COLOR_MASK.alpha = p_227668_3_;
         GL11.glColorMask(p_227668_0_, p_227668_1_, p_227668_2_, p_227668_3_);
      }

   }

   public static void _stencilFunc(int p_227705_0_, int p_227705_1_, int p_227705_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (p_227705_0_ != STENCIL.func.func || p_227705_0_ != STENCIL.func.ref || p_227705_0_ != STENCIL.func.mask) {
         STENCIL.func.func = p_227705_0_;
         STENCIL.func.ref = p_227705_1_;
         STENCIL.func.mask = p_227705_2_;
         GL11.glStencilFunc(p_227705_0_, p_227705_1_, p_227705_2_);
      }

   }

   public static void _stencilMask(int p_227764_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (p_227764_0_ != STENCIL.mask) {
         STENCIL.mask = p_227764_0_;
         GL11.glStencilMask(p_227764_0_);
      }

   }

   public static void _stencilOp(int p_227713_0_, int p_227713_1_, int p_227713_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (p_227713_0_ != STENCIL.fail || p_227713_1_ != STENCIL.zfail || p_227713_2_ != STENCIL.zpass) {
         STENCIL.fail = p_227713_0_;
         STENCIL.zfail = p_227713_1_;
         STENCIL.zpass = p_227713_2_;
         GL11.glStencilOp(p_227713_0_, p_227713_1_, p_227713_2_);
      }

   }

   public static void _clearDepth(double p_227631_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glClearDepth(p_227631_0_);
   }

   public static void _clearColor(float p_227673_0_, float p_227673_1_, float p_227673_2_, float p_227673_3_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glClearColor(p_227673_0_, p_227673_1_, p_227673_2_, p_227673_3_);
   }

   public static void _clearStencil(int p_227766_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glClearStencil(p_227766_0_);
   }

   public static void _clear(int p_227658_0_, boolean p_227658_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glClear(p_227658_0_);
      if (p_227658_1_) {
         _getError();
      }

   }

   @Deprecated
   public static void _matrixMode(int p_227768_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glMatrixMode(p_227768_0_);
   }

   @Deprecated
   public static void _loadIdentity() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glLoadIdentity();
   }

   @Deprecated
   public static void _pushMatrix() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glPushMatrix();
   }

   @Deprecated
   public static void _popMatrix() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glPopMatrix();
   }

   @Deprecated
   public static void _getMatrix(int p_227721_0_, FloatBuffer p_227721_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glGetFloatv(p_227721_0_, p_227721_1_);
   }

   @Deprecated
   public static void _ortho(double p_227633_0_, double p_227633_2_, double p_227633_4_, double p_227633_6_, double p_227633_8_, double p_227633_10_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glOrtho(p_227633_0_, p_227633_2_, p_227633_4_, p_227633_6_, p_227633_8_, p_227633_10_);
   }

   @Deprecated
   public static void _rotatef(float p_227689_0_, float p_227689_1_, float p_227689_2_, float p_227689_3_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glRotatef(p_227689_0_, p_227689_1_, p_227689_2_, p_227689_3_);
   }

   @Deprecated
   public static void _scalef(float p_227672_0_, float p_227672_1_, float p_227672_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glScalef(p_227672_0_, p_227672_1_, p_227672_2_);
   }

   @Deprecated
   public static void _scaled(double p_227632_0_, double p_227632_2_, double p_227632_4_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glScaled(p_227632_0_, p_227632_2_, p_227632_4_);
   }

   @Deprecated
   public static void _translatef(float p_227688_0_, float p_227688_1_, float p_227688_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glTranslatef(p_227688_0_, p_227688_1_, p_227688_2_);
   }

   @Deprecated
   public static void _translated(double p_227670_0_, double p_227670_2_, double p_227670_4_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glTranslated(p_227670_0_, p_227670_2_, p_227670_4_);
   }

   @Deprecated
   public static void _multMatrix(FloatBuffer p_227665_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glMultMatrixf(p_227665_0_);
   }

   @Deprecated
   public static void _multMatrix(Matrix4f p_227699_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      p_227699_0_.store(MATRIX_BUFFER);
      ((Buffer)MATRIX_BUFFER).rewind();
      _multMatrix(MATRIX_BUFFER);
   }

   @Deprecated
   public static void _color4f(float p_227702_0_, float p_227702_1_, float p_227702_2_, float p_227702_3_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (p_227702_0_ != COLOR.r || p_227702_1_ != COLOR.g || p_227702_2_ != COLOR.b || p_227702_3_ != COLOR.a) {
         COLOR.r = p_227702_0_;
         COLOR.g = p_227702_1_;
         COLOR.b = p_227702_2_;
         COLOR.a = p_227702_3_;
         GL11.glColor4f(p_227702_0_, p_227702_1_, p_227702_2_, p_227702_3_);
      }

   }

   @Deprecated
   public static void _clearCurrentColor() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      COLOR.r = -1.0F;
      COLOR.g = -1.0F;
      COLOR.b = -1.0F;
      COLOR.a = -1.0F;
   }

   @Deprecated
   public static void _normalPointer(int p_227652_0_, int p_227652_1_, long p_227652_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glNormalPointer(p_227652_0_, p_227652_1_, p_227652_2_);
   }

   @Deprecated
   public static void _texCoordPointer(int p_227650_0_, int p_227650_1_, int p_227650_2_, long p_227650_3_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glTexCoordPointer(p_227650_0_, p_227650_1_, p_227650_2_, p_227650_3_);
   }

   @Deprecated
   public static void _vertexPointer(int p_227679_0_, int p_227679_1_, int p_227679_2_, long p_227679_3_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glVertexPointer(p_227679_0_, p_227679_1_, p_227679_2_, p_227679_3_);
   }

   @Deprecated
   public static void _colorPointer(int p_227694_0_, int p_227694_1_, int p_227694_2_, long p_227694_3_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glColorPointer(p_227694_0_, p_227694_1_, p_227694_2_, p_227694_3_);
   }

   public static void _vertexAttribPointer(int p_227651_0_, int p_227651_1_, int p_227651_2_, boolean p_227651_3_, int p_227651_4_, long p_227651_5_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glVertexAttribPointer(p_227651_0_, p_227651_1_, p_227651_2_, p_227651_3_, p_227651_4_, p_227651_5_);
   }

   @Deprecated
   public static void _enableClientState(int p_227770_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glEnableClientState(p_227770_0_);
   }

   @Deprecated
   public static void _disableClientState(int p_227772_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glDisableClientState(p_227772_0_);
   }

   public static void _enableVertexAttribArray(int p_227606_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glEnableVertexAttribArray(p_227606_0_);
   }

   public static void _disableVertexAttribArray(int p_227608_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glEnableVertexAttribArray(p_227608_0_);
   }

   public static void _drawArrays(int p_227719_0_, int p_227719_1_, int p_227719_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glDrawArrays(p_227719_0_, p_227719_1_, p_227719_2_);
   }

   public static void _lineWidth(float p_227701_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glLineWidth(p_227701_0_);
   }

   public static void _pixelStore(int p_227748_0_, int p_227748_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glPixelStorei(p_227748_0_, p_227748_1_);
   }

   public static void _pixelTransfer(int p_227675_0_, float p_227675_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glPixelTransferf(p_227675_0_, p_227675_1_);
   }

   public static void _readPixels(int p_227648_0_, int p_227648_1_, int p_227648_2_, int p_227648_3_, int p_227648_4_, int p_227648_5_, ByteBuffer p_227648_6_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glReadPixels(p_227648_0_, p_227648_1_, p_227648_2_, p_227648_3_, p_227648_4_, p_227648_5_, p_227648_6_);
   }

   public static int _getError() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GL11.glGetError();
   }

   public static String _getString(int p_227610_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GL11.glGetString(p_227610_0_);
   }

   public static int _getInteger(int p_227612_0_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      return GL11.glGetInteger(p_227612_0_);
   }

   public static boolean supportsFramebufferBlit() {
      return fboBlitMode != GlStateManager.SupportType.NONE;
   }

   @Deprecated
   @OnlyIn(Dist.CLIENT)
   static class AlphaState {
      public final GlStateManager.BooleanState mode = new GlStateManager.BooleanState(3008);
      public int func = 519;
      public float reference = -1.0F;

      private AlphaState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class BlendState {
      public final GlStateManager.BooleanState mode = new GlStateManager.BooleanState(3042);
      public int srcRgb = 1;
      public int dstRgb = 0;
      public int srcAlpha = 1;
      public int dstAlpha = 0;

      private BlendState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class BooleanState {
      private final int state;
      private boolean enabled;

      public BooleanState(int p_i50871_1_) {
         this.state = p_i50871_1_;
      }

      public void disable() {
         this.setEnabled(false);
      }

      public void enable() {
         this.setEnabled(true);
      }

      public void setEnabled(boolean p_179199_1_) {
         RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
         if (p_179199_1_ != this.enabled) {
            this.enabled = p_179199_1_;
            if (p_179199_1_) {
               GL11.glEnable(this.state);
            } else {
               GL11.glDisable(this.state);
            }
         }

      }
   }

   @Deprecated
   @OnlyIn(Dist.CLIENT)
   static class Color {
      public float r = 1.0F;
      public float g = 1.0F;
      public float b = 1.0F;
      public float a = 1.0F;

      public Color() {
         this(1.0F, 1.0F, 1.0F, 1.0F);
      }

      public Color(float p_i50869_1_, float p_i50869_2_, float p_i50869_3_, float p_i50869_4_) {
         this.r = p_i50869_1_;
         this.g = p_i50869_2_;
         this.b = p_i50869_3_;
         this.a = p_i50869_4_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class ColorLogicState {
      public final GlStateManager.BooleanState enable = new GlStateManager.BooleanState(3058);
      public int op = 5379;

      private ColorLogicState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class ColorMask {
      public boolean red = true;
      public boolean green = true;
      public boolean blue = true;
      public boolean alpha = true;

      private ColorMask() {
      }
   }

   @Deprecated
   @OnlyIn(Dist.CLIENT)
   static class ColorMaterialState {
      public final GlStateManager.BooleanState enable = new GlStateManager.BooleanState(2903);
      public int face = 1032;
      public int mode = 5634;

      private ColorMaterialState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class CullState {
      public final GlStateManager.BooleanState enable = new GlStateManager.BooleanState(2884);
      public int mode = 1029;

      private CullState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class DepthState {
      public final GlStateManager.BooleanState mode = new GlStateManager.BooleanState(2929);
      public boolean mask = true;
      public int func = 513;

      private DepthState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum DestFactor {
      CONSTANT_ALPHA(32771),
      CONSTANT_COLOR(32769),
      DST_ALPHA(772),
      DST_COLOR(774),
      ONE(1),
      ONE_MINUS_CONSTANT_ALPHA(32772),
      ONE_MINUS_CONSTANT_COLOR(32770),
      ONE_MINUS_DST_ALPHA(773),
      ONE_MINUS_DST_COLOR(775),
      ONE_MINUS_SRC_ALPHA(771),
      ONE_MINUS_SRC_COLOR(769),
      SRC_ALPHA(770),
      SRC_COLOR(768),
      ZERO(0);

      public final int value;

      private DestFactor(int p_i51106_3_) {
         this.value = p_i51106_3_;
      }
   }

   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public static enum FogMode {
      LINEAR(9729),
      EXP(2048),
      EXP2(2049);

      public final int value;

      private FogMode(int p_i50862_3_) {
         this.value = p_i50862_3_;
      }
   }

   @Deprecated
   @OnlyIn(Dist.CLIENT)
   static class FogState {
      public final GlStateManager.BooleanState enable = new GlStateManager.BooleanState(2912);
      public int mode = 2048;
      public float density = 1.0F;
      public float start;
      public float end = 1.0F;

      private FogState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum FramebufferExtension {
      BASE,
      ARB,
      EXT;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum LogicOp {
      AND(5377),
      AND_INVERTED(5380),
      AND_REVERSE(5378),
      CLEAR(5376),
      COPY(5379),
      COPY_INVERTED(5388),
      EQUIV(5385),
      INVERT(5386),
      NAND(5390),
      NOOP(5381),
      NOR(5384),
      OR(5383),
      OR_INVERTED(5389),
      OR_REVERSE(5387),
      SET(5391),
      XOR(5382);

      public final int value;

      private LogicOp(int p_i50860_3_) {
         this.value = p_i50860_3_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class PolygonOffsetState {
      public final GlStateManager.BooleanState fill = new GlStateManager.BooleanState(32823);
      public final GlStateManager.BooleanState line = new GlStateManager.BooleanState(10754);
      public float factor;
      public float units;

      private PolygonOffsetState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class ScissorState {
      public final GlStateManager.BooleanState mode = new GlStateManager.BooleanState(3089);

      private ScissorState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum SourceFactor {
      CONSTANT_ALPHA(32771),
      CONSTANT_COLOR(32769),
      DST_ALPHA(772),
      DST_COLOR(774),
      ONE(1),
      ONE_MINUS_CONSTANT_ALPHA(32772),
      ONE_MINUS_CONSTANT_COLOR(32770),
      ONE_MINUS_DST_ALPHA(773),
      ONE_MINUS_DST_COLOR(775),
      ONE_MINUS_SRC_ALPHA(771),
      ONE_MINUS_SRC_COLOR(769),
      SRC_ALPHA(770),
      SRC_ALPHA_SATURATE(776),
      SRC_COLOR(768),
      ZERO(0);

      public final int value;

      private SourceFactor(int p_i50328_3_) {
         this.value = p_i50328_3_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class StencilFunc {
      public int func = 519;
      public int ref;
      public int mask = -1;

      private StencilFunc() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class StencilState {
      public final GlStateManager.StencilFunc func = new GlStateManager.StencilFunc();
      public int mask = -1;
      public int fail = 7680;
      public int zfail = 7680;
      public int zpass = 7680;

      private StencilState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum SupportType {
      BASE,
      EXT,
      NONE;
   }

   @Deprecated
   @OnlyIn(Dist.CLIENT)
   public static enum TexGen {
      S,
      T,
      R,
      Q;
   }

   @Deprecated
   @OnlyIn(Dist.CLIENT)
   static class TexGenCoord {
      public final GlStateManager.BooleanState enable;
      public final int coord;
      public int mode = -1;

      public TexGenCoord(int p_i50853_1_, int p_i50853_2_) {
         this.coord = p_i50853_1_;
         this.enable = new GlStateManager.BooleanState(p_i50853_2_);
      }
   }

   @Deprecated
   @OnlyIn(Dist.CLIENT)
   static class TexGenState {
      public final GlStateManager.TexGenCoord s = new GlStateManager.TexGenCoord(8192, 3168);
      public final GlStateManager.TexGenCoord t = new GlStateManager.TexGenCoord(8193, 3169);
      public final GlStateManager.TexGenCoord r = new GlStateManager.TexGenCoord(8194, 3170);
      public final GlStateManager.TexGenCoord q = new GlStateManager.TexGenCoord(8195, 3171);

      private TexGenState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class TextureState {
      public final GlStateManager.BooleanState enable = new GlStateManager.BooleanState(3553);
      public int binding;

      private TextureState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Viewport {
      INSTANCE;

      protected int x;
      protected int y;
      protected int width;
      protected int height;
   }
}
