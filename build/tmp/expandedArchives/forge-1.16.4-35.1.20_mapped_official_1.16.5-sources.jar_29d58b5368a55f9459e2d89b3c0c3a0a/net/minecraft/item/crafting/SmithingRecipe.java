package net.minecraft.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class SmithingRecipe implements IRecipe<IInventory> {
   private final Ingredient base;
   private final Ingredient addition;
   private final ItemStack result;
   private final ResourceLocation id;

   public SmithingRecipe(ResourceLocation p_i231600_1_, Ingredient p_i231600_2_, Ingredient p_i231600_3_, ItemStack p_i231600_4_) {
      this.id = p_i231600_1_;
      this.base = p_i231600_2_;
      this.addition = p_i231600_3_;
      this.result = p_i231600_4_;
   }

   public boolean matches(IInventory p_77569_1_, World p_77569_2_) {
      return this.base.test(p_77569_1_.getItem(0)) && this.addition.test(p_77569_1_.getItem(1));
   }

   public ItemStack assemble(IInventory p_77572_1_) {
      ItemStack itemstack = this.result.copy();
      CompoundNBT compoundnbt = p_77572_1_.getItem(0).getTag();
      if (compoundnbt != null) {
         itemstack.setTag(compoundnbt.copy());
      }

      return itemstack;
   }

   public boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ * p_194133_2_ >= 2;
   }

   public ItemStack getResultItem() {
      return this.result;
   }

   public boolean isAdditionIngredient(ItemStack p_241456_1_) {
      return this.addition.test(p_241456_1_);
   }

   public ItemStack getToastSymbol() {
      return new ItemStack(Blocks.SMITHING_TABLE);
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.SMITHING;
   }

   public IRecipeType<?> getType() {
      return IRecipeType.SMITHING;
   }

   public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SmithingRecipe> {
      public SmithingRecipe fromJson(ResourceLocation p_199425_1_, JsonObject p_199425_2_) {
         Ingredient ingredient = Ingredient.fromJson(JSONUtils.getAsJsonObject(p_199425_2_, "base"));
         Ingredient ingredient1 = Ingredient.fromJson(JSONUtils.getAsJsonObject(p_199425_2_, "addition"));
         ItemStack itemstack = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(p_199425_2_, "result"));
         return new SmithingRecipe(p_199425_1_, ingredient, ingredient1, itemstack);
      }

      public SmithingRecipe fromNetwork(ResourceLocation p_199426_1_, PacketBuffer p_199426_2_) {
         Ingredient ingredient = Ingredient.fromNetwork(p_199426_2_);
         Ingredient ingredient1 = Ingredient.fromNetwork(p_199426_2_);
         ItemStack itemstack = p_199426_2_.readItem();
         return new SmithingRecipe(p_199426_1_, ingredient, ingredient1, itemstack);
      }

      public void toNetwork(PacketBuffer p_199427_1_, SmithingRecipe p_199427_2_) {
         p_199427_2_.base.toNetwork(p_199427_1_);
         p_199427_2_.addition.toNetwork(p_199427_1_);
         p_199427_1_.writeItem(p_199427_2_.result);
      }
   }
}
