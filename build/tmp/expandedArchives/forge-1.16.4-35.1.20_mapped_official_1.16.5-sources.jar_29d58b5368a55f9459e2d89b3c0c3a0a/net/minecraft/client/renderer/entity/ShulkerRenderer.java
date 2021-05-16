package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.layers.ShulkerColorLayer;
import net.minecraft.client.renderer.entity.model.ShulkerModel;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShulkerRenderer extends MobRenderer<ShulkerEntity, ShulkerModel<ShulkerEntity>> {
   public static final ResourceLocation DEFAULT_TEXTURE_LOCATION = new ResourceLocation("textures/" + Atlases.DEFAULT_SHULKER_TEXTURE_LOCATION.texture().getPath() + ".png");
   public static final ResourceLocation[] TEXTURE_LOCATION = Atlases.SHULKER_TEXTURE_LOCATION.stream().map((p_229125_0_) -> {
      return new ResourceLocation("textures/" + p_229125_0_.texture().getPath() + ".png");
   }).toArray((p_229124_0_) -> {
      return new ResourceLocation[p_229124_0_];
   });

   public ShulkerRenderer(EntityRendererManager p_i47194_1_) {
      super(p_i47194_1_, new ShulkerModel<>(), 0.0F);
      this.addLayer(new ShulkerColorLayer(this));
   }

   public Vector3d getRenderOffset(ShulkerEntity p_225627_1_, float p_225627_2_) {
      int i = p_225627_1_.getClientSideTeleportInterpolation();
      if (i > 0 && p_225627_1_.hasValidInterpolationPositions()) {
         BlockPos blockpos = p_225627_1_.getAttachPosition();
         BlockPos blockpos1 = p_225627_1_.getOldAttachPosition();
         double d0 = (double)((float)i - p_225627_2_) / 6.0D;
         d0 = d0 * d0;
         double d1 = (double)(blockpos.getX() - blockpos1.getX()) * d0;
         double d2 = (double)(blockpos.getY() - blockpos1.getY()) * d0;
         double d3 = (double)(blockpos.getZ() - blockpos1.getZ()) * d0;
         return new Vector3d(-d1, -d2, -d3);
      } else {
         return super.getRenderOffset(p_225627_1_, p_225627_2_);
      }
   }

   public boolean shouldRender(ShulkerEntity p_225626_1_, ClippingHelper p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
      if (super.shouldRender(p_225626_1_, p_225626_2_, p_225626_3_, p_225626_5_, p_225626_7_)) {
         return true;
      } else {
         if (p_225626_1_.getClientSideTeleportInterpolation() > 0 && p_225626_1_.hasValidInterpolationPositions()) {
            Vector3d vector3d = Vector3d.atLowerCornerOf(p_225626_1_.getAttachPosition());
            Vector3d vector3d1 = Vector3d.atLowerCornerOf(p_225626_1_.getOldAttachPosition());
            if (p_225626_2_.isVisible(new AxisAlignedBB(vector3d1.x, vector3d1.y, vector3d1.z, vector3d.x, vector3d.y, vector3d.z))) {
               return true;
            }
         }

         return false;
      }
   }

   public ResourceLocation getTextureLocation(ShulkerEntity p_110775_1_) {
      return p_110775_1_.getColor() == null ? DEFAULT_TEXTURE_LOCATION : TEXTURE_LOCATION[p_110775_1_.getColor().getId()];
   }

   protected void setupRotations(ShulkerEntity p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
      super.setupRotations(p_225621_1_, p_225621_2_, p_225621_3_, p_225621_4_ + 180.0F, p_225621_5_);
      p_225621_2_.translate(0.0D, 0.5D, 0.0D);
      p_225621_2_.mulPose(p_225621_1_.getAttachFace().getOpposite().getRotation());
      p_225621_2_.translate(0.0D, -0.5D, 0.0D);
   }
}
