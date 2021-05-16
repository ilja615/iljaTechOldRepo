package net.minecraft.server.management;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerProfileCache {
   private static final Logger LOGGER = LogManager.getLogger();
   private static boolean usesAuthentication;
   private final Map<String, PlayerProfileCache.ProfileEntry> profilesByName = Maps.newConcurrentMap();
   private final Map<UUID, PlayerProfileCache.ProfileEntry> profilesByUUID = Maps.newConcurrentMap();
   private final GameProfileRepository profileRepository;
   private final Gson gson = (new GsonBuilder()).create();
   private final File file;
   private final AtomicLong operationCount = new AtomicLong();

   public PlayerProfileCache(GameProfileRepository p_i46836_1_, File p_i46836_2_) {
      this.profileRepository = p_i46836_1_;
      this.file = p_i46836_2_;
      Lists.reverse(this.load()).forEach(this::safeAdd);
   }

   private void safeAdd(PlayerProfileCache.ProfileEntry p_242118_1_) {
      GameProfile gameprofile = p_242118_1_.getProfile();
      p_242118_1_.setLastAccess(this.getNextOperation());
      String s = gameprofile.getName();
      if (s != null) {
         this.profilesByName.put(s.toLowerCase(Locale.ROOT), p_242118_1_);
      }

      UUID uuid = gameprofile.getId();
      if (uuid != null) {
         this.profilesByUUID.put(uuid, p_242118_1_);
      }

   }

   @Nullable
   private static GameProfile lookupGameProfile(GameProfileRepository p_187319_0_, String p_187319_1_) {
      final AtomicReference<GameProfile> atomicreference = new AtomicReference<>();
      ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
         public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_) {
            atomicreference.set(p_onProfileLookupSucceeded_1_);
         }

         public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_) {
            atomicreference.set((GameProfile)null);
         }
      };
      p_187319_0_.findProfilesByNames(new String[]{p_187319_1_}, Agent.MINECRAFT, profilelookupcallback);
      GameProfile gameprofile = atomicreference.get();
      if (!usesAuthentication() && gameprofile == null) {
         UUID uuid = PlayerEntity.createPlayerUUID(new GameProfile((UUID)null, p_187319_1_));
         gameprofile = new GameProfile(uuid, p_187319_1_);
      }

      return gameprofile;
   }

   public static void setUsesAuthentication(boolean p_187320_0_) {
      usesAuthentication = p_187320_0_;
   }

   private static boolean usesAuthentication() {
      return usesAuthentication;
   }

   public void add(GameProfile p_152649_1_) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(new Date());
      calendar.add(2, 1);
      Date date = calendar.getTime();
      PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = new PlayerProfileCache.ProfileEntry(p_152649_1_, date);
      this.safeAdd(playerprofilecache$profileentry);
      this.save();
   }

   private long getNextOperation() {
      return this.operationCount.incrementAndGet();
   }

   @Nullable
   public GameProfile get(String p_152655_1_) {
      String s = p_152655_1_.toLowerCase(Locale.ROOT);
      PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = this.profilesByName.get(s);
      boolean flag = false;
      if (playerprofilecache$profileentry != null && (new Date()).getTime() >= playerprofilecache$profileentry.expirationDate.getTime()) {
         this.profilesByUUID.remove(playerprofilecache$profileentry.getProfile().getId());
         this.profilesByName.remove(playerprofilecache$profileentry.getProfile().getName().toLowerCase(Locale.ROOT));
         flag = true;
         playerprofilecache$profileentry = null;
      }

      GameProfile gameprofile;
      if (playerprofilecache$profileentry != null) {
         playerprofilecache$profileentry.setLastAccess(this.getNextOperation());
         gameprofile = playerprofilecache$profileentry.getProfile();
      } else {
         gameprofile = lookupGameProfile(this.profileRepository, s);
         if (gameprofile != null) {
            this.add(gameprofile);
            flag = false;
         }
      }

      if (flag) {
         this.save();
      }

      return gameprofile;
   }

   @Nullable
   public GameProfile get(UUID p_152652_1_) {
      PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = this.profilesByUUID.get(p_152652_1_);
      if (playerprofilecache$profileentry == null) {
         return null;
      } else {
         playerprofilecache$profileentry.setLastAccess(this.getNextOperation());
         return playerprofilecache$profileentry.getProfile();
      }
   }

   private static DateFormat createDateFormat() {
      return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   }

   public List<PlayerProfileCache.ProfileEntry> load() {
      List<PlayerProfileCache.ProfileEntry> list = Lists.newArrayList();

      try (Reader reader = Files.newReader(this.file, StandardCharsets.UTF_8)) {
         JsonArray jsonarray = this.gson.fromJson(reader, JsonArray.class);
         if (jsonarray == null) {
            return list;
         }

         DateFormat dateformat = createDateFormat();
         jsonarray.forEach((p_242122_2_) -> {
            PlayerProfileCache.ProfileEntry playerprofilecache$profileentry = readGameProfile(p_242122_2_, dateformat);
            if (playerprofilecache$profileentry != null) {
               list.add(playerprofilecache$profileentry);
            }

         });
      } catch (FileNotFoundException filenotfoundexception) {
      } catch (JsonParseException | IOException ioexception) {
         LOGGER.warn("Failed to load profile cache {}", this.file, ioexception);
      }

      return list;
   }

   public void save() {
      JsonArray jsonarray = new JsonArray();
      DateFormat dateformat = createDateFormat();
      this.getTopMRUProfiles(1000).forEach((p_242120_2_) -> {
         jsonarray.add(writeGameProfile(p_242120_2_, dateformat));
      });
      String s = this.gson.toJson((JsonElement)jsonarray);

      try (Writer writer = Files.newWriter(this.file, StandardCharsets.UTF_8)) {
         writer.write(s);
      } catch (IOException ioexception) {
      }

   }

   private Stream<PlayerProfileCache.ProfileEntry> getTopMRUProfiles(int p_242117_1_) {
      return ImmutableList.copyOf(this.profilesByUUID.values()).stream().sorted(Comparator.comparing(PlayerProfileCache.ProfileEntry::getLastAccess).reversed()).limit((long)p_242117_1_);
   }

   private static JsonElement writeGameProfile(PlayerProfileCache.ProfileEntry p_242119_0_, DateFormat p_242119_1_) {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("name", p_242119_0_.getProfile().getName());
      UUID uuid = p_242119_0_.getProfile().getId();
      jsonobject.addProperty("uuid", uuid == null ? "" : uuid.toString());
      jsonobject.addProperty("expiresOn", p_242119_1_.format(p_242119_0_.getExpirationDate()));
      return jsonobject;
   }

   @Nullable
   private static PlayerProfileCache.ProfileEntry readGameProfile(JsonElement p_242121_0_, DateFormat p_242121_1_) {
      if (p_242121_0_.isJsonObject()) {
         JsonObject jsonobject = p_242121_0_.getAsJsonObject();
         JsonElement jsonelement = jsonobject.get("name");
         JsonElement jsonelement1 = jsonobject.get("uuid");
         JsonElement jsonelement2 = jsonobject.get("expiresOn");
         if (jsonelement != null && jsonelement1 != null) {
            String s = jsonelement1.getAsString();
            String s1 = jsonelement.getAsString();
            Date date = null;
            if (jsonelement2 != null) {
               try {
                  date = p_242121_1_.parse(jsonelement2.getAsString());
               } catch (ParseException parseexception) {
               }
            }

            if (s1 != null && s != null && date != null) {
               UUID uuid;
               try {
                  uuid = UUID.fromString(s);
               } catch (Throwable throwable) {
                  return null;
               }

               return new PlayerProfileCache.ProfileEntry(new GameProfile(uuid, s1), date);
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   static class ProfileEntry {
      private final GameProfile profile;
      private final Date expirationDate;
      private volatile long lastAccess;

      private ProfileEntry(GameProfile p_i241888_1_, Date p_i241888_2_) {
         this.profile = p_i241888_1_;
         this.expirationDate = p_i241888_2_;
      }

      public GameProfile getProfile() {
         return this.profile;
      }

      public Date getExpirationDate() {
         return this.expirationDate;
      }

      public void setLastAccess(long p_242126_1_) {
         this.lastAccess = p_242126_1_;
      }

      public long getLastAccess() {
         return this.lastAccess;
      }
   }
}
