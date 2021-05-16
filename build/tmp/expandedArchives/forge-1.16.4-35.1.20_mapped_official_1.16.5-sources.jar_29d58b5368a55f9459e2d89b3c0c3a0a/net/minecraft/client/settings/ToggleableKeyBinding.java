package net.minecraft.client.settings;

import java.util.function.BooleanSupplier;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ToggleableKeyBinding extends KeyBinding {
   private final BooleanSupplier needsToggle;

   public ToggleableKeyBinding(String p_i225917_1_, int p_i225917_2_, String p_i225917_3_, BooleanSupplier p_i225917_4_) {
      super(p_i225917_1_, InputMappings.Type.KEYSYM, p_i225917_2_, p_i225917_3_);
      this.needsToggle = p_i225917_4_;
   }

   public void setDown(boolean p_225593_1_) {
      if (this.needsToggle.getAsBoolean()) {
         if (p_225593_1_ && isConflictContextAndModifierActive()) {
            super.setDown(!this.isDown());
         }
      } else {
         super.setDown(p_225593_1_);
      }

   }
   @Override public boolean isDown() { return this.isDown && (isConflictContextAndModifierActive() || needsToggle.getAsBoolean()); }
}
