package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.renderer.entity.layers.MooshroomMushroomLayer;
import net.minecraft.client.renderer.entity.model.CowModel;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MooshroomRenderer extends MobRenderer<MooshroomEntity, CowModel<MooshroomEntity>> {
   private static final Map<MooshroomEntity.Type, ResourceLocation> TEXTURES = Util.make(Maps.newHashMap(), (p_217773_0_) -> {
      p_217773_0_.put(MooshroomEntity.Type.BROWN, new ResourceLocation("textures/entity/cow/brown_mooshroom.png"));
      p_217773_0_.put(MooshroomEntity.Type.RED, new ResourceLocation("textures/entity/cow/red_mooshroom.png"));
   });

   public MooshroomRenderer(EntityRendererManager p_i47200_1_) {
      super(p_i47200_1_, new CowModel<>(), 0.7F);
      this.addLayer(new MooshroomMushroomLayer<>(this));
   }

   public ResourceLocation getTextureLocation(MooshroomEntity p_110775_1_) {
      return TEXTURES.get(p_110775_1_.getMushroomType());
   }
}
