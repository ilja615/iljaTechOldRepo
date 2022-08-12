package ilja615.iljatech.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.stream.Stream;

public class CountedIngredient extends AbstractIngredient
{
    private final Item item;
    private final int count;

    public CountedIngredient(Item item, int count)
    {
        super(Stream.of(new ItemValue(new ItemStack(item))));

        this.item = item;
        this.count = count;
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer()
    {
        return CountedIngredient.Serializer.INSTANCE;
    }

    @Override
    public JsonElement toJson()
    {
        JsonObject json = new JsonObject();
        json.addProperty("type", CraftingHelper.getID(CountedIngredient.Serializer.INSTANCE).toString());

        json.addProperty("item", ForgeRegistries.ITEMS.getKey(item).toString());

        json.addProperty("count", String.valueOf(count));
        return json;
    }

    public static class Serializer implements IIngredientSerializer<CountedIngredient>
    {
        public static final CountedIngredient.Serializer INSTANCE = new CountedIngredient.Serializer();

        @Override
        public CountedIngredient parse(JsonObject json)
        {
            // parse items
            Item item;
            if (json.has("item"))
                item = CraftingHelper.getItem(GsonHelper.getAsString(json, "item"), true);
            else
                throw new JsonSyntaxException("Must set either 'item'");

            // parse count
            if (!json.has("count"))
                throw new JsonSyntaxException("Missing count, expected to find an int");
            int count = json.get("count").getAsInt();

            return new CountedIngredient(item, count);
        }

        @Override
        public CountedIngredient parse(FriendlyByteBuf buffer)
        {
            Item item = buffer.readRegistryIdUnsafe(ForgeRegistries.ITEMS);
            int count = buffer.readInt();
            return new CountedIngredient(item, count);
        }

        @Override
        public void write(FriendlyByteBuf buffer, CountedIngredient ingredient)
        {
            buffer.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, ingredient.item);
            buffer.writeInt(ingredient.count);
        }
    }
}
