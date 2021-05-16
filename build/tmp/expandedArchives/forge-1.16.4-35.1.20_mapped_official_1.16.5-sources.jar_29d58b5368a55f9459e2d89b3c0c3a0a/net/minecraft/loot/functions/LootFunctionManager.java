package net.minecraft.loot.functions;

import java.util.function.BiFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootTypesManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class LootFunctionManager {
   public static final BiFunction<ItemStack, LootContext, ItemStack> IDENTITY = (p_216240_0_, p_216240_1_) -> {
      return p_216240_0_;
   };
   public static final LootFunctionType SET_COUNT = register("set_count", new SetCount.Serializer());
   public static final LootFunctionType ENCHANT_WITH_LEVELS = register("enchant_with_levels", new EnchantWithLevels.Serializer());
   public static final LootFunctionType ENCHANT_RANDOMLY = register("enchant_randomly", new EnchantRandomly.Serializer());
   public static final LootFunctionType SET_NBT = register("set_nbt", new SetNBT.Serializer());
   public static final LootFunctionType FURNACE_SMELT = register("furnace_smelt", new Smelt.Serializer());
   public static final LootFunctionType LOOTING_ENCHANT = register("looting_enchant", new LootingEnchantBonus.Serializer());
   public static final LootFunctionType SET_DAMAGE = register("set_damage", new SetDamage.Serializer());
   public static final LootFunctionType SET_ATTRIBUTES = register("set_attributes", new SetAttributes.Serializer());
   public static final LootFunctionType SET_NAME = register("set_name", new SetName.Serializer());
   public static final LootFunctionType EXPLORATION_MAP = register("exploration_map", new ExplorationMap.Serializer());
   public static final LootFunctionType SET_STEW_EFFECT = register("set_stew_effect", new SetStewEffect.Serializer());
   public static final LootFunctionType COPY_NAME = register("copy_name", new CopyName.Serializer());
   public static final LootFunctionType SET_CONTENTS = register("set_contents", new SetContents.Serializer());
   public static final LootFunctionType LIMIT_COUNT = register("limit_count", new LimitCount.Serializer());
   public static final LootFunctionType APPLY_BONUS = register("apply_bonus", new ApplyBonus.Serializer());
   public static final LootFunctionType SET_LOOT_TABLE = register("set_loot_table", new SetLootTable.Serializer());
   public static final LootFunctionType EXPLOSION_DECAY = register("explosion_decay", new ExplosionDecay.Serializer());
   public static final LootFunctionType SET_LORE = register("set_lore", new SetLore.Serializer());
   public static final LootFunctionType FILL_PLAYER_HEAD = register("fill_player_head", new FillPlayerHead.Serializer());
   public static final LootFunctionType COPY_NBT = register("copy_nbt", new CopyNbt.Serializer());
   public static final LootFunctionType COPY_STATE = register("copy_state", new CopyBlockState.Serializer());

   private static LootFunctionType register(String p_237451_0_, ILootSerializer<? extends ILootFunction> p_237451_1_) {
      return Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(p_237451_0_), new LootFunctionType(p_237451_1_));
   }

   public static Object createGsonAdapter() {
      return LootTypesManager.builder(Registry.LOOT_FUNCTION_TYPE, "function", "function", ILootFunction::getType).build();
   }

   public static BiFunction<ItemStack, LootContext, ItemStack> compose(BiFunction<ItemStack, LootContext, ItemStack>[] p_216241_0_) {
      switch(p_216241_0_.length) {
      case 0:
         return IDENTITY;
      case 1:
         return p_216241_0_[0];
      case 2:
         BiFunction<ItemStack, LootContext, ItemStack> bifunction = p_216241_0_[0];
         BiFunction<ItemStack, LootContext, ItemStack> bifunction1 = p_216241_0_[1];
         return (p_216239_2_, p_216239_3_) -> {
            return bifunction1.apply(bifunction.apply(p_216239_2_, p_216239_3_), p_216239_3_);
         };
      default:
         return (p_216238_1_, p_216238_2_) -> {
            for(BiFunction<ItemStack, LootContext, ItemStack> bifunction2 : p_216241_0_) {
               p_216238_1_ = bifunction2.apply(p_216238_1_, p_216238_2_);
            }

            return p_216238_1_;
         };
      }
   }
}
