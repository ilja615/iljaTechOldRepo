package net.minecraft.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public interface IRecipeSerializer<T extends IRecipe<?>> extends net.minecraftforge.registries.IForgeRegistryEntry<IRecipeSerializer<?>> {
   IRecipeSerializer<ShapedRecipe> SHAPED_RECIPE = register("crafting_shaped", new ShapedRecipe.Serializer());
   IRecipeSerializer<ShapelessRecipe> SHAPELESS_RECIPE = register("crafting_shapeless", new ShapelessRecipe.Serializer());
   SpecialRecipeSerializer<ArmorDyeRecipe> ARMOR_DYE = register("crafting_special_armordye", new SpecialRecipeSerializer<>(ArmorDyeRecipe::new));
   SpecialRecipeSerializer<BookCloningRecipe> BOOK_CLONING = register("crafting_special_bookcloning", new SpecialRecipeSerializer<>(BookCloningRecipe::new));
   SpecialRecipeSerializer<MapCloningRecipe> MAP_CLONING = register("crafting_special_mapcloning", new SpecialRecipeSerializer<>(MapCloningRecipe::new));
   SpecialRecipeSerializer<MapExtendingRecipe> MAP_EXTENDING = register("crafting_special_mapextending", new SpecialRecipeSerializer<>(MapExtendingRecipe::new));
   SpecialRecipeSerializer<FireworkRocketRecipe> FIREWORK_ROCKET = register("crafting_special_firework_rocket", new SpecialRecipeSerializer<>(FireworkRocketRecipe::new));
   SpecialRecipeSerializer<FireworkStarRecipe> FIREWORK_STAR = register("crafting_special_firework_star", new SpecialRecipeSerializer<>(FireworkStarRecipe::new));
   SpecialRecipeSerializer<FireworkStarFadeRecipe> FIREWORK_STAR_FADE = register("crafting_special_firework_star_fade", new SpecialRecipeSerializer<>(FireworkStarFadeRecipe::new));
   SpecialRecipeSerializer<TippedArrowRecipe> TIPPED_ARROW = register("crafting_special_tippedarrow", new SpecialRecipeSerializer<>(TippedArrowRecipe::new));
   SpecialRecipeSerializer<BannerDuplicateRecipe> BANNER_DUPLICATE = register("crafting_special_bannerduplicate", new SpecialRecipeSerializer<>(BannerDuplicateRecipe::new));
   SpecialRecipeSerializer<ShieldRecipes> SHIELD_DECORATION = register("crafting_special_shielddecoration", new SpecialRecipeSerializer<>(ShieldRecipes::new));
   SpecialRecipeSerializer<ShulkerBoxColoringRecipe> SHULKER_BOX_COLORING = register("crafting_special_shulkerboxcoloring", new SpecialRecipeSerializer<>(ShulkerBoxColoringRecipe::new));
   SpecialRecipeSerializer<SuspiciousStewRecipe> SUSPICIOUS_STEW = register("crafting_special_suspiciousstew", new SpecialRecipeSerializer<>(SuspiciousStewRecipe::new));
   SpecialRecipeSerializer<RepairItemRecipe> REPAIR_ITEM = register("crafting_special_repairitem", new SpecialRecipeSerializer<>(RepairItemRecipe::new));
   CookingRecipeSerializer<FurnaceRecipe> SMELTING_RECIPE = register("smelting", new CookingRecipeSerializer<>(FurnaceRecipe::new, 200));
   CookingRecipeSerializer<BlastingRecipe> BLASTING_RECIPE = register("blasting", new CookingRecipeSerializer<>(BlastingRecipe::new, 100));
   CookingRecipeSerializer<SmokingRecipe> SMOKING_RECIPE = register("smoking", new CookingRecipeSerializer<>(SmokingRecipe::new, 100));
   CookingRecipeSerializer<CampfireCookingRecipe> CAMPFIRE_COOKING_RECIPE = register("campfire_cooking", new CookingRecipeSerializer<>(CampfireCookingRecipe::new, 100));
   IRecipeSerializer<StonecuttingRecipe> STONECUTTER = register("stonecutting", new SingleItemRecipe.Serializer<>(StonecuttingRecipe::new));
   IRecipeSerializer<SmithingRecipe> SMITHING = register("smithing", new SmithingRecipe.Serializer());

   T fromJson(ResourceLocation p_199425_1_, JsonObject p_199425_2_);

   @javax.annotation.Nullable
   T fromNetwork(ResourceLocation p_199426_1_, PacketBuffer p_199426_2_);

   void toNetwork(PacketBuffer p_199427_1_, T p_199427_2_);

   static <S extends IRecipeSerializer<T>, T extends IRecipe<?>> S register(String p_222156_0_, S p_222156_1_) {
      return Registry.register(Registry.RECIPE_SERIALIZER, p_222156_0_, p_222156_1_);
   }
}
