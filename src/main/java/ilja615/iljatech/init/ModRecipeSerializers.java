package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.blocks.crusher.CrushingRecipeType;
import ilja615.iljatech.blocks.stretcher.StretchingRecipeType;
import ilja615.iljatech.util.interactions.BoilingRecipeType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers
{
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, IljaTech.MOD_ID);

    public static final RegistryObject<RecipeSerializer<?>> STRETCHING = RECIPE_SERIALIZERS.register("stretching", StretchingRecipeType.Serializer::new);
    public static final RegistryObject<RecipeSerializer<?>> CRUSHING = RECIPE_SERIALIZERS.register("crushing", CrushingRecipeType.Serializer::new);
    public static final RegistryObject<RecipeSerializer<?>> BOILING = RECIPE_SERIALIZERS.register("boiling", BoilingRecipeType.Serializer::new);

    public static final class Types {
        public static final RecipeType<StretchingRecipeType> STRETCHING = registerType("stretching");
        public static final RecipeType<CrushingRecipeType> CRUSHING = registerType("crushing");
        public static final RecipeType<BoilingRecipeType> BOILING = registerType("boiling");
    }

    private static <T extends Recipe<?>> RecipeType<T> registerType(String name) {
        return Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(IljaTech.MOD_ID, name), new RecipeType<T>() {
            @Override
            public String toString() {
                return new ResourceLocation(IljaTech.MOD_ID, name).toString();
            }
        });
    }
}
