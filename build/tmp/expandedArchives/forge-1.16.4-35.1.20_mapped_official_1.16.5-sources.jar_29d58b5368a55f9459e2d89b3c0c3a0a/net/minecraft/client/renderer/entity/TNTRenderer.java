package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TNTRenderer extends EntityRenderer<TNTEntity> {
   public TNTRenderer(EntityRendererManager p_i46134_1_) {
      super(p_i46134_1_);
      this.shadowRadius = 0.5F;
   }

   public void render(TNTEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      p_225623_4_.pushPose();
      p_225623_4_.translate(0.0D, 0.5D, 0.0D);
      if ((float)p_225623_1_.getLife() - p_225623_3_ + 1.0F < 10.0F) {
         float f = 1.0F - ((float)p_225623_1_.getLife() - p_225623_3_ + 1.0F) / 10.0F;
         f = MathHelper.clamp(f, 0.0F, 1.0F);
         f = f * f;
         f = f * f;
         float f1 = 1.0F + f * 0.3F;
         p_225623_4_.scale(f1, f1, f1);
      }

      p_225623_4_.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
      p_225623_4_.translate(-0.5D, -0.5D, 0.5D);
      p_225623_4_.mulPose(Vector3f.YP.rotationDegrees(90.0F));
      TNTMinecartRenderer.renderWhiteSolidBlock(Blocks.TNT.defaultBlockState(), p_225623_4_, p_225623_5_, p_225623_6_, p_225623_1_.getLife() / 5 % 2 == 0);
      p_225623_4_.popPose();
      super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
   }

   public ResourceLocation getTextureLocation(TNTEntity p_110775_1_) {
      return AtlasTexture.LOCATION_BLOCKS;
   }
}
