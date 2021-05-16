package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class ItemGroup {
   public static ItemGroup[] TABS = new ItemGroup[12];
   public static final ItemGroup TAB_BUILDING_BLOCKS = (new ItemGroup(0, "buildingBlocks") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack makeIcon() {
         return new ItemStack(Blocks.BRICKS);
      }
   }).setRecipeFolderName("building_blocks");
   public static final ItemGroup TAB_DECORATIONS = new ItemGroup(1, "decorations") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack makeIcon() {
         return new ItemStack(Blocks.PEONY);
      }
   };
   public static final ItemGroup TAB_REDSTONE = new ItemGroup(2, "redstone") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack makeIcon() {
         return new ItemStack(Items.REDSTONE);
      }
   };
   public static final ItemGroup TAB_TRANSPORTATION = new ItemGroup(3, "transportation") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack makeIcon() {
         return new ItemStack(Blocks.POWERED_RAIL);
      }
   };
   public static final ItemGroup TAB_MISC = new ItemGroup(6, "misc") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack makeIcon() {
         return new ItemStack(Items.LAVA_BUCKET);
      }
   };
   public static final ItemGroup TAB_SEARCH = (new ItemGroup(5, "search") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack makeIcon() {
         return new ItemStack(Items.COMPASS);
      }
   }).setBackgroundSuffix("item_search.png");
   public static final ItemGroup TAB_FOOD = new ItemGroup(7, "food") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack makeIcon() {
         return new ItemStack(Items.APPLE);
      }
   };
   public static final ItemGroup TAB_TOOLS = (new ItemGroup(8, "tools") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack makeIcon() {
         return new ItemStack(Items.IRON_AXE);
      }
   }).setEnchantmentCategories(new EnchantmentType[]{EnchantmentType.VANISHABLE, EnchantmentType.DIGGER, EnchantmentType.FISHING_ROD, EnchantmentType.BREAKABLE});
   public static final ItemGroup TAB_COMBAT = (new ItemGroup(9, "combat") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack makeIcon() {
         return new ItemStack(Items.GOLDEN_SWORD);
      }
   }).setEnchantmentCategories(new EnchantmentType[]{EnchantmentType.VANISHABLE, EnchantmentType.ARMOR, EnchantmentType.ARMOR_FEET, EnchantmentType.ARMOR_HEAD, EnchantmentType.ARMOR_LEGS, EnchantmentType.ARMOR_CHEST, EnchantmentType.BOW, EnchantmentType.WEAPON, EnchantmentType.WEARABLE, EnchantmentType.BREAKABLE, EnchantmentType.TRIDENT, EnchantmentType.CROSSBOW});
   public static final ItemGroup TAB_BREWING = new ItemGroup(10, "brewing") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack makeIcon() {
         return PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
      }
   };
   public static final ItemGroup TAB_MATERIALS = TAB_MISC;
   public static final ItemGroup TAB_HOTBAR = new ItemGroup(4, "hotbar") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack makeIcon() {
         return new ItemStack(Blocks.BOOKSHELF);
      }

      @OnlyIn(Dist.CLIENT)
      public void fillItemList(NonNullList<ItemStack> p_78018_1_) {
         throw new RuntimeException("Implement exception client-side.");
      }

      @OnlyIn(Dist.CLIENT)
      public boolean isAlignedRight() {
         return true;
      }
   };
   public static final ItemGroup TAB_INVENTORY = (new ItemGroup(11, "inventory") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack makeIcon() {
         return new ItemStack(Blocks.CHEST);
      }
   }).setBackgroundSuffix("inventory.png").hideScroll().hideTitle();
   private final int id;
   private final String langId;
   private final ITextComponent displayName;
   private String recipeFolderName;
   private String backgroundSuffix = "items.png";
   private boolean canScroll = true;
   private boolean showTitle = true;
   private EnchantmentType[] enchantmentCategories = new EnchantmentType[0];
   private ItemStack iconItemStack;

   public ItemGroup(String label) {
       this(-1, label);
   }

   public ItemGroup(int p_i1853_1_, String p_i1853_2_) {
      this.langId = p_i1853_2_;
      this.displayName = new TranslationTextComponent("itemGroup." + p_i1853_2_);
      this.iconItemStack = ItemStack.EMPTY;
      this.id = addGroupSafe(p_i1853_1_, this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getId() {
      return this.id;
   }

   public String getRecipeFolderName() {
      return this.recipeFolderName == null ? this.langId : this.recipeFolderName;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getDisplayName() {
      return this.displayName;
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getIconItem() {
      if (this.iconItemStack.isEmpty()) {
         this.iconItemStack = this.makeIcon();
      }

      return this.iconItemStack;
   }

   @OnlyIn(Dist.CLIENT)
   public abstract ItemStack makeIcon();

   @OnlyIn(Dist.CLIENT)
   public String getBackgroundSuffix() {
      return this.backgroundSuffix;
   }

   public ItemGroup setBackgroundSuffix(String p_78025_1_) {
      this.backgroundSuffix = p_78025_1_;
      return this;
   }

   public ItemGroup setRecipeFolderName(String p_199783_1_) {
      this.recipeFolderName = p_199783_1_;
      return this;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean showTitle() {
      return this.showTitle;
   }

   public ItemGroup hideTitle() {
      this.showTitle = false;
      return this;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canScroll() {
      return this.canScroll;
   }

   public ItemGroup hideScroll() {
      this.canScroll = false;
      return this;
   }

   @OnlyIn(Dist.CLIENT)
   public int getColumn() {
      if (id > 11) return ((id - 12) % 10) % 5;
      return this.id % 6;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isTopRow() {
      if (id > 11) return ((id - 12) % 10) < 5;
      return this.id < 6;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isAlignedRight() {
      return this.getColumn() == 5;
   }

   public EnchantmentType[] getEnchantmentCategories() {
      return this.enchantmentCategories;
   }

   public ItemGroup setEnchantmentCategories(EnchantmentType... p_111229_1_) {
      this.enchantmentCategories = p_111229_1_;
      return this;
   }

   public boolean hasEnchantmentCategory(@Nullable EnchantmentType p_111226_1_) {
      if (p_111226_1_ != null) {
         for(EnchantmentType enchantmenttype : this.enchantmentCategories) {
            if (enchantmenttype == p_111226_1_) {
               return true;
            }
         }
      }

      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public void fillItemList(NonNullList<ItemStack> p_78018_1_) {
      for(Item item : Registry.ITEM) {
         item.fillItemCategory(this, p_78018_1_);
      }

   }

   public int getTabPage() {
      return id < 12 ? 0 : ((id - 12) / 10) + 1;
   }

   public boolean hasSearchBar() {
      return id == TAB_SEARCH.id;
   }

   /**
    * Gets the width of the search bar of the creative tab, use this if your
    * creative tab name overflows together with a custom texture.
    *
    * @return The width of the search bar, 89 by default
    */
   public int getSearchbarWidth() {
      return 89;
   }

   @OnlyIn(Dist.CLIENT)
   public net.minecraft.util.ResourceLocation getBackgroundImage() {
      return new net.minecraft.util.ResourceLocation("textures/gui/container/creative_inventory/tab_" + this.getBackgroundSuffix());
   }

   private static final net.minecraft.util.ResourceLocation CREATIVE_INVENTORY_TABS = new net.minecraft.util.ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
   @OnlyIn(Dist.CLIENT)
   public net.minecraft.util.ResourceLocation getTabsImage() {
      return CREATIVE_INVENTORY_TABS;
   }

   public int getLabelColor() {
      return 4210752;
   }

   public int getSlotColor() {
      return -2130706433;
   }

   public static synchronized int getGroupCountSafe() {
      return ItemGroup.TABS.length;
   }

   private static synchronized int addGroupSafe(int index, ItemGroup newGroup) {
      if(index == -1) {
         index = TABS.length;
      }
      if (index >= TABS.length) {
         ItemGroup[] tmp = new ItemGroup[index + 1];
         System.arraycopy(TABS, 0, tmp, 0, TABS.length);
         TABS = tmp;
      }
      TABS[index] = newGroup;
      return index;
   }
}
