package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class RecipeRenamer1510 extends RecipeRenamer {
   private static final Map<String, String> RECIPES = ImmutableMap.<String, String>builder().put("minecraft:acacia_bark", "minecraft:acacia_wood").put("minecraft:birch_bark", "minecraft:birch_wood").put("minecraft:dark_oak_bark", "minecraft:dark_oak_wood").put("minecraft:jungle_bark", "minecraft:jungle_wood").put("minecraft:oak_bark", "minecraft:oak_wood").put("minecraft:spruce_bark", "minecraft:spruce_wood").build();

   public RecipeRenamer1510(Schema p_i49780_1_, boolean p_i49780_2_) {
      super(p_i49780_1_, p_i49780_2_, "Recipes renamening fix", (p_230077_0_) -> {
         return RECIPES.getOrDefault(p_230077_0_, p_230077_0_);
      });
   }
}
