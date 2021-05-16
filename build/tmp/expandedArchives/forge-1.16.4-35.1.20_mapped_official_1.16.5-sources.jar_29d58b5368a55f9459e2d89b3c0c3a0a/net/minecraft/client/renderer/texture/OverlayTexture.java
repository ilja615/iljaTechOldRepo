package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OverlayTexture implements AutoCloseable {
   public static final int NO_OVERLAY = pack(0, 10);
   private final DynamicTexture texture = new DynamicTexture(16, 16, false);

   public OverlayTexture() {
      NativeImage nativeimage = this.texture.getPixels();

      for(int i = 0; i < 16; ++i) {
         for(int j = 0; j < 16; ++j) {
            if (i < 8) {
               nativeimage.setPixelRGBA(j, i, -1308622593);
            } else {
               int k = (int)((1.0F - (float)j / 15.0F * 0.75F) * 255.0F);
               nativeimage.setPixelRGBA(j, i, k << 24 | 16777215);
            }
         }
      }

      RenderSystem.activeTexture(33985);
      this.texture.bind();
      RenderSystem.matrixMode(5890);
      RenderSystem.loadIdentity();
      float f = 0.06666667F;
      RenderSystem.scalef(0.06666667F, 0.06666667F, 0.06666667F);
      RenderSystem.matrixMode(5888);
      this.texture.bind();
      nativeimage.upload(0, 0, 0, 0, 0, nativeimage.getWidth(), nativeimage.getHeight(), false, true, false, false);
      RenderSystem.activeTexture(33984);
   }

   public void close() {
      this.texture.close();
   }

   public void setupOverlayColor() {
      RenderSystem.setupOverlayColor(this.texture::getId, 16);
   }

   public static int u(float p_229199_0_) {
      return (int)(p_229199_0_ * 15.0F);
   }

   public static int v(boolean p_229202_0_) {
      return p_229202_0_ ? 3 : 10;
   }

   public static int pack(int p_229201_0_, int p_229201_1_) {
      return p_229201_0_ | p_229201_1_ << 16;
   }

   public static int pack(float p_229200_0_, boolean p_229200_1_) {
      return pack(u(p_229200_0_), v(p_229200_1_));
   }

   public void teardownOverlayColor() {
      RenderSystem.teardownOverlayColor();
   }
}
