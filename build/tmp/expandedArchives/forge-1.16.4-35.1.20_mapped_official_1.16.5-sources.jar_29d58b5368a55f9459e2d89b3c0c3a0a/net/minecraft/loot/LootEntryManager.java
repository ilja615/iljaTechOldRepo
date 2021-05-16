package net.minecraft.loot;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class LootEntryManager {
   public static final LootPoolEntryType EMPTY = register("empty", new EmptyLootEntry.Serializer());
   public static final LootPoolEntryType ITEM = register("item", new ItemLootEntry.Serializer());
   public static final LootPoolEntryType REFERENCE = register("loot_table", new TableLootEntry.Serializer());
   public static final LootPoolEntryType DYNAMIC = register("dynamic", new DynamicLootEntry.Serializer());
   public static final LootPoolEntryType TAG = register("tag", new TagLootEntry.Serializer());
   public static final LootPoolEntryType ALTERNATIVES = register("alternatives", ParentedLootEntry.createSerializer(AlternativesLootEntry::new));
   public static final LootPoolEntryType SEQUENCE = register("sequence", ParentedLootEntry.createSerializer(SequenceLootEntry::new));
   public static final LootPoolEntryType GROUP = register("group", ParentedLootEntry.createSerializer(GroupLootEntry::new));

   private static LootPoolEntryType register(String p_237419_0_, ILootSerializer<? extends LootEntry> p_237419_1_) {
      return Registry.register(Registry.LOOT_POOL_ENTRY_TYPE, new ResourceLocation(p_237419_0_), new LootPoolEntryType(p_237419_1_));
   }

   public static Object createGsonAdapter() {
      return LootTypesManager.builder(Registry.LOOT_POOL_ENTRY_TYPE, "entry", "type", LootEntry::getType).build();
   }
}
