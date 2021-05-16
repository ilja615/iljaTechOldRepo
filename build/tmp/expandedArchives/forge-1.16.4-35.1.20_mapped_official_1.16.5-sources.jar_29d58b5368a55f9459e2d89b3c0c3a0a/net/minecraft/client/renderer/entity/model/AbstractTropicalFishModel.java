package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractTropicalFishModel<E extends Entity> extends SegmentedModel<E> {
   private float r = 1.0F;
   private float g = 1.0F;
   private float b = 1.0F;

   public void setColor(float p_228257_1_, float p_228257_2_, float p_228257_3_) {
      this.r = p_228257_1_;
      this.g = p_228257_2_;
      this.b = p_228257_3_;
   }

   public void renderToBuffer(MatrixStack p_225598_1_, IVertexBuilder p_225598_2_, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
      super.renderToBuffer(p_225598_1_, p_225598_2_, p_225598_3_, p_225598_4_, this.r * p_225598_5_, this.g * p_225598_6_, this.b * p_225598_7_, p_225598_8_);
   }
}
