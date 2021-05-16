package net.minecraft.loot;

import net.minecraft.loot.conditions.ILootCondition;

public interface ILootConditionConsumer<T> {
   T when(ILootCondition.IBuilder p_212840_1_);

   T unwrap();
}
