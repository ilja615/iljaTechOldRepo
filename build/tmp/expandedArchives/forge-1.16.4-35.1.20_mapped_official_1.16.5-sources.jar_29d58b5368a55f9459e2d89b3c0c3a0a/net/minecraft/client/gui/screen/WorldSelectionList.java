package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.FolderName;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class WorldSelectionList extends ExtendedList<WorldSelectionList.Entry> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
   private static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
   private static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/world_selection.png");
   private static final ITextComponent FROM_NEWER_TOOLTIP_1 = (new TranslationTextComponent("selectWorld.tooltip.fromNewerVersion1")).withStyle(TextFormatting.RED);
   private static final ITextComponent FROM_NEWER_TOOLTIP_2 = (new TranslationTextComponent("selectWorld.tooltip.fromNewerVersion2")).withStyle(TextFormatting.RED);
   private static final ITextComponent SNAPSHOT_TOOLTIP_1 = (new TranslationTextComponent("selectWorld.tooltip.snapshot1")).withStyle(TextFormatting.GOLD);
   private static final ITextComponent SNAPSHOT_TOOLTIP_2 = (new TranslationTextComponent("selectWorld.tooltip.snapshot2")).withStyle(TextFormatting.GOLD);
   private static final ITextComponent WORLD_LOCKED_TOOLTIP = (new TranslationTextComponent("selectWorld.locked")).withStyle(TextFormatting.RED);
   private final WorldSelectionScreen screen;
   @Nullable
   private List<WorldSummary> cachedList;

   public WorldSelectionList(WorldSelectionScreen p_i49846_1_, Minecraft p_i49846_2_, int p_i49846_3_, int p_i49846_4_, int p_i49846_5_, int p_i49846_6_, int p_i49846_7_, Supplier<String> p_i49846_8_, @Nullable WorldSelectionList p_i49846_9_) {
      super(p_i49846_2_, p_i49846_3_, p_i49846_4_, p_i49846_5_, p_i49846_6_, p_i49846_7_);
      this.screen = p_i49846_1_;
      if (p_i49846_9_ != null) {
         this.cachedList = p_i49846_9_.cachedList;
      }

      this.refreshList(p_i49846_8_, false);
   }

   public void refreshList(Supplier<String> p_212330_1_, boolean p_212330_2_) {
      this.clearEntries();
      SaveFormat saveformat = this.minecraft.getLevelSource();
      if (this.cachedList == null || p_212330_2_) {
         try {
            this.cachedList = saveformat.getLevelList();
         } catch (AnvilConverterException anvilconverterexception) {
            LOGGER.error("Couldn't load level list", (Throwable)anvilconverterexception);
            this.minecraft.setScreen(new ErrorScreen(new TranslationTextComponent("selectWorld.unable_to_load"), new StringTextComponent(anvilconverterexception.getMessage())));
            return;
         }

         Collections.sort(this.cachedList);
      }

      if (this.cachedList.isEmpty()) {
         this.minecraft.setScreen(CreateWorldScreen.create((Screen)null));
      } else {
         String s = p_212330_1_.get().toLowerCase(Locale.ROOT);

         for(WorldSummary worldsummary : this.cachedList) {
            if (worldsummary.getLevelName().toLowerCase(Locale.ROOT).contains(s) || worldsummary.getLevelId().toLowerCase(Locale.ROOT).contains(s)) {
               this.addEntry(new WorldSelectionList.Entry(this, worldsummary));
            }
         }

      }
   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 20;
   }

   public int getRowWidth() {
      return super.getRowWidth() + 50;
   }

   protected boolean isFocused() {
      return this.screen.getFocused() == this;
   }

   public void setSelected(@Nullable WorldSelectionList.Entry p_241215_1_) {
      super.setSelected(p_241215_1_);
      if (p_241215_1_ != null) {
         WorldSummary worldsummary = p_241215_1_.summary;
         NarratorChatListener.INSTANCE.sayNow((new TranslationTextComponent("narrator.select", new TranslationTextComponent("narrator.select.world", worldsummary.getLevelName(), new Date(worldsummary.getLastPlayed()), worldsummary.isHardcore() ? new TranslationTextComponent("gameMode.hardcore") : new TranslationTextComponent("gameMode." + worldsummary.getGameMode().getName()), worldsummary.hasCheats() ? new TranslationTextComponent("selectWorld.cheats") : StringTextComponent.EMPTY, worldsummary.getWorldVersionName()))).getString());
      }

      this.screen.updateButtonStatus(p_241215_1_ != null && !p_241215_1_.summary.isLocked());
   }

   protected void moveSelection(AbstractList.Ordering p_241219_1_) {
      this.moveSelection(p_241219_1_, (p_241652_0_) -> {
         return !p_241652_0_.summary.isLocked();
      });
   }

   public Optional<WorldSelectionList.Entry> getSelectedOpt() {
      return Optional.ofNullable(this.getSelected());
   }

   public WorldSelectionScreen getScreen() {
      return this.screen;
   }

   @OnlyIn(Dist.CLIENT)
   public final class Entry extends ExtendedList.AbstractListEntry<WorldSelectionList.Entry> implements AutoCloseable {
      private final Minecraft minecraft;
      private final WorldSelectionScreen screen;
      private final WorldSummary summary;
      private final ResourceLocation iconLocation;
      private File iconFile;
      @Nullable
      private final DynamicTexture icon;
      private long lastClickTime;

      public Entry(WorldSelectionList p_i242066_2_, WorldSummary p_i242066_3_) {
         this.screen = p_i242066_2_.getScreen();
         this.summary = p_i242066_3_;
         this.minecraft = Minecraft.getInstance();
         String s = p_i242066_3_.getLevelId();
         this.iconLocation = new ResourceLocation("minecraft", "worlds/" + Util.sanitizeName(s, ResourceLocation::validPathChar) + "/" + Hashing.sha1().hashUnencodedChars(s) + "/icon");
         this.iconFile = p_i242066_3_.getIcon();
         if (!this.iconFile.isFile()) {
            this.iconFile = null;
         }

         this.icon = this.loadServerIcon();
      }

      public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
         String s = this.summary.getLevelName();
         String s1 = this.summary.getLevelId() + " (" + WorldSelectionList.DATE_FORMAT.format(new Date(this.summary.getLastPlayed())) + ")";
         if (StringUtils.isEmpty(s)) {
            s = I18n.get("selectWorld.world") + " " + (p_230432_2_ + 1);
         }

         ITextComponent itextcomponent = this.summary.getInfo();
         this.minecraft.font.draw(p_230432_1_, s, (float)(p_230432_4_ + 32 + 3), (float)(p_230432_3_ + 1), 16777215);
         this.minecraft.font.draw(p_230432_1_, s1, (float)(p_230432_4_ + 32 + 3), (float)(p_230432_3_ + 9 + 3), 8421504);
         this.minecraft.font.draw(p_230432_1_, itextcomponent, (float)(p_230432_4_ + 32 + 3), (float)(p_230432_3_ + 9 + 9 + 3), 8421504);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.minecraft.getTextureManager().bind(this.icon != null ? this.iconLocation : WorldSelectionList.ICON_MISSING);
         RenderSystem.enableBlend();
         AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 0.0F, 0.0F, 32, 32, 32, 32);
         RenderSystem.disableBlend();
         if (this.minecraft.options.touchscreen || p_230432_9_) {
            this.minecraft.getTextureManager().bind(WorldSelectionList.ICON_OVERLAY_LOCATION);
            AbstractGui.fill(p_230432_1_, p_230432_4_, p_230432_3_, p_230432_4_ + 32, p_230432_3_ + 32, -1601138544);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int i = p_230432_7_ - p_230432_4_;
            boolean flag = i < 32;
            int j = flag ? 32 : 0;
            if (this.summary.isLocked()) {
               AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 96.0F, (float)j, 32, 32, 256, 256);
               if (flag) {
                  this.screen.setToolTip(this.minecraft.font.split(WorldSelectionList.WORLD_LOCKED_TOOLTIP, 175));
               }
            } else if (this.summary.markVersionInList()) {
               AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 32.0F, (float)j, 32, 32, 256, 256);
               if (this.summary.askToOpenWorld()) {
                  AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 96.0F, (float)j, 32, 32, 256, 256);
                  if (flag) {
                     this.screen.setToolTip(ImmutableList.of(WorldSelectionList.FROM_NEWER_TOOLTIP_1.getVisualOrderText(), WorldSelectionList.FROM_NEWER_TOOLTIP_2.getVisualOrderText()));
                  }
               } else if (!SharedConstants.getCurrentVersion().isStable()) {
                  AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 64.0F, (float)j, 32, 32, 256, 256);
                  if (flag) {
                     this.screen.setToolTip(ImmutableList.of(WorldSelectionList.SNAPSHOT_TOOLTIP_1.getVisualOrderText(), WorldSelectionList.SNAPSHOT_TOOLTIP_2.getVisualOrderText()));
                  }
               }
            } else {
               AbstractGui.blit(p_230432_1_, p_230432_4_, p_230432_3_, 0.0F, (float)j, 32, 32, 256, 256);
            }
         }

      }

      public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
         if (this.summary.isLocked()) {
            return true;
         } else {
            WorldSelectionList.this.setSelected(this);
            this.screen.updateButtonStatus(WorldSelectionList.this.getSelectedOpt().isPresent());
            if (p_231044_1_ - (double)WorldSelectionList.this.getRowLeft() <= 32.0D) {
               this.joinWorld();
               return true;
            } else if (Util.getMillis() - this.lastClickTime < 250L) {
               this.joinWorld();
               return true;
            } else {
               this.lastClickTime = Util.getMillis();
               return false;
            }
         }
      }

      public void joinWorld() {
         if (!this.summary.isLocked()) {
            if (this.summary.shouldBackup()) {
               ITextComponent itextcomponent = new TranslationTextComponent("selectWorld.backupQuestion");
               ITextComponent itextcomponent1 = new TranslationTextComponent("selectWorld.backupWarning", this.summary.getWorldVersionName(), SharedConstants.getCurrentVersion().getName());
               this.minecraft.setScreen(new ConfirmBackupScreen(this.screen, (p_214436_1_, p_214436_2_) -> {
                  if (p_214436_1_) {
                     String s = this.summary.getLevelId();

                     try (SaveFormat.LevelSave saveformat$levelsave = this.minecraft.getLevelSource().createAccess(s)) {
                        EditWorldScreen.makeBackupAndShowToast(saveformat$levelsave);
                     } catch (IOException ioexception) {
                        SystemToast.onWorldAccessFailure(this.minecraft, s);
                        WorldSelectionList.LOGGER.error("Failed to backup level {}", s, ioexception);
                     }
                  }

                  this.loadWorld();
               }, itextcomponent, itextcomponent1, false));
            } else if (this.summary.askToOpenWorld()) {
               this.minecraft.setScreen(new ConfirmScreen((p_214434_1_) -> {
                  if (p_214434_1_) {
                     try {
                        this.loadWorld();
                     } catch (Exception exception) {
                        WorldSelectionList.LOGGER.error("Failure to open 'future world'", (Throwable)exception);
                        this.minecraft.setScreen(new AlertScreen(() -> {
                           this.minecraft.setScreen(this.screen);
                        }, new TranslationTextComponent("selectWorld.futureworld.error.title"), new TranslationTextComponent("selectWorld.futureworld.error.text")));
                     }
                  } else {
                     this.minecraft.setScreen(this.screen);
                  }

               }, new TranslationTextComponent("selectWorld.versionQuestion"), new TranslationTextComponent("selectWorld.versionWarning", this.summary.getWorldVersionName(), new TranslationTextComponent("selectWorld.versionJoinButton"), DialogTexts.GUI_CANCEL)));
            } else {
               this.loadWorld();
            }

         }
      }

      public void deleteWorld() {
         this.minecraft.setScreen(new ConfirmScreen((p_214440_1_) -> {
            if (p_214440_1_) {
               this.minecraft.setScreen(new WorkingScreen());
               SaveFormat saveformat = this.minecraft.getLevelSource();
               String s = this.summary.getLevelId();

               try (SaveFormat.LevelSave saveformat$levelsave = saveformat.createAccess(s)) {
                  saveformat$levelsave.deleteLevel();
               } catch (IOException ioexception) {
                  SystemToast.onWorldDeleteFailure(this.minecraft, s);
                  WorldSelectionList.LOGGER.error("Failed to delete world {}", s, ioexception);
               }

               WorldSelectionList.this.refreshList(() -> {
                  return this.screen.searchBox.getValue();
               }, true);
            }

            this.minecraft.setScreen(this.screen);
         }, new TranslationTextComponent("selectWorld.deleteQuestion"), new TranslationTextComponent("selectWorld.deleteWarning", this.summary.getLevelName()), new TranslationTextComponent("selectWorld.deleteButton"), DialogTexts.GUI_CANCEL));
      }

      public void editWorld() {
         String s = this.summary.getLevelId();

         try {
            SaveFormat.LevelSave saveformat$levelsave = this.minecraft.getLevelSource().createAccess(s);
            this.minecraft.setScreen(new EditWorldScreen((p_239096_3_) -> {
               try {
                  saveformat$levelsave.close();
               } catch (IOException ioexception1) {
                  WorldSelectionList.LOGGER.error("Failed to unlock level {}", s, ioexception1);
               }

               if (p_239096_3_) {
                  WorldSelectionList.this.refreshList(() -> {
                     return this.screen.searchBox.getValue();
                  }, true);
               }

               this.minecraft.setScreen(this.screen);
            }, saveformat$levelsave));
         } catch (IOException ioexception) {
            SystemToast.onWorldAccessFailure(this.minecraft, s);
            WorldSelectionList.LOGGER.error("Failed to access level {}", s, ioexception);
            WorldSelectionList.this.refreshList(() -> {
               return this.screen.searchBox.getValue();
            }, true);
         }

      }

      public void recreateWorld() {
         this.queueLoadScreen();
         DynamicRegistries.Impl dynamicregistries$impl = DynamicRegistries.builtin();

         try (
            SaveFormat.LevelSave saveformat$levelsave = this.minecraft.getLevelSource().createAccess(this.summary.getLevelId());
            Minecraft.PackManager minecraft$packmanager = this.minecraft.makeServerStem(dynamicregistries$impl, Minecraft::loadDataPacks, Minecraft::loadWorldData, false, saveformat$levelsave);
         ) {
            WorldSettings worldsettings = minecraft$packmanager.worldData().getLevelSettings();
            DatapackCodec datapackcodec = worldsettings.getDataPackConfig();
            DimensionGeneratorSettings dimensiongeneratorsettings = minecraft$packmanager.worldData().worldGenSettings();
            Path path = CreateWorldScreen.createTempDataPackDirFromExistingWorld(saveformat$levelsave.getLevelPath(FolderName.DATAPACK_DIR), this.minecraft);
            if (dimensiongeneratorsettings.isOldCustomizedWorld()) {
               this.minecraft.setScreen(new ConfirmScreen((p_239095_6_) -> {
                  this.minecraft.setScreen((Screen)(p_239095_6_ ? new CreateWorldScreen(this.screen, worldsettings, dimensiongeneratorsettings, path, datapackcodec, dynamicregistries$impl) : this.screen));
               }, new TranslationTextComponent("selectWorld.recreate.customized.title"), new TranslationTextComponent("selectWorld.recreate.customized.text"), DialogTexts.GUI_PROCEED, DialogTexts.GUI_CANCEL));
            } else {
               this.minecraft.setScreen(new CreateWorldScreen(this.screen, worldsettings, dimensiongeneratorsettings, path, datapackcodec, dynamicregistries$impl));
            }
         } catch (Exception exception) {
            WorldSelectionList.LOGGER.error("Unable to recreate world", (Throwable)exception);
            this.minecraft.setScreen(new AlertScreen(() -> {
               this.minecraft.setScreen(this.screen);
            }, new TranslationTextComponent("selectWorld.recreate.error.title"), new TranslationTextComponent("selectWorld.recreate.error.text")));
         }

      }

      private void loadWorld() {
         this.minecraft.getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         if (this.minecraft.getLevelSource().levelExists(this.summary.getLevelId())) {
            this.queueLoadScreen();
            this.minecraft.loadLevel(this.summary.getLevelId());
         }

      }

      private void queueLoadScreen() {
         this.minecraft.forceSetScreen(new DirtMessageScreen(new TranslationTextComponent("selectWorld.data_read")));
      }

      @Nullable
      private DynamicTexture loadServerIcon() {
         boolean flag = this.iconFile != null && this.iconFile.isFile();
         if (flag) {
            try (InputStream inputstream = new FileInputStream(this.iconFile)) {
               NativeImage nativeimage = NativeImage.read(inputstream);
               Validate.validState(nativeimage.getWidth() == 64, "Must be 64 pixels wide");
               Validate.validState(nativeimage.getHeight() == 64, "Must be 64 pixels high");
               DynamicTexture dynamictexture = new DynamicTexture(nativeimage);
               this.minecraft.getTextureManager().register(this.iconLocation, dynamictexture);
               return dynamictexture;
            } catch (Throwable throwable) {
               WorldSelectionList.LOGGER.error("Invalid icon for world {}", this.summary.getLevelId(), throwable);
               this.iconFile = null;
               return null;
            }
         } else {
            this.minecraft.getTextureManager().release(this.iconLocation);
            return null;
         }
      }

      public void close() {
         if (this.icon != null) {
            this.icon.close();
         }

      }
   }
}
