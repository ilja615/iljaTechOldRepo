package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.RabbitModel;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RabbitRenderer extends MobRenderer<RabbitEntity, RabbitModel<RabbitEntity>> {
   private static final ResourceLocation RABBIT_BROWN_LOCATION = new ResourceLocation("textures/entity/rabbit/brown.png");
   private static final ResourceLocation RABBIT_WHITE_LOCATION = new ResourceLocation("textures/entity/rabbit/white.png");
   private static final ResourceLocation RABBIT_BLACK_LOCATION = new ResourceLocation("textures/entity/rabbit/black.png");
   private static final ResourceLocation RABBIT_GOLD_LOCATION = new ResourceLocation("textures/entity/rabbit/gold.png");
   private static final ResourceLocation RABBIT_SALT_LOCATION = new ResourceLocation("textures/entity/rabbit/salt.png");
   private static final ResourceLocation RABBIT_WHITE_SPLOTCHED_LOCATION = new ResourceLocation("textures/entity/rabbit/white_splotched.png");
   private static final ResourceLocation RABBIT_TOAST_LOCATION = new ResourceLocation("textures/entity/rabbit/toast.png");
   private static final ResourceLocation RABBIT_EVIL_LOCATION = new ResourceLocation("textures/entity/rabbit/caerbannog.png");

   public RabbitRenderer(EntityRendererManager p_i47196_1_) {
      super(p_i47196_1_, new RabbitModel<>(), 0.3F);
   }

   public ResourceLocation getTextureLocation(RabbitEntity p_110775_1_) {
      String s = TextFormatting.stripFormatting(p_110775_1_.getName().getString());
      if (s != null && "Toast".equals(s)) {
         return RABBIT_TOAST_LOCATION;
      } else {
         switch(p_110775_1_.getRabbitType()) {
         case 0:
         default:
            return RABBIT_BROWN_LOCATION;
         case 1:
            return RABBIT_WHITE_LOCATION;
         case 2:
            return RABBIT_BLACK_LOCATION;
         case 3:
            return RABBIT_WHITE_SPLOTCHED_LOCATION;
         case 4:
            return RABBIT_GOLD_LOCATION;
         case 5:
            return RABBIT_SALT_LOCATION;
         case 99:
            return RABBIT_EVIL_LOCATION;
         }
      }
   }
}
