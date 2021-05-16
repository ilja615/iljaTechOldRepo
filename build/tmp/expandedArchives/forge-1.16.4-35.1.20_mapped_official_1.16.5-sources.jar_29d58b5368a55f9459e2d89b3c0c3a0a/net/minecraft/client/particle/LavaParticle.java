package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LavaParticle extends SpriteTexturedParticle {
   private LavaParticle(ClientWorld p_i232403_1_, double p_i232403_2_, double p_i232403_4_, double p_i232403_6_) {
      super(p_i232403_1_, p_i232403_2_, p_i232403_4_, p_i232403_6_, 0.0D, 0.0D, 0.0D);
      this.xd *= (double)0.8F;
      this.yd *= (double)0.8F;
      this.zd *= (double)0.8F;
      this.yd = (double)(this.random.nextFloat() * 0.4F + 0.05F);
      this.quadSize *= this.random.nextFloat() * 2.0F + 0.2F;
      this.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public int getLightColor(float p_189214_1_) {
      int i = super.getLightColor(p_189214_1_);
      int j = 240;
      int k = i >> 16 & 255;
      return 240 | k << 16;
   }

   public float getQuadSize(float p_217561_1_) {
      float f = ((float)this.age + p_217561_1_) / (float)this.lifetime;
      return this.quadSize * (1.0F - f * f);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      float f = (float)this.age / (float)this.lifetime;
      if (this.random.nextFloat() > f) {
         this.level.addParticle(ParticleTypes.SMOKE, this.x, this.y, this.z, this.xd, this.yd, this.zd);
      }

      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.yd -= 0.03D;
         this.move(this.xd, this.yd, this.zd);
         this.xd *= (double)0.999F;
         this.yd *= (double)0.999F;
         this.zd *= (double)0.999F;
         if (this.onGround) {
            this.xd *= (double)0.7F;
            this.zd *= (double)0.7F;
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprite;

      public Factory(IAnimatedSprite p_i50495_1_) {
         this.sprite = p_i50495_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         LavaParticle lavaparticle = new LavaParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_);
         lavaparticle.pickSprite(this.sprite);
         return lavaparticle;
      }
   }
}
