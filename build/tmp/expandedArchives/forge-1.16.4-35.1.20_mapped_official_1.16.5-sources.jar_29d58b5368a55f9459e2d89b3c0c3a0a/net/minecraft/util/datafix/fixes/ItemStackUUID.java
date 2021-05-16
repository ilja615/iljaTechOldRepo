package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class ItemStackUUID extends AbstractUUIDFix {
   public ItemStackUUID(Schema p_i231456_1_) {
      super(p_i231456_1_, TypeReferences.ITEM_STACK);
   }

   public TypeRewriteRule makeRule() {
      OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      return this.fixTypeEverywhereTyped("ItemStackUUIDFix", this.getInputSchema().getType(this.typeReference), (p_233277_2_) -> {
         OpticFinder<?> opticfinder1 = p_233277_2_.getType().findField("tag");
         return p_233277_2_.updateTyped(opticfinder1, (p_233278_3_) -> {
            return p_233278_3_.update(DSL.remainderFinder(), (p_233279_3_) -> {
               p_233279_3_ = this.updateAttributeModifiers(p_233279_3_);
               if (p_233277_2_.getOptional(opticfinder).map((p_233280_0_) -> {
                  return "minecraft:player_head".equals(p_233280_0_.getSecond());
               }).orElse(false)) {
                  p_233279_3_ = this.updateSkullOwner(p_233279_3_);
               }

               return p_233279_3_;
            });
         });
      });
   }

   private Dynamic<?> updateAttributeModifiers(Dynamic<?> p_233282_1_) {
      return p_233282_1_.update("AttributeModifiers", (p_233281_1_) -> {
         return p_233282_1_.createList(p_233281_1_.asStream().map((p_233285_0_) -> {
            return replaceUUIDLeastMost(p_233285_0_, "UUID", "UUID").orElse(p_233285_0_);
         }));
      });
   }

   private Dynamic<?> updateSkullOwner(Dynamic<?> p_233283_1_) {
      return p_233283_1_.update("SkullOwner", (p_233284_0_) -> {
         return replaceUUIDString(p_233284_0_, "Id", "Id").orElse(p_233284_0_);
      });
   }
}
