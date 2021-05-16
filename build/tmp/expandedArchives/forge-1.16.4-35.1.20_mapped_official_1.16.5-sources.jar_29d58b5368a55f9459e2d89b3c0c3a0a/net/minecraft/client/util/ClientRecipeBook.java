package net.minecraft.client.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientRecipeBook extends RecipeBook {
   private static final Logger LOGGER = LogManager.getLogger();
   private Map<RecipeBookCategories, List<RecipeList>> collectionsByTab = ImmutableMap.of();
   private List<RecipeList> allCollections = ImmutableList.of();

   public void setupCollections(Iterable<IRecipe<?>> p_243196_1_) {
      Map<RecipeBookCategories, List<List<IRecipe<?>>>> map = categorizeAndGroupRecipes(p_243196_1_);
      Map<RecipeBookCategories, List<RecipeList>> map1 = Maps.newHashMap();
      Builder<RecipeList> builder = ImmutableList.builder();
      map.forEach((p_243197_2_, p_243197_3_) -> {
         List list = map1.put(p_243197_2_, p_243197_3_.stream().map(RecipeList::new).peek(builder::add).collect(ImmutableList.toImmutableList()));
      });
      RecipeBookCategories.AGGREGATE_CATEGORIES.forEach((p_243199_1_, p_243199_2_) -> {
         List list = map1.put(p_243199_1_, p_243199_2_.stream().flatMap((p_243198_1_) -> {
            return map1.getOrDefault(p_243198_1_, ImmutableList.of()).stream();
         }).collect(ImmutableList.toImmutableList()));
      });
      this.collectionsByTab = ImmutableMap.copyOf(map1);
      this.allCollections = builder.build();
   }

   private static Map<RecipeBookCategories, List<List<IRecipe<?>>>> categorizeAndGroupRecipes(Iterable<IRecipe<?>> p_243201_0_) {
      Map<RecipeBookCategories, List<List<IRecipe<?>>>> map = Maps.newHashMap();
      Table<RecipeBookCategories, String, List<IRecipe<?>>> table = HashBasedTable.create();

      for(IRecipe<?> irecipe : p_243201_0_) {
         if (!irecipe.isSpecial()) {
            RecipeBookCategories recipebookcategories = getCategory(irecipe);
            String s = irecipe.getGroup();
            if (s.isEmpty()) {
               map.computeIfAbsent(recipebookcategories, (p_243202_0_) -> {
                  return Lists.newArrayList();
               }).add(ImmutableList.of(irecipe));
            } else {
               List<IRecipe<?>> list = table.get(recipebookcategories, s);
               if (list == null) {
                  list = Lists.newArrayList();
                  table.put(recipebookcategories, s, list);
                  map.computeIfAbsent(recipebookcategories, (p_202890_0_) -> {
                     return Lists.newArrayList();
                  }).add(list);
               }

               list.add(irecipe);
            }
         }
      }

      return map;
   }

   private static RecipeBookCategories getCategory(IRecipe<?> p_202887_0_) {
      IRecipeType<?> irecipetype = p_202887_0_.getType();
      if (irecipetype == IRecipeType.CRAFTING) {
         ItemStack itemstack = p_202887_0_.getResultItem();
         ItemGroup itemgroup = itemstack.getItem().getItemCategory();
         if (itemgroup == ItemGroup.TAB_BUILDING_BLOCKS) {
            return RecipeBookCategories.CRAFTING_BUILDING_BLOCKS;
         } else if (itemgroup != ItemGroup.TAB_TOOLS && itemgroup != ItemGroup.TAB_COMBAT) {
            return itemgroup == ItemGroup.TAB_REDSTONE ? RecipeBookCategories.CRAFTING_REDSTONE : RecipeBookCategories.CRAFTING_MISC;
         } else {
            return RecipeBookCategories.CRAFTING_EQUIPMENT;
         }
      } else if (irecipetype == IRecipeType.SMELTING) {
         if (p_202887_0_.getResultItem().getItem().isEdible()) {
            return RecipeBookCategories.FURNACE_FOOD;
         } else {
            return p_202887_0_.getResultItem().getItem() instanceof BlockItem ? RecipeBookCategories.FURNACE_BLOCKS : RecipeBookCategories.FURNACE_MISC;
         }
      } else if (irecipetype == IRecipeType.BLASTING) {
         return p_202887_0_.getResultItem().getItem() instanceof BlockItem ? RecipeBookCategories.BLAST_FURNACE_BLOCKS : RecipeBookCategories.BLAST_FURNACE_MISC;
      } else if (irecipetype == IRecipeType.SMOKING) {
         return RecipeBookCategories.SMOKER_FOOD;
      } else if (irecipetype == IRecipeType.STONECUTTING) {
         return RecipeBookCategories.STONECUTTER;
      } else if (irecipetype == IRecipeType.CAMPFIRE_COOKING) {
         return RecipeBookCategories.CAMPFIRE;
      } else if (irecipetype == IRecipeType.SMITHING) {
         return RecipeBookCategories.SMITHING;
      } else {
         LOGGER.warn("Unknown recipe category: {}/{}", () -> {
            return Registry.RECIPE_TYPE.getKey(p_202887_0_.getType());
         }, p_202887_0_::getId);
         return RecipeBookCategories.UNKNOWN;
      }
   }

   public List<RecipeList> getCollections() {
      return this.allCollections;
   }

   public List<RecipeList> getCollection(RecipeBookCategories p_202891_1_) {
      return this.collectionsByTab.getOrDefault(p_202891_1_, Collections.emptyList());
   }
}
