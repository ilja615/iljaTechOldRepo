package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.concurrent.Executor;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Texture implements AutoCloseable {
   protected int id = -1;
   protected boolean blur;
   protected boolean mipmap;

   public void setFilter(boolean p_174937_1_, boolean p_174937_2_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      this.blur = p_174937_1_;
      this.mipmap = p_174937_2_;
      int i;
      int j;
      if (p_174937_1_) {
         i = p_174937_2_ ? 9987 : 9729;
         j = 9729;
      } else {
         i = p_174937_2_ ? 9986 : 9728;
         j = 9728;
      }

      GlStateManager._texParameter(3553, 10241, i);
      GlStateManager._texParameter(3553, 10240, j);
   }

   // FORGE: This seems to have been stripped out, but we need it
   private boolean lastBlur;
   private boolean lastMipmap;

   public void setBlurMipmap(boolean blur, boolean mipmap) {
      this.lastBlur = this.blur;
      this.lastMipmap = this.mipmap;
      setFilter(blur, mipmap);
   }

   public void restoreLastBlurMipmap() {
      setFilter(this.lastBlur, this.lastMipmap);
   }

   public int getId() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      if (this.id == -1) {
         this.id = TextureUtil.generateTextureId();
      }

      return this.id;
   }

   public void releaseId() {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            if (this.id != -1) {
               TextureUtil.releaseTextureId(this.id);
               this.id = -1;
            }

         });
      } else if (this.id != -1) {
         TextureUtil.releaseTextureId(this.id);
         this.id = -1;
      }

   }

   public abstract void load(IResourceManager p_195413_1_) throws IOException;

   public void bind() {
      if (!RenderSystem.isOnRenderThreadOrInit()) {
         RenderSystem.recordRenderCall(() -> {
            GlStateManager._bindTexture(this.getId());
         });
      } else {
         GlStateManager._bindTexture(this.getId());
      }

   }

   public void reset(TextureManager p_215244_1_, IResourceManager p_215244_2_, ResourceLocation p_215244_3_, Executor p_215244_4_) {
      p_215244_1_.register(p_215244_3_, this);
   }

   public void close() {
   }
}
