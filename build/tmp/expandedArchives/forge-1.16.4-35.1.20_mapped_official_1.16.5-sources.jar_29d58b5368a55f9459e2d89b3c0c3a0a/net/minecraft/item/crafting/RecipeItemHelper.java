package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

public class RecipeItemHelper {
   public final Int2IntMap contents = new Int2IntOpenHashMap();

   public void accountSimpleStack(ItemStack p_195932_1_) {
      if (!p_195932_1_.isDamaged() && !p_195932_1_.isEnchanted() && !p_195932_1_.hasCustomHoverName()) {
         this.accountStack(p_195932_1_);
      }

   }

   public void accountStack(ItemStack p_194112_1_) {
      this.accountStack(p_194112_1_, 64);
   }

   public void accountStack(ItemStack p_221264_1_, int p_221264_2_) {
      if (!p_221264_1_.isEmpty()) {
         int i = getStackingIndex(p_221264_1_);
         int j = Math.min(p_221264_2_, p_221264_1_.getCount());
         this.put(i, j);
      }

   }

   public static int getStackingIndex(ItemStack p_194113_0_) {
      return Registry.ITEM.getId(p_194113_0_.getItem());
   }

   private boolean has(int p_194120_1_) {
      return this.contents.get(p_194120_1_) > 0;
   }

   private int take(int p_194122_1_, int p_194122_2_) {
      int i = this.contents.get(p_194122_1_);
      if (i >= p_194122_2_) {
         this.contents.put(p_194122_1_, i - p_194122_2_);
         return p_194122_1_;
      } else {
         return 0;
      }
   }

   private void put(int p_194117_1_, int p_194117_2_) {
      this.contents.put(p_194117_1_, this.contents.get(p_194117_1_) + p_194117_2_);
   }

   public boolean canCraft(IRecipe<?> p_194116_1_, @Nullable IntList p_194116_2_) {
      return this.canCraft(p_194116_1_, p_194116_2_, 1);
   }

   public boolean canCraft(IRecipe<?> p_194118_1_, @Nullable IntList p_194118_2_, int p_194118_3_) {
      return (new RecipeItemHelper.RecipePicker(p_194118_1_)).tryPick(p_194118_3_, p_194118_2_);
   }

   public int getBiggestCraftableStack(IRecipe<?> p_194114_1_, @Nullable IntList p_194114_2_) {
      return this.getBiggestCraftableStack(p_194114_1_, Integer.MAX_VALUE, p_194114_2_);
   }

   public int getBiggestCraftableStack(IRecipe<?> p_194121_1_, int p_194121_2_, @Nullable IntList p_194121_3_) {
      return (new RecipeItemHelper.RecipePicker(p_194121_1_)).tryPickAll(p_194121_2_, p_194121_3_);
   }

   public static ItemStack fromStackingIndex(int p_194115_0_) {
      return p_194115_0_ == 0 ? ItemStack.EMPTY : new ItemStack(Item.byId(p_194115_0_));
   }

   public void clear() {
      this.contents.clear();
   }

   class RecipePicker {
      private final IRecipe<?> recipe;
      private final List<Ingredient> ingredients = Lists.newArrayList();
      private final int ingredientCount;
      private final int[] items;
      private final int itemCount;
      private final BitSet data;
      private final IntList path = new IntArrayList();

      public RecipePicker(IRecipe<?> p_i47608_2_) {
         this.recipe = p_i47608_2_;
         this.ingredients.addAll(p_i47608_2_.getIngredients());
         this.ingredients.removeIf(Ingredient::isEmpty);
         this.ingredientCount = this.ingredients.size();
         this.items = this.getUniqueAvailableIngredientItems();
         this.itemCount = this.items.length;
         this.data = new BitSet(this.ingredientCount + this.itemCount + this.ingredientCount + this.ingredientCount * this.itemCount);

         for(int i = 0; i < this.ingredients.size(); ++i) {
            IntList intlist = this.ingredients.get(i).getStackingIds();

            for(int j = 0; j < this.itemCount; ++j) {
               if (intlist.contains(this.items[j])) {
                  this.data.set(this.getIndex(true, j, i));
               }
            }
         }

      }

      public boolean tryPick(int p_194092_1_, @Nullable IntList p_194092_2_) {
         if (p_194092_1_ <= 0) {
            return true;
         } else {
            int i;
            for(i = 0; this.dfs(p_194092_1_); ++i) {
               RecipeItemHelper.this.take(this.items[this.path.getInt(0)], p_194092_1_);
               int j = this.path.size() - 1;
               this.setSatisfied(this.path.getInt(j));

               for(int k = 0; k < j; ++k) {
                  this.toggleResidual((k & 1) == 0, this.path.get(k), this.path.get(k + 1));
               }

               this.path.clear();
               this.data.clear(0, this.ingredientCount + this.itemCount);
            }

            boolean flag = i == this.ingredientCount;
            boolean flag1 = flag && p_194092_2_ != null;
            if (flag1) {
               p_194092_2_.clear();
            }

            this.data.clear(0, this.ingredientCount + this.itemCount + this.ingredientCount);
            int l = 0;
            List<Ingredient> list = this.recipe.getIngredients();

            for(int i1 = 0; i1 < list.size(); ++i1) {
               if (flag1 && list.get(i1).isEmpty()) {
                  p_194092_2_.add(0);
               } else {
                  for(int j1 = 0; j1 < this.itemCount; ++j1) {
                     if (this.hasResidual(false, l, j1)) {
                        this.toggleResidual(true, j1, l);
                        RecipeItemHelper.this.put(this.items[j1], p_194092_1_);
                        if (flag1) {
                           p_194092_2_.add(this.items[j1]);
                        }
                     }
                  }

                  ++l;
               }
            }

            return flag;
         }
      }

