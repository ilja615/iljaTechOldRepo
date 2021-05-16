package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LargeSmokeParticle extends SmokeParticle {
   protected LargeSmokeParticle(ClientWorld p_i232402_1_, double p_i232402_2_, double p_i232402_4_, double p_i232402_6_, double p_i232402_8_, double p_i232402_10_, double p_i232402_12_, IAnimatedSprite p_i232402_14_) {
      super(p_i232402_1_, p_i232402_2_, p_i232402_4_, p_i232402_6_, p_i232402_8_, p_i232402_10_, p_i232402_12_, 2.5F, p_i232402_14_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprites;

      public Factory(IAnimatedSprite p_i50554_1_) {
         this.sprites = p_i50554_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new LargeSmokeParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.sprites);
      }
   }
}
