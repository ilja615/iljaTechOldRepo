package net.minecraft.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.potion.Potion;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class ConsumeItemTrigger extends AbstractCriterionTrigger<ConsumeItemTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("consume_item");

   public ResourceLocation getId() {
      return ID;
   }

   public ConsumeItemTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      return new ConsumeItemTrigger.Instance(p_230241_2_, ItemPredicate.fromJson(p_230241_1_.get("item")));
   }

   public void trigger(ServerPlayerEntity p_193148_1_, ItemStack p_193148_2_) {
      this.trigger(p_193148_1_, (p_226325_1_) -> {
         return p_226325_1_.matches(p_193148_2_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate item;

      public Instance(EntityPredicate.AndPredicate p_i231522_1_, ItemPredicate p_i231522_2_) {
         super(ConsumeItemTrigger.ID, p_i231522_1_);
         this.item = p_i231522_2_;
      }

      public static ConsumeItemTrigger.Instance usedItem() {
         return new ConsumeItemTrigger.Instance(EntityPredicate.AndPredicate.ANY, ItemPredicate.ANY);
      }

      public static ConsumeItemTrigger.Instance usedItem(IItemProvider p_203913_0_) {
         return new ConsumeItemTrigger.Instance(EntityPredicate.AndPredicate.ANY, new ItemPredicate((ITag<Item>)null, p_203913_0_.asItem(), MinMaxBounds.IntBound.ANY, MinMaxBounds.IntBound.ANY, EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, (Potion)null, NBTPredicate.ANY));
      }

      public boolean matches(ItemStack p_193193_1_) {
         return this.item.matches(p_193193_1_);
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}
