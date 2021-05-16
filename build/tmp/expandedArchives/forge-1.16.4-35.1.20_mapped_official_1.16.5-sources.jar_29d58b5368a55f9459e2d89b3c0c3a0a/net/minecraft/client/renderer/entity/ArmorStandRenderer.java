package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.ArmorStandArmorModel;
import net.minecraft.client.renderer.entity.model.ArmorStandModel;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArmorStandRenderer extends LivingRenderer<ArmorStandEntity, ArmorStandArmorModel> {
   public static final ResourceLocation DEFAULT_SKIN_LOCATION = new ResourceLocation("textures/entity/armorstand/wood.png");

   public ArmorStandRenderer(EntityRendererManager p_i46195_1_) {
      super(p_i46195_1_, new ArmorStandModel(), 0.0F);
      this.addLayer(new BipedArmorLayer<>(this, new ArmorStandArmorModel(0.5F), new ArmorStandArmorModel(1.0F)));
      this.addLayer(new HeldItemLayer<>(this));
      this.addLayer(new ElytraLayer<>(this));
      this.addLayer(new HeadLayer<>(this));
   }

   public ResourceLocation getTextureLocation(ArmorStandEntity p_110775_1_) {
      return DEFAULT_SKIN_LOCATION;
   }

   protected void setupRotations(ArmorStandEntity p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
      p_225621_2_.mulPose(Vector3f.YP.rotationDegrees(180.0F - p_225621_4_));
      float f = (float)(p_225621_1_.level.getGameTime() - p_225621_1_.lastHit) + p_225621_5_;
      if (f < 5.0F) {
         p_225621_2_.mulPose(Vector3f.YP.rotationDegrees(MathHelper.sin(f / 1.5F * (float)Math.PI) * 3.0F));
      }

   }

   protected boolean shouldShowName(ArmorStandEntity p_177070_1_) {
      double d0 = this.entityRenderDispatcher.distanceToSqr(p_177070_1_);
      float f = p_177070_1_.isCrouching() ? 32.0F : 64.0F;
      return d0 >= (double)(f * f) ? false : p_177070_1_.isCustomNameVisible();
   }

   @Nullable
   protected RenderType getRenderType(ArmorStandEntity p_230496_1_, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_) {
      if (!p_230496_1_.isMarker()) {
         return super.getRenderType(p_230496_1_, p_230496_2_, p_230496_3_, p_230496_4_);
      } else {
         ResourceLocation resourcelocation = this.getTextureLocation(p_230496_1_);
         if (p_230496_3_) {
            return RenderType.entityTranslucent(resourcelocation, false);
         } else {
            return p_230496_2_ ? RenderType.entityCutoutNoCull(resourcelocation, false) : null;
         }
      }
   }
}
