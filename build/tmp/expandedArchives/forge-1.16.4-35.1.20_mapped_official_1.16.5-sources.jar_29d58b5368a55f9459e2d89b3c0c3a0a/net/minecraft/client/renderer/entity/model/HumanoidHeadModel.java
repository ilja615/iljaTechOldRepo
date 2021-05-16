package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HumanoidHeadModel extends GenericHeadModel {
   private final ModelRenderer hat = new ModelRenderer(this, 32, 0);

   public HumanoidHeadModel() {
      super(0, 0, 64, 64);
      this.hat.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.25F);
      this.hat.setPos(0.0F, 0.0F, 0.0F);
   }

   public void setupAnim(float p_225603_1_, float p_225603_2_, float p_225603_3_) {
      super.setupAnim(p_225603_1_, p_225603_2_, p_225603_3_);
      this.hat.yRot = this.head.yRot;
      this.hat.xRot = this.head.xRot;
   }

   public void renderToBuffer(MatrixStack p_225598_1_, IVertexBuilder p_225598_2_, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
      super.renderToBuffer(p_225598_1_, p_225598_2_, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
      this.hat.render(p_225598_1_, p_225598_2_, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
   }
}
