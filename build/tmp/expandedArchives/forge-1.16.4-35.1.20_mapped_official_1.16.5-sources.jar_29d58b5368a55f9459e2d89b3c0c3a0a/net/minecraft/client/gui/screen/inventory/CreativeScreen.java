package net.minecraft.client.gui.screen.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.settings.CreativeSettings;
import net.minecraft.client.settings.HotbarSnapshot;
import net.minecraft.client.util.ISearchTree;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreativeScreen extends DisplayEffectsScreen<CreativeScreen.CreativeContainer> {
   private static final ResourceLocation CREATIVE_TABS_LOCATION = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
   private static final Inventory CONTAINER = new Inventory(45);
   private static final ITextComponent TRASH_SLOT_TOOLTIP = new TranslationTextComponent("inventory.binSlot");
   private static int selectedTab = ItemGroup.TAB_BUILDING_BLOCKS.getId();
   private float scrollOffs;
   private boolean scrolling;
   private TextFieldWidget searchBox;
   @Nullable
   private List<Slot> originalSlots;
   @Nullable
   private Slot destroyItemSlot;
   private CreativeCraftingListener listener;
   private boolean ignoreTextInput;
   private static int tabPage = 0;
   private int maxPages = 0;
   private boolean hasClickedOutside;
   private final Map<ResourceLocation, ITag<Item>> visibleTags = Maps.newTreeMap();

   public CreativeScreen(PlayerEntity p_i1088_1_) {
      super(new CreativeScreen.CreativeContainer(p_i1088_1_), p_i1088_1_.inventory, StringTextComponent.EMPTY);
      p_i1088_1_.containerMenu = this.menu;
      this.passEvents = true;
      this.imageHeight = 136;
      this.imageWidth = 195;
   }

   public void tick() {
      if (!this.minecraft.gameMode.hasInfiniteItems()) {
         this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
      } else if (this.searchBox != null) {
         this.searchBox.tick();
      }

   }

   protected void slotClicked(@Nullable Slot p_184098_1_, int p_184098_2_, int p_184098_3_, ClickType p_184098_4_) {
      if (this.isCreativeSlot(p_184098_1_)) {
         this.searchBox.moveCursorToEnd();
         this.searchBox.setHighlightPos(0);
      }

      boolean flag = p_184098_4_ == ClickType.QUICK_MOVE;
      p_184098_4_ = p_184098_2_ == -999 && p_184098_4_ == ClickType.PICKUP ? ClickType.THROW : p_184098_4_;
      if (p_184098_1_ == null && selectedTab != ItemGroup.TAB_INVENTORY.getId() && p_184098_4_ != ClickType.QUICK_CRAFT) {
         PlayerInventory playerinventory1 = this.minecraft.player.inventory;
         if (!playerinventory1.getCarried().isEmpty() && this.hasClickedOutside) {
            if (p_184098_3_ == 0) {
               this.minecraft.player.drop(playerinventory1.getCarried(), true);
               this.minecraft.gameMode.handleCreativeModeItemDrop(playerinventory1.getCarried());
               playerinventory1.setCarried(ItemStack.EMPTY);
            }

            if (p_184098_3_ == 1) {
               ItemStack itemstack6 = playerinventory1.getCarried().split(1);
               this.minecraft.player.drop(itemstack6, true);
               this.minecraft.gameMode.handleCreativeModeItemDrop(itemstack6);
            }
         }
      } else {
         if (p_184098_1_ != null && !p_184098_1_.mayPickup(this.minecraft.player)) {
            return;
         }

         if (p_184098_1_ == this.destroyItemSlot && flag) {
            for(int j = 0; j < this.minecraft.player.inventoryMenu.getItems().size(); ++j) {
               this.minecraft.gameMode.handleCreativeModeItemAdd(ItemStack.EMPTY, j);
            }
         } else if (selectedTab == ItemGroup.TAB_INVENTORY.getId()) {
            if (p_184098_1_ == this.destroyItemSlot) {
               this.minecraft.player.inventory.setCarried(ItemStack.EMPTY);
            } else if (p_184098_4_ == ClickType.THROW && p_184098_1_ != null && p_184098_1_.hasItem()) {
               ItemStack itemstack = p_184098_1_.remove(p_184098_3_ == 0 ? 1 : p_184098_1_.getItem().getMaxStackSize());
               ItemStack itemstack1 = p_184098_1_.getItem();
               this.minecraft.player.drop(itemstack, true);
               this.minecraft.gameMode.handleCreativeModeItemDrop(itemstack);
               this.minecraft.gameMode.handleCreativeModeItemAdd(itemstack1, ((CreativeScreen.CreativeSlot)p_184098_1_).target.index);
            } else if (p_184098_4_ == ClickType.THROW && !this.minecraft.player.inventory.getCarried().isEmpty()) {
               this.minecraft.player.drop(this.minecraft.player.inventory.getCarried(), true);
               this.minecraft.gameMode.handleCreativeModeItemDrop(this.minecraft.player.inventory.getCarried());
               this.minecraft.player.inventory.setCarried(ItemStack.EMPTY);
            } else {
               this.minecraft.player.inventoryMenu.clicked(p_184098_1_ == null ? p_184098_2_ : ((CreativeScreen.CreativeSlot)p_184098_1_).target.index, p_184098_3_, p_184098_4_, this.minecraft.player);
               this.minecraft.player.inventoryMenu.broadcastChanges();
            }
         } else if (p_184098_4_ != ClickType.QUICK_CRAFT && p_184098_1_.container == CONTAINER) {
            PlayerInventory playerinventory = this.minecraft.player.inventory;
            ItemStack itemstack5 = playerinventory.getCarried();
            ItemStack itemstack7 = p_184098_1_.getItem();
            if (p_184098_4_ == ClickType.SWAP) {
               if (!itemstack7.isEmpty()) {
                  ItemStack itemstack10 = itemstack7.copy();
                  itemstack10.setCount(itemstack10.getMaxStackSize());
                  this.minecraft.player.inventory.setItem(p_184098_3_, itemstack10);
                  this.minecraft.player.inventoryMenu.broadcastChanges();
               }

               return;
            }

            if (p_184098_4_ == ClickType.CLONE) {
               if (playerinventory.getCarried().isEmpty() && p_184098_1_.hasItem()) {
                  ItemStack itemstack9 = p_184098_1_.getItem().copy();
                  itemstack9.setCount(itemstack9.getMaxStackSize());
                  playerinventory.setCarried(itemstack9);
               }

               return;
            }

            if (p_184098_4_ == ClickType.THROW) {
               if (!itemstack7.isEmpty()) {
                  ItemStack itemstack8 = itemstack7.copy();
                  itemstack8.setCount(p_184098_3_ == 0 ? 1 : itemstack8.getMaxStackSize());
                  this.minecraft.player.drop(itemstack8, true);
                  this.minecraft.gameMode.handleCreativeModeItemDrop(itemstack8);
               }

               return;
            }

            if (!itemstack5.isEmpty() && !itemstack7.isEmpty() && itemstack5.sameItem(itemstack7) && ItemStack.tagMatches(itemstack5, itemstack7)) {
               if (p_184098_3_ == 0) {
                  if (flag) {
                     itemstack5.setCount(itemstack5.getMaxStackSize());
                  } else if (itemstack5.getCount() < itemstack5.getMaxStackSize()) {
                     itemstack5.grow(1);
                  }
               } else {
                  itemstack5.shrink(1);
               }
            } else if (!itemstack7.isEmpty() && itemstack5.isEmpty()) {
               playerinventory.setCarried(itemstack7.copy());
               itemstack5 = playerinventory.getCarried();
               if (flag) {
                  itemstack5.setCount(itemstack5.getMaxStackSize());
               }
            } else if (p_184098_3_ == 0) {
               playerinventory.setCarried(ItemStack.EMPTY);
            } else {
               playerinventory.getCarried().shrink(1);
            }
         } else if (this.menu != null) {
            ItemStack itemstack3 = p_184098_1_ == null ? ItemStack.EMPTY : this.menu.getSlot(p_184098_1_.index).getItem();
            this.menu.clicked(p_184098_1_ == null ? p_184098_2_ : p_184098_1_.index, p_184098_3_, p_184098_4_, this.minecraft.player);
            if (Container.getQuickcraftHeader(p_184098_3_) == 2) {
               for(int k = 0; k < 9; ++k) {
                  this.minecraft.gameMode.handleCreativeModeItemAdd(this.menu.getSlot(45 + k).getItem(), 36 + k);
               }
            } else if (p_184098_1_ != null) {
               ItemStack itemstack4 = this.menu.getSlot(p_184098_1_.index).getItem();
               this.minecraft.gameMode.handleCreativeModeItemAdd(itemstack4, p_184098_1_.index - (this.menu).slots.size() + 9 + 36);
               int i = 45 + p_184098_3_;
               if (p_184098_4_ == ClickType.SWAP) {
                  this.minecraft.gameMode.handleCreativeModeItemAdd(itemstack3, i - (this.menu).slots.size() + 9 + 36);
               } else if (p_184098_4_ == ClickType.THROW && !itemstack3.isEmpty()) {
                  ItemStack itemstack2 = itemstack3.copy();
                  itemstack2.setCount(p_184098_3_ == 0 ? 1 : itemstack2.getMaxStackSize());
                  this.minecraft.player.drop(itemstack2, true);
                  this.minecraft.gameMode.handleCreativeModeItemDrop(itemstack2);
               }

               this.minecraft.player.inventoryMenu.broadcastChanges();
            }
         }
      }

   }

   private boolean isCreativeSlot(@Nullable Slot p_208018_1_) {
      return p_208018_1_ != null && p_208018_1_.container == CONTAINER;
   }

   protected void checkEffectRendering() {
      int i = this.leftPos;
      super.checkEffectRendering();
      if (this.searchBox != null && this.leftPos != i) {
         this.searchBox.setX(this.leftPos + 82);
      }

   }

   protected void init() {
      if (this.minecraft.gameMode.hasInfiniteItems()) {
         super.init();
         int tabCount = ItemGroup.TABS.length;
         if (tabCount > 12) {
            addButton(new net.minecraft.client.gui.widget.button.Button(leftPos,              topPos - 50, 20, 20, new StringTextComponent("<"), b -> tabPage = Math.max(tabPage - 1, 0       )));
            addButton(new net.minecraft.client.gui.widget.button.Button(leftPos + imageWidth - 20, topPos - 50, 20, 20, new StringTextComponent(">"), b -> tabPage = Math.min(tabPage + 1, maxPages)));
            maxPages = (int) Math.ceil((tabCount - 12) / 10D);
         }
         this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
         this.searchBox = new TextFieldWidget(this.font, this.leftPos + 82, this.topPos + 6, 80, 9, new TranslationTextComponent("itemGroup.search"));
         this.searchBox.setMaxLength(50);
         this.searchBox.setBordered(false);
         this.searchBox.setVisible(false);
         this.searchBox.setTextColor(16777215);
         this.children.add(this.searchBox);
         int i = selectedTab;
         selectedTab = -1;
         this.selectTab(ItemGroup.TABS[i]);
         this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
         this.listener = new CreativeCraftingListener(this.minecraft);
         this.minecraft.player.inventoryMenu.addSlotListener(this.listener);
      } else {
         this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
      }

   }

   public void resize(Minecraft p_231152_1_, int p_231152_2_, int p_231152_3_) {
      String s = this.searchBox.getValue();
      this.init(p_231152_1_, p_231152_2_, p_231152_3_);
      this.searchBox.setValue(s);
      if (!this.searchBox.getValue().isEmpty()) {
         this.refreshSearchResults();
      }

   }

   public void removed() {
      super.removed();
      if (this.minecraft.player != null && this.minecraft.player.inventory != null) {
         this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
      }

      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean charTyped(char p_231042_1_, int p_231042_2_) {
      if (this.ignoreTextInput) {
         return false;
      } else if (!ItemGroup.TABS[selectedTab].hasSearchBar()) {
         return false;
      } else {
         String s = this.searchBox.getValue();
         if (this.searchBox.charTyped(p_231042_1_, p_231042_2_)) {
            if (!Objects.equals(s, this.searchBox.getValue())) {
               this.refreshSearchResults();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      this.ignoreTextInput = false;
      if (!ItemGroup.TABS[selectedTab].hasSearchBar()) {
         if (this.minecraft.options.keyChat.matches(p_231046_1_, p_231046_2_)) {
            this.ignoreTextInput = true;
            this.selectTab(ItemGroup.TAB_SEARCH);
            return true;
         } else {
            return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
         }
      } else {
         boolean flag = !this.isCreativeSlot(this.hoveredSlot) || this.hoveredSlot.hasItem();
         boolean flag1 = InputMappings.getKey(p_231046_1_, p_231046_2_).getNumericKeyValue().isPresent();
         if (flag && flag1 && this.checkHotbarKeyPressed(p_231046_1_, p_231046_2_)) {
            this.ignoreTextInput = true;
            return true;
         } else {
            String s = this.searchBox.getValue();
            if (this.searchBox.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_)) {
               if (!Objects.equals(s, this.searchBox.getValue())) {
                  this.refreshSearchResults();
               }

               return true;
            } else {
               return this.searchBox.isFocused() && this.searchBox.isVisible() && p_231046_1_ != 256 ? true : super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
            }
         }
      }
   }

   public boolean keyReleased(int p_223281_1_, int p_223281_2_, int p_223281_3_) {
      this.ignoreTextInput = false;
      return super.keyReleased(p_223281_1_, p_223281_2_, p_223281_3_);
   }

   private void refreshSearchResults() {
      (this.menu).items.clear();
      this.visibleTags.clear();

      ItemGroup tab = ItemGroup.TABS[selectedTab];
      if (tab.hasSearchBar() && tab != ItemGroup.TAB_SEARCH) {
         tab.fillItemList(menu.items);
         if (!this.searchBox.getValue().isEmpty()) {
            //TODO: Make this a SearchTree not a manual search
            String search = this.searchBox.getValue().toLowerCase(Locale.ROOT);
            java.util.Iterator<ItemStack> itr = menu.items.iterator();
            while (itr.hasNext()) {
               ItemStack stack = itr.next();
               boolean matches = false;
               for (ITextComponent line : stack.getTooltipLines(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL)) {
                  if (TextFormatting.stripFormatting(line.getString()).toLowerCase(Locale.ROOT).contains(search)) {
                     matches = true;
                     break;
                  }
               }
               if (!matches)
                  itr.remove();
            }
         }
         this.scrollOffs = 0.0F;
         menu.scrollTo(0.0F);
         return;
      }

      String s = this.searchBox.getValue();
      if (s.isEmpty()) {
         for(Item item : Registry.ITEM) {
            item.fillItemCategory(ItemGroup.TAB_SEARCH, (this.menu).items);
         }
      } else {
         ISearchTree<ItemStack> isearchtree;
         if (s.startsWith("#")) {
            s = s.substring(1);
            isearchtree = this.minecraft.getSearchTree(SearchTreeManager.CREATIVE_TAGS);
            this.updateVisibleTags(s);
         } else {
            isearchtree = this.minecraft.getSearchTree(SearchTreeManager.CREATIVE_NAMES);
         }

         (this.menu).items.addAll(isearchtree.search(s.toLowerCase(Locale.ROOT)));
      }

      this.scrollOffs = 0.0F;
      this.menu.scrollTo(0.0F);
   }

   private void updateVisibleTags(String p_214080_1_) {
      int i = p_214080_1_.indexOf(58);
      Predicate<ResourceLocation> predicate;
      if (i == -1) {
         predicate = (p_214084_1_) -> {
            return p_214084_1_.getPath().contains(p_214080_1_);
         };
      } else {
         String s = p_214080_1_.substring(0, i).trim();
         String s1 = p_214080_1_.substring(i + 1).trim();
         predicate = (p_214081_2_) -> {
            return p_214081_2_.getNamespace().contains(s) && p_214081_2_.getPath().contains(s1);
         };
      }

      ITagCollection<Item> itagcollection = ItemTags.getAllTags();
      itagcollection.getAvailableTags().stream().filter(predicate).forEach((p_214082_2_) -> {
         ITag itag = this.visibleTags.put(p_214082_2_, itagcollection.getTag(p_214082_2_));
      });
   }

   protected void renderLabels(MatrixStack p_230451_1_, int p_230451_2_, int p_230451_3_) {
      ItemGroup itemgroup = ItemGroup.TABS[selectedTab];
      if (itemgroup != null && itemgroup.showTitle()) {
         RenderSystem.disableBlend();
         this.font.draw(p_230451_1_, itemgroup.getDisplayName(), 8.0F, 6.0F, itemgroup.getLabelColor());
      }

   }

   public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
      if (p_231044_5_ == 0) {
         double d0 = p_231044_1_ - (double)this.leftPos;
         double d1 = p_231044_3_ - (double)this.topPos;

         for(ItemGroup itemgroup : ItemGroup.TABS) {
            if (itemgroup != null && this.checkTabClicked(itemgroup, d0, d1)) {
               return true;
            }
         }

         if (selectedTab != ItemGroup.TAB_INVENTORY.getId() && this.insideScrollbar(p_231044_1_, p_231044_3_)) {
            this.scrolling = this.canScroll();
            return true;
         }
      }

      return super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
   }

   public boolean mouseReleased(double p_231048_1_, double p_231048_3_, int p_231048_5_) {
      if (p_231048_5_ == 0) {
         double d0 = p_231048_1_ - (double)this.leftPos;
         double d1 = p_231048_3_ - (double)this.topPos;
         this.scrolling = false;

         for(ItemGroup itemgroup : ItemGroup.TABS) {
            if (itemgroup != null && this.checkTabClicked(itemgroup, d0, d1)) {
               this.selectTab(itemgroup);
               return true;
            }
         }
      }

      return super.mouseReleased(p_231048_1_, p_231048_3_, p_231048_5_);
   }

   private boolean canScroll() {
      if (ItemGroup.TABS[selectedTab] == null) return false;
      return selectedTab != ItemGroup.TAB_INVENTORY.getId() && ItemGroup.TABS[selectedTab].canScroll() && this.menu.canScroll();
   }

   private void selectTab(ItemGroup p_147050_1_) {
      if (p_147050_1_ == null) return;
      int i = selectedTab;
      selectedTab = p_147050_1_.getId();
      slotColor = p_147050_1_.getSlotColor();
      this.quickCraftSlots.clear();
      (this.menu).items.clear();
      if (p_147050_1_ == ItemGroup.TAB_HOTBAR) {
         CreativeSettings creativesettings = this.minecraft.getHotbarManager();

         for(int j = 0; j < 9; ++j) {
            HotbarSnapshot hotbarsnapshot = creativesettings.get(j);
            if (hotbarsnapshot.isEmpty()) {
               for(int k = 0; k < 9; ++k) {
                  if (k == j) {
                     ItemStack itemstack = new ItemStack(Items.PAPER);
                     itemstack.getOrCreateTagElement("CustomCreativeLock");
                     ITextComponent itextcomponent = this.minecraft.options.keyHotbarSlots[j].getTranslatedKeyMessage();
                     ITextComponent itextcomponent1 = this.minecraft.options.keySaveHotbarActivator.getTranslatedKeyMessage();
                     itemstack.setHoverName(new TranslationTextComponent("inventory.hotbarInfo", itextcomponent1, itextcomponent));
                     (this.menu).items.add(itemstack);
                  } else {
                     (this.menu).items.add(ItemStack.EMPTY);
                  }
               }
            } else {
               (this.menu).items.addAll(hotbarsnapshot);
            }
         }
      } else if (p_147050_1_ != ItemGroup.TAB_SEARCH) {
         p_147050_1_.fillItemList((this.menu).items);
      }

      if (p_147050_1_ == ItemGroup.TAB_INVENTORY) {
         Container container = this.minecraft.player.inventoryMenu;
         if (this.originalSlots == null) {
            this.originalSlots = ImmutableList.copyOf((this.menu).slots);
         }

         (this.menu).slots.clear();

         for(int l = 0; l < container.slots.size(); ++l) {
            int i1;
            int j1;
            if (l >= 5 && l < 9) {
               int l1 = l - 5;
               int j2 = l1 / 2;
               int l2 = l1 % 2;
               i1 = 54 + j2 * 54;
               j1 = 6 + l2 * 27;
            } else if (l >= 0 && l < 5) {
               i1 = -2000;
               j1 = -2000;
            } else if (l == 45) {
               i1 = 35;
               j1 = 20;
            } else {
               int k1 = l - 9;
               int i2 = k1 % 9;
               int k2 = k1 / 9;
               i1 = 9 + i2 * 18;
               if (l >= 36) {
                  j1 = 112;
               } else {
                  j1 = 54 + k2 * 18;
               }
            }

            Slot slot = new CreativeScreen.CreativeSlot(container.slots.get(l), l, i1, j1);
            (this.menu).slots.add(slot);
         }

         this.destroyItemSlot = new Slot(CONTAINER, 0, 173, 112);
         (this.menu).slots.add(this.destroyItemSlot);
      } else if (i == ItemGroup.TAB_INVENTORY.getId()) {
         (this.menu).slots.clear();
         (this.menu).slots.addAll(this.originalSlots);
         this.originalSlots = null;
      }

      if (this.searchBox != null) {
         if (p_147050_1_.hasSearchBar()) {
            this.searchBox.setVisible(true);
            this.searchBox.setCanLoseFocus(false);
            this.searchBox.setFocus(true);
            if (i != p_147050_1_.getId()) {
               this.searchBox.setValue("");
            }
            this.searchBox.setWidth(p_147050_1_.getSearchbarWidth());
            this.searchBox.x = this.leftPos + (82 /*default left*/ + 89 /*default width*/) - this.searchBox.getWidth();

            this.refreshSearchResults();
         } else {
            this.searchBox.setVisible(false);
            this.searchBox.setCanLoseFocus(true);
            this.searchBox.setFocus(false);
            this.searchBox.setValue("");
         }
      }

      this.scrollOffs = 0.0F;
      this.menu.scrollTo(0.0F);
   }

   public boolean mouseScrolled(double p_231043_1_, double p_231043_3_, double p_231043_5_) {
      if (!this.canScroll()) {
         return false;
      } else {
         int i = ((this.menu).items.size() + 9 - 1) / 9 - 5;
         this.scrollOffs = (float)((double)this.scrollOffs - p_231043_5_ / (double)i);
         this.scrollOffs = MathHelper.clamp(this.scrollOffs, 0.0F, 1.0F);
         this.menu.scrollTo(this.scrollOffs);
         return true;
      }
   }

   protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
      boolean flag = p_195361_1_ < (double)p_195361_5_ || p_195361_3_ < (double)p_195361_6_ || p_195361_1_ >= (double)(p_195361_5_ + this.imageWidth) || p_195361_3_ >= (double)(p_195361_6_ + this.imageHeight);
      this.hasClickedOutside = flag && !this.checkTabClicked(ItemGroup.TABS[selectedTab], p_195361_1_, p_195361_3_);
      return this.hasClickedOutside;
   }

   protected boolean insideScrollbar(double p_195376_1_, double p_195376_3_) {
      int i = this.leftPos;
      int j = this.topPos;
      int k = i + 175;
      int l = j + 18;
      int i1 = k + 14;
      int j1 = l + 112;
      return p_195376_1_ >= (double)k && p_195376_3_ >= (double)l && p_195376_1_ < (double)i1 && p_195376_3_ < (double)j1;
   }

   public boolean mouseDragged(double p_231045_1_, double p_231045_3_, int p_231045_5_, double p_231045_6_, double p_231045_8_) {
      if (this.scrolling) {
         int i = this.topPos + 18;
         int j = i + 112;
         this.scrollOffs = ((float)p_231045_3_ - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
         this.scrollOffs = MathHelper.clamp(this.scrollOffs, 0.0F, 1.0F);
         this.menu.scrollTo(this.scrollOffs);
         return true;
      } else {
         return super.mouseDragged(p_231045_1_, p_231045_3_, p_231045_5_, p_231045_6_, p_231045_8_);
      }
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);

      int start = tabPage * 10;
      int end = Math.min(ItemGroup.TABS.length, ((tabPage + 1) * 10) + 2);
      if (tabPage != 0) start += 2;
      boolean rendered = false;

       for (int x = start; x < end; x++) {
         ItemGroup itemgroup = ItemGroup.TABS[x];
         if (itemgroup != null && this.checkTabHovering(p_230430_1_, itemgroup, p_230430_2_, p_230430_3_)) {
            rendered = true;
            break;
         }
      }
      if (!rendered && !this.checkTabHovering(p_230430_1_, ItemGroup.TAB_SEARCH, p_230430_2_, p_230430_3_))
         this.checkTabHovering(p_230430_1_, ItemGroup.TAB_INVENTORY, p_230430_2_, p_230430_3_);

      if (this.destroyItemSlot != null && selectedTab == ItemGroup.TAB_INVENTORY.getId() && this.isHovering(this.destroyItemSlot.x, this.destroyItemSlot.y, 16, 16, (double)p_230430_2_, (double)p_230430_3_)) {
         this.renderTooltip(p_230430_1_, TRASH_SLOT_TOOLTIP, p_230430_2_, p_230430_3_);
      }

      if (maxPages != 0) {
          ITextComponent page = new StringTextComponent(String.format("%d / %d", tabPage + 1, maxPages + 1));
          RenderSystem.disableLighting();
          this.setBlitOffset(300);
          this.itemRenderer.blitOffset = 300.0F;
          font.drawShadow(p_230430_1_, page.getVisualOrderText(), leftPos + (imageWidth / 2) - (font.width(page) / 2), topPos - 44, -1);
          this.setBlitOffset(0);
          this.itemRenderer.blitOffset = 0.0F;
      }

      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
   }

   protected void renderTooltip(MatrixStack p_230457_1_, ItemStack p_230457_2_, int p_230457_3_, int p_230457_4_) {
      if (selectedTab == ItemGroup.TAB_SEARCH.getId()) {
         List<ITextComponent> list = p_230457_2_.getTooltipLines(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
         List<ITextComponent> list1 = Lists.newArrayList(list);
         Item item = p_230457_2_.getItem();
         ItemGroup itemgroup = item.getItemCategory();
         if (itemgroup == null && item == Items.ENCHANTED_BOOK) {
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(p_230457_2_);
            if (map.size() == 1) {
               Enchantment enchantment = map.keySet().iterator().next();

               for(ItemGroup itemgroup1 : ItemGroup.TABS) {
                  if (itemgroup1.hasEnchantmentCategory(enchantment.category)) {
                     itemgroup = itemgroup1;
                     break;
                  }
               }
            }
         }

         this.visibleTags.forEach((p_214083_2_, p_214083_3_) -> {
            if (p_214083_3_.contains(item)) {
               list1.add(1, (new StringTextComponent("#" + p_214083_2_)).withStyle(TextFormatting.DARK_PURPLE));
            }

         });
         if (itemgroup != null) {
            list1.add(1, itemgroup.getDisplayName().copy().withStyle(TextFormatting.BLUE));
         }

         net.minecraft.client.gui.FontRenderer font = p_230457_2_.getItem().getFontRenderer(p_230457_2_);
         net.minecraftforge.fml.client.gui.GuiUtils.preItemToolTip(p_230457_2_);
         this.renderWrappedToolTip(p_230457_1_, list1, p_230457_3_, p_230457_4_, (font == null ? this.font : font));
         net.minecraftforge.fml.client.gui.GuiUtils.postItemToolTip();
      } else {
         super.renderTooltip(p_230457_1_, p_230457_2_, p_230457_3_, p_230457_4_);
      }

   }

   protected void renderBg(MatrixStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      ItemGroup itemgroup = ItemGroup.TABS[selectedTab];

      int start = tabPage * 10;
      int end = Math.min(ItemGroup.TABS.length, ((tabPage + 1) * 10 + 2));
      if (tabPage != 0) start += 2;

      for (int idx = start; idx < end; idx++) {
         ItemGroup itemgroup1 = ItemGroup.TABS[idx];
         if (itemgroup1 != null && itemgroup1.getId() != selectedTab) {
            this.minecraft.getTextureManager().bind(itemgroup1.getTabsImage());
            this.renderTabButton(p_230450_1_, itemgroup1);
         }
      }

      if (tabPage != 0) {
         if (itemgroup != ItemGroup.TAB_SEARCH) {
            this.minecraft.getTextureManager().bind(ItemGroup.TAB_SEARCH.getTabsImage());
            renderTabButton(p_230450_1_, ItemGroup.TAB_SEARCH);
         }
         if (itemgroup != ItemGroup.TAB_INVENTORY) {
            this.minecraft.getTextureManager().bind(ItemGroup.TAB_INVENTORY.getTabsImage());
            renderTabButton(p_230450_1_, ItemGroup.TAB_INVENTORY);
         }
      }

      this.minecraft.getTextureManager().bind(itemgroup.getBackgroundImage());
      this.blit(p_230450_1_, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
      this.searchBox.render(p_230450_1_, p_230450_3_, p_230450_4_, p_230450_2_);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      int i = this.leftPos + 175;
      int j = this.topPos + 18;
      int k = j + 112;
      this.minecraft.getTextureManager().bind(itemgroup.getTabsImage());
      if (itemgroup.canScroll()) {
         this.blit(p_230450_1_, i, j + (int)((float)(k - j - 17) * this.scrollOffs), 232 + (this.canScroll() ? 0 : 12), 0, 12, 15);
      }

      if ((itemgroup == null || itemgroup.getTabPage() != tabPage) && (itemgroup != ItemGroup.TAB_SEARCH && itemgroup != ItemGroup.TAB_INVENTORY))
         return;

      this.renderTabButton(p_230450_1_, itemgroup);
      if (itemgroup == ItemGroup.TAB_INVENTORY) {
         InventoryScreen.renderEntityInInventory(this.leftPos + 88, this.topPos + 45, 20, (float)(this.leftPos + 88 - p_230450_3_), (float)(this.topPos + 45 - 30 - p_230450_4_), this.minecraft.player);
      }

   }

   protected boolean checkTabClicked(ItemGroup p_195375_1_, double p_195375_2_, double p_195375_4_) {
      if (p_195375_1_.getTabPage() != tabPage && p_195375_1_ != ItemGroup.TAB_SEARCH && p_195375_1_ != ItemGroup.TAB_INVENTORY) return false;
      int i = p_195375_1_.getColumn();
      int j = 28 * i;
      int k = 0;
      if (p_195375_1_.isAlignedRight()) {
         j = this.imageWidth - 28 * (6 - i) + 2;
      } else if (i > 0) {
         j += i;
      }

      if (p_195375_1_.isTopRow()) {
         k = k - 32;
      } else {
         k = k + this.imageHeight;
      }

      return p_195375_2_ >= (double)j && p_195375_2_ <= (double)(j + 28) && p_195375_4_ >= (double)k && p_195375_4_ <= (double)(k + 32);
   }

   protected boolean checkTabHovering(MatrixStack p_238809_1_, ItemGroup p_238809_2_, int p_238809_3_, int p_238809_4_) {
      int i = p_238809_2_.getColumn();
      int j = 28 * i;
      int k = 0;
      if (p_238809_2_.isAlignedRight()) {
         j = this.imageWidth - 28 * (6 - i) + 2;
      } else if (i > 0) {
         j += i;
      }

      if (p_238809_2_.isTopRow()) {
         k = k - 32;
      } else {
         k = k + this.imageHeight;
      }

      if (this.isHovering(j + 3, k + 3, 23, 27, (double)p_238809_3_, (double)p_238809_4_)) {
         this.renderTooltip(p_238809_1_, p_238809_2_.getDisplayName(), p_238809_3_, p_238809_4_);
         return true;
      } else {
         return false;
      }
   }

   protected void renderTabButton(MatrixStack p_238808_1_, ItemGroup p_238808_2_) {
      boolean flag = p_238808_2_.getId() == selectedTab;
      boolean flag1 = p_238808_2_.isTopRow();
      int i = p_238808_2_.getColumn();
      int j = i * 28;
      int k = 0;
      int l = this.leftPos + 28 * i;
      int i1 = this.topPos;
      int j1 = 32;
      if (flag) {
         k += 32;
      }

      if (p_238808_2_.isAlignedRight()) {
         l = this.leftPos + this.imageWidth - 28 * (6 - i);
      } else if (i > 0) {
         l += i;
      }

      if (flag1) {
         i1 = i1 - 28;
      } else {
         k += 64;
         i1 = i1 + (this.imageHeight - 4);
      }

      RenderSystem.color3f(1F, 1F, 1F); //Forge: Reset color in case Items change it.
      RenderSystem.enableBlend(); //Forge: Make sure blend is enabled else tabs show a white border.
      this.blit(p_238808_1_, l, i1, j, k, 28, 32);
      this.itemRenderer.blitOffset = 100.0F;
      l = l + 6;
      i1 = i1 + 8 + (flag1 ? 1 : -1);
      RenderSystem.enableRescaleNormal();
      ItemStack itemstack = p_238808_2_.getIconItem();
      this.itemRenderer.renderAndDecorateItem(itemstack, l, i1);
      this.itemRenderer.renderGuiItemDecorations(this.font, itemstack, l, i1);
      this.itemRenderer.blitOffset = 0.0F;
   }

   public int getSelectedTab() {
      return selectedTab;
   }

   public static void handleHotbarLoadOrSave(Minecraft p_192044_0_, int p_192044_1_, boolean p_192044_2_, boolean p_192044_3_) {
      ClientPlayerEntity clientplayerentity = p_192044_0_.player;
      CreativeSettings creativesettings = p_192044_0_.getHotbarManager();
      HotbarSnapshot hotbarsnapshot = creativesettings.get(p_192044_1_);
      if (p_192044_2_) {
         for(int i = 0; i < PlayerInventory.getSelectionSize(); ++i) {
            ItemStack itemstack = hotbarsnapshot.get(i).copy();
            clientplayerentity.inventory.setItem(i, itemstack);
            p_192044_0_.gameMode.handleCreativeModeItemAdd(itemstack, 36 + i);
         }

         clientplayerentity.inventoryMenu.broadcastChanges();
      } else if (p_192044_3_) {
         for(int j = 0; j < PlayerInventory.getSelectionSize(); ++j) {
            hotbarsnapshot.set(j, clientplayerentity.inventory.getItem(j).copy());
         }

         ITextComponent itextcomponent = p_192044_0_.options.keyHotbarSlots[p_192044_1_].getTranslatedKeyMessage();
         ITextComponent itextcomponent1 = p_192044_0_.options.keyLoadHotbarActivator.getTranslatedKeyMessage();
         p_192044_0_.gui.setOverlayMessage(new TranslationTextComponent("inventory.hotbarSaved", itextcomponent1, itextcomponent), false);
         creativesettings.save();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class CreativeContainer extends Container {
      public final NonNullList<ItemStack> items = NonNullList.create();

      public CreativeContainer(PlayerEntity p_i1086_1_) {
         super((ContainerType<?>)null, 0);
         PlayerInventory playerinventory = p_i1086_1_.inventory;

         for(int i = 0; i < 5; ++i) {
            for(int j = 0; j < 9; ++j) {
               this.addSlot(new CreativeScreen.LockedSlot(CreativeScreen.CONTAINER, i * 9 + j, 9 + j * 18, 18 + i * 18));
            }
         }

         for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerinventory, k, 9 + k * 18, 112));
         }

         this.scrollTo(0.0F);
      }

      public boolean stillValid(PlayerEntity p_75145_1_) {
         return true;
      }

      public void scrollTo(float p_148329_1_) {
         int i = (this.items.size() + 9 - 1) / 9 - 5;
         int j = (int)((double)(p_148329_1_ * (float)i) + 0.5D);
         if (j < 0) {
            j = 0;
         }

         for(int k = 0; k < 5; ++k) {
            for(int l = 0; l < 9; ++l) {
               int i1 = l + (k + j) * 9;
               if (i1 >= 0 && i1 < this.items.size()) {
                  CreativeScreen.CONTAINER.setItem(l + k * 9, this.items.get(i1));
               } else {
                  CreativeScreen.CONTAINER.setItem(l + k * 9, ItemStack.EMPTY);
               }
            }
         }

      }

      public boolean canScroll() {
         return this.items.size() > 45;
      }

      public ItemStack quickMoveStack(PlayerEntity p_82846_1_, int p_82846_2_) {
         if (p_82846_2_ >= this.slots.size() - 9 && p_82846_2_ < this.slots.size()) {
            Slot slot = this.slots.get(p_82846_2_);
            if (slot != null && slot.hasItem()) {
               slot.set(ItemStack.EMPTY);
            }
         }

         return ItemStack.EMPTY;
      }

      public boolean canTakeItemForPickAll(ItemStack p_94530_1_, Slot p_94530_2_) {
         return p_94530_2_.container != CreativeScreen.CONTAINER;
      }

      public boolean canDragTo(Slot p_94531_1_) {
         return p_94531_1_.container != CreativeScreen.CONTAINER;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class CreativeSlot extends Slot {
      private final Slot target;

      public CreativeSlot(Slot p_i229959_1_, int p_i229959_2_, int p_i229959_3_, int p_i229959_4_) {
         super(p_i229959_1_.container, p_i229959_2_, p_i229959_3_, p_i229959_4_);
         this.target = p_i229959_1_;
      }

      public ItemStack onTake(PlayerEntity p_190901_1_, ItemStack p_190901_2_) {
         return this.target.onTake(p_190901_1_, p_190901_2_);
      }

      public boolean mayPlace(ItemStack p_75214_1_) {
         return this.target.mayPlace(p_75214_1_);
      }

      public ItemStack getItem() {
         return this.target.getItem();
      }

      public boolean hasItem() {
         return this.target.hasItem();
      }

      public void set(ItemStack p_75215_1_) {
         this.target.set(p_75215_1_);
      }

      public void setChanged() {
         this.target.setChanged();
      }

      public int getMaxStackSize() {
         return this.target.getMaxStackSize();
      }

      public int getMaxStackSize(ItemStack p_178170_1_) {
         return this.target.getMaxStackSize(p_178170_1_);
      }

      @Nullable
      public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
         return this.target.getNoItemIcon();
      }

      public ItemStack remove(int p_75209_1_) {
         return this.target.remove(p_75209_1_);
      }

      public boolean isActive() {
         return this.target.isActive();
      }

      public boolean mayPickup(PlayerEntity p_82869_1_) {
         return this.target.mayPickup(p_82869_1_);
      }

      @Override
      public int getSlotIndex() {
         return this.target.getSlotIndex();
      }

      @Override
      public boolean isSameInventory(Slot other) {
         return this.target.isSameInventory(other);
      }

      @Override
      public Slot setBackground(ResourceLocation atlas, ResourceLocation sprite) {
         this.target.setBackground(atlas, sprite);
         return this;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class LockedSlot extends Slot {
      public LockedSlot(IInventory p_i47453_1_, int p_i47453_2_, int p_i47453_3_, int p_i47453_4_) {
         super(p_i47453_1_, p_i47453_2_, p_i47453_3_, p_i47453_4_);
      }

      public boolean mayPickup(PlayerEntity p_82869_1_) {
         if (super.mayPickup(p_82869_1_) && this.hasItem()) {
            return this.getItem().getTagElement("CustomCreativeLock") == null;
         } else {
            return !this.hasItem();
         }
      }
   }
}
