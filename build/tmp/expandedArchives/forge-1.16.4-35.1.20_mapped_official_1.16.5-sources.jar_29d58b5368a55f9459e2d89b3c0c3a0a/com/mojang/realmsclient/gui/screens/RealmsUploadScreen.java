package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.FileUpload;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.client.UploadStatus;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.util.UploadTokenCache;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.UploadSpeed;
import net.minecraft.realms.RealmsNarratorHelper;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsUploadScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ReentrantLock UPLOAD_LOCK = new ReentrantLock();
   private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
   private static final ITextComponent VERIFYING_TEXT = new TranslationTextComponent("mco.upload.verifying");
   private final RealmsResetWorldScreen lastScreen;
   private final WorldSummary selectedLevel;
   private final long worldId;
   private final int slotId;
   private final UploadStatus uploadStatus;
   private final RateLimiter narrationRateLimiter;
   private volatile ITextComponent[] errorMessage;
   private volatile ITextComponent status = new TranslationTextComponent("mco.upload.preparing");
   private volatile String progress;
   private volatile boolean cancelled;
   private volatile boolean uploadFinished;
   private volatile boolean showDots = true;
   private volatile boolean uploadStarted;
   private Button backButton;
   private Button cancelButton;
   private int tickCount;
   private Long previousWrittenBytes;
   private Long previousTimeSnapshot;
   private long bytesPersSecond;
   private final Runnable callback;

   public RealmsUploadScreen(long p_i232226_1_, int p_i232226_3_, RealmsResetWorldScreen p_i232226_4_, WorldSummary p_i232226_5_, Runnable p_i232226_6_) {
      this.worldId = p_i232226_1_;
      this.slotId = p_i232226_3_;
      this.lastScreen = p_i232226_4_;
      this.selectedLevel = p_i232226_5_;
      this.uploadStatus = new UploadStatus();
      this.narrationRateLimiter = RateLimiter.create((double)0.1F);
      this.callback = p_i232226_6_;
   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.backButton = this.addButton(new Button(this.width / 2 - 100, this.height - 42, 200, 20, DialogTexts.GUI_BACK, (p_238087_1_) -> {
         this.onBack();
      }));
      this.backButton.visible = false;
      this.cancelButton = this.addButton(new Button(this.width / 2 - 100, this.height - 42, 200, 20, DialogTexts.GUI_CANCEL, (p_238084_1_) -> {
         this.onCancel();
      }));
      if (!this.uploadStarted) {
         if (this.lastScreen.slot == -1) {
            this.upload();
         } else {
            this.lastScreen.switchSlot(() -> {
               if (!this.uploadStarted) {
                  this.uploadStarted = true;
                  this.minecraft.setScreen(this);
                  this.upload();
               }

            });
         }
      }

   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void onBack() {
      this.callback.run();
   }

   private void onCancel() {
      this.cancelled = true;
      this.minecraft.setScreen(this.lastScreen);
   }

   public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
      if (p_231046_1_ == 256) {
         if (this.showDots) {
            this.onCancel();
         } else {
            this.onBack();
         }

         return true;
      } else {
         return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
      }
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      this.renderBackground(p_230430_1_);
      if (!this.uploadFinished && this.uploadStatus.bytesWritten != 0L && this.uploadStatus.bytesWritten == this.uploadStatus.totalBytes) {
         this.status = VERIFYING_TEXT;
         this.cancelButton.active = false;
      }

      drawCenteredString(p_230430_1_, this.font, this.status, this.width / 2, 50, 16777215);
      if (this.showDots) {
         this.drawDots(p_230430_1_);
      }

      if (this.uploadStatus.bytesWritten != 0L && !this.cancelled) {
         this.drawProgressBar(p_230430_1_);
         this.drawUploadSpeed(p_230430_1_);
      }

      if (this.errorMessage != null) {
         for(int i = 0; i < this.errorMessage.length; ++i) {
            drawCenteredString(p_230430_1_, this.font, this.errorMessage[i], this.width / 2, 110 + 12 * i, 16711680);
         }
      }

      super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
   }

   private void drawDots(MatrixStack p_238086_1_) {
      int i = this.font.width(this.status);
      this.font.draw(p_238086_1_, DOTS[this.tickCount / 10 % DOTS.length], (float)(this.width / 2 + i / 2 + 5), 50.0F, 16777215);
   }

   private void drawProgressBar(MatrixStack p_238088_1_) {
      double d0 = Math.min((double)this.uploadStatus.bytesWritten / (double)this.uploadStatus.totalBytes, 1.0D);
      this.progress = String.format(Locale.ROOT, "%.1f", d0 * 100.0D);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableTexture();
      double d1 = (double)(this.width / 2 - 100);
      double d2 = 0.5D;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuilder();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
      bufferbuilder.vertex(d1 - 0.5D, 95.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      bufferbuilder.vertex(d1 + 200.0D * d0 + 0.5D, 95.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      bufferbuilder.vertex(d1 + 200.0D * d0 + 0.5D, 79.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      bufferbuilder.vertex(d1 - 0.5D, 79.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      bufferbuilder.vertex(d1, 95.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      bufferbuilder.vertex(d1 + 200.0D * d0, 95.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      bufferbuilder.vertex(d1 + 200.0D * d0, 80.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      bufferbuilder.vertex(d1, 80.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      tessellator.end();
      RenderSystem.enableTexture();
      drawCenteredString(p_238088_1_, this.font, this.progress + " %", this.width / 2, 84, 16777215);
   }

   private void drawUploadSpeed(MatrixStack p_238089_1_) {
      if (this.tickCount % 20 == 0) {
         if (this.previousWrittenBytes != null) {
            long i = Util.getMillis() - this.previousTimeSnapshot;
            if (i == 0L) {
               i = 1L;
            }

            this.bytesPersSecond = 1000L * (this.uploadStatus.bytesWritten - this.previousWrittenBytes) / i;
            this.drawUploadSpeed0(p_238089_1_, this.bytesPersSecond);
         }

         this.previousWrittenBytes = this.uploadStatus.bytesWritten;
         this.previousTimeSnapshot = Util.getMillis();
      } else {
         this.drawUploadSpeed0(p_238089_1_, this.bytesPersSecond);
      }

   }

   private void drawUploadSpeed0(MatrixStack p_238083_1_, long p_238083_2_) {
      if (p_238083_2_ > 0L) {
         int i = this.font.width(this.progress);
         String s = "(" + UploadSpeed.humanReadable(p_238083_2_) + "/s)";
         this.font.draw(p_238083_1_, s, (float)(this.width / 2 + i / 2 + 15), 84.0F, 16777215);
      }

   }

   public void tick() {
      super.tick();
      ++this.tickCount;
      if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
         List<String> list = Lists.newArrayList();
         list.add(this.status.getString());
         if (this.progress != null) {
            list.add(this.progress + "%");
         }

         if (this.errorMessage != null) {
            Stream.of(this.errorMessage).map(ITextComponent::getString).forEach(list::add);
         }

         RealmsNarratorHelper.now(String.join(System.lineSeparator(), list));
      }

   }

   private void upload() {
      this.uploadStarted = true;
      (new Thread(() -> {
         File file1 = null;
         RealmsClient realmsclient = RealmsClient.create();
         long i = this.worldId;

         try {
            if (UPLOAD_LOCK.tryLock(1L, TimeUnit.SECONDS)) {
               UploadInfo uploadinfo = null;

               for(int j = 0; j < 20; ++j) {
                  try {
                     if (this.cancelled) {
                        this.uploadCancelled();
                        return;
                     }

                     uploadinfo = realmsclient.requestUploadInfo(i, UploadTokenCache.get(i));
                     if (uploadinfo != null) {
                        break;
                     }
                  } catch (RetryCallException retrycallexception) {
                     Thread.sleep((long)(retrycallexception.delaySeconds * 1000));
                  }
               }

               if (uploadinfo == null) {
                  this.status = new TranslationTextComponent("mco.upload.close.failure");
                  return;
               }

               UploadTokenCache.put(i, uploadinfo.getToken());
               if (!uploadinfo.isWorldClosed()) {
                  this.status = new TranslationTextComponent("mco.upload.close.failure");
                  return;
               }

               if (this.cancelled) {
                  this.uploadCancelled();
                  return;
               }

               File file2 = new File(this.minecraft.gameDirectory.getAbsolutePath(), "saves");
               file1 = this.tarGzipArchive(new File(file2, this.selectedLevel.getLevelId()));
               if (this.cancelled) {
                  this.uploadCancelled();
                  return;
               }

               if (this.verify(file1)) {
                  this.status = new TranslationTextComponent("mco.upload.uploading", this.selectedLevel.getLevelName());
                  FileUpload fileupload = new FileUpload(file1, this.worldId, this.slotId, uploadinfo, this.minecraft.getUser(), SharedConstants.getCurrentVersion().getName(), this.uploadStatus);
                  fileupload.upload((p_238082_3_) -> {
                     if (p_238082_3_.statusCode >= 200 && p_238082_3_.statusCode < 300) {
                        this.uploadFinished = true;
                        this.status = new TranslationTextComponent("mco.upload.done");
                        this.backButton.setMessage(DialogTexts.GUI_DONE);
                        UploadTokenCache.invalidate(i);
                     } else if (p_238082_3_.statusCode == 400 && p_238082_3_.errorMessage != null) {
                        this.setErrorMessage(new TranslationTextComponent("mco.upload.failed", p_238082_3_.errorMessage));
                     } else {
                        this.setErrorMessage(new TranslationTextComponent("mco.upload.failed", p_238082_3_.statusCode));
                     }

                  });

                  while(!fileupload.isFinished()) {
                     if (this.cancelled) {
                        fileupload.cancel();
                        this.uploadCancelled();
                        return;
                     }

                     try {
                        Thread.sleep(500L);
                     } catch (InterruptedException interruptedexception) {
                        LOGGER.error("Failed to check Realms file upload status");
                     }
                  }

                  return;
               }

               long k = file1.length();
               UploadSpeed uploadspeed = UploadSpeed.getLargest(k);
               UploadSpeed uploadspeed1 = UploadSpeed.getLargest(5368709120L);
               if (UploadSpeed.humanReadable(k, uploadspeed).equals(UploadSpeed.humanReadable(5368709120L, uploadspeed1)) && uploadspeed != UploadSpeed.B) {
                  UploadSpeed uploadspeed2 = UploadSpeed.values()[uploadspeed.ordinal() - 1];
                  this.setErrorMessage(new TranslationTextComponent("mco.upload.size.failure.line1", this.selectedLevel.getLevelName()), new TranslationTextComponent("mco.upload.size.failure.line2", UploadSpeed.humanReadable(k, uploadspeed2), UploadSpeed.humanReadable(5368709120L, uploadspeed2)));
                  return;
               }

               this.setErrorMessage(new TranslationTextComponent("mco.upload.size.failure.line1", this.selectedLevel.getLevelName()), new TranslationTextComponent("mco.upload.size.failure.line2", UploadSpeed.humanReadable(k, uploadspeed), UploadSpeed.humanReadable(5368709120L, uploadspeed1)));
               return;
            }

            this.status = new TranslationTextComponent("mco.upload.close.failure");
         } catch (IOException ioexception) {
            this.setErrorMessage(new TranslationTextComponent("mco.upload.failed", ioexception.getMessage()));
            return;
         } catch (RealmsServiceException realmsserviceexception) {
            this.setErrorMessage(new TranslationTextComponent("mco.upload.failed", realmsserviceexception.toString()));
            return;
         } catch (InterruptedException interruptedexception1) {
            LOGGER.error("Could not acquire upload lock");
            return;
         } finally {
            this.uploadFinished = true;
            if (UPLOAD_LOCK.isHeldByCurrentThread()) {
               UPLOAD_LOCK.unlock();
               this.showDots = false;
               this.backButton.visible = true;
               this.cancelButton.visible = false;
               if (file1 != null) {
                  LOGGER.debug("Deleting file " + file1.getAbsolutePath());
                  file1.delete();
               }

            }

            return;
         }

      })).start();
   }

   private void setErrorMessage(ITextComponent... p_238085_1_) {
      this.errorMessage = p_238085_1_;
   }

   private void uploadCancelled() {
      this.status = new TranslationTextComponent("mco.upload.cancelled");
      LOGGER.debug("Upload was cancelled");
   }

   private boolean verify(File p_224692_1_) {
      return p_224692_1_.length() < 5368709120L;
   }

   private File tarGzipArchive(File p_224675_1_) throws IOException {
      TarArchiveOutputStream tararchiveoutputstream = null;

      File file2;
      try {
         File file1 = File.createTempFile("realms-upload-file", ".tar.gz");
         tararchiveoutputstream = new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(file1)));
         tararchiveoutputstream.setLongFileMode(3);
         this.addFileToTarGz(tararchiveoutputstream, p_224675_1_.getAbsolutePath(), "world", true);
         tararchiveoutputstream.finish();
         file2 = file1;
      } finally {
         if (tararchiveoutputstream != null) {
            tararchiveoutputstream.close();
         }

      }

      return file2;
   }

   private void addFileToTarGz(TarArchiveOutputStream p_224669_1_, String p_224669_2_, String p_224669_3_, boolean p_224669_4_) throws IOException {
      if (!this.cancelled) {
         File file1 = new File(p_224669_2_);
         String s = p_224669_4_ ? p_224669_3_ : p_224669_3_ + file1.getName();
         TarArchiveEntry tararchiveentry = new TarArchiveEntry(file1, s);
         p_224669_1_.putArchiveEntry(tararchiveentry);
         if (file1.isFile()) {
            IOUtils.copy(new FileInputStream(file1), p_224669_1_);
            p_224669_1_.closeArchiveEntry();
         } else {
            p_224669_1_.closeArchiveEntry();
            File[] afile = file1.listFiles();
            if (afile != null) {
               for(File file2 : afile) {
                  this.addFileToTarGz(p_224669_1_, file2.getAbsolutePath(), s + "/", false);
               }
            }
         }

      }
   }
}
