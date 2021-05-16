package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class DeceleratingParticle extends SpriteTexturedParticle {
   protected DeceleratingParticle(ClientWorld p_i232421_1_, double p_i232421_2_, double p_i232421_4_, double p_i232421_6_, double p_i232421_8_, double p_i232421_10_, double p_i232421_12_) {
      super(p_i232421_1_, p_i232421_2_, p_i232421_4_, p_i232421_6_, p_i232421_8_, p_i232421_10_, p_i232421_12_);
      this.xd = this.xd * (double)0.01F + p_i232421_8_;
      this.yd = this.yd * (double)0.01F + p_i232421_10_;
      this.zd = this.zd * (double)0.01F + p_i232421_12_;
      this.x += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.y += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.z += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.move(this.xd, this.yd, this.zd);
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
