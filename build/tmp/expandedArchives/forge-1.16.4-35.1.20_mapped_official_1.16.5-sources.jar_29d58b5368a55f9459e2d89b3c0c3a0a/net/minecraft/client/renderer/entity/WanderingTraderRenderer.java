package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WanderingTraderRenderer extends MobRenderer<WanderingTraderEntity, VillagerModel<WanderingTraderEntity>> {
   private static final ResourceLocation VILLAGER_BASE_SKIN = new ResourceLocation("textures/entity/wandering_trader.png");

   public WanderingTraderRenderer(EntityRendererManager p_i50953_1_) {
      super(p_i50953_1_, new VillagerModel<>(0.0F), 0.5F);
      this.addLayer(new HeadLayer<>(this));
      this.addLayer(new CrossedArmsItemLayer<>(this));
   }

   public ResourceLocation getTextureLocation(WanderingTraderEntity p_110775_1_) {
      return VILLAGER_BASE_SKIN;
   }

   protected void scale(WanderingTraderEntity p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
      float f = 0.9375F;
      p_225620_2_.scale(0.9375F, 0.9375F, 0.9375F);
   }
}
