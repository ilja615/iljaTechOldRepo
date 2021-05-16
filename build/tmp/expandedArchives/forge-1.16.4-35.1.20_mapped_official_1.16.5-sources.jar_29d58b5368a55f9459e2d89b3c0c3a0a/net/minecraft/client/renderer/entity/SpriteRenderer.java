package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpriteRenderer<T extends Entity & IRendersAsItem> extends EntityRenderer<T> {
   private final net.minecraft.client.renderer.ItemRenderer itemRenderer;
   private final float scale;
   private final boolean fullBright;

   public SpriteRenderer(EntityRendererManager p_i226035_1_, net.minecraft.client.renderer.ItemRenderer p_i226035_2_, float p_i226035_3_, boolean p_i226035_4_) {
      super(p_i226035_1_);
      this.itemRenderer = p_i226035_2_;
      this.scale = p_i226035_3_;
      this.fullBright = p_i226035_4_;
   }

   public SpriteRenderer(EntityRendererManager p_i50957_1_, net.minecraft.client.renderer.ItemRenderer p_i50957_2_) {
      this(p_i50957_1_, p_i50957_2_, 1.0F, false);
   }

   protected int getBlockLightLevel(T p_225624_1_, BlockPos p_225624_2_) {
      return this.fullBright ? 15 : super.getBlockLightLevel(p_225624_1_, p_225624_2_);
   }

   public void render(T p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      if (p_225623_1_.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(p_225623_1_) < 12.25D)) {
         p_225623_4_.pushPose();
         p_225623_4_.scale(this.scale, this.scale, this.scale);
         p_225623_4_.mulPose(this.entityRenderDispatcher.cameraOrientation());
         p_225623_4_.mulPose(Vector3f.YP.rotationDegrees(180.0F));
         this.itemRenderer.renderStatic(p_225623_1_.getItem(), ItemCameraTransforms.TransformType.GROUND, p_225623_6_, OverlayTexture.NO_OVERLAY, p_225623_4_, p_225623_5_);
         p_225623_4_.popPose();
         super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
      }
   }

   public ResourceLocation getTextureLocation(Entity p_110775_1_) {
      return AtlasTexture.LOCATION_BLOCKS;
   }
}
