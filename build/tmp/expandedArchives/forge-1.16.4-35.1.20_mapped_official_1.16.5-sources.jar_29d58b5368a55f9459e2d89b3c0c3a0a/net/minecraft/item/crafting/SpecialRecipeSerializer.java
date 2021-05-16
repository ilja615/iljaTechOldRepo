package net.minecraft.item.crafting;

import com.google.gson.JsonObject;
import java.util.function.Function;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class SpecialRecipeSerializer<T extends IRecipe<?>> extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>>  implements IRecipeSerializer<T> {
   private final Function<ResourceLocation, T> constructor;

   public SpecialRecipeSerializer(Function<ResourceLocation, T> p_i50024_1_) {
      this.constructor = p_i50024_1_;
   }

   public T fromJson(ResourceLocation p_199425_1_, JsonObject p_199425_2_) {
      return this.constructor.apply(p_199425_1_);
   }

   public T fromNetwork(ResourceLocation p_199426_1_, PacketBuffer p_199426_2_) {
      return this.constructor.apply(p_199426_1_);
   }

   public void toNetwork(PacketBuffer p_199427_1_, T p_199427_2_) {
   }
}
