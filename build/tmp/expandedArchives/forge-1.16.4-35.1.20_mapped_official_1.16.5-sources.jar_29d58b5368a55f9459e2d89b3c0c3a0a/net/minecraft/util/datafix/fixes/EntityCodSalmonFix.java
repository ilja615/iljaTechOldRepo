package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class EntityCodSalmonFix extends TypedEntityRenameHelper {
   public static final Map<String, String> RENAMED_IDS = ImmutableMap.<String, String>builder().put("minecraft:salmon_mob", "minecraft:salmon").put("minecraft:cod_mob", "minecraft:cod").build();
   public static final Map<String, String> RENAMED_EGG_IDS = ImmutableMap.<String, String>builder().put("minecraft:salmon_mob_spawn_egg", "minecraft:salmon_spawn_egg").put("minecraft:cod_mob_spawn_egg", "minecraft:cod_spawn_egg").build();

   public EntityCodSalmonFix(Schema p_i49670_1_, boolean p_i49670_2_) {
      super("EntityCodSalmonFix", p_i49670_1_, p_i49670_2_);
   }

   protected String rename(String p_211311_1_) {
      return RENAMED_IDS.getOrDefault(p_211311_1_, p_211311_1_);
   }
}
