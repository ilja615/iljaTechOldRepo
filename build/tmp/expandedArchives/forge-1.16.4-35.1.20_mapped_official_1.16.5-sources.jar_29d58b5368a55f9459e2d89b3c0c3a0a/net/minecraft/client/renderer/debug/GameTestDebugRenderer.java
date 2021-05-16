package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GameTestDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Map<BlockPos, GameTestDebugRenderer.Marker> markers = Maps.newHashMap();

   public void addMarker(BlockPos p_229022_1_, int p_229022_2_, String p_229022_3_, int p_229022_4_) {
      this.markers.put(p_229022_1_, new GameTestDebugRenderer.Marker(p_229022_2_, p_229022_3_, Util.getMillis() + (long)p_229022_4_));
   }

   public void clear() {
      this.markers.clear();
   }

   public void render(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      long i = Util.getMillis();
      this.markers.entrySet().removeIf((p_229021_2_) -> {
         return i > (p_229021_2_.getValue()).removeAtTime;
      });
      this.markers.forEach(this::renderMarker);
   }

   private void renderMarker(BlockPos p_229023_1_, GameTestDebugRenderer.Marker p_229023_2_) {
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      RenderSystem.color4f(0.0F, 1.0F, 0.0F, 0.75F);
      RenderSystem.disableTexture();
      DebugRenderer.renderFilledBox(p_229023_1_, 0.02F, p_229023_2_.getR(), p_229023_2_.getG(), p_229023_2_.getB(), p_229023_2_.getA());
      if (!p_229023_2_.text.isEmpty()) {
         double d0 = (double)p_229023_1_.getX() + 0.5D;
         double d1 = (double)p_229023_1_.getY() + 1.2D;
         double d2 = (double)p_229023_1_.getZ() + 0.5D;
         DebugRenderer.renderFloatingText(p_229023_2_.text, d0, d1, d2, -1, 0.01F, true, 0.0F, true);
      }

      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
      RenderSystem.popMatrix();
   }

   @OnlyIn(Dist.CLIENT)
   static class Marker {
      public int color;
      public String text;
      public long removeAtTime;

      public Marker(int p_i226032_1_, String p_i226032_2_, long p_i226032_3_) {
         this.color = p_i226032_1_;
         this.text = p_i226032_2_;
         this.removeAtTime = p_i226032_3_;
      }

      public float getR() {
         return (float)(this.color >> 16 & 255) / 255.0F;
      }

      public float getG() {
         return (float)(this.color >> 8 & 255) / 255.0F;
      }

      public float getB() {
         return (float)(this.color & 255) / 255.0F;
      }

      public float getA() {
         return (float)(this.color >> 24 & 255) / 255.0F;
      }
   }
}
