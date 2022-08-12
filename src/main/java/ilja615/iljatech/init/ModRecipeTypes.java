package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes
{
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, IljaTech.MOD_ID);

    public static final RegistryObject<RecipeType> STRETCHING = RECIPE_TYPES.register("stretching", () -> RecipeType.simple(new ResourceLocation(IljaTech.MOD_ID, "stretching")));
    public static final RegistryObject<RecipeType> CRUSHING = RECIPE_TYPES.register("crushing", () -> RecipeType.simple(new ResourceLocation(IljaTech.MOD_ID, "crushing")));
    public static final RegistryObject<RecipeType> BOILING = RECIPE_TYPES.register("boiling", () -> RecipeType.simple(new ResourceLocation(IljaTech.MOD_ID, "boiling")));
    public static final RegistryObject<RecipeType> FOUNDRY = RECIPE_TYPES.register("foundry", () -> RecipeType.simple(new ResourceLocation(IljaTech.MOD_ID, "foundry")));
}
