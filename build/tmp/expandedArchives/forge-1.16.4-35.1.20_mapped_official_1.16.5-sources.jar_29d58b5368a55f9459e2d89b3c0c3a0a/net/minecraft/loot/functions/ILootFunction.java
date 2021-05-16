package net.minecraft.loot.functions;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.IParameterized;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunctionType;

public interface ILootFunction extends IParameterized, BiFunction<ItemStack, LootContext, ItemStack> {
   LootFunctionType getType();

   static Consumer<ItemStack> decorate(BiFunction<ItemStack, LootContext, ItemStack> p_215858_0_, Consumer<ItemStack> p_215858_1_, LootContext p_215858_2_) {
      return (p_215857_3_) -> {
         p_215858_1_.accept(p_215858_0_.apply(p_215857_3_, p_215858_2_));
      };
   }

   public interface IBuilder {
      ILootFunction build();
   }
}
