package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SimpleAnimatedParticle extends SpriteTexturedParticle {
   protected final IAnimatedSprite sprites;
   private final float baseGravity;
   private float baseAirFriction = 0.91F;
   private float fadeR;
   private float fadeG;
   private float fadeB;
   private boolean hasFade;

   protected SimpleAnimatedParticle(ClientWorld p_i232422_1_, double p_i232422_2_, double p_i232422_4_, double p_i232422_6_, IAnimatedSprite p_i232422_8_, float p_i232422_9_) {
      super(p_i232422_1_, p_i232422_2_, p_i232422_4_, p_i232422_6_);
      this.sprites = p_i232422_8_;
      this.baseGravity = p_i232422_9_;
   }

   public void setColor(int p_187146_1_) {
      float f = (float)((p_187146_1_ & 16711680) >> 16) / 255.0F;
      float f1 = (float)((p_187146_1_ & '\uff00') >> 8) / 255.0F;
      float f2 = (float)((p_187146_1_ & 255) >> 0) / 255.0F;
      float f3 = 1.0F;
      this.setColor(f * 1.0F, f1 * 1.0F, f2 * 1.0F);
   }

   public void setFadeColor(int p_187145_1_) {
      this.fadeR = (float)((p_187145_1_ & 16711680) >> 16) / 255.0F;
      this.fadeG = (float)((p_187145_1_ & '\uff00') >> 8) / 255.0F;
      this.fadeB = (float)((p_187145_1_ & 255) >> 0) / 255.0F;
      this.hasFade = true;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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
            if (this.hasFade) {
               this.rCol += (this.fadeR - this.rCol) * 0.2F;
               this.gCol += (this.fadeG - this.gCol) * 0.2F;
               this.bCol += (this.fadeB - this.bCol) * 0.2F;
            }
         }

         this.yd += (double)this.baseGravity;
         this.move(this.xd, this.yd, this.zd);
         this.xd *= (double)this.baseAirFriction;
         this.yd *= (double)this.baseAirFriction;
         this.zd *= (double)this.baseAirFriction;
         if (this.onGround) {
            this.xd *= (double)0.7F;
            this.zd *= (double)0.7F;
         }

      }
   }

   public int getLightColor(float p_189214_1_) {
      return 15728880;
   }

   protected void setBaseAirFriction(float p_191238_1_) {
      this.baseAirFriction = p_191238_1_;
   }
}
