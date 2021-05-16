package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FireworkRocketRenderer extends EntityRenderer<FireworkRocketEntity> {
   private final net.minecraft.client.renderer.ItemRenderer itemRenderer;

   public FireworkRocketRenderer(EntityRendererManager p_i50970_1_, net.minecraft.client.renderer.ItemRenderer p_i50970_2_) {
      super(p_i50970_1_);
      this.itemRenderer = p_i50970_2_;
   }

   public void render(FireworkRocketEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      p_225623_4_.pushPose();
      p_225623_4_.mulPose(this.entityRenderDispatcher.cameraOrientation());
      p_225623_4_.mulPose(Vector3f.YP.rotationDegrees(180.0F));
      if (p_225623_1_.isShotAtAngle()) {
         p_225623_4_.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
         p_225623_4_.mulPose(Vector3f.YP.rotationDegrees(180.0F));
         p_225623_4_.mulPose(Vector3f.XP.rotationDegrees(90.0F));
      }

      this.itemRenderer.renderStatic(p_225623_1_.getItem(), ItemCameraTransforms.TransformType.GROUND, p_225623_6_, OverlayTexture.NO_OVERLAY, p_225623_4_, p_225623_5_);
      p_225623_4_.popPose();
      super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
   }

   public ResourceLocation getTextureLocation(FireworkRocketEntity p_110775_1_) {
      return AtlasTexture.LOCATION_BLOCKS;
   }
}