      private int[] getUniqueAvailableIngredientItems() {
         IntCollection intcollection = new IntAVLTreeSet();

         for(Ingredient ingredient : this.ingredients) {
            intcollection.addAll(ingredient.getStackingIds());
         }

         IntIterator intiterator = intcollection.iterator();

         while(intiterator.hasNext()) {
            if (!RecipeItemHelper.this.has(intiterator.nextInt())) {
               intiterator.remove();
            }
         }

         return intcollection.toIntArray();
      }

      private boolean dfs(int p_194098_1_) {
         int i = this.itemCount;

         for(int j = 0; j < i; ++j) {
            if (RecipeItemHelper.this.contents.get(this.items[j]) >= p_194098_1_) {
               this.visit(false, j);

               while(!this.path.isEmpty()) {
                  int k = this.path.size();
                  boolean flag = (k & 1) == 1;
                  int l = this.path.getInt(k - 1);
                  if (!flag && !this.isSatisfied(l)) {
                     break;
                  }

                  int i1 = flag ? this.ingredientCount : i;

                  for(int j1 = 0; j1 < i1; ++j1) {
                     if (!this.hasVisited(flag, j1) && this.hasConnection(flag, l, j1) && this.hasResidual(flag, l, j1)) {
                        this.visit(flag, j1);
                        break;
                     }
                  }

                  int k1 = this.path.size();
                  if (k1 == k) {
                     this.path.removeInt(k1 - 1);
                  }
               }

               if (!this.path.isEmpty()) {
                  return true;
               }
            }
         }

         return false;
      }

      private boolean isSatisfied(int p_194091_1_) {
         return this.data.get(this.getSatisfiedIndex(p_194091_1_));
      }

      private void setSatisfied(int p_194096_1_) {
         this.data.set(this.getSatisfiedIndex(p_194096_1_));
      }

      private int getSatisfiedIndex(int p_194094_1_) {
         return this.ingredientCount + this.itemCount + p_194094_1_;
      }

      private boolean hasConnection(boolean p_194093_1_, int p_194093_2_, int p_194093_3_) {
         return this.data.get(this.getIndex(p_194093_1_, p_194093_2_, p_194093_3_));
      }

      private boolean hasResidual(boolean p_194100_1_, int p_194100_2_, int p_194100_3_) {
         return p_194100_1_ != this.data.get(1 + this.getIndex(p_194100_1_, p_194100_2_, p_194100_3_));
      }

      private void toggleResidual(boolean p_194089_1_, int p_194089_2_, int p_194089_3_) {
         this.data.flip(1 + this.getIndex(p_194089_1_, p_194089_2_, p_194089_3_));
      }

      private int getIndex(boolean p_194095_1_, int p_194095_2_, int p_194095_3_) {
         int i = p_194095_1_ ? p_194095_2_ * this.ingredientCount + p_194095_3_ : p_194095_3_ * this.ingredientCount + p_194095_2_;
         return this.ingredientCount + this.itemCount + this.ingredientCount + 2 * i;
      }

      private void visit(boolean p_194088_1_, int p_194088_2_) {
         this.data.set(this.getVisitedIndex(p_194088_1_, p_194088_2_));
         this.path.add(p_194088_2_);
      }

      private boolean hasVisited(boolean p_194101_1_, int p_194101_2_) {
         return this.data.get(this.getVisitedIndex(p_194101_1_, p_194101_2_));
      }

      private int getVisitedIndex(boolean p_194099_1_, int p_194099_2_) {
         return (p_194099_1_ ? 0 : this.ingredientCount) + p_194099_2_;
      }

      public int tryPickAll(int p_194102_1_, @Nullable IntList p_194102_2_) {
         int i = 0;
         int j = Math.min(p_194102_1_, this.getMinIngredientCount()) + 1;

         while(true) {
            int k = (i + j) / 2;
            if (this.tryPick(k, (IntList)null)) {
               if (j - i <= 1) {
                  if (k > 0) {
                     this.tryPick(k, p_194102_2_);
                  }

                  return k;
               }

               i = k;
            } else {
               j = k;
            }
         }
      }

      private int getMinIngredientCount() {
         int i = Integer.MAX_VALUE;

         for(Ingredient ingredient : this.ingredients) {
            int j = 0;

            for(int k : ingredient.getStackingIds()) {
               j = Math.max(j, RecipeItemHelper.this.contents.get(k));
            }

            if (i > 0) {
               i = Math.min(i, j);
            }
         }

         return i;
      }
   }
}
