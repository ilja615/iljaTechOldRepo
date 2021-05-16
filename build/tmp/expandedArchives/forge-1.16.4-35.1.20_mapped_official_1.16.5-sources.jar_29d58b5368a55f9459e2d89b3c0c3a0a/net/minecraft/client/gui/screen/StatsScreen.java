package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StatsScreen extends Screen implements IProgressMeter {
   private static final ITextComponent PENDING_TEXT = new TranslationTextComponent("multiplayer.downloadingStats");
   protected final Screen lastScreen;
   private StatsScreen.CustomStatsList statsList;
   private StatsScreen.StatsList itemStatsList;
   private StatsScreen.MobStatsList mobsStatsList;
   private final StatisticsManager stats;
   @Nullable
   private ExtendedList<?> activeList;
   private boolean isLoading = true;

   public StatsScreen(Screen p_i1071_1_, StatisticsManager p_i1071_2_) {
      super(new TranslationTextComponent("gui.stats"));
      this.lastScreen = p_i1071_1_;
      this.stats = p_i1071_2_;
   }

   protected void init() {
      this.isLoading = true;
      this.minecraft.getConnection().send(new CClientStatusPacket(CClientStatusPacket.State.REQUEST_STATS));
   }

   public void initLists() {
      this.statsList = new StatsScreen.CustomStatsList(this.minecraft);
      this.itemStatsList = new StatsScreen.StatsList(this.minecraft);
      this.mobsStatsList = new StatsScreen.MobStatsList(this.minecraft);
   }

   public void initButtons() {
      this.addButton(new Button(this.width / 2 - 120, this.height - 52, 80, 20, new TranslationTextComponent("stat.generalButton"), (p_213109_1_) -> {
         this.setActiveList(this.statsList);
      }));
      Button button = this.addButton(new Button(this.width / 2 - 40, this.height - 52, 80, 20, new TranslationTextComponent("stat.itemsButton"), (p_213115_1_) -> {
         this.setActiveList(this.itemStatsList);
      }));
      Button button1 = this.addButton(new Button(this.width / 2 + 40, this.height - 52, 80, 20, new TranslationTextComponent("stat.mobsButton"), (p_213114_1_) -> {
         this.setActiveList(this.mobsStatsList);
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height - 28, 200, 20, DialogTexts.GUI_DONE, (p_213113_1_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      if (this.itemStatsList.children().isEmpty()) {
         button.active = false;
      }

      if (this.mobsStatsList.children().isEmpty()) {
         button1.active = false;
      }

   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      if (this.isLoading) {
         this.renderBackground(p_230430_1_);
         drawCenteredString(p_230430_1_, this.font, PENDING_TEXT, this.width / 2, this.height / 2, 16777215);
         drawCenteredString(p_230430_1_, this.font, LOADING_SYMBOLS[(int)(Util.getMillis() / 150L % (long)LOADING_SYMBOLS.length)], this.width / 2, this.height / 2 + 9 * 2, 16777215);
      } else {
         this.getActiveList().render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
         drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 20, 16777215);
         super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      }

   }

   public void onStatsUpdated() {
      if (this.isLoading) {
         this.initLists();
         this.initButtons();
         this.setActiveList(this.statsList);
         this.isLoading = false;
      }

   }

   public boolean isPauseScreen() {
      return !this.isLoading;
   }

   @Nullable
   public ExtendedList<?> getActiveList() {
      return this.activeList;
   }

   public void setActiveList(@Nullable ExtendedList<?> p_213110_1_) {
      this.children.remove(this.statsList);
      this.children.remove(this.itemStatsList);
      this.children.remove(this.mobsStatsList);
      if (p_213110_1_ != null) {
         this.children.add(0, p_213110_1_);
         this.activeList = p_213110_1_;
      }

   }

   private static String getTranslationKey(Stat<ResourceLocation> p_238672_0_) {
      return "stat." + p_238672_0_.getValue().toString().replace(':', '.');
   }

   private int getColumnX(int p_195224_1_) {
      return 115 + 40 * p_195224_1_;
   }

   private void blitSlot(MatrixStack p_238667_1_, int p_238667_2_, int p_238667_3_, Item p_238667_4_) {
      this.blitSlotIcon(p_238667_1_, p_238667_2_ + 1, p_238667_3_ + 1, 0, 0);
      RenderSystem.enableRescaleNormal();
      this.itemRenderer.renderGuiItem(p_238667_4_.getDefaultInstance(), p_238667_2_ + 2, p_238667_3_ + 2);
      RenderSystem.disableRescaleNormal();
   }

   private void blitSlotIcon(MatrixStack p_238674_1_, int p_238674_2_, int p_238674_3_, int p_238674_4_, int p_238674_5_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(STATS_ICON_LOCATION);
      blit(p_238674_1_, p_238674_2_, p_238674_3_, this.getBlitOffset(), (float)p_238674_4_, (float)p_238674_5_, 18, 18, 128, 128);
   }

   @OnlyIn(Dist.CLIENT)
   class CustomStatsList extends ExtendedList<StatsScreen.CustomStatsList.Entry> {
      public CustomStatsList(Minecraft p_i47553_2_) {
         super(p_i47553_2_, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 10);
         ObjectArrayList<Stat<ResourceLocation>> objectarraylist = new ObjectArrayList<>(Stats.CUSTOM.iterator());
         objectarraylist.sort(java.util.Comparator.comparing((p_238679_0_) -> {
            return I18n.get(StatsScreen.getTranslationKey(p_238679_0_));
         }));

         for(Stat<ResourceLocation> stat : objectarraylist) {
            this.addEntry(new StatsScreen.CustomStatsList.Entry(stat));
         }

      }

      protected void renderBackground(MatrixStack p_230433_1_) {
         StatsScreen.this.renderBackground(p_230433_1_);
      }

      @OnlyIn(Dist.CLIENT)
      class Entry extends ExtendedList.AbstractListEntry<StatsScreen.CustomStatsList.Entry> {
         private final Stat<ResourceLocation> stat;
         private final ITextComponent statDisplay;

         private Entry(Stat<ResourceLocation> p_i50466_2_) {
            this.stat = p_i50466_2_;
            this.statDisplay = new TranslationTextComponent(StatsScreen.getTranslationKey(p_i50466_2_));
         }

         public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
            AbstractGui.drawString(p_230432_1_, StatsScreen.this.font, this.statDisplay, p_230432_4_ + 2, p_230432_3_ + 1, p_230432_2_ % 2 == 0 ? 16777215 : 9474192);
            String s = this.stat.format(StatsScreen.this.stats.getValue(this.stat));
            AbstractGui.drawString(p_230432_1_, StatsScreen.this.font, s, p_230432_4_ + 2 + 213 - StatsScreen.this.font.width(s), p_230432_3_ + 1, p_230432_2_ % 2 == 0 ? 16777215 : 9474192);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   class MobStatsList extends ExtendedList<StatsScreen.MobStatsList.Entry> {
      public MobStatsList(Minecraft p_i47551_2_) {
         super(p_i47551_2_, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 9 * 4);

         for(EntityType<?> entitytype : Registry.ENTITY_TYPE) {
            if (StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(entitytype)) > 0 || StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(entitytype)) > 0) {
               this.addEntry(new StatsScreen.MobStatsList.Entry(entitytype));
            }
         }

      }

      protected void renderBackground(MatrixStack p_230433_1_) {
         StatsScreen.this.renderBackground(p_230433_1_);
      }

      @OnlyIn(Dist.CLIENT)
      class Entry extends ExtendedList.AbstractListEntry<StatsScreen.MobStatsList.Entry> {
         private final EntityType<?> type;
         private final ITextComponent mobName;
         private final ITextComponent kills;
         private final boolean hasKills;
         private final ITextComponent killedBy;
         private final boolean wasKilledBy;

         public Entry(EntityType<?> p_i50018_2_) {
            this.type = p_i50018_2_;
            this.mobName = p_i50018_2_.getDescription();
            int i = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(p_i50018_2_));
            if (i == 0) {
               this.kills = new TranslationTextComponent("stat_type.minecraft.killed.none", this.mobName);
               this.hasKills = false;
            } else {
               this.kills = new TranslationTextComponent("stat_type.minecraft.killed", i, this.mobName);
               this.hasKills = true;
            }

            int j = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(p_i50018_2_));
            if (j == 0) {
               this.killedBy = new TranslationTextComponent("stat_type.minecraft.killed_by.none", this.mobName);
               this.wasKilledBy = false;
            } else {
               this.killedBy = new TranslationTextComponent("stat_type.minecraft.killed_by", this.mobName, j);
               this.wasKilledBy = true;
            }

         }

         public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
            AbstractGui.drawString(p_230432_1_, StatsScreen.this.font, this.mobName, p_230432_4_ + 2, p_230432_3_ + 1, 16777215);
            AbstractGui.drawString(p_230432_1_, StatsScreen.this.font, this.kills, p_230432_4_ + 2 + 10, p_230432_3_ + 1 + 9, this.hasKills ? 9474192 : 6316128);
            AbstractGui.drawString(p_230432_1_, StatsScreen.this.font, this.killedBy, p_230432_4_ + 2 + 10, p_230432_3_ + 1 + 9 * 2, this.wasKilledBy ? 9474192 : 6316128);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   class StatsList extends ExtendedList<StatsScreen.StatsList.Entry> {
      protected final List<StatType<Block>> blockColumns;
      protected final List<StatType<Item>> itemColumns;
      private final int[] iconOffsets = new int[]{3, 4, 1, 2, 5, 6};
      protected int headerPressed = -1;
      protected final List<Item> statItemList;
      protected final java.util.Comparator<Item> itemStatSorter = new StatsScreen.StatsList.Comparator();
      @Nullable
      protected StatType<?> sortColumn;
      protected int sortOrder;

      public StatsList(Minecraft p_i47552_2_) {
         super(p_i47552_2_, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 20);
         this.blockColumns = Lists.newArrayList();
         this.blockColumns.add(Stats.BLOCK_MINED);
         this.itemColumns = Lists.newArrayList(Stats.ITEM_BROKEN, Stats.ITEM_CRAFTED, Stats.ITEM_USED, Stats.ITEM_PICKED_UP, Stats.ITEM_DROPPED);
         this.setRenderHeader(true, 20);
         Set<Item> set = Sets.newIdentityHashSet();

         for(Item item : Registry.ITEM) {
            boolean flag = false;

            for(StatType<Item> stattype : this.itemColumns) {
               if (stattype.contains(item) && StatsScreen.this.stats.getValue(stattype.get(item)) > 0) {
                  flag = true;
               }
            }

            if (flag) {
               set.add(item);
            }
         }

         for(Block block : Registry.BLOCK) {
            boolean flag1 = false;

            for(StatType<Block> stattype1 : this.blockColumns) {
               if (stattype1.contains(block) && StatsScreen.this.stats.getValue(stattype1.get(block)) > 0) {
                  flag1 = true;
               }
            }

            if (flag1) {
               set.add(block.asItem());
            }
         }

         set.remove(Items.AIR);
         this.statItemList = Lists.newArrayList(set);

         for(int i = 0; i < this.statItemList.size(); ++i) {
            this.addEntry(new StatsScreen.StatsList.Entry());
         }

      }

      protected void renderHeader(MatrixStack p_230448_1_, int p_230448_2_, int p_230448_3_, Tessellator p_230448_4_) {
         if (!this.minecraft.mouseHandler.isLeftPressed()) {
            this.headerPressed = -1;
         }

         for(int i = 0; i < this.iconOffsets.length; ++i) {
            StatsScreen.this.blitSlotIcon(p_230448_1_, p_230448_2_ + StatsScreen.this.getColumnX(i) - 18, p_230448_3_ + 1, 0, this.headerPressed == i ? 0 : 18);
         }

         if (this.sortColumn != null) {
            int k = StatsScreen.this.getColumnX(this.getColumnIndex(this.sortColumn)) - 36;
            int j = this.sortOrder == 1 ? 2 : 1;
            StatsScreen.this.blitSlotIcon(p_230448_1_, p_230448_2_ + k, p_230448_3_ + 1, 18 * j, 0);
         }

         for(int l = 0; l < this.iconOffsets.length; ++l) {
            int i1 = this.headerPressed == l ? 1 : 0;
            StatsScreen.this.blitSlotIcon(p_230448_1_, p_230448_2_ + StatsScreen.this.getColumnX(l) - 18 + i1, p_230448_3_ + 1 + i1, 18 * this.iconOffsets[l], 18);
         }

      }

      public int getRowWidth() {
         return 375;
      }

      protected int getScrollbarPosition() {
         return this.width / 2 + 140;
      }

      protected void renderBackground(MatrixStack p_230433_1_) {
         StatsScreen.this.renderBackground(p_230433_1_);
      }

      protected void clickedHeader(int p_230938_1_, int p_230938_2_) {
         this.headerPressed = -1;

         for(int i = 0; i < this.iconOffsets.length; ++i) {
            int j = p_230938_1_ - StatsScreen.this.getColumnX(i);
            if (j >= -36 && j <= 0) {
               this.headerPressed = i;
               break;
            }
         }

         if (this.headerPressed >= 0) {
            this.sortByColumn(this.getColumn(this.headerPressed));
            this.minecraft.getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         }

      }

      private StatType<?> getColumn(int p_195108_1_) {
         return p_195108_1_ < this.blockColumns.size() ? this.blockColumns.get(p_195108_1_) : this.itemColumns.get(p_195108_1_ - this.blockColumns.size());
      }

      private int getColumnIndex(StatType<?> p_195105_1_) {
         int i = this.blockColumns.indexOf(p_195105_1_);
         if (i >= 0) {
            return i;
         } else {
            int j = this.itemColumns.indexOf(p_195105_1_);
            return j >= 0 ? j + this.blockColumns.size() : -1;
         }
      }

      protected void renderDecorations(MatrixStack p_230447_1_, int p_230447_2_, int p_230447_3_) {
         if (p_230447_3_ >= this.y0 && p_230447_3_ <= this.y1) {
            StatsScreen.StatsList.Entry statsscreen$statslist$entry = this.getEntryAtPosition((double)p_230447_2_, (double)p_230447_3_);
            int i = (this.width - this.getRowWidth()) / 2;
            if (statsscreen$statslist$entry != null) {
               if (p_230447_2_ < i + 40 || p_230447_2_ > i + 40 + 20) {
                  return;
               }

               Item item = this.statItemList.get(this.children().indexOf(statsscreen$statslist$entry));
               this.renderMousehoverTooltip(p_230447_1_, this.getString(item), p_230447_2_, p_230447_3_);
            } else {
               ITextComponent itextcomponent = null;
               int j = p_230447_2_ - i;

               for(int k = 0; k < this.iconOffsets.length; ++k) {
                  int l = StatsScreen.this.getColumnX(k);
                  if (j >= l - 18 && j <= l) {
                     itextcomponent = this.getColumn(k).getDisplayName();
                     break;
                  }
               }

               this.renderMousehoverTooltip(p_230447_1_, itextcomponent, p_230447_2_, p_230447_3_);
            }

         }
      }

      protected void renderMousehoverTooltip(MatrixStack p_238680_1_, @Nullable ITextComponent p_238680_2_, int p_238680_3_, int p_238680_4_) {
         if (p_238680_2_ != null) {
            int i = p_238680_3_ + 12;
            int j = p_238680_4_ - 12;
            int k = StatsScreen.this.font.width(p_238680_2_);
            this.fillGradient(p_238680_1_, i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0.0F, 0.0F, 400.0F);
            StatsScreen.this.font.drawShadow(p_238680_1_, p_238680_2_, (float)i, (float)j, -1);
            RenderSystem.popMatrix();
         }
      }

      protected ITextComponent getString(Item p_200208_1_) {
         return p_200208_1_.getDescription();
      }

      protected void sortByColumn(StatType<?> p_195107_1_) {
         if (p_195107_1_ != this.sortColumn) {
            this.sortColumn = p_195107_1_;
            this.sortOrder = -1;
         } else if (this.sortOrder == -1) {
            this.sortOrder = 1;
         } else {
            this.sortColumn = null;
            this.sortOrder = 0;
         }

         this.statItemList.sort(this.itemStatSorter);
      }

      @OnlyIn(Dist.CLIENT)
      class Comparator implements java.util.Comparator<Item> {
         private Comparator() {
         }

         public int compare(Item p_compare_1_, Item p_compare_2_) {
            int i;
            int j;
            if (StatsList.this.sortColumn == null) {
               i = 0;
               j = 0;
            } else if (StatsList.this.blockColumns.contains(StatsList.this.sortColumn)) {
               StatType<Block> stattype = (StatType<Block>)StatsList.this.sortColumn;
               i = p_compare_1_ instanceof BlockItem ? StatsScreen.this.stats.getValue(stattype, ((BlockItem)p_compare_1_).getBlock()) : -1;
               j = p_compare_2_ instanceof BlockItem ? StatsScreen.this.stats.getValue(stattype, ((BlockItem)p_compare_2_).getBlock()) : -1;
            } else {
               StatType<Item> stattype1 = (StatType<Item>)StatsList.this.sortColumn;
               i = StatsScreen.this.stats.getValue(stattype1, p_compare_1_);
               j = StatsScreen.this.stats.getValue(stattype1, p_compare_2_);
            }

            return i == j ? StatsList.this.sortOrder * Integer.compare(Item.getId(p_compare_1_), Item.getId(p_compare_2_)) : StatsList.this.sortOrder * Integer.compare(i, j);
         }
      }

      @OnlyIn(Dist.CLIENT)
      class Entry extends ExtendedList.AbstractListEntry<StatsScreen.StatsList.Entry> {
         private Entry() {
         }

         public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
            Item item = StatsScreen.this.itemStatsList.statItemList.get(p_230432_2_);
            StatsScreen.this.blitSlot(p_230432_1_, p_230432_4_ + 40, p_230432_3_, item);

            for(int i = 0; i < StatsScreen.this.itemStatsList.blockColumns.size(); ++i) {
               Stat<Block> stat;
               if (item instanceof BlockItem) {
                  stat = StatsScreen.this.itemStatsList.blockColumns.get(i).get(((BlockItem)item).getBlock());
               } else {
                  stat = null;
               }

               this.renderStat(p_230432_1_, stat, p_230432_4_ + StatsScreen.this.getColumnX(i), p_230432_3_, p_230432_2_ % 2 == 0);
            }

            for(int j = 0; j < StatsScreen.this.itemStatsList.itemColumns.size(); ++j) {
               this.renderStat(p_230432_1_, StatsScreen.this.itemStatsList.itemColumns.get(j).get(item), p_230432_4_ + StatsScreen.this.getColumnX(j + StatsScreen.this.itemStatsList.blockColumns.size()), p_230432_3_, p_230432_2_ % 2 == 0);
            }

         }

         protected void renderStat(MatrixStack p_238681_1_, @Nullable Stat<?> p_238681_2_, int p_238681_3_, int p_238681_4_, boolean p_238681_5_) {
            String s = p_238681_2_ == null ? "-" : p_238681_2_.format(StatsScreen.this.stats.getValue(p_238681_2_));
            AbstractGui.drawString(p_238681_1_, StatsScreen.this.font, s, p_238681_3_ - StatsScreen.this.font.width(s), p_238681_4_ + 5, p_238681_5_ ? 16777215 : 9474192);
         }
      }
   }
}
