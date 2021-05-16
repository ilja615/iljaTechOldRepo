package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;

public class VillagerTradeTrigger extends AbstractCriterionTrigger<VillagerTradeTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("villager_trade");

   public ResourceLocation getId() {
      return ID;
   }

   public VillagerTradeTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      EntityPredicate.AndPredicate entitypredicate$andpredicate = EntityPredicate.AndPredicate.fromJson(p_230241_1_, "villager", p_230241_3_);
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_230241_1_.get("item"));
      return new VillagerTradeTrigger.Instance(p_230241_2_, entitypredicate$andpredicate, itempredicate);
   }

   public void trigger(ServerPlayerEntity p_215114_1_, AbstractVillagerEntity p_215114_2_, ItemStack p_215114_3_) {
      LootContext lootcontext = EntityPredicate.createContext(p_215114_1_, p_215114_2_);
      this.trigger(p_215114_1_, (p_227267_2_) -> {
         return p_227267_2_.matches(lootcontext, p_215114_3_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate.AndPredicate villager;
      private final ItemPredicate item;

      public Instance(EntityPredicate.AndPredicate p_i232013_1_, EntityPredicate.AndPredicate p_i232013_2_, ItemPredicate p_i232013_3_) {
         super(VillagerTradeTrigger.ID, p_i232013_1_);
         this.villager = p_i232013_2_;
         this.item = p_i232013_3_;
      }

      public static VillagerTradeTrigger.Instance tradedWithVillager() {
         return new VillagerTradeTrigger.Instance(EntityPredicate.AndPredicate.ANY, EntityPredicate.AndPredicate.ANY, ItemPredicate.ANY);
      }

      public boolean matches(LootContext p_236575_1_, ItemStack p_236575_2_) {
         if (!this.villager.matches(p_236575_1_)) {
            return false;
         } else {
            return this.item.matches(p_236575_2_);
         }
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.add("item", this.item.serializeToJson());
         jsonobject.add("villager", this.villager.toJson(p_230240_1_));
         return jsonobject;
      }
   }
}
