package net.minecraft.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.bridge.game.GameVersion;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MinecraftVersion implements GameVersion {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final GameVersion BUILT_IN = new MinecraftVersion();
   private final String id;
   private final String name;
   private final boolean stable;
   private final int worldVersion;
   private final int protocolVersion;
   private final int packVersion;
   private final Date buildTime;
   private final String releaseTarget;

   private MinecraftVersion() {
      this.id = UUID.randomUUID().toString().replaceAll("-", "");
      this.name = "1.16.4";
      this.stable = true;
      this.worldVersion = 2584;
      this.protocolVersion = SharedConstants.getProtocolVersion();
      this.packVersion = 6;
      this.buildTime = new Date();
      this.releaseTarget = "1.16.4";
   }

   private MinecraftVersion(JsonObject p_i51407_1_) {
      this.id = JSONUtils.getAsString(p_i51407_1_, "id");
      this.name = JSONUtils.getAsString(p_i51407_1_, "name");
      this.releaseTarget = JSONUtils.getAsString(p_i51407_1_, "release_target");
      this.stable = JSONUtils.getAsBoolean(p_i51407_1_, "stable");
      this.worldVersion = JSONUtils.getAsInt(p_i51407_1_, "world_version");
      this.protocolVersion = JSONUtils.getAsInt(p_i51407_1_, "protocol_version");
      this.packVersion = JSONUtils.getAsInt(p_i51407_1_, "pack_version");
      this.buildTime = Date.from(ZonedDateTime.parse(JSONUtils.getAsString(p_i51407_1_, "build_time")).toInstant());
   }

   public static GameVersion tryDetectVersion() {
      try (InputStream inputstream = MinecraftVersion.class.getResourceAsStream("/version.json")) {
         if (inputstream == null) {
            LOGGER.warn("Missing version information!");
            return BUILT_IN;
         } else {
            MinecraftVersion minecraftversion;
            try (InputStreamReader inputstreamreader = new InputStreamReader(inputstream)) {
               minecraftversion = new MinecraftVersion(JSONUtils.parse(inputstreamreader));
            }

            return minecraftversion;
         }
      } catch (JsonParseException | IOException ioexception) {
         throw new IllegalStateException("Game version information is corrupt", ioexception);
      }
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public String getReleaseTarget() {
      return this.releaseTarget;
   }

   public int getWorldVersion() {
      return this.worldVersion;
   }

   public int getProtocolVersion() {
      return this.protocolVersion;
   }

   public int getPackVersion() {
      return this.packVersion;
   }

   public Date getBuildTime() {
      return this.buildTime;
   }

   public boolean isStable() {
      return this.stable;
   }
}
