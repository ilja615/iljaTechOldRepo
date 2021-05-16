package com.mojang.realmsclient.client;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.SharedConstants;
import net.minecraft.world.storage.FolderName;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class FileDownload {
   private static final Logger LOGGER = LogManager.getLogger();
   private volatile boolean cancelled;
   private volatile boolean finished;
   private volatile boolean error;
   private volatile boolean extracting;
   private volatile File tempFile;
   private volatile File resourcePackPath;
   private volatile HttpGet request;
   private Thread currentThread;
   private final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build();
   private static final String[] INVALID_FILE_NAMES = new String[]{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

   public long contentLength(String p_224827_1_) {
      CloseableHttpClient closeablehttpclient = null;
      HttpGet httpget = null;

      long i;
      try {
         httpget = new HttpGet(p_224827_1_);
         closeablehttpclient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
         CloseableHttpResponse closeablehttpresponse = closeablehttpclient.execute(httpget);
         return Long.parseLong(closeablehttpresponse.getFirstHeader("Content-Length").getValue());
      } catch (Throwable throwable) {
         LOGGER.error("Unable to get content length for download");
         i = 0L;
      } finally {
         if (httpget != null) {
            httpget.releaseConnection();
         }

         if (closeablehttpclient != null) {
            try {
               closeablehttpclient.close();
            } catch (IOException ioexception) {
               LOGGER.error("Could not close http client", (Throwable)ioexception);
            }
         }

      }

      return i;
   }

   public void download(WorldDownload p_237688_1_, String p_237688_2_, RealmsDownloadLatestWorldScreen.DownloadStatus p_237688_3_, SaveFormat p_237688_4_) {
      if (this.currentThread == null) {
         this.currentThread = new Thread(() -> {
            CloseableHttpClient closeablehttpclient = null;

            try {
               this.tempFile = File.createTempFile("backup", ".tar.gz");
               this.request = new HttpGet(p_237688_1_.downloadLink);
               closeablehttpclient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
               HttpResponse httpresponse = closeablehttpclient.execute(this.request);
               p_237688_3_.totalBytes = Long.parseLong(httpresponse.getFirstHeader("Content-Length").getValue());
               if (httpresponse.getStatusLine().getStatusCode() == 200) {
                  OutputStream outputstream = new FileOutputStream(this.tempFile);
                  FileDownload.ProgressListener filedownload$progresslistener = new FileDownload.ProgressListener(p_237688_2_.trim(), this.tempFile, p_237688_4_, p_237688_3_);
                  FileDownload.DownloadCountingOutputStream filedownload$downloadcountingoutputstream = new FileDownload.DownloadCountingOutputStream(outputstream);
                  filedownload$downloadcountingoutputstream.setListener(filedownload$progresslistener);
                  IOUtils.copy(httpresponse.getEntity().getContent(), filedownload$downloadcountingoutputstream);
                  return;
               }

               this.error = true;
               this.request.abort();
            } catch (Exception exception1) {
               LOGGER.error("Caught exception while downloading: " + exception1.getMessage());
               this.error = true;
               return;
            } finally {
               this.request.releaseConnection();
               if (this.tempFile != null) {
                  this.tempFile.delete();
               }

               if (!this.error) {
                  if (!p_237688_1_.resourcePackUrl.isEmpty() && !p_237688_1_.resourcePackHash.isEmpty()) {
                     try {
                        this.tempFile = File.createTempFile("resources", ".tar.gz");
                        this.request = new HttpGet(p_237688_1_.resourcePackUrl);
                        HttpResponse httpresponse1 = closeablehttpclient.execute(this.request);
                        p_237688_3_.totalBytes = Long.parseLong(httpresponse1.getFirstHeader("Content-Length").getValue());
                        if (httpresponse1.getStatusLine().getStatusCode() != 200) {
                           this.error = true;
                           this.request.abort();
                           return;
                        }

                        OutputStream outputstream1 = new FileOutputStream(this.tempFile);
                        FileDownload.ResourcePackProgressListener filedownload$resourcepackprogresslistener = new FileDownload.ResourcePackProgressListener(this.tempFile, p_237688_3_, p_237688_1_);
                        FileDownload.DownloadCountingOutputStream filedownload$downloadcountingoutputstream1 = new FileDownload.DownloadCountingOutputStream(outputstream1);
                        filedownload$downloadcountingoutputstream1.setListener(filedownload$resourcepackprogresslistener);
                        IOUtils.copy(httpresponse1.getEntity().getContent(), filedownload$downloadcountingoutputstream1);
                     } catch (Exception exception) {
                        LOGGER.error("Caught exception while downloading: " + exception.getMessage());
                        this.error = true;
                     } finally {
                        this.request.releaseConnection();
                        if (this.tempFile != null) {
                           this.tempFile.delete();
                        }

                     }
                  } else {
                     this.finished = true;
                  }
               }

               if (closeablehttpclient != null) {
                  try {
                     closeablehttpclient.close();
                  } catch (IOException ioexception) {
                     LOGGER.error("Failed to close Realms download client");
                  }
               }

            }

         });
         this.currentThread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
         this.currentThread.start();
      }
   }

   public void cancel() {
      if (this.request != null) {
         this.request.abort();
      }

      if (this.tempFile != null) {
         this.tempFile.delete();
      }

      this.cancelled = true;
   }

   public boolean isFinished() {
      return this.finished;
   }

   public boolean isError() {
      return this.error;
   }

   public boolean isExtracting() {
      return this.extracting;
   }

   public static String findAvailableFolderName(String p_224828_0_) {
      p_224828_0_ = p_224828_0_.replaceAll("[\\./\"]", "_");

      for(String s : INVALID_FILE_NAMES) {
         if (p_224828_0_.equalsIgnoreCase(s)) {
            p_224828_0_ = "_" + p_224828_0_ + "_";
         }
      }

      return p_224828_0_;
   }

   private void untarGzipArchive(String p_237690_1_, File p_237690_2_, SaveFormat p_237690_3_) throws IOException {
      Pattern pattern = Pattern.compile(".*-([0-9]+)$");
      int i = 1;

      for(char c0 : SharedConstants.ILLEGAL_FILE_CHARACTERS) {
         p_237690_1_ = p_237690_1_.replace(c0, '_');
      }

      if (StringUtils.isEmpty(p_237690_1_)) {
         p_237690_1_ = "Realm";
      }

      p_237690_1_ = findAvailableFolderName(p_237690_1_);

      try {
         for(WorldSummary worldsummary : p_237690_3_.getLevelList()) {
            if (worldsummary.getLevelId().toLowerCase(Locale.ROOT).startsWith(p_237690_1_.toLowerCase(Locale.ROOT))) {
               Matcher matcher = pattern.matcher(worldsummary.getLevelId());
               if (matcher.matches()) {
                  if (Integer.valueOf(matcher.group(1)) > i) {
                     i = Integer.valueOf(matcher.group(1));
                  }
               } else {
                  ++i;
               }
            }
         }
      } catch (Exception exception1) {
         LOGGER.error("Error getting level list", (Throwable)exception1);
         this.error = true;
         return;
      }

      String s;
      if (p_237690_3_.isNewLevelIdAcceptable(p_237690_1_) && i <= 1) {
         s = p_237690_1_;
      } else {
         s = p_237690_1_ + (i == 1 ? "" : "-" + i);
         if (!p_237690_3_.isNewLevelIdAcceptable(s)) {
            boolean flag = false;

            while(!flag) {
               ++i;
               s = p_237690_1_ + (i == 1 ? "" : "-" + i);
               if (p_237690_3_.isNewLevelIdAcceptable(s)) {
                  flag = true;
               }
            }
         }
      }

      TarArchiveInputStream tararchiveinputstream = null;
      File file1 = new File(Minecraft.getInstance().gameDirectory.getAbsolutePath(), "saves");

      try {
         file1.mkdir();
         tararchiveinputstream = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(p_237690_2_))));

         for(TarArchiveEntry tararchiveentry = tararchiveinputstream.getNextTarEntry(); tararchiveentry != null; tararchiveentry = tararchiveinputstream.getNextTarEntry()) {
            File file2 = new File(file1, tararchiveentry.getName().replace("world", s));
            if (tararchiveentry.isDirectory()) {
               file2.mkdirs();
            } else {
               file2.createNewFile();

               try (FileOutputStream fileoutputstream = new FileOutputStream(file2)) {
                  IOUtils.copy(tararchiveinputstream, fileoutputstream);
               }
            }
         }
      } catch (Exception exception) {
         LOGGER.error("Error extracting world", (Throwable)exception);
         this.error = true;
      } finally {
         if (tararchiveinputstream != null) {
            tararchiveinputstream.close();
         }

         if (p_237690_2_ != null) {
            p_237690_2_.delete();
         }

         try (SaveFormat.LevelSave saveformat$levelsave = p_237690_3_.createAccess(s)) {
            saveformat$levelsave.renameLevel(s.trim());
            Path path = saveformat$levelsave.getLevelPath(FolderName.LEVEL_DATA_FILE);
            deletePlayerTag(path.toFile());
         } catch (IOException ioexception) {
            LOGGER.error("Failed to rename unpacked realms level {}", s, ioexception);
         }

         this.resourcePackPath = new File(file1, s + File.separator + "resources.zip");
      }

   }

   private static void deletePlayerTag(File p_237689_0_) {
      if (p_237689_0_.exists()) {
         try {
            CompoundNBT compoundnbt = CompressedStreamTools.readCompressed(p_237689_0_);
            CompoundNBT compoundnbt1 = compoundnbt.getCompound("Data");
            compoundnbt1.remove("Player");
            CompressedStreamTools.writeCompressed(compoundnbt, p_237689_0_);
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   class DownloadCountingOutputStream extends CountingOutputStream {
      private ActionListener listener;

      public DownloadCountingOutputStream(OutputStream p_i51649_2_) {
         super(p_i51649_2_);
      }

      public void setListener(ActionListener p_224804_1_) {
         this.listener = p_224804_1_;
      }

      protected void afterWrite(int p_afterWrite_1_) throws IOException {
         super.afterWrite(p_afterWrite_1_);
         if (this.listener != null) {
            this.listener.actionPerformed(new ActionEvent(this, 0, (String)null));
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   class ProgressListener implements ActionListener {
      private final String worldName;
      private final File tempFile;
      private final SaveFormat levelStorageSource;
      private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;

      private ProgressListener(String p_i232192_2_, File p_i232192_3_, SaveFormat p_i232192_4_, RealmsDownloadLatestWorldScreen.DownloadStatus p_i232192_5_) {
         this.worldName = p_i232192_2_;
         this.tempFile = p_i232192_3_;
         this.levelStorageSource = p_i232192_4_;
         this.downloadStatus = p_i232192_5_;
      }

      public void actionPerformed(ActionEvent p_actionPerformed_1_) {
         this.downloadStatus.bytesWritten = ((FileDownload.DownloadCountingOutputStream)p_actionPerformed_1_.getSource()).getByteCount();
         if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled && !FileDownload.this.error) {
            try {
               FileDownload.this.extracting = true;
               FileDownload.this.untarGzipArchive(this.worldName, this.tempFile, this.levelStorageSource);
            } catch (IOException ioexception) {
               FileDownload.LOGGER.error("Error extracting archive", (Throwable)ioexception);
               FileDownload.this.error = true;
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   class ResourcePackProgressListener implements ActionListener {
      private final File tempFile;
      private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
      private final WorldDownload worldDownload;

      private ResourcePackProgressListener(File p_i51645_2_, RealmsDownloadLatestWorldScreen.DownloadStatus p_i51645_3_, WorldDownload p_i51645_4_) {
         this.tempFile = p_i51645_2_;
         this.downloadStatus = p_i51645_3_;
         this.worldDownload = p_i51645_4_;
      }

      public void actionPerformed(ActionEvent p_actionPerformed_1_) {
         this.downloadStatus.bytesWritten = ((FileDownload.DownloadCountingOutputStream)p_actionPerformed_1_.getSource()).getByteCount();
         if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled) {
            try {
               String s = Hashing.sha1().hashBytes(Files.toByteArray(this.tempFile)).toString();
               if (s.equals(this.worldDownload.resourcePackHash)) {
                  FileUtils.copyFile(this.tempFile, FileDownload.this.resourcePackPath);
                  FileDownload.this.finished = true;
               } else {
                  FileDownload.LOGGER.error("Resourcepack had wrong hash (expected " + this.worldDownload.resourcePackHash + ", found " + s + "). Deleting it.");
                  FileUtils.deleteQuietly(this.tempFile);
                  FileDownload.this.error = true;
               }
            } catch (IOException ioexception) {
               FileDownload.LOGGER.error("Error copying resourcepack file", (Object)ioexception.getMessage());
               FileDownload.this.error = true;
            }
         }

      }
   }
}
