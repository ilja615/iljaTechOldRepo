package net.minecraft.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Arrays;
import java.util.List;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;

public class SetContents extends LootFunction {
   private final List<LootEntry> entries;

   private SetContents(ILootCondition[] p_i51226_1_, List<LootEntry> p_i51226_2_) {
      super(p_i51226_1_);
      this.entries = ImmutableList.copyOf(p_i51226_2_);
   }

   public LootFunctionType getType() {
      return LootFunctionManager.SET_CONTENTS;
   }

   public ItemStack run(ItemStack p_215859_1_, LootContext p_215859_2_) {
      if (p_215859_1_.isEmpty()) {
         return p_215859_1_;
      } else {
         NonNullList<ItemStack> nonnulllist = NonNullList.create();
         this.entries.forEach((p_215921_2_) -> {
            p_215921_2_.expand(p_215859_2_, (p_215922_2_) -> {
               p_215922_2_.createItemStack(LootTable.createStackSplitter(nonnulllist::add), p_215859_2_);
            });
         });
         CompoundNBT compoundnbt = new CompoundNBT();
         ItemStackHelper.saveAllItems(compoundnbt, nonnulllist);
         CompoundNBT compoundnbt1 = p_215859_1_.getOrCreateTag();
         compoundnbt1.put("BlockEntityTag", compoundnbt.merge(compoundnbt1.getCompound("BlockEntityTag")));
         return p_215859_1_;
      }
   }

   public void validate(ValidationTracker p_225580_1_) {
      super.validate(p_225580_1_);

      for(int i = 0; i < this.entries.size(); ++i) {
         this.entries.get(i).validate(p_225580_1_.forChild(".entry[" + i + "]"));
      }

   }

   public static SetContents.Builder setContents() {
      return new SetContents.Builder();
   }

   public static class Builder extends LootFunction.Builder<SetContents.Builder> {
      private final List<LootEntry> entries = Lists.newArrayList();

      protected SetContents.Builder getThis() {
         return this;
      }

      public SetContents.Builder withEntry(LootEntry.Builder<?> p_216075_1_) {
         this.entries.add(p_216075_1_.build());
         return this;
      }

      public ILootFunction build() {
         return new SetContents(this.getConditions(), this.entries);
      }
   }

   public static class Serializer extends LootFunction.Serializer<SetContents> {
      public void serialize(JsonObject p_230424_1_, SetContents p_230424_2_, JsonSerializationContext p_230424_3_) {
         super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
         p_230424_1_.add("entries", p_230424_3_.serialize(p_230424_2_.entries));
      }

      public SetContents deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         LootEntry[] alootentry = JSONUtils.getAsObject(p_186530_1_, "entries", p_186530_2_, LootEntry[].class);
         return new SetContents(p_186530_3_, Arrays.asList(alootentry));
      }
   }
}
