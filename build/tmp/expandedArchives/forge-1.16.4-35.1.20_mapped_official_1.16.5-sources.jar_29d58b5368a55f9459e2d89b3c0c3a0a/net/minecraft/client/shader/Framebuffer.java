package net.minecraft.client.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Framebuffer {
   public int width;
   public int height;
   public int viewWidth;
   public int viewHeight;
   public final boolean useDepth;
   public int frameBufferId;
   private int colorTextureId;
   private int depthBufferId;
   public final float[] clearChannels;
   public int filterMode;

   public Framebuffer(int p_i51175_1_, int p_i51175_2_, boolean p_i51175_3_, boolean p_i51175_4_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      this.useDepth = p_i51175_3_;
      this.frameBufferId = -1;
      this.colorTextureId = -1;
      this.depthBufferId = -1;
      this.clearChannels = new float[4];
      this.clearChannels[0] = 1.0F;
      this.clearChannels[1] = 1.0F;
      this.clearChannels[2] = 1.0F;
      this.clearChannels[3] = 0.0F;
      this.resize(p_i51175_1_, p_i51175_2_, p_i51175_4_);
   }

   public void resize(int p_216491_1_, int p_216491_2_, boolean p_216491_3_) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            this._resize(p_216491_1_, p_216491_2_, p_216491_3_);
         });
      } else {
         this._resize(p_216491_1_, p_216491_2_, p_216491_3_);
      }

   }

   private void _resize(int p_227586_1_, int p_227586_2_, boolean p_227586_3_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GlStateManager._enableDepthTest();
      if (this.frameBufferId >= 0) {
         this.destroyBuffers();
      }

      this.createBuffers(p_227586_1_, p_227586_2_, p_227586_3_);
      GlStateManager._glBindFramebuffer(FramebufferConstants.GL_FRAMEBUFFER, 0);
   }

   public void destroyBuffers() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      this.unbindRead();
      this.unbindWrite();
      if (this.depthBufferId > -1) {
         TextureUtil.releaseTextureId(this.depthBufferId);
         this.depthBufferId = -1;
      }

      if (this.colorTextureId > -1) {
         TextureUtil.releaseTextureId(this.colorTextureId);
         this.colorTextureId = -1;
      }

      if (this.frameBufferId > -1) {
         GlStateManager._glBindFramebuffer(FramebufferConstants.GL_FRAMEBUFFER, 0);
         GlStateManager._glDeleteFramebuffers(this.frameBufferId);
         this.frameBufferId = -1;
      }

   }

   public void copyDepthFrom(Framebuffer p_237506_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      if (GlStateManager.supportsFramebufferBlit()) {
         GlStateManager._glBindFramebuffer(36008, p_237506_1_.frameBufferId);
         GlStateManager._glBindFramebuffer(36009, this.frameBufferId);
         GlStateManager._glBlitFrameBuffer(0, 0, p_237506_1_.width, p_237506_1_.height, 0, 0, this.width, this.height, 256, 9728);
      } else {
         GlStateManager._glBindFramebuffer(FramebufferConstants.GL_FRAMEBUFFER, this.frameBufferId);
         int i = GlStateManager.getFramebufferDepthTexture();
         if (i != 0) {
            int j = GlStateManager.getActiveTextureName();
            GlStateManager._bindTexture(i);
            GlStateManager._glBindFramebuffer(FramebufferConstants.GL_FRAMEBUFFER, p_237506_1_.frameBufferId);
            GlStateManager._glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, Math.min(this.width, p_237506_1_.width), Math.min(this.height, p_237506_1_.height));
            GlStateManager._bindTexture(j);
         }
      }

      GlStateManager._glBindFramebuffer(FramebufferConstants.GL_FRAMEBUFFER, 0);
   }

   public void createBuffers(int p_216492_1_, int p_216492_2_, boolean p_216492_3_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      this.viewWidth = p_216492_1_;
      this.viewHeight = p_216492_2_;
      this.width = p_216492_1_;
      this.height = p_216492_2_;
      this.frameBufferId = GlStateManager.glGenFramebuffers();
      this.colorTextureId = TextureUtil.generateTextureId();
      if (this.useDepth) {
         this.depthBufferId = TextureUtil.generateTextureId();
         GlStateManager._bindTexture(this.depthBufferId);
         GlStateManager._texParameter(3553, 10241, 9728);
         GlStateManager._texParameter(3553, 10240, 9728);
         GlStateManager._texParameter(3553, 10242, 10496);
         GlStateManager._texParameter(3553, 10243, 10496);
         GlStateManager._texParameter(3553, 34892, 0);
         if (!stencilEnabled)
         GlStateManager._texImage2D(3553, 0, 6402, this.width, this.height, 0, 6402, 5126, (IntBuffer)null);
         else
         GlStateManager._texImage2D(3553, 0, org.lwjgl.opengl.GL30.GL_DEPTH32F_STENCIL8, this.width, this.height, 0, org.lwjgl.opengl.GL30.GL_DEPTH_STENCIL, org.lwjgl.opengl.GL30.GL_FLOAT_32_UNSIGNED_INT_24_8_REV, null);
      }

      this.setFilterMode(9728);
      GlStateManager._bindTexture(this.colorTextureId);
      GlStateManager._texImage2D(3553, 0, 32856, this.width, this.height, 0, 6408, 5121, (IntBuffer)null);
      GlStateManager._glBindFramebuffer(FramebufferConstants.GL_FRAMEBUFFER, this.frameBufferId);
      GlStateManager._glFramebufferTexture2D(FramebufferConstants.GL_FRAMEBUFFER, FramebufferConstants.GL_COLOR_ATTACHMENT0, 3553, this.colorTextureId, 0);
      if (this.useDepth) {
         if(!stencilEnabled)
         GlStateManager._glFramebufferTexture2D(FramebufferConstants.GL_FRAMEBUFFER, FramebufferConstants.GL_DEPTH_ATTACHMENT, 3553, this.depthBufferId, 0);
         else if(net.minecraftforge.common.ForgeConfig.CLIENT.useCombinedDepthStencilAttachment.get()) {
            GlStateManager._glFramebufferTexture2D(FramebufferConstants.GL_FRAMEBUFFER, org.lwjgl.opengl.GL30.GL_DEPTH_STENCIL_ATTACHMENT, 3553, this.depthBufferId, 0);
         } else {
            GlStateManager._glFramebufferTexture2D(FramebufferConstants.GL_FRAMEBUFFER, org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT, 3553, this.depthBufferId, 0);
            GlStateManager._glFramebufferTexture2D(FramebufferConstants.GL_FRAMEBUFFER, org.lwjgl.opengl.GL30.GL_STENCIL_ATTACHMENT, 3553, this.depthBufferId, 0);
         }
      }

      this.checkStatus();
      this.clear(p_216492_3_);
      this.unbindRead();
   }

   public void setFilterMode(int p_147607_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      this.filterMode = p_147607_1_;
      GlStateManager._bindTexture(this.colorTextureId);
      GlStateManager._texParameter(3553, 10241, p_147607_1_);
      GlStateManager._texParameter(3553, 10240, p_147607_1_);
      GlStateManager._texParameter(3553, 10242, 10496);
      GlStateManager._texParameter(3553, 10243, 10496);
      GlStateManager._bindTexture(0);
   }

   public void checkStatus() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      int i = GlStateManager.glCheckFramebufferStatus(FramebufferConstants.GL_FRAMEBUFFER);
      if (i != FramebufferConstants.GL_FRAMEBUFFER_COMPLETE) {
         if (i == FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
         } else if (i == FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
         } else if (i == FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
         } else if (i == FramebufferConstants.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
         } else {
            throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + i);
         }
      }
   }

   public void bindRead() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GlStateManager._bindTexture(this.colorTextureId);
   }

   public void unbindRead() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GlStateManager._bindTexture(0);
   }

   public void bindWrite(boolean p_147610_1_) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            this._bindWrite(p_147610_1_);
         });
      } else {
         this._bindWrite(p_147610_1_);
      }

   }

   private void _bindWrite(boolean p_227585_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GlStateManager._glBindFramebuffer(FramebufferConstants.GL_FRAMEBUFFER, this.frameBufferId);
      if (p_227585_1_) {
         GlStateManager._viewport(0, 0, this.viewWidth, this.viewHeight);
      }

   }

   public void unbindWrite() {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            GlStateManager._glBindFramebuffer(FramebufferConstants.GL_FRAMEBUFFER, 0);
         });
      } else {
         GlStateManager._glBindFramebuffer(FramebufferConstants.GL_FRAMEBUFFER, 0);
      }

   }

   public void setClearColor(float p_147604_1_, float p_147604_2_, float p_147604_3_, float p_147604_4_) {
      this.clearChannels[0] = p_147604_1_;
      this.clearChannels[1] = p_147604_2_;
      this.clearChannels[2] = p_147604_3_;
      this.clearChannels[3] = p_147604_4_;
   }

   public void blitToScreen(int p_147615_1_, int p_147615_2_) {
      this.blitToScreen(p_147615_1_, p_147615_2_, true);
   }

   public void blitToScreen(int p_178038_1_, int p_178038_2_, boolean p_178038_3_) {
      RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
      if (!RenderSystem.isInInitPhase()) {
         RenderSystem.recordRenderCall(() -> {
            this._blitToScreen(p_178038_1_, p_178038_2_, p_178038_3_);
         });
      } else {
         this._blitToScreen(p_178038_1_, p_178038_2_, p_178038_3_);
      }

   }

   private void _blitToScreen(int p_227588_1_, int p_227588_2_, boolean p_227588_3_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GlStateManager._colorMask(true, true, true, false);
      GlStateManager._disableDepthTest();
      GlStateManager._depthMask(false);
      GlStateManager._matrixMode(5889);
      GlStateManager._loadIdentity();
      GlStateManager._ortho(0.0D, (double)p_227588_1_, (double)p_227588_2_, 0.0D, 1000.0D, 3000.0D);
      GlStateManager._matrixMode(5888);
      GlStateManager._loadIdentity();
      GlStateManager._translatef(0.0F, 0.0F, -2000.0F);
      GlStateManager._viewport(0, 0, p_227588_1_, p_227588_2_);
      GlStateManager._enableTexture();
      GlStateManager._disableLighting();
      GlStateManager._disableAlphaTest();
      if (p_227588_3_) {
         GlStateManager._disableBlend();
         GlStateManager._enableColorMaterial();
      }

      GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.bindRead();
      float f = (float)p_227588_1_;
      float f1 = (float)p_227588_2_;
      float f2 = (float)this.viewWidth / (float)this.width;
      float f3 = (float)this.viewHeight / (float)this.height;
      Tessellator tessellator = RenderSystem.renderThreadTesselator();
      BufferBuilder bufferbuilder = tessellator.getBuilder();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      bufferbuilder.vertex(0.0D, (double)f1, 0.0D).uv(0.0F, 0.0F).color(255, 255, 255, 255).endVertex();
      bufferbuilder.vertex((double)f, (double)f1, 0.0D).uv(f2, 0.0F).color(255, 255, 255, 255).endVertex();
      bufferbuilder.vertex((double)f, 0.0D, 0.0D).uv(f2, f3).color(255, 255, 255, 255).endVertex();
      bufferbuilder.vertex(0.0D, 0.0D, 0.0D).uv(0.0F, f3).color(255, 255, 255, 255).endVertex();
      tessellator.end();
      this.unbindRead();
      GlStateManager._depthMask(true);
      GlStateManager._colorMask(true, true, true, true);
   }

   public void clear(boolean p_216493_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      this.bindWrite(true);
      GlStateManager._clearColor(this.clearChannels[0], this.clearChannels[1], this.clearChannels[2], this.clearChannels[3]);
      int i = 16384;
      if (this.useDepth) {
         GlStateManager._clearDepth(1.0D);
         i |= 256;
      }

      GlStateManager._clear(i, p_216493_1_);
      this.unbindWrite();
   }


   /*================================ FORGE START ================================================*/
   private boolean stencilEnabled = false;
   /**
    * Attempts to enable 8 bits of stencil buffer on this FrameBuffer.
    * Modders must call this directly to set things up.
    * This is to prevent the default cause where graphics cards do not support stencil bits.
    * <b>Make sure to call this on the main render thread!</b>
    */
   public void enableStencil()
   {
      if(stencilEnabled) return;
      stencilEnabled = true;
      this.resize(viewWidth, viewHeight, net.minecraft.client.Minecraft.ON_OSX);
   }

   /**
    * Returns wither or not this FBO has been successfully initialized with stencil bits.
    * If not, and a modder wishes it to be, they must call enableStencil.
    */
   public boolean isStencilEnabled()
   {
      return this.stencilEnabled;
   }
   /*================================ FORGE END   ================================================*/

   public int getColorTextureId() {
      return this.colorTextureId;
   }

   public int getDepthTextureId() {
      return this.depthBufferId;
   }
}
