package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FallingDustParticle extends SpriteTexturedParticle {
   private final float rotSpeed;
   private final IAnimatedSprite sprites;

   private FallingDustParticle(ClientWorld p_i232385_1_, double p_i232385_2_, double p_i232385_4_, double p_i232385_6_, float p_i232385_8_, float p_i232385_9_, float p_i232385_10_, IAnimatedSprite p_i232385_11_) {
      super(p_i232385_1_, p_i232385_2_, p_i232385_4_, p_i232385_6_);
      this.sprites = p_i232385_11_;
      this.rCol = p_i232385_8_;
      this.gCol = p_i232385_9_;
      this.bCol = p_i232385_10_;
      float f = 0.9F;
      this.quadSize *= 0.67499995F;
      int i = (int)(32.0D / (Math.random() * 0.8D + 0.2D));
      this.lifetime = (int)Math.max((float)i * 0.9F, 1.0F);
      this.setSpriteFromAge(p_i232385_11_);
      this.rotSpeed = ((float)Math.random() - 0.5F) * 0.1F;
      this.roll = (float)Math.random() * ((float)Math.PI * 2F);
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
         this.oRoll = this.roll;
         this.roll += (float)Math.PI * this.rotSpeed * 2.0F;
         if (this.onGround) {
            this.oRoll = this.roll = 0.0F;
         }

         this.move(this.xd, this.yd, this.zd);
         this.yd -= (double)0.003F;
         this.yd = Math.max(this.yd, (double)-0.14F);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BlockParticleData> {
      private final IAnimatedSprite sprite;

      public Factory(IAnimatedSprite p_i51109_1_) {
         this.sprite = p_i51109_1_;
      }

      @Nullable
      public Particle createParticle(BlockParticleData p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         BlockState blockstate = p_199234_1_.getState();
         if (!blockstate.isAir() && blockstate.getRenderShape() == BlockRenderType.INVISIBLE) {
            return null;
         } else {
            BlockPos blockpos = new BlockPos(p_199234_3_, p_199234_5_, p_199234_7_);
            int i = Minecraft.getInstance().getBlockColors().getColor(blockstate, p_199234_2_, blockpos);
            if (blockstate.getBlock() instanceof FallingBlock) {
               i = ((FallingBlock)blockstate.getBlock()).getDustColor(blockstate, p_199234_2_, blockpos);
            }

            float f = (float)(i >> 16 & 255) / 255.0F;
            float f1 = (float)(i >> 8 & 255) / 255.0F;
            float f2 = (float)(i & 255) / 255.0F;
            return new FallingDustParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, f, f1, f2, this.sprite);
         }
      }
   }
}
