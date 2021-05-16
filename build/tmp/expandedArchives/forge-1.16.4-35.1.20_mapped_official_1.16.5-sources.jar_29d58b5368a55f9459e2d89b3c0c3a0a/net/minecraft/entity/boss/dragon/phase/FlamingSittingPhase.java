package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class FlamingSittingPhase extends SittingPhase {
   private int flameTicks;
   private int flameCount;
   private AreaEffectCloudEntity flame;

   public FlamingSittingPhase(EnderDragonEntity p_i46786_1_) {
      super(p_i46786_1_);
   }

   public void doClientTick() {
      ++this.flameTicks;
      if (this.flameTicks % 2 == 0 && this.flameTicks < 10) {
         Vector3d vector3d = this.dragon.getHeadLookVector(1.0F).normalize();
         vector3d.yRot((-(float)Math.PI / 4F));
         double d0 = this.dragon.head.getX();
         double d1 = this.dragon.head.getY(0.5D);
         double d2 = this.dragon.head.getZ();

         for(int i = 0; i < 8; ++i) {
            double d3 = d0 + this.dragon.getRandom().nextGaussian() / 2.0D;
            double d4 = d1 + this.dragon.getRandom().nextGaussian() / 2.0D;
            double d5 = d2 + this.dragon.getRandom().nextGaussian() / 2.0D;

            for(int j = 0; j < 6; ++j) {
               this.dragon.level.addParticle(ParticleTypes.DRAGON_BREATH, d3, d4, d5, -vector3d.x * (double)0.08F * (double)j, -vector3d.y * (double)0.6F, -vector3d.z * (double)0.08F * (double)j);
            }

            vector3d.yRot(0.19634955F);
         }
      }

   }

   public void doServerTick() {
      ++this.flameTicks;
      if (this.flameTicks >= 200) {
         if (this.flameCount >= 4) {
            this.dragon.getPhaseManager().setPhase(PhaseType.TAKEOFF);
         } else {
            this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_SCANNING);
         }
      } else if (this.flameTicks == 10) {
         Vector3d vector3d = (new Vector3d(this.dragon.head.getX() - this.dragon.getX(), 0.0D, this.dragon.head.getZ() - this.dragon.getZ())).normalize();
         float f = 5.0F;
         double d0 = this.dragon.head.getX() + vector3d.x * 5.0D / 2.0D;
         double d1 = this.dragon.head.getZ() + vector3d.z * 5.0D / 2.0D;
         double d2 = this.dragon.head.getY(0.5D);
         double d3 = d2;
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(d0, d2, d1);

         while(this.dragon.level.isEmptyBlock(blockpos$mutable)) {
            --d3;
            if (d3 < 0.0D) {
               d3 = d2;
               break;
            }

            blockpos$mutable.set(d0, d3, d1);
         }

         d3 = (double)(MathHelper.floor(d3) + 1);
         this.flame = new AreaEffectCloudEntity(this.dragon.level, d0, d3, d1);
         this.flame.setOwner(this.dragon);
         this.flame.setRadius(5.0F);
         this.flame.setDuration(200);
         this.flame.setParticle(ParticleTypes.DRAGON_BREATH);
         this.flame.addEffect(new EffectInstance(Effects.HARM));
         this.dragon.level.addFreshEntity(this.flame);
      }

   }

   public void begin() {
      this.flameTicks = 0;
      ++this.flameCount;
   }

   public void end() {
      if (this.flame != null) {
         this.flame.remove();
         this.flame = null;
      }

   }

   public PhaseType<FlamingSittingPhase> getPhase() {
      return PhaseType.SITTING_FLAMING;
   }

   public void resetFlameCount() {
      this.flameCount = 0;
   }
}
