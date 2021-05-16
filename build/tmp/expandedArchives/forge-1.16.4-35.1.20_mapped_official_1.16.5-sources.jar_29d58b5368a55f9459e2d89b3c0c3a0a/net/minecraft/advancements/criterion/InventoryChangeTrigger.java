package net.minecraft.advancements.criterion;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.potion.Potion;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class InventoryChangeTrigger extends AbstractCriterionTrigger<InventoryChangeTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("inventory_changed");

   public ResourceLocation getId() {
      return ID;
   }

   public InventoryChangeTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.AndPredicate p_230241_2_, ConditionArrayParser p_230241_3_) {
      JsonObject jsonobject = JSONUtils.getAsJsonObject(p_230241_1_, "slots", new JsonObject());
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(jsonobject.get("occupied"));
      MinMaxBounds.IntBound minmaxbounds$intbound1 = MinMaxBounds.IntBound.fromJson(jsonobject.get("full"));
      MinMaxBounds.IntBound minmaxbounds$intbound2 = MinMaxBounds.IntBound.fromJson(jsonobject.get("empty"));
      ItemPredicate[] aitempredicate = ItemPredicate.fromJsonArray(p_230241_1_.get("items"));
      return new InventoryChangeTrigger.Instance(p_230241_2_, minmaxbounds$intbound, minmaxbounds$intbound1, minmaxbounds$intbound2, aitempredicate);
   }

   public void trigger(ServerPlayerEntity p_234803_1_, PlayerInventory p_234803_2_, ItemStack p_234803_3_) {
      int i = 0;
      int j = 0;
      int k = 0;

      for(int l = 0; l < p_234803_2_.getContainerSize(); ++l) {
         ItemStack itemstack = p_234803_2_.getItem(l);
         if (itemstack.isEmpty()) {
            ++j;
         } else {
            ++k;
            if (itemstack.getCount() >= itemstack.getMaxStackSize()) {
               ++i;
            }
         }
      }

      this.trigger(p_234803_1_, p_234803_2_, p_234803_3_, i, j, k);
   }

   private void trigger(ServerPlayerEntity p_234804_1_, PlayerInventory p_234804_2_, ItemStack p_234804_3_, int p_234804_4_, int p_234804_5_, int p_234804_6_) {
      this.trigger(p_234804_1_, (p_234802_5_) -> {
         return p_234802_5_.matches(p_234804_2_, p_234804_3_, p_234804_4_, p_234804_5_, p_234804_6_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final MinMaxBounds.IntBound slotsOccupied;
      private final MinMaxBounds.IntBound slotsFull;
      private final MinMaxBounds.IntBound slotsEmpty;
      private final ItemPredicate[] predicates;

      public Instance(EntityPredicate.AndPredicate p_i231597_1_, MinMaxBounds.IntBound p_i231597_2_, MinMaxBounds.IntBound p_i231597_3_, MinMaxBounds.IntBound p_i231597_4_, ItemPredicate[] p_i231597_5_) {
         super(InventoryChangeTrigger.ID, p_i231597_1_);
         this.slotsOccupied = p_i231597_2_;
         this.slotsFull = p_i231597_3_;
         this.slotsEmpty = p_i231597_4_;
         this.predicates = p_i231597_5_;
      }

      public static InventoryChangeTrigger.Instance hasItems(ItemPredicate... p_203923_0_) {
         return new InventoryChangeTrigger.Instance(EntityPredicate.AndPredicate.ANY, MinMaxBounds.IntBound.ANY, MinMaxBounds.IntBound.ANY, MinMaxBounds.IntBound.ANY, p_203923_0_);
      }

      public static InventoryChangeTrigger.Instance hasItems(IItemProvider... p_203922_0_) {
         ItemPredicate[] aitempredicate = new ItemPredicate[p_203922_0_.length];

         for(int i = 0; i < p_203922_0_.length; ++i) {
            aitempredicate[i] = new ItemPredicate((ITag<Item>)null, p_203922_0_[i].asItem(), MinMaxBounds.IntBound.ANY, MinMaxBounds.IntBound.ANY, EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, (Potion)null, NBTPredicate.ANY);
         }

         return hasItems(aitempredicate);
      }

      public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
         JsonObject jsonobject = super.serializeToJson(p_230240_1_);
         if (!this.slotsOccupied.isAny() || !this.slotsFull.isAny() || !this.slotsEmpty.isAny()) {
            JsonObject jsonobject1 = new JsonObject();
            jsonobject1.add("occupied", this.slotsOccupied.serializeToJson());
            jsonobject1.add("full", this.slotsFull.serializeToJson());
            jsonobject1.add("empty", this.slotsEmpty.serializeToJson());
            jsonobject.add("slots", jsonobject1);
         }

         if (this.predicates.length > 0) {
            JsonArray jsonarray = new JsonArray();

            for(ItemPredicate itempredicate : this.predicates) {
               jsonarray.add(itempredicate.serializeToJson());
            }

            jsonobject.add("items", jsonarray);
         }

         return jsonobject;
      }

      public boolean matches(PlayerInventory p_234805_1_, ItemStack p_234805_2_, int p_234805_3_, int p_234805_4_, int p_234805_5_) {
         if (!this.slotsFull.matches(p_234805_3_)) {
            return false;
         } else if (!this.slotsEmpty.matches(p_234805_4_)) {
            return false;
         } else if (!this.slotsOccupied.matches(p_234805_5_)) {
            return false;
         } else {
            int i = this.predicates.length;
            if (i == 0) {
               return true;
            } else if (i != 1) {
               List<ItemPredicate> list = new ObjectArrayList<>(this.predicates);
               int j = p_234805_1_.getContainerSize();

               for(int k = 0; k < j; ++k) {
                  if (list.isEmpty()) {
                     return true;
                  }

                  ItemStack itemstack = p_234805_1_.getItem(k);
                  if (!itemstack.isEmpty()) {
                     list.removeIf((p_234806_1_) -> {
                        return p_234806_1_.matches(itemstack);
                     });
                  }
               }

               return list.isEmpty();
            } else {
               return !p_234805_2_.isEmpty() && this.predicates[0].matches(p_234805_2_);
            }
         }
      }
   }
}
