package net.minecraft.loot;

import com.google.gson.GsonBuilder;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.LootConditionManager;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.loot.functions.LootFunctionManager;

public class LootSerializers {
   public static GsonBuilder createConditionSerializer() {
      return (new GsonBuilder()).registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer()).registerTypeAdapter(BinomialRange.class, new BinomialRange.Serializer()).registerTypeAdapter(ConstantRange.class, new ConstantRange.Serializer()).registerTypeHierarchyAdapter(ILootCondition.class, LootConditionManager.createGsonAdapter()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer());
   }

   public static GsonBuilder createFunctionSerializer() {
      return createConditionSerializer().registerTypeAdapter(IntClamper.class, new IntClamper.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, LootEntryManager.createGsonAdapter()).registerTypeHierarchyAdapter(ILootFunction.class, LootFunctionManager.createGsonAdapter());
   }

   public static GsonBuilder createLootTableSerializer() {
      return createFunctionSerializer().registerTypeAdapter(LootPool.class, new LootPool.Serializer()).registerTypeAdapter(LootTable.class, new LootTable.Serializer());
   }
}
