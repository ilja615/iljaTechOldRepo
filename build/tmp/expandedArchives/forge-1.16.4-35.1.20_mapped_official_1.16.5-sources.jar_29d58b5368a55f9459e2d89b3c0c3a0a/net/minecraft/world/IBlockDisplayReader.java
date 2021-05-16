package net.minecraft.world;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IBlockDisplayReader extends IBlockReader {
   @OnlyIn(Dist.CLIENT)
   float getShade(Direction p_230487_1_, boolean p_230487_2_);

   WorldLightManager getLightEngine();

   @OnlyIn(Dist.CLIENT)
   int getBlockTint(BlockPos p_225525_1_, ColorResolver p_225525_2_);

   default int getBrightness(LightType p_226658_1_, BlockPos p_226658_2_) {
      return this.getLightEngine().getLayerListener(p_226658_1_).getLightValue(p_226658_2_);
   }

   default int getRawBrightness(BlockPos p_226659_1_, int p_226659_2_) {
      return this.getLightEngine().getRawBrightness(p_226659_1_, p_226659_2_);
   }

   default boolean canSeeSky(BlockPos p_226660_1_) {
      return this.getBrightness(LightType.SKY, p_226660_1_) >= this.getMaxLightLevel();
   }
}
