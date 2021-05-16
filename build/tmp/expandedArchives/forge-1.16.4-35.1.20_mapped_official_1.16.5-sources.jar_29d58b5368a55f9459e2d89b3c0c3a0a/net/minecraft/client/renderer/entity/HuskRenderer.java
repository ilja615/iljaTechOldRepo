package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HuskRenderer extends ZombieRenderer {
   private static final ResourceLocation HUSK_LOCATION = new ResourceLocation("textures/entity/zombie/husk.png");

   public HuskRenderer(EntityRendererManager p_i47204_1_) {
      super(p_i47204_1_);
   }

   protected void scale(ZombieEntity p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
      float f = 1.0625F;
      p_225620_2_.scale(1.0625F, 1.0625F, 1.0625F);
      super.scale(p_225620_1_, p_225620_2_, p_225620_3_);
   }

   public ResourceLocation getTextureLocation(ZombieEntity p_110775_1_) {
      return HUSK_LOCATION;
   }
}
