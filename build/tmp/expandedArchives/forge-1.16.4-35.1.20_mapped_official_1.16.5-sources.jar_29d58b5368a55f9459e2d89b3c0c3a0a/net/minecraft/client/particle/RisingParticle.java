package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RisingParticle extends SpriteTexturedParticle {
   private final IAnimatedSprite sprites;
   private final double fallSpeed;

   protected RisingParticle(ClientWorld p_i232345_1_, double p_i232345_2_, double p_i232345_4_, double p_i232345_6_, float p_i232345_8_, float p_i232345_9_, float p_i232345_10_, double p_i232345_11_, double p_i232345_13_, double p_i232345_15_, float p_i232345_17_, IAnimatedSprite p_i232345_18_, float p_i232345_19_, int p_i232345_20_, double p_i232345_21_, boolean p_i232345_23_) {
      super(p_i232345_1_, p_i232345_2_, p_i232345_4_, p_i232345_6_, 0.0D, 0.0D, 0.0D);
      this.fallSpeed = p_i232345_21_;
      this.sprites = p_i232345_18_;
      this.xd *= (double)p_i232345_8_;
      this.yd *= (double)p_i232345_9_;
      this.zd *= (double)p_i232345_10_;
      this.xd += p_i232345_11_;
      this.yd += p_i232345_13_;
      this.zd += p_i232345_15_;
      float f = p_i232345_1_.random.nextFloat() * p_i232345_19_;
      this.rCol = f;
      this.gCol = f;
      this.bCol = f;
      this.quadSize *= 0.75F * p_i232345_17_;
      this.lifetime = (int)((double)p_i232345_20_ / ((double)p_i232345_1_.random.nextFloat() * 0.8D + 0.2D));
      this.lifetime = (int)((float)this.lifetime * p_i232345_17_);
      this.lifetime = Math.max(this.lifetime, 1);
      this.setSpriteFromAge(p_i232345_18_);
      this.hasPhysics = p_i232345_23_;
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
         this.setSpriteFromAge(this.sprites);
         this.yd += this.fallSpeed;
         this.move(this.xd, this.yd, this.zd);
         if (this.y == this.yo) {
            this.xd *= 1.1D;
            this.zd *= 1.1D;
         }

         this.xd *= (double)0.96F;
         this.yd *= (double)0.96F;
         this.zd *= (double)0.96F;
         if (this.onGround) {
            this.xd *= (double)0.7F;
            this.zd *= (double)0.7F;
         }

      }
   }
}
