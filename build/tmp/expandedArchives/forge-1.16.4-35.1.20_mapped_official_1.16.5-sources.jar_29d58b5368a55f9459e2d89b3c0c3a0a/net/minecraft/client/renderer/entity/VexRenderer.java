package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.model.VexModel;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VexRenderer extends BipedRenderer<VexEntity, VexModel> {
   private static final ResourceLocation VEX_LOCATION = new ResourceLocation("textures/entity/illager/vex.png");
   private static final ResourceLocation VEX_CHARGING_LOCATION = new ResourceLocation("textures/entity/illager/vex_charging.png");

   public VexRenderer(EntityRendererManager p_i47190_1_) {
      super(p_i47190_1_, new VexModel(), 0.3F);
   }

   protected int getBlockLightLevel(VexEntity p_225624_1_, BlockPos p_225624_2_) {
      return 15;
   }

   public ResourceLocation getTextureLocation(VexEntity p_110775_1_) {
      return p_110775_1_.isCharging() ? VEX_CHARGING_LOCATION : VEX_LOCATION;
   }

   protected void scale(VexEntity p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
      p_225620_2_.scale(0.4F, 0.4F, 0.4F);
   }
}
