package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.gui.fonts.EmptyGlyph;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.IGlyph;
import net.minecraft.client.gui.fonts.TexturedGlyph;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ICharacterConsumer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.CharacterManager;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextProcessing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FontRenderer {
   private static final Vector3f SHADOW_OFFSET = new Vector3f(0.0F, 0.0F, 0.03F);
   public final int lineHeight = 9;
   public final Random random = new Random();
   private final Function<ResourceLocation, Font> fonts;
   private final CharacterManager splitter;

   public FontRenderer(Function<ResourceLocation, Font> p_i232249_1_) {
      this.fonts = p_i232249_1_;
      this.splitter = new CharacterManager((p_238404_1_, p_238404_2_) -> {
         return this.getFontSet(p_238404_2_.getFont()).getGlyphInfo(p_238404_1_).getAdvance(p_238404_2_.isBold());
      });
   }

   private Font getFontSet(ResourceLocation p_238419_1_) {
      return this.fonts.apply(p_238419_1_);
   }

   public int drawShadow(MatrixStack p_238405_1_, String p_238405_2_, float p_238405_3_, float p_238405_4_, int p_238405_5_) {
      return this.drawInternal(p_238405_2_, p_238405_3_, p_238405_4_, p_238405_5_, p_238405_1_.last().pose(), true, this.isBidirectional());
   }

   public int drawShadow(MatrixStack p_238406_1_, String p_238406_2_, float p_238406_3_, float p_238406_4_, int p_238406_5_, boolean p_238406_6_) {
      RenderSystem.enableAlphaTest();
      return this.drawInternal(p_238406_2_, p_238406_3_, p_238406_4_, p_238406_5_, p_238406_1_.last().pose(), true, p_238406_6_);
   }

   public int draw(MatrixStack p_238421_1_, String p_238421_2_, float p_238421_3_, float p_238421_4_, int p_238421_5_) {
      RenderSystem.enableAlphaTest();
      return this.drawInternal(p_238421_2_, p_238421_3_, p_238421_4_, p_238421_5_, p_238421_1_.last().pose(), false, this.isBidirectional());
   }

   public int drawShadow(MatrixStack p_238407_1_, IReorderingProcessor p_238407_2_, float p_238407_3_, float p_238407_4_, int p_238407_5_) {
      RenderSystem.enableAlphaTest();
      return this.drawInternal(p_238407_2_, p_238407_3_, p_238407_4_, p_238407_5_, p_238407_1_.last().pose(), true);
   }

   public int drawShadow(MatrixStack p_243246_1_, ITextComponent p_243246_2_, float p_243246_3_, float p_243246_4_, int p_243246_5_) {
      RenderSystem.enableAlphaTest();
      return this.drawInternal(p_243246_2_.getVisualOrderText(), p_243246_3_, p_243246_4_, p_243246_5_, p_243246_1_.last().pose(), true);
   }

   public int draw(MatrixStack p_238422_1_, IReorderingProcessor p_238422_2_, float p_238422_3_, float p_238422_4_, int p_238422_5_) {
      RenderSystem.enableAlphaTest();
      return this.drawInternal(p_238422_2_, p_238422_3_, p_238422_4_, p_238422_5_, p_238422_1_.last().pose(), false);
   }

   public int draw(MatrixStack p_243248_1_, ITextComponent p_243248_2_, float p_243248_3_, float p_243248_4_, int p_243248_5_) {
      RenderSystem.enableAlphaTest();
      return this.drawInternal(p_243248_2_.getVisualOrderText(), p_243248_3_, p_243248_4_, p_243248_5_, p_243248_1_.last().pose(), false);
   }

   public String bidirectionalShaping(String p_147647_1_) {
      try {
         Bidi bidi = new Bidi((new ArabicShaping(8)).shape(p_147647_1_), 127);
         bidi.setReorderingMode(0);
         return bidi.writeReordered(2);
      } catch (ArabicShapingException arabicshapingexception) {
         return p_147647_1_;
      }
   }

   private int drawInternal(String p_228078_1_, float p_228078_2_, float p_228078_3_, int p_228078_4_, Matrix4f p_228078_5_, boolean p_228078_6_, boolean p_228078_7_) {
      if (p_228078_1_ == null) {
         return 0;
      } else {
         IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
         int i = this.drawInBatch(p_228078_1_, p_228078_2_, p_228078_3_, p_228078_4_, p_228078_6_, p_228078_5_, irendertypebuffer$impl, false, 0, 15728880, p_228078_7_);
         irendertypebuffer$impl.endBatch();
         return i;
      }
   }

   private int drawInternal(IReorderingProcessor p_238415_1_, float p_238415_2_, float p_238415_3_, int p_238415_4_, Matrix4f p_238415_5_, boolean p_238415_6_) {
      IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
      int i = this.drawInBatch(p_238415_1_, p_238415_2_, p_238415_3_, p_238415_4_, p_238415_6_, p_238415_5_, irendertypebuffer$impl, false, 0, 15728880);
      irendertypebuffer$impl.endBatch();
      return i;
   }

   public int drawInBatch(String p_228079_1_, float p_228079_2_, float p_228079_3_, int p_228079_4_, boolean p_228079_5_, Matrix4f p_228079_6_, IRenderTypeBuffer p_228079_7_, boolean p_228079_8_, int p_228079_9_, int p_228079_10_) {
      return this.drawInBatch(p_228079_1_, p_228079_2_, p_228079_3_, p_228079_4_, p_228079_5_, p_228079_6_, p_228079_7_, p_228079_8_, p_228079_9_, p_228079_10_, this.isBidirectional());
   }

   public int drawInBatch(String p_238411_1_, float p_238411_2_, float p_238411_3_, int p_238411_4_, boolean p_238411_5_, Matrix4f p_238411_6_, IRenderTypeBuffer p_238411_7_, boolean p_238411_8_, int p_238411_9_, int p_238411_10_, boolean p_238411_11_) {
      return this.drawInternal(p_238411_1_, p_238411_2_, p_238411_3_, p_238411_4_, p_238411_5_, p_238411_6_, p_238411_7_, p_238411_8_, p_238411_9_, p_238411_10_, p_238411_11_);
   }

   public int drawInBatch(ITextComponent p_243247_1_, float p_243247_2_, float p_243247_3_, int p_243247_4_, boolean p_243247_5_, Matrix4f p_243247_6_, IRenderTypeBuffer p_243247_7_, boolean p_243247_8_, int p_243247_9_, int p_243247_10_) {
      return this.drawInBatch(p_243247_1_.getVisualOrderText(), p_243247_2_, p_243247_3_, p_243247_4_, p_243247_5_, p_243247_6_, p_243247_7_, p_243247_8_, p_243247_9_, p_243247_10_);
   }

   public int drawInBatch(IReorderingProcessor p_238416_1_, float p_238416_2_, float p_238416_3_, int p_238416_4_, boolean p_238416_5_, Matrix4f p_238416_6_, IRenderTypeBuffer p_238416_7_, boolean p_238416_8_, int p_238416_9_, int p_238416_10_) {
      return this.drawInternal(p_238416_1_, p_238416_2_, p_238416_3_, p_238416_4_, p_238416_5_, p_238416_6_, p_238416_7_, p_238416_8_, p_238416_9_, p_238416_10_);
   }

   private static int adjustColor(int p_238403_0_) {
      return (p_238403_0_ & -67108864) == 0 ? p_238403_0_ | -16777216 : p_238403_0_;
   }

   private int drawInternal(String p_238423_1_, float p_238423_2_, float p_238423_3_, int p_238423_4_, boolean p_238423_5_, Matrix4f p_238423_6_, IRenderTypeBuffer p_238423_7_, boolean p_238423_8_, int p_238423_9_, int p_238423_10_, boolean p_238423_11_) {
      if (p_238423_11_) {
         p_238423_1_ = this.bidirectionalShaping(p_238423_1_);
      }

      p_238423_4_ = adjustColor(p_238423_4_);
      Matrix4f matrix4f = p_238423_6_.copy();
      if (p_238423_5_) {
         this.renderText(p_238423_1_, p_238423_2_, p_238423_3_, p_238423_4_, true, p_238423_6_, p_238423_7_, p_238423_8_, p_238423_9_, p_238423_10_);
         matrix4f.translate(SHADOW_OFFSET);
      }

      p_238423_2_ = this.renderText(p_238423_1_, p_238423_2_, p_238423_3_, p_238423_4_, false, matrix4f, p_238423_7_, p_238423_8_, p_238423_9_, p_238423_10_);
      return (int)p_238423_2_ + (p_238423_5_ ? 1 : 0);
   }

   private int drawInternal(IReorderingProcessor p_238424_1_, float p_238424_2_, float p_238424_3_, int p_238424_4_, boolean p_238424_5_, Matrix4f p_238424_6_, IRenderTypeBuffer p_238424_7_, boolean p_238424_8_, int p_238424_9_, int p_238424_10_) {
      p_238424_4_ = adjustColor(p_238424_4_);
      Matrix4f matrix4f = p_238424_6_.copy();
      if (p_238424_5_) {
         this.renderText(p_238424_1_, p_238424_2_, p_238424_3_, p_238424_4_, true, p_238424_6_, p_238424_7_, p_238424_8_, p_238424_9_, p_238424_10_);
         matrix4f.translate(SHADOW_OFFSET);
      }

      p_238424_2_ = this.renderText(p_238424_1_, p_238424_2_, p_238424_3_, p_238424_4_, false, matrix4f, p_238424_7_, p_238424_8_, p_238424_9_, p_238424_10_);
      return (int)p_238424_2_ + (p_238424_5_ ? 1 : 0);
   }

   private float renderText(String p_228081_1_, float p_228081_2_, float p_228081_3_, int p_228081_4_, boolean p_228081_5_, Matrix4f p_228081_6_, IRenderTypeBuffer p_228081_7_, boolean p_228081_8_, int p_228081_9_, int p_228081_10_) {
      FontRenderer.CharacterRenderer fontrenderer$characterrenderer = new FontRenderer.CharacterRenderer(p_228081_7_, p_228081_2_, p_228081_3_, p_228081_4_, p_228081_5_, p_228081_6_, p_228081_8_, p_228081_10_);
      TextProcessing.iterateFormatted(p_228081_1_, Style.EMPTY, fontrenderer$characterrenderer);
      return fontrenderer$characterrenderer.finish(p_228081_9_, p_228081_2_);
   }

   private float renderText(IReorderingProcessor p_238426_1_, float p_238426_2_, float p_238426_3_, int p_238426_4_, boolean p_238426_5_, Matrix4f p_238426_6_, IRenderTypeBuffer p_238426_7_, boolean p_238426_8_, int p_238426_9_, int p_238426_10_) {
      FontRenderer.CharacterRenderer fontrenderer$characterrenderer = new FontRenderer.CharacterRenderer(p_238426_7_, p_238426_2_, p_238426_3_, p_238426_4_, p_238426_5_, p_238426_6_, p_238426_8_, p_238426_10_);
      p_238426_1_.accept(fontrenderer$characterrenderer);
      return fontrenderer$characterrenderer.finish(p_238426_9_, p_238426_2_);
   }

   private void renderChar(TexturedGlyph p_228077_1_, boolean p_228077_2_, boolean p_228077_3_, float p_228077_4_, float p_228077_5_, float p_228077_6_, Matrix4f p_228077_7_, IVertexBuilder p_228077_8_, float p_228077_9_, float p_228077_10_, float p_228077_11_, float p_228077_12_, int p_228077_13_) {
      p_228077_1_.render(p_228077_3_, p_228077_5_, p_228077_6_, p_228077_7_, p_228077_8_, p_228077_9_, p_228077_10_, p_228077_11_, p_228077_12_, p_228077_13_);
      if (p_228077_2_) {
         p_228077_1_.render(p_228077_3_, p_228077_5_ + p_228077_4_, p_228077_6_, p_228077_7_, p_228077_8_, p_228077_9_, p_228077_10_, p_228077_11_, p_228077_12_, p_228077_13_);
      }

   }

   public int width(String p_78256_1_) {
      return MathHelper.ceil(this.splitter.stringWidth(p_78256_1_));
   }

   public int width(ITextProperties p_238414_1_) {
      return MathHelper.ceil(this.splitter.stringWidth(p_238414_1_));
   }

   public int width(IReorderingProcessor p_243245_1_) {
      return MathHelper.ceil(this.splitter.stringWidth(p_243245_1_));
   }

   public String plainSubstrByWidth(String p_238413_1_, int p_238413_2_, boolean p_238413_3_) {
      return p_238413_3_ ? this.splitter.plainTailByWidth(p_238413_1_, p_238413_2_, Style.EMPTY) : this.splitter.plainHeadByWidth(p_238413_1_, p_238413_2_, Style.EMPTY);
   }

   public String plainSubstrByWidth(String p_238412_1_, int p_238412_2_) {
      return this.splitter.plainHeadByWidth(p_238412_1_, p_238412_2_, Style.EMPTY);
   }

   public ITextProperties substrByWidth(ITextProperties p_238417_1_, int p_238417_2_) {
      return this.splitter.headByWidth(p_238417_1_, p_238417_2_, Style.EMPTY);
   }

   public void drawWordWrap(ITextProperties p_238418_1_, int p_238418_2_, int p_238418_3_, int p_238418_4_, int p_238418_5_) {
      Matrix4f matrix4f = TransformationMatrix.identity().getMatrix();

      for(IReorderingProcessor ireorderingprocessor : this.split(p_238418_1_, p_238418_4_)) {
         this.drawInternal(ireorderingprocessor, (float)p_238418_2_, (float)p_238418_3_, p_238418_5_, matrix4f, false);
         p_238418_3_ += 9;
      }

   }

   public int wordWrapHeight(String p_78267_1_, int p_78267_2_) {
      return 9 * this.splitter.splitLines(p_78267_1_, p_78267_2_, Style.EMPTY).size();
   }

   public List<IReorderingProcessor> split(ITextProperties p_238425_1_, int p_238425_2_) {
      return LanguageMap.getInstance().getVisualOrder(this.splitter.splitLines(p_238425_1_, p_238425_2_, Style.EMPTY));
   }

   public boolean isBidirectional() {
      return LanguageMap.getInstance().isDefaultRightToLeft();
   }

   public CharacterManager getSplitter() {
      return this.splitter;
   }

   @OnlyIn(Dist.CLIENT)
   class CharacterRenderer implements ICharacterConsumer {
      final IRenderTypeBuffer bufferSource;
      private final boolean dropShadow;
      private final float dimFactor;
      private final float r;
      private final float g;
      private final float b;
      private final float a;
      private final Matrix4f pose;
      private final boolean seeThrough;
      private final int packedLightCoords;
      private float x;
      private float y;
      @Nullable
      private List<TexturedGlyph.Effect> effects;

      private void addEffect(TexturedGlyph.Effect p_238442_1_) {
         if (this.effects == null) {
            this.effects = Lists.newArrayList();
         }

         this.effects.add(p_238442_1_);
      }

      public CharacterRenderer(IRenderTypeBuffer p_i232250_2_, float p_i232250_3_, float p_i232250_4_, int p_i232250_5_, boolean p_i232250_6_, Matrix4f p_i232250_7_, boolean p_i232250_8_, int p_i232250_9_) {
         this.bufferSource = p_i232250_2_;
         this.x = p_i232250_3_;
         this.y = p_i232250_4_;
         this.dropShadow = p_i232250_6_;
         this.dimFactor = p_i232250_6_ ? 0.25F : 1.0F;
         this.r = (float)(p_i232250_5_ >> 16 & 255) / 255.0F * this.dimFactor;
         this.g = (float)(p_i232250_5_ >> 8 & 255) / 255.0F * this.dimFactor;
         this.b = (float)(p_i232250_5_ & 255) / 255.0F * this.dimFactor;
         this.a = (float)(p_i232250_5_ >> 24 & 255) / 255.0F;
         this.pose = p_i232250_7_;
         this.seeThrough = p_i232250_8_;
         this.packedLightCoords = p_i232250_9_;
      }

      public boolean accept(int p_accept_1_, Style p_accept_2_, int p_accept_3_) {
         Font font = FontRenderer.this.getFontSet(p_accept_2_.getFont());
         IGlyph iglyph = font.getGlyphInfo(p_accept_3_);
         TexturedGlyph texturedglyph = p_accept_2_.isObfuscated() && p_accept_3_ != 32 ? font.getRandomGlyph(iglyph) : font.getGlyph(p_accept_3_);
         boolean flag = p_accept_2_.isBold();
         float f3 = this.a;
         Color color = p_accept_2_.getColor();
         float f;
         float f1;
         float f2;
         if (color != null) {
            int i = color.getValue();
            f = (float)(i >> 16 & 255) / 255.0F * this.dimFactor;
            f1 = (float)(i >> 8 & 255) / 255.0F * this.dimFactor;
            f2 = (float)(i & 255) / 255.0F * this.dimFactor;
         } else {
            f = this.r;
            f1 = this.g;
            f2 = this.b;
         }

         if (!(texturedglyph instanceof EmptyGlyph)) {
            float f5 = flag ? iglyph.getBoldOffset() : 0.0F;
            float f4 = this.dropShadow ? iglyph.getShadowOffset() : 0.0F;
            IVertexBuilder ivertexbuilder = this.bufferSource.getBuffer(texturedglyph.renderType(this.seeThrough));
            FontRenderer.this.renderChar(texturedglyph, flag, p_accept_2_.isItalic(), f5, this.x + f4, this.y + f4, this.pose, ivertexbuilder, f, f1, f2, f3, this.packedLightCoords);
         }

         float f6 = iglyph.getAdvance(flag);
         float f7 = this.dropShadow ? 1.0F : 0.0F;
         if (p_accept_2_.isStrikethrough()) {
            this.addEffect(new TexturedGlyph.Effect(this.x + f7 - 1.0F, this.y + f7 + 4.5F, this.x + f7 + f6, this.y + f7 + 4.5F - 1.0F, 0.01F, f, f1, f2, f3));
         }

         if (p_accept_2_.isUnderlined()) {
            this.addEffect(new TexturedGlyph.Effect(this.x + f7 - 1.0F, this.y + f7 + 9.0F, this.x + f7 + f6, this.y + f7 + 9.0F - 1.0F, 0.01F, f, f1, f2, f3));
         }

         this.x += f6;
         return true;
      }

      public float finish(int p_238441_1_, float p_238441_2_) {
         if (p_238441_1_ != 0) {
            float f = (float)(p_238441_1_ >> 24 & 255) / 255.0F;
            float f1 = (float)(p_238441_1_ >> 16 & 255) / 255.0F;
            float f2 = (float)(p_238441_1_ >> 8 & 255) / 255.0F;
            float f3 = (float)(p_238441_1_ & 255) / 255.0F;
            this.addEffect(new TexturedGlyph.Effect(p_238441_2_ - 1.0F, this.y + 9.0F, this.x + 1.0F, this.y - 1.0F, 0.01F, f1, f2, f3, f));
         }

         if (this.effects != null) {
            TexturedGlyph texturedglyph = FontRenderer.this.getFontSet(Style.DEFAULT_FONT).whiteGlyph();
            IVertexBuilder ivertexbuilder = this.bufferSource.getBuffer(texturedglyph.renderType(this.seeThrough));

            for(TexturedGlyph.Effect texturedglyph$effect : this.effects) {
               texturedglyph.renderEffect(texturedglyph$effect, this.pose, ivertexbuilder, this.packedLightCoords);
            }
         }

         return this.x;
      }
   }
}
