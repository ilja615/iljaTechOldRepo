package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PillagerRenderer extends IllagerRenderer<PillagerEntity> {
   private static final ResourceLocation PILLAGER = new ResourceLocation("textures/entity/illager/pillager.png");

   public PillagerRenderer(EntityRendererManager p_i50959_1_) {
      super(p_i50959_1_, new IllagerModel<>(0.0F, 0.0F, 64, 64), 0.5F);
      this.addLayer(new HeldItemLayer<>(this));
   }

   public ResourceLocation getTextureLocation(PillagerEntity p_110775_1_) {
      return PILLAGER;
   }
}
