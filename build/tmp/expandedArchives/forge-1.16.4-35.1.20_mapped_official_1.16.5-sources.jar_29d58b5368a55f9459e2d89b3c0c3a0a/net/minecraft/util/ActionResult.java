package net.minecraft.util;

public class ActionResult<T> {
   private final ActionResultType result;
   private final T object;

   public ActionResult(ActionResultType p_i46821_1_, T p_i46821_2_) {
      this.result = p_i46821_1_;
      this.object = p_i46821_2_;
   }

   public ActionResultType getResult() {
      return this.result;
   }

   public T getObject() {
      return this.object;
   }

   public static <T> ActionResult<T> success(T p_226248_0_) {
      return new ActionResult<>(ActionResultType.SUCCESS, p_226248_0_);
   }

   public static <T> ActionResult<T> consume(T p_226249_0_) {
      return new ActionResult<>(ActionResultType.CONSUME, p_226249_0_);
   }

   public static <T> ActionResult<T> pass(T p_226250_0_) {
      return new ActionResult<>(ActionResultType.PASS, p_226250_0_);
   }

   public static <T> ActionResult<T> fail(T p_226251_0_) {
      return new ActionResult<>(ActionResultType.FAIL, p_226251_0_);
   }

   public static <T> ActionResult<T> sidedSuccess(T p_233538_0_, boolean p_233538_1_) {
      return p_233538_1_ ? success(p_233538_0_) : consume(p_233538_0_);
   }
}
