package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

public class EnchantedItemTrigger extends AbstractCriterionTrigger<EnchantedItemTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("enchanted_item");

   public ResourceLocation getId() {
      return ID;
   }

   public EnchantedItemTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_230241_1_.get("item"));
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(p_230241_1_.get("levels"));
      return new EnchantedItemTrigger.Instance(p_230241_2_, itempredicate, minmaxbounds$intbound);
   }

   public void trigger(ServerPlayerEntity p_192190_1_, ItemStack p_192190_2_, int p_192190_3_) {
      this.trigger(p_192190_1_, (p_226528_2_) -> {
         return p_226528_2_.matches(p_192190_2_, p_192190_3_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate item;
      private final MinMaxBounds.IntBound levels;

      public Instance(EntityPredicate.AndPredicate p_i231556_1_, ItemPredicate p_i231556_2_, MinMaxBounds.IntBound p_i231556_3_) {
         super(EnchantedItemTrigger.ID, p_i231556_1_);
         this.item = p_i231556_2_;
         this.levels = p_i231556_3_;
      }

      public static EnchantedItemTrigger.Instance enchantedItem() {
         return new EnchantedItemTrigger.Instance(EntityPredicate.AndPredicate.ANY, ItemPredicate.ANY, MinMaxBounds.IntBound.ANY);
      }

      public boolean matches(ItemStack p_192257_1_, int p_192257_2_) {
         if (!this.item.matches(p_192257_1_)) {
            return false;
         } else {
            return this.levels.matches(p_192257_2_);
         }
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.add("item", this.item.serializeToJson());
         jsonobject.add("levels", this.levels.serializeToJson());
         return jsonobject;
      }
   }
}
