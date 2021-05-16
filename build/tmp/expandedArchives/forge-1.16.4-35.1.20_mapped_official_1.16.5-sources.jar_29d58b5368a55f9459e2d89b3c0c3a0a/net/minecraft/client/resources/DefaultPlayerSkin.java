package net.minecraft.client.resources;

import java.util.UUID;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DefaultPlayerSkin {
   private static final ResourceLocation STEVE_SKIN_LOCATION = new ResourceLocation("textures/entity/steve.png");
   private static final ResourceLocation ALEX_SKIN_LOCATION = new ResourceLocation("textures/entity/alex.png");

   public static ResourceLocation getDefaultSkin() {
      return STEVE_SKIN_LOCATION;
   }

   public static ResourceLocation getDefaultSkin(UUID p_177334_0_) {
      return isAlexDefault(p_177334_0_) ? ALEX_SKIN_LOCATION : STEVE_SKIN_LOCATION;
   }

   public static String getSkinModelName(UUID p_177332_0_) {
      return isAlexDefault(p_177332_0_) ? "slim" : "default";
   }

   private static boolean isAlexDefault(UUID p_177333_0_) {
      return (p_177333_0_.hashCode() & 1) == 1;
   }
}
