package net.minecraft.loot.conditions;

import java.util.function.Predicate;
import net.minecraft.loot.IParameterized;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;

public interface ILootCondition extends IParameterized, Predicate<LootContext> {
   LootConditionType getType();

   @FunctionalInterface
   public interface IBuilder {
      ILootCondition build();

      default ILootCondition.IBuilder invert() {
         return Inverted.invert(this);
      }

      default Alternative.Builder or(ILootCondition.IBuilder p_216297_1_) {
         return Alternative.alternative(this, p_216297_1_);
      }
   }
}
