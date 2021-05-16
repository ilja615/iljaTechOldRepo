package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.Objects;

public class EntityRavagerRenameFix extends TypedEntityRenameHelper {
   public static final Map<String, String> RENAMED_IDS = ImmutableMap.<String, String>builder().put("minecraft:illager_beast_spawn_egg", "minecraft:ravager_spawn_egg").build();

   public EntityRavagerRenameFix(Schema p_i50427_1_, boolean p_i50427_2_) {
      super("EntityRavagerRenameFix", p_i50427_1_, p_i50427_2_);
   }

   protected String rename(String p_211311_1_) {
      return Objects.equals("minecraft:illager_beast", p_211311_1_) ? "minecraft:ravager" : p_211311_1_;
   }
}
