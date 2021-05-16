package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WhiteAshParticle extends RisingParticle {
   protected WhiteAshParticle(ClientWorld p_i232459_1_, double p_i232459_2_, double p_i232459_4_, double p_i232459_6_, double p_i232459_8_, double p_i232459_10_, double p_i232459_12_, float p_i232459_14_, IAnimatedSprite p_i232459_15_) {
      super(p_i232459_1_, p_i232459_2_, p_i232459_4_, p_i232459_6_, 0.1F, -0.1F, 0.1F, p_i232459_8_, p_i232459_10_, p_i232459_12_, p_i232459_14_, p_i232459_15_, 0.0F, 20, -5.0E-4D, false);
      this.rCol = 0.7294118F;
      this.gCol = 0.69411767F;
      this.bCol = 0.7607843F;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprites;

      public Factory(IAnimatedSprite p_i232460_1_) {
         this.sprites = p_i232460_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         Random random = p_199234_2_.random;
         double d0 = (double)random.nextFloat() * -1.9D * (double)random.nextFloat() * 0.1D;
         double d1 = (double)random.nextFloat() * -0.5D * (double)random.nextFloat() * 0.1D * 5.0D;
         double d2 = (double)random.nextFloat() * -1.9D * (double)random.nextFloat() * 0.1D;
         return new WhiteAshParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, d0, d1, d2, 1.0F, this.sprites);
      }
   }
}
