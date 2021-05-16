package net.minecraft.client.gui.fonts;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TexturedGlyph {
   private final RenderType normalType;
   private final RenderType seeThroughType;
   private final float u0;
   private final float u1;
   private final float v0;
   private final float v1;
   private final float left;
   private final float right;
   private final float up;
   private final float down;

   public TexturedGlyph(RenderType p_i225922_1_, RenderType p_i225922_2_, float p_i225922_3_, float p_i225922_4_, float p_i225922_5_, float p_i225922_6_, float p_i225922_7_, float p_i225922_8_, float p_i225922_9_, float p_i225922_10_) {
      this.normalType = p_i225922_1_;
      this.seeThroughType = p_i225922_2_;
      this.u0 = p_i225922_3_;
      this.u1 = p_i225922_4_;
      this.v0 = p_i225922_5_;
      this.v1 = p_i225922_6_;
      this.left = p_i225922_7_;
      this.right = p_i225922_8_;
      this.up = p_i225922_9_;
      this.down = p_i225922_10_;
   }

   public void render(boolean p_225595_1_, float p_225595_2_, float p_225595_3_, Matrix4f p_225595_4_, IVertexBuilder p_225595_5_, float p_225595_6_, float p_225595_7_, float p_225595_8_, float p_225595_9_, int p_225595_10_) {
      int i = 3;
      float f = p_225595_2_ + this.left;
      float f1 = p_225595_2_ + this.right;
      float f2 = this.up - 3.0F;
      float f3 = this.down - 3.0F;
      float f4 = p_225595_3_ + f2;
      float f5 = p_225595_3_ + f3;
      float f6 = p_225595_1_ ? 1.0F - 0.25F * f2 : 0.0F;
      float f7 = p_225595_1_ ? 1.0F - 0.25F * f3 : 0.0F;
      p_225595_5_.vertex(p_225595_4_, f + f6, f4, 0.0F).color(p_225595_6_, p_225595_7_, p_225595_8_, p_225595_9_).uv(this.u0, this.v0).uv2(p_225595_10_).endVertex();
      p_225595_5_.vertex(p_225595_4_, f + f7, f5, 0.0F).color(p_225595_6_, p_225595_7_, p_225595_8_, p_225595_9_).uv(this.u0, this.v1).uv2(p_225595_10_).endVertex();
      p_225595_5_.vertex(p_225595_4_, f1 + f7, f5, 0.0F).color(p_225595_6_, p_225595_7_, p_225595_8_, p_225595_9_).uv(this.u1, this.v1).uv2(p_225595_10_).endVertex();
      p_225595_5_.vertex(p_225595_4_, f1 + f6, f4, 0.0F).color(p_225595_6_, p_225595_7_, p_225595_8_, p_225595_9_).uv(this.u1, this.v0).uv2(p_225595_10_).endVertex();
   }

   public void renderEffect(TexturedGlyph.Effect p_228162_1_, Matrix4f p_228162_2_, IVertexBuilder p_228162_3_, int p_228162_4_) {
      p_228162_3_.vertex(p_228162_2_, p_228162_1_.x0, p_228162_1_.y0, p_228162_1_.depth).color(p_228162_1_.r, p_228162_1_.g, p_228162_1_.b, p_228162_1_.a).uv(this.u0, this.v0).uv2(p_228162_4_).endVertex();
      p_228162_3_.vertex(p_228162_2_, p_228162_1_.x1, p_228162_1_.y0, p_228162_1_.depth).color(p_228162_1_.r, p_228162_1_.g, p_228162_1_.b, p_228162_1_.a).uv(this.u0, this.v1).uv2(p_228162_4_).endVertex();
      p_228162_3_.vertex(p_228162_2_, p_228162_1_.x1, p_228162_1_.y1, p_228162_1_.depth).color(p_228162_1_.r, p_228162_1_.g, p_228162_1_.b, p_228162_1_.a).uv(this.u1, this.v1).uv2(p_228162_4_).endVertex();
      p_228162_3_.vertex(p_228162_2_, p_228162_1_.x0, p_228162_1_.y1, p_228162_1_.depth).color(p_228162_1_.r, p_228162_1_.g, p_228162_1_.b, p_228162_1_.a).uv(this.u1, this.v0).uv2(p_228162_4_).endVertex();
   }

   public RenderType renderType(boolean p_228163_1_) {
      return p_228163_1_ ? this.seeThroughType : this.normalType;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Effect {
      protected final float x0;
      protected final float y0;
      protected final float x1;
      protected final float y1;
      protected final float depth;
      protected final float r;
      protected final float g;
      protected final float b;
      protected final float a;

      public Effect(float p_i225923_1_, float p_i225923_2_, float p_i225923_3_, float p_i225923_4_, float p_i225923_5_, float p_i225923_6_, float p_i225923_7_, float p_i225923_8_, float p_i225923_9_) {
         this.x0 = p_i225923_1_;
         this.y0 = p_i225923_2_;
         this.x1 = p_i225923_3_;
         this.y1 = p_i225923_4_;
         this.depth = p_i225923_5_;
         this.r = p_i225923_6_;
         this.g = p_i225923_7_;
         this.b = p_i225923_8_;
         this.a = p_i225923_9_;
      }
   }
}
