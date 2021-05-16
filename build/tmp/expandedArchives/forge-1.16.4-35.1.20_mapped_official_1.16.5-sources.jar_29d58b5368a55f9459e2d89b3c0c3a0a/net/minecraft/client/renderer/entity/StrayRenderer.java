package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.StayClothingLayer;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StrayRenderer extends SkeletonRenderer {
   private static final ResourceLocation STRAY_SKELETON_LOCATION = new ResourceLocation("textures/entity/skeleton/stray.png");

   public StrayRenderer(EntityRendererManager p_i47191_1_) {
      super(p_i47191_1_);
      this.addLayer(new StayClothingLayer<>(this));
   }

   public ResourceLocation getTextureLocation(AbstractSkeletonEntity p_110775_1_) {
      return STRAY_SKELETON_LOCATION;
   }
}
