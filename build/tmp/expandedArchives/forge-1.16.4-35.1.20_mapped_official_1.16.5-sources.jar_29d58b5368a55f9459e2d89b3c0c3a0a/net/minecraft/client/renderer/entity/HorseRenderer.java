package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.renderer.entity.layers.HorseMarkingsLayer;
import net.minecraft.client.renderer.entity.layers.LeatherHorseArmorLayer;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.entity.passive.horse.CoatColors;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class HorseRenderer extends AbstractHorseRenderer<HorseEntity, HorseModel<HorseEntity>> {
   private static final Map<CoatColors, ResourceLocation> LOCATION_BY_VARIANT = Util.make(Maps.newEnumMap(CoatColors.class), (p_239384_0_) -> {
      p_239384_0_.put(CoatColors.WHITE, new ResourceLocation("textures/entity/horse/horse_white.png"));
      p_239384_0_.put(CoatColors.CREAMY, new ResourceLocation("textures/entity/horse/horse_creamy.png"));
      p_239384_0_.put(CoatColors.CHESTNUT, new ResourceLocation("textures/entity/horse/horse_chestnut.png"));
      p_239384_0_.put(CoatColors.BROWN, new ResourceLocation("textures/entity/horse/horse_brown.png"));
      p_239384_0_.put(CoatColors.BLACK, new ResourceLocation("textures/entity/horse/horse_black.png"));
      p_239384_0_.put(CoatColors.GRAY, new ResourceLocation("textures/entity/horse/horse_gray.png"));
      p_239384_0_.put(CoatColors.DARKBROWN, new ResourceLocation("textures/entity/horse/horse_darkbrown.png"));
   });

   public HorseRenderer(EntityRendererManager p_i47205_1_) {
      super(p_i47205_1_, new HorseModel<>(0.0F), 1.1F);
      this.addLayer(new HorseMarkingsLayer(this));
      this.addLayer(new LeatherHorseArmorLayer(this));
   }

   public ResourceLocation getTextureLocation(HorseEntity p_110775_1_) {
      return LOCATION_BY_VARIANT.get(p_110775_1_.getVariant());
   }
}
