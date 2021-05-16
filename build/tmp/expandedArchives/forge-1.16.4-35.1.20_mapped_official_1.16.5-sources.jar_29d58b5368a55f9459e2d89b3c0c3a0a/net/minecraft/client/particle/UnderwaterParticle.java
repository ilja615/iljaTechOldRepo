package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UnderwaterParticle extends SpriteTexturedParticle {
   private UnderwaterParticle(ClientWorld p_i232437_1_, double p_i232437_2_, double p_i232437_4_, double p_i232437_6_) {
      super(p_i232437_1_, p_i232437_2_, p_i232437_4_ - 0.125D, p_i232437_6_);
      this.rCol = 0.4F;
      this.gCol = 0.4F;
      this.bCol = 0.7F;
      this.setSize(0.01F, 0.01F);
      this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
      this.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
      this.hasPhysics = false;
   }

   private UnderwaterParticle(ClientWorld p_i232438_1_, double p_i232438_2_, double p_i232438_4_, double p_i232438_6_, double p_i232438_8_, double p_i232438_10_, double p_i232438_12_) {
      super(p_i232438_1_, p_i232438_2_, p_i232438_4_ - 0.125D, p_i232438_6_, p_i232438_8_, p_i232438_10_, p_i232438_12_);
      this.setSize(0.01F, 0.01F);
      this.quadSize *= this.random.nextFloat() * 0.6F + 0.6F;
      this.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
      this.hasPhysics = false;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.lifetime-- <= 0) {
         this.remove();
      } else {
         this.move(this.xd, this.yd, this.zd);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class CrimsonSporeFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprite;

      public CrimsonSporeFactory(IAnimatedSprite p_i232441_1_) {
         this.sprite = p_i232441_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         Random random = p_199234_2_.random;
         double d0 = random.nextGaussian() * (double)1.0E-6F;
         double d1 = random.nextGaussian() * (double)1.0E-4F;
         double d2 = random.nextGaussian() * (double)1.0E-6F;
         UnderwaterParticle underwaterparticle = new UnderwaterParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, d0, d1, d2);
         underwaterparticle.pickSprite(this.sprite);
         underwaterparticle.setColor(0.9F, 0.4F, 0.5F);
         return underwaterparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class UnderwaterFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprite;

      public UnderwaterFactory(IAnimatedSprite p_i232442_1_) {
         this.sprite = p_i232442_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         UnderwaterParticle underwaterparticle = new UnderwaterParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_);
         underwaterparticle.pickSprite(this.sprite);
         return underwaterparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class WarpedSporeFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprite;

      public WarpedSporeFactory(IAnimatedSprite p_i232443_1_) {
         this.sprite = p_i232443_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         double d0 = (double)p_199234_2_.random.nextFloat() * -1.9D * (double)p_199234_2_.random.nextFloat() * 0.1D;
         UnderwaterParticle underwaterparticle = new UnderwaterParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, 0.0D, d0, 0.0D);
         underwaterparticle.pickSprite(this.sprite);
         underwaterparticle.setColor(0.1F, 0.1F, 0.3F);
         underwaterparticle.setSize(0.001F, 0.001F);
         return underwaterparticle;
      }
   }
}
