package net.minecraft.advancements;

import com.google.gson.JsonObject;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;

public interface ICriterionTrigger<T extends ICriterionInstance> {
   ResourceLocation getId();

   void addPlayerListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<T> p_192165_2_);

   void removePlayerListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<T> p_192164_2_);

   void removePlayerListeners(PlayerAdvancements p_192167_1_);

   T createInstance(JsonObject p_230307_1_, ConditionArrayParser p_230307_2_);

   public static class Listener<T extends ICriterionInstance> {
      private final T trigger;
      private final Advancement advancement;
      private final String criterion;

      public Listener(T p_i47405_1_, Advancement p_i47405_2_, String p_i47405_3_) {
         this.trigger = p_i47405_1_;
         this.advancement = p_i47405_2_;
         this.criterion = p_i47405_3_;
      }

      public T getTriggerInstance() {
         return this.trigger;
      }

      public void run(PlayerAdvancements p_192159_1_) {
         p_192159_1_.award(this.advancement, this.criterion);
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            ICriterionTrigger.Listener<?> listener = (ICriterionTrigger.Listener)p_equals_1_;
            if (!this.trigger.equals(listener.trigger)) {
               return false;
            } else {
               return !this.advancement.equals(listener.advancement) ? false : this.criterion.equals(listener.criterion);
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         int i = this.trigger.hashCode();
         i = 31 * i + this.advancement.hashCode();
         return 31 * i + this.criterion.hashCode();
      }
   }
}
