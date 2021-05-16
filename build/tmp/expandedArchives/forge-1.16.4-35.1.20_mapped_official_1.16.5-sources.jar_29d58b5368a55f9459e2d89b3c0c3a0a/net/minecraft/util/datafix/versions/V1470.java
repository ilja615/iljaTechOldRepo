package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V1470 extends NamespacedSchema {
   public V1470(int p_i49593_1_, Schema p_i49593_2_) {
      super(p_i49593_1_, p_i49593_2_);
   }

   protected static void registerMob(Schema p_206563_0_, Map<String, Supplier<TypeTemplate>> p_206563_1_, String p_206563_2_) {
      p_206563_0_.register(p_206563_1_, p_206563_2_, () -> {
         return V0100.equipment(p_206563_0_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_registerEntities_1_);
      registerMob(p_registerEntities_1_, map, "minecraft:turtle");
      registerMob(p_registerEntities_1_, map, "minecraft:cod_mob");
      registerMob(p_registerEntities_1_, map, "minecraft:tropical_fish");
      registerMob(p_registerEntities_1_, map, "minecraft:salmon_mob");
      registerMob(p_registerEntities_1_, map, "minecraft:puffer_fish");
      registerMob(p_registerEntities_1_, map, "minecraft:phantom");
      registerMob(p_registerEntities_1_, map, "minecraft:dolphin");
      registerMob(p_registerEntities_1_, map, "minecraft:drowned");
      p_registerEntities_1_.register(map, "minecraft:trident", (p_206561_1_) -> {
         return DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_));
      });
      return map;
   }
}
