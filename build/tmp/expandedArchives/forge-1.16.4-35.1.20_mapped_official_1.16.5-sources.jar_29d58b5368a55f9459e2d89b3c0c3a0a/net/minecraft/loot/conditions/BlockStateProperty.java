package net.minecraft.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Set;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class BlockStateProperty implements ILootCondition {
   private final Block block;
   private final StatePropertiesPredicate properties;

   private BlockStateProperty(Block p_i225896_1_, StatePropertiesPredicate p_i225896_2_) {
      this.block = p_i225896_1_;
      this.properties = p_i225896_2_;
   }

   public LootConditionType getType() {
      return LootConditionManager.BLOCK_STATE_PROPERTY;
   }

   public Set<LootParameter<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootParameters.BLOCK_STATE);
   }

   public boolean test(LootContext p_test_1_) {
      BlockState blockstate = p_test_1_.getParamOrNull(LootParameters.BLOCK_STATE);
      return blockstate != null && this.block == blockstate.getBlock() && this.properties.matches(blockstate);
   }

   public static BlockStateProperty.Builder hasBlockStateProperties(Block p_215985_0_) {
      return new BlockStateProperty.Builder(p_215985_0_);
   }

   public static class Builder implements ILootCondition.IBuilder {
      private final Block block;
      private StatePropertiesPredicate properties = StatePropertiesPredicate.ANY;

      public Builder(Block p_i50576_1_) {
         this.block = p_i50576_1_;
      }

      public BlockStateProperty.Builder setProperties(StatePropertiesPredicate.Builder p_227567_1_) {
         this.properties = p_227567_1_.build();
         return this;
      }

      public ILootCondition build() {
         return new BlockStateProperty(this.block, this.properties);
      }
   }

   public static class Serializer implements ILootSerializer<BlockStateProperty> {
      public void serialize(JsonObject p_230424_1_, BlockStateProperty p_230424_2_, JsonSerializationContext p_230424_3_) {
         p_230424_1_.addProperty("block", Registry.BLOCK.getKey(p_230424_2_.block).toString());
         p_230424_1_.add("properties", p_230424_2_.properties.serializeToJson());
      }

      public BlockStateProperty deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_230423_1_, "block"));
         Block block = Registry.BLOCK.getOptional(resourcelocation).orElseThrow(() -> {
            return new IllegalArgumentException("Can't find block " + resourcelocation);
         });
         StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.fromJson(p_230423_1_.get("properties"));
         statepropertiespredicate.checkState(block.getStateDefinition(), (p_227568_1_) -> {
            throw new JsonSyntaxException("Block " + block + " has no property " + p_227568_1_);
         });
         return new BlockStateProperty(block, statepropertiespredicate);
      }
   }
}
