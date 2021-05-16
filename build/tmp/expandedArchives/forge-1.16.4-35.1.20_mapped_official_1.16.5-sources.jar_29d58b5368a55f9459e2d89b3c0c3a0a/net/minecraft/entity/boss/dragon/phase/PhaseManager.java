package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PhaseManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final EnderDragonEntity dragon;
   private final IPhase[] phases = new IPhase[PhaseType.getCount()];
   private IPhase currentPhase;

   public PhaseManager(EnderDragonEntity p_i46781_1_) {
      this.dragon = p_i46781_1_;
      this.setPhase(PhaseType.HOVERING);
   }

   public void setPhase(PhaseType<?> p_188758_1_) {
      if (this.currentPhase == null || p_188758_1_ != this.currentPhase.getPhase()) {
         if (this.currentPhase != null) {
            this.currentPhase.end();
         }

         this.currentPhase = this.getPhase(p_188758_1_);
         if (!this.dragon.level.isClientSide) {
            this.dragon.getEntityData().set(EnderDragonEntity.DATA_PHASE, p_188758_1_.getId());
         }

         LOGGER.debug("Dragon is now in phase {} on the {}", p_188758_1_, this.dragon.level.isClientSide ? "client" : "server");
         this.currentPhase.begin();
      }
   }

   public IPhase getCurrentPhase() {
      return this.currentPhase;
   }

   public <T extends IPhase> T getPhase(PhaseType<T> p_188757_1_) {
      int i = p_188757_1_.getId();
      if (this.phases[i] == null) {
         this.phases[i] = p_188757_1_.createInstance(this.dragon);
      }

      return (T)this.phases[i];
   }
}
