package net.minecraft.loot;

import java.util.function.Consumer;
import net.minecraft.item.ItemStack;

public interface ILootGenerator {
   int getWeight(float p_186361_1_);

   void createItemStack(Consumer<ItemStack> p_216188_1_, LootContext p_216188_2_);
}
