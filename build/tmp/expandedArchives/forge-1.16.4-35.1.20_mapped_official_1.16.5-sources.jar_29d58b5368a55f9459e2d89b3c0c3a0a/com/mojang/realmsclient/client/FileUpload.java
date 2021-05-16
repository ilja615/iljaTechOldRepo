package com.mojang.realmsclient.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.gui.screens.UploadResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import net.minecraft.util.Session;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class FileUpload {
   private static final Logger LOGGER = LogManager.getLogger();
   private final File file;
   private final long worldId;
   private final int slotId;
   private final UploadInfo uploadInfo;
   private final String sessionId;
   private final String username;
   private final String clientVersion;
   private final UploadStatus uploadStatus;
   private final AtomicBoolean cancelled = new AtomicBoolean(false);
   private CompletableFuture<UploadResult> uploadTask;
   private final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout((int)TimeUnit.MINUTES.toMillis(10L)).setConnectTimeout((int)TimeUnit.SECONDS.toMillis(15L)).build();

   public FileUpload(File p_i232194_1_, long p_i232194_2_, int p_i232194_4_, UploadInfo p_i232194_5_, Session p_i232194_6_, String p_i232194_7_, UploadStatus p_i232194_8_) {
      this.file = p_i232194_1_;
      this.worldId = p_i232194_2_;
      this.slotId = p_i232194_4_;
      this.uploadInfo = p_i232194_5_;
      this.sessionId = p_i232194_6_.getSessionId();
      this.username = p_i232194_6_.getName();
      this.clientVersion = p_i232194_7_;
      this.uploadStatus = p_i232194_8_;
   }

   public void upload(Consumer<UploadResult> p_224874_1_) {
      if (this.uploadTask == null) {
         this.uploadTask = CompletableFuture.supplyAsync(() -> {
            return this.requestUpload(0);
         });
         this.uploadTask.thenAccept(p_224874_1_);
      }
   }

   public void cancel() {
      this.cancelled.set(true);
      if (this.uploadTask != null) {
         this.uploadTask.cancel(false);
         this.uploadTask = null;
      }

   }

   private UploadResult requestUpload(int p_224879_1_) {
      UploadResult.Builder uploadresult$builder = new UploadResult.Builder();
      if (this.cancelled.get()) {
         return uploadresult$builder.build();
      } else {
         this.uploadStatus.totalBytes = this.file.length();
         HttpPost httppost = new HttpPost(this.uploadInfo.getUploadEndpoint().resolve("/upload/" + this.worldId + "/" + this.slotId));
         CloseableHttpClient closeablehttpclient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();

         UploadResult uploadresult;
         try {
            this.setupRequest(httppost);
            HttpResponse httpresponse = closeablehttpclient.execute(httppost);
            long i = this.getRetryDelaySeconds(httpresponse);
            if (!this.shouldRetry(i, p_224879_1_)) {
               this.handleResponse(httpresponse, uploadresult$builder);
               return uploadresult$builder.build();
            }

            uploadresult = this.retryUploadAfter(i, p_224879_1_);
         } catch (Exception exception) {
            if (!this.cancelled.get()) {
               LOGGER.error("Caught exception while uploading: ", (Throwable)exception);
            }

            return uploadresult$builder.build();
         } finally {
            this.cleanup(httppost, closeablehttpclient);
         }

         return uploadresult;
      }
   }

   private void cleanup(HttpPost p_224877_1_, CloseableHttpClient p_224877_2_) {
      p_224877_1_.releaseConnection();
      if (p_224877_2_ != null) {
         try {
            p_224877_2_.close();
         } catch (IOException ioexception) {
            LOGGER.error("Failed to close Realms upload client");
         }
      }

   }

   private void setupRequest(HttpPost p_224872_1_) throws FileNotFoundException {
      p_224872_1_.setHeader("Cookie", "sid=" + this.sessionId + ";token=" + this.uploadInfo.getToken() + ";user=" + this.username + ";version=" + this.clientVersion);
      FileUpload.CustomInputStreamEntity fileupload$custominputstreamentity = new FileUpload.CustomInputStreamEntity(new FileInputStream(this.file), this.file.length(), this.uploadStatus);
      fileupload$custominputstreamentity.setContentType("application/octet-stream");
      p_224872_1_.setEntity(fileupload$custominputstreamentity);
   }

   private void handleResponse(HttpResponse p_224875_1_, UploadResult.Builder p_224875_2_) throws IOException {
      int i = p_224875_1_.getStatusLine().getStatusCode();
      if (i == 401) {
         LOGGER.debug("Realms server returned 401: " + p_224875_1_.getFirstHeader("WWW-Authenticate"));
      }

      p_224875_2_.withStatusCode(i);
      if (p_224875_1_.getEntity() != null) {
         String s = EntityUtils.toString(p_224875_1_.getEntity(), "UTF-8");
         if (s != null) {
            try {
               JsonParser jsonparser = new JsonParser();
               JsonElement jsonelement = jsonparser.parse(s).getAsJsonObject().get("errorMsg");
               Optional<String> optional = Optional.ofNullable(jsonelement).map(JsonElement::getAsString);
               p_224875_2_.withErrorMessage(optional.orElse((String)null));
            } catch (Exception exception) {
            }
         }
      }

   }

   private boolean shouldRetry(long p_224882_1_, int p_224882_3_) {
      return p_224882_1_ > 0L && p_224882_3_ + 1 < 5;
   }

   private UploadResult retryUploadAfter(long p_224876_1_, int p_224876_3_) throws InterruptedException {
      Thread.sleep(Duration.ofSeconds(p_224876_1_).toMillis());
      return this.requestUpload(p_224876_3_ + 1);
   }

   private long getRetryDelaySeconds(HttpResponse p_224880_1_) {
      return Optional.ofNullable(p_224880_1_.getFirstHeader("Retry-After")).map(Header::getValue).map(Long::valueOf).orElse(0L);
   }

   public boolean isFinished() {
      return this.uploadTask.isDone() || this.uploadTask.isCancelled();
   }

   @OnlyIn(Dist.CLIENT)
   static class CustomInputStreamEntity extends InputStreamEntity {
      private final long length;
      private final InputStream content;
      private final UploadStatus uploadStatus;

      public CustomInputStreamEntity(InputStream p_i51622_1_, long p_i51622_2_, UploadStatus p_i51622_4_) {
         super(p_i51622_1_);
         this.content = p_i51622_1_;
         this.length = p_i51622_2_;
         this.uploadStatus = p_i51622_4_;
      }

      public void writeTo(OutputStream p_writeTo_1_) throws IOException {
         Args.notNull(p_writeTo_1_, "Output stream");
         InputStream inputstream = this.content;

         try {
            byte[] abyte = new byte[4096];
            int j;
            if (this.length < 0L) {
               while((j = inputstream.read(abyte)) != -1) {
                  p_writeTo_1_.write(abyte, 0, j);
                  this.uploadStatus.bytesWritten += (long)j;
               }
            } else {
               long i = this.length;

               while(i > 0L) {
                  j = inputstream.read(abyte, 0, (int)Math.min(4096L, i));
                  if (j == -1) {
                     break;
                  }

                  p_writeTo_1_.write(abyte, 0, j);
                  this.uploadStatus.bytesWritten += (long)j;
                  i -= (long)j;
                  p_writeTo_1_.flush();
               }
            }
         } finally {
            inputstream.close();
         }

      }
   }
}
