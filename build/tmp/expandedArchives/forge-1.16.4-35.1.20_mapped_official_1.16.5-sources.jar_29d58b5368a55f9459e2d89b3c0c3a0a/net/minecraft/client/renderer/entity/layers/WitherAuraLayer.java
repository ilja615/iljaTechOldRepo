package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.WitherModel;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WitherAuraLayer extends EnergyLayer<WitherEntity, WitherModel<WitherEntity>> {
   private static final ResourceLocation WITHER_ARMOR_LOCATION = new ResourceLocation("textures/entity/wither/wither_armor.png");
   private final WitherModel<WitherEntity> model = new WitherModel<>(0.5F);

   public WitherAuraLayer(IEntityRenderer<WitherEntity, WitherModel<WitherEntity>> p_i50915_1_) {
      super(p_i50915_1_);
   }

   protected float xOffset(float p_225634_1_) {
      return MathHelper.cos(p_225634_1_ * 0.02F) * 3.0F;
   }

   protected ResourceLocation getTextureLocation() {
      return WITHER_ARMOR_LOCATION;
   }

   protected EntityModel<WitherEntity> model() {
      return this.model;
   }
}
