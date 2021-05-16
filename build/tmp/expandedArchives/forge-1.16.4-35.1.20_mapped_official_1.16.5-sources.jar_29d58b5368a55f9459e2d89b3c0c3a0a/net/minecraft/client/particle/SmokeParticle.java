package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SmokeParticle extends RisingParticle {
   protected SmokeParticle(ClientWorld p_i232425_1_, double p_i232425_2_, double p_i232425_4_, double p_i232425_6_, double p_i232425_8_, double p_i232425_10_, double p_i232425_12_, float p_i232425_14_, IAnimatedSprite p_i232425_15_) {
      super(p_i232425_1_, p_i232425_2_, p_i232425_4_, p_i232425_6_, 0.1F, 0.1F, 0.1F, p_i232425_8_, p_i232425_10_, p_i232425_12_, p_i232425_14_, p_i232425_15_, 0.3F, 8, 0.004D, true);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprites;

      public Factory(IAnimatedSprite p_i51045_1_) {
         this.sprites = p_i51045_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new SmokeParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, 1.0F, this.sprites);
      }
   }
}
