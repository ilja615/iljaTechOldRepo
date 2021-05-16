package net.minecraft.item.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class CookingRecipeSerializer<T extends AbstractCookingRecipe> extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
   private final int defaultCookingTime;
   private final CookingRecipeSerializer.IFactory<T> factory;

   public CookingRecipeSerializer(CookingRecipeSerializer.IFactory<T> p_i50025_1_, int p_i50025_2_) {
      this.defaultCookingTime = p_i50025_2_;
      this.factory = p_i50025_1_;
   }

   public T fromJson(ResourceLocation p_199425_1_, JsonObject p_199425_2_) {
      String s = JSONUtils.getAsString(p_199425_2_, "group", "");
      JsonElement jsonelement = (JsonElement)(JSONUtils.isArrayNode(p_199425_2_, "ingredient") ? JSONUtils.getAsJsonArray(p_199425_2_, "ingredient") : JSONUtils.getAsJsonObject(p_199425_2_, "ingredient"));
      Ingredient ingredient = Ingredient.fromJson(jsonelement);
      //Forge: Check if primitive string to keep vanilla or a object which can contain a count field.
      if (!p_199425_2_.has("result")) throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
      ItemStack itemstack;
      if (p_199425_2_.get("result").isJsonObject()) itemstack = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(p_199425_2_, "result"));
      else {
      String s1 = JSONUtils.getAsString(p_199425_2_, "result");
      ResourceLocation resourcelocation = new ResourceLocation(s1);
      itemstack = new ItemStack(Registry.ITEM.getOptional(resourcelocation).orElseThrow(() -> {
         return new IllegalStateException("Item: " + s1 + " does not exist");
      }));
      }
      float f = JSONUtils.getAsFloat(p_199425_2_, "experience", 0.0F);
      int i = JSONUtils.getAsInt(p_199425_2_, "cookingtime", this.defaultCookingTime);
      return this.factory.create(p_199425_1_, s, ingredient, itemstack, f, i);
   }

   public T fromNetwork(ResourceLocation p_199426_1_, PacketBuffer p_199426_2_) {
      String s = p_199426_2_.readUtf(32767);
      Ingredient ingredient = Ingredient.fromNetwork(p_199426_2_);
      ItemStack itemstack = p_199426_2_.readItem();
      float f = p_199426_2_.readFloat();
      int i = p_199426_2_.readVarInt();
      return this.factory.create(p_199426_1_, s, ingredient, itemstack, f, i);
   }

   public void toNetwork(PacketBuffer p_199427_1_, T p_199427_2_) {
      p_199427_1_.writeUtf(p_199427_2_.group);
      p_199427_2_.ingredient.toNetwork(p_199427_1_);
      p_199427_1_.writeItem(p_199427_2_.result);
      p_199427_1_.writeFloat(p_199427_2_.experience);
      p_199427_1_.writeVarInt(p_199427_2_.cookingTime);
   }

   interface IFactory<T extends AbstractCookingRecipe> {
      T create(ResourceLocation p_create_1_, String p_create_2_, Ingredient p_create_3_, ItemStack p_create_4_, float p_create_5_, int p_create_6_);
   }
}
