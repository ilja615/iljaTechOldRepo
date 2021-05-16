package net.minecraft.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.ITextComponent;

public class SetLore extends LootFunction {
   private final boolean replace;
   private final List<ITextComponent> lore;
   @Nullable
   private final LootContext.EntityTarget resolutionContext;

   public SetLore(ILootCondition[] p_i51220_1_, boolean p_i51220_2_, List<ITextComponent> p_i51220_3_, @Nullable LootContext.EntityTarget p_i51220_4_) {
      super(p_i51220_1_);
      this.replace = p_i51220_2_;
      this.lore = ImmutableList.copyOf(p_i51220_3_);
      this.resolutionContext = p_i51220_4_;
   }

   public LootFunctionType getType() {
      return LootFunctionManager.SET_LORE;
   }

   public Set<LootParameter<?>> getReferencedContextParams() {
      return this.resolutionContext != null ? ImmutableSet.of(this.resolutionContext.getParam()) : ImmutableSet.of();
   }

   public ItemStack run(ItemStack p_215859_1_, LootContext p_215859_2_) {
      ListNBT listnbt = this.getLoreTag(p_215859_1_, !this.lore.isEmpty());
      if (listnbt != null) {
         if (this.replace) {
            listnbt.clear();
         }

         UnaryOperator<ITextComponent> unaryoperator = SetName.createResolver(p_215859_2_, this.resolutionContext);
         this.lore.stream().map(unaryoperator).map(ITextComponent.Serializer::toJson).map(StringNBT::valueOf).forEach(listnbt::add);
      }

      return p_215859_1_;
   }

   @Nullable
   private ListNBT getLoreTag(ItemStack p_215942_1_, boolean p_215942_2_) {
      CompoundNBT compoundnbt;
      if (p_215942_1_.hasTag()) {
         compoundnbt = p_215942_1_.getTag();
      } else {
         if (!p_215942_2_) {
            return null;
         }

         compoundnbt = new CompoundNBT();
         p_215942_1_.setTag(compoundnbt);
      }

      CompoundNBT compoundnbt1;
      if (compoundnbt.contains("display", 10)) {
         compoundnbt1 = compoundnbt.getCompound("display");
      } else {
         if (!p_215942_2_) {
            return null;
         }

         compoundnbt1 = new CompoundNBT();
         compoundnbt.put("display", compoundnbt1);
      }

      if (compoundnbt1.contains("Lore", 9)) {
         return compoundnbt1.getList("Lore", 8);
      } else if (p_215942_2_) {
         ListNBT listnbt = new ListNBT();
         compoundnbt1.put("Lore", listnbt);
         return listnbt;
      } else {
         return null;
      }
   }

   public static class Serializer extends LootFunction.Serializer<SetLore> {
      public void serialize(JsonObject p_230424_1_, SetLore p_230424_2_, JsonSerializationContext p_230424_3_) {
         super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
         p_230424_1_.addProperty("replace", p_230424_2_.replace);
         JsonArray jsonarray = new JsonArray();

         for(ITextComponent itextcomponent : p_230424_2_.lore) {
            jsonarray.add(ITextComponent.Serializer.toJsonTree(itextcomponent));
         }

         p_230424_1_.add("lore", jsonarray);
         if (p_230424_2_.resolutionContext != null) {
            p_230424_1_.add("entity", p_230424_3_.serialize(p_230424_2_.resolutionContext));
         }

      }

      public SetLore deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         boolean flag = JSONUtils.getAsBoolean(p_186530_1_, "replace", false);
         List<ITextComponent> list = Streams.stream(JSONUtils.getAsJsonArray(p_186530_1_, "lore")).map(ITextComponent.Serializer::fromJson).collect(ImmutableList.toImmutableList());
         LootContext.EntityTarget lootcontext$entitytarget = JSONUtils.getAsObject(p_186530_1_, "entity", (LootContext.EntityTarget)null, p_186530_2_, LootContext.EntityTarget.class);
         return new SetLore(p_186530_3_, flag, list, lootcontext$entitytarget);
      }
   }
}
