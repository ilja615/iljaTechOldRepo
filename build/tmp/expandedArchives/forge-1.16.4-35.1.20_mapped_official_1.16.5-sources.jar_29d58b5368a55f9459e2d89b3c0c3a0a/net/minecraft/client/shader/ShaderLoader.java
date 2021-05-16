package net.minecraft.client.shader;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class ShaderLoader {
   private final ShaderLoader.ShaderType type;
   private final String name;
   private final int id;
   private int references;

   private ShaderLoader(ShaderLoader.ShaderType p_i45091_1_, int p_i45091_2_, String p_i45091_3_) {
      this.type = p_i45091_1_;
      this.id = p_i45091_2_;
      this.name = p_i45091_3_;
   }

   public void attachToEffect(IShaderManager p_148056_1_) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      ++this.references;
      GlStateManager.glAttachShader(p_148056_1_.getId(), this.id);
   }

   public void close() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      --this.references;
      if (this.references <= 0) {
         GlStateManager.glDeleteShader(this.id);
         this.type.getPrograms().remove(this.name);
      }

   }

   public String getName() {
      return this.name;
   }

   public static ShaderLoader compileShader(ShaderLoader.ShaderType p_216534_0_, String p_216534_1_, InputStream p_216534_2_, String p_216534_3_) throws IOException {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      String s = TextureUtil.readResourceAsString(p_216534_2_);
      if (s == null) {
         throw new IOException("Could not load program " + p_216534_0_.getName());
      } else {
         int i = GlStateManager.glCreateShader(p_216534_0_.getGlType());
         GlStateManager.glShaderSource(i, s);
         GlStateManager.glCompileShader(i);
         if (GlStateManager.glGetShaderi(i, 35713) == 0) {
            String s1 = StringUtils.trim(GlStateManager.glGetShaderInfoLog(i, 32768));
            throw new IOException("Couldn't compile " + p_216534_0_.getName() + " program (" + p_216534_3_ + ", " + p_216534_1_ + ") : " + s1);
         } else {
            ShaderLoader shaderloader = new ShaderLoader(p_216534_0_, i, p_216534_1_);
            p_216534_0_.getPrograms().put(p_216534_1_, shaderloader);
            return shaderloader;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum ShaderType {
      VERTEX("vertex", ".vsh", 35633),
      FRAGMENT("fragment", ".fsh", 35632);

      private final String name;
      private final String extension;
      private final int glType;
      private final Map<String, ShaderLoader> programs = Maps.newHashMap();

      private ShaderType(String p_i45090_3_, String p_i45090_4_, int p_i45090_5_) {
         this.name = p_i45090_3_;
         this.extension = p_i45090_4_;
         this.glType = p_i45090_5_;
      }

      public String getName() {
         return this.name;
      }

      public String getExtension() {
         return this.extension;
      }

      private int getGlType() {
         return this.glType;
      }

      public Map<String, ShaderLoader> getPrograms() {
         return this.programs;
      }
   }
}
