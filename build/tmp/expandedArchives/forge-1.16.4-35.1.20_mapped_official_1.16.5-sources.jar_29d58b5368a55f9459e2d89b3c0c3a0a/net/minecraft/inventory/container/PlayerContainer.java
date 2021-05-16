package net.minecraft.inventory.container;

import com.mojang.datafixers.util.Pair;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBookCategory;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PlayerContainer extends RecipeBookContainer<CraftingInventory> {
   public static final ResourceLocation BLOCK_ATLAS = new ResourceLocation("textures/atlas/blocks.png");
   public static final ResourceLocation EMPTY_ARMOR_SLOT_HELMET = new ResourceLocation("item/empty_armor_slot_helmet");
   public static final ResourceLocation EMPTY_ARMOR_SLOT_CHESTPLATE = new ResourceLocation("item/empty_armor_slot_chestplate");
   public static final ResourceLocation EMPTY_ARMOR_SLOT_LEGGINGS = new ResourceLocation("item/empty_armor_slot_leggings");
   public static final ResourceLocation EMPTY_ARMOR_SLOT_BOOTS = new ResourceLocation("item/empty_armor_slot_boots");
   public static final ResourceLocation EMPTY_ARMOR_SLOT_SHIELD = new ResourceLocation("item/empty_armor_slot_shield");
   private static final ResourceLocation[] TEXTURE_EMPTY_SLOTS = new ResourceLocation[]{EMPTY_ARMOR_SLOT_BOOTS, EMPTY_ARMOR_SLOT_LEGGINGS, EMPTY_ARMOR_SLOT_CHESTPLATE, EMPTY_ARMOR_SLOT_HELMET};
   private static final EquipmentSlotType[] SLOT_IDS = new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};
   private final CraftingInventory craftSlots = new CraftingInventory(this, 2, 2);
   private final CraftResultInventory resultSlots = new CraftResultInventory();
   public final boolean active;
   private final PlayerEntity owner;

   public PlayerContainer(PlayerInventory p_i1819_1_, boolean p_i1819_2_, PlayerEntity p_i1819_3_) {
      super((ContainerType<?>)null, 0);
      this.active = p_i1819_2_;
      this.owner = p_i1819_3_;
      this.addSlot(new CraftingResultSlot(p_i1819_1_.player, this.craftSlots, this.resultSlots, 0, 154, 28));

      for(int i = 0; i < 2; ++i) {
         for(int j = 0; j < 2; ++j) {
            this.addSlot(new Slot(this.craftSlots, j + i * 2, 98 + j * 18, 18 + i * 18));
         }
      }

      for(int k = 0; k < 4; ++k) {
         final EquipmentSlotType equipmentslottype = SLOT_IDS[k];
         this.addSlot(new Slot(p_i1819_1_, 39 - k, 8, 8 + k * 18) {
            public int getMaxStackSize() {
               return 1;
            }

            public boolean mayPlace(ItemStack p_75214_1_) {
               return p_75214_1_.canEquip(equipmentslottype, owner);
            }

            public boolean mayPickup(PlayerEntity p_82869_1_) {
               ItemStack itemstack = this.getItem();
               return !itemstack.isEmpty() && !p_82869_1_.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.mayPickup(p_82869_1_);
            }

            @OnlyIn(Dist.CLIENT)
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
               return Pair.of(PlayerContainer.BLOCK_ATLAS, PlayerContainer.TEXTURE_EMPTY_SLOTS[equipmentslottype.getIndex()]);
            }
         });
      }

      for(int l = 0; l < 3; ++l) {
         for(int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot(p_i1819_1_, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));
         }
      }

      for(int i1 = 0; i1 < 9; ++i1) {
         this.addSlot(new Slot(p_i1819_1_, i1, 8 + i1 * 18, 142));
      }

      this.addSlot(new Slot(p_i1819_1_, 40, 77, 62) {
         @OnlyIn(Dist.CLIENT)
         public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return Pair.of(PlayerContainer.BLOCK_ATLAS, PlayerContainer.EMPTY_ARMOR_SLOT_SHIELD);
         }
      });
   }

   public void fillCraftSlotsStackedContents(RecipeItemHelper p_201771_1_) {
      this.craftSlots.fillStackedContents(p_201771_1_);
   }

   public void clearCraftingContent() {
      this.resultSlots.clearContent();
      this.craftSlots.clearContent();
   }

   public boolean recipeMatches(IRecipe<? super CraftingInventory> p_201769_1_) {
      return p_201769_1_.matches(this.craftSlots, this.owner.level);
   }

   public void slotsChanged(IInventory p_75130_1_) {
      WorkbenchContainer.slotChangedCraftingGrid(this.containerId, this.owner.level, this.owner, this.craftSlots, this.resultSlots);
   }

   public void removed(PlayerEntity p_75134_1_) {
      super.removed(p_75134_1_);
      this.resultSlots.clearContent();
      if (!p_75134_1_.level.isClientSide) {
         this.clearContainer(p_75134_1_, p_75134_1_.level, this.craftSlots);
      }
   }

   public boolean stillValid(PlayerEntity p_75145_1_) {
      return true;
   }

   public ItemStack quickMoveStack(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.slots.get(p_82846_2_);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         EquipmentSlotType equipmentslottype = MobEntity.getEquipmentSlotForItem(itemstack);
         if (p_82846_2_ == 0) {
            if (!this.moveItemStackTo(itemstack1, 9, 45, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemstack1, itemstack);
         } else if (p_82846_2_ >= 1 && p_82846_2_ < 5) {
            if (!this.moveItemStackTo(itemstack1, 9, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if (p_82846_2_ >= 5 && p_82846_2_ < 9) {
            if (!this.moveItemStackTo(itemstack1, 9, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if (equipmentslottype.getType() == EquipmentSlotType.Group.ARMOR && !this.slots.get(8 - equipmentslottype.getIndex()).hasItem()) {
            int i = 8 - equipmentslottype.getIndex();
            if (!this.moveItemStackTo(itemstack1, i, i + 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (equipmentslottype == EquipmentSlotType.OFFHAND && !this.slots.get(45).hasItem()) {
            if (!this.moveItemStackTo(itemstack1, 45, 46, false)) {
               return ItemStack.EMPTY;
            }
         } else if (p_82846_2_ >= 9 && p_82846_2_ < 36) {
            if (!this.moveItemStackTo(itemstack1, 36, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if (p_82846_2_ >= 36 && p_82846_2_ < 45) {
            if (!this.moveItemStackTo(itemstack1, 9, 36, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(itemstack1, 9, 45, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         ItemStack itemstack2 = slot.onTake(p_82846_1_, itemstack1);
         if (p_82846_2_ == 0) {
            p_82846_1_.drop(itemstack2, false);
         }
      }

      return itemstack;
   }

   public boolean canTakeItemForPickAll(ItemStack p_94530_1_, Slot p_94530_2_) {
      return p_94530_2_.container != this.resultSlots && super.canTakeItemForPickAll(p_94530_1_, p_94530_2_);
   }

   public int getResultSlotIndex() {
      return 0;
   }

   public int getGridWidth() {
      return this.craftSlots.getWidth();
   }

   public int getGridHeight() {
      return this.craftSlots.getHeight();
   }

   @OnlyIn(Dist.CLIENT)
   public int getSize() {
      return 5;
   }

   public CraftingInventory getCraftSlots() {
      return this.craftSlots;
   }

   @OnlyIn(Dist.CLIENT)
   public RecipeBookCategory getRecipeBookType() {
      return RecipeBookCategory.CRAFTING;
   }
}
