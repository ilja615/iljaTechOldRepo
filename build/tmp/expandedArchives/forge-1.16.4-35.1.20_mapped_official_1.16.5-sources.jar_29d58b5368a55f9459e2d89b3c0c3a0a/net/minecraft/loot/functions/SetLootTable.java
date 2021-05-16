package net.minecraft.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class SetLootTable extends LootFunction {
   private final ResourceLocation name;
   private final long seed;

   private SetLootTable(ILootCondition[] p_i51224_1_, ResourceLocation p_i51224_2_, long p_i51224_3_) {
      super(p_i51224_1_);
      this.name = p_i51224_2_;
      this.seed = p_i51224_3_;
   }

   public LootFunctionType getType() {
      return LootFunctionManager.SET_LOOT_TABLE;
   }

   public ItemStack run(ItemStack p_215859_1_, LootContext p_215859_2_) {
      if (p_215859_1_.isEmpty()) {
         return p_215859_1_;
      } else {
         CompoundNBT compoundnbt = new CompoundNBT();
         compoundnbt.putString("LootTable", this.name.toString());
         if (this.seed != 0L) {
            compoundnbt.putLong("LootTableSeed", this.seed);
         }

         p_215859_1_.getOrCreateTag().put("BlockEntityTag", compoundnbt);
         return p_215859_1_;
      }
   }

   public void validate(ValidationTracker p_225580_1_) {
      if (p_225580_1_.hasVisitedTable(this.name)) {
         p_225580_1_.reportProblem("Table " + this.name + " is recursively called");
      } else {
         super.validate(p_225580_1_);
         LootTable loottable = p_225580_1_.resolveLootTable(this.name);
         if (loottable == null) {
            p_225580_1_.reportProblem("Unknown loot table called " + this.name);
         } else {
            loottable.validate(p_225580_1_.enterTable("->{" + this.name + "}", this.name));
         }

      }
   }

   public static class Serializer extends LootFunction.Serializer<SetLootTable> {
      public void serialize(JsonObject p_230424_1_, SetLootTable p_230424_2_, JsonSerializationContext p_230424_3_) {
         super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
         p_230424_1_.addProperty("name", p_230424_2_.name.toString());
         if (p_230424_2_.seed != 0L) {
            p_230424_1_.addProperty("seed", p_230424_2_.seed);
         }

      }

      public SetLootTable deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_186530_1_, "name"));
         long i = JSONUtils.getAsLong(p_186530_1_, "seed", 0L);
         return new SetLootTable(p_186530_3_, resourcelocation, i);
      }
   }
}
