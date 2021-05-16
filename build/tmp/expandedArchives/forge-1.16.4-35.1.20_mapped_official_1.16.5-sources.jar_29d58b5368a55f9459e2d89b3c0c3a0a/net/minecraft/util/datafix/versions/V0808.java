package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V0808 extends NamespacedSchema {
   public V0808(int p_i49581_1_, Schema p_i49581_2_) {
      super(p_i49581_1_, p_i49581_2_);
   }

   protected static void registerInventory(Schema p_206601_0_, Map<String, Supplier<TypeTemplate>> p_206601_1_, String p_206601_2_) {
      p_206601_0_.register(p_206601_1_, p_206601_2_, () -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_206601_0_)));
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_registerBlockEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(p_registerBlockEntities_1_);
      registerInventory(p_registerBlockEntities_1_, map, "minecraft:shulker_box");
      return map;
   }
}
