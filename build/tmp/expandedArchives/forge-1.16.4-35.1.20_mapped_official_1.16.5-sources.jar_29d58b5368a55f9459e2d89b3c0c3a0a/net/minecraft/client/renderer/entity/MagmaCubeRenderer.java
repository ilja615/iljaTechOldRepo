package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.model.MagmaCubeModel;
import net.minecraft.entity.monster.MagmaCubeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MagmaCubeRenderer extends MobRenderer<MagmaCubeEntity, MagmaCubeModel<MagmaCubeEntity>> {
   private static final ResourceLocation MAGMACUBE_LOCATION = new ResourceLocation("textures/entity/slime/magmacube.png");

   public MagmaCubeRenderer(EntityRendererManager p_i46159_1_) {
      super(p_i46159_1_, new MagmaCubeModel<>(), 0.25F);
   }

   protected int getBlockLightLevel(MagmaCubeEntity p_225624_1_, BlockPos p_225624_2_) {
      return 15;
   }

   public ResourceLocation getTextureLocation(MagmaCubeEntity p_110775_1_) {
      return MAGMACUBE_LOCATION;
   }

   protected void scale(MagmaCubeEntity p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
      int i = p_225620_1_.getSize();
      float f = MathHelper.lerp(p_225620_3_, p_225620_1_.oSquish, p_225620_1_.squish) / ((float)i * 0.5F + 1.0F);
      float f1 = 1.0F / (f + 1.0F);
      p_225620_2_.scale(f1 * (float)i, 1.0F / f1 * (float)i, f1 * (float)i);
   }
}
