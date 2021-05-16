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

public class SlideDownBlockTrigger extends AbstractCriterionTrigger<SlideDownBlockTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("slide_down_block");

   public ResourceLocation getId() {
      return ID;
   }

   public SlideDownBlockTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      Block block = deserializeBlock(p_230241_1_);
      StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.fromJson(p_230241_1_.get("state"));
      if (block != null) {
         statepropertiespredicate.checkState(block.getStateDefinition(), (p_227148_1_) -> {
            throw new JsonSyntaxException("Block " + block + " has no property " + p_227148_1_);
         });
      }

      return new SlideDownBlockTrigger.Instance(p_230241_2_, block, statepropertiespredicate);
   }

   @Nullable
   private static Block deserializeBlock(JsonObject p_227150_0_) {
      if (p_227150_0_.has("block")) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_227150_0_, "block"));
         return Registry.BLOCK.getOptional(resourcelocation).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
         });
      } else {
         return null;
      }
   }

   public void trigger(ServerPlayerEntity p_227152_1_, BlockState p_227152_2_) {
      this.trigger(p_227152_1_, (p_227149_1_) -> {
         return p_227149_1_.matches(p_227152_2_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final Block block;
      private final StatePropertiesPredicate state;

      public Instance(EntityPredicate.AndPredicate p_i231896_1_, @Nullable Block p_i231896_2_, StatePropertiesPredicate p_i231896_3_) {
         super(SlideDownBlockTrigger.ID, p_i231896_1_);
         this.block = p_i231896_2_;
         this.state = p_i231896_3_;
      }

      public static SlideDownBlockTrigger.Instance slidesDownBlock(Block p_227156_0_) {
         return new SlideDownBlockTrigger.Instance(EntityPredicate.AndPredicate.ANY, p_227156_0_, StatePropertiesPredicate.ANY);
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         if (this.block != null) {
            jsonobject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         jsonobject.add("state", this.state.serializeToJson());
         return jsonobject;
      }

      public boolean matches(BlockState p_227157_1_) {
         if (this.block != null && !p_227157_1_.is(this.block)) {
            return false;
         } else {
            return this.state.matches(p_227157_1_);
         }
      }
   }
}
