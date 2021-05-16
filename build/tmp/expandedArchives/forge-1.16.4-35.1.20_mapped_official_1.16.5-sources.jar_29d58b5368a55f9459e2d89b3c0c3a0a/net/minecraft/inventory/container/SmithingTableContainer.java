package net.minecraft.inventory.container;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SmithingRecipe;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;

public class SmithingTableContainer extends AbstractRepairContainer {
   private final World level;
   @Nullable
   private SmithingRecipe selectedRecipe;
   private final List<SmithingRecipe> recipes;

   public SmithingTableContainer(int p_i231590_1_, PlayerInventory p_i231590_2_) {
      this(p_i231590_1_, p_i231590_2_, IWorldPosCallable.NULL);
   }

   public SmithingTableContainer(int p_i231591_1_, PlayerInventory p_i231591_2_, IWorldPosCallable p_i231591_3_) {
      super(ContainerType.SMITHING, p_i231591_1_, p_i231591_2_, p_i231591_3_);
      this.level = p_i231591_2_.player.level;
      this.recipes = this.level.getRecipeManager().getAllRecipesFor(IRecipeType.SMITHING);
   }

   protected boolean isValidBlock(BlockState p_230302_1_) {
      return p_230302_1_.is(Blocks.SMITHING_TABLE);
   }

   protected boolean mayPickup(PlayerEntity p_230303_1_, boolean p_230303_2_) {
      return this.selectedRecipe != null && this.selectedRecipe.matches(this.inputSlots, this.level);
   }

   protected ItemStack onTake(PlayerEntity p_230301_1_, ItemStack p_230301_2_) {
      p_230301_2_.onCraftedBy(p_230301_1_.level, p_230301_1_, p_230301_2_.getCount());
      this.resultSlots.awardUsedRecipes(p_230301_1_);
      this.shrinkStackInSlot(0);
      this.shrinkStackInSlot(1);
      this.access.execute((p_234653_0_, p_234653_1_) -> {
         p_234653_0_.levelEvent(1044, p_234653_1_, 0);
      });
      return p_230301_2_;
   }

   private void shrinkStackInSlot(int p_234654_1_) {
      ItemStack itemstack = this.inputSlots.getItem(p_234654_1_);
      itemstack.shrink(1);
      this.inputSlots.setItem(p_234654_1_, itemstack);
   }

   public void createResult() {
      List<SmithingRecipe> list = this.level.getRecipeManager().getRecipesFor(IRecipeType.SMITHING, this.inputSlots, this.level);
      if (list.isEmpty()) {
         this.resultSlots.setItem(0, ItemStack.EMPTY);
      } else {
         this.selectedRecipe = list.get(0);
         ItemStack itemstack = this.selectedRecipe.assemble(this.inputSlots);
         this.resultSlots.setRecipeUsed(this.selectedRecipe);
         this.resultSlots.setItem(0, itemstack);
      }

   }

   protected boolean shouldQuickMoveToAdditionalSlot(ItemStack p_241210_1_) {
      return this.recipes.stream().anyMatch((p_241444_1_) -> {
         return p_241444_1_.isAdditionIngredient(p_241210_1_);
      });
   }

   public boolean canTakeItemForPickAll(ItemStack p_94530_1_, Slot p_94530_2_) {
      return p_94530_2_.container != this.resultSlots && super.canTakeItemForPickAll(p_94530_1_, p_94530_2_);
   }
}
