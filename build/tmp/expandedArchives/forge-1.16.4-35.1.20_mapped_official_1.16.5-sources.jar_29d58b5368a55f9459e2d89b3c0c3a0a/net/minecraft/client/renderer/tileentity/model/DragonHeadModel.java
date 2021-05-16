package net.minecraft.client.renderer.tileentity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DragonHeadModel extends GenericHeadModel {
   private final ModelRenderer head;
   private final ModelRenderer jaw;

   public DragonHeadModel(float p_i46588_1_) {
      this.texWidth = 256;
      this.texHeight = 256;
      float f = -16.0F;
      this.head = new ModelRenderer(this);
      this.head.addBox("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, p_i46588_1_, 176, 44);
      this.head.addBox("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, p_i46588_1_, 112, 30);
      this.head.mirror = true;
      this.head.addBox("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, p_i46588_1_, 0, 0);
      this.head.addBox("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, p_i46588_1_, 112, 0);
      this.head.mirror = false;
      this.head.addBox("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, p_i46588_1_, 0, 0);
      this.head.addBox("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, p_i46588_1_, 112, 0);
      this.jaw = new ModelRenderer(this);
      this.jaw.setPos(0.0F, 4.0F, -8.0F);
      this.jaw.addBox("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16, p_i46588_1_, 176, 65);
      this.head.addChild(this.jaw);
   }

   public void setupAnim(float p_225603_1_, float p_225603_2_, float p_225603_3_) {
      this.jaw.xRot = (float)(Math.sin((double)(p_225603_1_ * (float)Math.PI * 0.2F)) + 1.0D) * 0.2F;
      this.head.yRot = p_225603_2_ * ((float)Math.PI / 180F);
      this.head.xRot = p_225603_3_ * ((float)Math.PI / 180F);
   }

   public void renderToBuffer(MatrixStack p_225598_1_, IVertexBuilder p_225598_2_, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
      p_225598_1_.pushPose();
      p_225598_1_.translate(0.0D, (double)-0.374375F, 0.0D);
      p_225598_1_.scale(0.75F, 0.75F, 0.75F);
      this.head.render(p_225598_1_, p_225598_2_, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
      p_225598_1_.popPose();
   }
}
