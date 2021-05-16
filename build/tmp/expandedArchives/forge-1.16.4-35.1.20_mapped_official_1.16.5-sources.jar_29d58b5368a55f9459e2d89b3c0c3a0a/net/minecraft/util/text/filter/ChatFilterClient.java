package net.minecraft.util.text.filter;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.DelegatedTaskExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatFilterClient implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final AtomicInteger WORKER_COUNT = new AtomicInteger(1);
   private static final ThreadFactory THREAD_FACTORY = (p_244570_0_) -> {
      Thread thread = new Thread(p_244570_0_);
      thread.setName("Chat-Filter-Worker-" + WORKER_COUNT.getAndIncrement());
      return thread;
   };
   private final URL chatEndpoint = null;
   private final URL joinEndpoint = null;
   private final URL leaveEndpoint = null;
   private final String authKey = null;
   private final int ruleId = 0;
   private final String serverId = null;
   private final ChatFilterClient.IIgnoreTest chatIgnoreStrategy = null;
   private final ExecutorService workerPool = null;

   private void processJoinOrLeave(GameProfile p_244568_1_, URL p_244568_2_, Executor p_244568_3_) {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("server", this.serverId);
      jsonobject.addProperty("room", "Chat");
      jsonobject.addProperty("user_id", p_244568_1_.getId().toString());
      jsonobject.addProperty("user_display_name", p_244568_1_.getName());
      p_244568_3_.execute(() -> {
         try {
            this.processRequest(jsonobject, p_244568_2_);
         } catch (Exception exception) {
            LOGGER.warn("Failed to send join/leave packet to {} for player {}", p_244568_2_, p_244568_1_, exception);
         }

      });
   }

   private CompletableFuture<Optional<String>> requestMessageProcessing(GameProfile p_244567_1_, String p_244567_2_, ChatFilterClient.IIgnoreTest p_244567_3_, Executor p_244567_4_) {
      if (p_244567_2_.isEmpty()) {
         return CompletableFuture.completedFuture(Optional.of(""));
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("rule", this.ruleId);
         jsonobject.addProperty("server", this.serverId);
         jsonobject.addProperty("room", "Chat");
         jsonobject.addProperty("player", p_244567_1_.getId().toString());
         jsonobject.addProperty("player_display_name", p_244567_1_.getName());
         jsonobject.addProperty("text", p_244567_2_);
         return CompletableFuture.supplyAsync(() -> {
            try {
               JsonObject jsonobject1 = this.processRequestResponse(jsonobject, this.chatEndpoint);
               boolean flag = JSONUtils.getAsBoolean(jsonobject1, "response", false);
               if (flag) {
                  return Optional.of(p_244567_2_);
               } else {
                  String s = JSONUtils.getAsString(jsonobject1, "hashed", (String)null);
                  if (s == null) {
                     return Optional.empty();
                  } else {
                     int i = JSONUtils.getAsJsonArray(jsonobject1, "hashes").size();
                     return p_244567_3_.shouldIgnore(s, i) ? Optional.empty() : Optional.of(s);
                  }
               }
            } catch (Exception exception) {
               LOGGER.warn("Failed to validate message '{}'", p_244567_2_, exception);
               return Optional.empty();
            }
         }, p_244567_4_);
      }
   }

   public void close() {
      this.workerPool.shutdownNow();
   }

   private void drainStream(InputStream p_244569_1_) throws IOException {
      byte[] abyte = new byte[1024];

      while(p_244569_1_.read(abyte) != -1) {
      }

   }

   private JsonObject processRequestResponse(JsonObject p_244564_1_, URL p_244564_2_) throws IOException {
      HttpURLConnection httpurlconnection = this.makeRequest(p_244564_1_, p_244564_2_);

      JsonObject jsonobject;
      try (InputStream inputstream = httpurlconnection.getInputStream()) {
         if (httpurlconnection.getResponseCode() != 204) {
            try {
               return Streams.parse(new JsonReader(new InputStreamReader(inputstream))).getAsJsonObject();
            } finally {
               this.drainStream(inputstream);
            }
         }

         jsonobject = new JsonObject();
      }

      return jsonobject;
   }

   private void processRequest(JsonObject p_244573_1_, URL p_244573_2_) throws IOException {
      HttpURLConnection httpurlconnection = this.makeRequest(p_244573_1_, p_244573_2_);

      try (InputStream inputstream = httpurlconnection.getInputStream()) {
         this.drainStream(inputstream);
      }

   }

   private HttpURLConnection makeRequest(JsonObject p_244575_1_, URL p_244575_2_) throws IOException {
      HttpURLConnection httpurlconnection = (HttpURLConnection)p_244575_2_.openConnection();
      httpurlconnection.setConnectTimeout(15000);
      httpurlconnection.setReadTimeout(2000);
      httpurlconnection.setUseCaches(false);
      httpurlconnection.setDoOutput(true);
      httpurlconnection.setDoInput(true);
      httpurlconnection.setRequestMethod("POST");
      httpurlconnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
      httpurlconnection.setRequestProperty("Accept", "application/json");
      httpurlconnection.setRequestProperty("Authorization", "Basic " + this.authKey);
      httpurlconnection.setRequestProperty("User-Agent", "Minecraft server" + SharedConstants.getCurrentVersion().getName());

      try (
         OutputStreamWriter outputstreamwriter = new OutputStreamWriter(httpurlconnection.getOutputStream(), StandardCharsets.UTF_8);
         JsonWriter jsonwriter = new JsonWriter(outputstreamwriter);
      ) {
         Streams.write(p_244575_1_, jsonwriter);
      }

      int i = httpurlconnection.getResponseCode();
      if (i >= 200 && i < 300) {
         return httpurlconnection;
      } else {
         throw new ChatFilterClient.ConnectionException(i + " " + httpurlconnection.getResponseMessage());
      }
   }

   public IChatFilter createContext(GameProfile p_244566_1_) {
      return new ChatFilterClient.ProfileFilter(p_244566_1_);
   }

   private ChatFilterClient() {
      throw new RuntimeException("Synthetic constructor added by MCP, do not call");
   }

   public static class ConnectionException extends RuntimeException {
      private ConnectionException(String p_i244509_1_) {
         super(p_i244509_1_);
      }
   }

   @FunctionalInterface
   public interface IIgnoreTest {
      ChatFilterClient.IIgnoreTest NEVER_IGNORE = (p_244583_0_, p_244583_1_) -> {
         return false;
      };
      ChatFilterClient.IIgnoreTest IGNORE_FULLY_FILTERED = (p_244581_0_, p_244581_1_) -> {
         return p_244581_0_.length() == p_244581_1_;
      };

      boolean shouldIgnore(String p_shouldIgnore_1_, int p_shouldIgnore_2_);
   }

   class ProfileFilter implements IChatFilter {
      private final GameProfile profile;
      private final Executor streamExecutor;

      private ProfileFilter(GameProfile p_i244507_2_) {
         this.profile = p_i244507_2_;
         DelegatedTaskExecutor<Runnable> delegatedtaskexecutor = DelegatedTaskExecutor.create(ChatFilterClient.this.workerPool, "chat stream for " + p_i244507_2_.getName());
         this.streamExecutor = delegatedtaskexecutor::tell;
      }

      public void join() {
         ChatFilterClient.this.processJoinOrLeave(this.profile, ChatFilterClient.this.joinEndpoint, this.streamExecutor);
      }

      public void leave() {
         ChatFilterClient.this.processJoinOrLeave(this.profile, ChatFilterClient.this.leaveEndpoint, this.streamExecutor);
      }

      public CompletableFuture<Optional<List<String>>> processMessageBundle(List<String> p_244433_1_) {
         List<CompletableFuture<Optional<String>>> list = p_244433_1_.stream().map((p_244589_1_) -> {
            return ChatFilterClient.this.requestMessageProcessing(this.profile, p_244589_1_, ChatFilterClient.this.chatIgnoreStrategy, this.streamExecutor);
         }).collect(ImmutableList.toImmutableList());
         return Util.sequence(list).thenApply((p_244590_0_) -> {
            return Optional.<List<String>>of(p_244590_0_.stream().map((p_244588_0_) -> {
               return p_244588_0_.orElse("");
            }).collect(ImmutableList.toImmutableList()));
         }).exceptionally((p_244587_0_) -> {
            return Optional.empty();
         });
      }

      public CompletableFuture<Optional<String>> processStreamMessage(String p_244432_1_) {
         return ChatFilterClient.this.requestMessageProcessing(this.profile, p_244432_1_, ChatFilterClient.this.chatIgnoreStrategy, this.streamExecutor);
      }
   }
}
