package net.minecraft.client.gui.screen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonWriter;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult.PartialResult;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.WorldGenSettingsExport;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.FolderName;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class EditWorldScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson WORLD_GEN_SETTINGS_GSON = (new GsonBuilder()).setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
   private static final ITextComponent NAME_LABEL = new TranslationTextComponent("selectWorld.enterName");
   private Button renameButton;
   private final BooleanConsumer callback;
   private TextFieldWidget nameEdit;
   private final SaveFormat.LevelSave levelAccess;

   public EditWorldScreen(BooleanConsumer p_i232318_1_, SaveFormat.LevelSave p_i232318_2_) {
      super(new TranslationTextComponent("selectWorld.edit.title"));
      this.callback = p_i232318_1_;
      this.levelAccess = p_i232318_2_;
   }

   public void tick() {
      this.nameEdit.tick();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      Button button = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 0 + 5, 200, 20, new TranslationTextComponent("selectWorld.edit.resetIcon"), (p_214309_1_) -> {
         FileUtils.deleteQuietly(this.levelAccess.getIconFile());
         p_214309_1_.active = false;
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 24 + 5, 200, 20, new TranslationTextComponent("selectWorld.edit.openFolder"), (p_214303_1_) -> {
         Util.getPlatform().openFile(this.levelAccess.getLevelPath(FolderName.ROOT).toFile());
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 48 + 5, 200, 20, new TranslationTextComponent("selectWorld.edit.backup"), (p_214304_1_) -> {
         boolean flag = makeBackupAndShowToast(this.levelAccess);
         this.callback.accept(!flag);
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 72 + 5, 200, 20, new TranslationTextComponent("selectWorld.edit.backupFolder"), (p_214302_1_) -> {
         SaveFormat saveformat = this.minecraft.getLevelSource();
         Path path = saveformat.getBackupPath();

         try {
            Files.createDirectories(Files.exists(path) ? path.toRealPath() : path);
         } catch (IOException ioexception) {
            throw new RuntimeException(ioexception);
         }

         Util.getPlatform().openFile(path.toFile());
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 5, 200, 20, new TranslationTextComponent("selectWorld.edit.optimize"), (p_214310_1_) -> {
         this.minecraft.setScreen(new ConfirmBackupScreen(this, (p_214305_1_, p_214305_2_) -> {
            if (p_214305_1_) {
               makeBackupAndShowToast(this.levelAccess);
            }

            this.minecraft.setScreen(OptimizeWorldScreen.create(this.minecraft, this.callback, this.minecraft.getFixerUpper(), this.levelAccess, p_214305_2_));
         }, new TranslationTextComponent("optimizeWorld.confirm.title"), new TranslationTextComponent("optimizeWorld.confirm.description"), true));
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 5, 200, 20, new TranslationTextComponent("selectWorld.edit.export_worldgen_settings"), (p_239023_1_) -> {
         DynamicRegistries.Impl dynamicregistries$impl = DynamicRegistries.builtin();

         DataResult<String> dataresult;
         try (Minecraft.PackManager minecraft$packmanager = this.minecraft.makeServerStem(dynamicregistries$impl, Minecraft::loadDataPacks, Minecraft::loadWorldData, false, this.levelAccess)) {
            DynamicOps<JsonElement> dynamicops = WorldGenSettingsExport.create(JsonOps.INSTANCE, dynamicregistries$impl);
            DataResult<JsonElement> dataresult1 = DimensionGeneratorSettings.CODEC.encodeStart(dynamicops, minecraft$packmanager.worldData().worldGenSettings());
            dataresult = dataresult1.flatMap((p_239017_1_) -> {
               Path path = this.levelAccess.getLevelPath(FolderName.ROOT).resolve("worldgen_settings_export.json");

               try (JsonWriter jsonwriter = WORLD_GEN_SETTINGS_GSON.newJsonWriter(Files.newBufferedWriter(path, StandardCharsets.UTF_8))) {
                  WORLD_GEN_SETTINGS_GSON.toJson(p_239017_1_, jsonwriter);
               } catch (JsonIOException | IOException ioexception) {
                  return DataResult.error("Error writing file: " + ioexception.getMessage());
               }

               return DataResult.success(path.toString());
            });
         } catch (ExecutionException | InterruptedException interruptedexception) {
            dataresult = DataResult.error("Could not parse level data!");
         }

         ITextComponent itextcomponent = new StringTextComponent(dataresult.get().map(Function.identity(), PartialResult::message));
         ITextComponent itextcomponent1 = new TranslationTextComponent(dataresult.result().isPresent() ? "selectWorld.edit.export_worldgen_settings.success" : "selectWorld.edit.export_worldgen_settings.failure");
         dataresult.error().ifPresent((p_239018_0_) -> {
            LOGGER.error("Error exporting world settings: {}", (Object)p_239018_0_);
         });
         this.minecraft.getToasts().addToast(SystemToast.multiline(this.minecraft, SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER, itextcomponent1, itextcomponent));
      }));
      this.renameButton = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20, new TranslationTextComponent("selectWorld.edit.save"), (p_214308_1_) -> {
         this.onRename();
      }));
      this.addButton(new Button(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20, DialogTexts.GUI_CANCEL, (p_214306_1_) -> {
         this.callback.accept(false);
      }));
      button.active = this.levelAccess.getIconFile().isFile();
      WorldSummary worldsummary = this.levelAccess.getSummary();
      String s = worldsummary == null ? "" : worldsummary.getLevelName();
      this.nameEdit = new TextFieldWidget(this.font, this.width / 2 - 100, 38, 200, 20, new TranslationTextComponent("selectWorld.enterName"));
      this.nameEdit.setValue(s);
      this.nameEdit.setResponder((p_214301_1_) -> {
         this.renameButton.active = !p_214301_1_.trim().isEmpty();
      });
      this.children.add(this.nameEdit);
      this.setInitialFocus(this.nameEdit);
   }

   public void resize(Minecraft p_231152_1_, int p_231152_2_, int p_231152_3_) {
      String s = this.nameEdit.getValue();
      this.init(p_231152_1_, p_231152_2_, p_231152_3_);
      this.nameEdit.setValue(s);
   }

   public void onClose() {
      this.callback.accept(false);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void onRename() {
      try {
         this.levelAccess.renameLevel(this.nameEdit.getValue().trim());
         this.callback.accept(true);
      } catch (IOException ioexception) {
         LOGGER.error("Failed to access world '{}'", this.levelAccess.getLevelId(), ioexception);
         SystemToast.onWorldAccessFailure(this.minecraft, this.levelAccess.getLevelId());
         this.callback.accept(true);
      }

   }

   public static void makeBackupAndShowToast(SaveFormat p_241651_0_, String p_241651_1_) {
      boolean flag = false;

      try (SaveFormat.LevelSave saveformat$levelsave = p_241651_0_.createAccess(p_241651_1_)) {
         flag = true;
         makeBackupAndShowToast(saveformat$levelsave);
      } catch (IOException ioexception) {
         if (!flag) {
            SystemToast.onWorldAccessFailure(Minecraft.getInstance(), p_241651_1_);
         }

         LOGGER.warn("Failed to create backup of level {}", p_241651_1_, ioexception);
      }

   }

   public static boolean makeBackupAndShowToast(SaveFormat.LevelSave p_239019_0_) {
      long i = 0L;
      IOException ioexception = null;

      try {
         i = p_239019_0_.makeWorldBackup();
      } catch (IOException ioexception1) {
         ioexception = ioexception1;
      }

      if (ioexception != null) {
         ITextComponent itextcomponent2 = new TranslationTextComponent("selectWorld.edit.backupFailed");
         ITextComponent itextcomponent3 = new StringTextComponent(ioexception.getMessage());
         Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.Type.WORLD_BACKUP, itextcomponent2, itextcomponent3));
         return false;
      } else {
         ITextComponent itextcomponent = new TranslationTextComponent("selectWorld.edit.backupCreated", p_239019_0_.getLevelId());
         ITextComponent itextcomponent1 = new TranslationTextComponent("selectWorld.edit.backupSize", MathHelper.ceil((double)i / 1048576.0D));
         Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.Type.WORLD_BACKUP, itextcomponent, itextcomponent1));
         return true;
      }
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 15, 16777215);
      drawString(p_230430_1_, this.font, NAME_LABEL, this.width / 2 - 100, 24, 10526880);
      this.nameEdit.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }
}
