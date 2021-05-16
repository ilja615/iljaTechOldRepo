package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.OcelotModel;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OcelotRenderer extends MobRenderer<OcelotEntity, OcelotModel<OcelotEntity>> {
   private static final ResourceLocation CAT_OCELOT_LOCATION = new ResourceLocation("textures/entity/cat/ocelot.png");

   public OcelotRenderer(EntityRendererManager p_i47199_1_) {
      super(p_i47199_1_, new OcelotModel<>(0.0F), 0.4F);
   }

   public ResourceLocation getTextureLocation(OcelotEntity p_110775_1_) {
      return CAT_OCELOT_LOCATION;
   }
}
