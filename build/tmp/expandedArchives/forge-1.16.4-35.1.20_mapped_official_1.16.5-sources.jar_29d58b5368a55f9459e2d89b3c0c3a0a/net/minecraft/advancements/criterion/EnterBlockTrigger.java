package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class EnterBlockTrigger extends AbstractCriterionTrigger<EnterBlockTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("enter_block");

   public ResourceLocation getId() {
      return ID;
   }

   public EnterBlockTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      Block block = deserializeBlock(p_230241_1_);
      StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.fromJson(p_230241_1_.get("state"));
      if (block != null) {
         statepropertiespredicate.checkState(block.getStateDefinition(), (p_226548_1_) -> {
            throw new JsonSyntaxException("Block " + block + " has no property " + p_226548_1_);
         });
      }

      return new EnterBlockTrigger.Instance(p_230241_2_, block, statepropertiespredicate);
   }

   @Nullable
   private static Block deserializeBlock(JsonObject p_226550_0_) {
      if (p_226550_0_.has("block")) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_226550_0_, "block"));
         return Registry.BLOCK.getOptional(resourcelocation).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
         });
      } else {
         return null;
      }
   }

   public void trigger(ServerPlayerEntity p_192193_1_, BlockState p_192193_2_) {
      this.trigger(p_192193_1_, (p_226549_1_) -> {
         return p_226549_1_.matches(p_192193_2_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final Block block;
      private final StatePropertiesPredicate state;

      public Instance(EntityPredicate.AndPredicate p_i231560_1_, @Nullable Block p_i231560_2_, StatePropertiesPredicate p_i231560_3_) {
         super(EnterBlockTrigger.ID, p_i231560_1_);
         this.block = p_i231560_2_;
         this.state = p_i231560_3_;
      }

      public static EnterBlockTrigger.Instance entersBlock(Block p_203920_0_) {
         return new EnterBlockTrigger.Instance(EntityPredicate.AndPredicate.ANY, p_203920_0_, StatePropertiesPredicate.ANY);
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         if (this.block != null) {
            jsonobject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         jsonobject.add("state", this.state.serializeToJson());
         return jsonobject;
      }

      public boolean matches(BlockState p_192260_1_) {
         if (this.block != null && !p_192260_1_.is(this.block)) {
            return false;
         } else {
            return this.state.matches(p_192260_1_);
         }
      }
   }
}
