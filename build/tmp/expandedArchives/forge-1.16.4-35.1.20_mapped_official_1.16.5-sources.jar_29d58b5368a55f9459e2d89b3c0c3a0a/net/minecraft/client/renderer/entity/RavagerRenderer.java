package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.RavagerModel;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RavagerRenderer extends MobRenderer<RavagerEntity, RavagerModel> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/illager/ravager.png");

   public RavagerRenderer(EntityRendererManager p_i50958_1_) {
      super(p_i50958_1_, new RavagerModel(), 1.1F);
   }

   public ResourceLocation getTextureLocation(RavagerEntity p_110775_1_) {
      return TEXTURE_LOCATION;
   }
}
