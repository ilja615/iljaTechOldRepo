package net.minecraft.entity.boss.dragon.phase;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

public class PhaseType<T extends IPhase> {
   private static PhaseType<?>[] phases = new PhaseType[0];
   public static final PhaseType<HoldingPatternPhase> HOLDING_PATTERN = create(HoldingPatternPhase.class, "HoldingPattern");
   public static final PhaseType<StrafePlayerPhase> STRAFE_PLAYER = create(StrafePlayerPhase.class, "StrafePlayer");
   public static final PhaseType<LandingApproachPhase> LANDING_APPROACH = create(LandingApproachPhase.class, "LandingApproach");
   public static final PhaseType<LandingPhase> LANDING = create(LandingPhase.class, "Landing");
   public static final PhaseType<TakeoffPhase> TAKEOFF = create(TakeoffPhase.class, "Takeoff");
   public static final PhaseType<FlamingSittingPhase> SITTING_FLAMING = create(FlamingSittingPhase.class, "SittingFlaming");
   public static final PhaseType<ScanningSittingPhase> SITTING_SCANNING = create(ScanningSittingPhase.class, "SittingScanning");
   public static final PhaseType<AttackingSittingPhase> SITTING_ATTACKING = create(AttackingSittingPhase.class, "SittingAttacking");
   public static final PhaseType<ChargingPlayerPhase> CHARGING_PLAYER = create(ChargingPlayerPhase.class, "ChargingPlayer");
   public static final PhaseType<DyingPhase> DYING = create(DyingPhase.class, "Dying");
   public static final PhaseType<HoverPhase> HOVERING = create(HoverPhase.class, "Hover");
   private final Class<? extends IPhase> instanceClass;
   private final int id;
   private final String name;

   private PhaseType(int p_i46782_1_, Class<? extends IPhase> p_i46782_2_, String p_i46782_3_) {
      this.id = p_i46782_1_;
      this.instanceClass = p_i46782_2_;
      this.name = p_i46782_3_;
   }

   public IPhase createInstance(EnderDragonEntity p_188736_1_) {
      try {
         Constructor<? extends IPhase> constructor = this.getConstructor();
         return constructor.newInstance(p_188736_1_);
      } catch (Exception exception) {
         throw new Error(exception);
      }
   }

   protected Constructor<? extends IPhase> getConstructor() throws NoSuchMethodException {
      return this.instanceClass.getConstructor(EnderDragonEntity.class);
   }

   public int getId() {
      return this.id;
   }

   public String toString() {
      return this.name + " (#" + this.id + ")";
   }

   public static PhaseType<?> getById(int p_188738_0_) {
      return p_188738_0_ >= 0 && p_188738_0_ < phases.length ? phases[p_188738_0_] : HOLDING_PATTERN;
   }

   public static int getCount() {
      return phases.length;
   }

   private static <T extends IPhase> PhaseType<T> create(Class<T> p_188735_0_, String p_188735_1_) {
      PhaseType<T> phasetype = new PhaseType<>(phases.length, p_188735_0_, p_188735_1_);
      phases = Arrays.copyOf(phases, phases.length + 1);
      phases[phasetype.getId()] = phasetype;
      return phasetype;
   }
}
