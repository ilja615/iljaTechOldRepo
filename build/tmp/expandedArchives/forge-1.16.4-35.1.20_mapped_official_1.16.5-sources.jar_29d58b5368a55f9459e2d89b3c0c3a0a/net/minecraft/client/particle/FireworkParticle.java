package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.DyeColor;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FireworkParticle {
   @OnlyIn(Dist.CLIENT)
   public static class Overlay extends SpriteTexturedParticle {
      private Overlay(ClientWorld p_i232387_1_, double p_i232387_2_, double p_i232387_4_, double p_i232387_6_) {
         super(p_i232387_1_, p_i232387_2_, p_i232387_4_, p_i232387_6_);
         this.lifetime = 4;
      }

      public IParticleRenderType getRenderType() {
         return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
      }

      public void render(IVertexBuilder p_225606_1_, ActiveRenderInfo p_225606_2_, float p_225606_3_) {
         this.setAlpha(0.6F - ((float)this.age + p_225606_3_ - 1.0F) * 0.25F * 0.5F);
         super.render(p_225606_1_, p_225606_2_, p_225606_3_);
      }

      public float getQuadSize(float p_217561_1_) {
         return 7.1F * MathHelper.sin(((float)this.age + p_217561_1_ - 1.0F) * 0.25F * (float)Math.PI);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class OverlayFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprite;

      public OverlayFactory(IAnimatedSprite p_i50889_1_) {
         this.sprite = p_i50889_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         FireworkParticle.Overlay fireworkparticle$overlay = new FireworkParticle.Overlay(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_);
         fireworkparticle$overlay.pickSprite(this.sprite);
         return fireworkparticle$overlay;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Spark extends SimpleAnimatedParticle {
      private boolean trail;
      private boolean flicker;
      private final ParticleManager engine;
      private float fadeR;
      private float fadeG;
      private float fadeB;
      private boolean hasFade;

      private Spark(ClientWorld p_i232389_1_, double p_i232389_2_, double p_i232389_4_, double p_i232389_6_, double p_i232389_8_, double p_i232389_10_, double p_i232389_12_, ParticleManager p_i232389_14_, IAnimatedSprite p_i232389_15_) {
         super(p_i232389_1_, p_i232389_2_, p_i232389_4_, p_i232389_6_, p_i232389_15_, -0.004F);
         this.xd = p_i232389_8_;
         this.yd = p_i232389_10_;
         this.zd = p_i232389_12_;
         this.engine = p_i232389_14_;
         this.quadSize *= 0.75F;
         this.lifetime = 48 + this.random.nextInt(12);
         this.setSpriteFromAge(p_i232389_15_);
      }

      public void setTrail(boolean p_92045_1_) {
         this.trail = p_92045_1_;
      }

      public void setFlicker(boolean p_92043_1_) {
         this.flicker = p_92043_1_;
      }

      public void render(IVertexBuilder p_225606_1_, ActiveRenderInfo p_225606_2_, float p_225606_3_) {
         if (!this.flicker || this.age < this.lifetime / 3 || (this.age + this.lifetime) / 3 % 2 == 0) {
            super.render(p_225606_1_, p_225606_2_, p_225606_3_);
         }

      }

      public void tick() {
         super.tick();
         if (this.trail && this.age < this.lifetime / 2 && (this.age + this.lifetime) % 2 == 0) {
            FireworkParticle.Spark fireworkparticle$spark = new FireworkParticle.Spark(this.level, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D, this.engine, this.sprites);
            fireworkparticle$spark.setAlpha(0.99F);
            fireworkparticle$spark.setColor(this.rCol, this.gCol, this.bCol);
            fireworkparticle$spark.age = fireworkparticle$spark.lifetime / 2;
            if (this.hasFade) {
               fireworkparticle$spark.hasFade = true;
               fireworkparticle$spark.fadeR = this.fadeR;
               fireworkparticle$spark.fadeG = this.fadeG;
               fireworkparticle$spark.fadeB = this.fadeB;
            }

            fireworkparticle$spark.flicker = this.flicker;
            this.engine.add(fireworkparticle$spark);
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class SparkFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprites;

      public SparkFactory(IAnimatedSprite p_i50883_1_) {
         this.sprites = p_i50883_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         FireworkParticle.Spark fireworkparticle$spark = new FireworkParticle.Spark(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, Minecraft.getInstance().particleEngine, this.sprites);
         fireworkparticle$spark.setAlpha(0.99F);
         return fireworkparticle$spark;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Starter extends MetaParticle {
      private int life;
      private final ParticleManager engine;
      private ListNBT explosions;
      private boolean twinkleDelay;

      public Starter(ClientWorld p_i232391_1_, double p_i232391_2_, double p_i232391_4_, double p_i232391_6_, double p_i232391_8_, double p_i232391_10_, double p_i232391_12_, ParticleManager p_i232391_14_, @Nullable CompoundNBT p_i232391_15_) {
         super(p_i232391_1_, p_i232391_2_, p_i232391_4_, p_i232391_6_);
         this.xd = p_i232391_8_;
         this.yd = p_i232391_10_;
         this.zd = p_i232391_12_;
         this.engine = p_i232391_14_;
         this.lifetime = 8;
         if (p_i232391_15_ != null) {
            this.explosions = p_i232391_15_.getList("Explosions", 10);
            if (this.explosions.isEmpty()) {
               this.explosions = null;
            } else {
               this.lifetime = this.explosions.size() * 2 - 1;

               for(int i = 0; i < this.explosions.size(); ++i) {
                  CompoundNBT compoundnbt = this.explosions.getCompound(i);
                  if (compoundnbt.getBoolean("Flicker")) {
                     this.twinkleDelay = true;
                     this.lifetime += 15;
                     break;
                  }
               }
            }
         }

      }

      public void tick() {
         if (this.life == 0 && this.explosions != null) {
            boolean flag = this.isFarAwayFromCamera();
            boolean flag1 = false;
            if (this.explosions.size() >= 3) {
               flag1 = true;
            } else {
               for(int i = 0; i < this.explosions.size(); ++i) {
                  CompoundNBT compoundnbt = this.explosions.getCompound(i);
                  if (FireworkRocketItem.Shape.byId(compoundnbt.getByte("Type")) == FireworkRocketItem.Shape.LARGE_BALL) {
                     flag1 = true;
                     break;
                  }
               }
            }

            SoundEvent soundevent1;
            if (flag1) {
               soundevent1 = flag ? SoundEvents.FIREWORK_ROCKET_LARGE_BLAST_FAR : SoundEvents.FIREWORK_ROCKET_LARGE_BLAST;
            } else {
               soundevent1 = flag ? SoundEvents.FIREWORK_ROCKET_BLAST_FAR : SoundEvents.FIREWORK_ROCKET_BLAST;
            }

            this.level.playLocalSound(this.x, this.y, this.z, soundevent1, SoundCategory.AMBIENT, 20.0F, 0.95F + this.random.nextFloat() * 0.1F, true);
         }

         if (this.life % 2 == 0 && this.explosions != null && this.life / 2 < this.explosions.size()) {
            int k = this.life / 2;
            CompoundNBT compoundnbt1 = this.explosions.getCompound(k);
            FireworkRocketItem.Shape fireworkrocketitem$shape = FireworkRocketItem.Shape.byId(compoundnbt1.getByte("Type"));
            boolean flag4 = compoundnbt1.getBoolean("Trail");
            boolean flag2 = compoundnbt1.getBoolean("Flicker");
            int[] aint = compoundnbt1.getIntArray("Colors");
            int[] aint1 = compoundnbt1.getIntArray("FadeColors");
            if (aint.length == 0) {
               aint = new int[]{DyeColor.BLACK.getFireworkColor()};
            }

            switch(fireworkrocketitem$shape) {
            case SMALL_BALL:
            default:
               this.createParticleBall(0.25D, 2, aint, aint1, flag4, flag2);
               break;
            case LARGE_BALL:
               this.createParticleBall(0.5D, 4, aint, aint1, flag4, flag2);
               break;
            case STAR:
               this.createParticleShape(0.5D, new double[][]{{0.0D, 1.0D}, {0.3455D, 0.309D}, {0.9511D, 0.309D}, {0.3795918367346939D, -0.12653061224489795D}, {0.6122448979591837D, -0.8040816326530612D}, {0.0D, -0.35918367346938773D}}, aint, aint1, flag4, flag2, false);
               break;
            case CREEPER:
               this.createParticleShape(0.5D, new double[][]{{0.0D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.6D}, {0.6D, 0.6D}, {0.6D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.0D}, {0.4D, 0.0D}, {0.4D, -0.6D}, {0.2D, -0.6D}, {0.2D, -0.4D}, {0.0D, -0.4D}}, aint, aint1, flag4, flag2, true);
               break;
            case BURST:
               this.createParticleBurst(aint, aint1, flag4, flag2);
            }

            int j = aint[0];
            float f = (float)((j & 16711680) >> 16) / 255.0F;
            float f1 = (float)((j & '\uff00') >> 8) / 255.0F;
            float f2 = (float)((j & 255) >> 0) / 255.0F;
            Particle particle = this.engine.createParticle(ParticleTypes.FLASH, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
            particle.setColor(f, f1, f2);
         }

         ++this.life;
         if (this.life > this.lifetime) {
            if (this.twinkleDelay) {
               boolean flag3 = this.isFarAwayFromCamera();
               SoundEvent soundevent = flag3 ? SoundEvents.FIREWORK_ROCKET_TWINKLE_FAR : SoundEvents.FIREWORK_ROCKET_TWINKLE;
               this.level.playLocalSound(this.x, this.y, this.z, soundevent, SoundCategory.AMBIENT, 20.0F, 0.9F + this.random.nextFloat() * 0.15F, true);
            }

            this.remove();
         }

      }

      private boolean isFarAwayFromCamera() {
         Minecraft minecraft = Minecraft.getInstance();
         return minecraft.gameRenderer.getMainCamera().getPosition().distanceToSqr(this.x, this.y, this.z) >= 256.0D;
      }

      private void createParticle(double p_92034_1_, double p_92034_3_, double p_92034_5_, double p_92034_7_, double p_92034_9_, double p_92034_11_, int[] p_92034_13_, int[] p_92034_14_, boolean p_92034_15_, boolean p_92034_16_) {
         FireworkParticle.Spark fireworkparticle$spark = (FireworkParticle.Spark)this.engine.createParticle(ParticleTypes.FIREWORK, p_92034_1_, p_92034_3_, p_92034_5_, p_92034_7_, p_92034_9_, p_92034_11_);
         fireworkparticle$spark.setTrail(p_92034_15_);
         fireworkparticle$spark.setFlicker(p_92034_16_);
         fireworkparticle$spark.setAlpha(0.99F);
         int i = this.random.nextInt(p_92034_13_.length);
         fireworkparticle$spark.setColor(p_92034_13_[i]);
         if (p_92034_14_.length > 0) {
            fireworkparticle$spark.setFadeColor(Util.getRandom(p_92034_14_, this.random));
         }

      }

      private void createParticleBall(double p_92035_1_, int p_92035_3_, int[] p_92035_4_, int[] p_92035_5_, boolean p_92035_6_, boolean p_92035_7_) {
         double d0 = this.x;
         double d1 = this.y;
         double d2 = this.z;

         for(int i = -p_92035_3_; i <= p_92035_3_; ++i) {
            for(int j = -p_92035_3_; j <= p_92035_3_; ++j) {
               for(int k = -p_92035_3_; k <= p_92035_3_; ++k) {
                  double d3 = (double)j + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                  double d4 = (double)i + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                  double d5 = (double)k + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                  double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5) / p_92035_1_ + this.random.nextGaussian() * 0.05D;
                  this.createParticle(d0, d1, d2, d3 / d6, d4 / d6, d5 / d6, p_92035_4_, p_92035_5_, p_92035_6_, p_92035_7_);
                  if (i != -p_92035_3_ && i != p_92035_3_ && j != -p_92035_3_ && j != p_92035_3_) {
                     k += p_92035_3_ * 2 - 1;
                  }
               }
            }
         }

      }

      private void createParticleShape(double p_92038_1_, double[][] p_92038_3_, int[] p_92038_4_, int[] p_92038_5_, boolean p_92038_6_, boolean p_92038_7_, boolean p_92038_8_) {
         double d0 = p_92038_3_[0][0];
         double d1 = p_92038_3_[0][1];
         this.createParticle(this.x, this.y, this.z, d0 * p_92038_1_, d1 * p_92038_1_, 0.0D, p_92038_4_, p_92038_5_, p_92038_6_, p_92038_7_);
         float f = this.random.nextFloat() * (float)Math.PI;
         double d2 = p_92038_8_ ? 0.034D : 0.34D;

         for(int i = 0; i < 3; ++i) {
            double d3 = (double)f + (double)((float)i * (float)Math.PI) * d2;
            double d4 = d0;
            double d5 = d1;

            for(int j = 1; j < p_92038_3_.length; ++j) {
               double d6 = p_92038_3_[j][0];
               double d7 = p_92038_3_[j][1];

               for(double d8 = 0.25D; d8 <= 1.0D; d8 += 0.25D) {
                  double d9 = MathHelper.lerp(d8, d4, d6) * p_92038_1_;
                  double d10 = MathHelper.lerp(d8, d5, d7) * p_92038_1_;
                  double d11 = d9 * Math.sin(d3);
                  d9 = d9 * Math.cos(d3);

                  for(double d12 = -1.0D; d12 <= 1.0D; d12 += 2.0D) {
                     this.createParticle(this.x, this.y, this.z, d9 * d12, d10, d11 * d12, p_92038_4_, p_92038_5_, p_92038_6_, p_92038_7_);
                  }
               }

               d4 = d6;
               d5 = d7;
            }
         }

      }

      private void createParticleBurst(int[] p_92036_1_, int[] p_92036_2_, boolean p_92036_3_, boolean p_92036_4_) {
         double d0 = this.random.nextGaussian() * 0.05D;
         double d1 = this.random.nextGaussian() * 0.05D;

         for(int i = 0; i < 70; ++i) {
            double d2 = this.xd * 0.5D + this.random.nextGaussian() * 0.15D + d0;
            double d3 = this.zd * 0.5D + this.random.nextGaussian() * 0.15D + d1;
            double d4 = this.yd * 0.5D + this.random.nextDouble() * 0.5D;
            this.createParticle(this.x, this.y, this.z, d2, d4, d3, p_92036_1_, p_92036_2_, p_92036_3_, p_92036_4_);
         }

      }
   }
}
