package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.SpiderModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpiderEyesLayer<T extends Entity, M extends SpiderModel<T>> extends AbstractEyesLayer<T, M> {
   private static final RenderType SPIDER_EYES = RenderType.eyes(new ResourceLocation("textures/entity/spider_eyes.png"));

   public SpiderEyesLayer(IEntityRenderer<T, M> p_i50921_1_) {
      super(p_i50921_1_);
   }

   public RenderType renderType() {
      return SPIDER_EYES;
   }
}
