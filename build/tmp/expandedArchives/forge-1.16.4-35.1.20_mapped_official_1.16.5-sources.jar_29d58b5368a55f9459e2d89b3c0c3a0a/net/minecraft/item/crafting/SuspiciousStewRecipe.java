package net.minecraft.item.crafting;

import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.potion.Effect;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class SuspiciousStewRecipe extends SpecialRecipe {
   public SuspiciousStewRecipe(ResourceLocation p_i50020_1_) {
      super(p_i50020_1_);
   }

   public boolean matches(CraftingInventory p_77569_1_, World p_77569_2_) {
      boolean flag = false;
      boolean flag1 = false;
      boolean flag2 = false;
      boolean flag3 = false;

      for(int i = 0; i < p_77569_1_.getContainerSize(); ++i) {
         ItemStack itemstack = p_77569_1_.getItem(i);
         if (!itemstack.isEmpty()) {
            if (itemstack.getItem() == Blocks.BROWN_MUSHROOM.asItem() && !flag2) {
               flag2 = true;
            } else if (itemstack.getItem() == Blocks.RED_MUSHROOM.asItem() && !flag1) {
               flag1 = true;
            } else if (itemstack.getItem().is(ItemTags.SMALL_FLOWERS) && !flag) {
               flag = true;
            } else {
               if (itemstack.getItem() != Items.BOWL || flag3) {
                  return false;
               }

               flag3 = true;
            }
         }
      }

      return flag && flag2 && flag1 && flag3;
   }

   public ItemStack assemble(CraftingInventory p_77572_1_) {
      ItemStack itemstack = ItemStack.EMPTY;

      for(int i = 0; i < p_77572_1_.getContainerSize(); ++i) {
         ItemStack itemstack1 = p_77572_1_.getItem(i);
         if (!itemstack1.isEmpty() && itemstack1.getItem().is(ItemTags.SMALL_FLOWERS)) {
            itemstack = itemstack1;
            break;
         }
      }

      ItemStack itemstack2 = new ItemStack(Items.SUSPICIOUS_STEW, 1);
      if (itemstack.getItem() instanceof BlockItem && ((BlockItem)itemstack.getItem()).getBlock() instanceof FlowerBlock) {
         FlowerBlock flowerblock = (FlowerBlock)((BlockItem)itemstack.getItem()).getBlock();
         Effect effect = flowerblock.getSuspiciousStewEffect();
         SuspiciousStewItem.saveMobEffect(itemstack2, effect, flowerblock.getEffectDuration());
      }

      return itemstack2;
   }

   public boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ >= 2 && p_194133_2_ >= 2;
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.SUSPICIOUS_STEW;
   }
}
