package ilja615.iljatech.blocks.foundry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import ilja615.iljatech.init.ModRecipeSerializers;
import ilja615.iljatech.init.ModRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.HashSet;

public class FoundryRecipe implements Recipe<Container>
{
    final NonNullList<Ingredient> ingredients;
    protected final ItemStack result;
    private final RecipeSerializer<?> serializer;
    protected final ResourceLocation id;

    public FoundryRecipe(RecipeSerializer<?> p_44417_, ResourceLocation p_44418_,  NonNullList<Ingredient> in, ItemStack r) {
        this.serializer = p_44417_;
        this.id = p_44418_;
        this.ingredients = in;
        this.result = r;
    }

    @Override
    public RecipeType<?> getType() {
            return ModRecipeTypes.FOUNDRY.get();
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
        return this.ingredients;
    }

    @Override
    public boolean canCraftInDimensions(int p_44424_, int p_44425_) {
            return true;
            }

    @Override
    public boolean matches(Container container, Level p_44484_) {
        return true;
    }

    @Override
    public ItemStack getToastSymbol() {
            return this.result;
        }

    public ItemStack assemble(Container p_44427_) {
        return this.result.copy();
    }

    public static class Serializer implements RecipeSerializer<FoundryRecipe>
    {
        public FoundryRecipe fromJson(ResourceLocation recipeId, JsonObject pSerializedRecipe)
        {
            NonNullList<Ingredient> nonnulllist = itemsFromJson(GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients"));
            String s1 = GsonHelper.getAsString(pSerializedRecipe, "result");
            int i = GsonHelper.getAsInt(pSerializedRecipe, "count");
            // TODO : check if it works
            //ItemStack itemstack = new ItemStack(Registry.ITEM.get(new ResourceLocation(s1)), i);
            ItemStack itemstack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(s1)), i);
            return new FoundryRecipe(ModRecipeSerializers.FOUNDRY.get(), recipeId, nonnulllist, itemstack);
        }

        private static NonNullList<Ingredient> itemsFromJson(JsonArray p_44276_) {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();

            for(int i = 0; i < p_44276_.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(p_44276_.get(i));
                if (true || !ingredient.isEmpty()) { // FORGE: Skip checking if an ingredient is empty during shapeless recipe deserialization to prevent complex ingredients from caching tags too early. Can not be done using a config value due to sync issues.
                    nonnulllist.add(ingredient);
                }
            }

            return nonnulllist;
        }

        public FoundryRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            String s = buffer.readUtf();
            int i = buffer.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

            for(int j = 0; j < nonnulllist.size(); ++j) {
                nonnulllist.set(j, Ingredient.fromNetwork(buffer));
            }

            ItemStack itemstack = buffer.readItem();
            return new FoundryRecipe(ModRecipeSerializers.FOUNDRY.get(), recipeId, nonnulllist, itemstack);
        }

        public void toNetwork(FriendlyByteBuf buffer, FoundryRecipe foundryRecipe) {
            buffer.writeVarInt(foundryRecipe.ingredients.size());

            for(Ingredient ingredient : foundryRecipe.ingredients) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(foundryRecipe.result);
        }
    }
}
