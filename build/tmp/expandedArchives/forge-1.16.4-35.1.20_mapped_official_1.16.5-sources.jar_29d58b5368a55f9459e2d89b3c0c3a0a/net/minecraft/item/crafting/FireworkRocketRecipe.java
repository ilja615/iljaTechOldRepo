package net.minecraft.item.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class FireworkRocketRecipe extends SpecialRecipe {
   private static final Ingredient PAPER_INGREDIENT = Ingredient.of(Items.PAPER);
   private static final Ingredient GUNPOWDER_INGREDIENT = Ingredient.of(Items.GUNPOWDER);
   private static final Ingredient STAR_INGREDIENT = Ingredient.of(Items.FIREWORK_STAR);

   public FireworkRocketRecipe(ResourceLocation p_i48168_1_) {
      super(p_i48168_1_);
   }

   public boolean matches(CraftingInventory p_77569_1_, World p_77569_2_) {
      boolean flag = false;
      int i = 0;

      for(int j = 0; j < p_77569_1_.getContainerSize(); ++j) {
         ItemStack itemstack = p_77569_1_.getItem(j);
         if (!itemstack.isEmpty()) {
            if (PAPER_INGREDIENT.test(itemstack)) {
               if (flag) {
                  return false;
               }

               flag = true;
            } else if (GUNPOWDER_INGREDIENT.test(itemstack)) {
               ++i;
               if (i > 3) {
                  return false;
               }
            } else if (!STAR_INGREDIENT.test(itemstack)) {
               return false;
            }
         }
      }

      return flag && i >= 1;
   }

   public ItemStack assemble(CraftingInventory p_77572_1_) {
      ItemStack itemstack = new ItemStack(Items.FIREWORK_ROCKET, 3);
      CompoundNBT compoundnbt = itemstack.getOrCreateTagElement("Fireworks");
      ListNBT listnbt = new ListNBT();
      int i = 0;

      for(int j = 0; j < p_77572_1_.getContainerSize(); ++j) {
         ItemStack itemstack1 = p_77572_1_.getItem(j);
         if (!itemstack1.isEmpty()) {
            if (GUNPOWDER_INGREDIENT.test(itemstack1)) {
               ++i;
            } else if (STAR_INGREDIENT.test(itemstack1)) {
               CompoundNBT compoundnbt1 = itemstack1.getTagElement("Explosion");
               if (compoundnbt1 != null) {
                  listnbt.add(compoundnbt1);
               }
            }
         }
      }

      compoundnbt.putByte("Flight", (byte)i);
      if (!listnbt.isEmpty()) {
         compoundnbt.put("Explosions", listnbt);
      }

      return itemstack;
   }

   public boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ * p_194133_2_ >= 2;
   }

   public ItemStack getResultItem() {
      return new ItemStack(Items.FIREWORK_ROCKET);
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.FIREWORK_ROCKET;
   }
}
