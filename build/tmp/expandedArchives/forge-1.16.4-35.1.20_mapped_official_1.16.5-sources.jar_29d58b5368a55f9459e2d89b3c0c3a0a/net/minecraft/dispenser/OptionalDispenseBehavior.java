package net.minecraft.dispenser;

public abstract class OptionalDispenseBehavior extends DefaultDispenseItemBehavior {
   private boolean success = true;

   public boolean isSuccess() {
      return this.success;
   }

   public void setSuccess(boolean p_239796_1_) {
      this.success = p_239796_1_;
   }

   protected void playSound(IBlockSource p_82485_1_) {
      p_82485_1_.getLevel().levelEvent(this.isSuccess() ? 1000 : 1001, p_82485_1_.getPos(), 0);
   }
}
