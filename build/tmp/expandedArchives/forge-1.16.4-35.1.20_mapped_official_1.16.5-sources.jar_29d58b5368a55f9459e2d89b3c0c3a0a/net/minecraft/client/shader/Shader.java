package net.minecraft.client.shader;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.List;
import java.util.function.IntSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Shader implements AutoCloseable {
   private final ShaderInstance effect;
   public final Framebuffer inTarget;
   public final Framebuffer outTarget;
   private final List<IntSupplier> auxAssets = Lists.newArrayList();
   private final List<String> auxNames = Lists.newArrayList();
   private final List<Integer> auxWidths = Lists.newArrayList();
   private final List<Integer> auxHeights = Lists.newArrayList();
   private Matrix4f shaderOrthoMatrix;

   public Shader(IResourceManager p_i45089_1_, String p_i45089_2_, Framebuffer p_i45089_3_, Framebuffer p_i45089_4_) throws IOException {
      this.effect = new ShaderInstance(p_i45089_1_, p_i45089_2_);
      this.inTarget = p_i45089_3_;
      this.outTarget = p_i45089_4_;
   }

   public void close() {
      this.effect.close();
   }

   public void addAuxAsset(String p_148041_1_, IntSupplier p_148041_2_, int p_148041_3_, int p_148041_4_) {
      this.auxNames.add(this.auxNames.size(), p_148041_1_);
      this.auxAssets.add(this.auxAssets.size(), p_148041_2_);
      this.auxWidths.add(this.auxWidths.size(), p_148041_3_);
      this.auxHeights.add(this.auxHeights.size(), p_148041_4_);
   }

   public void setOrthoMatrix(Matrix4f p_195654_1_) {
      this.shaderOrthoMatrix = p_195654_1_;
   }

   public void process(float p_148042_1_) {
      this.inTarget.unbindWrite();
      float f = (float)this.outTarget.width;
      float f1 = (float)this.outTarget.height;
      RenderSystem.viewport(0, 0, (int)f, (int)f1);
      this.effect.setSampler("DiffuseSampler", this.inTarget::getColorTextureId);

      for(int i = 0; i < this.auxAssets.size(); ++i) {
         this.effect.setSampler(this.auxNames.get(i), this.auxAssets.get(i));
         this.effect.safeGetUniform("AuxSize" + i).set((float)this.auxWidths.get(i).intValue(), (float)this.auxHeights.get(i).intValue());
      }

      this.effect.safeGetUniform("ProjMat").set(this.shaderOrthoMatrix);
      this.effect.safeGetUniform("InSize").set((float)this.inTarget.width, (float)this.inTarget.height);
      this.effect.safeGetUniform("OutSize").set(f, f1);
      this.effect.safeGetUniform("Time").set(p_148042_1_);
      Minecraft minecraft = Minecraft.getInstance();
      this.effect.safeGetUniform("ScreenSize").set((float)minecraft.getWindow().getWidth(), (float)minecraft.getWindow().getHeight());
      this.effect.apply();
      this.outTarget.clear(Minecraft.ON_OSX);
      this.outTarget.bindWrite(false);
      RenderSystem.depthFunc(519);
      BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
      bufferbuilder.vertex(0.0D, 0.0D, 500.0D).color(255, 255, 255, 255).endVertex();
      bufferbuilder.vertex((double)f, 0.0D, 500.0D).color(255, 255, 255, 255).endVertex();
      bufferbuilder.vertex((double)f, (double)f1, 500.0D).color(255, 255, 255, 255).endVertex();
      bufferbuilder.vertex(0.0D, (double)f1, 500.0D).color(255, 255, 255, 255).endVertex();
      bufferbuilder.end();
      WorldVertexBufferUploader.end(bufferbuilder);
      RenderSystem.depthFunc(515);
      this.effect.clear();
      this.outTarget.unbindWrite();
      this.inTarget.unbindRead();

      for(Object object : this.auxAssets) {
         if (object instanceof Framebuffer) {
            ((Framebuffer)object).unbindRead();
         }
      }

   }

   public ShaderInstance getEffect() {
      return this.effect;
   }
}
