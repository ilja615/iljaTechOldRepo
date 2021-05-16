package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;

public class ThrownItemPickedUpByEntityTrigger extends AbstractCriterionTrigger<ThrownItemPickedUpByEntityTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("thrown_item_picked_up_by_entity");

   public ResourceLocation getId() {
      return ID;
   }

   protected ThrownItemPickedUpByEntityTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_230241_1_.get("item"));
      EntityPredicate.AndPredicate entitypredicate$andpredicate = EntityPredicate.AndPredicate.fromJson(p_230241_1_, "entity", p_230241_3_);
      return new ThrownItemPickedUpByEntityTrigger.Instance(p_230241_2_, itempredicate, entitypredicate$andpredicate);
   }

   public void trigger(ServerPlayerEntity p_234830_1_, ItemStack p_234830_2_, Entity p_234830_3_) {
      LootContext lootcontext = EntityPredicate.createContext(p_234830_1_, p_234830_3_);
      this.trigger(p_234830_1_, (p_234831_3_) -> {
         return p_234831_3_.matches(p_234830_1_, p_234830_2_, lootcontext);
      });
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate item;
      private final EntityPredicate.AndPredicate entity;

      public Instance(EntityPredicate.AndPredicate p_i231599_1_, ItemPredicate p_i231599_2_, EntityPredicate.AndPredicate p_i231599_3_) {
         super(ThrownItemPickedUpByEntityTrigger.ID, p_i231599_1_);
         this.item = p_i231599_2_;
         this.entity = p_i231599_3_;
      }

      public static ThrownItemPickedUpByEntityTrigger.Instance itemPickedUpByEntity(EntityPredicate.AndPredicate p_234835_0_, ItemPredicate.Builder p_234835_1_, EntityPredicate.AndPredicate p_234835_2_) {
         return new ThrownItemPickedUpByEntityTrigger.Instance(p_234835_0_, p_234835_1_.build(), p_234835_2_);
      }

      public boolean matches(ServerPlayerEntity p_234836_1_, ItemStack p_234836_2_, LootContext p_234836_3_) {
         if (!this.item.matches(p_234836_2_)) {
            return false;
         } else {
            return this.entity.matches(p_234836_3_);
         }
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.add("item", this.item.serializeToJson());
         jsonobject.add("entity", this.entity.toJson(p_230240_1_));
         return jsonobject;
      }
   }
}
