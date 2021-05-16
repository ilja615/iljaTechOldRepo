package net.minecraft.client.gui.fonts;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EmptyGlyph extends TexturedGlyph {
   public EmptyGlyph() {
      super(RenderType.text(new ResourceLocation("")), RenderType.textSeeThrough(new ResourceLocation("")), 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   }

   public void render(boolean p_225595_1_, float p_225595_2_, float p_225595_3_, Matrix4f p_225595_4_, IVertexBuilder p_225595_5_, float p_225595_6_, float p_225595_7_, float p_225595_8_, float p_225595_9_, int p_225595_10_) {
   }
}
