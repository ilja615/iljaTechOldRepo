package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.CreeperModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreeperChargeLayer extends EnergyLayer<CreeperEntity, CreeperModel<CreeperEntity>> {
   private static final ResourceLocation POWER_LOCATION = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
   private final CreeperModel<CreeperEntity> model = new CreeperModel<>(2.0F);

   public CreeperChargeLayer(IEntityRenderer<CreeperEntity, CreeperModel<CreeperEntity>> p_i50947_1_) {
      super(p_i50947_1_);
   }

   protected float xOffset(float p_225634_1_) {
      return p_225634_1_ * 0.01F;
   }

   protected ResourceLocation getTextureLocation() {
      return POWER_LOCATION;
   }

   protected EntityModel<CreeperEntity> model() {
      return this.model;
   }
}
