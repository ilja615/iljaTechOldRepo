package ilja615.iljatech.blocks.stretcher;

import com.google.gson.JsonObject;
import ilja615.iljatech.IljaTech;
import ilja615.iljatech.init.ModBlocks;
import ilja615.iljatech.init.ModRecipe;
import ilja615.iljatech.util.interactions.BoilingRecipeType;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class StretchingRecipeType implements Recipe<Container>
{
    protected final Ingredient ingredient;
    protected final ItemStack result;
    private final RecipeType<?> type;
    private final RecipeSerializer<?> serializer;
    protected final ResourceLocation id;

    public StretchingRecipeType(RecipeType<?> p_44416_, RecipeSerializer<?> p_44417_, ResourceLocation p_44418_, Ingredient p_44420_, ItemStack p_44421_) {
        this.type = p_44416_;
        this.serializer = p_44417_;
        this.id = p_44418_;
        this.ingredient = p_44420_;
        this.result = p_44421_;
    }

    @Override
    public RecipeType<?> getType() {
        return this.type;
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
        return new ItemStack(ModBlocks.STRETCHER.get());
    }

    public ItemStack assemble(Container p_44427_) {
        return this.result.copy();
    }

    public static class Serializer implements RecipeSerializer<StretchingRecipeType>
    {
        public StretchingRecipeType fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject pSerializedRecipe)
        {
            Ingredient ingredient;
            ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "ingredient"));
            String s1 = GsonHelper.getAsString(pSerializedRecipe, "result");
            int i = GsonHelper.getAsInt(pSerializedRecipe, "count");
            ItemStack itemstack = new ItemStack(Registry.ITEM.get(new ResourceLocation(s1)), i);
            return new StretchingRecipeType(ModRecipe.Types.STRETCHING.get(), ModRecipe.STRETCHING.get(), recipeId, ingredient, itemstack);
        }

        public StretchingRecipeType fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull FriendlyByteBuf buffer)
        {
            String s = buffer.readUtf();
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack itemstack = buffer.readItem();
            return new StretchingRecipeType(ModRecipe.Types.STRETCHING.get(), ModRecipe.STRETCHING.get(), recipeId, ingredient, itemstack);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, StretchingRecipeType recipeType)
        {
            recipeType.ingredient.toNetwork(buffer);
            buffer.writeItem(recipeType.result);
        }
    }

    public static class Type implements RecipeType<StretchingRecipeType>
    {
        @Override
        public String toString() {
            return new ResourceLocation(IljaTech.MOD_ID, "stretching").toString();
        }
    }
}
