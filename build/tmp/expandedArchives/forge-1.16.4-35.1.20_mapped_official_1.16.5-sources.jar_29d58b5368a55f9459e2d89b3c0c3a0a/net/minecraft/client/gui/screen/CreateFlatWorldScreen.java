package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreateFlatWorldScreen extends Screen {
   protected final CreateWorldScreen parent;
   private final Consumer<FlatGenerationSettings> applySettings;
   private FlatGenerationSettings generator;
   private ITextComponent columnType;
   private ITextComponent columnHeight;
   private CreateFlatWorldScreen.DetailsList list;
   private Button deleteLayerButton;

   public CreateFlatWorldScreen(CreateWorldScreen p_i242055_1_, Consumer<FlatGenerationSettings> p_i242055_2_, FlatGenerationSettings p_i242055_3_) {
      super(new TranslationTextComponent("createWorld.customize.flat.title"));
      this.parent = p_i242055_1_;
      this.applySettings = p_i242055_2_;
      this.generator = p_i242055_3_;
   }

   public FlatGenerationSettings settings() {
      return this.generator;
   }

   public void setConfig(FlatGenerationSettings p_238602_1_) {
      this.generator = p_238602_1_;
   }

   protected void init() {
      this.columnType = new TranslationTextComponent("createWorld.customize.flat.tile");
      this.columnHeight = new TranslationTextComponent("createWorld.customize.flat.height");
      this.list = new CreateFlatWorldScreen.DetailsList();
      this.children.add(this.list);
      this.deleteLayerButton = this.addButton(new Button(this.width / 2 - 155, this.height - 52, 150, 20, new TranslationTextComponent("createWorld.customize.flat.removeLayer"), (p_213007_1_) -> {
         if (this.hasValidSelection()) {
            List<FlatLayerInfo> list = this.generator.getLayersInfo();
            int i = this.list.children().indexOf(this.list.getSelected());
            int j = list.size() - i - 1;
            list.remove(j);
            this.list.setSelected(list.isEmpty() ? null : this.list.children().get(Math.min(i, list.size() - 1)));
            this.generator.updateLayers();
            this.list.resetRows();
            this.updateButtonValidity();
         }
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 52, 150, 20, new TranslationTextComponent("createWorld.customize.presets"), (p_213011_1_) -> {
         this.minecraft.setScreen(new FlatPresetsScreen(this));
         this.generator.updateLayers();
         this.updateButtonValidity();
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, DialogTexts.GUI_DONE, (p_213010_1_) -> {
         this.applySettings.accept(this.generator);
         this.minecraft.setScreen(this.parent);
         this.generator.updateLayers();
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, DialogTexts.GUI_CANCEL, (p_213009_1_) -> {
         this.minecraft.setScreen(this.parent);
         this.generator.updateLayers();
      }));
      this.generator.updateLayers();
      this.updateButtonValidity();
   }

   private void updateButtonValidity() {
      this.deleteLayerButton.active = this.hasValidSelection();
   }

   private boolean hasValidSelection() {
      return this.list.getSelected() != null;
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      this.list.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 8, 16777215);
      int i = this.width / 2 - 92 - 16;
      drawString(p_230430_1_, this.font, this.columnType, i, 32, 16777215);
      drawString(p_230430_1_, this.font, this.columnHeight, i + 2 + 213 - this.font.width(this.columnHeight), 32, 16777215);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }

   @OnlyIn(Dist.CLIENT)
   class DetailsList extends ExtendedList<CreateFlatWorldScreen.DetailsList.LayerEntry> {
      public DetailsList() {
         super(CreateFlatWorldScreen.this.minecraft, CreateFlatWorldScreen.this.width, CreateFlatWorldScreen.this.height, 43, CreateFlatWorldScreen.this.height - 60, 24);

         for(int i = 0; i < CreateFlatWorldScreen.this.generator.getLayersInfo().size(); ++i) {
            this.addEntry(new CreateFlatWorldScreen.DetailsList.LayerEntry());
         }

      }

      public void setSelected(@Nullable CreateFlatWorldScreen.DetailsList.LayerEntry p_241215_1_) {
         super.setSelected(p_241215_1_);
         if (p_241215_1_ != null) {
            FlatLayerInfo flatlayerinfo = CreateFlatWorldScreen.this.generator.getLayersInfo().get(CreateFlatWorldScreen.this.generator.getLayersInfo().size() - this.children().indexOf(p_241215_1_) - 1);
            Item item = flatlayerinfo.getBlockState().getBlock().asItem();
            if (item != Items.AIR) {
               NarratorChatListener.INSTANCE.sayNow((new TranslationTextComponent("narrator.select", item.getName(new ItemStack(item)))).getString());
            }
         }

         CreateFlatWorldScreen.this.updateButtonValidity();
      }

      protected boolean isFocused() {
         return CreateFlatWorldScreen.this.getFocused() == this;
      }

      protected int getScrollbarPosition() {
         return this.width - 70;
      }

      public void resetRows() {
         int i = this.children().indexOf(this.getSelected());
         this.clearEntries();

         for(int j = 0; j < CreateFlatWorldScreen.this.generator.getLayersInfo().size(); ++j) {
            this.addEntry(new CreateFlatWorldScreen.DetailsList.LayerEntry());
         }

         List<CreateFlatWorldScreen.DetailsList.LayerEntry> list = this.children();
         if (i >= 0 && i < list.size()) {
            this.setSelected(list.get(i));
         }

      }

      @OnlyIn(Dist.CLIENT)
      class LayerEntry extends ExtendedList.AbstractListEntry<CreateFlatWorldScreen.DetailsList.LayerEntry> {
         private LayerEntry() {
         }

         public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
            FlatLayerInfo flatlayerinfo = CreateFlatWorldScreen.this.generator.getLayersInfo().get(CreateFlatWorldScreen.this.generator.getLayersInfo().size() - p_230432_2_ - 1);
            BlockState blockstate = flatlayerinfo.getBlockState();
            Item item = blockstate.getBlock().asItem();
            if (item == Items.AIR) {
               if (blockstate.is(Blocks.WATER)) {
                  item = Items.WATER_BUCKET;
               } else if (blockstate.is(Blocks.LAVA)) {
                  item = Items.LAVA_BUCKET;
               }
            }

            ItemStack itemstack = new ItemStack(item);
            this.blitSlot(p_230432_1_, p_230432_4_, p_230432_3_, itemstack);
            CreateFlatWorldScreen.this.font.draw(p_230432_1_, item.getName(itemstack), (float)(p_230432_4_ + 18 + 5), (float)(p_230432_3_ + 3), 16777215);
            String s;
            if (p_230432_2_ == 0) {
               s = I18n.get("createWorld.customize.flat.layer.top", flatlayerinfo.getHeight());
            } else if (p_230432_2_ == CreateFlatWorldScreen.this.generator.getLayersInfo().size() - 1) {
               s = I18n.get("createWorld.customize.flat.layer.bottom", flatlayerinfo.getHeight());
            } else {
               s = I18n.get("createWorld.customize.flat.layer", flatlayerinfo.getHeight());
            }

            CreateFlatWorldScreen.this.font.draw(p_230432_1_, s, (float)(p_230432_4_ + 2 + 213 - CreateFlatWorldScreen.this.font.width(s)), (float)(p_230432_3_ + 3), 16777215);
         }

         public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
            if (p_231044_5_ == 0) {
               DetailsList.this.setSelected(this);
               return true;
            } else {
               return false;
            }
         }

         private void blitSlot(MatrixStack p_238605_1_, int p_238605_2_, int p_238605_3_, ItemStack p_238605_4_) {
            this.blitSlotBg(p_238605_1_, p_238605_2_ + 1, p_238605_3_ + 1);
            RenderSystem.enableRescaleNormal();
            if (!p_238605_4_.isEmpty()) {
               CreateFlatWorldScreen.this.itemRenderer.renderGuiItem(p_238605_4_, p_238605_2_ + 2, p_238605_3_ + 2);
            }

            RenderSystem.disableRescaleNormal();
         }

         private void blitSlotBg(MatrixStack p_238604_1_, int p_238604_2_, int p_238604_3_) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            DetailsList.this.minecraft.getTextureManager().bind(AbstractGui.STATS_ICON_LOCATION);
            AbstractGui.blit(p_238604_1_, p_238604_2_, p_238604_3_, CreateFlatWorldScreen.this.getBlitOffset(), 0.0F, 0.0F, 18, 18, 128, 128);
         }
      }
   }
}
