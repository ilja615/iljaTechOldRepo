package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SplashParticle extends RainParticle {
   private SplashParticle(ClientWorld p_i232433_1_, double p_i232433_2_, double p_i232433_4_, double p_i232433_6_, double p_i232433_8_, double p_i232433_10_, double p_i232433_12_) {
      super(p_i232433_1_, p_i232433_2_, p_i232433_4_, p_i232433_6_);
      this.gravity = 0.04F;
      if (p_i232433_10_ == 0.0D && (p_i232433_8_ != 0.0D || p_i232433_12_ != 0.0D)) {
         this.xd = p_i232433_8_;
         this.yd = 0.1D;
         this.zd = p_i232433_12_;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprite;

      public Factory(IAnimatedSprite p_i50679_1_) {
         this.sprite = p_i50679_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         SplashParticle splashparticle = new SplashParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
         splashparticle.pickSprite(this.sprite);
         return splashparticle;
      }
   }
}
