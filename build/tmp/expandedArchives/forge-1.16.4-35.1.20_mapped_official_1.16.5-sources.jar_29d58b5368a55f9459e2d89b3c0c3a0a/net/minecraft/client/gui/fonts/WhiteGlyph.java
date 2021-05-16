package net.minecraft.client.gui.fonts;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum WhiteGlyph implements IGlyphInfo {
   INSTANCE;

   private static final NativeImage IMAGE_DATA = Util.make(new NativeImage(NativeImage.PixelFormat.RGBA, 5, 8, false), (p_228173_0_) -> {
      for(int i = 0; i < 8; ++i) {
         for(int j = 0; j < 5; ++j) {
            if (j != 0 && j + 1 != 5 && i != 0 && i + 1 != 8) {
               boolean flag = false;
            } else {
               boolean flag1 = true;
            }

            p_228173_0_.setPixelRGBA(j, i, -1);
         }
      }

      p_228173_0_.untrack();
   });

   public int getPixelWidth() {
      return 5;
   }

   public int getPixelHeight() {
      return 8;
   }

   public float getAdvance() {
      return 6.0F;
   }

   public float getOversample() {
      return 1.0F;
   }

   public void upload(int p_211573_1_, int p_211573_2_) {
      IMAGE_DATA.upload(0, p_211573_1_, p_211573_2_, false);
   }

   public boolean isColored() {
      return true;
   }
}
