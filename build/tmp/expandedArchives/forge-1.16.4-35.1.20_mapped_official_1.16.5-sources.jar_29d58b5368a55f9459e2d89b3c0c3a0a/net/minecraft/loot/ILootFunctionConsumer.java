package net.minecraft.loot;

import net.minecraft.loot.functions.ILootFunction;

public interface ILootFunctionConsumer<T> {
   T apply(ILootFunction.IBuilder p_212841_1_);

   T unwrap();
}
