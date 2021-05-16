package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BookModel extends Model {
   private final ModelRenderer leftLid = (new ModelRenderer(64, 32, 0, 0)).addBox(-6.0F, -5.0F, -0.005F, 6.0F, 10.0F, 0.005F);
   private final ModelRenderer rightLid = (new ModelRenderer(64, 32, 16, 0)).addBox(0.0F, -5.0F, -0.005F, 6.0F, 10.0F, 0.005F);
   private final ModelRenderer leftPages;
   private final ModelRenderer rightPages;
   private final ModelRenderer flipPage1;
   private final ModelRenderer flipPage2;
   private final ModelRenderer seam = (new ModelRenderer(64, 32, 12, 0)).addBox(-1.0F, -5.0F, 0.0F, 2.0F, 10.0F, 0.005F);
   private final List<ModelRenderer> parts;

   public BookModel() {
      super(RenderType::entitySolid);
      this.leftPages = (new ModelRenderer(64, 32, 0, 10)).addBox(0.0F, -4.0F, -0.99F, 5.0F, 8.0F, 1.0F);
      this.rightPages = (new ModelRenderer(64, 32, 12, 10)).addBox(0.0F, -4.0F, -0.01F, 5.0F, 8.0F, 1.0F);
      this.flipPage1 = (new ModelRenderer(64, 32, 24, 10)).addBox(0.0F, -4.0F, 0.0F, 5.0F, 8.0F, 0.005F);
      this.flipPage2 = (new ModelRenderer(64, 32, 24, 10)).addBox(0.0F, -4.0F, 0.0F, 5.0F, 8.0F, 0.005F);
      this.parts = ImmutableList.of(this.leftLid, this.rightLid, this.seam, this.leftPages, this.rightPages, this.flipPage1, this.flipPage2);
      this.leftLid.setPos(0.0F, 0.0F, -1.0F);
      this.rightLid.setPos(0.0F, 0.0F, 1.0F);
      this.seam.yRot = ((float)Math.PI / 2F);
   }

   public void renderToBuffer(MatrixStack p_225598_1_, IVertexBuilder p_225598_2_, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
      this.render(p_225598_1_, p_225598_2_, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
   }

   public void render(MatrixStack p_228249_1_, IVertexBuilder p_228249_2_, int p_228249_3_, int p_228249_4_, float p_228249_5_, float p_228249_6_, float p_228249_7_, float p_228249_8_) {
      this.parts.forEach((p_228248_8_) -> {
         p_228248_8_.render(p_228249_1_, p_228249_2_, p_228249_3_, p_228249_4_, p_228249_5_, p_228249_6_, p_228249_7_, p_228249_8_);
      });
   }

   public void setupAnim(float p_228247_1_, float p_228247_2_, float p_228247_3_, float p_228247_4_) {
      float f = (MathHelper.sin(p_228247_1_ * 0.02F) * 0.1F + 1.25F) * p_228247_4_;
      this.leftLid.yRot = (float)Math.PI + f;
      this.rightLid.yRot = -f;
      this.leftPages.yRot = f;
      this.rightPages.yRot = -f;
      this.flipPage1.yRot = f - f * 2.0F * p_228247_2_;
      this.flipPage2.yRot = f - f * 2.0F * p_228247_3_;
      this.leftPages.x = MathHelper.sin(f);
      this.rightPages.x = MathHelper.sin(f);
      this.flipPage1.x = MathHelper.sin(f);
      this.flipPage2.x = MathHelper.sin(f);
   }
}
