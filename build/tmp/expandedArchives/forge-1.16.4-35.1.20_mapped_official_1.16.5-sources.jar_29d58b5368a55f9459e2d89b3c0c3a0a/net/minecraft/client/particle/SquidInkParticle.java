package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SquidInkParticle extends SimpleAnimatedParticle {
   private SquidInkParticle(ClientWorld p_i232435_1_, double p_i232435_2_, double p_i232435_4_, double p_i232435_6_, double p_i232435_8_, double p_i232435_10_, double p_i232435_12_, IAnimatedSprite p_i232435_14_) {
      super(p_i232435_1_, p_i232435_2_, p_i232435_4_, p_i232435_6_, p_i232435_14_, 0.0F);
      this.quadSize = 0.5F;
      this.setAlpha(1.0F);
      this.setColor(0.0F, 0.0F, 0.0F);
      this.lifetime = (int)((double)(this.quadSize * 12.0F) / (Math.random() * (double)0.8F + (double)0.2F));
      this.setSpriteFromAge(p_i232435_14_);
      this.hasPhysics = false;
      this.xd = p_i232435_8_;
      this.yd = p_i232435_10_;
      this.zd = p_i232435_12_;
      this.setBaseAirFriction(0.0F);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
         if (this.age > this.lifetime / 2) {
            this.setAlpha(1.0F - ((float)this.age - (float)(this.lifetime / 2)) / (float)this.lifetime);
         }

         this.move(this.xd, this.yd, this.zd);
         if (this.level.getBlockState(new BlockPos(this.x, this.y, this.z)).isAir()) {
            this.yd -= (double)0.008F;
         }

         this.xd *= (double)0.92F;
         this.yd *= (double)0.92F;
         this.zd *= (double)0.92F;
         if (this.onGround) {
            this.xd *= (double)0.7F;
            this.zd *= (double)0.7F;
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprites;

      public Factory(IAnimatedSprite p_i50599_1_) {
         this.sprites = p_i50599_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new SquidInkParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.sprites);
      }
   }
}
