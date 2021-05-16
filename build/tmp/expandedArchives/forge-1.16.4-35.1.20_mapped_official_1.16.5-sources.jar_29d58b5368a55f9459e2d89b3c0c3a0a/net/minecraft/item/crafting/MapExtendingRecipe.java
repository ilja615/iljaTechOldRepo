package net.minecraft.item.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

public class MapExtendingRecipe extends ShapedRecipe {
   public MapExtendingRecipe(ResourceLocation p_i48164_1_) {
      super(p_i48164_1_, "", 3, 3, NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.FILLED_MAP), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER)), new ItemStack(Items.MAP));
   }

   public boolean matches(CraftingInventory p_77569_1_, World p_77569_2_) {
      if (!super.matches(p_77569_1_, p_77569_2_)) {
         return false;
      } else {
         ItemStack itemstack = ItemStack.EMPTY;

         for(int i = 0; i < p_77569_1_.getContainerSize() && itemstack.isEmpty(); ++i) {
            ItemStack itemstack1 = p_77569_1_.getItem(i);
            if (itemstack1.getItem() == Items.FILLED_MAP) {
               itemstack = itemstack1;
            }
         }

         if (itemstack.isEmpty()) {
            return false;
         } else {
            MapData mapdata = FilledMapItem.getOrCreateSavedData(itemstack, p_77569_2_);
            if (mapdata == null) {
               return false;
            } else if (this.isExplorationMap(mapdata)) {
               return false;
            } else {
               return mapdata.scale < 4;
            }
         }
      }
   }

   private boolean isExplorationMap(MapData p_190934_1_) {
      if (p_190934_1_.decorations != null) {
         for(MapDecoration mapdecoration : p_190934_1_.decorations.values()) {
            if (mapdecoration.getType() == MapDecoration.Type.MANSION || mapdecoration.getType() == MapDecoration.Type.MONUMENT) {
               return true;
            }
         }
      }

      return false;
   }

   public ItemStack assemble(CraftingInventory p_77572_1_) {
      ItemStack itemstack = ItemStack.EMPTY;

      for(int i = 0; i < p_77572_1_.getContainerSize() && itemstack.isEmpty(); ++i) {
         ItemStack itemstack1 = p_77572_1_.getItem(i);
         if (itemstack1.getItem() == Items.FILLED_MAP) {
            itemstack = itemstack1;
         }
      }

      itemstack = itemstack.copy();
      itemstack.setCount(1);
      itemstack.getOrCreateTag().putInt("map_scale_direction", 1);
      return itemstack;
   }

   public boolean isSpecial() {
      return true;
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.MAP_EXTENDING;
   }
}
