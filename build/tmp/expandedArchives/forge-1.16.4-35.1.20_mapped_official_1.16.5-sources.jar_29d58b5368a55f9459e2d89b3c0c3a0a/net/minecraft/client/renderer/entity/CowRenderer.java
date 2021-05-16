package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.CowModel;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CowRenderer extends MobRenderer<CowEntity, CowModel<CowEntity>> {
   private static final ResourceLocation COW_LOCATION = new ResourceLocation("textures/entity/cow/cow.png");

   public CowRenderer(EntityRendererManager p_i47210_1_) {
      super(p_i47210_1_, new CowModel<>(), 0.7F);
   }

   public ResourceLocation getTextureLocation(CowEntity p_110775_1_) {
      return COW_LOCATION;
   }
}
