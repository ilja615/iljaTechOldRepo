package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NoteParticle extends SpriteTexturedParticle {
   private NoteParticle(ClientWorld p_i232409_1_, double p_i232409_2_, double p_i232409_4_, double p_i232409_6_, double p_i232409_8_) {
      super(p_i232409_1_, p_i232409_2_, p_i232409_4_, p_i232409_6_, 0.0D, 0.0D, 0.0D);
      this.xd *= (double)0.01F;
      this.yd *= (double)0.01F;
      this.zd *= (double)0.01F;
      this.yd += 0.2D;
      this.rCol = Math.max(0.0F, MathHelper.sin(((float)p_i232409_8_ + 0.0F) * ((float)Math.PI * 2F)) * 0.65F + 0.35F);
      this.gCol = Math.max(0.0F, MathHelper.sin(((float)p_i232409_8_ + 0.33333334F) * ((float)Math.PI * 2F)) * 0.65F + 0.35F);
      this.bCol = Math.max(0.0F, MathHelper.sin(((float)p_i232409_8_ + 0.6666667F) * ((float)Math.PI * 2F)) * 0.65F + 0.35F);
      this.quadSize *= 1.5F;
      this.lifetime = 6;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public float getQuadSize(float p_217561_1_) {
      return this.quadSize * MathHelper.clamp(((float)this.age + p_217561_1_) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.move(this.xd, this.yd, this.zd);
         if (this.y == this.yo) {
            this.xd *= 1.1D;
            this.zd *= 1.1D;
         }

         this.xd *= (double)0.66F;
         this.yd *= (double)0.66F;
         this.zd *= (double)0.66F;
         if (this.onGround) {
            this.xd *= (double)0.7F;
            this.zd *= (double)0.7F;
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprite;

      public Factory(IAnimatedSprite p_i50044_1_) {
         this.sprite = p_i50044_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         NoteParticle noteparticle = new NoteParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_);
         noteparticle.pickSprite(this.sprite);
         return noteparticle;
      }
   }
}
