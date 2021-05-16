package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class BeeNestDestroyedTrigger extends AbstractCriterionTrigger<BeeNestDestroyedTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("bee_nest_destroyed");

   public ResourceLocation getId() {
      return ID;
   }

   public BeeNestDestroyedTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      Block block = deserializeBlock(p_230241_1_);
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_230241_1_.get("item"));
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(p_230241_1_.get("num_bees_inside"));
      return new BeeNestDestroyedTrigger.Instance(p_230241_2_, block, itempredicate, minmaxbounds$intbound);
   }

   @Nullable
   private static Block deserializeBlock(JsonObject p_226221_0_) {
      if (p_226221_0_.has("block")) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_226221_0_, "block"));
         return Registry.BLOCK.getOptional(resourcelocation).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
         });
      } else {
         return null;
      }
   }

   public void trigger(ServerPlayerEntity p_226223_1_, Block p_226223_2_, ItemStack p_226223_3_, int p_226223_4_) {
      this.trigger(p_226223_1_, (p_226220_3_) -> {
         return p_226220_3_.matches(p_226223_2_, p_226223_3_, p_226223_4_);
      });
   }

   public static class Instance extends CriterionInstance {
      @Nullable
      private final Block block;
      private final ItemPredicate item;
      private final MinMaxBounds.IntBound numBees;

      public Instance(EntityPredicate.AndPredicate p_i231471_1_, @Nullable Block p_i231471_2_, ItemPredicate p_i231471_3_, MinMaxBounds.IntBound p_i231471_4_) {
         super(BeeNestDestroyedTrigger.ID, p_i231471_1_);
         this.block = p_i231471_2_;
         this.item = p_i231471_3_;
         this.numBees = p_i231471_4_;
      }

      public static BeeNestDestroyedTrigger.Instance destroyedBeeNest(Block p_226229_0_, ItemPredicate.Builder p_226229_1_, MinMaxBounds.IntBound p_226229_2_) {
         return new BeeNestDestroyedTrigger.Instance(EntityPredicate.AndPredicate.ANY, p_226229_0_, p_226229_1_.build(), p_226229_2_);
      }

      public boolean matches(Block p_226228_1_, ItemStack p_226228_2_, int p_226228_3_) {
         if (this.block != null && p_226228_1_ != this.block) {
            return false;
         } else {
            return !this.item.matches(p_226228_2_) ? false : this.numBees.matches(p_226228_3_);
         }
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         if (this.block != null) {
            jsonobject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         jsonobject.add("item", this.item.serializeToJson());
         jsonobject.add("num_bees_inside", this.numBees.serializeToJson());
         return jsonobject;
      }
   }
}
