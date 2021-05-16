package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SUpdateRecipesPacket implements IPacket<IClientPlayNetHandler> {
   private List<IRecipe<?>> recipes;

   public SUpdateRecipesPacket() {
   }

   public SUpdateRecipesPacket(Collection<IRecipe<?>> p_i48176_1_) {
      this.recipes = Lists.newArrayList(p_i48176_1_);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleUpdateRecipes(this);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.recipes = Lists.newArrayList();
      int i = p_148837_1_.readVarInt();

      for(int j = 0; j < i; ++j) {
         this.recipes.add(fromNetwork(p_148837_1_));
      }

   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.recipes.size());

      for(IRecipe<?> irecipe : this.recipes) {
         toNetwork(irecipe, p_148840_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public List<IRecipe<?>> getRecipes() {
      return this.recipes;
   }

   public static IRecipe<?> fromNetwork(PacketBuffer p_218772_0_) {
      ResourceLocation resourcelocation = p_218772_0_.readResourceLocation();
      ResourceLocation resourcelocation1 = p_218772_0_.readResourceLocation();
      return Registry.RECIPE_SERIALIZER.getOptional(resourcelocation).orElseThrow(() -> {
         return new IllegalArgumentException("Unknown recipe serializer " + resourcelocation);
      }).fromNetwork(resourcelocation1, p_218772_0_);
   }

   public static <T extends IRecipe<?>> void toNetwork(T p_218771_0_, PacketBuffer p_218771_1_) {
      p_218771_1_.writeResourceLocation(Registry.RECIPE_SERIALIZER.getKey(p_218771_0_.getSerializer()));
      p_218771_1_.writeResourceLocation(p_218771_0_.getId());
      ((net.minecraft.item.crafting.IRecipeSerializer<T>)p_218771_0_.getSerializer()).toNetwork(p_218771_1_, p_218771_0_);
   }
}
