package net.minecraft.client.gui.screen;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class PackScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ITextComponent DRAG_AND_DROP = (new TranslationTextComponent("pack.dropInfo")).withStyle(TextFormatting.GRAY);
   private static final ITextComponent DIRECTORY_BUTTON_TOOLTIP = new TranslationTextComponent("pack.folderInfo");
   private static final ResourceLocation DEFAULT_ICON = new ResourceLocation("textures/misc/unknown_pack.png");
   private final PackLoadingManager model;
   private final Screen lastScreen;
   @Nullable
   private PackScreen.PackDirectoryWatcher watcher;
   private long ticksToReload;
   private net.minecraft.client.gui.widget.list.ResourcePackList availablePackList;
   private net.minecraft.client.gui.widget.list.ResourcePackList selectedPackList;
   private final File packDir;
   private Button doneButton;
   private final Map<String, ResourceLocation> packIcons = Maps.newHashMap();

   public PackScreen(Screen p_i242060_1_, ResourcePackList p_i242060_2_, Consumer<ResourcePackList> p_i242060_3_, File p_i242060_4_, ITextComponent p_i242060_5_) {
      super(p_i242060_5_);
      this.lastScreen = p_i242060_1_;
      this.model = new PackLoadingManager(this::populateLists, this::getPackIcon, p_i242060_2_, p_i242060_3_);
      this.packDir = p_i242060_4_;
      this.watcher = PackScreen.PackDirectoryWatcher.create(p_i242060_4_);
   }

   public void onClose() {
      this.model.commit();
      this.minecraft.setScreen(this.lastScreen);
      this.closeWatcher();
   }

   private void closeWatcher() {
      if (this.watcher != null) {
         try {
            this.watcher.close();
            this.watcher = null;
         } catch (Exception exception) {
         }
      }

   }

   protected void init() {
      this.doneButton = this.addButton(new Button(this.width / 2 + 4, this.height - 48, 150, 20, DialogTexts.GUI_DONE, (p_238903_1_) -> {
         this.onClose();
      }));
      this.addButton(new Button(this.width / 2 - 154, this.height - 48, 150, 20, new TranslationTextComponent("pack.openFolder"), (p_238896_1_) -> {
         Util.getPlatform().openFile(this.packDir);
      }, (p_238897_1_, p_238897_2_, p_238897_3_, p_238897_4_) -> {
         this.renderTooltip(p_238897_2_, DIRECTORY_BUTTON_TOOLTIP, p_238897_3_, p_238897_4_);
      }));
      this.availablePackList = new net.minecraft.client.gui.widget.list.ResourcePackList(this.minecraft, 200, this.height, new TranslationTextComponent("pack.available.title"));
      this.availablePackList.setLeftPos(this.width / 2 - 4 - 200);
      this.children.add(this.availablePackList);
      this.selectedPackList = new net.minecraft.client.gui.widget.list.ResourcePackList(this.minecraft, 200, this.height, new TranslationTextComponent("pack.selected.title"));
      this.selectedPackList.setLeftPos(this.width / 2 + 4);
      this.children.add(this.selectedPackList);
      this.reload();
   }

   public void tick() {
      if (this.watcher != null) {
         try {
            if (this.watcher.pollForChanges()) {
               this.ticksToReload = 20L;
            }
         } catch (IOException ioexception) {
            LOGGER.warn("Failed to poll for directory {} changes, stopping", (Object)this.packDir);
            this.closeWatcher();
         }
      }

      if (this.ticksToReload > 0L && --this.ticksToReload == 0L) {
         this.reload();
      }

   }

   private void populateLists() {
      this.updateList(this.selectedPackList, this.model.getSelected());
      this.updateList(this.availablePackList, this.model.getUnselected());
      this.doneButton.active = !this.selectedPackList.children().isEmpty();
   }

   private void updateList(net.minecraft.client.gui.widget.list.ResourcePackList p_238899_1_, Stream<PackLoadingManager.IPack> p_238899_2_) {
      p_238899_1_.children().clear();
      p_238899_2_.filter(PackLoadingManager.IPack::notHidden).forEach((p_238898_2_) -> {
         p_238899_1_.children().add(new net.minecraft.client.gui.widget.list.ResourcePackList.ResourcePackEntry(this.minecraft, p_238899_1_, this, p_238898_2_));
      });
   }

   private void reload() {
      this.model.findNewPacks();
      this.populateLists();
      this.ticksToReload = 0L;
      this.packIcons.clear();
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderDirtBackground(0);
      this.availablePackList.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      this.selectedPackList.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
      drawCenteredString(p_230430_1_, this.font, this.title, this.width / 2, 8, 16777215);
      drawCenteredString(p_230430_1_, this.font, DRAG_AND_DROP, this.width / 2, 20, 16777215);
      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }

   protected static void copyPacks(Minecraft p_238895_0_, List<Path> p_238895_1_, Path p_238895_2_) {
      MutableBoolean mutableboolean = new MutableBoolean();
      p_238895_1_.forEach((p_238901_2_) -> {
         try (Stream<Path> stream = Files.walk(p_238901_2_)) {
            stream.forEach((p_238900_3_) -> {
               try {
                  Util.copyBetweenDirs(p_238901_2_.getParent(), p_238895_2_, p_238900_3_);
               } catch (IOException ioexception1) {
                  LOGGER.warn("Failed to copy datapack file  from {} to {}", p_238900_3_, p_238895_2_, ioexception1);
                  mutableboolean.setTrue();
               }

            });
         } catch (IOException ioexception) {
            LOGGER.warn("Failed to copy datapack file from {} to {}", p_238901_2_, p_238895_2_);
            mutableboolean.setTrue();
         }

      });
      if (mutableboolean.isTrue()) {
         SystemToast.onPackCopyFailure(p_238895_0_, p_238895_2_.toString());
      }

   }

   public void onFilesDrop(List<Path> p_230476_1_) {
      String s = p_230476_1_.stream().map(Path::getFileName).map(Path::toString).collect(Collectors.joining(", "));
      this.minecraft.setScreen(new ConfirmScreen((p_238902_2_) -> {
         if (p_238902_2_) {
            copyPacks(this.minecraft, p_230476_1_, this.packDir.toPath());
            this.reload();
         }

         this.minecraft.setScreen(this);
      }, new TranslationTextComponent("pack.dropConfirm"), new StringTextComponent(s)));
   }

   private ResourceLocation loadPackIcon(TextureManager p_243397_1_, ResourcePackInfo p_243397_2_) {
      try (
         IResourcePack iresourcepack = p_243397_2_.open();
         InputStream inputstream = iresourcepack.getRootResource("pack.png");
      ) {
         String s = p_243397_2_.getId();
         ResourceLocation resourcelocation = new ResourceLocation("minecraft", "pack/" + Util.sanitizeName(s, ResourceLocation::validPathChar) + "/" + Hashing.sha1().hashUnencodedChars(s) + "/icon");
         NativeImage nativeimage = NativeImage.read(inputstream);
         p_243397_1_.register(resourcelocation, new DynamicTexture(nativeimage));
         return resourcelocation;
      } catch (FileNotFoundException filenotfoundexception) {
      } catch (Exception exception) {
         LOGGER.warn("Failed to load icon from pack {}", p_243397_2_.getId(), exception);
      }

      return DEFAULT_ICON;
   }

   private ResourceLocation getPackIcon(ResourcePackInfo p_243395_1_) {
      return this.packIcons.computeIfAbsent(p_243395_1_.getId(), (p_243396_2_) -> {
         return this.loadPackIcon(this.minecraft.getTextureManager(), p_243395_1_);
      });
   }

   @OnlyIn(Dist.CLIENT)
   static class PackDirectoryWatcher implements AutoCloseable {
      private final WatchService watcher;
      private final Path packPath;

      public PackDirectoryWatcher(File p_i242061_1_) throws IOException {
         this.packPath = p_i242061_1_.toPath();
         this.watcher = this.packPath.getFileSystem().newWatchService();

         try {
            this.watchDir(this.packPath);

            try (DirectoryStream<Path> directorystream = Files.newDirectoryStream(this.packPath)) {
               for(Path path : directorystream) {
                  if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
                     this.watchDir(path);
                  }
               }
            }

         } catch (Exception exception) {
            this.watcher.close();
            throw exception;
         }
      }

      @Nullable
      public static PackScreen.PackDirectoryWatcher create(File p_243403_0_) {
         try {
            return new PackScreen.PackDirectoryWatcher(p_243403_0_);
         } catch (IOException ioexception) {
            PackScreen.LOGGER.warn("Failed to initialize pack directory {} monitoring", p_243403_0_, ioexception);
            return null;
         }
      }

      private void watchDir(Path p_243404_1_) throws IOException {
         p_243404_1_.register(this.watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
      }

      public boolean pollForChanges() throws IOException {
         boolean flag = false;

         WatchKey watchkey;
         while((watchkey = this.watcher.poll()) != null) {
            for(WatchEvent<?> watchevent : watchkey.pollEvents()) {
               flag = true;
               if (watchkey.watchable() == this.packPath && watchevent.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                  Path path = this.packPath.resolve((Path)watchevent.context());
                  if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
                     this.watchDir(path);
                  }
               }
            }

            watchkey.reset();
         }

         return flag;
      }

      public void close() throws IOException {
         this.watcher.close();
      }
   }
}
