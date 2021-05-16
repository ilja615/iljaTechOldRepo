package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V0702 extends Schema {
   public V0702(int p_i49585_1_, Schema p_i49585_2_) {
      super(p_i49585_1_, p_i49585_2_);
   }

   protected static void registerMob(Schema p_206636_0_, Map<String, Supplier<TypeTemplate>> p_206636_1_, String p_206636_2_) {
      p_206636_0_.register(p_206636_1_, p_206636_2_, () -> {
         return V0100.equipment(p_206636_0_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(p_registerEntities_1_);
      registerMob(p_registerEntities_1_, map, "ZombieVillager");
      registerMob(p_registerEntities_1_, map, "Husk");
      return map;
   }
}
