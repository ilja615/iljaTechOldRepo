package net.minecraft.potion;

public class InstantEffect extends Effect {
   public InstantEffect(EffectType p_i50392_1_, int p_i50392_2_) {
      super(p_i50392_1_, p_i50392_2_);
   }

   public boolean isInstantenous() {
      return true;
   }

   public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_) {
      return p_76397_1_ >= 1;
   }
}
