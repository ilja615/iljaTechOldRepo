package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BubblePopParticle extends SpriteTexturedParticle {
   private final IAnimatedSprite sprites;

   private BubblePopParticle(ClientWorld p_i232353_1_, double p_i232353_2_, double p_i232353_4_, double p_i232353_6_, double p_i232353_8_, double p_i232353_10_, double p_i232353_12_, IAnimatedSprite p_i232353_14_) {
      super(p_i232353_1_, p_i232353_2_, p_i232353_4_, p_i232353_6_);
      this.sprites = p_i232353_14_;
      this.lifetime = 4;
      this.gravity = 0.008F;
      this.xd = p_i232353_8_;
      this.yd = p_i232353_10_;
      this.zd = p_i232353_12_;
      this.setSpriteFromAge(p_i232353_14_);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.yd -= (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         this.setSpriteFromAge(this.sprites);
      }
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprites;

      public Factory(IAnimatedSprite p_i49967_1_) {
         this.sprites = p_i49967_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new BubblePopParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.sprites);
      }
   }
}
