package ilja615.iljatech.init;

import ilja615.iljatech.IljaTech;
import ilja615.iljatech.blocks.elongating_mill.ElongationRecipeType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRecipeSerializers
{
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, IljaTech.MOD_ID);

    public static final RegistryObject<RecipeSerializer<?>> ELONGATION = RECIPE_SERIALIZERS.register("elongation", ElongationRecipeType.Serializer::new);

    public static final class Types {
        public static final RecipeType<ElongationRecipeType> ELONGATION = registerType("elongation");
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
