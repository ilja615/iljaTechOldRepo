package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.monster.ElderGuardianEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElderGuardianRenderer extends GuardianRenderer {
   public static final ResourceLocation GUARDIAN_ELDER_LOCATION = new ResourceLocation("textures/entity/guardian_elder.png");

   public ElderGuardianRenderer(EntityRendererManager p_i47209_1_) {
      super(p_i47209_1_, 1.2F);
   }

   protected void scale(GuardianEntity p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
      p_225620_2_.scale(ElderGuardianEntity.ELDER_SIZE_SCALE, ElderGuardianEntity.ELDER_SIZE_SCALE, ElderGuardianEntity.ELDER_SIZE_SCALE);
   }

   public ResourceLocation getTextureLocation(GuardianEntity p_110775_1_) {
      return GUARDIAN_ELDER_LOCATION;
   }
}
