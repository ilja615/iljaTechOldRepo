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
   private static final Logger field_244549_a = LogManager.getLogger();
   private static final AtomicInteger field_244550_b = new AtomicInteger(1);
   private static final ThreadFactory field_244551_c = (p_244570_0_) -> {
      Thread thread = new Thread(p_244570_0_);
      thread.setName("Chat-Filter-Worker-" + field_244550_b.getAndIncrement());
      return thread;
   };
   private final URL field_244552_d = null;
   private final URL field_244553_e = null;
   private final URL field_244554_f = null;
   private final String field_244555_g = null;
   private final int field_244556_h = 0;
   private final String field_244557_i = null;
   private final ChatFilterClient.IIgnoreTest field_244558_j = null;
   private final ExecutorService field_244559_k = null;

   private void func_244568_a(GameProfile p_244568_1_, URL p_244568_2_, Executor p_244568_3_) {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("server", this.field_244557_i);
      jsonobject.addProperty("room", "Chat");
      jsonobject.addProperty("user_id", p_244568_1_.getId().toString());
      jsonobject.addProperty("user_display_name", p_244568_1_.getName());
      p_244568_3_.execute(() -> {
         try {
            this.func_244573_b(jsonobject, p_244568_2_);
         } catch (Exception exception) {
            field_244549_a.warn("Failed to send join/leave packet to {} for player {}", p_244568_2_, p_244568_1_, exception);
         }

      });
   }

   private CompletableFuture<Optional<String>> func_244567_a(GameProfile p_244567_1_, String p_244567_2_, ChatFilterClient.IIgnoreTest p_244567_3_, Executor p_244567_4_) {
      if (p_244567_2_.isEmpty()) {
         return CompletableFuture.completedFuture(Optional.of(""));
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("rule", this.field_244556_h);
         jsonobject.addProperty("server", this.field_244557_i);
         jsonobject.addProperty("room", "Chat");
         jsonobject.addProperty("player", p_244567_1_.getId().toString());
         jsonobject.addProperty("player_display_name", p_244567_1_.getName());
         jsonobject.addProperty("text", p_244567_2_);
         return CompletableFuture.supplyAsync(() -> {
            try {
               JsonObject jsonobject1 = this.func_244564_a(jsonobject, this.field_244552_d);
               boolean flag = JSONUtils.getBoolean(jsonobject1, "response", false);
               if (flag) {
                  return Optional.of(p_244567_2_);
               } else {
                  String s = JSONUtils.getString(jsonobject1, "hashed", (String)null);
                  if (s == null) {
                     return Optional.empty();
                  } else {
                     int i = JSONUtils.getJsonArray(jsonobject1, "hashes").size();
                     return p_244567_3_.shouldIgnore(s, i) ? Optional.empty() : Optional.of(s);
                  }
               }
            } catch (Exception exception) {
               field_244549_a.warn("Failed to validate message '{}'", p_244567_2_, exception);
               return Optional.empty();
            }
         }, p_244567_4_);
      }
   }

   public void close() {
      this.field_244559_k.shutdownNow();
   }

   private void func_244569_a(InputStream p_244569_1_) throws IOException {
      byte[] abyte = new byte[1024];

      while(p_244569_1_.read(abyte) != -1) {
      }

   }

   private JsonObject func_244564_a(JsonObject p_244564_1_, URL p_244564_2_) throws IOException {
      HttpURLConnection httpurlconnection = this.func_244575_c(p_244564_1_, p_244564_2_);

      JsonObject jsonobject;
      try (InputStream inputstream = httpurlconnection.getInputStream()) {
         if (httpurlconnection.getResponseCode() != 204) {
            try {
               return Streams.parse(new JsonReader(new InputStreamReader(inputstream))).getAsJsonObject();
            } finally {
               this.func_244569_a(inputstream);
            }
         }

         jsonobject = new JsonObject();
      }

      return jsonobject;
   }

   private void func_244573_b(JsonObject p_244573_1_, URL p_244573_2_) throws IOException {
      HttpURLConnection httpurlconnection = this.func_244575_c(p_244573_1_, p_244573_2_);

      try (InputStream inputstream = httpurlconnection.getInputStream()) {
         this.func_244569_a(inputstream);
      }

   }

   private HttpURLConnection func_244575_c(JsonObject p_244575_1_, URL p_244575_2_) throws IOException {
      HttpURLConnection httpurlconnection = (HttpURLConnection)p_244575_2_.openConnection();
      httpurlconnection.setConnectTimeout(15000);
      httpurlconnection.setReadTimeout(2000);
      httpurlconnection.setUseCaches(false);
      httpurlconnection.setDoOutput(true);
      httpurlconnection.setDoInput(true);
      httpurlconnection.setRequestMethod("POST");
      httpurlconnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
      httpurlconnection.setRequestProperty("Accept", "application/json");
      httpurlconnection.setRequestProperty("Authorization", "Basic " + this.field_244555_g);
      httpurlconnection.setRequestProperty("User-Agent", "Minecraft server" + SharedConstants.getVersion().getName());

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

   public IChatFilter func_244566_a(GameProfile p_244566_1_) {
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
      ChatFilterClient.IIgnoreTest field_244577_a = (p_244583_0_, p_244583_1_) -> {
         return false;
      };
      ChatFilterClient.IIgnoreTest field_244578_b = (p_244581_0_, p_244581_1_) -> {
         return p_244581_0_.length() == p_244581_1_;
      };

      boolean shouldIgnore(String p_shouldIgnore_1_, int p_shouldIgnore_2_);
   }

   class ProfileFilter implements IChatFilter {
      private final GameProfile field_244585_b;
      private final Executor field_244586_c;

      private ProfileFilter(GameProfile p_i244507_2_) {
         this.field_244585_b = p_i244507_2_;
         DelegatedTaskExecutor<Runnable> delegatedtaskexecutor = DelegatedTaskExecutor.create(ChatFilterClient.this.field_244559_k, "chat stream for " + p_i244507_2_.getName());
         this.field_244586_c = delegatedtaskexecutor::enqueue;
      }

      public void func_244800_a() {
         ChatFilterClient.this.func_244568_a(this.field_244585_b, ChatFilterClient.this.field_244553_e, this.field_244586_c);
      }

      public void func_244434_b() {
         ChatFilterClient.this.func_244568_a(this.field_244585_b, ChatFilterClient.this.field_244554_f, this.field_244586_c);
      }

      public CompletableFuture<Optional<List<String>>> func_244433_a(List<String> p_244433_1_) {
         List<CompletableFuture<Optional<String>>> list = p_244433_1_.stream().map((p_244589_1_) -> {
            return ChatFilterClient.this.func_244567_a(this.field_244585_b, p_244589_1_, ChatFilterClient.this.field_244558_j, this.field_244586_c);
         }).collect(ImmutableList.toImmutableList());
         return Util.gather(list).thenApply((p_244590_0_) -> {
            return Optional.<List<String>>of(p_244590_0_.stream().map((p_244588_0_) -> {
               return p_244588_0_.orElse("");
            }).collect(ImmutableList.toImmutableList()));
         }).exceptionally((p_244587_0_) -> {
            return Optional.empty();
         });
      }

      public CompletableFuture<Optional<String>> func_244432_a(String p_244432_1_) {
         return ChatFilterClient.this.func_244567_a(this.field_244585_b, p_244432_1_, ChatFilterClient.this.field_244558_j, this.field_244586_c);
      }
   }
}
