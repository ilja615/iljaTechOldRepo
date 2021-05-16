package net.minecraft.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class SetAttributes extends LootFunction {
   private final List<SetAttributes.Modifier> modifiers;

   private SetAttributes(ILootCondition[] p_i51228_1_, List<SetAttributes.Modifier> p_i51228_2_) {
      super(p_i51228_1_);
      this.modifiers = ImmutableList.copyOf(p_i51228_2_);
   }

   public LootFunctionType getType() {
      return LootFunctionManager.SET_ATTRIBUTES;
   }

   public ItemStack run(ItemStack p_215859_1_, LootContext p_215859_2_) {
      Random random = p_215859_2_.getRandom();

      for(SetAttributes.Modifier setattributes$modifier : this.modifiers) {
         UUID uuid = setattributes$modifier.id;
         if (uuid == null) {
            uuid = UUID.randomUUID();
         }

         EquipmentSlotType equipmentslottype = Util.getRandom(setattributes$modifier.slots, random);
         p_215859_1_.addAttributeModifier(setattributes$modifier.attribute, new AttributeModifier(uuid, setattributes$modifier.name, (double)setattributes$modifier.amount.getFloat(random), setattributes$modifier.operation), equipmentslottype);
      }

      return p_215859_1_;
   }

   static class Modifier {
      private final String name;
      private final Attribute attribute;
      private final AttributeModifier.Operation operation;
      private final RandomValueRange amount;
      @Nullable
      private final UUID id;
      private final EquipmentSlotType[] slots;

      private Modifier(String p_i232172_1_, Attribute p_i232172_2_, AttributeModifier.Operation p_i232172_3_, RandomValueRange p_i232172_4_, EquipmentSlotType[] p_i232172_5_, @Nullable UUID p_i232172_6_) {
         this.name = p_i232172_1_;
         this.attribute = p_i232172_2_;
         this.operation = p_i232172_3_;
         this.amount = p_i232172_4_;
         this.id = p_i232172_6_;
         this.slots = p_i232172_5_;
      }

      public JsonObject serialize(JsonSerializationContext p_186592_1_) {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("name", this.name);
         jsonobject.addProperty("attribute", Registry.ATTRIBUTE.getKey(this.attribute).toString());
         jsonobject.addProperty("operation", operationToString(this.operation));
         jsonobject.add("amount", p_186592_1_.serialize(this.amount));
         if (this.id != null) {
            jsonobject.addProperty("id", this.id.toString());
         }

         if (this.slots.length == 1) {
            jsonobject.addProperty("slot", this.slots[0].getName());
         } else {
            JsonArray jsonarray = new JsonArray();

            for(EquipmentSlotType equipmentslottype : this.slots) {
               jsonarray.add(new JsonPrimitive(equipmentslottype.getName()));
            }

            jsonobject.add("slot", jsonarray);
         }

         return jsonobject;
      }

      public static SetAttributes.Modifier deserialize(JsonObject p_186586_0_, JsonDeserializationContext p_186586_1_) {
         String s = JSONUtils.getAsString(p_186586_0_, "name");
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_186586_0_, "attribute"));
         Attribute attribute = Registry.ATTRIBUTE.get(resourcelocation);
         if (attribute == null) {
            throw new JsonSyntaxException("Unknown attribute: " + resourcelocation);
         } else {
            AttributeModifier.Operation attributemodifier$operation = operationFromString(JSONUtils.getAsString(p_186586_0_, "operation"));
            RandomValueRange randomvaluerange = JSONUtils.getAsObject(p_186586_0_, "amount", p_186586_1_, RandomValueRange.class);
            UUID uuid = null;
            EquipmentSlotType[] aequipmentslottype;
            if (JSONUtils.isStringValue(p_186586_0_, "slot")) {
               aequipmentslottype = new EquipmentSlotType[]{EquipmentSlotType.byName(JSONUtils.getAsString(p_186586_0_, "slot"))};
            } else {
               if (!JSONUtils.isArrayNode(p_186586_0_, "slot")) {
                  throw new JsonSyntaxException("Invalid or missing attribute modifier slot; must be either string or array of strings.");
               }

               JsonArray jsonarray = JSONUtils.getAsJsonArray(p_186586_0_, "slot");
               aequipmentslottype = new EquipmentSlotType[jsonarray.size()];
               int i = 0;

               for(JsonElement jsonelement : jsonarray) {
                  aequipmentslottype[i++] = EquipmentSlotType.byName(JSONUtils.convertToString(jsonelement, "slot"));
               }

               if (aequipmentslottype.length == 0) {
                  throw new JsonSyntaxException("Invalid attribute modifier slot; must contain at least one entry.");
               }
            }

            if (p_186586_0_.has("id")) {
               String s1 = JSONUtils.getAsString(p_186586_0_, "id");

               try {
                  uuid = UUID.fromString(s1);
               } catch (IllegalArgumentException illegalargumentexception) {
                  throw new JsonSyntaxException("Invalid attribute modifier id '" + s1 + "' (must be UUID format, with dashes)");
               }
            }

            return new SetAttributes.Modifier(s, attribute, attributemodifier$operation, randomvaluerange, aequipmentslottype, uuid);
         }
      }

      private static String operationToString(AttributeModifier.Operation p_216244_0_) {
         switch(p_216244_0_) {
         case ADDITION:
            return "addition";
         case MULTIPLY_BASE:
            return "multiply_base";
         case MULTIPLY_TOTAL:
            return "multiply_total";
         default:
            throw new IllegalArgumentException("Unknown operation " + p_216244_0_);
         }
      }

      private static AttributeModifier.Operation operationFromString(String p_216246_0_) {
         switch(p_216246_0_) {
         case "addition":
            return AttributeModifier.Operation.ADDITION;
         case "multiply_base":
            return AttributeModifier.Operation.MULTIPLY_BASE;
         case "multiply_total":
            return AttributeModifier.Operation.MULTIPLY_TOTAL;
         default:
            throw new JsonSyntaxException("Unknown attribute modifier operation " + p_216246_0_);
         }
      }
   }

   public static class Serializer extends LootFunction.Serializer<SetAttributes> {
      public void serialize(JsonObject p_230424_1_, SetAttributes p_230424_2_, JsonSerializationContext p_230424_3_) {
         super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
         JsonArray jsonarray = new JsonArray();

         for(SetAttributes.Modifier setattributes$modifier : p_230424_2_.modifiers) {
            jsonarray.add(setattributes$modifier.serialize(p_230424_3_));
         }

         p_230424_1_.add("modifiers", jsonarray);
      }

      public SetAttributes deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         JsonArray jsonarray = JSONUtils.getAsJsonArray(p_186530_1_, "modifiers");
         List<SetAttributes.Modifier> list = Lists.newArrayListWithExpectedSize(jsonarray.size());

         for(JsonElement jsonelement : jsonarray) {
            list.add(SetAttributes.Modifier.deserialize(JSONUtils.convertToJsonObject(jsonelement, "modifier"), p_186530_2_));
         }

         if (list.isEmpty()) {
            throw new JsonSyntaxException("Invalid attribute modifiers array; cannot be empty");
         } else {
            return new SetAttributes(p_186530_3_, list);
         }
      }
   }
}
