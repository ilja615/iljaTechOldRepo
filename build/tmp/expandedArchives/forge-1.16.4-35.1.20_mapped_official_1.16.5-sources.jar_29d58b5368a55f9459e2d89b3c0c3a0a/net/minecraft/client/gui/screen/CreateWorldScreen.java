package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.Commands;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.FolderPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.ServerPackFinder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FileUtil;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.FolderName;
import net.minecraft.world.storage.SaveFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class CreateWorldScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ITextComponent GAME_MODEL_LABEL = new TranslationTextComponent("selectWorld.gameMode");
   private static final ITextComponent SEED_LABEL = new TranslationTextComponent("selectWorld.enterSeed");
   private static final ITextComponent SEED_INFO = new TranslationTextComponent("selectWorld.seedInfo");
   private static final ITextComponent NAME_LABEL = new TranslationTextComponent("selectWorld.enterName");
   private static final ITextComponent OUTPUT_DIR_INFO = new TranslationTextComponent("selectWorld.resultFolder");
   private static final ITextComponent COMMANDS_INFO = new TranslationTextComponent("selectWorld.allowCommands.info");
   private final Screen lastScreen;
   private TextFieldWidget nameEdit;
   private String resultFolder;
   private CreateWorldScreen.GameMode gameMode = CreateWorldScreen.GameMode.SURVIVAL;
   @Nullable
   private CreateWorldScreen.GameMode oldGameMode;
   private Difficulty selectedDifficulty = Difficulty.NORMAL;
   private Difficulty effectiveDifficulty = Difficulty.NORMAL;
   private boolean commands;
   private boolean commandsChanged;
   public boolean hardCore;
   protected DatapackCodec dataPacks;
   @Nullable
   private Path tempDataPackDir;
   @Nullable
   private ResourcePackList tempDataPackRepository;
   private boolean displayOptions;
   private Button createButton;
   private Button modeButton;
   private Button difficultyButton;
   private Button moreOptionsButton;
   private Button gameRulesButton;
   private Button dataPacksButton;
   private Button commandsButton;
   private ITextComponent gameModeHelp1;
   private ITextComponent gameModeHelp2;
   private String initName;
   private GameRules gameRules = new GameRules();
   public final WorldOptionsScreen worldGenSettingsComponent;

   public CreateWorldScreen(@Nullable Screen p_i242064_1_, WorldSettings p_i242064_2_, DimensionGeneratorSettings p_i242064_3_, @Nullable Path p_i242064_4_, DatapackCodec p_i242064_5_, DynamicRegistries.Impl p_i242064_6_) {
      this(p_i242064_1_, p_i242064_5_, new WorldOptionsScreen(p_i242064_6_, p_i242064_3_, BiomeGeneratorTypeScreens.of(p_i242064_3_), OptionalLong.of(p_i242064_3_.seed())));
      this.initName = p_i242064_2_.levelName();
      this.commands = p_i242064_2_.allowCommands();
      this.commandsChanged = true;
      this.selectedDifficulty = p_i242064_2_.difficulty();
      this.effectiveDifficulty = this.selectedDifficulty;
      this.gameRules.assignFrom(p_i242064_2_.gameRules(), (MinecraftServer)null);
      if (p_i242064_2_.hardcore()) {
         this.gameMode = CreateWorldScreen.GameMode.HARDCORE;
      } else if (p_i242064_2_.gameType().isSurvival()) {
         this.gameMode = CreateWorldScreen.GameMode.SURVIVAL;
      } else if (p_i242064_2_.gameType().isCreative()) {
         this.gameMode = CreateWorldScreen.GameMode.CREATIVE;
      }

      this.tempDataPackDir = p_i242064_4_;
   }

   public static CreateWorldScreen create(@Nullable Screen p_243425_0_) {
      DynamicRegistries.Impl dynamicregistries$impl = DynamicRegistries.builtin();
      return new CreateWorldScreen(p_243425_0_, DatapackCodec.DEFAULT, new WorldOptionsScreen(dynamicregistries$impl, net.minecraftforge.client.ForgeHooksClient.getDefaultWorldType().map(type -> type.create(dynamicregistries$impl, new java.util.Random().nextLong(), true, false)).orElseGet(() -> DimensionGeneratorSettings.makeDefault(dynamicregistries$impl.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY), dynamicregistries$impl.registryOrThrow(Registry.BIOME_REGISTRY), dynamicregistries$impl.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY))), net.minecraftforge.client.ForgeHooksClient.getDefaultWorldType(), OptionalLong.empty()));
   }

   private CreateWorldScreen(@Nullable Screen p_i242063_1_, DatapackCodec p_i242063_2_, WorldOptionsScreen p_i242063_3_) {
      super(new TranslationTextComponent("selectWorld.create"));
      this.lastScreen = p_i242063_1_;
      this.initName = I18n.get("selectWorld.newWorld");
      this.dataPacks = p_i242063_2_;
      this.worldGenSettingsComponent = p_i242063_3_;
   }

   public void tick() {
      this.nameEdit.tick();
      this.worldGenSettingsComponent.tick();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.nameEdit = new TextFieldWidget(this.font, this.width / 2 - 100, 60, 200, 20, new TranslationTextComponent("selectWorld.enterName")) {
         protected IFormattableTextComponent createNarrationMessage() {
            return super.createNarrationMessage().append(". ").append(new TranslationTextComponent("selectWorld.resultFolder")).append(" ").append(CreateWorldScreen.this.resultFolder);
         }
      };
      this.nameEdit.setValue(this.initName);
      this.nameEdit.setResponder((p_214319_1_) -> {
         this.initName = p_214319_1_;
         this.createButton.active = !this.nameEdit.getValue().isEmpty();
         this.updateResultFolder();
      });
      this.children.add(this.nameEdit);
      int i = this.width / 2 - 155;
      int j = this.width / 2 + 5;
      this.modeButton = this.addButton(new Button(i, 100, 150, 20, StringTextComponent.EMPTY, (p_214316_1_) -> {
         switch(this.gameMode) {
         case SURVIVAL:
            this.setGameMode(CreateWorldScreen.GameMode.HARDCORE);
            break;
         case HARDCORE:
            this.setGameMode(CreateWorldScreen.GameMode.CREATIVE);
            break;
         case CREATIVE:
            this.setGameMode(CreateWorldScreen.GameMode.SURVIVAL);
         }

         p_214316_1_.queueNarration(250);
      }) {
         public ITextComponent getMessage() {
            return new TranslationTextComponent("options.generic_value", CreateWorldScreen.GAME_MODEL_LABEL, new TranslationTextComponent("selectWorld.gameMode." + CreateWorldScreen.this.gameMode.name));
         }

         protected IFormattableTextComponent createNarrationMessage() {
            return super.createNarrationMessage().append(". ").append(CreateWorldScreen.this.gameModeHelp1).append(" ").append(CreateWorldScreen.this.gameModeHelp2);
         }
      });
      this.difficultyButton = this.addButton(new Button(j, 100, 150, 20, new TranslationTextComponent("options.difficulty"), (p_238956_1_) -> {
         this.selectedDifficulty = this.selectedDifficulty.nextById();
         this.effectiveDifficulty = this.selectedDifficulty;
         p_238956_1_.queueNarration(250);
      }) {
         public ITextComponent getMessage() {
            return (new TranslationTextComponent("options.difficulty")).append(": ").append(CreateWorldScreen.this.effectiveDifficulty.getDisplayName());
         }
      });
      this.commandsButton = this.addButton(new Button(i, 151, 150, 20, new TranslationTextComponent("selectWorld.allowCommands"), (p_214322_1_) -> {
         this.commandsChanged = true;
         this.commands = !this.commands;
         p_214322_1_.queueNarration(250);
      }) {
         public ITextComponent getMessage() {
            return DialogTexts.optionStatus(super.getMessage(), CreateWorldScreen.this.commands && !CreateWorldScreen.this.hardCore);
         }

         protected IFormattableTextComponent createNarrationMessage() {
            return super.createNarrationMessage().append(". ").append(new TranslationTextComponent("selectWorld.allowCommands.info"));
         }
      });
      this.dataPacksButton = this.addButton(new Button(j, 151, 150, 20, new TranslationTextComponent("selectWorld.dataPacks"), (p_214320_1_) -> {
         this.openDataPackSelectionScreen();
      }));
      this.gameRulesButton = this.addButton(new Button(i, 185, 150, 20, new TranslationTextComponent("selectWorld.gameRules"), (p_214312_1_) -> {
         this.minecraft.setScreen(new EditGamerulesScreen(this.gameRules.copy(), (p_238946_1_) -> {
            this.minecraft.setScreen(this);
            p_238946_1_.ifPresent((p_238941_1_) -> {
               this.gameRules = p_238941_1_;
            });
         }));
      }));
      this.worldGenSettingsComponent.init(this, this.minecraft, this.font);
      this.moreOptionsButton = this.addButton(new Button(j, 185, 150, 20, new TranslationTextComponent("selectWorld.moreWorldOptions"), (p_214321_1_) -> {
         this.toggleDisplayOptions();
      }));
      this.createButton = this.addButton(new Button(i, this.height - 28, 150, 20, new TranslationTextComponent("selectWorld.create"), (p_214318_1_) -> {
         this.onCreate();
      }));
      this.createButton.active = !this.initName.isEmpty();
      this.addButton(new Button(j, this.height - 28, 150, 20, DialogTexts.GUI_CANCEL, (p_214317_1_) -> {
         this.popScreen();
      }));
      this.updateDisplayOptions();
      this.setInitialFocus(this.nameEdit);
      this.setGameMode(this.gameMode);
      this.updateResultFolder();
   }

   private void updateGameModeHelp() {
      this.gameModeHelp1 = new TranslationTextComponent("selectWorld.gameMode." + this.gameMode.name + ".line1");
      this.gameModeHelp2 = new TranslationTextComponent("selectWorld.gameMode." + this.gameMode.name + ".line2");
   }

   private void updateResultFolder() {
      this.resultFolder = this.nameEdit.getValue().trim();
      if (this.resultFolder.isEmpty()) {
         this.resultFolder = "World";
      }

      try {
         this.resultFolder = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), this.resultFolder, "");
      } catch (Exception exception1) {
         this.resultFolder = "World";

         try {
            this.resultFolder = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), this.resultFolder, "");
         } catch (Exception exception) {
            throw new RuntimeException("Could not create save folder", exception);
         }
      }

   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void onCreate() {
      this.minecraft.forceSetScreen(new DirtMessageScreen(new TranslationTextComponent("createWorld.preparing")));
      if (this.copyTempDataPackDirToNewWorld()) {
         this.cleanupTempResources();
         DimensionGeneratorSettings dimensiongeneratorsettings = this.worldGenSettingsComponent.makeSettings(this.hardCore);
         WorldSettings worldsettings;
         if (dimensiongeneratorsettings.isDebug()) {
            GameRules gamerules = new GameRules();
            gamerules.getRule(GameRules.RULE_DAYLIGHT).set(false, (MinecraftServer)null);
            worldsettings = new WorldSettings(this.nameEdit.getValue().trim(), GameType.SPECTATOR, false, Difficulty.PEACEFUL, true, gamerules, DatapackCodec.DEFAULT);
         } else {
            worldsettings = new WorldSettings(this.nameEdit.getValue().trim(), this.gameMode.gameType, this.hardCore, this.effectiveDifficulty, this.commands && !this.hardCore, this.gameRules, this.dataPacks);
         }

         this.minecraft.createLevel(this.resultFolder, worldsettings, this.worldGenSettingsComponent.registryHolder(), dimensiongeneratorsettings);
      }
   }

   private void toggleDisplayOptions() {
      this.setDisplayOptions(!this.displayOptions);
   }

   private void setGameMode(CreateWorldScreen.GameMode p_228200_1_) {
      if (!this.commandsChanged) {
         this.commands = p_228200_1_ == CreateWorldScreen.GameMode.CREATIVE;
      }

      if (p_228200_1_ == CreateWorldScreen.GameMode.HARDCORE) {
         this.hardCore = true;
         this.commandsButton.active = false;
         this.worldGenSettingsComponent.bonusItemsButton.active = false;
         this.effectiveDifficulty = Difficulty.HARD;
         this.difficultyButton.active = false;
      } else {
         this.hardCore = false;
         this.commandsButton.active = true;
         this.worldGenSettingsComponent.bonusItemsButton.active = true;
         this.effectiveDifficulty = this.selectedDifficulty;
         this.difficultyButton.active = true;
      }

      this.gameMode = p_228200_1_;
      this.updateGameModeHelp();
   }

   public void updateDisplayOptions() {
      this.setDisplayOptions(this.displayOptions);
   }

   private void setDisplayOptions(boolean p_146316_1_) {
      this.displayOptions = p_146316_1_;
      this.modeButton.visible = !this.displayOptions;
      this.difficultyButton.visible = !this.displayOptions;
      if (this.worldGenSettingsComponent.isDebug()) {
         this.dataPacksButton.visible = false;
         this.modeButton.active = false;
         if (this.oldGameMode == null) {
            this.oldGameMode = this.gameMode;
         }

         this.setGameMode(CreateWorldScreen.GameMode.DEBUG);
         this.commandsButton.visible = false;
      } else {
         this.modeButton.active = true;
         if (this.oldGameMode != null) {
            this.setGameMode(this.oldGameMode);
         }

         this.commandsButton.visible = !this.displayOptions;
         this.dataPacksButton.visible = !this.displayOptions;
      }

      this.worldGenSettingsComponent.setDisplayOptions(this.displayOptions);
      this.nameEdit.setVisible(!this.displayOptions);
      if (this.displayOptions) {
         this.moreOptionsButton.setMessage(DialogTexts.GUI_DONE);
      } else {
         this.moreOptionsButton.setMessage(new TranslationTextComponent("selectWorld.moreWorldOptions"));
      }

      this.gameRulesButton.visible = !this.displayOptions;
   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      if (super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_)) {
         return true;
      } else if (p_231046_1_ != 257 && p_231046_1_ != 335) {
         return false;
      } else {
         this.onCreate();
         return true;
      }
   }

   public void onClose() {
      if (this.displayOptions) {
         this.setDisplayOptions(false);
      } else {
         this.popScreen();
      }

   }

   public void popScreen() {
      this.minecraft.setScreen(this.lastScreen);
      this.cleanupTempResources();
   }

   private void cleanupTempResources() {
      if (this.tempDataPackRepository != null) {
         this.tempDataPackRepository.close();
      }

      this.removeTempDataPackDir();
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 20, -1);
      if (this.displayOptions) {
         drawString(p_230430_1_, this.font, SEED_LABEL, this.width / 2 - 100, 47, -6250336);
         drawString(p_230430_1_, this.font, SEED_INFO, this.width / 2 - 100, 85, -6250336);
         this.worldGenSettingsComponent.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      } else {
         drawString(p_230430_1_, this.font, NAME_LABEL, this.width / 2 - 100, 47, -6250336);
         drawString(p_230430_1_, this.font, (new StringTextComponent("")).append(OUTPUT_DIR_INFO).append(" ").append(this.resultFolder), this.width / 2 - 100, 85, -6250336);
         this.nameEdit.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
         drawString(p_230430_1_, this.font, this.gameModeHelp1, this.width / 2 - 150, 122, -6250336);
         drawString(p_230430_1_, this.font, this.gameModeHelp2, this.width / 2 - 150, 134, -6250336);
         if (this.commandsButton.visible) {
            drawString(p_230430_1_, this.font, COMMANDS_INFO, this.width / 2 - 150, 172, -6250336);
         }
      }

      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }

   protected <T extends IGuiEventListener> T addWidget(T p_230481_1_) {
      return super.addWidget(p_230481_1_);
   }

   protected <T extends Widget> T addButton(T p_230480_1_) {
      return super.addButton(p_230480_1_);
   }

   @Nullable
   protected Path getTempDataPackDir() {
      if (this.tempDataPackDir == null) {
         try {
            this.tempDataPackDir = Files.createTempDirectory("mcworld-");
         } catch (IOException ioexception) {
            LOGGER.warn("Failed to create temporary dir", (Throwable)ioexception);
            SystemToast.onPackCopyFailure(this.minecraft, this.resultFolder);
            this.popScreen();
         }
      }

      return this.tempDataPackDir;
   }

   private void openDataPackSelectionScreen() {
      Pair<File, ResourcePackList> pair = this.getDataPackSelectionSettings();
      if (pair != null) {
         this.minecraft.setScreen(new PackScreen(this, pair.getSecond(), this::tryApplyNewDataPacks, pair.getFirst(), new TranslationTextComponent("dataPack.title")));
      }

   }

   private void tryApplyNewDataPacks(ResourcePackList p_241621_1_) {
      List<String> list = ImmutableList.copyOf(p_241621_1_.getSelectedIds());
      List<String> list1 = p_241621_1_.getAvailableIds().stream().filter((p_241626_1_) -> {
         return !list.contains(p_241626_1_);
      }).collect(ImmutableList.toImmutableList());
      DatapackCodec datapackcodec = new DatapackCodec(list, list1);
      if (list.equals(this.dataPacks.getEnabled())) {
         this.dataPacks = datapackcodec;
      } else {
         this.minecraft.tell(() -> {
            this.minecraft.setScreen(new DirtMessageScreen(new TranslationTextComponent("dataPack.validation.working")));
         });
         DataPackRegistries.loadResources(p_241621_1_.openAllSelected(), Commands.EnvironmentType.INTEGRATED, 2, Util.backgroundExecutor(), this.minecraft).handle((p_241623_2_, p_241623_3_) -> {
            if (p_241623_3_ != null) {
               LOGGER.warn("Failed to validate datapack", p_241623_3_);
               this.minecraft.tell(() -> {
                  this.minecraft.setScreen(new ConfirmScreen((p_241630_1_) -> {
                     if (p_241630_1_) {
                        this.openDataPackSelectionScreen();
                     } else {
                        this.dataPacks = DatapackCodec.DEFAULT;
                        this.minecraft.setScreen(this);
                     }

                  }, new TranslationTextComponent("dataPack.validation.failed"), StringTextComponent.EMPTY, new TranslationTextComponent("dataPack.validation.back"), new TranslationTextComponent("dataPack.validation.reset")));
               });
            } else {
               this.minecraft.tell(() -> {
                  this.dataPacks = datapackcodec;
                  this.worldGenSettingsComponent.updateDataPacks(p_241623_2_);
                  p_241623_2_.close();
                  this.minecraft.setScreen(this);
               });
            }

            return null;
         });
      }
   }

   private void removeTempDataPackDir() {
      if (this.tempDataPackDir != null) {
         try (Stream<Path> stream = Files.walk(this.tempDataPackDir)) {
            stream.sorted(Comparator.reverseOrder()).forEach((p_238948_0_) -> {
               try {
                  Files.delete(p_238948_0_);
               } catch (IOException ioexception1) {
                  LOGGER.warn("Failed to remove temporary file {}", p_238948_0_, ioexception1);
               }

            });
         } catch (IOException ioexception) {
            LOGGER.warn("Failed to list temporary dir {}", (Object)this.tempDataPackDir);
         }

         this.tempDataPackDir = null;
      }

   }

   private static void copyBetweenDirs(Path p_238945_0_, Path p_238945_1_, Path p_238945_2_) {
      try {
         Util.copyBetweenDirs(p_238945_0_, p_238945_1_, p_238945_2_);
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to copy datapack file from {} to {}", p_238945_2_, p_238945_1_);
         throw new CreateWorldScreen.DatapackException(ioexception);
      }
   }

   private boolean copyTempDataPackDirToNewWorld() {
      if (this.tempDataPackDir != null) {
         try (
            SaveFormat.LevelSave saveformat$levelsave = this.minecraft.getLevelSource().createAccess(this.resultFolder);
            Stream<Path> stream = Files.walk(this.tempDataPackDir);
         ) {
            Path path = saveformat$levelsave.getLevelPath(FolderName.DATAPACK_DIR);
            Files.createDirectories(path);
            stream.filter((p_238942_1_) -> {
               return !p_238942_1_.equals(this.tempDataPackDir);
            }).forEach((p_238949_2_) -> {
               copyBetweenDirs(this.tempDataPackDir, path, p_238949_2_);
            });
         } catch (CreateWorldScreen.DatapackException | IOException ioexception) {
            LOGGER.warn("Failed to copy datapacks to world {}", this.resultFolder, ioexception);
            SystemToast.onPackCopyFailure(this.minecraft, this.resultFolder);
            this.popScreen();
            return false;
         }
      }

      return true;
   }

   @Nullable
   public static Path createTempDataPackDirFromExistingWorld(Path p_238943_0_, Minecraft p_238943_1_) {
      MutableObject<Path> mutableobject = new MutableObject<>();

      try (Stream<Path> stream = Files.walk(p_238943_0_)) {
         stream.filter((p_238944_1_) -> {
            return !p_238944_1_.equals(p_238943_0_);
         }).forEach((p_238947_2_) -> {
            Path path = mutableobject.getValue();
            if (path == null) {
               try {
                  path = Files.createTempDirectory("mcworld-");
               } catch (IOException ioexception1) {
                  LOGGER.warn("Failed to create temporary dir");
                  throw new CreateWorldScreen.DatapackException(ioexception1);
               }

               mutableobject.setValue(path);
            }

            copyBetweenDirs(p_238943_0_, path, p_238947_2_);
         });
      } catch (CreateWorldScreen.DatapackException | IOException ioexception) {
         LOGGER.warn("Failed to copy datapacks from world {}", p_238943_0_, ioexception);
         SystemToast.onPackCopyFailure(p_238943_1_, p_238943_0_.toString());
         return null;
      }

      return mutableobject.getValue();
   }

   @Nullable
   private Pair<File, ResourcePackList> getDataPackSelectionSettings() {
      Path path = this.getTempDataPackDir();
      if (path != null) {
         File file1 = path.toFile();
         if (this.tempDataPackRepository == null) {
            this.tempDataPackRepository = new ResourcePackList(new ServerPackFinder(), new FolderPackFinder(file1, IPackNameDecorator.DEFAULT));
            net.minecraftforge.fml.packs.ResourcePackLoader.loadResourcePacks(this.tempDataPackRepository, net.minecraftforge.fml.server.ServerLifecycleHooks::buildPackFinder);
            this.tempDataPackRepository.reload();
         }

         this.tempDataPackRepository.setSelected(this.dataPacks.getEnabled());
         return Pair.of(file1, this.tempDataPackRepository);
      } else {
         return null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class DatapackException extends RuntimeException {
      public DatapackException(Throwable p_i232309_1_) {
         super(p_i232309_1_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static enum GameMode {
      SURVIVAL("survival", GameType.SURVIVAL),
      HARDCORE("hardcore", GameType.SURVIVAL),
      CREATIVE("creative", GameType.CREATIVE),
      DEBUG("spectator", GameType.SPECTATOR);

      private final String name;
      private final GameType gameType;

      private GameMode(String p_i225940_3_, GameType p_i225940_4_) {
         this.name = p_i225940_3_;
         this.gameType = p_i225940_4_;
      }
   }
}
