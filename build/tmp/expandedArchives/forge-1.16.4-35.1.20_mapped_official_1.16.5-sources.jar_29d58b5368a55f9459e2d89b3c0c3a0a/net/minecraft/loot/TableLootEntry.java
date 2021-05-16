package net.minecraft.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class TableLootEntry extends StandaloneLootEntry {
   private final ResourceLocation name;

   private TableLootEntry(ResourceLocation p_i51251_1_, int p_i51251_2_, int p_i51251_3_, ILootCondition[] p_i51251_4_, ILootFunction[] p_i51251_5_) {
      super(p_i51251_2_, p_i51251_3_, p_i51251_4_, p_i51251_5_);
      this.name = p_i51251_1_;
   }

   public LootPoolEntryType getType() {
      return LootEntryManager.REFERENCE;
   }

   public void createItemStack(Consumer<ItemStack> p_216154_1_, LootContext p_216154_2_) {
      LootTable loottable = p_216154_2_.getLootTable(this.name);
      loottable.getRandomItemsRaw(p_216154_2_, p_216154_1_);
   }

   public void validate(ValidationTracker p_225579_1_) {
      if (p_225579_1_.hasVisitedTable(this.name)) {
         p_225579_1_.reportProblem("Table " + this.name + " is recursively called");
      } else {
         super.validate(p_225579_1_);
         LootTable loottable = p_225579_1_.resolveLootTable(this.name);
         if (loottable == null) {
            p_225579_1_.reportProblem("Unknown loot table called " + this.name);
         } else {
            loottable.validate(p_225579_1_.enterTable("->{" + this.name + "}", this.name));
         }

      }
   }

   public static StandaloneLootEntry.Builder<?> lootTableReference(ResourceLocation p_216171_0_) {
      return simpleBuilder((p_216173_1_, p_216173_2_, p_216173_3_, p_216173_4_) -> {
         return new TableLootEntry(p_216171_0_, p_216173_1_, p_216173_2_, p_216173_3_, p_216173_4_);
      });
   }

   public static class Serializer extends StandaloneLootEntry.Serializer<TableLootEntry> {
      public void serializeCustom(JsonObject p_230422_1_, TableLootEntry p_230422_2_, JsonSerializationContext p_230422_3_) {
         super.serializeCustom(p_230422_1_, p_230422_2_, p_230422_3_);
         p_230422_1_.addProperty("name", p_230422_2_.name.toString());
      }

      protected TableLootEntry deserialize(JsonObject p_212829_1_, JsonDeserializationContext p_212829_2_, int p_212829_3_, int p_212829_4_, ILootCondition[] p_212829_5_, ILootFunction[] p_212829_6_) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_212829_1_, "name"));
         return new TableLootEntry(resourcelocation, p_212829_3_, p_212829_4_, p_212829_5_, p_212829_6_);
      }
   }
}
