package net.minecraft.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class CopyBlockState extends LootFunction {
   private final Block block;
   private final Set<Property<?>> properties;

   private CopyBlockState(ILootCondition[] p_i225890_1_, Block p_i225890_2_, Set<Property<?>> p_i225890_3_) {
      super(p_i225890_1_);
      this.block = p_i225890_2_;
      this.properties = p_i225890_3_;
   }

   public LootFunctionType getType() {
      return LootFunctionManager.COPY_STATE;
   }

   public Set<LootParameter<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootParameters.BLOCK_STATE);
   }

   protected ItemStack run(ItemStack p_215859_1_, LootContext p_215859_2_) {
      BlockState blockstate = p_215859_2_.getParamOrNull(LootParameters.BLOCK_STATE);
      if (blockstate != null) {
         CompoundNBT compoundnbt = p_215859_1_.getOrCreateTag();
         CompoundNBT compoundnbt1;
         if (compoundnbt.contains("BlockStateTag", 10)) {
            compoundnbt1 = compoundnbt.getCompound("BlockStateTag");
         } else {
            compoundnbt1 = new CompoundNBT();
            compoundnbt.put("BlockStateTag", compoundnbt1);
         }

         this.properties.stream().filter(blockstate::hasProperty).forEach((p_227548_2_) -> {
            compoundnbt1.putString(p_227548_2_.getName(), serialize(blockstate, p_227548_2_));
         });
      }

      return p_215859_1_;
   }

   public static CopyBlockState.Builder copyState(Block p_227545_0_) {
      return new CopyBlockState.Builder(p_227545_0_);
   }

   private static <T extends Comparable<T>> String serialize(BlockState p_227546_0_, Property<T> p_227546_1_) {
      T t = p_227546_0_.getValue(p_227546_1_);
      return p_227546_1_.getName(t);
   }

   public static class Builder extends LootFunction.Builder<CopyBlockState.Builder> {
      private final Block block;
      private final Set<Property<?>> properties = Sets.newHashSet();

      private Builder(Block p_i225892_1_) {
         this.block = p_i225892_1_;
      }

      public CopyBlockState.Builder copy(Property<?> p_227552_1_) {
         if (!this.block.getStateDefinition().getProperties().contains(p_227552_1_)) {
            throw new IllegalStateException("Property " + p_227552_1_ + " is not present on block " + this.block);
         } else {
            this.properties.add(p_227552_1_);
            return this;
         }
      }

      protected CopyBlockState.Builder getThis() {
         return this;
      }

      public ILootFunction build() {
         return new CopyBlockState(this.getConditions(), this.block, this.properties);
      }
   }

   public static class Serializer extends LootFunction.Serializer<CopyBlockState> {
      public void serialize(JsonObject p_230424_1_, CopyBlockState p_230424_2_, JsonSerializationContext p_230424_3_) {
         super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
         p_230424_1_.addProperty("block", Registry.BLOCK.getKey(p_230424_2_.block).toString());
         JsonArray jsonarray = new JsonArray();
         p_230424_2_.properties.forEach((p_227553_1_) -> {
            jsonarray.add(p_227553_1_.getName());
         });
         p_230424_1_.add("properties", jsonarray);
      }

      public CopyBlockState deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_186530_1_, "block"));
         Block block = Registry.BLOCK.getOptional(resourcelocation).orElseThrow(() -> {
            return new IllegalArgumentException("Can't find block " + resourcelocation);
         });
         StateContainer<Block, BlockState> statecontainer = block.getStateDefinition();
         Set<Property<?>> set = Sets.newHashSet();
         JsonArray jsonarray = JSONUtils.getAsJsonArray(p_186530_1_, "properties", (JsonArray)null);
         if (jsonarray != null) {
            jsonarray.forEach((p_227554_2_) -> {
               set.add(statecontainer.getProperty(JSONUtils.convertToString(p_227554_2_, "property")));
            });
         }

         return new CopyBlockState(p_186530_3_, block, set);
      }
   }
}
