package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.SnowmanHeadLayer;
import net.minecraft.client.renderer.entity.model.SnowManModel;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SnowManRenderer extends MobRenderer<SnowGolemEntity, SnowManModel<SnowGolemEntity>> {
   private static final ResourceLocation SNOW_GOLEM_LOCATION = new ResourceLocation("textures/entity/snow_golem.png");

   public SnowManRenderer(EntityRendererManager p_i46140_1_) {
      super(p_i46140_1_, new SnowManModel<>(), 0.5F);
      this.addLayer(new SnowmanHeadLayer(this));
   }

   public ResourceLocation getTextureLocation(SnowGolemEntity p_110775_1_) {
      return SNOW_GOLEM_LOCATION;
   }
}
