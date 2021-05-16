package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.DolphinCarriedItemLayer;
import net.minecraft.client.renderer.entity.model.DolphinModel;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DolphinRenderer extends MobRenderer<DolphinEntity, DolphinModel<DolphinEntity>> {
   private static final ResourceLocation DOLPHIN_LOCATION = new ResourceLocation("textures/entity/dolphin.png");

   public DolphinRenderer(EntityRendererManager p_i48949_1_) {
      super(p_i48949_1_, new DolphinModel<>(), 0.7F);
      this.addLayer(new DolphinCarriedItemLayer(this));
   }

   public ResourceLocation getTextureLocation(DolphinEntity p_110775_1_) {
      return DOLPHIN_LOCATION;
   }
}
