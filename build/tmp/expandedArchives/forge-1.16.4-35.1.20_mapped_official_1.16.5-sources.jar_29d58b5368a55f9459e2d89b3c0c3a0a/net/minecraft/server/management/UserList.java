package net.minecraft.server.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class UserList<K, V extends UserListEntry<K>> {
   protected static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final File file;
   private final Map<String, V> map = Maps.newHashMap();

   public UserList(File p_i1144_1_) {
      this.file = p_i1144_1_;
   }

   public File getFile() {
      return this.file;
   }

   public void add(V p_152687_1_) {
      this.map.put(this.getKeyForUser(p_152687_1_.getUser()), p_152687_1_);

      try {
         this.save();
      } catch (IOException ioexception) {
         LOGGER.warn("Could not save the list after adding a user.", (Throwable)ioexception);
      }

   }

   @Nullable
   public V get(K p_152683_1_) {
      this.removeExpired();
      return this.map.get(this.getKeyForUser(p_152683_1_));
   }

   public void remove(K p_152684_1_) {
      this.map.remove(this.getKeyForUser(p_152684_1_));

      try {
         this.save();
      } catch (IOException ioexception) {
         LOGGER.warn("Could not save the list after removing a user.", (Throwable)ioexception);
      }

   }

   public void remove(UserListEntry<K> p_199042_1_) {
      this.remove(p_199042_1_.getUser());
   }

   public String[] getUserList() {
      return this.map.keySet().toArray(new String[this.map.size()]);
   }

   public boolean isEmpty() {
      return this.map.size() < 1;
   }

   protected String getKeyForUser(K p_152681_1_) {
      return p_152681_1_.toString();
   }

   protected boolean contains(K p_152692_1_) {
      return this.map.containsKey(this.getKeyForUser(p_152692_1_));
   }

   private void removeExpired() {
      List<K> list = Lists.newArrayList();

      for(V v : this.map.values()) {
         if (v.hasExpired()) {
            list.add(v.getUser());
         }
      }

      for(K k : list) {
         this.map.remove(this.getKeyForUser(k));
      }

   }

   protected abstract UserListEntry<K> createEntry(JsonObject p_152682_1_);

   public Collection<V> getEntries() {
      return this.map.values();
   }

   public void save() throws IOException {
      JsonArray jsonarray = new JsonArray();
      this.map.values().stream().map((p_232646_0_) -> {
         return Util.make(new JsonObject(), p_232646_0_::serialize);
      }).forEach(jsonarray::add);

      try (BufferedWriter bufferedwriter = Files.newWriter(this.file, StandardCharsets.UTF_8)) {
         GSON.toJson((JsonElement)jsonarray, bufferedwriter);
      }

   }

   public void load() throws IOException {
      if (this.file.exists()) {
         try (BufferedReader bufferedreader = Files.newReader(this.file, StandardCharsets.UTF_8)) {
            JsonArray jsonarray = GSON.fromJson(bufferedreader, JsonArray.class);
            this.map.clear();

            for(JsonElement jsonelement : jsonarray) {
               JsonObject jsonobject = JSONUtils.convertToJsonObject(jsonelement, "entry");
               UserListEntry<K> userlistentry = this.createEntry(jsonobject);
               if (userlistentry.getUser() != null) {
                  this.map.put(this.getKeyForUser(userlistentry.getUser()), (V)userlistentry);
               }
            }
         }

      }
   }
}
