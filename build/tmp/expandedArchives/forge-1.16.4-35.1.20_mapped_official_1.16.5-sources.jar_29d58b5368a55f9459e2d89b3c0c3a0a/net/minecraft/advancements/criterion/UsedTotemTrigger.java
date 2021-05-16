package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class UsedTotemTrigger extends AbstractCriterionTrigger<UsedTotemTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("used_totem");

   public ResourceLocation getId() {
      return ID;
   }

   public UsedTotemTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_230241_1_.get("item"));
      return new UsedTotemTrigger.Instance(p_230241_2_, itempredicate);
   }

   public void trigger(ServerPlayerEntity p_193187_1_, ItemStack p_193187_2_) {
      this.trigger(p_193187_1_, (p_227409_1_) -> {
         return p_227409_1_.matches(p_193187_2_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate item;

      public Instance(EntityPredicate.AndPredicate p_i232051_1_, ItemPredicate p_i232051_2_) {
         super(UsedTotemTrigger.ID, p_i232051_1_);
         this.item = p_i232051_2_;
      }

      public static UsedTotemTrigger.Instance usedTotem(IItemProvider p_203941_0_) {
         return new UsedTotemTrigger.Instance(EntityPredicate.AndPredicate.ANY, ItemPredicate.Builder.item().of(p_203941_0_).build());
      }

      public boolean matches(ItemStack p_193218_1_) {
         return this.item.matches(p_193218_1_);
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}
