package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.util.JSONUtils;
import net.minecraft.world.raid.Raid;

public class EntityEquipmentPredicate {
   public static final EntityEquipmentPredicate ANY = new EntityEquipmentPredicate(ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
   public static final EntityEquipmentPredicate CAPTAIN = new EntityEquipmentPredicate(ItemPredicate.Builder.item().of(Items.WHITE_BANNER).hasNbt(Raid.getLeaderBannerInstance().getTag()).build(), ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
   private final ItemPredicate head;
   private final ItemPredicate chest;
   private final ItemPredicate legs;
   private final ItemPredicate feet;
   private final ItemPredicate mainhand;
   private final ItemPredicate offhand;

   public EntityEquipmentPredicate(ItemPredicate p_i50809_1_, ItemPredicate p_i50809_2_, ItemPredicate p_i50809_3_, ItemPredicate p_i50809_4_, ItemPredicate p_i50809_5_, ItemPredicate p_i50809_6_) {
      this.head = p_i50809_1_;
      this.chest = p_i50809_2_;
      this.legs = p_i50809_3_;
      this.feet = p_i50809_4_;
      this.mainhand = p_i50809_5_;
      this.offhand = p_i50809_6_;
   }

   public boolean matches(@Nullable Entity p_217955_1_) {
      if (this == ANY) {
         return true;
      } else if (!(p_217955_1_ instanceof LivingEntity)) {
         return false;
      } else {
         LivingEntity livingentity = (LivingEntity)p_217955_1_;
         if (!this.head.matches(livingentity.getItemBySlot(EquipmentSlotType.HEAD))) {
            return false;
         } else if (!this.chest.matches(livingentity.getItemBySlot(EquipmentSlotType.CHEST))) {
            return false;
         } else if (!this.legs.matches(livingentity.getItemBySlot(EquipmentSlotType.LEGS))) {
            return false;
         } else if (!this.feet.matches(livingentity.getItemBySlot(EquipmentSlotType.FEET))) {
            return false;
         } else if (!this.mainhand.matches(livingentity.getItemBySlot(EquipmentSlotType.MAINHAND))) {
            return false;
         } else {
            return this.offhand.matches(livingentity.getItemBySlot(EquipmentSlotType.OFFHAND));
         }
      }
   }

   public static EntityEquipmentPredicate fromJson(@Nullable JsonElement p_217956_0_) {
      if (p_217956_0_ != null && !p_217956_0_.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_217956_0_, "equipment");
         ItemPredicate itempredicate = ItemPredicate.fromJson(jsonobject.get("head"));
         ItemPredicate itempredicate1 = ItemPredicate.fromJson(jsonobject.get("chest"));
         ItemPredicate itempredicate2 = ItemPredicate.fromJson(jsonobject.get("legs"));
         ItemPredicate itempredicate3 = ItemPredicate.fromJson(jsonobject.get("feet"));
         ItemPredicate itempredicate4 = ItemPredicate.fromJson(jsonobject.get("mainhand"));
         ItemPredicate itempredicate5 = ItemPredicate.fromJson(jsonobject.get("offhand"));
         return new EntityEquipmentPredicate(itempredicate, itempredicate1, itempredicate2, itempredicate3, itempredicate4, itempredicate5);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("head", this.head.serializeToJson());
         jsonobject.add("chest", this.chest.serializeToJson());
         jsonobject.add("legs", this.legs.serializeToJson());
         jsonobject.add("feet", this.feet.serializeToJson());
         jsonobject.add("mainhand", this.mainhand.serializeToJson());
         jsonobject.add("offhand", this.offhand.serializeToJson());
         return jsonobject;
      }
   }

   public static class Builder {
      private ItemPredicate head = ItemPredicate.ANY;
      private ItemPredicate chest = ItemPredicate.ANY;
      private ItemPredicate legs = ItemPredicate.ANY;
      private ItemPredicate feet = ItemPredicate.ANY;
      private ItemPredicate mainhand = ItemPredicate.ANY;
      private ItemPredicate offhand = ItemPredicate.ANY;

      public static EntityEquipmentPredicate.Builder equipment() {
         return new EntityEquipmentPredicate.Builder();
      }

      public EntityEquipmentPredicate.Builder head(ItemPredicate p_234267_1_) {
         this.head = p_234267_1_;
         return this;
      }

      public EntityEquipmentPredicate.Builder chest(ItemPredicate p_234269_1_) {
         this.chest = p_234269_1_;
         return this;
      }

      public EntityEquipmentPredicate.Builder legs(ItemPredicate p_234270_1_) {
         this.legs = p_234270_1_;
         return this;
      }

      public EntityEquipmentPredicate.Builder feet(ItemPredicate p_234271_1_) {
         this.feet = p_234271_1_;
         return this;
      }

      public EntityEquipmentPredicate build() {
         return new EntityEquipmentPredicate(this.head, this.chest, this.legs, this.feet, this.mainhand, this.offhand);
      }
   }
}
