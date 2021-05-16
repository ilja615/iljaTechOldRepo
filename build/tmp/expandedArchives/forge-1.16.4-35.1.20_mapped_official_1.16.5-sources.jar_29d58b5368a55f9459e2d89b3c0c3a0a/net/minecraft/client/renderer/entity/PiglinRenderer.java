package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PiglinModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.piglin.AbstractPiglinEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PiglinRenderer extends BipedRenderer<MobEntity, PiglinModel<MobEntity>> {
   private static final Map<EntityType<?>, ResourceLocation> resourceLocations = ImmutableMap.of(EntityType.PIGLIN, new ResourceLocation("textures/entity/piglin/piglin.png"), EntityType.ZOMBIFIED_PIGLIN, new ResourceLocation("textures/entity/piglin/zombified_piglin.png"), EntityType.PIGLIN_BRUTE, new ResourceLocation("textures/entity/piglin/piglin_brute.png"));

   public PiglinRenderer(EntityRendererManager p_i232472_1_, boolean p_i232472_2_) {
      super(p_i232472_1_, createModel(p_i232472_2_), 0.5F, 1.0019531F, 1.0F, 1.0019531F);
      this.addLayer(new BipedArmorLayer<>(this, new BipedModel(0.5F), new BipedModel(1.02F)));
   }

   private static PiglinModel<MobEntity> createModel(boolean p_239395_0_) {
      PiglinModel<MobEntity> piglinmodel = new PiglinModel<>(0.0F, 64, 64);
      if (p_239395_0_) {
         piglinmodel.earLeft.visible = false;
      }

      return piglinmodel;
   }

   public ResourceLocation getTextureLocation(MobEntity p_110775_1_) {
      ResourceLocation resourcelocation = resourceLocations.get(p_110775_1_.getType());
      if (resourcelocation == null) {
         throw new IllegalArgumentException("I don't know what texture to use for " + p_110775_1_.getType());
      } else {
         return resourcelocation;
      }
   }

   protected boolean isShaking(MobEntity p_230495_1_) {
      return p_230495_1_ instanceof AbstractPiglinEntity && ((AbstractPiglinEntity)p_230495_1_).isConverting();
   }
}
