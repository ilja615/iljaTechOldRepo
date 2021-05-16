package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.WorkingScreen;
import net.minecraft.resources.FilePack;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.VanillaPack;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.HTTPUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class DownloadingPackFinder implements IPackFinder {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
   private final VanillaPack vanillaPack;
   private final File serverPackDir;
   private final ReentrantLock downloadLock = new ReentrantLock();
   private final ResourceIndex assetIndex;
   @Nullable
   private CompletableFuture<?> currentDownload;
   @Nullable
   private ResourcePackInfo serverPack;

   public DownloadingPackFinder(File p_i48116_1_, ResourceIndex p_i48116_2_) {
      this.serverPackDir = p_i48116_1_;
      this.assetIndex = p_i48116_2_;
      this.vanillaPack = new VirtualAssetsPack(p_i48116_2_);
   }

   public void loadPacks(Consumer<ResourcePackInfo> p_230230_1_, ResourcePackInfo.IFactory p_230230_2_) {
      ResourcePackInfo resourcepackinfo = ResourcePackInfo.create("vanilla", true, () -> {
         return this.vanillaPack;
      }, p_230230_2_, ResourcePackInfo.Priority.BOTTOM, IPackNameDecorator.BUILT_IN);
      if (resourcepackinfo != null) {
         p_230230_1_.accept(resourcepackinfo);
      }

      if (this.serverPack != null) {
         p_230230_1_.accept(this.serverPack);
      }

      ResourcePackInfo resourcepackinfo1 = this.createProgrammerArtPack(p_230230_2_);
      if (resourcepackinfo1 != null) {
         p_230230_1_.accept(resourcepackinfo1);
      }

   }

   public VanillaPack getVanillaPack() {
      return this.vanillaPack;
   }

   private static Map<String, String> getDownloadHeaders() {
      Map<String, String> map = Maps.newHashMap();
      map.put("X-Minecraft-Username", Minecraft.getInstance().getUser().getName());
      map.put("X-Minecraft-UUID", Minecraft.getInstance().getUser().getUuid());
      map.put("X-Minecraft-Version", SharedConstants.getCurrentVersion().getName());
      map.put("X-Minecraft-Version-ID", SharedConstants.getCurrentVersion().getId());
      map.put("X-Minecraft-Pack-Format", String.valueOf(SharedConstants.getCurrentVersion().getPackVersion()));
      map.put("User-Agent", "Minecraft Java/" + SharedConstants.getCurrentVersion().getName());
      return map;
   }

   public CompletableFuture<?> downloadAndSelectResourcePack(String p_217818_1_, String p_217818_2_) {
      String s = DigestUtils.sha1Hex(p_217818_1_);
      String s1 = SHA1.matcher(p_217818_2_).matches() ? p_217818_2_ : "";
      this.downloadLock.lock();

      CompletableFuture completablefuture1;
      try {
         this.clearServerPack();
         this.clearOldDownloads();
         File file1 = new File(this.serverPackDir, s);
         CompletableFuture<?> completablefuture;
         if (file1.exists()) {
            completablefuture = CompletableFuture.completedFuture("");
         } else {
            WorkingScreen workingscreen = new WorkingScreen();
            Map<String, String> map = getDownloadHeaders();
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.executeBlocking(() -> {
               minecraft.setScreen(workingscreen);
            });
            completablefuture = HTTPUtil.downloadTo(file1, p_217818_1_, map, 104857600, workingscreen, minecraft.getProxy());
         }

         this.currentDownload = completablefuture.thenCompose((p_217812_3_) -> {
            return !this.checkHash(s1, file1) ? Util.failedFuture(new RuntimeException("Hash check failure for file " + file1 + ", see log")) : this.setServerPack(file1, IPackNameDecorator.SERVER);
         }).whenComplete((p_217815_1_, p_217815_2_) -> {
            if (p_217815_2_ != null) {
               LOGGER.warn("Pack application failed: {}, deleting file {}", p_217815_2_.getMessage(), file1);
               deleteQuietly(file1);
            }

         });
         completablefuture1 = this.currentDownload;
      } finally {
         this.downloadLock.unlock();
      }

      return completablefuture1;
   }

   private static void deleteQuietly(File p_217811_0_) {
      try {
         Files.delete(p_217811_0_.toPath());
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to delete file {}: {}", p_217811_0_, ioexception.getMessage());
      }

   }

   public void clearServerPack() {
      this.downloadLock.lock();

      try {
         if (this.currentDownload != null) {
            this.currentDownload.cancel(true);
         }

         this.currentDownload = null;
         if (this.serverPack != null) {
            this.serverPack = null;
            Minecraft.getInstance().delayTextureReload();
         }
      } finally {
         this.downloadLock.unlock();
      }

   }

   private boolean checkHash(String p_195745_1_, File p_195745_2_) {
      try (FileInputStream fileinputstream = new FileInputStream(p_195745_2_)) {
         String s = DigestUtils.sha1Hex((InputStream)fileinputstream);
         if (p_195745_1_.isEmpty()) {
            LOGGER.info("Found file {} without verification hash", (Object)p_195745_2_);
            return true;
         }

         if (s.toLowerCase(Locale.ROOT).equals(p_195745_1_.toLowerCase(Locale.ROOT))) {
            LOGGER.info("Found file {} matching requested hash {}", p_195745_2_, p_195745_1_);
            return true;
         }

         LOGGER.warn("File {} had wrong hash (expected {}, found {}).", p_195745_2_, p_195745_1_, s);
      } catch (IOException ioexception) {
         LOGGER.warn("File {} couldn't be hashed.", p_195745_2_, ioexception);
      }

      return false;
   }

   private void clearOldDownloads() {
      try {
         List<File> list = Lists.newArrayList(FileUtils.listFiles(this.serverPackDir, TrueFileFilter.TRUE, (IOFileFilter)null));
         list.sort(LastModifiedFileComparator.LASTMODIFIED_REVERSE);
         int i = 0;

         for(File file1 : list) {
            if (i++ >= 10) {
               LOGGER.info("Deleting old server resource pack {}", (Object)file1.getName());
               FileUtils.deleteQuietly(file1);
            }
         }
      } catch (IllegalArgumentException illegalargumentexception) {
         LOGGER.error("Error while deleting old server resource pack : {}", (Object)illegalargumentexception.getMessage());
      }

   }

   public CompletableFuture<Void> setServerPack(File p_217816_1_, IPackNameDecorator p_217816_2_) {
      PackMetadataSection packmetadatasection;
      try (FilePack filepack = new FilePack(p_217816_1_)) {
         packmetadatasection = filepack.getMetadataSection(PackMetadataSection.SERIALIZER);
      } catch (IOException ioexception) {
         return Util.failedFuture(new IOException(String.format("Invalid resourcepack at %s", p_217816_1_), ioexception));
      }

      LOGGER.info("Applying server pack {}", (Object)p_217816_1_);
      this.serverPack = new ResourcePackInfo("server", true, () -> {
         return new FilePack(p_217816_1_);
      }, new TranslationTextComponent("resourcePack.server.name"), packmetadatasection.getDescription(), PackCompatibility.forFormat(packmetadatasection.getPackFormat()), ResourcePackInfo.Priority.TOP, true, p_217816_2_);
      return Minecraft.getInstance().delayTextureReload();
   }

   @Nullable
   private ResourcePackInfo createProgrammerArtPack(ResourcePackInfo.IFactory p_239453_1_) {
      ResourcePackInfo resourcepackinfo = null;
      File file1 = this.assetIndex.getFile(new ResourceLocation("resourcepacks/programmer_art.zip"));
      if (file1 != null && file1.isFile()) {
         resourcepackinfo = createProgrammerArtPack(p_239453_1_, () -> {
            return createProgrammerArtZipPack(file1);
         });
      }

      if (resourcepackinfo == null && SharedConstants.IS_RUNNING_IN_IDE) {
         File file2 = this.assetIndex.getRootFile("../resourcepacks/programmer_art");
         if (file2 != null && file2.isDirectory()) {
            resourcepackinfo = createProgrammerArtPack(p_239453_1_, () -> {
               return createProgrammerArtDirPack(file2);
            });
         }
      }

      return resourcepackinfo;
   }

   @Nullable
   private static ResourcePackInfo createProgrammerArtPack(ResourcePackInfo.IFactory p_239454_0_, Supplier<IResourcePack> p_239454_1_) {
      return ResourcePackInfo.create("programer_art", false, p_239454_1_, p_239454_0_, ResourcePackInfo.Priority.TOP, IPackNameDecorator.BUILT_IN);
   }

   private static FolderPack createProgrammerArtDirPack(File p_239459_0_) {
      return new FolderPack(p_239459_0_) {
         public String getName() {
            return "Programmer Art";
         }
      };
   }

   private static IResourcePack createProgrammerArtZipPack(File p_239460_0_) {
      return new FilePack(p_239460_0_) {
         public String getName() {
            return "Programmer Art";
         }
      };
   }
}
