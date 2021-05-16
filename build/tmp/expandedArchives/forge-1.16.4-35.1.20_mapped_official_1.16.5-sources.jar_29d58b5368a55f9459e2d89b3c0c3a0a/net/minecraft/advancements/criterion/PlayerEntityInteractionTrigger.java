package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;

public class PlayerEntityInteractionTrigger extends AbstractCriterionTrigger<PlayerEntityInteractionTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("player_interacted_with_entity");

   public ResourceLocation getId() {
      return ID;
   }

   protected PlayerEntityInteractionTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_230241_1_.get("item"));
      EntityPredicate.AndPredicate entitypredicate$andpredicate = EntityPredicate.AndPredicate.fromJson(p_230241_1_, "entity", p_230241_3_);
      return new PlayerEntityInteractionTrigger.Instance(p_230241_2_, itempredicate, entitypredicate$andpredicate);
   }

   public void trigger(ServerPlayerEntity p_241476_1_, ItemStack p_241476_2_, Entity p_241476_3_) {
      LootContext lootcontext = EntityPredicate.createContext(p_241476_1_, p_241476_3_);
      this.trigger(p_241476_1_, (p_241475_2_) -> {
         return p_241475_2_.matches(p_241476_2_, lootcontext);
      });
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate item;
      private final EntityPredicate.AndPredicate entity;

      public Instance(EntityPredicate.AndPredicate p_i241240_1_, ItemPredicate p_i241240_2_, EntityPredicate.AndPredicate p_i241240_3_) {
         super(PlayerEntityInteractionTrigger.ID, p_i241240_1_);
         this.item = p_i241240_2_;
         this.entity = p_i241240_3_;
      }

      public static PlayerEntityInteractionTrigger.Instance itemUsedOnEntity(EntityPredicate.AndPredicate p_241480_0_, ItemPredicate.Builder p_241480_1_, EntityPredicate.AndPredicate p_241480_2_) {
         return new PlayerEntityInteractionTrigger.Instance(p_241480_0_, p_241480_1_.build(), p_241480_2_);
      }

      public boolean matches(ItemStack p_241481_1_, LootContext p_241481_2_) {
         return !this.item.matches(p_241481_1_) ? false : this.entity.matches(p_241481_2_);
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.add("item", this.item.serializeToJson());
         jsonobject.add("entity", this.entity.toJson(p_230240_1_));
         return jsonobject;
      }
   }
}
