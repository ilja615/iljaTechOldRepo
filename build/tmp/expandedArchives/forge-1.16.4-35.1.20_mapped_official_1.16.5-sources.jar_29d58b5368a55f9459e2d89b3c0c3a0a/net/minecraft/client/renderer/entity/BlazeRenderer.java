package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.BlazeModel;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlazeRenderer extends MobRenderer<BlazeEntity, BlazeModel<BlazeEntity>> {
   private static final ResourceLocation BLAZE_LOCATION = new ResourceLocation("textures/entity/blaze.png");

   public BlazeRenderer(EntityRendererManager p_i46191_1_) {
      super(p_i46191_1_, new BlazeModel<>(), 0.5F);
   }

   protected int getBlockLightLevel(BlazeEntity p_225624_1_, BlockPos p_225624_2_) {
      return 15;
   }

   public ResourceLocation getTextureLocation(BlazeEntity p_110775_1_) {
      return BLAZE_LOCATION;
   }
}
