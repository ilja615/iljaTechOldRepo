package net.minecraft.util;

public enum ActionResultType {
   SUCCESS,
   CONSUME,
   PASS,
   FAIL;

   public boolean consumesAction() {
      return this == SUCCESS || this == CONSUME;
   }

   public boolean shouldSwing() {
      return this == SUCCESS;
   }

   public static ActionResultType sidedSuccess(boolean p_233537_0_) {
      return p_233537_0_ ? SUCCESS : CONSUME;
   }
}
