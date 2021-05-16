package net.minecraft.client.gui.screen;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.command.Commands;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.FolderPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.ServerPackFinder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.WorldGenSettingsExport;
import net.minecraft.util.registry.WorldSettingsImport;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

@OnlyIn(Dist.CLIENT)
public class WorldOptionsScreen implements IScreen, IRenderable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ITextComponent CUSTOM_WORLD_DESCRIPTION = new TranslationTextComponent("generator.custom");
   private static final ITextComponent AMPLIFIED_HELP_TEXT = new TranslationTextComponent("generator.amplified.info");
   private static final ITextComponent MAP_FEATURES_INFO = new TranslationTextComponent("selectWorld.mapFeatures.info");
   private IBidiRenderer amplifiedWorldInfo = IBidiRenderer.EMPTY;
   private FontRenderer font;
   private int width;
   private TextFieldWidget seedEdit;
   private Button featuresButton;
   public Button bonusItemsButton;
   private Button typeButton;
   private Button customizeTypeButton;
   private Button importSettingsButton;
   private DynamicRegistries.Impl registryHolder;
   private DimensionGeneratorSettings settings;
   private Optional<BiomeGeneratorTypeScreens> preset;
   private OptionalLong seed;

   public WorldOptionsScreen(DynamicRegistries.Impl p_i242065_1_, DimensionGeneratorSettings p_i242065_2_, Optional<BiomeGeneratorTypeScreens> p_i242065_3_, OptionalLong p_i242065_4_) {
      this.registryHolder = p_i242065_1_;
      this.settings = p_i242065_2_;
      this.preset = p_i242065_3_;
      this.seed = p_i242065_4_;
   }

   public void init(final CreateWorldScreen p_239048_1_, Minecraft p_239048_2_, FontRenderer p_239048_3_) {
      this.font = p_239048_3_;
      this.width = p_239048_1_.width;
      this.seedEdit = new TextFieldWidget(this.font, this.width / 2 - 100, 60, 200, 20, new TranslationTextComponent("selectWorld.enterSeed"));
      this.seedEdit.setValue(toString(this.seed));
      this.seedEdit.setResponder((p_239058_1_) -> {
         this.seed = this.parseSeed();
      });
      p_239048_1_.addWidget(this.seedEdit);
      int i = this.width / 2 - 155;
      int j = this.width / 2 + 5;
      this.featuresButton = p_239048_1_.addButton(new Button(i, 100, 150, 20, new TranslationTextComponent("selectWorld.mapFeatures"), (p_239056_1_) -> {
         this.settings = this.settings.withFeaturesToggled();
         p_239056_1_.queueNarration(250);
      }) {
         public ITextComponent getMessage() {
            return DialogTexts.optionStatus(super.getMessage(), WorldOptionsScreen.this.settings.generateFeatures());
         }

         protected IFormattableTextComponent createNarrationMessage() {
            return super.createNarrationMessage().append(". ").append(new TranslationTextComponent("selectWorld.mapFeatures.info"));
         }
      });
      this.featuresButton.visible = false;
      this.typeButton = p_239048_1_.addButton(new Button(j, 100, 150, 20, new TranslationTextComponent("selectWorld.mapType"), (p_239050_2_) -> {
         while(true) {
            if (this.preset.isPresent()) {
               int k = BiomeGeneratorTypeScreens.PRESETS.indexOf(this.preset.get()) + 1;
               if (k >= BiomeGeneratorTypeScreens.PRESETS.size()) {
                  k = 0;
               }

               BiomeGeneratorTypeScreens biomegeneratortypescreens = BiomeGeneratorTypeScreens.PRESETS.get(k);
               this.preset = Optional.of(biomegeneratortypescreens);
               this.settings = biomegeneratortypescreens.create(this.registryHolder, this.settings.seed(), this.settings.generateFeatures(), this.settings.generateBonusChest());
               if (this.settings.isDebug() && !Screen.hasShiftDown()) {
                  continue;
               }
            }

            p_239048_1_.updateDisplayOptions();
            p_239050_2_.queueNarration(250);
            return;
         }
      }) {
         public ITextComponent getMessage() {
            return super.getMessage().copy().append(" ").append(WorldOptionsScreen.this.preset.map(BiomeGeneratorTypeScreens::description).orElse(WorldOptionsScreen.CUSTOM_WORLD_DESCRIPTION));
         }

         protected IFormattableTextComponent createNarrationMessage() {
            return Objects.equals(WorldOptionsScreen.this.preset, Optional.of(BiomeGeneratorTypeScreens.AMPLIFIED)) ? super.createNarrationMessage().append(". ").append(WorldOptionsScreen.AMPLIFIED_HELP_TEXT) : super.createNarrationMessage();
         }
      });
      this.typeButton.visible = false;
      this.typeButton.active = this.preset.isPresent();
      this.customizeTypeButton = p_239048_1_.addButton(new Button(j, 120, 150, 20, new TranslationTextComponent("selectWorld.customizeType"), (p_239044_3_) -> {
         BiomeGeneratorTypeScreens.IFactory biomegeneratortypescreens$ifactory = BiomeGeneratorTypeScreens.EDITORS.get(this.preset);
         biomegeneratortypescreens$ifactory = net.minecraftforge.client.ForgeHooksClient.getBiomeGeneratorTypeScreenFactory(this.preset, biomegeneratortypescreens$ifactory);
         if (biomegeneratortypescreens$ifactory != null) {
            p_239048_2_.setScreen(biomegeneratortypescreens$ifactory.createEditScreen(p_239048_1_, this.settings));
         }

      }));
      this.customizeTypeButton.visible = false;
      this.bonusItemsButton = p_239048_1_.addButton(new Button(i, 151, 150, 20, new TranslationTextComponent("selectWorld.bonusItems"), (p_239047_1_) -> {
         this.settings = this.settings.withBonusChestToggled();
         p_239047_1_.queueNarration(250);
      }) {
         public ITextComponent getMessage() {
            return DialogTexts.optionStatus(super.getMessage(), WorldOptionsScreen.this.settings.generateBonusChest() && !p_239048_1_.hardCore);
         }
      });
      this.bonusItemsButton.visible = false;
      this.importSettingsButton = p_239048_1_.addButton(new Button(i, 185, 150, 20, new TranslationTextComponent("selectWorld.import_worldgen_settings"), (p_239049_3_) -> {
         TranslationTextComponent translationtextcomponent = new TranslationTextComponent("selectWorld.import_worldgen_settings.select_file");
         String s = TinyFileDialogs.tinyfd_openFileDialog(translationtextcomponent.getString(), (CharSequence)null, (PointerBuffer)null, (CharSequence)null, false);
         if (s != null) {
            DynamicRegistries.Impl dynamicregistries$impl = DynamicRegistries.builtin();
            ResourcePackList resourcepacklist = new ResourcePackList(new ServerPackFinder(), new FolderPackFinder(p_239048_1_.getTempDataPackDir().toFile(), IPackNameDecorator.WORLD));

            DataPackRegistries datapackregistries;
            try {
               MinecraftServer.configurePackRepository(resourcepacklist, p_239048_1_.dataPacks, false);
               CompletableFuture<DataPackRegistries> completablefuture = DataPackRegistries.loadResources(resourcepacklist.openAllSelected(), Commands.EnvironmentType.INTEGRATED, 2, Util.backgroundExecutor(), p_239048_2_);
               p_239048_2_.managedBlock(completablefuture::isDone);
               datapackregistries = completablefuture.get();
            } catch (ExecutionException | InterruptedException interruptedexception) {
               LOGGER.error("Error loading data packs when importing world settings", (Throwable)interruptedexception);
               ITextComponent itextcomponent = new TranslationTextComponent("selectWorld.import_worldgen_settings.failure");
               ITextComponent itextcomponent1 = new StringTextComponent(interruptedexception.getMessage());
               p_239048_2_.getToasts().addToast(SystemToast.multiline(p_239048_2_, SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER, itextcomponent, itextcomponent1));
               resourcepacklist.close();
               return;
            }

            WorldSettingsImport<JsonElement> worldsettingsimport = WorldSettingsImport.create(JsonOps.INSTANCE, datapackregistries.getResourceManager(), dynamicregistries$impl);
            JsonParser jsonparser = new JsonParser();

            DataResult<DimensionGeneratorSettings> dataresult;
            try (BufferedReader bufferedreader = Files.newBufferedReader(Paths.get(s))) {
               JsonElement jsonelement = jsonparser.parse(bufferedreader);
               dataresult = DimensionGeneratorSettings.CODEC.parse(worldsettingsimport, jsonelement);
            } catch (JsonIOException | JsonSyntaxException | IOException ioexception) {
               dataresult = DataResult.error("Failed to parse file: " + ioexception.getMessage());
            }

            if (dataresult.error().isPresent()) {
               ITextComponent itextcomponent2 = new TranslationTextComponent("selectWorld.import_worldgen_settings.failure");
               String s1 = dataresult.error().get().message();
               LOGGER.error("Error parsing world settings: {}", (Object)s1);
               ITextComponent itextcomponent3 = new StringTextComponent(s1);
               p_239048_2_.getToasts().addToast(SystemToast.multiline(p_239048_2_, SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER, itextcomponent2, itextcomponent3));
            }

            datapackregistries.close();
            Lifecycle lifecycle = dataresult.lifecycle();
            dataresult.resultOrPartial(LOGGER::error).ifPresent((p_239046_5_) -> {
               BooleanConsumer booleanconsumer = (p_239045_5_) -> {
                  p_239048_2_.setScreen(p_239048_1_);
                  if (p_239045_5_) {
                     this.importSettings(dynamicregistries$impl, p_239046_5_);
                  }

               };
               if (lifecycle == Lifecycle.stable()) {
                  this.importSettings(dynamicregistries$impl, p_239046_5_);
               } else if (lifecycle == Lifecycle.experimental()) {
                  p_239048_2_.setScreen(new ConfirmScreen(booleanconsumer, new TranslationTextComponent("selectWorld.import_worldgen_settings.experimental.title"), new TranslationTextComponent("selectWorld.import_worldgen_settings.experimental.question")));
               } else {
                  p_239048_2_.setScreen(new ConfirmScreen(booleanconsumer, new TranslationTextComponent("selectWorld.import_worldgen_settings.deprecated.title"), new TranslationTextComponent("selectWorld.import_worldgen_settings.deprecated.question")));
               }

            });
         }
      }));
      this.importSettingsButton.visible = false;
      this.amplifiedWorldInfo = IBidiRenderer.create(p_239048_3_, AMPLIFIED_HELP_TEXT, this.typeButton.getWidth());
   }

   private void importSettings(DynamicRegistries.Impl p_239052_1_, DimensionGeneratorSettings p_239052_2_) {
      this.registryHolder = p_239052_1_;
      this.settings = p_239052_2_;
      this.preset = BiomeGeneratorTypeScreens.of(p_239052_2_);
      this.seed = OptionalLong.of(p_239052_2_.seed());
      this.seedEdit.setValue(toString(this.seed));
      this.typeButton.active = this.preset.isPresent();
   }

   public void tick() {
      this.seedEdit.tick();
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      if (this.featuresButton.visible) {
         this.font.drawShadow(p_230430_1_, MAP_FEATURES_INFO, (float)(this.width / 2 - 150), 122.0F, -6250336);
      }

      this.seedEdit.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      if (this.preset.equals(Optional.of(BiomeGeneratorTypeScreens.AMPLIFIED))) {
         this.amplifiedWorldInfo.renderLeftAligned(p_230430_1_, this.typeButton.x + 2, this.typeButton.y + 22, 9, 10526880);
      }

   }

   public void updateSettings(DimensionGeneratorSettings p_239043_1_) {
      this.settings = p_239043_1_;
   }

   private static String toString(OptionalLong p_243445_0_) {
      return p_243445_0_.isPresent() ? Long.toString(p_243445_0_.getAsLong()) : "";
   }

   private static OptionalLong parseLong(String p_239053_0_) {
      try {
         return OptionalLong.of(Long.parseLong(p_239053_0_));
      } catch (NumberFormatException numberformatexception) {
         return OptionalLong.empty();
      }
   }

   public DimensionGeneratorSettings makeSettings(boolean p_239054_1_) {
      OptionalLong optionallong = this.parseSeed();
      return this.settings.withSeed(p_239054_1_, optionallong);
   }

   private OptionalLong parseSeed() {
      String s = this.seedEdit.getValue();
      OptionalLong optionallong;
      if (StringUtils.isEmpty(s)) {
         optionallong = OptionalLong.empty();
      } else {
         OptionalLong optionallong1 = parseLong(s);
         if (optionallong1.isPresent() && optionallong1.getAsLong() != 0L) {
            optionallong = optionallong1;
         } else {
            optionallong = OptionalLong.of((long)s.hashCode());
         }
      }

      return optionallong;
   }

   public boolean isDebug() {
      return this.settings.isDebug();
   }

   public void setDisplayOptions(boolean p_239059_1_) {
      this.typeButton.visible = p_239059_1_;
      if (this.settings.isDebug()) {
         this.featuresButton.visible = false;
         this.bonusItemsButton.visible = false;
         this.customizeTypeButton.visible = false;
         this.importSettingsButton.visible = false;
      } else {
         this.featuresButton.visible = p_239059_1_;
         this.bonusItemsButton.visible = p_239059_1_;
         this.customizeTypeButton.visible = p_239059_1_ && (BiomeGeneratorTypeScreens.EDITORS.containsKey(this.preset) || net.minecraftforge.client.ForgeHooksClient.hasBiomeGeneratorSettingsOptionsScreen(this.preset));
         this.importSettingsButton.visible = p_239059_1_;
      }

      this.seedEdit.setVisible(p_239059_1_);
   }

   public DynamicRegistries.Impl registryHolder() {
      return this.registryHolder;
   }

   void updateDataPacks(DataPackRegistries p_243447_1_) {
      DynamicRegistries.Impl dynamicregistries$impl = DynamicRegistries.builtin();
      WorldGenSettingsExport<JsonElement> worldgensettingsexport = WorldGenSettingsExport.create(JsonOps.INSTANCE, this.registryHolder);
      WorldSettingsImport<JsonElement> worldsettingsimport = WorldSettingsImport.create(JsonOps.INSTANCE, p_243447_1_.getResourceManager(), dynamicregistries$impl);
      DataResult<DimensionGeneratorSettings> dataresult = DimensionGeneratorSettings.CODEC.encodeStart(worldgensettingsexport, this.settings).flatMap((p_243446_1_) -> {
         return DimensionGeneratorSettings.CODEC.parse(worldsettingsimport, p_243446_1_);
      });
      dataresult.resultOrPartial(Util.prefix("Error parsing worldgen settings after loading data packs: ", LOGGER::error)).ifPresent((p_243448_2_) -> {
         this.settings = p_243448_2_;
         this.registryHolder = dynamicregistries$impl;
      });
   }
}
