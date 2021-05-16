package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DripParticle extends SpriteTexturedParticle {
   private final Fluid type;
   protected boolean isGlowing;

   private DripParticle(ClientWorld p_i232361_1_, double p_i232361_2_, double p_i232361_4_, double p_i232361_6_, Fluid p_i232361_8_) {
      super(p_i232361_1_, p_i232361_2_, p_i232361_4_, p_i232361_6_);
      this.setSize(0.01F, 0.01F);
      this.gravity = 0.06F;
      this.type = p_i232361_8_;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public int getLightColor(float p_189214_1_) {
      return this.isGlowing ? 240 : super.getLightColor(p_189214_1_);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.preMoveUpdate();
      if (!this.removed) {
         this.yd -= (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         this.postMoveUpdate();
         if (!this.removed) {
            this.xd *= (double)0.98F;
            this.yd *= (double)0.98F;
            this.zd *= (double)0.98F;
            BlockPos blockpos = new BlockPos(this.x, this.y, this.z);
            FluidState fluidstate = this.level.getFluidState(blockpos);
            if (fluidstate.getType() == this.type && this.y < (double)((float)blockpos.getY() + fluidstate.getHeight(this.level, blockpos))) {
               this.remove();
            }

         }
      }
   }

   protected void preMoveUpdate() {
      if (this.lifetime-- <= 0) {
         this.remove();
      }

   }

   protected void postMoveUpdate() {
   }

   @OnlyIn(Dist.CLIENT)
   static class Dripping extends DripParticle {
      private final IParticleData fallingParticle;

      private Dripping(ClientWorld p_i232365_1_, double p_i232365_2_, double p_i232365_4_, double p_i232365_6_, Fluid p_i232365_8_, IParticleData p_i232365_9_) {
         super(p_i232365_1_, p_i232365_2_, p_i232365_4_, p_i232365_6_, p_i232365_8_);
         this.fallingParticle = p_i232365_9_;
         this.gravity *= 0.02F;
         this.lifetime = 40;
      }

      protected void preMoveUpdate() {
         if (this.lifetime-- <= 0) {
            this.remove();
            this.level.addParticle(this.fallingParticle, this.x, this.y, this.z, this.xd, this.yd, this.zd);
         }

      }

      protected void postMoveUpdate() {
         this.xd *= 0.02D;
         this.yd *= 0.02D;
         this.zd *= 0.02D;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class DrippingHoneyFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite sprite;

      public DrippingHoneyFactory(IAnimatedSprite p_i225960_1_) {
         this.sprite = p_i225960_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         DripParticle.Dripping dripparticle$dripping = new DripParticle.Dripping(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Fluids.EMPTY, ParticleTypes.FALLING_HONEY);
         dripparticle$dripping.gravity *= 0.01F;
         dripparticle$dripping.lifetime = 100;
         dripparticle$dripping.setColor(0.622F, 0.508F, 0.082F);
         dripparticle$dripping.pickSprite(this.sprite);
         return dripparticle$dripping;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class DrippingLava extends DripParticle.Dripping {
      private DrippingLava(ClientWorld p_i232363_1_, double p_i232363_2_, double p_i232363_4_, double p_i232363_6_, Fluid p_i232363_8_, IParticleData p_i232363_9_) {
         super(p_i232363_1_, p_i232363_2_, p_i232363_4_, p_i232363_6_, p_i232363_8_, p_i232363_9_);
      }

      protected void preMoveUpdate() {
         this.rCol = 1.0F;
         this.gCol = 16.0F / (float)(40 - this.lifetime + 16);
         this.bCol = 4.0F / (float)(40 - this.lifetime + 8);
         super.preMoveUpdate();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class DrippingLavaFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite sprite;

      public DrippingLavaFactory(IAnimatedSprite p_i50505_1_) {
         this.sprite = p_i50505_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         DripParticle.DrippingLava dripparticle$drippinglava = new DripParticle.DrippingLava(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Fluids.LAVA, ParticleTypes.FALLING_LAVA);
         dripparticle$drippinglava.pickSprite(this.sprite);
         return dripparticle$drippinglava;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class DrippingObsidianTearFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite sprite;

      public DrippingObsidianTearFactory(IAnimatedSprite p_i232376_1_) {
         this.sprite = p_i232376_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         DripParticle.Dripping dripparticle$dripping = new DripParticle.Dripping(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Fluids.EMPTY, ParticleTypes.FALLING_OBSIDIAN_TEAR);
         dripparticle$dripping.isGlowing = true;
         dripparticle$dripping.gravity *= 0.01F;
         dripparticle$dripping.lifetime = 100;
         dripparticle$dripping.setColor(0.51171875F, 0.03125F, 0.890625F);
         dripparticle$dripping.pickSprite(this.sprite);
         return dripparticle$dripping;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class DrippingWaterFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite sprite;

      public DrippingWaterFactory(IAnimatedSprite p_i50502_1_) {
         this.sprite = p_i50502_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         DripParticle dripparticle = new DripParticle.Dripping(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Fluids.WATER, ParticleTypes.FALLING_WATER);
         dripparticle.setColor(0.2F, 0.3F, 1.0F);
         dripparticle.pickSprite(this.sprite);
         return dripparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class FallingHoneyFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite sprite;

      public FallingHoneyFactory(IAnimatedSprite p_i225959_1_) {
         this.sprite = p_i225959_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         DripParticle dripparticle = new DripParticle.FallingHoneyParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Fluids.EMPTY, ParticleTypes.LANDING_HONEY);
         dripparticle.gravity = 0.01F;
         dripparticle.setColor(0.582F, 0.448F, 0.082F);
         dripparticle.pickSprite(this.sprite);
         return dripparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class FallingHoneyParticle extends DripParticle.FallingLiquidParticle {
      private FallingHoneyParticle(ClientWorld p_i232373_1_, double p_i232373_2_, double p_i232373_4_, double p_i232373_6_, Fluid p_i232373_8_, IParticleData p_i232373_9_) {
         super(p_i232373_1_, p_i232373_2_, p_i232373_4_, p_i232373_6_, p_i232373_8_, p_i232373_9_);
      }

      protected void postMoveUpdate() {
         if (this.onGround) {
            this.remove();
            this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
            this.level.playLocalSound(this.x + 0.5D, this.y, this.z + 0.5D, SoundEvents.BEEHIVE_DRIP, SoundCategory.BLOCKS, 0.3F + this.level.random.nextFloat() * 2.0F / 3.0F, 1.0F, false);
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class FallingLavaFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite sprite;

      public FallingLavaFactory(IAnimatedSprite p_i50506_1_) {
         this.sprite = p_i50506_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         DripParticle dripparticle = new DripParticle.FallingLiquidParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Fluids.LAVA, ParticleTypes.LANDING_LAVA);
         dripparticle.setColor(1.0F, 0.2857143F, 0.083333336F);
         dripparticle.pickSprite(this.sprite);
         return dripparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class FallingLiquidParticle extends DripParticle.FallingNectarParticle {
      protected final IParticleData landParticle;

      private FallingLiquidParticle(ClientWorld p_i232369_1_, double p_i232369_2_, double p_i232369_4_, double p_i232369_6_, Fluid p_i232369_8_, IParticleData p_i232369_9_) {
         super(p_i232369_1_, p_i232369_2_, p_i232369_4_, p_i232369_6_, p_i232369_8_);
         this.landParticle = p_i232369_9_;
      }

      protected void postMoveUpdate() {
         if (this.onGround) {
            this.remove();
            this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class FallingNectarFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite sprite;

      public FallingNectarFactory(IAnimatedSprite p_i225962_1_) {
         this.sprite = p_i225962_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         DripParticle dripparticle = new DripParticle.FallingNectarParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Fluids.EMPTY);
         dripparticle.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
         dripparticle.gravity = 0.007F;
         dripparticle.setColor(0.92F, 0.782F, 0.72F);
         dripparticle.pickSprite(this.sprite);
         return dripparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class FallingNectarParticle extends DripParticle {
      private FallingNectarParticle(ClientWorld p_i232371_1_, double p_i232371_2_, double p_i232371_4_, double p_i232371_6_, Fluid p_i232371_8_) {
         super(p_i232371_1_, p_i232371_2_, p_i232371_4_, p_i232371_6_, p_i232371_8_);
         this.lifetime = (int)(64.0D / (Math.random() * 0.8D + 0.2D));
      }

      protected void postMoveUpdate() {
         if (this.onGround) {
            this.remove();
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class FallingObsidianTearFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite sprite;

      public FallingObsidianTearFactory(IAnimatedSprite p_i232375_1_) {
         this.sprite = p_i232375_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         DripParticle dripparticle = new DripParticle.FallingLiquidParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Fluids.EMPTY, ParticleTypes.LANDING_OBSIDIAN_TEAR);
         dripparticle.isGlowing = true;
         dripparticle.gravity = 0.01F;
         dripparticle.setColor(0.51171875F, 0.03125F, 0.890625F);
         dripparticle.pickSprite(this.sprite);
         return dripparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class FallingWaterFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite sprite;

      public FallingWaterFactory(IAnimatedSprite p_i50503_1_) {
         this.sprite = p_i50503_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         DripParticle dripparticle = new DripParticle.FallingLiquidParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Fluids.WATER, ParticleTypes.SPLASH);
         dripparticle.setColor(0.2F, 0.3F, 1.0F);
         dripparticle.pickSprite(this.sprite);
         return dripparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Landing extends DripParticle {
      private Landing(ClientWorld p_i232367_1_, double p_i232367_2_, double p_i232367_4_, double p_i232367_6_, Fluid p_i232367_8_) {
         super(p_i232367_1_, p_i232367_2_, p_i232367_4_, p_i232367_6_, p_i232367_8_);
         this.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LandingHoneyFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite sprite;

      public LandingHoneyFactory(IAnimatedSprite p_i225961_1_) {
         this.sprite = p_i225961_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         DripParticle dripparticle = new DripParticle.Landing(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Fluids.EMPTY);
         dripparticle.lifetime = (int)(128.0D / (Math.random() * 0.8D + 0.2D));
         dripparticle.setColor(0.522F, 0.408F, 0.082F);
         dripparticle.pickSprite(this.sprite);
         return dripparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LandingLavaFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite sprite;

      public LandingLavaFactory(IAnimatedSprite p_i50504_1_) {
         this.sprite = p_i50504_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         DripParticle dripparticle = new DripParticle.Landing(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Fluids.LAVA);
         dripparticle.setColor(1.0F, 0.2857143F, 0.083333336F);
         dripparticle.pickSprite(this.sprite);
         return dripparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LandingObsidianTearFactory implements IParticleFactory<BasicParticleType> {
      protected final IAnimatedSprite sprite;

      public LandingObsidianTearFactory(IAnimatedSprite p_i232377_1_) {
         this.sprite = p_i232377_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, ClientWorld p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         DripParticle dripparticle = new DripParticle.Landing(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, Fluids.EMPTY);
         dripparticle.isGlowing = true;
         dripparticle.lifetime = (int)(28.0D / (Math.random() * 0.8D + 0.2D));
         dripparticle.setColor(0.51171875F, 0.03125F, 0.890625F);
         dripparticle.pickSprite(this.sprite);
         return dripparticle;
      }
   }
}
