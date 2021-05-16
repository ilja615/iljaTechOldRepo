package net.minecraft.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ILootFunction;

public class EmptyLootEntry extends StandaloneLootEntry {
   private EmptyLootEntry(int p_i51258_1_, int p_i51258_2_, ILootCondition[] p_i51258_3_, ILootFunction[] p_i51258_4_) {
      super(p_i51258_1_, p_i51258_2_, p_i51258_3_, p_i51258_4_);
   }

   public LootPoolEntryType getType() {
      return LootEntryManager.EMPTY;
   }

   public void createItemStack(Consumer<ItemStack> p_216154_1_, LootContext p_216154_2_) {
   }

   public static StandaloneLootEntry.Builder<?> emptyItem() {
      return simpleBuilder(EmptyLootEntry::new);
   }

   public static class Serializer extends StandaloneLootEntry.Serializer<EmptyLootEntry> {
      public EmptyLootEntry deserialize(JsonObject p_212829_1_, JsonDeserializationContext p_212829_2_, int p_212829_3_, int p_212829_4_, ILootCondition[] p_212829_5_, ILootFunction[] p_212829_6_) {
         return new EmptyLootEntry(p_212829_3_, p_212829_4_, p_212829_5_, p_212829_6_);
      }
   }
}
