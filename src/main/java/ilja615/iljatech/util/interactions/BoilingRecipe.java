package ilja615.iljatech.util.interactions;

import com.google.gson.JsonObject;
import ilja615.iljatech.init.ModRecipeSerializers;
import ilja615.iljatech.init.ModRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class BoilingRecipe implements Recipe<Container>
{
    protected final Ingredient ingredient;
    protected final ItemStack result;
    private final RecipeSerializer<?> serializer;
    protected final ResourceLocation id;

    public BoilingRecipe(RecipeSerializer<?> p_44417_, ResourceLocation p_44418_, Ingredient p_44420_, ItemStack p_44421_) {
            this.serializer = p_44417_;
            this.id = p_44418_;
            this.ingredient = p_44420_;
            this.result = p_44421_;
            }

    @Override
    public RecipeType<?> getType() {
            return ModRecipeTypes.BOILING.get();
            }

    @Override
    public RecipeSerializer<?> getSerializer() {
            return this.serializer;
            }

    @Override
    public ResourceLocation getId() {
            return this.id;
            }

    @Override
    public ItemStack getResultItem() {
            return this.result;
            }

    @Override
    public NonNullList<Ingredient> getIngredients() {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();
            nonnulllist.add(this.ingredient);
            return nonnulllist;
            }

    @Override
    public boolean canCraftInDimensions(int p_44424_, int p_44425_) {
            return true;
            }

    @Override
    public boolean matches(Container p_44483_, Level p_44484_) {
            return this.ingredient.test(p_44483_.getItem(0));
            }

    @Override
    public ItemStack getToastSymbol() {
        return this.result;
    }

    public ItemStack assemble(Container p_44427_) {
        return this.result.copy();
        }

    public static class Serializer implements RecipeSerializer<BoilingRecipe>
    {
        public BoilingRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject pSerializedRecipe)
        {
            Ingredient ingredient;
            ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "ingredient"));
            String s1 = GsonHelper.getAsString(pSerializedRecipe, "result");
            int i = GsonHelper.getAsInt(pSerializedRecipe, "count");
            // TODO : check if it works
            //ItemStack itemstack = new ItemStack(Registry.ITEM.get(new ResourceLocation(s1)), i);
            ItemStack itemstack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(s1)), i);
            return new BoilingRecipe(ModRecipeSerializers.BOILING.get(), recipeId, ingredient, itemstack);
        }

        public BoilingRecipe fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull FriendlyByteBuf buffer)
        {
            String s = buffer.readUtf();
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack itemstack = buffer.readItem();
            return new BoilingRecipe(ModRecipeSerializers.BOILING.get(), recipeId, ingredient, itemstack);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, BoilingRecipe recipeType)
        {
            recipeType.ingredient.toNetwork(buffer);
            buffer.writeItem(recipeType.result);
        }
    }
}
