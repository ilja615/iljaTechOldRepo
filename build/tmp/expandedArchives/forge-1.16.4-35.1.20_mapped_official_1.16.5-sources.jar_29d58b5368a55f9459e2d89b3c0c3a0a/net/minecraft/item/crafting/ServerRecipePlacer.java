package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPlaceGhostRecipePacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerRecipePlacer<C extends IInventory> implements IRecipePlacer<Integer> {
   protected static final Logger LOGGER = LogManager.getLogger();
   protected final RecipeItemHelper stackedContents = new RecipeItemHelper();
   protected PlayerInventory inventory;
   protected RecipeBookContainer<C> menu;

   public ServerRecipePlacer(RecipeBookContainer<C> p_i50752_1_) {
      this.menu = p_i50752_1_;
   }

   public void recipeClicked(ServerPlayerEntity p_194327_1_, @Nullable IRecipe<C> p_194327_2_, boolean p_194327_3_) {
      if (p_194327_2_ != null && p_194327_1_.getRecipeBook().contains(p_194327_2_)) {
         this.inventory = p_194327_1_.inventory;
         if (this.testClearGrid() || p_194327_1_.isCreative()) {
            this.stackedContents.clear();
            p_194327_1_.inventory.fillStackedContents(this.stackedContents);
            this.menu.fillCraftSlotsStackedContents(this.stackedContents);
            if (this.stackedContents.canCraft(p_194327_2_, (IntList)null)) {
               this.handleRecipeClicked(p_194327_2_, p_194327_3_);
            } else {
               this.clearGrid();
               p_194327_1_.connection.send(new SPlaceGhostRecipePacket(p_194327_1_.containerMenu.containerId, p_194327_2_));
            }

            p_194327_1_.inventory.setChanged();
         }
      }
   }

   protected void clearGrid() {
      for(int i = 0; i < this.menu.getGridWidth() * this.menu.getGridHeight() + 1; ++i) {
         if (i != this.menu.getResultSlotIndex() || !(this.menu instanceof WorkbenchContainer) && !(this.menu instanceof PlayerContainer)) {
            this.moveItemToInventory(i);
         }
      }

      this.menu.clearCraftingContent();
   }

   protected void moveItemToInventory(int p_201510_1_) {
      ItemStack itemstack = this.menu.getSlot(p_201510_1_).getItem();
      if (!itemstack.isEmpty()) {
         for(; itemstack.getCount() > 0; this.menu.getSlot(p_201510_1_).remove(1)) {
            int i = this.inventory.getSlotWithRemainingSpace(itemstack);
            if (i == -1) {
               i = this.inventory.getFreeSlot();
            }

            ItemStack itemstack1 = itemstack.copy();
            itemstack1.setCount(1);
            if (!this.inventory.add(i, itemstack1)) {
               LOGGER.error("Can't find any space for item in the inventory");
            }
         }

      }
   }

   protected void handleRecipeClicked(IRecipe<C> p_201508_1_, boolean p_201508_2_) {
      boolean flag = this.menu.recipeMatches(p_201508_1_);
      int i = this.stackedContents.getBiggestCraftableStack(p_201508_1_, (IntList)null);
      if (flag) {
         for(int j = 0; j < this.menu.getGridHeight() * this.menu.getGridWidth() + 1; ++j) {
            if (j != this.menu.getResultSlotIndex()) {
               ItemStack itemstack = this.menu.getSlot(j).getItem();
               if (!itemstack.isEmpty() && Math.min(i, itemstack.getMaxStackSize()) < itemstack.getCount() + 1) {
                  return;
               }
            }
         }
      }

      int j1 = this.getStackSize(p_201508_2_, i, flag);
      IntList intlist = new IntArrayList();
      if (this.stackedContents.canCraft(p_201508_1_, intlist, j1)) {
         int k = j1;

         for(int l : intlist) {
            int i1 = RecipeItemHelper.fromStackingIndex(l).getMaxStackSize();
            if (i1 < k) {
               k = i1;
            }
         }

         if (this.stackedContents.canCraft(p_201508_1_, intlist, k)) {
            this.clearGrid();
            this.placeRecipe(this.menu.getGridWidth(), this.menu.getGridHeight(), this.menu.getResultSlotIndex(), p_201508_1_, intlist.iterator(), k);
         }
      }

   }

   public void addItemToSlot(Iterator<Integer> p_201500_1_, int p_201500_2_, int p_201500_3_, int p_201500_4_, int p_201500_5_) {
      Slot slot = this.menu.getSlot(p_201500_2_);
      ItemStack itemstack = RecipeItemHelper.fromStackingIndex(p_201500_1_.next());
      if (!itemstack.isEmpty()) {
         for(int i = 0; i < p_201500_3_; ++i) {
            this.moveItemToGrid(slot, itemstack);
         }
      }

   }

   protected int getStackSize(boolean p_201509_1_, int p_201509_2_, boolean p_201509_3_) {
      int i = 1;
      if (p_201509_1_) {
         i = p_201509_2_;
      } else if (p_201509_3_) {
         i = 64;

         for(int j = 0; j < this.menu.getGridWidth() * this.menu.getGridHeight() + 1; ++j) {
            if (j != this.menu.getResultSlotIndex()) {
               ItemStack itemstack = this.menu.getSlot(j).getItem();
               if (!itemstack.isEmpty() && i > itemstack.getCount()) {
                  i = itemstack.getCount();
               }
            }
         }

         if (i < 64) {
            ++i;
         }
      }

      return i;
   }

   protected void moveItemToGrid(Slot p_194325_1_, ItemStack p_194325_2_) {
      int i = this.inventory.findSlotMatchingUnusedItem(p_194325_2_);
      if (i != -1) {
         ItemStack itemstack = this.inventory.getItem(i).copy();
         if (!itemstack.isEmpty()) {
            if (itemstack.getCount() > 1) {
               this.inventory.removeItem(i, 1);
            } else {
               this.inventory.removeItemNoUpdate(i);
            }

            itemstack.setCount(1);
            if (p_194325_1_.getItem().isEmpty()) {
               p_194325_1_.set(itemstack);
            } else {
               p_194325_1_.getItem().grow(1);
            }

         }
      }
   }

   private boolean testClearGrid() {
      List<ItemStack> list = Lists.newArrayList();
      int i = this.getAmountOfFreeSlotsInInventory();

      for(int j = 0; j < this.menu.getGridWidth() * this.menu.getGridHeight() + 1; ++j) {
         if (j != this.menu.getResultSlotIndex()) {
            ItemStack itemstack = this.menu.getSlot(j).getItem().copy();
            if (!itemstack.isEmpty()) {
               int k = this.inventory.getSlotWithRemainingSpace(itemstack);
               if (k == -1 && list.size() <= i) {
                  for(ItemStack itemstack1 : list) {
                     if (itemstack1.sameItem(itemstack) && itemstack1.getCount() != itemstack1.getMaxStackSize() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) {
                        itemstack1.grow(itemstack.getCount());
                        itemstack.setCount(0);
                        break;
                     }
                  }

                  if (!itemstack.isEmpty()) {
                     if (list.size() >= i) {
                        return false;
                     }

                     list.add(itemstack);
                  }
               } else if (k == -1) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private int getAmountOfFreeSlotsInInventory() {
      int i = 0;

      for(ItemStack itemstack : this.inventory.items) {
         if (itemstack.isEmpty()) {
            ++i;
         }
      }

      return i;
   }
}
