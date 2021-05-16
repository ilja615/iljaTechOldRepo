package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.play.server.SRecipeBookPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerRecipeBook extends RecipeBook {
   private static final Logger LOGGER = LogManager.getLogger();

   public int addRecipes(Collection<IRecipe<?>> p_197926_1_, ServerPlayerEntity p_197926_2_) {
      List<ResourceLocation> list = Lists.newArrayList();
      int i = 0;

      for(IRecipe<?> irecipe : p_197926_1_) {
         ResourceLocation resourcelocation = irecipe.getId();
         if (!this.known.contains(resourcelocation) && !irecipe.isSpecial()) {
            this.add(resourcelocation);
            this.addHighlight(resourcelocation);
            list.add(resourcelocation);
            CriteriaTriggers.RECIPE_UNLOCKED.trigger(p_197926_2_, irecipe);
            ++i;
         }
      }

      this.sendRecipes(SRecipeBookPacket.State.ADD, p_197926_2_, list);
      return i;
   }

   public int removeRecipes(Collection<IRecipe<?>> p_197925_1_, ServerPlayerEntity p_197925_2_) {
      List<ResourceLocation> list = Lists.newArrayList();
      int i = 0;

      for(IRecipe<?> irecipe : p_197925_1_) {
         ResourceLocation resourcelocation = irecipe.getId();
         if (this.known.contains(resourcelocation)) {
            this.remove(resourcelocation);
            list.add(resourcelocation);
            ++i;
         }
      }

      this.sendRecipes(SRecipeBookPacket.State.REMOVE, p_197925_2_, list);
      return i;
   }

   private void sendRecipes(SRecipeBookPacket.State p_194081_1_, ServerPlayerEntity p_194081_2_, List<ResourceLocation> p_194081_3_) {
      p_194081_2_.connection.send(new SRecipeBookPacket(p_194081_1_, p_194081_3_, Collections.emptyList(), this.getBookSettings()));
   }

   public CompoundNBT toNbt() {
      CompoundNBT compoundnbt = new CompoundNBT();
      this.getBookSettings().write(compoundnbt);
      ListNBT listnbt = new ListNBT();

      for(ResourceLocation resourcelocation : this.known) {
         listnbt.add(StringNBT.valueOf(resourcelocation.toString()));
      }

      compoundnbt.put("recipes", listnbt);
      ListNBT listnbt1 = new ListNBT();

      for(ResourceLocation resourcelocation1 : this.highlight) {
         listnbt1.add(StringNBT.valueOf(resourcelocation1.toString()));
      }

      compoundnbt.put("toBeDisplayed", listnbt1);
      return compoundnbt;
   }

   public void fromNbt(CompoundNBT p_192825_1_, RecipeManager p_192825_2_) {
      this.setBookSettings(RecipeBookStatus.read(p_192825_1_));
      ListNBT listnbt = p_192825_1_.getList("recipes", 8);
      this.loadRecipes(listnbt, this::add, p_192825_2_);
      ListNBT listnbt1 = p_192825_1_.getList("toBeDisplayed", 8);
      this.loadRecipes(listnbt1, this::addHighlight, p_192825_2_);
   }

   private void loadRecipes(ListNBT p_223417_1_, Consumer<IRecipe<?>> p_223417_2_, RecipeManager p_223417_3_) {
      for(int i = 0; i < p_223417_1_.size(); ++i) {
         String s = p_223417_1_.getString(i);

         try {
            ResourceLocation resourcelocation = new ResourceLocation(s);
            Optional<? extends IRecipe<?>> optional = p_223417_3_.byKey(resourcelocation);
            if (!optional.isPresent()) {
               LOGGER.error("Tried to load unrecognized recipe: {} removed now.", (Object)resourcelocation);
            } else {
               p_223417_2_.accept(optional.get());
            }
         } catch (ResourceLocationException resourcelocationexception) {
            LOGGER.error("Tried to load improperly formatted recipe: {} removed now.", (Object)s);
         }
      }

   }

   public void sendInitialRecipeBook(ServerPlayerEntity p_192826_1_) {
      p_192826_1_.connection.send(new SRecipeBookPacket(SRecipeBookPacket.State.INIT, this.known, this.highlight, this.getBookSettings()));
   }
}
