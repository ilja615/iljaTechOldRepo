package com.mojang.blaze3d.vertex;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class DefaultColorVertexBuilder implements IVertexBuilder {
   protected boolean defaultColorSet = false;
   protected int defaultR = 255;
   protected int defaultG = 255;
   protected int defaultB = 255;
   protected int defaultA = 255;

   public void defaultColor(int p_225611_1_, int p_225611_2_, int p_225611_3_, int p_225611_4_) {
      this.defaultR = p_225611_1_;
      this.defaultG = p_225611_2_;
      this.defaultB = p_225611_3_;
      this.defaultA = p_225611_4_;
      this.defaultColorSet = true;
   }
}
