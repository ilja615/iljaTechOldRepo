package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemFrameRenderer extends EntityRenderer<ItemFrameEntity> {
   private static final ModelResourceLocation FRAME_LOCATION = new ModelResourceLocation("item_frame", "map=false");
   private static final ModelResourceLocation MAP_FRAME_LOCATION = new ModelResourceLocation("item_frame", "map=true");
   private final Minecraft minecraft = Minecraft.getInstance();
   private final net.minecraft.client.renderer.ItemRenderer itemRenderer;

   public ItemFrameRenderer(EntityRendererManager p_i46166_1_, net.minecraft.client.renderer.ItemRenderer p_i46166_2_) {
      super(p_i46166_1_);
      this.itemRenderer = p_i46166_2_;
   }

   public void render(ItemFrameEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
      p_225623_4_.pushPose();
      Direction direction = p_225623_1_.getDirection();
      Vector3d vector3d = this.getRenderOffset(p_225623_1_, p_225623_3_);
      p_225623_4_.translate(-vector3d.x(), -vector3d.y(), -vector3d.z());
      double d0 = 0.46875D;
      p_225623_4_.translate((double)direction.getStepX() * 0.46875D, (double)direction.getStepY() * 0.46875D, (double)direction.getStepZ() * 0.46875D);
      p_225623_4_.mulPose(Vector3f.XP.rotationDegrees(p_225623_1_.xRot));
      p_225623_4_.mulPose(Vector3f.YP.rotationDegrees(180.0F - p_225623_1_.yRot));
      boolean flag = p_225623_1_.isInvisible();
      if (!flag) {
         BlockRendererDispatcher blockrendererdispatcher = this.minecraft.getBlockRenderer();
         ModelManager modelmanager = blockrendererdispatcher.getBlockModelShaper().getModelManager();
         ModelResourceLocation modelresourcelocation = p_225623_1_.getItem().getItem() instanceof FilledMapItem ? MAP_FRAME_LOCATION : FRAME_LOCATION;
         p_225623_4_.pushPose();
         p_225623_4_.translate(-0.5D, -0.5D, -0.5D);
         blockrendererdispatcher.getModelRenderer().renderModel(p_225623_4_.last(), p_225623_5_.getBuffer(Atlases.solidBlockSheet()), (BlockState)null, modelmanager.getModel(modelresourcelocation), 1.0F, 1.0F, 1.0F, p_225623_6_, OverlayTexture.NO_OVERLAY);
         p_225623_4_.popPose();
      }

      ItemStack itemstack = p_225623_1_.getItem();
      if (!itemstack.isEmpty()) {
         MapData mapdata = FilledMapItem.getOrCreateSavedData(itemstack, p_225623_1_.level);
         if (flag) {
            p_225623_4_.translate(0.0D, 0.0D, 0.5D);
         } else {
            p_225623_4_.translate(0.0D, 0.0D, 0.4375D);
         }

         int i = mapdata != null ? p_225623_1_.getRotation() % 4 * 2 : p_225623_1_.getRotation();
         p_225623_4_.mulPose(Vector3f.ZP.rotationDegrees((float)i * 360.0F / 8.0F));
         if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderItemInFrameEvent(p_225623_1_, this, p_225623_4_, p_225623_5_, p_225623_6_))) {
         if (mapdata != null) {
            p_225623_4_.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
            float f = 0.0078125F;
            p_225623_4_.scale(0.0078125F, 0.0078125F, 0.0078125F);
            p_225623_4_.translate(-64.0D, -64.0D, 0.0D);
            p_225623_4_.translate(0.0D, 0.0D, -1.0D);
            if (mapdata != null) {
               this.minecraft.gameRenderer.getMapRenderer().render(p_225623_4_, p_225623_5_, mapdata, true, p_225623_6_);
            }
         } else {
            p_225623_4_.scale(0.5F, 0.5F, 0.5F);
            this.itemRenderer.renderStatic(itemstack, ItemCameraTransforms.TransformType.FIXED, p_225623_6_, OverlayTexture.NO_OVERLAY, p_225623_4_, p_225623_5_);
         }
         }
      }

      p_225623_4_.popPose();
   }

   public Vector3d getRenderOffset(ItemFrameEntity p_225627_1_, float p_225627_2_) {
      return new Vector3d((double)((float)p_225627_1_.getDirection().getStepX() * 0.3F), -0.25D, (double)((float)p_225627_1_.getDirection().getStepZ() * 0.3F));
   }

   public ResourceLocation getTextureLocation(ItemFrameEntity p_110775_1_) {
      return AtlasTexture.LOCATION_BLOCKS;
   }

   protected boolean shouldShowName(ItemFrameEntity p_177070_1_) {
      if (Minecraft.renderNames() && !p_177070_1_.getItem().isEmpty() && p_177070_1_.getItem().hasCustomHoverName() && this.entityRenderDispatcher.crosshairPickEntity == p_177070_1_) {
         double d0 = this.entityRenderDispatcher.distanceToSqr(p_177070_1_);
         float f = p_177070_1_.isDiscrete() ? 32.0F : 64.0F;
         return d0 < (double)(f * f);
      } else {
         return false;
      }
   }

   protected void renderNameTag(ItemFrameEntity p_225629_1_, ITextComponent p_225629_2_, MatrixStack p_225629_3_, IRenderTypeBuffer p_225629_4_, int p_225629_5_) {
      super.renderNameTag(p_225629_1_, p_225629_1_.getItem().getHoverName(), p_225629_3_, p_225629_4_, p_225629_5_);
   }
}
