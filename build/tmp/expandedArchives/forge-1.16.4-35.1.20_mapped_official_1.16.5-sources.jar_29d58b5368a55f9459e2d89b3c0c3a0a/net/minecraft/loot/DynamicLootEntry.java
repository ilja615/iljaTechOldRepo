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

public class DynamicLootEntry extends StandaloneLootEntry {
   private final ResourceLocation name;

   private DynamicLootEntry(ResourceLocation p_i51260_1_, int p_i51260_2_, int p_i51260_3_, ILootCondition[] p_i51260_4_, ILootFunction[] p_i51260_5_) {
      super(p_i51260_2_, p_i51260_3_, p_i51260_4_, p_i51260_5_);
      this.name = p_i51260_1_;
   }

   public LootPoolEntryType getType() {
      return LootEntryManager.DYNAMIC;
   }

   public void createItemStack(Consumer<ItemStack> p_216154_1_, LootContext p_216154_2_) {
      p_216154_2_.addDynamicDrops(this.name, p_216154_1_);
   }

   public static StandaloneLootEntry.Builder<?> dynamicEntry(ResourceLocation p_216162_0_) {
      return simpleBuilder((p_216164_1_, p_216164_2_, p_216164_3_, p_216164_4_) -> {
         return new DynamicLootEntry(p_216162_0_, p_216164_1_, p_216164_2_, p_216164_3_, p_216164_4_);
      });
   }

   public static class Serializer extends StandaloneLootEntry.Serializer<DynamicLootEntry> {
      public void serializeCustom(JsonObject p_230422_1_, DynamicLootEntry p_230422_2_, JsonSerializationContext p_230422_3_) {
         super.serializeCustom(p_230422_1_, p_230422_2_, p_230422_3_);
         p_230422_1_.addProperty("name", p_230422_2_.name.toString());
      }

      protected DynamicLootEntry deserialize(JsonObject p_212829_1_, JsonDeserializationContext p_212829_2_, int p_212829_3_, int p_212829_4_, ILootCondition[] p_212829_5_, ILootFunction[] p_212829_6_) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_212829_1_, "name"));
         return new DynamicLootEntry(resourcelocation, p_212829_3_, p_212829_4_, p_212829_5_, p_212829_6_);
      }
   }
}
