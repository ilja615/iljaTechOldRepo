package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.TextRenderingUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsNarratorHelper;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsSelectWorldTemplateScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation LINK_ICON = new ResourceLocation("realms", "textures/gui/realms/link_icons.png");
   private static final ResourceLocation TRAILER_ICON = new ResourceLocation("realms", "textures/gui/realms/trailer_icons.png");
   private static final ResourceLocation SLOT_FRAME_LOCATION = new ResourceLocation("realms", "textures/gui/realms/slot_frame.png");
   private static final ITextComponent PUBLISHER_LINK_TOOLTIP = new TranslationTextComponent("mco.template.info.tooltip");
   private static final ITextComponent TRAILER_LINK_TOOLTIP = new TranslationTextComponent("mco.template.trailer.tooltip");
   private final NotifableRealmsScreen lastScreen;
   private RealmsSelectWorldTemplateScreen.WorldTemplateSelectionList worldTemplateObjectSelectionList;
   private int selectedTemplate = -1;
   private ITextComponent title;
   private Button selectButton;
   private Button trailerButton;
   private Button publisherButton;
   @Nullable
   private ITextComponent toolTip;
   private String currentLink;
   private final RealmsServer.ServerType worldType;
   private int clicks;
   @Nullable
   private ITextComponent[] warning;
   private String warningURL;
   private boolean displayWarning;
   private boolean hoverWarning;
   @Nullable
   private List<TextRenderingUtils.Line> noTemplatesMessage;

   public RealmsSelectWorldTemplateScreen(NotifableRealmsScreen p_i51752_1_, RealmsServer.ServerType p_i51752_2_) {
      this(p_i51752_1_, p_i51752_2_, (WorldTemplatePaginatedList)null);
   }

   public RealmsSelectWorldTemplateScreen(NotifableRealmsScreen p_i51753_1_, RealmsServer.ServerType p_i51753_2_, @Nullable WorldTemplatePaginatedList p_i51753_3_) {
      this.lastScreen = p_i51753_1_;
      this.worldType = p_i51753_2_;
      if (p_i51753_3_ == null) {
         this.worldTemplateObjectSelectionList = new RealmsSelectWorldTemplateScreen.WorldTemplateSelectionList();
         this.fetchTemplatesAsync(new WorldTemplatePaginatedList(10));
      } else {
         this.worldTemplateObjectSelectionList = new RealmsSelectWorldTemplateScreen.WorldTemplateSelectionList(Lists.newArrayList(p_i51753_3_.templates));
         this.fetchTemplatesAsync(p_i51753_3_);
      }

      this.title = new TranslationTextComponent("mco.template.title");
   }

   public void setTitle(ITextComponent p_238001_1_) {
      this.title = p_238001_1_;
   }

   public void setWarning(ITextComponent... p_238002_1_) {
      this.warning = p_238002_1_;
      this.displayWarning = true;
   }

   public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
      if (this.hoverWarning && this.warningURL != null) {
         Util.getPlatform().openUri("https://www.minecraft.net/realms/adventure-maps-in-1-9");
         return true;
      } else {
         return super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
      }
   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.worldTemplateObjectSelectionList = new RealmsSelectWorldTemplateScreen.WorldTemplateSelectionList(this.worldTemplateObjectSelectionList.getTemplates());
      this.trailerButton = this.addButton(new Button(this.width / 2 - 206, this.height - 32, 100, 20, new TranslationTextComponent("mco.template.button.trailer"), (p_238011_1_) -> {
         this.onTrailer();
      }));
      this.selectButton = this.addButton(new Button(this.width / 2 - 100, this.height - 32, 100, 20, new TranslationTextComponent("mco.template.button.select"), (p_238008_1_) -> {
         this.selectTemplate();
      }));
      ITextComponent itextcomponent = this.worldType == RealmsServer.ServerType.MINIGAME ? DialogTexts.GUI_CANCEL : DialogTexts.GUI_BACK;
      Button button = new Button(this.width / 2 + 6, this.height - 32, 100, 20, itextcomponent, (p_238006_1_) -> {
         this.backButtonClicked();
      });
      this.addButton(button);
      this.publisherButton = this.addButton(new Button(this.width / 2 + 112, this.height - 32, 100, 20, new TranslationTextComponent("mco.template.button.publisher"), (p_238000_1_) -> {
         this.onPublish();
      }));
      this.selectButton.active = false;
      this.trailerButton.visible = false;
      this.publisherButton.visible = false;
      this.addWidget(this.worldTemplateObjectSelectionList);
      this.magicalSpecialHackyFocus(this.worldTemplateObjectSelectionList);
      Stream<ITextComponent> stream = Stream.of(this.title);
      if (this.warning != null) {
         stream = Stream.concat(Stream.of(this.warning), stream);
      }

      RealmsNarratorHelper.now(stream.filter(Objects::nonNull).map(ITextComponent::getString).collect(Collectors.toList()));
   }

   private void updateButtonStates() {
      this.publisherButton.visible = this.shouldPublisherBeVisible();
      this.trailerButton.visible = this.shouldTrailerBeVisible();
      this.selectButton.active = this.shouldSelectButtonBeActive();
   }

   private boolean shouldSelectButtonBeActive() {
      return this.selectedTemplate != -1;
   }

   private boolean shouldPublisherBeVisible() {
      return this.selectedTemplate != -1 && !this.getSelectedTemplate().link.isEmpty();
   }

   private WorldTemplate getSelectedTemplate() {
      return this.worldTemplateObjectSelectionList.get(this.selectedTemplate);
   }

   private boolean shouldTrailerBeVisible() {
      return this.selectedTemplate != -1 && !this.getSelectedTemplate().trailer.isEmpty();
   }

   public void tick() {
      super.tick();
      --this.clicks;
      if (this.clicks < 0) {
         this.clicks = 0;
      }

   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      if (p_231046_1_ == 256) {
         this.backButtonClicked();
         return true;
      } else {
         return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
      }
   }

   private void backButtonClicked() {
      this.lastScreen.callback((WorldTemplate)null);
      this.minecraft.setScreen(this.lastScreen);
   }

   private void selectTemplate() {
      if (this.hasValidTemplate()) {
         this.lastScreen.callback(this.getSelectedTemplate());
      }

   }

   private boolean hasValidTemplate() {
      return this.selectedTemplate >= 0 && this.selectedTemplate < this.worldTemplateObjectSelectionList.getItemCount();
   }

   private void onTrailer() {
      if (this.hasValidTemplate()) {
         WorldTemplate worldtemplate = this.getSelectedTemplate();
         if (!"".equals(worldtemplate.trailer)) {
            Util.getPlatform().openUri(worldtemplate.trailer);
         }
      }

   }

   private void onPublish() {
      if (this.hasValidTemplate()) {
         WorldTemplate worldtemplate = this.getSelectedTemplate();
         if (!"".equals(worldtemplate.link)) {
            Util.getPlatform().openUri(worldtemplate.link);
         }
      }

   }

   private void fetchTemplatesAsync(final WorldTemplatePaginatedList p_224497_1_) {
      (new Thread("realms-template-fetcher") {
         public void run() {
            WorldTemplatePaginatedList worldtemplatepaginatedlist = p_224497_1_;

            RealmsClient realmsclient = RealmsClient.create();
            while (worldtemplatepaginatedlist != null) {
               Either<WorldTemplatePaginatedList, String> either = RealmsSelectWorldTemplateScreen.this.fetchTemplates(worldtemplatepaginatedlist, realmsclient);
            worldtemplatepaginatedlist = RealmsSelectWorldTemplateScreen.this.minecraft.submit(() -> {
               if (either.right().isPresent()) {
                  RealmsSelectWorldTemplateScreen.LOGGER.error("Couldn't fetch templates: {}", either.right().get());
                  if (RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.isEmpty()) {
                     RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose(I18n.get("mco.template.select.failure"));
                  }

                  return null;
               } else {
                  WorldTemplatePaginatedList worldtemplatepaginatedlist1 = either.left().get();

                  for(WorldTemplate worldtemplate : worldtemplatepaginatedlist1.templates) {
                     RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.addEntry(worldtemplate);
                  }

                  if (worldtemplatepaginatedlist1.templates.isEmpty()) {
                     if (RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.isEmpty()) {
                        String s = I18n.get("mco.template.select.none", "%link");
                        TextRenderingUtils.LineSegment textrenderingutils$linesegment = TextRenderingUtils.LineSegment.link(I18n.get("mco.template.select.none.linkTitle"), "https://aka.ms/MinecraftRealmsContentCreator");
                        RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose(s, textrenderingutils$linesegment);
                     }

                     return null;
                  } else {
                     return worldtemplatepaginatedlist1;
                  }
               }
            }).join();
            }
         }
      }).start();
   }

   private Either<WorldTemplatePaginatedList, String> fetchTemplates(WorldTemplatePaginatedList p_224509_1_, RealmsClient p_224509_2_) {
      try {
         return Either.left(p_224509_2_.fetchWorldTemplates(p_224509_1_.page + 1, p_224509_1_.size, this.worldType));
      } catch (RealmsServiceException realmsserviceexception) {
         return Either.right(realmsserviceexception.getMessage());
      }
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.toolTip = null;
      this.currentLink = null;
      this.hoverWarning = false;
      this.renderBackground(p_230430_1_);
      this.worldTemplateObjectSelectionList.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      if (this.noTemplatesMessage != null) {
         this.renderMultilineMessage(p_230430_1_, p_230430_2_, p_230430_3_, this.noTemplatesMessage);
      }

      drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 13, 16777215);
      if (this.displayWarning) {
         ITextComponent[] aitextcomponent = this.warning;

         for(int i = 0; i < aitextcomponent.length; ++i) {
            int j = this.font.width(aitextcomponent[i]);
            int k = this.width / 2 - j / 2;
            int l = row(-1 + i);
            if (p_230430_2_ >= k && p_230430_2_ <= k + j && p_230430_3_ >= l && p_230430_3_ <= l + 9) {
               this.hoverWarning = true;
            }
         }

         for(int i1 = 0; i1 < aitextcomponent.length; ++i1) {
            ITextComponent itextcomponent = aitextcomponent[i1];
            int j1 = 10526880;
            if (this.warningURL != null) {
               if (this.hoverWarning) {
                  j1 = 7107012;
                  itextcomponent = itextcomponent.copy().withStyle(TextFormatting.STRIKETHROUGH);
               } else {
                  j1 = 3368635;
               }
            }

            drawCenteredString(p_230430_1_, this.font, itextcomponent, this.width / 2, row(-1 + i1), j1);
         }
      }

      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      this.renderMousehoverTooltip(p_230430_1_, this.toolTip, p_230430_2_, p_230430_3_);
   }

   private void renderMultilineMessage(MatrixStack p_237992_1_, int p_237992_2_, int p_237992_3_, List<TextRenderingUtils.Line> p_237992_4_) {
      for(int i = 0; i < p_237992_4_.size(); ++i) {
         TextRenderingUtils.Line textrenderingutils$line = p_237992_4_.get(i);
         int j = row(4 + i);
         int k = textrenderingutils$line.segments.stream().mapToInt((p_237999_1_) -> {
            return this.font.width(p_237999_1_.renderedText());
         }).sum();
         int l = this.width / 2 - k / 2;

         for(TextRenderingUtils.LineSegment textrenderingutils$linesegment : textrenderingutils$line.segments) {
            int i1 = textrenderingutils$linesegment.isLink() ? 3368635 : 16777215;
            int j1 = this.font.drawShadow(p_237992_1_, textrenderingutils$linesegment.renderedText(), (float)l, (float)j, i1);
            if (textrenderingutils$linesegment.isLink() && p_237992_2_ > l && p_237992_2_ < j1 && p_237992_3_ > j - 3 && p_237992_3_ < j + 8) {
               this.toolTip = new StringTextComponent(textrenderingutils$linesegment.getLinkUrl());
               this.currentLink = textrenderingutils$linesegment.getLinkUrl();
            }

            l = j1;
         }
      }

   }

   protected void renderMousehoverTooltip(MatrixStack p_237993_1_, @Nullable ITextComponent p_237993_2_, int p_237993_3_, int p_237993_4_) {
      if (p_237993_2_ != null) {
         int i = p_237993_3_ + 12;
         int j = p_237993_4_ - 12;
         int k = this.font.width(p_237993_2_);
         this.fillGradient(p_237993_1_, i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
         this.font.drawShadow(p_237993_1_, p_237993_2_, (float)i, (float)j, 16777215);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class WorldTemplateSelectionEntry extends ExtendedList.AbstractListEntry<RealmsSelectWorldTemplateScreen.WorldTemplateSelectionEntry> {
      private final WorldTemplate template;

      public WorldTemplateSelectionEntry(WorldTemplate p_i51724_2_) {
         this.template = p_i51724_2_;
      }

      public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
         this.renderWorldTemplateItem(p_230432_1_, this.template, p_230432_4_, p_230432_3_, p_230432_7_, p_230432_8_);
      }

      private void renderWorldTemplateItem(MatrixStack p_238029_1_, WorldTemplate p_238029_2_, int p_238029_3_, int p_238029_4_, int p_238029_5_, int p_238029_6_) {
         int i = p_238029_3_ + 45 + 20;
         RealmsSelectWorldTemplateScreen.this.font.draw(p_238029_1_, p_238029_2_.name, (float)i, (float)(p_238029_4_ + 2), 16777215);
         RealmsSelectWorldTemplateScreen.this.font.draw(p_238029_1_, p_238029_2_.author, (float)i, (float)(p_238029_4_ + 15), 7105644);
         RealmsSelectWorldTemplateScreen.this.font.draw(p_238029_1_, p_238029_2_.version, (float)(i + 227 - RealmsSelectWorldTemplateScreen.this.font.width(p_238029_2_.version)), (float)(p_238029_4_ + 1), 7105644);
         if (!"".equals(p_238029_2_.link) || !"".equals(p_238029_2_.trailer) || !"".equals(p_238029_2_.recommendedPlayers)) {
            this.drawIcons(p_238029_1_, i - 1, p_238029_4_ + 25, p_238029_5_, p_238029_6_, p_238029_2_.link, p_238029_2_.trailer, p_238029_2_.recommendedPlayers);
         }

         this.drawImage(p_238029_1_, p_238029_3_, p_238029_4_ + 1, p_238029_5_, p_238029_6_, p_238029_2_);
      }

      private void drawImage(MatrixStack p_238027_1_, int p_238027_2_, int p_238027_3_, int p_238027_4_, int p_238027_5_, WorldTemplate p_238027_6_) {
         RealmsTextureManager.bindWorldTemplate(p_238027_6_.id, p_238027_6_.image);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         AbstractGui.blit(p_238027_1_, p_238027_2_ + 1, p_238027_3_ + 1, 0.0F, 0.0F, 38, 38, 38, 38);
         RealmsSelectWorldTemplateScreen.this.minecraft.getTextureManager().bind(RealmsSelectWorldTemplateScreen.SLOT_FRAME_LOCATION);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         AbstractGui.blit(p_238027_1_, p_238027_2_, p_238027_3_, 0.0F, 0.0F, 40, 40, 40, 40);
      }

      private void drawIcons(MatrixStack p_238028_1_, int p_238028_2_, int p_238028_3_, int p_238028_4_, int p_238028_5_, String p_238028_6_, String p_238028_7_, String p_238028_8_) {
         if (!"".equals(p_238028_8_)) {
            RealmsSelectWorldTemplateScreen.this.font.draw(p_238028_1_, p_238028_8_, (float)p_238028_2_, (float)(p_238028_3_ + 4), 5000268);
         }

         int i = "".equals(p_238028_8_) ? 0 : RealmsSelectWorldTemplateScreen.this.font.width(p_238028_8_) + 2;
         boolean flag = false;
         boolean flag1 = false;
         boolean flag2 = "".equals(p_238028_6_);
         if (p_238028_4_ >= p_238028_2_ + i && p_238028_4_ <= p_238028_2_ + i + 32 && p_238028_5_ >= p_238028_3_ && p_238028_5_ <= p_238028_3_ + 15 && p_238028_5_ < RealmsSelectWorldTemplateScreen.this.height - 15 && p_238028_5_ > 32) {
            if (p_238028_4_ <= p_238028_2_ + 15 + i && p_238028_4_ > i) {
               if (flag2) {
                  flag1 = true;
               } else {
                  flag = true;
               }
            } else if (!flag2) {
               flag1 = true;
            }
         }

         if (!flag2) {
            RealmsSelectWorldTemplateScreen.this.minecraft.getTextureManager().bind(RealmsSelectWorldTemplateScreen.LINK_ICON);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.pushMatrix();
            RenderSystem.scalef(1.0F, 1.0F, 1.0F);
            float f = flag ? 15.0F : 0.0F;
            AbstractGui.blit(p_238028_1_, p_238028_2_ + i, p_238028_3_, f, 0.0F, 15, 15, 30, 15);
            RenderSystem.popMatrix();
         }

         if (!"".equals(p_238028_7_)) {
            RealmsSelectWorldTemplateScreen.this.minecraft.getTextureManager().bind(RealmsSelectWorldTemplateScreen.TRAILER_ICON);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.pushMatrix();
            RenderSystem.scalef(1.0F, 1.0F, 1.0F);
            int j = p_238028_2_ + i + (flag2 ? 0 : 17);
            float f1 = flag1 ? 15.0F : 0.0F;
            AbstractGui.blit(p_238028_1_, j, p_238028_3_, f1, 0.0F, 15, 15, 30, 15);
            RenderSystem.popMatrix();
         }

         if (flag) {
            RealmsSelectWorldTemplateScreen.this.toolTip = RealmsSelectWorldTemplateScreen.PUBLISHER_LINK_TOOLTIP;
            RealmsSelectWorldTemplateScreen.this.currentLink = p_238028_6_;
         } else if (flag1 && !"".equals(p_238028_7_)) {
            RealmsSelectWorldTemplateScreen.this.toolTip = RealmsSelectWorldTemplateScreen.TRAILER_LINK_TOOLTIP;
            RealmsSelectWorldTemplateScreen.this.currentLink = p_238028_7_;
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   class WorldTemplateSelectionList extends RealmsObjectSelectionList<RealmsSelectWorldTemplateScreen.WorldTemplateSelectionEntry> {
      public WorldTemplateSelectionList() {
         this(Collections.emptyList());
      }

      public WorldTemplateSelectionList(Iterable<WorldTemplate> p_i51726_2_) {
         super(RealmsSelectWorldTemplateScreen.this.width, RealmsSelectWorldTemplateScreen.this.height, RealmsSelectWorldTemplateScreen.this.displayWarning ? RealmsSelectWorldTemplateScreen.row(1) : 32, RealmsSelectWorldTemplateScreen.this.height - 40, 46);
         p_i51726_2_.forEach(this::addEntry);
      }

      public void addEntry(WorldTemplate p_223876_1_) {
         this.addEntry(RealmsSelectWorldTemplateScreen.this.new WorldTemplateSelectionEntry(p_223876_1_));
      }

      public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
         if (p_231044_5_ == 0 && p_231044_3_ >= (double)this.y0 && p_231044_3_ <= (double)this.y1) {
            int i = this.width / 2 - 150;
            if (RealmsSelectWorldTemplateScreen.this.currentLink != null) {
               Util.getPlatform().openUri(RealmsSelectWorldTemplateScreen.this.currentLink);
            }

            int j = (int)Math.floor(p_231044_3_ - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
            int k = j / this.itemHeight;
            if (p_231044_1_ >= (double)i && p_231044_1_ < (double)this.getScrollbarPosition() && k >= 0 && j >= 0 && k < this.getItemCount()) {
               this.selectItem(k);
               this.itemClicked(j, k, p_231044_1_, p_231044_3_, this.width);
               if (k >= RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.getItemCount()) {
                  return super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
               }

               RealmsSelectWorldTemplateScreen.this.clicks = RealmsSelectWorldTemplateScreen.this.clicks + 7;
               if (RealmsSelectWorldTemplateScreen.this.clicks >= 10) {
                  RealmsSelectWorldTemplateScreen.this.selectTemplate();
               }

               return true;
            }
         }

         return super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
      }

      public void selectItem(int p_231400_1_) {
         this.setSelectedItem(p_231400_1_);
         if (p_231400_1_ != -1) {
            WorldTemplate worldtemplate = RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.get(p_231400_1_);
            String s = I18n.get("narrator.select.list.position", p_231400_1_ + 1, RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.getItemCount());
            String s1 = I18n.get("mco.template.select.narrate.version", worldtemplate.version);
            String s2 = I18n.get("mco.template.select.narrate.authors", worldtemplate.author);
            String s3 = RealmsNarratorHelper.join(Arrays.asList(worldtemplate.name, s2, worldtemplate.recommendedPlayers, s1, s));
            RealmsNarratorHelper.now(I18n.get("narrator.select", s3));
         }

      }

      public void setSelected(@Nullable RealmsSelectWorldTemplateScreen.WorldTemplateSelectionEntry p_241215_1_) {
         super.setSelected(p_241215_1_);
         RealmsSelectWorldTemplateScreen.this.selectedTemplate = this.children().indexOf(p_241215_1_);
         RealmsSelectWorldTemplateScreen.this.updateButtonStates();
      }

      public int getMaxPosition() {
         return this.getItemCount() * 46;
      }

      public int getRowWidth() {
         return 300;
      }

      public void renderBackground(MatrixStack p_230433_1_) {
         RealmsSelectWorldTemplateScreen.this.renderBackground(p_230433_1_);
      }

      public boolean isFocused() {
         return RealmsSelectWorldTemplateScreen.this.getFocused() == this;
      }

      public boolean isEmpty() {
         return this.getItemCount() == 0;
      }

      public WorldTemplate get(int p_223877_1_) {
         return (this.children().get(p_223877_1_)).template;
      }

      public List<WorldTemplate> getTemplates() {
         return this.children().stream().map((p_223875_0_) -> {
            return p_223875_0_.template;
         }).collect(Collectors.toList());
      }
   }
}
