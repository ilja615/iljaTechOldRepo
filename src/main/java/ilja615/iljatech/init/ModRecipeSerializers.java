package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.blocks.crusher.CrushingRecipe;
import ilja615.iljatech.blocks.foundry.FoundryRecipe;
import ilja615.iljatech.blocks.stretcher.StretchingRecipe;
import ilja615.iljatech.util.interactions.BoilingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers
{
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, IljaTech.MOD_ID);

    public static final RegistryObject<RecipeSerializer<?>> STRETCHING = RECIPE_SERIALIZERS.register("stretching", StretchingRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<?>> CRUSHING = RECIPE_SERIALIZERS.register("crushing", CrushingRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<?>> BOILING = RECIPE_SERIALIZERS.register("boiling", BoilingRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<?>> FOUNDRY = RECIPE_SERIALIZERS.register("foundry", FoundryRecipe.Serializer::new);
}
